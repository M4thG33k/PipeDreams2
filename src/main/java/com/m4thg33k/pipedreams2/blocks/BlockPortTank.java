package com.m4thg33k.pipedreams2.blocks;

import com.m4thg33k.pipedreams2.blocks.templates.BaseBlockTESRDismantle;
import com.m4thg33k.pipedreams2.core.lib.Names;
import com.m4thg33k.pipedreams2.tiles.TilePortTank;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockPortTank extends BaseBlockTESRDismantle {

    public BlockPortTank()
    {
        super(Names.TILE_PORTABLE_TANK);
        this.setDefaultState(blockState.getBaseState());
    }

    @Override
    public void handleRegName() {
        this.setRegistryName(Names.MODID, Names.TILE_PORTABLE_TANK);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TilePortTank();
    }
}
