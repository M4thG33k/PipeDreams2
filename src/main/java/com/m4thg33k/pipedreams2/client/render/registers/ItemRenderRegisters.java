package com.m4thg33k.pipedreams2.client.render.registers;

import com.m4thg33k.pipedreams2.blocks.ModBlocks;
import com.m4thg33k.pipedreams2.client.render.TankRenderHelper;
import com.m4thg33k.pipedreams2.core.lib.Names;
import com.m4thg33k.pipedreams2.items.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;

import javax.annotation.ParametersAreNonnullByDefault;

public class ItemRenderRegisters {

    public static void registerItemRenderers()
    {
        // Gilded Iron
        reg(ModItems.itemGildedIron, Names.ITEM_GILDED_IRON);
        reg(ModItems.itemGildedIronNugget, Names.ITEM_GILDED_IRON_NUGGET);
        reg(Item.getItemFromBlock(ModBlocks.blockGildedIron), Names.BLOCK_GILDED_IRON);


        // Wrench
        for (int i=0; i < 2; i++)
        {
            ModelLoader.setCustomModelResourceLocation(ModItems.itemWrench,
                    i,
                    new ModelResourceLocation(Names.MODID + ":" + Names.ITEM_WRENCH, "meta="+i));
        }

        // Portable Tank renderer
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ModBlocks.blockPortTank),
                0,
                TankRenderHelper.SMART_MODEL);
    }

    private static void reg(Item item, String name)
    {
        ModelLoader.setCustomModelResourceLocation(item,
                0,
                new ModelResourceLocation(Names.MODID + ":" + name, "inventory"));
    }

    @ParametersAreNonnullByDefault
    private static void reg(Item item, int meta, ModelResourceLocation model)
    {

        ItemModelMesher mesher = Minecraft.getMinecraft().getRenderItem().getItemModelMesher();
        ModelLoader.registerItemVariants(item, model);
        mesher.register(item, meta, model);
    }


    @ParametersAreNonnullByDefault
    private static void reg(Item item, int meta, String name, String variants)
    {
        ItemModelMesher mesher = Minecraft.getMinecraft().getRenderItem().getItemModelMesher();
        ModelResourceLocation model = new ModelResourceLocation(Names.MODID + ":" + name, variants);

        ModelLoader.registerItemVariants(item, model);
        mesher.register(item, meta, model);

//        .register(item, meta,
//                new ModelResourceLocation(Names.MODID + ":" + name, variants));
    }
}
