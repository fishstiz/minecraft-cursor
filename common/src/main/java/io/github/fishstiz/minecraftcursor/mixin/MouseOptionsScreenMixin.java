package io.github.fishstiz.minecraftcursor.mixin;

import io.github.fishstiz.minecraftcursor.gui.screen.ConfigurationScreen;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.screens.MouseSettingsScreen;
import net.minecraft.client.gui.screens.OptionsSubScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(MouseSettingsScreen.class)
public abstract class MouseOptionsScreenMixin extends OptionsSubScreen {
    @Unique
    private static final Component SETTINGS_TEXT = Component.translatable("minecraft-cursor.options").append(CommonComponents.ELLIPSIS);

    @Shadow
    private OptionsList list;

    protected MouseOptionsScreenMixin(Screen parent, Options gameOptions, Component title) {
        super(parent, gameOptions, title);
    }

    @Inject(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/MouseSettingsScreen;addWidget(Lnet/minecraft/client/gui/components/events/GuiEventListener;)Lnet/minecraft/client/gui/components/events/GuiEventListener;"))
    public void addButtonOnInit(CallbackInfo ci) {
        var settingsOption = minecraft_cursor$createDummyOption(() -> Objects.requireNonNull(this.minecraft).setScreen(new ConfigurationScreen(this)));
        var fillerOption = minecraft_cursor$createDummyOption(null);
        list.addSmall(settingsOption, fillerOption);

        AbstractWidget fillerButton = list.findOption(fillerOption);
        AbstractWidget settingsButton = list.findOption(settingsOption);

        if (settingsButton != null && fillerButton != null) {
            settingsButton.setMessage(SETTINGS_TEXT);
            fillerButton.setMessage(CommonComponents.EMPTY);
            fillerButton.active = false;
            fillerButton.visible = false;
        }
    }

    @Unique
    private static OptionInstance<Boolean> minecraft_cursor$createDummyOption(@Nullable Runnable runnable) {
        return OptionInstance.createBoolean("", false, runnable != null
                ? value -> runnable.run()
                : value -> {}
        );
    }
}
