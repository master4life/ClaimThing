package de.kiyan.claim;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import de.kiyan.claim.commands.ClaimCommand;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class Claim extends JavaPlugin
{
    private static Claim instance;

    @Override
    public void onEnable()
    {
        instance = this;
        new Config( this ).prepareConfig();

        this.getCommand( "claim" ).setExecutor( new ClaimCommand() );
    }

    public static Claim getInstance()
    {
        return instance;
    }

    public static int getBlockLimit() {
        return new Config( Claim.getInstance() ).getBlockSize();
    }

    public static WorldEditPlugin getWorldedit(){
        final Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
        if( plugin instanceof WorldEditPlugin) {
            return (WorldEditPlugin) plugin;
        } else {
            return null;
        }
    }
}
