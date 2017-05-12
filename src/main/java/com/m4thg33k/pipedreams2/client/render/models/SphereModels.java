package com.m4thg33k.pipedreams2.client.render.models;

import com.m4thg33k.pipedreams2.core.lib.Names;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SphereModels {

    private static Map<String, ModelSphere> cache = new HashMap<String, ModelSphere>();
    private static List<String> names = new ArrayList<String>();
    private static String BASE_TEXTURE = "pipe_dream_tank_sphere";

    public static ModelSphere getSphereFromFluid(Fluid fluid)
    {
        if (fluid == null)
        {
            return null;
        }
        return getSphereFromFluidName(fluid.getName());
    }

    public static ModelSphere getSphereFromFluidName(String name)
    {
        if (!(names.contains(name)))
        {
            createModel(name);
        }
        return fetchModel(name);
    }

    private static void createModel(String name)
    {
        if (name.equals(BASE_TEXTURE))
        {
            cache.put(name, new ModelSphere(new ResourceLocation(Names.MODID, "blocks/tank_sphere")));
        }
        else
        {
            cache.put(name, new ModelSphere(FluidRegistry.getFluid(name).getStill()));
            names.add(name);
        }
    }

    private static ModelSphere fetchModel(String name)
    {
        return cache.get(name);
    }
}
