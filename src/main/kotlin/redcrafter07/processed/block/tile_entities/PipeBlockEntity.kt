package redcrafter07.processed.block.tile_entities

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import redcrafter07.processed.ProcessedMod
import redcrafter07.processed.block.PipeLikeState

class PipeBlockEntity(pos: BlockPos, state: BlockState): BlockEntity(ModTileEntities.PIPE_BLOCK_ENTITY.get(), pos, state) {
    var stateNorth: PipeLikeState = PipeLikeState.Normal;
    var stateSouth: PipeLikeState = PipeLikeState.Normal;
    var stateWest: PipeLikeState = PipeLikeState.Normal;
    var stateEast: PipeLikeState = PipeLikeState.Normal;
    var stateUp: PipeLikeState = PipeLikeState.Normal;
    var stateDown: PipeLikeState = PipeLikeState.Normal;

    fun getState(direction: Direction): PipeLikeState {
        return when (direction) {
            Direction.UP -> stateUp
            Direction.DOWN -> stateDown
            Direction.NORTH -> stateNorth
            Direction.SOUTH -> stateSouth
            Direction.WEST -> stateWest
            Direction.EAST -> stateEast
        }
    }

    fun setState(direction: Direction, value: PipeLikeState) {
        when (direction) {
            Direction.UP -> stateUp = value
            Direction.DOWN -> stateDown = value
            Direction.NORTH -> stateNorth = value
            Direction.SOUTH -> stateSouth = value
            Direction.WEST -> stateWest = value
            Direction.EAST -> stateEast = value;
        }
    }

    override fun saveAdditional(nbt: CompoundTag) {
        super.saveAdditional(nbt)
        val ushort = stateUp.save().rotateLeft(10) or
                stateDown.save().rotateLeft(8) or
                stateWest.save().rotateLeft(6) or
                stateEast.save().rotateLeft(4) or
                stateNorth.save().rotateLeft(2) or
                stateSouth.save()
        ProcessedMod.LOGGER.info("Saving PipeBlockEntityState: {}", ushort);
        nbt.putShort("states", ushort.toShort())
    }

    override fun load(nbt: CompoundTag) {
        super.load(nbt)
        val ushort = nbt.getShort("states").toUShort();
        ProcessedMod.LOGGER.info("Loading PipeBlockEntityState: {}", ushort);
        val utwo = 0b11.toUShort();
        stateUp = PipeLikeState.load(ushort.rotateRight(10) and utwo);
        stateDown = PipeLikeState.load(ushort.rotateRight(8) and utwo);
        stateWest = PipeLikeState.load(ushort.rotateRight(6) and utwo);
        stateEast = PipeLikeState.load(ushort.rotateRight(4) and utwo);
        stateNorth = PipeLikeState.load(ushort.rotateRight(2) and utwo);
        stateSouth = PipeLikeState.load(ushort and utwo);
        ProcessedMod.LOGGER.info("Loaded PipeblockEntity: up {} down {} north {} south {} west {} east {}", stateUp, stateDown, stateNorth, stateSouth, stateWest, stateEast);
    }
}