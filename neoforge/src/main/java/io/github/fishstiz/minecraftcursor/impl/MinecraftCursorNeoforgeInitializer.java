package io.github.fishstiz.minecraftcursor.impl;

import io.github.fishstiz.minecraftcursor.api.CursorTypeRegistrar;
import io.github.fishstiz.minecraftcursor.api.ElementRegistrar;
import io.github.fishstiz.minecraftcursor.api.MinecraftCursorInitializer;
import io.github.fishstiz.minecraftcursor.cursorhandler.ingame.TradeOfferButtonCursorHandler;

public class MinecraftCursorNeoforgeInitializer implements MinecraftCursorInitializer {
    @Override
    public void init(CursorTypeRegistrar cursorRegistrar, ElementRegistrar elementRegistrar) {
        elementRegistrar.register(new TradeOfferButtonCursorHandler("net.minecraft.client.gui.screens.inventory.MerchantScreen$TradeOfferButton"));
    }
}
