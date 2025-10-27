package io.github.fishstiz.minecraftcursor.gui.screen;

import io.github.fishstiz.minecraftcursor.CursorResourceLoader;
import io.github.fishstiz.minecraftcursor.api.CursorController;
import io.github.fishstiz.minecraftcursor.cursor.CursorManager;
import io.github.fishstiz.minecraftcursor.MinecraftCursor;
import io.github.fishstiz.minecraftcursor.api.CursorType;
import io.github.fishstiz.minecraftcursor.cursor.Cursor;
import io.github.fishstiz.minecraftcursor.gui.CursorAnimationHelper;
import io.github.fishstiz.minecraftcursor.gui.screen.panel.*;
import io.github.fishstiz.minecraftcursor.gui.widget.ContainerEventHandlerPatch;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class ConfigurationScreen extends CatalogBrowserScreen implements ContainerEventHandlerPatch {
    private static final Component GLOBAL_TEXT = Component.translatable("minecraft-cursor.options.global");
    private static final Component ADAPTIVE_TEXT = Component.translatable("minecraft-cursor.options.adapt");
    private static final Component COMPAT_TEXT = Component.translatable("minecraft-cursor.options.compat");
    private static final Component CURSORS_TEXT = Component.translatable("minecraft-cursor.options.cursor-types");
    private static final Component DEBUG_TEXT = Component.translatable("minecraft-cursor.options.debug");
    private static final int SPACING = 8;
    private static final int HEADER_HEIGHT = 20;
    private static final int SIDEBAR_WIDTH = 140;
    private static final int MAX_CONTENT_WIDTH = 128 * 2 + SPACING;
    private static final int LIST_CURSOR_SIZE = 16;
    private static final int BUSY_OVERRIDE = -10;
    private static final CatalogItem GLOBAL_CATEGORY = new CatalogItem("global", GLOBAL_TEXT);
    private static final CatalogItem CURSORS_CATEGORY = new CatalogItem("cursors", CURSORS_TEXT);
    private final CursorAnimationHelper animationHelper = new CursorAnimationHelper();
    private CatalogItem defaultItem;
    private CompletableFuture<Void> refreshFuture;

    public ConfigurationScreen(Screen previous) {
        super(Component.translatable("minecraft-cursor.options"), HEADER_HEIGHT, SIDEBAR_WIDTH, MAX_CONTENT_WIDTH, SPACING, previous);
    }

    @Override
    public void onClose() {
        CursorController.getInstance().removeOverride(BUSY_OVERRIDE);
        MinecraftCursor.CONFIG.save();
        super.onClose();
    }

    @Override
    protected void initItems() {
        Collection<Cursor> cursors = CursorManager.INSTANCE.getCursors();
        List<CompletableFuture<Void>> futures = new ArrayList<>(cursors.size());
        for (Cursor cursor : cursors) {
            if (cursor.isLazy()) {
                futures.add(CompletableFuture.runAsync(() -> CursorResourceLoader.loadCursorTexture(cursor), Util.backgroundExecutor()));
            }
        }
        CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();

        this.addGlobalItems();
        this.addAdaptiveItems();
        this.addCursorItems();
        this.addCompatibilityItems();
        this.addDebugItems();
    }

    @Override
    protected void postInit() {
        this.selectItem(this.defaultItem);
    }

    @Override
    protected void refreshItemsAndPanel() {
        if (this.refreshFuture != null && !this.refreshFuture.isDone()) {
            return;
        }

        this.getRefreshButton().active = false;
        CursorController.getInstance().overrideCursor(CursorType.BUSY, BUSY_OVERRIDE);
        this.refreshFuture = CompletableFuture.runAsync(CursorResourceLoader::reload, Util.backgroundExecutor())
                .thenRunAsync(
                        () -> {
                            this.addCursorItems();
                            super.refreshItemsAndPanel();
                            this.getRefreshButton().active = true;
                            CursorController.getInstance().removeOverride(BUSY_OVERRIDE);
                        },
                        this.minecraft
                );
    }

    private void addGlobalItems() {
        this.addCategoryOnly(GLOBAL_CATEGORY, new GlobalOptionsPanel(this::refreshCursors));
    }

    private void addAdaptiveItems() {
        this.addCategoryOnly(new CatalogItem("adaptive", ADAPTIVE_TEXT), new AdaptiveOptionsPanel(ADAPTIVE_TEXT, this.animationHelper, this::refreshCursors));
    }

    private void addCompatibilityItems() {
        this.addCategoryOnly(new CatalogItem("compatibility", COMPAT_TEXT), new CompatibilityOptionsPanel(COMPAT_TEXT));
    }

    private void addCursorItems() {
        for (CatalogItem cursorItem : this.createCursorItems()) {
            if (CursorType.DEFAULT.getKey().equals(cursorItem.id())) {
                this.defaultItem = cursorItem;
            }

            this.addOrUpdateItem(CURSORS_CATEGORY, cursorItem, new CursorOptionsPanel(
                    this.animationHelper,
                    this::refreshCursors,
                    GLOBAL_CATEGORY,
                    Objects.requireNonNull(CursorManager.INSTANCE.getCursor(cursorItem.id()))
            ));
        }
    }

    private void addDebugItems() {
        this.addCategoryOnly(new CatalogItem("debug", DEBUG_TEXT), new DebugOptionsPanel(DEBUG_TEXT));
    }

    private int renderListCursor(GuiGraphics guiGraphics, Font font, CatalogItem item, LayoutElement bounds, int spacing, int mouseX, int mouseY, float partialTick) {
        Cursor cursor = CursorManager.INSTANCE.getCursor(item.id());

        if (cursor != null && cursor.isLoaded()) {
            int prefixX = bounds.getX() + spacing;
            int prefixY = bounds.getY() + (bounds.getHeight() - LIST_CURSOR_SIZE) / 2;
            this.animationHelper.drawSprite(guiGraphics, cursor, prefixX, prefixY, LIST_CURSOR_SIZE);
        }

        return LIST_CURSOR_SIZE;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return ContainerEventHandlerPatch.super.mouseReleased(mouseX, mouseY, button);
    }

    private void refreshCursors() {
        this.addCursorItems();
        this.refreshItems();
    }

    private List<CatalogItem> createCursorItems() {
        return CursorManager.INSTANCE.getCursors()
                .stream()
                .map(cursor -> new CatalogItem(
                        cursor.getTypeKey(),
                        cursor.getText().copy().withStyle(getCursorFormat(cursor)),
                        this::renderListCursor
                ))
                .toList();
    }

    private static ChatFormatting getCursorFormat(@NotNull Cursor cursor) {
        if (cursor.isEnabled()) {
            return ChatFormatting.WHITE;
        }
        if (cursor.isLoaded()) {
            return ChatFormatting.GRAY;
        }
        return ChatFormatting.DARK_GRAY;
    }
}
