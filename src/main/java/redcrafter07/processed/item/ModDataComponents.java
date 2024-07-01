package redcrafter07.processed.item;

import net.minecraft.core.component.DataComponentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import redcrafter07.processed.ProcessedMod;

import java.util.function.UnaryOperator;

public final class ModDataComponents {
    public final static DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(ProcessedMod.ID);

    public final static DeferredHolder<DataComponentType<?>, DataComponentType<WrenchMode>> WRENCH_MODE =
            register("wrench_mode", (builder) -> builder.persistent(WrenchMode.CODEC).networkSynchronized(WrenchMode.STREAM_CODEC));

    private static <T> DeferredHolder<DataComponentType<?>, DataComponentType<T>> register(
            String name,
            UnaryOperator<DataComponentType.Builder<T>> provider
    ) {
        return DATA_COMPONENTS.register(name, () -> provider.apply(DataComponentType.builder()).build());
    }
}