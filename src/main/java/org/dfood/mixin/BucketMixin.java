package org.dfood.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.dfood.block.FoodBlocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class BucketMixin {

    @Inject(method = "useOn", at = @At("HEAD"), cancellable = true)
    private void placeBucket(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        Item self = (Item)(Object)this;

        if (context.getPlayer() != null && self instanceof BucketItem bucketItem && context.getPlayer().isShiftKeyDown()) {
            // 获取桶内的流体
            Fluid fluid = bucketItem.getFluid();

            InteractionResult result = dFood$placeBucketBlock(context, fluid);
            if (result.shouldAwardStats()) {
                cir.setReturnValue(result);
                cir.cancel();
            }
        }
    }

    @Unique
    private InteractionResult dFood$placeBucketBlock(UseOnContext context, Fluid fluid) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();

        // 根据流体类型获取对应的方块状态
        BlockState blockState = dFood$getBucketBlockStateForFluid(fluid, new BlockPlaceContext(context));
        if (blockState == null) {
            return InteractionResult.FAIL;
        }

        // 如果点击的是可替换方块（如草、空气等），则放置位置为点击位置
        BlockPos placementPos = pos;
        if (!level.getBlockState(pos).canBeReplaced()) {
            placementPos = pos.relative(context.getClickedFace());
        }

        // 检查是否可以放置
        if (!level.getBlockState(placementPos).canBeReplaced()) {
            return InteractionResult.FAIL;
        }

        // 放置方块
        if (!level.setBlock(placementPos, blockState, Block.UPDATE_ALL_IMMEDIATE)) {
            return InteractionResult.FAIL;
        }

        // 触发方块放置事件
        level.gameEvent(GameEvent.BLOCK_PLACE, placementPos, GameEvent.Context.of(player, blockState));

        // 播放放置音效
        SoundType soundType = blockState.getSoundType();
        level.playSound(
                player,
                placementPos,
                soundType.getPlaceSound(),
                SoundSource.BLOCKS,
                (soundType.getVolume() + 1.0F) / 2.0F,
                soundType.getPitch() * 0.8F
        );

        // 消耗物品（非创造模式）
        if (player != null && !player.isCreative()) {
            ItemStack heldItem = context.getItemInHand();
            heldItem.shrink(1);
        }

        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Unique
    private BlockState dFood$getBucketBlockStateForFluid(Fluid fluid, BlockPlaceContext context) {
        if (fluid == Fluids.WATER) {
            return FoodBlocks.WATER_BUCKET.getStateForPlacement(context);
        } else if (fluid == Fluids.LAVA) {
            return FoodBlocks.LAVA_BUCKET.getStateForPlacement(context);
        } else if (fluid == Fluids.EMPTY) {
            return FoodBlocks.BUCKET.getStateForPlacement(context);
        }

        return null;
    }
}