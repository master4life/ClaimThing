package de.kiyan.claim.menu;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.RegionSelector;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import de.kiyan.claim.Claim;
import de.kiyan.claim.Config;
import de.kiyan.claim.api.AnvilGUI;
import de.kiyan.claim.util.Utils;
import me.mattstudios.mfgui.gui.components.GuiType;
import me.mattstudios.mfgui.gui.components.util.ItemBuilder;
import me.mattstudios.mfgui.gui.guis.Gui;
import me.mattstudios.mfgui.gui.guis.GuiItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static de.kiyan.claim.Claim.getWorldedit;

public class MainMenu {

    public void openMenu(Player player) {
        final RegionContainer regionContainer = WorldGuard.getInstance().getPlatform().getRegionContainer();
        final RegionManager regionManager = regionContainer.get(BukkitAdapter.adapt(player.getWorld()));

        Gui menu = new Gui(GuiType.HOPPER, "§aClaiming");
        int counter = 0;
        int blockSize = 0;
        if (regionManager != null) {
            for (ProtectedRegion region : regionManager.getRegions().values()) {
                if (region.getId().contains("claim_" + player.getUniqueId().toString())) {
                    counter++;
                    blockSize = blockSize + (region.volume() / 256);
                }
            }
        }

        GuiItem playerHead = new GuiItem(ItemBuilder.from(Material.PLAYER_HEAD)
                .setSkullOwner(Bukkit.getOfflinePlayer(player.getUniqueId()))
                .setLore("§aYou got §e§l" + counter + "§a claims so far",
                        "§aYou used §f§l" + blockSize + "§a blocks from §f§l" + new Config(Claim.getInstance()).getBlockSize() + "§a blocks.")
                .build());

        final RegionSelector selection = getWorldedit().getSession(player).getRegionSelector(BukkitAdapter.adapt(player.getWorld()));

        BlockVector3 minimum;
        BlockVector3 maximum;

        try {
            if (!selection.isDefined())
                minimum = selection.getPrimaryPosition();
            else
                minimum = selection.getRegion().getMinimumPoint();
        } catch (IncompleteRegionException e) {
            minimum = null;
        }

        try {
            maximum = selection.getRegion().getMaximumPoint();
        } catch (IncompleteRegionException e) {
            maximum = null;
        }

        /*
              You gets an Wand to select an area
         */
        GuiItem select = new GuiItem(new ItemBuilder(Material.WOODEN_AXE).setName("§5Get this WE Tool").setLore("", "§7Select an designated area", "§5POS1: " + (minimum == null ? "§5Not set yet" : minimum.toString()), "§5POS2: " + (maximum == null ? "§5Not set yet" : maximum.toString()), "").build(),
                event -> {
                    Player eventPlayer = (Player) event.getWhoClicked();
                    boolean bWoodAxe = false;
                    for (ItemStack is : eventPlayer.getInventory().getContents())
                        if (is != null)
                            if (is.getType().equals(Material.WOODEN_AXE))
                                bWoodAxe = true;

                    if (!bWoodAxe)
                        eventPlayer.getInventory().addItem(new ItemStack(Material.WOODEN_AXE));

                    eventPlayer.sendMessage("§e§lSelect an area.§a Once you are done go back to /claim and create your claim");
                    eventPlayer.closeInventory();
                });

        /*

         */
        GuiItem create = ItemBuilder.from(Material.GRASS_BLOCK)
                .setLore("§f§l-Create your Land-", "", "§7Be sure to select your area before")
                .asGuiItem(event -> {
                    if (!selection.isDefined()) {
                        event.getWhoClicked().sendMessage("Please make select a zone first");
                        return;
                    }
                    Player eventPlayer = (Player) event.getWhoClicked();

                    AnvilGUI anvil = new AnvilGUI(eventPlayer, new AnvilGUI.AnvilClickEventHandler() {
                        @Override
                        public void onAnvilClick(AnvilGUI.AnvilClickEvent event) {
                            if (event.getSlot() == AnvilGUI.AnvilSlot.OUTPUT && event.hasText()) {
                                BlockVector3 p1, p2;
                                try {
                                    p1 = selection.getRegion().getMinimumPoint();
                                    p2 = selection.getRegion().getMaximumPoint();
                                } catch (IncompleteRegionException e) {
                                    p1 = null;
                                    p2 = null;
                                }
                                ProtectedRegion parentRegion = null;
                                final BlockVector2 pos1 = BlockVector2.at(p1.toBlockVector2().getX(), p1.toBlockVector2().getZ());
                                final BlockVector2 pos2 = BlockVector2.at(p2.toBlockVector2().getX(), p2.toBlockVector2().getZ());
                                final BlockVector2 pos3 = BlockVector2.at(p1.toBlockVector2().getX(), p2.toBlockVector2().getZ());
                                final BlockVector2 pos4 = BlockVector2.at(p2.toBlockVector2().getX(), p1.toBlockVector2().getZ());
                                final List<BlockVector2> points = Lists.newArrayList(pos1, pos2, pos3, pos4);
                                for (final ProtectedRegion region : regionManager.getRegions().values()) {
                                    if (region.containsAny(points)) {
                                        if (region.contains(points.get(0)) && region.contains(points.get(1))
                                                && region.contains(points.get(2)) && region.contains(points.get(3))) {
                                            if (region.getOwners().contains(eventPlayer.getName())) {
                                                if (region.getParent() == null) {
                                                    parentRegion = region;
                                                    continue;
                                                } else {
                                                    eventPlayer.sendMessage(" Your claim is overlaping with another claim of yours!");
                                                    return;
                                                }
                                            } else {
                                                eventPlayer.sendMessage("Claim is overlaping with another claim! (§4" + region.getId() + "§f)");
                                                return;
                                            }
                                        } else {
                                            eventPlayer.sendMessage("Claim overlaps with another claim! (" + region.getId().split("_" + player.getUniqueId() + "_")[1] + ")");
                                            return;
                                        }
                                    }
                                }

                                String claimName = event.getText();
                                if (!Utils.isClaimed("claim_" + eventPlayer.getUniqueId().toString() + "_" + claimName)) {
                                    final ProtectedRegion region = new ProtectedCuboidRegion("claim_" + eventPlayer.getUniqueId().toString() + "_" + claimName,
                                            BlockVector3.at(p1.getBlockX(), 0, p1.getBlockZ()), BlockVector3.at(p2.getBlockX(), 255, p2.getBlockZ()));
                                    final List<ProtectedRegion> overlapingClaims = region.getIntersectingRegions(regionManager.getRegions().values());
                                    if (parentRegion != null) {
                                        try {
                                            region.setParent(parentRegion);
                                        } catch (ProtectedRegion.CircularInheritanceException e) {
                                            e.printStackTrace();
                                        }
                                        region.setPriority(2);
                                        region.setFlags(parentRegion.getFlags());
                                    } else {
                                        if (overlapingClaims.size() != 0) {
                                            eventPlayer.sendMessage("Claim is overlaping with another claim!");
                                            return;
                                        }
                                        region.setPriority(1);
                                    }
                                    int regionSize = (region.volume() / 256);
                                    for (ProtectedRegion calc : regionManager.getRegions().values()) {
                                        if (calc.getId().contains("claim_" + eventPlayer.getUniqueId().toString())) {
                                            regionSize += (calc.volume() / 256);
                                        }
                                    }

                                    // Checks if the zone is smaller than 6x6
                                    if ((p2.getX() - p1.getX() >= 5) && (p2.getZ() - p1.getZ() >= 5)) {
                                        // Checks if you exceeded your claim
                                        if (regionSize <= new Config(Claim.getInstance()).getBlockSize()) {
                                            regionManager.addRegion(region);
                                            region.getOwners().addPlayer(eventPlayer.getUniqueId());
                                            final Map<Flag<?>, Object> map = Maps.newHashMap();
                                            map.put(Flags.PVP, StateFlag.State.DENY);
                                            map.put(Flags.CREEPER_EXPLOSION, StateFlag.State.DENY);
                                            map.put(Flags.GREET_MESSAGE, "§aEntering §c§l" + claimName + "§a owned by §b" + Bukkit.getOfflinePlayer(UUID.fromString(region.getId().split("_")[1])).getName());
                                            map.put(Flags.FAREWELL_MESSAGE, "§cLeaving §b§l" + claimName + "§c owned by §b" + Bukkit.getOfflinePlayer(UUID.fromString(region.getId().split("_")[1])).getName());
                                            region.setFlags(map);
                                            eventPlayer.sendMessage("Claim " + region.getId().split("_" + eventPlayer.getUniqueId() + "_")[1] + " created!");
                                            openMenu(eventPlayer);
                                        } else {
                                            eventPlayer.sendMessage("This selection exceed your available block sizes!");
                                            eventPlayer.closeInventory();
                                        }
                                    } else {
                                        eventPlayer.sendMessage("Claim not big enough! Claims must be atleast 6x6 wide.");
                                        eventPlayer.closeInventory();
                                    }
                                } else
                                    eventPlayer.sendMessage("Claim with that name already exist");
                            }
                        }
                    });

                    ItemStack i = new ItemStack(Material.GRASS_BLOCK);
                    anvil.setSlot(AnvilGUI.AnvilSlot.INPUT_LEFT, i);
                    anvil.setSlotName(AnvilGUI.AnvilSlot.INPUT_LEFT, "§r");
                    anvil.setTitle("Type a new name");

                    anvil.open();
                });

        GuiItem listRegions = ItemBuilder.from(Material.BOOKSHELF).setName("§5Show all claims").asGuiItem(event -> {
            new ClaimList().openMenu((Player) event.getWhoClicked());
        });

        GuiItem guide = ItemBuilder.from(Material.BOOK)
                .setName("§eGuide: ")
                .setLore(
                        "§7(1)   §eClick at the Axe icon",
                        "§7(2)   §eSelect your locations",
                        "§7(3)   §eGo type /claim again",
                        "§7(4)   §eClick at the Grass icon to provide an name",
                        "§7(6)   §eYou're done!",
                        "§7You can use as many claims as you like",
                        "§7but dont exceed your limit of " + new Config(Claim.getInstance()).getBlockSize() + " blocks",
                        "§7and a claim has to be minimum §53x3")
                .asGuiItem();

        menu.setDefaultClickAction(event -> event.setCancelled(true));

        menu.setItem(0, playerHead);
        menu.setItem(1, guide);
        menu.setItem(2, create);
        menu.setItem(3, listRegions);
        menu.setItem(4, select);
        menu.open(player);
    }


}
