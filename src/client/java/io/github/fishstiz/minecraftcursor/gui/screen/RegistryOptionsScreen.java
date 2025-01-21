package io.github.fishstiz.minecraftcursor.gui.screen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

import java.util.List;

public class RegistryOptionsScreen extends Screen {
    private final ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget(this);
    private final Screen previousScreen;
    private RegistryListWidget body;

    protected RegistryOptionsScreen(Screen previousScreen) {
        super(Text.translatable("minecraft-cursor.options.more"));
        this.previousScreen = previousScreen;
    }

    @Override
    protected void init() {
        this.layout.addHeader(this.title, this.textRenderer);
        this.body = this.layout.addBody(new RegistryListWidget(this.client, this));
        this.layout.addFooter(ButtonWidget.builder(ScreenTexts.DONE, btn -> this.close()).build());
        this.layout.forEachChild(this::addDrawableChild);

        if (this.body != null) {
            this.refreshWidgetPositions();
        }
    }

    @Override
    protected void refreshWidgetPositions() {
        this.layout.refreshPositions();
        if (this.body != null) {
            this.body.position(this.width, this.layout);
        }
    }

    @Override
    public void close() {
        if (this.client != null) {
            this.client.setScreen(previousScreen);
        }
    }

    public static class RegistryListWidget extends ElementListWidget<RegistryListWidget.RegistryEntry> {
        public RegistryListWidget(MinecraftClient minecraftClient, RegistryOptionsScreen options) {
            super(minecraftClient, 0, options.layout.getHeaderHeight(), options.width, options.layout.getContentHeight());
        }

        public static class RegistryEntry extends ElementListWidget.Entry<RegistryListWidget.RegistryEntry> {
            private List<? extends Element> children;

            @Override
            public List<? extends Selectable> selectableChildren() {
                return List.of();
            }

            @Override
            public List<? extends Element> children() {
                return this.children;
            }

            @Override
            public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {

            }
        }
    }
}
