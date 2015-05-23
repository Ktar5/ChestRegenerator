package me.ktar5.restocker.util;

import java.util.List;

import me.ktar5.restocker.storage.ChunkChest;
import me.ktar5.restocker.storage.ConfigCache;

import org.bukkit.plugin.Plugin;

public class StaggeredRunnable implements Runnable
{
	private final Plugin myPlugin;
	private final List<ChunkChest> hugeList;

	private int taskId;

	private int iteratorCount = 0;
	private final int maxIterationsPerTick = ConfigCache.getRegensPerCycle();

	public StaggeredRunnable(Plugin myPlugin, List<ChunkChest> hugeList)
	{
		this.myPlugin = myPlugin;
		this.hugeList = hugeList;
	}

	public void start()
	{
		// reset whenever we call this method
		iteratorCount = 0;

		long delay_before_starting = 10;
		long delay_between_restarting = ConfigCache.getCycleTicks();

		// synchronous - thread safe
		taskId = myPlugin.getServer().getScheduler().runTaskTimer(myPlugin, this, delay_before_starting, delay_between_restarting).getTaskId();
	}

	// this example will stagger parsing a huge list

	@Override
	public void run()
	{
		iteratorCount = 0;

		// while the list isnt empty, and we havent exceeded matIteraternsPerTick....
		// the loop will stop when it reaches 300 iterations OR the list becomes empty
		// this ensures that the server will be happy clappy, not doing too much per tick.

		while (!hugeList.isEmpty() && iteratorCount < maxIterationsPerTick)
		{
			hugeList.get(0).locationStrings.stream().forEach(ChestUtils::createChest);
			hugeList.remove(0);
			iteratorCount++;
		}

		System.out.println("Done, moving on to next batch... " + hugeList.size() + " chunks left to go! :)");

		if (hugeList.isEmpty()){
			myPlugin.getServer().getScheduler().cancelTask(taskId);
		}

	}

}