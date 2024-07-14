package redcrafter07.processed.block.machine_abstractions;

import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import redcrafter07.processed.block.tile_entities.capabilities.SimpleDroppingContainer;
import redcrafter07.processed.item.ModItems;

import javax.annotation.Nullable;

public abstract class ProcessedBlock extends Block implements EntityBlock {
    public ProcessedBlock(Properties properties) {
        super(properties.pushReaction(PushReaction.BLOCK));
    }

    public static final DirectionProperty STATE_FACING = BlockStateProperties.FACING;
    public static final DirectionProperty STATE_HORIZONTAL_FACING = BlockStateProperties.HORIZONTAL_FACING;

    @Override
    public ItemInteractionResult useItemOn(
            ItemStack item,
            BlockState block,
            Level level,
            BlockPos blockPos,
            Player player,
            InteractionHand hand,
            BlockHitResult hitResult
    ) {
        if (item.is(ModItems.WRENCH)) return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
        return super.useItemOn(
                item,
                block,
                level,
                blockPos,
                player,
                hand,
                hitResult
        );
    }

    protected void addBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateDefinition) {
    }

    @Override
    public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateDefinition) {
        stateDefinition.add(STATE_HORIZONTAL_FACING);
        addBlockStateDefinition(stateDefinition);
    }

    public @Nullable BlockState getBlockState(BlockPlaceContext context) {
        return null;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        var blockState = getBlockState(context);
        if (blockState == null) blockState = defaultBlockState();
        return blockState.setValue(STATE_HORIZONTAL_FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(
            Level level,
            BlockState state,
            BlockEntityType<T> blockEntityType
    ) {
        return (lv, pos, tickState, ticker) -> {
            if (ticker instanceof ProcessedMachine machine) machine.handleTick(lv, pos, tickState);
        };
    }

    public void addDrops(SimpleDroppingContainer drops, @Nullable BlockEntity blockEntity) {
    }

    @Override
    public void onRemove(
            BlockState state,
            Level level,
            BlockPos pos,
            BlockState newState,
            boolean isMoving
    ) {
        if (state.is(newState.getBlock())) {
            super.onRemove(state, level, pos, newState, isMoving);
            return;
        }
        final var blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof ProcessedMachine machine) machine.dropItems();
        final var container = new SimpleDroppingContainer();
        addDrops(container, blockEntity);
        if (container.getContainerSize() > 0) Containers.dropContents(level, pos, container);


        super.onRemove(state, level, pos, newState, isMoving);
    }
}

