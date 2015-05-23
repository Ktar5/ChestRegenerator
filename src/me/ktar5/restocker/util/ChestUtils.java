package me.ktar5.restocker.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import me.ktar5.restocker.Restocker;
import me.ktar5.restocker.storage.ConfigCache;
import me.ktar5.restocker.storage.ItemCache;
import me.ktar5.restocker.storage.ItemCache.ItemWrapper;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * Created by Carter on 4/7/2015.
 */
public class ChestUtils {

	static Random random = new Random();

	public static ItemStack[] getRandomItems(){
		random.setSeed(System.nanoTime());
		int amountOfItems = randomBetween(ConfigCache.getMaxItems(), ConfigCache.getMinItems());

		List<ItemStack> chestItems = new ArrayList<>();

		for(int i = 0 ; i < amountOfItems ; i++){
			chestItems.add(getRandomItem().item);
		}
		return chestItems.toArray(new ItemStack[chestItems.size()]);
	}

	public static ItemWrapper getRandomItem(){
		int sum = 0;
		int counter = 0;
		List<ItemWrapper> list = ItemCache.getItems();
		Collections.shuffle(list, new Random(System.nanoTime()));
		while(sum < random.nextInt(ItemCache.getTotalSum())){
			sum = sum + list.get(counter++).chance;
		}
		return list.get(Math.max(0, counter - 1));
	}

	public static void createChest(Location location){
		Block block = location.getBlock();

		if(block.getType().equals(Material.AIR)){
			block.setType(Material.CHEST);
		}else if(!block.getType().equals(Material.CHEST)){
			return;
		}

		Chest matChest = (Chest) block.getState();

		matChest.getInventory().setContents(getRandomItems());
		try {
			matChest.update();
		} catch(Exception e) {
			e.printStackTrace();
		}
		block.setMetadata("ChestTech", new FixedMetadataValue(Restocker.getInstance(), true));
	}

	public static void removeChest(Chest chest){
		Bukkit.getScheduler().runTaskLater(Restocker.getInstance(), () -> {
			chest.removeMetadata("ChestTech", Restocker.getInstance());
			chest.getBlockInventory().clear();
			chest.update();
			chest.getBlock().setType(Material.AIR);
		}, 15L);
	}

	public static int randomBetween(int high, int low){
		random.setSeed(System.nanoTime());
		return random.nextInt(++high-low) + low;
	}
}
