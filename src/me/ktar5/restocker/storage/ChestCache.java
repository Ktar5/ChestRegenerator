package me.ktar5.restocker.storage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import me.ktar5.restocker.Restocker;
import me.ktar5.restocker.util.StaggeredRunnable;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

public class ChestCache {

	private static Location min, max;
	public static ArrayList<ChunkChest> chests = new ArrayList<>();
	private static World world;


	public ChestCache(){
		min = max = null;
		chests.clear();
		loadChests();
	}

	private static void setMinMaxLocations(){
		world = Bukkit.getWorld(Restocker.config.getConfig().getString("locations.world"));
		Location p1 = configToLocation(Restocker.config.getConfig().getConfigurationSection("locations.low"), world);
		Location p2 = configToLocation(Restocker.config.getConfig().getConfigurationSection("locations.high"), world);
		min = new Location(p1.getWorld(),
				Math.min(p1.getBlockX(), p2.getBlockX()),
				Math.min(p1.getBlockY(), p2.getBlockY()),
				Math.min(p1.getBlockZ(), p2.getBlockZ()));

		max = new Location(p1.getWorld(),
				Math.max(p1.getBlockX(), p2.getBlockX()),
				Math.max(p1.getBlockY(), p2.getBlockY()),
				Math.max(p1.getBlockZ(), p2.getBlockZ()));
	}

	private void loadChests(){
		ResultSet rs;
		setMinMaxLocations();
		try{
			rs = Restocker.sql.querySQL("SELECT * FROM ChestStorage");
			while(rs.next()){
				String chunk = rs.getString("chunk");
				String block = rs.getString("block");
				if(chunk != null && block != null){
					addChest(rs.getString("chunk"), rs.getString("block"));
				}
			}
			rs.close();
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}

	public static void scanWorld(){
		Restocker.getInstance().getLogger().info("Scanning world...");
		setMinMaxLocations();
		for(int x = min.getBlockX() ; x <= max.getBlockX() ; x += 16)
			for(int z = min.getBlockZ() ; z <= max.getBlockZ() ; z += 16)
				loadChunk(x,z);
		for(int x = min.getBlockX() ; x <= max.getBlockX() ; x += 16)
			loadChunk(x,max.getBlockZ());
		for(int z = min.getBlockZ() ; z <= max.getBlockX() ; z += 16)
			loadChunk(max.getBlockX(),z);
		loadChunk(max.getBlockX(),max.getBlockZ());
	}

	public static void writeToDatabase(){
		List<String> statements = new ArrayList<>();
		for(ChunkChest cc : chests) {
			List<Location> locations = cc.locationStrings;
			statements.addAll(locations.stream().map(location ->
			"INSERT OR IGNORE INTO ChestStorage(chunk, block) VALUES('" + cc.chunkString + "','" + locationToString(location) + "')")
			.collect(Collectors.toList()));
		}
		try{
			Restocker.sql.sendBatchStatement(statements.toArray(new String[statements.size()]));
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}

	public static void addChest(String chunkCoordinate, String location) {
		for(int i = 0 ; i < chests.size() ; i++) {
			ChunkChest cc = chests.get(i);
			if(cc.chunkString == chunkCoordinate){
				if (!cc.locationStrings.contains(stringToLocation(location))) {
					chests.remove(i);
					cc.addLocation(stringToLocation(location));
					chests.add(cc);
				}
				return;
			}
		}
		chests.add(new ChunkChest(chunkCoordinate,stringToLocation(location)));
	}

	public static void addAllChests(Chunk chunk){
		System.out.println("Loading chunk (" + chunkToString(chunk) + ")...");
		for(int x = 0 ; x <= 15 ; x++){
			for(int z = 0 ; z <= 15 ; z++){
				for(int y = min.getBlockY() ; y <= max.getBlockY() ; y++){
					if(chunk.getBlock(x,y,z).getType().equals(Material.CHEST)){
						addChest(chunkToString(chunk), locationToString(chunk.getBlock(x,y,z).getLocation()));
					}
				}
			}
		}
		System.out.println("Chunk loaded successfully!<3");
		chunk.unload();
	}

	public static void regenAllChests(){
		new StaggeredRunnable(Restocker.getInstance(), cloneList(chests)).start();
	}

	public static List<ChunkChest> cloneList(List<ChunkChest> list) {
		List<ChunkChest> clone = new ArrayList<ChunkChest>(list.size());
		for(ChunkChest item: list){
			clone.add(item);
			//clone.add(new ChunkChest(item));
		}
		return clone;
	}

	public static Location stringToLocation(String string){
		int[] coordinates = Arrays.stream(string.split(",")).mapToInt(Integer::parseInt).toArray();
		return new Location(world, coordinates[0], coordinates[1], coordinates[2]);
	}

	public static String locationToString(Location location){
		return location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ();
	}

	public static String chunkToString(Chunk chunk){
		return chunk.getX() + "," + chunk.getZ();
	}

	public static Chunk stringToChunk(String string){
		int[] coordinates = Arrays.stream(string.split(",")).mapToInt(Integer::parseInt).toArray();
		return world.getChunkAt(coordinates[0], coordinates[1]);
	}

	public static void loadChunk(int x, int z){
		final int finalX = x;
		final int finalZ = z;
		Bukkit.getScheduler().runTaskLater(Restocker.getInstance(), new Runnable(){
			@Override
			public void run() {
				addAllChests(world.getChunkAt(new Location(world,finalX, 1, finalZ)));
			}
		},10L);
	}

	private static Location configToLocation(ConfigurationSection section, World world){
		return new Location (
				world,
				Integer.valueOf(section.getString("x")),
				Integer.valueOf(section.getString("y")),
				Integer.valueOf(section.getString("z")));	
	}

}
