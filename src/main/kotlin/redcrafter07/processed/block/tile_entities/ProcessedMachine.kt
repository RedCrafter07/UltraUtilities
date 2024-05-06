package redcrafter07.processed.block.tile_entities

import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.world.Containers
import net.minecraft.world.SimpleContainer
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.common.util.INBTSerializable
import net.neoforged.neoforge.energy.IEnergyStorage
import net.neoforged.neoforge.fluids.capability.IFluidHandler
import net.neoforged.neoforge.items.IItemHandler
import net.neoforged.neoforge.items.IItemHandlerModifiable
import org.joml.Vector2i
import redcrafter07.processed.ProcessedMod
import redcrafter07.processed.block.ProcessedTier
import redcrafter07.processed.block.WrenchInteractableBlock
import redcrafter07.processed.block.tile_entities.capabilities.*
import redcrafter07.processed.gui.ConfigScreen
import net.neoforged.neoforge.items.wrapper.EmptyHandler as EmptyItemHandler

enum class IoState {
    None,
    Input,
    Output,
    InputOutput,
    Additional,
    Auxiliary;


    companion object {
        fun load(value: Byte): IoState {
            return when (value.toInt()) {
                0 -> None
                1 -> Input
                2 -> Output
                3 -> InputOutput
                4 -> Additional
                5 -> Auxiliary
                else -> Input
            }
        }
    }

    fun save(): Byte {
        return when (this) {
            None -> 0.toByte()
            Input -> 1.toByte()
            Output -> 2.toByte()
            InputOutput -> 3.toByte()
            Additional -> 4.toByte()
            Auxiliary -> 5.toByte()
        }
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
        return when (this) {
            None -> Component.translatable("processed.io_state.none")
            Input -> Component.translatable("processed.io_state.input")
            Output -> Component.translatable("processed.io_state.output")
            InputOutput -> Component.translatable("processed.io_state.input_output")
            Additional -> Component.translatable("processed.io_state.additional")
            Auxiliary -> Component.translatable("processed.io_state.auxiliary")
        }
    }
}

// translation: IoSide <=> Direction
// Down:    Bottom
// Top:     Up
// Front:   North
// Back:    South
// Left:    West
// Right:   East

// Direction translation Matrix. Do LOOKUP[Facing.3dData][Direction.3dData] and you get the "real" direction! (its like Direction + Direction)
val DIRECTION_LOOKUP: List<List<Direction>> = listOf(
    listOf(
        // ---{ rotation in direction: Down (Facing Bottom) }---
        // front side: bottom
        // back side: top
        // left side: left
        // right side: right
        // top side: front
        // bottom side: back

        // Down
        Direction.SOUTH,
        // Up
        Direction.NORTH,
        // Front
        Direction.DOWN,
        // Back
        Direction.UP,
        // Left
        Direction.WEST,
        // Right
        Direction.EAST
    ),
    listOf(
        // ---{ rotation in direction: Up (Facing Top) }---
        // front side: Top
        // back side: Bottom
        // left side: Left
        // right side: Right
        // top side: Back
        // bottom side: Front

        // Down
        Direction.NORTH,
        // Up
        Direction.SOUTH,
        // Front
        Direction.UP,
        // Back
        Direction.SOUTH,
        // Left
        Direction.WEST,
        // Right
        Direction.EAST
    ),
    listOf(
        // ---{ rotation in direction: N/A (Facing North) }---
        // front side: front
        // back side: back
        // left side: left
        // right side: right
        // top side: top
        // bottom side: bottom

        // Down
        Direction.DOWN,
        // Up
        Direction.UP,
        // Front
        Direction.NORTH,
        // Back
        Direction.SOUTH,
        // Left
        Direction.WEST,
        // Right
        Direction.EAST
    ),
    listOf(
        // ---{ 2x rotation in direction: Right (Facing South) }---
        // front side: back
        // back side: front
        // left side: right
        // right side: left
        // top side: top
        // bottom side: bottom

        // Down
        Direction.DOWN,
        // Up
        Direction.UP,
        // Front
        Direction.SOUTH,
        // Back
        Direction.NORTH,
        // Left
        Direction.EAST,
        // Right
        Direction.WEST,
    ),
    listOf(
        // ---{ rotation in direction: Left (Facing West) }---
        // front side: left
        // back side: right
        // left side: back
        // right side: front
        // top side: top
        // bottom side: bottom

        // Down
        Direction.DOWN,
        // Up
        Direction.UP,
        // Front
        Direction.WEST,
        // Back
        Direction.EAST,
        // Left
        Direction.SOUTH,
        // Right
        Direction.NORTH
    ),
    listOf(
        // ---{ rotation in direction: Right (Facing East) }---
        // front side: right
        // back side: left
        // left side: front
        // right side: back
        // top side: top
        // bottom side: bottom

        // Down
        Direction.DOWN,
        // Up
        Direction.UP,
        // Front
        Direction.EAST,
        // Back
        Direction.WEST,
        // Left
        Direction.NORTH,
        // Right
        Direction.SOUTH
    ),
)

enum class BlockSide {
    Top,
    Bottom,
    Left,
    Right,
    Front,
    Back;

    companion object {
        fun load(value: Byte): BlockSide {
            return when (value.toInt()) {
                0 -> Top
                1 -> Bottom
                2 -> Left
                3 -> Right
                4 -> Front
                5 -> Back
                else -> Top
            }
        }

        fun fromDirection(direction: Direction): BlockSide {
            return when (direction) {
                Direction.UP -> Top
                Direction.DOWN -> Bottom
                Direction.NORTH -> Front
                Direction.SOUTH -> Back
                Direction.WEST -> Left
                Direction.EAST -> Right
            }
        }

        fun getFacing(machineFacing: Direction, direction: Direction): BlockSide {
            if (machineFacing == Direction.NORTH) return fromDirection(direction)
            if (direction == Direction.NORTH) return fromDirection(machineFacing)
            return fromDirection(DIRECTION_LOOKUP[machineFacing.get3DDataValue()][direction.get3DDataValue()])
        }
    }

