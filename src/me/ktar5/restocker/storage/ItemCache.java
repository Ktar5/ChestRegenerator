package me.ktar5.restocker.storage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import me.ktar5.restocker.Restocker;
import me.ktar5.restocker.util.Utils;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Created by Carter on 4/7/2015.
 */
public class ItemCache {

	private static Map<String, ItemWrapper> items = new HashMap<>();
	private static int totalSum;

	public ItemCache(){
		items.clear();
		totalSum = 0;
		FileConfiguration config = Restocker.config.getConfig();
		for(String itemName : config.getConfigurationSection("items").getKeys(false)){
			loadItemStack(config.getConfigurationSection("items." + itemName), itemName);
		}
	}

	public void loadItemStack(ConfigurationSection itemSection, String itemName) {
		if(itemSection != null){
			@SuppressWarnings("deprecation")
			Material material = Material.getMaterial(itemSection.getInt("material", 0));
			if(material != null && !material.equals(Material.AIR)){
				//Retrieve lore
				List<String> lore = Utils.colorList(itemSection.getStringList("lore"));

				//Create ItemStack from config
				ItemStack item = new ItemStack(material,
						Utils.limitNumber(itemSection.getInt("amount", 1), 64),
						(short)Utils.limitNumber(itemSection.getInt("damage", 0), 32767));

				//Apply enchants
				for(String string : itemSection.getStringList("enchants")){
					String[] parts = string.split(":");
					item.addUnsafeEnchantment(Enchantment.getByName(parts[0].toUpperCase()), Integer.valueOf(parts[1]));
				}

				//Retrieve itemmeta
				ItemMeta meta = item.getItemMeta();

				//Set displayname
				meta.setDisplayName(ChatColor.RESET + itemSection.getString("name"));

				//Set lore
				if (!lore.isEmpty())
					meta.setLore(lore);

				//Set item meta
				item.setItemMeta(meta);

				//Add to the total sum for later
				totalSum += itemSection.getInt("chance",1);

				items.put(itemName, new ItemWrapper(item, itemSection.getInt("chance", 1)));
			}
		}
	}

	public static List<ItemWrapper> getItems(){
		return items.values().stream().collect(Collectors.toList());
	}

	public static int getTotalSum(){
		return totalSum;
	}

	public class ItemWrapper {
		public ItemStack item;
		public int chance;
		public ItemWrapper(ItemStack item, int chance) {
			this.item = item;
			this.chance = chance;
		}
	}


}
