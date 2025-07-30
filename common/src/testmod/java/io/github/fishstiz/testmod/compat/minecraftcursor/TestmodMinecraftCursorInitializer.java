package io.github.fishstiz.testmod.compat.minecraftcursor;

import io.github.fishstiz.minecraftcursor.api.*;
import io.github.fishstiz.testmod.gui.components.HandledCursorElement;
import io.github.fishstiz.testmod.gui.components.SafeCursorElement;
import io.github.fishstiz.testmod.gui.components.StatefulCursorElement;
import net.minecraft.network.chat.Component;

public class TestmodMinecraftCursorInitializer implements MinecraftCursorInitializer {
    @Override
    public void init(CursorTypeRegistrar cursorRegistrar, ElementRegistrar elementRegistrar) {
        // Register safe cursors
        for (TestCursorSafe cursor : TestCursorSafe.values()) {
            cursor.setCursorType(cursorRegistrar.register(cursor.getKey()));
        }

        // Register unsafe cursors
        cursorRegistrar.register(TestCursorUnsafe.values());

        // Register SafeCursorElement
        elementRegistrar.register(
                SafeCursorElement.class,
                (safeCursorElement, mouseX, mouseY) -> getCursorType(safeCursorElement.getCursor())
        );

        // Register HandledCursorElement
        Component rgb = Component.literal("rgb");
        Component cell = Component.literal("cell");
        elementRegistrar.register(
                HandledCursorElement.class,
                (handledCursorElement, mouseX, mouseY) -> {
                    Component message = handledCursorElement.getMessage();
                    if (message.contains(rgb)) {
                        return getCursorType(TestCursorSafe.TEST_RGB_LOOP);
                    }
                    if (message.contains(cell)) {
                        return CursorType.CROSSHAIR;
                    }
                    return CursorType.DEFAULT;
                }
        );

        // Register StatefulCursorElement
        elementRegistrar.register(new StatefulCursorElementHandler());
    }

    private static CursorType getCursorType(TestCursorSafe safeCursor) {
        Object cursorType = TestCursorSafe.getCursorType(safeCursor.name());
        if (cursorType instanceof CursorType safeCursorType) {
            return safeCursorType;
        }
        return CursorType.DEFAULT;
    }

    // CursorHandler can be used for automatic registration
    private static class StatefulCursorElementHandler implements CursorHandler<StatefulCursorElement> {
        @Override
        public CursorType getCursorType(StatefulCursorElement element, double mouseX, double mouseY) {
            if (element.isEditing()) {
                return CursorType.TEXT;
            }
            if (element.isSelecting()) {
                return CursorType.CROSSHAIR;
            }
            return CursorType.DEFAULT;
        }
    }
}
