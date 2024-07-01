package redcrafter07.processed.item;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

import java.util.function.IntFunction;

public enum WrenchMode implements StringRepresentable {
    Config(0, "config"), Rotate(1, "rotate");

    WrenchMode(int id, String modeName) {
        this.id = id;
        this.modeName = modeName;
    }

    private final int id;
    private final String modeName;

    public final static IntFunction<WrenchMode> BY_ID = ByIdMap.continuous(WrenchMode::getId, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
    public final static EnumCodec<WrenchMode> CODEC = StringRepresentable.fromEnum(WrenchMode::values);
    public final static StreamCodec<ByteBuf, WrenchMode> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, WrenchMode::getId);

    @Override
    public String getSerializedName() {
        return modeName;
    }

    public int getId() {
        return id;
    }

    public WrenchMode next() {
        return BY_ID.apply(getId() + 1);
    }

    public WrenchMode previous() {
        return BY_ID.apply(getId() - 1);
    }

    public String translation() {
        return "item.processed.wrench.mode." + modeName;
    }
}