package io.github.fishstiz.testmod.gui.screens;

import net.minecraft.client.Minecraft;

public class TestScreenCursor extends TestScreen {
    public static void open() {
        Minecraft.getInstance().setScreen(new TestScreenCursor());
    }
}
