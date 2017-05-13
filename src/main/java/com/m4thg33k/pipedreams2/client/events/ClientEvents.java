package com.m4thg33k.pipedreams2.client.events;

import com.m4thg33k.pipedreams2.client.render.FluidColorHelper;
import com.m4thg33k.pipedreams2.core.lib.Names;
import com.m4thg33k.pipedreams2.util.LogHelper;
import net.minecraft.util.ResourceLocation;
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
}
