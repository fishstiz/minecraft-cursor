package io.github.fishstiz.minecraftcursor.gui.screen.panel;

import io.github.fishstiz.minecraftcursor.cursor.resolver.CursorTypeResolver;
import io.github.fishstiz.minecraftcursor.gui.widget.ButtonWidget;
import io.github.fishstiz.minecraftcursor.gui.widget.OptionsListWidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DebugOptionsPanel extends AbstractOptionsPanel {
    private static final String CLEAR_CACHE_KEY = "minecraft-cursor.options.debug.cache.clear";
    private static final String ISSUES_LINK = "https://github.com/fishstiz/minecraft-cursor/issues";
    private static final String WIKI_LINK = "https://fishstiz.github.io/minecraft-cursor-wiki/resource-pack/getting-started";
    private static final Component INSPECT_TEXT = Component.translatable("minecraft-cursor.options.debug.inspect");
    private static final Component CACHE_TEXT = Component.literal(Component.translatable(CLEAR_CACHE_KEY).getString().replace("\\:.*", ""));
    private static final Component REPORT_ISSUES_TEXT = Component.translatable("minecraft-cursor.options.debug.report_issues");
    private static final Component OPEN_WIKI_TEXT = Component.translatable("minecraft-cursor.options.debug.open_wiki");
    private OptionsListWidget optionsList;
    private ButtonWidget cacheButton;
    private int previousCacheSize = CursorTypeResolver.INSTANCE.cacheSize();

    public DebugOptionsPanel(Component title) {
        super(title);
    }

    @Override
    protected void initContents() {
        this.optionsList = new OptionsListWidget(this.getMinecraft(), this.getFont(), this.getSpacing());

        this.optionsList.addToggle(
                CursorTypeResolver.INSTANCE.getInspector().isInspecting(),
                v -> CursorTypeResolver.INSTANCE.toggleInspector(),
                this.index(INSPECT_TEXT),
                null,
                true
        );

        this.previousCacheSize = -1;
        this.cacheButton = new ButtonWidget(createCacheText(), CursorTypeResolver.INSTANCE::clearCache);
        this.optionsList.addWidget(this.cacheButton);
        this.index(CACHE_TEXT);

        this.optionsList.addWidget(new ButtonWidget(
                this.index(OPEN_WIKI_TEXT),
                ConfirmLinkScreen.confirmLink(this.getScreen(), WIKI_LINK, true)
        ));
        this.optionsList.addWidget(new ButtonWidget(
                this.index(REPORT_ISSUES_TEXT),
                ConfirmLinkScreen.confirmLink(this.getScreen(), ISSUES_LINK, true)
        ));

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

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (this.cacheButton != null && CursorTypeResolver.INSTANCE.cacheSize() != this.previousCacheSize) {
            this.previousCacheSize = CursorTypeResolver.INSTANCE.cacheSize();
            this.cacheButton.setMessage(createCacheText());
        }

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    private static Component createCacheText() {
        return Component.translatable(CLEAR_CACHE_KEY, CursorTypeResolver.INSTANCE.cacheSize());
    }
}
