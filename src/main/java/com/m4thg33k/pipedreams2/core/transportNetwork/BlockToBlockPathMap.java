package com.m4thg33k.pipedreams2.core.transportNetwork;

import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.Map;

public class BlockToBlockPathMap {
    Map<BlockPos, Map<BlockPos, TransportPath>> map = new HashMap<>();

    public void put(BlockPos head, BlockPos tail, TransportPath path)
    {
        if (!map.containsKey(head))
        {
            map.put(head, new HashMap<>());
        }

        map.get(head).put(tail, path);
    }

    public TransportPath get(BlockPos head, BlockPos tail)
    {
        if (map.containsKey(head))
        {
            return map.get(head).get(tail);
        }
        return null;
    }

    public void remove(BlockPos head, BlockPos tail)
    {
        if (map.containsKey(head))
        {
            map.get(head).remove(tail);
        }
    }
}
