package mc.kmagic.particle;

import mc.kmagic.KMagic;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModParticles {
    // Создаем наш кастомный тип частицы
    public static final DefaultParticleType LIGHTNING_SPARK = FabricParticleTypes.simple();

    public static void registerParticles() {
        Registry.register(Registries.PARTICLE_TYPE, new Identifier(KMagic.MOD_ID, "lightning_spark"), LIGHTNING_SPARK);
    }
}
