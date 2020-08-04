package me.jetcobblestone.cratePlugin.resources.guis;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.jetcobblestone.cratePlugin.resources.items.Crate;
import me.jetcobblestone.cratePlugin.resources.util.ItemFactory;
import me.jetcobblestone.cratePlugin.resources.util.Tracker;

//This class makes and provided the GUIs for the crate editor.

public class CrateCreatorGUI {
	
	private static CrateCreatorGUI instance;
	
	private final ArrayList<Inventory> inventoryList = new ArrayList<Inventory>();
	private Inventory page;
	private Inventory editPage;
	private final Tracker tracker = Tracker.getInstance();
	
	private CrateCreatorGUI() {
		createCrateEditPage();
		if (inventoryList.isEmpty()) {
			getPage("Crate Creator", inventoryList);
		}
	}
	
	public static CrateCreatorGUI getInstance() {
		if (instance == null) {
			instance = new CrateCreatorGUI();
		}
		return instance;
	}
	
	public void getPage(String name, ArrayList<Inventory> list) {
		page = Bukkit.createInventory(null, 54, ChatColor.GOLD + name);
		if(list.size() != 0) {
			page.setItem(48, tracker.getBackArrow());
			list.get(list.size() - 2).setItem(50, tracker.getForwardArrow());
		}
		page.setItem(49, ItemFactory.createItem("Page " + Integer.toString(list.size() + 1), ChatColor.RESET, Material.PAPER, null));		
		list.add(page);
	}
	
	public void addCrateToGUI(ItemStack crate) {
		if (inventoryList.get(inventoryList.size()-1).getItem(44) != null) {
			getPage("Crate Creator", inventoryList);
		}
		inventoryList.get(inventoryList.size()-1).addItem(crate);
	}
	
	public void removeCrateFromGUI(ItemStack crate) {
		int slot;
		for(Inventory itterator: inventoryList) {
			if(itterator.contains(crate)) {
				page = itterator;
			}
		}
		page.remove(crate);
		do {
			slot = page.firstEmpty();
			page.setItem(slot, page.getItem(slot + 1));
			page.clear(slot + 1);
			
		} while(page.getItem(page.firstEmpty() + 1) != null);		
	}
	
	private void createCrateEditPage() {
		Bukkit.getConsoleSender().sendMessage("Inventory created");
		editPage = Bukkit.createInventory(null, 27, ChatColor.GOLD + "Edit");
		editPage.setItem(22, tracker.getBackArrow());
		editPage.setItem(12, ItemFactory.createItem("Get Crate", ChatColor.GOLD, Material.GREEN_WOOL, null));
		editPage.setItem(13, ItemFactory.createItem("Edit", ChatColor.GOLD, Material.PAPER, null));
		editPage.setItem(14, ItemFactory.createItem("Delete", ChatColor.GOLD, Material.BARRIER, null));
		
	}
	
	public Inventory getCrateEditPage(Crate crate) {
		if(crate != null) {
			editPage.setItem(4, crate.getItem());
		}
		return editPage;
	}
	
	public ArrayList<Inventory> getInventoryList(){
		return inventoryList;
	}	

}
