package org.dfood.block.entity;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.dfood.ThreedFood;
import org.dfood.block.FoodBlocks;

import java.util.function.Supplier;

public class ModBlockEntityTypes {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, ThreedFood.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ComplexFoodBlockEntity>> COMPLEX_FOOD =
            registerBlockEntity("complex_food",
                    () -> BlockEntityType.Builder.of(
                            ComplexFoodBlockEntity::new
                    ).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PotionBlockEntity>> POTION_BLOCK_ENTITY =
            registerBlockEntity("potion_block_entity",
                    () -> BlockEntityType.Builder.of(
                            PotionBlockEntity::new,
                            FoodBlocks.POTION
                    ).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SuspiciousStewBlockEntity>> SUSPICIOUS_STEW_BLOCK_ENTITY =
            registerBlockEntity("suspicious_stew_block_entity",
                    () -> BlockEntityType.Builder.of(
                            SuspiciousStewBlockEntity::new,
                            FoodBlocks.SUSPICIOUS_STEW
                    ).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EnchantedGoldenAppleBlockEntity>> ENCHANTED_GOLDEN_APPLE =
            registerBlockEntity("enchanted_golden_apple",
                    () -> BlockEntityType.Builder.of(
                            EnchantedGoldenAppleBlockEntity::new,
                            FoodBlocks.ENCHANTED_GOLDEN_APPLE
                    ).build(null));

    private static <T extends BlockEntityType<?>> DeferredHolder<BlockEntityType<?>, T> registerBlockEntity(
            String name, Supplier<T> supplier) {
        return BLOCK_ENTITIES.register(name, supplier);
    }

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}