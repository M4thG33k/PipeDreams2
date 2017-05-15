package com.m4thg33k.pipedreams2.core.transportNetwork;

import com.m4thg33k.pipedreams2.util.PipeDreams2Util;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/*
    Keeps track of the path for transporting items/fluids/etc through the pipe network.
    The locations set holds onto hash codes corresponding to the relative location of the block positions
    from the head to any pipe in the path.

    The directions list is an array which tells which direction(s) need to be travelled in order to traverse the path.
 */
public class TransportPath {

    private Set<Integer> locations = new HashSet<>();
    private List<Direction> directions;
    private BlockPos head;
    private BlockPos last = new BlockPos(0, 0, 0);
    private int length = 0;

    public TransportPath(BlockPos head)
    {
        this.head = head;
        locations.add(last.hashCode());
        length = 1;
    }

    public void addDirection(EnumFacing facing)
    {
        if (directions == null)
        {
            directions = new ArrayList<>();
            directions.add(new Direction(facing));
        }
        else
        {
            Direction end = directions.get(directions.size()-1);
            if (end.isSameDirection(facing))
            {
                end.increment();
            }
            else
            {
                directions.add(new Direction(facing));
            }
        }
        last = last.offset(facing);
        locations.add(last.hashCode());
        length += 1;
    }

    public boolean isPosInPath(BlockPos pos)
    {
        BlockPos relative = pos.subtract(head);
        return locations.contains(relative.hashCode());
    }

    public BlockPos getLast()
    {
        return last;
    }

    public TransportPath getReversePath()
    {
        BlockPos newHead = head.add(last);
        TransportPath ret = new TransportPath(newHead);

        for (int i=directions.size()-1; i>=0; i--)
        {
            EnumFacing facing = directions.get(i).getFacing().getOpposite();
            int count = directions.get(i).getCount();
            while (count > 0)
            {
                ret.addDirection(facing);
                count -= 1;
            }
        }

        if (ret.getLast() != this.head)
        {
            throw new Error("Error creating reversed path. Original: " + this.toString() + " New: " + ret.toString());
        }

        return ret;
    }

    @Override
    public String toString() {
        String ret = "";
        for (Direction direction : directions)
        {
            ret += direction.toString() + "#";
        }
        return ret.substring(0, ret.length()-1);
    }

    public int pathLength()
    {
        return length;
    }

    public NBTTagCompound writeToNBT(NBTTagCompound tagCompound)
    {
        tagCompound.setIntArray("locations", locationsAsArray());
        NBTTagList list = new NBTTagList();
        for (int i=0; i < directions.size(); i++)
        {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setInteger("index", i);
            tag = directions.get(i).writeToNBT(tag);
            list.appendTag(tag);
        }
        tagCompound.setTag("directions", list);
        tagCompound.setTag("head", PipeDreams2Util.getPosTag(head));
        tagCompound.setTag("last", PipeDreams2Util.getPosTag(last));
        tagCompound.setInteger("length", length);

        return tagCompound;
    }

    public void readFromNBT(NBTTagCompound tagCompound)
    {
        getLocationsFromArray(tagCompound.getIntArray("locations"));
        NBTTagList list = tagCompound.getTagList("directions", 10);
        directions = new ArrayList<>(list.tagCount());
        for (int i=0; i<list.tagCount(); i++)
        {
            NBTTagCompound tag = list.getCompoundTagAt(i);
            int index = tag.getInteger("index");
            directions.set(index, new Direction(null));
            directions.get(index).readFromNBT(tag);
        }
        head = PipeDreams2Util.getPosFromTag(tagCompound.getCompoundTag("head"));
        last = PipeDreams2Util.getPosFromTag(tagCompound.getCompoundTag("last"));
        length = tagCompound.getInteger("length");
    }

    private int[] locationsAsArray()
    {
        int[] locs = new int[locations.size()];
        int i = 0;
        for (Integer ell : locations)
        {
            locs[i] = ell;
            i += 1;
        }
        return locs;
    }

    private void getLocationsFromArray(int[] array)
    {
        locations = new HashSet<>();
        for (int v : array)
        {
            locations.add(v);
        }
    }

    private class Direction
    {
        private EnumFacing facing;
        private int count;

        public Direction(EnumFacing facing)
        {
            this.facing = facing;
            count = 1;
        }

        public boolean isSameDirection(EnumFacing facing)
        {
            return this.facing == facing;
        }

        public void increment()
        {
            this.count += 1;
        }

        public EnumFacing getFacing() {
            return facing;
        }

        public int getCount() {
            return count;
        }

        @Override
        public String toString() {
            return PipeDreams2Util.facingChar(facing) + ":" + count;
        }

        public NBTTagCompound writeToNBT(NBTTagCompound compound)
        {
            compound.setInteger("facing", facing.ordinal());
            compound.setInteger("count", count);
            return compound;
        }

        public void readFromNBT(NBTTagCompound compound)
        {
            facing = EnumFacing.values()[compound.getInteger("facing")];
            count = compound.getInteger("count");
        }
    }
}
