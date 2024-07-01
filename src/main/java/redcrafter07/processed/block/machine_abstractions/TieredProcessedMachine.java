package redcrafter07.processed.block.machine_abstractions;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class TieredProcessedMachine
        extends
        ProcessedMachine {

    private ProcessedTier tier = ProcessedTier.DEFAULT;

    public TieredProcessedMachine(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    public ProcessedTier getTier() {
        return tier;
    }

    public void setTier(ProcessedTier newTier) {
        final var oldTier = tier;
        tier = newTier;
        onTierChanged(oldTier, tier);
    }


    @Override
    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider provider) {
        super.loadAdditional(nbt, provider);
        tier = ProcessedTier.TIERS.get(Math.min(Math.max(nbt.getInt("machine_tier"), 0), ProcessedTier.TIERS.size() - 1));
    }

    @Override
    public void saveAdditional(CompoundTag nbt, HolderLookup.Provider provider) {
        super.saveAdditional(nbt, provider);
        nbt.putInt("machine_tier", tier.tier());
    }

    /**
     * Gets called when the tier gets changed.
     * <p>
     * NOTE: **DOES NOT** get called on construction, so if you want that code to execute, call it yourself on construction!
     */
    protected void onTierChanged(ProcessedTier oldTier, ProcessedTier newTier) {
    }

    protected void useScaledEnergyCapability(int capacity) {
        useEnergyCapability(tier.getScaledPower(capacity), tier.getMaxPower());
    }

    protected void useScaledOutputEnergyCapability(int capacity) {
        useOutputEnergyCapability(tier.getScaledPower(capacity), tier.getMaxPower());
    }

    protected boolean useScaledPower(int amps) {
        return usePower(tier.getPowerUsageForAmp(amps));
    }
}
