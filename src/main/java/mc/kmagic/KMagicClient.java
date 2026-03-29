package mc.kmagic;

import mc.kmagic.event.KeyInputHandler;
import mc.kmagic.particle.LightningSparkParticle;
import mc.kmagic.particle.ModParticles;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;

public class KMagicClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        KeyInputHandler.register();
        
        // Регистрация нашей фабрики кастомных частиц на клиенте
        ParticleFactoryRegistry.getInstance().register(ModParticles.LIGHTNING_SPARK, LightningSparkParticle.Factory::new);
    }
}
