package redcrafter07.processed.item

import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag


open class ModItem(properties: Properties, private val itemID: String) : Item(properties) {
    override fun appendHoverText(
        stack: ItemStack,
        context: TooltipContext,
        tooltip: MutableList<Component>,
        flag: TooltipFlag
    ) {
        tooltip.add(Component.translatable("item.processed.$itemID.tooltip"))
        getAdditionalTooltip(stack, context, flag)?.let { tooltip.add(it) }

        tooltip.add(Component.empty())
    }

    open fun getAdditionalTooltip(stack: ItemStack, context: TooltipContext, flag: TooltipFlag): MutableComponent? {
        return null
    }
}