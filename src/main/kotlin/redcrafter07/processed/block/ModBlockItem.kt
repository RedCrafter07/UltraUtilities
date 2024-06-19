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

open class TieredModBlockItem(private val tieredProcessedBlock: TieredProcessedBlock, itemProperties: Properties) : BlockItem(tieredProcessedBlock, itemProperties) {
    override fun appendHoverText(
        itemStack: ItemStack,
        context: TooltipContext,
        components: MutableList<Component>,
        flag: TooltipFlag
    ) {
        if (Screen.hasShiftDown()) tieredProcessedBlock.getShiftDescription(components, flag)
        else components.add(Component.translatable("item.processed.hold_shift"))
        components.add(Component.empty())
        tieredProcessedBlock.getDescription(components, flag)
        components.add(Component.empty())

        super.appendHoverText(itemStack, context, components, flag)
    }

    override fun getName(itemStack: ItemStack): Component {
        return tieredProcessedBlock.name
    }
}