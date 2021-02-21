package de.kiyan.claim.menu;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import de.kiyan.claim.Claim;
import de.kiyan.claim.Config;
import de.kiyan.claim.util.SkullType;
import me.mattstudios.mfgui.gui.components.ScrollType;
import me.mattstudios.mfgui.gui.components.util.ItemBuilder;
import me.mattstudios.mfgui.gui.guis.ScrollingGui;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FlagManager
{
    public void openMenu(Player player, ProtectedRegion region, int page)
    {
        ScrollingGui menu = new ScrollingGui(6, "Manage your flags", ScrollType.HORIZONTAL);

        Iterator<Flag<?>> it = WorldGuard.getInstance().getFlagRegistry().iterator();
        List<Flag<?>> allFlags = new ArrayList<>();
        while (it.hasNext())
            allFlags.add(it.next());

        for (Flag<?> flag : allFlags)
        {
            if (!(flag instanceof StateFlag)) continue;

            Config config = new Config(Claim.getInstance());
            boolean skip = false;
            for (String string : config.getBannedFlags())
                if (flag.getName().contains(string))
                {
                    skip = true;
                    break;
                }

            List<String> donor = config.getDonorFlags();
            if (donor.size() > 0)
            {
                for (String string : donor)
                {
                    if (flag.getName().contains(string) && (!player.hasPermission(config.getPermissions(1)) || !player.isOp()))
                    {
                        skip = true;
                        break;
                    }
                }
            }

            if (skip) continue;

            menu.addItem(ItemBuilder.from(region.getFlag(flag) == null ? Material.WHITE_STAINED_GLASS_PANE : region.getFlag(flag) == StateFlag.State.ALLOW ? Material.GREEN_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE)
                    .setName("§e§l" + flag.getName())
                    .asGuiItem(event ->
                    {
                        if (event.isLeftClick())
                        {
                            if (region.getFlag(flag) == null)
                                region.setFlag(flag, null);

                            region.getFlags().put(flag, region.getFlag(flag) == StateFlag.State.ALLOW ? StateFlag.State.DENY : StateFlag.State.ALLOW);

                        }
                        if (event.isRightClick())
                        {
                            if (region.getFlag(flag) != null)
                            {
                                region.getFlags().remove(flag);
                            }
                        }

                        openMenu((Player) event.getWhoClicked(), region, menu.getCurrentPageNum());
                    }));
        }

        menu.setDefaultClickAction(event -> event.setCancelled(true));
        menu.getFiller().fillBottom(ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).setName("§7").asGuiItem());

        // Back to previous page
        menu.setItem(6, 3, ItemBuilder.from(Material.PLAYER_HEAD).setName("§eBack").setSkullTexture(SkullType.LEFT.getTexture()).asGuiItem(event -> menu.previous()));
        // back to Ownerlist
        menu.setItem(6, 5, ItemBuilder.from(Material.PLAYER_HEAD).setName("§eBack to managing claim").setSkullTexture(SkullType.DOWN.getTexture()).asGuiItem(event -> new SelectClaim().openMenu((Player) event.getWhoClicked(), region)));
        // Next to next page
        menu.setItem(6, 7, ItemBuilder.from(Material.PLAYER_HEAD).setName("§eNext").setSkullTexture(SkullType.RIGHT.getTexture()).asGuiItem(event -> menu.next()));
        menu.open(player, page);
    }
}
