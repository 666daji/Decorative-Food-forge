package org.dfood.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class EnchantedGoldenAppleBlockEntity extends BlockEntity {
    public EnchantedGoldenAppleBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.ENCHANTED_GOLDEN_APPLE.get(), pos, state);
    }
}
