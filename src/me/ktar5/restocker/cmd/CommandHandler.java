package me.ktar5.restocker.cmd;

import java.util.Set;

import me.ktar5.restocker.storage.ChestCache;
import me.ktar5.restocker.util.ChestUtils;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandHandler implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
		if(sender instanceof Player) {
			Player player = (Player) sender;
			if(cmd.getName().equalsIgnoreCase("restocker")){
				if(args.length == 1) {
					switch (args[0].toLowerCase()){
					case "regen":
						if(player.hasPermission("restocker.regen")){
							Block block = player.getTargetBlock((Set<Material>) null, 10);
							if(block != null){
								if(block.getType().equals(Material.CHEST)){
									ChestUtils.createChest(block.getLocation());
									player.sendMessage("Regenerating the selected chest");
								}
							}else{
								player.sendMessage("Please be looking at a chest, or get closer to the chest you are looking at");
							}
						}
						break;
					case "regenall":
						if(player.hasPermission("restocker.regenall")) {
							ChestCache.regenAllChests();
						}
						break;
					case "scan":
						if(player.hasPermission("restocker.scan")) {
							ChestCache.scanWorld();
						}
						break;
					case "write":
						if(player.hasPermission("restocker.write")) {
							ChestCache.writeToDatabase();
						}
						break;
					case "help":
						if(player.hasPermission("restocker.help")) {
							player.sendMessage(new String[]{
									"/ct regen - Regenerates the chest you are looking at",
									"/ct regenall - Regenerates all the chests in the world",
									"/ct scan - Re-scans the whole world for chests",
									"/ct write - Write everything to the database",
							});
						}
						break;
					default:
						player.sendMessage(args[0].toLowerCase() + " is not a known command");
						break;
					}
				}
			}
		}
		return false;
	}
}
