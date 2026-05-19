package org.dfood.block.entity;

import com.mojang.serialization.DataResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.dfood.block.ComplexFoodBlock;
import org.dfood.util.DFoodUtils;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * 复杂食物方块对应的方块实体，用于按栈结构（先进后出）存储物品堆栈的 NBT 数据。
 * <p>
 * 每个 {@link ComplexFoodBlock} 实例对应一个此实体，负责在放置和取出物品时保持 NBT 一致性。
 *
 * @see ComplexFoodBlock
 */
public class ComplexFoodBlockEntity extends BlockEntity {
    private static final Logger LOGGER = LoggerFactory.getLogger(ComplexFoodBlockEntity.class);

    protected final Stack<DataComponentPatch> patchStack = new Stack<>();
    protected int maxCapacity;

    public ComplexFoodBlockEntity(BlockPos pos, BlockState state) {
        this(ModBlockEntityTypes.COMPLEX_FOOD.get(), pos, state);
    }

    public ComplexFoodBlockEntity(BlockEntityType<? extends ComplexFoodBlockEntity> type, BlockPos pos, BlockState state) {
        super(type, pos, state);

        // 从方块状态中获取最大容量
        if (state.getBlock() instanceof ComplexFoodBlock complexFoodBlock) {
            this.maxCapacity = complexFoodBlock.MAX_FOOD;
        }
    }

    public boolean pushPatch(@Nullable DataComponentPatch patch) {
        if (patchStack.size() >= maxCapacity) {
            return false;
        }
        if (patch == null) {
            patch = DataComponentPatch.EMPTY;
        }
        patchStack.push(patch);
        setChanged();
        return true;
    }

    /**
     * 从栈中弹出 NBT 数据（先进后出）。
     *
     * @return 栈顶的 NBT 数据，若栈为空则返回 {@code null}
     */
    @Nullable
    public DataComponentPatch popPatch() {
        if (patchStack.isEmpty()) {
            return null;
        }
        DataComponentPatch popped = patchStack.pop();
        setChanged();
        return popped;
    }

    /**
     * 查看栈顶的 NBT 数据但不移除。
     *
     * @return 栈顶的 NBT 数据，若栈为空则返回 {@code null}
     */
    @Nullable
    public DataComponentPatch peekPatch() {
        return patchStack.isEmpty() ? null : patchStack.peek();
    }

    public int getPatchCount() {
        return patchStack.size();
    }

    /**
     * 检查栈是否已满。
     */
    public boolean isFull() {
        return patchStack.size() >= maxCapacity;
    }

    /**
     * 检查栈是否为空。
     */
    public boolean isEmpty() {
        return patchStack.isEmpty();
    }

    public void clearPatches() {
        patchStack.clear();
        setChanged();
    }

    /**
     * 序列化方块实体数据到 NBT。
     */
    @Override
    protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        super.saveAdditional(nbt, registries);

        ListTag patchList = new ListTag();
        for (DataComponentPatch patch : patchStack) {
            DataResult<Tag> result = DataComponentPatch.CODEC.encodeStart(NbtOps.INSTANCE, patch);
            result.result().ifPresent(patchList::add);
            result.error().ifPresent(error -> LOGGER.error("Failed to encode DataComponentPatch: {}", error.message()));
        }

        nbt.put("PatchStack", patchList);
        nbt.putInt("MaxCapacity", maxCapacity);
    }

    /**
     * 从 NBT 反序列化方块实体数据。
     */
    @Override
    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        super.loadAdditional(nbt, registries);

        patchStack.clear();

        if (nbt.contains("PatchStack", Tag.TAG_LIST)) {
            ListTag patchList = nbt.getList("PatchStack", Tag.TAG_COMPOUND);
            for (int i = 0; i < patchList.size(); i++) {
                DataResult<DataComponentPatch> result = DataComponentPatch.CODEC.parse(NbtOps.INSTANCE, patchList.getCompound(i));
                result.result().ifPresent(patchStack::push);
                result.error().ifPresent(error -> LOGGER.error("Failed to parse DataComponentPatch: {}", error.message()));
            }
        }

        if (nbt.contains("MaxCapacity", Tag.TAG_INT)) {
            maxCapacity = nbt.getInt("MaxCapacity");
        }
    }

    public List<DataComponentPatch> getAllPatches() {
        return new ArrayList<>(patchStack);
    }

    /**
     * 获取用于预览的所有存储物品堆栈，每个物品堆栈数量为1.
     *
     * @return 添加的所有原物品堆栈
     */
    public List<ItemStack> getAllStack() {
        int count = DFoodUtils.getFoodBlockCount(getBlockState());
        List<ItemStack> stacks = new ArrayList<>(count);

        for (int i = 0; i < count; i++) {
            ItemStack stack = new ItemStack(getBlockState().getBlock().asItem());
            DataComponentPatch patch = getPatchAt(i);
            if (patch != null && !patch.equals(DataComponentPatch.EMPTY)) {
                stack.applyComponents(patch);
            }
            stacks.add(stack);
        }

        return stacks;
    }

    /**
     * 获取指定索引处的 NBT 数据。
     *
     * @param index 索引位置，0 为栈底，size-1 为栈顶
     * @return 指定位置的 NBT 数据，若索引越界则返回 {@code null}
     */
    @Nullable
    public DataComponentPatch getPatchAt(int index) {
        if (index < 0 || index >= patchStack.size()) {
            return null;
        }
        return patchStack.get(index);
    }
}