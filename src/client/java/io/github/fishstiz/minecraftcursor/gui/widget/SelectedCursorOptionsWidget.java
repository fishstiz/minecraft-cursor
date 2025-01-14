package io.github.fishstiz.minecraftcursor.gui.widget;

import io.github.fishstiz.minecraftcursor.cursor.Cursor;
import io.github.fishstiz.minecraftcursor.gui.screen.CursorOptionsScreen;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.*;
import net.minecraft.text.Text;

import java.util.List;

import static io.github.fishstiz.minecraftcursor.config.CursorConfig.Defaults.*;

public class SelectedCursorOptionsWidget extends ContainerWidget {
    protected static final int OPTIONS_HEIGHT = 26;
    private static final int GRID_PADDING = 4;
    private static final int BOX_WIDGET_TEXTURE_SIZE = 96;
    private static final Text ENABLED_TEXT = Text.translatable("minecraft-cursor.options.enabled");
    private static final Text SCALE_TEXT = Text.translatable("minecraft-cursor.options.scale");
    private static final Text XHOT_TEXT = Text.translatable("minecraft-cursor.options.xhot");
    private static final Text YHOT_TEXT = Text.translatable("minecraft-cursor.options.yhot");
    private static final String HOT_UNIT = "px";
    protected SelectedCursorToggleWidget enableButton;
    protected SelectedCursorSliderWidget scaleSlider;
    protected SelectedCursorSliderWidget xhotSlider;
    protected SelectedCursorSliderWidget yhotSlider;
    protected SelectedCursorHotspotWidget cursorHotspot;
    protected SelectedCursorTestWidget cursorTest;
    protected final CursorOptionsScreen optionsScreen;

    public SelectedCursorOptionsWidget(int width, CursorOptionsScreen optionsScreen) {
        super(0, optionsScreen.layout.getHeaderHeight(), width, optionsScreen.layout.getContentHeight(), Text.empty());
        this.optionsScreen = optionsScreen;
        initWidgets();
    }

    private void initWidgets() {
        Cursor cursor = optionsScreen.getSelectedCursor();

        enableButton = SelectedCursorToggleWidget.build(
                ENABLED_TEXT, cursor.getEnabled(), optionsScreen::onPressEnabled);
        scaleSlider = new SelectedCursorSliderWidget(
                SCALE_TEXT, cursor.getScale(), SCALE_MIN, SCALE_MAX, SCALE_STEP,
                optionsScreen::onChangeScale, this);
        xhotSlider = new SelectedCursorSliderWidget(
                XHOT_TEXT, cursor.getXhot(),
                HOT_MIN, HOT_MAX, 1, HOT_UNIT,
                optionsScreen::onChangeXHot, this);
        yhotSlider = new SelectedCursorSliderWidget(
                YHOT_TEXT, cursor.getYhot(),
                HOT_MIN, HOT_MAX, 1, HOT_UNIT,
                optionsScreen::onChangeYHot, this);
        cursorHotspot = new SelectedCursorHotspotWidget(BOX_WIDGET_TEXTURE_SIZE, this);
        cursorTest = new SelectedCursorTestWidget(BOX_WIDGET_TEXTURE_SIZE, this);

        placeWidgets();
        refreshWidgets();
    }

    private void placeWidgets() {
        grid(enableButton, 0, 0);
        grid(scaleSlider, 1, 0);
        grid(xhotSlider, 0, 1);
        grid(yhotSlider, 1, 1);
        grid(cursorHotspot, 0, 2, true);
        grid(cursorTest, 1, 2, true);
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

        enableButton.setValue(cursor.getEnabled());
        scaleSlider.setValue(cursor.getScale());
        xhotSlider.setValue(cursor.getXhot());
        yhotSlider.setValue(cursor.getYhot());

        children().forEach(widget -> widget.setFocused(false));
    }

    @Override
    public List<? extends Element> children() {
        return List.of(enableButton, scaleSlider, xhotSlider, yhotSlider, cursorHotspot, cursorTest);
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        enableButton.render(context, mouseX, mouseY, delta);
        scaleSlider.renderWidget(context, mouseX, mouseY, delta);
        xhotSlider.renderWidget(context, mouseX, mouseY, delta);
        yhotSlider.renderWidget(context, mouseX, mouseY, delta);
        cursorHotspot.renderWidget(context, mouseX, mouseY, delta);
        cursorTest.renderWidget(context, mouseX, mouseY, delta);
    }

    @Override
    public void setX(int x) {
        super.setX(x);
        placeWidgets();
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
