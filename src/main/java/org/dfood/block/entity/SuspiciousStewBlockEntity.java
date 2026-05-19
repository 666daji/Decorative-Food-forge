package org.dfood.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.SuspiciousStewEffects;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class SuspiciousStewBlockEntity extends BlockEntity {
    private SuspiciousStewEffects effects = SuspiciousStewEffects.EMPTY;

    public SuspiciousStewBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public SuspiciousStewBlockEntity(BlockPos pos, BlockState state) {
        this(ModBlockEntityTypes.SUSPICIOUS_STEW_BLOCK_ENTITY.get(), pos, state);
    }

    public void readCustomDataFromItem(SuspiciousStewEffects effects) {
        this.effects = effects != null ? effects : SuspiciousStewEffects.EMPTY;
        this.setChanged();
    }

    public SuspiciousStewEffects createStewEffectsComponent() {
        return this.effects;
    }

    public ItemStack getStewStack() {
        ItemStack stack = new ItemStack(Items.SUSPICIOUS_STEW);
        if (!this.effects.equals(SuspiciousStewEffects.EMPTY)) {
            stack.set(DataComponents.SUSPICIOUS_STEW_EFFECTS, this.effects);
        }
        return stack;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (!this.effects.equals(SuspiciousStewEffects.EMPTY)) {
            SuspiciousStewEffects.CODEC.encodeStart(NbtOps.INSTANCE, this.effects)
                    .resultOrPartial()
                    .ifPresent(encoded -> tag.put("StewEffects", encoded));
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("StewEffects")) {
            SuspiciousStewEffects.CODEC.parse(NbtOps.INSTANCE, tag.get("StewEffects"))
                    .resultOrPartial()
                    .ifPresent(e -> this.effects = e);
        }
    }
}