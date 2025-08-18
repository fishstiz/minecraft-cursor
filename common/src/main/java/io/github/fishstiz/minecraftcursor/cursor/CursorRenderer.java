package io.github.fishstiz.minecraftcursor.cursor;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.fishstiz.minecraftcursor.util.CursorTypeUtil;
import io.github.fishstiz.minecraftcursor.util.SettingsUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.system.MemoryUtil;

import static org.lwjgl.glfw.GLFW.*;

sealed interface CursorRenderer permits CursorRenderer.Native, CursorRenderer.Virtual {
    void setCursor(@NotNull Cursor cursor);

    void resetCursor();

    void render(Minecraft minecraft, GuiGraphics guiGraphics, int mouseX, int mouseY);

    final class Native implements CursorRenderer {
        @Override
        public void setCursor(@NotNull Cursor cursor) {
            glfwSetCursor(CursorTypeUtil.WINDOW, cursor.getId());
        }

        @Override
        public void resetCursor() {
            glfwSetCursor(CursorTypeUtil.WINDOW, MemoryUtil.NULL);
        }

        @Override
        public void render(Minecraft minecraft, GuiGraphics guiGraphics, int mouseX, int mouseY) {
            // no-op
        }
    }

    final class Virtual implements CursorRenderer {
        private static final float Z = 5000f;
        private ResourceLocation textureLocation;
        private int textureWidth;
        private int textureHeight;
        private int vOffset;
        private int xhot;
        private int yhot;
        private double size;

        Virtual() {
        }

        @Override
        public void setCursor(@NotNull Cursor cursor) {
            this.textureLocation = cursor.getLocation();
            this.textureWidth = cursor.getTextureWidth();
            this.textureHeight = cursor.getTextureHeight();
            this.vOffset = this.textureWidth * cursor.getTextureIndex();
            this.xhot = Math.round((float) cursor.getScale() * cursor.getXHot());
            this.yhot = Math.round((float) cursor.getScale() * cursor.getYHot());
            this.size = this.textureWidth * SettingsUtil.getAutoScale(cursor.getScale());
        }

        @Override
        public void resetCursor() {
            glfwSetInputMode(CursorTypeUtil.WINDOW, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
            this.textureLocation = null;
        }

        @Override
        public void render(Minecraft minecraft, GuiGraphics guiGraphics, int mouseX, int mouseY) {
            if (!minecraft.mouseHandler.isMouseGrabbed() && this.textureLocation != null) {
                int guiScale = (int) minecraft.getWindow().getGuiScale();
                int scaledSize = (int) Math.round(this.size / guiScale);
                int x = mouseX - (this.xhot / guiScale);
                int y = mouseY - (this.yhot / guiScale);

                glfwSetInputMode(CursorTypeUtil.WINDOW, GLFW_CURSOR, GLFW_CURSOR_HIDDEN);

                PoseStack poseStack = guiGraphics.pose();

                poseStack.pushPose();
                poseStack.translate(0f,0f, Z);
                guiGraphics.blit(
                        this.textureLocation,
                        x, y,
                        scaledSize, scaledSize,
                        0, this.vOffset,
                        this.textureWidth, this.textureWidth,
                        this.textureWidth, this.textureHeight
                );
                poseStack.popPose();
            }
        }
    }
}
