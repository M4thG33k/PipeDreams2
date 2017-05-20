package com.m4thg33k.pipedreams2.tiles;

import com.m4thg33k.pipedreams2.core.interfaces.IPipeTE;
import com.m4thg33k.pipedreams2.core.transportNetwork.TransportNetwork;
import com.m4thg33k.pipedreams2.core.transportNetwork.TransportNetworkWorldSavedData;
import com.m4thg33k.pipedreams2.core.transportNetwork.TransportPath;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class TilePort extends TileEntity implements IPipeTE{

    private final static String NID_NBT = "PipeNetworkId";
    private int networkId;
    private int maxFlow = 100;

    public TilePort()
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

    @Override
    public void initializeNetworkId() {
        TransportNetworkWorldSavedData data = TransportNetworkWorldSavedData.get(world);
        setNetworkId(data.getNetworkForBlockPos(world, pos, true));
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.networkId  = compound.getInteger(NID_NBT);
    }

    @Override
    @Nonnull
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
    @Nonnull
    public NBTTagCompound getUpdateTag() {
        return this.writeToNBT(new NBTTagCompound());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        this.readFromNBT(pkt.getNbtCompound());
    }

    public void attemptToMoveFluidFrom(EnumFacing direction)
    {
        if (world.isRemote)
        {
            return;
        }
        TileEntity neighbor = world.getTileEntity(pos.offset(direction));
        if (neighbor == null)
        {
            return;
        }
        if (neighbor.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, direction.getOpposite()))
        {
            IFluidHandler fluidHandler = neighbor.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, direction.getOpposite());
            if (fluidHandler == null)
            {
                return;
            }
            FluidStack toMove = fluidHandler.drain(maxFlow, false);
            if (toMove != null && toMove.amount > 0)
            {
                int fluidMoved = 0;
                TransportNetwork network = TransportNetworkWorldSavedData.get(world).getNetwork(this.getNetworkId());
                List<BlockPos> tails = network.getSortedListOfTailsFor(pos);

                // for simplicity, at the moment, always push the fluid in the same direction it was pulled
                for (BlockPos tail : tails)
                {
                    if (toMove.amount == 0)
                    {
                        break;
                    }
                    if (tail.hashCode() == pos.hashCode())
                    {
                        continue;
                    }
                    TileEntity tailTile = world.getTileEntity(tail.offset(direction));
                    if (tailTile == null)
                    {
                        continue;
                    }
                    IFluidHandler tailFluidHandler = tailTile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, direction.getOpposite());
                    if (tailFluidHandler == null)
                    {
                        continue;
                    }

                    int amountToMoveHere = tailFluidHandler.fill(toMove, false);
                    if (amountToMoveHere > 0)
                    {
                        tailFluidHandler.fill(toMove, true);
                        toMove.amount -= amountToMoveHere;
                        fluidMoved += amountToMoveHere;
                    }
                }

                if (fluidMoved > 0)
                {
                    fluidHandler.drain(fluidMoved, true);
                }
            }
        }
    }


}
