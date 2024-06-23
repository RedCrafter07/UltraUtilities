package redcrafter07.processed.block.tile_entities

import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.HolderLookup.Provider
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.util.ByIdMap
import net.minecraft.util.StringRepresentable
import net.minecraft.world.Containers
import net.minecraft.world.SimpleContainer
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.common.util.INBTSerializable
import net.neoforged.neoforge.energy.IEnergyStorage
import net.neoforged.neoforge.fluids.capability.IFluidHandler
import net.neoforged.neoforge.items.IItemHandler
import net.neoforged.neoforge.items.IItemHandlerModifiable
import net.neoforged.neoforge.items.wrapper.EmptyItemHandler
import org.joml.Vector2i
import redcrafter07.processed.ProcessedMod
import redcrafter07.processed.block.ProcessedTier
import redcrafter07.processed.block.ProcessedTiers
import redcrafter07.processed.block.WrenchInteractableBlock
import redcrafter07.processed.block.tile_entities.capabilities.*
import redcrafter07.processed.gui.ConfigScreen

enum class IoState(private val id: Int, private val stateName: String) : StringRepresentable {
    None(0, "none"),
    Input(1, "input"),
    Output(2, "output"),
    InputOutput(3, "input_output"),
    Additional(4, "additional"),
    Auxiliary(5, "auxiliary");


    companion object {
        val BY_ID =
            ByIdMap.continuous(IoState::getId, IoState.entries.toTypedArray(), ByIdMap.OutOfBoundsStrategy.WRAP)

        val CODEC = StringRepresentable.fromEnum(IoState::values)
        val STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, IoState::getId)
    }

    override fun getSerializedName(): String {
        return stateName
    }

    fun getId(): Int {
        return id
    }

    fun next(): IoState {
        return when (this) {
            Auxiliary -> None
            None -> Input
            Input -> Output
            Output -> InputOutput
            InputOutput -> Additional
            Additional -> Auxiliary
        }
    }

    fun previous(): IoState {
        return when (this) {
            Input -> None
            Output -> Input
            InputOutput -> Output
            Additional -> InputOutput
            Auxiliary -> Additional
            None -> Auxiliary
        }
    }

    fun toComponent(): Component {
        return Component.translatable("processed.io_state.$stateName")
    }
}

// translation: IoSide <=> Direction
// Down:    Bottom
// Top:     Up
// Front:   North
// Back:    South
// Left:    East
// Right:   West

// bugs:

// Direction translation Matrix. Do LOOKUP[Facing.3dData][Direction.3dData] and you get the "real" direction! (its like Direction + Direction)
val DIRECTION_LOOKUP: List<List<Direction>> = listOf(
    listOf(
        // ---{ Facing: Down }---
        Direction.NORTH, // Bottom: Front (North)
        Direction.SOUTH, // Top: Back (South)
        Direction.UP,    // North: Up (Up)
        Direction.DOWN,  // South: Down (Down)
        Direction.WEST,  // West: Right (West)
        Direction.EAST   // East: Left (East)
    ),
    listOf(
        // ---{ Facing: Up }---
        Direction.SOUTH, // Bottom: Back (South)
        Direction.NORTH, // Top: Front (North)
        Direction.DOWN,  // North: Down (Down)
        Direction.UP,    // South: Up (Up)
        Direction.WEST,  // West: Right (West)
        Direction.EAST   // East: Left (East)
    ),
    listOf(
        // ---{ Facing: North }---
        Direction.DOWN,  // Bottom: Down (Down)
        Direction.UP,    // Top: Top (Up)
        Direction.NORTH, // North: Front (North)
        Direction.SOUTH, // South: Back (South)
        Direction.WEST,  // West: Right (West)
        Direction.EAST   // East: Left (East)
    ),
    listOf(
        // ---{ Facing: South }---
        Direction.DOWN,  // Bottom: Down (Down)
        Direction.UP,    // Top: Up (Up)
        Direction.SOUTH, // North: Back (South)
        Direction.NORTH, // South: Front (North)
        Direction.EAST,  // West: Left (East)
        Direction.WEST,  // East: Right (West)
    ),
    listOf(
        // ---{ Facing: West }---
        Direction.DOWN,  // Bottom: Down (Down)
        Direction.UP,    // Top: Up (Up)
        Direction.EAST,  // North: Left (East)
        Direction.WEST,  // South: Right (West)
        Direction.NORTH, // West: Front (North)
        Direction.SOUTH, // East: Back (South)
    ),
    listOf(
        // ---{ Facing: East }---
        Direction.DOWN,  // Bottom: Down (Down)
        Direction.UP,    // Top: Up (Up)
        Direction.WEST,  // North: Left (West)
        Direction.EAST,  // South: Right (East)
        Direction.SOUTH, // West: Back (South)
        Direction.NORTH, // East: Front (North)
    ),
)

