package de.kiyan.claim;

import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Config
{
    private final Claim instance;

    public Config(Claim plugin)
    {
        this.instance = plugin;
    }

    public void prepareConfig()
    {
        instance.saveDefaultConfig();
        instance.reloadConfig();
    }

    public int getCap()
    {
        return instance.getConfig().getInt("gap-limit");
    }

    public int getBlockSize(Player player)
    {
        int configSize = instance.getConfig().getInt("block-size");
        int played_in_minute = ((player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20) / 60);
        configSize += Math.min(played_in_minute, getCap());

        return configSize;
    }

    public boolean getTeleportation()
    {
        return instance.getConfig().getBoolean("allow-individual-tp");
    }

    public String getPermissions(int which)
    {
        return which == 0 ? instance.getConfig().getString("admin-perm") : instance.getConfig().getString("donor-perm");
    }

    public List<String> getBannedFlags()
    {
        return new ArrayList<>(instance.getConfig().getStringList("banned-flags"));
    }

    public List<String> getDonorFlags()
    {
        return new ArrayList<>(instance.getConfig().getStringList("donor-flags"));
    }

    public List<World> getWorlds()
    {
        List<World> list = new ArrayList<>();

        for (String worldName : instance.getConfig().getStringList("worlds"))
        {
            if (Bukkit.getWorld(worldName) != null)
            {
                list.add(Bukkit.getWorld(worldName));
            }
        }
        //if non worlds are legit then he will just apply all server-wide worlds
        if (list.size() == 0) list = Bukkit.getWorlds();
        return list;
    }
}
