package io.github.fishstiz.minecraftcursor;

import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class FabricMixinConfigPlugin implements IMixinConfigPlugin {
    private static final Logger LOGGER = LoggerFactory.getLogger("minecraft-cursor");
    private final String[] modsEarlyLoadingGLFW = {"earlyloadingscreen"};

    private boolean isGLFWEarlyLoaded() {
        for (String modId : modsEarlyLoadingGLFW) {
            if (FabricLoader.getInstance().isModLoaded(modId)) {
                return true;
            }
        }
        return false;
    }

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
        return false;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
        // Do nothing
    }

    @Override
    public List<String> getMixins() {
        if (!isGLFWEarlyLoaded()) return List.of("compat.glfw.GlfwMixin");

        String infoLink = "https://github.com/fishstiz/minecraft-cursor/blob/mc/1.21.4/README.md#fabric-only-features";
        LOGGER.error("[minecraft-cursor] Fabric-only compatibility features could not be applied due to one of these mods: {}", (Object) modsEarlyLoadingGLFW);
        LOGGER.error("[minecraft-cursor] Learn more about the Fabric-only compatibility features: {}", infoLink);
        return List.of();
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
