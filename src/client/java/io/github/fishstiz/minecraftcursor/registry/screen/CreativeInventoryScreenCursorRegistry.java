package io.github.fishstiz.minecraftcursor.registry.screen;

import io.github.fishstiz.minecraftcursor.MinecraftCursor;
import io.github.fishstiz.minecraftcursor.cursor.CursorType;
import io.github.fishstiz.minecraftcursor.registry.ScreenCursorRegistry;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class CreativeInventoryScreenCursorRegistry {
    private Field selectedTabField;
    private Method getTabXMethod;
    private Method getTabYMethod;
    private Method isPointWithinBoundsMethod;

    public CreativeInventoryScreenCursorRegistry(ScreenCursorRegistry screenCursorRegistry) {
        try {
            selectedTabField = CreativeInventoryScreen.class.getDeclaredField("selectedTab");
            selectedTabField.setAccessible(true);
            getTabXMethod = CreativeInventoryScreen.class.getDeclaredMethod("getTabX", ItemGroup.class);
            getTabXMethod.setAccessible(true);
            getTabYMethod = CreativeInventoryScreen.class.getDeclaredMethod("getTabY", ItemGroup.class);
            getTabYMethod.setAccessible(true);
            isPointWithinBoundsMethod = HandledScreen.class.getDeclaredMethod("isPointWithinBounds", int.class, int.class, int.class, int.class, double.class, double.class);
            isPointWithinBoundsMethod.setAccessible(true);
        } catch (NoSuchMethodException e) {
            MinecraftCursor.LOGGER.error("No such methods exists on CreativeInventoryScreen", e);
        } catch (NoSuchFieldException e) {
            MinecraftCursor.LOGGER.error("No such field selectedTab exists on CreativeInventoryScreen", e);
        }

        screenCursorRegistry.register(CreativeInventoryScreen.class, this::getCursorType);
    }

    private CursorType getCursorType(Element element, double mouseX, double mouseY, float delta) {
        CreativeInventoryScreen creativeInventoryScreen = (CreativeInventoryScreen) element;

        if (isTabHoveredAndUnselected(creativeInventoryScreen, mouseX, mouseY)) {
            return CursorType.POINTER;
        }

        return CursorType.DEFAULT;
    }

    private boolean isTabHoveredAndUnselected(CreativeInventoryScreen creativeInventoryScreen, double mouseX, double mouseY) {
        try {
            for (ItemGroup itemGroup : ItemGroups.getGroups()) {
                // taken directly from net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen.renderTabTooltipIfHovered
                int i = (int) getTabXMethod.invoke(creativeInventoryScreen, itemGroup);
                int j = (int) getTabYMethod.invoke(creativeInventoryScreen, itemGroup);
                boolean isPointWithinBounds = (boolean) isPointWithinBoundsMethod.invoke(creativeInventoryScreen, i + 3, j + 3, 21, 27, mouseX, mouseY);
                boolean isTabSelected = selectedTabField.get(creativeInventoryScreen) == itemGroup;

                if (isPointWithinBounds && !isTabSelected) {
                    return true;
                }
            }
        } catch (InvocationTargetException | IllegalAccessException e) {
            MinecraftCursor.LOGGER.error("Could not check if tab is hovered on CreativeInventoryScreen", e);
        }
        return false;
    }
}
