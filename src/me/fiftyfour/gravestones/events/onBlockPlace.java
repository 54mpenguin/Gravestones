package me.fiftyfour.gravestones.events;

import me.fiftyfour.gravestones.Gravestone;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class onBlockPlace implements Listener {

    @EventHandler
    public void blockPlace(BlockPlaceEvent event) {
        if (event.getBlockPlaced().getType().equals(Material.CHEST ) || event.getBlockPlaced().getType().equals(Material.TRAPPED_CHEST)) {
            if (Gravestone.graveNear(event.getBlockPlaced().getLocation()) > 0) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(ChatColor.LIGHT_PURPLE + "You cannot place a chest or trapped chest near a grave!");
            }
        }
    }
}
