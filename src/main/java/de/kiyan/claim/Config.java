package de.kiyan.claim;

import org.apache.commons.compress.utils.Lists;

import java.util.ArrayList;
import java.util.List;

public class Config
{
    private final Claim instance;

    public Config( Claim plugin )
    {
        this.instance = plugin;
    }

    public void prepareConfig()
    {
        instance.saveDefaultConfig();
        instance.reloadConfig();
    }

    public int getBlockSize() {
        return instance.getConfig().getInt( "block-size" );
    }

    public List< String > getBannedFlags( )
    {
        return new ArrayList<>( instance.getConfig().getStringList( "banned-flags" ) );
    }
}
