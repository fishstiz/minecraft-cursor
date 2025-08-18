package io.github.fishstiz.minecraftcursor.gui.screen.panel;

import io.github.fishstiz.minecraftcursor.compat.ExternalCursorTracker;
import io.github.fishstiz.minecraftcursor.cursor.CursorManager;
import io.github.fishstiz.minecraftcursor.gui.widget.OptionsListWidget;
import io.github.fishstiz.minecraftcursor.config.Flag;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static io.github.fishstiz.minecraftcursor.MinecraftCursor.CONFIG;

public class CompatibilityOptionsPanel extends AbstractOptionsPanel {
    private static final Component AGGRESSIVE_TEXT = Component.translatable("minecraft-cursor.options.compat.aggressive_cursor");
    private static final Tooltip AGGRESSIVE_INFO = Tooltip.create(Component.translatable("minecraft-cursor.options.compat.aggressive_cursor.info"));
    private static final Component REMAP_TEXT = Component.translatable("minecraft-cursor.options.compat.remap_cursors");
    private static final Tooltip REMAP_INFO = Tooltip.create(Component.translatable(Flag.REMAP.getInfoKey()));
    private static final Tooltip REMAP_EMPTY_INFO = Tooltip.create(Component.translatable("minecraft-cursor.options.compat.remap_cursors.empty"));
    private static final Component VIRTUAL_TEXT = Component.translatable("minecraft-cursor.options.compat.virtual_mode");
    private static final Tooltip VIRTUAL_INFO = Tooltip.create(Component.translatable("minecraft-cursor.options.compat.virtual_mode.info"));
    private OptionsListWidget optionsList;

    public CompatibilityOptionsPanel(Component title) {
        super(title);
    }

    @Override
    protected void initContents() {
        this.optionsList = new OptionsListWidget(this.getMinecraft(), this.getFont(), this.getSpacing());

        this.optionsList.addToggle(
                CONFIG.isAggressiveCursor(),
                CONFIG::setAggressiveCursor,
                this.index(AGGRESSIVE_TEXT),
                AGGRESSIVE_INFO,
                true
        );
        this.optionsList.addToggle(
                Flag.REMAP.isEnabled() && CONFIG.isRemapCursorsEnabled(),
                CONFIG::setRemapCursorsEnabled,
                this.index(REMAP_TEXT),
                !Flag.REMAP.isEnabled() || ExternalCursorTracker.isTracking() ? REMAP_INFO : REMAP_EMPTY_INFO,
                ExternalCursorTracker.isTracking()
        );
        this.optionsList.addToggle(
                CursorManager.INSTANCE.isVirtual(),
                value -> {
                    CursorManager.INSTANCE.toggleVirtual();
                    CONFIG.setVirtualMode(CursorManager.INSTANCE.isVirtual());
                },
                this.index(VIRTUAL_TEXT),
                VIRTUAL_INFO,
                true
        );

        this.optionsList.search(this.getSearch());

        this.addRenderableWidget(this.optionsList);
    }

    @Override
    protected void repositionContents(int x, int y) {
        if (this.optionsList != null) {
            this.optionsList.setSize(this.getWidth(), this.computeMaxHeight(y));
            this.optionsList.setPosition(x, y);
        }
    }

    @Override
    protected void searched(@NotNull String search, @Nullable Component matched) {
        if (this.optionsList != null) {
            this.optionsList.search(search);
        }
    }
}
