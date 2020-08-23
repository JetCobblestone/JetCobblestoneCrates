package me.jetcobblestone.cratePlugin.resources.guis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.jetcobblestone.cratePlugin.CratePlugin;
import me.jetcobblestone.cratePlugin.resources.items.Crate;
import me.jetcobblestone.cratePlugin.resources.items.WeightedItem;
import me.jetcobblestone.cratePlugin.resources.util.CustomSlot;
import me.jetcobblestone.cratePlugin.resources.util.ItemFactory;
import me.jetcobblestone.cratePlugin.resources.util.Tracker;
import net.wesjd.anvilgui.AnvilGUI;
import net.wesjd.anvilgui.AnvilGUI.Builder;

//This class makes and provided the GUIs for the crate editor.

public class CrateCreatorGUI {
	
	private static CrateCreatorGUI instance;
	
	private final ArrayList<GUI> guiList = new ArrayList<GUI>();
	private final Tracker tracker = Tracker.getInstance();	
	
	/**
	 * =============
	 */
	
	private CrateCreatorGUI() {
		guiList.add(getNewGuiPage(ChatColor.GOLD +"Crate Creator"));
	}
	
	public static CrateCreatorGUI getInstance() {
		if (instance == null) {
			instance = new CrateCreatorGUI();
		}
		return instance;
	}
	
	public void addCrateToGUI(Crate crate) {
		if (guiList.get(guiList.size()-1).getInventory().getItem(44) != null) {
			guiList.add(getNewGuiPage(ChatColor.GOLD + "Crate Creator"));	
		}
		guiList.get(guiList.size()-1).add(crate.getSlot());
	}
	
	public void updateInventoryList(ArrayList<GUI> list) {
		for (int i = 0; i < list.size(); i++) {
			for (GUI gui : list) {
				addGuiPageItems(gui);
				tracker.refreshInventory(list.get(i).getInventory());
			}
		}
	}
	
	public ArrayList<GUI> getGuiList(){
		return guiList;
	}
	
	/**
	 * ============= multi-paged GUI for displaying the contents of each crate, and all the crates
	 */
	
	public GUI getNewGuiPage(String name) {
		final GUI gui = new GUI(ChatColor.GOLD + name, 6);
		addGuiPageItems(gui);
		return gui;
	}
	
	private void addGuiPageItems(GUI gui) {
		final List<String> addButtonLore = Arrays.asList(new String[]{"", ChatColor.RESET + "Click to create your item"});
		final ItemStack addCrateButton = ItemFactory.createItem("Add Item", ChatColor.RESET, Material.GREEN_WOOL, addButtonLore);
		final CustomSlot  addButtonSlot = new CustomSlot(addCrateButton, event -> {
			Bukkit.getConsoleSender().sendMessage("Nothing here");
		});
		gui.setItem(45, addButtonSlot);
		gui.setItem(49, tracker.getBackArrow());;
	}
	
	/**
	 * =============
	 */
	
	public GUI getInfoPage(String name, ItemStack item) {
		
		final ItemStack getItem = ItemFactory.createItem("Get Item", ChatColor.GOLD, Material.GREEN_WOOL, null);
		final CustomSlot getItemSlot = new CustomSlot(getItem, event -> {
			event.getWhoClicked().getInventory().addItem(event.getClickedInventory().getItem(4));
		});
		final ItemStack deleteItem = ItemFactory.createItem("Delete", ChatColor.GOLD, Material.BARRIER, null);
		final CustomSlot deleteItemSlot = new CustomSlot(deleteItem, event -> {
			Bukkit.getConsoleSender().sendMessage("Uh oh, something went wrong. Specific delete behaviour should be here...");
		});
		
		final GUI gui = new GUI(ChatColor.GOLD + "", 3);
		gui.setItem(22, tracker.getBackArrow());
		gui.setItem(12, getItemSlot);
		gui.setItem(14, deleteItemSlot);
		gui.setItem(4, new CustomSlot(item, event -> {}));
		return gui;
	}
	
	
	/**
	 * =============
	 */
	
