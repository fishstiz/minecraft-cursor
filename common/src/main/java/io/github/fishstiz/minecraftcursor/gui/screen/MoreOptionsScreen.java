package io.github.fishstiz.minecraftcursor.gui.screen;

import io.github.fishstiz.minecraftcursor.MinecraftCursor;
import io.github.fishstiz.minecraftcursor.gui.widget.ContainerEventHandlerPatch;
import io.github.fishstiz.minecraftcursor.gui.widget.MoreOptionsListWidget;
import io.github.fishstiz.minecraftcursor.gui.widget.SelectedCursorButtonWidget;
import io.github.fishstiz.minecraftcursor.gui.widget.SelectedCursorHotspotWidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MoreOptionsScreen extends Screen implements ContainerEventHandlerPatch {
    private static final int FOOTER_SPACING = 8;
    private static final int HOTSPOT_WIDGET_SIZE = 96;
    private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
    private final Screen previousScreen;
    private @Nullable SelectedCursorHotspotWidget hotspotWidget;
    private MoreOptionsListWidget list;
    private Button inspectButton;
    private Button doneButton;

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
        }

        this.layout.addToHeader(new StringWidget(this.title, this.font));
        this.layout.addToContents(this.addRenderableWidget(this.list));
        this.layout.addToFooter(this.createFooter());
        this.layout.visitWidgets(this::addRenderableWidget);
        this.layout.arrangeElements();
        this.repositionElements();
    }

    private LinearLayout createFooter() {
        LinearLayout footer = new LinearLayout(0, 0, LinearLayout.Orientation.HORIZONTAL);

        ResourceLocation inspectIcon = new ResourceLocation("minecraft", "textures/gui/sprites/icon/search.png");
        int iconSize = 12;
        int inspectSize = 20;

        Button inspect = new SelectedCursorButtonWidget(inspectIcon, iconSize, iconSize, MinecraftCursor::toggleInspect);
        inspect.setWidth(inspectSize);

        this.doneButton = footer.addChild(Button.builder(CommonComponents.GUI_DONE, btn -> this.onClose()).build());
        this.inspectButton = footer.addChild(inspect, LayoutSettings.defaults().paddingRight(FOOTER_SPACING));

        return footer;
    }

    @Override
    protected void repositionElements() {
        if (this.list != null) {
            this.layout.arrangeElements();
            this.list.position(width, getContentHeight(), layout.getHeaderHeight());

            if (this.inspectButton != null && this.doneButton != null) {
                int footerWidth = this.inspectButton.getWidth() + this.doneButton.getWidth() + FOOTER_SPACING;
                int footerX = this.width / 2 - footerWidth / 2;

                this.inspectButton.setX(footerX);
                this.doneButton.setX(footerX + this.inspectButton.getWidth() + FOOTER_SPACING);
            }
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

    public int getContentHeight() {
        return height - layout.getHeaderHeight() - layout.getFooterHeight();
    }
}
