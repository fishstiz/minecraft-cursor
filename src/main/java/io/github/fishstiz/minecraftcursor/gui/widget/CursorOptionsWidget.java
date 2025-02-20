package io.github.fishstiz.minecraftcursor.gui.widget;

import io.github.fishstiz.minecraftcursor.config.CursorConfig;
import io.github.fishstiz.minecraftcursor.gui.screen.CursorOptionsScreen;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.ContainerWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleConsumer;

import static io.github.fishstiz.minecraftcursor.MinecraftCursor.CONFIG;
import static io.github.fishstiz.minecraftcursor.config.CursorConfig.Settings.Default.*;

public class CursorOptionsWidget extends ContainerWidget {
    private static final int OPTIONS_HEIGHT = 24;
    private static final int GRID_PADDING = 4;
    private static final int BOX_WIDGET_TEXTURE_SIZE = 96;
    private static final int HELPER_BUTTON_SIZE = 16;
    private static final int HELPER_ICON_SIZE = 10;
    private static final Identifier HELPER_ICON = Identifier.ofVanilla("textures/gui/sprites/icon/unseen_notification.png");
    private static final String GLOBAL_TEXT_KEY = "minecraft-cursor.options.global.tooltip";
    private static final Text ANIMATE_TEXT = Text.translatable("minecraft-cursor.options.animate");
    private static final Text RESET_ANIMATION_TEXT = Text.translatable("minecraft-cursor.options.animate-reset");
    private static final String HOT_UNIT = "px";

    public static final Text ENABLED_TEXT = Text.translatable("minecraft-cursor.options.enabled");
    public static final Text SCALE_TEXT = Text.translatable("minecraft-cursor.options.scale");
    public static final Text XHOT_TEXT = Text.translatable("minecraft-cursor.options.xhot");
    public static final Text YHOT_TEXT = Text.translatable("minecraft-cursor.options.yhot");

    private final CursorOptionsScreen parent;
    private final CursorOptionsHandler handler;
    SelectedCursorToggleWidget enableButton;
    SelectedCursorSliderWidget scaleSlider;
    SelectedCursorSliderWidget xhotSlider;
    SelectedCursorSliderWidget yhotSlider;
    SelectedCursorToggleWidget animateButton;
    SelectedCursorButtonWidget resetAnimation;
    SelectedCursorHotspotWidget cursorHotspot;
    SelectedCursorTestWidget cursorTest;

    public CursorOptionsWidget(int width, CursorOptionsScreen optionsScreen) {
        super(0, optionsScreen.layout.getHeaderHeight(), width, optionsScreen.layout.getContentHeight(), Text.empty());

        this.parent = optionsScreen;
        this.handler = new CursorOptionsHandler(this);

        initWidgets();
    }

    private void initWidgets() {
        CursorConfig.Settings settings = handler.getSettings();

        enableButton = new SelectedCursorToggleWidget(ENABLED_TEXT, settings.isEnabled(), handler::handleEnable);
        scaleSlider = new SelectedCursorSliderWidget(
                SCALE_TEXT, settings.getScale(),
                SCALE_MIN, SCALE_MAX, SCALE_STEP,
                handler::handleChangeScale, CursorOptionsHandler::removeScaleOverride);
        bindHelperButton(scaleSlider);

        xhotSlider = createHotspotSlider(XHOT_TEXT, settings.getXHot(), handler::handleChangeXHot);
        yhotSlider = createHotspotSlider(YHOT_TEXT, settings.getYHot(), handler::handleChangeYHot);

        animateButton = new SelectedCursorToggleWidget(ANIMATE_TEXT, handler.isAnimated(), handler::handlePressAnimate);
        resetAnimation = new SelectedCursorButtonWidget(RESET_ANIMATION_TEXT, handler::handleResetAnimation);

        cursorHotspot = new SelectedCursorHotspotWidget(BOX_WIDGET_TEXTURE_SIZE, this);
        cursorTest = new SelectedCursorTestWidget(BOX_WIDGET_TEXTURE_SIZE, this);

        refreshWidgets();
    }

