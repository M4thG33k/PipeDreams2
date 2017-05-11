package com.m4thg33k.pipedreams2.client.render.models;

import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.obj.OBJModel;

import java.util.HashMap;
import java.util.Map;

public class ModelBaseMultiModel {

    protected Map<String, IBakedModel> cachedModels = new HashMap<String, IBakedModel>();

    public ModelBaseMultiModel()
    {

    }

    public void createModelsWithResourceLocations(String modelName, String toReplace, Map<String, ResourceLocation> map)
    {
        OBJModel parent = ModelHelper.loadModel(modelName);

        for (String key : map.keySet())
        {
            IModel model = ModelHelper.retextureFromResourceLocation(parent, toReplace, map.get(key));
            cachedModels.put(key, ModelHelper.bake(model));
        }
    }

    public void createModelsWithTextureName(String modelName, String toReplace, Map<String, String> map)
    {
        OBJModel parent = ModelHelper.loadModel(modelName);

        for (String key : map.keySet())
        {
            IModel model = ModelHelper.retexture(parent, toReplace, map.get(key));
            cachedModels.put(key, ModelHelper.bake(model));
        }
    }

    public void renderModel(String key)
    {
        renderModel(cachedModels.get(key));
    }

    public void renderModel(IBakedModel model)
    {
        renderModel(model, -1);
    }

    public void renderModel(IBakedModel model, int color)
    {
        ModelHelper.renderModel(model, color);
    }
}
