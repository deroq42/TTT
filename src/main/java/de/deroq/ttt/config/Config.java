package de.deroq.ttt.config;

public abstract class Config {

    private final String fileName;

    public Config(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
}
