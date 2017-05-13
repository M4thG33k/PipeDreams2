package com.m4thg33k.pipedreams2.core.particles;

import com.m4thg33k.pipedreams2.client.render.FluidColorHelper;
import com.m4thg33k.pipedreams2.util.LogHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;

public class ParticleFluidOrb extends Particle{

    private TextureAtlasSprite icon;
    private float SCALE_FACTOR = 0.125f;

    protected ParticleFluidOrb(World world, double x, double y, double z)
    {
        super(world, x, y, z);
        this.particleRed = this.particleGreen = this.particleBlue = 1.0f;
        this.particleTextureJitterX = this.particleTextureJitterY = 0.0f;
    }

    public ParticleFluidOrb(World world, double x, double y, double z, double velX, double velY, double velZ, String fluidName, int life)
    {
        this(world, x, y, z);

//        LogHelper.info("Creating fluid orb with: " + x + " " + y + " " + z);
//        LogHelper.info("\t"+velX + " " + velY + " " + velZ);

        this.motionX = velX;
        this.motionY = velY;
        this.motionZ = velZ;

        this.icon = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(FluidRegistry.getFluid(fluidName).getFlowing().toString());
        this.particleScale = SCALE_FACTOR;
        this.particleMaxAge = life;
        this.setParticleTexture(icon);

        Vec3d color = FluidColorHelper.getFluidRGB(fluidName);
        this.particleRed = (float)color.xCoord;
        this.particleGreen = (float)color.yCoord;
        this.particleBlue = (float)color.zCoord;
    }

    @Override
    public void onUpdate() {
//        LogHelper.info(this + "moving");
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        if (this.particleAge++ >= this.particleMaxAge)
        {
            this.setExpired();
        }

        this.move(this.motionX, this.motionY, this.motionZ);
    }

    @Override
    public void move(double x, double y, double z) {
        this.posX += x;
        this.posY += y;
        this.posZ += z;
    }

    @Override
    public int getFXLayer() {
        return 1;
    }
}
