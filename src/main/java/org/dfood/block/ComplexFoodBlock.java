package org.dfood.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.Level;
import org.dfood.block.entity.ComplexFoodBlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 支持存储物品 NBT 数据的复杂食物方块。
 * <p>
 * 该方块允许将物品堆栈的 {@link CompoundTag} 存储于关联的 {@link ComplexFoodBlockEntity} 中，
 * 并在取出或破坏方块时恢复原样。存储机制为栈结构（先进后出）。
 *
 * @see ComplexFoodBlockEntity
 */
public class ComplexFoodBlock extends FoodBlock implements EntityBlock {
    protected ComplexFoodBlock(Properties properties, int maxFood, boolean isFood,
                               @Nullable VoxelShape simpleShape, boolean useItemTranslationKey,
                               @Nullable EnforceAsItem cItem) {
        super(properties, maxFood, isFood, simpleShape, useItemTranslationKey, cItem);
    }

    public static class Builder extends FoodBlockBuilder<ComplexFoodBlock, Builder> {
        private Builder() {}

        public static Builder create() {
            return new Builder();
        }

        @Override
        protected ComplexFoodBlock createBlock() {
            return new ComplexFoodBlock(
                    this.settings,
                    this.maxFood,
                    this.isFood,
                    this.simpleShape,
                    this.useItemTranslationKey,
                    this.cItem
            );
        }
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ComplexFoodBlockEntity(pos, state);
    }

    /**
     * 默认不比较 NBT 数据，子类可重写以支持特定 NBT 匹配逻辑。
     *
     * @param stack       手持物品堆栈
     * @param blockEntity 对应的方块实体
     * @return 若匹配返回 {@code true}
     */
    @Override
    public boolean isSame(ItemStack stack, @Nullable BlockEntity blockEntity) {
        return super.isSame(stack, blockEntity);
    }

    /**
     * 尝试增加堆叠数量，并将物品 NBT 存入方块实体。
     *
     * @return {@code true} 如果操作成功
     */
    @Override
    protected boolean tryAdd(BlockState state, Level world, BlockPos pos, Player player,
                             ItemStack handStack, @Nullable BlockEntity blockEntity) {
        int currentCount = state.getValue(NUMBER_OF_FOOD);

        if (currentCount < MAX_FOOD) {
            if (blockEntity instanceof ComplexFoodBlockEntity complexFoodBlockEntity) {
                CompoundTag stackNbt = handStack.hasTag() ? handStack.getTag().copy() : new CompoundTag();
                complexFoodBlockEntity.pushNbt(stackNbt);
            }

            BlockState newState = state.setValue(NUMBER_OF_FOOD, currentCount + 1);
            return world.setBlock(pos, newState, Block.UPDATE_ALL);
        }

        return false;
    }

    /**
     * 尝试减少堆叠数量，并从方块实体恢复物品 NBT 后给予玩家。
     *
     * @return {@code true} 如果操作成功
     */
    @Override
    protected boolean tryRemove(BlockState state, Level world, BlockPos pos,
                                Player player, @Nullable BlockEntity blockEntity) {
        int currentCount = state.getValue(NUMBER_OF_FOOD);

        if (currentCount > 0) {
            int newCount = currentCount - 1;

            if (newCount > 0) {
                world.setBlock(pos, state.setValue(NUMBER_OF_FOOD, newCount), Block.UPDATE_ALL);
            } else {
                world.removeBlock(pos, false);
            }

            ItemStack foodItem = createStack(1, blockEntity);

            if (!player.isCreative()) {
                if (!player.addItem(foodItem)) {
                    player.drop(foodItem, false);
                }
            }

            return true;
        }

        return false;
    }

    /**
     * 创建物品堆栈，并从方块实体恢复对应的 NBT 数据。
     *
     * @param count 创建数量（通常为 1）
     * @param blockEntity 对应的方块实体
     * @return 带有原 NBT 数据的物品堆栈
     * @throws IllegalArgumentException 如果数量超出范围
     */
    @Override
    public ItemStack createStack(int count, @Nullable BlockEntity blockEntity) {
        if (count <= 0 || count > MAX_FOOD) {
            throw new IllegalArgumentException("Count must be between 1 and " + MAX_FOOD);
        }

        ItemStack stack = new ItemStack(this.asItem(), count);

        if (blockEntity instanceof ComplexFoodBlockEntity complexFoodBlockEntity) {
            CompoundTag storedNbt = complexFoodBlockEntity.popNbt();
            if (storedNbt != null && !storedNbt.isEmpty()) {
                stack.setTag(storedNbt);
            }
        }

        return stack;
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state,
                            @Nullable LivingEntity placer, ItemStack itemStack) {
        super.setPlacedBy(world, pos, state, placer, itemStack);

        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof ComplexFoodBlockEntity complexFoodBlockEntity) {
            CompoundTag stackNbt = itemStack.hasTag() ? itemStack.getTag().copy() : new CompoundTag();
            complexFoodBlockEntity.pushNbt(stackNbt);
            complexFoodBlockEntity.setChanged();
        }
    }

    /**
     * 获取掉落物列表，确保每个掉落物都带有正确的 NBT 数据。
     *
     * @return 按先进后出顺序恢复 NBT 的掉落物列表
     */
    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        int foodCount = state.getValue(NUMBER_OF_FOOD);

        if (foodCount <= 0) {
            return Collections.emptyList();
        }

        BlockEntity blockEntity = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);

        if (blockEntity instanceof ComplexFoodBlockEntity complexFoodBlockEntity) {
            List<ItemStack> droppedStacks = new ArrayList<>();
            for (int i = 0; i < foodCount; i++) {
                droppedStacks.add(createStack(1, complexFoodBlockEntity));
            }
            return droppedStacks;
        }

        return Collections.singletonList(new ItemStack(this.asItem(), foodCount));
    }
}