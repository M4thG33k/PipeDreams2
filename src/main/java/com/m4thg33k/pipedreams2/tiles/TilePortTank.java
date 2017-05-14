package com.m4thg33k.pipedreams2.tiles;

import com.m4thg33k.pipedreams2.core.connections.FluidConnectionQuantifiers;
import com.m4thg33k.pipedreams2.core.connections.FluidConnections;
import com.m4thg33k.pipedreams2.core.enums.EnumFluidConnectionType;
import com.m4thg33k.pipedreams2.core.interfaces.IDismantleableTile;
import com.m4thg33k.pipedreams2.core.network.ModNetwork;
import com.m4thg33k.pipedreams2.core.network.packets.PacketTankFilling;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;

public class TilePortTank extends TileEntity implements ITickable, IDismantleableTile, IFluidHandler{

    public static final int CAPACITY = 8 * Fluid.BUCKET_VOLUME;
    protected FluidTank tank = new FluidTank(CAPACITY);

    protected FluidConnections fluidConnections = new FluidConnections();
    protected FluidConnectionQuantifiers fluidConnectionQuantifiers = new FluidConnectionQuantifiers(new int[6]);

    protected int MAX_FLOW = 100;

    protected HashMap<EnumFacing, SideHandler> sideHandlers = new HashMap<EnumFacing, SideHandler>();

    protected final static String fluidNBT = "tankFluidNBT";

    public TilePortTank()
    {
        for (EnumFacing facing : EnumFacing.VALUES)
        {
            sideHandlers.put(facing, new SideHandler(facing));
        }

        sideHandlers.put(null, new SideHandler(null));
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        tank.readFromNBT(compound);
        fluidConnections.readFromNBT(compound);
        fluidConnectionQuantifiers.readFromNBT(compound);

    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound = super.writeToNBT(compound);
        compound = tank.writeToNBT(compound);
        compound = fluidConnections.writeToNBT(compound);
        compound = fluidConnectionQuantifiers.writeToNBT(compound);

        return compound;
    }

    @Override
    public NBTTagCompound getItemNBT(EnumFacing playerFacing) {
        if (this.tank.getFluidAmount() == 0)
        {
            return null;
        }
        if (playerFacing == null)
        {
            playerFacing = EnumFacing.NORTH;
        }

        NBTTagCompound compound = this.writeToNBT(new NBTTagCompound());
        compound.removeTag("x");
        compound.removeTag("y");
        compound.removeTag("z");

        compound.setInteger("PlayerFacing", playerFacing.ordinal());

        return compound;
    }

