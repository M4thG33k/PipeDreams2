package com.m4thg33k.pipedreams2.core.inventory;

import com.m4thg33k.pipedreams2.core.inventory.containers.ContainerTilePort;
import com.m4thg33k.pipedreams2.core.inventory.guicontainers.GuiTilePort;
import com.m4thg33k.pipedreams2.tiles.TilePort;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

import javax.annotation.Nullable;

public class ModGuiHandler implements IGuiHandler {

    public static final int PORT_GUI = 0;

    @Nullable
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
        switch (ID)
        {
            case PORT_GUI:
                if (tile == null || !(tile instanceof TilePort))
                {
                    return null;
                }
                return new ContainerTilePort(player.inventory, (TilePort) tile);
            default:
                return null;
        }
    }

    @Nullable
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
        switch (ID)
        {
            case PORT_GUI:
                if (tile == null || !(tile instanceof TilePort))
                {
                    return null;
                }
                return new GuiTilePort(player.inventory, (TilePort) tile);
            default:
                return null;
        }
    }
}
