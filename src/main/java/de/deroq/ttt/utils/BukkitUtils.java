package de.deroq.ttt.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class BukkitUtils {

    public static String getOnlinePlayers() {
        return "§7[§3" + Bukkit.getOnlinePlayers().size() + "§7/§3" + Constants.NEEDED_PLAYERS + "§7]";
    }

    public static String locationToString(Location location) {
        return location.getWorld().getName() + ";" +
                location.getX() + ";" +
                location.getY() + ";" +
                location.getZ() + ";" +
                location.getYaw() + ";" +
                location.getPitch();
    }

    public static Location locationFromString(String serialized) {
        String[] coordinates = serialized.split(";");
        World world = Bukkit.getWorld(coordinates[0]);
        double x = Double.parseDouble(coordinates[1]);
        double y = Double.parseDouble(coordinates[2]);
        double z = Double.parseDouble(coordinates[3]);
        float yaw = Float.parseFloat(coordinates[4]);
        float pitch = Float.parseFloat(coordinates[5]);
        return new Location(world, x, y, z, yaw, pitch);
    }
}
