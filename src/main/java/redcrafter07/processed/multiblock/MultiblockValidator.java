package redcrafter07.processed.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;

import javax.annotation.Nullable;
import java.util.Set;

public interface MultiblockValidator {
    @Nullable
    Set<BlockPos> getBlocks(LevelAccessor level, BlockPos controller, Direction facing);
}
