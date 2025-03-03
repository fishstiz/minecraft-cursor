package io.github.fishstiz.minecraftcursor.gui.widget;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class SelectedCursorButtonWidget extends Button {
    private ResourceLocation icon;
    private int iconWidth;
    private int iconHeight;

    public SelectedCursorButtonWidget(ResourceLocation icon, int iconWidth, int iconHeight, Runnable onPress) {
        this(Component.empty(), onPress);

        this.icon = icon;
        this.iconWidth = iconWidth;
        this.iconHeight = iconHeight;
    }

    public SelectedCursorButtonWidget(Component message, Runnable onPress) {
        super(0, 0, 150, 20, message, b -> onPress.run(), Button.DEFAULT_NARRATION);
    }

    @Override
    public void onRelease(double mouseX, double mouseY) {
        super.onRelease(mouseX, mouseY);
        setFocused(false);
    }

    @Override
    protected void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.renderWidget(context, mouseX, mouseY, delta);

        if (icon != null) {
            int iconX = getX() + (getWidth() - iconWidth) / 2;
            int iconY = getY() + (getHeight() - iconHeight) / 2;

            context.blit(RenderType::guiTextured, icon,
                    iconX, iconY,
                    0, 0,
                    iconWidth, iconHeight,
                    iconWidth, iconHeight
            );
        }
    }
}
