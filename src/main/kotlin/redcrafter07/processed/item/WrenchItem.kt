package redcrafter07.processed.item

import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionResult
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.UseOnContext

class WrenchItem : Item(Properties().stacksTo(1)) {

    override fun onItemUseFirst(stack: ItemStack, context: UseOnContext): InteractionResult {
        val blockState = context.level.getBlockState(context.clickedPos) ?: return super.onItemUseFirst(stack, context);

        return InteractionResult.SUCCESS;
    }
}