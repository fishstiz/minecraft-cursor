package io.github.fishstiz.minecraftcursor.mixin.client;

import io.github.fishstiz.minecraftcursor.registry.gui.ingame.CreativeInventoryScreenCursor;
import net.fabricmc.fabric.api.client.itemgroup.v1.FabricCreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemGroup;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreativeInventoryScreen.class)
public abstract class CreativeInventoryScreenMixin extends HandledScreen<CreativeInventoryScreen.CreativeScreenHandler> implements FabricCreativeInventoryScreen {
    @Unique
    private CreativeInventoryScreenCursor cursorHandler;

    @Shadow
    protected abstract int getTabX(ItemGroup group);

    @Shadow
    protected abstract int getTabY(ItemGroup group);

    @Shadow
    private static ItemGroup selectedTab;

    @Inject(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;init()V"))
    public void init(CallbackInfo ci) {
        cursorHandler = CreativeInventoryScreenCursor.getInstance();
        cursorHandler.reflect = this::reflectProperties;
        reflectProperties();
    }

    @Unique
    public void reflectProperties() {
        cursorHandler.getTabX = this::getTabX;
        cursorHandler.getTabY = this::getTabY;
        cursorHandler.isPointWithinBounds = this::isPointWithinBounds;
        CreativeInventoryScreenCursor.selectedTab = selectedTab;
    }

    public CreativeInventoryScreenMixin(CreativeInventoryScreen.CreativeScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }
}
