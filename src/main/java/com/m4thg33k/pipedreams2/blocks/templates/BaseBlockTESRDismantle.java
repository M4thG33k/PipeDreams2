package com.m4thg33k.pipedreams2.blocks.templates;

import com.m4thg33k.pipedreams2.core.interfaces.IDismantleable;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BaseBlockTESRDismantle extends BaseBlockTESR implements IDismantleable {

    public BaseBlockTESRDismantle(String unlocalizedName, Material material, float hardness, float resistance)
    {
        super(unlocalizedName, material, hardness, resistance);
    }

    public BaseBlockTESRDismantle(String unlocalizedName, float hardness, float resistance)
    {
        super(unlocalizedName, hardness, resistance);
    }

    public BaseBlockTESRDismantle(String unlocalizedName)
    {
        super(unlocalizedName);
    }

    @Override
    public boolean isDismantleable(EntityPlayer player, World world, BlockPos pos) {
        return true;
    }

    @Override
    public void dismantle(EntityPlayer player, World world, BlockPos pos) {

    }
}
