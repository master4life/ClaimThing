package de.kiyan.claim.menu;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import me.mattstudios.mfgui.gui.components.util.ItemBuilder;
import me.mattstudios.mfgui.gui.guis.Gui;
import me.mattstudios.mfgui.gui.guis.GuiItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class DeleteClaim
{
    // Confirmation window YES - NO
    public void openMenu(Player player, ProtectedRegion region)
    {
        Gui menu = new Gui(3, "§4§lARE YOU SURE?");

        GuiItem delete = new GuiItem(ItemBuilder.from(Material.RED_CONCRETE).setName("§4Delete!").setLore("§4This cant be undone!").build(), event ->
        {
            final RegionContainer regionContainer = WorldGuard.getInstance().getPlatform().getRegionContainer();
            final RegionManager regionManager = regionContainer.get(BukkitAdapter.adapt(player.getWorld()));
            Player eventPlayer = (Player) event.getWhoClicked();

            if (region != null && regionManager != null)
            {
                eventPlayer.sendMessage("You have successfully removed the claim: " + region.getId().split("_")[2]);

                regionManager.removeRegion(region.getId());
            }

            new MainMenu().openMenu(eventPlayer);
        });

        GuiItem back = new GuiItem(ItemBuilder.from(Material.GREEN_CONCRETE).setName("§aBack!").setLore("§eAlmost!").build(), event ->
        {
            new SelectClaim().openMenu(player, region);
        });

        menu.getFiller().fillBorder(ItemBuilder.from(Material.GRAY_STAINED_GLASS_PANE).setName("§7").asGuiItem());

        menu.setDefaultClickAction(event -> event.setCancelled(true));
        menu.setItem(11, delete);
        menu.setItem(15, back);
        menu.open(player);
    }
}
