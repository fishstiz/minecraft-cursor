package io.github.fishstiz.minecraftcursor.mixin.access;

import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.CreativeModeTab;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(CreativeModeInventoryScreen.class)
public interface CreativeInventoryScreenAccessor extends HandledScreenAccessor<CreativeModeInventoryScreen.ItemPickerMenu> {
    @Accessor("selectedTab")
    static CreativeModeTab getSelectedTab() {
        throw new AssertionError();
    }

    @Accessor("destroyItemSlot")
    Slot getDeleteItemSlot();

    @Invoker("getTabX")
    int invokeGetTabX(CreativeModeTab group);

    @Invoker("getTabY")
    int invokeGetTabY(CreativeModeTab group);
}
