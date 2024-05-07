package redcrafter07.processed.block.tile_entities.capabilities

import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.items.IItemHandlerModifiable
import redcrafter07.processed.block.tile_entities.IoState
import redcrafter07.processed.block.tile_entities.ProcessedMachine

class MergedIoItemCapability(private val handlers: ProcessedMachine.CapabilityHandlers) : IItemHandlerModifiable {

    override fun getSlots(): Int {
        return (handlers.getItemHandlerForState(IoState.Input)?.slots ?: 0) +
                (handlers.getItemHandlerForState(IoState.Output)?.slots ?: 0)
    }

    override fun getStackInSlot(slot: Int): ItemStack {
        val handler = handlers.getItemHandlerForState(IoState.Output) ?: return ItemStack.EMPTY
        if (slot >= handler.slots) return ItemStack.EMPTY
        return handler.getStackInSlot(slot)
    }

    override fun setStackInSlot(slot: Int, stack: ItemStack) {
        val handler = handlers.getItemHandlerForState(IoState.Input)
        if (handler !is IItemHandlerModifiable) return
        if (slot >= handler.slots) return
        return handler.setStackInSlot(slot, stack)
    }

    override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack {
        val handler = handlers.getItemHandlerForState(IoState.Input) ?: return stack
        if (slot >= handler.slots) return stack
        return handler.insertItem(slot, stack, simulate)
    }

    override fun extractItem(slot: Int, maxAmount: Int, simulate: Boolean): ItemStack {
        val handler = handlers.getItemHandlerForState(IoState.Output) ?: return ItemStack.EMPTY
        if (slot >= handler.slots) return ItemStack.EMPTY
        return handler.extractItem(
            slot,
            maxAmount,
            simulate
        )
    }

    override fun getSlotLimit(slot: Int): Int {
        val handlerInput = handlers.getItemHandlerForState(IoState.Input)
        val handlerOutput = handlers.getItemHandlerForState(IoState.Output)

        val slotLimitInput = if (handlerInput != null && slot < handlerInput.slots) handlerInput.getSlotLimit(slot) else 0
        val slotLimitOutput = if (handlerOutput != null && slot < handlerOutput.slots) handlerOutput.getSlotLimit(slot) else 0
        return slotLimitInput.coerceAtLeast(slotLimitOutput)
    }

    override fun isItemValid(slot: Int, stack: ItemStack): Boolean {
        val handlerInput = handlers.getItemHandlerForState(IoState.Input)
        val handlerOutput = handlers.getItemHandlerForState(IoState.Output)
        if (handlerInput != null && slot < handlerInput.slots && !handlerInput.isItemValid(slot, stack)) return false
        if (handlerOutput != null && slot < handlerOutput.slots && !handlerOutput.isItemValid(
                slot,
                stack
            )
        ) return false
        return true
    }
}