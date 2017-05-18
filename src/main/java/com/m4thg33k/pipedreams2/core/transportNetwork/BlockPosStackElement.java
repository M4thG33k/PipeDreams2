package com.m4thg33k.pipedreams2.core.transportNetwork;

import net.minecraft.util.math.BlockPos;

public class BlockPosStackElement {
    public BlockPos pos;
    public int networkId;

    public BlockPosStackElement(BlockPos pos, int id)
    {
        this.pos = pos;
        this.networkId = id;
    }
}
