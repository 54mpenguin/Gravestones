package me.fiftyfour.gravestones;

import me.fiftyfour.gravestones.commands.GravesAdmin;
import me.fiftyfour.gravestones.commands.RestoreGrave;
import me.fiftyfour.gravestones.events.onClick;
import me.fiftyfour.gravestones.events.onDeath;
import me.fiftyfour.gravestones.events.onEntityExplode;
import me.fiftyfour.gravestones.events.onRespawn;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class Main extends JavaPlugin {
    public static HashMap<UUID, ArrayList<Gravestone>> graves = new HashMap<>();
    public static ArrayList<Location> gravesloc = new ArrayList<>();
    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(new onDeath(), this);
        Bukkit.getPluginManager().registerEvents(new onRespawn(), this);
        Bukkit.getPluginManager().registerEvents(new onClick(), this);
        Bukkit.getPluginManager().registerEvents(new onEntityExplode(), this);
        this.getCommand("gravesadmin").setExecutor(new GravesAdmin());
        this.getCommand("restoregrave").setExecutor(new RestoreGrave());
        loadConfig();
        if (!Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays")) {
            getLogger().severe("*** HolographicDisplays is not installed or not enabled. ***");
            getLogger().severe("*** This plugin will be disabled. ***");
            this.setEnabled(false);
        }
    }

    @Override
    public void onDisable() {
        for(UUID key : graves.keySet()){
            for(Gravestone grave : graves.get(key)){
                Gravestone.expire(grave);
            }
        }
    }

    private void loadConfig() {
        getConfig().options().copyDefaults(true);
        saveConfig();
    }
}
