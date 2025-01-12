package io.github.fishstiz.minecraftcursor.registry.widget;

import io.github.fishstiz.minecraftcursor.cursor.CursorType;
import io.github.fishstiz.minecraftcursor.registry.CursorTypeRegistry;
import io.github.fishstiz.minecraftcursor.registry.WidgetCursorRegistry;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.option.ControlsListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.OptionSliderWidget;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.option.SimpleOption;

import java.lang.reflect.Field;
import java.util.Map;

public class CursorPointerWidgetRegistry {
    private final WidgetCursorRegistry cursorTypeRegistry;

    public CursorPointerWidgetRegistry(WidgetCursorRegistry cursorTypeRegistry) {
        this.cursorTypeRegistry = cursorTypeRegistry;

        register(PressableWidget.class);
        register(OptionSliderWidget.class);
        register("net.minecraft.client.gui.screen.option.LanguageOptionsScreen$LanguageSelectionListWidget$LanguageEntry");
        register("net.minecraft.client.gui.widget.OptionListWidget$WidgetEntry");
        cursorTypeRegistry.register(ControlsListWidget.KeyBindingEntry.class, CursorPointerWidgetRegistry::keyBindingEntryCursor);
        cursorTypeRegistry.register("net.minecraft.client.gui.widget.OptionListWidget$OptionWidgetEntry", CursorPointerWidgetRegistry::optionEntryCursor);
    }

    public void register(String fullyQualifiedClassName) {
        cursorTypeRegistry.register(fullyQualifiedClassName, CursorTypeRegistry::elementToPointer);
    }

    public void register(Class<? extends Element> widget) {
        cursorTypeRegistry.register(widget, CursorTypeRegistry::elementToPointer);
    }

    private static CursorType keyBindingEntryCursor(Element entry, double mouseX, double mouseY, float delta) {
        for (Element element : ((ControlsListWidget.KeyBindingEntry) entry).children()) {
            if (!(element instanceof ButtonWidget)) {
                continue;
            }
            if (((ButtonWidget) element).isHovered() && ((ButtonWidget) element).active) {
                return CursorType.POINTER;
            }
        }
        return CursorType.DEFAULT;
    }

    @SuppressWarnings("unchecked")
    private static CursorType optionEntryCursor(Element entry, double mouseX, double mouseY, float delta) {
        try {
            Field optionWidgetsField = entry.getClass().getField("optionWidgets");
            Map<SimpleOption<?>, ClickableWidget> optionWidgets = (Map<SimpleOption<?>, ClickableWidget>) optionWidgetsField.get(entry);
            ClickableWidget optionWidget = (ClickableWidget) optionWidgets.values().toArray()[optionWidgets.size() - 1];
            return optionWidget.active ? CursorType.POINTER : CursorType.DEFAULT;
        } catch (Exception e) {
            return CursorType.DEFAULT;
        }
    }
}
