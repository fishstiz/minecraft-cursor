package io.github.fishstiz.minecraftcursor.gui.widget;

import io.github.fishstiz.minecraftcursor.config.Config;
import io.github.fishstiz.minecraftcursor.cursor.AnimatedCursor;
import io.github.fishstiz.minecraftcursor.cursor.Cursor;
import io.github.fishstiz.minecraftcursor.gui.screen.CursorOptionsScreen;
import io.github.fishstiz.minecraftcursor.util.SettingsUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleConsumer;

import static io.github.fishstiz.minecraftcursor.MinecraftCursor.CONFIG;
import static io.github.fishstiz.minecraftcursor.util.SettingsUtil.*;

public class CursorOptionsWidget extends AbstractContainerWidget implements ContainerEventHandlerPatch {
    private static final int OPTIONS_HEIGHT = 24;
    private static final int GRID_PADDING = 4;
    private static final int BOX_WIDGET_TEXTURE_SIZE = 96;
    private static final int HELPER_BUTTON_SIZE = 16;
    private static final int HELPER_ICON_SIZE = 10;
    private static final ResourceLocation HELPER_ICON = new ResourceLocation("minecraft", "textures/gui/unseen_notification.png");
    private static final String GLOBAL_TEXT_KEY = "minecraft-cursor.options.global.tooltip";
    private static final Component ANIMATE_TEXT = Component.translatable("minecraft-cursor.options.animate");
    private static final Component RESET_ANIMATION_TEXT = Component.translatable("minecraft-cursor.options.animate-reset");
    private static final String HOT_UNIT = "px";

    public static final Component ENABLED_TEXT = Component.translatable("minecraft-cursor.options.enabled");
    public static final Component SCALE_TEXT = Component.translatable("minecraft-cursor.options.scale");
    public static final Component XHOT_TEXT = Component.translatable("minecraft-cursor.options.xhot");
    public static final Component YHOT_TEXT = Component.translatable("minecraft-cursor.options.yhot");

    private final CursorOptionsHandler handler;
    private final CursorOptionsScreen parent;
    private final List<GuiEventListener> children = new ArrayList<>();
    SelectedCursorToggleWidget enableButton;
    SelectedCursorSliderWidget scaleSlider;
    SelectedCursorSliderWidget xhotSlider;
    SelectedCursorSliderWidget yhotSlider;
    SelectedCursorToggleWidget animateButton;
    SelectedCursorButtonWidget resetAnimation;
    SelectedCursorHotspotWidget cursorHotspot;
    SelectedCursorTestWidget cursorTest;

    public CursorOptionsWidget(int x, int width, int height, int y, CursorOptionsScreen optionsScreen) {
        super(x, y, width, height, Component.empty());

        this.parent = optionsScreen;
        this.handler = new CursorOptionsHandler(this);

        initWidgets();
    }

    @Override
    protected void renderBackground(@NotNull GuiGraphics context) {
        // override to remove box
    }

    @Override
    public void renderTexture(@NotNull GuiGraphics context, @NotNull ResourceLocation texture, int x, int y, int u, int v, int hoveredVOffset, int width, int height, int textureWidth, int textureHeight) {
        // override to remove texture
    }

    private void initWidgets() {
        Config.Settings settings = handler.getSettings();

        enableButton = this.addChild(new SelectedCursorToggleWidget(ENABLED_TEXT, settings.isEnabled(), handler::handleEnable));
        scaleSlider = this.addChild(new SelectedCursorSliderWidget(
                SCALE_TEXT, settings.getScale(),
                SCALE_MIN, SCALE_MAX, SCALE_STEP,
                handler::handleChangeScale, CursorOptionsHandler::removeScaleOverride
        ));
        scaleSlider.setTextMapper(SettingsUtil::getAutoText);
        bindHelperButton(scaleSlider);
        xhotSlider = this.addChild(createHotspotSlider(XHOT_TEXT, settings.getXHot(), handler::handleChangeXHot));
        yhotSlider = this.addChild(createHotspotSlider(YHOT_TEXT, settings.getYHot(), handler::handleChangeYHot));
        animateButton = this.addChild(new SelectedCursorToggleWidget(ANIMATE_TEXT, handler.isAnimated(), handler::handlePressAnimate));
        resetAnimation = this.addChild(new SelectedCursorButtonWidget(RESET_ANIMATION_TEXT, handler::handleResetAnimation));
        cursorHotspot = this.addChild(new SelectedCursorHotspotWidget(BOX_WIDGET_TEXTURE_SIZE, this));
        cursorHotspot.setChangeEventListener(handler::handleChangeHotspotWidget);
        cursorTest = this.addChild(new SelectedCursorTestWidget(BOX_WIDGET_TEXTURE_SIZE, this));

        refreshWidgets();
    }

