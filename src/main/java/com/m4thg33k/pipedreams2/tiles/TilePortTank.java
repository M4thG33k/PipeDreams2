package com.m4thg33k.pipedreams2.tiles;

import com.m4thg33k.pipedreams2.core.interfaces.IDismantleableTile;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import scala.unchecked;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;

public class TilePortTank extends TileEntity implements ITickable, IDismantleableTile, IFluidHandler{

    public static final int CAPACITY = 8 * Fluid.BUCKET_VOLUME;
    protected FluidTank tank = new FluidTank(CAPACITY);

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
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound = super.writeToNBT(compound);
        compound = tank.writeToNBT(compound);

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
//        while (oldFacing != playerFacing)
//        {
//            //// TODO: 5/11/2017 handle rotation of connections
//        }

        this.markDirty();
        this.world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 1);
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) || super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull  Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
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
            if (side != null)
            {
                int amount = tank.fill(resource, doFill);
                if (doFill && amount > 0)
                {
                    performUpdate();
                }
                return amount;
            }
            return 0;
        }

        @Nullable
        @Override
        public FluidStack drain(FluidStack resource, boolean doDrain) {
            if (side != null)
            {
                FluidStack moved = tank.drain(resource, doDrain);
                if (doDrain && moved != null && moved.amount > 0)
                {
                    performUpdate();
                }
                return moved;
            }
            return null;
        }

        @Nullable
        @Override
        public FluidStack drain(int maxDrain, boolean doDrain) {
            if (side != null)
            {
                FluidStack moved = tank.drain(maxDrain, doDrain);
                if (doDrain && moved != null && moved.amount > 0)
                {
                    performUpdate();
                }
                return moved;
            }
            return null;
        }
    }
}
