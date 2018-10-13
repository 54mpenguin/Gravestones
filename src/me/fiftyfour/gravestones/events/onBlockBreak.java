package me.fiftyfour.gravestones.events;

import me.fiftyfour.gravestones.Main;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class onBlockBreak implements Listener {

    @EventHandler
    public void blockBreak(BlockBreakEvent event){
        if (Main.gravesloc.contains(event.getBlock().getLocation())){
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.LIGHT_PURPLE + "You may not break Graves! The only way to remove it is for it to be redeemed or expire!");
        }
    }
}
