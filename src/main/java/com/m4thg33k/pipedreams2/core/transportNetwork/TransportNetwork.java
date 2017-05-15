package com.m4thg33k.pipedreams2.core.transportNetwork;

import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TransportNetwork {

    private Set<BlockPos> pipeSet = new HashSet<>();
    private Set<BlockPos> portSet = new HashSet<>();
    private Map<BlockPos, Set<BlockPos>> adjacencyList = new HashMap<>();
    private Map<BlockPosTuple, TransportPath> paths = new HashMap<>();


}
