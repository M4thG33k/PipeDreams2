package com.m4thg33k.pipedreams2.items;

import com.m4thg33k.pipedreams2.PipeDreams2;
import com.m4thg33k.pipedreams2.core.lib.Names;
import net.minecraft.item.Item;

public class ItemBaseItem extends Item {

    public ItemBaseItem(String name)
    {
        super();

        this.setCreativeTab(PipeDreams2.tabPipeDreams);
        this.setUnlocalizedName(name);

        this.handleRegName(name);
    }

    protected void handleRegName(String name)
    {
        this.setRegistryName(Names.MODID, name);
    }
}
