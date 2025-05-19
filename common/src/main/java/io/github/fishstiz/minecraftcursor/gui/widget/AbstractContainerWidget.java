package io.github.fishstiz.minecraftcursor.gui.widget;

import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.components.AbstractScrollWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractContainerWidget extends AbstractScrollWidget implements ContainerEventHandlerPatch {
    @Nullable
    private GuiEventListener focusedElement;
    private boolean dragging;

    protected AbstractContainerWidget(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
    }

    @Override
    public final boolean isDragging() {
        return this.dragging;
    }

    @Override
    public final void setDragging(boolean dragging) {
        this.dragging = dragging;
    }

    @Nullable
    @Override
    public GuiEventListener getFocused() {
        return this.focusedElement;
    }

    @Override
    public void setFocused(@Nullable GuiEventListener focused) {
        if (this.focusedElement != null) {
            this.focusedElement.setFocused(false);
        }

        if (focused != null) {
            focused.setFocused(true);
        }

        this.focusedElement = focused;
    }

    @Nullable
    @Override
    public ComponentPath nextFocusPath(FocusNavigationEvent navigation) {
        return ContainerEventHandlerPatch.super.nextFocusPath(navigation);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);
        return ContainerEventHandlerPatch.super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        super.mouseReleased(mouseX, mouseY, button);
        return ContainerEventHandlerPatch.super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        return ContainerEventHandlerPatch.super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean isFocused() {
        return ContainerEventHandlerPatch.super.isFocused();
    }

    @Override
    public void setFocused(boolean focused) {
        ContainerEventHandlerPatch.super.setFocused(focused);
    }
}