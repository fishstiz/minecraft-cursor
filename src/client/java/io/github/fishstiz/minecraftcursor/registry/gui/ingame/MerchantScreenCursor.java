package io.github.fishstiz.minecraftcursor.registry.gui.ingame;

import io.github.fishstiz.minecraftcursor.MinecraftCursor;
import io.github.fishstiz.minecraftcursor.cursor.CursorType;
import io.github.fishstiz.minecraftcursor.registry.CursorTypeRegistry;
import io.github.fishstiz.minecraftcursor.registry.utils.CursorTypeUtils;
import io.github.fishstiz.minecraftcursor.registry.utils.LookupUtils;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.ingame.MerchantScreen;
import net.minecraft.client.gui.widget.ButtonWidget;

import java.lang.invoke.VarHandle;

import static io.github.fishstiz.minecraftcursor.registry.utils.LookupUtils.NAMESPACE;
import static io.github.fishstiz.minecraftcursor.registry.utils.LookupUtils.RESOLVER;

public class MerchantScreenCursor extends HandledScreenCursor {
    private static final String BUTTON_NAME = "net.minecraft.class_492$class_493";
    private static final String OFFERS_NAME = "field_19162";
    private static VarHandle offers;

    public static void register(CursorTypeRegistry cursorTypeRegistry) {
        try {
            initHandles();
            cursorTypeRegistry.register(MerchantScreen.class, MerchantScreenCursor::getCursorType);
        } catch (NoSuchFieldException | ClassNotFoundException | IllegalAccessException e) {
            MinecraftCursor.LOGGER.warn("Could not register cursor type for MerchantScreen");
        }
    }

    public static void initHandles() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        offers = LookupUtils.getVarHandle(
                MerchantScreen.class,
                OFFERS_NAME,
                Class.forName("[L" + RESOLVER.mapClassName(NAMESPACE, BUTTON_NAME) + ";")
        );
    }

    public static CursorType getCursorType(Element element, double mouseX, double mouseY) {
        CursorType handledScreenCursor = HandledScreenCursor.getCursorType(element, mouseX, mouseY);
        if (handledScreenCursor != CursorType.DEFAULT && handledScreenCursor != CursorType.GRABBING) {
            return handledScreenCursor;
        }

        boolean isGrabbing = handledScreenCursor == CursorType.GRABBING;
        ButtonWidget[] offers = (ButtonWidget[]) MerchantScreenCursor.offers.get(element);

        for (ButtonWidget offer : offers) {
            if (offer.isMouseOver(mouseX, mouseY)) {
                if (CursorTypeUtils.canShift(isGrabbing)) {
                    return isGrabbing ? CursorType.SHIFT_GRABBING : CursorType.SHIFT;
                } else if (!isGrabbing) {
                    return CursorType.POINTER;
                }
            }
        }
        return handledScreenCursor;
    }
}
