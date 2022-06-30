package de.deroq.ttt.config;

import com.google.gson.Gson;
import de.deroq.ttt.config.models.LocationsConfig;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;

public class FileManager {

    private final File FOLDER;
    private final File LOCATIONS_FILE;
    private LocationsConfig locationsConfig;

    public FileManager() {
        this.FOLDER = new File("plugins/TTT/");
        this.LOCATIONS_FILE = new File(FOLDER.getPath(), "locations.json");
    }

    public void loadFiles() {
        try {
            if (!FOLDER.exists()) {
                FOLDER.mkdirs();
            }

            if(FOLDER.isDirectory() && FOLDER.listFiles().length == 0) {
                createLocationsConfig();
                return;
            }

            this.locationsConfig = (LocationsConfig) readConfig(LOCATIONS_FILE, LocationsConfig.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createLocationsConfig() throws IOException {
        if(!LOCATIONS_FILE.exists()) {
            if (!LOCATIONS_FILE.createNewFile()) {
                throw new IOException("Error while creating Roles file: File has not been created.");
            }
        }

        this.locationsConfig = LocationsConfig.create(LOCATIONS_FILE, new HashMap<>());
        saveConfig(locationsConfig);
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

    public LocationsConfig getLocationsConfig() {
        return locationsConfig;
    }
}
