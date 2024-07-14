package redcrafter07.processed.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkAccess;
import redcrafter07.processed.Attachments;

import javax.annotation.Nullable;
import java.util.HashMap;

public class MultiblockCasingCache {
    public static MultiblockCasingCache getOrDefault(ChunkAccess access) {
        return access.getData(Attachments.MULTIBLOCK_CHUNK_ATTACHMENT);
    }

    public static @Nullable MultiblockCasingCache get(ChunkAccess access) {
        if (!access.hasData(Attachments.MULTIBLOCK_CHUNK_ATTACHMENT)) return null;
        return access.getData(Attachments.MULTIBLOCK_CHUNK_ATTACHMENT);
    }

    private static void remove(ChunkAccess access) {
        access.removeData(Attachments.MULTIBLOCK_CHUNK_ATTACHMENT);
    }

    public void set(ChunkAccess access) {
        if (this.multiblockMap.isEmpty()) remove(access);
        else access.setData(Attachments.MULTIBLOCK_CHUNK_ATTACHMENT, this);
    }

    public static @Nullable BlockPos getControllerForCasing(LevelAccessor level, BlockPos casing) {
        ChunkAccess chunkAccess = level.getChunk(SectionPos.blockToSectionCoord(casing.getX()), SectionPos.blockToSectionCoord(casing.getZ()));
        MultiblockCasingCache cache = get(chunkAccess);
        if (cache != null && cache.multiblockMap.containsKey(casing)) return cache.multiblockMap.get(casing);
        return null;
    }

    public static void removeCasing(LevelAccessor level, BlockPos casing) {
        ChunkAccess chunkAccess = level.getChunk(SectionPos.blockToSectionCoord(casing.getX()), SectionPos.blockToSectionCoord(casing.getZ()));
        MultiblockCasingCache cache = get(chunkAccess);
        if (cache == null) return;
        if (cache.multiblockMap.containsKey(casing)) {
            cache.multiblockMap.remove(casing);
            cache.set(chunkAccess);
        }
    }

    public static void setControllerForCasing(LevelAccessor level, BlockPos casing, BlockPos controller) {
        ChunkAccess chunkAccess = level.getChunk(SectionPos.blockToSectionCoord(casing.getX()), SectionPos.blockToSectionCoord(casing.getZ()));
        MultiblockCasingCache cache = getOrDefault(chunkAccess);
        cache.multiblockMap.put(casing, controller);
        cache.set(chunkAccess);
    }

    private final HashMap<BlockPos, BlockPos> multiblockMap;

    public MultiblockCasingCache() {
        multiblockMap = new HashMap<>();
    }

    public HashMap<BlockPos, BlockPos> multiblockMap() {
        return multiblockMap;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("{\n");
        for (var entry : multiblockMap.entrySet()) {
            builder.append("    ");
            builder.append(entry.getKey());
            builder.append(" => ");
            builder.append(entry.getValue());
            builder.append(",\n");
        }
        builder.append('}');
        return builder.toString();
    }
}
