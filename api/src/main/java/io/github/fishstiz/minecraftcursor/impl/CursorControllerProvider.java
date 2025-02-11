package io.github.fishstiz.minecraftcursor.impl;

import io.github.fishstiz.minecraftcursor.api.CursorController;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public final class CursorControllerProvider {
    private static CursorController instance;

    private CursorControllerProvider() {
    }

    public static void init(CursorController impl) {
        if (instance != null) {
            throw new IllegalStateException("CursorController has already been initialized");
        }
        instance = impl;
    }

    public static CursorController getInstance() {
        if (instance == null) {
            throw new IllegalStateException("CursorController has not been initialized");
        }
        return instance;
    }
}
