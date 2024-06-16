package redcrafter07.processed.block

import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component

class ProcessedTier(val tier: Int, val multiplier_speed: Int, val multiplier_energy: Int) {
    fun named(): String {
        return "tier_$tier"; }

    fun translated(): Component {
        return Component.translatable("processed." + named())
    }

    fun save(name: String, nbt: CompoundTag) {
        val tag = CompoundTag()
        tag.putInt("tier", tier)
        tag.putInt("speed_mul", multiplier_speed)
        tag.putInt("energy_mul", multiplier_energy)
        nbt.put(name, tag)
    }

    companion object {
        fun load(name: String, nbt: CompoundTag): ProcessedTier {
            val tag = nbt.get(name)
            if (tag !is CompoundTag) return ProcessedTier(1, 1, 1)
            return ProcessedTier(tag.getInt("tier"), tag.getInt("speed_mul"), tag.getInt("energy_mul"))
        }
    }
}

object ProcessedTiers {
    val machine = setOf(
        ProcessedTier(1, 1, 1),
        ProcessedTier(2, 2, 2),
        ProcessedTier(3, 3, 2),
    )
}