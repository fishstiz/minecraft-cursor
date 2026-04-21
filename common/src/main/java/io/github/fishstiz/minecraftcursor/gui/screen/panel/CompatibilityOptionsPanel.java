package io.github.fishstiz.minecraftcursor.gui.screen.panel;

import io.github.fishstiz.minecraftcursor.compat.ExternalCursorTracker;
import io.github.fishstiz.minecraftcursor.cursor.CursorManager;
import io.github.fishstiz.minecraftcursor.gui.widget.OptionsListWidget;
import io.github.fishstiz.minecraftcursor.config.Flag;
import io.github.fishstiz.minecraftcursor.platform.Services;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static io.github.fishstiz.minecraftcursor.MinecraftCursor.CONFIG;

public class CompatibilityOptionsPanel extends AbstractOptionsPanel {
    private static final Tooltip RESTART_TO_APPLY_INFO = Tooltip.create(Component.translatable("minecraft-cursor.options.restart_to_apply"));
    private static final Component AGGRESSIVE_TEXT = Component.translatable("minecraft-cursor.options.compat.aggressive_cursor");
    private static final Tooltip AGGRESSIVE_INFO = Tooltip.create(Component.translatable("minecraft-cursor.options.compat.aggressive_cursor.info"));
    private static final Component REMAP_TEXT = Component.translatable("minecraft-cursor.options.compat.remap_cursors");
    private static final Tooltip REMAP_INFO = Tooltip.create(Component.translatable(Flag.REMAP.getInfoKey()));
    private static final Tooltip REMAP_EMPTY_INFO = Tooltip.create(Component.translatable("minecraft-cursor.options.compat.remap_cursors.empty"));
    private static final Component VIRTUAL_TEXT = Component.translatable("minecraft-cursor.options.compat.virtual_mode");
    private static final Tooltip VIRTUAL_INFO = Tooltip.create(Component.translatable("minecraft-cursor.options.compat.virtual_mode.info"));
    private static final Component IMGUI_DISABLE_CURSOR_CHANGES_TEXT = Component.translatable("minecraft-cursor.options.compat.imgui.disable_cursor_changes");
    private static final boolean VEIL_LOADED = Services.PLATFORM.isModLoaded("veil");
    private static final Component VEIL_TEXT = Component.translatable("minecraft-cursor.options.compat.veil")
            .withStyle(ChatFormatting.BOLD)
            .withStyle(VEIL_LOADED ? ChatFormatting.WHITE : ChatFormatting.DARK_GRAY);
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
                CursorManager.INSTANCE.isVirtual(),
                value -> {
                    CursorManager.INSTANCE.toggleVirtual();
                    CONFIG.setVirtualMode(CursorManager.INSTANCE.isVirtual());
                },
                this.index(VIRTUAL_TEXT),
                VIRTUAL_INFO,
                true
        );
        this.optionsList.addToggle(
                Flag.REMAP.isEnabled() && CONFIG.isRemapCursorsEnabled(),
                CONFIG::setRemapCursorsEnabled,
                this.index(REMAP_TEXT),
                !Flag.REMAP.isEnabled() || ExternalCursorTracker.isTracking() ? REMAP_INFO : REMAP_EMPTY_INFO,
                Flag.REMAP.isEnabled()
        );

        this.optionsList.addWidget(this.createModSectionTitle(VEIL_TEXT, VEIL_LOADED));
        this.optionsList.addToggle(
                VEIL_LOADED && CONFIG.isVeilCursorChangesDisabled(),
                CONFIG::setVeilCursorChangesDisabled,
                Component.literal("└ ").append(this.index(IMGUI_DISABLE_CURSOR_CHANGES_TEXT)),
                VEIL_LOADED ? RESTART_TO_APPLY_INFO : Tooltip.create(Component.translatable("minecraft-cursor.options.compat.mod_not_loaded", VEIL_TEXT)),
                VEIL_LOADED
        );

        this.optionsList.search(this.getSearch());

        this.addRenderableWidget(this.optionsList);
    }

    private StringWidget createModSectionTitle(Component modName, boolean loaded) {
        StringWidget title = new StringWidget(this.index(modName), this.getFont()).alignLeft();
        title.setTooltip(Tooltip.create(Component.translatable(
                loaded ? "minecraft-cursor.options.compat.mod_loaded" : "minecraft-cursor.options.compat.mod_not_loaded",
                modName
        )));
        return title;
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
