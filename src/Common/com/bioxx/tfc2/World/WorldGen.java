package com.bioxx.tfc2.World;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import com.bioxx.jMapGen.IslandParameters;
import com.bioxx.jMapGen.IslandParameters.Feature;
import com.bioxx.jMapGen.Map;
import com.bioxx.tfc2.api.Util.Helper;


public class WorldGen
{
	public static WorldGen instance;
	HashMap<Integer, CachedIsland> islandCache;
	World world;
	public static final int ISLAND_SIZE = 4096;

	public WorldGen(World w) 
	{
		world = w;
		islandCache = new HashMap<Integer, CachedIsland>();
	}

	public static void initialize(World world)
	{
		if(instance == null)
			instance = new WorldGen(world);
	}

	/**
	 * Coordinates should already be in MapCoords
	 */
	public Map getIslandMap(int x, int z)
	{
		int id = Helper.cantorize(x, z);
		if(islandCache.containsKey(id))
		{
			return islandCache.get(id).getIslandMap();
		}
		else
		{
			Map m = loadMap(x, z);
			if(m != null)
			{
				islandCache.put(id, new CachedIsland(m));
				return m;
			}
		}

		return createIsland(x, z);
	}

	private Map createIsland(int x, int z)
	{
		long seed = world.getSeed()+Helper.cantorize(x, z);
		IslandParameters id = createParams(seed, x, z);
		Map mapgen = new Map(4096, seed);
		mapgen.newIsland(id);
		mapgen.go();
		islandCache.put(Helper.cantorize(x, z), new CachedIsland(mapgen));
		return mapgen;
	}

	private IslandParameters createParams(long seed, int x, int z)
	{
		IslandParameters id = new IslandParameters(seed, ISLAND_SIZE, 0.5, 0.3);
		Random r = new Random(seed);
		id.setCoords(x, z);
		int fcount = 1+r.nextInt(3);
		//Choose Features
		for(int i = 0; i < fcount; i++)
		{
			Feature f = Feature.getRandomFeature();
			if(f == Feature.Canyons)
				id.setFeatures(Feature.Gorges);

			if(id.hasFeature(f)){i--; continue;}
			else id.setFeatures(f);
		}
		return id;
	}

	public void resetCache()
	{
		for(CachedIsland c : islandCache.values())
		{
			saveMap(c);
		}
		islandCache = new HashMap<Integer, CachedIsland>();
	}

	public void trimCache()
	{
		long now = System.currentTimeMillis();
		Set<Integer> keys = islandCache.keySet();

		for(Iterator<Integer> iter = keys.iterator(); iter.hasNext();)
		{
			int key = iter.next();
			CachedIsland c = islandCache.get(key);
			if(now-c.lastAccess > 12000)//12 seconds of no access will trim the map
			{
				saveMap(c);
				islandCache.remove(key);
			}

		}
	}

	public void saveMap(CachedIsland island)
	{
		try
		{
			File file1 = world.getSaveHandler().getMapFileFromName(island.islandData.islandParams.getXCoord() + "," + 
					island.islandData.islandParams.getZCoord());

			if (file1 != null && !file1.exists())
			{
				NBTTagCompound dataNBT = new NBTTagCompound();
				island.islandData.writeToNBT(dataNBT);

				NBTTagCompound finalNBT = new NBTTagCompound();
				finalNBT.setTag("data", dataNBT);
				island.islandData.islandParams.writeToNBT(finalNBT);

				FileOutputStream fileoutputstream = new FileOutputStream(file1);
				CompressedStreamTools.writeCompressed(finalNBT, fileoutputstream);
				fileoutputstream.close();
			}
		}
		catch (Exception exception)
		{
			exception.printStackTrace();
		}
	}

	public Map loadMap(int x, int z)
	{
		try
		{
			File file1 = world.getSaveHandler().getMapFileFromName(x + "," + z);

			if (file1 != null && file1.exists())
			{
				FileInputStream input = new FileInputStream(file1);
				NBTTagCompound nbt = CompressedStreamTools.readCompressed(input);
				input.close();
				IslandParameters ip = new IslandParameters();
				ip.readFromNBT(nbt);
				long seed = world.getSeed()+Helper.cantorize(x, z);
				Map m = new Map(ISLAND_SIZE, seed);
				m.newIsland(ip);
				m.readFromNBT(nbt.getCompoundTag("data"));
				return m;
			}

		}
		catch (Exception exception)
		{
			exception.printStackTrace();
		}

		return null;
	}


}
