package me.fiftyfour.gravestones;

import com.sainttx.holograms.HologramPlugin;
import com.sainttx.holograms.api.HologramManager;
import me.fiftyfour.gravestones.commands.GravesAdmin;
import me.fiftyfour.gravestones.commands.RestoreGrave;
import me.fiftyfour.gravestones.events.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class Main extends JavaPlugin {
    public static HashMap<UUID, ArrayList<Gravestone>> graves = new HashMap<>();
    public static ArrayList<Location> gravesloc = new ArrayList<>();
    public static boolean disabled;
    public static ArrayList<Player> bypassed = new ArrayList<>();
    public static HologramManager hologramManager;

    @Override
    public void onEnable() {
        if (!Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays") && !Bukkit.getPluginManager().isPluginEnabled("Holograms")) {
            getLogger().severe("*** HolographicDisplays or Holograms is not installed or not enabled. ***");
            getLogger().severe("*** This plugin will be disabled. ***");
            this.setEnabled(false);
            return;
        }
        if (Bukkit.getPluginManager().isPluginEnabled("Holograms"))
            Main.hologramManager = JavaPlugin.getPlugin(HologramPlugin.class).getHologramManager();
        Bukkit.getPluginManager().registerEvents(new onDeath(), this);
        Bukkit.getPluginManager().registerEvents(new onRespawn(), this);
        Bukkit.getPluginManager().registerEvents(new onClick(), this);
        Bukkit.getPluginManager().registerEvents(new onEntityExplode(), this);
        Bukkit.getPluginManager().registerEvents(new onBlockPlace(), this);
        Bukkit.getPluginManager().registerEvents(new onBlockBreak(), this);
        this.getCommand("gravesadmin").setExecutor(new GravesAdmin());
        this.getCommand("restoregrave").setExecutor(new RestoreGrave());
        this.saveDefaultConfig();
        disabled = this.getConfig().getBoolean("disabled");
        getLogger().info("Gravestones plugin Enabled!");
        getLogger().info("Author: 54mpenguin");
    }

    @Override
    public void onDisable() {
        for(UUID key : graves.keySet()){
            for(Gravestone grave : graves.get(key)){
                Gravestone.expire(grave);
            }
        }
    }
}
