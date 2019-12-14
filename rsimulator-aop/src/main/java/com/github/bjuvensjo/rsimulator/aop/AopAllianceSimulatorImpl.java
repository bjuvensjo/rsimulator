package com.github.bjuvensjo.rsimulator.aop;

import com.github.bjuvensjo.rsimulator.core.config.CoreModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import org.aopalliance.intercept.MethodInvocation;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;

/**
 * AopAllianceSimulatorImpl.
 */
public class AopAllianceSimulatorImpl implements AopAllianceSimulator {
    @Inject
    private SimulatorAdapter simulatorAdapter;
    private String rootPath;
    private boolean useRootRelativePath = false;

    /**
     * Creates an AopAllianceSimulatorImpl.
     */
    public AopAllianceSimulatorImpl() {
        Injector injector = Guice.createInjector(new CoreModule());
        injector.injectMembers(this);
    }

    /**
     * {@inheritDoc}
     */
    public void setRootPath(Class<?> testClass) {
        String resource = testClass.getSimpleName() + ".class";
        try {
            URL classResource = testClass.getResource(resource);
            if (classResource == null) {
                throw new RuntimeException("Can not set rootPath since class " + resource + " can not be found.");
            }
            setRootPath(new File(classResource.toURI()).getParentFile().toPath().toString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void setRootPath(String aRootPath) {
        this.rootPath = aRootPath;
    }

    /**
     * {@inheritDoc}
     */
    public void setUseRootRelativePath(boolean aUseRootRelativePath) {
        this.useRootRelativePath = aUseRootRelativePath;
    }

    /**
     * Returns some simulation response if found.
     *
     * @param mi the MethodInvocation
     * @return some simulation response
     * @throws Exception if something goes wrong
     */
    public Object invoke(MethodInvocation mi) throws Exception {
        Method method = mi.getMethod();
        return simulatorAdapter.service(method.getDeclaringClass().getCanonicalName(), method.getName(), mi.getArguments(), rootPath, useRootRelativePath);
    }
}
