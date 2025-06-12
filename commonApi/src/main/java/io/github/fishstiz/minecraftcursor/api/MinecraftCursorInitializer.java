package io.github.fishstiz.minecraftcursor.api;

import io.github.fishstiz.minecraftcursor.api.ElementRegistrar.CursorTypeFunction;
import net.minecraft.client.gui.components.events.GuiEventListener;

/**
 * The entrypoint for Minecraft Cursor on initialization.
 *
 * <p>This is where you can initialize your cursor types and register elements.</p>
 *
 * <p><b>Fabric</b>: Register your entrypoint in {@code fabric.mod.json} under the {@code minecraft-cursor} key.</p>
 * <p><b>Forge</b> & <b>NeoForge</b>: Register your entrypoint as a service using the {@code ServiceLoader} pattern.</p>
 */
public interface MinecraftCursorInitializer {
    /**
     * The {@link CursorTypeRegistrar} and {@link ElementRegistrar} instances are injected here
     * where you can register custom {@link CursorType}s and {@link GuiEventListener}s.
     *
     * @param cursorRegistrar  used to register {@link CursorType}s.
     * @param elementRegistrar used to register {@link GuiEventListener}s with a corresponding {@link CursorTypeFunction}.
     */
    void init(CursorTypeRegistrar cursorRegistrar, ElementRegistrar elementRegistrar);
}
