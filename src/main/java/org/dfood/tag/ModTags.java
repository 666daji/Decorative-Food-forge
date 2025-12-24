package org.dfood.tag;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import org.dfood.Threedfood;

public class ModTags {
    public static final TagKey<Block> FOOD_PLACE = of("food_place");

    private static TagKey<Block> of(String id) {
        return TagKey.create(Registries.BLOCK, new ResourceLocation(Threedfood.MOD_ID, id));
    }
}
