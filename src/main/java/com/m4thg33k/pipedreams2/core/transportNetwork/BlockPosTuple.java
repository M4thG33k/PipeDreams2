package com.m4thg33k.pipedreams2.core.transportNetwork;

import net.minecraft.util.math.BlockPos;

public class BlockPosTuple {
    public final BlockPos first;
    public final BlockPos second;
    public BlockPosTuple(BlockPos first, BlockPos second)
    {
        this.first = first;
        this.second = second;
    }
}
