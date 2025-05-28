package io.github.fishstiz.minecraftcursor.gui.screen;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.UnaryOperator;

public record CatalogItem(@NotNull String id, @NotNull Component text, @Nullable Prefix prefix) {
    public CatalogItem {
        Objects.requireNonNull(id);
        Objects.requireNonNull(text);
    }

    public CatalogItem(String id, Component text) {
        this(id, text, null);
    }

    public CatalogItem withPrefix(UnaryOperator<Prefix> prefixModifier) {
        return new CatalogItem(this.id, this.text, prefixModifier.apply(this.prefix));
    }

    public CatalogItem withText(UnaryOperator<Component> textModifier) {
        return new CatalogItem(this.id, textModifier.apply(this.text), this.prefix);
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other == this) {
            return true;
        }
        return other instanceof CatalogItem otherItem && otherItem.id().equals(this.id());
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

    @FunctionalInterface
    public interface Prefix {
        /**
         * @return width of prefix.
         */
        int render(GuiGraphics guiGraphics, Font font, CatalogItem item, LayoutElement bounds, int spacing, int mouseX, int mouseY, float partialTick);
    }
}