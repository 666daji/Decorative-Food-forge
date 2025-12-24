package org.dfood.mixin;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;
import org.dfood.util.DFoodUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(BlockItem.class)
public abstract class BlockItemMixin {

    @Shadow
    @Nullable
    protected abstract BlockState getPlacementState(BlockPlaceContext context);

    /**
     * 阻止玩家在站立状态下放置食物方块，
     * 解决了放置与食用的冲突问题。
     */
    @Inject(method = "useOn", at = @At("HEAD"), cancellable = true)
    private void useOnBlockMixin(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        var player = context.getPlayer();
        var placementContext = new BlockPlaceContext(context);
        BlockState expectedState = getPlacementState(placementContext);

        if (expectedState != null && player != null &&
                !player.isCrouching() && DFoodUtils.isModFoodBlock(expectedState.getBlock())) {
            cir.setReturnValue(InteractionResult.PASS);
        }
    }
}