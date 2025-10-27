package io.github.fishstiz.minecraftcursor;

import io.github.fishstiz.minecraftcursor.gui.screen.ConfigurationScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.forgespi.language.IModInfo;

import java.nio.file.Path;

@Mod(MinecraftCursorForge.MOD_ID)
public class MinecraftCursorForge {
    // Forge does not support dashes in mod id
    public static final String MOD_ID = "minecraft_cursor";

    public MinecraftCursorForge() {
        if (FMLEnvironment.dist.isClient()) {
            ConfigScreenHolder.register();
        }
    }

    private static class ConfigScreenHolder {
        private static void register() {
            MinecraftForge.registerConfigScreen(ConfigurationScreen::new);
        }
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void addPackFinder(AddPackFindersEvent event) {
            ResourceLocation location = ResourceLocation.fromNamespaceAndPath(MOD_ID, "resourcepacks/cursors_extended");
            String packId = "mod/" + location;

            IModInfo modInfo = ModList.get().getModContainerById(MOD_ID).map(ModContainer::getModInfo).orElseThrow();
            Path path = modInfo.getOwningFile().getFile().findResource(location.getPath());

            Pack pack = Pack.readMetaAndCreate(
                    packId,
                    Component.literal("Cursors Extended"),
                    false,
                    id -> new PathPackResources(id, path, false),
                    PackType.CLIENT_RESOURCES,
                    Pack.Position.TOP,
                    PackSource.BUILT_IN
            );

            event.addRepositorySource(consumer -> consumer.accept(pack));
        }

        @SubscribeEvent
        public static void registerReloadListeners(RegisterClientReloadListenersEvent event) {
            MinecraftCursor.init();
            event.registerReloadListener(new CursorResourceReloadListener());
        }
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class ClientForgeEvents {
        @SubscribeEvent
        public static void afterClientTick(TickEvent.ClientTickEvent event) {
            if (event.phase == TickEvent.Phase.END) {
                MinecraftCursor.afterClientTick(Minecraft.getInstance());
            }
        }

        @SubscribeEvent
        public static void afterScreenRender(ScreenEvent.Render.Post event) {
            MinecraftCursor.afterCurrentScreenRender(Minecraft.getInstance(), event.getScreen(), event.getGuiGraphics(), event.getMouseX(), event.getMouseY());
        }
    }
}
