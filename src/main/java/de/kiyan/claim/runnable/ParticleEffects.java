package de.kiyan.claim.runnable;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import de.kiyan.claim.Claim;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ParticleEffects extends BukkitRunnable {
    private final BlockVector3 pos1, pos2;
    private final ProtectedRegion region;
    private final Player player;
    private int timer = 0;

    public ParticleEffects( Player player, ProtectedRegion region ) {
        this.player = player;
        this.region = region;
        this.pos1 = region.getMinimumPoint();
        this.pos2 = region.getMaximumPoint();
        this.timer = 70;

        this.runTaskTimer( Claim.getInstance(), 5L, 10L );
    }

    @Override
    public void run() {
        int startX = pos1.getBlockX();
        int startY = pos1.getBlockY();
        int startZ = pos1.getBlockZ();
        int endX = pos2.getBlockX();
        int endY = pos2.getBlockY();
        int endZ = pos2.getBlockZ();

        for( double x = startX; x <= endX + 1; x++ ) {
            for( double y = startY; y <= endY + 1; y++ ) {
                for( double z = startZ; z <= endZ + 1; z++ ) {
                    boolean edge = false;
                    if( ( ( int ) x == startX || ( int ) x == endX + 1 ) &&
                            ( ( int ) y == startY || ( int ) y == endY + 1 ) ) edge = true;
                    if( ( ( int ) z == startZ || ( int ) z == endZ + 1 ) &&
                            ( ( int ) y == startY || ( int ) y == endY + 1 ) ) edge = true;
                    if( ( ( int ) x == startX || ( int ) x == endX + 1 ) &&
                            ( ( int ) z == startZ || ( int ) z == endZ + 1 ) ) edge = true;

                    if( edge )
                        player.spawnParticle( Particle.FLAME, x, y, z, 0 );
                }
            }
        }
        timer--;

        if( 0 > this.timer ) {
            System.out.println( "killed" );
            this.cancel();
        }
    }
}
