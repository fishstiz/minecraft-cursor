package io.github.fishstiz.minecraftcursor.registry.gui.modmenu;

import com.terraformersmc.modmenu.gui.ModsScreen;
import com.terraformersmc.modmenu.gui.widget.ModListWidget;
import com.terraformersmc.modmenu.gui.widget.entries.ModListEntry;
import io.github.fishstiz.minecraftcursor.cursor.CursorType;
import io.github.fishstiz.minecraftcursor.registry.CursorTypeRegistry;
import net.minecraft.client.gui.Element;

import java.util.Optional;

public class ModScreenCursor {
    private static final int ICON_SIZE = 32;
    private static final int ENTRY_OFFSET_X = 5;

    public static void register(CursorTypeRegistry cursorTypeRegistry) {
        cursorTypeRegistry.register(ModsScreen.class, ModScreenCursor::getCursorModScreen);
    }

    private static CursorType getCursorModScreen(Element element, double mouseX, double mouseY, float delta) {
        ModsScreen modsScreen = (ModsScreen) element;
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
