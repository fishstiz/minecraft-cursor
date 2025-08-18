package io.github.fishstiz.testmod.gui.screens;

import io.github.fishstiz.minecraftcursor.api.CursorController;
import io.github.fishstiz.minecraftcursor.api.CursorType;
import io.github.fishstiz.testmod.compat.minecraftcursor.TestCursorSafe;
import io.github.fishstiz.testmod.compat.minecraftcursor.TestCursorUnsafe;
import io.github.fishstiz.testmod.gui.components.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.navigation.ScreenRectangle;
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

        // These elements have no cursor logic, which is safe to use when minecraft-cursor is not loaded
        // The cursor logic is instead handled in the MinecraftCursorInitializer implementation
        this.addRenderableWidget(this.grid.addChild(new HandledCursorElement(Component.literal("rgb")), ++row, col));
        this.addRenderableWidget(this.grid.addChild(new HandledCursorElement(Component.literal("cell")), ++row, col));
        this.addRenderableWidget(this.grid.addChild(new StatefulCursorElement(), 0, ++col));

        this.grid.arrangeElements();
        this.addOverlapTests();
    }

    // cursor of green should take precedence when both red and green are hovered
    private void addOverlapTests() {
        ScreenRectangle rectangle = this.getRectangle();
        final int red = 0xFFFF0000;
        final int green = 0xFF00FF00;
        
        // Hovering over SHIFT should not change cursor when DEFAULT is also hovered
        this.addRenderableWidget(new RectangleElement(Component.literal("SHIFT").withStyle(ChatFormatting.WHITE)))
                .apply(btn -> btn.pos(rectangle.right() - btn.getWidth() - 20, rectangle.bottom() - btn.getHeight() - 20))
                .cursorType(CursorType.SHIFT)
                .color(red);
        this.addRenderableWidget(new RectangleElement(Component.literal("DEFAULT").withStyle(ChatFormatting.WHITE)))
                .apply(btn -> btn.pos(rectangle.right() - btn.getWidth() - 30, rectangle.bottom() - btn.getHeight() - 30))
                .color(green);

        // GRABBING should change cursor when hovered
        this.addRenderableWidget(new RectangleElement(Component.literal("DEFAULT").withStyle(ChatFormatting.WHITE)))
                .apply(btn -> btn.pos(rectangle.right() - btn.getWidth() - 20, rectangle.bottom() - btn.getHeight() - 70))
                .color(red);
        this.addRenderableWidget(new RectangleElement(Component.literal("GRABBING").withStyle(ChatFormatting.WHITE)))
                .apply(btn -> btn.pos(rectangle.right() - btn.getWidth() - 30, rectangle.bottom() - btn.getHeight() - 80))
                .cursorType(CursorType.GRABBING)
                .color(green);

        // Ideally, NOT_ALLOWED should take precedence as it is rendered over CROSSHAIR,
        // but the first child (CROSSHAIR) consumes the click because of the default implementation of mouseClicked,
        // despite being covered by the next sibling, so the cursor of CROSSHAIR should take precedence
        this.addRenderableWidget(new RectangleElement(Component.literal("CROSSHAIR").withStyle(ChatFormatting.WHITE)))
                .apply(btn -> btn.pos(rectangle.right() - btn.getWidth() - 20, rectangle.bottom() - btn.getHeight() - 120))
                .cursorType(CursorType.CROSSHAIR)
                .color(green);
        this.addRenderableWidget(new RectangleElement(Component.literal("NOT_ALLOWED").withStyle(ChatFormatting.WHITE)))
                .apply(btn -> btn.pos(rectangle.right() - btn.getWidth() - 30, rectangle.bottom() - btn.getHeight() - 130))
                .cursorType(CursorType.NOT_ALLOWED)
                .color(red);

        // Add TEXT (not as a renderable) first so it consumes the mouse click first,
        // then add to renderables later so it renders on top of BUSY
        var overlap8 = this.addWidget(new RectangleElement(Component.literal("TEXT").withStyle(ChatFormatting.WHITE)))
                .apply(btn -> btn.pos(rectangle.right() - btn.getWidth() - 30, rectangle.bottom() - btn.getHeight() - 180))
                .cursorType(CursorType.TEXT)
                .color(green);
        this.addRenderableWidget(new RectangleElement(Component.literal("BUSY").withStyle(ChatFormatting.WHITE)))
                .apply(btn -> btn.pos(rectangle.right() - btn.getWidth() - 20, rectangle.bottom() - btn.getHeight() - 170))
                .cursorType(CursorType.BUSY)
                .color(red);
        this.addRenderableOnly(overlap8);
    }

    @Override
    public void removed() {
        CursorController.getInstance().removeOverride(100);
        CursorController.getInstance().removeOverride(200);
    }

    public static void open() {
        Minecraft.getInstance().setScreen(new TestScreen());
    }
}
