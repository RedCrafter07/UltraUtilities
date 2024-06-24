package redcrafter07.processed.block

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.material.PushReaction
import net.minecraft.world.level.saveddata.SavedData
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape
import net.neoforged.neoforge.capabilities.BlockCapability
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.capability.IFluidHandler
import net.neoforged.neoforge.items.IItemHandler
import redcrafter07.processed.ProcessedMod
import redcrafter07.processed.block.tile_entities.ModTileEntities
import java.util.*

class PipeData(
    val maxExtract: Int,
    val capability: BlockCapability<*, Direction?>,
    val identifier: ResourceLocation
) {
    companion object {

        /** NOTE: This WILL NOT transfer any items **/
        val DEFAULT = PipeData(0, Capabilities.ItemHandler.BLOCK, ProcessedMod.rl("default_pipe_data"))

        val ITEM_PIPE_IRON = PipeData(1, Capabilities.ItemHandler.BLOCK, ProcessedMod.rl("item_pipe_iron"))

        val VALUES = listOf(DEFAULT, ITEM_PIPE_IRON)
    }
}

class PipeBlockEntity(pos: BlockPos, blockState: BlockState) : BlockEntity(
    ModTileEntities.PIPE_BLOCK_ENTITY.get(), pos,
    blockState
) {
    val state: DirectionalPipeState = DirectionalPipeState(this)
    var pipeNet: UUID? = null

    override fun loadAdditional(pTag: CompoundTag, pRegistries: HolderLookup.Provider) {
        super.loadAdditional(pTag, pRegistries)
        state.loadFromNBT("pipeState", pTag)
        pipeNet = if (pTag.hasUUID("network")) pTag.getUUID("network")
        else null
    }

    override fun saveAdditional(pTag: CompoundTag, pRegistries: HolderLookup.Provider) {
        super.saveAdditional(pTag, pRegistries)
        state.saveToNBT("pipeState", pTag)
        if (pipeNet != null) pTag.putUUID("network", pipeNet ?: return)
    }
}

