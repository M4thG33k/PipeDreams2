package com.m4thg33k.pipedreams2.tiles;

import com.m4thg33k.pipedreams2.core.interfaces.IPipeTE;
import com.m4thg33k.pipedreams2.core.transportNetwork.TransportNetworkWorldSavedData;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nullable;

public class TilePipe extends TileEntity implements IPipeTE {

    private final String NID_NBT = "PipeNetworkId";
    private int networkId;

    public TilePipe()
    {
        super();

    }

    @Override
    public int getNetworkId() {
        return this.networkId;
    }

    @Override
    public void setNetworkId(int id) {
        this.networkId = id;
        this.markDirty();
        world.markAndNotifyBlock(pos, null, world.getBlockState(pos), world.getBlockState(pos), 2);
    }

    public void initializeNetworkId()
    {
        TransportNetworkWorldSavedData data = TransportNetworkWorldSavedData.get(world);
        setNetworkId(data.getNetworkForBlockPos(world, pos, false));
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.networkId  = compound.getInteger(NID_NBT);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound = super.writeToNBT(compound);
        compound.setInteger(NID_NBT, getNetworkId());
        return compound;
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(pos, 0, this.getUpdateTag());
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return this.writeToNBT(new NBTTagCompound());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        this.readFromNBT(pkt.getNbtCompound());
    }
}
