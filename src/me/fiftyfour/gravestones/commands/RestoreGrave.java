package me.fiftyfour.gravestones.commands;

import me.fiftyfour.gravestones.Gravestone;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class RestoreGrave implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (s.equals("restoregrave")) {
            if (!(commandSender instanceof Player)) {
                commandSender.sendMessage("You must be a player to use this command!");
                return false;
            }
            Player p = (Player) commandSender;
            if (!(p.hasPermission("gravestones.restoregrave"))) {
                p.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
                return false;
            }
            ArrayList<Gravestone> graveList = Gravestone.getGrave(p.getUniqueId());
            if (graveList == null) {
                p.sendMessage(ChatColor.RED + "You don't have a grave to restore!");
                return false;
            }
            Gravestone grave = graveList.get(graveList.toArray().length - 1);
            grave.getLocation().getBlock().setType(Material.AIR);
            p.spawnParticle(Particle.TOTEM, p.getLocation(), 500);
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
        return false;
    }
}
