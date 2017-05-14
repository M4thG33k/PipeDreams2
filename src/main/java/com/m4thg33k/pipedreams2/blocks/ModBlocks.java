package com.m4thg33k.pipedreams2.blocks;

import com.m4thg33k.pipedreams2.blocks.itemblocks.ItemPortTank;
import com.m4thg33k.pipedreams2.core.lib.Names;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModBlocks {

    public static BlockPortTank blockPortTank = new BlockPortTank();
    public static ItemPortTank itemPortTank = new ItemPortTank(blockPortTank);
    public static BlockGildedIron blockGildedIron = new BlockGildedIron();

    public static void preInit()
    {
        GameRegistry.register(blockPortTank);
        GameRegistry.register(itemPortTank.setRegistryName(Names.MODID, Names.TILE_PORTABLE_TANK));
        GameRegistry.register(blockGildedIron);
        GameRegistry.register(new ItemBlock(blockGildedIron).setRegistryName(Names.MODID, Names.BLOCK_GILDED_IRON));
//        GameRegistry.register(new ItemBlock(blockPortTank).setRegistryName(Names.MODID, Names.TILE_PORTABLE_TANK));
    }
}
