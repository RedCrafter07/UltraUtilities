package redcrafter07.processed.datagen.blocks

import net.neoforged.neoforge.client.model.generators.BlockStateProvider
import net.neoforged.neoforge.registries.DeferredBlock
import redcrafter07.processed.block.*

class PipeProvider(private val blockStateProvider: BlockStateProvider) {

    private val models = blockStateProvider::models
    private val itemModels = blockStateProvider::itemModels
    private val modLoc = blockStateProvider::modLoc
    private val getMultipartBuilder = blockStateProvider::getMultipartBuilder

    private val directions = arrayOf("north", "east", "south", "west", "top", "bottom")
    private val states = arrayOf("normal", "push", "pull")


    fun pipeBlock(blockRegistryObject: DeferredBlock<*>) {
        val blockID = blockRegistryObject.id.path

        pipeBlockModels(blockRegistryObject)
        pipeItemModel(blockID)
        pipeBlockStates(blockRegistryObject)
    }

    private fun pipeBlockModels(blockRegistryObject: DeferredBlock<*>) {
        val id = blockRegistryObject.id.path

        val newStates = states.toMutableList().apply { add("center") }.toTypedArray()

        for (state in newStates) {

            // this should be registered into the /pipe folder for the block models
            models().getBuilder("block/pipe/${id}/$state")
                .parent(models().getExistingFile(modLoc("block/pipe_components/$state")))
                .texture("0", modLoc("block/pipe/$id")).texture("particle", modLoc("block/pipe/$id"))
        }
    }

    private fun pipeItemModel(blockID: String) {
        itemModels().getBuilder(blockID).parent(models().getBuilder("block/pipe/${blockID}/center"))
    }

    private fun pipeBlockStates(blockRegistryObject: DeferredBlock<*>) {
        val block = blockRegistryObject.get()
        val id = blockRegistryObject.id.path
        val builder = getMultipartBuilder(block)

        builder.part().modelFile(models().getBuilder("block/pipe/${id}/center")).rotationY(0).uvLock(false).addModel()
            .end()

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

                val newModelBuilder =
                    builder.part().modelFile(models().getBuilder("block/pipe/${id}/$state")).uvLock(false)

                if (direction != "top" && direction != "bottom") newModelBuilder.rotationY(getRotationY(direction))
                else newModelBuilder.rotationX(getRotationX(direction))

                newModelBuilder.addModel().condition(pipeDirectionState, pipeLikeState).end()
            }
        }
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