package redcrafter07.processed.multiblock;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.HashMap;
import java.util.Map;

public enum MultiblockPart {
    Ignored,
    Empty,
    Casing,
    Controller;

    public static Map<Block, MultiblockPart> map(Block wall, Block controller) {
        final Map<Block, MultiblockPart> map = new HashMap<>();

        map.put(wall, MultiblockPart.Casing);
        map.put(controller, MultiblockPart.Controller);
        map.put(Blocks.AIR, MultiblockPart.Empty);

        return map;
    }
}
