package io.github.fishstiz.minecraftcursor.gui.screen;

import io.github.fishstiz.minecraftcursor.CursorManager;
import io.github.fishstiz.minecraftcursor.api.CursorProvider;
import io.github.fishstiz.minecraftcursor.api.CursorType;
import io.github.fishstiz.minecraftcursor.gui.widget.MoreOptionsListWidget;
import io.github.fishstiz.minecraftcursor.gui.widget.SelectedCursorHotspotWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.*;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public class MoreOptionsScreen extends Screen implements CursorProvider {
    private static final int HOTSPOT_WIDGET_SIZE = 96;
    private final ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget(this);
    private final Screen previousScreen;
    private final CursorManager cursorManager;
    private final @Nullable SelectedCursorHotspotWidget hotspotWidget;
    private final ButtonWidget doneButton = ButtonWidget.builder(ScreenTexts.DONE, btn -> this.close()).build();
    private MoreOptionsListWidget list;

    protected MoreOptionsScreen(Screen previousScreen, CursorManager cursorManager) {
        super(Text.translatable("minecraft-cursor.options.more"));

        this.previousScreen = previousScreen;
        this.cursorManager = cursorManager;

        if (previousScreen instanceof CursorOptionsScreen optionsScreen && optionsScreen.body != null) {
            this.hotspotWidget = new SelectedCursorHotspotWidget(
                    HOTSPOT_WIDGET_SIZE,
                    optionsScreen.body.selectedCursorColumn
            );

            this.hotspotWidget.visible = false;
            this.addSelectableChild(this.hotspotWidget);
        } else {
            this.hotspotWidget = null;
        }
    }

    @Override
    protected void init() {
        this.layout.addHeader(new TextWidget(this.title, this.textRenderer));

        this.list = this.layout.addBody(new MoreOptionsListWidget(
                this.client,
                width,
                getContentHeight(),
                layout.getHeaderHeight(),
                layout.getHeaderHeight() + getContentHeight(),
                cursorManager
        ));

        if (this.hotspotWidget != null) {
            this.hotspotWidget.setChangeEventListener(this.list::handleChangeHotspotWidget);
        }

        this.layout.addFooter(doneButton);

        this.layout.forEachChild(this::addDrawableChild);
        this.addSelectableChild(this.list);

        this.refreshWidgetPositions();
    }

    protected void refreshWidgetPositions() {
        if (this.list != null) {
            this.layout.refreshPositions();
            this.list.position(width, getContentHeight(), layout.getHeaderHeight());
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        this.renderBackgroundTexture(context);
        list.render(context, mouseX, mouseY, delta);
        doneButton.render(context, mouseX, mouseY, delta);

        if (hotspotWidget == null) return;

        if (list.isEditingHotspot()) {
            int rowGap = list.getRowGap();
            int x = list.getRowLeft() - hotspotWidget.getWidth() - rowGap;
            int y = list.getYEntry(1) + rowGap / 2;

            hotspotWidget.setPosition(x, y);
            hotspotWidget.visible = true;
            hotspotWidget.active = true;

            context.enableScissor(x, layout.getHeaderHeight(), list.getRowLeft(), layout.getHeaderHeight() + getContentHeight());
            hotspotWidget.renderButton(context, mouseX, mouseY, delta);
            context.disableScissor();
        } else {
            hotspotWidget.visible = false;
            hotspotWidget.active = false;
        }
    }

    @Override
    public void close() {
        if (this.client != null) {
            if (previousScreen instanceof CursorOptionsScreen options && options.body != null) {
                CursorOptionsScreen optionsScreen = new CursorOptionsScreen(options.previousScreen, cursorManager);
                this.client.setScreen(optionsScreen);
                optionsScreen.selectCursor(options.getSelectedCursor());
            } else {
                this.client.setScreen(previousScreen);
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
