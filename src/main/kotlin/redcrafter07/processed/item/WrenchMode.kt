package redcrafter07.processed.item

enum class WrenchMode {
    Config, Rotate;

    fun next(): WrenchMode {
        return when (this) {
            Config -> Rotate
            Rotate -> Config
        }
    }

    fun previous(): WrenchMode {
        return when (this) {
            Config -> Rotate
            Rotate -> Config
        }
    }

    fun translation(): String {
        val prefix = "item.processed.wrench.mode"
        val suffix = when (this) {
            Config -> "config"
            Rotate -> "rotate"
        }

        return "$prefix.$suffix"
    }

    fun save(): Byte {
        return when (this) {
            Config -> 0.toByte()
            Rotate -> 2.toByte()
            else -> 0.toByte()
        }
    }

    companion object {
        fun load(value: Byte): WrenchMode {
            return when (value.toInt()) {
                0 -> Config
                2 -> Rotate
                else -> Config
            }
        }
    }
}