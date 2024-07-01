package redcrafter07.processed.worldgen;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import redcrafter07.processed.ProcessedMod;
import redcrafter07.processed.block.ModBlocks;

public class ModConfiguredFeatures {
    public static final ResourceKey<ConfiguredFeature<?, ?>> OVERWORLD_BLITZ_ORE_KEY = registerKey("blitz_ore");

    public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        final var stoneReplaceable = new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES);

        register(
                context,
                OVERWORLD_BLITZ_ORE_KEY,
                Feature.ORE,
                new OreConfiguration(stoneReplaceable, ModBlocks.BLITZ_ORE.get().defaultBlockState(), 3)
        );
    }

    private static ResourceKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, ProcessedMod.rl(name));
    }

    private static <FC extends FeatureConfiguration, F extends Feature<FC>> void register(
            BootstrapContext<ConfiguredFeature<?, ?>> context,
            ResourceKey<ConfiguredFeature<?, ?>> key,
            F feature,
            FC configuration
    ) {
        context.register(key, new ConfiguredFeature<>(feature, configuration));
    }
}