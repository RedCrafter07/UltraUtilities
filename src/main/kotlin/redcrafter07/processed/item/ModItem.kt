package redcrafter07.processed.item

import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level

open class ModItem(properties: Properties, private val itemID: String) : Item(properties) {
    override fun appendHoverText(stack: ItemStack, world: Level?, tooltip: MutableList<Component>, flag: TooltipFlag) {
        if (Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("item.$itemID.tooltip"))
        } else tooltip.add(Component.translatable("item.hold_shift"))
    }
}