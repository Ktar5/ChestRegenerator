package me.ktar5.restocker.listeners;

import me.ktar5.restocker.util.ChestUtils;

import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

/**
 * Created by Carter on 4/7/2015.
 */
public class ChestUse implements Listener {
	@EventHandler
	public void onInv(InventoryCloseEvent event){
		if(event.getInventory().getHolder() instanceof Chest){
			Chest c = (Chest)event.getInventory().getHolder();
			if(hasItems(c)){
				return;
			}
			if(c.hasMetadata("ChestTech")){
				ChestUtils.removeChest(c);
			}
		}else if (event.getInventory().getHolder() instanceof DoubleChest){
			DoubleChest c = (DoubleChest) event.getInventory().getHolder();
			if(hasItems(c)){
				return;
			}
			Chest left = (Chest) c.getLeftSide();
			Chest right = (Chest) c.getRightSide();
			if(right != null && left != null){
				if(right.hasMetadata("ChestTech") && left.hasMetadata("ChestTech")){
					ChestUtils.removeChest(right);
					ChestUtils.removeChest(left);
				}
			}
		}
	}

	public boolean hasItems(Chest c){
		if(c.getInventory().getContents().length != 0 && c.getInventory().getContents() != null){
			return true;
		}else{
			return false;
		}
	}

	public boolean hasItems(DoubleChest c){
		if(c.getInventory().getContents().length != 0 && c.getInventory().getContents() != null){
			return true;
		}else{
			return false;
		}
	}

}
