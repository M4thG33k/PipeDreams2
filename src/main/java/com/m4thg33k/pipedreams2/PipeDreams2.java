package com.m4thg33k.pipedreams2;

import com.m4thg33k.pipedreams2.core.lib.Names;
import com.m4thg33k.pipedreams2.proxy.CommonProxy;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.util.Random;

@Mod(modid = Names.MODID, name = Names.MODNAME, version = Names.VERSION)
public class PipeDreams2 {

    public static final Random RAND = new Random();

    @Mod.Instance
    public static PipeDreams2 INSTANCE = new PipeDreams2();

    @SidedProxy(clientSide = "com.m4thg33k.pipedreams2.proxy.ClientProxy",
            serverSide = "com.m4thg33k.pipedreams2.proxy.ServerProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        proxy.preinit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        proxy.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        proxy.postinit(event);
    }

    public static CreativeTabs tabPipeDreams = new CreativeTabs("tabPipeDream") {
        @Override
        public ItemStack getTabIconItem() {
            return new ItemStack(Blocks.SKULL);
        }
    };
}
