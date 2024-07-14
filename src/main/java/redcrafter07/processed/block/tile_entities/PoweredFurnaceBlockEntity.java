package redcrafter07.processed.block.tile_entities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import redcrafter07.processed.block.machine_abstractions.IoState;
import redcrafter07.processed.block.machine_abstractions.ProcessedTier;
import redcrafter07.processed.block.machine_abstractions.TieredProcessedMachine;
import redcrafter07.processed.gui.PoweredFurnaceMenu;

import javax.annotation.Nullable;

public final class PoweredFurnaceBlockEntity extends TieredProcessedMachine {
    private final ContainerData data;
    private int progress = 0;
    private int maxProgress = 78;

    public PoweredFurnaceBlockEntity(BlockPos pos, BlockState state) {
        super(ModTileEntities.POWERED_FURNACE.get(), pos, state);

        useItemCapability(IoState.Input);
        useItemCapability(IoState.Output);
        onTierChanged(getTier(), getTier());

        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> progress;
                    case 1 -> maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> progress = value;
                    case 1 -> maxProgress = value;
                    default -> {
                    }
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    @Override
    public void onTierChanged(ProcessedTier oldTier, ProcessedTier newTier) {
        useScaledEnergyCapability(1000);
    }

    @Override
    public void commonTick(Level level, BlockPos pos, BlockState state) {
        if (hasRecipeAndSync()) {
            if (!useScaledPower(1)) {
                if (progress <= 0) return;
                progress -= getTier().multiplierSpeed() * 2;
                if (progress < 0) progress = 0;
                setChanged(level, pos, state);
                return;
            }
            progress += getTier().multiplierSpeed(); // we could also just do `maxProgress = recipe.cookingTime` in `hasRecipeAndSync`, but this takes less computation power!

            if (progress > maxProgress) {
                progress = 0;
                craftItem();
            }
            setChanged(level, pos, state);
        } else if (progress != 0) {
            progress = 0;
            setChanged(level, pos, state);
        }
    }

    private void craftItem() {
        final var recipe = getCurrentRecipe();
        if (recipe == null) return;
        if (level == null) return;
        final var result = recipe.getResultItem(level.registryAccess());

        var ingredient = recipe.getIngredients().getFirst().getItems()[0];
        if (ingredient.isEmpty()) return;
        var remainingItems = recipe.getRemainingItems(new SingleRecipeInput(getInputItemHandler().getStackInSlot(0)));
        boolean hasRemainingItems = !remainingItems.isEmpty() && !remainingItems.getFirst().isEmpty();
        if (hasRemainingItems) getInputItemHandler().setStackInSlot(0, remainingItems.getFirst().copy());
        else {
            ItemStack input = getInputItemHandler().getStackInSlot(0);
            getInputItemHandler().setStackInSlot(0, input.copyWithCount(input.getCount() - ingredient.getCount()));
        }

        getOutputItemHandler().setStackInSlot(
                0,
                new ItemStack(result.getItem(), getOutputItemHandler().getStackInSlot(0).getCount() + result.getCount())
        );
    }

    private boolean hasRecipeAndSync() {
        final var recipe = getCurrentRecipe();
        if (recipe == null) return false;
        if (level == null) return false;
        final var result = recipe.getResultItem(level.registryAccess());
        if (!canInsertAmountIntoOutputSlot(result.getCount()) || !canInsertItemIntoOutputSlot(result.getItem()))
            return false;
        maxProgress = recipe.getCookingTime();

        return true;
    }

    private @Nullable SmeltingRecipe getCurrentRecipe() {
        final var level = this.level;
        if (level == null) return null;

        final var recipe = level.getRecipeManager().getRecipeFor(
                RecipeType.SMELTING,
                new SingleRecipeInput(getInputItemHandler().getStackInSlot(0)),
                level
        ).map(RecipeHolder::value).orElse(null);
        if (recipe == null) return null;

        final var result = recipe.getResultItem(level.registryAccess());
        ItemStack currentOutput = getOutputItemHandler().getStackInSlot(0);
        if (result.getCount() + currentOutput.getCount() > getOutputItemHandler().getSlotLimit(0) || result.getCount() + currentOutput.getCount() > currentOutput.getMaxStackSize())
            return null;
        if (!currentOutput.isEmpty()) {
            if (!ItemStack.isSameItemSameComponents(currentOutput, result)) return null;
        }

        var ingredient = recipe.getIngredients().getFirst().getItems()[0];
        if (ingredient.isEmpty()) return null;
        var remainingItems = recipe.getRemainingItems(new SingleRecipeInput(getInputItemHandler().getStackInSlot(0)));
        boolean hasRemainingItems = !remainingItems.isEmpty() && !remainingItems.getFirst().isEmpty();
        if (hasRemainingItems && getInputItemHandler().getStackInSlot(0).getCount() > ingredient.getCount())
            return null;

        return recipe;
    }

    private boolean canInsertItemIntoOutputSlot(Item item) {
        final var outputStack = getOutputItemHandler().getStackInSlot(0);
        return outputStack.isEmpty() || outputStack.is(item);
    }

    private boolean canInsertAmountIntoOutputSlot(int count) {
        final var outputStack = getOutputItemHandler().getStackInSlot(0);
        return outputStack.getCount() + count <= getOutputItemHandler().getSlotLimit(0);
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new PoweredFurnaceMenu(containerId, playerInventory, this, data);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.processed.powered_furnace", getTier().translated());
    }

    @Override
    public void saveAdditional(CompoundTag nbt, Provider provider) {
        super.saveAdditional(nbt, provider);
        nbt.putInt("progress", progress);
    }

    @Override
    public void loadAdditional(CompoundTag nbt, Provider provider) {
        super.loadAdditional(nbt, provider);
        progress = nbt.getInt("progress");
    }
}