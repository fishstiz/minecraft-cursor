package io.github.fishstiz.testmod.compat.minecraftcursor;

import io.github.fishstiz.minecraftcursor.api.CursorType;

// Requires additional care if minecraft-cursor is an optional dependency
public enum TestCursorUnsafe implements CursorType {
    TEST_8,
    TEST_16,
    TEST_24,
    TEST_32,
    TEST_48,
    TEST_64,
    TEST_128,
    TEST_AUTO,
    BAD_APPLE;

    private final String key;

    TestCursorUnsafe() {
        this.key = this.name().toLowerCase();
    }

    @Override
    public String getKey() {
        return this.key;
    }
}
