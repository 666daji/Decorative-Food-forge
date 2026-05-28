package org.dfood;

import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import org.dfood.block.entity.ModBlockEntityTypes;
import org.dfood.client.EnchantedGoldenAppleBlockEntityRenderer;

@Mod(value = ThreedFood.MOD_ID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = ThreedFood.MOD_ID, value = Dist.CLIENT)
public class ThreedFoodClient {
    public ThreedFoodClient(ModContainer container) {

    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> BlockEntityRenderers.register(
                ModBlockEntityTypes.ENCHANTED_GOLDEN_APPLE.get(),
                EnchantedGoldenAppleBlockEntityRenderer::new
        ));
    }
}
