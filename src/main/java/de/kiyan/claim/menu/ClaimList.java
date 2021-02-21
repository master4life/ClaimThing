package de.kiyan.claim.menu;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import de.kiyan.claim.Claim;
import de.kiyan.claim.Config;
import de.kiyan.claim.util.SkullType;
import de.kiyan.claim.util.StringUtils;
import me.mattstudios.mfgui.gui.components.util.ItemBuilder;
import me.mattstudios.mfgui.gui.guis.PaginatedGui;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ClaimList {
    public void openMenu(Player player) {
        PaginatedGui menu = new PaginatedGui(4, "§5§lLists claims");

        final RegionContainer regionContainer = WorldGuard.getInstance().getPlatform().getRegionContainer();
        for (World world : new Config(Claim.getInstance()).getWorlds()) {
            final RegionManager regionManager = regionContainer.get(BukkitAdapter.adapt(world));
            for (final ProtectedRegion region : regionManager.getRegions().values()) {
                String[] wholeClaim = region.getId().split("_");
                if (wholeClaim[0].equalsIgnoreCase("claim") && region.getOwners().contains(player.getUniqueId())) {
                    OfflinePlayer offPlayer = Bukkit.getOfflinePlayer(UUID.fromString(wholeClaim[1]));
                    menu.addItem(ItemBuilder.from(offPlayer.getUniqueId().equals(player.getUniqueId()) ? Material.GRASS_BLOCK : Material.GRASS_PATH)
                            .setName("§e§lClaim: §3§l" + wholeClaim[2])
                            .setLore("§e§lUsed Blocks: §f" + (region.volume() / 256),
                                    "§e§lClaim belongs: §f" + offPlayer.getName(),
                                    "§e§lOwners: §f" + StringUtils.getOwners(region, 60),
                                    "§e§lMembers: §f" + StringUtils.getMembers(region, 60)
                            )
                            .asGuiItem(event -> new SelectClaim().openMenu(player, region)));
                }
            }
        }

        // fills the bottom of inventory with glass panels
        menu.getFiller().fillBottom(ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).setName("§7").asGuiItem());

        // disable item movements
        menu.setDefaultClickAction(event -> event.setCancelled(true));

        menu.setItem(4, 3, ItemBuilder.from(Material.PLAYER_HEAD).setName("§eBack").setSkullTexture(SkullType.LEFT.getTexture()).asGuiItem(event1 -> menu.previous()));
        // back
        menu.setItem(4, 5, ItemBuilder.from(Material.PLAYER_HEAD).setName("§eBack to main menu").setSkullTexture(SkullType.DOWN.getTexture()).asGuiItem(event -> new MainMenu().openMenu((Player) event.getWhoClicked())));
        // next page
        menu.setItem(4, 7, ItemBuilder.from(Material.PLAYER_HEAD).setName("§eNext").setSkullTexture(SkullType.RIGHT.getTexture()).asGuiItem(event -> menu.next() ));

        // opens prepared menu
        menu.open(player);
    }
}
