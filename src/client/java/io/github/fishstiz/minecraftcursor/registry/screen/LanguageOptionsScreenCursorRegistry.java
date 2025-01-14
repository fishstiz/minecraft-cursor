package io.github.fishstiz.minecraftcursor.registry.screen;

import io.github.fishstiz.minecraftcursor.cursor.CursorType;
import io.github.fishstiz.minecraftcursor.registry.ScreenCursorRegistry;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.option.LanguageOptionsScreen;
import net.minecraft.client.gui.widget.EntryListWidget;

public class LanguageOptionsScreenCursorRegistry {
    public LanguageOptionsScreenCursorRegistry(ScreenCursorRegistry screenCursorRegistry) {
        screenCursorRegistry.register(LanguageOptionsScreen.class, this::getLanguageScreenCursorType);
    }

    private CursorType getLanguageScreenCursorType(Element element, double mouseX, double mouseY, float delta) {
        LanguageOptionsScreen languageOptionsScreen = (LanguageOptionsScreen) element;

        for (Element child : languageOptionsScreen.children()) {
            if (!(child instanceof EntryListWidget<?> entryListWidget)) {
                continue;
            }

            for (Element entry : entryListWidget.children()) {
                if (entry.isMouseOver(mouseX, mouseY) && !entry.isFocused()) {
                    return CursorType.POINTER;
                }
            }
        }

        return CursorType.DEFAULT;
    }
}
