package redcrafter07.processed.item

import net.minecraft.core.component.DataComponentType
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister
import redcrafter07.processed.ProcessedMod
import java.util.function.Supplier
import java.util.function.UnaryOperator

object DataComponents {
    val DATA_COMPONENTS = DeferredRegister.createDataComponents(ProcessedMod.ID)

    val WRENCH_MODE = register("wrench_mode") { builder ->
        builder.persistent(WrenchMode.CODEC).networkSynchronized(WrenchMode.STREAM_CODEC)
    }

    private fun <T> register(
        name: String,
        provider: UnaryOperator<DataComponentType.Builder<T>>
    ): DeferredHolder<DataComponentType<*>, DataComponentType<T>> {
        return DATA_COMPONENTS.register(name, Supplier { provider.apply(DataComponentType.builder()).build() })
    }
}