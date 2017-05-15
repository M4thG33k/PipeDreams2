package com.m4thg33k.pipedreams2.util;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class PipeDreams2Util {

    public static String facingChar(EnumFacing facing)
    {
        return facing.name().substring(0,1);
    }

    public static EnumFacing getFacingFromChar(String s)
    {
        switch (s)
        {
            case "u":
                return EnumFacing.UP;
            case "d":
                return EnumFacing.DOWN;
            case "n":
                return EnumFacing.NORTH;
            case "s":
                return EnumFacing.SOUTH;
            case "e":
                return EnumFacing.EAST;
            case "w":
                return EnumFacing.WEST;
            default:
                return null;
        }
    }

    public static NBTTagCompound getPosTag(BlockPos pos)
    {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("x", pos.getX());
        tag.setInteger("y", pos.getY());
        tag.setInteger("z", pos.getZ());

        return tag;
    }

    public static BlockPos getPosFromTag(NBTTagCompound tag)
    {
        return new BlockPos(tag.getInteger("x"), tag.getInteger("y"), tag.getInteger("z"));
    }
}
