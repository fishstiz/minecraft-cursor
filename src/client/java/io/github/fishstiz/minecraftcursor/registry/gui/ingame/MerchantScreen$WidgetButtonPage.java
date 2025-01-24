package io.github.fishstiz.minecraftcursor.registry.gui.ingame;

import io.github.fishstiz.minecraftcursor.cursor.CursorType;
import io.github.fishstiz.minecraftcursor.registry.CursorTypeRegistry;
import io.github.fishstiz.minecraftcursor.utils.CursorTypeUtils;
import io.github.fishstiz.minecraftcursor.registry.gui.GuiCursorHandler;
import net.minecraft.client.gui.Element;

public class MerchantScreen$WidgetButtonPage extends GuiCursorHandler {
    private static final String NAME = "net.minecraft.class_492$class_493";

    public static void register(CursorTypeRegistry cursorTypeRegistry) {
        cursorTypeRegistry.register(NAME, new MerchantScreen$WidgetButtonPage()::getCursorType);
    }

    protected CursorType getCursorType(Element element, double mouseX, double mouseY) {
        return CursorTypeUtils.canShift() ? CursorType.SHIFT : CursorType.POINTER;
    }
}
