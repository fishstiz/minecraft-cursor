package io.github.fishstiz.minecraftcursor.cursorhandler.ingame;

import io.github.fishstiz.minecraftcursor.api.CursorHandler;
import io.github.fishstiz.minecraftcursor.api.CursorType;
import io.github.fishstiz.minecraftcursor.util.CursorTypeUtil;
import net.minecraft.client.gui.Element;
import org.jetbrains.annotations.NotNull;

public class MerchantScreenButtonCursorHandler implements CursorHandler<Element> {
    @Override
    public @NotNull TargetElement<Element> getTargetElement() {
        return TargetElement.fromClassName("net.minecraft.class_492$class_493");
    }

    @Override
    public CursorType getCursorType(Element element, double mouseX, double mouseY) {
        return CursorTypeUtil.canShift() ? CursorType.SHIFT : CursorType.POINTER;
    }
}