    private SelectedCursorSliderWidget createHotspotSlider(Text prefix, int value, DoubleConsumer onApply) {
        var slider = new SelectedCursorSliderWidget(
                prefix, value,
                HOT_MIN, HOT_MAX, 1, HOT_UNIT,
                handler.handleChangeHotspots(onApply)
        );
        bindHelperButton(slider);

        return slider;
    }

    private void bindHelperButton(SelectedCursorSliderWidget sliderWidget) {
        var helperButton = new SelectedCursorButtonWidget(HELPER_ICON, HELPER_ICON_SIZE, HELPER_ICON_SIZE, parent::toMoreOptions);
        helperButton.setTooltip(Tooltip.of(Text.translatable(GLOBAL_TEXT_KEY, sliderWidget.getPrefix())));
        sliderWidget.setHelperButton(helperButton, HELPER_BUTTON_SIZE, HELPER_BUTTON_SIZE);
    }

    private void refreshWidgets() {
        CursorConfig.GlobalSettings global = CONFIG.getGlobal();
        CursorConfig.Settings settings = handler.getSettings();

        enableButton.setValue(settings.isEnabled());
        scaleSlider.update(settings.getScale(), !global.isScaleActive());
        xhotSlider.update(settings.getXHot(), !global.isXHotActive());
        yhotSlider.update(settings.getYHot(), !global.isYHotActive());

        boolean isAnimated = handler.isAnimated();
        animateButton.active = handler.getCursorAsAnimatedCursor().isPresent();
        resetAnimation.active = isAnimated && enableButton.value;
        animateButton.setValue(isAnimated);

        cursorHotspot.setRulerRendered(true, true);

        children().forEach(widget -> widget.setFocused(false));
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        placeWidgets();

        enableButton.render(context, mouseX, mouseY, delta);
        scaleSlider.renderWidget(context, mouseX, mouseY, delta);
        xhotSlider.renderWidget(context, mouseX, mouseY, delta);
        yhotSlider.renderWidget(context, mouseX, mouseY, delta);

        if (handler.getCursorAsAnimatedCursor().isPresent()) {
            animateButton.render(context, mouseX, mouseY, delta);
            resetAnimation.render(context, mouseX, mouseY, delta);
        }

        cursorHotspot.renderWidget(context, mouseX, mouseY, delta);
        cursorTest.renderWidget(context, mouseX, mouseY, delta);
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

    private void grid(ClickableWidget widget, int gridX, int gridY) {
        grid(widget, gridX, gridY, false);
    }

    private void grid(ClickableWidget widget, int gridX, int gridY, boolean absolute) {
        if (!absolute) {
            widget.setWidth((getWidth() / 2) - GRID_PADDING);
            widget.setHeight(OPTIONS_HEIGHT - GRID_PADDING);
        }
        widget.setX(getX() + ((getWidth() / 2) * gridX));
        widget.setY(getY() + (OPTIONS_HEIGHT * (gridY)));
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

    @Override
    public List<? extends Element> children() {
        List<Element> children = new ArrayList<>(List.of(
                enableButton,
                scaleSlider,
                xhotSlider,
                yhotSlider,
                animateButton,
                resetAnimation,
                cursorHotspot,
                cursorTest
        ));

        addHelperButton(scaleSlider, children);
        addHelperButton(xhotSlider, children);
        addHelperButton(yhotSlider, children);

        return children;
    }

    private void addHelperButton(SelectedCursorSliderWidget slider, List<Element> children) {
        if (slider != null) {
            var helperButton = slider.getHelperButton();
            if (helperButton != null) {
                children.add(helperButton);
            }
        }
    }

    @Override
    protected int getContentsHeightWithPadding() {
        return 0;
    }

    @Override
    protected double getDeltaYPerScroll() {
        return 0;
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
    }
}
