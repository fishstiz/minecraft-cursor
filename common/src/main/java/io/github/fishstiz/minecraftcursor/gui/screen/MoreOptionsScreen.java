package io.github.fishstiz.minecraftcursor.gui.screen;

import io.github.fishstiz.minecraftcursor.MinecraftCursor;
import io.github.fishstiz.minecraftcursor.gui.widget.MoreOptionsListWidget;
import io.github.fishstiz.minecraftcursor.gui.widget.SelectedCursorButtonWidget;
import io.github.fishstiz.minecraftcursor.gui.widget.SelectedCursorHotspotWidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MoreOptionsScreen extends Screen {
    private static final int FOOTER_SPACING = 8;
    private static final int HOTSPOT_WIDGET_SIZE = 96;
    private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
    private final Screen previousScreen;
    private @Nullable SelectedCursorHotspotWidget hotspotWidget;
    private MoreOptionsListWidget list;

    protected MoreOptionsScreen(Screen previousScreen) {
        super(Component.translatable("minecraft-cursor.options.more"));

        this.previousScreen = previousScreen;
    }

    @Override
    protected void init() {
        this.list = new MoreOptionsListWidget(this.minecraft, width, layout.getContentHeight(), layout.getHeaderHeight());

        if (previousScreen instanceof CursorOptionsScreen previous && previous.options != null) {
            this.hotspotWidget = new SelectedCursorHotspotWidget(
                    HOTSPOT_WIDGET_SIZE,
                    previous.options
            );

            this.hotspotWidget.visible = false;
            this.hotspotWidget.setChangeEventListener(this.list::handleChangeHotspotWidget);
            this.addWidget(this.hotspotWidget);
        }

        this.layout.addTitleHeader(this.title, this.font);
        this.layout.addToContents(this.list);
        this.layout.addToFooter(this.createFooter());
        this.layout.visitWidgets(this::addRenderableWidget);

        this.repositionElements();
    }

    private LinearLayout createFooter() {
        LinearLayout footer = LinearLayout.horizontal().spacing(FOOTER_SPACING);

        ResourceLocation inspectIcon = ResourceLocation.withDefaultNamespace("textures/gui/sprites/icon/search.png");
        int iconSize = 12;
        int inspectSize = 20;

        Button inspect = new SelectedCursorButtonWidget(inspectIcon, iconSize, iconSize, MinecraftCursor::toggleInspect);
        inspect.setSize(inspectSize, inspectSize);

        footer.addChild(inspect);
        footer.addChild(Button.builder(CommonComponents.GUI_DONE, btn -> this.onClose()).build());

        return footer;
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
            if (previousScreen instanceof CursorOptionsScreen screen && screen.options != null) {
                screen.options.refresh();
            }
            this.minecraft.setScreen(previousScreen);
        }
    }
}
