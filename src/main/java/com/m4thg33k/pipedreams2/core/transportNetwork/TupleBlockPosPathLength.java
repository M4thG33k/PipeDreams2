package com.m4thg33k.pipedreams2.core.transportNetwork;

import net.minecraft.util.math.BlockPos;

public class TupleBlockPosPathLength {

    private BlockPos pos;
    private int length;

    public TupleBlockPosPathLength(BlockPos pos, int length) {
        this.pos = pos;
        this.length = length;
    }

    public BlockPos getPos() {
        return pos;
    }

    public void setPos(BlockPos pos) {
        this.pos = pos;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }
}
