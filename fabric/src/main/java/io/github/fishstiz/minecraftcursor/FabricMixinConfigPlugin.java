package io.github.fishstiz.minecraftcursor;

import io.github.fishstiz.minecraftcursor.config.Flag;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.impl.launch.FabricLauncherBase;
import org.objectweb.asm.tree.ClassNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class FabricMixinConfigPlugin implements IMixinConfigPlugin {
    private static final Logger LOGGER = LoggerFactory.getLogger("minecraft-cursor | FabricMixinConfigPlugin");

    @Override
    public void onLoad(String mixinPackage) {
        // Do nothing
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (mixinClassName.startsWith("io.github.fishstiz.minecraftcursor.mixin.compat.veil")) {
            return FabricLoader.getInstance().isModLoaded("veil");
        }

        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
        // Do nothing
    }

    @Override
    public List<String> getMixins() {
        if (FabricLoader.getInstance().getEnvironmentType() != EnvType.CLIENT) {
            return null;
        }

        if (FabricLauncherBase.getLauncher().isClassLoaded("org.lwjgl.glfw.GLFW")) {
            LOGGER.warn("[minecraft-cursor] GLFWMixin could not be applied as it has been loaded early. Fabric compatibility features have been disabled.");
            Flag.REMAP.disable();
            return null;
        }

        return List.of("compat.glfw.GLFWMixin");
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        // Do nothing
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        // Do nothing
    }
}
