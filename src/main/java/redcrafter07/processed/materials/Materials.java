package redcrafter07.processed.materials;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import redcrafter07.processed.gui.RenderUtils;

import java.util.ArrayList;

public class Materials {
    public static ArrayList<Material> MATERIALS = new ArrayList<>();

    public static Material ALUMINIUM = register(new Material(
            "aluminium",
            RenderUtils.color(0xd0, 0xd5, 0xd9),
            "Al",
            BlockTags.NEEDS_STONE_TOOL,
            BlockBehaviour.Properties.of().instrument(NoteBlockInstrument.BASEDRUM).strength(5, 10).requiresCorrectToolForDrops(),
            BlockBehaviour.Properties.of().sound(SoundType.STONE).strength(5, 10).requiresCorrectToolForDrops()
    ));
    public static Material STEEL = register(new Material(
            "steel",
            RenderUtils.color(0x49, 0x4b, 0x4d),
            "Fe",
            BlockTags.NEEDS_IRON_TOOL,
            BlockBehaviour.Properties.of().instrument(NoteBlockInstrument.BASEDRUM).strength(5, 10).requiresCorrectToolForDrops(),
            BlockBehaviour.Properties.of().sound(SoundType.STONE).strength(5, 10).requiresCorrectToolForDrops()
    ));
    public static Material NICKEL = register(new Material(
            "nickel",
            RenderUtils.color(0xd4, 0xd4, 0xa9),
            "Ni",
            BlockTags.NEEDS_IRON_TOOL,
            BlockBehaviour.Properties.of().instrument(NoteBlockInstrument.BASEDRUM).strength(5, 10).requiresCorrectToolForDrops(),
            BlockBehaviour.Properties.of().sound(SoundType.STONE).strength(5, 10).requiresCorrectToolForDrops()
    ));
    public static Material TITANIUM = register(new Material(
            "titanium",
            RenderUtils.color(0xcf, 0x71, 0xaf),
            "Ti",
            BlockTags.NEEDS_DIAMOND_TOOL,
            BlockBehaviour.Properties.of().instrument(NoteBlockInstrument.BASEDRUM).strength(5, 10).requiresCorrectToolForDrops(),
            BlockBehaviour.Properties.of().sound(SoundType.STONE).strength(5, 10).requiresCorrectToolForDrops()
    ));

    public static Material register(Material material) {
        MATERIALS.add(material);
        return material;
    }
}
