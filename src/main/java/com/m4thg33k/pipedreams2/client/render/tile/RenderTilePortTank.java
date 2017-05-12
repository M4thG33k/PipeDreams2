package com.m4thg33k.pipedreams2.client.render.tile;

import com.m4thg33k.pipedreams2.client.handlers.ClientTickHandler;
import com.m4thg33k.pipedreams2.client.render.models.ModelSphere;
import com.m4thg33k.pipedreams2.client.render.models.ModelTankValve;
import com.m4thg33k.pipedreams2.client.render.models.SphereModels;
import com.m4thg33k.pipedreams2.core.lib.Names;
import com.m4thg33k.pipedreams2.tiles.TilePortTank;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

import java.util.Random;

public class RenderTilePortTank extends RenderTileSymmetricBase<TilePortTank> {

    ModelTankValve tankValve;
    ModelSphere center = null;
    ModelSphere sphere = null;
    double radius = 0;

    @Override
    public void renderTileEntityAt(TilePortTank te, double x, double y, double z, float partialTicks, int destroyStage) {
        if (te == null || !te.getWorld().isBlockLoaded(te.getPos(), false))
        {
            return;
        }

        if (tankValve == null)
        {
            tankValve = new ModelTankValve();
        }

        if (center == null)
        {
            center = new ModelSphere(new ResourceLocation(Names.MODID, "blocks/tank_sphere"));
        }

        radius = te.getRadius();
        sphere = radius > 0 ? SphereModels.getSphereFromFluid(te.getFluid()) : null;
        radius += 0.1;

        double worldTime = (double)(ClientTickHandler.ticksInGame + partialTicks) +
                new Random(te.getPos().hashCode()).nextInt(360);

        //get matrix set to correct location
        GlStateManager.pushMatrix();
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableBlend();
        GlStateManager.disableLighting();

        GlStateManager.color(1f, 1f, 1f, 1f);
        Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        GlStateManager.translate(x + 0.5, y + 0.5, z + 0.5);

        GlStateManager.pushMatrix();
        GlStateManager.scale(0.1, 0.1, 0.1);
        center.renderModel();
        GlStateManager.popMatrix();

        renderValves(te);
//        renderValves(te, connections);

        if (sphere != null)
        {
            GlStateManager.pushMatrix();
            GlStateManager.scale(radius, radius, radius);
            GlStateManager.rotate((float) worldTime, 0f, 1f, 0f);
            sphere.renderModel();
            GlStateManager.popMatrix();
        }

        GlStateManager.popMatrix();
    }

    private void renderValves(TilePortTank tank)
    {
        //// TODO: 5/11/2017 handle special renders for connections types
        GlStateManager.pushMatrix();
        rotate(EnumFacing.DOWN);
        tankValve.renderModel(0);
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        rotate(EnumFacing.UP);
        tankValve.renderModel(0);
        GlStateManager.popMatrix();
    }
}
