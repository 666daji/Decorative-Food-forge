package org.dfood.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.entity.player.Player;
import org.dfood.util.DFoodUtils;

import java.util.List;

public class ModEggItem extends EggItem implements HaveBlock {
    private final Block block;

    public ModEggItem(Properties properties, Block block) {
        super(properties);
        this.block = block;
    }

    @Override
    public Block getBlock() {
        return block;
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
        if (!actionResult.consumesAction() && context.getItemInHand().has(DataComponents.FOOD)) {
            InteractionResult actionResult2 = this.use(context.getLevel(), context.getPlayer(), context.getHand()).getResult();
            return actionResult2 == InteractionResult.CONSUME ? InteractionResult.CONSUME_PARTIAL : actionResult2;
        } else {
            return actionResult;
        }
    }

    @Override
    public String getDescriptionId() {
        return this.getBlock().getDescriptionId();
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        this.getBlock().appendHoverText(stack, context, tooltip, flag);
    }

    @Override
    public void onDestroyed(ItemEntity entity) {
        ItemContainerContents itemcontainercontents = entity.getItem().set(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
        if (itemcontainercontents != null) {
            ItemUtils.onContainerDestroyed(entity, itemcontainercontents.nonEmptyItemsCopy());
        }
    }

    @Override
    public FeatureFlagSet requiredFeatures() {
        return this.getBlock().requiredFeatures();
    }
}