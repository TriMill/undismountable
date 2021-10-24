package io.github.trimill.undismountable;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.spigotmc.event.entity.EntityDismountEvent;

public class DismountListener implements Listener {
    // This is called whenever an entity dismounts from another entity
    @EventHandler
    public void onDismount(EntityDismountEvent event) {
        // If the dismounter is a player and the mount is not dead
        if (event.getEntity() instanceof Player && !event.getDismounted().isDead()) {
            Player player = (Player)(event.getEntity());
            LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);

            // Check if this player bypasses regions
            boolean canBypass = WorldGuard.getInstance().getPlatform().getSessionManager().hasBypass(localPlayer, localPlayer.getWorld());
            if(canBypass) {
                return;
            }

            // Get the region that the dismount occurs in
            RegionQuery query = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
            Location loc = BukkitAdapter.adapt(event.getEntity().getLocation());
            ApplicableRegionSet regionSet = query.getApplicableRegions(loc);

            // Get the state of the vehicle-dismount flag for this player
            StateFlag.State state = regionSet.queryState(localPlayer, Undismountable.FLAG_VEHICLE_DISMOUNT);

            // If the flag is DENY, prevent the player from dismounting
            if (state == StateFlag.State.DENY) {
                event.setCancelled(true);
                // Send a dismount error message if configured
                if (Undismountable.DISMOUNT_DENIED_MESSAGE != null
                 && Undismountable.DISMOUNT_DENIED_MESSAGE.length() > 0
                ) {
                    // Prevent repeat messages by checking the difference between
                    // the current time and the last time this player was sent an error
                    long currentTime = System.currentTimeMillis();
                    long lastErrorTime = Undismountable.LastErrorMessage.getOrDefault(player.getUniqueId(), 0l);
                    if(currentTime >= lastErrorTime + Undismountable.ERROR_TIMEOUT) {
                        // Send the message and set the last dismount time
                        player.sendMessage(Undismountable.DISMOUNT_DENIED_MESSAGE);
                        Undismountable.LastErrorMessage.put(player.getUniqueId(), currentTime);
                    }
                }
            }
        }
    }
}
