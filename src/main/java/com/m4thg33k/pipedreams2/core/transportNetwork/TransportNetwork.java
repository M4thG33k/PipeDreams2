package com.m4thg33k.pipedreams2.core.transportNetwork;

import com.m4thg33k.pipedreams2.util.LogHelper;
import com.m4thg33k.pipedreams2.util.PipeDreams2Util;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.stream.Collectors;

public class TransportNetwork {

    private Set<BlockPos> pipeSet = new HashSet<>();
    private Set<BlockPos> portSet = new HashSet<>();
    private Map<BlockPos, Set<BlockPos>> adjacencyList = new HashMap<>();
//    private Map<BlockPosTuple, TransportPath> paths = new HashMap<>();
    private BlockToBlockPathMap paths = new BlockToBlockPathMap();

    // data used for articulation point calculations
    private Map<BlockPos, Boolean> isArticulationPoint;
    private Map<BlockPos, Integer> depth;
    private Map<BlockPos, Boolean> visited;
    private Map<BlockPos, Integer> lowValue;
    private Map<BlockPos, BlockPos> parent;

    private boolean isDirty = true;

    public TransportNetwork(BlockPos start, boolean isPort)
    {
        pipeSet.add(start);
        if (isPort)
        {
            portSet.add(start);
            paths.put(new BlockPosTuple(start, start), new TransportPath(start));
        }
    }

    public TransportNetwork(BlockPos start)
    {
        this(start, false);
    }

    /** Merges the input networks together into one large, connected network.
     * Recall that all networks should be connected components. All networks should
     * also not initially contain pos, but should have a/some pipe(s) adjacent
     * to pos; the merge will create a single connected component with an
     * articulation point at pos. (That is, none of the original paths will change.
     * The function that calls this method should be responsible for deleting all non-primary networks
     * once the operation is complete.
     * Returns null iff the merge is unsuccessful. **/
    @Nullable
    @ParametersAreNonnullByDefault
    public static TransportNetwork mergeNetworks(BlockPos pos, boolean isPort, List<TransportNetwork> networks)
    {
        if (networks.size() == 0)
        {
            return null;
        }

        for (TransportNetwork net : networks)
        {
            if (net.contains(pos) || net.getNeighborsIn(pos).size() == 0)
            {
                return null;
            }
        }

        // Copy the first network in the list (all others will be merged into this one)
        TransportNetwork ret = TransportNetwork.loadFromNBT(networks.get(0).writeToNBT(new NBTTagCompound()));
        Set<BlockPos> firstPorts = new HashSet<>(ret.portSet);
        Map<BlockPos, TransportPath> firstPaths = ret.createPathsToPortsStartingAt(pos);
        ret.add(pos, isPort, firstPaths);

        // For each subsequent network
        for (int i=1; i<networks.size(); i++)
        {
            TransportNetwork net = networks.get(i);
            Set<BlockPos> secondPorts = net.portSet;
            Map<BlockPos, TransportPath> secondPaths = net.createPathsToPortsStartingAt(pos);

            // Create paths between the two components
            for (BlockPos firstPos : firstPorts)
            {
                for (BlockPos secondPos : secondPorts)
                {
                    TransportPath newPath = firstPaths.get(firstPos).getReversePath().appendPath(secondPaths.get(secondPos));
                    ret.setPath(new BlockPosTuple(firstPos, secondPos), newPath);
                    ret.setPath(new BlockPosTuple(secondPos, firstPos), newPath.getReversePath());
                }
            }

            // if the input location is a port, we have to add all the paths accordingly
            if (isPort)
            {
                for (BlockPos tail : secondPorts)
                {
                    ret.setPath(new BlockPosTuple(pos, tail), secondPaths.get(tail));
                    ret.setPath(new BlockPosTuple(tail, pos), secondPaths.get(tail).getReversePath());
                }
            }

            // Update our temporary information
            firstPorts.addAll(secondPorts);
            firstPaths.putAll(secondPaths);

            // Copy all original data from the second component to the one we're returning
            ret.addPipes(net.pipeSet);
            ret.addPorts(net.portSet);
            ret.addPaths(net.paths);
            ret.adjacencyList.putAll(net.adjacencyList);
        }

        // make sure to add the new pipe's adjacency information after merging all the networks
        ret.addToAdjacencyList(pos);

        return ret;
    }

