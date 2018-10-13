package me.fiftyfour.gravestones.events;

import me.fiftyfour.gravestones.Gravestone;
import me.fiftyfour.gravestones.Main;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class onRespawn implements Listener {

    @EventHandler
    public void onRespawnEvent(PlayerRespawnEvent event){
        Player player = event.getPlayer();
        ArrayList<Gravestone> graves = Main.graves.get(player.getUniqueId());
        if (graves == null)return;
        int latestgravenumber = graves.toArray().length - 1;
        if (latestgravenumber < 0) latestgravenumber = 0;
        Gravestone latestGrave;
        try {
            latestGrave = Main.graves.get(player.getUniqueId()).get(latestgravenumber);
        }catch (IndexOutOfBoundsException iobe){
            return;
        }
        Location graveLoc = latestGrave.getLocation().clone();
        int accuracy = getGpsRadius(player);
        int randomX = (int)(Math.random() * accuracy + 1);
        int randomZ = (int)(Math.random() * accuracy + 1);
        int randomY = (int)(Math.random() * (accuracy/10) + 1);
        graveLoc.add(randomX, randomY, randomZ);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        ItemStack writtenBook = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta bookMeta = (BookMeta) writtenBook.getItemMeta();
        bookMeta.setTitle(player.getName() + "'s Grave info");
        bookMeta.setAuthor("Gravestones");
        List<String> pages = new ArrayList<>();
        pages.add("Time of Death: " + dtf.format(now) + "\nYour inventory, armor and  exp levels are in a grave somewhere at:\nX: " + graveLoc.getBlockX() + " \nY: " + graveLoc.getBlockY () + " \nZ: " + graveLoc.getBlockZ() + " \n(Accuracy: " + accuracy + " Blocks)");
        bookMeta.setPages(pages);
        writtenBook.setItemMeta(bookMeta);
        player.getInventory().addItem(writtenBook);
        player.sendMessage(ChatColor.LIGHT_PURPLE + "Your inventory, armor and  exp levels are in a grave somewhere at: X:" + graveLoc.getBlockX() + " Y: " + graveLoc.getBlockY () + " Z: " + graveLoc.getBlockZ() + " (Accuracy: " + accuracy + " Blocks)");

    }
    private int getGpsRadius(Player p){
        int minLevel = 0;
        int level = p.hasPermission("gravestones.gps.radius.") ? 1 : 0;
        if (p.isOp()){
            level=1;
        }else {
            for (int i = 100; i >= minLevel; i--)
                if (p.hasPermission("gravestones.gps.radius." + i))
                    level = i;
        }
        return level;
    }
}
