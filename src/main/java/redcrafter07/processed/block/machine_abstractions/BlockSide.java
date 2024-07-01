package redcrafter07.processed.block.machine_abstractions;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import org.joml.Vector2i;

import java.util.List;
import java.util.function.IntFunction;

public enum BlockSide implements StringRepresentable {
    Top(0, "top"),
    Bottom(1, "bottom"),
    Left(2, "left"),
    Right(3, "right"),
    Front(4, "front"),
    Back(5, "back");

    private final int id;
    private final String stateName;

    BlockSide(int id, String stateName) {
        this.id = id;
        this.stateName = stateName;
    }

    // translation: IoSide <=> Direction
// Down:    Bottom
// Top:     Up
// Front:   North
// Back:    South
// Left:    East
// Right:   West

    // Direction translation Matrix. Do LOOKUP[Facing.3dData][Direction.3dData] and you get the "real" direction! (its like Direction + Direction)
    static final List<List<Direction>> DIRECTION_LOOKUP =
            List.of(
                    List.of(
                            // ---{ Facing: Down }---
                            Direction.NORTH, // Bottom: Front (North)
                            Direction.SOUTH, // Top: Back (South)
                            Direction.UP,    // North: Up (Up)
                            Direction.DOWN,  // South: Down (Down)
                            Direction.WEST,  // West: Right (West)
                            Direction.EAST   // East: Left (East)
                    ),

                    List.of(
                            // ---{ Facing: Up }---
                            Direction.SOUTH, // Bottom: Back (South)
                            Direction.NORTH, // Top: Front (North)
                            Direction.DOWN,  // North: Down (Down)
                            Direction.UP,    // South: Up (Up)
                            Direction.WEST,  // West: Right (West)
                            Direction.EAST   // East: Left (East)
                    ),

                    List.of(
                            // ---{ Facing: North }---
                            Direction.DOWN,  // Bottom: Down (Down)
                            Direction.UP,    // Top: Top (Up)
                            Direction.NORTH, // North: Front (North)
                            Direction.SOUTH, // South: Back (South)
                            Direction.WEST,  // West: Right (West)
                            Direction.EAST   // East: Left (East)
                    ),

                    List.of(
                            // ---{ Facing: South }---
                            Direction.DOWN,  // Bottom: Down (Down)
                            Direction.UP,    // Top: Up (Up)
                            Direction.SOUTH, // North: Back (South)
                            Direction.NORTH, // South: Front (North)
                            Direction.EAST,  // West: Left (East)
                            Direction.WEST  // East: Right (West)
                    ),

                    List.of(
                            // ---{ Facing: West }---
                            Direction.DOWN,  // Bottom: Down (Down)
                            Direction.UP,    // Top: Up (Up)
                            Direction.EAST,  // North: Left (East)
                            Direction.WEST,  // South: Right (West)
                            Direction.NORTH, // West: Front (North)
                            Direction.SOUTH // East: Back (South)
                    ),

                    List.of(
                            // ---{ Facing: East }---
                            Direction.DOWN,  // Bottom: Down (Down)
                            Direction.UP,    // Top: Up (Up)
                            Direction.WEST,  // North: Left (West)
                            Direction.EAST,  // South: Right (East)
                            Direction.SOUTH, // West: Back (South)
                            Direction.NORTH // East: Front (North)
                    )
            );

    static BlockSide fromDirection(Direction direction) {
        return switch (direction) {
            case UP -> Top;
            case DOWN -> Bottom;
            case NORTH -> Front;
            case SOUTH -> Back;
            case WEST -> Right;
            case EAST -> Left;
        };
    }

    static BlockSide getFacing(Direction machineFacing, Direction direction) {
        return fromDirection(DIRECTION_LOOKUP.get(machineFacing.get3DDataValue()).get(direction.get3DDataValue()));
    }

    private static final List<DirectionProperty> facingProperties = List.of(DirectionalBlock.FACING,
            HorizontalDirectionalBlock.FACING,
            BlockStateProperties.HORIZONTAL_FACING,
            BlockStateProperties.FACING);

    public static BlockSide translateDirection(Direction direction, BlockState state) {
        for (var property : facingProperties) {
            if (state.hasProperty(property)) return BlockSide.getFacing(state.getValue(property), direction);
        }
        return BlockSide.fromDirection(direction);
    }

    static final BlockSide DEFAULT = Front;


//        val BY_ID = ByIdMap.continuous(BlockSide::getId, entries.toTypedArray(), ByIdMap.OutOfBoundsStrategy.WRAP)
//
//        val CODEC = StringRepresentable.fromEnum(BlockSide::values)
//        val STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, BlockSide::getId)
//    }

    public static final IntFunction<BlockSide> BY_ID = ByIdMap.continuous(BlockSide::getId, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
    public static final EnumCodec<BlockSide> CODEC = StringRepresentable.fromEnum(BlockSide::values);
    public static final StreamCodec<ByteBuf, BlockSide> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, BlockSide::getId);

    @Override
    public String getSerializedName() {
        return stateName;
    }

    public int getId() {
        return id;
    }

    public Vector2i getButtonPos() {
        return switch (this) {
            case Top -> new Vector2i(31, 30);
            case Bottom -> new Vector2i(31, 72);
            case Left -> new Vector2i(10, 51);
            case Front -> new Vector2i(31, 51);
            case Right -> new Vector2i(52, 51);
            case Back -> new Vector2i(73, 51);
        };
    }

    public Component toComponent() {
        return Component.translatable("processed.side." + stateName);
    }
}
