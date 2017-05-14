package com.m4thg33k.pipedreams2.items;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class ItemBaseMetaItem extends ItemBaseItem{

    public ItemBaseMetaItem(String name)
    {
        super(name);
    }

    @Override
    @Nonnull
    public String getUnlocalizedName(ItemStack stack) {
        return super.getUnlocalizedName() + "_" + stack.getItemDamage();
    }
}
