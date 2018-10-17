package me.fiftyfour.gravestones.commands;

import me.fiftyfour.gravestones.Gravestone;
import me.fiftyfour.gravestones.Main;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.UUID;

public class GravesAdmin implements CommandExecutor {
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (s.equals("gravesadmin")) {
            if (!(commandSender instanceof Player)) {
                commandSender.sendMessage("You must be a player to use this command!");
                return false;
            }
            Player player = (Player) commandSender;
            if (!(player.hasPermission("gravestones.admin"))) {
                player.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
                return false;
            }
            if (args.length == 0) {
                player.sendMessage(ChatColor.GOLD + "|-------------[Graves Admin]-------------|");
                player.sendMessage(ChatColor.RED + "/gravesadmin reincarnate - create a reincarnation zombie!");
                player.sendMessage(ChatColor.RED + "/gravesadmin list - list all graves and their owners!");
                player.sendMessage(ChatColor.RED + "/gravesadmin disable - disable the graves plugin!");
                player.sendMessage(ChatColor.RED + "/gravesadmin enable - enable the graves plugin!");
                player.sendMessage(ChatColor.RED + "/gravesadmin bypass - let you or someone else open any grave!");
                player.sendMessage(ChatColor.RED + "/gravesadmin restore - restore a player's grave for them!");
                return false;
            }
            if (args[0].equalsIgnoreCase("reincarnate")) {
                player.sendMessage(ChatColor.LIGHT_PURPLE + "You're corpse has been REINCARNATED!!");
                Zombie zombie = (Zombie) player.getLocation().getWorld().spawnEntity(player.getLocation(), EntityType.ZOMBIE);
                zombie.setBaby(false);
                zombie.setCustomName(player.getName() + " reincarnated");
                zombie.setRemoveWhenFarAway(false);
                zombie.setCustomNameVisible(true);
                zombie.setTarget(player);
                zombie.setHealth(20);
                zombie.setInvulnerable(true);
                zombie.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 10000, 1));
                zombie.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 1000000000, 1));
                zombie.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 1000000000, 1));
                zombie.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 10000, 5));
                player.getLocation().getWorld().strikeLightningEffect(player.getLocation());
                zombie.setInvulnerable(false);
                return true;
            }else if (args[0].equalsIgnoreCase("disable")){
                Main.disabled = true;
                Main.getPlugin(Main.class).getConfig().set("disabled", "true");
                Main.getPlugin(Main.class).saveConfig();
                player.sendMessage(ChatColor.GREEN + "Graves will no longer be created, Keep inventory is now on!");
                return true;
            }else if (args[0].equalsIgnoreCase("enable")){
                Main.disabled = false;
                Main.getPlugin(Main.class).getConfig().set("disabled", "false");
                Main.getPlugin(Main.class).saveConfig();
                player.sendMessage(ChatColor.GREEN + "Graves will now be created, Items will not drop on death!");
                return true;
            }else if (args[0].equalsIgnoreCase("list")){
                if (Main.graves.keySet().toArray().length <= 0){
                    player.sendMessage(ChatColor.GOLD + "There are currently no graves!");
                    return true;
                }
                player.sendMessage(ChatColor.GOLD + "|-------------[Graves List]-------------|");
                for (UUID uuid : Main.graves.keySet()){
                    int graves = Main.graves.get(uuid).toArray().length;
                    Player graveOwner = Bukkit.getPlayer(uuid);
                    if (graveOwner != null)
                        player.sendMessage(ChatColor.GREEN + graveOwner.getName() + " - " + graves);
                    else
                    player.sendMessage(ChatColor.GREEN + uuid.toString() + " - " + graves);
                }
                return true;
            }else if (args[0].equalsIgnoreCase("bypass")){
                if (args.length >= 2){
                    Player bypass = Bukkit.getPlayer(args[1]);
                    if (bypass != null && !Main.bypassed.contains(bypass)){
                        Main.bypassed.add(bypass);
                        player.sendMessage(ChatColor.GOLD + args[1] + " Can now open any grave!");
                    }else if (bypass != null && Main.bypassed.contains(bypass)) {
                        Main.bypassed.remove(bypass);
                        player.sendMessage(ChatColor.GOLD + args[1] + " Can no longer open any grave!");
                    }else{
                        player.sendMessage(ChatColor.RED + args[1] + "is not a player's name!");
                        return false;
                    }
                    return true;
                }else{
                    if (!Main.bypassed.contains(player)){
                        Main.bypassed.add(player);
                        player.sendMessage(ChatColor.GOLD + "You can now open any grave!");
                    }else if (Main.bypassed.contains(player)) {
                        Main.bypassed.remove(player);
                        player.sendMessage(ChatColor.GOLD + "You can no longer open any grave!");
                    }
                    return true;
                }
            }else if (args[0].equalsIgnoreCase("restore")){
                if (args.length >= 2){
                    Player p = Bukkit.getPlayerExact(args[1]);
                    if (p == null){
                        player.sendMessage(ChatColor.RED + args[1] + " is not a player's name!");
                        return false;
                    }
                    ArrayList<Gravestone> graveList = Gravestone.getGrave(p.getUniqueId());
                    if (graveList == null || graveList.toArray().length <= 0) {
                        player.sendMessage(ChatColor.RED + p.getName() + " does not have a grave to restore!");
                        return false;
                    }
                    Gravestone grave = graveList.get(graveList.toArray().length - 1);
                    grave.getLocation().getBlock().setType(Material.AIR);
                    p.spawnParticle(Particle.TOTEM, p.getLocation(), 500);
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
                    Gravestone.deleteGrave(grave);
                    p.sendMessage(ChatColor.LIGHT_PURPLE + "Your inventory, armor and exp levels have been brought back from the grave!");
                }else{
                    player.sendMessage(ChatColor.RED + "Please provide a player's name!");
                    return false;
                }
                return true;
            }
            else{
                player.sendMessage(ChatColor.GOLD + "|-------------[Graves Admin]-------------|");
                player.sendMessage(ChatColor.RED + "/gravesadmin reincarnate - create a reincarnation zombie!");
                player.sendMessage(ChatColor.RED + "/gravesadmin list - list all graves and their owners!");
                player.sendMessage(ChatColor.RED + "/gravesadmin disable - disable the graves plugin!");
                player.sendMessage(ChatColor.RED + "/gravesadmin enable - enable the graves plugin!");
                player.sendMessage(ChatColor.RED + "/gravesadmin bypass - let you or someone else open any grave!");
                player.sendMessage(ChatColor.RED + "/gravesadmin restore - restore a player's grave for them!");
                return false;
            }
        }
        return false;
    }

}
