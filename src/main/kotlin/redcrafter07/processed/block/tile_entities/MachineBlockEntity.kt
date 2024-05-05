package redcrafter07.processed.block.tile_entities

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.state.BlockState
import redcrafter07.processed.block.ProcessedTier

class MachineBlockEntity(pos: BlockPos, state: BlockState): TieredProcessedMachine(ModTileEntities.MACHINE.get(), pos, state) {
}