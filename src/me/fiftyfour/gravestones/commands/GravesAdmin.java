package me.fiftyfour.gravestones.commands;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

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
                player.sendMessage(ChatColor.RED + "Usage: /gravesadmin <test|reincarnate>");
                return false;
            }
            if (args[0].equals("test")){
                player.sendMessage(ChatColor.LIGHT_PURPLE + "Testing grave spawning...(you will die)");
                player.getInventory().addItem(new ItemStack(Material.STONE, 64));
                player.giveExp(50);
                player.setGameMode(GameMode.SURVIVAL);
                player.setHealth(0);
                player.sendMessage(ChatColor.LIGHT_PURPLE + "Beginning grave spawning....");
                return true;
            }else if (args[0].equals("reincarnate")) {
                player.sendMessage(ChatColor.LIGHT_PURPLE + "You're corpse has been REINCARNATED!!");
                Zombie zombie = (Zombie) player.getLocation().getWorld().spawnEntity(player.getLocation(), EntityType.ZOMBIE);
                zombie.setBaby(false);
                zombie.setCustomName(player.getName() + " reincarnated");
                zombie.setRemoveWhenFarAway(false);
                zombie.setCanPickupItems(false);
                zombie.setCustomNameVisible(true);
                zombie.setTarget(player);
                zombie.setHealth(20);
                zombie.setInvulnerable(true);
                zombie.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 100, 1));
                zombie.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 1000000000, 2));
                zombie.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 1000000000, 1));
                zombie.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 1000000000, 1));
                zombie.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 100, 5));
                player.getLocation().getWorld().strikeLightningEffect(player.getLocation());
                zombie.setInvulnerable(false);
            }else{
                player.sendMessage(ChatColor.RED + "Usage: /gravesadmin <test|reincarnate>");
                return false;
            }
        }
        return false;
    }

}