class PipeBlock(private val data: PipeData) : Block(
    Properties.of().sound(SoundType.STONE).isRedstoneConductor { _, _, _ -> false }.noOcclusion()
        .lightLevel { 4 }), EntityBlock, WrenchInteractableBlock {

    override fun hasDynamicShape(): Boolean {
        return true
    }

    override fun getShape(
        blockState: BlockState, blockGetter: BlockGetter, blockPos: BlockPos, collisionContext: CollisionContext
    ): VoxelShape {
        return getShape(blockState)
    }

    override fun propagatesSkylightDown(blockState: BlockState, blockGetter: BlockGetter, blockPos: BlockPos): Boolean {
        return true
    }

    override fun getShadeBrightness(blockState: BlockState, blockGetter: BlockGetter, blockPos: BlockPos): Float {
        return 1f
    }

    override fun createBlockStateDefinition(definitions: StateDefinition.Builder<Block, BlockState>) {
        definitions.add(
            PIPE_STATE_TOP,
            PIPE_STATE_BOTTOM,
            PIPE_STATE_NORTH,
            PIPE_STATE_EAST,
            PIPE_STATE_SOUTH,
            PIPE_STATE_WEST,
        )
    }

    override fun updateShape(
        blockStateA: BlockState,
        direction: Direction,
        blockStateB: BlockState,
        level: LevelAccessor,
        blockPosA: BlockPos,
        blockPosB: BlockPos
    ): BlockState {
        val realLevel =
            level.getBlockEntity(blockPosA)?.level ?: level.getBlockEntity(blockPosB)?.level ?: return blockStateA
        val blockEntity = level.getBlockEntity(blockPosA)
        if (blockEntity !is PipeBlockEntity) return blockStateA
        val newState = connectionType(realLevel, blockEntity, blockPosB, direction.opposite, null)

        if (realLevel is ServerLevel) {
            val pipeNet = blockEntity.pipeNet
            if (pipeNet != null) PipeNetworkData.getForLevel(realLevel).update(blockPosA, direction, newState, pipeNet)
        }

        return blockStateA.setValue(
            propertyForDirection(direction),
            newState
        )
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState? {
        val blockPos = context.clickedPos
        val level = context.level
        var defaultBlockState = stateDefinition.any()
        for (direction in Direction.stream()) {
            val otherBlockPos = blockPos.relative(direction)
            defaultBlockState = defaultBlockState.setValue(
                propertyForDirection(direction), connectionType(
                    level, null, otherBlockPos, direction, PipeState.Normal
                )
            )
        }

        return defaultBlockState
    }

    fun connectionType(
        level: Level,
        myBlockEntity: BlockEntity?,
        other: BlockPos,
        direction: Direction,
        defaultPipeState: PipeState?
    ): PipeState {
        val myPipeState =
            if (myBlockEntity is PipeBlockEntity) myBlockEntity.state.getState(direction) else (defaultPipeState
                ?: return PipeState.None)
        if (myPipeState == PipeState.None) return myPipeState
        val otherBlockState = level.getBlockState(other)
        val otherBlockEntity = level.getBlockEntity(other)
        if (level.getCapability(
                data.capability,
                other,
                otherBlockState,
                otherBlockEntity,
                direction.opposite
            ) != null
        ) return myPipeState
        val otherBlock = otherBlockState.block
        if (otherBlock !is PipeBlock || otherBlockEntity !is PipeBlockEntity) return PipeState.None
        if (otherBlock.data.identifier != data.identifier) return PipeState.None
        if (otherBlockEntity.state.getState(direction.opposite) == PipeState.None) return PipeState.None

        return PipeState.Normal
    }

    override fun onPlace(
        pState: BlockState,
        level: Level,
        pos: BlockPos,
        pOldState: BlockState,
        pMovedByPiston: Boolean
    ) {
        if (!pMovedByPiston && pOldState.block != pState.block) {
            val blockEntity =
                level.getBlockEntity(pos)
            if (blockEntity is PipeBlockEntity && level is ServerLevel)
                PipeNetworkData.getForLevel(level).add(pos, data, blockEntity, level)

        }
        super.onPlace(pState, level, pos, pOldState, pMovedByPiston)
    }

    override fun onRemove(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        newState: BlockState,
        movedByPiston: Boolean
    ) {
        if (state.block != newState.block) return super.onRemove(state, level, pos, newState, movedByPiston)
        val be = level.getBlockEntity(pos)
        if (be !is PipeBlockEntity) return super.onRemove(state, level, pos, newState, movedByPiston)
        val pipeNet = be.pipeNet
        if (pipeNet != null && level is ServerLevel) PipeNetworkData.getForLevel(level).remove(pos, pipeNet)

        super.onRemove(state, level, pos, newState, movedByPiston)
    }

    override fun getPistonPushReaction(state: BlockState): PushReaction {
        return PushReaction.BLOCK
    }

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return PipeBlockEntity(pos, state)
    }

    override fun onWrenchUse(context: UseOnContext, state: BlockState) {}
}

class PipeNetworkData(private val pipeNetworks: HashMap<UUID, PipeNet>) : SavedData() {
    constructor() : this(hashMapOf())

    override fun save(tag: CompoundTag, pRegistries: HolderLookup.Provider): CompoundTag {
        val allNetworks = ListTag()

        for ((_, net) in pipeNetworks) allNetworks.add(net.serializeNBT())
        tag.put("networks", allNetworks)

        return tag
    }

    fun remove(block: BlockPos, pipeNetwork: UUID) {
        val pipeNet = pipeNetworks[pipeNetwork] ?: return
        if (pipeNet.size <= 1) pipeNetworks.remove(pipeNetwork)
        else pipeNet.removeFromNetwork(block)
    }

    private fun merge(first: UUID, second: UUID, level: Level) {
        if (first == second) return
        if (!pipeNetworks.contains(second)) return
        val network = pipeNetworks.remove(second) ?: return

        if (!pipeNetworks.contains(first)) {
            pipeNetworks[first] = network.copyWithId(first)
            return
        }

        pipeNetworks[first]?.merge(network, level)
    }

    fun add(pos: BlockPos, data: PipeData, blockEntity: PipeBlockEntity, level: Level) {
        var uuid: UUID? = blockEntity.pipeNet
        for (direction in Direction.entries) {
            val newBlockPos = pos.relative(direction)
            val otherBlockEntity = level.getBlockEntity(newBlockPos) ?: continue
            if (otherBlockEntity !is PipeBlockEntity) continue
            val blockNet = otherBlockEntity.pipeNet ?: continue
            if (uuid == null) uuid = blockNet
            else merge(uuid, blockNet, level)
        }

        if (uuid == null) {
            uuid = getNewUUID()
            pipeNetworks[uuid] = PipeNet(data, uuid, pos, level)
            blockEntity.pipeNet = uuid
        } else {
            pipeNetworks[uuid]?.addToNetwork(level, pos)
            blockEntity.pipeNet = uuid
        }
    }

