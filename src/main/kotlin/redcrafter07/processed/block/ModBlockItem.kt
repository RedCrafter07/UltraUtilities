package redcrafter07.processed.block

import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block

open class ModBlockItem(block: Block, itemProperties: Properties, private val itemID: String) :
    BlockItem(block, itemProperties) {
    override fun appendHoverText(stack: ItemStack, world: Level?, tooltip: MutableList<Component>, flag: TooltipFlag) {
        if (Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("block.processed.$itemID.tooltip"))
        } else tooltip.add(Component.translatable("item.processed.hold_shift"))
    }
}