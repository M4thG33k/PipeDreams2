package com.m4thg33k.pipedreams2.client.guiElements;

public class GuiTextureBundle {
    private int uPos;
    private int vPos;
    private int texWidth;
    private int texHeight;

    public GuiTextureBundle(int uPos, int vPos, int texWidth, int texHeight) {
        this.uPos = uPos;
        this.vPos = vPos;
        this.texWidth = texWidth;
        this.texHeight = texHeight;
    }

    public int getuPos() {
        return uPos;
    }

    public void setuPos(int uPos) {
        this.uPos = uPos;
    }

    public int getvPos() {
        return vPos;
    }

    public void setvPos(int vPos) {
        this.vPos = vPos;
    }

    public int getTexWidth() {
        return texWidth;
    }

    public void setTexWidth(int texWidth) {
        this.texWidth = texWidth;
    }

    public int getTexHeight() {
        return texHeight;
    }

    public void setTexHeight(int texHeight) {
        this.texHeight = texHeight;
    }
}