enum class BlockSide(private val id: Int, private val stateName: String) : StringRepresentable {
    Top(0, "top"),
    Bottom(1, "bottom"),
    Left(2, "left"),
    Right(3, "right"),
    Front(4, "front"),
    Back(5, "back");

    companion object {
        fun fromDirection(direction: Direction): BlockSide {
            return when (direction) {
                Direction.UP -> Top
                Direction.DOWN -> Bottom
                Direction.NORTH -> Front
                Direction.SOUTH -> Back
                Direction.WEST -> Right
                Direction.EAST -> Left
            }
        }

        fun getFacing(machineFacing: Direction, direction: Direction): BlockSide {
            return fromDirection(DIRECTION_LOOKUP[machineFacing.get3DDataValue()][direction.get3DDataValue()])
        }

        val DEFAULT = Front

        val BY_ID = ByIdMap.continuous(BlockSide::getId, entries.toTypedArray(), ByIdMap.OutOfBoundsStrategy.WRAP)

        val CODEC = StringRepresentable.fromEnum(BlockSide::values)
        val STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, BlockSide::getId)
    }

    override fun getSerializedName(): String {
        return stateName
    }

    fun getId(): Int {
        return id
    }

    fun getButtonPos(): Vector2i {
        return when (this) {
            Top -> Vector2i(27, 28)
            Bottom -> Vector2i(27, 54)
            Left -> Vector2i(14, 41)
            Front -> Vector2i(27, 41)
            Right -> Vector2i(40, 41)
            Back -> Vector2i(53, 41)
        }
    }

    fun toComponent(): Component {
        return when (this) {
            Top -> Component.translatable("processed.side.top")
            Bottom -> Component.translatable("processed.side.bottom")
            Left -> Component.translatable("processed.side.left")
            Right -> Component.translatable("processed.side.right")
            Front -> Component.translatable("processed.side.front")
            Back -> Component.translatable("processed.side.back")
        }
    }

    override fun toString(): String {
        return when (this) {
            Top -> "Top"
            Bottom -> "Bottom"
            Left -> "Left"
            Right -> "Right"
            Front -> "Front"
            Back -> "Back"
        }
    }
}

interface ItemCapableBlockEntity {
    fun itemCapabilityForSide(side: BlockSide, state: BlockState): IItemHandler?
}

interface EnergyCapableBlockEntity {
    fun energyCapabilityForSide(side: BlockSide, state: BlockState): IEnergyStorage?
}

interface FluidCapableBlockEntity {
    fun fluidCapabilityForSide(side: BlockSide, state: BlockState): IFluidHandler?
}

