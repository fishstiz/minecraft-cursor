package io.github.fishstiz.minecraftcursor.gui.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

import static io.github.fishstiz.minecraftcursor.util.SettingsUtil.clamp;

public abstract class AbstractListWidget<E extends AbstractListWidget<E>.Entry> extends ContainerObjectSelectionList<E> implements LayoutElement {
    public static final int DEFAULT_HEADER_HEIGHT = -4; // removes offsets in AbstractSelectionList
    public static final int SCROLLBAR_WIDTH = 6;
    protected final int rowGap;

    protected AbstractListWidget(Minecraft minecraft, int width, int y, int height, int itemHeight) {
        super(minecraft, width, height, y, y + height, itemHeight);
        this.setRenderHeader(true, DEFAULT_HEADER_HEIGHT);
        this.rowGap = 0;
    }

    protected AbstractListWidget(Minecraft minecraft, int width, int y, int height, int itemHeight, int rowGap) {
        super(minecraft, width, height, y, y + height, itemHeight + rowGap);

        this.setRenderHeader(true, DEFAULT_HEADER_HEIGHT);
        this.rowGap = rowGap;
    }

    @Override
    protected void renderList(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        int left = this.getRowLeft();
        int width = this.getRowWidth();

        for (int i = 0; i < this.getItemCount(); i++) {
            int top = this.getRowTop(i);
            int bottom = this.getRowBottom(i);
            if (bottom >= this.getY() && top <= this.getBottom()) {
                this.renderItem(guiGraphics, mouseX, mouseY, partialTick, i, left, top, width, this.itemHeight);
            }
        }
    }

    public void setClampedScrollAmount(double scrollAmount) {
        this.setScrollAmount(clamp(scrollAmount, 0d, this.getMaxScroll()));
    }

    public void clampScrollAmount() {
        this.setClampedScrollAmount(this.getScrollAmount());
    }

    public boolean scrollbarVisible() {
        return this.getMaxScroll() > 0;
    }

    @Override
    public int getMaxScroll() {
        return Math.max(0, (super.getMaxScroll() - this.rowGap));
    }

    @Override
    protected int getScrollbarPosition() {
        return this.scrollbarVisible() ? this.getRight() - SCROLLBAR_WIDTH : this.getRight();
    }

    @Override
    public int getRowWidth() {
        return this.scrollbarVisible() ? this.getWidth() - SCROLLBAR_WIDTH : this.getWidth();
    }

    @Override
    public int getRowLeft() {
        return this.getX();
    }

    public void setWidth(int width) {
        this.width = width;
        this.x1 = this.x0 + this.width;
    }

    public void setHeight(int height) {
        this.height = height;
        this.y1 = this.y0 + this.height;
        this.clampScrollAmount();
    }

    public void setSize(int width, int height) {
        this.setWidth(width);
        this.setHeight(height);
    }

    @Override
    public void setX(int x) {
        this.setLeftPos(x);
    }

    @Override
    public void setY(int y) {
        this.y0 = y;
        this.y1 = this.y0 + this.height;
    }

    @Override
    public int getX() {
        return this.x0;
    }

    @Override
    public int getY() {
        return this.y0;
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    public int getRight() {
        return this.getX() + this.getWidth();
    }

    public int getBottom() {
        return this.getY() + this.getHeight();
    }

    @Override
    public @NotNull ScreenRectangle getRectangle() {
        return LayoutElement.super.getRectangle();
    }

    @Override
    public void visitWidgets(@NotNull Consumer<AbstractWidget> visitor) {
    }

    protected abstract class Entry extends ContainerObjectSelectionList.Entry<E> implements LayoutElement, ContainerEventHandlerPatch {
        private final int index;

        protected Entry(int index) {
            this.index = index;
        }

        protected Entry() {
            this(AbstractListWidget.this.children().size());
        }

        @Override
        public void setX(int i) {
        }

        @Override
        public void setY(int i) {
        }

        @Override
        public int getX() {
            return AbstractListWidget.this.getRowLeft();
        }

        @Override
        public int getY() {
            return AbstractListWidget.this.getRowTop(this.index);
        }

        @Override
        public int getWidth() {
            return AbstractListWidget.this.getRowWidth();
        }

        @Override
        public int getHeight() {
            return AbstractListWidget.this.itemHeight - AbstractListWidget.this.rowGap;
        }

        public int getRight() {
            return this.getX() + this.getWidth();
        }

        public int getBottom() {
            return this.getY() + this.getHeight();
        }

        @Override
        public @NotNull ScreenRectangle getRectangle() {
            return LayoutElement.super.getRectangle();
        }

        @Override
        public void visitWidgets(@NotNull Consumer<AbstractWidget> visitor) {
        }

        @Override
        public boolean mouseReleased(double mouseX, double mouseY, int button) {
            return ContainerEventHandlerPatch.super.mouseReleased(mouseX, mouseY, button);
        }
    }
}
