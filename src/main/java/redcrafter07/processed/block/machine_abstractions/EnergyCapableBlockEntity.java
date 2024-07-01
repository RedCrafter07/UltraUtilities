package redcrafter07.processed.block.machine_abstractions;

import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;

import javax.annotation.Nullable;

public interface EnergyCapableBlockEntity {
    @Nullable
    IEnergyStorage energyCapabilityForSide(BlockSide side, BlockState state);
}
