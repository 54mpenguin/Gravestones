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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;

public class onClick implements Listener {

    private ArrayList<Gravestone> redeemed = new ArrayList<>();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        if (event.getAction().equals(Action.LEFT_CLICK_BLOCK) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (event.getClickedBlock().getType().equals(Material.CHEST) || event.getClickedBlock().getType().equals(Material.TRAPPED_CHEST)) {
                if (Main.gravesloc.contains(event.getClickedBlock().getLocation())){
                    event.setCancelled(true);
                    Gravestone grave = Gravestone.getGrave(event.getClickedBlock().getLocation(), p.getUniqueId());
                    if (grave == null) {
                        p.sendMessage(ChatColor.LIGHT_PURPLE + "That is not your grave! You cannot open it!");
                        return;
                    }
                    if (redeemed.contains(grave)){
                        return;
                    }
                    redeemed.add(grave);
                    int zombieChance = (int)(Math.random() * 10 + 1);
                    if (zombieChance >= 8){
                        p.sendMessage(ChatColor.LIGHT_PURPLE + "You're corpse has been REINCARNATED!!");
                        Zombie zombie = (Zombie) grave.getLocation().getWorld().spawnEntity(grave.getLocation(), EntityType.ZOMBIE);
                        zombie.setBaby(false);
                        zombie.setCustomName(p.getName() + " reincarnated");
                        zombie.setRemoveWhenFarAway(false);
                        zombie.setCanPickupItems(false);
                        zombie.setCustomNameVisible(true);
                        zombie.setTarget(p);
                        zombie.setHealth(20);
                        zombie.setInvulnerable(true);
                        zombie.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 100, 1));
                        zombie.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 1000000000, 2));
                        zombie.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 1000000000, 1));
                        zombie.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 1000000000, 1));
                        zombie.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 100, 5));
                        grave.getLocation().getWorld().strikeLightningEffect(grave.getLocation());
                        zombie.setInvulnerable(false);
                    }
                    p.spawnParticle(Particle.TOTEM, event.getClickedBlock().getLocation(), 500);
                    p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_YES, 50, 1);
                    p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 50, 1);
                    for (ItemStack item : p.getInventory().getStorageContents()) {
                        if (item != null && !item.getType().equals(Material.AIR)) {
                            p.getWorld().dropItem(p.getLocation(), item);
                        }
                    }
                    if (p.getInventory().getItemInOffHand() != null && !p.getInventory().getItemInOffHand().getType().equals(Material.AIR)){
                        p.getWorld().dropItem(p.getLocation(), p.getInventory().getItemInOffHand());
                    }
                    for (ItemStack item : p.getInventory().getArmorContents()) {
                        if (item != null  && !p.getInventory().getItemInOffHand().getType().equals(Material.AIR)) {
                            p.getWorld().dropItem(p.getLocation(), item);
                        }
                    }
                    ArrayList<ItemStack> items = grave.getItems();
                    if (items != null) {
                        ItemStack[] itemsArray = items.toArray(new ItemStack[35]);
                        p.getInventory().setContents(itemsArray);
                    }
                    if (grave.getOffHand() != null){
                        p.getInventory().setItemInOffHand(grave.getOffHand());
                    }
                    ArrayList<ItemStack> armor = grave.getArmor();
                    if (armor != null) {
                        ItemStack[] armorArray = armor.toArray(new ItemStack[3]);
                        p.getInventory().setArmorContents(armorArray);
                    }
                    p.giveExp(grave.getEXPLevel());
                    redeemed.remove(grave);
                    Gravestone.deleteGrave(grave);
                    p.sendMessage(ChatColor.LIGHT_PURPLE + "Your inventory, armor and exp levels have been brought back from the grave!");
                }
            }
        }
    }
}