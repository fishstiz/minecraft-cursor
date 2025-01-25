package io.github.fishstiz.minecraftcursor.registry.gui.ingame;

import io.github.fishstiz.minecraftcursor.cursor.CursorType;
import io.github.fishstiz.minecraftcursor.mixin.client.access.AdvancementsScreenAccessor;
import io.github.fishstiz.minecraftcursor.registry.CursorTypeRegistry;
import io.github.fishstiz.minecraftcursor.registry.gui.GuiCursorHandler;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.advancement.AdvancementTab;
import net.minecraft.client.gui.screen.advancement.AdvancementsScreen;

public class AdvancementScreenCursor extends GuiCursorHandler {
    private AdvancementScreenCursor() {
    }

    public static void register(CursorTypeRegistry cursorTypeRegistry) {
        cursorTypeRegistry.register(AdvancementsScreen.class, new AdvancementScreenCursor()::getCursorType);
    }

    @Override
    protected CursorType getCursorType(Element element, double mouseX, double mouseY) {
        AdvancementsScreen advancementsScreen = (AdvancementsScreen) element;
        int x = (advancementsScreen.width - AdvancementsScreen.WINDOW_WIDTH) / 2;
        int y = (advancementsScreen.height - AdvancementsScreen.WINDOW_HEIGHT) / 2;
        for (AdvancementTab tab : ((AdvancementsScreenAccessor) advancementsScreen).getTabs().values()) {
            if (tab.isClickOnTab(x, y, mouseX, mouseY)
                    && tab != ((AdvancementsScreenAccessor) advancementsScreen).getSelectedTab()) {
                return CursorType.POINTER;
            }
        }
        return CursorType.DEFAULT;
    }
}
