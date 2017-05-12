package com.m4thg33k.pipedreams2.core.connections;

import com.m4thg33k.pipedreams2.core.enums.EnumFluidConnectionType;
import com.m4thg33k.pipedreams2.core.interfaces.ISideProperties;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

public class FluidConnectionQuantifiers implements ISideProperties<EnumFluidConnectionType> {

    protected EnumFluidConnectionType[] connections = new EnumFluidConnectionType[6];
    public static final String FCQ_NBT_KEY = "FluidConnectionQuantifiersNBT";
    public static final int numOptions = EnumFluidConnectionType.values().length;

    public FluidConnectionQuantifiers(int[] initial_values)
    {
        constructArrayFromIntegerArray(initial_values);
    }

    private void constructArrayFromIntegerArray(int[] data)
    {
        EnumFluidConnectionType[] values = EnumFluidConnectionType.values();
        for (int i=0; i < 6; i++)
        {
            if (i < data.length)
            {
                int ordinal = data[i];
                if (ordinal >= 0 && ordinal < numOptions)
                {
                    connections[i] = values[ordinal];
                }
                else
                {
                    connections[i] = EnumFluidConnectionType.DEFAULT;
                }
            }
            else
            {
                connections[i] = EnumFluidConnectionType.DEFAULT;
            }
        }
    }

    @Override
    public int[] getDataAsIntArray()
    {
        int[] ret = new int[6];
        for (int i=0; i<6; i++)
        {
            ret[i] = connections[i].ordinal();
        }
        return ret;
    }

    @Override
    public boolean doesSideMatch(EnumFacing side, EnumFluidConnectionType value) {
        return value == connections[side.ordinal()];
    }

    @Override
    public void incrementSide(EnumFacing side) {
        this.setSide(side, EnumFluidConnectionType.values()[(connections[side.ordinal()].ordinal()+1) % numOptions]);
    }

    @Override
    public void setSide(EnumFacing side, EnumFluidConnectionType value) {
        connections[side.ordinal()] = value;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        if (compound.hasKey(FCQ_NBT_KEY))
        {
            constructArrayFromIntegerArray(compound.getIntArray(FCQ_NBT_KEY));
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setIntArray(FCQ_NBT_KEY, getDataAsIntArray());
        return compound;
    }

    @Override
    public void rotateAboutAxis(EnumFacing.Axis axis) {
        EnumFluidConnectionType[] temp = new EnumFluidConnectionType[6];

        for (EnumFacing facing : EnumFacing.values())
        {
            temp[facing.ordinal()] = connections[facing.rotateAround(axis).ordinal()];
        }

        for (EnumFacing facing : EnumFacing.values())
        {
            connections[facing.ordinal()] = temp[facing.ordinal()];
        }
    }

    @Override
    public EnumFluidConnectionType getSideType(EnumFacing side) {
        return connections[side.ordinal()];
    }
}
