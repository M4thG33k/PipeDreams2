package com.m4thg33k.pipedreams2.items;

import com.google.common.collect.Lists;
import com.m4thg33k.pipedreams2.PipeDreams2;
import com.m4thg33k.pipedreams2.core.lib.Names;
import com.m4thg33k.pipedreams2.core.lib.registry.IRegObject;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

public class ItemBaseItem extends Item{

    public ItemBaseItem(String name)
    {
        super();

        this.setCreativeTab(PipeDreams2.tabPipeDreams);
        this.setUnlocalizedName(name);

        this.handleRegName(name);
    }

    @Override
    @Nonnull
    public String getUnlocalizedName(ItemStack stack) {
        return super.getUnlocalizedName() + "_" + stack.getItemDamage();
    }

    protected void handleRegName(String name)
    {
        this.setRegistryName(Names.MODID, name);
    }
}
