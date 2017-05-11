package com.m4thg33k.pipedreams2.blocks.templates;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class BaseBlockTESR extends BaseBlock{

    public BaseBlockTESR(String unlocalizedName, Material material, float hardness, float resistance)
    {
        super(unlocalizedName, material, hardness, resistance);
    }

    public BaseBlockTESR(String unlocalizedName, float hardness, float resistance)
    {
        super(unlocalizedName, hardness, resistance);
    }

    public BaseBlockTESR(String unlocalizedName)
    {
        super(unlocalizedName);
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public boolean isFullyOpaque(IBlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isBlockNormalCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        return false;
    }

    @Override
    public boolean isFullBlock(IBlockState state) {
        return false;
    }
}
