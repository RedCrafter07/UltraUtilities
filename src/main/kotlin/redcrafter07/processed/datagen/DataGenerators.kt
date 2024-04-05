package redcrafter07.processed.datagen

import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.data.event.GatherDataEvent
import redcrafter07.processed.ProcessedMod

@Mod.EventBusSubscriber(modid = ProcessedMod.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
object DataGenerators {
    @SubscribeEvent
    fun gatherData(event: GatherDataEvent) {
        val generator = event.generator;
        val packOutput = generator.packOutput;
        val lookupProvider = event.lookupProvider;

        generator.addProvider(event.includeServer(), ModWorldGenProvider(packOutput, lookupProvider));
    }
}