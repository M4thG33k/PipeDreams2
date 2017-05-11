package com.m4thg33k.pipedreams2.client.render;

import com.m4thg33k.pipedreams2.client.render.models.TankItemModel;
import com.m4thg33k.pipedreams2.core.lib.Names;
import com.m4thg33k.pipedreams2.util.LogHelper;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class TankRenderHelper {

    public static final String SMART_MODEL_NAME = Names.MODID + ":tileportabletank";
    public static final ModelResourceLocation SMART_MODEL = new ModelResourceLocation(SMART_MODEL_NAME, "inventory");

    @SubscribeEvent
    public void onModelBake(ModelBakeEvent event)
    {
        LogHelper.info("Handling bake event");
        Object object = event.getModelRegistry().getObject(SMART_MODEL);
        if (object != null)
        {
            IBakedModel existingModel = (IBakedModel) object;
            TankItemModel customModel = new TankItemModel(existingModel);
            event.getModelRegistry().putObject(SMART_MODEL, customModel);
            TankItemOverrideHandler.baseModel = customModel;
        }
    }
}
