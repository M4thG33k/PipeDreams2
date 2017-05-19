package com.m4thg33k.pipedreams2.core.transportNetwork;

import com.m4thg33k.pipedreams2.util.LogHelper;
import com.m4thg33k.pipedreams2.util.PipeDreams2Util;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BlockToBlockPathMap {
    private Map<BlockPos, Map<BlockPos, TransportPath>> map = new HashMap<>();

    public void put(BlockPos head, BlockPos tail, TransportPath path)
    {
        if (path == null)
        {
            LogHelper.error("Null path being submitted by " + head + "\t" + tail);
        }
        if (!map.containsKey(head))
        {
            map.put(head, new HashMap<>());
        }

        map.get(head).put(tail, path);
    }

    public void put(BlockPosTuple tup, TransportPath path)
    {
        if (path == null)
        {
            LogHelper.error("Null path being submitted by " + tup.getFirst() + "\t" + tup.getSecond());
        }
        this.put(tup.getFirst(), tup.getSecond(), path);
    }

    public TransportPath get(BlockPos head, BlockPos tail)
    {
        if (map.containsKey(head))
        {
            return map.get(head).get(tail);
        }
        return null;
    }

    public TransportPath get(BlockPosTuple tup)
    {
        return this.get(tup.getFirst(), tup.getSecond());
    }

    public void remove(BlockPos head, BlockPos tail)
    {
        if (map.containsKey(head))
        {
            map.get(head).remove(tail);
        }
    }

    public void removeHead(BlockPos head)
    {
        if (map.containsKey(head))
        {
            map.remove(head);
        }
    }

    public Set<BlockPos> getHeads()
    {
        return map.keySet();
    }

    public Set<BlockPos> getTails(BlockPos head)
    {
        if (map.containsKey(head))
        {
            return map.get(head).keySet();
        }
        return null;
    }

    public Map<BlockPos, TransportPath> getHeadMap(BlockPos head)
    {
        return map.get(head);
    }

    public void putAll(BlockToBlockPathMap other)
    {
        for (BlockPos head : other.getHeads())
        {
            if (map.containsKey(head))
            {
                map.get(head).putAll(other.getHeadMap(head));
            }
            else
            {
                map.put(head, other.getHeadMap(head));
            }
        }
    }

    public NBTTagList getTagList()
    {
        NBTTagList list = new NBTTagList();
        for (BlockPos head : map.keySet())
        {
            NBTTagCompound headNBT = PipeDreams2Util.getPosTag(head);
            NBTTagList tailList = new NBTTagList();
            for (BlockPos tail : map.get(head).keySet())
            {
                NBTTagCompound tailNBT = PipeDreams2Util.getPosTag(tail);
                tailNBT = map.get(head).get(tail).writeToNBT(tailNBT);
                tailList.appendTag(tailNBT);
            }
            headNBT.setTag("tailList", tailList);
            list.appendTag(headNBT);
        }
        return list;
    }

    public static BlockToBlockPathMap loadFromTagList(NBTTagList list)
    {
        BlockToBlockPathMap ret = new BlockToBlockPathMap();
        for (int i=0; i<list.tagCount(); i++)
        {
            NBTTagCompound headNBT = list.getCompoundTagAt(i);
            BlockPos head = PipeDreams2Util.getPosFromTag(headNBT);
            NBTTagList tailList = headNBT.getTagList("tailList", 10);
            for (int j=0; j<tailList.tagCount(); j++)
            {
                NBTTagCompound tailNBT = tailList.getCompoundTagAt(j);
                BlockPos tail = PipeDreams2Util.getPosFromTag(tailNBT);
                TransportPath path = TransportPath.loadFromNBT(tailNBT);
                ret.put(head, tail, path);
            }
        }

        return ret;
    }

    public Set<BlockPosTuple> keySet()
    {
        Set<BlockPosTuple> ret = new HashSet<>();
        for (BlockPos head : getHeads())
        {
            for (BlockPos tail : getTails(head))
            {
                ret.add(new BlockPosTuple(head, tail));
            }
        }

        return ret;
    }
}
