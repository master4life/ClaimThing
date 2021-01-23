package de.kiyan.claim.menu;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import me.mattstudios.mfgui.gui.components.util.ItemBuilder;
import me.mattstudios.mfgui.gui.guis.PaginatedGui;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ClaimList
{
    public void openMenu( Player player )
    {
        PaginatedGui menu = new PaginatedGui( 4, "§5§lLists claims" );

        menu.setDefaultClickAction( event -> {
            if( event.getSlot() > 26 ) {
                event.setCancelled( true );
            }
        } );

        final RegionContainer regionContainer = WorldGuard.getInstance().getPlatform().getRegionContainer();
        final RegionManager regionManager = regionContainer.get( BukkitAdapter.adapt(player.getWorld()));


        for( final ProtectedRegion region : regionManager.getRegions().values() ) {
            String[] wholeClaim = region.getId().split( "_" );
            if( wholeClaim[0].equalsIgnoreCase( "claim" ) && region.getOwners().contains( player.getUniqueId() ))
            {
                String owner = "";
                for( UUID uuid : region.getOwners().getUniqueIds() )
                {
                    OfflinePlayer offPlayer = Bukkit.getOfflinePlayer( uuid );
                    owner = owner.equals( "" ) ? ( "§f" + offPlayer.getName() ) : ( owner + " §7| §f" + offPlayer.getName() );
                }
                String member = "";
                for( UUID uuid : region.getMembers().getUniqueIds() )
                {
                    OfflinePlayer offPlayer = Bukkit.getOfflinePlayer( uuid );
                    member = member.equals( "" ) ? ( "§f" + offPlayer.getName() ) : ( member + " §7| §f" + offPlayer.getName() );
                }

                owner = ( owner.length() > 70 ) ? owner.substring ( 0 , 70 ).concat( "…" ) : owner;
                member = ( member.length() > 70 ) ? member.substring ( 0 , 70 ).concat( "…" ) : member;

                OfflinePlayer offPlayer = Bukkit.getOfflinePlayer( UUID.fromString(wholeClaim[1] ));
                menu.addItem( ItemBuilder.from( offPlayer.getUniqueId().equals( player.getUniqueId() ) ? Material.GRASS_BLOCK : Material.GRASS_PATH )
                        .setName( "§e§lClaim: §3§l" + wholeClaim[2] )
                        .setLore( "§e§lUsed Blocks: §f" + (region.volume() / 256 ),
                                "§e§lClaim belongs: §f" + offPlayer.getName(),
                                "§e§lOwners: §f" + owner,
                                "§e§lMembers: §f" + member )
                        .asGuiItem( event -> new SelectClaim().openMenu( player, region )) );
            }
        }

        menu.getFiller().fillBottom( ItemBuilder.from( Material.BLACK_STAINED_GLASS_PANE ).setName( "§7" ).asGuiItem() );

        menu.setItem( 4, 3, ItemBuilder.from( Material.PLAYER_HEAD).setName( "§eBack" ).setSkullTexture( "ewogICJ0aW1lc3RhbXAiIDogMTYwMDk5NjI2NTkwOCwKICAicHJvZmlsZUlkIiA6ICJhNjhmMGI2NDhkMTQ0MDAwYTk1ZjRiOWJhMTRmOGRmOSIsCiAgInByb2ZpbGVOYW1lIiA6ICJNSEZfQXJyb3dMZWZ0IiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2Y3YWFjYWQxOTNlMjIyNjk3MWVkOTUzMDJkYmE0MzM0MzhiZTQ2NDRmYmFiNWViZjgxODA1NDA2MTY2N2ZiZTIiCiAgICB9CiAgfQp9" ).asGuiItem( event -> menu.previous()) );
        menu.setItem( 4, 5, ItemBuilder.from( Material.PLAYER_HEAD ).setName( "§eBack to main menu" ).setSkullTexture( "ewogICJ0aW1lc3RhbXAiIDogMTYwMDk5NjI1NDg4MiwKICAicHJvZmlsZUlkIiA6ICI2OGY1OWI5YjViMGI0YjA1YTlmMmUxZDE0MDVhYTM0OCIsCiAgInByb2ZpbGVOYW1lIiA6ICJNSEZfQXJyb3dEb3duIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2ZlM2Q3NTVjZWNiYjEzYTM5ZThlOTM1NDgyM2E5YTAyYTAxZGNlMGFjYTY4ZmZkNDJlM2VhOWE5ZDI5ZTJkZjIiCiAgICB9CiAgfQp9" ).asGuiItem( event -> new MainMenu().openMenu( (Player) event.getWhoClicked() )) );
        menu.setItem( 4, 7, ItemBuilder.from( Material.PLAYER_HEAD).setName( "§eNext" ).setSkullTexture( "ewogICJ0aW1lc3RhbXAiIDogMTYwMDk5NjI3NjA3OSwKICAicHJvZmlsZUlkIiA6ICI1MGM4NTEwYjVlYTA0ZDYwYmU5YTdkNTQyZDZjZDE1NiIsCiAgInByb2ZpbGVOYW1lIiA6ICJNSEZfQXJyb3dSaWdodCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9kMzRlZjA2Mzg1MzcyMjJiMjBmNDgwNjk0ZGFkYzBmODVmYmUwNzU5ZDU4MWFhN2ZjZGYyZTQzMTM5Mzc3MTU4IgogICAgfQogIH0KfQ==" ).asGuiItem( event -> menu.next()) );
        menu.open( player );
    }
}
