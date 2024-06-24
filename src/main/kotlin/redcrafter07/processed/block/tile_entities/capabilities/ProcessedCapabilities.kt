package redcrafter07.processed.block.tile_entities.capabilities

import net.minecraft.nbt.Tag
import net.neoforged.neoforge.common.util.INBTSerializable
import net.neoforged.neoforge.items.IItemHandlerModifiable

interface IProcessedItemHandler<T : Tag> : IItemHandlerModifiable, INBTSerializable<T>
interface IProcessedFluidHandler<T : Tag> : IFluidHandlerModifiable, INBTSerializable<T>
interface IProcessedEnergyHandler<T : Tag> : IEnergyStorageModifiable, INBTSerializable<T>
