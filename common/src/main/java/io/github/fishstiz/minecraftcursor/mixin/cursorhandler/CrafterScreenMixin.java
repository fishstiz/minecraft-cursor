package io.github.fishstiz.minecraftcursor.mixin.cursorhandler;

import io.github.fishstiz.minecraftcursor.api.CursorType;
import net.minecraft.client.gui.screens.inventory.CrafterScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CrafterMenu;
import net.minecraft.world.inventory.CrafterSlot;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(CrafterScreen.class)
public abstract class CrafterScreenMixin extends AbstractContainerScreenMixin<CrafterMenu> {
    @Shadow
    @Final
    private Player player;

    protected CrafterScreenMixin(Component title) {
        super(title);
    }

    @Override
    public @NotNull CursorType minecraft_cursor$getCursorType(double mouseX, double mouseY) {
        if (this.hoveredSlot instanceof CrafterSlot crafterSlot
            && this.menu.getCarried().isEmpty()
            && !crafterSlot.hasItem()
            && !player.isSpectator()) {
            return CursorType.POINTER;
        }
        return super.minecraft_cursor$getCursorType(mouseX, mouseY);
    }
}
