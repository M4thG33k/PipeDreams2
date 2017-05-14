package com.m4thg33k.pipedreams2.items;

import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModItems {

    public static ItemWrench itemWrench = new ItemWrench();
    public static ItemGildedIron itemGildedIron = new ItemGildedIron();
    public static ItemGildedIronNugget itemGildedIronNugget = new ItemGildedIronNugget();

    public static void preInit()
    {
        GameRegistry.register(itemWrench);
        GameRegistry.register(itemGildedIron);
        GameRegistry.register(itemGildedIronNugget);
    }
}
