package io.github.fishstiz.minecraftcursor.gui.widget;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.gui.components.events.ContainerEventHandler;

/**
 * Ensure mouse release is propagated to focused child
 * <p>
 * Copied from 1.21.1 version of {@link ContainerEventHandler#mouseReleased(double, double, int)}
 */
public interface ContainerEventHandlerPatch extends ContainerEventHandler {
    @Override
    default boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == InputConstants.MOUSE_BUTTON_LEFT && this.isDragging()) {
            this.setDragging(false);
            if (this.getFocused() != null) {
                return this.getFocused().mouseReleased(mouseX, mouseY, button);
            }
        }

        return this.getChildAt(mouseX, mouseY).filter(child -> child.mouseReleased(mouseX, mouseY, button)).isPresent();
    }
}
