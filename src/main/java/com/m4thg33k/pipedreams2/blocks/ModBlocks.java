package com.m4thg33k.pipedreams2.blocks;

import com.m4thg33k.pipedreams2.core.lib.Names;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModBlocks {

    public static BlockPortTank blockPortTank = new BlockPortTank();

    public static void preInit()
    {
        GameRegistry.register(blockPortTank);
        GameRegistry.register(new ItemBlock(blockPortTank).setRegistryName(Names.MODID, Names.TILE_PORTABLE_TANK));
    }
}
