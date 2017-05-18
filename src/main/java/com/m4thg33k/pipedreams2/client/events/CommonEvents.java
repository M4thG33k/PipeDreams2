package com.m4thg33k.pipedreams2.client.events;

import com.m4thg33k.pipedreams2.blocks.ModBlocks;
import com.m4thg33k.pipedreams2.core.interfaces.IPipe;
import com.m4thg33k.pipedreams2.core.interfaces.IPipeTE;
import com.m4thg33k.pipedreams2.core.transportNetwork.TransportNetworkWorldSavedData;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CommonEvents {

    @SubscribeEvent
    public void onBlockDestroyed(BlockEvent.BreakEvent event)
    {
        if (event.isCanceled() || event.getWorld().isRemote)
        {
            return;
        }

        IBlockState state = event.getState();
        if (state.getBlock() instanceof IPipe)
        {
            TileEntity tile = event.getWorld().getTileEntity(event.getPos());
            if (tile instanceof IPipeTE)
            {
                TransportNetworkWorldSavedData data = TransportNetworkWorldSavedData.get(event.getWorld());
                int id = ((IPipeTE) tile).getNetworkId();
                if (id == 0)
                {
                    return;
                }
                data.removeBlockFromNetwork(event.getWorld(), event.getPos(), ((IPipeTE) tile).getNetworkId());
            }
        }
    }
}
