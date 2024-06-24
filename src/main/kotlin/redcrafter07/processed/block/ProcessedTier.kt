package redcrafter07.processed.block

import net.minecraft.network.chat.Component

class ProcessedTier(val tier: Int, val multiplierSpeed: Int, val multiplierEnergy: Int) {
    fun named(): String {
        return "tier_$tier"; }

    fun translated(): Component {
        return Component.translatable("processed.${named()}")
    }

    fun colored(): Component {
        return Component.translatable("processed.${named()}.colored")
    }

    fun getMaxPower(): Int {
        return multiplierEnergy * 32
    }
}

object ProcessedTiers {
    val machine = listOf(
        ProcessedTier(0, 1, 1), // Rudimentary
        ProcessedTier(1, 3, 4), // Basic
        ProcessedTier(2, 9, 16), // Advanced
        ProcessedTier(3, 27, 64), // Void
        ProcessedTier(4, 81, 256), // Nuclear
        ProcessedTier(5, 243, 1024), // Quantum
        ProcessedTier(6, 729, 4096), // Final
    )
}