package org.dfood;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.dfood.block.entity.ModBlockEntityTypes;
import org.dfood.sound.ModSounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(ThreedFood.MOD_ID)
public class ThreedFood {
    public static final String MOD_ID = "dfood";
    public static final Logger LOGGER = LoggerFactory.getLogger("TW`s Decorative Food");

    public ThreedFood(IEventBus modEventBus, ModContainer modContainer) {
        ModBlockEntityTypes.register(modEventBus);
        ModSounds.registerAll(modEventBus);
    }

    @SubscribeEvent
    public void init(final FMLCommonSetupEvent event) {
        LOGGER.info("Hello neoforge world!");
    }
}
