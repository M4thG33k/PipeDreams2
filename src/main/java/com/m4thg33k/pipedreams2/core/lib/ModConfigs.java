package com.m4thg33k.pipedreams2.core.lib;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ModConfigs {

    public static Configuration config;

    public static String SPHERE_MODEL;

    public static void preInit(FMLPreInitializationEvent event)
    {
        config = new Configuration(event.getSuggestedConfigurationFile());

        config.load();

        SPHERE_MODEL = "Sphere" + config.get("Graphics",
                "tankRenderDetail",
                2,
                "Set to a number between 1 and 4 (inclusive) to change the quality of the tank's sphere render. " +
                        "Higher numbers will be higher quality, but can slow down your system. " +
                        "[Defaults to 2]", 1, 4).getInt();

        config.save();
    }
}
