package io.github.fishstiz.minecraftcursor.gui.widget;

import io.github.fishstiz.minecraftcursor.util.DrawUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class OptionsListWidget extends AbstractListWidget<OptionsListWidget.AbstractEntry> {
    private static final int BACKGROUND_PADDING_Y = 2;
    private static final int SEARCH_HIGHLIGHT_COLOR = 0x66FFD700; // 40% yellow
    private final ElementSlidingBackground hoveredBackground = new ElementSlidingBackground(0x26FFFFFF); // 15% white
    private final Font font;
    private @NotNull String search = "";

    public OptionsListWidget(Minecraft minecraft, Font font, int itemHeight, int spacing) {
        super(minecraft, 0, 0, 0, itemHeight, spacing);

        this.font = font;
    }

    public OptionsListWidget(Minecraft minecraft, Font font, int spacing) {
        this(minecraft, font, Button.DEFAULT_HEIGHT, spacing);
    }

    public void addWidget(AbstractWidget widget) {
        this.addEntry(new WidgetEntry(widget));
    }

    public void addToggle(
            boolean value,
            @NotNull Consumer<Boolean> onToggle,
            @NotNull Component label,
            @Nullable Tooltip tooltip,
            boolean active
    ) {
        this.addEntry(new ToggleEntry(value, onToggle, label, null, tooltip, active));
    }

    public void addToggle(
            boolean value,
            @NotNull Consumer<Boolean> onToggle,
            @NotNull Component label,
            @Nullable Prefix prefix,
            @Nullable Tooltip tooltip,
            boolean active
    ) {
        this.addEntry(new ToggleEntry(value, onToggle, label, prefix, tooltip, active));
    }

    public void addToggleableSlider(
            @NotNull SliderWidget slider,
            boolean value,
            @NotNull Consumer<Boolean> onToggle,
            @Nullable Tooltip tooltip
    ) {
        this.addEntry(new ToggleableSliderEntry(slider, value, onToggle, tooltip, true));
    }

    public void search(@NotNull String search) {
        this.search = search.toLowerCase();

        if (!this.search.isEmpty()) {
            for (AbstractEntry entry : this.children()) {
                if (entry.indexedLabel.contains(this.search)) {
                    this.ensureVisible(entry);
                    break;
                }
            }
        }
    }

    private int computeBackgroundX(AbstractEntry entry) {
        return entry.getX() - this.rowGap;
    }

    private int computeBackgroundY(AbstractEntry entry) {
        return entry.getY() - BACKGROUND_PADDING_Y;
    }

    private int computeBackgroundWidth(AbstractEntry entry) {
        return entry.getWidth() + this.rowGap + (this.scrollbarVisible() ? SCROLLBAR_WIDTH : BACKGROUND_PADDING_Y);
    }

    private int computeBackgroundHeight(AbstractEntry entry) {
        return entry.getHeight() + BACKGROUND_PADDING_Y * 2;
    }

    protected void renderSearchBackground(@NotNull GuiGraphics guiGraphics) {
        if (!this.search.isEmpty()) {
            for (AbstractEntry entry : this.children()) {
                if (entry.indexedLabel.contains(this.search)) {
                    int bgX = this.computeBackgroundX(entry);
                    int bgY = this.computeBackgroundY(entry);
                    int bgWidth = this.computeBackgroundWidth(entry);
                    int bgHeight = this.computeBackgroundHeight(entry);

                    guiGraphics.fill(bgX, bgY, bgX + bgWidth, bgY + bgHeight, SEARCH_HIGHLIGHT_COLOR);
                }
            }
        }
    }

    protected void renderEntryBackground(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        int minX = this.getX() - this.rowGap;
        int minY = this.getY() - BACKGROUND_PADDING_Y;
        int maxX = this.getRight() + SCROLLBAR_WIDTH;
        int maxY = this.getBottom() + BACKGROUND_PADDING_Y;
        guiGraphics.enableScissor(minX, minY, maxX, maxY);

        this.renderSearchBackground(guiGraphics);

        AbstractEntry entry = this.getEntryAtPosition(mouseX, mouseY);
        if (entry == null) {
            this.hoveredBackground.reset();
        } else {
            int x = this.computeBackgroundX(entry);
            int y = this.computeBackgroundY(entry);
            int width = this.computeBackgroundWidth(entry);
            int height = this.computeBackgroundHeight(entry);
            this.hoveredBackground.render(guiGraphics, x, y, width, height, partialTick);
        }

        guiGraphics.disableScissor();
    }

    @Override
    public void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderEntryBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    protected void renderListBackground(@NotNull GuiGraphics guiGraphics) {
        // remove background
    }

    @Override
    protected void renderListSeparators(@NotNull GuiGraphics guiGraphics) {
        // remove separators
    }

    protected abstract class AbstractEntry extends AbstractListWidget<AbstractEntry>.Entry {
        protected final Component label;
        private final String indexedLabel;
        private final List<AbstractWidget> children = new ArrayList<>();

        protected AbstractEntry(Component label) {
            this.label = label;
            this.indexedLabel = label.getString().toLowerCase();
        }

        @Override
        public void render(@NotNull GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick) {
            for (AbstractWidget child : this.children) {
                child.render(guiGraphics, mouseX, mouseY, partialTick);
            }
        }

        protected <T extends AbstractWidget> T addChild(T child) {
            this.children.add(child);
            return child;
        }

        @Override
        public @NotNull List<AbstractWidget> children() {
            return this.children;
        }

        @Override
        public @NotNull List<AbstractWidget> narratables() {
            return this.children;
        }
    }

    private class WidgetEntry extends AbstractEntry {
        private final AbstractWidget widget;

        private WidgetEntry(@NotNull AbstractWidget widget) {
            super(widget.getMessage());

            this.widget = this.addChild(widget);
        }

        @Override
        public void render(@NotNull GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick) {
            this.widget.setSize(width, height - OptionsListWidget.this.rowGap);
            this.widget.setPosition(left, top);
            super.render(guiGraphics, index, top, left, width, height, mouseX, mouseY, hovering, partialTick);
        }
    }

    private class ToggleEntry extends AbstractEntry {
        protected static final int BUTTON_WIDTH = 40;
        private static final int LABEL_COLOR = 0xFFFFFFFF; // white
        private static final int DISABLED_COLOR = 0xFFAAAAAA; // gray
        private final ButtonWidget button;
        private final Consumer<Boolean> onToggle;
        private final @Nullable Prefix prefix;
        protected boolean value;

        private ToggleEntry(
                boolean value,
                @NotNull Consumer<Boolean> onToggle,
                @NotNull Component label,
                @Nullable Prefix prefix,
                @Nullable Tooltip tooltip,
                boolean active
        ) {
            super(label);

            this.value = value;
            this.onToggle = onToggle;
            this.button = new ButtonWidget(
                    this.getRight() - BUTTON_WIDTH,
                    this.getY(),
                    BUTTON_WIDTH,
                    Button.DEFAULT_HEIGHT,
                    value ? CommonComponents.OPTION_ON : CommonComponents.OPTION_OFF,
                    this::onPress
            );
            this.button.setTooltip(tooltip);
            this.button.active = active;
            this.prefix = prefix;

            this.addChild(this.button);
        }

        protected void onPress(Button button) {
            this.value = !this.value;
            this.button.setMessage(this.value ? CommonComponents.OPTION_ON : CommonComponents.OPTION_OFF);
            this.onToggle.accept(this.value);
        }

        protected void renderLabel(@NotNull GuiGraphics guiGraphics) {
            int marginX = 0;

            if (this.prefix != null) {
                marginX = this.prefix.render(guiGraphics, OptionsListWidget.this.font, this.getX(), this.getY(), this.getHeight());
                if (marginX > 0) marginX += OptionsListWidget.this.rowGap;
            }

            int startX = this.getX() + marginX;
            int startY = this.getY();
            int endX = this.button.getX() - OptionsListWidget.this.rowGap;
            int endY = this.getBottom();
            int color = this.value ? LABEL_COLOR : DISABLED_COLOR;
            DrawUtil.drawScrollableTextLeftAlign(guiGraphics, OptionsListWidget.this.font, this.label, startX, startY, endX, endY, color);
        }

        @Override
        public void render(@NotNull GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick) {
            this.button.setPosition(this.getRight() - this.button.getWidth(), top);

            this.renderLabel(guiGraphics);
            super.render(guiGraphics, index, top, left, width, height, mouseX, mouseY, hovering, partialTick);
        }
    }

    private class ToggleableSliderEntry extends ToggleEntry {
        private final SliderWidget slider;

        private ToggleableSliderEntry(
                @NotNull SliderWidget slider,
                boolean value,
                @NotNull Consumer<Boolean> onToggle,
                @Nullable Tooltip tooltip,
                boolean active
        ) {
            super(value, onToggle, slider.getMessage(), null, tooltip, active);

            this.slider = slider;
            this.slider.active = value;
            this.addChild(this.slider);
        }

        @Override
        protected void onPress(Button button) {
            super.onPress(button);
            this.slider.active = this.value;
        }

        @Override
        protected void renderLabel(@NotNull GuiGraphics guiGraphics) {
            // remove label
        }

        @Override
        public void render(@NotNull GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick) {
            super.render(guiGraphics, index, top, left, width, height, mouseX, mouseY, hovering, partialTick);

            this.slider.setWidth(width - BUTTON_WIDTH - OptionsListWidget.this.rowGap);
            this.slider.setPosition(left, top);
            this.slider.render(guiGraphics, mouseX, mouseY, partialTick);
        }
    }

    @FunctionalInterface
    public interface Prefix {
        /**
         * @return width of prefix
         */
        int render(@NotNull GuiGraphics guiGraphics, Font font, int x, int y, int height);
    }
}