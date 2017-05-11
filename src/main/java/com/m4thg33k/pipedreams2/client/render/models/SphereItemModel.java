package com.m4thg33k.pipedreams2.client.render.models;

import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidRegistry;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SphereItemModel extends ModelBaseMultiModel {

    public static final int QUARTER = 1;
    public static final int HALF = 2;
    public static final int THREE_QUARTERS = 3;
    public static final int FULL = 4;

    public static boolean isValidSize(int size)
    {
        return size >= 1 && size <= 4;
    }

    public SphereItemModel(int size)
    {
        Set<String> fluidNames = FluidRegistry.getRegisteredFluids().keySet();
        Map<String, ResourceLocation> map = new HashMap<String, ResourceLocation>();

        for (String name : fluidNames)
        {
            map.put(name, FluidRegistry.getFluid(name).getStill());
        }

        createModelsWithResourceLocations("item_tank_sphere"+size, "#None", map);
    }

    public IBakedModel getModel(String name)
    {
        return cachedModels.get(name);
    }
}
