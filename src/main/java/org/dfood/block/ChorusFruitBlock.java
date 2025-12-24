package org.dfood.block;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.util.RandomSource;

public class ChorusFruitBlock extends FoodBlock {
    protected ChorusFruitBlock(BlockBehaviour.Properties properties, int maxFood) {
        super(properties, maxFood, true, null, true, null);
    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        if (random.nextInt(100) == 0) {
            world.playLocalSound(
                    pos.getX() + 0.5,
                    pos.getY() + 0.5,
                    pos.getZ() + 0.5,
                    SoundEvents.PORTAL_AMBIENT,
                    SoundSource.BLOCKS,
                    0.5F,
                    random.nextFloat() * 0.4F + 0.8F,
                    false
            );
        }
    }

    public static class Builder extends FoodBlockBuilder<ChorusFruitBlock, Builder> {
        private Builder() {}

        public static Builder create() {
            return new Builder();
        }

        @Override
        protected ChorusFruitBlock createBlock() {
            return new ChorusFruitBlock(
                    this.settings, this.maxFood
            );
        }
    }
}