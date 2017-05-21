package com.m4thg33k.pipedreams2.core.transportNetwork;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class TupleBPFacingLength {

    private BlockPos pos;
    private EnumFacing facing;
    private int length;

    public TupleBPFacingLength(BlockPos pos, EnumFacing facing, int length) {
        this.pos = pos;
        this.facing = facing;
        this.length = length;
    }

    public BlockPos getPos() {
        return pos;
    }

    public void setPos(BlockPos pos) {
        this.pos = pos;
    }

    public EnumFacing getFacing() {
        return facing;
    }

    public void setFacing(EnumFacing facing) {
        this.facing = facing;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }
}
