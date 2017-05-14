package com.m4thg33k.pipedreams2.blocks.templates;

import com.m4thg33k.pipedreams2.PipeDreams2;
import com.m4thg33k.pipedreams2.core.lib.Names;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class BaseBlock extends Block{

    public BaseBlock(String unlocalizedName, Material material, float hardness, float resistance)
    {
        super(material);
        this.setUnlocalizedName(unlocalizedName);
        this.setHardness(hardness);
        this.setResistance(resistance);

        this.handleRegName(unlocalizedName);

        this.setCreativeTab(PipeDreams2.tabPipeDreams);
    }

    public BaseBlock(String unlocalizedName, float hardness, float resistance)
    {
        this(unlocalizedName, Material.ROCK, hardness, resistance);
    }

    public BaseBlock(String unlocalizedName)
    {
        this(unlocalizedName, 2.0f, 10.0f);
    }

    private void handleRegName(String name){
        this.setRegistryName(Names.MODID, name);
    }
}
