package com.m4thg33k.pipedreams2.core.network.packets;

import com.m4thg33k.pipedreams2.PipeDreams2;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import javax.annotation.Nullable;

public class PacketTankFilling extends BaseThreadsafePacket{

    private EnumFacing side; //null -> bucket
    private boolean isFilling;
    private BlockPos pos;
    private String fluidName;
    private int amount;

    public PacketTankFilling()
    {

    }

    public PacketTankFilling(BlockPos pos, @Nullable EnumFacing side, boolean isFilling, String fluidName, int amount)
    {
        this.pos = pos;
        this.side = side;
        this.isFilling = isFilling;
        this.fluidName = fluidName;
        this.amount = amount;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        this.writePos(this.pos, buf);
        buf.writeInt(side==null ? -1 : side.ordinal());
        buf.writeBoolean(isFilling);
        buf.writeInt(amount);
        ByteBufUtils.writeUTF8String(buf, fluidName);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.pos = this.readPos(buf);
        int direction = buf.readInt();
        this.side = direction == -1 ? null : EnumFacing.VALUES[direction];
        this.isFilling = buf.readBoolean();
        this.amount = buf.readInt();
        this.fluidName = ByteBufUtils.readUTF8String(buf);
    }

    public EnumFacing getSide() {
        return side;
    }

    public boolean isFilling() {
        return isFilling;
    }

    public BlockPos getPos() {
        return pos;
    }

    public String getFluidName() {
        return fluidName;
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public void handleServerSafe(NetHandlerPlayServer netHandler) {

    }

    @Override
    public void handleClientSafe(NetHandlerPlayClient netHandler) {
        PipeDreams2.proxy.handleRenderingPacket(this);
    }
}
