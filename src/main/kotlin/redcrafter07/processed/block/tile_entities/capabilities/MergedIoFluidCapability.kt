package redcrafter07.processed.block.tile_entities.capabilities

import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.capability.IFluidHandler
import redcrafter07.processed.block.tile_entities.IoState
import redcrafter07.processed.block.tile_entities.ProcessedMachine

class MergedIoFluidCapability(private val handlers: ProcessedMachine.CapabilityHandlers) : IFluidHandlerModifiable {
    override fun setStackInTank(tank: Int, stack: FluidStack) {
        val handler = handlers.getFluidHandlerForState(IoState.Input) ?: return
        if (tank >= handler.tanks) return
        return handler.setStackInTank(tank, stack)
    }

    override fun getStackInTank(tank: Int): FluidStack {
        val handler = handlers.getFluidHandlerForState(IoState.Output) ?: return FluidStack.EMPTY
        if (tank >= handler.tanks) return FluidStack.EMPTY
        return handler.getStackInTank(tank)
    }

    override fun getTanks(): Int {
        return (handlers.getFluidHandlerForState(IoState.Input)?.tanks ?: 0) +
                (handlers.getFluidHandlerForState(IoState.Output)?.tanks ?: 0)
    }

    override fun getFluidInTank(tank: Int): FluidStack {
        val handler = handlers.getFluidHandlerForState(IoState.Output) ?: return FluidStack.EMPTY
        if (tank >= handler.tanks) return FluidStack.EMPTY
        return handler.getFluidInTank(tank)
    }

    override fun getTankCapacity(tank: Int): Int {
        val handlerInput = handlers.getFluidHandlerForState(IoState.Input)
        val handlerOutput = handlers.getFluidHandlerForState(IoState.Output)

        val tankCapacityInput =
            if (handlerInput != null && tank < handlerInput.tanks) handlerInput.getTankCapacity(tank) else 0
        val tankCapacityOutput =
            if (handlerOutput != null && tank < handlerOutput.tanks) handlerOutput.getTankCapacity(tank) else 0
        return tankCapacityInput.coerceAtLeast(tankCapacityOutput)
    }

    override fun isFluidValid(tank: Int, stack: FluidStack): Boolean {
        val handlerInput = handlers.getFluidHandlerForState(IoState.Input)
        val handlerOutput = handlers.getFluidHandlerForState(IoState.Output)
        if (handlerInput != null && tank < handlerInput.tanks && !handlerInput.isFluidValid(tank, stack)) return false
        if (handlerOutput != null && tank < handlerOutput.tanks && !handlerOutput.isFluidValid(
                tank,
                stack
            )
        ) return false
        return true
    }

    override fun fill(stack: FluidStack, action: IFluidHandler.FluidAction): Int {
        return (handlers.getFluidHandlerForState(IoState.Input) ?: return stack.amount).fill(stack, action)
    }

    override fun drain(stack: FluidStack, action: IFluidHandler.FluidAction): FluidStack {
        return (handlers.getFluidHandlerForState(IoState.Output) ?: return FluidStack.EMPTY).drain(stack, action)
    }

    override fun drain(maxDrain: Int, action: IFluidHandler.FluidAction): FluidStack {
        return (handlers.getFluidHandlerForState(IoState.Output) ?: return FluidStack.EMPTY).drain(maxDrain, action)
    }
}