package com.m4thg33k.pipedreams2.core.transportNetwork;

import com.m4thg33k.pipedreams2.util.PipeDreams2Util;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

public class BlockPosTuple {
    public final BlockPos first;
    public final BlockPos second;

    public BlockPosTuple(BlockPos first, BlockPos second)
    {
        this.first = first;
        this.second = second;
    }

    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        compound.setTag("firstTup", PipeDreams2Util.getPosTag(first));
        compound.setTag("secondTup", PipeDreams2Util.getPosTag(second));
        return compound;
    }

    public static BlockPosTuple loadFromNBT(NBTTagCompound compound)
    {
        return new BlockPosTuple(PipeDreams2Util.getPosFromTag(compound.getCompoundTag("firstTup")),
                PipeDreams2Util.getPosFromTag(compound.getCompoundTag("secondTup")));
    }
}
