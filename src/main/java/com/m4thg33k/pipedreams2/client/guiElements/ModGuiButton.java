package com.m4thg33k.pipedreams2.client.guiElements;

import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.init.SoundEvents;

// Loosely modeled after the GuiButton class
public class ModGuiButton extends Gui{
    public int xPos; //top-left corner
    public int yPos; //top-left corner
    public boolean enabled;
    public boolean visible;
    protected boolean hovered;
    private GuiTextureBundle normTexBundle;
    private GuiTextureBundle hoverTexBundle;

    public ModGuiButton(int xPos, int yPos, String normalTex, String hoverTex)
    {
        this.xPos = xPos;
        this.yPos = yPos;
        this.normTexBundle = GuiResourceLocationManager.getBundle(normalTex);
        this.hoverTexBundle = GuiResourceLocationManager.getBundle(hoverTex);
    }

    public ModGuiButton(int xPos, int yPos, String normalTex)
    {
        this(xPos, yPos, normalTex, normalTex);
    }

    protected int getHoverState(boolean mouseOver)
    {
        return !enabled ? 0 : mouseOver ? 2 : 1;
    }

    private void calcHover(int x, int y)
    {
        this.hovered = x >= this.xPos && y >= this.yPos &&
                x < this.xPos + this.normTexBundle.getTexWidth() && y < this.yPos + this.normTexBundle.getTexHeight();
    }

    private void bindTexture(TextureManager texMan)
    {
        texMan.bindTexture(GuiResourceLocationManager.WIDGETS);
    }

    public void drawButton(TextureManager texMan, int mouseX, int mouseY)
    {
        if (this.visible)
        {
            calcHover(mouseX, mouseY);
            bindTexture(texMan);
            GuiTextureBundle bundle = hovered ? this.hoverTexBundle : this.normTexBundle;
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            this.drawTexturedModalRect(
                    this.xPos,
                    this.yPos,
                    bundle.getuPos(),
                    bundle.getvPos(),
                    bundle.getTexWidth(),
                    bundle.getTexHeight()
            );
        }
    }

    public boolean mousePressed(int x, int y)
    {
        calcHover(x, y);
        return this.enabled && this.hovered;
    }

    public boolean isMouseOver()
    {
        return this.hovered;
    }

    public void playPressSound(SoundHandler soundHandler)
    {
        soundHandler.playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f));
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