    private SelectedCursorSliderWidget createHotspotSlider(Component prefix, int value, DoubleConsumer onApply) {
        var slider = new SelectedCursorSliderWidget(
                prefix, value,
                HOT_MIN, GLOBAL_HOT_MAX, 1, HOT_UNIT,
                handler.handleChangeHotspots(onApply)
        );
        bindHelperButton(slider);

        return slider;
    }

    private void bindHelperButton(SelectedCursorSliderWidget sliderWidget) {
        var helperButton = this.addChild(new SelectedCursorButtonWidget(HELPER_ICON, HELPER_ICON_SIZE, HELPER_ICON_SIZE, parent::toMoreOptions));
        helperButton.setTooltip(Tooltip.create(Component.translatable(GLOBAL_TEXT_KEY, sliderWidget.getPrefix())));
        sliderWidget.setInactiveHelperButton(helperButton, HELPER_BUTTON_SIZE, HELPER_BUTTON_SIZE);
    }

    private void refreshWidgets() {
        Cursor cursor = handler.getCursor();
        Config.GlobalSettings global = CONFIG.getGlobal();
        Config.Settings settings = CONFIG.getOrCreateCursorSettings(cursor);

        enableButton.setValue(settings.isEnabled());

        if (cursor.isLoaded()) {
            scaleSlider.update(SCALE_MIN, SCALE_MAX, settings.getScale(), !global.isScaleActive());

            int maxHotspot = cursor.getTextureWidth() - 1;
            xhotSlider.update(0, maxHotspot, settings.getXHot(), !global.isXHotActive());
            yhotSlider.update(0, maxHotspot, settings.getYHot(), !global.isYHotActive());

            animateButton.active = cursor instanceof AnimatedCursor;
            boolean animationEnabled = animateButton.active && ((AnimatedCursor) cursor).isAnimated();
            resetAnimation.active = animationEnabled;
            animateButton.setValue(animationEnabled);

            cursorHotspot.setRulerRendered(true, true);
            cursorHotspot.active = !(global.isXHotActive() && global.isYHotActive());
        }

        children().forEach(widget -> widget.setFocused(false));
    }

    @Override
    protected void renderContents(@NotNull GuiGraphics context, int mouseX, int mouseY, float delta) {
        placeWidgets();

        Cursor cursor = handler.getCursor();

        enableButton.render(context, mouseX, mouseY, delta);

        if (cursor.isLoaded()) {
            scaleSlider.renderWidget(context, mouseX, mouseY, delta);
            xhotSlider.renderWidget(context, mouseX, mouseY, delta);
            yhotSlider.renderWidget(context, mouseX, mouseY, delta);

            if (cursor instanceof AnimatedCursor) {
                animateButton.render(context, mouseX, mouseY, delta);
                resetAnimation.render(context, mouseX, mouseY, delta);
            }

            cursorHotspot.render(context, mouseX, mouseY, delta);
            cursorTest.renderWidget(context, mouseX, mouseY, delta);
        }
    }

    private void placeWidgets() {
        boolean isAnimatedCursor = handler.getCursorAsAnimatedCursor().isPresent();

        grid(enableButton, 0, 0);
        grid(scaleSlider, 1, 0);
        grid(xhotSlider, 0, 1);
        grid(yhotSlider, 1, 1);

        if (isAnimatedCursor) {
            grid(animateButton, 0, 2);
            grid(resetAnimation, 1, 2);
            grid(cursorHotspot, 0, 3, true);
            grid(cursorTest, 1, 3, true);
        } else {
            grid(cursorHotspot, 0, 2, true);
            grid(cursorTest, 1, 2, true);
        }
    }

    private void grid(AbstractWidget widget, int gridX, int gridY) {
        grid(widget, gridX, gridY, false);
    }

    private void grid(AbstractWidget widget, int gridX, int gridY, boolean absolute) {
        if (!absolute) {
            widget.setWidth((getWidth() / 2) - GRID_PADDING);
            widget.height = OPTIONS_HEIGHT - GRID_PADDING;
        }
        widget.setX((getX() + ((getWidth() / 2) * gridX)) + 1);
        widget.setY((getY() + (OPTIONS_HEIGHT * (gridY))) + 1);
    }

    public void save() {
        handler.updateSettings();
    }

    public void refresh() {
        refreshWidgets();
    }

    public CursorOptionsScreen parent() {
        return parent;
    }

    private <T extends GuiEventListener> T addChild(T child) {
        this.children.add(child);
        return child;
    }

    @Override
    public @NotNull List<? extends GuiEventListener> children() {
        return this.handler.getCursor().isLoaded() ? this.children : List.of(enableButton);
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public void setX(int x) {
        super.setX(x);
        placeWidgets();
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput builder) {
        // not supported
    }

    @Override
    protected int getInnerHeight() {
        return this.getHeight();
    }

    @Override
    protected double scrollRate() {
        return 0;
    }
}
