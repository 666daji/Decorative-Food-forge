package org.dfood.item;

import com.google.common.collect.Maps;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.dfood.block.FoodBlock;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * 表示该模组独有的方块物品，虽然这样做会有很大一部分重复代码，但是可以很好地与其他模组兼容。
 * <p>示例:{@linkplain ModPotionItem}</p>
 */
public interface HaveBlock {
    /**
     * 针对 forge 端口的特殊处理。
     * <p>当{@linkplain FoodBlock#asItem()} 无法直接从原版{@link Item#BY_BLOCK} 中获取对应的物品时则尝试从这里恢复。</p>
     */
    Map<Block, Item> BY_BLOCK = Maps.newHashMap();

    Block getBlock();

    /**
     * 将该方块物品添加到指定的映射中。
     *
     * @param map  目标映射
     * @param item 该方块物品对应的物品实例
     * @apiNote 该方法用于代替 BlockItem.appendBlocks，以确保方块物品能够正确注册到模组的方块物品映射中。
     */
    default void appendBlocks(Map<Block, Item> map, Item item) {
        map.put(this.getBlock(), item);
        BY_BLOCK.put(this.getBlock(), item);
    }

    /**
     * 尝试从备用映射中取出对应物品。
     *
     * @param block 原方块
     * @return 对应的物品，如果未找到则返回空气
     */
    static Item byBlock(Block block) {
        if (BY_BLOCK.containsKey(block)) {
            return BY_BLOCK.get(block);
        }
        return Items.AIR;
    }

    default InteractionResult place(BlockPlaceContext context) {
        if (!this.getBlock().isEnabled(context.getLevel().enabledFeatures())) {
            return InteractionResult.FAIL;
        }
        if (!context.canPlace()) {
            return InteractionResult.FAIL;
        }

        BlockPlaceContext itemPlacementContext = this.getPlacementContext(context);
        if (itemPlacementContext == null) {
            return InteractionResult.FAIL;
        }

        BlockState blockState = this.getPlacementState(itemPlacementContext);
        if (blockState == null) {
            return InteractionResult.FAIL;
        }
        if (!this.place(itemPlacementContext, blockState)) {
            return InteractionResult.FAIL;
        }

        BlockPos blockPos = itemPlacementContext.getClickedPos();
        Level world = itemPlacementContext.getLevel();
        Player playerEntity = itemPlacementContext.getPlayer();
        ItemStack itemStack = itemPlacementContext.getItemInHand();
        BlockState blockState2 = world.getBlockState(blockPos);

        if (blockState2.is(blockState.getBlock())) {
            blockState2 = this.placeFromNbt(blockPos, world, itemStack, blockState2);
            this.postPlacement(blockPos, world, playerEntity, itemStack, blockState2);
            copyComponentsToBlockEntity(world, blockPos, itemStack);
            blockState2.getBlock().setPlacedBy(world, blockPos, blockState2, playerEntity, itemStack);
            if (playerEntity instanceof ServerPlayer) {
                CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayer) playerEntity, blockPos, itemStack);
            }
        }

        SoundType blockSoundGroup = blockState2.getSoundType();
        world.playSound(
                playerEntity,
                blockPos,
                this.getPlaceSound(blockState2),
                SoundSource.BLOCKS,
                (blockSoundGroup.getVolume() + 1.0F) / 2.0F,
                blockSoundGroup.getPitch() * 0.8F
        );
        world.gameEvent(GameEvent.BLOCK_PLACE, blockPos, GameEvent.Context.of(playerEntity, blockState2));
        itemStack.consume(1, playerEntity);

        return InteractionResult.sidedSuccess(world.isClientSide);
    }

    @Nullable
    default BlockPlaceContext getPlacementContext(BlockPlaceContext context) {
        return context;
    }

    @Nullable
    default BlockState getPlacementState(BlockPlaceContext context) {
        BlockState blockState = this.getBlock().getStateForPlacement(context);
        return blockState != null && this.canPlace(context, blockState) ? blockState : null;
    }

    default boolean canPlace(BlockPlaceContext context, BlockState state) {
        Player playerEntity = context.getPlayer();
        CollisionContext shapeContext = playerEntity == null ? CollisionContext.empty() : CollisionContext.of(playerEntity);
        return (!this.checkStatePlacement() || state.canSurvive(context.getLevel(), context.getClickedPos()))
                && context.getLevel().isUnobstructed(state, context.getClickedPos(), shapeContext);
    }

    default boolean checkStatePlacement() {
        return true;
    }

    default boolean place(BlockPlaceContext context, BlockState state) {
        return context.getLevel().setBlock(context.getClickedPos(), state, Block.UPDATE_ALL);
    }

    /**
     * 将物品中的方块状态组件应用到方块。
     */
    default BlockState placeFromNbt(BlockPos pos, Level world, ItemStack stack, BlockState state) {
        BlockItemStateProperties blockStateComponent = stack.getOrDefault(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY);
        if (blockStateComponent.isEmpty()) {
            return state;
        }
        BlockState newState = blockStateComponent.apply(state);
        if (newState != state) {
            world.setBlock(pos, newState, Block.UPDATE_CLIENTS);
        }
        return newState;
    }

    /**
     * 写入方块实体数据（兼容原 NBT 与组件）。
     */
    default boolean postPlacement(BlockPos pos, Level world, @Nullable Player player, ItemStack stack, BlockState state) {
        return BlockItem.updateCustomBlockEntityTag(world, player, pos, stack);
    }

    default SoundEvent getPlaceSound(BlockState state) {
        return state.getSoundType().getPlaceSound();
    }

    /**
     * 将物品的组件复制到方块实体。
     */
    private static void copyComponentsToBlockEntity(Level world, BlockPos pos, ItemStack stack) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity != null) {
            blockEntity.applyComponentsFromItemStack(stack);
            blockEntity.setChanged();
        }
    }
}