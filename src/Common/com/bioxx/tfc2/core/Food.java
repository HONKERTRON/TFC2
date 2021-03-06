package com.bioxx.tfc2.core;

import java.util.Iterator;
import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.FoodStats;
import net.minecraft.util.text.TextFormatting;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.bioxx.tfc2.api.FoodRegistry;
import com.bioxx.tfc2.api.FoodRegistry.FoodGroupPair;
import com.bioxx.tfc2.api.FoodRegistry.TFCFood;
import com.bioxx.tfc2.api.interfaces.IFood;
import com.bioxx.tfc2.api.interfaces.IFoodStatsTFC;

public class Food 
{
	public static final int DRYHOURS = 4;
	public static final int SMOKEHOURS = 12;

	private static NBTTagCompound getProcTag(ItemStack is)
	{
		if(is.hasTagCompound() && is.getTagCompound().hasKey("Processing Tag"))
		{
			return (NBTTagCompound) is.getTagCompound().getTag("Processing Tag");
		}
		else
			return new NBTTagCompound();
	}

	private static void setProcTag(ItemStack is, NBTTagCompound nbt)
	{
		if(!is.hasTagCompound())
			is.setTagCompound(new NBTTagCompound());
		is.getTagCompound().setTag("Processing Tag", nbt);
	}

	private static NBTTagCompound getNBT(ItemStack is)
	{
		if (is != null && is.hasTagCompound())
		{
			return is.getTagCompound();
		}
		else if (is != null && !is.hasTagCompound())
		{
			is.setTagCompound(new NBTTagCompound());
			return is.getTagCompound();
		}
		else
		{
			return new NBTTagCompound();
		}
	}

	public static boolean areEqual(ItemStack is1, ItemStack is2)
	{
		if(isBrined(is1) != isBrined(is2))
			return false;
		if(isPickled(is1) != isPickled(is2))
			return false;
		if(isCooked(is1) != isCooked(is2))
			return false;
		if(isDried(is1) != isDried(is2))
			return false;
		if(isSalted(is1) != isSalted(is2))
			return false;
		return true;
	}

	public static boolean isBrined(ItemStack is)
	{
		NBTTagCompound nbt = getProcTag(is);
		return nbt.getBoolean("Brined");
	}

	public static void setBrined(ItemStack is, boolean value)
	{
		NBTTagCompound nbt = getProcTag(is);
		nbt.setBoolean("Brined", value);
		setProcTag(is, nbt);
	}

	public static boolean isPickled(ItemStack is)
	{
		NBTTagCompound nbt = getProcTag(is);
		return nbt.getBoolean("Pickled");
	}

	public static void setPickled(ItemStack is, boolean value)
	{
		NBTTagCompound nbt = getProcTag(is);
		nbt.setBoolean("Pickled", value);
		setProcTag(is, nbt);
	}

	public static boolean isSalted(ItemStack is)
	{
		NBTTagCompound nbt = getProcTag(is);
		return nbt.getBoolean("Salted");
	}

	public static void setSalted(ItemStack is, boolean value)
	{
		NBTTagCompound nbt = getProcTag(is);
		nbt.setBoolean("Salted", value);
		setProcTag(is, nbt);
	}

	public static boolean isCooked(ItemStack is)
	{
		NBTTagCompound nbt = getProcTag(is);
		return nbt.getFloat("Cooked") > 600;
	}

	public static float getCooked(ItemStack is)
	{
		NBTTagCompound nbt = getProcTag(is);
		return nbt.getFloat("Cooked");
	}

	public static void setCooked(ItemStack is, float value)
	{
		NBTTagCompound nbt = getProcTag(is);
		nbt.setFloat("Cooked", value);
		setProcTag(is, nbt);
	}

	public static void setDecayTimer(ItemStack is, long value)
	{
		NBTTagCompound nbt = getNBT(is);
		nbt.setLong("Expiration", value);
	}

	public static long getDecayTimer(ItemStack is)
	{
		NBTTagCompound nbt = getNBT(is);
		if (nbt.hasKey("Expiration"))
			return nbt.getLong("Expiration");
		else
			return Timekeeper.getInstance().getTotalTicks();
	}

	public static boolean hasDecayTimer(ItemStack is)
	{
		NBTTagCompound nbt = getNBT(is);
		return nbt.hasKey("Expiration");
	}

	public static boolean isDried(ItemStack is)
	{
		NBTTagCompound nbt = getProcTag(is);
		return nbt.getShort("Dried") >= DRYHOURS;
	}

	public static short getDried(ItemStack is)
	{
		NBTTagCompound nbt = getProcTag(is);
		return nbt.getShort("Dried");
	}

