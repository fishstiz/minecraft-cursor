package io.github.fishstiz.minecraftcursor.cursorhandler.modmenu;

import com.terraformersmc.modmenu.gui.ModsScreen;
import com.terraformersmc.modmenu.gui.widget.ModListWidget;
import com.terraformersmc.modmenu.gui.widget.entries.ModListEntry;
import io.github.fishstiz.minecraftcursor.api.CursorType;
import io.github.fishstiz.minecraftcursor.api.CursorHandler;
import net.minecraft.client.gui.Element;

import java.util.Optional;

public class ModScreenCursorHandler implements CursorHandler<ModsScreen> {
    public static final int ICON_SIZE = 32;
    public static final int ENTRY_OFFSET_X = 5;

    @Override
    public CursorType getCursorType(ModsScreen modsScreen, double mouseX, double mouseY) {
        Optional<Element> hoveredElementOpt = modsScreen.hoveredElement(mouseX, mouseY);

        if (hoveredElementOpt.isEmpty()) {
            return CursorType.DEFAULT;
        }

        Element hoveredElement = hoveredElementOpt.get();
        if (!(hoveredElement instanceof ModListWidget modListWidget)) {
            return CursorType.DEFAULT;
        }

        for (ModListEntry entry : modListWidget.children()) {
            if (entry.isMouseOver(mouseX, mouseY) && modsScreen.getModHasConfigScreen(entry.mod.getId())) {
                if (mouseX >= modListWidget.getX() && mouseX <= modListWidget.getX() + ICON_SIZE + ENTRY_OFFSET_X) {
                    return CursorType.POINTER;
                }
            }
        }

        return CursorType.DEFAULT;
    }
}
