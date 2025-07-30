package io.github.fishstiz.testmod.gui.components;

import io.github.fishstiz.minecraftcursor.api.CursorController;
import io.github.fishstiz.minecraftcursor.api.CursorType;
import io.github.fishstiz.testmod.compat.minecraftcursor.MinecraftCursorUtil;
import net.minecraft.network.chat.Component;

// Override cursor state using CursorController
public class OverrideCursorElement extends Buttons.Stub {
    private final CursorType cursorType;
    private final int override;
    private boolean toggled;

    public OverrideCursorElement(CursorType cursorType, int override) {
        super(Component.literal("Toggle ").append(MinecraftCursorUtil.getTranslation(cursorType)).append(" " + override));
        this.cursorType = cursorType;
        this.override = override;
    }

    @Override
    public void onPress() {
        this.toggled = !this.toggled;

        if (this.toggled) {
            CursorController.getInstance().overrideCursor(this.cursorType, this.override);
        } else {
            CursorController.getInstance().removeOverride(this.override);
        }
    }
}
