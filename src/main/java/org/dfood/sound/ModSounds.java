package org.dfood.sound;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.dfood.ThreedFood;
import org.dfood.util.DFoodUtils;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, ThreedFood.MOD_ID);

    // 肉
    public static final RegistryObject<SoundEvent> MEAT_BREAK = register("meat_break");
    public static final RegistryObject<SoundEvent> MEAT_STEP = register("meat_step");
    public static final RegistryObject<SoundEvent> MEAT_PLACE = register("meat_place");
    public static final RegistryObject<SoundEvent> MEAT_HIT = register("meat_hit");
    public static final RegistryObject<SoundEvent> MEAT_FALL = register("meat_fall");

    // 鱼
    public static final RegistryObject<SoundEvent> FISH_BREAK = register("fish_break");
    public static final RegistryObject<SoundEvent> FISH_STEP = register("fish_step");
    public static final RegistryObject<SoundEvent> FISH_PLACE = register("fish_place");
    public static final RegistryObject<SoundEvent> FISH_HIT = register("fish_hit");
    public static final RegistryObject<SoundEvent> FISH_FALL = register("fish_fall");

    // 面包
    public static final RegistryObject<SoundEvent> BREAD_BREAK = register("bread_break");
    public static final RegistryObject<SoundEvent> BREAD_STEP = register("bread_step");
    public static final RegistryObject<SoundEvent> BREAD_PLACE = register("bread_place");
    public static final RegistryObject<SoundEvent> BREAD_HIT = register("bread_hit");
    public static final RegistryObject<SoundEvent> BREAD_FALL = register("bread_fall");

    // 紫菘果
    public static final RegistryObject<SoundEvent> CHORUS_BREAK = register("chorus_break");
    public static final RegistryObject<SoundEvent> CHORUS_STEP = register("chorus_step");
    public static final RegistryObject<SoundEvent> CHORUS_PLACE = register("chorus_place");
    public static final RegistryObject<SoundEvent> CHORUS_HIT = register("chorus_hit");
    public static final RegistryObject<SoundEvent> CHORUS_FALL = register("chorus_fall");

    // 鸡蛋
    public static final RegistryObject<SoundEvent> EGG_BREAK = register("egg_break");
    public static final RegistryObject<SoundEvent> EGG_STEP = register("egg_step");
    public static final RegistryObject<SoundEvent> EGG_PLACE = register("egg_place");
    public static final RegistryObject<SoundEvent> EGG_HIT = register("egg_hit");
    public static final RegistryObject<SoundEvent> EGG_FALL = register("egg_fall");

    // 瓶子
    public static final RegistryObject<SoundEvent> POTION_BREAK = register("potion_break");
    public static final RegistryObject<SoundEvent> POTION_STEP = register("potion_step");
    public static final RegistryObject<SoundEvent> POTION_PLACE = register("potion_place");
    public static final RegistryObject<SoundEvent> POTION_HIT = register("potion_hit");
    public static final RegistryObject<SoundEvent> POTION_FALL = register("potion_fall");

    // 草方块
    public static final RegistryObject<SoundEvent> GLASS_BOTTLE_BREAK = register("glass_bottle_break");
    public static final RegistryObject<SoundEvent> GLASS_BOTTLE_STEP = register("glass_bottle_step");
    public static final RegistryObject<SoundEvent> GLASS_BOTTLE_PLACE = register("glass_bottle_place");
    public static final RegistryObject<SoundEvent> GLASS_BOTTLE_HIT = register("glass_bottle_hit");
    public static final RegistryObject<SoundEvent> GLASS_BOTTLE_FALL = register("glass_bottle_fall");

    // 水桶
    public static final RegistryObject<SoundEvent> WATER_BUCKET_BREAK = register("water_bucket_break");
    public static final RegistryObject<SoundEvent> WATER_BUCKET_STEP = register("water_bucket_step");
    public static final RegistryObject<SoundEvent> WATER_BUCKET_PLACE = register("water_bucket_place");
    public static final RegistryObject<SoundEvent> WATER_BUCKET_HIT = register("water_bucket_hit");
    public static final RegistryObject<SoundEvent> WATER_BUCKET_FALL = register("water_bucket_fall");

    // 岩浆桶
    public static final RegistryObject<SoundEvent> LAVA_BUCKET_BREAK = register("lava_bucket_break");
    public static final RegistryObject<SoundEvent> LAVA_BUCKET_STEP = register("lava_bucket_step");
    public static final RegistryObject<SoundEvent> LAVA_BUCKET_PLACE = register("lava_bucket_place");
    public static final RegistryObject<SoundEvent> LAVA_BUCKET_HIT = register("lava_bucket_hit");
    public static final RegistryObject<SoundEvent> LAVA_BUCKET_FALL = register("lava_bucket_fall");

    private static RegistryObject<SoundEvent> register(String path) {
        ResourceLocation id = DFoodUtils.createModId(path);
        return SOUND_EVENTS.register(path, () -> SoundEvent.createVariableRangeEvent(id));
    }

    public static void registerAll(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}