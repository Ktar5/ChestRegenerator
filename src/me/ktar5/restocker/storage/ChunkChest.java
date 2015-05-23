package me.ktar5.restocker.storage;

import java.util.ArrayList;

import org.bukkit.Location;

public class ChunkChest{

	public final String chunkString;
	public final ArrayList<Location> locationStrings;

	public ChunkChest(String chunkString, Location loc){
		this.chunkString = chunkString;
		locationStrings = new ArrayList<>();
		addLocation(loc);
	}

	public ChunkChest(ChunkChest chunkChest){
		chunkString = chunkChest.chunkString;
		locationStrings = (ArrayList<Location>) chunkChest.locationStrings.clone();
	}

	public void addLocation(Location loc){
		locationStrings.add(loc);
	}


}
