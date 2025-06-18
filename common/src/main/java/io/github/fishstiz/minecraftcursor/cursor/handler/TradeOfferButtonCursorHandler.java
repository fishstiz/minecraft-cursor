package io.github.fishstiz.minecraftcursor.cursor.handler;

import io.github.fishstiz.minecraftcursor.api.CursorHandler;
import io.github.fishstiz.minecraftcursor.api.CursorType;
import io.github.fishstiz.minecraftcursor.util.CursorTypeUtil;
import net.minecraft.client.gui.components.events.GuiEventListener;
import org.jetbrains.annotations.NotNull;

public class TradeOfferButtonCursorHandler implements CursorHandler<GuiEventListener> {
    private final String className;

    public TradeOfferButtonCursorHandler(String className) {
        this.className = className;
    }

    @Override
    public @NotNull TargetElement<GuiEventListener> getTargetElement() {
        return TargetElement.fromClassName(this.className);
    }

    @Override
    public CursorType getCursorType(GuiEventListener element, double mouseX, double mouseY) {
        return CursorTypeUtil.canShift() ? CursorType.SHIFT : CursorType.POINTER;
    }
}
