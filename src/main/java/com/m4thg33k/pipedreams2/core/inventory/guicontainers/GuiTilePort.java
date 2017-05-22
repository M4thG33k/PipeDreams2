package com.m4thg33k.pipedreams2.core.inventory.guicontainers;

import com.m4thg33k.pipedreams2.core.inventory.containers.ContainerTilePort;
import com.m4thg33k.pipedreams2.tiles.TilePort;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.IInventory;

public class GuiTilePort extends GuiContainer {
    public GuiTilePort(IInventory playerInventory, TilePort te)
    {
        super(new ContainerTilePort(playerInventory, te));

        this.xSize = 176;
        this.ySize = 166;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {

    }
}
