package redcrafter07.processed.item

import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.world.InteractionResult
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.block.DirectionalBlock
import net.minecraft.world.level.block.HorizontalDirectionalBlock
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import redcrafter07.processed.block.WrenchInteractableBlock
import redcrafter07.processed.item.DataComponents.WRENCH_MODE

class WrenchItem : ModItem(Properties().stacksTo(1), "wrench") {
    companion object {
        fun getMode(stack: ItemStack): WrenchMode {
            return stack.getOrDefault(WRENCH_MODE, WrenchMode.Rotate)
        }

        fun setMode(stack: ItemStack, mode: WrenchMode): ItemStack {
            stack.set(WRENCH_MODE, mode)
            return stack
        }

        private val facingProperties =
            listOf(
                DirectionalBlock.FACING,
                HorizontalDirectionalBlock.FACING,
                BlockStateProperties.HORIZONTAL_FACING,
                BlockStateProperties.FACING
            )
    }

    override fun getDefaultInstance(): ItemStack {
        return setMode(super.getDefaultInstance(), WrenchMode.Config)
    }

    override fun useOn(context: UseOnContext): InteractionResult {
        val stack = context.player?.getItemInHand(context.hand) ?: return InteractionResult.PASS
        val wrenchMode = getMode(stack)

        return when (wrenchMode) {
            WrenchMode.Config -> configure(stack, context)
            WrenchMode.Rotate -> rotate(stack, context)
        }
    }

    private fun rotate(stack: ItemStack, context: UseOnContext): InteractionResult {
        val blockState = context.level.getBlockState(context.clickedPos) ?: return super.onItemUseFirst(stack, context)

        for (property in facingProperties) {
            if (blockState.hasProperty(property)) {
                val facing = blockState.getValue(property)
                val newFacing =
                    if (context.player?.isShiftKeyDown == true) facing.counterClockWise else facing.clockWise
                val newState = blockState.setValue(property, newFacing)
                context.level.setBlock(context.clickedPos, newState, 3)
                return InteractionResult.SUCCESS
            }
        }

        return InteractionResult.PASS
    }

    private fun configure(stack: ItemStack, context: UseOnContext): InteractionResult {
        val blockState = context.level.getBlockState(context.clickedPos) ?: return super.onItemUseFirst(stack, context)
        val block = blockState.block

        if (block is WrenchInteractableBlock) {
            block.onWrenchUse(context, blockState)
            return InteractionResult.SUCCESS
        }
        val blockEntity =
            context.level.getBlockEntity(context.clickedPos) ?: return super.onItemUseFirst(stack, context)

        if (blockEntity is WrenchInteractableBlock) {
            blockEntity.onWrenchUse(context, blockState)
            return InteractionResult.SUCCESS
        }

        return InteractionResult.PASS
    }

    override fun getAdditionalTooltip(stack: ItemStack, context: TooltipContext, flag: TooltipFlag): MutableComponent? {
        return Component.translatable(
            "item.processed.wrench.mode", Component.translatable(getMode(stack).translation())
        )
    }
}