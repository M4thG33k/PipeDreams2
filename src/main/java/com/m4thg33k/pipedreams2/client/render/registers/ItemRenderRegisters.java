package com.m4thg33k.pipedreams2.client.render.registers;

import com.m4thg33k.pipedreams2.blocks.ModBlocks;
import com.m4thg33k.pipedreams2.client.render.TankRenderHelper;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;

public class ItemRenderRegisters {

    public static void registerItemRenderers()
    {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ModBlocks.blockPortTank),
                0,
                TankRenderHelper.SMART_MODEL);
    }
}
