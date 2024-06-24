package redcrafter07.processed.datagen

import net.minecraft.core.HolderLookup
import net.minecraft.core.RegistrySetBuilder
import net.minecraft.core.registries.Registries
import net.minecraft.data.PackOutput
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider
import net.neoforged.neoforge.registries.NeoForgeRegistries
import redcrafter07.processed.ProcessedMod
import redcrafter07.processed.worldgen.ModBiomeModifiers
import redcrafter07.processed.worldgen.ModConfiguredFeatures
import redcrafter07.processed.worldgen.ModPlacedFeatures
import java.util.concurrent.CompletableFuture

val BUILDER: RegistrySetBuilder = RegistrySetBuilder()
    .add(Registries.CONFIGURED_FEATURE, ModConfiguredFeatures::bootstrap)
    .add(Registries.PLACED_FEATURE, ModPlacedFeatures::bootstrap)
    .add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, ModBiomeModifiers::bootstrap)

class ModWorldGenProvider(
    output: PackOutput,
    registries: CompletableFuture<HolderLookup.Provider>,
) : DatapackBuiltinEntriesProvider(
    output, registries, BUILDER, setOf(ProcessedMod.ID)
)