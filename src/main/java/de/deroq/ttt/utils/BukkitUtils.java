package de.deroq.ttt.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class BukkitUtils {

    public static String getOnlinePlayers(int neededPlayers) {
        return "§7[§3" + Bukkit.getOnlinePlayers().size() + "§7/§3" + neededPlayers + "§7]";
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

    public static void sendBroadcastMessage(String message) {
        Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(Constants.PREFIX + message));
    }

    public static void sendBroadcastSound(Sound sound) {
        Bukkit.getOnlinePlayers().forEach(player -> player.playSound(player.getLocation(), sound, 3f, 3f));
    }

    public static void sendBroadcastSoundInRadius(Location location, int x, int y, int z, Sound sound) {
        location.getWorld().getNearbyEntities(location, x, y, z).forEach(entity -> ((Player) entity).playSound(entity.getLocation(), sound, 3f, 3f));
    }

    public static void sendBroadcastSoundInRadius(Player player, int x, int y, int z, Sound sound) {
        player.getNearbyEntities(x, y, z).forEach(entity -> ((Player) entity).playSound(entity.getLocation(), sound, 3f, 3f));
    }
}
