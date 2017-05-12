package com.m4thg33k.pipedreams2.core.interfaces;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IToggleSides {

    boolean toggleSide(World world, BlockPos pos, EnumFacing side);

    boolean toggleOppositeSide(World world, BlockPos pos, EnumFacing side);
}
