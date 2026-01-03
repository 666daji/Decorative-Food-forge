package org.dfood.item;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * 用于{@link org.dfood.block.FoodBlock}的双方块物品，
 * 在不和原版冲突的情况下，使玩家能够通过原版的物品放置出食物方块
 */
public class DoubleBlockItem extends BlockItem {
    private final Block block2;

    public DoubleBlockItem(Block block, Properties properties, Block block2) {
        super(block, properties);
        this.block2 = block2;
    }

    @Override
    protected @Nullable BlockState getPlacementState(BlockPlaceContext context) {
        Block actualBlock = getActualBlock(context);
        BlockState state = actualBlock.getStateForPlacement(context);
        return (state != null && canPlace(context, state)) ? state : null;
    }

    private Block getActualBlock(BlockPlaceContext context) {
        BlockState block = this.getBlock().defaultBlockState();
        if (context.getPlayer() != null && context.getPlayer().isCrouching()){
            return block2;
        }

        if (block.canSurvive(context.getLevel(), context.getClickedPos())) {
            return this.getBlock();
        } else {
            return this.block2;
        }
    }

    @Override
    public String getDescriptionId() {
        return this.getSecondBlock().getDescriptionId();
    }

    public Block getSecondBlock() {
        return this.block2;
    }
}