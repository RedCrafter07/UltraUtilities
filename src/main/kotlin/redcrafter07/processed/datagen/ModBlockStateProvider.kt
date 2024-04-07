package redcrafter07.processed.datagen

import net.minecraft.data.PackOutput
import net.neoforged.neoforge.client.model.generators.BlockStateProvider
import net.neoforged.neoforge.common.data.ExistingFileHelper
import net.neoforged.neoforge.registries.DeferredBlock
import redcrafter07.processed.ProcessedMod
import redcrafter07.processed.block.*

class ModBlockStateProvider(output: PackOutput, exFileHelper: ExistingFileHelper) :
    BlockStateProvider(output, ProcessedMod.ID, exFileHelper) {
    override fun registerStatesAndModels() {
        blockWithItem(ModBlocks.BLITZ_ORE);
        blockWithItem(ModBlocks.BLOCK_PIPE_PRESSURIZER)

        pipeBlock(ModBlocks.BLOCK_ITEM_PIPE)
    }

    private fun blockWithItem(blockRegistryObject: DeferredBlock<*>) {
        simpleBlockWithItem(blockRegistryObject.get(), cubeAll(blockRegistryObject.get()));
    }

    private fun pipeBlock(blockRegistryObject: DeferredBlock<*>) {
        val block = blockRegistryObject.get()
        val id = blockRegistryObject.id.path
        val builder = getMultipartBuilder(block)

        builder.part().modelFile(models().getExistingFile(modLoc("block/${id}_center")))
            .rotationY(0).uvLock(false).addModel().end()

        val directions = arrayOf("north", "east", "south", "west", "top", "bottom")
        val states = arrayOf("normal", "push", "pull")

        for (state in states) {
            for (direction in directions) {
                val pipeDirectionState = when (direction) {
                    "north" -> PIPE_STATE_NORTH
                    "east" -> PIPE_STATE_EAST
                    "south" -> PIPE_STATE_SOUTH
                    "west" -> PIPE_STATE_WEST
                    "top" -> PIPE_STATE_TOP
                    "bottom" -> PIPE_STATE_BOTTOM
                    else -> PIPE_STATE_NORTH
                }

                val pipeLikeState = when (state) {
                    "normal" -> PipeLikeState.Normal
                    "push" -> PipeLikeState.Push
                    "pull" -> PipeLikeState.Pull
                    else -> PipeLikeState.Normal
                }

                if (direction != "top" && direction != "bottom")
                    builder.part().modelFile(models().getExistingFile(modLoc("block/${id}_$state")))
                        .rotationY(getRotationY(direction)).uvLock(false).addModel()
                        .condition(pipeDirectionState, pipeLikeState).end()
                else
                    builder.part().modelFile(models().getExistingFile(modLoc("block/${id}_$state")))
                        .rotationX(getRotationX(direction)).uvLock(false).addModel()
                        .condition(pipeDirectionState, pipeLikeState).end()
            }
        }

        // register the item model

        itemModels().getBuilder(id).parent(models().getExistingFile(modLoc("block/${id}_center")))
    }

    private fun getRotationY(direction: String): Int {
        return when (direction) {
            "north" -> 0
            "east" -> 90
            "south" -> 180
            "west" -> 270
            else -> 0
        }
    }

    private fun getRotationX(direction: String): Int {
        return when (direction) {
            "top" -> 270
            "bottom" -> 90
            else -> 0
        }
    }
}