package com.m4thg33k.pipedreams2.core.interfaces;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IDismantleable {

    boolean isDismantleable(EntityPlayer player, World world, BlockPos pos);

    void dismantle(EntityPlayer player, World world, BlockPos pos);
}
