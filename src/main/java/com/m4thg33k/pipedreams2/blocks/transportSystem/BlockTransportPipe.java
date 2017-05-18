package com.m4thg33k.pipedreams2.blocks.transportSystem;

import com.m4thg33k.pipedreams2.blocks.templates.BaseBlock;
import com.m4thg33k.pipedreams2.blocks.templates.BaseBlockTESRDismantle;
import com.m4thg33k.pipedreams2.core.interfaces.IPipe;
import com.m4thg33k.pipedreams2.core.lib.Names;
import com.m4thg33k.pipedreams2.tiles.TilePipe;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

public class BlockTransportPipe extends BaseBlock implements IPipe{

    public BlockTransportPipe()
    {
        super(Names.TILE_TRANSPORT_PIPE);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    @ParametersAreNonnullByDefault
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TilePipe();
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        if (worldIn.isRemote)
        {
            return;
        }
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if (tileEntity instanceof TilePipe)
        {
            ((TilePipe) tileEntity).initializeNetworkId();
        }
    }

    @Override
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
        return super.removedByPlayer(state, world, pos, player, willHarvest);
    }
}
