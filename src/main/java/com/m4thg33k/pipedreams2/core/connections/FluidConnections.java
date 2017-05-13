package com.m4thg33k.pipedreams2.core.connections;

import com.m4thg33k.pipedreams2.core.interfaces.ISideConnector;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

public class FluidConnections implements ISideConnector {

    private boolean[] connections = new boolean[6];
    private String FL_CONN_NBT_KEY = "FluidConnectionNBT";

    public FluidConnections()
    {
        this(3); // defaults only top and bottom connections to "on"
    }

    public FluidConnections(int initialSettings)
    {
        this.getConnectionsFromInteger(initialSettings);
    }

    private void getConnectionsFromInteger(int value)
    {
        for (int i=0; i < 6; i++)
        {
            connections[i] = (1 == (value % 2));
            value >>= 1;
        }
    }

    @Override
    public boolean isSideConnected(EnumFacing side) {
        return connections[side.ordinal()];
    }

    @Override
    public void toggleSideConnection(EnumFacing side) {
        setConnection(side, !connections[side.ordinal()]);
    }

    @Override
    public void setConnection(EnumFacing side, boolean isConnected) {
        connections[side.ordinal()] = isConnected;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        if (compound.hasKey(FL_CONN_NBT_KEY))
        {
            int value = compound.getInteger(FL_CONN_NBT_KEY);

            getConnectionsFromInteger(value);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setInteger(FL_CONN_NBT_KEY, getConnectionsAsInteger());
        return compound;
    }

    public int getConnectionsAsInteger()
    {
        int value = 0;
        for (int i = 5; i>=0; i--)
        {
            value = (value << 1) + (connections[i] ? 1 : 0);
        }
        return value;
    }

    @Override
    public void rotateAboutAxis(EnumFacing.Axis axis) {
        boolean[] temp = new boolean[6];

        for (EnumFacing facing : EnumFacing.values())
        {
            temp[facing.ordinal()] = connections[facing.rotateAround(axis).ordinal()];
        }

        for (EnumFacing facing : EnumFacing.values())
        {
            connections[facing.ordinal()] = temp[facing.ordinal()];
        }
    }
}
