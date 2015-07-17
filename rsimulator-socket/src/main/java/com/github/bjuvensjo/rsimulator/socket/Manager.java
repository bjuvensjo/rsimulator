package com.github.bjuvensjo.rsimulator.socket;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.github.bjuvensjo.rsimulator.core.Simulator;
import com.github.bjuvensjo.rsimulator.core.SimulatorResponse;
import com.github.bjuvensjo.rsimulator.socket.config.GlobalConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Optional;

/**
 * Manager
 *
 * @author Magnus Bjuvensjö
 */
@Singleton
public class Manager {

    @Inject
    private RequestReader requestReader;

    @Inject
    private ResponseWriter responseWriter;

    @Inject
    private Simulator simulator;

    @Inject
    @Named("simulatorContentType")
    private String simulatorContentType;

    public void handle(Socket s) throws IOException {
        Worker worker = new Worker(s);

        new Thread(worker).start();
    }

    class Worker implements Runnable {
        private final Logger log = LoggerFactory.getLogger(Worker.class);
        private Socket s;

        public Worker(Socket s) throws IOException {
            this.s = s;
        }

        private RequestReader.Request read(InputStream in) throws IOException {
            return requestReader.read(in);
        }

        private void write(RequestReader.Request request, SimulatorResponse simulatorResponse, OutputStream out) throws IOException {
            responseWriter.write(request, simulatorResponse, out);
        }

        private String getRootRelativePath(RequestReader.Request request) {
            if (GlobalConfig.useRootRelativePath) {
                return request.getBody().substring(0, 3); // the name of the service
            }
            return "";
        }

        public void run() {
            boolean connected = true;
            while (!s.isClosed() && connected) {
                InputStream in = null;
                OutputStream out = null;
                try {
                    in = s.getInputStream();
                    out = s.getOutputStream();
                    RequestReader.Request request = read(in);
                    if (request.isValid()) {
                        Optional<SimulatorResponse> simulatorResponseOptional = simulator.service(GlobalConfig.rootPath, getRootRelativePath(request), request.getBody(), simulatorContentType);
                        SimulatorResponse simulatorResponse = simulatorResponseOptional.get();
                        write(request, simulatorResponse, out);
                    }
                } catch (Exception e) {
                    log.error(null, e);
                    try {
                        if (!s.isClosed()) {
                            s.close();
                        }
                    } catch (IOException e1) {
                        log.debug(null, e1);
                    }
                } finally {
                    connected = false;
                }
            }
        }
    }
}