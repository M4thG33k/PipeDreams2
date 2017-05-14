package com.m4thg33k.pipedreams2.core.init;

import com.m4thg33k.pipedreams2.blocks.ModBlocks;
import com.m4thg33k.pipedreams2.items.ModItems;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class ModRecipes {

    public static void createRecipes()
    {
        registerOreDictionary();

        // Gilded Iron
        addShapelessRecipe(ModItems.itemGildedIron, 0, 1, "ingotIron", "nuggetGold");
        addShapelessRecipe(ModItems.itemGildedIronNugget, 0, 9, "ingotGildedIron");
        addShapelessRecipe(Item.getItemFromBlock(ModBlocks.blockGildedIron), 0, 1, "blockIron", "ingotGold");
        addShapelessRecipe(ModItems.itemGildedIron, 0, 9, "blockGildedIron");

        addShapedRecipe(ModItems.itemGildedIron, 0, 1, "nnn", "nnn", "nnn", 'n', "nuggetGildedIron");
        addShapedRecipe(Item.getItemFromBlock(ModBlocks.blockGildedIron), 0, 1, "iii", "iii", "iii", 'i', "ingotGildedIron");

        // Wrench
        addShapedRecipe(ModItems.itemWrench, 0, 1, " n ", " sn", "n  ", 'n', "nuggetGildedIron", 's', "stone");

        // Portable Tank
        addShapedRecipe(ModBlocks.blockPortTank, 0, 1, "nnn", " b ", "nnn", 'n', "nuggetGildedIron", 'b', Items.BUCKET);
        addShapedRecipe(ModBlocks.blockPortTank, 0, 1, "nnn", "iii", "nnn", 'n', "nuggetGildedIron", 'i', "ingotIron");
        addShapelessRecipe(ModBlocks.blockPortTank, 0, 1, ModBlocks.blockPortTank);
    }

    private static void addShapelessRecipe(Item item, int meta, int amount, Object... objects)
    {
        GameRegistry.addRecipe(new ShapelessOreRecipe(
                new ItemStack(item, amount, meta),
                objects
        ));
    }

    private static void addShapelessRecipe(Block block, int meta, int amount, Object... objects)
    {
        GameRegistry.addRecipe(new ShapelessOreRecipe(
                new ItemStack(block, amount, meta),
                objects
        ));
    }

    private static void addShapedRecipe(Item item, int meta, int amount, Object... objects)
    {
        GameRegistry.addRecipe(new ShapedOreRecipe(
                new ItemStack(item, amount, meta),
                objects
        ));
    }

    private static void addShapedRecipe(Block block, int meta, int amount, Object... objects)
    {
        GameRegistry.addRecipe(new ShapedOreRecipe(
                new ItemStack(block, amount, meta),
                objects
        ));
    }

    private static void registerOreDictionary()
    {
        OreDictionary.registerOre("ingotGildedIron", ModItems.itemGildedIron);
        OreDictionary.registerOre("nuggetGildedIron", ModItems.itemGildedIronNugget);
        OreDictionary.registerOre("blockGildedIron", ModBlocks.blockGildedIron);
    }
}
