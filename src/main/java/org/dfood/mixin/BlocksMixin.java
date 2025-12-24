package org.dfood.mixin;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.dfood.block.FoodBlocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Blocks.class)
public class BlocksMixin {

    @Inject(method = "<clinit>", at = @At("HEAD"), remap = false)
    private static void registerFoodBlocks(CallbackInfo ci) {
        // 遍历映射表中的所有方块并进行注册
        FoodBlocks.FOOD_BLOCK_REGISTRY.forEach(BlocksMixin::dfood$register);
    }

    @Unique
    private static void dfood$register(String id, Block block) {
        Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.parse(id), block);
    }
}