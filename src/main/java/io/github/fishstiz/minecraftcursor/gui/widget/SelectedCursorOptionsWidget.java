package io.github.fishstiz.minecraftcursor.gui.widget;

import io.github.fishstiz.minecraftcursor.cursor.AnimatedCursor;
import io.github.fishstiz.minecraftcursor.cursor.Cursor;
import io.github.fishstiz.minecraftcursor.gui.screen.CursorOptionsScreen;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.ContainerWidget;
import net.minecraft.text.Text;

import java.util.List;
import java.util.function.Consumer;

import static io.github.fishstiz.minecraftcursor.config.CursorConfig.Settings.Default.*;

public class SelectedCursorOptionsWidget extends ContainerWidget {
    protected static final int OPTIONS_HEIGHT = 24;
    private static final int GRID_PADDING = 4;
    private static final int BOX_WIDGET_TEXTURE_SIZE = 96;
    private static final Text ENABLED_TEXT = Text.translatable("minecraft-cursor.options.enabled");
    private static final Text SCALE_TEXT = Text.translatable("minecraft-cursor.options.scale");
    private static final Text XHOT_TEXT = Text.translatable("minecraft-cursor.options.xhot");
    private static final Text YHOT_TEXT = Text.translatable("minecraft-cursor.options.yhot");
    private static final Text ANIMATE_TEXT = Text.translatable("minecraft-cursor.options.animate");
    private static final Text RESET_ANIMATION_TEXT = Text.translatable("minecraft-cursor.options.animate-reset");
    private static final String HOT_UNIT = "px";
    protected final CursorOptionsScreen optionsScreen;
    SelectedCursorToggleWidget enableButton;
    SelectedCursorSliderWidget scaleSlider;
    SelectedCursorSliderWidget xhotSlider;
    SelectedCursorSliderWidget yhotSlider;
    SelectedCursorToggleWidget animateButton;
    SelectedCursorButtonWidget resetAnimation;
    SelectedCursorHotspotWidget cursorHotspot;
    SelectedCursorTestWidget cursorTest;

    public SelectedCursorOptionsWidget(int width, CursorOptionsScreen optionsScreen) {
        super(0, optionsScreen.layout.getHeaderHeight(), width, optionsScreen.layout.getContentHeight(), Text.empty());
        this.optionsScreen = optionsScreen;
        initWidgets();
    }

    private void initWidgets() {
        Cursor cursor = optionsScreen.getSelectedCursor();

        enableButton = SelectedCursorToggleWidget.build(
                ENABLED_TEXT, cursor.isEnabled(), optionsScreen::onPressEnabled);
        scaleSlider = new SelectedCursorSliderWidget(
                SCALE_TEXT, cursor.getScale(), SCALE_MIN, SCALE_MAX, SCALE_STEP,
                optionsScreen::onChangeScale, optionsScreen::removeOverride, this);
        xhotSlider = new SelectedCursorSliderWidget(
                XHOT_TEXT, cursor.getXhot(),
                HOT_MIN, HOT_MAX, 1, HOT_UNIT,
                handleChangeHotspots(optionsScreen::onChangeXHot), this);
        yhotSlider = new SelectedCursorSliderWidget(
                YHOT_TEXT, cursor.getYhot(),
                HOT_MIN, HOT_MAX, 1, HOT_UNIT,
                handleChangeHotspots(optionsScreen::onChangeYHot), this);

        if (cursor instanceof AnimatedCursor animatedCursor) {
            animateButton = SelectedCursorToggleWidget.build(ANIMATE_TEXT, animatedCursor.isAnimated(), this::handlePressAnimate);
            resetAnimation = new SelectedCursorButtonWidget(RESET_ANIMATION_TEXT, this::handleResetAnimation);
        }

        cursorHotspot = new SelectedCursorHotspotWidget(BOX_WIDGET_TEXTURE_SIZE, this);
        cursorTest = new SelectedCursorTestWidget(BOX_WIDGET_TEXTURE_SIZE, this);

        placeWidgets();
        refreshWidgets();
    }

    private void placeWidgets() {
        boolean isAnimatedCursor = optionsScreen.getSelectedCursor() instanceof AnimatedCursor;

        grid(enableButton, 0, 0);
        grid(scaleSlider, 1, 0);
        grid(xhotSlider, 0, 1);
        grid(yhotSlider, 1, 1);

        if (isAnimatedCursor) {
            grid(animateButton, 0, 2);
            grid(resetAnimation, 1, 2);
        }

        grid(cursorHotspot, 0, isAnimatedCursor ? 3 : 2, true);
        grid(cursorTest, 1, isAnimatedCursor ? 3 : 2, true);
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

    public void refreshWidgets() {
        Cursor cursor = optionsScreen.getSelectedCursor();

        enableButton.setValue(cursor.isEnabled());
        scaleSlider.setValue(cursor.getScale());
        xhotSlider.setValue(cursor.getXhot());
        yhotSlider.setValue(cursor.getYhot());

        if (cursor instanceof AnimatedCursor animatedCursor) {
            animateButton.setValue(animatedCursor.isAnimated());
            resetAnimation.active = animatedCursor.isAnimated();
        }

        cursorHotspot.setRulerRendered(true, true);

        children().forEach(widget -> widget.setFocused(false));
        placeWidgets();
    }

    @Override
    public List<? extends Element> children() {
        return List.of(
                enableButton,
                scaleSlider,
                xhotSlider,
                yhotSlider,
                animateButton,
                resetAnimation,
                cursorHotspot,
                cursorTest
        );
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        enableButton.render(context, mouseX, mouseY, delta);
        scaleSlider.renderWidget(context, mouseX, mouseY, delta);
        xhotSlider.renderWidget(context, mouseX, mouseY, delta);
        yhotSlider.renderWidget(context, mouseX, mouseY, delta);

        if (optionsScreen.getSelectedCursor() instanceof AnimatedCursor) {
            animateButton.render(context, mouseX, mouseY, delta);
            resetAnimation.render(context, mouseX, mouseY, delta);
        }

        cursorHotspot.renderWidget(context, mouseX, mouseY, delta);
        cursorTest.renderWidget(context, mouseX, mouseY, delta);
    }

    private Consumer<Double> handleChangeHotspots(Consumer<Double> onChangeHotspot) {
        return value -> {
            onChangeHotspot.accept(value);
            cursorHotspot.setRulerRendered(true, true);
        };
    }

    private void handlePressAnimate(boolean value) {
        optionsScreen.onPressAnimate(value);
        resetAnimation.active = value;
    }

    private void handleResetAnimation() {
        optionsScreen.onResetAnimation();
        cursorHotspot.setRulerRendered(false, false);
    }

    @Override
    public void setX(int x) {
        super.setX(x);
        placeWidgets();
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
    }
}
