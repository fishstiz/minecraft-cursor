package io.github.fishstiz.minecraftcursor.api;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
final class CursorTypeImpl implements CursorType {
    private final String key;

    CursorTypeImpl(String key) {
        this.key = key;
    }

    @Override
    public String getKey() {
        return this.key;
    }

    @Override
    public String toString() {
        return "CursorTypeImpl{key='" + key + '}';
    }
}
