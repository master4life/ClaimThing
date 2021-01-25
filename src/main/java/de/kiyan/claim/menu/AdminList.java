package de.kiyan.claim.menu;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import de.kiyan.claim.api.AnvilGUI;
import de.kiyan.claim.util.StringUtils;
import me.mattstudios.mfgui.gui.components.util.ItemBuilder;
import me.mattstudios.mfgui.gui.guis.PaginatedGui;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.UUID;

public class AdminList {

    public void openMenu( Player player ) {
        PaginatedGui menu = new PaginatedGui( 4, "§5§lLists all claims" );

        menu.setDefaultClickAction( event -> {
            if( event.getSlot() > 26 ) {
                event.setCancelled( true );
            }
        } );

        final RegionContainer regionContainer = WorldGuard.getInstance().getPlatform().getRegionContainer();
        final RegionManager regionManager = regionContainer.get( BukkitAdapter.adapt( player.getWorld() ) );

        for( final ProtectedRegion region : regionManager.getRegions().values() ) {
            String[] wholeClaim = region.getId().split( "_" );
            if( wholeClaim[ 0 ].equalsIgnoreCase( "claim" ) ) {
                OfflinePlayer offPlayer = Bukkit.getOfflinePlayer( UUID.fromString( wholeClaim[ 1 ] ) );

                menu.addItem( ItemBuilder.from( Material.PLAYER_HEAD )
                            .setSkullOwner( offPlayer )
                            .setName( "§e§lClaim: §3§l" + wholeClaim[ 2 ] )
                            .setLore( "§e§lUsed Blocks: §f" + ( region.volume() / 256 ),
                                    "§e§lClaim belongs: §f" + offPlayer.getName(),
                                    "§e§lOwners: §f" + StringUtils.getOwners( region, 60 ),
                                    "§e§lMembers: §f" + StringUtils.getMembers( region, 60 ) )
                            .asGuiItem( event -> new SelectClaim().openMenu( player, region ) ) );
                }
        }

        menu.getFiller().fillBottom( ItemBuilder.from( Material.BLACK_STAINED_GLASS_PANE ).setName( "§7" ).asGuiItem() );

        menu.setItem( 4, 3, ItemBuilder.from( Material.PLAYER_HEAD ).setName( "§eBack" ).setSkullTexture( "ewogICJ0aW1lc3RhbXAiIDogMTYwMDk5NjI2NTkwOCwKICAicHJvZmlsZUlkIiA6ICJhNjhmMGI2NDhkMTQ0MDAwYTk1ZjRiOWJhMTRmOGRmOSIsCiAgInByb2ZpbGVOYW1lIiA6ICJNSEZfQXJyb3dMZWZ0IiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2Y3YWFjYWQxOTNlMjIyNjk3MWVkOTUzMDJkYmE0MzM0MzhiZTQ2NDRmYmFiNWViZjgxODA1NDA2MTY2N2ZiZTIiCiAgICB9CiAgfQp9" ).asGuiItem( event -> menu.previous() ) );
        menu.setItem( 4, 5, ItemBuilder.from( Material.FEATHER ).setName( "§eSearch" ).setLore( "§fNot selected yet", "§eRight click for clear" ).asGuiItem( event -> requestForSearchValue( ( Player ) event.getWhoClicked() ) ) );
        menu.setItem( 4, 7, ItemBuilder.from( Material.PLAYER_HEAD ).setName( "§eNext" ).setSkullTexture( "ewogICJ0aW1lc3RhbXAiIDogMTYwMDk5NjI3NjA3OSwKICAicHJvZmlsZUlkIiA6ICI1MGM4NTEwYjVlYTA0ZDYwYmU5YTdkNTQyZDZjZDE1NiIsCiAgInByb2ZpbGVOYW1lIiA6ICJNSEZfQXJyb3dSaWdodCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9kMzRlZjA2Mzg1MzcyMjJiMjBmNDgwNjk0ZGFkYzBmODVmYmUwNzU5ZDU4MWFhN2ZjZGYyZTQzMTM5Mzc3MTU4IgogICAgfQogIH0KfQ==" ).asGuiItem( event -> menu.next() ) );
        menu.open( player );
    }

