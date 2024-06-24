package redcrafter07.processed.block.tile_entities.capabilities

import net.minecraft.nbt.Tag
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.common.util.INBTSerializable
import net.neoforged.neoforge.energy.IEnergyStorage
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.capability.IFluidHandler
import net.neoforged.neoforge.items.IItemHandler
import net.neoforged.neoforge.items.IItemHandlerModifiable

interface IProcessedItemHandler<T : Tag> : IItemHandlerModifiable, INBTSerializable<T>
interface IProcessedFluidHandler<T : Tag> : IFluidHandlerModifiable, INBTSerializable<T>
interface IProcessedEnergyHandler<T : Tag> : IEnergyStorageModifiable, INBTSerializable<T>

fun toGenericResourceHandler(value: Any): IGenericResourceHandler<*>? {
    if (value is IItemHandlerModifiable) return GenericItemHandlerModifiable(value)
    if (value is IFluidHandlerModifiable) return GenericFluidHandlerModifiable(value)
    if (value is IEnergyStorageModifiable) return GenericEnergyHandlerModifiable(value)

    if (value is IItemHandler) return GenericItemHandler(value)
    if (value is IFluidHandler) return GenericFluidHandler(value)
    if (value is IEnergyStorage) return GenericEnergyHandler(value)

    return null
}

interface IGenericResourceHandler<T> {
    fun getSlots(): Int

    /**
     * @return Returns null if the slot doesn't exist or there's nothing in the slot
     */
    fun getInSlot(slot: Int): T?

    /**
     * @return Returns the amount that specific resource is
     */
    fun getAmount(value: T): Int

    fun isEmpty(value: T): Boolean

    fun ensureNotEmpty(value: T?): T? {
        if (value == null) return null
        if (getAmount(value) < 1) return null
        if (isEmpty(value)) return null
        return value
    }

    /**
     * Tries to insert the resource
     *
     * @return Returns the remaining resource after the insert
     **/
    fun insertResource(slot: Int, value: T, simulate: Boolean): T?

    /**
     * @return Returns the item you extracted (or nothing if it couldn't extract from the slot)
     */
    fun extractResource(slot: Int, amount: Int, simulate: Boolean): T?

    fun getSlotLimit(slot: Int): Int
    fun isValid(slot: Int, value: T): Boolean
}

interface IGenericResourceHandlerModifiable<T> : IGenericResourceHandler<T> {
    fun setInSlot(slot: Int, value: T)
}

open class GenericItemHandler(protected val itemHandler: IItemHandler) : IGenericResourceHandler<ItemStack> {
    override fun isEmpty(value: ItemStack): Boolean {
        return value.isEmpty
    }

    override fun getAmount(value: ItemStack): Int {
        return value.count
    }

    override fun getSlots(): Int {
        return itemHandler.slots
    }

    override fun getInSlot(slot: Int): ItemStack? {
        return ensureNotEmpty(itemHandler.getStackInSlot(slot))
    }

    override fun getSlotLimit(slot: Int): Int {
        return itemHandler.getSlotLimit(slot)
    }

    override fun isValid(slot: Int, value: ItemStack): Boolean {
        return itemHandler.isItemValid(slot, value)
    }

    override fun extractResource(slot: Int, amount: Int, simulate: Boolean): ItemStack? {
        return ensureNotEmpty(itemHandler.extractItem(slot, amount, simulate))
    }

    override fun insertResource(slot: Int, value: ItemStack, simulate: Boolean): ItemStack? {
        return ensureNotEmpty(itemHandler.insertItem(slot, value, simulate))
    }
}

open class GenericFluidHandler(protected val fluidHandler: IFluidHandler) : IGenericResourceHandler<FluidStack> {
    override fun isEmpty(value: FluidStack): Boolean {
        return value.isEmpty
    }

    override fun getAmount(value: FluidStack): Int {
        return value.amount
    }

    override fun getSlots(): Int {
        return fluidHandler.tanks
    }

    override fun getInSlot(slot: Int): FluidStack? {
        return ensureNotEmpty(fluidHandler.getFluidInTank(slot))
    }

    override fun getSlotLimit(slot: Int): Int {
        return fluidHandler.getTankCapacity(slot)
    }

    override fun isValid(slot: Int, value: FluidStack): Boolean {
        return fluidHandler.isFluidValid(slot, value)
    }

    override fun extractResource(slot: Int, amount: Int, simulate: Boolean): FluidStack? {
        return ensureNotEmpty(
            fluidHandler.drain(
                amount,
                if (simulate) IFluidHandler.FluidAction.SIMULATE else IFluidHandler.FluidAction.EXECUTE
            )
        )
    }

    override fun insertResource(slot: Int, value: FluidStack, simulate: Boolean): FluidStack? {
        if (value.isEmpty) return null

        val amountLeft = value.amount - fluidHandler.fill(
            value,
            if (simulate) IFluidHandler.FluidAction.SIMULATE else IFluidHandler.FluidAction.EXECUTE
        )
        if (amountLeft < 1) return null
        return value.copyWithAmount(amountLeft)
    }
}

open class GenericEnergyHandler(protected val energyHandler: IEnergyStorage) : IGenericResourceHandler<Int> {
    override fun isEmpty(value: Int): Boolean {
        return value < 1
    }

    override fun getAmount(value: Int): Int {
        return value
    }

