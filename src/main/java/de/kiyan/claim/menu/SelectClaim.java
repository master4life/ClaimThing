package de.kiyan.claim.menu;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import de.kiyan.claim.runnable.ParticleEffects;
import me.mattstudios.mfgui.gui.components.util.ItemBuilder;
import me.mattstudios.mfgui.gui.guis.Gui;
import me.mattstudios.mfgui.gui.guis.GuiItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class SelectClaim
{
    public void openMenu( Player player, ProtectedRegion region )
    {
        String claimName = region.getId().split( "_" )[2];

        Gui menu = new Gui( 2, "§5Managing " + claimName );

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

        GuiItem info = new GuiItem( ItemBuilder.from( Material.GRASS_BLOCK )
                .setName( "§aYour select claim: §7" + claimName )
                .setLore( "§e§lUsed Blocks: §f" + (region.volume() / 256 ),
                        "§e§lOwners: §f" + owner,
                        "§e§lMembers: §f" + member )
                .build());

        GuiItem owners = new GuiItem( ItemBuilder.from( Material.PLAYER_HEAD )
                .setName( "§2Manage Owners" )
                .setLore( "§7List all owners of this claim" )
                .setSkullOwner( player )
                .build(), event -> new OwnerList().openMenu( (Player) event.getWhoClicked(), region ));

        GuiItem members = new GuiItem( ItemBuilder.from( Material.CHEST )
                .setName( "§bManage Members" )
                .setLore( "§7list all members of this claim" )
                .build(), event -> new MemberList().openMenu( (Player) event.getWhoClicked(), region ));

        GuiItem flages = new GuiItem( ItemBuilder.from( Material.SHIELD )
                .setName( "§eManage Flags" )
                .setLore( "§7Organize your flags" )
                .build(), event -> new FlagManager().openMenu( (Player ) event.getWhoClicked(), region, 0 ));

        GuiItem show = new GuiItem( ItemBuilder.from( Material.ENDER_EYE )
                .setName( "§eShow me boundrias" )
                .setLore( "§7Particles will visual your claim edges" )
                .build(), event -> { new ParticleEffects( (Player) event.getWhoClicked(), region ).run(); ((Player) event.getWhoClicked()).closeInventory(); });

        GuiItem back = ItemBuilder.from( Material.PLAYER_HEAD )
                .setName( "§7Back to claim list" )
                .setSkullTexture( "ewogICJ0aW1lc3RhbXAiIDogMTYwMDk5NjI1NDg4MiwKICAicHJvZmlsZUlkIiA6ICI2OGY1OWI5YjViMGI0YjA1YTlmMmUxZDE0MDVhYTM0OCIsCiAgInByb2ZpbGVOYW1lIiA6ICJNSEZfQXJyb3dEb3duIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2ZlM2Q3NTVjZWNiYjEzYTM5ZThlOTM1NDgyM2E5YTAyYTAxZGNlMGFjYTY4ZmZkNDJlM2VhOWE5ZDI5ZTJkZjIiCiAgICB9CiAgfQp9" )
                .asGuiItem( event -> new ClaimList().openMenu( ( Player ) event.getWhoClicked() ) );

        GuiItem delete = new GuiItem( ItemBuilder.from( Material.BARRIER )
                .setName( "§eRemove this claim" )
                .setLore( "§4§lCAUTION!", "§cThis cant be undone!" )
                .build(), event -> new DeleteClaim().openMenu( (Player) event.getWhoClicked(), region ) );

        menu.setDefaultClickAction( event -> event.setCancelled( true ) );

        menu.getFiller().fillBottom( ItemBuilder.from( Material.BLACK_STAINED_GLASS_PANE ).setName( "§r" ).asGuiItem() );

        menu.setItem( 0, info );
        menu.setItem( 2, owners );
        menu.setItem( 4, members );
        menu.setItem( 6, show );
        menu.setItem( 8, flages );
        menu.setItem( 9, back );
        menu.setItem( 17, delete );
        menu.open( player );
    }
}
