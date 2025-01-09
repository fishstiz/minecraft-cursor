package io.github.fishstiz.minecraftcursor.gui;

import net.minecraft.text.Text;

public enum HotspotVisibility {
    MODIFIED("minecraft-cursor.options.hot.modified"),
    ALWAYS("minecraft-cursor.options.hot.always"),
    HIDE("minecraft-cursor.options.hot.hide");

    public final String key;
    int ordinal;
    final static HotspotVisibility[] values = HotspotVisibility.values();

    static {
        int counter = 1;
        for (HotspotVisibility visibility : values)
            visibility.ordinal = counter++;
    }

    HotspotVisibility(String key) {
        this.key = key;
    }

    public Text getText() {
        return Text.translatable(this.key);
    }

    public static HotspotVisibility getVisibility(int ordinal) {
        return values[ordinal - 1];
    }
}
