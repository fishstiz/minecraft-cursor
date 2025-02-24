package io.github.fishstiz.minecraftcursor.mixin.client;

import io.github.fishstiz.minecraftcursor.CursorManager;
import io.github.fishstiz.minecraftcursor.gui.screen.CursorOptionsScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.screen.option.MouseOptionsScreen;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.OptionListWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.minecraft.client.option.SimpleOption.emptyTooltip;

@Mixin(MouseOptionsScreen.class)
public abstract class MouseOptionsScreenMixin extends GameOptionsScreen {
    @Unique
    private static final Text CURSOR_SETTINGS_TEXT = Text.translatable("minecraft-cursor.options");

    @Shadow
    private OptionListWidget buttonList;

    protected MouseOptionsScreenMixin(Screen parent, GameOptions gameOptions, Text title) {
        super(parent, gameOptions, title);
    }

    @Inject(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/option/MouseOptionsScreen;addSelectableChild(Lnet/minecraft/client/gui/Element;)Lnet/minecraft/client/gui/Element;"))
    public void init(CallbackInfo ci) {
        SimpleOption<Boolean> button = SimpleOption.ofBoolean(
                "",
                emptyTooltip(),
                false,
                value -> {
                    if (this.client != null) {
                        this.client.setScreen(new CursorOptionsScreen(this, CursorManager.getInstance()));
                    }
                });
        buttonList.addSingleOptionEntry(button);
        CyclingButtonWidget<?> widget = (CyclingButtonWidget<?>) buttonList.getWidgetFor(button);
        assert widget != null;
        widget.setMessage(Text.empty().append(CURSOR_SETTINGS_TEXT).append("..."));
    }
}
