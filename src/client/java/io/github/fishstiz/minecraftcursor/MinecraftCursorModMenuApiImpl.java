package io.github.fishstiz.minecraftcursor;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import io.github.fishstiz.minecraftcursor.gui.screen.CursorOptionsScreen;

public class MinecraftCursorModMenuApiImpl implements ModMenuApi {
    @Override
    public ConfigScreenFactory<CursorOptionsScreen> getModConfigScreenFactory() {
        return screen -> new CursorOptionsScreen(screen, MinecraftCursorClient.CURSOR_MANAGER);
    }
}
