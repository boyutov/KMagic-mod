package mc.kmagic.network;

import mc.kmagic.KMagic;
import mc.kmagic.event.ServerTickHandler;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class ModMessages {
    public static final Identifier CAST_SPELL_ID = new Identifier(KMagic.MOD_ID, "cast_spell");

    public static void registerC2SPackets() {
        ServerPlayNetworking.registerGlobalReceiver(CAST_SPELL_ID, (server, player, handler, buf, responseSender) -> {
            int spellIndex = buf.readInt();
            
            server.execute(() -> {
                if (spellIndex == 1) { // Заклинание 1 (Гроза)
                    castLightningSpell(player);
                }
            });
        });
    }

    private static void castLightningSpell(ServerPlayerEntity player) {
        // Мы передаем управление в ServerTickHandler на 1.5 секунды (30 тиков)
        // Вся магия, включая молнии и частицы, будет работать там!
        ServerTickHandler.startLightningAura(player, 30);
    }
}
