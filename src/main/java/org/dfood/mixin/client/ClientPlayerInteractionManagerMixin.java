package org.dfood.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.dfood.util.DFoodUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiPlayerGameMode.class)
public abstract class ClientPlayerInteractionManagerMixin {

    @Final
    @Shadow
    private Minecraft minecraft;

    /**
     * 注入到 performUseItemOn 方法中，在检查潜行绕过之前
     * 这样当玩家潜行时，仍能与模组的食物方块交互
     */
    @Inject(method = "performUseItemOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;doesSneakBypassUse(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/player/Player;)Z", ordinal = 0), cancellable = true)
    private void onPerformUseItemOn(LocalPlayer player, InteractionHand hand,
                                    BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir) {
        if (this.minecraft.level == null) {
            return;
        }

        BlockPos blockPos = hitResult.getBlockPos();
        BlockState blockState = this.minecraft.level.getBlockState(blockPos);

        if (DFoodUtils.isModFoodBlock(blockState.getBlock())) {
            InteractionResult result = blockState.use(this.minecraft.level, player, hand, hitResult);
            if (result.consumesAction()) {
                cir.setReturnValue(result);
            }
        }
    }
}