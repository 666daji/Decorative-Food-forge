package org.dfood.mixin;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.dfood.item.HaveBlock;
import org.dfood.util.FoodToBlocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Items.class)
public abstract class FoodToBlockMixin {

    /**
     * 该Mixin类通过掉换注册方法的Item实例来实现将食物物品转换为对应的方块。
     * 原本的Item实例被更改了标识符变为了被遗弃的物品
     */
    @ModifyVariable(method = "registerItem(Ljava/lang/String;Lnet/minecraft/world/item/Item;)Lnet/minecraft/world/item/Item;", at = @At("HEAD"), argsOnly = true)
    private static Item modifyItem(Item item, String value) {
        if (FoodToBlocks.FOOD_MAP.containsKey(value)) {
            Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(value + "_de"), item);
            return FoodToBlocks.getItem(value);
        } else {
            return item;
        }
    }

    @Inject(method = "registerItem(Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/world/item/Item;)Lnet/minecraft/world/item/Item;", at = @At("HEAD"))
    private static void registerBlock(ResourceKey<Item> key, Item item, CallbackInfoReturnable<Item> cir) {
        if (item instanceof HaveBlock haveBlock) {
            haveBlock.appendBlocks(Item.BY_BLOCK, item);
        }
    }
}