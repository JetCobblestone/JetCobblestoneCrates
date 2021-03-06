package me.jetcobblestone.cratePlugin.resources.guis;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import me.jetcobblestone.cratePlugin.resources.util.Action;
import me.jetcobblestone.cratePlugin.resources.util.CustomSlot;
import me.jetcobblestone.cratePlugin.resources.util.Tracker;

public class GUI{
	
	private static final HashMap<Inventory, GUI> identifierTable = new HashMap<Inventory, GUI>();
	private static final ArrayList<CustomSlot> blankList = new ArrayList<CustomSlot>();
	private ArrayList<CustomSlot> contents = new ArrayList<CustomSlot>();
	private Inventory inv;
	private Action action;
	
	static {
		for (int i = 0; i < 56; i++) {
			blankList.add(null);
		}
	}
	
	@SuppressWarnings("unchecked")
	public GUI(String name, int rows) {
		inv = Bukkit.createInventory(null, 9 * rows, name);
		contents = (ArrayList<CustomSlot>) blankList.clone();
		identifierTable.put(inv, this);
	}
	
	@SuppressWarnings("unchecked")
	public void clear() {
		inv.clear();
		contents = (ArrayList<CustomSlot>) blankList.clone();
	}
	
	public void open(Player player) {
		player.openInventory(inv);
		Tracker.getInstance().add(player, this);
	}
	
	public void unTrackedOpen(Player player) {
		player.openInventory(inv);
	}
	
	public void setItem(int i, CustomSlot slot) {
		contents.set(i, slot);
		if (slot == null ) {
			inv.setItem(i, null);
		}
		else {
			inv.setItem(i, slot.getItem());
		}
	}
	
	public void setAction(Action action) {
		this.action = action;
	}
	
	public void clickItem(int i, InventoryClickEvent event) {
		CustomSlot slot = contents.get(i);
		if (slot != null) {
			slot.click(event);
		}
	}
	
	public void runGuiAction(InventoryClickEvent event) {
		if (action != null) {
			action.run(event);
		}
	}
	
	public void add(CustomSlot slot) {
		setItem(nextFreeSlot(), slot);
	}
	
	public void remove(CustomSlot inputSlot) {
		contents.remove(inputSlot);
		inv.remove(inputSlot.getItem());
	}
	
	public ArrayList<CustomSlot> getContents() {
		return contents;
	}
	
	public Inventory getInventory() {
		return inv;
	}
	
	public static GUI getGUI(Inventory inv1) {
		return identifierTable.get(inv1);
	}
	
	private int nextFreeSlot() {
		for (int i = 0; i < contents.size(); i++) {
			if (contents.get(i) == null) {
				return i;
			}
		}
		return -1;
	}
}
