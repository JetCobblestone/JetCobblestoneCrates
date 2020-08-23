package me.jetcobblestone.cratePlugin.resources.saver;

import java.util.ArrayList;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import me.jetcobblestone.cratePlugin.CratePlugin;
import me.jetcobblestone.cratePlugin.resources.items.Crate;

/**The purpose of this class is to keep track of all the crate objects in a list,
  *as well load and save the crate objects between the configuration file.
**/
public class Saver {
	
	private static Saver instance;
	
	public static Saver getInstance() {
		if(instance == null) {
			instance = new Saver();
		}
		
		return instance;
	}
	
	private final Plugin mainInstance = CratePlugin.getInstance();
	private final FileConfiguration config = mainInstance.getConfig();
	private final ArrayList<Crate> crateList = new ArrayList<Crate>();
	
	//Saves crates in cratesList to config
	public void saveCrates() {
		for(int x = 0; x < crateList.size(); x++) {
			
			config.set(("Crates.Crate " + Integer.toString(x) + ".Name"), crateList.get(x).getName());
			for(int i = 0; i < (crateList.get(x).getContents().size()); i++) {
				config.set(("Crates.Crate " + Integer.toString(x) + ".ItemStacks.ItemStack " + Integer.toString(i)), crateList.get(x).getContents().get(i).getWeightedItem().getItem());
				config.set(("Crates.Crate " + Integer.toString(x) + ".ItemStacks.Weight " + Integer.toString(i)), crateList.get(x).getContents().get(i).getWeightedItem().getWeight());
			config.set(("Crates.Crate " + Integer.toString(x) + ".NumberOfItems"), crateList.get(x).getContents().size());
		}
		config.set("CrateNumber", crateList.size());
		mainInstance.saveConfig();
		}
	}
	
	//Loads crates from config to cratesList
	public void loadCrates() {
		for(int x = 0; x < config.getInt("CrateNumber"); x++) {
			Crate crate = new Crate(config.getString("Crates.Crate " + Integer.toString(x) + ".Name", "invalid"), null);
			for( int i = 0; i < config.getInt("Crates.Crate " + Integer.toString(x) + ".NumberOfItems", 0); i++){
				ItemStack item = config.getItemStack("Crates.Crate " + Integer.toString(x) + ".ItemStacks.ItemStack " + Integer.toString(i));
				Double weight = config.getDouble("Crates.Crate " + Integer.toString(x) + ".ItemStacks.Weight " + Integer.toString(i));
				crate.addItem(item, weight);
			}
		}
	}
	
	public ArrayList<Crate> getCrateList() {
		return crateList;
	}
}
