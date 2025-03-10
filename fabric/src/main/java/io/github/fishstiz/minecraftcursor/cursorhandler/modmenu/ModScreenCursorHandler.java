package io.github.fishstiz.minecraftcursor.cursorhandler.modmenu;

import com.terraformersmc.modmenu.gui.ModsScreen;
import com.terraformersmc.modmenu.gui.widget.ModListWidget;
import com.terraformersmc.modmenu.gui.widget.entries.ModListEntry;
import io.github.fishstiz.minecraftcursor.api.CursorHandler;
import io.github.fishstiz.minecraftcursor.api.CursorType;
import net.minecraft.client.gui.components.events.GuiEventListener;

import java.util.Optional;

public class ModScreenCursorHandler implements CursorHandler<ModsScreen> {
    public static final int ICON_SIZE = 32;
    public static final int ENTRY_OFFSET_X = 5;

    @Override
    public CursorType getCursorType(ModsScreen modsScreen, double mouseX, double mouseY) {
        Optional<GuiEventListener> hoveredElementOpt = modsScreen.getChildAt(mouseX, mouseY);

        if (hoveredElementOpt.isEmpty()) {
            return CursorType.DEFAULT;
        }

        GuiEventListener hoveredElement = hoveredElementOpt.get();
        if (!(hoveredElement instanceof ModListWidget modListWidget)) {
            return CursorType.DEFAULT;
        }

        for (ModListEntry entry : modListWidget.children()) {
            if (entry.isMouseOver(mouseX, mouseY) && modsScreen.getModHasConfigScreen().get(entry.mod.getId())) {
                if (mouseX >= modListWidget.getRowLeft() && mouseX <= modListWidget.getRowLeft() + ICON_SIZE + ENTRY_OFFSET_X) {
                    return CursorType.POINTER;
                }
            }
        }

        return CursorType.DEFAULT;
    }
}
