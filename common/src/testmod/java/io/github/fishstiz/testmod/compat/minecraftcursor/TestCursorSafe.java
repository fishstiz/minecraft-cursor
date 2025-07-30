package io.github.fishstiz.testmod.compat.minecraftcursor;

import org.jetbrains.annotations.Nullable;

// Safe (but a little hacky) to use regardless if minecraft-cursor is loaded or not
public enum TestCursorSafe {
    TEST_RED,
    TEST_GREEN,
    TEST_BLUE,
    TEST_RGB_LOOP,
    TEST_RGB_LOOP_REVERSE,
    TEST_RGB_FORWARDS,
    TEST_RGB_REVERSE,
    TEST_RGB_OSCILLATE,
    TEST_RGB_RANDOM,
    TEST_RGB_RANDOM_CYCLE,
    TEST_NOT_FOUND;

    private final String key;
    private Object cursorType;

    TestCursorSafe() {
        this.key = this.name().toLowerCase();
    }

    void setCursorType(Object cursorType) {
        if (this.cursorType != null) {
            throw new IllegalStateException("CursorType already set for " + this.key);
        }
        this.cursorType = cursorType;
    }

    public String getKey() {
        return this.key;
    }

    static @Nullable Object getCursorType(String name) {
        try {
            return TestCursorSafe.valueOf(name).cursorType;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
