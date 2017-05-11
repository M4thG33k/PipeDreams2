package com.m4thg33k.pipedreams2.client.render.tile;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

public class RenderTileSymmetricBase<T extends TileEntity> extends TileEntitySpecialRenderer<T> {

    protected EnumFacing[] facings = EnumFacing.VALUES;

    protected void rotate(EnumFacing facing)
    {
        if (facing == null)
        {
            return;
        }

        int vertAngle = (facing == EnumFacing.DOWN ? 0 : (facing == EnumFacing.UP ? 180 : 90));
        int horizAngle = facing == EnumFacing.WEST ? 90 :
                facing == EnumFacing.SOUTH ? 180 :
                        facing == EnumFacing.EAST ? 270 : 0;

        GlStateManager.rotate(horizAngle, 0f, 1f, 0f);
        GlStateManager.rotate(vertAngle, 1f, 0f, 0f);
    }
}
