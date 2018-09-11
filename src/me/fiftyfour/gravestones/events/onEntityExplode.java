package me.fiftyfour.gravestones.events;

import me.fiftyfour.gravestones.Main;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.ArrayList;

public class onEntityExplode implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void EntityExplode(EntityExplodeEvent event) {
        for (Block block : new ArrayList<>(event.blockList())){
            if(block.getType() == Material.CHEST && Main.gravesloc.contains(block.getLocation())){
                event.blockList().remove(block);
            }
        }
    }
}