	public static void setDried(ItemStack is, int value)
	{
		NBTTagCompound nbt = getProcTag(is);
		nbt.setShort("Dried", (short)value);
		setProcTag(is, nbt);
	}

	public static short getSmokeCounter(ItemStack is)
	{
		NBTTagCompound nbt = getProcTag(is);
		return nbt.getShort("SmokeCounter");
	}

	public static void setSmokeCounter(ItemStack is, int value)
	{
		NBTTagCompound nbt = getProcTag(is);
		nbt.setShort("SmokeCounter", (short)value);
		setProcTag(is, nbt);
	}

	public static int getCookedColorMultiplier(ItemStack is)
	{
		float cookedLevel = Food.getCooked(is);
		int r = 255 - (int)(160 * (Math.max(cookedLevel-600, 0) / 600f)); 
		int b = 255 - (int)(160 * (Math.max(cookedLevel-600, 0) / 600f));
		int g = 255 - (int)(160 * (Math.max(cookedLevel-600, 0) / 600f));
		return (r << 16) + (b << 8) + g;
	}

	public static void setMealSkill(ItemStack is, int val)
	{
		if(!is.hasTagCompound())
			is.setTagCompound(new NBTTagCompound());
		is.getTagCompound().setInteger("mealSkill", val);
	}

	public static int getMealSkill(ItemStack is)
	{
		return is.getTagCompound().getInteger("mealSkill");
	}

	public static boolean hasMealSkill(ItemStack is)
	{
		return is.getTagCompound().hasKey("mealSkill");
	}

	public static long getExpirationTimer(ItemStack is)
	{
		if(FoodRegistry.getInstance().hasKey(is.getItem(), is.getItemDamage()))
		{
			return Food.getExpirationTimer(FoodRegistry.getInstance().getFood(is.getItem(), is.getItemDamage()), is);
		}
		return -1L;
	}

	public static long getExpirationTimer(TFCFood food, ItemStack is)
	{
		long timer = food.expiration;

		// Do decay timer altering stuff here

		return timer;
	}

	public static void addInformation(ItemStack is, EntityPlayer player, List arraylist, Item item)
	{
		if(FoodRegistry.getInstance().hasKey(is.getItem(), is.getItemDamage()))
		{
			TFCFood food = FoodRegistry.getInstance().getFood(is.getItem(), is.getItemDamage());
			IFood ifood = (IFood)item;
			arraylist.add(food.getDisplayString());
			long time = Food.getDecayTimer(is)-Timekeeper.getInstance().getTotalTicks();
			if(!Food.hasDecayTimer(is))
			{
				arraylist.add(TextFormatting.GREEN+"NO DECAY");
			}
			else if(time <= 0)
			{
				arraylist.add(TextFormatting.RED+"Expired x"+Math.min(1+(time / Food.getExpirationTimer(food, is))* (-1), is.stackSize));
			}
			else
			{
				String out = String.format("%d:%02d", time/60/20, (time/20) % 60);
				arraylist.add("Expires: " + out);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public static void getSubItems(Item itemIn, CreativeTabs tab, List subItems)
	{
		ItemStack is = new ItemStack(itemIn, 1, 0);
		Food.setDecayTimer(is, net.minecraft.client.Minecraft.getMinecraft().theWorld.getWorldTime()+ Food.getExpirationTimer(is));
		subItems.add(is);
	}

	@SideOnly(Side.CLIENT)
	public static void addDecayTimerForCreative(List<ItemStack> list)
	{
		for(ItemStack is : list)
		{
			Food.setDecayTimer(is, net.minecraft.client.Minecraft.getMinecraft().theWorld.getWorldTime()+Food.getExpirationTimer(is));
		}
	}

	public static void addNutrition(FoodStats fs, ItemStack is)
	{
		IFoodStatsTFC stats = (IFoodStatsTFC)fs;
		TFCFood food = FoodRegistry.getInstance().getFood(is.getItem(), is.getItemDamage());
		if(food != null && is.getItem() instanceof ItemFood)
		{
			ItemFood item = (ItemFood)is.getItem();
			Iterator iter = food.foodGroup.iterator();
			while(iter.hasNext())
			{
				FoodGroupPair pair = (FoodGroupPair) iter.next();
				float amount = pair.amount;
				amount = Math.min(stats.getNutritionMap().get(pair.foodGroup) + (item.getHealAmount(is) * (pair.amount / 100f)*0.25f), 20);
				stats.getNutritionMap().put(pair.foodGroup, amount);
			}
		}
	}

}
