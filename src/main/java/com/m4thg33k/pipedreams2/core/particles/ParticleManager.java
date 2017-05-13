package com.m4thg33k.pipedreams2.core.particles;

import com.m4thg33k.pipedreams2.PipeDreams2;
import com.m4thg33k.pipedreams2.core.network.packets.PacketTankFilling;
import com.m4thg33k.pipedreams2.tiles.TilePortTank;
import com.m4thg33k.pipedreams2.util.LogHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ParticleManager {

    public static Vec3d getSphereLocation(double radius, BlockPos blockPos, double theta, double phi)
    {
        return new Vec3d(blockPos).addVector(radius*Math.sin(theta)*Math.sin(phi),
                0.5 + radius*Math.cos(phi),
                radius*Math.cos(theta)*Math.sin(phi));
//        return new Vec3d(temp.getX(), temp.getY(), temp.getZ());
    }

    public static double angleToRad(int angle)
    {
        return angle * Math.PI / 180.0;
    }

    public static double getTheta(EnumFacing side)
    {
        if (side == null || side == EnumFacing.DOWN || side == EnumFacing.UP)
        {
            return angleToRad(PipeDreams2.RAND.nextInt(360));
        }
        int theta = PipeDreams2.RAND.nextInt(181);
        switch (side)
        {
            case EAST:
                return angleToRad(theta);
            case WEST:
                return angleToRad(theta + 180);
            case NORTH:
                return angleToRad(theta + 90);
            default:
                return angleToRad(theta + 270);
        }
    }

    public static double getPhi(EnumFacing side)
    {
        if (side == null)
        {
            return angleToRad(PipeDreams2.RAND.nextInt(181));
        }
        switch (side)
        {
            case UP:
                return angleToRad(PipeDreams2.RAND.nextInt(91));
            case DOWN:
                return angleToRad(PipeDreams2.RAND.nextInt(91)+90);
            default:
                return angleToRad(PipeDreams2.RAND.nextInt(181));
        }
    }

    public static Vec3d getRandomSphereLocation(double radius, EnumFacing side, BlockPos pos)
    {
        return getSphereLocation(radius, pos, getTheta(side), getPhi(side));
    }

    public static void doDrainParticles(Vec3d end, Vec3d delta, String fluidName)
    {
        Minecraft.getMinecraft().effectRenderer.addEffect(new ParticleFluidOrb(
                Minecraft.getMinecraft().world,
                end.xCoord + 0.5,
                end.yCoord,
                end.zCoord + 0.5,
                -delta.xCoord,
                -delta.yCoord,
                -delta.zCoord,
                fluidName,
                10));
    }

    public static void doFillParticles(Vec3d start, Vec3d delta, String fluidName)
    {
        Minecraft.getMinecraft().effectRenderer.addEffect(new ParticleFluidOrb(
                Minecraft.getMinecraft().world,
                start.xCoord,
                start.yCoord,
                start.zCoord,
                delta.xCoord,
                delta.yCoord,
                delta.zCoord,
                fluidName,
                10
        ));
    }

    public static void spawnFillingParticles(double radius, EnumFacing side, BlockPos blockPos, Vec3d start, boolean isFilling, String name, Vec3d extra)
    {
        Vec3d end = getRandomSphereLocation(radius, side, blockPos);
        Vec3d delta = (end.subtract(new Vec3d(blockPos).add(extra))).scale(0.1);

//        LogHelper.info("spawnFillingParticles", end, delta, "end SFP");

        if (isFilling)
        {
            doFillParticles(start, delta, name);
        }
        else
        {
            doDrainParticles(end, delta, name);
        }
    }

    public static void tankFillingParticles(PacketTankFilling packet)
    {
        World world = Minecraft.getMinecraft().world;
        BlockPos pos = packet.getPos();
        TileEntity tile = world.getTileEntity(pos);

        if (tile == null || !(tile instanceof TilePortTank))
        {
            return;
        }

        boolean isFilling = packet.isFilling();
        EnumFacing side = packet.getSide();
        String name = packet.getFluidName();
        double radius = (((TilePortTank) tile).getRadius() + 0.1) * 0.5;

        if (side != null) {
            Vec3d toAddToPos = new Vec3d(0.5, 0.5, 0.5).add(new Vec3d(side.getDirectionVec()).scale(0.5));
            Vec3d startPos = new Vec3d(pos).add(toAddToPos);

            Vec3d extra = toAddToPos.subtract(0.5, 0.0, 0.5);

//            LogHelper.info(pos, startPos, extra);

            for (int n=0; n <= packet.getAmount(); n += 10000)
            {
                spawnFillingParticles(radius, side, pos, startPos, isFilling, name, extra);
            }
        }
        else
        {
            double theta, phi;
            Vec3d startPos, end, delta;

            for (int n=0; n <= packet.getAmount(); n += 100)
            {
                theta = getTheta(null);
                phi = getPhi(null);
                startPos = getSphereLocation(0.5, pos, theta, phi);
                end = getSphereLocation(radius, pos, theta, phi);

                if (!isFilling)
                {
                    Vec3d temp = startPos;
                    startPos = end;
                    end = temp;
                }

                delta = (end.subtract(startPos)).scale(0.1);

                Minecraft.getMinecraft().effectRenderer.addEffect(
                        new ParticleFluidOrb(world,
                                startPos.xCoord + 0.5,
                                startPos.yCoord,
                                startPos.zCoord + 0.5,
                                delta.xCoord,
                                delta.yCoord,
                                delta.zCoord,
                                name,
                                10)
                );
            }
        }
    }

}
