package mc.kmagic.particle;

import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

public class LightningSparkParticle extends SpriteBillboardParticle {
    
    protected LightningSparkParticle(ClientWorld clientWorld, double x, double y, double z, double velocityX, double velocityY, double velocityZ, SpriteProvider spriteProvider) {
        super(clientWorld, x, y, z, velocityX, velocityY, velocityZ);

        this.velocityMultiplier = 0.96F;
        
        // Устанавливаем единственный спрайт (так как у нас теперь только одна картинка)
        this.setSprite(spriteProvider);
        
        this.scale *= 1.5F + this.random.nextFloat() * 1.5F;
        this.maxAge = 5 + this.random.nextInt(10); 
        
        this.velocityX = velocityX * 0.5;
        this.velocityY = velocityY * 0.5 + 0.1;
        this.velocityZ = velocityZ * 0.5;

        // Устанавливаем цвет в белый, чтобы использовать оригинальный цвет текстуры
        this.red = 1.0F;
        this.green = 1.0F; 
        this.blue = 1.0F;
        
        this.alpha = 0.8F + this.random.nextFloat() * 0.2F;
    }

    @Override
    public ParticleTextureSheet getType() {
        // Оставляем PARTICLE_SHEET_LIT, чтобы черный фон текстуры (если он есть) был прозрачным
        return ParticleTextureSheet.PARTICLE_SHEET_LIT; 
    }
    
    @Override
    public int getBrightness(float tint) {
        return 15728880; 
    }

    @Override
    public void tick() {
        super.tick();
        
        // Удален this.setSpriteForAge(), так как анимации больше нет
        
        if (this.age % 2 == 0) {
            this.alpha = 0.5F + this.random.nextFloat() * 0.5F;
            this.velocityX += (this.random.nextFloat() - 0.5F) * 0.1;
            this.velocityZ += (this.random.nextFloat() - 0.5F) * 0.1;
        } else {
            this.alpha = 0.9F;
        }

        if (this.age > this.maxAge / 2) {
            this.scale *= 0.9F;
        }
    }

    public static class Factory implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Override
        public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double x, double y, double z, double vx, double vy, double vz) {
            return new LightningSparkParticle(clientWorld, x, y, z, vx, vy, vz, this.spriteProvider);
        }
    }
}
