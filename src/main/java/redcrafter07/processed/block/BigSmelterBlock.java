package redcrafter07.processed.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import org.jetbrains.annotations.Nullable;
import redcrafter07.processed.block.tile_entities.BigSmelterBlockEntity;
import redcrafter07.processed.multiblock.MultiblockBlock;

public class BigSmelterBlock extends MultiblockBlock {
    public BigSmelterBlock() {
        super(Properties.of().pushReaction(PushReaction.BLOCK));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BigSmelterBlockEntity(pos, state);
    }
}
