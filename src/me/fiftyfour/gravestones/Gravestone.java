package me.fiftyfour.gravestones;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.UUID;

public class Gravestone {

    private ArrayList<ItemStack> items = new ArrayList<>();
    private ArrayList<ItemStack> armor = new ArrayList<>();
    private ItemStack offHand;
    private int EXPLevel;
    private Location location;
    private UUID owner;
    private int timeTillExpire;
    private int number;
    private Hologram hologram;

    public static void createGrave(Gravestone grave) {
        Plugin plugin = Main.getPlugin(Main.class);
        if (grave.hasEverything()) {
            if (!Main.graves.containsKey(grave.getOwner())) {
                ArrayList<Gravestone> graves = new ArrayList<>();
                graves.add(grave);
                Main.graves.put(grave.getOwner(), graves);
            } else {
                int gravesAmount = Main.graves.get(grave.getOwner()).toArray().length;
                grave.setNumber(gravesAmount + 1);
                Main.graves.get(grave.getOwner()).add(grave);
            }
            Location loc = grave.getLocation();
            if (trappedChest(loc)) {
                Block currentBlock = loc.getBlock();
                currentBlock.setType(Material.TRAPPED_CHEST);
            } else {
                Block currentBlock = loc.getBlock();
                currentBlock.setType(Material.CHEST);
            }
            Main.gravesloc.add(grave.getLocation());
            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                @Override
                public void run() {
                    try {
                        if (getGrave(grave) != null) {
                            Gravestone.expire(grave);
                            Gravestone.deleteGrave(grave);
                        }
                    }catch (IndexOutOfBoundsException e){
                        return;
                    }
                }
            }, (1200 * grave.getTimeTillExpire()));
        } else {
            if (!grave.hasOwner())
                throw new NullPointerException("Grave must have an owner to be created!!");
            else if (!grave.hasLocation())
                throw new NullPointerException("Grave must have a Location to be created!!");
            else {
                grave.setTimeTillExpire(10);
                grave.setEXPLevel(0);
                int gravesAmount = 0;
                if (!Main.graves.containsKey(grave.getOwner())) {
                    ArrayList<Gravestone> graves = new ArrayList<>();
                    graves.add(grave);
                    Main.graves.put(grave.getOwner(), graves);
                } else {
                    gravesAmount = Main.graves.get(grave.getOwner()).toArray().length;
                    grave.setNumber(gravesAmount + 1);
                    Main.graves.get(grave.getOwner()).add(grave);
                }
                grave.setNumber(gravesAmount + 1);
                Main.graves.get(grave.getOwner()).add(grave);
                Location loc = grave.getLocation();
                if (trappedChest(loc)) {
                    Block currentBlock = loc.getBlock();
                    currentBlock.setType(Material.TRAPPED_CHEST);
                } else {
                    Block currentBlock = loc.getBlock();
                    currentBlock.setType(Material.CHEST);
                }
                Main.gravesloc.add(grave.getLocation());
                plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (getGrave(grave) != null) {
                                Gravestone.expire(grave);
                                Gravestone.deleteGrave(grave);
                            }
                        }catch (IndexOutOfBoundsException e){
                            return;
                        }
                    }
                }, (1200 * grave.getTimeTillExpire()));
            }
        }
    }

    public static Location checkNear(Location locationGiven) {
        Location location = locationGiven.clone();
        if (location.getBlock().getType().equals(Material.CHEST) || location.getBlock().getType().equals(Material.TRAPPED_CHEST)
                || location.getBlock().getType().equals(Material.SPONGE) || location.getBlock().getType().equals(Material.BEDROCK)) {
            location.add(1, 0, 0);
            if (location.getBlock().getType().equals(Material.CHEST) || location.getBlock().getType().equals(Material.TRAPPED_CHEST)
                    || location.getBlock().getType().equals(Material.SPONGE) || location.getBlock().getType().equals(Material.BEDROCK)) {
                location.add(-2, 0, 0);
                if (location.getBlock().getType().equals(Material.CHEST) || location.getBlock().getType().equals(Material.TRAPPED_CHEST)
                        || location.getBlock().getType().equals(Material.SPONGE) || location.getBlock().getType().equals(Material.BEDROCK)) {
                    location.add(1, 0, 1);
                    if (location.getBlock().getType().equals(Material.CHEST) || location.getBlock().getType().equals(Material.TRAPPED_CHEST)
                            || location.getBlock().getType().equals(Material.SPONGE) || location.getBlock().getType().equals(Material.BEDROCK)) {
                        location.add(0, 0, -2);
                        if (location.getBlock().getType().equals(Material.CHEST) || location.getBlock().getType().equals(Material.TRAPPED_CHEST)
                                || location.getBlock().getType().equals(Material.SPONGE) || location.getBlock().getType().equals(Material.BEDROCK)) {
                            location.add(0, 1, 1);
                            boolean morethan255 = false;
                            if (location.getBlockY() > 255) {
                                location.add(0, -2, 0);
                                morethan255 = true;
                            }
                            if (location.getBlock().getType().equals(Material.CHEST) || location.getBlock().getType().equals(Material.TRAPPED_CHEST)
                                    || location.getBlock().getType().equals(Material.SPONGE) || location.getBlock().getType().equals(Material.BEDROCK)) {
                                if (morethan255) {
                                    location.add(0, 1, 0);
                                    checkNear(location);
                                } else {
                                    if (location.getBlock().getType().equals(Material.CHEST) || location.getBlock().getType().equals(Material.TRAPPED_CHEST)
                                            || location.getBlock().getType().equals(Material.SPONGE) || location.getBlock().getType().equals(Material.BEDROCK)) {
                                        location.add(0, -2, 0);
                                        if (location.getBlock().getType().equals(Material.CHEST) || location.getBlock().getType().equals(Material.TRAPPED_CHEST)
                                                || location.getBlock().getType().equals(Material.SPONGE) || location.getBlock().getType().equals(Material.BEDROCK)) {
                                            location.add(0, 1, 1);
                                            checkNear(location);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        int chest = 0;
        int trapped = 0;
        Block block = location.getBlock();
        if (block.getRelative(BlockFace.NORTH).getType().equals(Material.TRAPPED_CHEST)) trapped++;
        if (block.getRelative(BlockFace.SOUTH).getType().equals(Material.TRAPPED_CHEST)) trapped++;
        if (block.getRelative(BlockFace.EAST).getType().equals(Material.TRAPPED_CHEST)) trapped++;
        if (block.getRelative(BlockFace.WEST).getType().equals(Material.TRAPPED_CHEST)) trapped++;

        if (block.getRelative(BlockFace.NORTH).getType().equals(Material.CHEST)) chest++;
        if (block.getRelative(BlockFace.SOUTH).getType().equals(Material.CHEST)) chest++;
        if (block.getRelative(BlockFace.EAST).getType().equals(Material.CHEST)) chest++;
        if (block.getRelative(BlockFace.WEST).getType().equals(Material.CHEST)) chest++;
        if (trapped >= 1 && chest >= 1) {

            location.add(0, 1, 0);
            if (location.getBlockY() > 255) {
                location.add(0, -2, 0);
                return checkNear(location);
            } else {
                return checkNear(location);
            }
        }
        else return location;
    }

    private static boolean trappedChest(Location locationGiven) {
        int chest = 0;
        int trapped = 0;
        Block block = locationGiven.getBlock();
        if (block.getRelative(BlockFace.NORTH).getType().equals(Material.TRAPPED_CHEST)) trapped++;
        if (block.getRelative(BlockFace.SOUTH).getType().equals(Material.TRAPPED_CHEST)) trapped++;
        if (block.getRelative(BlockFace.EAST).getType().equals(Material.TRAPPED_CHEST)) trapped++;
        if (block.getRelative(BlockFace.WEST).getType().equals(Material.TRAPPED_CHEST)) trapped++;

        if (block.getRelative(BlockFace.NORTH).getType().equals(Material.CHEST)) chest++;
        if (block.getRelative(BlockFace.SOUTH).getType().equals(Material.CHEST)) chest++;
        if (block.getRelative(BlockFace.EAST).getType().equals(Material.CHEST)) chest++;
        if (block.getRelative(BlockFace.WEST).getType().equals(Material.CHEST)) chest++;

        if (chest == 0 && trapped == 0) return false;
        if (chest == 4 || chest == 1) return true;
        if (trapped == 4 || trapped == 1) return false;
        if (chest >= 2 && trapped == 0) return true;
        if (trapped >= 2 && chest == 0) return false;
        return true;
    }

    public static int graveNear(Location locationGiven) {
        int gravesNear = 0;
        for (Location loopLocation : Main.gravesloc) {
            Location location = locationGiven.clone();
            location.add(1, 0, 0);
            if (location.equals(loopLocation)) gravesNear++;
            else {
                location.add(-2, 0, 0);
                if (location.equals(loopLocation)) gravesNear++;
                else {
                    location.add(1, 0, 1);
                    if (location.equals(loopLocation)) gravesNear++;
                    else {
                        location.add(0, 0, -2);
                        if (location.equals(loopLocation)) gravesNear++;
                    }
                }
            }
        }
        return gravesNear;
    }


    public static void deleteGrave(Gravestone grave) {
        grave.setNumber(grave.getNumber() - 1);
        if (!Main.graves.get(grave.getOwner()).isEmpty() && Main.graves.get(grave.getOwner()).toArray().length >= grave.getNumber()) {
            Main.graves.get(grave.getOwner()).remove(grave.getNumber());
            Main.gravesloc.remove(grave.getLocation());
        } else {
            for (Gravestone gravestone : Main.graves.get(grave.getOwner())) {
                if (gravestone.getLocation().equals(grave.getLocation())) {
                    grave.setNumber(Main.graves.get(grave.getOwner()).indexOf(gravestone));
                    break;
                }
            }
            Main.graves.get(grave.getOwner()).remove(grave.getNumber());
            Main.gravesloc.remove(grave.getLocation());
        }
        if (!Main.graves.get(grave.getOwner()).isEmpty()) {
            for (Gravestone gravestone : Main.graves.get(grave.getOwner())) {
                gravestone.setNumber(Main.graves.get(grave.getOwner()).indexOf(gravestone) + 1);
            }
        }
        Hologram hologram = grave.getHologram();
        hologram.delete();
        Location loc = grave.getLocation();
        Block currentBlock = loc.getBlock();
        if (currentBlock.getType().equals(Material.CHEST) || currentBlock.getType().equals(Material.TRAPPED_CHEST))
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

    public static ArrayList<Gravestone> getGrave(UUID owner) {
        return Main.graves.get(owner);
    }

    private static Gravestone getGrave(Gravestone grave) {
        UUID owner = grave.getOwner();
        int number = grave.getNumber();
        if (Main.graves.get(owner).isEmpty() || Main.graves.get(grave.getOwner()).toArray().length < grave.getNumber()) {
            return null;
        }
        return Main.graves.get(owner).get(number);
    }

    static void expire(Gravestone grave) {
        for (ItemStack item : grave.getItems()) {
            if (item != null && !item.getType().equals(Material.AIR))
                Bukkit.getWorld(grave.getLocation().getWorld().getName()).dropItem(grave.getLocation(), item);
        }
        for (ItemStack item : grave.getArmor()) {
            if (item != null && !item.getType().equals(Material.AIR))
                Bukkit.getWorld(grave.getLocation().getWorld().getName()).dropItem(grave.getLocation(), item);
        }
        Player player = Bukkit.getPlayer(grave.getOwner());
        if (player != null) {
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 100, 1);
            player.sendMessage(ChatColor.LIGHT_PURPLE + "Your grave has expired and the items have been dropped on the ground!");
        }
        Hologram hologram = grave.getHologram();
        hologram.delete();
        Location loc = grave.getLocation();
        Block currentBlock = loc.getBlock();
        if (currentBlock.getType().equals(Material.CHEST) || currentBlock.getType().equals(Material.TRAPPED_CHEST))
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

    public void setArmor(final ArrayList<ItemStack> armor) {
        this.armor = armor;
    }

    public void setEXPLevel(int EXPLevel) {
        this.EXPLevel = EXPLevel;
    }

    public void setItems(final ArrayList<ItemStack> items) {
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

    private boolean hasOwner() {
        return this.getOwner() != null;
    }

    private boolean hasItems() {
        return this.getItems() != null;
    }

    private boolean hasArmor() {
        return this.getArmor() != null;
    }

    private boolean hasEverything() {
        return this.hasOwner() && this.hasArmor() && this.hasItems() && this.hasLocation();
    }

    private boolean hasLocation() {
        return this.getLocation() != null;
    }

    public String toString() {
        return "Owner: " + this.owner + " Number: " + this.number + " Location: " + this.location + " Items: " + this.items
                + " Armor: " + this.armor + " ExpLevel: " + this.EXPLevel + " Time till expire: " + this.timeTillExpire;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    public ItemStack getOffHand() {
        return offHand;
    }

    public void setOffHand(ItemStack offHand) {
        this.offHand = offHand;
    }
}

