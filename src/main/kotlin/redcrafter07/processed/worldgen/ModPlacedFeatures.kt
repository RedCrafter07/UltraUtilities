package redcrafter07.processed.worldgen

import net.minecraft.core.Holder
import net.minecraft.core.registries.Registries
import net.minecraft.data.worldgen.BootstapContext
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.levelgen.VerticalAnchor
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement
import net.minecraft.world.level.levelgen.placement.PlacedFeature
import net.minecraft.world.level.levelgen.placement.PlacementModifier
import redcrafter07.processed.ProcessedMod

object ModPlacedFeatures {
    val OVERWORLD_BLITZ_ORE_PLACED_KEY = registerKey("sapphire_ore_placed");

    fun bootstrap(context: BootstapContext<PlacedFeature>) {
        val configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);

        register(
            context,
            OVERWORLD_BLITZ_ORE_PLACED_KEY,
            configuredFeatures.getOrThrow(ModConfiguredFeatures.OVERWORLD_BLITZ_ORE_KEY),
            ModOrePlacement.commonOrePlacement(
                8,
                HeightRangePlacement.uniform(VerticalAnchor.absolute(8), VerticalAnchor.absolute(30))
            )
        );
    }

    fun registerKey(name: String): ResourceKey<PlacedFeature> {
        return ResourceKey.create(Registries.PLACED_FEATURE, ResourceLocation(ProcessedMod.ID, name));
    }

    private fun register(
        context: BootstapContext<PlacedFeature>,
        key: ResourceKey<PlacedFeature>,
        configuration: Holder<ConfiguredFeature<*, *>>,
        modifiers: List<PlacementModifier>,
    ) {
        context.register(key, PlacedFeature(configuration, modifiers));
    }
}