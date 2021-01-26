package de.kiyan.claim.menu;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import de.kiyan.claim.Claim;
import de.kiyan.claim.Config;
import de.kiyan.claim.runnable.ParticleEffects;
import de.kiyan.claim.util.StringUtils;
import de.kiyan.claim.util.Utils;
import me.mattstudios.mfgui.gui.components.util.ItemBuilder;
import me.mattstudios.mfgui.gui.guis.Gui;
import me.mattstudios.mfgui.gui.guis.GuiItem;
import org.bukkit.*;
import org.bukkit.entity.Player;

public class SelectClaim {
    public void openMenu(Player player, ProtectedRegion region) {
        String[] claimName = region.getId().split("_");

        Gui menu = new Gui(2, "§5Managing " + claimName[2]);

        GuiItem info = new GuiItem(ItemBuilder.from(Material.GRASS_BLOCK)
                .setName("§aYour select claim: §7" + claimName[2])
                .setLore("§e§lUsed Blocks: §f" + (region.volume() / 256),
                        "§e§lOwners: §f" + StringUtils.getOwners(region, 60),
                        "§e§lMembers: §f" + StringUtils.getOwners(region, 60)
                )
                .build());

        GuiItem owners = new GuiItem(ItemBuilder.from(Material.PLAYER_HEAD)
                .setName("§2Manage Owners")
                .setLore("§7List all owners of this claim")
                .setSkullOwner(player)
                .build(), event -> new OwnerList().openMenu((Player) event.getWhoClicked(), region));

        GuiItem members = new GuiItem(ItemBuilder.from(Material.CHEST)
                .setName("§bManage Members")
                .setLore("§7list all members of this claim")
                .build(), event -> new MemberList().openMenu((Player) event.getWhoClicked(), region));

        GuiItem flages = new GuiItem(ItemBuilder.from(Material.SHIELD)
                .setName("§eManage Flags")
                .setLore("§7Organize your flags")
                .build(), event -> new FlagManager().openMenu((Player) event.getWhoClicked(), region, 0));

        GuiItem show = new GuiItem(ItemBuilder.from(Material.ENDER_EYE)
                .setName("§eShow me boundrias")
                .setLore("§7Particles will visual your claim edges")
                .build(), event -> {
            new ParticleEffects((Player) event.getWhoClicked(), region).run();
            ((Player) event.getWhoClicked()).closeInventory();
        });

        GuiItem back = ItemBuilder.from(Material.PLAYER_HEAD)
                .setName("§7Back to claim list")
                .setSkullTexture("ewogICJ0aW1lc3RhbXAiIDogMTYwMDk5NjI1NDg4MiwKICAicHJvZmlsZUlkIiA6ICI2OGY1OWI5YjViMGI0YjA1YTlmMmUxZDE0MDVhYTM0OCIsCiAgInByb2ZpbGVOYW1lIiA6ICJNSEZfQXJyb3dEb3duIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2ZlM2Q3NTVjZWNiYjEzYTM5ZThlOTM1NDgyM2E5YTAyYTAxZGNlMGFjYTY4ZmZkNDJlM2VhOWE5ZDI5ZTJkZjIiCiAgICB9CiAgfQp9")
                .asGuiItem(event -> new ClaimList().openMenu((Player) event.getWhoClicked()));

        GuiItem delete = new GuiItem(ItemBuilder.from(Material.BARRIER)
                .setName("§eRemove this claim")
                .setLore("§4§lCAUTION!", "§cThis cant be undone!")
                .build(), event -> new DeleteClaim().openMenu((Player) event.getWhoClicked(), region));

        menu.setDefaultClickAction(event -> event.setCancelled(true));

        menu.getFiller().fillBottom(ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).setName("§r").asGuiItem());

        menu.setItem(0, info);
        menu.setItem(2, owners);
        menu.setItem(4, members);
        menu.setItem(6, show);
        menu.setItem(8, flages);
        menu.setItem(9, back);
        com.sk89q.worldedit.util.Location wLoc = region.getFlag(Flags.TELE_LOC);
        if (wLoc != null) {
            Location loc = new Location(Utils.getRegionWorld(region), wLoc.getBlockX(), wLoc.getBlockY(), wLoc.getBlockZ());
            GuiItem teleport = new GuiItem(ItemBuilder.from(Material.ENDER_PEARL).setName("§2Teleport to region")
                    .setLore("§4You might get stuck!", "", "§f§lRight Click to reset").build(), event ->
            {
                Player eventPlayer = (Player) event.getWhoClicked();
                if (event.isLeftClick()) {
                    eventPlayer.teleport(loc);
                    eventPlayer.sendMessage("§aYou teleported to your Claim: §b" + claimName[2]);
                }
                if (event.isRightClick()) {
                    region.getFlags().remove(Flags.TELE_LOC);
                    eventPlayer.sendMessage("§cYou reseted your home");
                    eventPlayer.closeInventory();
                }
            });

            menu.setItem(13, teleport);
        } else {
            wLoc = BukkitAdapter.adapt(player.getLocation());
            com.sk89q.worldedit.util.Location finalWLoc = wLoc;
            GuiItem teleport = new GuiItem(ItemBuilder.from(Material.ENDER_PEARL).setName("§aSet your home selection")
                    .setLore("§7Be sure to be in your claim").build(), event -> {
                Player eventPlayer = (Player) event.getWhoClicked();
                if (region.contains(BlockVector2.at(eventPlayer.getLocation().getBlockX(), eventPlayer.getLocation().getBlockZ()))) {
                    region.getFlags().put(Flags.TELE_LOC, finalWLoc);
                    eventPlayer.sendMessage("§2You set a home to your claim: §b" + claimName[2]);
                    eventPlayer.closeInventory();
                } else {
                    eventPlayer.sendMessage("§cYou must be in your claim to set a home!");

                }
            });

            menu.setItem(13, teleport);
        }

        if (player.isOp() || player.getUniqueId().toString().equalsIgnoreCase(claimName[1]))
            menu.setItem(17, delete);
        menu.open(player);
    }
}
