package io.github.fishstiz.minecraftcursor.registry.gui.modmenu;

import com.terraformersmc.modmenu.ModMenu;
import com.terraformersmc.modmenu.gui.ModsScreen;
import com.terraformersmc.modmenu.gui.widget.ModListWidget;
import com.terraformersmc.modmenu.gui.widget.entries.ModListEntry;
import io.github.fishstiz.minecraftcursor.cursor.CursorType;
import io.github.fishstiz.minecraftcursor.registry.CursorTypeRegistry;
import io.github.fishstiz.minecraftcursor.registry.gui.GuiCursorHandler;
import net.minecraft.client.gui.Element;

import java.util.Optional;

public class ModScreenCursor extends GuiCursorHandler {
    public static final int ICON_SIZE = 32;
    public static final int ENTRY_OFFSET_X = 5;

    public static void register(CursorTypeRegistry cursorTypeRegistry) {
        cursorTypeRegistry.register(ModsScreen.class, new ModScreenCursor()::getCursorType);
        cursorTypeRegistry.register("com.terraformersmc.modmenu.gui.widget.DescriptionListWidget$MojangCreditsEntry", CursorTypeRegistry::elementToPointer);
        cursorTypeRegistry.register("com.terraformersmc.modmenu.gui.widget.DescriptionListWidget$LinkEntry", CursorTypeRegistry::elementToPointer);
    }

    @Override
    protected CursorType getCursorType(Element element, double mouseX, double mouseY) {
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
            if (entry.isMouseOver(mouseX, mouseY) && modsScreen.getModHasConfigScreen().get(entry.mod.getId())) {
                if (mouseX >= modListWidget.getRowLeft() && mouseX <= modListWidget.getRowLeft() + ICON_SIZE + ENTRY_OFFSET_X) {
                    return CursorType.POINTER;
                }
            }
        }

        return CursorType.DEFAULT;
    }
}
