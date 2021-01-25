package de.kiyan.claim.runnable;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import de.kiyan.claim.Claim;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ParticleEffects extends BukkitRunnable {
    private final ProtectedRegion region;
    private final Player player;
    private int timer = 0;

    // Initiate an repeatable task
    public ParticleEffects( Player player, ProtectedRegion region ) {
        this.player = player;
        this.region = region;
        this.timer = 20;
        this.runTaskTimer( Claim.getInstance(), 5L, 15L );
    }

    // Displays the region with particle effects.
    @Override
    public void run() {
        final int minX = region.getMinimumPoint().getX();
        final int minZ = region.getMinimumPoint().getZ();
        final int maxX = region.getMaximumPoint().getX();
        final int maxZ = region.getMaximumPoint().getZ();
        final int midX = (maxX - minX)/2;
        final int midZ = (maxZ - minZ)/2;
        double playerY = player.getLocation().getY();

        if(midX/6 >0) {
            int tmp = 0;
            final int amount = midX/5;
            for(int i = 0; i< amount; i++) {
                final int x = minX + midX - tmp;
                final int x2 = minX + midX + tmp;
                for( double y = playerY - 10; y <= playerY + 10; y++ ) {
                    player.spawnParticle( Particle.FLAME, x, y, minZ, 0 );
                    player.spawnParticle( Particle.FLAME, x, y, maxZ, 0 );
                    player.spawnParticle( Particle.FLAME, x2, y, minZ, 0 );
                    player.spawnParticle( Particle.FLAME, x2, y, maxZ, 0 );
                }
                tmp+= 5;
            }
        }
        if(midZ/6 >0) {
            int tmp = 0;
            final int amount = midZ/5;
            for(int i = 0; i< amount; i++) {
                final int z = minZ + midZ - tmp;
                final int z2 = minZ + midZ + tmp;

                for( double y = playerY - 10; y <= playerY + 10; y++ ) {
                    player.spawnParticle( Particle.FLAME, minX, y, z, 0 );
                    player.spawnParticle( Particle.FLAME, maxX, y, z, 0 );
                    player.spawnParticle( Particle.FLAME, minX, y, z2, 0 );
                    player.spawnParticle( Particle.FLAME, maxX, y, z2, 0 );
                }
                tmp += 5;
            }
        }

        for( double y = playerY - 40; y <= 256; y++ ) {
            player.spawnParticle( Particle.FLAME, minX, y, minZ, 0 );
            player.spawnParticle( Particle.FLAME, minX, y, maxZ, 0 );
            player.spawnParticle( Particle.FLAME, maxX, y, minZ, 0 );
            player.spawnParticle( Particle.FLAME, maxX, y, maxZ, 0 );
        }

        timer--;

        if( 0 > this.timer )
        {
            player.sendMessage( "§5Boundaries have been expired." );
            this.cancel();
        }
    }
}
