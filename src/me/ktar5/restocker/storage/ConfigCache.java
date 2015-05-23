package me.ktar5.restocker.storage;

import me.ktar5.restocker.Restocker;

/**
 * Created by Carter on 4/7/2015.
 */
public class ConfigCache {

	private static int maxItems, minItems, regenTime, regensPerCycle, cycleTicks;

	public ConfigCache(){
		maxItems = minItems = regenTime = 0;
		maxItems = Restocker.config.getConfig().getInt("maxItems");
		minItems = Restocker.config.getConfig().getInt("minItems");
		regenTime = Restocker.config.getConfig().getInt("regenTime");
		regensPerCycle = Restocker.config.getConfig().getInt("regensPerCycle");
		cycleTicks = Restocker.config.getConfig().getInt("cycleTicks");
	}

	public static int getMaxItems(){
		return maxItems;
	}

	public static int getMinItems(){
		return minItems;
	}

	public static int getTime(){
		return regenTime;
	}

	public static int getRegensPerCycle(){
		return regensPerCycle;
	}

	public static int getCycleTicks(){
		return cycleTicks;
	}
}
