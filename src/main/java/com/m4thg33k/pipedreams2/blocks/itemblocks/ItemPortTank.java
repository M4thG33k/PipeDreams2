package com.m4thg33k.pipedreams2.blocks.itemblocks;

import com.m4thg33k.pipedreams2.core.lib.ModConfigs;
import com.m4thg33k.pipedreams2.core.lib.Names;
import com.m4thg33k.pipedreams2.tiles.TilePortTank;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

public class ItemPortTank extends ItemBlock{

    protected int capacity = TilePortTank.CAPACITY;

    public ItemPortTank(Block block)
    {
        super(block);

        this.setUnlocalizedName(Names.TILE_PORTABLE_TANK);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        return new FluidHandlerItemStack(stack, capacity);
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState) {
        boolean supers = super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState);

        if (!supers)
        {
            return false;
        }

        TileEntity tile = world.getTileEntity(pos);

        if (tile != null && tile instanceof TilePortTank && stack.hasTagCompound())
        {
            NBTTagCompound compound = stack.getTagCompound();
            compound.setInteger("x", pos.getX());
            compound.setInteger("y", pos.getY());
            compound.setInteger("z", pos.getZ());
            ((TilePortTank) tile).readItemNBT(player.getHorizontalFacing(), stack.getTagCompound());
        }

        return true;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, playerIn, tooltip, advanced);

        if (stack.hasTagCompound())
        {
            NBTTagCompound compound = stack.getTagCompound();
            if (compound.hasKey("Empty"))
            {
                return;
            }

            String fluid = compound.getString("FluidName");
            int amount = compound.getInteger("Amount");

            tooltip.add(fluid);
            tooltip.add(amount + "mb / " + capacity + "mb");
        }
    }
}
