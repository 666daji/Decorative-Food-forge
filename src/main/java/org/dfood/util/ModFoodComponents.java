package org.dfood.util;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;

/**
 * 包含模组中非炖菜类食物的食物组件定义
 */
public class ModFoodComponents {
    // 零食类
    public static final FoodProperties COOKIE = createFood(2, 0.1F);
    public static final FoodProperties APPLE = createFood(4, 0.3F);
    public static final FoodProperties MELON_SLICE = createFood(2, 0.3F);
    public static final FoodProperties BREAD = createFood(5, 0.6F);
    public static final FoodProperties DRIED_KELP = createFood(1, 0.3F);

    // 蔬菜类
    public static final FoodProperties BEETROOT = createFood(1, 0.6F);
    public static final FoodProperties POTATO = createFood(1, 0.3F);
    public static final FoodProperties BAKED_POTATO = createFood(5, 0.6F);
    public static final FoodProperties CARROT = createFood(3, 0.6F);
    public static final FoodProperties SWEET_BERRIES = createFood(2, 0.1F);
    public static final FoodProperties GLOW_BERRIES = createFood(2, 0.1F);

    // 金制食物
    public static final FoodProperties GOLDEN_APPLE = new FoodProperties.Builder()
            .nutrition(4)
            .saturationModifier(1.2F)
            .effect(new MobEffectInstance(MobEffects.REGENERATION, 100, 1), 1.0F)
            .effect(new MobEffectInstance(MobEffects.ABSORPTION, 2400, 0), 1.0F)
            .alwaysEdible()
            .build();
    public static final FoodProperties ENCHANTED_GOLDEN_APPLE = new FoodProperties.Builder()
            .nutrition(4)
            .saturationModifier(1.2F)
            .effect(new MobEffectInstance(MobEffects.REGENERATION, 400, 1), 1.0F)
            .effect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 6000, 0), 1.0F)
            .effect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 6000, 0), 1.0F)
            .effect(new MobEffectInstance(MobEffects.ABSORPTION, 2400, 3), 1.0F)
            .alwaysEdible()
            .build();
    public static final FoodProperties GOLDEN_CARROT = new FoodProperties.Builder().nutrition(6).saturationModifier(1.2F).build();

    // 生熟肉类
    public static final FoodProperties CHICKEN = createFoodWithEffect(
            2, 0.3F,
            new MobEffectInstance(MobEffects.HUNGER, 600, 0),
            0.3F
    );
    public static final FoodProperties COOKED_CHICKEN = createFood(6, 0.6F);
    public static final FoodProperties BEEF = createFood(3, 0.3F);
    public static final FoodProperties COOKED_BEEF = createFood(8, 0.8F);
    public static final FoodProperties MUTTON = createFood(2, 0.3F);
    public static final FoodProperties COOKED_MUTTON = createFood(6, 0.8F);
    public static final FoodProperties PORKCHOP = createFood(3, 0.3F);
    public static final FoodProperties COOKED_PORKCHOP = createFood(8, 0.8F);
    public static final FoodProperties RABBIT = createFood(3, 0.3F);
    public static final FoodProperties COOKED_RABBIT = createFood(5, 0.6F);

    // 鱼类
    public static final FoodProperties COD = createFood(2, 0.1F);
    public static final FoodProperties COOKED_COD = createFood(5, 0.6F);
    public static final FoodProperties SALMON = createFood(2, 0.1F);
    public static final FoodProperties COOKED_SALMON = createFood(6, 0.8F);
    public static final FoodProperties PUFFERFISH = createFoodWithEffects(
            1, 0.1F,
            new MobEffectInstance(MobEffects.POISON, 1200, 1),
            new MobEffectInstance(MobEffects.HUNGER, 300, 2),
            new MobEffectInstance(MobEffects.CONFUSION, 300, 0)
    );
    public static final FoodProperties TROPICAL_FISH = createFood(1, 0.1F);

    public static final FoodProperties SPIDER_EYE = createFoodWithEffect(2, 0.8F, new MobEffectInstance(MobEffects.POISON, 100, 0), 1.0F);

    // 其他非炖菜类
    public static final FoodProperties PUMPKIN_PIE = createFood(8, 0.3F);
    public static final FoodProperties CHORUS_FRUIT = createAlwaysEdibleFood(4, 0.3F);
    public static final FoodProperties POISONOUS_POTATO = createFoodWithEffect(
            2, 0.3F,
            new MobEffectInstance(MobEffects.POISON, 100, 0),
            0.6F
    );
    public static final FoodProperties HONEY_BOTTLE = createFood(6, 0.1F);

    // 创建基本食物组件
    private static FoodProperties createFood(int nutrition, float saturationModifier) {
        return new FoodProperties.Builder()
                .nutrition(nutrition)
                .saturationModifier(saturationModifier)
                .build();
    }
    private static FoodProperties createAlwaysEdibleFood(int nutrition, float saturationModifier){
        return new FoodProperties.Builder()
                .nutrition(nutrition)
                .saturationModifier(saturationModifier)
                .alwaysEdible()
                .build();
    }

    // 创建带状态效果的食物组件
    private static FoodProperties createFoodWithEffect(int nutrition, float saturationModifier,
                                                      MobEffectInstance effect, float probability) {
        return new FoodProperties.Builder()
                .nutrition(nutrition)
                .saturationModifier(saturationModifier)
                .effect(effect, probability)
                .build();
    }

    // 创建带多个状态效果的食物组件
    private static FoodProperties createFoodWithEffects(int nutrition, float saturationModifier,
                                                       MobEffectInstance... effects) {
        FoodProperties.Builder builder = new FoodProperties.Builder()
                .nutrition(nutrition)
                .saturationModifier(saturationModifier);

        for (MobEffectInstance effect : effects) {
            builder.effect(effect, 1.0F);
        }

        return builder.build();
    }
}