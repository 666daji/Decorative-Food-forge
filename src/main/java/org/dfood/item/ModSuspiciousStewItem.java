package org.dfood.item;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@ParametersAreNonnullByDefault
public class ModSuspiciousStewItem extends BlockItem {

    public ModSuspiciousStewItem(Block block, Properties properties) {
        super(block, properties);
    }

    private static void forEachEffect(ItemStack stew, Consumer<MobEffectInstance> effectConsumer) {
        CompoundTag nbtCompound = stew.getTag();
        if (nbtCompound != null && nbtCompound.contains("Effects", Tag.TAG_LIST)) {
            ListTag nbtList = nbtCompound.getList("Effects", Tag.TAG_COMPOUND);

            for (int i = 0; i < nbtList.size(); i++) {
                CompoundTag nbtCompound2 = nbtList.getCompound(i);
                int j;
                if (nbtCompound2.contains("EffectDuration", Tag.TAG_ANY_NUMERIC)) {
                    j = nbtCompound2.getInt("EffectDuration");
                } else {
                    j = 160;
                }

                MobEffect mobEffect = MobEffect.byId(nbtCompound2.getInt("EffectId"));
                if (mobEffect != null) {
                    effectConsumer.accept(new MobEffectInstance(mobEffect, j));
                }
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
        super.appendHoverText(stack, world, tooltip, context);
        if (context.isCreative()) {
            List<MobEffectInstance> list = new ArrayList<>();
            forEachEffect(stack, list::add);
            PotionUtils.addPotionTooltip(list, tooltip, 1.0F);
        }
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity user) {
        ItemStack itemStack = super.finishUsingItem(stack, world, user);
        forEachEffect(itemStack, user::addEffect);
        return user instanceof Player && ((Player)user).getAbilities().instabuild ? itemStack : new ItemStack(Items.BOWL);
    }
}