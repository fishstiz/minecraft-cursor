package io.github.fishstiz.minecraftcursor.gui.screen;

import io.github.fishstiz.minecraftcursor.gui.widget.MoreOptionsListWidget;
import io.github.fishstiz.minecraftcursor.gui.widget.SelectedCursorHotspotWidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MoreOptionsScreen extends Screen {
    private static final int HOTSPOT_WIDGET_SIZE = 96;
    private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
    private final Screen previousScreen;
    private final Button doneButton = Button.builder(CommonComponents.GUI_DONE, btn -> this.onClose()).build();
    private @Nullable SelectedCursorHotspotWidget hotspotWidget;
    private MoreOptionsListWidget list;

    protected MoreOptionsScreen(Screen previousScreen) {
        super(Component.translatable("minecraft-cursor.options.more"));

        this.previousScreen = previousScreen;
    }

    @Override
    protected void init() {
        this.list = new MoreOptionsListWidget(this.minecraft, width, layout.getContentHeight(), layout.getHeaderHeight());

        if (previousScreen instanceof CursorOptionsScreen optionsScreen && optionsScreen.options != null) {
            this.hotspotWidget = new SelectedCursorHotspotWidget(
                    HOTSPOT_WIDGET_SIZE,
                    optionsScreen.options
            );

            this.hotspotWidget.visible = false;
            this.hotspotWidget.setChangeEventListener(this.list::handleChangeHotspotWidget);
            this.addWidget(this.hotspotWidget);
        }

        this.layout.addTitleHeader(this.title, this.font);
        this.layout.addToContents(this.list);
        this.layout.addToFooter(doneButton);
        this.layout.visitWidgets(this::addRenderableWidget);

        this.repositionElements();
    }

    @Override
    protected void repositionElements() {
        if (this.list != null) {
            this.layout.arrangeElements();
            this.list.updateSize(width, layout);
        }
    }

    @Override
    public void render(@NotNull GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        if (list == null || hotspotWidget == null) return;

        if (list.isEditingHotspot()) {
            int rowGap = list.getRowGap();
            int x = list.getRowLeft() - hotspotWidget.getWidth() - rowGap;
            int y = list.getYEntry(1) + rowGap / 2;

            hotspotWidget.setPosition(x, y);
            hotspotWidget.visible = true;
            hotspotWidget.active = true;

            context.enableScissor(x, layout.getHeaderHeight(), list.getRowLeft(), layout.getHeaderHeight() + layout.getContentHeight());
            hotspotWidget.render(context, mouseX, mouseY, delta);
            context.disableScissor();
        } else {
            hotspotWidget.visible = false;
            hotspotWidget.active = false;
        }
    }

    @Override
    public void onClose() {
        list.applyConfig();

        if (this.minecraft != null) {
            if (previousScreen instanceof CursorOptionsScreen previous && previous.options != null) {
                CursorOptionsScreen optionsScreen = new CursorOptionsScreen(previous.previousScreen);
                optionsScreen.selectCursor(previous.getSelectedCursor());
                this.minecraft.setScreen(optionsScreen);
            } else {
                this.minecraft.setScreen(previousScreen);
            }
        }
    }
}
