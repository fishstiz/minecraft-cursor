package io.github.fishstiz.minecraftcursor.mixin.client.access;

import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.item.ItemGroup;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(CreativeInventoryScreen.class)
public interface CreativeInventoryScreenAccessor extends HandledScreenAccessor<CreativeInventoryScreen.CreativeScreenHandler> {
    @Accessor("selectedTab")
    ItemGroup getSelectedTab();

    @Accessor("deleteItemSlot")
    Slot getDeleteItemSlot();

    @Invoker("getTabX")
    int invokeGetTabX(ItemGroup group);

    @Invoker("getTabY")
    int invokeGetTabY(ItemGroup group);
}
