package org.dfood.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import org.dfood.util.DFoodUtils;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ModStewItem extends BowlFoodItem implements HaveBlock{
    private final Block block;

    public ModStewItem(Properties properties, Block block1) {
        super(properties);
        this.block = block1;
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
    public boolean canBeDepleted() {
        return !(this.block instanceof ShulkerBoxBlock);
    }

    @Override
    public void onDestroyed(ItemEntity entity) {
        if (this.block instanceof ShulkerBoxBlock) {
            ItemStack itemStack = entity.getItem();
            CompoundTag nbtCompound = HaveBlock.getBlockEntityNbt(itemStack);
            if (nbtCompound != null && nbtCompound.contains("Items", Tag.TAG_LIST)) {
                ListTag nbtList = nbtCompound.getList("Items", Tag.TAG_COMPOUND);
                ItemUtils.onContainerDestroyed(entity, nbtList.stream().map(CompoundTag.class::cast).map(ItemStack::of));
            }
        }
    }
}