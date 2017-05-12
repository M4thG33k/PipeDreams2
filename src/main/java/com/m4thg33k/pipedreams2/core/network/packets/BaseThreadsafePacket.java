package com.m4thg33k.pipedreams2.core.network.packets;

import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public abstract  class BaseThreadsafePacket extends BasePacket {

    @Override
    public IMessage handleClient(final NetHandlerPlayClient netHandler) {
        FMLCommonHandler.instance().getWorldThread(netHandler).addScheduledTask(() -> {
            handleClientSafe(netHandler);
        });
        return null;
    }

    @Override
    public IMessage handleServer(final NetHandlerPlayServer netHandler) {
        FMLCommonHandler.instance().getWorldThread(netHandler).addScheduledTask(() -> {
            handleServerSafe(netHandler);
        });
        return null;
    }

    public abstract void handleClientSafe(NetHandlerPlayClient netHandler);

    public abstract void handleServerSafe(NetHandlerPlayServer netHandler);
}
