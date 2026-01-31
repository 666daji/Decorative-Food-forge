package org.dfood.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.dfood.block.entity.EnchantedGoldenAppleBlockEntity;
import org.jetbrains.annotations.Nullable;

public class EnchantedGoldenAppleBlock extends FoodBlock implements EntityBlock {

    protected EnchantedGoldenAppleBlock(Properties settings, int maxFood) {
        super(settings, maxFood, true, null, true, null);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    public static class Builder extends FoodBlockBuilder<EnchantedGoldenAppleBlock, Builder> {
        private Builder() {}

        public static Builder create() {
            return new Builder();
        }

        @Override
        protected EnchantedGoldenAppleBlock createBlock() {
            return new EnchantedGoldenAppleBlock(
                    this.settings, this.maxFood
            );
        }
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new EnchantedGoldenAppleBlockEntity(pos, state);
    }
}
