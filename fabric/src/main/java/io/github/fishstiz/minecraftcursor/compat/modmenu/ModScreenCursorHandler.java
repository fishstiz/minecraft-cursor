package io.github.fishstiz.minecraftcursor.compat.modmenu;

import com.terraformersmc.modmenu.config.ModMenuConfig;
import com.terraformersmc.modmenu.gui.ModsScreen;
import com.terraformersmc.modmenu.gui.widget.ModListWidget;
import com.terraformersmc.modmenu.gui.widget.entries.ModListEntry;
import io.github.fishstiz.minecraftcursor.api.CursorHandler;
import io.github.fishstiz.minecraftcursor.api.CursorType;
import net.minecraft.client.gui.components.events.GuiEventListener;

import java.util.Optional;

public class ModScreenCursorHandler implements CursorHandler<ModsScreen> {
    public static final int COMPACT_ICON_SIZE = 19;
    public static final int ICON_SIZE = 32;

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

        if (ModMenuConfig.QUICK_CONFIGURE.getValue()) {
            int iconSize = ModMenuConfig.COMPACT_LIST.getValue() ? COMPACT_ICON_SIZE : ICON_SIZE;
            for (ModListEntry entry : modListWidget.children()) {
                if (entry.isMouseOver(mouseX, mouseY)
                    && modsScreen.getModHasConfigScreen(entry.mod.getId())
                    && mouseX >= modListWidget.getX()
                    && mouseX - modListWidget.getRowLeft() <= iconSize)
                    return CursorType.POINTER;
            }
        }

        return CursorType.DEFAULT;
    }
}
