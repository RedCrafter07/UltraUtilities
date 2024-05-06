package redcrafter07.processed.block.tile_entities.capabilities

import net.minecraft.core.NonNullList
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction

open class SimpleInputFluidStore(protected var tanks: NonNullList<FluidStack>, protected var capacity: Int) : IProcessedFluidHandler<CompoundTag> {

    constructor(size: Int, capacity: Int) : this(NonNullList.withSize(size, FluidStack.EMPTY), capacity)
    constructor() : this(NonNullList.of(FluidStack.EMPTY), 1000)

    protected fun validateSlotIndex(slot: Int) {
        if (slot < 0 || slot >= this.tanks.size) {
            throw RuntimeException("Slot " + slot + " not in valid range - [0," + this.tanks.size + ")")
        }
    }

    override fun getTanks(): Int {
        return tanks.size
    }

    override fun getFluidInTank(slot: Int): FluidStack {
        validateSlotIndex(slot)
        return tanks[slot]
    }

    override fun getTankCapacity(slot: Int): Int {
        validateSlotIndex(slot)
        return capacity
    }

    override fun isFluidValid(slot: Int, stack: FluidStack): Boolean {
        return true
    }

    override fun fill(stack: FluidStack, action: FluidAction): Int {
        if (stack.isEmpty || stack.amount < 1) return stack.amount
        var fluidLeft = stack.amount
        for (slot in 0..<tanks.size) {
            if (tanks[slot].amount >= capacity) continue
            if (tanks[slot].isEmpty || tanks[slot].isFluidEqual(stack)) {
                val amount = capacity.coerceAtMost(tanks[slot].amount + fluidLeft)
                fluidLeft -= amount - tanks[slot].amount

                if (action.execute()) {
                    tanks[slot] = stack.copyWithAmount(amount)
                    onContentsChanged(slot)
                }
            }

            if (fluidLeft < 1) break
        }

        return fluidLeft
    }

    override fun drain(stack: FluidStack, action: FluidAction): FluidStack {
        var fluid = FluidStack.EMPTY
        var amountLeft = stack.amount

        for (slot in 0..<tanks.size) {
            if (tanks[slot].isEmpty || tanks[slot].amount < 1 || !tanks[slot].isFluidEqual(stack)) continue

            if (tanks[slot].amount <= amountLeft) {
                if (fluid.isEmpty) fluid = stack.copyWithAmount(tanks[slot].amount)
                else fluid.amount += tanks[slot].amount
                amountLeft -= tanks[slot].amount

                if (action.execute()) {
                    tanks[slot] = FluidStack.EMPTY
                    onContentsChanged(slot)
                }

                if (amountLeft < 1) break
            } else {
                if (fluid.isEmpty) fluid = stack.copyWithAmount(amountLeft)
                else fluid.amount += amountLeft

                if (action.execute()) {
                    tanks[slot].amount -= amountLeft
                    onContentsChanged(slot)
                }

                break
            }
        }

        return fluid
    }

    override fun drain(maxDrain: Int, action: FluidAction): FluidStack {
        var fluid = FluidStack.EMPTY
        var amountLeft = maxDrain

        for (slot in 0..<tanks.size) {
            if (tanks[slot].isEmpty || tanks[slot].amount < 1) continue

            if (tanks[slot].amount <= amountLeft) {
                if (fluid.isEmpty) fluid = tanks[slot].copyWithAmount(tanks[slot].amount)
                else fluid.amount += tanks[slot].amount
                amountLeft -= tanks[slot].amount

                if (action.execute()) {
                    tanks[slot] = FluidStack.EMPTY
                    onContentsChanged(slot)
                }

                if (amountLeft < 1) break
            } else {
                if (fluid.isEmpty) fluid = tanks[slot].copyWithAmount(amountLeft)
                else fluid.amount += amountLeft

                if (action.execute()) {
                    tanks[slot].amount -= amountLeft
                    onContentsChanged(slot)
                }

                break
            }
        }

        return fluid
    }

    open fun drain(maxDrain: Int, slot: Int, action: FluidAction): FluidStack {
        validateSlotIndex(slot)
        if (tanks[slot].isEmpty || tanks[slot].amount < 1) return FluidStack.EMPTY

        if (tanks[slot].amount <= maxDrain) {
            val fluid = tanks[slot]
            if (action.execute()) {
                tanks[slot] = FluidStack.EMPTY
                onContentsChanged(slot)
            }
            return fluid.copy()
        } else {
            if (action.execute()) {
                tanks[slot].amount -= maxDrain
                onContentsChanged(slot)
            }
            return tanks[slot].copyWithAmount(maxDrain)
        }
    }

    override fun setStackInTank(tank: Int, stack: FluidStack) {
        validateSlotIndex(tank)
        tanks[tank] = stack
        onContentsChanged(tank)
    }

    override fun getStackInTank(tank: Int): FluidStack {
        validateSlotIndex(tank)
        return tanks[tank]
    }

    override fun serializeNBT(): CompoundTag {
        val nbtListTag = ListTag()

        for (i in 0..<tanks.size) {
            if (!tanks[i].isEmpty && tanks[i].amount > 0) {
                val tag = CompoundTag()
                tag.putInt("Slot", i)
                tanks[i].writeToNBT(tag)
                nbtListTag.add(tag)
            }
        }

        val nbt = CompoundTag()
        nbt.put("Fluids", nbtListTag)
        nbt.putInt("Size", tanks.size)
        return nbt
    }

    override fun deserializeNBT(nbt: CompoundTag) {
        setSize(if (nbt.contains("Size", 3)) nbt.getInt("Size") else tanks.size)
        val tagList = nbt.getList("Fluids", 10)

        for (i in 0..<tagList.size) {
            val tag = tagList.getCompound(i)
            val slot = tag.getInt("Slot")
            if (slot >= 0 && slot < tanks.size) tanks[slot] = FluidStack.loadFluidStackFromNBT(tag)
        }

        onLoad()
    }

    open fun setSize(size: Int) {
        tanks = NonNullList.withSize(size, FluidStack.EMPTY)
    }

    protected open fun onLoad() {
    }

    protected open fun onContentsChanged(slot: Int) {
    }
}