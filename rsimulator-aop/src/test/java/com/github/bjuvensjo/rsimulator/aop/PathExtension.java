package com.github.bjuvensjo.rsimulator.aop;

import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;

public class PathExtension implements TestInstancePostProcessor, BeforeTestExecutionCallback {
    private String testResourcesPath;

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Override
    public void postProcessTestInstance(Object testInstance,
                                        ExtensionContext context) throws Exception {
        Optional<String> resource = Optional.of(testInstance)
                .map(Object::getClass)
                .map(Class::getName)
                .map(name -> "/" + name.replaceAll("\\.", "/") + ".class");

        resource
                .map(r -> testInstance.getClass().getResource(r))
                .map(url -> {
                    try {
                        return url.toURI();
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                })
                .map(Paths::get)
                .map(Objects::toString)
                .map(s -> s.replace(File.separator + testInstance.getClass() + ".class", ""))
                .ifPresent(s -> {
                    testResourcesPath = s;
                    String testBasePath = testResourcesPath.replace(resource.get(), "");

                    setField(testInstance, "testBasePath", testBasePath);
                    setField(testInstance, "testPath", testResourcesPath);
                });
    }

    private void setField(Object testInstance, String name, String value) {
        try {
            testInstance.getClass().getDeclaredField(name).set(testInstance, value);
        } catch (Exception nfe) {
            // Do nothing
        }
    }

    @Override
    public void beforeTestExecution(ExtensionContext extensionContext) throws Exception {
        extensionContext.getTestMethod()
                .map(Method::getName)
                .ifPresent(methodName -> {
                    extensionContext.getTestInstance().ifPresent(t -> {
                        String testMethodPath = testResourcesPath.replace(".class", File.separator) + methodName;
                        setField(t, "testMethodPath", testMethodPath);
                    });
                });
    }
}
