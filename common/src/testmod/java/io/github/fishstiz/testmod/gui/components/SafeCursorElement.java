package io.github.fishstiz.testmod.gui.components;

import io.github.fishstiz.testmod.compat.minecraftcursor.MinecraftCursorUtil;
import io.github.fishstiz.testmod.compat.minecraftcursor.TestCursorSafe;
import net.minecraft.client.gui.components.Button;

// Safe to use regardless if minecraft-cursor is loaded or not, but requires additional setup
public class SafeCursorElement extends Button {
    private final TestCursorSafe safeCursor;

    public SafeCursorElement(TestCursorSafe cursor) {
        super(0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT, MinecraftCursorUtil.getTranslation(cursor.getKey()), Buttons::stub, DEFAULT_NARRATION);

        this.safeCursor = cursor;
    }

    public TestCursorSafe getCursor() {
        return this.safeCursor;
    }
}
