package io.github.fishstiz.minecraftcursor;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.server.packs.PackType;

public class MinecraftCursorFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        MinecraftCursor.init();

        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new CursorResourceReloadListener());

        ScreenEvents.BEFORE_INIT.register((client, screen, width, height) -> {
            MinecraftCursor.getInstance().beforeScreenInit(screen);

            if (client.screen != null) {
                ScreenEvents.afterRender(client.screen).register((currentScreen, context, mouseX, mouseY, delta) ->
                        MinecraftCursor.getInstance().afterRenderScreen(mouseX, mouseY));
            }
        });

        ClientTickEvents.START_CLIENT_TICK.register(client -> MinecraftCursor.getInstance().tick());
    }
}
