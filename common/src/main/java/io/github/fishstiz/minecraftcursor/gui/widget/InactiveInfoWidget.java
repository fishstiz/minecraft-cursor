package io.github.fishstiz.minecraftcursor.gui.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class InactiveInfoWidget extends ButtonWidget {
    private static final ResourceLocation ICON = new ResourceLocation("textures/gui/unseen_notification.png");
    private static final int SIZE = 16;
    private static final int ICON_SIZE = 10;
    private static final int MARGIN_RIGHT = 2;
    private final AbstractWidget widget;

    public InactiveInfoWidget(AbstractWidget widget, Tooltip tooltip, Runnable onPress) {
        super(CommonComponents.EMPTY, onPress);

        this.widget = widget;
        this.active = false;
        this.setSize(SIZE, SIZE);
        this.setTooltip(tooltip);
        this.refreshPosition();
        this.refreshVisibility();
    }

    private void refreshVisibility() {
        this.active = !this.widget.active && this.widget.visible;
    }

    private void refreshPosition() {
        int x = (this.widget.getX() + this.widget.getWidth()) - this.getWidth() - MARGIN_RIGHT;
        int y = this.widget.getY() + (this.widget.getHeight() - this.getHeight()) / 2;
        this.setPosition(x, y);
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.refreshPosition();
        this.refreshVisibility();

        if (this.active) {
            PoseStack poseStack = guiGraphics.pose();
            poseStack.pushPose();
            poseStack.translate(0, 0, 1);

            super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);

            int iconX = this.getX() + (this.getWidth() - ICON_SIZE) / 2;
            int iconY = this.getY() + (this.getHeight() - ICON_SIZE) / 2;
            guiGraphics.blit(
                    ICON,
                    iconX, iconY,
                    0,0,
                    ICON_SIZE, ICON_SIZE,
                    ICON_SIZE, ICON_SIZE
            );

            poseStack.popPose();
        } else {
            this.isHovered = false;
        }
    }
}
