package io.github.fishstiz.minecraftcursor.mixin.client;

import io.github.fishstiz.minecraftcursor.MinecraftCursorClient;
import io.github.fishstiz.minecraftcursor.registry.screen.CreativeInventoryScreenCursorRegistry;
import net.fabricmc.fabric.api.client.itemgroup.v1.FabricCreativeInventoryScreen;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
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
    private CreativeInventoryScreenCursorRegistry cursorRegistry;

    @Shadow
    protected abstract int getTabX(ItemGroup group);

    @Shadow
    protected abstract int getTabY(ItemGroup group);

    @Shadow
    private static ItemGroup selectedTab;

    @Inject(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;init()V"))
    public void init(CallbackInfo ci) {
        cursorRegistry = MinecraftCursorClient.getScreenCursorRegistry().creativeInventoryScreenRegistry;
    }

    @Inject(method = "render", at = @At("TAIL"))
    public void render(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        boolean isHovered = false;

        for (ItemGroup itemGroup : ItemGroups.getGroupsToDisplay()) {
            int i = this.getTabX(itemGroup);
            int j = this.getTabY(itemGroup);

            if (this.isPointWithinBounds(i + 3, j + 3, 21, 27, mouseX, mouseY) && itemGroup != selectedTab) {
                isHovered = true;
            }
        }

        cursorRegistry.setIsUnselectedTabHovered(isHovered);
    }

    public CreativeInventoryScreenMixin(CreativeInventoryScreen.CreativeScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }
}
