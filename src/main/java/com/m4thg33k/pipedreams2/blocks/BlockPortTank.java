package com.m4thg33k.pipedreams2.blocks;

import com.m4thg33k.pipedreams2.blocks.templates.BaseBlockTESRDismantle;
import com.m4thg33k.pipedreams2.core.interfaces.IDismantleableTile;
import com.m4thg33k.pipedreams2.core.interfaces.IToggleSides;
import com.m4thg33k.pipedreams2.core.lib.Names;
import com.m4thg33k.pipedreams2.tiles.TilePortTank;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidUtil;

import javax.annotation.Nullable;

public class BlockPortTank extends BaseBlockTESRDismantle implements IToggleSides {

    public BlockPortTank()
    {
        super(Names.TILE_PORTABLE_TANK);
        this.setDefaultState(blockState.getBaseState());
    }

    @Override
    public void handleRegName() {
        this.setRegistryName(Names.MODID, Names.TILE_PORTABLE_TANK);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TilePortTank();
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (worldIn.isRemote)
        {
            return true;
        }

        TileEntity tile = worldIn.getTileEntity(pos);
        ItemStack heldItem = playerIn.getHeldItem(hand);
        if (tile != null && tile instanceof TilePortTank)
        {
            if (!heldItem.isEmpty())
            {
                //add case for wrench logic
                boolean movedFluid = FluidUtil.interactWithFluidHandler(playerIn, hand, worldIn, pos, null);
                return movedFluid;
            }
            else if (!playerIn.isSneaking())
            {
                ((TilePortTank) tile).incrementConnectionType(facing);
                return true;
            }
            else
            {
                ((TilePortTank) tile).incrementConnectionType(facing.getOpposite());
                return true;
            }
        }
        return false;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        TileEntity tile = source.getTileEntity(pos);
        double factor = tile == null || !(tile instanceof TilePortTank) ? 0.1 : 0.05+0.25*((TilePortTank)tile).getPercentage();
        double small = Math.min(0.5 - factor, 0.4);
        double big = Math.max(0.5+factor, 0.6);
        AxisAlignedBB toReturn = new AxisAlignedBB(small, small, small, big, big, big);
        return toReturn;
    }

    @Override
    public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player) {
//        LogHelper.info("Harvest");
//        LogHelper.info(worldIn.getBlockState(pos).getBlock());
        super.onBlockHarvested(worldIn, pos, state, player);
    }

    @Override
    public void onBlockDestroyedByPlayer(World worldIn, BlockPos pos, IBlockState state) {
//        LogHelper.info("Destroy");
//        LogHelper.info(worldIn.getBlockState(pos).getBlock());
        super.onBlockDestroyedByPlayer(worldIn, pos, state);
    }

    @Override
    public void dismantle(EntityPlayer player, World world, BlockPos pos) {
        if (world.isRemote)
        {
            return;
        }

        TileEntity tile = world.getTileEntity(pos);
        ItemStack stack = new ItemStack(world.getBlockState(pos).getBlock(), 1);
        boolean shouldSpawn = true;
        if (tile != null && tile instanceof IDismantleableTile) {
            NBTTagCompound tagCompound = ((IDismantleableTile) tile).getItemNBT(player.getHorizontalFacing());
            if (tagCompound == null && player.isCreative()) {
                shouldSpawn = false;
            }
            stack.setTagCompound(tagCompound);
        }
        if (shouldSpawn)
        {
            InventoryHelper.spawnItemStack(world, player.posX, player.posY, player.posZ, stack);
        }

        world.removeTileEntity(pos);
        world.setBlockToAir(pos);
    }

    @Override
    public boolean toggleSide(World world, BlockPos pos, EnumFacing side) {
        TileEntity tile = world.getTileEntity(pos);

        if (tile != null && tile instanceof TilePortTank)
        {
            ((TilePortTank) tile).toggleFluidConnection(side);
            return true;
        }

        return false;
    }

    @Override
    public boolean toggleOppositeSide(World world, BlockPos pos, EnumFacing side) {
        return toggleSide(world, pos, side.getOpposite());
    }


}
