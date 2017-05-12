package com.m4thg33k.pipedreams2.items;

import com.m4thg33k.pipedreams2.core.interfaces.IDismantleable;
import com.m4thg33k.pipedreams2.core.lib.Names;
import com.m4thg33k.pipedreams2.util.LogHelper;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class ItemWrench extends ItemBaseItem {

    public ItemWrench()
    {
        super(Names.ITEM_WRENCH);

        setMaxStackSize(1);
    }

    @Override
    @Nonnull
    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        if (world.isRemote)
        {
            return EnumActionResult.PASS;
        }

        Block block = world.getBlockState(pos).getBlock();

        if (block instanceof IDismantleable)
        {
            if (player.isSneaking())
            {
                if (((IDismantleable) block).isDismantleable(player, world, pos))
                {
                    ((IDismantleable) block).dismantle(player, world, pos);
                }
                LogHelper.info("Success");
                return EnumActionResult.SUCCESS;
            }
            LogHelper.info("Pass1");
            return EnumActionResult.PASS;
        }
        LogHelper.info("Pass2");
        return EnumActionResult.PASS;
    }

    @Override
    @Nonnull
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, @Nonnull EnumHand handIn) {
        if (worldIn.isRemote)
        {
            return ActionResult.newResult(EnumActionResult.PASS, playerIn.getHeldItem(handIn));
        }

        ItemStack stack = playerIn.getHeldItem(handIn);
        if (!stack.isEmpty() && stack.getItem() instanceof ItemWrench && playerIn.isSneaking())
        {

            stack.setItemDamage((stack.getItemDamage()+1) % 2);
            return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
        }

        return ActionResult.newResult(EnumActionResult.PASS, stack);
    }
}
