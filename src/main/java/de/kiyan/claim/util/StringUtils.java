package de.kiyan.claim.util;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

public class StringUtils {
    // Converts a string line out of owners Name | Name2 | Name 3
    public static String getOwners(ProtectedRegion region, int limitSize) {
        String owners = "";
        for (UUID uuid : region.getOwners().getUniqueIds()) {
            OfflinePlayer offPlayer = Bukkit.getOfflinePlayer(uuid);
            owners = owners.equals("") ? ("§f" + offPlayer.getName()) : (owners + " §7| §f" + offPlayer.getName());
        }

        if (!owners.equals("")) {
            if (limitSize > 0)
                owners = (owners.length() > limitSize) ? owners.substring(0, limitSize).concat(" …") : owners;
        }
        return owners;
    }

    // Converts a string line out of owners Name | Name2 | Name 3
    public static String getMembers(ProtectedRegion region, int limitSize) {
        String members = "";
        for (UUID uuid : region.getMembers().getUniqueIds()) {
            OfflinePlayer offPlayer = Bukkit.getOfflinePlayer(uuid);
            members = members.equals("") ? ("§f" + offPlayer.getName()) : (members + " §7| §f" + offPlayer.getName());
        }

        if (!members.equals("")) {
            if (limitSize > 0)
                members = (members.length() > limitSize) ? members.substring(0, limitSize).concat(" …") : members;
        }
        return members;
    }

}
