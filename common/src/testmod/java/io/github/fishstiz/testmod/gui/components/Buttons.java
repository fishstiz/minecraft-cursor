package io.github.fishstiz.testmod.gui.components;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class Buttons {
    private Buttons() {
    }

    public static void stub(Button ignore) {
        // no-op
    }

    public static class Stub extends Button {
        protected Stub(Component message) {
            super(0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT, message, Buttons::stub, DEFAULT_NARRATION);
        }
    }
}
