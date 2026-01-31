package org.dfood.block.entity;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.dfood.Threedfood;
import org.dfood.block.FoodBlocks;

import java.util.function.Supplier;

public class ModBlockEntityTypes {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Threedfood.MOD_ID);

    public static final RegistryObject<BlockEntityType<ComplexFoodBlockEntity>> COMPLEX_FOOD =
            registerBlockEntity("complex_food",
                    () -> BlockEntityType.Builder.of(
                            ComplexFoodBlockEntity::new
                    ).build(null));

    public static final RegistryObject<BlockEntityType<PotionBlockEntity>> POTION_BLOCK_ENTITY =
            registerBlockEntity("potion_block_entity",
                    () -> BlockEntityType.Builder.of(
                            PotionBlockEntity::new,
                            FoodBlocks.POTION
                    ).build(null));

    public static final RegistryObject<BlockEntityType<SuspiciousStewBlockEntity>> SUSPICIOUS_STEW_BLOCK_ENTITY =
            registerBlockEntity("suspicious_stew_block_entity",
                    () -> BlockEntityType.Builder.of(
                            SuspiciousStewBlockEntity::new,
                            FoodBlocks.SUSPICIOUS_STEW
                    ).build(null));

    public static final RegistryObject<BlockEntityType<EnchantedGoldenAppleBlockEntity>> ENCHANTED_GOLDEN_APPLE =
            registerBlockEntity("enchanted_golden_apple",
                    () -> BlockEntityType.Builder.of(
                            EnchantedGoldenAppleBlockEntity::new,
                            FoodBlocks.ENCHANTED_GOLDEN_APPLE
                    ).build(null));

    private static <T extends BlockEntityType<?>> RegistryObject<T> registerBlockEntity(
            String name, Supplier<T> supplier) {
        return BLOCK_ENTITIES.register(name, supplier);
    }

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}