abstract class ProcessedMachine(blockEntityType: BlockEntityType<*>, blockPos: BlockPos, blockState: BlockState) :
    BlockEntity(
        blockEntityType,
        blockPos, blockState
    ), WrenchInteractableBlock, ItemCapableBlockEntity, EnergyCapableBlockEntity, FluidCapableBlockEntity {
    companion object {
        val EMPTY_ITEM_HANDLER: IItemHandlerModifiable = EmptyItemHandler()
        val EMPTY_FLUID_HANDLER: IFluidHandlerModifiable = EmptyFluidHandler()
        val EMPTY_ENERGY_HANDLER: IEnergyStorageModifiable = EmptyEnergyStorage()
    }

    private var sides: Array<IoState> = arrayOf(
        IoState.None,
        IoState.None,
        IoState.None,
        IoState.None,
        IoState.None,
        IoState.None,
        IoState.None,
        IoState.None,
        IoState.None,
        IoState.None,
        IoState.None,
        IoState.None
    )
    private val capabilityHandlers = CapabilityHandlers()

    fun getSide(itemOrFluid: Boolean, side: BlockSide): IoState {
        return if (itemOrFluid) sides[side.getId()] else sides[side.getId() + 6]
    }

    fun setSide(itemOrFluid: Boolean, side: BlockSide, value: IoState) {
        if (itemOrFluid) sides[side.getId()] = value
        else sides[side.getId() + 6] = value
        this.setChanged()
    }

    override fun loadAdditional(nbt: CompoundTag, provider: Provider) {
        super.loadAdditional(nbt, provider)

        val byteArray = nbt.getByteArray("io_states")
        for (index in 0..<12) {
            sides[index] = if (byteArray.size <= index) IoState.None else IoState.BY_ID.apply(byteArray[index].toInt())
        }

        capabilityHandlers.deserializeNBT(provider, nbt.getCompound("capability_handlers"))
    }

    override fun saveAdditional(nbt: CompoundTag, provider: Provider) {
        super.saveAdditional(nbt, provider)
        val list = sides.map { it.getId().toByte() }.toList()
        nbt.putByteArray("io_states", list)
        nbt.put("capability_handlers", capabilityHandlers.serializeNBT(provider))
    }

    override fun getUpdatePacket(): Packet<ClientGamePacketListener>? {
        return ClientboundBlockEntityDataPacket.create(this)
    }

    override fun getUpdateTag(provider: Provider): CompoundTag {
        return saveWithoutMetadata(provider)
    }

    override fun onWrenchUse(context: UseOnContext, state: BlockState) {
        val me = context.level.getBlockEntity(context.clickedPos) ?: return
        if (me !is ProcessedMachine) return

        if (context.level.isClientSide) Minecraft.getInstance().setScreen(ConfigScreen(this, context.clickedPos))
    }

    override fun energyCapabilityForSide(side: BlockSide, state: BlockState): IEnergyStorage? {
        return capabilityHandlers.energyStore
    }

    override fun itemCapabilityForSide(side: BlockSide, state: BlockState): IItemHandler? {
        return capabilityHandlers.getItemHandlerForState(getSide(true, side))
    }

    override fun fluidCapabilityForSide(side: BlockSide, state: BlockState): IFluidHandler? {
        return capabilityHandlers.getFluidHandlerForState(getSide(false, side))
    }

    open fun clientTick(level: Level, pos: BlockPos, state: BlockState) {}
    open fun serverTick(level: Level, pos: BlockPos, state: BlockState) {}
    open fun tick(level: Level, pos: BlockPos, state: BlockState) {}

    /// ###########################################
    /// #  E N E R G Y   C A P A B I L I T I E S  #
    /// ###########################################
    protected fun useEnergyCapability() {
        capabilityHandlers.energyStore = SimpleEnergyStore(10000, 100, 0, 0)
        this.invalidateCapabilities()
        this.setChanged()
    }

    protected fun useEnergyCapability(capacity: Int) {
        capabilityHandlers.energyStore = SimpleEnergyStore(capacity, capacity, 0, 0)
        this.invalidateCapabilities()
        this.setChanged()
    }

    protected fun useEnergyCapability(capacity: Int, maxReceive: Int) {
        capabilityHandlers.energyStore = SimpleEnergyStore(capacity, maxReceive, 0, 0)
        this.invalidateCapabilities()
        this.setChanged()
    }

    protected fun useEnergyCapability(capability: IProcessedEnergyHandler<CompoundTag>?) {
        this.capabilityHandlers.energyStore = capability
        this.invalidateCapabilities()
        this.setChanged()
    }

    protected fun stopEnergyCapability() {
        this.capabilityHandlers.energyStore = null
        this.invalidateCapabilities()
        this.setChanged()
    }

    /**
     * Returns the [IEnergyStorage] for the state.
     * If no Capability for that state was registered, it returns an empty [IEnergyStorage]
     *
     * @see EmptyEnergyStorage
     **/
    protected fun getEnergyCapability(): IEnergyStorageModifiable {
        return this.capabilityHandlers.energyStore ?: EMPTY_ENERGY_HANDLER
    }

    protected fun hasEnergyCapability(): Boolean {
        return this.capabilityHandlers.energyStore != null
    }

    val energyHandler get() = getEnergyCapability()

    /// #######################################
    /// #  I T E M   C A P A B I L I T I E S  #
    /// #######################################
    protected fun useItemCapability(state: IoState) {
        if (state == IoState.None) return
        capabilityHandlers.setItemHandlerForState(
            state,
            if (state == IoState.Output) SimpleOutputItemStore(1) else SimpleInputItemStore(1)
        )
        this.invalidateCapabilities()
        this.setChanged()
    }

    protected fun useItemCapability(state: IoState, size: Int) {
        if (state == IoState.None) return
        this.capabilityHandlers.setItemHandlerForState(
            state,
            if (state == IoState.Output) SimpleOutputItemStore(size) else SimpleInputItemStore(size)
        )
        this.invalidateCapabilities()
        this.setChanged()
    }

    protected fun useItemCapability(state: IoState, capability: IProcessedItemHandler<CompoundTag>?) {
        if (state == IoState.None) return
        this.capabilityHandlers.setItemHandlerForState(state, capability)
        this.invalidateCapabilities()
        this.setChanged()
    }

    protected fun stopItemCapability(state: IoState) {
        if (state == IoState.None) return
        this.capabilityHandlers.setItemHandlerForState(state, null)
        this.invalidateCapabilities()
        this.setChanged()
    }

    protected fun stopItemCapability() {
        this.capabilityHandlers.setItemHandlerForState(IoState.Input, null)
        this.capabilityHandlers.setItemHandlerForState(IoState.Output, null)
        this.capabilityHandlers.setItemHandlerForState(IoState.Additional, null)
        this.capabilityHandlers.setItemHandlerForState(IoState.Auxiliary, null)
        this.invalidateCapabilities()
        this.setChanged()
    }


    /**
     * Returns the [IItemHandlerModifiable] for the state.
     * If no Capability for that state was registered, it returns an empty [IItemHandlerModifiable]
     *
     * @see EmptyItemHandler
     **/
    protected fun getItemCapability(state: IoState): IItemHandlerModifiable {
        return this.capabilityHandlers.getItemHandlerForState(state) ?: EMPTY_ITEM_HANDLER
    }

    protected fun hasItemCapability(state: IoState): Boolean {
        return this.capabilityHandlers.getItemHandlerForState(state) != null
    }

    val inputItemHandler
        get() = getItemCapability(IoState.Input)
    val outputItemHandler
        get() = getItemCapability(IoState.Output)
    val inputOutputItemHandler
        get() = getItemCapability(IoState.InputOutput)
    val additionalItemHandler
        get() = getItemCapability(IoState.Additional)
    val auxiliaryItemHandler
        get() = getItemCapability(IoState.Auxiliary)

    private fun dropItemForState(state: IoState) {
        val capability = this.capabilityHandlers.getItemHandlerForState(state) ?: return
        if (capability.slots < 1) return
        val inventory = SimpleContainer(capability.slots)
        for (i in 0..<capability.slots) inventory.setItem(i, capability.getStackInSlot(i))
        level?.let { Containers.dropContents(it, this.worldPosition, inventory) }
    }

    open fun dropItems() {
        dropItemForState(IoState.Input)
        dropItemForState(IoState.Output)
        dropItemForState(IoState.Additional)
        dropItemForState(IoState.Auxiliary)
    }

    /// #########################################
    /// #  F L U I D   C A P A B I L I T I E S  #
    /// #########################################
    protected fun useFluidCapability(state: IoState) {
        if (state == IoState.None) return
        this.capabilityHandlers.setFluidHandlerForState(
            state,
            if (state == IoState.Output) SimpleOutputFluidStore(1, 10000) else SimpleInputFluidStore(1, 10000)
        )
        this.invalidateCapabilities()
        this.setChanged()
    }

    protected fun useFluidCapability(state: IoState, capacity: Int) {
        if (state == IoState.None) return
        this.capabilityHandlers.setFluidHandlerForState(
            state,
            if (state == IoState.Output) SimpleOutputFluidStore(1, capacity) else SimpleInputFluidStore(1, capacity)
        )
        this.invalidateCapabilities()
        this.setChanged()
    }

    protected fun useFluidCapability(state: IoState, slots: Int, capacity: Int) {
        if (state == IoState.None) return
        this.capabilityHandlers.setFluidHandlerForState(
            state,
            (if (state == IoState.Output) SimpleOutputFluidStore(slots, capacity) else SimpleInputFluidStore(
                slots,
                capacity
            ))
        )
        this.invalidateCapabilities()
        this.setChanged()
    }

    protected fun useFluidCapability(state: IoState, capability: IProcessedFluidHandler<CompoundTag>?) {
        if (state == IoState.None) return
        this.capabilityHandlers.setFluidHandlerForState(state, capability)
        this.invalidateCapabilities()
        this.setChanged()
    }

    protected fun stopFluidCapability(state: IoState) {
        if (state == IoState.None) return
        this.capabilityHandlers.setFluidHandlerForState(state, null)
        this.invalidateCapabilities()
        this.setChanged()
    }

    protected fun stopFluidCapability() {
        this.capabilityHandlers.setFluidHandlerForState(IoState.Input, null)
        this.capabilityHandlers.setFluidHandlerForState(IoState.Output, null)
        this.capabilityHandlers.setFluidHandlerForState(IoState.Additional, null)
        this.capabilityHandlers.setFluidHandlerForState(IoState.Auxiliary, null)
        this.invalidateCapabilities()
        this.setChanged()
    }

    /**
     * Returns the [IFluidHandlerModifiable] for the state.
     * If no Capability for that state was registered, it returns an empty [IFluidHandlerModifiable]
     *
     * @see EmptyFluidHandler
     **/
    protected fun getFluidCapability(state: IoState): IFluidHandlerModifiable {
        return capabilityHandlers.getFluidHandlerForState(state) ?: EMPTY_FLUID_HANDLER
    }

    protected fun hasFluidCapability(state: IoState): Boolean {
        return capabilityHandlers.getFluidHandlerForState(state) != null
    }

    val inputFluidHandler
        get() = getFluidCapability(IoState.Input)
    val outputFluidHandler
        get() = getFluidCapability(IoState.Output)
    val inputOutputFluidHandler
        get() = getFluidCapability(IoState.InputOutput)
    val additionalFluidHandler
        get() = getFluidCapability(IoState.Additional)
    val auxiliaryFluidHandler
        get() = getFluidCapability(IoState.Auxiliary)


    class CapabilityHandlers : INBTSerializable<CompoundTag> {
        init {
            ProcessedMod.LOGGER.info("Initializing Capability Handlers")
        }

        var energyStore: IProcessedEnergyHandler<CompoundTag>? = null
        private var inputItemHandler: IProcessedItemHandler<CompoundTag>? = null
        private var outputItemHandler: IProcessedItemHandler<CompoundTag>? = null
        private var additionalItemHandler: IProcessedItemHandler<CompoundTag>? = null
        private var auxiliaryItemHandler: IProcessedItemHandler<CompoundTag>? = null
        private var mergedIoItemCapabilityHandler = MergedIoItemCapability(this)

        private var inputFluidHandler: IProcessedFluidHandler<CompoundTag>? = null
        private var outputFluidHandler: IProcessedFluidHandler<CompoundTag>? = null
        private var additionalFluidHandler: IProcessedFluidHandler<CompoundTag>? = null
        private var auxiliaryFluidHandler: IProcessedFluidHandler<CompoundTag>? = null
        private var mergedIoFluidCapabilityHandler = MergedIoFluidCapability(this)


        fun getItemHandlerForState(state: IoState): IItemHandlerModifiable? {
            return when (state) {
                IoState.None -> null
                IoState.Input -> inputItemHandler
                IoState.Output -> outputItemHandler
                IoState.InputOutput -> if (inputItemHandler == null && outputItemHandler == null) null else mergedIoItemCapabilityHandler
                IoState.Additional -> additionalItemHandler
                IoState.Auxiliary -> auxiliaryItemHandler
            }
        }

        fun setItemHandlerForState(state: IoState, handler: IProcessedItemHandler<CompoundTag>?) {
            when (state) {
                IoState.None -> {}
                IoState.Input -> inputItemHandler = handler
                IoState.Output -> outputItemHandler = handler
                IoState.InputOutput -> {}
                IoState.Additional -> additionalItemHandler = handler
                IoState.Auxiliary -> auxiliaryItemHandler = handler
            }
            ProcessedMod.LOGGER.info("Setting item handler $state to $handler")
            ProcessedMod.LOGGER.info("output: {}", inputItemHandler)
            ProcessedMod.LOGGER.info("input: {}", outputItemHandler)
            ProcessedMod.LOGGER.info("me: {}", this)
        }


        fun getFluidHandlerForState(state: IoState): IFluidHandlerModifiable? {
            return when (state) {
                IoState.None -> null
                IoState.Input -> inputFluidHandler
                IoState.Output -> outputFluidHandler
                IoState.InputOutput -> if (inputFluidHandler == null && outputFluidHandler == null) null else mergedIoFluidCapabilityHandler
                IoState.Additional -> additionalFluidHandler
                IoState.Auxiliary -> auxiliaryFluidHandler
            }
        }

        fun setFluidHandlerForState(state: IoState, handler: IProcessedFluidHandler<CompoundTag>?) {
            when (state) {
                IoState.None -> {}
                IoState.Input -> inputFluidHandler = handler
                IoState.Output -> outputFluidHandler = handler
                IoState.InputOutput -> {}
                IoState.Additional -> additionalFluidHandler = handler
                IoState.Auxiliary -> auxiliaryFluidHandler = handler
            }
        }

        override fun serializeNBT(provider: Provider): CompoundTag {
            val tag = CompoundTag()

            // energy
            val energyStoreNbt = energyStore?.serializeNBT(provider)

            if (energyStoreNbt != null) tag.put("energyStore", energyStoreNbt)


            // items
            val inputItemNbt = inputItemHandler?.serializeNBT(provider)
            val outputItemNbt = outputItemHandler?.serializeNBT(provider)
            val additionalItemNbt = additionalItemHandler?.serializeNBT(provider)
            val auxiliaryItemNbt = auxiliaryItemHandler?.serializeNBT(provider)

            if (inputItemNbt != null) tag.put("inputItemNbt", inputItemNbt)
            if (outputItemNbt != null) tag.put("outputItemNbt", outputItemNbt)
            if (additionalItemNbt != null) tag.put("additionalItemNbt", additionalItemNbt)
            if (auxiliaryItemNbt != null) tag.put("auxiliaryItemNbt", auxiliaryItemNbt)


            // fluids
            val inputFluidNbt = inputFluidHandler?.serializeNBT(provider)
            val outputFluidNbt = outputFluidHandler?.serializeNBT(provider)
            val additionalFluidNbt = additionalFluidHandler?.serializeNBT(provider)
            val auxiliaryFluidNbt = auxiliaryFluidHandler?.serializeNBT(provider)

            if (inputFluidNbt != null) tag.put("inputFluidNbt", inputFluidNbt)
            if (outputFluidNbt != null) tag.put("outputFluidNbt", outputFluidNbt)
            if (additionalFluidNbt != null) tag.put("additionalFluidNbt", additionalFluidNbt)
            if (auxiliaryFluidNbt != null) tag.put("auxiliaryFluidNbt", auxiliaryFluidNbt)

            return tag
        }

        override fun deserializeNBT(provider: Provider, tag: CompoundTag) {
            // energy
            if (tag.contains("energyStore", 10)) energyStore?.deserializeNBT(provider, tag.getCompound("energyStore"))

            // items
            if (tag.contains("inputItemNbt", 10)) inputItemHandler?.deserializeNBT(
                provider,
                tag.getCompound("inputItemNbt")
            )
            if (tag.contains("outputItemNbt", 10)) outputItemHandler?.deserializeNBT(
                provider,
                tag.getCompound("outputItemNbt")
            )
            if (tag.contains(
                    "additionalItemNbt",
                    10
                )
            ) additionalItemHandler?.deserializeNBT(provider, tag.getCompound("additionalItemNbt"))
            if (tag.contains(
                    "auxiliaryItemNbt",
                    10
                )
            ) auxiliaryItemHandler?.deserializeNBT(provider, tag.getCompound("auxiliaryItemNbt"))

            // fluids
            if (tag.contains("inputFluidNbt", 10)) inputFluidHandler?.deserializeNBT(
                provider,
                tag.getCompound("inputFluidNbt")
            )
            if (tag.contains(
                    "outputFluidNbt",
                    10
                )
            ) outputFluidHandler?.deserializeNBT(provider, tag.getCompound("outputFluidNbt"))
            if (tag.contains(
                    "additionalFluidNbt",
                    10
                )
            ) additionalFluidHandler?.deserializeNBT(provider, tag.getCompound("additionalFluidNbt"))
            if (tag.contains(
                    "auxiliaryFluidNbt",
                    10
                )
            ) auxiliaryFluidHandler?.deserializeNBT(provider, tag.getCompound("auxiliaryFluidNbt"))
        }
    }
}

abstract class TieredProcessedMachine(
    blockEntityType: BlockEntityType<*>,
    blockPos: BlockPos,
    blockState: BlockState,
) : ProcessedMachine(blockEntityType, blockPos, blockState) {
    var tier: ProcessedTier
        get() {
            return hiddenTier
        }
        set(newTier) {
            val oldTier = hiddenTier
            hiddenTier = newTier
            onTierChanged(oldTier, hiddenTier)
        }

    private var hiddenTier: ProcessedTier = ProcessedTier(1, 1, 1)

    override fun loadAdditional(nbt: CompoundTag, provider: Provider) {
        super.loadAdditional(nbt, provider)
        tier = ProcessedTiers.machine[(nbt.getInt("machine_tier").coerceIn(0, ProcessedTiers.machine.size))]
    }

    override fun saveAdditional(nbt: CompoundTag, provider: Provider) {
        super.saveAdditional(nbt, provider)
        nbt.putInt("machine_tier", tier.tier)
    }

    /**
     * Gets called when the tier gets changed.
     *
     * NOTE: **DOES NOT** get called on construction, so if you want that code to execute, call it yourself on construction!
     */
    open fun onTierChanged(old: ProcessedTier, new: ProcessedTier) {}
}