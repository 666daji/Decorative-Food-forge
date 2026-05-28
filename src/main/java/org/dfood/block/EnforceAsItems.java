package org.dfood.block;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

public class EnforceAsItems {
    public static final FoodBlock.EnforceAsItem POTATO = createAsItem("potato");
    public static final FoodBlock.EnforceAsItem CARROT = createAsItem("carrot");
    public static final FoodBlock.EnforceAsItem SWEET_BERRIES = createAsItem("sweet_berries");
    public static final FoodBlock.EnforceAsItem GLOW_BERRIES = createAsItem("glow_berries");

    // 桶
    public static final FoodBlock.EnforceAsItem BUCKET = createAsItem("bucket");
    public static final FoodBlock.EnforceAsItem WATER_BUCKET = createAsItem("water_bucket");
    public static final FoodBlock.EnforceAsItem LAVA_BUCKET = createAsItem("lava_bucket");

    private static FoodBlock.EnforceAsItem createAsItem(String item) {
        return () -> BuiltInRegistries.ITEM.get(ResourceLocation.parse(item));
    }
}