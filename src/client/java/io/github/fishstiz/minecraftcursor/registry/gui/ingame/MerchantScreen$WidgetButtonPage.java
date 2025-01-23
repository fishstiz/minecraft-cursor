package io.github.fishstiz.minecraftcursor.registry.gui.ingame;

import io.github.fishstiz.minecraftcursor.cursor.CursorType;
import io.github.fishstiz.minecraftcursor.registry.CursorTypeRegistry;
import io.github.fishstiz.minecraftcursor.registry.utils.CursorTypeUtils;
import net.minecraft.client.gui.Element;

public class MerchantScreen$WidgetButtonPage {
    private static final String NAME = "net.minecraft.class_492$class_493";

    public static void register(CursorTypeRegistry cursorTypeRegistry) {
        cursorTypeRegistry.register(NAME, MerchantScreen$WidgetButtonPage::getCursorType);
    }

    public static CursorType getCursorType(Element element, double mouseX, double mouseY) {
        return CursorTypeUtils.canShift() ? CursorType.SHIFT : CursorType.POINTER;
    }
}
