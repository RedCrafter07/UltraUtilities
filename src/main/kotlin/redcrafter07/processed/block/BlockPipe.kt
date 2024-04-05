package redcrafter07.processed.block

import net.minecraft.util.StringRepresentable
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.properties.EnumProperty

enum class BlockState(name: String) : StringRepresentable {
    Normal("n"),
    Push("s"),
    Pull("l");

    override fun getSerializedName(): String {
        return this.name;
    }
}

class BlockPipe : Block {
    val STATE = EnumProperty.create("state", BlockState::class.java);

    constructor() : super(Properties.of()
        .sound(SoundType.STONE)
        .isRedstoneConductor(StatePredicate { _, _, _ -> false })
    ) {
        val defaultState = this.defaultBlockState();
        defaultState.setValue(STATE, BlockState.Normal);
    }
}