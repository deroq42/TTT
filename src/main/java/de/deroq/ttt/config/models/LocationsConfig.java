package de.deroq.ttt.config.models;

import de.deroq.ttt.config.Config;
import de.deroq.ttt.utils.BukkitUtils;
import org.bukkit.Location;

import java.io.File;
import java.util.Map;

public class LocationsConfig extends Config {

    private final Map<String, String> locations;

    private LocationsConfig(File file, Map<String, String> locations) {
        super(file.getName());
        this.locations = locations;
    }

    public Map<String, String> getLocations() {
        return locations;
    }

    public Location getLocation(String name) {
        if (locations.containsKey(name)) {
            return BukkitUtils.locationFromString(locations.get(name));
        }

        return null;
    }

    public static LocationsConfig create(File file, Map<String, String> locations) {
        return new LocationsConfig(file, locations);
    }
}
