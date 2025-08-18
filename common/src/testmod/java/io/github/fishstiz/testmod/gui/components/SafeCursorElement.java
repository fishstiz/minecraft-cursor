package io.github.fishstiz.testmod.gui.components;

import io.github.fishstiz.testmod.compat.minecraftcursor.MinecraftCursorUtil;
import io.github.fishstiz.testmod.compat.minecraftcursor.TestCursorSafe;

// Safe to use regardless if minecraft-cursor is loaded or not, but requires additional setup
public class SafeCursorElement extends Buttons.Stub {
    private final TestCursorSafe safeCursor;

    public SafeCursorElement(TestCursorSafe cursor) {
        super(MinecraftCursorUtil.getTranslation(cursor.getKey()));
        this.safeCursor = cursor;
    }

    public TestCursorSafe getCursor() {
        return this.safeCursor;
    }
}