    public void requestForSearchValue( Player player )
    {
        //Set up an input window for 'contain' filtering
        AnvilGUI anvil = new AnvilGUI( player, new AnvilGUI.AnvilClickEventHandler() {
            @Override
            public void onAnvilClick( AnvilGUI.AnvilClickEvent anvilEvent ) {
                if( anvilEvent.getSlot() == AnvilGUI.AnvilSlot.OUTPUT && anvilEvent.hasText() )
                {
                    player.closeInventory();
                    String message = anvilEvent.getText();

                    PaginatedGui menu = new PaginatedGui( 4, "§5§lLists claims" );

                    menu.setDefaultClickAction( event -> {
                        if( event.getSlot() > 26 ) {
                            event.setCancelled( true );
                        }
                    } );

                    final RegionContainer regionContainer = WorldGuard.getInstance().getPlatform().getRegionContainer();
                    final RegionManager regionManager = regionContainer.get( BukkitAdapter.adapt( player.getWorld() ) );

                    for( final ProtectedRegion region : regionManager.getRegions().values() ) {
                        String[] wholeClaim = region.getId().split( "_" );
                        if( wholeClaim[ 0 ].equalsIgnoreCase( "claim" ) ) {
                            OfflinePlayer offPlayer = Bukkit.getOfflinePlayer( UUID.fromString( wholeClaim[ 1 ] ) );
                            if( Objects.requireNonNull( offPlayer.getName() ).toLowerCase().contains( message.toLowerCase() ) ) {
                                menu.addItem( ItemBuilder.from( Material.PLAYER_HEAD )
                                        .setSkullOwner( offPlayer )
                                        .setName( "§e§lClaim: §3§l" + wholeClaim[ 2 ] )
                                        .setLore( "§e§lUsed Blocks: §f" + ( region.volume() / 256 ),
                                                "§e§lClaim belongs: §f" + offPlayer.getName(),
                                                "§e§lOwners: §f" + StringUtils.getOwners( region, 60 ),
                                                "§e§lMembers: §f" + StringUtils.getMembers( region, 60 ) )
                                        .asGuiItem( event -> new SelectClaim().openMenu( (Player) event.getWhoClicked(), region ) ) );
                            }
                        }
                    }

                    menu.getFiller().fillBottom( ItemBuilder.from( Material.BLACK_STAINED_GLASS_PANE ).setName( "§7" ).asGuiItem() );

                    menu.setItem( 4, 3, ItemBuilder.from( Material.PLAYER_HEAD ).setName( "§eBack" ).setSkullTexture( "ewogICJ0aW1lc3RhbXAiIDogMTYwMDk5NjI2NTkwOCwKICAicHJvZmlsZUlkIiA6ICJhNjhmMGI2NDhkMTQ0MDAwYTk1ZjRiOWJhMTRmOGRmOSIsCiAgInByb2ZpbGVOYW1lIiA6ICJNSEZfQXJyb3dMZWZ0IiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2Y3YWFjYWQxOTNlMjIyNjk3MWVkOTUzMDJkYmE0MzM0MzhiZTQ2NDRmYmFiNWViZjgxODA1NDA2MTY2N2ZiZTIiCiAgICB9CiAgfQp9" ).asGuiItem( event -> menu.previous() ) );
                    menu.setItem( 4, 5, ItemBuilder.from( Material.FEATHER ).setName( "§eSearch" ).setLore( "§f" + message, "§eRight click for clear" ).asGuiItem( event ->
                    {
                        if( event.isLeftClick() )
                            requestForSearchValue( ( Player ) event.getWhoClicked() );
                        else if( event.isRightClick() )
                        {
                            openMenu( ( Player ) event.getWhoClicked()  );
                        }

                    } ) );
                    menu.setItem( 4, 7, ItemBuilder.from( Material.PLAYER_HEAD ).setName( "§eNext" ).setSkullTexture( "ewogICJ0aW1lc3RhbXAiIDogMTYwMDk5NjI3NjA3OSwKICAicHJvZmlsZUlkIiA6ICI1MGM4NTEwYjVlYTA0ZDYwYmU5YTdkNTQyZDZjZDE1NiIsCiAgInByb2ZpbGVOYW1lIiA6ICJNSEZfQXJyb3dSaWdodCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9kMzRlZjA2Mzg1MzcyMjJiMjBmNDgwNjk0ZGFkYzBmODVmYmUwNzU5ZDU4MWFhN2ZjZGYyZTQzMTM5Mzc3MTU4IgogICAgfQogIH0KfQ==" ).asGuiItem( event -> menu.next() ) );
                    menu.open( player );
                }
            }
        } );

        ItemStack i = new ItemStack( Material.GRASS_BLOCK );
        anvil.setSlot( AnvilGUI.AnvilSlot.INPUT_LEFT, i );
        anvil.setSlotName( AnvilGUI.AnvilSlot.INPUT_LEFT, "§r" );
        anvil.setTitle( "Type a new name" );

        anvil.open();
    }
}
