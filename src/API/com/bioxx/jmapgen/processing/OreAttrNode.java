package com.bioxx.jmapgen.processing;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

import com.bioxx.jmapgen.graph.Center;

public class OreAttrNode 
{
	//This is the offset from the center of the hex
	BlockPos offset;
	//This is the offset from the midpoint of the edge between this hex and the next
	BlockPos nextOffset;
	Center next;
	//This is the offset from the midpoint of the edge between this hex and the previous
	BlockPos prevOffset;
	Center prev;

	String oreType = "";

	int nodeWidth = 2;
	int nodeHeight = 2;

	public OreAttrNode(String ore) 
	{
		offset = new BlockPos(0,0,0);
		nextOffset = new BlockPos(0,0,0);
		prevOffset = new BlockPos(0,0,0);
		oreType = ore;
	}

	public BlockPos getOffset() 
	{
		return offset;
	}

	public void setOffset(BlockPos offset) 
	{
		this.offset = offset;
	}

	public BlockPos getNextOffset() 
	{
		return nextOffset;
	}

	public void setNextOffset(BlockPos nextOffset) 
	{
		this.nextOffset = nextOffset;
	}

	public Center getNext() 
	{
		return next;
	}

	public OreAttrNode setNext(Center next) 
	{
		this.next = next;
		return this;
	}

	public BlockPos getPrevOffset() 
	{
		return prevOffset;
	}

	public void setPrevOffset(BlockPos prevOffset) 
	{
		this.prevOffset = prevOffset;
	}

	public Center getPrev() 
	{
		return prev;
	}

	public OreAttrNode setPrev(Center prev) 
	{
		this.prev = prev;
		return this;
	}

	public int getNodeWidth() {
		return nodeWidth;
	}

	public void setNodeWidth(int nodeWidth) {
		this.nodeWidth = Math.max(nodeWidth, 1);
	}

	public int getNodeHeight() {
		return nodeHeight;
	}

	public void setNodeHeight(int nodeHeight) {
		this.nodeHeight = Math.max(nodeHeight, 1);
	}

	public String getOreType() {
		return oreType;
	}

	public void setOreType(String oreType) {
		this.oreType = oreType;
	}

	public void writeToNBT(NBTTagCompound nbt) 
	{
		nbt.setString("oreType", oreType);
		nbt.setLong("offset", this.offset.toLong());
		nbt.setLong("nextOffset", this.nextOffset.toLong());
		nbt.setLong("prevOffset", this.prevOffset.toLong());
		if(next != null)
			nbt.setInteger("next", next.index);
		if(prev != null)
			nbt.setInteger("prev", prev.index);
		nbt.setInteger("nodeWidth", nodeWidth);
		nbt.setInteger("nodeHeight", nodeHeight);
	}

	public void readFromNBT(NBTTagCompound nbt, com.bioxx.jmapgen.IslandMap m) 
	{
		this.oreType = nbt.getString("oreType");
		this.offset = BlockPos.fromLong(nbt.getLong("offset"));
		this.nextOffset = BlockPos.fromLong(nbt.getLong("nextOffset"));
		this.prevOffset = BlockPos.fromLong(nbt.getLong("prevOffset"));
		if(nbt.hasKey("next"))
			next = m.centers.get(nbt.getInteger("next"));
		if(nbt.hasKey("prev"))
			prev = m.centers.get(nbt.getInteger("prev"));
		nodeWidth = nbt.getInteger("nodeWidth");
		nodeHeight = nbt.getInteger("nodeHeight");
	}
}