package redcrafter07.processed.worldgen

import net.minecraft.core.HolderSet
import net.minecraft.core.registries.Registries
import net.minecraft.data.worldgen.BootstapContext
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.BiomeTags
import net.minecraft.world.level.levelgen.GenerationStep
import net.neoforged.neoforge.common.world.BiomeModifier
import net.neoforged.neoforge.common.world.BiomeModifiers
import net.neoforged.neoforge.registries.NeoForgeRegistries
import redcrafter07.processed.ProcessedMod

object ModBiomeModifiers {
    val ADD_BLITZ_ORE = registerKey("add_blitz_ore");

    fun bootstrap(context: BootstapContext<BiomeModifier>) {
        val placedFeatures = context.lookup(Registries.PLACED_FEATURE);
        val biomes = context.lookup(Registries.BIOME);

        context.register(
            ADD_BLITZ_ORE, BiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.OVERWORLD_BLITZ_ORE_PLACED_KEY)),
                GenerationStep.Decoration.UNDERGROUND_ORES
            )
        );
    }

    private fun registerKey(name: String): ResourceKey<BiomeModifier> {
        return ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, ResourceLocation(ProcessedMod.ID, name));
    }
}