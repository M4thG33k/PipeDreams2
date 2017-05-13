package com.m4thg33k.pipedreams2.proxy;

import com.m4thg33k.pipedreams2.blocks.ModBlocks;
import com.m4thg33k.pipedreams2.core.lib.ModConfigs;
import com.m4thg33k.pipedreams2.core.network.ModNetwork;
import com.m4thg33k.pipedreams2.core.network.packets.BaseRenderingPacket;
import com.m4thg33k.pipedreams2.core.network.packets.PacketTankFilling;
import com.m4thg33k.pipedreams2.items.ModItems;
import com.m4thg33k.pipedreams2.tiles.ModTiles;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy {

    public void preinit(FMLPreInitializationEvent event)
    {
        ModConfigs.preInit(event);
        ModNetwork.setup();
        ModItems.preInit();
        ModBlocks.preInit();
    }

    public void init(FMLInitializationEvent event)
    {
        ModTiles.init();
    }

    public void postinit(FMLPostInitializationEvent event)
    {

    }

    public void handleRenderingPacket(BaseRenderingPacket pkt)
    {

    }
}
