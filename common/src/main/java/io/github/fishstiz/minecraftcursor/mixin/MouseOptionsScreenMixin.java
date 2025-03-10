package io.github.fishstiz.minecraftcursor.mixin;

import io.github.fishstiz.minecraftcursor.CursorManager;
import io.github.fishstiz.minecraftcursor.gui.screen.CursorOptionsScreen;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.screens.MouseSettingsScreen;
import net.minecraft.client.gui.screens.OptionsSubScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.minecraft.client.OptionInstance.noTooltip;

@Mixin(MouseSettingsScreen.class)
public abstract class MouseOptionsScreenMixin extends OptionsSubScreen {
    @Unique
    private static final Component CURSOR_SETTINGS_TEXT = Component.translatable("minecraft-cursor.options");

    @Shadow
    private OptionsList list;

    protected MouseOptionsScreenMixin(Screen parent, Options gameOptions, Component title) {
        super(parent, gameOptions, title);
    }

    @Inject(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/MouseSettingsScreen;addWidget(Lnet/minecraft/client/gui/components/events/GuiEventListener;)Lnet/minecraft/client/gui/components/events/GuiEventListener;"))
    public void init(CallbackInfo ci) {
        OptionInstance<Boolean> button = OptionInstance.createBoolean(
                "",
                noTooltip(),
                false,
                value -> {
                    if (this.minecraft != null) {
                        this.minecraft.setScreen(new CursorOptionsScreen(this, CursorManager.INSTANCE));
                    }
                });
        list.addBig(button);
        CycleButton<?> widget = (CycleButton<?>) list.findOption(button);
        assert widget != null;
        widget.setMessage(Component.empty().append(CURSOR_SETTINGS_TEXT).append("..."));
    }
}
