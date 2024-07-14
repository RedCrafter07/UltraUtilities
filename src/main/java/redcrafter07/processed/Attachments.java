package redcrafter07.processed;

import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import redcrafter07.processed.multiblock.MultiblockCasingCache;

import java.util.function.Supplier;

public class Attachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, ProcessedMod.ID);

    public static final Supplier<AttachmentType<MultiblockCasingCache>> MULTIBLOCK_CHUNK_ATTACHMENT =
            ATTACHMENT_TYPES.register("multiblock_chunk_attachment", () -> AttachmentType.builder(MultiblockCasingCache::new).build());
}
