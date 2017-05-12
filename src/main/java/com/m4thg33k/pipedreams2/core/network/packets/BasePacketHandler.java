package com.m4thg33k.pipedreams2.core.network.packets;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class BasePacketHandler implements IMessageHandler<BasePacket, IMessage> {

    @Override
    public IMessage onMessage(BasePacket message, MessageContext ctx) {
        if (ctx.side == Side.SERVER)
        {
            return message.handleServer(ctx.getServerHandler());
        }
        else
        {
            return message.handleClient(ctx.getClientHandler());
        }
    }
}
