package redcrafter07.processed.worldgen

import net.minecraft.core.registries.Registries
import net.minecraft.data.worldgen.BootstapContext
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.BlockTags
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature
import net.minecraft.world.level.levelgen.feature.Feature
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest
import redcrafter07.processed.ProcessedMod
import redcrafter07.processed.block.ModBlocks

object ModConfiguredFeatures {
    val OVERWORLD_BLITZ_ORE_KEY = registerKey("blitz_ore");

    fun bootstrap(context: BootstapContext<ConfiguredFeature<*, *>>) {
        val stoneReplaceable = TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES);

        register(
            context,
            OVERWORLD_BLITZ_ORE_KEY,
            Feature.ORE,
            OreConfiguration(stoneReplaceable, ModBlocks.BLITZ_ORE.get().defaultBlockState(), 3)
        );
    }

    fun registerKey(name: String): ResourceKey<ConfiguredFeature<*, *>> {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, ResourceLocation(ProcessedMod.ID, name));
    }

    private fun <FC : FeatureConfiguration, F : Feature<FC>> register(
        context: BootstapContext<ConfiguredFeature<*, *>>,
        key: ResourceKey<ConfiguredFeature<*, *>>,
        feature: F,
        configuration: FC
    ) {
        context.register(key, ConfiguredFeature(feature, configuration));
    }
}