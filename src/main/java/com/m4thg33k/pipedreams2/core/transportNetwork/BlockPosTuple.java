package com.m4thg33k.pipedreams2.core.transportNetwork;

import com.m4thg33k.pipedreams2.util.PipeDreams2Util;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

public class BlockPosTuple {
    private final BlockPos first;
    private final BlockPos second;

    public BlockPosTuple(BlockPos first, BlockPos second)
    {
        this.first = first;
        this.second = second;
    }

    public BlockPos getFirst() {
        return first;
    }

    public BlockPos getSecond() {
        return second;
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

    public BlockPosTuple getReversed()
    {
        return new BlockPosTuple(this.second, this.first);
    }

    @Override
    public String toString() {
        return "[[" + first.toString() + " & " + second.toString() + "]]";
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
