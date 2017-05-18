package com.m4thg33k.pipedreams2.proxy;

import com.m4thg33k.pipedreams2.blocks.ModBlocks;
import com.m4thg33k.pipedreams2.client.events.CommonEvents;
import com.m4thg33k.pipedreams2.core.init.ModRecipes;
import com.m4thg33k.pipedreams2.core.lib.ModConfigs;
import com.m4thg33k.pipedreams2.core.network.ModNetwork;
import com.m4thg33k.pipedreams2.core.network.packets.BaseRenderingPacket;
import com.m4thg33k.pipedreams2.core.network.packets.PacketTankFilling;
import com.m4thg33k.pipedreams2.items.ModItems;
import com.m4thg33k.pipedreams2.tiles.ModTiles;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy {

    public void preinit(FMLPreInitializationEvent event)
    {
        ModConfigs.preInit(event);
        ModNetwork.setup();
        MinecraftForge.EVENT_BUS.register(new CommonEvents());
        ModItems.preInit();
        ModBlocks.preInit();
    }

    public void init(FMLInitializationEvent event)
    {
        ModTiles.init();
        ModRecipes.createRecipes();
    }

    public void postinit(FMLPostInitializationEvent event)
    {

    }

    public void handleRenderingPacket(BaseRenderingPacket pkt)
    {

    }
}
