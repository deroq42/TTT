package de.deroq.ttt.config;

import com.google.gson.Gson;
import de.deroq.ttt.config.models.SettingsConfig;

import java.io.*;
import java.util.Arrays;
import java.util.Optional;

public class FileManager {

    private final File FOLDER;
    private final File SETTINGS_FILE;
    private SettingsConfig settingsConfig;

    public FileManager() {
        this.FOLDER = new File("plugins/TTT/");
        this.SETTINGS_FILE = new File(FOLDER.getPath(), "settings.json");
    }

    public void loadFiles() {
        try {
            if (!FOLDER.exists()) {
                FOLDER.mkdirs();
            }

            if(FOLDER.isDirectory() && FOLDER.listFiles().length == 0) {
                createSettingsConfig();
                return;
            }

            this.settingsConfig = (SettingsConfig) readConfig(SETTINGS_FILE, SettingsConfig.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createSettingsConfig() throws IOException {
        if(!SETTINGS_FILE.exists()) {
            if (!SETTINGS_FILE.createNewFile()) {
                throw new IOException("Error while creating Settings file: File has not been created.");
            }
        }

        this.settingsConfig = SettingsConfig.create(SETTINGS_FILE);
        saveConfig(settingsConfig);
    }

    public void saveConfig(Config config) throws IOException {
        Optional<File> optionalConfigFile = Arrays.stream(FOLDER.listFiles())
                .filter(file -> file.getName().equals(config.getFileName()))
                .findFirst();

        if(!optionalConfigFile.isPresent()) {
            throw new IOException("Error while getting file " + config.getFileName() + ": File can not be found");
        }

        File configFile = optionalConfigFile.get();

        try (FileWriter fileWriter = new FileWriter(configFile)) {
            fileWriter.write(new Gson().toJson(config));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Config readConfig(File file, Class<? extends Config> clazz) throws FileNotFoundException {
        return new Gson().fromJson(new FileReader(file), clazz);
    }

    public SettingsConfig getSettingsConfig() {
        return settingsConfig;
    }
}
