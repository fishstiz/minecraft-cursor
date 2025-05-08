package io.github.fishstiz.minecraftcursor.gui.screen;

import io.github.fishstiz.minecraftcursor.api.CursorProvider;
import io.github.fishstiz.minecraftcursor.api.CursorType;
import io.github.fishstiz.minecraftcursor.gui.widget.MoreOptionsListWidget;
import io.github.fishstiz.minecraftcursor.gui.widget.SelectedCursorHotspotWidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MoreOptionsScreen extends Screen implements CursorProvider {
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
        this.list = new MoreOptionsListWidget(
                this.minecraft,
                width,
                getContentHeight(),
                layout.getHeaderHeight(),
                layout.getHeaderHeight() + getContentHeight()
        );

        if (previousScreen instanceof CursorOptionsScreen optionsScreen && optionsScreen.body != null) {
            this.hotspotWidget = new SelectedCursorHotspotWidget(HOTSPOT_WIDGET_SIZE, optionsScreen.body.selectedCursorColumn);
            this.hotspotWidget.visible = false;
            this.hotspotWidget.setChangeEventListener(this.list::handleChangeHotspotWidget);
            this.addWidget(this.hotspotWidget);
        } else {
            this.hotspotWidget = null;
        }

        this.layout.addToHeader(new StringWidget(this.title, this.font));
        this.layout.addToContents(this.addRenderableWidget(this.list));
        this.layout.addToFooter(this.doneButton);
        this.layout.visitWidgets(this::addRenderableWidget);
        this.repositionElements();
    }

    @Override
    protected void repositionElements() {
        if (this.list != null) {
            this.layout.arrangeElements();
            this.list.position(width, getContentHeight(), layout.getHeaderHeight());
        }
    }

    @Override
    public void render(@NotNull GuiGraphics context, int mouseX, int mouseY, float delta) {
        this.renderDirtBackground(context);
        super.render(context, mouseX, mouseY, delta);

        if (hotspotWidget == null) return;

        if (list.isEditingHotspot()) {
            int rowGap = list.getRowGap();
            int x = list.getRowLeft() - hotspotWidget.getWidth() - rowGap;
            int y = list.getYEntry(1) + rowGap / 2;

            hotspotWidget.setPosition(x, y);
            hotspotWidget.visible = true;
            hotspotWidget.active = true;

            context.enableScissor(x, layout.getHeaderHeight(), list.getRowLeft(), layout.getHeaderHeight() + getContentHeight());
            hotspotWidget.renderWidget(context, mouseX, mouseY, delta);
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
            if (previousScreen instanceof CursorOptionsScreen options && options.body != null) {
                CursorOptionsScreen optionsScreen = new CursorOptionsScreen(options.previousScreen);
                this.minecraft.setScreen(optionsScreen);
                optionsScreen.selectCursor(options.getSelectedCursor());
            } else {
                this.minecraft.setScreen(previousScreen);
            }
        }
    }

    @Override
    public CursorType getCursorType(double mouseX, double mouseY) {
        int headerHeight = layout.getHeaderHeight();
        if ((mouseY < headerHeight || mouseY > headerHeight + getContentHeight())
            && mouseX > doneButton.getX() + doneButton.getWidth()) {
            return CursorType.DEFAULT_FORCE;
        }
        return CursorType.DEFAULT;
    }

    public int getContentHeight() {
        return height - layout.getHeaderHeight() - layout.getFooterHeight();
    }
}
