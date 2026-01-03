package org.dfood.block;

import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.dfood.block.entity.SuspiciousStewBlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class SuspiciousStewBlock extends FoodBlock implements EntityBlock {
    protected SuspiciousStewBlock(Properties properties, int maxFood) {
        super(properties, maxFood, true, null, true, null);
    }

    public static class Builder extends FoodBlockBuilder<SuspiciousStewBlock, Builder> {
        private Builder() {}

        public static Builder create() {
            return new Builder();
        }

        @Override
        protected SuspiciousStewBlock createBlock() {
            return new SuspiciousStewBlock(
                    this.settings, this.maxFood
            );
        }
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SuspiciousStewBlockEntity(pos, state);
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.setPlacedBy(world, pos, state, placer, itemStack);
        if (!world.isClientSide) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof SuspiciousStewBlockEntity suspiciousStewBlockEntity) {
                CompoundTag stackNbt = itemStack.getTag();
                if (stackNbt != null) {
                    suspiciousStewBlockEntity.readCustomDataFromItem(stackNbt);
                }
            }
        }
    }

    @Override
    public boolean isSame(ItemStack stack, BlockState state, BlockEntity blockEntity) {
        if (blockEntity instanceof SuspiciousStewBlockEntity suspiciousStewBlockEntity && super.isSame(stack, state, blockEntity)) {
            CompoundTag stewStackNbt = suspiciousStewBlockEntity.getStewStack().getTag();
            CompoundTag stackNbt = stack.getTag();

            // 检查两个NBT是否都包含效果列表
            if (stackNbt != null && stewStackNbt != null &&
                    stackNbt.contains("Effects") && stewStackNbt.contains("Effects")) {
                // 比较效果列表是否相同
                return Objects.equals(stewStackNbt.get("Effects"), stackNbt.get("Effects"));
            }
            // 如果其中一个没有效果，则认为它们不匹配
            return false;
        }
        return false;
    }

    @Override
    public ItemStack createStack(int count, BlockState state, @Nullable BlockEntity blockEntity) {
        ItemStack stack = new ItemStack(Items.SUSPICIOUS_STEW, count);

        if (blockEntity instanceof SuspiciousStewBlockEntity suspiciousStewBlockEntity) {
            // 将方块实体中的效果写入物品NBT
            CompoundTag nbt = stack.getOrCreateTag();
            suspiciousStewBlockEntity.writeCustomDataToItem(nbt);
        }

        return stack;
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        BlockEntity blockEntity = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);

        if (blockEntity instanceof SuspiciousStewBlockEntity suspiciousStewBlockEntity) {
            int count = state.getValue(NUMBER_OF_FOOD);
            ItemStack baseStack = suspiciousStewBlockEntity.getStewStack();
            List<ItemStack> drops = new java.util.ArrayList<>();

            for (int i = 0; i < count; i++) {
                drops.add(baseStack.copy());
            }

            return drops;
        }

        // 没有方块实体时，返回空列表
        return List.of();
    }
}