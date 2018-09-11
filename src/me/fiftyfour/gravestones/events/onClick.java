package me.fiftyfour.gravestones.events;

import me.fiftyfour.gravestones.Gravestone;
import me.fiftyfour.gravestones.Main;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class onClick implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        if (event.getAction().equals(Action.LEFT_CLICK_BLOCK) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (event.getClickedBlock().getType().equals(Material.CHEST)) {
                if (Main.gravesloc.toString().contains(event.getClickedBlock().getLocation().toString())){
                    event.setCancelled(true);
                    Gravestone grave = Gravestone.getGrave(event.getClickedBlock().getLocation(), p.getUniqueId());
                    if (grave == null) {
                        p.sendMessage(ChatColor.LIGHT_PURPLE + "That is not your grave! You cannot open it!");
                        return;
                    }
                    int zombieChance = (int)(Math.random() * 10 + 1);
                    if (zombieChance >= 8){
                        p.sendMessage(ChatColor.LIGHT_PURPLE + "You're corpse has been REINCARNATED!!");
                        grave.getLocation().getWorld().strikeLightningEffect(grave.getLocation());
                        Zombie zombie = (Zombie) grave.getLocation().getWorld().spawnEntity(grave.getLocation(), EntityType.ZOMBIE);
                        zombie.setBaby(false);
                        zombie.setCustomName(p.getName() + " reincarnated");
                        zombie.setRemoveWhenFarAway(false);
                        zombie.setCanPickupItems(false);
                        zombie.setCustomNameVisible(true);
                        zombie.setTarget(p);
                        zombie.setHealth(20);
                        zombie.setNoDamageTicks(0);
                    }
                    p.spawnParticle(Particle.TOTEM, event.getClickedBlock().getLocation(), 500);
                    p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_YES, 50, 1);
                    p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 50, 1);
                    for (ItemStack item : p.getInventory().getContents()) {
                        if (item != null) {
                            p.getWorld().dropItem(p.getLocation(), item);
                        }
                    }
                    for (ItemStack item : p.getInventory().getArmorContents()) {
                        if (item != null) {
                            p.getWorld().dropItem(p.getLocation(), item);
                        }
                    }
                    ArrayList<ItemStack> items = grave.getItems();
                    if (items != null) {
                        ItemStack[] itemsArray = items.toArray(new ItemStack[35]);
                        p.getInventory().setContents(itemsArray);
                    }
                    ArrayList<ItemStack> armor = grave.getArmor();
                    if (armor != null) {
                        ItemStack[] armorArray = armor.toArray(new ItemStack[3]);
                        p.getInventory().setArmorContents(armorArray);
                    }
                    p.giveExp(grave.getEXPLevel());
                    Gravestone.deleteGrave(grave);
                    p.sendMessage(ChatColor.LIGHT_PURPLE + "Your inventory, armor and exp levels have been brought back from the grave!");
                }
            }
        }
    }
}