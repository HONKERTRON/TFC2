package com.bioxx.tfc2.handlers.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.bioxx.tfc2.core.PlayerInfo;
import com.bioxx.tfc2.core.PlayerManagerTFC;
import com.bioxx.tfc2.entity.EntityCart;
import com.bioxx.tfc2.gui.*;
import com.bioxx.tfc2.tileentities.TileAnvil;

public class GuiHandler extends com.bioxx.tfc2.handlers.GuiHandler
{
	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) 
	{
		BlockPos pos = new BlockPos(x, y, z);
		PlayerInfo pi = PlayerManagerTFC.getInstance().getPlayerInfoFromPlayer(player);
		TileEntity te;
		try
		{
			te = world.getTileEntity(pos);
		}
		catch(Exception e)
		{
			te = null;
		}

		switch(id)
		{
		case 0:
			return new GuiKnapping(player.inventory, pi.specialCraftingTypeAlternate == null ? pi.specialCraftingType : null, world, x, y, z);
		case 1:
			return new GuiCart(player.inventory, ((EntityCart)pi.entityForInventory).cartInv, world, x, y, z);
		case 2:
			return new GuiAnvil(player.inventory, (TileAnvil)te, world, x, y, z);
		case 3:
			return new GuiSkills(player);
		case 4:
			return new GuiHealth(player);
		default:
			return null;
		}
	}

	@SubscribeEvent
	public void openGuiHandler(GuiOpenEvent event)
	{
		if(event.getGui() instanceof GuiInventory && !(event.getGui() instanceof GuiInventoryTFC))
			event.setGui(new GuiInventoryTFC(Minecraft.getMinecraft().thePlayer));
	}
}