    public boolean contains(BlockPos pos)
    {
        return pipeSet.contains(pos);
    }

    private void setPath(BlockPosTuple tuple, TransportPath path)
    {
        this.paths.put(tuple, path);
    }

    private void addPipes(Set<BlockPos> pipes)
    {
        this.pipeSet.addAll(pipes);
        this.isDirty = true;
    }

    private void addPorts(Set<BlockPos> ports)
    {
        this.portSet.addAll(ports);
        this.isDirty = true;
    }

    private void addPaths(BlockToBlockPathMap map)
    {
        this.paths.putAll(map);
        this.isDirty = true;
    }

    private void addToAdjacencyList(BlockPos pos)
    {
        Set<BlockPos> neighbors = getNeighborsIn(pos);
        if (adjacencyList.containsKey(pos))
        {
            adjacencyList.get(pos).addAll(neighbors);
        }
        else
        {
            adjacencyList.put(pos, neighbors);
        }

        for (BlockPos n : neighbors)
        {
            if (!adjacencyList.containsKey(n)) {
                adjacencyList.put(n, new HashSet<>());
            }
            adjacencyList.get(n).add(pos);
        }

        this.isDirty = true;
    }

    public Set<BlockPos> getNeighborsIn(BlockPos pos)
    {
        HashSet<BlockPos> ret = new HashSet<>();
        for (EnumFacing facing : EnumFacing.values())
        {
            if (pipeSet.contains(pos.offset(facing)))
            {
                ret.add(pos.offset(facing));
            }
        }

        return ret;
    }

    public void add(BlockPos pos, boolean isPort, Map<BlockPos, TransportPath> pathsFromPos)
    {
        pipeSet.add(pos);

        // For each current path, check if there is a shorter path through the newly added pipe/port
//        Set<BlockPosTuple> keySet = paths.keySet();
        Set<BlockPos> headSet = paths.getHeads();
        for (BlockPos head : headSet)
        {
            Set<BlockPos> tailSet = paths.getTails(head);
            for (BlockPos tail : tailSet)
            {
                TransportPath path = paths.get(head, tail);
                TransportPath toHead = pathsFromPos.get(head);
                TransportPath toTail = pathsFromPos.get(tail);

                if (path.pathLength() > (toHead.pathLength() + toTail.pathLength()))
                {
                    TransportPath shorterPath = toHead.getReversePath().appendPath(toTail);
                    paths.put(head, tail, shorterPath);
                }
            }
        }
//        for (BlockPosTuple key : keySet)
//        {
//            TransportPath path = paths.get(key);
//            TransportPath toFirst = pathsFromPos.get(key.first);
//            TransportPath toSecond = pathsFromPos.get(key.second);
//
//            if (path.pathLength() > (toFirst.pathLength() + toSecond.pathLength()))
//            {
//                TransportPath shorterPath = toFirst.getReversePath().appendPath(toSecond);
//
//                paths.put(key, shorterPath);
//            }
//        }

        // If the BlockPos corresponds to a port, add it to the port list. Then calculate the path between
        // this new port and all the previously stored ports
        if (isPort)
        {
            portSet.add(pos);
            // Since the input pathsFromPos didn't know this was a port originally, we
            // have to manually add the path from this port to itself.
            paths.put(new BlockPosTuple(pos, pos), new TransportPath(pos));

            for (BlockPos end: portSet)
            {
                if (end.hashCode() == pos.hashCode())
                {
                    continue;
                }
                paths.put(new BlockPosTuple(pos, end), pathsFromPos.get(end));
                paths.put(new BlockPosTuple(end, pos), pathsFromPos.get(end).getReversePath());
            }
        }

        addToAdjacencyList(pos);
    }

