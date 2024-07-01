package redcrafter07.processed.block.tile_entities.capabilities;

import net.minecraft.nbt.Tag;
import net.neoforged.neoforge.common.util.INBTSerializable;

import javax.annotation.Nullable;

public abstract class IProcessedEnergyHandler<T extends Tag> implements
        IEnergyStorageModifiable,
        INBTSerializable<T> {
    private @Nullable OnChangeHandler onChangeHandler = null;

    public void setOnChange(@Nullable OnChangeHandler newOnChangeHandler) {
        onChangeHandler = newOnChangeHandler;
    }

    protected void setChanged() {
        if (onChangeHandler != null) onChangeHandler.onChange();
    }
}

