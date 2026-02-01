package org.dfood.jade;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.dfood.util.DFoodUtils;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public class FoodBlockComponentProvider implements IBlockComponentProvider {

    @Override
    public void appendTooltip(ITooltip iTooltip, BlockAccessor blockAccessor, IPluginConfig iPluginConfig) {
        // 显示当前食物方块的堆叠数量
        int count = DFoodUtils.getFoodBlockCount(blockAccessor.getBlockState());
        iTooltip.add(Component.translatable("jade.dfood.food_count", count));
    }

    @Override
    public ResourceLocation getUid() {
        return DFoodUtils.createModId("food_block");
    }
}
