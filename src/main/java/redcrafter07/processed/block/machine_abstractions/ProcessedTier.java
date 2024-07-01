package redcrafter07.processed.block.machine_abstractions;

import net.minecraft.network.chat.Component;

import java.util.List;

public record ProcessedTier(int tier, int multiplierSpeed, int multiplierEnergy) {
    public String named() {
        return "tier_" + tier;
    }

    public Component translated() {
        return Component.translatable("processed." + named());
    }

    public Component colored() {
        return Component.translatable("processed." + named() + ".colored");
    }

    public int getMaxPower() {
        return multiplierEnergy * 32;
    }

    public int getPowerUsageForAmp(int amp) {
        if (amp < 1) return 8 * multiplierEnergy;
        if (amp > 4) return 32 * multiplierEnergy;
        return 8 * amp * multiplierEnergy;
    }

    /**
     * For things like energy storages
     */
    public int getScaledPower(int defaultPower) {
        return 8 * multiplierEnergy * defaultPower;
    }

    public static List<ProcessedTier> TIERS = List.of(
            new ProcessedTier(0, 1, 1), // Rudimentary
            new ProcessedTier(1, 3, 4), // Basic
            new ProcessedTier(2, 9, 16), // Advanced
            new ProcessedTier(3, 27, 64), // Void
            new ProcessedTier(4, 81, 256), // Nuclear
            new ProcessedTier(5, 243, 1024), // Quantum
            new ProcessedTier(6, 729, 4096) // Final
    );
    public static ProcessedTier DEFAULT = new ProcessedTier(-1, 0, 0);
}