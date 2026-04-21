package io.github.fishstiz.minecraftcursor;

import io.github.fishstiz.minecraftcursor.config.Flag;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.LoadingModList;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class NeoForgeMixinConfigPlugin implements IMixinConfigPlugin {
    private boolean applyCursorRemapping;

    @Override
    public void onLoad(String mixinPackage) {
        // do nothing
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (!FMLEnvironment.dist.isClient()) {
            return false;
        }

        final String mixinPackage = "io.github.fishstiz.minecraftcursor.mixin";

        if (mixinClassName.startsWith(mixinPackage + ".compat.ftblibrary")) {
            boolean loaded = isModLoaded("ftblibrary");
            if (loaded) this.applyCursorRemapping = true;
            return loaded;
        }
        if (mixinClassName.startsWith(mixinPackage + ".compat.owo")) {
            boolean loaded = isModLoaded("owo");
            if (loaded) this.applyCursorRemapping = true;
            return loaded;
        }
        if (mixinClassName.startsWith(mixinPackage + ".compat.veil")) {
            return isModLoaded("veil");
        }

        return false;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
        // do nothing
    }

    @Override
    public List<String> getMixins() {
        if (!this.applyCursorRemapping) {
            Flag.REMAP.disable();
        }

        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        // do nothing
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        // do nothing
    }

    private static boolean isModLoaded(String modId) {
        return LoadingModList.get().getModFileById(modId) != null;
    }
}
