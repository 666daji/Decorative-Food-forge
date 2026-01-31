package org.dfood.item;


import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.EnchantedGoldenAppleItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.dfood.util.DFoodUtils;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ModEnchantedGoldenAppleItem extends EnchantedGoldenAppleItem implements HaveBlock{
    private final Block block;

    public ModEnchantedGoldenAppleItem(Properties p_41170_, Block block) {
        super(p_41170_);
        this.block = block;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        Item item = context.getItemInHand().getItem();

        // 仅当父类方法失败时才尝试放置方块
        if (super.useOn(context) != InteractionResult.PASS ||
                (player != null && !player.isShiftKeyDown() && DFoodUtils.isModFoodItem(item))) {
            return InteractionResult.PASS;
        }

        InteractionResult actionResult = this.place(new BlockPlaceContext(context));
        if (!actionResult.consumesAction() && this.isEdible()) {
            InteractionResult actionResult2 = this.use(context.getLevel(), context.getPlayer(), context.getHand()).getResult();
            return actionResult2 == InteractionResult.CONSUME ? InteractionResult.CONSUME_PARTIAL : actionResult2;
        } else {
            return actionResult;
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
        super.appendHoverText(stack, world, tooltip, context);
        this.getBlock().appendHoverText(stack, world, tooltip, context);
    }

    @Override
    public Block getBlock() {
        return this.block;
    }
}