	public Builder getWeightEditor(Player player, WeightedItem weightedItem) {
		final Builder weightEditor = new Builder();
		weightEditor.plugin(CratePlugin.getInstance());
		weightEditor.title(ChatColor.GOLD + "Enter new weight");
		weightEditor.item(ItemFactory.createItem("Weight", ChatColor.RESET, Material.PAPER, null));
		weightEditor.text("Enter Weight");
		
		weightEditor.onComplete((inputPlayer, text) -> { 
			try {
				Double weight = Double.parseDouble(text);
				weightedItem.setWeight(weight);
			}
			catch(Exception e){
				try {
					Double weight = new Double(Integer.parseInt(text));
					weightedItem.setWeight(weight);
				}
				catch(Exception e2) {
					return AnvilGUI.Response.text("Invalid");
				}
			}
			tracker.untrackedBack(player);
			return AnvilGUI.Response.close();	
		});
		
		return weightEditor;
	}

		//TO-DO (open up new crate maker)

	
	private void addItemGuiItems(GUI gui) {
		final List<String> addButtonLore = Arrays.asList(new String[]{"", ChatColor.RESET + "Click to create your item"});
		final ItemStack addCrateButton = ItemFactory.createItem("Add Item", ChatColor.RESET, Material.GREEN_WOOL, addButtonLore);
		final CustomSlot  addButtonSlot = new CustomSlot(addCrateButton, event -> {
			Bukkit.getConsoleSender().sendMessage("Nothing here");
		});
		final List<String> itemSlotLore = Arrays.asList(new String[]{"", ChatColor.RESET + "Put your new item in this slot"});
		final ItemStack itemSlot = ItemFactory.createItem("Item Slot", ChatColor.RESET, Material.GRAY_STAINED_GLASS_PANE, itemSlotLore);
		final CustomSlot itemSlotSlot = new CustomSlot(itemSlot, event -> {
			Bukkit.getConsoleSender().sendMessage("Nothing here");
		});
		final List<String> removeAmountLore = Arrays.asList(new String[]{ChatColor.RESET + "Click for -1", ChatColor.RESET + "Shift + Click for -5"});
		final ItemStack removeAmount = ItemFactory.createItem("Remove amount", ChatColor.RESET, Material.RED_STAINED_GLASS, removeAmountLore);
		final CustomSlot removeAmountSlot = new CustomSlot(removeAmount, event ->{
			Bukkit.getConsoleSender().sendMessage("Nothing here");
		});
		final List<String> addAmountLore = Arrays.asList(new String[]{ChatColor.RESET + "Click for +1", ChatColor.RESET + "Shift + Click for +5"});
		final ItemStack addAmount = ItemFactory.createItem("Add amount", ChatColor.RESET, Material.GREEN_STAINED_GLASS, addAmountLore);
		final CustomSlot addAmountSlot = new CustomSlot(addAmount, event ->{
			Bukkit.getConsoleSender().sendMessage("Nothing here");
		});	
		final List<String> itemAddStep2Lore = Arrays.asList(new String[]{"", ChatColor.RESET + "Click to go to the next step"});
		final ItemStack itemAddStep2 = ItemFactory.createItem("Add Item", ChatColor.RESET, Material.GREEN_WOOL, itemAddStep2Lore);
		CustomSlot itemAddStep2Slot = new CustomSlot(itemAddStep2, event -> {
			Bukkit.getConsoleSender().sendMessage("Nothing here");
		});
		
		gui.setItem(4, itemSlotSlot);
		gui.setItem(12, removeAmountSlot);
		gui.setItem(13, addButtonSlot);
		gui.setItem(14, addAmountSlot);
		gui.setItem(16, itemAddStep2Slot);
		gui.setItem(20, tracker.getBackArrow());
	}
	
	
	public GUI getAddItemGui() {
		final GUI gui = new GUI(ChatColor.GOLD + "Add Item", 3);
		addItemGuiItems(gui);
		return gui;
	}
	
}