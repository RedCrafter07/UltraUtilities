package redcrafter07.processed.block.machine_abstractions;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

import java.util.function.IntFunction;

public enum IoState implements StringRepresentable {
    None(0, "none"),
    Input(1, "input"),
    Output(2, "output"),
    InputOutput(3, "input_output"),
    Additional(4, "additional"),
    Extra(5, "extra");

    IoState(int id, String stateName) {
        this.id = id;
        this.stateName = stateName;
    }

    private final int id;
    private final String stateName;

    public static final IntFunction<IoState> BY_ID = ByIdMap.continuous(IoState::getId, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
    public static final EnumCodec<IoState> CODEC = StringRepresentable.fromEnum(IoState::values);
    public static final StreamCodec<ByteBuf, IoState> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, IoState::getId);

    @Override
    public String getSerializedName() {
        return stateName;
    }

    @Override
    public String toString() {
        return stateName;
    }

    public int getId() {
        return id;
    }

    public IoState next() {
        return BY_ID.apply(getId() + 1);
    }

    public IoState previous() {
        return BY_ID.apply(getId() - 1);
    }

    public Component toComponent() {
        return Component.translatable("processed.io_state." + stateName);
    }
}
