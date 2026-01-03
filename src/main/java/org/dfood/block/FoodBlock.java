package org.dfood.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.dfood.item.DoubleBlockItem;
import org.dfood.item.HaveBlock;
import org.dfood.shape.FoodShapeHandle;
import org.dfood.tag.ModTags;
import org.dfood.util.DFoodUtils;
import org.dfood.util.IntegerPropertyManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 食物方块的抽象基类，定义了所有食物方块的通用行为和属性。
 * 支持堆叠放置、方向控制、自定义交互逻辑等功能。
 */
public class FoodBlock extends Block {
    /** 所有食物方块的注册集合，用于统一管理 */
    private static final Set<FoodBlock> REGISTERED_FOOD_BLOCKS = new HashSet<>();

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    protected static final FoodShapeHandle SHAPE_HANDLE = FoodShapeHandle.getInstance();

    public final IntegerProperty NUMBER_OF_FOOD;
    public final int MAX_FOOD;

    /** 是否使用对应物品的翻译键（用于替换原版物品的情况） */
    public final boolean useItemTranslationKey;
    /** 简化的固定形状（若为null则使用动态形状） */
    @Nullable
    public final VoxelShape simpleShape;
    /**
     * 强制指定对应物品（用于{@linkplain DoubleBlockItem}中的第二个方块）
     * @see EnforceAsItem
     */
    @Nullable
    private final EnforceAsItem cItem;

    /** 自定义交互钩子，允许子类扩展交互行为 */
    @Nullable
    protected OnUseHook onUseHook = null;

    /**
     * 构造函数
     * @param settings               方块设置
     * @param maxFood               最大堆叠数量
     * @param isFood                是否为食物方块（控制是否加入注册集合）
     * @param simpleShape           固定形状（可为null）
     * @param useItemTranslationKey 是否使用物品翻译键
     * @param cItem                 强制指定的对应物品（可为null）
     */
    protected FoodBlock(Properties settings,
                        int maxFood,
                        boolean isFood,
                        @Nullable VoxelShape simpleShape,
                        boolean useItemTranslationKey,
                        @Nullable EnforceAsItem cItem) {
        super(settings);
        this.MAX_FOOD = maxFood;
        this.NUMBER_OF_FOOD = IntegerPropertyManager.create("number_of_food", MAX_FOOD);
        this.cItem = cItem;
        this.simpleShape = simpleShape;
        this.useItemTranslationKey = useItemTranslationKey;

        // 如果是食物方块，则加入全局注册集合
        if (isFood) {
            REGISTERED_FOOD_BLOCKS.add(this);
        }

        // 设置默认状态：朝北、数量为1
        this.registerDefaultState(this.getStateDefinition().any()
                .setValue(FACING, Direction.NORTH)
                .setValue(NUMBER_OF_FOOD, 1));
    }

    /**
     * 检查是否指定了强制对应物品
     * @return 若指定则返回true，否则返回false
     */
    public boolean hasCItem() {
        return this.cItem != null;
    }

    /**
     * 获取翻译键，根据配置决定使用物品翻译键或方块翻译键
     */
    @Override
    public String getDescriptionId() {
        if (useItemTranslationKey) {
            return "item." + BuiltInRegistries.BLOCK.getKey(this).toString().replace(':', '.');
        }
        return super.getDescriptionId();
    }

