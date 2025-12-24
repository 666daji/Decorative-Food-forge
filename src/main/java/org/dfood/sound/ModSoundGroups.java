package org.dfood.sound;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.SoundType;
import net.minecraftforge.common.util.ForgeSoundType;

public class ModSoundGroups {
    public static final SoundType MEAT = new ForgeSoundType(
            1.0f,
            1.0f,
            () -> SoundEvents.SLIME_DEATH_SMALL,
            () -> SoundEvents.SLIME_JUMP_SMALL,
            () -> SoundEvents.SLIME_DEATH_SMALL,
            () -> SoundEvents.SLIME_DEATH_SMALL,
            () -> SoundEvents.SLIME_HURT_SMALL
    );
    public static final SoundType FISH = new ForgeSoundType(
            1.0f,
            1.0f,
            () -> SoundEvents.COD_FLOP,
            () -> SoundEvents.PUFFER_FISH_FLOP,
            () -> SoundEvents.COD_FLOP,
            () -> SoundEvents.COD_FLOP,
            () -> SoundEvents.COD_FLOP
    );
    public static final SoundType BREAD = new ForgeSoundType(
            1.0f,
            1.0f,
            () -> SoundEvents.FOX_AMBIENT,
            () -> SoundEvents.FOX_AMBIENT,
            () -> SoundEvents.FOX_AMBIENT,
            () -> SoundEvents.FOX_AMBIENT,
            () -> SoundEvents.FOX_AMBIENT
    );
    public static final SoundType CHORUS_FRUIT = new ForgeSoundType(
            1.0f,
            1.0f,
            () -> SoundEvents.ENDERMAN_TELEPORT,
            () -> SoundEvents.ENDERMAN_HURT,
            () -> SoundEvents.ENDERMAN_TELEPORT,
            () -> SoundEvents.ENDERMAN_SCREAM,
            () -> SoundEvents.ENDERMAN_TELEPORT
    );
    public static final SoundType EGG = new ForgeSoundType(
            1.0f,
            1.0f,
            () -> SoundEvents.CHICKEN_EGG,
            () -> SoundEvents.CHICKEN_EGG,
            () -> SoundEvents.CHICKEN_EGG,
            () -> SoundEvents.CHICKEN_EGG,
            () -> SoundEvents.CHICKEN_EGG
    );
    public static final SoundType POTION = new ForgeSoundType(
            1.0f,
            1.0f,
            () -> SoundEvents.BOTTLE_FILL,
            () -> SoundEvents.BOTTLE_FILL,
            () -> SoundEvents.BOTTLE_FILL,
            () -> SoundEvents.BOTTLE_EMPTY,
            () -> SoundEvents.BOTTLE_EMPTY
    );
    public static final SoundType WATER_BUCKET = new ForgeSoundType(
            1.0f,
            1.0f,
            () -> SoundEvents.BUCKET_FILL,
            () -> SoundEvents.BUCKET_FILL,
            () -> SoundEvents.BUCKET_FILL,
            () -> SoundEvents.BUCKET_EMPTY,
            () -> SoundEvents.BUCKET_EMPTY
    );
    public static final SoundType BUCKET = SoundType.METAL;
    public static final SoundType LAVA_BUCKET = new ForgeSoundType(
            1.0f,
            1.0f,
            () -> SoundEvents.BUCKET_FILL_LAVA,
            () -> SoundEvents.BUCKET_FILL_LAVA,
            () -> SoundEvents.BUCKET_FILL_LAVA,
            () -> SoundEvents.BUCKET_EMPTY_LAVA,
            () -> SoundEvents.BUCKET_EMPTY_LAVA
    );
}