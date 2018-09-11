package me.fiftyfour.gravestones.commands;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import me.fiftyfour.gravestones.Gravestone;
import me.fiftyfour.gravestones.Main;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;

public class GravesAdmin implements CommandExecutor {
    private Plugin plugin = Main.getPlugin(Main.class);
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
                player.sendMessage(ChatColor.RED + "Usage: /gravesadmin <reload|test>");
                return false;
            }
            if(args[0].equals("reload")) {
                plugin.reloadConfig();
                player.sendMessage(ChatColor.LIGHT_PURPLE + "Config has been reloaded!");
                return true;
            }else if (args[0].equals("test")){
                player.sendMessage(ChatColor.LIGHT_PURPLE + "Saving test grave!");
                int graveNumber;
                if(Main.graves.get(player.getUniqueId()) != null) {
                    graveNumber = Main.graves.get(player.getUniqueId()).toArray().length;
                }else{
                    graveNumber = 0;
                }
                Gravestone grave = new Gravestone();
                final ArrayList<ItemStack> armorCont = new ArrayList<>(Arrays.asList(player.getInventory().getArmorContents()));
                final ArrayList<ItemStack> invCont = new ArrayList<>();
                for(ItemStack stack : player.getInventory().getStorageContents()){
                    if(stack != null) invCont.add(stack);
                }
                grave.setArmor(armorCont);
                grave.setItems(invCont);
                grave.setEXPLevel(player.getTotalExperience());
                grave.setLocation(player.getLocation());
                grave.setOwner(player.getUniqueId());
                grave.setNumber(graveNumber + 1);
                grave.setTimeTillExpire(10);
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
                LocalDateTime now = LocalDateTime.now();
                Location holoLoc = player.getLocation().clone();
                holoLoc.add(0.5, 3, 0.5);
                Hologram hologram = HologramsAPI.createHologram(plugin, holoLoc);
                ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
                SkullMeta sm = (SkullMeta) skull.getItemMeta();
                sm.setOwningPlayer(player);
                skull.setItemMeta(sm);
                hologram.appendItemLine(skull);
                hologram.appendTextLine(ChatColor.GOLD + "" + ChatColor.BOLD + player.getName() + "'s Grave");
                hologram.appendTextLine(ChatColor.GOLD + "Time of Death: " + dtf.format(now));
                hologram.appendTextLine(ChatColor.GRAY + "" + ChatColor.ITALIC + "They died");
                grave.setHologram(hologram);
                Gravestone.createGrave(grave);
                player.sendMessage(ChatColor.LIGHT_PURPLE + "Test grave saved at current location with current inventory, armor and exp!");
                return true;
            }else{
                player.sendMessage(ChatColor.RED + "Usage: /gravesadmin <reload|test>");
                return false;
            }
        }
        return false;
    }

}
