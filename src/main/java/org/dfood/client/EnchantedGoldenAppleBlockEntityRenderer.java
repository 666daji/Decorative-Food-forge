package org.dfood.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.dfood.block.FoodBlock;
import org.dfood.block.FoodBlocks;
import org.dfood.block.entity.EnchantedGoldenAppleBlockEntity;
import org.dfood.util.DFoodUtils;

public class EnchantedGoldenAppleBlockEntityRenderer implements BlockEntityRenderer<EnchantedGoldenAppleBlockEntity> {
    protected final BlockRenderDispatcher blockRenderer;

    public EnchantedGoldenAppleBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        blockRenderer = context.getBlockRenderDispatcher();
    }
    @Override
    public void render(EnchantedGoldenAppleBlockEntity entity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        if (entity.getBlockState().getBlock() instanceof FoodBlock foodBlock) {
            Direction facing = entity.getBlockState().getValue(FoodBlock.FACING);
            int count = DFoodUtils.getFoodBlockCount(entity.getBlockState());
            BlockState state = FoodBlocks.GOLDEN_APPLE.defaultBlockState()
                    .setValue(FoodBlock.FACING, facing)
                    .setValue(foodBlock.NUMBER_OF_FOOD, count);

            if (entity.getLevel() != null) {
                blockRenderer.renderBatched(state, entity.getBlockPos(), entity.getLevel(), matrices,
                        vertexConsumers.getBuffer(RenderType.cutout()), true, RandomSource.create());

                blockRenderer.renderBatched(state, entity.getBlockPos(), entity.getLevel(), matrices,
                        vertexConsumers.getBuffer(RenderType.glint()), true, RandomSource.create());
            }
        }
    }
}
