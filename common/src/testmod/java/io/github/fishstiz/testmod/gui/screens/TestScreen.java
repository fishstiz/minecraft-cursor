package io.github.fishstiz.testmod.gui.screens;

import io.github.fishstiz.minecraftcursor.api.CursorController;
import io.github.fishstiz.minecraftcursor.api.CursorType;
import io.github.fishstiz.testmod.compat.minecraftcursor.TestCursorSafe;
import io.github.fishstiz.testmod.compat.minecraftcursor.TestCursorUnsafe;
import io.github.fishstiz.testmod.gui.components.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class TestScreen extends Screen {
    private final GridLayout grid = new GridLayout();

    public TestScreen() {
        super(Component.literal("Test Screen"));
    }

    @Override
    protected void init() {
        int row = 0;
        int col = 0;

        this.addRenderableWidget(this.grid.addChild(new SafeCursorElement(TestCursorSafe.TEST_RED), row, col));
        this.addRenderableWidget(this.grid.addChild(new SafeCursorElement(TestCursorSafe.TEST_GREEN), ++row, col));
        this.addRenderableWidget(this.grid.addChild(new SafeCursorElement(TestCursorSafe.TEST_BLUE), ++row, col));

        // Requires additional care if minecraft-cursor is an optional dependency
        this.addRenderableWidget(this.grid.addChild(new UnsafeCursorElement(CursorType.CROSSHAIR), ++row, col));
        this.addRenderableWidget(this.grid.addChild(new UnsafeCursorElement(CursorType.RESIZE_EW), ++row, col));
        this.addRenderableWidget(this.grid.addChild(new UnsafeCursorElement(CursorType.RESIZE_NS), ++row, col));

        // These elements are not part of the screen's element tree
        this.addRenderableOnly(this.grid.addChild(new DetachedElement(CursorType.POINTER), ++row, col));
        this.addRenderableOnly(this.grid.addChild(new DetachedElement(CursorType.TEXT), ++row, col));

        // Overrides should be removed on close
        this.addRenderableWidget(this.grid.addChild(new OverrideCursorElement(CursorType.BUSY, 100), ++row, col));
        this.addRenderableWidget(this.grid.addChild(new OverrideCursorElement(TestCursorUnsafe.BAD_APPLE, 200), ++row, col));

        this.addRenderableWidget(this.grid.addChild(new HandledCursorElement(Component.literal("rgb")), ++row, col));
        this.addRenderableWidget(this.grid.addChild(new HandledCursorElement(Component.literal("cell")), ++row, col));

        row = 0;

        this.addRenderableWidget(this.grid.addChild(new StatefulCursorElement(), row, ++col));

        this.repositionElements();
    }

    @Override
    protected void repositionElements() {
        this.grid.arrangeElements();
    }

    @Override
    public void removed() {
        CursorController.getInstance().removeOverride(100);
        CursorController.getInstance().removeOverride(200);
    }

    private static Button.Builder testButton() {
        return testButton("");
    }

    private static Button.Builder testButton(String text) {
        return Button.builder(Component.literal(text), btn -> {});
    }

    public static void open() {
        Minecraft.getInstance().setScreen(new TestScreen());
    }
}
