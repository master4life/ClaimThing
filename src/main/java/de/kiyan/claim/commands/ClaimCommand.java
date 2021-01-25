package de.kiyan.claim.commands;

import com.google.common.collect.Maps;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import de.kiyan.claim.Claim;
import de.kiyan.claim.Config;
import de.kiyan.claim.menu.AdminList;
import de.kiyan.claim.menu.MainMenu;
import org.behindbars.gamecore.core.handlers.PlayerHandler;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.*;

public class ClaimCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand( CommandSender sender, Command command, String label, String[] args ) {
        // Excludes console
        if( !( sender instanceof Player ) ) {
            sender.sendMessage( "You must be a player" );
            return true;
        }

        // Makes sure only claim as command is accepted.
        if( !label.equalsIgnoreCase( "claim" ) ) {
            return true;
        }
        Player player = ( Player ) sender;

        // Shows the MainMenu
        if( args.length == 0 ) {
            new MainMenu().openMenu( player );
        }
        if( args.length == 1 ) {
            // Opens the administration menu, which lists all claims starting with claim_
            if( args[ 0 ].equalsIgnoreCase( "admin" ) ) {
                if( new PlayerHandler( player ).getRank() == 10 ) {
                    new AdminList().openMenu( player );

                } else {
                    player.sendMessage( "§cOnly for admins!" );
                    return true;
                }
            }
            final RegionContainer regionContainer = WorldGuard.getInstance().getPlatform().getRegionContainer();
            final ApplicableRegionSet set = regionContainer.createQuery().getApplicableRegions( BukkitAdapter.adapt( player.getLocation() ) );
            // Whenever banned-flags was updated this commaond refrehes all claims
            if( args[ 0 ].equalsIgnoreCase( "update" ) ) {
                if( new PlayerHandler( player ).getRank() == 10 )
                {
                    int counter = 0;
                    for( ProtectedRegion region : set ) {
                        if( !region.getId().contains( "claim_" ) ) continue;

                        final Map< Flag<?>, Object> cloneFlags = region.getFlags();
                        for( Flag<?> entry : cloneFlags.keySet() )
                        {
                            for( String bannedFlags : new Config( Claim.getInstance() ).getBannedFlags() )
                            {
                                if( entry.getName().contains( bannedFlags ) )
                                {
                                    cloneFlags.remove( entry );
                                    counter++;
                                }
                            }

                            region.setFlags( cloneFlags );
                        }
                    }
                    player.sendMessage( counter == 0 ? "§a§lNo illegal flags found" : "§2§lFOUND FLAGS: §b" + counter + " §aillegal flags has been removed."  );
                    return true;
                } else {
                    player.sendMessage( "§cOnly for admins!" );
                    return true;
                }
            }
            // Equivalent to /rg info but from WOrldGuard but for every player.
            if( args[ 0 ].equalsIgnoreCase( "info" ) ) {
                if( set.size() <= 0 ) {
                    player.sendMessage( "§cThere is no claim at your location" );
                    return true;
                }
                String[] claimName = new String[ 3 ];
                String owners = "";
                String members = "";
                String flags = "";
                for( ProtectedRegion region : set ) {
                    if( !region.getId().contains( "claim_" ) ) {
                        player.sendMessage( "§cThere is no claim at your location" );
                        return true;
                    }

                    claimName = region.getId().split( "_" );
                    for( UUID uuid : region.getOwners().getUniqueIds() ) {
                        OfflinePlayer offPlayer = Bukkit.getOfflinePlayer( uuid );
                        owners = owners.equals( "" ) ? ( offPlayer.getName() ) : ( owners + " §7| §b" + offPlayer.getName() );
                    }
                    for( UUID uuid : region.getMembers().getUniqueIds() ) {
                        OfflinePlayer offPlayer = Bukkit.getOfflinePlayer( uuid );
                        if( members != null ) {
                            members = members.equals( "" ) ? ( offPlayer.getName() ) : ( members + " §7| §b" + offPlayer.getName() );
                        }
                    }
                    final Map map = region.getFlags();
                    for( final Flag flag : region.getFlags().keySet() ) {
                        if( flag == Flags.GREET_MESSAGE || flag == Flags.FAREWELL_MESSAGE || flag == Flags.TELE_LOC ) continue;

                        flags += ( map.get( flag ).toString().contains( "ALLOW" ) ? "§2" : "§4" ) + flag.getName() + " §7| ";
                    }
                }

                player.sendMessage( "§b----------------------------------" );
                player.sendMessage( " §a§lClaim: §b" + claimName[ 2 ] );
                player.sendMessage( " §a§LBelongs to: §b" + Bukkit.getOfflinePlayer( UUID.fromString( claimName[ 1 ] ) ).getName() );
                player.sendMessage( " §a§LOwners: §b" + owners );
                player.sendMessage( " §a§LMembers: §b" + members );
                player.sendMessage( " §a§LFlags: §7" + flags );
                player.sendMessage( "§b----------------------------------" );

                return true;
            }
        }

        return false;
    }

    @Override
    public List< String > onTabComplete( CommandSender sender, Command command, String alias, String[] args ) {
        List< String > completions = new ArrayList<>();
        List< String > commands = new ArrayList<>();
        if( !( sender instanceof Player ) ) return null;
        Player player = ( Player ) sender;

        if( args.length == 1 ) {
            commands.add( "info" );
            if( new PlayerHandler( player ).getRank() == 10 )
            {
                commands.add( "admin" );
                commands.add( "update" );
            }
            StringUtil.copyPartialMatches( args[ 0 ], commands, completions );
        }

        Collections.sort( completions );
        return completions;
    }
}
