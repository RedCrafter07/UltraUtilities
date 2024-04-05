package redcrafter07.processed.item

import net.minecraft.world.InteractionResult
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.UseOnContext
import redcrafter07.processed.block.WrenchInteractableBlock

class WrenchItem : Item(Properties().stacksTo(1)) {

    override fun onItemUseFirst(stack: ItemStack, context: UseOnContext): InteractionResult {
        val blockState = context.level.getBlockState(context.clickedPos) ?: return super.onItemUseFirst(stack, context)
        val block = blockState.block

        if (block is WrenchInteractableBlock) {
            block.onWrenchUse(context, blockState)
            return InteractionResult.SUCCESS
        } else {
            return super.onItemUseFirst(stack, context)
        }
    }
}