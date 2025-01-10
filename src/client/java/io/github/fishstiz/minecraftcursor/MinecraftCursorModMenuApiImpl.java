package io.github.fishstiz.minecraftcursor;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class MinecraftCursorModMenuApiImpl implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        // TODO: Mod Menu Screen
        return screen -> screen;
    }
}
