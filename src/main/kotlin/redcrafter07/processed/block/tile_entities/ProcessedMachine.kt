package redcrafter07.processed.block.tile_entities

import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import org.joml.Vector2i
import redcrafter07.processed.block.WrenchInteractableBlock
import redcrafter07.processed.gui.ConfigScreen

enum class IoState {
    None,
    Input,
    Output,
    InputOutput,
    Additional,
    Auxiliary;


    companion object {
        fun load(value: Byte): IoState {
            return when (value.toInt()) {
                0 -> None
                1 -> Input
                2 -> Output
                3 -> InputOutput
                4 -> Additional
                5 -> Auxiliary
                else -> Input
            }
        }
    }

    fun save(): Byte {
        return when (this) {
            None -> 0.toByte()
            Input -> 1.toByte()
            Output -> 2.toByte()
            InputOutput -> 3.toByte()
            Additional -> 4.toByte()
            Auxiliary -> 5.toByte()
        }
    }

    fun next(): IoState {
        return when (this) {
            Auxiliary -> None
            None -> Input
            Input -> Output
            Output -> InputOutput
            InputOutput -> Additional
            Additional -> Auxiliary
        }
    }

    fun previous(): IoState {
        return when (this) {
            Input -> None
            Output -> Input
            InputOutput -> Output
            Additional -> InputOutput
            Auxiliary -> Additional
            None -> Auxiliary
        }
    }

    fun toComponent(): Component {
        return when (this) {
            None -> Component.translatable("processed.io_state.none")
            Input -> Component.translatable("processed.io_state.input")
            Output -> Component.translatable("processed.io_state.output")
            InputOutput -> Component.translatable("processed.io_state.input_output")
            Additional -> Component.translatable("processed.io_state.additional")
            Auxiliary -> Component.translatable("processed.io_state.auxiliary")
        }
    }
}

enum class IoSide {
    Top,
    Bottom,
    Left,
    Right,
    Front,
    Back;

    companion object {
        fun load(value: Byte): IoSide {
            return when (value.toInt()) {
                0 -> Top
                1 -> Bottom
                2 -> Left
                3 -> Right
                4 -> Front
                5 -> Back
                else -> Top
            }
        }
    }

    fun save(): Byte {
        return when (this) {
            Top -> 0.toByte()
            Bottom -> 1.toByte()
            Left -> 2.toByte()
            Right -> 3.toByte()
            Front -> 4.toByte()
            Back -> 5.toByte()
        }
    }

    fun getButtonPos(): Vector2i {
        return when (this) {
            Top -> Vector2i(27, 28)
            Bottom -> Vector2i(27, 54)
            Left -> Vector2i(14, 41)
            Front -> Vector2i(27, 41)
            Right -> Vector2i(40, 41)
            Back -> Vector2i(53, 41)
        }
    }

    fun toComponent(): Component {
        return when (this) {
            Top -> Component.translatable("processed.side.top")
            Bottom -> Component.translatable("processed.side.bottom")
            Left -> Component.translatable("processed.side.left")
            Right -> Component.translatable("processed.side.right")
            Front -> Component.translatable("processed.side.front")
            Back -> Component.translatable("processed.side.back")
        }
    }
}

open class ProcessedMachine(blockEntityType: BlockEntityType<*>, blockPos: BlockPos, blockState: BlockState) : BlockEntity(
    blockEntityType,
    blockPos, blockState
), WrenchInteractableBlock {
    private var sides: Array<IoState> = arrayOf(
        IoState.None,
        IoState.None,
        IoState.None,
        IoState.None,
        IoState.None,
        IoState.None,
        IoState.None,
        IoState.None,
        IoState.None,
        IoState.None,
        IoState.None,
        IoState.None
    )

    fun getSide(itemOrFluid: Boolean, side: IoSide): IoState {
        return if (itemOrFluid) sides[side.save().toInt()] else sides[side.save().toInt() + 6]
    }

    fun setSide(itemOrFluid: Boolean, side: IoSide, value: IoState) {
        if (itemOrFluid) sides[side.save().toInt()] = value
        else sides[side.save().toInt() + 6] = value
    }

    override fun load(nbt: CompoundTag) {
        super.load(nbt)

        val byteArray = nbt.getByteArray("io_states")
        for (index in 0..<12) {
            sides[index] = if (byteArray.size <= index) IoState.None else IoState.load(byteArray[index])
        }
    }

    override fun saveAdditional(nbt: CompoundTag) {
        super.saveAdditional(nbt)
        nbt.putByteArray("io_states", sides.map { it.save() }.toList())
    }

    override fun onWrenchUse(context: UseOnContext, state: BlockState) {
        val me = context.level.getBlockEntity(context.clickedPos) ?: return;
        if (me !is ProcessedMachine) return;

        if (context.level.isClientSide) Minecraft.getInstance().setScreen(ConfigScreen(this, context.clickedPos))
    }
}