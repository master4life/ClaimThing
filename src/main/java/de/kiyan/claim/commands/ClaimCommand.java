package de.kiyan.claim.commands;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import de.kiyan.claim.Claim;
import de.kiyan.claim.menu.MainMenu;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public class ClaimCommand implements CommandExecutor {
    @Override
    public boolean onCommand( CommandSender sender, Command command, String label, String[] args ) {
        if( !( sender instanceof Player ) ) {
            sender.sendMessage( "You must be a player" );
            return true;
        }

        if( !label.equalsIgnoreCase( "claim" ) ) {
            return true;
        }

        Player player = ( Player ) sender;

        if( args.length == 0 ) {
            new MainMenu().openMenu( player );
        }
        if( args.length == 1 ) {
            if( args[ 0 ].equalsIgnoreCase( "info" ) ) {
                final RegionContainer regionContainer = WorldGuard.getInstance().getPlatform().getRegionContainer();
                final ApplicableRegionSet set = regionContainer.createQuery().getApplicableRegions( BukkitAdapter.adapt( player.getLocation() ) );
                if( set.size() <= 0 ) {
                    player.sendMessage( "§cThere is no region at your location" );
                    return true;
                }
                String[] claimName = new String[ 3 ];
                String owners = "";
                String members = "";
                String flags = "";
                for( ProtectedRegion region : set ) {
                    if( !region.getId().contains( "claim_" ) )
                    {
                        player.sendMessage( "§cThere is no region at your location" );
                        return true;
                    }

                    claimName = region.getId().split( "_" );
                    for( UUID uuid : region.getOwners().getUniqueIds() ) {
                        OfflinePlayer offPlayer = Bukkit.getOfflinePlayer( uuid );
                        owners = owners.equals( "" ) ? ( offPlayer.getName() ) : ( owners + " §7| §b" + offPlayer.getName() );
                    }
                    for( UUID uuid : region.getMembers().getUniqueIds() ) {
                        OfflinePlayer offPlayer = Bukkit.getOfflinePlayer( uuid );
                        members = members.equals( "" ) ? ( offPlayer.getName() ) : ( members + " §7| §b" + offPlayer.getName() );
                    }
                    final Map map = region.getFlags();
                    for( final Flag flag : region.getFlags().keySet() ) {
                        if( flag == Flags.GREET_MESSAGE || flag == Flags.FAREWELL_MESSAGE ) continue;

                        flags += flag.getName() + ": " + map.get( flag ) + " §7| §7";
                    }
                }

                player.sendMessage( "§b----------------------------------" );
                player.sendMessage( " §a§lClaim: §b" + claimName[ 2 ] );
                player.sendMessage( " §a§LBelongs to: §b" + Bukkit.getOfflinePlayer( UUID.fromString( claimName[ 1 ] ) ).getName() );
                player.sendMessage( " §a§LOwners: §b" + owners );
                player.sendMessage( " §a§LMembers: §b" + members );
                player.sendMessage( " §a§LFlags: §7" + flags );
                player.sendMessage( "§b----------------------------------" );

            }
        }

        return false;
    }
}
