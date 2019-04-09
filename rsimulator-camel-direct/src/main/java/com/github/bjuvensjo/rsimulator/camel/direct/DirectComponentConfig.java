package com.github.bjuvensjo.rsimulator.camel.direct;

public class DirectComponentConfig {
    private String rootPath;

    public DirectComponentConfig(String rootPath) {
        this.rootPath = rootPath;
    }

    public String getRootPath() {
        return rootPath;
    }

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }
}
