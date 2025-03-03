package io.github.fishstiz.minecraftcursor.cursorhandler.ingame;

import io.github.fishstiz.minecraftcursor.api.CursorHandler;
import io.github.fishstiz.minecraftcursor.api.CursorType;
import io.github.fishstiz.minecraftcursor.util.CursorTypeUtil;
import net.minecraft.client.gui.components.events.GuiEventListener;
import org.jetbrains.annotations.NotNull;

public class TradeOfferButtonCursorHandler implements CursorHandler<GuiEventListener> {
    private final String fullyQualifiedClassName;

    public TradeOfferButtonCursorHandler(String fullyQualifiedClassName) {
        this.fullyQualifiedClassName = fullyQualifiedClassName;
    }

    @Override
    public @NotNull TargetElement<GuiEventListener> getTargetElement() {
        return TargetElement.fromClassName(fullyQualifiedClassName);
    }

    @Override
    public CursorType getCursorType(GuiEventListener element, double mouseX, double mouseY) {
        return CursorTypeUtil.canShift() ? CursorType.SHIFT : CursorType.POINTER;
    }
}
