package com.m4thg33k.pipedreams2.items;

import com.m4thg33k.pipedreams2.core.interfaces.IDismantleable;
import com.m4thg33k.pipedreams2.core.interfaces.IToggleSides;
import com.m4thg33k.pipedreams2.core.lib.Names;
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
import java.util.List;

public class ItemWrench extends ItemBaseMetaItem {

    public ItemWrench()
    {
        super(Names.ITEM_WRENCH);

        setMaxStackSize(1);
    }

    @Override
    @Nonnull
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (world.isRemote)
        {
            return EnumActionResult.SUCCESS;
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
                return EnumActionResult.SUCCESS;
            }
        }
        if (block instanceof IToggleSides)
        {
            ItemStack wrench = player.getHeldItem(hand);
            if (wrench.getItem() == ModItems.itemWrench)
            {
                switch (wrench.getItemDamage())
                {
                    case 1:
                        // Inverted
                        ((IToggleSides) block).toggleOppositeSide(world, pos, facing);
                        break;
                    default:
                        ((IToggleSides) block).toggleSide(world, pos, facing);
                }
                return EnumActionResult.SUCCESS;
            }
        }
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

    @Override
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        switch (stack.getItemDamage())
        {
            case 1:
                tooltip.add("Affects the opposite side of the block");
                break;
            default:
        }
    }
}
