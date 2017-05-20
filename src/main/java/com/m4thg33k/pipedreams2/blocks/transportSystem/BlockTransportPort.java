package com.m4thg33k.pipedreams2.blocks.transportSystem;

import com.m4thg33k.pipedreams2.blocks.templates.BaseBlock;
import com.m4thg33k.pipedreams2.core.interfaces.IPipe;
import com.m4thg33k.pipedreams2.core.interfaces.IPipeTE;
import com.m4thg33k.pipedreams2.core.lib.Names;
import com.m4thg33k.pipedreams2.core.transportNetwork.TransportNetwork;
import com.m4thg33k.pipedreams2.core.transportNetwork.TransportNetworkWorldSavedData;
import com.m4thg33k.pipedreams2.tiles.TilePipe;
import com.m4thg33k.pipedreams2.tiles.TilePort;
import com.m4thg33k.pipedreams2.util.LogHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Set;

public class BlockTransportPort extends BaseBlock implements IPipe {

    public BlockTransportPort()
    {
        super(Names.TILE_TRANSPORT_PORT);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    @ParametersAreNonnullByDefault
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TilePort();
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        if (worldIn.isRemote)
        {
            return;
        }
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if (tileEntity instanceof IPipeTE)
        {
            ((IPipeTE) tileEntity).initializeNetworkId();
        }
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (worldIn.isRemote)
        {
            return true;
        }
        if (!playerIn.getHeldItem(hand).isEmpty())
        {
            return true;
        }
        TransportNetworkWorldSavedData data = TransportNetworkWorldSavedData.get(worldIn);
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof IPipeTE)
        {
            TransportNetwork network = data.getNetwork(((IPipeTE) tile).getNetworkId());
            Set<BlockPos> tails = network.getConnectedPorts(pos);
            if (tails == null)
            {
                return false;
            }
            LogHelper.info("The port at position: " + pos + " has access to " + tails.size() + " ports.");
            List<BlockPos> sortedTails = network.getSortedListOfTailsFor(pos);
            for (BlockPos tail : sortedTails)
            {
                LogHelper.info("\t\t" + tail + "\t" + network.getPath(pos, tail));
            }

            if (tile instanceof TilePort)
            {
                ((TilePort) tile).attemptToMoveFluidFrom(EnumFacing.UP);
            }
        }
        return true;
    }
}
