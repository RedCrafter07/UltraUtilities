package redcrafter07.processed.block.tile_entities

import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.world.Containers
import net.minecraft.world.MenuProvider
import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.ContainerData
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.item.crafting.SmeltingRecipe
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.items.ItemStackHandler
import redcrafter07.processed.gui.PoweredFurnaceMenu
import kotlin.jvm.optionals.getOrNull

class PoweredFurnaceBlockEntity(pos: BlockPos, state: BlockState) :
    TieredProcessedMachine(ModTileEntities.POWERED_FURNACE.get(), pos, state), MenuProvider {
    companion object {
        private val SLOT_INPUT = 0
        private val SLOT_OUTPUT = 1
    }

    val itemHandler = ItemStackHandler(2)

    protected val data: ContainerData
    private var progress = 0
    private var maxProgress = 78

    init {
        this.data = object : ContainerData {
            override fun get(index: Int): Int {
                return when (index) {
                    0 -> progress
                    1 -> maxProgress
                    else -> 0
                }
            }

            override fun set(index: Int, value: Int) {
                when (index) {
                    0 -> progress = value
                    1 -> maxProgress = value
                    else -> {}
                }
            }

            override fun getCount(): Int {
                return 2
            }
        }
    }

    fun drops() {
        val inventory = SimpleContainer(itemHandler.slots)
        for (i in 0..<inventory.containerSize) {
            inventory.setItem(i, itemHandler.getStackInSlot(i))
        }

        this.level?.let { Containers.dropContents(it, this.worldPosition, inventory) }
    }

    fun tick(level: Level, pos: BlockPos, state: BlockState) {
        if (hasRecipeAndSync()) {
            progress += tier.multiplier_speed // we could also just do `maxProgress = recipe.cookingTime` in `hasRecipeAndSync`, but this takes less computation power!

            if (progress > maxProgress) {
                progress = 0
                craftItem()
            }
            setChanged(level, pos, state)
        } else if (progress != 0) {
            progress = 0
            setChanged(level, pos, state)
        }
    }

    private fun craftItem() {
        val recipe = getCurrentRecipe() ?: return
        val result = recipe.getResultItem(level?.registryAccess() ?: return)
        this.itemHandler.extractItem(SLOT_INPUT, 1, false)

        this.itemHandler.setStackInSlot(
            SLOT_OUTPUT,
            ItemStack(result.item, itemHandler.getStackInSlot(SLOT_OUTPUT).count + result.count)
        )
    }

    private fun hasRecipeAndSync(): Boolean {
        val recipe = getCurrentRecipe() ?: return false
        val result = recipe.getResultItem(level?.registryAccess() ?: return false)
        if (!canInsertAmountIntoOutputSlot(result.count) || !canInsertItemIntoOutputSlot(result.item)) return false
        maxProgress = recipe.cookingTime

        return true
    }

    private fun getCurrentRecipe(): SmeltingRecipe? {
        val inventory = SimpleContainer(this.itemHandler.getStackInSlot(SLOT_INPUT))
        val level = this.level ?: return null

        return level.recipeManager.getRecipeFor(RecipeType.SMELTING, inventory, level).map { recipe -> recipe.value }.getOrNull()
    }

    private fun canInsertItemIntoOutputSlot(item: Item): Boolean {
        return itemHandler.getStackInSlot(SLOT_OUTPUT).isEmpty || itemHandler.getStackInSlot(SLOT_OUTPUT).`is`(item)
    }

    private fun canInsertAmountIntoOutputSlot(count: Int): Boolean {
        return itemHandler.getStackInSlot(SLOT_OUTPUT).count + count <= itemHandler.getStackInSlot(SLOT_OUTPUT).maxStackSize
    }

    override fun createMenu(containerId: Int, playerInventory: Inventory, player: Player): AbstractContainerMenu {
        return PoweredFurnaceMenu(containerId, playerInventory, this, data)
    }

    override fun getDisplayName(): Component {
        return Component.translatable("processed.screen.powered_furnace.name", tier.translated())
    }

    override fun saveAdditional(nbt: CompoundTag) {
        super.saveAdditional(nbt)
        nbt.put("inventory", itemHandler.serializeNBT())
        nbt.putInt("powered_furnace.progress", progress)
    }

    override fun load(nbt: CompoundTag) {
        super.load(nbt)
        itemHandler.deserializeNBT(nbt.get("inventory") as CompoundTag)
        progress = nbt.getInt("powered_furance.progress")
    }
}