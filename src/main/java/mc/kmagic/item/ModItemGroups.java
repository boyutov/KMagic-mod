package mc.kmagic.item;

import mc.kmagic.KMagic;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroups {
    public static final ItemGroup KMAGIC_GROUP = Registry.register(Registries.ITEM_GROUP,
            new Identifier(KMagic.MOD_ID, "kmagic"),
            FabricItemGroup.builder().displayName(Text.translatable("itemgroup.kmagic"))
                    // Временно используем стандартную звезду незера как иконку, пока нет своего предмета
                    .icon(() -> new ItemStack(Items.NETHER_STAR)).entries((displayContext, entries) -> {
//                        entries.add(ModItems.Ruby);
//                        entries.add(ModItems.RAW_RUBY);
                    }).build());

    public static void registerItemGroups() {
        KMagic.LOGGER.info("Registering Item Groups for " + KMagic.MOD_ID);
    }

}