    public void add(BlockPos pos, boolean isPort)
    {
        pipeSet.add(pos);

        // Find the path between the newly added pipe/port to all ports already in the network
        Map<BlockPos, TransportPath> pathsFromPos = createPathsToPortsStartingAt(pos);

        // For each current path, check if there is a shorter path through the newly added pipe/port
        Set<BlockPos> headSet = paths.getHeads();
        for (BlockPos head : headSet)
        {
            Set<BlockPos> tailSet = paths.getTails(head);
            for (BlockPos tail : tailSet)
            {
                TransportPath path = paths.get(head, tail);
                TransportPath toHead = pathsFromPos.get(head);
                TransportPath toTail = pathsFromPos.get(tail);

                if (path.pathLength() > (toHead.pathLength() + toTail.pathLength()))
                {
                    TransportPath shorterPath = toHead.getReversePath().appendPath(toTail);
                    paths.put(head, tail, shorterPath);
                }
            }
        }

//        Set<BlockPosTuple> keySet = paths.keySet();
//        for (BlockPosTuple key : keySet)
//        {
//            TransportPath path = paths.get(key);
//            TransportPath toFirst = pathsFromPos.get(key.first);
//            TransportPath toSecond = pathsFromPos.get(key.second);
//
//            if (path.pathLength() > (toFirst.pathLength() + toSecond.pathLength()))
//            {
//                TransportPath shorterPath = toFirst.getReversePath().appendPath(toSecond);
//
//                paths.put(key, shorterPath);
//            }
//        }

        // If the BlockPos corresponds to a port, add it to the port list. Then calculate the path between
        // this new port and all the previously stored ports
        if (isPort)
        {
            portSet.add(pos);
            // Since the input pathsFromPos didn't know this was a port originally, we
            // have to manually add the path from this port to itself.
            paths.put(new BlockPosTuple(pos, pos), new TransportPath(pos));

            for (BlockPos end: portSet)
            {
                if (end.hashCode() == pos.hashCode())
                {
                    continue;
                }
                paths.put(new BlockPosTuple(pos, end), pathsFromPos.get(end));
                paths.put(new BlockPosTuple(end, pos), pathsFromPos.get(end).getReversePath());
            }

            LogHelper.info("Added port at: " + pos);
            LogHelper.info(toString());
        }

        addToAdjacencyList(pos);
    }


    public Map<BlockPos, TransportPath> createPathsToPortsStartingAt(BlockPos pos)
    {
        Set<BlockPos> seen = new HashSet<>();
        Map<BlockPos, TransportPath> newPaths = new HashMap<>();
        LinkedList<PathQueueElement> queue = new LinkedList<>();
        queue.add(new PathQueueElement(pos, new TransportPath(pos)));

        while (queue.size() > 0)
        {
            PathQueueElement element = queue.pop();
            BlockPos ePos = element.getPos();
            TransportPath path = element.getPath();

            if (seen.contains(ePos))
            {
                continue;
            }

            seen.add(ePos);

            if (portSet.contains(ePos))
            {
                newPaths.put(ePos, path.copy());
            }

            for (EnumFacing side : EnumFacing.values())
            {
                BlockPos child = ePos.offset(side);
                if (pipeSet.contains(child) && !seen.contains(child))
                {
                    TransportPath childPath = path.copy();
                    childPath.addDirection(side);
                    queue.add(new PathQueueElement(child, childPath));
                }
            }
        }

        return newPaths;
    }

    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        // save pipeSet
        compound.setTag("pipeList", PipeDreams2Util.getTagListForBlockPosSet(pipeSet));

        // save portSet
        compound.setTag("portList", PipeDreams2Util.getTagListForBlockPosSet(portSet));

        // save adjacencyList
        NBTTagList adj = new NBTTagList();
        for (BlockPos pos : adjacencyList.keySet())
        {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setTag("key", PipeDreams2Util.getPosTag(pos));
            tag.setTag("values", PipeDreams2Util.getTagListForBlockPosSet(adjacencyList.get(pos)));
            adj.appendTag(tag);
        }
        compound.setTag("adjacencyList", adj);

