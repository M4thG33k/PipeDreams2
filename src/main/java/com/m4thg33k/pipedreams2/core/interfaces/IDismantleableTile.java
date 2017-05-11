package com.m4thg33k.pipedreams2.core.interfaces;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

public interface IDismantleableTile {

    NBTTagCompound getItemNBT(EnumFacing playerFacing);

    void readItemNBT(EnumFacing playerFacing, NBTTagCompound compound);
}
