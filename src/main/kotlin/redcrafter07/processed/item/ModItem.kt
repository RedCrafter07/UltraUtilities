package redcrafter07.processed.item

import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag


open class ModItem(properties: Properties, private val itemID: String) : Item(properties) {
    override fun appendHoverText(stack: ItemStack, context: TooltipContext, tooltip: MutableList<Component>, flag: TooltipFlag) {
        getAdditionalTooltip(stack, context, flag)?.let { tooltip.add(it) }

        if (Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("item.processed.$itemID.tooltip"))
        } else tooltip.add(Component.translatable("item.processed.hold_shift"))
    }

    open fun getAdditionalTooltip(stack: ItemStack, context: TooltipContext, flag: TooltipFlag): MutableComponent? {
        return null
    }
}