    /**
     * 获取方块的轮廓形状。
     * <p>当拥有简单的自定义形状时会直接返回，
     * 否则会从{@linkplain FoodShapeHandle}中获取</p>
     * @return 对应的形状
     */
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        if (simpleShape != null) {
            return simpleShape;
        }
        return SHAPE_HANDLE.getShape(state, NUMBER_OF_FOOD);
    }

    /**
     * 获取环境光遮蔽等级（食物方块通常为全亮）
     */
    @Override
    public float getShadeBrightness(BlockState state, BlockGetter world, BlockPos pos) {
        return 1.0F;
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState()
                .setValue(FACING, ctx.getHorizontalDirection())
                .setValue(NUMBER_OF_FOOD, 1);
    }

    @Override
    public InteractionResult use(BlockState state,
                                 Level world,
                                 BlockPos pos,
                                 Player player,
                                 InteractionHand hand,
                                 BlockHitResult hit) {
        // 优先执行自定义钩子
        if (this.onUseHook != null) {
            InteractionResult hookResult = this.onUseHook.interact(state, world, pos, player, hand, hit);
            if (hookResult != InteractionResult.PASS) {
                return hookResult;
            }
        }

        ItemStack handStack = player.getItemInHand(hand);
        int currentCount = state.getValue(NUMBER_OF_FOOD);
        BlockEntity blockEntity = world.getBlockEntity(pos);

        // 检查手持物品是否与方块匹配
        boolean isHoldingSameItem = isSame(handStack, state, blockEntity);

        // 尝试添加物品的情况
        if (isHoldingSameItem && currentCount < MAX_FOOD &&
                tryAdd(state, world, pos, player, handStack, blockEntity)) {
            // 播放放置声音
            playPlaceSound(world, pos, player);

            // 消耗物品（非创造模式）
            consumeItemIfNotCreative(player, handStack, hand);

            return InteractionResult.SUCCESS;
        }

        // 尝试取出物品的情况
        if (currentCount > 0 && tryRemove(state, world, pos, player, blockEntity)) {
            // 播放取出声音
            playTakeSound(world, pos, player);

            return InteractionResult.SUCCESS;
        }

        // 没有可执行的操作
        return InteractionResult.PASS;
    }

    /**
     * 尝试增加堆叠数量
     * @return 操作是否成功
     */
    protected boolean tryAdd(BlockState state,
                             Level world,
                             BlockPos pos,
                             Player player,
                             ItemStack handStack,
                             BlockEntity blockEntity) {
        int currentCount = state.getValue(NUMBER_OF_FOOD);
        BlockState newState = state.setValue(NUMBER_OF_FOOD, currentCount + 1);
        return world.setBlock(pos, newState, Block.UPDATE_ALL);
    }

    /**
     * 消耗玩家手中的物品
     * @param player 执行操作的玩家
     * @param handStack 手持物品堆栈
     * @param hand 使用的手
     * @return 消耗是否成功
     */
    protected boolean consumeItemIfNotCreative(Player player, ItemStack handStack, InteractionHand hand) {
        if (!player.isCreative() && !handStack.isEmpty()) {
            handStack.shrink(1);
            player.setItemInHand(hand, handStack);
            return true;
        }
        return false;
    }

    /**
     * 尝试减少堆叠数量
     * @return 操作是否成功
     */
    protected boolean tryRemove(BlockState state,
                                Level world,
                                BlockPos pos,
                                Player player,
                                BlockEntity blockEntity) {
        int currentCount = state.getValue(NUMBER_OF_FOOD);
        int newCount = currentCount - 1;

        if (newCount > 0) {
            BlockState newState = state.setValue(NUMBER_OF_FOOD, newCount);
            world.setBlock(pos, newState, Block.UPDATE_ALL);
        } else {
            world.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
        }

        // 给予玩家物品（非创造模式）
        if (!player.isCreative()) {
            ItemStack foodItem = createStack(1, state, blockEntity);
            if (!player.addItem(foodItem)) {
                Containers.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), foodItem);
            }
        }

        return true;
    }

    /**
     * 检查手持物品是否与方块匹配
     * @return 匹配返回true，否则返回false
     */
    public boolean isSame(ItemStack stack, BlockState state, BlockEntity blockEntity) {
        return stack.getItem() == this.asItem();
    }

    /**
     * 创建对应物品堆栈
     *
     * @param count 物品数量
     * @param state 对应的方块状态
     * @param blockEntity 对应的方块实体（可为null）
     * @return 物品堆栈
     * @throws IllegalArgumentException 当数量不在有效范围内时抛出
     */
    public ItemStack createStack(int count, BlockState state, @Nullable BlockEntity blockEntity) {
        if (count <= 0 || count > MAX_FOOD) {
            throw new IllegalArgumentException("Count must be between 1 and " + MAX_FOOD);
        }
        return new ItemStack(this.asItem(), count);
    }

    /**
     * 播放放置物品的声音
     */
    public void playPlaceSound(Level world, BlockPos pos, Player player) {
        world.playSound(
                player,
                pos,
                this.soundType.getPlaceSound(),
                SoundSource.BLOCKS,
                1.0F,
                world.getRandom().nextFloat() * 0.1F + 0.9F
        );
    }

    /**
     * 播放取出物品的声音
     */
    public void playTakeSound(Level world, BlockPos pos, Player player) {
        world.playSound(
                player,
                pos,
                this.soundType.getBreakSound(),
                SoundSource.BLOCKS,
                1.0F,
                world.getRandom().nextFloat() * 0.1F + 0.9F
        );
    }

    public @Nullable OnUseHook getOnUseHook() {
        return onUseHook;
    }

    /**
     * 设置自定义交互钩子
     * @param onUseHook 自定义钩子实例（可为null以移除钩子）
     */
    public void setOnUseHook(@Nullable OnUseHook onUseHook) {
        this.onUseHook = onUseHook;
    }

    /**
     * 获取所有注册的食物方块，需要方块在构造时指定isFood为true
     * @return 不可变集合，包含所有注册的食物方块
     */
    public static Set<FoodBlock> getRegisteredFoodBlocks() {
        return Collections.unmodifiableSet(REGISTERED_FOOD_BLOCKS);
    }

    /**
     * 检查是否指定了强制对应物品
     * @return 若指定则返回true，否则返回false
     */
    public boolean haveCItem() {
        return this.cItem != null;
    }

    @Override
    public @NotNull BlockState updateShape(BlockState state,
                                           @NotNull Direction direction,
                                           @NotNull BlockState neighborState,
                                           @NotNull LevelAccessor world,
                                           @NotNull BlockPos pos,
                                           @NotNull BlockPos neighborPos) {
        return !state.canSurvive(world, pos)
                ? Blocks.AIR.defaultBlockState()
                : super.updateShape(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        BlockPos downPos = pos.below();
        BlockState checkState = world.getBlockState(downPos);
        return !checkState.is(ModTags.FOOD_PLACE) && !DFoodUtils.isModFoodBlock(checkState.getBlock());
    }

    /**
     * 硬编码掉落物，直接返回方块对应的物品
     */
    @Override
    public List<ItemStack> getDrops(BlockState state,
                                    LootParams.Builder builder) {
        int foodCount = state.getValue(NUMBER_OF_FOOD);
        if (foodCount <= 0) {
            return Collections.emptyList();
        }
        return Collections.singletonList(new ItemStack(this.asItem(), foodCount));
    }

    /**
     * 获取方块对应的物品（若指定了强制物品则返回该物品）
     */
    @Override
    public @NotNull Item asItem() {
        if (this.cItem != null) {
            return cItem.getItem();
        }

        return super.asItem() == Items.AIR?
                HaveBlock.byBlock(this):
                super.asItem();
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
        builder.add(IntegerPropertyManager.take());
    }

    /**
     * 完全使用默认的参数。
     */
    public static class Builder extends FoodBlockBuilder<FoodBlock, Builder> {
        private Builder() {}

        public static Builder create() {
            return new Builder();
        }

        @Override
        protected FoodBlock createBlock() {
            return new FoodBlock(
                    this.settings,
                    this.maxFood,
                    this.isFood,
                    this.simpleShape,
                    this.useItemTranslationKey,
                    this.cItem
            );
        }
    }

    /* ==================== 功能接口定义 ==================== */

    /**
     * 自定义交互钩子接口，允许为特定食物方块扩展交互逻辑
     */
    @FunctionalInterface
    public interface OnUseHook {
        /**
         * 定义方块被使用时的自定义行为
         * @return 操作结果，决定后续行为
         */
        InteractionResult interact(BlockState state, Level world, BlockPos pos,
                                   Player player, InteractionHand hand, BlockHitResult hit);
    }

    /**
     * 强制指定方块对应物品的接口
     */
    public interface EnforceAsItem {
        /**
         * 获取强制指定的物品
         */
        Item getItem();
    }
}