package io.github.fishstiz.minecraftcursor.gui.widget;

import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.components.AbstractScrollWidget;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public abstract class ContainerWidget extends AbstractScrollWidget implements ContainerEventHandler {
    @Nullable
    private GuiEventListener focusedElement;
    private boolean dragging;

    public ContainerWidget(int i, int j, int k, int l, Component text) {
        super(i, j, k, l, text);
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
        return ContainerEventHandler.super.nextFocusPath(navigation);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
//        boolean bl = this.checkScrollbarDragged(mouseX, mouseY, button);
//        return ParentElement.super.mouseClicked(mouseX, mouseY, button) || bl;
        return ContainerEventHandler.super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        super.mouseReleased(mouseX, mouseY, button);
        return ContainerEventHandler.super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        return ContainerEventHandler.super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean isFocused() {
        return ContainerEventHandler.super.isFocused();
    }

    @Override
    public void setFocused(boolean focused) {
        ContainerEventHandler.super.setFocused(focused);
    }
}