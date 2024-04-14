package redcrafter07.processed.block

import net.minecraft.core.Direction
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState

interface WrenchInteractableBlock {
    fun onWrenchUse(context: UseOnContext, state: BlockState);
}