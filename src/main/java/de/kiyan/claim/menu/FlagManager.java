package de.kiyan.claim.menu;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import de.kiyan.claim.Claim;
import de.kiyan.claim.Config;
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
        menu.setItem(6, 3, ItemBuilder.from(Material.PLAYER_HEAD).setName("§eBack").setSkullTexture("ewogICJ0aW1lc3RhbXAiIDogMTYwMDk5NjI2NTkwOCwKICAicHJvZmlsZUlkIiA6ICJhNjhmMGI2NDhkMTQ0MDAwYTk1ZjRiOWJhMTRmOGRmOSIsCiAgInByb2ZpbGVOYW1lIiA6ICJNSEZfQXJyb3dMZWZ0IiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2Y3YWFjYWQxOTNlMjIyNjk3MWVkOTUzMDJkYmE0MzM0MzhiZTQ2NDRmYmFiNWViZjgxODA1NDA2MTY2N2ZiZTIiCiAgICB9CiAgfQp9").asGuiItem(event -> menu.previous()));
        // back to Ownerlist
        menu.setItem(6, 5, ItemBuilder.from(Material.PLAYER_HEAD).setName("§eBack to managing claim").setSkullTexture("ewogICJ0aW1lc3RhbXAiIDogMTYwMDk5NjI1NDg4MiwKICAicHJvZmlsZUlkIiA6ICI2OGY1OWI5YjViMGI0YjA1YTlmMmUxZDE0MDVhYTM0OCIsCiAgInByb2ZpbGVOYW1lIiA6ICJNSEZfQXJyb3dEb3duIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2ZlM2Q3NTVjZWNiYjEzYTM5ZThlOTM1NDgyM2E5YTAyYTAxZGNlMGFjYTY4ZmZkNDJlM2VhOWE5ZDI5ZTJkZjIiCiAgICB9CiAgfQp9").asGuiItem(event -> new SelectClaim().openMenu((Player) event.getWhoClicked(), region)));
        // Next to next page
        menu.setItem(6, 7, ItemBuilder.from(Material.PLAYER_HEAD).setName("§eNext").setSkullTexture("ewogICJ0aW1lc3RhbXAiIDogMTYwMDk5NjI3NjA3OSwKICAicHJvZmlsZUlkIiA6ICI1MGM4NTEwYjVlYTA0ZDYwYmU5YTdkNTQyZDZjZDE1NiIsCiAgInByb2ZpbGVOYW1lIiA6ICJNSEZfQXJyb3dSaWdodCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9kMzRlZjA2Mzg1MzcyMjJiMjBmNDgwNjk0ZGFkYzBmODVmYmUwNzU5ZDU4MWFhN2ZjZGYyZTQzMTM5Mzc3MTU4IgogICAgfQogIH0KfQ==").asGuiItem(event -> menu.next()));
        menu.open(player, page);
    }
}
