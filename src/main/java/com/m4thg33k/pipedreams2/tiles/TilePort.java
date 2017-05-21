package com.m4thg33k.pipedreams2.tiles;

import com.m4thg33k.pipedreams2.core.interfaces.IPipeTE;
import com.m4thg33k.pipedreams2.core.transportNetwork.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class TilePort extends TileEntity implements ITickable, IPipeTE{

    private final static String NID_NBT = "PipeNetworkId";
    private int networkId;
    private int maxFlow = 1000;
    private final static int MAX_TICK = 40;
    private int fluidTick = 40;
    private int tick = 0;

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
        this.tick = compound.getInteger("tick");
        if (compound.hasKey("fluidTick")) {
            this.fluidTick = compound.getInteger("fluidTick");
        }
    }

    @Override
    @Nonnull
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound = super.writeToNBT(compound);
        compound.setInteger(NID_NBT, getNetworkId());
        compound.setInteger("tick", tick);
        compound.setInteger("fluidTick", fluidTick);
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

    public int getFluidPriority(EnumFacing facing)
    {
        // temporarily set all priorities the ordinal of the facing
        return facing.ordinal();
    }

    public int getFluidFlow(EnumFacing facing)
    {
        return maxFlow;
    }

    public void moveFluidFromHere()
    {
        if (world.isRemote)
        {
            return;
        }

        List<TupleBPFacingLength> sortedTails = getSortedOutputs();

        for (EnumFacing fromDirection: EnumFacing.values())
        {
            // get the neighboring IFluidHandler (if it exists - skip otherwise)
            TileEntity neighbor = world.getTileEntity(pos.offset(fromDirection));
            if (neighbor == null)
            {
                continue;
            }
            IFluidHandler fluidHandler = getFluidHandler(world, pos.offset(fromDirection), fromDirection.getOpposite());
            if (fluidHandler == null)
            {
                continue;
            }

            // get the fluid to move; skip if there is no such fluid
            FluidStack stackToMove = fluidHandler.drain(this.getFluidFlow(fromDirection), false);
            if (stackToMove == null || stackToMove.amount == 0)
            {
                continue;
            }

            int totalFluidMoved = 0;

            // filter the outputs to include only those with the same channel as the input
            int channel = this.getFluidChannel(fromDirection);
            List<TupleBPFacingLength> sortedChannelTails = sortedTails.stream().filter(x -> {
                TileEntity tailTile = world.getTileEntity(x.getPos());
                return (tailTile != null) &&
                        (tailTile instanceof TilePort) &&
                        ((TilePort)tailTile).getFluidChannel(x.getFacing()) == channel;
            }).collect(Collectors.toList());

            for (TupleBPFacingLength tup : sortedChannelTails)
            {
                // if there is no fluid to move, break
                if (stackToMove.amount == 0)
                {
                    break;
                }

                // if we're trying to fill the same tile from the same direction, skip it
                if (pos.hashCode() == tup.getPos().hashCode() && fromDirection == tup.getFacing())
                {
                    continue;
                }

                TileEntity tailPort = world.getTileEntity(tup.getPos());
                if (tailPort == null || !(tailPort instanceof TilePort))
                {
                    continue;
                }

                int amountMovedHere = ((TilePort) tailPort).moveFluidTo(tup.getFacing(), stackToMove);
                if (amountMovedHere > 0)
                {
                    totalFluidMoved += amountMovedHere;
                    stackToMove.amount -= amountMovedHere;
                }
            }

            if (totalFluidMoved > 0)
            {
                fluidHandler.drain(totalFluidMoved, true);
            }
        }
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
//                TransportNetwork network = TransportNetworkWorldSavedData.get(world).getNetwork(this.getNetworkId());
//                List<BlockPos> tails = network.getSortedListOfTailsFor(pos);

                List<TupleBPFacingLength> outputs = getSortedOutputs();
                for (TupleBPFacingLength tup : outputs)
                {
                    if (toMove.amount == 0)
                    {
                        break;
                    }
                    //stop the port from feeding into the same side it's pulling from
                    if (tup.getPos().hashCode() == pos.hashCode() && tup.getFacing()==direction)
                    {
                        continue;
                    }

                    TileEntity tileTail = world.getTileEntity(tup.getPos());
                    if (tileTail == null || !(tileTail instanceof TilePort))
                    {
                        continue;
                    }

                    int amountToMoveHere = ((TilePort) tileTail).moveFluidTo(tup.getFacing(), toMove);
                    if (amountToMoveHere > 0)
                    {
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

    public int moveFluidTo(EnumFacing direction, FluidStack fluidStack)
    {
        IFluidHandler fluidHandler = getFluidHandler(world, pos.offset(direction), direction.getOpposite());
        if (fluidHandler == null)
        {
            return 0;
        }
        int amountToMove = fluidHandler.fill(fluidStack, true);
        return amountToMove;
    }

    public static IFluidHandler getFluidHandler(World world, BlockPos pos, EnumFacing facing)
    {
        TileEntity tile = world.getTileEntity(pos);
        if (tile != null && tile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing))
        {
            return tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing);
        }
        return null;
    }

    public List<TupleBPFacingLength> getSortedOutputs()
    {
        List<TupleBPFacingLength> toReturn;

        TransportNetwork network = TransportNetworkWorldSavedData.get(world).getNetwork(this.networkId);

        Set<TupleBPFacingLength> set = new HashSet<>();

        for (TupleBlockPosPathLength tup : network.getSetOfTailsWithLengthsFor(pos))
        {
            TileEntity tile = world.getTileEntity(tup.getPos());
            if (tile == null || !(tile instanceof TilePort))
            {
                continue;
            }
            for (EnumFacing facing : EnumFacing.values()) {
                set.add(new TupleBPFacingLength(tup.getPos(), facing, tup.getLength()));
            }
        }

        Comparator<TupleBPFacingLength> comparator = new Comparator<TupleBPFacingLength>() {
            @Override
            public int compare(TupleBPFacingLength first, TupleBPFacingLength second) {
                TileEntity tileFirst = world.getTileEntity(first.getPos());
                if (tileFirst == null || !(tileFirst instanceof TilePort))
                {
                    return 1;
                }
                TileEntity tileSecond = world.getTileEntity(second.getPos());
                if ( tileSecond == null || !(tileSecond instanceof TilePort))
                {
                    return -1;
                }

                int priorityCheck = Integer.compare(((TilePort) tileFirst).getFluidPriority(first.getFacing()),
                        ((TilePort) tileSecond).getFluidPriority(second.getFacing()));
                if (priorityCheck != 0)
                {
                    return priorityCheck;
                }

                // They have the same priority, so chose the closer one
                return Integer.compare(first.getLength(), second.getLength());
            }
        };

        toReturn = new ArrayList<>(set);
        Collections.sort(toReturn, comparator);
        return toReturn;
    }

    @Override
    public void update() {
        if (world.isRemote)
        {
            return;
        }
        if (fluidTick == 0)
        {
            fluidTick = 40;
        }

        tick = (tick + 1) % MAX_TICK;

        if (tick % fluidTick == 0)
        {
            moveFluidFromHere();
//            attemptToMoveFluidFrom(EnumFacing.UP);
        }
    }

    public int getFluidChannel(EnumFacing facing)
    {
        // temp. return the ordinal
        return facing.ordinal();
    }
}
