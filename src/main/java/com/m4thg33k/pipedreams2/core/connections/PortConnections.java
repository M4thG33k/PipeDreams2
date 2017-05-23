package com.m4thg33k.pipedreams2.core.connections;

import com.m4thg33k.pipedreams2.core.enums.EnumPortConnectionType;
import com.m4thg33k.pipedreams2.core.interfaces.ISideProperties;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

public class PortConnections implements ISideProperties<EnumPortConnectionType> {

    protected EnumPortConnectionType[] connections = new EnumPortConnectionType[6];
    public static final String PC_NBT_KEY = "PortConnectionsNBT";
    public static final int numOptions = EnumPortConnectionType.values().length;

    public PortConnections(int[] initialValues)
    {
        constructArrayFromIntegerArray(initialValues);
    }

    public PortConnections()
    {
        this(new int[0]);
    }

    private void constructArrayFromIntegerArray(int[] data)
    {
        EnumPortConnectionType[] values = EnumPortConnectionType.values();
        for (int i=0; i<6; i++)
        {
            if (i < data.length)
            {
                int ordinal = data[i];
                if (ordinal >=0 && ordinal < numOptions)
                {
                    connections[i] = values[ordinal];
                }
                else
                {
                    connections[i] = EnumPortConnectionType.DISABLED;
                }
            }
            else
            {
                connections[i] = EnumPortConnectionType.DISABLED;
            }
        }
    }

    @Override
    public boolean doesSideMatch(EnumFacing side, EnumPortConnectionType value) {
        return connections[side.ordinal()] == value;
    }

    @Override
    public void incrementSide(EnumFacing side) {
        int ordinal = connections[side.ordinal()].ordinal();
        setSide(side, EnumPortConnectionType.values()[(ordinal+1)%numOptions]);
    }

    @Override
    public void decrementSide(EnumFacing side) {
        int ordinal = connections[side.ordinal()].ordinal();
        if (ordinal == 0)
        {
            ordinal = numOptions - 1;
        }
        else
        {
            ordinal -= 1;
        }
        setSide(side, EnumPortConnectionType.values()[ordinal]);
    }

    @Override
    public void setSide(EnumFacing side, EnumPortConnectionType value) {
        connections[side.ordinal()] = value;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        constructArrayFromIntegerArray(compound.hasKey(PC_NBT_KEY) ?
                compound.getIntArray(PC_NBT_KEY) : new int[0]);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setIntArray(PC_NBT_KEY, getDataAsIntArray());
        return compound;
    }

    @Override
    public void rotateAboutAxis(EnumFacing.Axis axis) {
        EnumPortConnectionType[] temp = new EnumPortConnectionType[6];

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
    public EnumPortConnectionType getSideType(EnumFacing side) {
        return connections[side.ordinal()];
    }

    @Override
    public int[] getDataAsIntArray() {
        int[] ret = new int[6];
        for (int i=0; i<6; i++)
        {
            ret[i] = connections[i].ordinal();
        }
        return ret;
    }
}
