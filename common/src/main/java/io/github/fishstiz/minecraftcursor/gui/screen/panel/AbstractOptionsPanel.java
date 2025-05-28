package io.github.fishstiz.minecraftcursor.gui.screen.panel;

import io.github.fishstiz.minecraftcursor.CursorLoader;
import io.github.fishstiz.minecraftcursor.cursor.Cursor;
import io.github.fishstiz.minecraftcursor.gui.screen.CatalogBrowserScreen;
import io.github.fishstiz.minecraftcursor.gui.widget.ContainerEventHandlerPatch;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractOptionsPanel extends CatalogBrowserScreen.ContentPanel implements ContainerEventHandlerPatch {
    protected static final Component ENABLE_TEXT = Component.translatable("minecraft-cursor.options.enabled");
    protected static final Component SCALE_TEXT = Component.translatable("minecraft-cursor.options.scale");
    protected static final Component GUI_SCALE_TEXT = Component.translatable("minecraft-cursor.options.scale.gui");
    protected static final Component XHOT_TEXT = Component.translatable("minecraft-cursor.options.xhot");
    protected static final Component YHOT_TEXT = Component.translatable("minecraft-cursor.options.yhot");
    protected static final Component HOTSPOT_SUFFIX = Component.translatable("minecraft-cursor.options.hotspot-suffix");
    private final Component title;
    private StringWidget titleWidget;

    protected AbstractOptionsPanel(Component title) {
        this.title = withBold(title);
    }

    protected abstract void initContents();

    protected abstract void repositionContents(int x, int y);

    @Override
    protected final void init() {
        this.titleWidget = new StringWidget(this.getHeaderWidth(), this.getHeaderHeight(), this.title, this.getFont()).alignLeft();
        this.addRenderableIndexedWidget(this.titleWidget);
        this.initContents();
    }

    @Override
    protected final void repositionElements() {
        this.titleWidget.setWidth(this.getHeaderWidth());
        this.titleWidget.setPosition(this.getX(), this.getY());

        this.repositionContents(this.getX(), this.getY() + this.titleWidget.getHeight() + this.getSpacing());
    }

    protected int computeMaxHeight(int top) {
        return Math.max(0, this.getHeight() - (top - this.getY()));
    }

    protected boolean loadCursor(@NotNull Cursor deferredCursor) {
        if (deferredCursor.isLoaded()) {
            throw new IllegalStateException("Cursor is already loaded");
        }
        if (CursorLoader.loadCursorTexture(this.getMinecraft().getResourceManager(), deferredCursor)) {
            return true;
        }
        this.getMinecraft().getToasts().addToast(SystemToast.multiline(
                this.getMinecraft(),
                SystemToast.SystemToastIds.PACK_LOAD_FAILURE,
                Component.translatable("resourcePack.load_fail"),
                Component.translatable("minecraft-cursor.options.global.deferred_loading.fail", deferredCursor.getText())
        ));
        return false;
    }

    protected void refreshWidgets() {
        this.clearWidgets();
        this.init();
    }

    @Override
    protected void added() {
        this.refreshWidgets();
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return ContainerEventHandlerPatch.super.mouseReleased(mouseX, mouseY, button);
    }

    private static MutableComponent withBold(Component text) {
        return text.copy().withStyle(style -> style.withBold(true));
    }
}
