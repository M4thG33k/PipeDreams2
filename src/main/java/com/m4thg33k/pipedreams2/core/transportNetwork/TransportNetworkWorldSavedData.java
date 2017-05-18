package com.m4thg33k.pipedreams2.core.transportNetwork;

import com.m4thg33k.pipedreams2.core.interfaces.IPipeTE;
import com.m4thg33k.pipedreams2.core.lib.Names;
import com.m4thg33k.pipedreams2.util.LogHelper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.storage.MapStorage;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

public class TransportNetworkWorldSavedData extends WorldSavedData {

    private static final String DATA_NAME = Names.MODID + "_TransportNetworkData";
    private int test = 0;
    private NBTTagCompound data = new NBTTagCompound();
    private static final String NBT_NAME = DATA_NAME + "_NBT";
    private static final String NBT_NETWORK_PREFIX = "PD2_network_id_";
    private static final int NBT_NET_PRE_LEN = NBT_NETWORK_PREFIX.length();

    // Maps integers to transport networks. Every pipe should keep a reference to which network it is a part of.
    private Map<Integer, TransportNetwork> networkMap = new HashMap<>();

    public TransportNetworkWorldSavedData()
    {
        super(DATA_NAME);
    }
    public TransportNetworkWorldSavedData(String s)
    {
        super(s);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void readFromNBT(NBTTagCompound nbt) {
        data = nbt.getCompoundTag(NBT_NAME);

        for (String key : data.getKeySet())
        {
            if (key.startsWith(NBT_NETWORK_PREFIX))
            {
                int id = Integer.parseInt(key.substring(NBT_NET_PRE_LEN));
                networkMap.put(id, TransportNetwork.loadFromNBT(data.getCompoundTag(key)));
            }
        }

        LogHelper.info("There are currently " + networkMap.size() + " networks saved.");
        Iterator<Integer> iterator = networkMap.keySet().iterator();
        while (iterator.hasNext())
        {
            LogHelper.info(networkMap.get(iterator.next()).toString());
        }
    }

    @Override
    @ParametersAreNonnullByDefault
    @Nonnull
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setTag(NBT_NAME, data);
        return compound;
    }

    public static TransportNetworkWorldSavedData get(World world)
    {
        MapStorage storage = world.getMapStorage();
        TransportNetworkWorldSavedData instance = (TransportNetworkWorldSavedData) storage.getOrLoadData(TransportNetworkWorldSavedData.class, DATA_NAME);

        if (instance == null)
        {
            instance = new TransportNetworkWorldSavedData();
            storage.setData(DATA_NAME, instance);
        }

        return instance;
    }

    public int getTest()
    {
        return test;
    }

    public void increment()
    {
        test++;
        data.setInteger("test", test);
        markDirty();
    }

    public TransportNetwork getNetwork(int id)
    {
        return networkMap.get(id);
    }

    public int getNetworkForBlockPos(World worldIn, BlockPos pos, boolean isPort) {
        Set<Integer> neighborNetworks = new HashSet<>();
        for (EnumFacing facing : EnumFacing.values()) {
            TileEntity te = worldIn.getTileEntity(pos.offset(facing));
            if (te instanceof IPipeTE) {
                neighborNetworks.add(((IPipeTE) te).getNetworkId());
            }
        }

        // If there are no valid neighbors, create a new network
        if (neighborNetworks.size() == 0)
        {
            int newId = getNextValidKey(pos.hashCode());
//            while (networkMap.containsKey(newId))
//            {
//                newId += 1;
//            }

            networkMap.put(newId, new TransportNetwork(pos, isPort));
            updateData(newId);
            return newId;
        }
        // If there is only one valid neighbor, add this block to that network
        else if (neighborNetworks.size() == 1)
        {
            Iterator<Integer> iterator = neighborNetworks.iterator();
            int myId = iterator.next();
            networkMap.get(myId).add(pos, isPort);
            updateData(myId);
            return myId;
        }
        // Otherwise, we need to merge all the networks together on this new block.
        else
        {
            List<TransportNetwork> neighboringNetworks = new ArrayList<>(neighborNetworks.size());
            int lastId = 0;
            for (int id : neighborNetworks)
            {
                neighboringNetworks.add(networkMap.remove(id));
                updateData(id);
                lastId = id;
            }

            networkMap.put(lastId, TransportNetwork.mergeNetworks(pos, isPort, neighboringNetworks));
            updateData(lastId);
            // For each pipe in this newly constructed network, we need to update the network id of each block
            for (BlockPos bp : networkMap.get(lastId).getPipeSet())
            {
                TileEntity tileEntity = worldIn.getTileEntity(bp);
                if (tileEntity instanceof IPipeTE)
                {
                    ((IPipeTE) tileEntity).setNetworkId(lastId);
                }
            }

            return lastId;
        }
    }

    public void removeBlockFromNetwork(World worldIn, BlockPos pos, int networkId)
    {
        LogHelper.info("Attempting to remove from network: " + networkId);
        TransportNetwork network = networkMap.get(networkId);
        LogHelper.info(network.toString());

        // If the network only has one item, just remove the network
        if (network.getSize() == 1)
        {
            networkMap.remove(networkId);
            updateData(networkId);
        }
        // Otherwise, if the block is an articulation point for the network, we have to break the network
        // into smaller ones.
        else if (network.isPosAnArticulationPoint(pos))
        {
            List<TransportNetwork> smallerNets = network.getChildNetworksAt(pos);
            networkMap.remove(networkId);
            updateData(networkId);
            for (TransportNetwork net : smallerNets)
            {
                networkId = getNextValidKey(networkId);
//                while (networkMap.containsKey(networkId))
//                {
//                    networkId += 1;
//                }
                putAndUpdate(worldIn, networkId, net);
            }
        }
        // Otherwise, removing this block doesn't affect the connectedness of the network, but it may change
        // paths, so we need to be careful of that.
        else
        {
            networkMap.get(networkId).removeNonArticulationPoint(pos);
            updateData(networkId);
        }
    }

    private void putAndUpdate(World world, int id, TransportNetwork net)
    {
        networkMap.put(id, net);
        for (BlockPos pos : net.getPipeSet())
        {
            TileEntity tile = world.getTileEntity(pos);
            if (tile instanceof IPipeTE)
            {
                ((IPipeTE) tile).setNetworkId(id);
            }
        }
        updateData(id);
    }

    private void updateData(int changedId)
    {
        LogHelper.info("TNWSD currently has " + this.networkMap.size() + " networks saved!");
        if (networkMap.containsKey(changedId))
        {
            data.setTag(NBT_NETWORK_PREFIX+changedId, networkMap.get(changedId).writeToNBT(new NBTTagCompound()));
            markDirty();
        }
        else
        {
            if (data.hasKey(NBT_NETWORK_PREFIX+changedId))
            {
                data.removeTag(NBT_NETWORK_PREFIX+changedId);
                markDirty();
            }
        }
    }

    private int getNextValidKey(int key)
    {
        while (key==0 || networkMap.containsKey(key))
        {
            key += 1;
        }
        return key;
    }
}
