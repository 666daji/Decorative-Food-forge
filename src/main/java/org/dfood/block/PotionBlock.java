package org.dfood.block;

import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import org.dfood.block.entity.PotionBlockEntity;
import org.jetbrains.annotations.Nullable;

public class PotionBlock extends ComplexFoodBlock implements EntityBlock {
    protected PotionBlock(Properties properties, int maxFood) {
        super(properties, maxFood, true, null, true, null);
    }

    public static class Builder extends FoodBlockBuilder<PotionBlock, Builder> {
        private Builder() {}

        public static Builder create() {
            return new Builder();
        }

        @Override
        protected PotionBlock createBlock() {
            return new PotionBlock(
                    this.settings, this.maxFood
            );
        }
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PotionBlockEntity(pos, state);
    }
}