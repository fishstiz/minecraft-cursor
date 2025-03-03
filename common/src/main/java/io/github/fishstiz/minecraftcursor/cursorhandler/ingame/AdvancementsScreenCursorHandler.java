package io.github.fishstiz.minecraftcursor.cursorhandler.ingame;

import io.github.fishstiz.minecraftcursor.MinecraftCursor;
import io.github.fishstiz.minecraftcursor.api.CursorHandler;
import io.github.fishstiz.minecraftcursor.api.CursorType;
import io.github.fishstiz.minecraftcursor.mixin.access.AdvancementsScreenAccessor;
import net.minecraft.client.gui.screens.advancements.AdvancementTab;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;

public class AdvancementsScreenCursorHandler implements CursorHandler<AdvancementsScreen> {
    @Override
    public CursorType getCursorType(AdvancementsScreen advancementsScreen, double mouseX, double mouseY) {
        if (!MinecraftCursor.CONFIG.isAdvancementTabsEnabled()) return CursorType.DEFAULT;

        int x = (advancementsScreen.width - AdvancementsScreen.WINDOW_WIDTH) / 2;
        int y = (advancementsScreen.height - AdvancementsScreen.WINDOW_HEIGHT) / 2;
        for (AdvancementTab tab : ((AdvancementsScreenAccessor) advancementsScreen).getTabs().values()) {
            if (tab.isMouseOver(x, y, mouseX, mouseY)
                    && tab != ((AdvancementsScreenAccessor) advancementsScreen).getSelectedTab()) {
                return CursorType.POINTER;
            }
        }
        return CursorType.DEFAULT;
    }
}
