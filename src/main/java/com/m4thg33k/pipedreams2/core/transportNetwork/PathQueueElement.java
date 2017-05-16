package com.m4thg33k.pipedreams2.core.transportNetwork;

import net.minecraft.util.math.BlockPos;

public class PathQueueElement {
    private TransportPath path;
    private BlockPos pos;

    public PathQueueElement(BlockPos pos, TransportPath path)
    {
        this.pos = pos;
        this.path = path;
    }

    public TransportPath getPath() {
        return path;
    }

    public BlockPos getPos() {
        return pos;
    }
}
