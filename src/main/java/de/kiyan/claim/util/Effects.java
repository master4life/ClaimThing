package de.kiyan.claim.util;

import org.bukkit.Particle;
import org.bukkit.World;

import java.util.Arrays;
import java.util.Optional;

public class Effects
{
    public static void spawnParticle( World world, String particle, double x, double y, double z) {
        Optional< Particle > optional = Arrays.stream(Particle.values())
                .filter((it) -> it.name().equalsIgnoreCase(particle))
                .findAny();
        optional.ifPresent( value -> world.spawnParticle( value, x, y, z, 1, 0, 0, 0, 0 ) );
    }
}
