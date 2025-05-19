package io.github.fishstiz.minecraftcursor.gui.widget;

import net.minecraft.client.gui.components.events.ContainerEventHandler;

import static com.mojang.blaze3d.platform.InputConstants.MOUSE_BUTTON_LEFT;

public interface ContainerEventHandlerPatch extends ContainerEventHandler {
    // should propagate mouse release to focused child
    @Override
    default boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == MOUSE_BUTTON_LEFT && this.isDragging()) {
            this.setDragging(false);
            if (this.getFocused() != null) {
                return this.getFocused().mouseReleased(mouseX, mouseY, button);
            }
        }

        return this.getChildAt(mouseX, mouseY).filter(child -> child.mouseReleased(mouseX, mouseY, button)).isPresent();
    }
}
