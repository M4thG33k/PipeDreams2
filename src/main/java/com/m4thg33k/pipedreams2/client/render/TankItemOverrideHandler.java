package com.m4thg33k.pipedreams2.client.render;

import com.google.common.collect.ImmutableList;
import com.m4thg33k.pipedreams2.client.render.models.TankItemModel;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

public class TankItemOverrideHandler extends ItemOverrideList{
    public static final TankItemOverrideHandler INSTANCE = new TankItemOverrideHandler();
    public static IBakedModel baseModel;

    public TankItemOverrideHandler()
    {
        super(ImmutableList.<ItemOverride> of());
    }

    @Override
    @ParametersAreNonnullByDefault
    @Nonnull
    public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity) {
        if (stack.isEmpty())
        {
            return baseModel;
        }
        return new TankItemModel(baseModel).handleItemState(stack);
    }
}
