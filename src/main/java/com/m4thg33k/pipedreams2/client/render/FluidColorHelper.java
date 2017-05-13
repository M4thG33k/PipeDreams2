package com.m4thg33k.pipedreams2.client.render;

import com.m4thg33k.pipedreams2.util.LogHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fluids.FluidRegistry;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.IntBuffer;
import java.util.HashMap;

public class FluidColorHelper {

    private static BufferedImage blockTextures;
    private static int WIDTH, HEIGHT;
    private static boolean colorMapGenerated = false;
    private static HashMap<String, Vec3d> colorMap = new HashMap<>();

    // Borrowed from LexManos's TextureDump: https://gist.github.com/LexManos/3c700306331080598daf
    public static void saveGlTexture(String filename, int textureId, int mipmapLevels)
    {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);

        GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 1);
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);

        for (int level=0; level <= mipmapLevels; level++)
        {
            int width = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, level, GL11.GL_TEXTURE_WIDTH);
            int height = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, level, GL11.GL_TEXTURE_HEIGHT);
            int size = width*height;

            BufferedImage bufferedImage = new BufferedImage(width, height, 2);
            File output = new File(filename + "_" + textureId + ".png");

            IntBuffer buffer = BufferUtils.createIntBuffer(size);
            int[] data = new int[size];

            GL11.glGetTexImage(GL11.GL_TEXTURE_2D, level, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, buffer);
            buffer.get(data);
            bufferedImage.setRGB(0, 0, width, height, data, 0, width);

            if (textureId == Minecraft.getMinecraft().getTextureMapBlocks().getGlTextureId())
            {
                blockTextures = bufferedImage;
                WIDTH = width;
                HEIGHT = height;
            }

            try {
                ImageIO.write(bufferedImage, "png", output);
            } catch (IOException e)
            {
                LogHelper.info("Failed to write file: " + filename);
            }
        }
    }

    private static void createFluidColorMaps()
    {
        if (colorMapGenerated)
        {
            return;
        }

        colorMapGenerated = true;

        TextureMap textureMap = Minecraft.getMinecraft().getTextureMapBlocks();
        int umin, umax, vmin, vmax, r, g, b, pixel;
        float numPix;

        for (String name : FluidRegistry.getRegisteredFluids().keySet())
        {
            TextureAtlasSprite icon = textureMap.getAtlasSprite(FluidRegistry.getFluid(name).getStill().toString());

            umin = (int)(WIDTH*icon.getMinU());
            umax = (int)(WIDTH*icon.getMaxU());
            vmin = (int)(HEIGHT*icon.getMinV());
            vmax = (int)(HEIGHT*icon.getMaxV());

            numPix = (umax-umin+1)*(vmax-vmin+1);

            r = g = b = 0;

            for (int u=umin; u<umax; u++)
            {
                for (int v=vmin; v < vmax; v++)
                {
                    pixel = blockTextures.getRGB(u, v);
                    b += pixel & 255;
                    g += (pixel >> 8) & 255;
                    r += (pixel >> 16) & 255;
                }
            }

            r = (int)(r/numPix);
            g = (int)(g/numPix);
            b = (int)(b/numPix);

            colorMap.put(name, new Vec3d(r/256.0, g/256.0, b/256.0));
        }
    }

    public static Vec3d getFluidRGB(String name)
    {
        if (!colorMapGenerated)
        {
            createFluidColorMaps();
        }

        return colorMap.get(name);
    }

    public static void postTextureStitch(TextureStitchEvent.Post post) throws Exception
    {
        saveGlTexture("totalTextureDump", post.getMap().getGlTextureId(), 0);
    }
}
