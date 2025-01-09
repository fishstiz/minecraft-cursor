package io.github.fishstiz.minecraftcursor.gui;

import io.github.fishstiz.minecraftcursor.MinecraftCursorHandler;
import io.github.fishstiz.minecraftcursor.config.MinecraftCursorConfigDefinition;
import io.github.fishstiz.minecraftcursor.config.MinecraftCursorConfigManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.widget.OptionListWidget;
import net.minecraft.text.Text;

import java.util.Objects;

public class MinecraftCursorOptionsScreen extends GameOptionsScreen {
    private final MinecraftCursorSimpleOptions cursorOptions;
    private final MinecraftCursorConfigManager configManager;
    private final MinecraftCursorHotspot cursorHotspot;

    public MinecraftCursorOptionsScreen(Screen previous, MinecraftCursorConfigManager configManager, MinecraftCursorHandler cursorHandler) {
        super(previous, null, Text.translatable("minecraft-cursor.options"));

        this.configManager = configManager;
        this.cursorHotspot = new MinecraftCursorHotspot(cursorHandler);
        this.cursorOptions = new MinecraftCursorSimpleOptions(cursorHandler, configManager, this.cursorHotspot);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        if (this.body == null) {
            return;
        }

        boolean showHotspot = false;

        HotspotVisibility visibility = HotspotVisibility.getVisibility(this.cursorOptions.getShowHotspot().getValue());

        if (visibility == HotspotVisibility.MODIFIED) {
            boolean isXHotModified = !Objects.equals(this.cursorOptions.getXhot().getValue(), MinecraftCursorConfigDefinition.X_HOTSPOT.defaultValue);
            boolean isYHotModified = !Objects.equals(this.cursorOptions.getYhot().getValue(), MinecraftCursorConfigDefinition.Y_HOTSPOT.defaultValue);

            showHotspot = isXHotModified || isYHotModified;
        } else if (visibility == HotspotVisibility.ALWAYS) {
            showHotspot = true;
        }

        if (showHotspot && this.cursorOptions.getEnabled().getValue()) {
            int cursorScale = (int) Math.round(this.cursorOptions.getScale().getValue());
            cursorHotspot.render(context, mouseX, mouseY, cursorScale);
        }
    }

    @Override
    protected void initBody() {
        this.body = this.layout.addBody(new OptionListWidget(this.client, this.width, this));
        this.addOptions();
    }

    @Override
    protected void addOptions() {
        if (this.body == null) {
            return;
        }

        this.body.addAll(
                this.cursorOptions.getEnabled(),
                this.cursorOptions.getScale(),
                this.cursorOptions.getXhot(),
                this.cursorOptions.getYhot(),
                this.cursorOptions.getShowHotspot(),
                this.cursorOptions.getHotspotColor()
        );
    }

    @Override
    public void close() {
        if (this.body != null) {
            boolean hasChanges = false;

            if (this.cursorOptions.getEnabled().getValue() != configManager.getEnabled()) {
                hasChanges = true;
                configManager.setEnabled(this.cursorOptions.getEnabled().getValue());
            }
            if (this.cursorOptions.getScale().getValue() != configManager.getScale()) {
                hasChanges = true;
                configManager.setScale(this.cursorOptions.getScale().getValue());
            }
            if (this.cursorOptions.getXhot().getValue() != configManager.getXhot()) {
                hasChanges = true;
                configManager.setXhot(this.cursorOptions.getXhot().getValue());
            }
            if (this.cursorOptions.getYhot().getValue() != configManager.getYhot()) {
                hasChanges = true;
                configManager.setYhot(this.cursorOptions.getYhot().getValue());
            }

            if (hasChanges) {
                configManager.saveConfig();
            }
        }

        super.close();
    }
}
