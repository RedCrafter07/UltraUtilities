package redcrafter07.processed.mixins

import org.objectweb.asm.tree.ClassNode
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin
import org.spongepowered.asm.mixin.extensibility.IMixinInfo

class ProcessedMixinPlugin : IMixinConfigPlugin {
    override fun onLoad(mixinPackage: String?) {}

    override fun getRefMapperConfig(): String {
        return ""
    }

    override fun shouldApplyMixin(targetClassName: String?, mixinClassName: String?): Boolean {
        return true
    }

    override fun acceptTargets(myTargets: MutableSet<String>?, otherTargets: MutableSet<String>?) {

    }

    override fun getMixins(): MutableList<String> {
        return arrayOf("PlayerSkinMixing").toMutableList()
    }

    override fun preApply(
        targetClassName: String?,
        targetClass: ClassNode?,
        mixinClassName: String?,
        mixinInfo: IMixinInfo?
    ) {
    }

    override fun postApply(
        targetClassName: String?,
        targetClass: ClassNode?,
        mixinClassName: String?,
        mixinInfo: IMixinInfo?
    ) {
    }
}