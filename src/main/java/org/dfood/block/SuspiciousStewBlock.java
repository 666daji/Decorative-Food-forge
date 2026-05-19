package org.dfood.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.SuspiciousStewEffects;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.dfood.block.entity.SuspiciousStewBlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SuspiciousStewBlock extends FoodBlock implements EntityBlock {

    public SuspiciousStewBlock(Properties settings, int maxFood) {
        super(settings, maxFood, true, null, true, null);
    }

    public static class Builder extends FoodBlockBuilder<SuspiciousStewBlock, Builder> {
        private Builder() {}

        public static Builder create() {
            return new Builder();
        }

        @Override
        protected SuspiciousStewBlock createBlock() {
            return new SuspiciousStewBlock(this.settings, this.maxFood);
        }
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SuspiciousStewBlockEntity(pos, state);
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state,
                            @Nullable LivingEntity placer, ItemStack itemStack) {
        super.setPlacedBy(world, pos, state, placer, itemStack);
        if (!world.isClientSide) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof SuspiciousStewBlockEntity stewEntity) {
                SuspiciousStewEffects stewEffects = itemStack.get(DataComponents.SUSPICIOUS_STEW_EFFECTS);
                stewEntity.readCustomDataFromItem(stewEffects);
            }
        }
    }

    @Override
    public boolean isSame(ItemStack stack, BlockState state, @Nullable BlockEntity blockEntity) {
        if (blockEntity instanceof SuspiciousStewBlockEntity stewEntity && super.isSame(stack, state, blockEntity)) {
            SuspiciousStewEffects blockEffects = stewEntity.createStewEffectsComponent();
            SuspiciousStewEffects stackEffects = stack.get(DataComponents.SUSPICIOUS_STEW_EFFECTS);

            if (blockEffects.equals(SuspiciousStewEffects.EMPTY) && stackEffects == null) {
                return true;
            }
            if (blockEffects.equals(SuspiciousStewEffects.EMPTY) || stackEffects == null) {
                return false;
            }
            return blockEffects.equals(stackEffects);
        }
        return false;
    }

    @Override
    public ItemStack createStack(int count, BlockState state, @Nullable BlockEntity blockEntity) {
        ItemStack stack = new ItemStack(Items.SUSPICIOUS_STEW, count);

        if (blockEntity instanceof SuspiciousStewBlockEntity stewEntity) {
            SuspiciousStewEffects component = stewEntity.createStewEffectsComponent();
            if (!component.equals(SuspiciousStewEffects.EMPTY)) {
                stack.set(DataComponents.SUSPICIOUS_STEW_EFFECTS, component);
            }
        }
        return stack;
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        BlockEntity blockEntity = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);

        if (blockEntity instanceof SuspiciousStewBlockEntity stewEntity) {
            int count = state.getValue(NUMBER_OF_FOOD);
            ItemStack baseStack = stewEntity.getStewStack();
            List<ItemStack> drops = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                drops.add(baseStack.copy());
            }

            return drops;
        }

        // 没有方块实体时，返回空列表
        return List.of();
    }
}