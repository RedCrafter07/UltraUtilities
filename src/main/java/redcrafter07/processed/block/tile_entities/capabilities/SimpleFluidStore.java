package redcrafter07.processed.block.tile_entities.capabilities;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

public class SimpleFluidStore extends
        IProcessedFluidHandler<CompoundTag> {

    NonNullList<FluidStack> tanks;
    int capacity;

    public SimpleFluidStore(NonNullList<FluidStack> tanks, int capacity) {
        super();
        this.tanks = tanks;
        this.capacity = capacity;
    }

    public SimpleFluidStore(int tanks, int capacity) {
        this(NonNullList.withSize(tanks, FluidStack.EMPTY), capacity);
    }

    protected boolean invalidSlotIndex(int slot) {
        return (slot < 0 || slot >= this.tanks.size());
    }

    @Override
    public int getTanks() {
        return tanks.size();
    }

    @Override
    public FluidStack getFluidInTank(int tank) {
        if (invalidSlotIndex(tank)) return FluidStack.EMPTY;
        return tanks.get(tank);
    }

    @Override
    public int getTankCapacity(int tank) {
        if (invalidSlotIndex(tank)) return 0;
        return capacity;
    }

    @Override
    public boolean isFluidValid(int tank, FluidStack stack) {
        return true;
    }

    @Override
    public int fill(FluidStack stack, FluidAction action) {
        if (stack.isEmpty() || stack.getAmount() < 1) return 0;
        var fluidLeft = stack.getAmount();

        for (int slot = 0; slot < tanks.size(); ++slot) {
            final var amountInTank = tanks.get(slot).getAmount();

            if (amountInTank >= capacity) continue;
            if (tanks.get(slot).isEmpty() || FluidStack.isSameFluidSameComponents(tanks.get(slot), stack)) {
                final var amount = Math.min(capacity - amountInTank, fluidLeft);
                fluidLeft -= amount;

                if (action.execute()) {
                    tanks.set(slot, stack.copyWithAmount(amount + amountInTank));
                    setChanged(slot);
                }
            }

            if (fluidLeft < 1) break;

        }

        return stack.getAmount() - fluidLeft;
    }

    @Override
    public FluidStack drain(FluidStack stack, FluidAction action) {
        if (stack.isEmpty()) return drain(stack.getAmount(), action);
        var fluid = FluidStack.EMPTY;
        var amountLeft = stack.getAmount();

        for (int slot = 0; slot < tanks.size(); ++slot) {
            if (tanks.get(slot).isEmpty() || tanks.get(slot).getAmount() < 1
                    || !FluidStack.isSameFluidSameComponents(tanks.get(slot), stack)) continue;

            if (tanks.get(slot).getAmount() <= amountLeft) {
                if (fluid.isEmpty()) fluid = stack.copyWithAmount(tanks.get(slot).getAmount());
                else fluid.setAmount(fluid.getAmount() + tanks.get(slot).getAmount());
                amountLeft -= tanks.get(slot).getAmount();

                if (action.execute()) {
                    tanks.set(slot, FluidStack.EMPTY);
                    setChanged(slot);
                }

                if (amountLeft < 1) break;
            } else {
                if (fluid.isEmpty()) fluid = stack.copyWithAmount(amountLeft);
                else fluid.setAmount(fluid.getAmount() + amountLeft);

                if (action.execute()) {
                    tanks.get(slot).setAmount(tanks.get(slot).getAmount() - amountLeft);
                    setChanged(slot);
                }

                break;
            }
        }

        return fluid;
    }

    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        var fluid = FluidStack.EMPTY;
        var amountLeft = maxDrain;

        for (int slot = 0; slot < tanks.size(); ++slot) {
            if (tanks.get(slot).isEmpty() || tanks.get(slot).getAmount() < 1) continue;

            if (tanks.get(slot).getAmount() <= amountLeft) {
                if (fluid.isEmpty()) fluid = tanks.get(slot).copyWithAmount(tanks.get(slot).getAmount());
                else fluid.setAmount(fluid.getAmount() + tanks.get(slot).getAmount());
                amountLeft -= tanks.get(slot).getAmount();

                if (action.execute()) {
                    tanks.set(slot, FluidStack.EMPTY);
                    setChanged(slot);
                }

                if (amountLeft < 1) break;
            } else {
                if (fluid.isEmpty()) fluid = tanks.get(slot).copyWithAmount(amountLeft);
                else fluid.setAmount(fluid.getAmount() + amountLeft);

                if (action.execute()) {
                    tanks.get(slot).setAmount(tanks.get(slot).getAmount() - amountLeft);
                    setChanged(slot);
                }

                break;
            }
        }

        return fluid;
    }

    @Override
    public void setFluidInTank(int tank, FluidStack stack) {
        if (invalidSlotIndex(tank)) return;
        tanks.set(tank, stack);
        setChanged(tank);
    }

    @Override
    public @NotNull CompoundTag serializeNBT(Provider provider) {
        final var nbtListTag = new ListTag();

        for (int i = 0; i < tanks.size(); ++i) {
            if (!tanks.get(i).isEmpty() && tanks.get(i).getAmount() > 0) {
                final var tag = new CompoundTag();
                tag.putInt("Slot", i);
                tag.put("Fluid", tanks.get(i).saveOptional(provider));
                nbtListTag.add(tag);
            }
        }

        final var nbt = new CompoundTag();
        nbt.put("Fluids", nbtListTag);
        nbt.putInt("Size", tanks.size());
        return nbt;
    }

    @Override
    public void deserializeNBT(Provider provider, CompoundTag nbt) {
        setSize((nbt.contains("Size", 3)) ? nbt.getInt("Size") : tanks.size());
        final var tagList = nbt.getList("Fluids", 10);

        for (int i = 0;i<tagList.size();++i) {
            final var tag = tagList.getCompound(i);
            final var slot = tag.getInt("Slot");
            if (slot >= 0 && slot < tanks.size())
                tanks.set(slot, FluidStack.parseOptional(provider, tag.getCompound("Fluid")));
        }
    }

    public void setSize(int newSize) {
        tanks = NonNullList.withSize(newSize, FluidStack.EMPTY);
        setChanged(-1);
    }
}