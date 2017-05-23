package com.m4thg33k.pipedreams2.client.guiElements;

import com.m4thg33k.pipedreams2.core.lib.Names;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class GuiResourceLocationManager {

    private static Map<String, GuiTextureBundle> cache;
    public static ResourceLocation WIDGETS = new ResourceLocation(Names.PATH_GUI_TEX + "widgets.png");

    private static void initialize()
    {
        cache = new HashMap<>();

        // error
        cache.put("error", new GuiTextureBundle(0, 0, 18, 18));

        // test
        cache.put("test", new GuiTextureBundle(0, 18, 18, 18));
        cache.put("test2", new GuiTextureBundle(18, 18, 18, 18));
    }

    public static GuiTextureBundle getBundle(String name)
    {
        if (cache == null)
        {
            initialize();
        }
        return cache.containsKey(name) ? cache.get(name) : cache.get("error");
    }

}
