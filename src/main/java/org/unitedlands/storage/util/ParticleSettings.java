package org.unitedlands.storage.util;

import org.bukkit.Particle;
import org.bukkit.configuration.Configuration;

public record ParticleSettings(
        Particle particle,
        int      count,
        double   offset
) {

    public static ParticleSettings load(Configuration config, String path) {
        return new ParticleSettings(
                Particle.valueOf(config.getString(path + ".particle", "HAPPY_VILLAGER")),
                config.getInt(path    + ".count", 10),
                config.getDouble(path + ".offset", 0.5)
        );
    }

}
