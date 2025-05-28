package io.github.fishstiz.minecraftcursor.impl;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import io.github.fishstiz.minecraftcursor.gui.screen.ConfigurationScreen;

public class MinecraftCursorModMenuApiImpl implements ModMenuApi {
    @Override
    public ConfigScreenFactory<ConfigurationScreen> getModConfigScreenFactory() {
        return ConfigurationScreen::new;
    }
}
