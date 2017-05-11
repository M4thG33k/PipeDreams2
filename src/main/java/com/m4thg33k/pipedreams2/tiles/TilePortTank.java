package com.m4thg33k.pipedreams2.tiles;

import com.m4thg33k.pipedreams2.core.interfaces.IDismantleableTile;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import javax.annotation.Nullable;

public class TilePortTank extends TileEntity implements ITickable, IDismantleableTile, IFluidHandler{

    public static final int CAPACITY = 8 * Fluid.BUCKET_VOLUME;
    protected FluidTank tank = new FluidTank(CAPACITY);
    protected static final int STANDARD = 0;
    protected static final int PUSH = 1;
    protected static final int PULL = 2;

    protected int MAX_FLOW = 100;

    protected final static String fluidNBT = "tankFluidNBT";

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
        return null;
    }

    @Override
    public void readItemNBT(EnumFacing playerFacing, NBTTagCompound compound) {

    }

    @Override
    public IFluidTankProperties[] getTankProperties() {
        return new IFluidTankProperties[0];
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        return 0;
    }

    @Nullable
    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain) {
        return null;
    }

    @Nullable
    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        return null;
    }

    @Override
    public void update() {
    }
}
