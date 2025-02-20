package io.github.fishstiz.minecraftcursor.gui.widget;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class SelectedCursorButtonWidget extends ButtonWidget {
    private Identifier icon;
    private int iconWidth;
    private int iconHeight;

    public SelectedCursorButtonWidget(Identifier icon, int iconWidth, int iconHeight, Runnable onPress) {
        this(Text.empty(), onPress);

        this.icon = icon;
        this.iconWidth = iconWidth;
        this.iconHeight = iconHeight;
    }

    public SelectedCursorButtonWidget(Text message, Runnable onPress) {
        super(0, 0, 150, 20, message, b -> onPress.run(), ButtonWidget.DEFAULT_NARRATION_SUPPLIER);
    }

    @Override
    public void onRelease(double mouseX, double mouseY) {
        super.onRelease(mouseX, mouseY);
        setFocused(false);
    }


    @Override
    protected void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
        super.renderButton(context, mouseX, mouseY, delta);

        if (icon != null) {
            int iconX = getX() + (getWidth() - iconWidth) / 2;
            int iconY = getY() + (getHeight() - iconHeight) / 2;

            context.drawTexture(
                    icon,
                    iconX, iconY,
                    0, 0,
                    iconWidth, iconHeight,
                    iconWidth, iconHeight
            );
        }
    }
}
