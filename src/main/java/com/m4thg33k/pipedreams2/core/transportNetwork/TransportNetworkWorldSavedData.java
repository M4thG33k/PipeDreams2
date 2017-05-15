package com.m4thg33k.pipedreams2.core.transportNetwork;

import com.m4thg33k.pipedreams2.core.lib.Names;
import com.m4thg33k.pipedreams2.util.LogHelper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.storage.MapStorage;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;

public class TransportNetworkWorldSavedData extends WorldSavedData {

    private static final String DATA_NAME = Names.MODID + "_TransportNetworkData";
    private int test = 0;
    private NBTTagCompound data = new NBTTagCompound();
    private static final String NBT_NAME = DATA_NAME + "_NBT";

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
        test = data.getInteger("test");
        LogHelper.info("Test after reading NBT: " + test);
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
}
