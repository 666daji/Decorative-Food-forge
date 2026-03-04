package org.dfood.jade;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.dfood.block.SimpleFoodBlock;
import org.dfood.util.DFoodUtils;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public class FoodBlockComponentProvider implements IBlockComponentProvider {
    public static final ResourceLocation ID = DFoodUtils.createModId("food_block");

    @Override
    public void appendTooltip(ITooltip iTooltip, BlockAccessor blockAccessor, IPluginConfig iPluginConfig) {
        // 显示当前食物方块的堆叠数量
        if (DFoodUtils.isModFoodBlock(blockAccessor.getBlock()) && !(blockAccessor.getBlock() instanceof SimpleFoodBlock)) {
            int count = DFoodUtils.getFoodBlockCount(blockAccessor.getBlockState());
            iTooltip.add(Component.translatable("jade.dfood.food_count", count));
        }
    }

    @Override
    public ResourceLocation getUid() {
        return ID;
    }
}