    fun save(): Byte {
        return when (this) {
            Top -> 0.toByte()
            Bottom -> 1.toByte()
            Left -> 2.toByte()
            Right -> 3.toByte()
            Front -> 4.toByte()
            Back -> 5.toByte()
        }
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
        return if (itemOrFluid) sides[side.save().toInt()] else sides[side.save().toInt() + 6]
    }

    fun setSide(itemOrFluid: Boolean, side: BlockSide, value: IoState) {
        if (itemOrFluid) sides[side.save().toInt()] = value
        else sides[side.save().toInt() + 6] = value
        this.setChanged()
    }

    override fun load(nbt: CompoundTag) {
        super.load(nbt)

        val byteArray = nbt.getByteArray("io_states")
        for (index in 0..<12) {
            sides[index] = if (byteArray.size <= index) IoState.None else IoState.load(byteArray[index])
        }

        capabilityHandlers.deserializeNBT(nbt.getCompound("capability_handlers"))
    }

    override fun saveAdditional(nbt: CompoundTag) {
        super.saveAdditional(nbt)
        val list = sides.map { it.save() }.toList()
        nbt.putByteArray("io_states", list)
        nbt.put("capability_handlers", capabilityHandlers.serializeNBT())
    }

    override fun getUpdatePacket(): Packet<ClientGamePacketListener>? {
        return ClientboundBlockEntityDataPacket.create(this)
    }

    override fun getUpdateTag(): CompoundTag {
        return saveWithoutMetadata()
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

        override fun serializeNBT(): CompoundTag {
            val tag = CompoundTag()

            ProcessedMod.LOGGER.info("output: {}", inputItemHandler)
            ProcessedMod.LOGGER.info("input: {}", outputItemHandler)
            ProcessedMod.LOGGER.info("me: {}", this)

            // energy
            val energyStoreNbt = energyStore?.serializeNBT();

            if (energyStoreNbt != null) tag.put("energyStore", energyStoreNbt)


            // items
            val inputItemNbt = inputItemHandler?.serializeNBT();
            val outputItemNbt = outputItemHandler?.serializeNBT();
            val additionalItemNbt = additionalItemHandler?.serializeNBT();
            val auxiliaryItemNbt = auxiliaryItemHandler?.serializeNBT();

            if (inputItemNbt != null) tag.put("inputItemNbt", inputItemNbt)
            if (outputItemNbt != null) tag.put("outputItemNbt", outputItemNbt)
            if (additionalItemNbt != null) tag.put("additionalItemNbt", additionalItemNbt)
            if (auxiliaryItemNbt != null) tag.put("auxiliaryItemNbt", auxiliaryItemNbt)


            // fluids
            val inputFluidNbt = inputFluidHandler?.serializeNBT();
            val outputFluidNbt = outputFluidHandler?.serializeNBT();
            val additionalFluidNbt = additionalFluidHandler?.serializeNBT();
            val auxiliaryFluidNbt = auxiliaryFluidHandler?.serializeNBT();

            if (inputFluidNbt != null) tag.put("inputFluidNbt", inputFluidNbt)
            if (outputFluidNbt != null) tag.put("outputFluidNbt", outputFluidNbt)
            if (additionalFluidNbt != null) tag.put("additionalFluidNbt", additionalFluidNbt)
            if (auxiliaryFluidNbt != null) tag.put("auxiliaryFluidNbt", auxiliaryFluidNbt)

            return tag
        }

        override fun deserializeNBT(tag: CompoundTag) {
            // energy
            if (tag.contains("energyStore", 10)) energyStore?.deserializeNBT(tag.getCompound("energyStore"))

            // items
            if (tag.contains("inputItemNbt", 10)) inputItemHandler?.deserializeNBT(tag.getCompound("inputItemNbt"))
            if (tag.contains("outputItemNbt", 10)) outputItemHandler?.deserializeNBT(tag.getCompound("outputItemNbt"))
            if (tag.contains("additionalItemNbt", 10)) additionalItemHandler?.deserializeNBT(tag.getCompound("additionalItemNbt"))
            if (tag.contains("auxiliaryItemNbt", 10)) auxiliaryItemHandler?.deserializeNBT(tag.getCompound("auxiliaryItemNbt"))

            // fluids
            if (tag.contains("inputFluidNbt", 10)) inputFluidHandler?.deserializeNBT(tag.getCompound("inputFluidNbt"))
            if (tag.contains("outputFluidNbt", 10)) outputFluidHandler?.deserializeNBT(tag.getCompound("outputFluidNbt"))
            if (tag.contains("additionalFluidNbt", 10)) additionalFluidHandler?.deserializeNBT(tag.getCompound("additionalFluidNbt"))
            if (tag.contains("auxiliaryFluidNbt", 10)) auxiliaryFluidHandler?.deserializeNBT(tag.getCompound("auxiliaryFluidNbt"))
        }
    }
}

abstract class TieredProcessedMachine(
    blockEntityType: BlockEntityType<*>,
    blockPos: BlockPos,
    blockState: BlockState,
) : ProcessedMachine(blockEntityType, blockPos, blockState) {
    var tier: ProcessedTier = ProcessedTier(1, 1, 1)

    override fun load(nbt: CompoundTag) {
        super.load(nbt)
        tier = ProcessedTier.load("machine_tier", nbt)
    }

    override fun saveAdditional(nbt: CompoundTag) {
        super.saveAdditional(nbt)
        tier.save("machine_tier", nbt)
    }
}