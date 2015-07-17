package com.github.bjuvensjo.rsimulator.socket;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.github.bjuvensjo.rsimulator.core.config.CoreModule;
import com.github.bjuvensjo.rsimulator.socket.config.GlobalConfig;
import com.github.bjuvensjo.rsimulator.socket.config.SocketModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * SocketSimulator.
 *
 * @author Magnus Bjuvensjö
 */
public class SocketSimulator {
    private static final Logger log = LoggerFactory.getLogger(SocketSimulator.class);

    @Inject
    private Manager manager;

    public SocketSimulator() {
        Injector injector = Guice.createInjector(new CoreModule(), new SocketModule());
        injector.injectMembers(this);
        run();
    }

    private void run() {
        try {
            int port = GlobalConfig.port;
            ServerSocket ss = new ServerSocket(port);
            log.info("Started {} on port {}", this.getClass().getName(), port);
            while (true) {
                try {
                    Socket s = ss.accept();
                    log.debug("Accepted {}", s);
                    manager.handle(s);
                } catch (IOException e) {
                    log.error(null, e);
                }
            }
        } catch (IOException e) {
            log.error(null, e);
        }
    }

    public static void main(String[] args) {
        new SocketSimulator();
    }
}
