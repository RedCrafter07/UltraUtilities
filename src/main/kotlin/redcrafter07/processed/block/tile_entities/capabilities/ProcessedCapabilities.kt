package redcrafter07.processed.block.tile_entities.capabilities

import net.minecraft.nbt.Tag
import net.neoforged.neoforge.common.util.INBTSerializable
import net.neoforged.neoforge.items.IItemHandlerModifiable

abstract class IProcessedItemHandler<T : Tag> :
    IItemHandlerModifiable,
    INBTSerializable<T> {
    private var onChangeHandler: IOnChangeHandler? = null

    fun setOnChange(newOnChangeHandler: IOnChangeHandler?) {
        onChangeHandler = newOnChangeHandler
    }

    open fun setChanged(slot: Int) {
        onChangeHandler?.onChange()
    }
}

abstract class IProcessedFluidHandler<T : Tag> :
    IFluidHandlerModifiable,
    INBTSerializable<T> {
    private var onChangeHandler: IOnChangeHandler? = null

    fun setOnChange(newOnChangeHandler: IOnChangeHandler?) {
        onChangeHandler = newOnChangeHandler
    }

    open fun setChanged(slot: Int) {
        onChangeHandler?.onChange()
    }
}

abstract class IProcessedEnergyHandler<T : Tag> :
    IEnergyStorageModifiable,
    INBTSerializable<T> {
    private var onChangeHandler: IOnChangeHandler? = null

    fun setOnChange(newOnChangeHandler: IOnChangeHandler?) {
        onChangeHandler = newOnChangeHandler
    }

    open fun setChanged() {
        onChangeHandler?.onChange()
    }
}

fun interface IOnChangeHandler {
    fun onChange()
}