    @Override
    public void readItemNBT(EnumFacing playerFacing, NBTTagCompound compound) {
        if (compound == null)
        {
            return;
        }

        this.readFromNBT(compound);
        EnumFacing oldFacing = EnumFacing.values()[compound.getInteger("PlayerFacing")];
        while (oldFacing != playerFacing)
        {
            oldFacing = this.rotateConnections(oldFacing);
        }

        this.markDirty();
        this.world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 1);
    }

    protected EnumFacing rotateConnections(EnumFacing old)
    {
        fluidConnections.rotateAboutAxis(EnumFacing.Axis.Y);
        fluidConnectionQuantifiers.rotateAboutAxis(EnumFacing.Axis.Y);
        return old.rotateYCCW();
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && isSideConnected(facing)) || super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull  Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && isSideConnected(facing))
        {
            return (T) sideHandlers.get(facing);
        }
        return super.getCapability(capability, facing);
    }

    private void performUpdate()
    {
        this.markDirty();
        this.world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 0);
    }

    protected boolean isSideConnected(EnumFacing side)
    {
        return (side == null) || fluidConnections.isSideConnected(side);
    }

    @Override
    public IFluidTankProperties[] getTankProperties() {
        return tank.getTankProperties();
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        int amount = tank.fill(resource, doFill);
        if (doFill && amount>0)
        {
            performUpdate();
        }
        return amount;
    }

    @Nullable
    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain) {
        FluidStack toReturn = tank.drain(resource, doDrain);
        if (doDrain && toReturn != null && toReturn.amount > 0)
        {
            performUpdate();
        }
        return toReturn;
    }

    @Nullable
    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        FluidStack toReturn = tank.drain(maxDrain, doDrain);
        if (doDrain && toReturn != null && toReturn.amount > 0)
        {
            performUpdate();
        }
        return toReturn;
    }

    public double getRadius()
    {
        return 0.65 * this.getPercentage();
    }

    public double getPercentage()
    {
        return tank.getFluidAmount() / ((double) tank.getCapacity());
    }

    public Fluid getFluid()
    {
        if (tank.getFluidAmount() != 0)
        {
            return tank.getFluid().getFluid();
        }
        return null;
    }

    @Override
    public void update() {
        if (world.isRemote)
        {
            return;
        }

        attemptFluidPush();
        attemptFluidPull();
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        NBTTagCompound tagCompound = new NBTTagCompound();

        return this.writeToNBT(tagCompound);
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(pos, 0, this.getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        this.readFromNBT(pkt.getNbtCompound());
    }

    public int getFluidConnectionsAsInteger()
    {
        return fluidConnections.getConnectionsAsInteger();
    }

    public void toggleFluidConnection(EnumFacing side)
    {
        fluidConnections.toggleSideConnection(side);
        performUpdate();
    }

    public EnumFluidConnectionType getConnectionType(EnumFacing side)
    {
        return fluidConnectionQuantifiers.getSideType(side);
    }

    public void incrementConnectionType(EnumFacing side)
    {
        if (!isSideConnected(side))
        {
            return;
        }
        fluidConnectionQuantifiers.incrementSide(side);
        performUpdate();
    }

    protected void moveFluid(boolean shouldMoveOut)
    {
        boolean isDirty = false;

        for (EnumFacing side : EnumFacing.values())
        {
            if (isSideConnected(side) &&
                    (fluidConnectionQuantifiers.doesSideMatch(side, shouldMoveOut ?
                            EnumFluidConnectionType.PUSH :
                            EnumFluidConnectionType.PULL)))
            {
                TileEntity tile = world.getTileEntity(pos.offset(side));
                if (tile != null &&
                        tile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side.getOpposite()))
                {
                    IFluidHandler from = shouldMoveOut ? getFluidHandlerFor(this, side) : getFluidHandlerFor(tile, side.getOpposite());
                    IFluidHandler to = shouldMoveOut ? getFluidHandlerFor(tile, side.getOpposite()) : getFluidHandlerFor(this, side);

                    int amount = to.fill(from.drain(MAX_FLOW, false), false);
                    if (amount > 0)
                    {
                        to.fill(from.drain(amount, true), true);
                        isDirty = true;
                    }
                }
            }
        }

        if (isDirty)
        {
            performUpdate();
        }
    }

    protected void attemptFluidPull()
    {
        this.moveFluid(false);
    }

    protected void attemptFluidPush()
    {
        this.moveFluid(true);
    }

    public static IFluidHandler getFluidHandlerFor(TileEntity tile, EnumFacing side)
    {
        return tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side);
    }

    @Override
    @Nonnull
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(pos, pos.add(1, 1, 1));
    }

    private class SideHandler implements IFluidHandler{
        private EnumFacing side;

        public SideHandler(EnumFacing side)
        {
            this.side = side;
        }

        @Override
        public IFluidTankProperties[] getTankProperties() {
            return tank.getTankProperties();
        }

        @Override
        public int fill(FluidStack resource, boolean doFill) {
            if (side == null)
            {
                int amount = tank.fill(resource, doFill);
                if (doFill && amount > 0)
                {
                    ModNetwork.sendToAllAround(new PacketTankFilling(
                            pos,
                            side,
                            true,
                            resource.getFluid().getName(),
                            amount
                    ), new NetworkRegistry.TargetPoint(
                            world.provider.getDimension(),
                            pos.getX(),
                            pos.getY(),
                            pos.getZ(),
                            64
                    ));
                    performUpdate();
                }
                return amount;
            }
            if (isSideConnected(side))
            {
                switch (fluidConnectionQuantifiers.getSideType(side))
                {
                    case PULL:
                    case DEFAULT:
                        int amount = tank.fill(resource, doFill);
                        if (doFill && amount > 0)
                        {
                            ModNetwork.sendToAllAround(new PacketTankFilling(
                                    pos,
                                    side,
                                    true,
                                    resource.getFluid().getName(),
                                    amount
                            ), new NetworkRegistry.TargetPoint(
                                    world.provider.getDimension(),
                                    pos.getX(),
                                    pos.getY(),
                                    pos.getZ(),
                                    64
                            ));
                            performUpdate();
                        }
                        return amount;
                    default:
                        return 0;
                }
            }
            return 0;
        }

        @Nullable
        @Override
        public FluidStack drain(FluidStack resource, boolean doDrain) {
            if (side == null)
            {
                FluidStack moved = tank.drain(resource, doDrain);
                if (doDrain && moved != null && moved.amount > 0)
                {
                    ModNetwork.sendToAllAround(new PacketTankFilling(
                            pos,
                            side,
                            false,
                            resource.getFluid().getName(),
                            moved.amount
                    ), new NetworkRegistry.TargetPoint(
                            world.provider.getDimension(),
                            pos.getX(),
                            pos.getY(),
                            pos.getZ(),
                            64
                    ));
                    performUpdate();
                }
                return moved;
            }
            if (isSideConnected(side))
            {
                switch (fluidConnectionQuantifiers.getSideType(side))
                {
                    case PUSH:
                    case DEFAULT:
                        FluidStack moved = tank.drain(resource, doDrain);
                        if (doDrain && moved != null && moved.amount > 0)
                        {
                            ModNetwork.sendToAllAround(new PacketTankFilling(
                                    pos,
                                    side,
                                    false,
                                    resource.getFluid().getName(),
                                    moved.amount
                            ), new NetworkRegistry.TargetPoint(
                                    world.provider.getDimension(),
                                    pos.getX(),
                                    pos.getY(),
                                    pos.getZ(),
                                    64
                            ));
                            performUpdate();
                        }
                        return moved;
                }
            }
            return null;
//            FluidStack moved = tank.drain(resource, doDrain);
//            if (doDrain && moved != null && moved.amount > 0)
//            {
//                performUpdate();
//            }
//            return moved;
        }

        @Nullable
        @Override
        public FluidStack drain(int maxDrain, boolean doDrain) {
            if (side == null)
            {
                FluidStack moved = tank.drain(maxDrain, doDrain);
                if (doDrain && moved != null && moved.amount > 0)
                {
                    ModNetwork.sendToAllAround(new PacketTankFilling(
                            pos,
                            side,
                            false,
                            moved.getFluid().getName(),
                            moved.amount
                    ), new NetworkRegistry.TargetPoint(
                            world.provider.getDimension(),
                            pos.getX(),
                            pos.getY(),
                            pos.getZ(),
                            64
                    ));
                    performUpdate();
                }
                return moved;
            }
            if (isSideConnected(side))
            {
                switch (fluidConnectionQuantifiers.getSideType(side))
                {
                    case PUSH:
                    case DEFAULT:
                        FluidStack moved = tank.drain(maxDrain, doDrain);
                        if (doDrain && moved != null && moved.amount > 0)
                        {
                            ModNetwork.sendToAllAround(new PacketTankFilling(
                                    pos,
                                    side,
                                    false,
                                    moved.getFluid().getName(),
                                    moved.amount
                            ), new NetworkRegistry.TargetPoint(
                                    world.provider.getDimension(),
                                    pos.getX(),
                                    pos.getY(),
                                    pos.getZ(),
                                    64
                            ));
                            performUpdate();
                        }
                        return moved;
                }
            }
            return null;
        }
    }
}
