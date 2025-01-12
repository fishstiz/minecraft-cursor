package io.github.fishstiz.minecraftcursor.registry;

import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;

public class ScreenCursorRegistry extends CursorTypeRegistry {
    public ScreenCursorRegistry() {
    }

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
            throw new RuntimeException("Class not found: " + fullyQualifiedClassName, e);
        }
    }

    @Override
    public void register(Class<? extends Element> screenClass, ElementCursorTypeFunction elementToCursorType) {
        assert Screen.class.isAssignableFrom(screenClass) :
                screenClass.getName() + " is not an instance of net.minecraft.client.gui.screen.Screen";
        super.register(screenClass, elementToCursorType);
    }
}
