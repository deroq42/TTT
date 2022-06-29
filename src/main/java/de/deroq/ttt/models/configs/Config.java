package de.deroq.ttt.models.configs;

public abstract class Config {

    private final String fileName;

    public Config(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
}
