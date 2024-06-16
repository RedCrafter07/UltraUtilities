package redcrafter07.processed.item

import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.util.ByIdMap
import net.minecraft.util.StringRepresentable

enum class WrenchMode(private val id: Int, private val modeName: String) : StringRepresentable {
    Config(0, "config"), Rotate(1, "rotate");

    companion object {
        val BY_ID = ByIdMap.continuous(WrenchMode::getId, entries.toTypedArray(), ByIdMap.OutOfBoundsStrategy.ZERO)

        val CODEC = StringRepresentable.fromEnum(WrenchMode::values)
        val STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, WrenchMode::getId)
    }

    override fun getSerializedName(): String {
        return modeName
    }

    fun getId(): Int {
        return id
    }

    fun next(): WrenchMode {
        return BY_ID.apply(this.getId() + 1)
    }

    fun previous(): WrenchMode {
        return BY_ID.apply(this.getId() - 1)
    }

    fun translation(): String {
        return "item.processed.wrench.mode.$modeName"
    }
}