    override fun isValid(slot: Int, value: Int): Boolean {
        return true
    }

    override fun getSlots(): Int {
        return 1
    }

    override fun getSlotLimit(slot: Int): Int {
        if (slot != 1) return 0
        return energyHandler.maxEnergyStored
    }

    override fun getInSlot(slot: Int): Int? {
        if (slot != 1) return null
        if (energyHandler.energyStored < 1) return null
        return energyHandler.energyStored
    }

    override fun extractResource(slot: Int, amount: Int, simulate: Boolean): Int? {
        if (slot != 1) return null
        return energyHandler.extractEnergy(amount, simulate)
    }

    override fun insertResource(slot: Int, value: Int, simulate: Boolean): Int? {
        if (slot != 1) return null
        return energyHandler.receiveEnergy(value, simulate)
    }
}

open class GenericItemHandlerModifiable(itemHandler: IItemHandlerModifiable) : GenericItemHandler(itemHandler),
    IGenericResourceHandlerModifiable<ItemStack> {
    override fun isEmpty(value: ItemStack): Boolean {
        return value.isEmpty
    }

    override fun getAmount(value: ItemStack): Int {
        return value.count
    }

    override fun getSlots(): Int {
        return itemHandler.slots
    }

    override fun getInSlot(slot: Int): ItemStack? {
        return ensureNotEmpty(itemHandler.getStackInSlot(slot))
    }

    override fun setInSlot(slot: Int, value: ItemStack) {
        if (itemHandler is IItemHandlerModifiable) return itemHandler.setStackInSlot(slot, value)
    }

    override fun getSlotLimit(slot: Int): Int {
        return itemHandler.getSlotLimit(slot)
    }

    override fun isValid(slot: Int, value: ItemStack): Boolean {
        return itemHandler.isItemValid(slot, value)
    }

    override fun extractResource(slot: Int, amount: Int, simulate: Boolean): ItemStack? {
        return ensureNotEmpty(itemHandler.extractItem(slot, amount, simulate))
    }

    override fun insertResource(slot: Int, value: ItemStack, simulate: Boolean): ItemStack? {
        return ensureNotEmpty(itemHandler.insertItem(slot, value, simulate))
    }
}

open class GenericFluidHandlerModifiable(fluidHandler: IFluidHandlerModifiable) : GenericFluidHandler(fluidHandler),
    IGenericResourceHandlerModifiable<FluidStack> {
    override fun isEmpty(value: FluidStack): Boolean {
        return value.isEmpty
    }

    override fun getAmount(value: FluidStack): Int {
        return value.amount
    }

    override fun getSlots(): Int {
        return fluidHandler.tanks
    }

    override fun getInSlot(slot: Int): FluidStack? {
        return ensureNotEmpty(fluidHandler.getFluidInTank(slot))
    }

    override fun setInSlot(slot: Int, value: FluidStack) {
        if (fluidHandler is IFluidHandlerModifiable) return fluidHandler.setStackInTank(slot, value)
    }

    override fun getSlotLimit(slot: Int): Int {
        return fluidHandler.getTankCapacity(slot)
    }

    override fun isValid(slot: Int, value: FluidStack): Boolean {
        return fluidHandler.isFluidValid(slot, value)
    }

    override fun extractResource(slot: Int, amount: Int, simulate: Boolean): FluidStack? {
        return ensureNotEmpty(
            fluidHandler.drain(
                amount,
                if (simulate) IFluidHandler.FluidAction.SIMULATE else IFluidHandler.FluidAction.EXECUTE
            )
        )
    }

    override fun insertResource(slot: Int, value: FluidStack, simulate: Boolean): FluidStack? {
        if (value.isEmpty) return null

        val amountLeft = value.amount - fluidHandler.fill(
            value,
            if (simulate) IFluidHandler.FluidAction.SIMULATE else IFluidHandler.FluidAction.EXECUTE
        )
        if (amountLeft < 1) return null
        return value.copyWithAmount(amountLeft)
    }
}

open class GenericEnergyHandlerModifiable(energyHandler: IEnergyStorageModifiable) : GenericEnergyHandler(energyHandler),
    IGenericResourceHandlerModifiable<Int> {
    override fun isEmpty(value: Int): Boolean {
        return value < 1
    }

    override fun getAmount(value: Int): Int {
        return value
    }

    override fun isValid(slot: Int, value: Int): Boolean {
        return true
    }

    override fun getSlots(): Int {
        return 1
    }

    override fun getSlotLimit(slot: Int): Int {
        if (slot != 1) return 0
        return energyHandler.maxEnergyStored
    }

    override fun getInSlot(slot: Int): Int? {
        if (slot != 1) return null
        if (energyHandler.energyStored < 1) return null
        return energyHandler.energyStored
    }

    override fun setInSlot(slot: Int, value: Int) {
        if (slot != 1) return
        if (energyHandler is IEnergyStorageModifiable) energyHandler.energyStored = value
    }

    override fun extractResource(slot: Int, amount: Int, simulate: Boolean): Int? {
        if (slot != 1) return null
        return energyHandler.extractEnergy(amount, simulate)
    }

    override fun insertResource(slot: Int, value: Int, simulate: Boolean): Int? {
        if (slot != 1) return null
        return energyHandler.receiveEnergy(value, simulate)
    }
}
