package io.github.fishstiz.minecraftcursor.config.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class FabricMixinConfigPlugin implements IMixinConfigPlugin {
    private static final Logger LOGGER = LoggerFactory.getLogger("minecraft-cursor | FabricMixinConfigPlugin");
    private final String[] modsEarlyLoadingGLFW = {"earlyloadingscreen"};

    private record GlfwMod(String modId, boolean isLoaded) {
    }

    private GlfwMod isGLFWModLoaded() {
        for (String modId : modsEarlyLoadingGLFW) {
            if (FabricLoader.getInstance().isModLoaded(modId)) {
                return new GlfwMod(modId, true);
            }
        }
        return new GlfwMod(Arrays.toString(modsEarlyLoadingGLFW), false);
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
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
        // Do nothing
    }

    @Override
    public List<String> getMixins() {
        if (FabricLoader.getInstance().getEnvironmentType() != EnvType.CLIENT) return null;

        final var props = new MixinConfigProperties(LOGGER, FabricLoader.getInstance().getConfigDir());
        List<String> mixins = new ArrayList<>();

        GlfwMod glfwMod = isGLFWModLoaded();
        if (props.forceDisableGlfw.value()) {
            LOGGER.warn("[minecraft-cursor] Property set: {}. Disabling cursor tracking for other mods...", props.forceDisableGlfw.key());
        } else if (props.ignoreModCheckGlfw.value() || !glfwMod.isLoaded()) {
            if (props.ignoreModCheckGlfw.value()) {
                LOGGER.warn("[minecraft-cursor] Property set: {}. Enabling cursor tracking for other mods. May crash game due to: {}", props.ignoreModCheckGlfw.key(), glfwMod.modId);
            } else {
                LOGGER.info("[minecraft-cursor] Enabling cursor tracking for other mods... If game crashes, report issue and set '{}' to true in '{}' as workaround (disables cursor tracking).", props.forceDisableGlfw.key(), props.file);
            }

            mixins.add("compat.glfw.GlfwMixin");
        } else {
            LOGGER.warn("[minecraft-cursor] Cursor tracking could not be enabled due to: {}. Compatibility issues may occur. To ignore this check, set '{}' to true in '{}'.", glfwMod.modId, props.ignoreModCheckGlfw.key(), props.file);
        }

        return !mixins.isEmpty() ? mixins : null;
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
