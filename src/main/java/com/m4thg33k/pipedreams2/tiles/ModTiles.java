package com.m4thg33k.pipedreams2.tiles;

import com.m4thg33k.pipedreams2.core.lib.Names;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModTiles {

    public static void init()
    {
        String prefix = "tile." + Names.MODID;
        GameRegistry.registerTileEntity(TilePortTank.class, prefix + Names.TILE_PORTABLE_TANK);
        GameRegistry.registerTileEntity(TilePipe.class, prefix + Names.TILE_TRANSPORT_PIPE);
        GameRegistry.registerTileEntity(TilePort.class, prefix + Names.TILE_TRANSPORT_PORT);
    }
}
