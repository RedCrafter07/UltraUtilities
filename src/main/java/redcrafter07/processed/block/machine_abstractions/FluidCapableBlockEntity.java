package redcrafter07.processed.block.machine_abstractions;

import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import javax.annotation.Nullable;

public interface FluidCapableBlockEntity {
    @Nullable
    IFluidHandler fluidCapabilityForSide(@Nullable BlockSide side, BlockState state);
}
