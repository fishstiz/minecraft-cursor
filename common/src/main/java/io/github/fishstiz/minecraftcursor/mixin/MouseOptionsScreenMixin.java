package io.github.fishstiz.minecraftcursor.mixin;

import io.github.fishstiz.minecraftcursor.CursorManager;
import io.github.fishstiz.minecraftcursor.gui.screen.CursorOptionsScreen;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.MouseSettingsScreen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseSettingsScreen.class)
public abstract class MouseOptionsScreenMixin extends OptionsSubScreen {
    protected MouseOptionsScreenMixin(Screen parent, Options gameOptions, Component title) {
        super(parent, gameOptions, title);
    }

    @Inject(method = "addOptions", at = @At("TAIL"))
    protected void addOptions(CallbackInfo ci) {
        if (this.list == null) {
            return;
        }

        Button settingsBtn = Button.builder(
                Component.translatable("minecraft-cursor.options").append("..."),
                btn -> {
                    if (this.minecraft != null) {
                        this.minecraft.setScreen(new CursorOptionsScreen(this, CursorManager.INSTANCE));
                    }
                }).build();

        Button fillerBtn = Button.builder(Component.empty(), button -> {
        }).build();
        fillerBtn.visible = false;
        fillerBtn.active = false;

        this.list.addSmall(settingsBtn, fillerBtn);
    }
}
