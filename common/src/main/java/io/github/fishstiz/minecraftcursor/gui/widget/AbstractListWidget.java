package io.github.fishstiz.minecraftcursor.gui.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractListWidget<E extends AbstractListWidget<E>.Entry> extends ContainerObjectSelectionList<E> {
    public static final int DEFAULT_HEADER_HEIGHT = -4; // removes offsets in AbstractSelectionList
    protected final int rowGap;

    protected AbstractListWidget(Minecraft minecraft, int width, int y, int height, int itemHeight) {
        super(minecraft, width, y, height, itemHeight);
        this.setRenderHeader(true, DEFAULT_HEADER_HEIGHT);
        this.rowGap = 0;
    }

    protected AbstractListWidget(Minecraft minecraft, int width, int y, int height, int itemHeight, int rowGap) {
        super(minecraft, width, y, height, itemHeight + rowGap);

        this.setRenderHeader(true, DEFAULT_HEADER_HEIGHT);
        this.rowGap = rowGap;
    }

    @Override
    protected void renderListItems(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
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

    @Override
    public int getMaxScroll() {
        return Math.max(0, super.getMaxScroll() - this.rowGap);
    }

    @Override
    protected int getScrollbarPosition() {
        return this.getRight() - SCROLLBAR_WIDTH;
    }

    @Override
    public int getRowWidth() {
        return this.scrollbarVisible() ? this.getWidth() - SCROLLBAR_WIDTH : this.getWidth();
    }

    @Override
    public int getRowLeft() {
        return this.getX();
    }

    @Override
    public void setHeight(int height) {
        super.setHeight(height);
        this.clampScrollAmount();
    }

    @Override
    public void setSize(int width, int height) {
        super.setSize(width, height);
        this.clampScrollAmount();
    }

    protected abstract class Entry extends ContainerObjectSelectionList.Entry<E> implements ElementView {
        private final int index;

        protected Entry(int index) {
            this.index = index;
        }

        protected Entry() {
            this(AbstractListWidget.this.children().size());
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

        @Override
        public @NotNull ScreenRectangle getRectangle() {
            return ElementView.super.getRectangle();
        }
    }
}
