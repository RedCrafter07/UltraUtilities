package redcrafter07.processed.block

import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item.TooltipContext
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.block.Block

interface AdditionalBlockInfo {
    fun getAdditionalTooltip(stack: ItemStack, context: TooltipContext, flag: TooltipFlag): MutableComponent? {
        return null
    }
}

open class ModBlockItem(block: Block, itemProperties: Properties, private val itemID: String) :
    BlockItem(block, itemProperties) {
    override fun appendHoverText(
        stack: ItemStack,
        context: TooltipContext,
        tooltip: MutableList<Component>,
        flag: TooltipFlag
    ) {
        tooltip.add(Component.translatable("block.processed.$itemID.tooltip"))

        val thisBlock = block
        if (thisBlock is AdditionalBlockInfo) {
            thisBlock.getAdditionalTooltip(stack, context, flag)
                ?.let { tooltip.add(it); }
        }

        tooltip.add(Component.empty())

        super.appendHoverText(stack, context, tooltip, flag)
    }
}

open class TieredModBlockItem(private val tieredProcessedBlock: TieredProcessedBlock, itemProperties: Properties) :
    BlockItem(tieredProcessedBlock, itemProperties) {
    override fun appendHoverText(
        stack: ItemStack,
        context: TooltipContext,
        components: MutableList<Component>,
        flag: TooltipFlag
    ) {
        tieredProcessedBlock.getDescription(components, flag)

        val thisBlock = block
        if (thisBlock is AdditionalBlockInfo) {
            thisBlock.getAdditionalTooltip(stack, context, flag)
                ?.let { components.add(it); }
        }

        components.add(Component.empty())
        super.appendHoverText(stack, context, components, flag)
    }

    override fun getName(itemStack: ItemStack): Component {
        return tieredProcessedBlock.name
    }
}