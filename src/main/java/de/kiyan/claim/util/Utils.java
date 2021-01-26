package de.kiyan.claim.util;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.World;

public class Utils {
    public static World getRegionWorld(ProtectedRegion region) {
        for (RegionManager regionManager : WorldGuard.getInstance().getPlatform().getRegionContainer().getLoaded())
            for (ProtectedRegion targeted : regionManager.getRegions().values())
                if (targeted.getId().equals(region.getId()))
                    return Bukkit.getWorld(regionManager.getName());

        return null;
    }

    public static boolean isClaimed(String id) {
        for (RegionManager regionManager : WorldGuard.getInstance().getPlatform().getRegionContainer().getLoaded())
            for (ProtectedRegion targeted : regionManager.getRegions().values())
                if (targeted.getId().equalsIgnoreCase(id))
                    return true;

        return false;
    }
}
