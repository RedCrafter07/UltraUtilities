package redcrafter07.processed.block.tile_entities.capabilities;

import net.minecraft.nbt.Tag;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

import javax.annotation.Nullable;

public abstract class IProcessedItemHandler<T extends Tag> implements
        IItemHandlerModifiable,
        INBTSerializable<T> {
    private @Nullable OnChangeHandler onChangeHandler = null;

    public void setOnChange(@Nullable OnChangeHandler newOnChangeHandler) {
        onChangeHandler = newOnChangeHandler;
    }

    protected void setChanged(int slot) {
        if (onChangeHandler != null) onChangeHandler.onChange();
    }
}
