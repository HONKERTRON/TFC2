package com.bioxx.tfc2.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.bioxx.tfc2.core.PlayerInventory;
import com.bioxx.tfc2.tileentities.TileAnvil;

public class ContainerAnvil extends ContainerTFC
{
	private World world;
	private TileAnvil anvil;
	private EntityPlayer player;
	int recipeIndex = -1;

	public ContainerAnvil(InventoryPlayer playerinv, TileAnvil anvil, World world, int x, int y, int z)
	{
		this.player = playerinv.player;

		this.world = world;
		this.anvil = anvil;
		anvil.openInventory(player);
		PlayerInventory.buildInventoryLayout(this, playerinv, 8, 91, false, true);
		layoutContainer(playerinv, anvil, 0, 0);
	}

	/**
	 * Callback for when the crafting gui is closed.
	 */
	@Override
	public void onContainerClosed(EntityPlayer par1EntityPlayer)
	{
		super.onContainerClosed(par1EntityPlayer);
		if(!world.isRemote)
			anvil.closeInventory(par1EntityPlayer);
	}

	@Override
	public void onCraftGuiOpened(ICrafting listener)
	{
		super.onCraftGuiOpened(listener);
		listener.sendAllWindowProperties(this, this.anvil);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int id, int data)
	{
		this.anvil.setField(id, data);
	}

	@Override
	public void detectAndSendChanges()
	{
		super.detectAndSendChanges();
		for (int i = 0; i < this.listeners.size(); ++i)
		{
			ICrafting icrafting = (ICrafting)this.listeners.get(i);

			if(this.recipeIndex != anvil.getField(0))
			{
				icrafting.sendProgressBarUpdate(this, 0, anvil.getField(0));
			}
		}

		this.recipeIndex = anvil.getField(0);
	}



	/**
	 * Called to transfer a stack from one inventory to the other eg. when shift clicking.
	 */
	@Override
	public ItemStack transferStackInSlotTFC(EntityPlayer player, int slotNum)
	{
		ItemStack origStack = null;
		Slot slot = (Slot)this.inventorySlots.get(slotNum);

		if (slot != null && slot.getHasStack())
		{
			ItemStack slotStack = slot.getStack();
			origStack = slotStack.copy();

			// From pile to inventory
			if (slotNum >= 9 && slotNum < 27)
			{
				if (!this.mergeItemStack(slotStack, 27, inventorySlots.size(), true))
					return null;
			}
			else if (slotNum < 9)
			{
				if (!this.mergeItemStack(slotStack, 9, 27, true))
					return null;
			}
			else
			{
				if (!this.mergeItemStack(slotStack, 9, 27, false))
					return null;
			}

			if (slotStack.stackSize <= 0)
				slot.putStack(null);
			else
				slot.onSlotChanged();

			if (slotStack.stackSize == origStack.stackSize)
				return null;

			slot.onPickupFromSlot(player, slotStack);
		}

		return origStack;
	}

	@Override
	public boolean canInteractWith(EntityPlayer var1)
	{
		return true;
	}

	protected void layoutContainer(IInventory playerInventory, IInventory chestInventory, int xSize, int ySize)
	{
		this.addSlotToContainer(new Slot(chestInventory, 0, 8, 14));
		this.addSlotToContainer(new Slot(chestInventory, 1, 26, 14));
	}

	public EntityPlayer getPlayer()
	{
		return player;
	}
}