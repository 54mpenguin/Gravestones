package me.fiftyfour.gravestones;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.UUID;

public class Gravestone {

    private ArrayList<ItemStack> items = new ArrayList<>();
    private ArrayList<ItemStack> armor = new ArrayList<>();
    private int EXPLevel;
    private Location location;
    private UUID owner;
    private int timeTillExpire;
    private int number;
    private Hologram hologram;

    public static void createGrave(Gravestone grave){
        Plugin plugin = Main.getPlugin(Main.class);
        if (grave.hasEverything()) {
            if (!Main.graves.containsKey(grave.getOwner())) {
                ArrayList<Gravestone> graves = new ArrayList<>();
                graves.add(grave);
                Main.graves.put(grave.getOwner(), graves);
            }else {
                int gravesAmount = Main.graves.get(grave.getOwner()).toArray().length;
                grave.setNumber(gravesAmount + 1);
                Main.graves.get(grave.getOwner()).add(grave);
            }
            Main.gravesloc.add(grave.getLocation());
            Location loc = grave.getLocation();
            Block currentBlock = loc.getBlock();
            currentBlock.setType(Material.CHEST);
            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                @Override
                public void run() {
                    if (getGrave(grave) != null) {
                        Gravestone.expire(grave);
                        Gravestone.deleteGrave(grave);
                        Player player = Bukkit.getPlayer(grave.getOwner());
                        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 100, 1);
                        player.sendMessage(ChatColor.LIGHT_PURPLE + "You're grave has expired and so you're items have been thrown on the ground!");
                    }
                }
            }, (1200 * grave.getTimeTillExpire()));
        }else{
            if (!grave.hasOwner())
                throw new NullPointerException("Grave must have an owner to be created!!");
            else if (!grave.hasLocation())
                throw new NullPointerException("Grave must have a Location to be created!!");
            else{
                grave.setTimeTillExpire(10);
                grave.setEXPLevel(0);
                int gravesAmount = 0;
                if (!Main.graves.containsKey(grave.getOwner())) {
                    ArrayList<Gravestone> graves = new ArrayList<>();
                    graves.add(grave);
                    Main.graves.put(grave.getOwner(), graves);
                }else {
                    gravesAmount = Main.graves.get(grave.getOwner()).toArray().length;
                    grave.setNumber(gravesAmount + 1);
                    Main.graves.get(grave.getOwner()).add(grave);
                }
                grave.setNumber(gravesAmount + 1);
                Main.graves.get(grave.getOwner()).add(grave);
                Main.gravesloc.add(grave.getLocation());
                Location loc = grave.getLocation();
                Block currentBlock = loc.getBlock();
                currentBlock.setType(Material.CHEST);
                plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        if (getGrave(grave) != null) {
                            Gravestone.expire(grave);
                            Gravestone.deleteGrave(grave);
                        }
                    }
                }, (1200 * grave.getTimeTillExpire()));
            }
        }
    }

    public static void deleteGrave(Gravestone grave){
        grave.setNumber(grave.getNumber() - 1);
        if (!Main.graves.get(grave.getOwner()).isEmpty() || Main.graves.get(grave.getOwner()).toArray().length < grave.getNumber())
            Main.graves.get(grave.getOwner()).remove(grave.getNumber());
        if (!Main.gravesloc.isEmpty()|| Main.graves.get(grave.getOwner()).toArray().length < grave.getNumber())
            Main.gravesloc.remove(grave.getLocation());
        if (!Main.graves.get(grave.getOwner()).isEmpty()) {
            for (int i = 0; i >= Main.graves.get(grave.getOwner()).toArray().length; i++) {
                Gravestone loopGrave = Main.graves.get(grave.getOwner()).get(i);
                loopGrave.setNumber(i);
            }
        }
        Hologram hologram = grave.getHologram();
        hologram.delete();
        Location loc = grave.getLocation();
        Block currentBlock = loc.getBlock();
        if (currentBlock.getType().equals(Material.CHEST))
            currentBlock.setType(Material.AIR);
    }

    public static Gravestone getGrave(Location location, UUID clicker) {
        if (Main.graves.get(clicker) == null) return null;
        for (Gravestone grave : Main.graves.get(clicker)) {
            if (grave.getLocation().equals(location)) {
                return grave;
            }
        }
        return null;
    }

    public static ArrayList<Gravestone> getGrave(UUID owner){
        return Main.graves.get(owner);
    }

    private static Gravestone getGrave(Gravestone grave){
        UUID owner = grave.getOwner();
        int number = grave.getNumber();
        if (Main.graves.get(owner).isEmpty() || Main.graves.get(grave.getOwner()).toArray().length < grave.getNumber()){
            return null;
        }
        return Main.graves.get(owner).get(number);
    }

    static void expire(Gravestone grave){
        for(ItemStack item : grave.getItems()){
            if (item !=null)
                Bukkit.getWorld(grave.getLocation().getWorld().getName()).dropItem(grave.getLocation(), item);
        }
        for(ItemStack item : grave.getArmor()){
            if (item !=null)
                Bukkit.getWorld(grave.getLocation().getWorld().getName()).dropItem(grave.getLocation(), item);
        }
        Player player = Bukkit.getPlayer(grave.getOwner());
        if (player != null){
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 100, 1);
            player.sendMessage(ChatColor.LIGHT_PURPLE + "Your grave has expired and the items have been dropped on the ground!");
        }
        Hologram hologram = grave.getHologram();
        hologram.delete();
        Location loc = grave.getLocation();
        Block currentBlock = loc.getBlock();
        if (currentBlock.getType().equals(Material.CHEST))
            currentBlock.setType(Material.AIR);
    }

    public int getEXPLevel() {
        return EXPLevel;
    }

    private int getTimeTillExpire() {
        return timeTillExpire;
    }

    public final ArrayList<ItemStack> getArmor() {
        return armor;
    }

    public final ArrayList<ItemStack> getItems() {
        return items;
    }

    public Location getLocation() {
        return location;
    }

    private UUID getOwner() {
        return owner;
    }

    private int getNumber() {
        return number;
    }

    private Hologram getHologram() {
        return hologram;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setArmor(final ArrayList<ItemStack>  armor) {
        this.armor = armor;
    }

    public void setEXPLevel(int EXPLevel) {
        this.EXPLevel = EXPLevel;
    }

    public void setItems(final ArrayList<ItemStack>  items) {
        this.items = items;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    public void setTimeTillExpire(int timeTillExpire) {
        this.timeTillExpire = timeTillExpire;
    }

    public void setHologram(Hologram hologram) {
        this.hologram = hologram;
    }

    private boolean hasOwner(){
        return this.getOwner() != null;
    }

    private boolean hasItems(){
        return this.getItems() != null;
    }

    private boolean hasArmor(){
        return this.getArmor() != null;
    }

    private boolean hasEverything(){
        return this.hasOwner() && this.hasArmor() && this.hasItems() && this.hasLocation();
    }

    private boolean hasLocation(){
        return this.getLocation() != null;
    }

    public String toString(){
        return "Owner: " + this.owner + " Number: " + this.number +  " Location: " + this.location + " Items: " + this.items
                + " Armor: " + this.armor + " ExpLevel: " + this.EXPLevel + " Time till expire: " + this.timeTillExpire;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}

