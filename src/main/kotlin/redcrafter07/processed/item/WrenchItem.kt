package redcrafter07.processed.item

import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.Level
import redcrafter07.processed.block.WrenchInteractableBlock

class WrenchItem : ModItem(Properties().stacksTo(1), "wrench") {
    private fun getMode(stack: ItemStack): WrenchMode {
        val nbt = stack.orCreateTag
        return WrenchMode.Config.load(nbt.getShort("mode").toUShort())
    }

    override fun getDefaultInstance(): ItemStack {
        val stack = super.getDefaultInstance()
        val nbt = stack.orCreateTag

        nbt.putShort("mode", WrenchMode.Config.save().toShort())

        return stack
    }

    override fun onItemUseFirst(stack: ItemStack, context: UseOnContext): InteractionResult {
        val wrenchMode = getMode(stack)

        return when (wrenchMode) {
            WrenchMode.Config -> configure(stack, context)
            else -> InteractionResult.PASS
        }
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
    }

    override fun use(level: Level, player: Player, hand: InteractionHand): InteractionResultHolder<ItemStack> {
        val stack = player.getItemInHand(hand)
        val mode = getMode(stack).next()
        val nbt = stack.orCreateTag
        nbt.putShort("mode", mode.save().toShort())

        return InteractionResultHolder.success(stack)
    }

    override fun getAdditionalTooltip(stack: ItemStack, world: Level?): MutableComponent {
        return Component.translatable("item.processed.wrench.mode", Component.translatable(getMode(stack).translation()))
    }

    override fun getName(stack: ItemStack): Component {
        return Component.translatable("item.processed.wrench.name", Component.translatable(getMode(stack).translation()))
    }
}