    fun update(pos: BlockPos, direction: Direction, newState: PipeState, network: UUID) {
        pipeNetworks[network]?.update(pos, direction, newState)
    }

    private fun getNewUUID(): UUID {
        while (true) {
            val uuid = UUID.randomUUID()
            if (!pipeNetworks.contains(uuid)) return uuid
        }
    }

    companion object {
        private fun load(tag: CompoundTag, lookupProvider: HolderLookup.Provider): PipeNetworkData {
            val networksTag = tag.get("networks")
            val listTag = if (networksTag is ListTag) networksTag else ListTag()
            val pipeNetworks: HashMap<UUID, PipeNet> = hashMapOf()
            for (value in listTag) {
                if (value is CompoundTag) {
                    val pipeNet = PipeNet.deserializeNBT(value) ?: continue
                    pipeNetworks[pipeNet.id] = pipeNet
                }
            }

            return PipeNetworkData(pipeNetworks)
        }

        fun getForLevel(level: ServerLevel): PipeNetworkData {
            return level.dataStorage.computeIfAbsent(
                Factory(::PipeNetworkData, ::load),
                "processed_pipe_network"
            )
        }
    }
}

class PipeNet(
    val data: PipeData,
    val id: UUID,
    private val blocks: MutableList<BlockPos>,
    private val pushing: MutableList<DirectionalPosition>,
    private val pulling: MutableList<DirectionalPosition>
) {
    private var lastPushingIndex = 0

    class DirectionalPosition(val pos: BlockPos, val direction: Direction)

    constructor(data: PipeData, id: UUID) : this(data, id, arrayListOf(), arrayListOf(), arrayListOf())

    constructor(data: PipeData, id: UUID, initial: BlockPos, level: Level) : this(data, id) {
        scan(level, initial)
    }

    val size
        get() = blocks.size

    fun update(pos: BlockPos, direction: Direction, newState: PipeState) {
        pushing.removeIf { it.pos == pos && it.direction == direction }
        pulling.removeIf { it.pos == pos && it.direction == direction }
        if (newState == PipeState.Pull)
            pulling.add(DirectionalPosition(pos, direction))
        if (newState == PipeState.Push)
            pushing.add(DirectionalPosition(pos, direction))
    }

    fun addToNetwork(level: Level, blockPos: BlockPos) {
        if (blocks.contains(blockPos)) return
        val block = level.getBlockState(blockPos).block
        val blockEntity = level.getBlockEntity(blockPos) ?: return
        if (blockEntity !is PipeBlockEntity || block !is PipeBlock) return
        blocks.add(blockPos)
        blockEntity.pipeNet = id
        blockEntity.setChanged()

        for (direction in Direction.entries) {
            val newBlockPos = blockPos.relative(direction)
            val connectionType = block.connectionType(level, blockEntity, newBlockPos, direction, null)
            if (connectionType == PipeState.Pull) pulling.add(DirectionalPosition(blockPos, direction))
            if (connectionType == PipeState.Push) pushing.add(DirectionalPosition(blockPos, direction))
        }
    }

    fun removeFromNetwork(pos: BlockPos) {
        blocks.remove(pos)
        pushing.removeIf { it.pos == pos }
        pulling.removeIf { it.pos == pos }
    }

    fun merge(other: PipeNet, level: Level): Boolean {
        if (other.data.identifier != data.identifier || other.data.capability.name() != data.capability.name()) return false

        for (pos in other.pulling) if (!pulling.contains(pos)) pulling.add(pos)
        for (pos in other.pushing) if (!pushing.contains(pos)) pushing.add(pos)

        for (pos in other.blocks) {
            if (!blocks.contains(pos)) blocks.add(pos)

            val be = level.getBlockEntity(pos)
            if (be !is PipeBlockEntity) continue
            be.pipeNet = id
            be.setChanged()
        }
        return true
    }

    private fun scan(level: Level, initialBlock: BlockPos) {
        blocks.clear()
        pulling.clear()
        pushing.clear()

        val alreadyWalked: MutableSet<BlockPos> = mutableSetOf()
        val toWalk: Stack<BlockPos> = Stack()
        toWalk.push(initialBlock)

        while (toWalk.size > 0) {
            val curBlockPos = toWalk.pop()
            alreadyWalked.add(curBlockPos)
            val block = level.getBlockState(curBlockPos).block
            val blockEntity = level.getBlockEntity(curBlockPos)
            if (block !is PipeBlock || blockEntity !is PipeBlockEntity) return
            blocks.add(curBlockPos)
            blockEntity.pipeNet = id
            blockEntity.setChanged()

            for (direction in Direction.entries) {
                val newBlockPos = curBlockPos.relative(direction)

                val connectionType = block.connectionType(level, blockEntity, newBlockPos, direction, null)
                if (connectionType != PipeState.None) {
                    if (connectionType == PipeState.Normal
                        && (level.getBlockEntity(newBlockPos) is PipeBlockEntity)
                        && !alreadyWalked.contains(newBlockPos)
                    ) toWalk.push(newBlockPos)
                    if (connectionType == PipeState.Pull) pulling.add(DirectionalPosition(curBlockPos, direction))
                    if (connectionType == PipeState.Push) pushing.add(DirectionalPosition(curBlockPos, direction))
                }
            }
        }
    }

    private fun updateItem(level: Level, maxExtract: Int) {
        var capability: IItemHandler? = null
        var slot = 0
        var itemStack = ItemStack.EMPTY

        for (pullingPos in pulling) {
            val localCapability = level.getCapability(
                Capabilities.ItemHandler.BLOCK,
                pullingPos.pos.relative(pullingPos.direction),
                pullingPos.direction.opposite
            ) ?: continue

            for (localSlot in 0..<localCapability.slots) {
                val item = localCapability.extractItem(localSlot, maxExtract, true)
                if (item.isEmpty) continue
                itemStack = item
                capability = localCapability
                slot = localSlot
            }

            if (capability != null) break
        }
        if (capability == null) return

        for (i in 1..pushing.size) {
            val pushingPos = pushing[(i + lastPushingIndex) % pushing.size] // fake round robbin
            val localCapability =
                level.getCapability(Capabilities.ItemHandler.BLOCK, pushingPos.pos, pushingPos.direction)
                    ?: continue

            for (localSlot in 0..<localCapability.slots) {
                itemStack = localCapability.insertItem(localSlot, itemStack, false)
                if (itemStack.isEmpty) break
            }
            if (itemStack.isEmpty) {
                lastPushingIndex = (i + lastPushingIndex) % pushing.size
                break
            }
        }

        if (itemStack.isEmpty) capability.extractItem(slot, maxExtract, false)
        else capability.extractItem(slot, maxExtract - itemStack.count, false)
    }

    private fun updateFluid(level: Level, maxExtract: Int) {
        var capability: IFluidHandler? = null
        var fluidStack = FluidStack.EMPTY

        for (pullingPos in pulling) {
            val localCapability = level.getCapability(
                Capabilities.FluidHandler.BLOCK,
                pullingPos.pos.relative(pullingPos.direction),
                pullingPos.direction.opposite
            ) ?: continue

            val item = localCapability.drain(maxExtract, IFluidHandler.FluidAction.SIMULATE)
            if (item.isEmpty) continue
            fluidStack = item
            capability = localCapability
            break
        }
        if (capability == null) return

        for (i in 1..pushing.size) {
            val pushingPos = pushing[(i + lastPushingIndex) % pushing.size] // fake round robbin
            val localCapability =
                level.getCapability(Capabilities.FluidHandler.BLOCK, pushingPos.pos, pushingPos.direction)
                    ?: continue

            fluidStack = fluidStack.copyWithAmount(localCapability.fill(fluidStack, IFluidHandler.FluidAction.EXECUTE))
            if (fluidStack.isEmpty) {
                lastPushingIndex = (i + lastPushingIndex) % pushing.size
                break
            }
        }

        if (fluidStack.isEmpty) capability.drain(maxExtract, IFluidHandler.FluidAction.EXECUTE)
        else capability.drain(maxExtract - fluidStack.amount, IFluidHandler.FluidAction.EXECUTE)
    }

    private fun updateEnergy(level: Level, maxExtract: Int) {
        var leftForExtraction = maxExtract
        val capabilities =
            pushing.mapNotNull { level.getCapability(Capabilities.EnergyStorage.BLOCK, it.pos, it.direction) }
        if (capabilities.isEmpty()) return

        for (pushingPos in pushing) {
            val localCapability =
                level.getCapability(Capabilities.EnergyStorage.BLOCK, pushingPos.pos, pushingPos.direction) ?: continue
            val originallyExtractedEnergy = localCapability.extractEnergy(leftForExtraction, true)
            var energyAvailable = originallyExtractedEnergy
            // this is fine because if we don't use up all the energy, we will return anyway
            leftForExtraction -= energyAvailable

            for (capability in capabilities) {
                energyAvailable -= capability.receiveEnergy(energyAvailable, false)
                if (energyAvailable < 1) break
            }

            // return if all recipients have all the energy they could possibly have
            if (energyAvailable > 0) {
                localCapability.extractEnergy(originallyExtractedEnergy - energyAvailable, false)
                return
            } else localCapability.extractEnergy(originallyExtractedEnergy, false)
        }
    }

    fun update(level: Level) {
        if (data.capability.name() == Capabilities.ItemHandler.BLOCK.name()) updateItem(level, data.maxExtract)
        if (data.capability.name() == Capabilities.ItemHandler.BLOCK.name()) updateFluid(level, data.maxExtract)
        if (data.capability.name() == Capabilities.ItemHandler.BLOCK.name()) updateEnergy(level, data.maxExtract)
    }

    fun serializeNBT(): CompoundTag {
        val tag = CompoundTag()

        tag.putString("data", data.identifier.toString())
        tag.putIntArray("blocks", serializeBlockPos(blocks))
        tag.putIntArray("pushing", serializeDirectionalBlockPos(pushing))
        tag.putIntArray("pulling", serializeDirectionalBlockPos(pulling))
        tag.putUUID("id", id)

        return tag
    }

    fun copyWithId(newId: UUID): PipeNet {
        return PipeNet(data, newId, blocks, pushing, pulling)
    }

    companion object {
        fun deserializeNBT(nbt: CompoundTag): PipeNet? {
            val id = ResourceLocation.tryParse(nbt.getString("data")) ?: return null
            val data = PipeData.VALUES.find { it.identifier == id } ?: return null

            val blocks = deserializeBlockPos(nbt.getIntArray("blocks"))
            val pushing = deserializeDirectionalBlockPos(nbt.getIntArray("pushing"))
            val pulling = deserializeDirectionalBlockPos(nbt.getIntArray("pulling"))
            if (blocks.isEmpty()) return null

            return PipeNet(data, nbt.getUUID("id"), blocks, pushing, pulling)
        }

        private fun serializeBlockPos(blocks: List<BlockPos>): List<Int> {
            val array = arrayListOf<Int>()
            for (pos in blocks) {
                array.add(pos.x)
                array.add(pos.y)
                array.add(pos.z)
            }
            return array
        }

        private fun serializeDirectionalBlockPos(blocks: List<DirectionalPosition>): List<Int> {
            val array = arrayListOf<Int>()
            for (pos in blocks) {
                array.add(pos.pos.x)
                array.add(pos.pos.y)
                array.add(pos.pos.z)
                array.add(pos.direction.get3DDataValue())
            }
            return array
        }

        private fun deserializeBlockPos(intArray: IntArray): MutableList<BlockPos> {
            val array = arrayListOf<BlockPos>()
            for (i in 0..<intArray.size.floorDiv(3)) {
                val actualIndex = i * 3
                array.add(BlockPos(intArray[actualIndex], intArray[actualIndex + 1], intArray[actualIndex + 2]))
            }
            return array
        }

        private fun deserializeDirectionalBlockPos(intArray: IntArray): MutableList<DirectionalPosition> {
            val array = arrayListOf<DirectionalPosition>()
            for (i in 0..<intArray.size.floorDiv(4)) {
                val actualIndex = i * 4
                array.add(
                    DirectionalPosition(
                        BlockPos(
                            intArray[actualIndex],
                            intArray[actualIndex + 1],
                            intArray[actualIndex + 2]
                        ),
                        Direction.BY_ID.apply(intArray[actualIndex + 3])
                    )
                )
            }
            return array
        }
    }
}