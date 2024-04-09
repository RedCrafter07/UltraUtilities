package redcrafter07.processed.datagen.blocks

import net.minecraft.world.level.block.Block
import net.neoforged.neoforge.client.model.generators.BlockStateProvider
import net.neoforged.neoforge.registries.DeferredBlock
import redcrafter07.processed.block.*

class PipeProvider(private val blockStateProvider: BlockStateProvider) {

    //destructure properties from blockStateProvider
    private fun models() = blockStateProvider.models()
    private fun itemModels() = blockStateProvider.itemModels()
    private fun modLoc(path: String) = blockStateProvider.modLoc(path)
    private fun getMultipartBuilder(block: Block) = blockStateProvider.getMultipartBuilder(block)

    private val directions = arrayOf("north", "east", "south", "west", "top", "bottom")
    private val states = arrayOf("normal", "push", "pull")


    fun pipeBlock(blockRegistryObject: DeferredBlock<*>, texturePath: String) {
        val blockID = blockRegistryObject.id.path

        println("Registered $blockID")

        pipeBlockModels(blockRegistryObject, texturePath)
        pipeItemModel(blockID)
        pipeBlockStates(blockRegistryObject)
    }

    private fun pipeBlockModels(blockRegistryObject: DeferredBlock<*>, texturePath: String) {
        val id = blockRegistryObject.id.path

        println("Registering models for $id.")

        val newStates = states.toMutableList().apply { add("center") }.toTypedArray()

        for (state in newStates) {
            println("Registering model for $id with state $state.")

            // this should be registered into the /pipe folder for the block models
            models().getBuilder("block/pipe/${id}_$state")
                .parent(models().getExistingFile(modLoc("block/pipe_components/$state")))
                .texture("0", texturePath)
                .texture("particle", texturePath)
        }
    }

    private fun pipeItemModel(blockID: String) {
        itemModels().getBuilder(blockID).parent(models().getBuilder("block/pipe/${blockID}_center"))
    }

    private fun pipeBlockStates(blockRegistryObject: DeferredBlock<*>) {
        val block = blockRegistryObject.get()
        val id = blockRegistryObject.id.path
        val builder = getMultipartBuilder(block)

        println("Registering states for $id.")

        builder.part().modelFile(models().getBuilder("block/pipe/${id}_center")).rotationY(0).uvLock(false).addModel()
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
                    builder.part().modelFile(models().getBuilder("block/pipe/${id}_$state")).uvLock(false)

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