package org.dfood.block.entity;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.ListTag;
import net.minecraft.core.BlockPos;

import java.util.HashMap;
import java.util.Map;

public class SuspiciousStewBlockEntity extends BlockEntity {
    protected final Map<Integer, Integer> EffectMap = new HashMap<>();

    public SuspiciousStewBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public SuspiciousStewBlockEntity(BlockPos pos, BlockState state) {
        this(ModBlockEntityTypes.SUSPICIOUS_STEW_BLOCK_ENTITY.get(), pos, state);
    }

    private void readEffectsFromNbt(CompoundTag nbt) {
        EffectMap.clear();
        // 检查NBT中是否包含效果列表
        if (nbt != null && nbt.contains("Effects", Tag.TAG_LIST)) {
            ListTag effectsList = nbt.getList("Effects", Tag.TAG_COMPOUND);
            // 获取效果ID
            for (int i = 0; i < effectsList.size(); i++) {
                CompoundTag effectTag = effectsList.getCompound(i);
                if (effectTag.contains("EffectId", Tag.TAG_ANY_NUMERIC)) {
                    int effectId = effectTag.getInt("EffectId");
                    // 获取持续时间，如果不存在则使用默认值160
                    int duration = effectTag.contains("EffectDuration", Tag.TAG_ANY_NUMERIC)
                            ? effectTag.getInt("EffectDuration")
                            : 160;

                    EffectMap.put(effectId, duration);
                }
            }
        }
    }

    public void readCustomDataFromItem(CompoundTag nbt) {
        readEffectsFromNbt(nbt);
        this.setChanged();
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        readEffectsFromNbt(nbt);
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);
        writeCustomDataToItem(nbt);
    }

    public void writeCustomDataToItem(CompoundTag nbt) {
        if (!EffectMap.isEmpty()) {
            ListTag effectsList = new ListTag();

            for (Map.Entry<Integer, Integer> entry : EffectMap.entrySet()) {
                CompoundTag effectTag = new CompoundTag();
                effectTag.putInt("EffectId", entry.getKey());
                effectTag.putInt("EffectDuration", entry.getValue());
                effectsList.add(effectTag);
            }

            nbt.put("Effects", effectsList);
        }
    }

    public ItemStack getStewStack() {
        ItemStack stack = new ItemStack(Items.SUSPICIOUS_STEW);
        writeCustomDataToItem(stack.getOrCreateTag());
        return stack;
    }

    public Map<Integer, Integer> getEffectMap() {
        return new HashMap<>(EffectMap);
    }

    public void addEffect(int effectId, int duration) {
        EffectMap.put(effectId, duration);
        this.setChanged();
    }

    public void clearEffects() {
        EffectMap.clear();
        this.setChanged();
    }
}