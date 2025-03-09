package io.github.fishstiz.minecraftcursor.mixin.access;

import net.minecraft.advancements.Advancement;
import net.minecraft.client.gui.screens.advancements.AdvancementTab;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(AdvancementsScreen.class)
public interface AdvancementsScreenAccessor {
    @Accessor("tabs")
    Map<Advancement, AdvancementTab> getTabs();

    @Accessor("selectedTab")
    AdvancementTab getSelectedTab();
}
