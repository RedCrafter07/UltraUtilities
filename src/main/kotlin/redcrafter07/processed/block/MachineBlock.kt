package redcrafter07.processed.block

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import redcrafter07.processed.block.tile_entities.MachineBlockEntity
import redcrafter07.processed.block.tile_entities.TieredProcessedMachine

class MachineBlock(private val tier: ProcessedTier): Block(Properties.of().sound(SoundType.STONE)), EntityBlock {
    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        val entity = MachineBlockEntity(pos, state);
        entity.tier = tier;
        return entity;
    }
}