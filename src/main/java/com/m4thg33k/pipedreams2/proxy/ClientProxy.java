package com.m4thg33k.pipedreams2.proxy;

import com.m4thg33k.pipedreams2.client.events.ClientEvents;
import com.m4thg33k.pipedreams2.client.handlers.ClientTickHandler;
import com.m4thg33k.pipedreams2.client.render.ModTESRs;
import com.m4thg33k.pipedreams2.client.render.TankRenderHelper;
import com.m4thg33k.pipedreams2.client.render.registers.ItemRenderRegisters;
import com.m4thg33k.pipedreams2.core.lib.Names;
import com.m4thg33k.pipedreams2.core.network.packets.PacketTankFilling;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {

    @Override
    public void preinit(FMLPreInitializationEvent event) {
        super.preinit(event);

        OBJLoader.INSTANCE.addDomain(Names.MODID);

        MinecraftForge.EVENT_BUS.register(new ClientTickHandler());
        MinecraftForge.EVENT_BUS.register(new ClientEvents());
        MinecraftForge.EVENT_BUS.register(new TankRenderHelper());

        ModTESRs.preinit();
        ItemRenderRegisters.registerItemRenderers();
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
    }

    @Override
    public void handleRenderingPacket(PacketTankFilling pkt) {
        super.handleRenderingPacket(pkt);
    }
}
