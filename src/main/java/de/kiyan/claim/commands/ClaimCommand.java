package de.kiyan.claim.commands;

import de.kiyan.claim.menu.MainMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.List;

public class ClaimCommand implements CommandExecutor
{
    @Override
    public boolean onCommand( CommandSender sender, Command command, String label, String[] args )
    {
        if( !(sender instanceof Player ) )
        {
            sender.sendMessage( "You must be a player" );
            return true;
        }

        if( !label.equalsIgnoreCase( "claim" ) )
        {
            return true;
        }

        Player player = (Player) sender;

        if( args.length == 0 )
        {
            new MainMenu( ).openMenu( player );
        }

        return false;
    }
}
