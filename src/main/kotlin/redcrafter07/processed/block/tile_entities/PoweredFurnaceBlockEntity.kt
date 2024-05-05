package redcrafter07.processed.block.tile_entities

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.state.BlockState

class PoweredFurnaceBlockEntity(pos: BlockPos, state: BlockState): TieredProcessedMachine(ModTileEntities.MACHINE.get(), pos, state) {
}