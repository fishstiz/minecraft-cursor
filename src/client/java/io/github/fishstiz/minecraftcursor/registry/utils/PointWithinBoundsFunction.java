package io.github.fishstiz.minecraftcursor.registry.utils;

@FunctionalInterface
public interface PointWithinBoundsFunction {
    boolean apply(int x, int y, int width, int height, double pointX, double pointY);
}