package io.github.fishstiz.minecraftcursor.cursorhandler.ingame;

import io.github.fishstiz.minecraftcursor.MinecraftCursor;
import io.github.fishstiz.minecraftcursor.MinecraftCursorClient;
import io.github.fishstiz.minecraftcursor.api.CursorType;
import io.github.fishstiz.minecraftcursor.mixin.client.access.CreativeInventoryScreenAccessor;
import io.github.fishstiz.minecraftcursor.util.CursorTypeUtil;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.screen.slot.Slot;

public class CreativeInventoryScreenCursorHandler extends HandledScreenCursorHandler<CreativeInventoryScreen.CreativeScreenHandler, CreativeInventoryScreen> {
    // Derived from CreativeInventoryScreen#renderTabTooltipIfHovered
    public static final int TAB_WIDTH = 21;
    public static final int TAB_HEIGHT = 27;
    public static final int TAB_OFFSET_X = 3;
    public static final int TAB_OFFSET_Y = 3;

    @Override
    public CursorType getCursorType(CreativeInventoryScreen creativeInventoryScreen, double mouseX, double mouseY) {
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
        if (!MinecraftCursorClient.CONFIG.get().isCreativeTabsEnabled()) return CursorType.DEFAULT;
        try {
            boolean isHovered = false;
            for (ItemGroup itemGroup : ItemGroups.getGroupsToDisplay()) {
                if (creativeInventoryScreen.invokeIsPointWithinBounds(
                        creativeInventoryScreen.invokeGetTabX(itemGroup) + TAB_OFFSET_X,
                        creativeInventoryScreen.invokeGetTabY(itemGroup) + TAB_OFFSET_Y,
                        TAB_WIDTH,
                        TAB_HEIGHT,
                        mouseX,
                        mouseY)
                        && itemGroup != creativeInventoryScreen.getSelectedTab()) {
                    isHovered = true;
                    break;
                }
            }
            return isHovered ? CursorType.POINTER : CursorType.DEFAULT;
        } catch (Throwable e) {
            MinecraftCursor.LOGGER.warn("Cannot get cursor type for CreativeInventoryScreen");
        }
        return CursorType.DEFAULT;
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