        // save paths
        NBTTagList pathList = paths.getTagList();
//        NBTTagList pathList = new NBTTagList();
//        for (BlockPosTuple tuple : paths.keySet())
//        {
//            NBTTagCompound tag = new NBTTagCompound();
//            tag.setTag("key", tuple.writeToNBT(new NBTTagCompound()));
//            tag.setTag("value", paths.get(tuple).writeToNBT(new NBTTagCompound()));
//            pathList.appendTag(tag);
//        }
        compound.setTag("paths", pathList);

        // save articulation points
        NBTTagList apList = new NBTTagList();
        if (isArticulationPoint != null) {
            calculateArticulationPoints();
            for (BlockPos pos : isArticulationPoint.keySet()) {
                NBTTagCompound tag = PipeDreams2Util.getPosTag(pos);
                tag.setBoolean("isArticulationPoint", isArticulationPoint.get(pos));
                apList.appendTag(tag);
            }
        }
        compound.setTag("articulationPoints", apList);

        compound.setBoolean("isDirty", isDirty);

        return compound;
    }

    public void readFromNBT(NBTTagCompound compound)
    {
        // read pipeSet
        this.pipeSet = PipeDreams2Util.getBlockPosSetFromTagList(compound.getTagList("pipeList", 10));

        // read portSet
        this.portSet = PipeDreams2Util.getBlockPosSetFromTagList(compound.getTagList("portList", 10));

        // read adjacencyList
        NBTTagList adj = compound.getTagList("adjacencyList", 10);
        adjacencyList = new HashMap<>();
        for (int i=0; i<adj.tagCount(); i++)
        {
            NBTTagCompound tag = adj.getCompoundTagAt(i);
            BlockPos key = PipeDreams2Util.getPosFromTag(tag.getCompoundTag("key"));
            HashSet<BlockPos> values = PipeDreams2Util.getBlockPosSetFromTagList(tag.getTagList("values", 10));
            adjacencyList.put(key, values);
        }

        // read paths
        NBTTagList pathList = compound.getTagList("paths", 10);
        paths = BlockToBlockPathMap.loadFromTagList(pathList);
//        paths = new HashMap<>();
//        NBTTagList pathList = compound.getTagList("paths", 10);
//        for (int i=0; i<pathList.tagCount(); i++)
//        {
//            NBTTagCompound tag = pathList.getCompoundTagAt(i);
//            BlockPosTuple key = BlockPosTuple.loadFromNBT(tag.getCompoundTag("key"));
//            TransportPath value = TransportPath.loadFromNBT(tag.getCompoundTag("value"));
//            paths.put(key, value);
//        }

        isArticulationPoint = new HashMap<>();
        NBTTagList apList = compound.getTagList("articulationPoints", 10);
        for (int i=0; i<apList.tagCount(); i++)
        {
            NBTTagCompound tag = apList.getCompoundTagAt(i);
            BlockPos pos = PipeDreams2Util.getPosFromTag(tag);
            isArticulationPoint.put(pos, tag.getBoolean("isArticulationPoint"));
        }

        isDirty = compound.getBoolean("isDirty");
    }

    public static TransportNetwork loadFromNBT(NBTTagCompound compound)
    {
        TransportNetwork ret = new TransportNetwork(new BlockPos(0, 0, 0));
        ret.readFromNBT(compound);
        return ret;
    }

    private void resetArticulationPointData()
    {
        visited = new HashMap<>();
        depth = new HashMap<>();
        lowValue = new HashMap<>();
        isArticulationPoint = new HashMap<>();
        parent = new HashMap<>();

        for (BlockPos pos : pipeSet)
        {
            visited.put(pos, false);
            isArticulationPoint.put(pos, false);
            parent.put(pos, null);
        }
    }

    private void GetArticulationPoints(BlockPos index, int d)
    {
        visited.put(index, true);
        depth.put(index, d);
        lowValue.put(index, d);
        int childCount = 0;

        for (BlockPos child : adjacencyList.get(index))
        {
            if (!visited.get(child))
            {
                parent.put(child, index);
                GetArticulationPoints(child, d+1);
                childCount += 1;
                if (lowValue.get(child) >= depth.get(index))
                {
                    isArticulationPoint.put(index, true);
                }
                lowValue.put(index, Math.min(lowValue.get(index), lowValue.get(child)));
            }
            else if (parent.get(index) != null && child.hashCode() != parent.get(index).hashCode())
            {
                lowValue.put(index, Math.min(lowValue.get(index), depth.get(child)));
            }
        }

        if (parent.get(index) == null)
        {
            isArticulationPoint.put(index, childCount > 1);
        }
    }

    public void calculateArticulationPoints()
    {
//        if (!isDirty)
//        {
//            return;
//        }
//        isDirty = false;
        resetArticulationPointData();
        boolean done = false;
        for (BlockPos pos : pipeSet)
        {
            if (done)
            {
                return;
            }
//            LogHelper.info("Trying to find AP with root at: " + pos);
            GetArticulationPoints(pos, 0);
            done = true;
//            LogHelper.info(isArticulationPoint);
        }

    }

    public boolean isPosAnArticulationPoint(BlockPos pos)
    {
        this.calculateArticulationPoints();
        return isArticulationPoint.get(pos);
    }

    private void recalculatePathsThrough(BlockPos pos)
    {
        Set<BlockPos> heads = new HashSet<>(paths.getHeads());
        for (BlockPos head : heads)
        {
            if (head.hashCode() == pos.hashCode())
            {
                paths.removeHead(head);
                continue;
            }

            Set<BlockPos> tails = new HashSet<>(paths.getTails(head));
            for (BlockPos tail : tails)
            {
                if (tail.hashCode() == pos.hashCode())
                {
                    paths.remove(head, tail);
                    continue;
                }

                TransportPath path = paths.get(head, tail);
                if (path.isPosInPath(pos))
                {
                    paths.remove(head, tail);
                    TransportPath newPath = findPathFromTo(head, tail);
                    if (newPath == null)
                    {
                        continue;
                    }
                    paths.put(head, tail, findPathFromTo(head, tail));
                }
            }
        }

//        Set<BlockPosTuple> endpoints = paths.keySet();
//
//        for (BlockPosTuple tup: endpoints)
//        {
//            if (paths.get(tup).isPosInPath(pos))
//            {
//                paths.remove(tup);
//                paths.remove(tup.getReversed());
//
//                paths.put(tup, findPathFromTo(tup.first, tup.second));
//                paths.put(tup.getReversed(), paths.get(tup).getReversePath());
//            }
//        }
    }

    private TransportPath findPathFromTo(BlockPos start, BlockPos end)
    {
        Set<BlockPos> seen = new HashSet<>();
        LinkedList<PathQueueElement> queue = new LinkedList<>();
        queue.add(new PathQueueElement(start, new TransportPath(start)));

        while (queue.size() > 0)
        {
            PathQueueElement element = queue.pop();
            BlockPos pos = element.getPos();
            TransportPath path = element.getPath();

            if (pos == end)
            {
                return path;
            }

            if (seen.contains(pos))
            {
                continue;
            }

            seen.add(pos);

            for (EnumFacing facing : EnumFacing.values())
            {
                BlockPos newPos = pos.offset(facing);
                if (!seen.contains(newPos) && pipeSet.contains(newPos))
                {
                    TransportPath childPath = path.copy();
                    childPath.addDirection(facing);
                    queue.add(new PathQueueElement(newPos, childPath));
                }
            }
        }

        return null;
    }

    /*
    Gets a list of all TransportNetworks created by deleting the pipe at the given location.
    We assume that the position passed in is an articulation point of this network.
     */
    public List<TransportNetwork> getChildNetworksAt(BlockPos pos)
    {
        Set<BlockPos> seen = new HashSet<>();
        seen.add(pos);

        List<TransportNetwork> ret = new ArrayList<>();

        int networkId = 0;
        for (BlockPos child : adjacencyList.get(pos))
        {
            if (seen.contains(child))
            {
                continue;
            }

            //create a new network with only this element
            ret.add(new TransportNetwork(child, this.portSet.contains(child)));
            Set<BlockPos> component = new HashSet<>();
            component.add(child);
            component.add(pos);
            LinkedList<BlockPos> queue = new LinkedList<>();
            queue.addAll(adjacencyList.get(child));

            while (queue.size() > 0)
            {
                BlockPos current = queue.pop();
                if (component.contains(current))
                {
                    continue;
                }
                component.add(current);
                ret.get(networkId).add(current, this.portSet.contains(current));

                for (BlockPos currentChild : adjacencyList.get(current))
                {
                    if (!component.contains(currentChild))
                    {
                        queue.add(currentChild);
                    }
                }
            }

            seen.addAll(component);
            networkId += 1;
        }

        return ret;
    }

    public Set<BlockPos> getPipeSet()
    {
        return this.pipeSet;
    }

    public int getSize()
    {
        return pipeSet.size();
    }

    public void removeNonArticulationPoint(BlockPos pos)
    {
        this.pipeSet.remove(pos);
        boolean isPort = this.portSet.contains(pos);
        if (isPort)
        {
            this.portSet.remove(pos);

            // remove all paths that had this position as an endpoint
            Set<BlockPos> heads = new HashSet<>(paths.getHeads());
            for (BlockPos head : heads)
            {
                if (head.hashCode() == pos.hashCode())
                {
                    paths.removeHead(head);
                    continue;
                }
                Set<BlockPos> tails = new HashSet<>(paths.getTails(head));
                for (BlockPos tail : tails)
                {
                    if (tail.hashCode() == pos.hashCode())
                    {
                        paths.remove(head, tail);
                    }
                }
            }
//            Set<BlockPosTuple> keys = paths.keySet();
//            for (BlockPosTuple key : keys)
//            {
//                if (key.first == pos || key.second == pos)
//                {
//                    paths.remove(key);
//                }
//            }
        }

        // make sure to remove data from the adjacency list
        for (BlockPos nei : adjacencyList.get(pos))
        {
            adjacencyList.get(nei).remove(pos);
        }
        adjacencyList.remove(pos);

        recalculatePathsThrough(pos);
    }

    @Override
    public String toString() {
        String ret = "TransportNetwork\t" + pipeSet.size() + "\t" + portSet.size() + "\n";
        for (BlockPosTuple key : paths.keySet())
        {
            try {
                ret += key.toString() + " --> " + paths.get(key).toString() + "\n";
            }
            catch (NullPointerException e)
            {
                LogHelper.error("ERROR! NPE");
            }
        }
        return ret;
    }

    public Set<BlockPos> getConnectedPorts(BlockPos pos)
    {
        return paths.getTails(pos);
    }

    public List<BlockPos> getSortedListOfTailsFor(BlockPos head)
    {
        List<BlockPos> toReturn = new ArrayList<>(paths.getTails(head));
        Collections.sort(toReturn, new Comparator<BlockPos>() {
            @Override
            public int compare(BlockPos o1, BlockPos o2) {
                return paths.get(head, o1).compareTo(paths.get(head, o2));
            }
        });
        return toReturn;
    }

    public Set<TupleBlockPosPathLength> getSetOfTailsWithLengthsFor(BlockPos head)
    {
        Set<TupleBlockPosPathLength> ret;
        ret = paths.getTails(head).stream()
                .map(t -> new TupleBlockPosPathLength(t, paths.get(head, t).pathLength()))
                .collect(Collectors.toSet());
//        for (BlockPos tail : paths.getTails(head))
//        {
//            ret.add(new TupleBlockPosPathLength(tail, paths.get(head, tail).pathLength()));
//        }
        return ret;
    }

    public TransportPath getPath(BlockPos head, BlockPos tail)
    {
        return paths.get(head, tail);
    }
}
