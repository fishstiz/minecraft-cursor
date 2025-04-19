package io.github.fishstiz.minecraftcursor.gui.screen;

import io.github.fishstiz.minecraftcursor.CursorManager;
import io.github.fishstiz.minecraftcursor.cursor.Cursor;
import io.github.fishstiz.minecraftcursor.gui.CursorAnimationHelper;
import io.github.fishstiz.minecraftcursor.gui.widget.CursorListWidget;
import io.github.fishstiz.minecraftcursor.gui.widget.CursorOptionsHandler;
import io.github.fishstiz.minecraftcursor.gui.widget.CursorOptionsWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static io.github.fishstiz.minecraftcursor.MinecraftCursor.CONFIG;

public class CursorOptionsScreen extends Screen {
    private static final Component TITLE_TEXT = Component.translatable("minecraft-cursor.options");
    private static final int CURSORS_COLUMN_WIDTH = 96;
    private static final int SELECTED_CURSOR_COLUMN_WIDTH = 200;
    private static final int COLUMN_GAP = 8;

    final Screen previousScreen;
    public final CursorAnimationHelper animationHelper = new CursorAnimationHelper();
    private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
    private final List<Cursor> cursors;
    private Cursor selectedCursor;
    private CursorListWidget list;
    @Nullable CursorOptionsWidget options;

    public CursorOptionsScreen(Screen previousScreen) {
        super(TITLE_TEXT);

        this.previousScreen = previousScreen;

        cursors = CursorManager.INSTANCE.getLoadedCursors();
    }

    @Override
    protected void init() {
        selectedCursor = this.cursors.getFirst();

        this.layout.addTitleHeader(this.title, this.font);
        this.layout.addToContents(initContents(LinearLayout.horizontal().spacing(COLUMN_GAP)));
        this.layout.addToFooter(initFooter(LinearLayout.horizontal().spacing(COLUMN_GAP)));
        this.layout.visitWidgets(this::addRenderableWidget);
        this.repositionElements();
    }

    protected LinearLayout initContents(LinearLayout contents) {
        this.list = contents.addChild(new CursorListWidget(
                minecraft,
                CURSORS_COLUMN_WIDTH,
                layout.getContentHeight(),
                layout.getHeaderHeight(),
                this
        ));
        this.options = contents.addChild(new CursorOptionsWidget(0, SELECTED_CURSOR_COLUMN_WIDTH,
                layout.getContentHeight(),
                layout.getHeaderHeight(),
                this
        ));
        return contents;
    }

    protected LinearLayout initFooter(LinearLayout footer) {
        footer.addChild(Button.builder(Component.translatable("minecraft-cursor.options.more").append("..."),
                btn -> toMoreOptions()).build());
        footer.addChild(Button.builder(CommonComponents.GUI_DONE, btn -> this.onClose()).build());
        return footer;
    }

    @Override
    protected void repositionElements() {
        this.layout.arrangeElements();
        this.list.setHeight(layout.getContentHeight());
        this.list.setY(layout.getHeaderHeight());

        if (this.options != null) {
            this.options.setHeight(layout.getContentHeight());
            this.options.setY(layout.getHeaderHeight());
        }
    }

    public void selectCursor(Cursor cursor) {
        updateSelectedCursorConfig();
        selectedCursor = cursor;

        if (this.options != null) {
            this.options.refresh();
        }
    }

    private void updateSelectedCursorConfig() {
        if (this.options != null) {
            this.options.save();
        }
    }

    public Cursor getSelectedCursor() {
        return selectedCursor;
    }

    public List<Cursor> getCursors() {
        return cursors;
    }

    public void toMoreOptions() {
        if (minecraft != null) {
            minecraft.setScreen(new MoreOptionsScreen(this));
        }
    }

    @Override
    public void onClose() {
        CursorOptionsHandler.removeScaleOverride();
        CONFIG.save();

        if (this.minecraft != null) {
            this.minecraft.setScreen(previousScreen);
        }
    }
}
