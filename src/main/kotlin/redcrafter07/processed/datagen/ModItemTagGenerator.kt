package redcrafter07.processed.datagen

import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.data.tags.ItemTagsProvider
import net.minecraft.tags.ItemTags
import net.minecraft.world.level.block.Block
import net.neoforged.neoforge.common.Tags
import net.neoforged.neoforge.common.data.ExistingFileHelper
import redcrafter07.processed.ProcessedMod
import redcrafter07.processed.item.ModItems
import redcrafter07.processed.tags.ProcessedTags
import java.util.concurrent.CompletableFuture

class ModItemTagGenerator(
    p_275343_: PackOutput,
    p_275729_: CompletableFuture<HolderLookup.Provider>,
    p_275322_: CompletableFuture<TagLookup<Block>>,
    existingFileHelper: ExistingFileHelper?
) : ItemTagsProvider(p_275343_, p_275729_, p_275322_, ProcessedMod.ID, existingFileHelper) {
    override fun addTags(p0: HolderLookup.Provider) {
        this.tag(Tags.Items.INGOTS)
            .add(ModItems.BLITZ_ORB.get());

        this.tag(ProcessedTags.Items.FORGE_INGOT_BLITZ)
            .add(ModItems.BLITZ_ORB.get());
    }
}