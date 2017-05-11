package com.m4thg33k.pipedreams2.client.handlers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayDeque;
import java.util.Queue;

public class ClientTickHandler {

    public static volatile Queue<Runnable> scheduledActions = new ArrayDeque<Runnable>();

    public static int ticksInGame = 0;
    public static float partialTicks = 0f;
    public static float delta = 0f;
    public static float total = 0f;

    private void calculateDelta()
    {
        float oldTotal = total;
        total = ticksInGame + partialTicks;
        delta = total - oldTotal;
    }

    @SubscribeEvent
    public void renderTick(TickEvent.RenderTickEvent event)
    {
        if (event.phase == TickEvent.Phase.START)
        {
            partialTicks = event.renderTickTime;
        }
    }

    @SubscribeEvent
    public void clientTickEnd(TickEvent.ClientTickEvent event)
    {
        if (event.phase == TickEvent.Phase.END)
        {
            Minecraft mc = Minecraft.getMinecraft();

            GuiScreen gui = mc.currentScreen;

            if (gui == null || !gui.doesGuiPauseGame())
            {
                ++ticksInGame;
                partialTicks = 0;
            }
        }

        calculateDelta();
    }
}
