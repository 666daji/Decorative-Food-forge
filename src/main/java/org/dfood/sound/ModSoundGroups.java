package org.dfood.sound;

import net.minecraft.world.level.block.SoundType;
import net.minecraftforge.common.util.ForgeSoundType;

public class ModSoundGroups {
    public static final SoundType MEAT = new ForgeSoundType(
            1.0f,
            1.0f,
            ModSounds.MEAT_BREAK,
            ModSounds.MEAT_STEP,
            ModSounds.MEAT_PLACE,
            ModSounds.MEAT_HIT,
            ModSounds.MEAT_FALL
    );
    public static final SoundType FISH = new ForgeSoundType(
            1.0f,
            1.0f,
            ModSounds.FISH_BREAK,
            ModSounds.FISH_STEP,
            ModSounds.FISH_PLACE,
            ModSounds.FISH_HIT,
            ModSounds.FISH_FALL
    );
    public static final SoundType BREAD = new ForgeSoundType(
            1.0f,
            1.0f,
            ModSounds.BREAD_BREAK,
            ModSounds.BREAD_STEP,
            ModSounds.BREAD_PLACE,
            ModSounds.BREAD_HIT,
            ModSounds.BREAD_FALL
    );
    public static final SoundType CHORUS_FRUIT = new ForgeSoundType(
            1.0f,
            1.0f,
            ModSounds.CHORUS_BREAK,
            ModSounds.CHORUS_STEP,
            ModSounds.CHORUS_PLACE,
            ModSounds.CHORUS_HIT,
            ModSounds.CHORUS_FALL
    );
    public static final SoundType EGG = new ForgeSoundType(
            1.0f,
            1.0f,
            ModSounds.EGG_BREAK,
            ModSounds.EGG_STEP,
            ModSounds.EGG_PLACE,
            ModSounds.EGG_HIT,
            ModSounds.EGG_FALL
    );
    public static final SoundType POTION = new ForgeSoundType(
            1.0f,
            1.0f,
            ModSounds.POTION_BREAK,
            ModSounds.POTION_STEP,
            ModSounds.POTION_PLACE,
            ModSounds.POTION_HIT,
            ModSounds.POTION_FALL
    );
    public static final SoundType GLASS_BOTTLE = new ForgeSoundType(
            1.0f,
            1.0f,
            ModSounds.GLASS_BOTTLE_BREAK,
            ModSounds.GLASS_BOTTLE_STEP,
            ModSounds.GLASS_BOTTLE_PLACE,
            ModSounds.GLASS_BOTTLE_HIT,
            ModSounds.GLASS_BOTTLE_FALL
    );
    public static final SoundType WATER_BUCKET = new ForgeSoundType(
            1.0f,
            1.0f,
            ModSounds.WATER_BUCKET_BREAK,
            ModSounds.WATER_BUCKET_STEP,
            ModSounds.WATER_BUCKET_PLACE,
            ModSounds.WATER_BUCKET_HIT,
            ModSounds.WATER_BUCKET_FALL
    );
    public static final SoundType BUCKET = SoundType.METAL;
    public static final SoundType LAVA_BUCKET = new ForgeSoundType(
            1.0f,
            1.0f,
            ModSounds.LAVA_BUCKET_BREAK,
            ModSounds.LAVA_BUCKET_STEP,
            ModSounds.LAVA_BUCKET_PLACE,
            ModSounds.LAVA_BUCKET_HIT,
            ModSounds.LAVA_BUCKET_FALL
    );
}