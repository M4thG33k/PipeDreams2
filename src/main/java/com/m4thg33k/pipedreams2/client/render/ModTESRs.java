package com.m4thg33k.pipedreams2.client.render;

import com.m4thg33k.pipedreams2.client.render.tile.RenderTilePortTank;
import com.m4thg33k.pipedreams2.tiles.TilePortTank;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class ModTESRs {

    public static void preinit()
    {
        ClientRegistry.bindTileEntitySpecialRenderer(TilePortTank.class, new RenderTilePortTank());
    }
}
