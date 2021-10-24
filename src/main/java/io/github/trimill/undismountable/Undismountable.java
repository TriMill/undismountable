package io.github.trimill.undismountable;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;

public final class Undismountable extends JavaPlugin {
    // WorldGuard flag to set whether players can dismount in a region
    public static StateFlag FLAG_VEHICLE_DISMOUNT = null;
    // The error message to send when dismounting is denied, send nothing if null or empty string
    public static String DISMOUNT_DENIED_MESSAGE = null;
    // The timeout between repeated error messages (ms)
    public static long ERROR_TIMEOUT = 1000;
    // When did each player last recieve an error message?
    public static HashMap<UUID, Long> LastErrorMessage;

    @Override
    public void onEnable() {
        // Initialize constants and register the dismount event listener
        LastErrorMessage = new HashMap<>();
        getServer().getPluginManager().registerEvents(new DismountListener(), this);

        // Load from the config file
        saveDefaultConfig();
        FileConfiguration cfg = getConfig();
        DISMOUNT_DENIED_MESSAGE = cfg.getString("dismount_message");
        ERROR_TIMEOUT = cfg.getLong("error_timeout", 1000);
    }

    @Override
    public void onLoad() {
        // Attempt to register the "vehicle-dismount" flag, setting FLAG_VEHICLE_DISMOUNT if successful
        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
        try {
            StateFlag flag = new StateFlag("vehicle-dismount", true);
            registry.register(flag);
            FLAG_VEHICLE_DISMOUNT = flag;
        } catch (FlagConflictException e) {
            Flag<?> existing = registry.get("vehicle-dismount");
            if (existing instanceof StateFlag) {
                FLAG_VEHICLE_DISMOUNT = (StateFlag) existing;
            } else {
                getLogger().log(Level.WARNING, "Flag name 'vehicle-dismount' is already taken, Undismountable cannot load.");
            }
        }
    }

    public static Undismountable inst() {
        return getPlugin(Undismountable.class);
    }
}
