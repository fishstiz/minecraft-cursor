package io.github.fishstiz.minecraftcursor.registry.widget;

import io.github.fishstiz.minecraftcursor.MinecraftCursor;
import io.github.fishstiz.minecraftcursor.cursor.CursorType;
import io.github.fishstiz.minecraftcursor.registry.CursorTypeRegistry;
import io.github.fishstiz.minecraftcursor.registry.WidgetCursorRegistry;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ParentElement;
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
        cursorTypeRegistry.register(ControlsListWidget.KeyBindingEntry.class, CursorPointerWidgetRegistry::keyBindingEntryCursor);
        cursorTypeRegistry.register("net.minecraft.client.gui.widget.OptionListWidget$WidgetEntry", CursorPointerWidgetRegistry::widgetEntryCursor);
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

    private static CursorType widgetEntryCursor(Element entry, double mouseX, double mouseY, float delta) {
        boolean isHovered = ((ParentElement) entry).children().stream().anyMatch(element -> ((ClickableWidget) element).isHovered());
        return isHovered ? CursorType.POINTER : CursorType.DEFAULT;
    }

    @SuppressWarnings("unchecked")
    private static CursorType optionEntryCursor(Element entry, double mouseX, double mouseY, float delta) {
        try {
            Field optionWidgetsField = entry.getClass().getField("optionWidgets");
            Map<SimpleOption<?>, ClickableWidget> optionWidgets = (Map<SimpleOption<?>, ClickableWidget>) optionWidgetsField.get(entry);
            return optionWidgets.values().stream().anyMatch(ClickableWidget::isHovered) ? CursorType.POINTER : CursorType.DEFAULT;
        } catch (NoSuchFieldException e) {
            MinecraftCursor.LOGGER.error("No such field optionWidgets exist on net.minecraft.client.gui.widget.OptionListWidget$OptionWidgetEntry", e);
        } catch (IllegalAccessException e) {
            MinecraftCursor.LOGGER.error("Cannot access field optionWidgets on net.minecraft.client.gui.widget.OptionListWidget$OptionWidgetEntry", e);
        }
        return CursorType.DEFAULT;
    }
}
