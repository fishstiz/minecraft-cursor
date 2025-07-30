package io.github.fishstiz.testmod.gui.components;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

// This element has no context of cursor but has many states that can determine cursor
public class StatefulCursorElement extends AbstractContainerEventHandler implements Renderable, NarratableEntry, LayoutElement {
    private final Button editButton = Button.builder(Component.literal("Edit"), this::onPressEdit).size(40, Button.DEFAULT_HEIGHT).build();
    private final Button selectButton = Button.builder(Component.literal("Select"), this::onPressSelect).size(40, Button.DEFAULT_HEIGHT).build();
    private final List<AbstractWidget> children = List.of(this.editButton, this.selectButton);
    private Mode mode = Mode.NONE;
    private int x;
    private int y;

    private void onPressEdit(Button button) {
        this.mode = this.mode.toggle(Mode.EDIT);
    }

    private void onPressSelect(Button button) {
        this.mode = this.mode.toggle(Mode.SELECT);
    }

    public boolean isEditing() {
        return this.mode == Mode.EDIT;
    }

    public boolean isSelecting() {
        return this.mode == Mode.SELECT;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX >= this.getX() &&
               mouseX < this.getX() + this.getWidth() &&
               mouseY >= this.getY() &&
               mouseY < this.getY() + this.getHeight();
    }

    @Override
    public @NotNull List<AbstractWidget> children() {
        return this.children;
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.fill(this.getX(), this.getY(), this.getX() + this.getWidth(), this.getY() + this.getHeight(), 0x7F000000);
        for (var child : this.children) {
            child.render(guiGraphics, mouseX, mouseY, partialTick);
        }
    }

    @Override
    public @NotNull NarrationPriority narrationPriority() {
        return this.editButton.isHovered() || this.selectButton.isHovered() ? NarrationPriority.HOVERED : NarrationPriority.NONE;
    }

    @Override
    public void updateNarration(@NotNull NarrationElementOutput narrationElementOutput) {
        for (var child : this.children) {
            child.updateNarration(narrationElementOutput);
        }
    }

    @Override
    public void setX(int x) {
        this.x = x;
        this.editButton.setX(x);
        this.selectButton.setX(x + this.editButton.getWidth());
    }

    @Override
    public void setY(int y) {
        this.y = y;
        this.editButton.setY(y);
        this.selectButton.setY(y);
    }

    @Override
    public int getX() {
        return this.x;
    }

    @Override
    public int getY() {
        return this.y;
    }

    @Override
    public int getWidth() {
        return Button.DEFAULT_WIDTH;
    }

    @Override
    public int getHeight() {
        return Button.DEFAULT_HEIGHT;
    }

    @Override
    public void visitWidgets(@NotNull Consumer<AbstractWidget> visitor) {
        this.children.forEach(visitor);
    }

    @Override
    public @NotNull ScreenRectangle getRectangle() {
        return LayoutElement.super.getRectangle();
    }

    private enum Mode {
        NONE,
        EDIT,
        SELECT;

        Mode toggle(Mode to) {
            return this != to ? to : Mode.NONE;
        }
    }
}
