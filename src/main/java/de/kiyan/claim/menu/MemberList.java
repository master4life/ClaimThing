package de.kiyan.claim.menu;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.mattstudios.mfgui.gui.components.util.ItemBuilder;
import me.mattstudios.mfgui.gui.guis.GuiItem;
import me.mattstudios.mfgui.gui.guis.PaginatedGui;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class MemberList {
    public void openMenu( Player player, ProtectedRegion region ) {
        PaginatedGui menu = new PaginatedGui( 4, "§2Lists members" );

        for( UUID uuid : region.getMembers().getUniqueIds() )
        {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer( uuid );
            GuiItem added = new GuiItem(
                    ItemBuilder.from( Material.PLAYER_HEAD ).setName( "§a§l" + offlinePlayer.getName() ).setSkullOwner( offlinePlayer ).build(), event -> {
                if( event.isRightClick() )
                {
                    Player eventPlayer = (Player) event.getWhoClicked();
                    region.getMembers().removePlayer( uuid );
                    OfflinePlayer offPLayer = Bukkit.getOfflinePlayer( uuid );
                    if( offPLayer.isOnline() )
                        ((Player) offPLayer).sendMessage( "§cYou was removed as Member from §e" + eventPlayer.getName() + "§c claim called: §5" + region.getId().split( "_" )[2] );

                    openMenu( eventPlayer, region );
                }
            } );

            menu.addItem( added );
        }

        menu.setDefaultClickAction( event -> event.setCancelled( true ) );
        menu.getFiller().fillBottom( ItemBuilder.from( Material.BLACK_STAINED_GLASS_PANE ).setName( "§7" ).asGuiItem() );

        menu.setItem( 4, 3, ItemBuilder.from( Material.PLAYER_HEAD ).setName( "§eBack" ).setSkullTexture( "ewogICJ0aW1lc3RhbXAiIDogMTYwMDk5NjI2NTkwOCwKICAicHJvZmlsZUlkIiA6ICJhNjhmMGI2NDhkMTQ0MDAwYTk1ZjRiOWJhMTRmOGRmOSIsCiAgInByb2ZpbGVOYW1lIiA6ICJNSEZfQXJyb3dMZWZ0IiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2Y3YWFjYWQxOTNlMjIyNjk3MWVkOTUzMDJkYmE0MzM0MzhiZTQ2NDRmYmFiNWViZjgxODA1NDA2MTY2N2ZiZTIiCiAgICB9CiAgfQp9" ).asGuiItem( event -> menu.previous() ) );
        menu.setItem( 4, 4, ItemBuilder.from( Material.BIRCH_SIGN ).setName( "§aAdd new member" ).asGuiItem( event -> listPlayers( (Player) event.getWhoClicked(), region )  ) );
        menu.setItem( 4, 5, ItemBuilder.from( Material.PLAYER_HEAD ).setName( "§eBack to claim managing" ).setSkullTexture( "ewogICJ0aW1lc3RhbXAiIDogMTYwMDk5NjI1NDg4MiwKICAicHJvZmlsZUlkIiA6ICI2OGY1OWI5YjViMGI0YjA1YTlmMmUxZDE0MDVhYTM0OCIsCiAgInByb2ZpbGVOYW1lIiA6ICJNSEZfQXJyb3dEb3duIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2ZlM2Q3NTVjZWNiYjEzYTM5ZThlOTM1NDgyM2E5YTAyYTAxZGNlMGFjYTY4ZmZkNDJlM2VhOWE5ZDI5ZTJkZjIiCiAgICB9CiAgfQp9" ).asGuiItem( event -> new SelectClaim().openMenu( ( Player ) event.getWhoClicked(), region ) ) );
        menu.setItem( 4, 6, ItemBuilder.from( Material.PAPER ).setName( "§e§lGuide:" ).setLore( "§7Members can interact with your claim", "§7but they cant add ore remove", "members, owners or change flags.", "", "§f§LRIGHT CLICK", "  §5Remove player on the list" ).asGuiItem() );
        menu.setItem( 4, 7, ItemBuilder.from( Material.PLAYER_HEAD ).setName( "§eNext" ).setSkullTexture( "ewogICJ0aW1lc3RhbXAiIDogMTYwMDk5NjI3NjA3OSwKICAicHJvZmlsZUlkIiA6ICI1MGM4NTEwYjVlYTA0ZDYwYmU5YTdkNTQyZDZjZDE1NiIsCiAgInByb2ZpbGVOYW1lIiA6ICJNSEZfQXJyb3dSaWdodCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9kMzRlZjA2Mzg1MzcyMjJiMjBmNDgwNjk0ZGFkYzBmODVmYmUwNzU5ZDU4MWFhN2ZjZGYyZTQzMTM5Mzc3MTU4IgogICAgfQogIH0KfQ==" ).asGuiItem( event -> menu.next() ) );
        menu.open( player );
    }

    public void listPlayers( Player player, ProtectedRegion region )
    {
        PaginatedGui menu = new PaginatedGui( 4, "§2Choose a player" );

        for( Player all : Bukkit.getOnlinePlayers() )
        {
            if( all.equals( player ) || all.getUniqueId().equals( UUID.fromString( region.getId().split( "_" )[1] ) ) ) continue;
            menu.addItem( ItemBuilder.from( Material.PLAYER_HEAD ).setName( "§a§l" + all.getName() ).setSkullOwner( all ).asGuiItem( event ->
            {
                if( all.isOnline() )
                {
                    Player eventPlayer = (Player) event.getWhoClicked();String claimName = region.getId().split( "_" )[2];
                    eventPlayer.sendMessage( "§aYou added §e" + all.getDisplayName() + "§a to your claim: §b" + claimName );
                    all.sendMessage( "§aYou was added as Member to §b" + eventPlayer.getName() + "'s§a claim called: §b" + claimName  );
                    region.getMembers().addPlayer( all.getUniqueId() );

                    openMenu( player, region );
                } else
                    menu.update();

            }) );
        }

        menu.setDefaultClickAction( event -> event.setCancelled( true ) );
        menu.getFiller().fillBottom( ItemBuilder.from( Material.BLACK_STAINED_GLASS_PANE ).setName( "§7" ).asGuiItem() );

        // Back to previous page
        menu.setItem( 4, 3, ItemBuilder.from( Material.PLAYER_HEAD ).setName( "§eBack" ).setSkullTexture( "ewogICJ0aW1lc3RhbXAiIDogMTYwMDk5NjI2NTkwOCwKICAicHJvZmlsZUlkIiA6ICJhNjhmMGI2NDhkMTQ0MDAwYTk1ZjRiOWJhMTRmOGRmOSIsCiAgInByb2ZpbGVOYW1lIiA6ICJNSEZfQXJyb3dMZWZ0IiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2Y3YWFjYWQxOTNlMjIyNjk3MWVkOTUzMDJkYmE0MzM0MzhiZTQ2NDRmYmFiNWViZjgxODA1NDA2MTY2N2ZiZTIiCiAgICB9CiAgfQp9" ).asGuiItem( event -> menu.previous() ) );
        // back to Ownerlist
        menu.setItem( 4, 5, ItemBuilder.from( Material.PLAYER_HEAD ).setName( "§eBack to members" ).setSkullTexture( "ewogICJ0aW1lc3RhbXAiIDogMTYwMDk5NjI1NDg4MiwKICAicHJvZmlsZUlkIiA6ICI2OGY1OWI5YjViMGI0YjA1YTlmMmUxZDE0MDVhYTM0OCIsCiAgInByb2ZpbGVOYW1lIiA6ICJNSEZfQXJyb3dEb3duIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2ZlM2Q3NTVjZWNiYjEzYTM5ZThlOTM1NDgyM2E5YTAyYTAxZGNlMGFjYTY4ZmZkNDJlM2VhOWE5ZDI5ZTJkZjIiCiAgICB9CiAgfQp9" ).asGuiItem( event -> openMenu( ( Player ) event.getWhoClicked(), region ) ) );
        // Next to next page
        menu.setItem( 4, 7, ItemBuilder.from( Material.PLAYER_HEAD ).setName( "§eNext" ).setSkullTexture( "ewogICJ0aW1lc3RhbXAiIDogMTYwMDk5NjI3NjA3OSwKICAicHJvZmlsZUlkIiA6ICI1MGM4NTEwYjVlYTA0ZDYwYmU5YTdkNTQyZDZjZDE1NiIsCiAgInByb2ZpbGVOYW1lIiA6ICJNSEZfQXJyb3dSaWdodCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9kMzRlZjA2Mzg1MzcyMjJiMjBmNDgwNjk0ZGFkYzBmODVmYmUwNzU5ZDU4MWFhN2ZjZGYyZTQzMTM5Mzc3MTU4IgogICAgfQogIH0KfQ==" ).asGuiItem( event -> menu.next() ) );
        menu.open( player );
    }
}
