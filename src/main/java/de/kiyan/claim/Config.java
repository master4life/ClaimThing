package de.kiyan.claim;

import org.apache.commons.compress.utils.Lists;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;

public class Config {
    private final Claim instance;

    public Config(Claim plugin) {
        this.instance = plugin;
    }

    public void prepareConfig() {
        instance.saveDefaultConfig();
        instance.reloadConfig();
    }

    public int getBlockSize() {
        return instance.getConfig().getInt("block-size");
    }

    public List<String> getBannedFlags() {
        return new ArrayList<>(instance.getConfig().getStringList("banned-flags"));
    }

    public List<World> getWorlds() {
        List<World> list = new ArrayList<>();

        for (String worldName : instance.getConfig().getStringList("worlds")) {
            if (Bukkit.getWorld(worldName) != null) {
                list.add(Bukkit.getWorld(worldName));
            }
        }
        //if non worlds are legit then he will just apply all server-wide worlds
        if (list.size() == 0) list = Bukkit.getWorlds();
        return list;
    }
}
