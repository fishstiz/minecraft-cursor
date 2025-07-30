package io.github.fishstiz.testmod.gui.components;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

// This element has no context of cursor and should always be safe to use
public class HandledCursorElement extends Button {
    public HandledCursorElement(Component message) {
        super(0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT, message, Buttons::stub, DEFAULT_NARRATION);
    }
}
