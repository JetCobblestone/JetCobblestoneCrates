package me.jetcobblestone.cratePlugin.resources.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.jetcobblestone.cratePlugin.resources.guis.GUI;

//This class creates a history of the GUIs a Player visits in order, so a back arrow can easily send the player backwards.

public class Tracker {
	
	private static Tracker instance;
	
	private final HashMap<UUID, ArrayList<GUI>> inventoryTree = new HashMap<UUID, ArrayList<GUI>>();
	private final ItemStack forwardArrow = ItemFactory.createItem("Foward", null, Material.ARROW, null);
	
	private final ItemStack backArrow = ItemFactory.createItem("Back", ChatColor.RESET, Material.ARROW, null);
	private final CustomSlot backArrowSlot = new CustomSlot(backArrow, event -> { goBack((Player) event.getWhoClicked()); event.setCancelled(true);	});
	
	public static Tracker getInstance() {
	     if(instance == null)
	        instance = new Tracker();
	     return instance;
	}
	
	//This will read the player's history and open the previous inventory they were looking in, whilst popping the inventory they are currently looking in off their history
	public void goBack(Player player) {
		ArrayList<GUI> list = inventoryTree.get(player.getUniqueId());
		list.get(list.size()-2).unTrackedOpen(player);
		list.remove(list.size()-1);
	}
	
	public void add(Player player, GUI gui){
		UUID uuid = player.getUniqueId();
		if(inventoryTree.get(uuid) == null) {
			inventoryTree.put(uuid, new ArrayList<GUI>());
		}
		inventoryTree.get(uuid).add(gui);
	}
	
	public HashMap<UUID, ArrayList<GUI>> getInventoryTree(){
		return inventoryTree;
	}
	
	public void clear(Player player) {
		inventoryTree.put(player.getUniqueId(), new ArrayList<GUI>());
	}
	
	public CustomSlot getBackArrow() {
		return backArrowSlot;
	}
	public ItemStack getForwardArrow() {
		return forwardArrow;
	}
	
	public void untrackedBack(Player player) {
		ArrayList<GUI> list = inventoryTree.get(player.getUniqueId());
		list.get(list.size()-1).unTrackedOpen(player);
	}
	
	public void refreshInventory(Inventory inv) {
		for (HumanEntity player : inv.getViewers()) {
			((Player) player).updateInventory();
		}
	}
}