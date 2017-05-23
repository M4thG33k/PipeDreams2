package com.m4thg33k.pipedreams2.core.interfaces;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

public interface ISideProperties<E extends Enum<E>> {

    boolean doesSideMatch(EnumFacing side, E value);

    void incrementSide(EnumFacing side);

    void decrementSide(EnumFacing side);

    void setSide(EnumFacing side, E value);

    void readFromNBT(NBTTagCompound compound);

    NBTTagCompound writeToNBT(NBTTagCompound compound);

    void rotateAboutAxis(EnumFacing.Axis axis);

    E getSideType(EnumFacing side);

    int[] getDataAsIntArray();
}
