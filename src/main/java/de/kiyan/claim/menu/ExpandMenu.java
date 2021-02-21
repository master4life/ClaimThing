package de.kiyan.claim.menu;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import de.kiyan.claim.Claim;
import de.kiyan.claim.Config;
import de.kiyan.claim.runnable.ParticleEffects;
import de.kiyan.claim.util.SkullType;
import de.kiyan.claim.util.Utils;
import me.mattstudios.mfgui.gui.components.util.ItemBuilder;
import me.mattstudios.mfgui.gui.guis.Gui;
import me.mattstudios.mfgui.gui.guis.GuiItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ExpandMenu
{
    private enum Direction
    {
        NORTH,
        SOUTH,
        WEST,
        EAST;
    }

    public void openMenu(Player player, ProtectedRegion region)
    {
        Gui menu = new Gui(3, "Expansion");

        GuiItem addNorth = new GuiItem(ItemBuilder.from(Material.PLAYER_HEAD).setName("§5Expand: §2§lNorth").setSkullTexture(SkullType.NORTH.getTexture()).build(), event -> expand((Player) event.getWhoClicked(), region, Direction.NORTH, false));
        GuiItem addSouth = new GuiItem(ItemBuilder.from(Material.PLAYER_HEAD).setName("§5Expand: §2§lSouth").setSkullTexture(SkullType.SOUTH.getTexture()).build(), event -> expand((Player) event.getWhoClicked(), region, Direction.SOUTH, false));
        GuiItem addWest = new GuiItem(ItemBuilder.from(Material.PLAYER_HEAD).setName("§5Expand: §2§lWest").setSkullTexture(SkullType.WEST.getTexture()).build(), event -> expand((Player) event.getWhoClicked(), region, Direction.WEST, false));
        GuiItem addEast = new GuiItem(ItemBuilder.from(Material.PLAYER_HEAD).setName("§5Expand: §2§lEast").setSkullTexture(SkullType.EAST.getTexture()).build(), event -> expand((Player) event.getWhoClicked(), region, Direction.EAST, false));

        GuiItem removeNorth = new GuiItem(ItemBuilder.from(Material.PLAYER_HEAD).setName("§5Expand: §2§lNorth").setSkullTexture(SkullType.SOUTH.getTexture()).build(), event -> expand((Player) event.getWhoClicked(), region, Direction.NORTH, true));
        GuiItem removeSouth = new GuiItem(ItemBuilder.from(Material.PLAYER_HEAD).setName("§5Expand: §2§lSouth").setSkullTexture(SkullType.NORTH.getTexture()).build(), event -> expand((Player) event.getWhoClicked(), region, Direction.SOUTH, true));
        GuiItem removeWest = new GuiItem(ItemBuilder.from(Material.PLAYER_HEAD).setName("§5Expand: §2§lWest").setSkullTexture(SkullType.EAST.getTexture()).build(), event -> expand((Player) event.getWhoClicked(), region, Direction.WEST, true));
        GuiItem removeEast = new GuiItem(ItemBuilder.from(Material.PLAYER_HEAD).setName("§5Expand: §2§lEast").setSkullTexture(SkullType.WEST.getTexture()).build(), event -> expand((Player) event.getWhoClicked(), region, Direction.EAST, true));

        GuiItem info = new GuiItem(ItemBuilder.from(Material.PLAYER_HEAD).setName("§aYou are looking: [§r§l" + player.getFacing().toString() + "§a]").setSkullOwner(player).build());

        GuiItem back = new GuiItem(ItemBuilder.from(Material.PLAYER_HEAD).setName("§cBack").setSkullTexture(SkullType.DOWN.getTexture()).build(), event -> new SelectClaim().openMenu((Player) event.getWhoClicked(), region));

        menu.getFiller().fill(ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).setName("§r").asGuiItem());
        menu.setDefaultClickAction(event -> event.setCancelled(true));

        menu.setItem(2, 5, info);

        menu.setItem(3, 5, back);
        menu.setItem(1, 3, addNorth);
        menu.setItem(2, 2, addWest);
        menu.setItem(2, 4, addEast);
        menu.setItem(3, 3, addSouth);

        menu.setItem(1, 7, removeNorth);
        menu.setItem(2, 6, removeWest);
        menu.setItem(2, 8, removeEast);
        menu.setItem(3, 7, removeSouth);

        menu.open(player);
    }

    public void expand(Player player, ProtectedRegion region, Direction direction, boolean shorten)
    {
        final BlockVector3 min = region.getMinimumPoint();
        final BlockVector3 max = region.getMaximumPoint();

        ProtectedRegion newRegion = null;
        if (direction == Direction.NORTH)
            newRegion = new ProtectedCuboidRegion(region.getId(), !shorten ? min.subtract(0, 0, 1) : min.add(0, 0, 1), max);
        else if (direction == Direction.SOUTH)
            newRegion = new ProtectedCuboidRegion(region.getId(), min, !shorten ? max.add(0, 0, 1) : max.subtract(0, 0, 1));
        else if (direction == Direction.WEST)
            newRegion = new ProtectedCuboidRegion(region.getId(), !shorten ? min.subtract(1, 0, 0) : min.add(1, 0, 0), max);
        else if (direction == Direction.EAST)
            newRegion = new ProtectedCuboidRegion(region.getId(), min, !shorten ? max.add(1, 0, 0) : max.subtract(1, 0, 0));

        final RegionManager regionManager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(Utils.getRegionWorld(region)));
        for (final ProtectedRegion overlapingClaim : newRegion.getIntersectingRegions(regionManager.getRegions().values()))
        {
            if (overlapingClaim.getParent() == null && !overlapingClaim.getId().equals(newRegion.getId()))
            {
                if (region.getParent() != null && region.getParent().getId().equals(overlapingClaim.getId()))
                {
                    player.sendMessage("You can not expand a child claim!");
                    return;
                } else
                {
                    player.sendMessage("Expansion failed! Claim overlaping claim: " + overlapingClaim.getId().substring(43));
                    return;
                }
            }
        }

        final int oldVolume = new Config(Claim.getInstance()).getBlockSize(player);
        final int newVolume = newRegion.volume() / 256;

        final BlockVector3 newMin = newRegion.getMinimumPoint();
        final BlockVector3 newMax = newRegion.getMaximumPoint();

        if (        (newMax.getX() - newMin.getX() <= 5)
                ||  (newMax.getZ() - newMin.getZ() <= 5))
        {
            player.sendMessage("§cClaim cannot be reduced this shorten that small.");
            return;
        }
        if (newVolume <= oldVolume)
        {
            newRegion.copyFrom(region);
            regionManager.removeRegion(region.getId());
            regionManager.addRegion(newRegion);
            player.sendMessage("§aClaim !" + (!shorten ? "expanded!" : "shorten!"));
            openMenu(player, newRegion);
            new ParticleEffects(player, region);
        } else
            player.sendMessage("§Your amount of block sizes aren't enough for this expand!");
    }
}
