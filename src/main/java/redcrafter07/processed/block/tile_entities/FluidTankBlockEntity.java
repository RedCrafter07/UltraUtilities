package redcrafter07.processed.block.tile_entities;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;
import redcrafter07.processed.block.machine_abstractions.BlockSide;
import redcrafter07.processed.block.machine_abstractions.FluidCapableBlockEntity;
import redcrafter07.processed.block.tile_entities.capabilities.SimpleFluidStore;
import redcrafter07.processed.gui.RenderUtils;

public class FluidTankBlockEntity extends BlockEntity implements FluidCapableBlockEntity {
    protected final SimpleFluidStore fluidHandler = new SimpleFluidStore(1, 80000);


    public FluidTankBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModTileEntities.FLUID_TANK.get(), pos, blockState);
        fluidHandler.setOnChange(this::sync);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    public void sync() {
        if (this.level instanceof ServerLevel) {
            final BlockState state = getBlockState();
            this.level.sendBlockUpdated(this.worldPosition, state, state, 3);
            setChanged();
        }
    }

    @Nullable
    public ItemInteractionResult useItemOn(ItemStack stack, Player player, InteractionHand hand) {
        final var fluidCapability = stack.getCapability(Capabilities.FluidHandler.ITEM);
        if (fluidCapability == null) return null;
        var capacity = fluidHandler.getTankCapacity(0);
        var fluid = fluidHandler.getFluidInTank(0);
        boolean extraction_success = false;
        if (fluid.isEmpty()) {
            var extracted = fluidCapability.drain(capacity, IFluidHandler.FluidAction.EXECUTE);
            if (extracted.getAmount() > 0 && !extracted.isEmpty()) {
                fluidHandler.setFluidInTank(0, extracted);
                extraction_success = true;
            }
        } else {
            var extracted = fluidCapability.drain(fluid.copyWithAmount(capacity - fluid.getAmount()), IFluidHandler.FluidAction.SIMULATE);
            if (extracted.getAmount() <= capacity - fluid.getAmount() && extracted.getAmount() > 0 && !extracted.isEmpty()) {
                fluidHandler.setFluidInTank(0, fluid.copyWithAmount(fluid.getAmount() + extracted.getAmount()));
                extraction_success = true;
                fluidCapability.drain(fluid.copyWithAmount(capacity - fluid.getAmount()), IFluidHandler.FluidAction.EXECUTE);
            }
        }
        if (extraction_success) {
            if (!player.isCreative()) {
                ItemStack container = fluidCapability.getContainer();
                if (!container.isEmpty()) {
                    if (stack.getCount() == 1) player.setItemInHand(hand, container);
                    else if (player.getInventory().add(container)) stack.shrink(1);
                } else {
                    stack.shrink(1);
                    if (stack.isEmpty()) player.setItemInHand(hand, ItemStack.EMPTY);
                }
            }
            return ItemInteractionResult.SUCCESS;
        }

        var filled = fluidCapability.fill(fluid, IFluidHandler.FluidAction.EXECUTE);
        if (filled > 0) {
            if (fluid.getAmount() - filled <= 0) fluidHandler.setFluidInTank(0, FluidStack.EMPTY);
            else fluidHandler.setFluidInTank(0, fluid.copyWithAmount(fluid.getAmount() - filled));
            ItemStack container = fluidCapability.getContainer();
            if (!player.isCreative()) {
                if (stack.getCount() == 1) player.setItemInHand(hand, container);
                else if (stack.getCount() > 1 && !player.getInventory().add(container)) stack.shrink(1);
                else {
                    player.drop(container, false, true);
                    stack.shrink(1);
                }
            }
            return ItemInteractionResult.SUCCESS;
        }
        return null;
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        fluidHandler.deserializeNBT(registries, tag.getCompound("FluidStorage"));
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        tag.put("FluidStorage", fluidHandler.serializeNBT(registries));
    }

    @Nullable
    @Override
    public IFluidHandler fluidCapabilityForSide(@Nullable BlockSide side, BlockState state) {
        return fluidHandler;
    }

    private float getSize() {
        return ((float) fluidHandler.getFluidInTank(0).getAmount()) / ((float) fluidHandler.getTankCapacity(0));
    }

    public static class FluidTankEntityRenderer implements BlockEntityRenderer<FluidTankBlockEntity> {
        public FluidTankEntityRenderer(BlockEntityRendererProvider.Context ctx) {
        }

        @Override
        public void render(FluidTankBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
            final var fluid = blockEntity.fluidHandler.getFluidInTank(0);
            if (fluid.isEmpty()) return;
            var sprite = RenderUtils.getFluidTexture(fluid, false);
            int color = RenderUtils.getFluidColor(fluid);
            float alpha = 1f;
            float red = (color >> 16 & 0xff) / 255.0f;
            float green = (color >> 8 & 0xff) / 255.0f;
            float blue = (color & 0xff) / 255.0f;
            float height = 0.75f * blockEntity.getSize() + 0.125f;
            var buffer = bufferSource.getBuffer(Sheets.translucentCullBlockSheet());

            poseStack.pushPose();
            poseStack.translate(0d, 0d, 0d);

            float xMin = 0.1875f;
            float zMin = 0.1875f;
            float xMax = 0.8125f;
            float zMax = 0.8125f;
            float yMin = 0.125f;

            RenderUtils.renderCube(buffer, poseStack, xMax, xMin, yMin, height, zMin, zMax, sprite, red, green, blue, alpha, packedLight, packedOverlay);

            poseStack.popPose();
        }
    }
}
