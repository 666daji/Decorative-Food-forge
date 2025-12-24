package org.dfood.item;

import com.google.common.collect.Maps;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.dfood.block.FoodBlock;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Objects;

/**
 * 表示该模组独有的方块物品，虽然这样做会有很大一部分重复代码，但是可以很好地与其他模组兼容。
 * <p>示例:{@linkplain ModPotionItem}</p>
 */
public interface HaveBlock {
    /**
     * 针对forge端口的特殊处理。
     * <p>当{@linkplain FoodBlock#asItem()}无法直接从{@linkplain Item#BY_BLOCK}中获取对应的物品时则尝试从这里恢复</p>
     */
    Map<Block, Item> BY_BLOCK = Maps.newHashMap();

    Block getBlock();

    /**
     * 将该方块物品添加到指定的映射中。
     * @param map 目标映射
     * @param item 该方块物品对应的物品实例
     * @apiNote 该方法用于代替BlockItem.appendBlocks，以确保方块物品能够正确注册到模组的方块物品映射中。
     */
    default void appendBlocks(Map<Block, Item> map, Item item) {
        map.put(this.getBlock(), item);

        BY_BLOCK.put(this.getBlock(), item);
    }

    /**
     * 尝试从备用映射中取出对应物品。
     * @param block 原方块
     * @return 对应的物品，如果未找到则返回空
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
        } else if (!context.canPlace()) {
            return InteractionResult.FAIL;
        } else {
            BlockPlaceContext itemPlacementContext = this.getPlacementContext(context);
            if (itemPlacementContext == null) {
                return InteractionResult.FAIL;
            } else {
                BlockState blockState = this.getPlacementState(itemPlacementContext);
                if (blockState == null) {
                    return InteractionResult.FAIL;
                } else if (!this.place(itemPlacementContext, blockState)) {
                    return InteractionResult.FAIL;
                } else {
                    BlockPos blockPos = itemPlacementContext.getClickedPos();
                    Level world = itemPlacementContext.getLevel();
                    Player playerEntity = itemPlacementContext.getPlayer();
                    ItemStack itemStack = itemPlacementContext.getItemInHand();
                    BlockState blockState2 = world.getBlockState(blockPos);
                    if (blockState2.is(blockState.getBlock())) {
                        blockState2 = this.placeFromNbt(blockPos, world, itemStack, blockState2);
                        this.postPlacement(blockPos, world, playerEntity, itemStack, blockState2);
                        blockState2.getBlock().setPlacedBy(world, blockPos, blockState2, playerEntity, itemStack);
                        if (playerEntity instanceof ServerPlayer) {
                            CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayer)playerEntity, blockPos, itemStack);
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
                    if (playerEntity == null || !playerEntity.getAbilities().instabuild) {
                        itemStack.shrink(1);
                    }

                    return net.minecraft.world.InteractionResult.sidedSuccess(world.isClientSide);
                }
            }
        }
    }

    @Nullable
    default BlockState getPlacementState(BlockPlaceContext context) {
        BlockState blockState = this.getBlock().getStateForPlacement(context);
        return blockState != null && this.canPlace(context, blockState) ? blockState : null;
    }

    default SoundEvent getPlaceSound(BlockState state) {
        return state.getSoundType().getPlaceSound();
    }

    @Nullable
    default BlockPlaceContext getPlacementContext(BlockPlaceContext context) {
        return context;
    }

    default boolean postPlacement(BlockPos pos, Level world, @Nullable Player player, ItemStack stack, BlockState state) {
        return writeNbtToBlockEntity(world, player, pos, stack);
    }

    private BlockState placeFromNbt(BlockPos pos, Level world, ItemStack stack, BlockState state) {
        BlockState blockState = state;
        CompoundTag nbtCompound = stack.getTag();
        if (nbtCompound != null) {
            CompoundTag nbtCompound2 = nbtCompound.getCompound("BlockStateTag");
            StateDefinition<Block, BlockState> stateManager = state.getBlock().getStateDefinition();

            for (String string : nbtCompound2.getAllKeys()) {
                Property<?> property = stateManager.getProperty(string);
                if (property != null) {
                    String string2 = Objects.requireNonNull(nbtCompound2.get(string)).getAsString();
                    blockState = with(blockState, property, string2);
                }
            }
        }

        if (blockState != state) {
            world.setBlock(pos, blockState, Block.UPDATE_CLIENTS);
        }

        return blockState;
    }

    private static <T extends Comparable<T>> BlockState with(BlockState state, Property<T> property, String name) {
        return property.getValue(name).map(value -> state.setValue(property, value)).orElse(state);
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

    static boolean writeNbtToBlockEntity(Level world, @Nullable Player player, BlockPos pos, ItemStack stack) {
        MinecraftServer minecraftServer = world.getServer();
        if (minecraftServer != null) {
            CompoundTag nbtCompound = getBlockEntityNbt(stack);
            if (nbtCompound != null) {
                BlockEntity blockEntity = world.getBlockEntity(pos);
                if (blockEntity != null) {
                    if (!world.isClientSide && blockEntity.onlyOpCanSetNbt() && (player == null || !player.canUseGameMasterBlocks())) {
                        return false;
                    }

                    CompoundTag nbtCompound2 = blockEntity.saveWithFullMetadata();
                    CompoundTag nbtCompound3 = nbtCompound2.copy();
                    nbtCompound2.merge(nbtCompound);
                    if (!nbtCompound2.equals(nbtCompound3)) {
                        blockEntity.load(nbtCompound2);
                        blockEntity.setChanged();
                        return true;
                    }
                }
            }

        }
        return false;
    }

    @Nullable
    static CompoundTag getBlockEntityNbt(ItemStack stack) {
        return stack.getTagElement("BlockEntityTag");
    }

    default boolean place(BlockPlaceContext context, BlockState state) {
        return context.getLevel().setBlock(context.getClickedPos(), state, Block.UPDATE_ALL_IMMEDIATE | Block.UPDATE_CLIENTS);
    }
}