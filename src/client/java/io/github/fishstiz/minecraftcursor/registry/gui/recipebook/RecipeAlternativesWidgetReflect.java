package io.github.fishstiz.minecraftcursor.registry.gui.recipebook;

import net.minecraft.client.gui.widget.ClickableWidget;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class RecipeAlternativesWidgetReflect {
    public Supplier<Boolean> isVisible;
    public List<ClickableWidget> alternativeButtons = new ArrayList<>();

    public void clear() {
        alternativeButtons.clear();
    }
}
