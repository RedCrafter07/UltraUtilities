package redcrafter07.processed.block.tile_entities.capabilities

import net.minecraft.core.HolderLookup
import net.minecraft.core.NonNullList
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.Tag
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import kotlin.math.min

open class ProcessedItemStackHandler(protected var items: NonNullList<ItemStack>) :
    IProcessedItemHandler<CompoundTag>(
    ) {
    constructor(size: Int) : this(NonNullList.withSize(size, ItemStack.EMPTY))
    constructor() : this(1)

    override fun getSlots(): Int {
        return items.size
    }

    override fun getStackInSlot(slot: Int): ItemStack {
        if (slot >= items.size) return ItemStack.EMPTY
        return items[slot]
    }

    override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack {
        if (stack.isEmpty) return ItemStack.EMPTY
        if (!isItemValid(slot, stack)) return stack
        if (invalidSlotIndex(slot)) return ItemStack.EMPTY
        val existing = items[slot]
        var limit = getStackLimit(slot, stack)
        if (!existing.isEmpty) {
            if (!ItemStack.isSameItemSameComponents(stack, existing))
                return stack

            limit -= existing.count
        }

        if (limit <= 0) return stack
        val reachedLimit = stack.count > limit

        if (!simulate) {
            if (existing.isEmpty) {
                items.set(slot, if (reachedLimit) stack.copyWithCount(limit) else stack)
            } else {
                existing.grow(if (reachedLimit) limit else stack.count)
            }
            setChanged(slot)
        }

        return if (reachedLimit) stack.copyWithCount(stack.count - limit) else ItemStack.EMPTY
    }

    override fun extractItem(slot: Int, amount: Int, simulate: Boolean): ItemStack {
        if (amount <= 0) return ItemStack.EMPTY
        if (invalidSlotIndex(slot)) return ItemStack.EMPTY
        val existing = items[slot]
        if (existing.isEmpty) return ItemStack.EMPTY
        val toExtract = min(amount.toDouble(), existing.maxStackSize.toDouble()).toInt()
        if (existing.count <= toExtract) {
            if (!simulate) {
                items[slot] = ItemStack.EMPTY
                setChanged(slot)
                return existing
            } else {
                return existing.copy()
            }
        } else {
            if (!simulate) {
                items[slot] = existing.copyWithCount(existing.count - toExtract)
                setChanged(slot)
            }

            return existing.copyWithCount(toExtract)
        }
    }

    override fun getSlotLimit(slot: Int): Int {
        if (invalidSlotIndex(slot)) return 0
        return Item.ABSOLUTE_MAX_STACK_SIZE
    }

    override fun isItemValid(slot: Int, stack: ItemStack): Boolean {
        return true
    }

    override fun setStackInSlot(slot: Int, stack: ItemStack) {
        if (invalidSlotIndex(slot)) return
        items[slot] = stack
        setChanged(slot)
    }

    override fun serializeNBT(provider: HolderLookup.Provider): CompoundTag {
        val nbtTagList = ListTag()
        for (i in items.indices) {
            if (items[i].isEmpty) continue
            val itemTag = CompoundTag()
            itemTag.putInt("Slot", i)
            nbtTagList.add(items[i].save(provider, itemTag))
        }
        val nbt = CompoundTag()
        nbt.put("Items", nbtTagList)
        nbt.putInt("Size", items.size)
        return nbt
    }

    override fun deserializeNBT(provider: HolderLookup.Provider, nbt: CompoundTag) {
        setSize(if (nbt.contains("Size", Tag.TAG_INT.toInt())) nbt.getInt("Size") else items.size)
        val tagList = nbt.getList("Items", Tag.TAG_COMPOUND.toInt())
        for (i in tagList.indices) {
            val itemTags = tagList.getCompound(i)
            val slot = itemTags.getInt("Slot")

            if (slot >= 0 && slot < items.size) {
                ItemStack.parse(provider, itemTags).ifPresent { stack: ItemStack? ->
                    items[slot] = stack ?: ItemStack.EMPTY
                }
            }
        }
    }

    open fun setSize(newSize: Int) {
        items = NonNullList.withSize(newSize, ItemStack.EMPTY)
        setChanged(-1)
    }

    protected fun invalidSlotIndex(slot: Int): Boolean {
        return (slot < 0 || slot >= items.size)
    }

    protected fun getStackLimit(slot: Int, stack: ItemStack): Int {
        return min(getSlotLimit(slot).toDouble(), stack.maxStackSize.toDouble()).toInt()
    }
}