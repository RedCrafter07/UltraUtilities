package redcrafter07.processed.mixins;

import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerSkin.class)
abstract class PlayerSkinMixin {
    @Inject(
            at = @At("HEAD"),
            method = {"capeTexture", "elytraTexture"}
    )
    public void processed$capeTexture(CallbackInfoReturnable<ResourceLocation> cir) {
        // TODO: Milestone Capes :3
    }
}