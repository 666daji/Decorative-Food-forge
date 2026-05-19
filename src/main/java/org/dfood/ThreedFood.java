package org.dfood;

import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.dfood.block.entity.ModBlockEntityTypes;
import org.dfood.client.EnchantedGoldenAppleBlockEntityRenderer;
import org.dfood.sound.ModSounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(ThreedFood.MOD_ID)
public class ThreedFood {
    public static final String MOD_ID = "dfood";
    public static final Logger LOGGER = LoggerFactory.getLogger("TW`s Decorative Food");

    public ThreedFood(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();

        ModBlockEntityTypes.register(modEventBus);
        ModSounds.registerAll(modEventBus);
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public void init(final FMLCommonSetupEvent event) {
            LOGGER.info("Hello forge world!");
        }

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            event.enqueueWork(() -> BlockEntityRenderers.register(
                    ModBlockEntityTypes.ENCHANTED_GOLDEN_APPLE.get(),
                    EnchantedGoldenAppleBlockEntityRenderer::new
            ));
        }
    }
}
