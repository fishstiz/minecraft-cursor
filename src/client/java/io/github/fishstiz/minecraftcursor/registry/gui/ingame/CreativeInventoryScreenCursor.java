package io.github.fishstiz.minecraftcursor.registry.gui.ingame;

import io.github.fishstiz.minecraftcursor.MinecraftCursor;
import io.github.fishstiz.minecraftcursor.cursor.CursorType;
import io.github.fishstiz.minecraftcursor.registry.CursorTypeRegistry;
import io.github.fishstiz.minecraftcursor.registry.utils.LookupUtils;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.VarHandle;

public class CreativeInventoryScreenCursor {
    // Derived from CreativeInventoryScreen.renderTabTooltipIfHovered()
    public static final int TAB_WIDTH = 21;
    public static final int TAB_HEIGHT = 27;
    public static final int TAB_OFFSET_X = 3;
    public static final int TAB_OFFSET_Y = 3;

    public static final String ITEM_GROUP_NAME = "net/minecraft/class_1761";
    public static final String GET_TAB_X_NAME = "method_47422";
    public static final String GET_TAB_X_DESC = String.format("(L%s;)I", ITEM_GROUP_NAME);
    public static MethodHandle getTabX;
    public static final String GET_TAB_Y_NAME = "method_47423";
    public static final String GET_TAB_Y_DESC = String.format("(L%s;)I", ITEM_GROUP_NAME);
    public static MethodHandle getTabY;
    public static final String SELECTED_TAB_NAME = "field_2896";
    public static final String SELECTED_TAB_DESC = String.format("L%s;", ITEM_GROUP_NAME);
    public static VarHandle selectedTab;
    public static final String POINT_W_BOUNDS_NAME = "method_2378";
    public static final String POINT_W_BOUNDS_DESC = "(IIIIDD)Z";
    public static MethodHandle isPointWithinBounds;

    public static void register(CursorTypeRegistry cursorTypeRegistry) {
        try {
            initHandles();
            cursorTypeRegistry.register(CreativeInventoryScreen.class, CreativeInventoryScreenCursor::getCursorType);
        } catch (NoSuchFieldException | IllegalAccessException | NoSuchMethodException e) {
            MinecraftCursor.LOGGER.warn("Could not register cursor type for CreativeInventoryScreen");
        }
    }

    private static void initHandles() throws IllegalAccessException, NoSuchMethodException, NoSuchFieldException {
        Class<CreativeInventoryScreen> targetClass = CreativeInventoryScreen.class;
        getTabX = LookupUtils.getMethodHandle(targetClass, GET_TAB_X_NAME, GET_TAB_X_DESC, int.class, ItemGroup.class);
        getTabY = LookupUtils.getMethodHandle(targetClass, GET_TAB_Y_NAME, GET_TAB_Y_DESC, int.class, ItemGroup.class);
        selectedTab = LookupUtils.getStaticVarHandle(targetClass, SELECTED_TAB_NAME, SELECTED_TAB_DESC, ItemGroup.class);
        isPointWithinBounds = LookupUtils.getMethodHandle(HandledScreen.class, POINT_W_BOUNDS_NAME, POINT_W_BOUNDS_DESC,
                boolean.class, int.class, int.class, int.class, int.class, double.class, double.class
        );
    }

    private static CursorType getCursorType(Element element, double mouseX, double mouseY) {
        try {
            boolean isHovered = false;
            for (ItemGroup itemGroup : ItemGroups.getGroupsToDisplay()) {
                int i = (int) getTabX.invoke(element, itemGroup);
                int j = (int) getTabY.invoke(element, itemGroup);

                ItemGroup selectedTab = (ItemGroup) CreativeInventoryScreenCursor.selectedTab.get();

                boolean isMouseOver = (boolean) isPointWithinBounds.invoke(element,
                        i + TAB_OFFSET_X,
                        j + TAB_OFFSET_Y,
                        TAB_WIDTH,
                        TAB_HEIGHT,
                        mouseX,
                        mouseY
                );

                if (isMouseOver && itemGroup != selectedTab) {
                    isHovered = true;
                }
            }
            return isHovered ? CursorType.POINTER : CursorType.DEFAULT;
        } catch (Throwable e) {
            MinecraftCursor.LOGGER.warn("Cannot get cursor type for CreativeInventoryScreen");
        }
        return CursorType.DEFAULT;
    }
}
