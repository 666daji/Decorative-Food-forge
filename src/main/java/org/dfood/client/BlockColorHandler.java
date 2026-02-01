package org.dfood.client;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.dfood.ThreedFood;
import org.dfood.block.FoodBlocks;
import org.dfood.block.entity.PotionBlockEntity;

@Mod.EventBusSubscriber(modid = ThreedFood.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class BlockColorHandler {

    @SubscribeEvent
    public static void onBlockColorRegister(RegisterColorHandlersEvent.Block event) {

        // 注册药水方块颜色
        event.register((state, world, pos, tintIndex) -> {
            if (world != null && pos != null && tintIndex != -1) {
                BlockEntity blockEntity = world.getBlockEntity(pos);
                if (blockEntity instanceof PotionBlockEntity potionBlockEntity) {
                    return potionBlockEntity.getColor(tintIndex);
                }
            }
            return -1;
        }, FoodBlocks.POTION);

        // 注册水桶方块颜色
        event.register((state, world, pos, tintIndex) ->
                        tintIndex != -1 ? 4159204 : -1,
                FoodBlocks.WATER_BUCKET
        );
    }
}