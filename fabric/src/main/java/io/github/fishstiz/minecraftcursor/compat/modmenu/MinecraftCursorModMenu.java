package io.github.fishstiz.minecraftcursor.compat.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import io.github.fishstiz.minecraftcursor.gui.screen.ConfigurationScreen;

public class MinecraftCursorModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<ConfigurationScreen> getModConfigScreenFactory() {
        return ConfigurationScreen::new;
    }
}
