package redcrafter07.processed.block

import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.block.Block

open class ModBlockItem(block: Block, itemProperties: Properties, private val itemID: String) :
    BlockItem(block, itemProperties) {
    override fun appendHoverText(stack: ItemStack, context: TooltipContext, tooltip: MutableList<Component>, flag: TooltipFlag) {
        if (Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("block.processed.$itemID.tooltip"))
        } else tooltip.add(Component.translatable("item.processed.hold_shift"))
        tooltip.add(Component.empty())

        super.appendHoverText(stack, context, tooltip, flag)
    }
}