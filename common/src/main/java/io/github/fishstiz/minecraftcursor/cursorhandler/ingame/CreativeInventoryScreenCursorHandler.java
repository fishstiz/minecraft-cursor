package io.github.fishstiz.minecraftcursor.cursorhandler.ingame;

import io.github.fishstiz.minecraftcursor.MinecraftCursor;
import io.github.fishstiz.minecraftcursor.api.CursorType;
import io.github.fishstiz.minecraftcursor.mixin.access.CreativeInventoryScreenAccessor;
import io.github.fishstiz.minecraftcursor.util.CursorTypeUtil;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;

public class CreativeInventoryScreenCursorHandler extends HandledScreenCursorHandler<CreativeModeInventoryScreen.ItemPickerMenu, CreativeModeInventoryScreen> {
    // Derived from CreativeInventoryScreen#renderTabTooltipIfHovered
    public static final int TAB_WIDTH = 21;
    public static final int TAB_HEIGHT = 27;
    public static final int TAB_OFFSET_X = 3;
    public static final int TAB_OFFSET_Y = 3;

    @Override
    public CursorType getCursorType(CreativeModeInventoryScreen creativeInventoryScreen, double mouseX, double mouseY) {
        CursorType handledScreenCursor = super.getCursorType(creativeInventoryScreen, mouseX, mouseY);
        if (handledScreenCursor != CursorType.DEFAULT) {
            return handledScreenCursor;
        }

        CreativeInventoryScreenAccessor accessor = (CreativeInventoryScreenAccessor) creativeInventoryScreen;
        CursorType cursorType = getCursorTypeTabs(accessor, mouseX, mouseY);
        cursorType = cursorType != CursorType.DEFAULT ? cursorType : getCursorTypeDelete(accessor);
        return cursorType;
    }

    private CursorType getCursorTypeTabs(CreativeInventoryScreenAccessor creativeInventoryScreen, double mouseX, double mouseY) {
        if (!MinecraftCursor.CONFIG.isCreativeTabsEnabled()) return CursorType.DEFAULT;

        boolean isHovered = false;
        for (CreativeModeTab itemGroup : CreativeModeTabs.tabs()) {
            // noinspection ConstantConditions
            if (creativeInventoryScreen.invokeIsPointWithinBounds(
                    creativeInventoryScreen.invokeGetTabX(itemGroup) + TAB_OFFSET_X,
                    creativeInventoryScreen.invokeGetTabY(itemGroup) + TAB_OFFSET_Y,
                    TAB_WIDTH,
                    TAB_HEIGHT,
                    mouseX,
                    mouseY)
                    && itemGroup != CreativeInventoryScreenAccessor.getSelectedTab()) {
                isHovered = true;
                break;
            }
        }
        // noinspection ConstantConditions
        return isHovered ? CursorType.POINTER : CursorType.DEFAULT;
    }

    private CursorType getCursorTypeDelete(CreativeInventoryScreenAccessor creativeInventoryScreen) {
        Slot focusedSlot = creativeInventoryScreen.getFocusedSlot();
        if (CursorTypeUtil.canShift()
                && focusedSlot != null
                && focusedSlot == creativeInventoryScreen.getDeleteItemSlot()) {
            return CursorType.SHIFT;
        }
        return CursorType.DEFAULT;
    }
}
