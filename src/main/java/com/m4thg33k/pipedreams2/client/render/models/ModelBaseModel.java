package com.m4thg33k.pipedreams2.client.render.models;

import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.obj.OBJModel;

import javax.annotation.Nullable;

public class ModelBaseModel {
    protected IBakedModel myModel;

    public ModelBaseModel(String modelName, @Nullable String toReplace, @Nullable String replacement)
    {
        OBJModel theModel = ModelHelper.loadModel(modelName);

        if (toReplace == null || replacement == null)
        {
            myModel = ModelHelper.bake(theModel);
        }
        else
        {
            IModel model = ModelHelper.retexture(theModel, toReplace, replacement);

            myModel = ModelHelper.bake(model);
        }
    }

    public ModelBaseModel(String modelName)
    {
        this(modelName, null, "");
    }

    public ModelBaseModel(String modelName, String toReplace, ResourceLocation location)
    {
        OBJModel theModel = ModelHelper.loadModel(modelName);

        IModel model = ModelHelper.retextureFromResourceLocation(theModel, toReplace, location);

        myModel = ModelHelper.bake(model);
    }

    public void renderModel()
    {
        renderModel(myModel);
    }

    public void renderModel(IBakedModel model)
    {
        renderModel(model, -1);
    }

    public void renderModel(IBakedModel model, int color)
    {
        ModelHelper.renderModel(model, color);
    }

    public IBakedModel getModel()
    {
        return myModel;
    }
}
