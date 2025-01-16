package io.github.fishstiz.minecraftcursor.registry.gui.ingame;

import io.github.fishstiz.minecraftcursor.cursor.CursorType;
import io.github.fishstiz.minecraftcursor.registry.CursorTypeRegistry;
import io.github.fishstiz.minecraftcursor.registry.utils.PointWithinBoundsFunction;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;

import java.util.function.Function;

// arms are heavy
public class CreativeInventoryScreenCursor {
    public Runnable reflect;
    public Function<ItemGroup, Integer> getTabX;
    public Function<ItemGroup, Integer> getTabY;
    public static ItemGroup selectedTab;
    public PointWithinBoundsFunction isPointWithinBounds;

    private static CreativeInventoryScreenCursor instance;

    private CreativeInventoryScreenCursor() {
    }

    public static CreativeInventoryScreenCursor getInstance() {
        if (instance == null) {
            instance = new CreativeInventoryScreenCursor();
        }
        return instance;
    }

    public static void register(CursorTypeRegistry cursorTypeRegistry) {
        cursorTypeRegistry.register(CreativeInventoryScreen.class, CreativeInventoryScreenCursor.getInstance()::getCursorType);
    }

    private CursorType getCursorType(Element element, double mouseX, double mouseY) {
        if (reflect == null) {
            return CursorType.DEFAULT;
        }
        reflect.run();

        boolean isHovered = false;
        for (ItemGroup itemGroup : ItemGroups.getGroupsToDisplay()) {
            int i = getTabX.apply(itemGroup);
            int j = getTabY.apply(itemGroup);
            if (isPointWithinBounds.apply(i + 3, j + 3, 21, 27, mouseX, mouseY) && itemGroup != selectedTab) {
                isHovered = true;
            }
        }
        return isHovered ? CursorType.POINTER : CursorType.DEFAULT;
    }
}
