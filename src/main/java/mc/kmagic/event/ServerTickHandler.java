package mc.kmagic.event;

import mc.kmagic.particle.ModParticles;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ServerTickHandler {

    private static final Map<UUID, AuraData> activeAuras = new HashMap<>();

    public static void startLightningAura(ServerPlayerEntity player, int ticks) {
        activeAuras.put(player.getUuid(), new AuraData(player, ticks));
    }

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            Iterator<Map.Entry<UUID, AuraData>> iterator = activeAuras.entrySet().iterator();

            while (iterator.hasNext()) {
                Map.Entry<UUID, AuraData> entry = iterator.next();
                AuraData data = entry.getValue();
                
                ServerPlayerEntity player = server.getPlayerManager().getPlayer(entry.getKey());
                if (player == null || data.ticksRemaining <= 0) {
                    iterator.remove();
                    continue;
                }

                ServerWorld world = player.getServerWorld();
                Vec3d pos = player.getPos();
                double radius = 5.0;

                // 1. Удары молнии
                if (data.ticksRemaining % 5 == 0) {
                    LightningEntity lightning = new LightningEntity(EntityType.LIGHTNING_BOLT, world);
                    lightning.setPosition(pos);
                    lightning.setCosmetic(true);
                    world.spawnEntity(lightning);

                    world.playSound(null, pos.x, pos.y, pos.z, SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER, SoundCategory.WEATHER, 5.0F, 0.8F + (float)Math.random() * 0.2F);
                }

                // 2. Урон и поджог
                if (data.ticksRemaining % 5 == 0) {
                    Box area = player.getBoundingBox().expand(radius);
                    List<LivingEntity> targets = world.getEntitiesByClass(LivingEntity.class, area, entity -> entity != player && entity.squaredDistanceTo(player) <= radius * radius);

                    for (LivingEntity target : targets) {
                        target.damage(world.getDamageSources().lightningBolt(), 6.7f); 
                        target.setOnFireFor(4);
                        // Вызываем нашу кастомную частицу на врагах при ударе
                        world.spawnParticles(ModParticles.LIGHTNING_SPARK, target.getX(), target.getBodyY(0.5), target.getZ(), 10, 0.5, 0.5, 0.5, 0.5);
                    }
                }

                // 3. Рисуем синюю грань круга
                DustParticleEffect blueDust = new DustParticleEffect(new Vector3f(0.0f, 0.8f, 1.0f), 2.0f);
                int pointsOnCircle = 60;
                for (int i = 0; i < pointsOnCircle; i++) {
                    double angle = (i * 2 * Math.PI) / pointsOnCircle;
                    double x = pos.x + radius * Math.cos(angle);
                    double z = pos.z + radius * Math.sin(angle);
                    world.spawnParticles(blueDust, x, pos.y + 0.1, z, 1, 0, 0, 0, 0);
                }

                // 4. НАШИ КАСТОМНЫЕ ЧАСТИЦЫ ТОКА И СИНИЙ ОГОНЬ (только на земле)
                for (int i = 0; i < 20; i++) {
                    double angle = Math.random() * 2 * Math.PI;
                    double r = Math.sqrt(Math.random()) * radius;
                    double x = pos.x + r * Math.cos(angle);
                    double z = pos.z + r * Math.sin(angle);
                    
                    // Убираем высоту, спавним только на земле (чуть выше блока)
                    double groundY = pos.y + 0.1;
                    
                    // Скорость для частиц (разлетаются по земле)
                    double vx = (Math.random() - 0.5) * 0.5;
                    double vy = 0.1; // Легкий подскок вверх
                    double vz = (Math.random() - 0.5) * 0.5;

                    // Спавним кастомную искру
                    world.spawnParticles(ModParticles.LIGHTNING_SPARK, x, groundY, z, 1, vx, vy, vz, 0.1);

                    // Спавним синий огонь душ (SOUL_FIRE_FLAME) вместе с искрами
                    if (Math.random() > 0.5) { // Огонь спавним чуть реже
                        world.spawnParticles(ParticleTypes.SOUL_FIRE_FLAME, x, groundY, z, 1, vx * 0.5, 0.05, vz * 0.5, 0.02);
                    }
                }

                data.ticksRemaining--;
            }
        });
    }

    private static class AuraData {
        ServerPlayerEntity player;
        int ticksRemaining;

        public AuraData(ServerPlayerEntity player, int ticksRemaining) {
            this.player = player;
            this.ticksRemaining = ticksRemaining;
        }
    }
}
