package com.m4thg33k.pipedreams2.client.render.models;

import com.google.common.collect.ImmutableMap;
import com.m4thg33k.pipedreams2.core.lib.Names;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.crash.CrashReport;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.Attributes;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.client.model.obj.OBJModel;
import net.minecraftforge.client.model.pipeline.LightUtil;
import net.minecraftforge.common.model.TRSRTransformation;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class ModelHelper {

    public static OBJModel loadModel(String suffix)
    {
        OBJModel model;

        try {
            model = ((OBJModel) OBJLoader.INSTANCE.loadModel(
                    new ResourceLocation(Names.PATH_BLOCK_MODELS + suffix + ".obj"))
            );
            return model;
        } catch (Exception e)
        {
            throw new ReportedException(new CrashReport("Error making the model for " + suffix + "!", e));
        }
    }

    public static IModel retextureFromResourceLocation(OBJModel model, String toReplace, ResourceLocation location)
    {
        return (((OBJModel) model.retexture(ImmutableMap.of(toReplace, location.toString())))
                .process(ImmutableMap.of("flip-v", "true")));
    }

    public static IModel retextureFromLocation(OBJModel model, String toReplace, String modID, String suffix)
    {
        return (((OBJModel) model.retexture(ImmutableMap.of(toReplace, modID + ":" + suffix)))
                .process(ImmutableMap.of("flip-v", "true")));
    }

    public static IModel retexture(OBJModel model, String toReplace, String suffix)
    {
        return retextureFromLocation(model, toReplace, Names.MODID, "blocks/"+suffix); //// TODO: 5/11/2017 check this line
    }

    public static IBakedModel bake(IModel model)
    {
        return model.bake(TRSRTransformation.identity(),
                Attributes.DEFAULT_BAKED_FORMAT,
                ModelLoader.defaultTextureGetter());
    }

    public static void renderModel(IBakedModel model, int color)
    {
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.ITEM);

        List<BakedQuad> quads = model.getQuads(null, null, 0);

        for (BakedQuad bakedQuad : quads)
        {
            LightUtil.renderQuadColor(buffer, bakedQuad, color);
        }

        tessellator.draw();
    }
}
