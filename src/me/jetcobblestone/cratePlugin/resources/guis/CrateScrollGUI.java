package me.jetcobblestone.cratePlugin.resources.guis;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.jetcobblestone.cratePlugin.resources.util.ItemFactory;

//This class provides a GUI to the Crate object when the roll function is called

public class CrateScrollGUI{
	
	private static CrateScrollGUI instance;
	private final ItemStack glass1 = ItemFactory.createItem(" ", null, Material.ORANGE_STAINED_GLASS_PANE, null);
	private final ItemStack glass2 = ItemFactory.createItem(" ", null, Material.YELLOW_STAINED_GLASS_PANE, null);
	private final ItemStack hopper = ItemFactory.createItem("Reward", ChatColor.GOLD, Material.HOPPER, null);
	private final Inventory inventory = Bukkit.createInventory(null, 27, ChatColor.GOLD + "");
	
	public static CrateScrollGUI getInstance() {
		if (instance == null) {
			instance = new CrateScrollGUI();
		}
		return instance;
	}
	
	public CrateScrollGUI() {
		for (int i = 0; i <= 26; i += 2) {
			if (i < 9 || i > 17) {
				inventory.setItem(i, glass1);
			}
		}
		for (int i = 1; i <= 26; i += 2) {
			if (i < 9 || i > 17) {
				inventory.setItem(i, glass2);
			}
		}
		inventory.setItem(22, hopper);
	}
	
	//This creates the GUI used in the scrolling animation for the crate
	public Inventory getNewGUI(String name) {
		final Inventory clone = Bukkit.createInventory(null, 27, ChatColor.GOLD + name);
		clone.setContents(inventory.getContents());
		return clone;
	}


}