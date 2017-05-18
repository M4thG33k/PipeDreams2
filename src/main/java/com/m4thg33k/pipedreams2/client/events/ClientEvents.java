package com.m4thg33k.pipedreams2.client.events;

import com.m4thg33k.pipedreams2.blocks.ModBlocks;
import com.m4thg33k.pipedreams2.client.render.FluidColorHelper;
import com.m4thg33k.pipedreams2.core.lib.Names;
import com.m4thg33k.pipedreams2.tiles.TilePipe;
import com.m4thg33k.pipedreams2.util.LogHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClientEvents {

    @SubscribeEvent
    public void stitchTexture(TextureStitchEvent.Pre pre)
    {

        LogHelper.info("Stitching textures into the map");

        reg(pre, "tank_valve");
        reg(pre, "tank_valve_pull");
        reg(pre, "tank_valve_push");
        reg(pre, "tank_sphere");
        reg(pre, "tank_item");
    }

    private void reg(TextureStitchEvent.Pre pre, String suffix)
    {
        pre.getMap().registerSprite(new ResourceLocation(Names.MODID, "blocks/"+suffix));
    }

    @SubscribeEvent
    public void postStitchTexture(TextureStitchEvent.Post post) throws Exception
    {
        FluidColorHelper.postTextureStitch(post);
    }

    @SubscribeEvent
    public void onBlockHighlight(DrawBlockHighlightEvent event)
    {
        RayTraceResult trace = event.getTarget();
        if (trace != null && trace.typeOfHit == RayTraceResult.Type.BLOCK)
        {
            World world = Minecraft.getMinecraft().world;
            IBlockState state = world.getBlockState(trace.getBlockPos());

            if (state.getBlock() == ModBlocks.blockTransportPipe)
            {
                TileEntity tile = world.getTileEntity(trace.getBlockPos());
                if (tile != null && tile instanceof TilePipe)
                {
                    int id = ((TilePipe) tile).getNetworkId();
                    renderNetworkId(id, event.getPlayer(), trace.getBlockPos(), event.getPartialTicks());
                }
            }
        }
    }

    private void renderNetworkId(int id, EntityPlayer player, BlockPos pos, float partialTicks)
    {
        double x = pos.getX();
        double y = pos.getY();
        double z = pos.getZ();

        double dx = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
        double dy = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
        double dz = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;

        float angleH = player.rotationYawHead;
        float angleV = 0f;

        this.renderLabel(""+id, x - dx, y-dy, z-dz, angleH, angleV);
    }

    protected void renderLabel(String name, double x, double y, double z, float angleH, float angleV)
    {
        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRendererObj;

        GlStateManager.pushMatrix();
        GlStateManager.translate(x+0.5, y+1.5, z+0.5);
        GlStateManager.glNormal3f(0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(-angleH, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(-angleV, 1.0f, 0.0f, 0.0f);
        GlStateManager.scale(-0.025f, -0.025f, 0.025f);
        GlStateManager.disableLighting();
        GlStateManager.depthMask(false);
        GlStateManager.disableDepth();

        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ZERO);
        GlStateManager.disableTexture2D();

        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer vertexBuffer = tessellator.getBuffer();
        int strLenHalved = fontRenderer.getStringWidth(name) / 2;

        vertexBuffer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        vertexBuffer.pos(-strLenHalved - 1, -1, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
        vertexBuffer.pos(-strLenHalved - 1,  8, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
        vertexBuffer.pos( strLenHalved + 1,  8, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
        vertexBuffer.pos( strLenHalved + 1, -1, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
        tessellator.draw();

        GlStateManager.enableTexture2D();

//        fontRenderer.drawString(name, -strLenHalved, 0, giveGravePriority ? 0xFFFFFF : 0x000000);
        GlStateManager.enableDepth();

        GlStateManager.depthMask(true);
        fontRenderer.drawString(name, -strLenHalved, 0, 0);

        GlStateManager.disableBlend();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);

        GlStateManager.popMatrix();
    }


}
