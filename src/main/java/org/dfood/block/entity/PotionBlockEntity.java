package org.dfood.block.entity;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;

public class PotionBlockEntity extends ComplexFoodBlockEntity {
    public PotionBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.POTION_BLOCK_ENTITY.get(), pos, state);
    }

    /**
     * 获取指定索引处的药水
     */
    @Nullable
    public Potion getPotionAtIndex(int index) {
        CompoundTag nbt = getNbtAt(index);
        return nbt != null ? getPotionFromNbt(nbt) : null;
    }

    /**
     * 从NBT中读取药水数据
     */
    private Potion getPotionFromNbt(CompoundTag nbt) {
        if (nbt.contains("Potion")) {
            return Potion.byName(nbt.getString("Potion"));
        }
        return Potions.EMPTY;
    }

    public int getColor(int index) {
        CompoundTag nbt = getNbtAt(index);
        if (nbt != null && !nbt.isEmpty()) {
            Potion potion = getPotionFromNbt(nbt);
            return potion != null ? PotionUtils.getColor(potion) : 16253176;
        }
        return 16253176; // 默认颜色
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    @Override
    @Nullable
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}