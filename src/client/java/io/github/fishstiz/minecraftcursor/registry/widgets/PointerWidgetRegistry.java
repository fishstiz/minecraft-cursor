package io.github.fishstiz.minecraftcursor.registry.widgets;

import io.github.fishstiz.minecraftcursor.cursor.CursorType;
import io.github.fishstiz.minecraftcursor.registry.CursorTypeRegistry;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.option.ControlsListWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.OptionSliderWidget;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.option.SimpleOption;

import java.lang.reflect.Field;
import java.util.Map;

public class PointerWidgetRegistry {
    public static void register(String fullyQualifiedClassName) {
        CursorTypeRegistry.register(fullyQualifiedClassName, CursorTypeRegistry::elementToPointer);
    }

    public static void register(Class<? extends Element> widget) {
        CursorTypeRegistry.register(widget, CursorTypeRegistry::elementToPointer);
    }

    public static void init() {
        register(PressableWidget.class);
        register(OptionSliderWidget.class);
        register(ControlsListWidget.Entry.class);
        register("net.minecraft.client.gui.screen.option.LanguageOptionsScreen$LanguageSelectionListWidget$LanguageEntry");
        register("net.minecraft.client.gui.widget.OptionListWidget$WidgetEntry");
        CursorTypeRegistry.register("net.minecraft.client.gui.widget.OptionListWidget$OptionWidgetEntry", PointerWidgetRegistry::optionEntryCursor);
    }

    @SuppressWarnings("unchecked")
    public static CursorType optionEntryCursor(Element entry, double mouseX, double mouseY, float delta) {
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
