package com.m4thg33k.pipedreams2.client.render.models;

import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraftforge.fluids.Fluid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SphereItemModels {
    private static Map<Integer, SphereItemModel> cache = new HashMap<Integer, SphereItemModel>();
    private static List<Integer> sizes = new ArrayList<Integer>();

    public static IBakedModel getSphereFromFluid(Fluid fluid, int size)
    {
        if (fluid == null)
        {
            return null;
        }
        return getSphereFromFluidName(fluid.getName(), size);
    }

    public static IBakedModel getSphereFromFluidName(String name, int size)
    {
        if (!SphereItemModel.isValidSize(size))
        {
            return null;
        }

        if (!(sizes.contains(size)))
        {
            createModel(size);
        }

        return fetchModel(name, size);
    }

    public static void createModel(int size)
    {
        cache.put(size, new SphereItemModel(size));
        sizes.add(size);
    }

    public static IBakedModel fetchModel(String name, int size)
    {
        return cache.get(size).getModel(name);
    }
}
