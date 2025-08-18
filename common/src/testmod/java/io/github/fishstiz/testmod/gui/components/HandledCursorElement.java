package io.github.fishstiz.testmod.gui.components;

import net.minecraft.network.chat.Component;

// This element has no context of cursor and should always be safe to use
public class HandledCursorElement extends Buttons.Stub {
    public HandledCursorElement(Component message) {
        super(message);
    }
}
