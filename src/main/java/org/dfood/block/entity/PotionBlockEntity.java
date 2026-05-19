package org.dfood.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class PotionBlockEntity extends ComplexFoodBlockEntity {
    public PotionBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.POTION_BLOCK_ENTITY.get(), pos, state);
    }

    /**
     * 获取指定索引处的药水注册项。
     *
     * @param index 索引位置
     * @return 药水的 Holder，若未找到则返回 null
     */
    @Nullable
    public Holder<Potion> getPotionAtIndex(int index) {
        PotionContents potionContents = getPotionContentsAtIndex(index);
        if (potionContents != null) {
            return potionContents.potion().orElse(null);
        }
        return null;
    }

    /**
     * 获取指定索引处的药水内容组件。
     *
     * @param index 索引位置
     * @return 药水内容组件，若未找到则返回 null
     */
    @Nullable
    public PotionContents getPotionContentsAtIndex(int index) {
        DataComponentPatch patch = getPatchAt(index);
        if (patch != null && !patch.equals(DataComponentPatch.EMPTY)) {
            Optional<? extends PotionContents> opt = patch.get(DataComponents.POTION_CONTENTS);
            if (opt.isPresent()) {
                return opt.get();
            }
        }
        return null;
    }

    /**
     * 获取指定索引处的药水颜色。
     *
     * @param index 索引位置
     * @return 颜色值，若未找到则返回默认颜色 16253176
     */
    public int getColor(int index) {
        PotionContents potionContents = getPotionContentsAtIndex(index);
        if (potionContents != null) {
            return potionContents.getColor();
        }
        return 16253176; // 默认颜色
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveWithoutMetadata(registries);
    }

    @Override
    @Nullable
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}