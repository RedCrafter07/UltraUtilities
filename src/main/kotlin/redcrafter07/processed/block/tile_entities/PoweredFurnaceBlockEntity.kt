package redcrafter07.processed.block.tile_entities

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.FurnaceBlock

class PoweredFurnaceBlockEntity(pos: BlockPos, state: BlockState): TieredProcessedMachine(ModTileEntities.MACHINE.get(), pos, state) {

}