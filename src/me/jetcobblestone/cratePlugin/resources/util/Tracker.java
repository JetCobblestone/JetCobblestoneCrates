package me.jetcobblestone.cratePlugin.resources.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

//This class creates a history of the GUIs a Player visits in order, so a back arrow can easily send the player backwards.

public class Tracker {
	
	private static Tracker instance;
	
	private final HashMap<UUID, ArrayList<Inventory>> inventoryTree = new HashMap<UUID, ArrayList<Inventory>>();
	private ArrayList<Inventory> list;
	private ItemStack forwardArrow;
	private ItemStack backArrow;
	
	private Tracker() {
		backArrow = ItemFactory.createItem("Back", ChatColor.RESET, Material.ARROW, null);
		forwardArrow = ItemFactory.createItem("Foward", null, Material.ARROW, null);
	}
	
	public static Tracker getInstance() {
	     if(instance == null)
	        instance = new Tracker();
	     return instance;
	}
	
	public void goBack(Player player) {
		list = inventoryTree.get(player.getUniqueId());
		player.openInventory(list.get(list.size()-2));
		list.remove(list.size()-1);
	}
	
	
	public void add(Player player, Inventory inventory){
		if(inventoryTree.get(player.getUniqueId()) == null) {
			inventoryTree.put(player.getUniqueId(), new ArrayList<Inventory>());
		}
		inventoryTree.get(player.getUniqueId()).add(inventory);
	}
	
	public HashMap<UUID, ArrayList<Inventory>> getInventoryTree(){
		return inventoryTree;
	}
	
	public void clear(Player player) {
		inventoryTree.put(player.getUniqueId(), new ArrayList<Inventory>());
	}
	
	public ItemStack getBackArrow() {
		return backArrow;
	}
	public ItemStack getForwardArrow() {
		return forwardArrow;
	}
}