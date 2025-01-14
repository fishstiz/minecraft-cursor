package io.github.fishstiz.minecraftcursor.registry;

import io.github.fishstiz.minecraftcursor.MinecraftCursor;
import io.github.fishstiz.minecraftcursor.registry.screen.ModScreenCursorRegistry;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;

public class ScreenCursorRegistry extends CursorTypeRegistry {
    public ScreenCursorRegistry() {
        // Needs work
//        new CreativeInventoryScreenCursorRegistry(this);
//        new InventoryScreenCursorRegistry(this);

        try {
            // Mod Menu
            new ModScreenCursorRegistry(this);
        } catch (NoClassDefFoundError ignored) {
        }
    }

    /**
     * Use with caution. May not work when out of dev environment
     */
    @Override
    @SuppressWarnings("unchecked")
    public void register(String fullyQualifiedClassName, ElementCursorTypeFunction elementToCursorType) {
        try {
            Class<?> screenClass = Class.forName(fullyQualifiedClassName);

            assert Screen.class.isAssignableFrom(screenClass) :
                    fullyQualifiedClassName + " is not an instance of net.minecraft.client.gui.screen.Screen";

            if (Element.class.isAssignableFrom(screenClass)) {
                register((Class<? extends Screen>) screenClass, elementToCursorType);
            }
        } catch (ClassNotFoundException e) {
            MinecraftCursor.LOGGER.error("Error registering screen cursor type. Class not found: {}", fullyQualifiedClassName, e);
        }
    }

    @Override
    public void register(Class<? extends Element> screenClass, ElementCursorTypeFunction elementToCursorType) {
        assert Screen.class.isAssignableFrom(screenClass) :
                screenClass.getName() + " is not an instance of net.minecraft.client.gui.screen.Screen";
        super.register(screenClass, elementToCursorType);
    }
}
