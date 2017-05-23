package com.m4thg33k.pipedreams2.core.inventory.containers;

import com.m4thg33k.pipedreams2.tiles.TilePort;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerTilePort extends Container {

    private TilePort tilePort;

    private static int P_INV_X = 8;
    private static int P_INV_Y = 84;
    private static int P_INV_DX = 18;
    private static int P_INV_DY = 18;

    private boolean needToSendTest = false;

    public ContainerTilePort(IInventory playerInventory, TilePort te)
    {
        this.tilePort = te;

        addAllSlotsToContainer(playerInventory);
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return tilePort.isUsableByPlayer(playerIn);
    }

    /*
    SLOTS:
    Player Hotbar (0-8) .......... 0 - 8
    Player Inventory (9-35) ...... 9 - 35
     */
    private void addAllSlotsToContainer(IInventory inventory)
    {
        // put player hotbar in 0-8
        for (int x=0; x < 9; x++)
        {
            this.addSlotToContainer(new Slot(inventory, x, P_INV_X + x*P_INV_DX, P_INV_Y + 3*P_INV_DY + 4));
        }

        // put player main inventory in 9-35
        for (int y = 0; y<3; y++)
        {
            for (int x=0; x<9; x++)
            {
                this.addSlotToContainer(new Slot(inventory, x + y*9 + 9, P_INV_X + x*P_INV_DX, P_INV_Y + y*P_INV_DY));
            }
        }
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack previous = null;
        Slot slot = (Slot) this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack())
        {
            ItemStack current = slot.getStack();
            previous = current.copy();

            // insert custom moving code here

            if (current.isEmpty())
            {
                slot.putStack(ItemStack.EMPTY);
            }
            else
            {
                slot.onSlotChanged();
            }

            if (current.getCount() == previous.getCount())
            {
                return ItemStack.EMPTY;
            }
            slot.onTake(playerIn, current);
        }

        return previous;
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();

        if (needToSendTest)
        {
            tilePort.testFunction();
            needToSendTest = false;
        }
    }

    public void testFunction()
    {
        needToSendTest = true;
    }
}
