package com.m4thg33k.pipedreams2.core.inventory.guicontainers;

import com.m4thg33k.pipedreams2.client.guiElements.ModGuiButton;
import com.m4thg33k.pipedreams2.core.inventory.containers.ContainerTilePort;
import com.m4thg33k.pipedreams2.core.lib.Names;
import com.m4thg33k.pipedreams2.tiles.TilePort;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GuiTilePort extends GuiContainer {
    private ResourceLocation back_tex = new ResourceLocation(Names.PATH_GUI_TEX + "gui_port.png");
    private TextureManager textureManager;

    private List<ModGuiButton> modButtonList = new ArrayList<>();
    private ModGuiButton test;

    public GuiTilePort(IInventory playerInventory, TilePort te)
    {
        super(new ContainerTilePort(playerInventory, te));

        this.xSize = 176;
        this.ySize = 166;

        textureManager = Minecraft.getMinecraft().getTextureManager();
    }

    @Override
    public void initGui() {
        super.initGui();

        modButtonList.add(this.test = new ModGuiButton(this.getGuiLeft()-18, this.getGuiTop(), "test", "test2"));
        this.test.setEnabled(true);
        this.test.setVisible(true);
    }

    private void drawModGuiButtons(int mouseX, int mouseY)
    {
        for (ModGuiButton button : modButtonList)
        {
            button.drawButton(textureManager, mouseX, mouseY);
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        textureManager.bindTexture(back_tex);

        int k = (width - xSize) / 2;
        int ell = (height - ySize) / 2;

        //draw background
        drawTexturedModalRect(k, ell, 0, 0, xSize, ySize);

        drawModGuiButtons(mouseX, mouseY);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        for (ModGuiButton button : modButtonList)
        {
            if (button.isEnabled() && button.isMouseOver())
            {
                button.playPressSound(Minecraft.getMinecraft().getSoundHandler());
                this.getContainer().testFunction();
                break;
            }
        }
    }

    private ContainerTilePort getContainer()
    {
        return (ContainerTilePort)this.inventorySlots;
    }
}
