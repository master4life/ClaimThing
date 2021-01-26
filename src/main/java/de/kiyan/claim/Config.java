package de.kiyan.claim;

import org.apache.commons.compress.utils.Lists;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.World;
import org.bukkit.entity.Player;

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

    public int getBlockSize(Player player) {
        int configSize = instance.getConfig().getInt("block-size");
        configSize += ((player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20) / 60);
        return configSize;
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
