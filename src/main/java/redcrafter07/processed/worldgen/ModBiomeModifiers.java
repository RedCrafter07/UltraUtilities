package redcrafter07.processed.worldgen;

import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import redcrafter07.processed.ProcessedMod;

public class ModBiomeModifiers {
    public static final ResourceKey<BiomeModifier> ADD_BLITZ_ORE = registerKey("add_blitz_ore");

    public static void bootstrap(BootstrapContext<BiomeModifier> context) {
        final var placedFeatures = context.lookup(Registries.PLACED_FEATURE);
        final var biomes = context.lookup(Registries.BIOME);

        context.register(
                ADD_BLITZ_ORE, new BiomeModifiers.AddFeaturesBiomeModifier(
                        biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                        HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.OVERWORLD_BLITZ_ORE_PLACED_KEY)),
                        GenerationStep.Decoration.UNDERGROUND_ORES
                )
        );
    }

    private static ResourceKey<BiomeModifier> registerKey(String name) {
        return ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, ProcessedMod.rl(name));
    }
}