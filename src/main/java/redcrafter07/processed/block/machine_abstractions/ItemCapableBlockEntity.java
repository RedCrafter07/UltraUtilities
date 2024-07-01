package redcrafter07.processed.block.machine_abstractions;

import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;

import javax.annotation.Nullable;

public interface ItemCapableBlockEntity {
    @Nullable
    IItemHandler itemCapabilityForSide(BlockSide side, BlockState state);
}
