package me.ktar5.restocker;

import java.sql.Connection;
import java.sql.SQLException;

import me.ktar5.restocker.cmd.CommandHandler;
import me.ktar5.restocker.listeners.ChestUse;
import me.ktar5.restocker.storage.ChestCache;
import me.ktar5.restocker.storage.ConfigCache;
import me.ktar5.restocker.storage.ItemCache;
import me.ktar5.restocker.storage.sqlite.SQLite;
import me.ktar5.restocker.storage.yml.CustomConfig;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;

public class Restocker extends JavaPlugin{

	private static JavaPlugin instance = null;

	public static CustomConfig config;

	public static SQLite sql = null;
	Connection c = null;

	@Override
	public void onLoad(){
		instance = this;
	}

	@Override
	public void onEnable(){
		saveDefaultConfig();
		config = new CustomConfig(getDataFolder(), "config.yml");
		try {
			sql = new SQLite(this, "chests.db");
			c = sql.openConnection();
			sql.updateSQL("CREATE TABLE IF NOT EXISTS ChestStorage ("
					+ "chunk varchar(3) NOT NULL, block varchar(5) NOT NULL UNIQUE)");
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		new ConfigCache();
		new ItemCache();

		getServer().getPluginManager().registerEvents(new ChestUse(), this);
		CommandExecutor ce = new CommandHandler();
		getCommand("restocker").setExecutor(ce);

		Bukkit.getScheduler().runTaskLater(this, new Runnable(){
			@Override
			public void run(){
				new ChestCache();
			}
		}, 10L);

		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			@Override
			public void run(){
				ChestCache.regenAllChests();
			}
		}, 20*40L, ConfigCache.getTime()*20L);

	}

	@Override
	public void onDisable(){
		instance = null;
		ChestCache.writeToDatabase();
		getServer().getScheduler().cancelTasks(this);
	}

	public static JavaPlugin getInstance() {
		return instance;
	}

}
