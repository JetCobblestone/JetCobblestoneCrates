package me.jetcobblestone.cratePlugin.resources.guis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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

	// =============
	
	private CrateCreatorGUI() {
		final GUI gui = getNewGuiPage(ChatColor.GOLD +"Crate Creator");
		guiList.add(gui);
		addGuiPage(gui);
	}
	
	public static CrateCreatorGUI getInstance() {
		if (instance == null) {
			instance = new CrateCreatorGUI();
		}
		return instance;
	}
	
	// =============
	
	public void addGuiPage(GUI gui) {
		if (guiList.indexOf(gui) == 0) {
			gui.setItem(49, null);
		}
		else {
			final ItemStack forwardArrow = ItemFactory.createItem("Next Page", ChatColor.RESET, Material.ARROW, null);
			final CustomSlot fowardArrowSlot = new CustomSlot(forwardArrow, event-> {
				guiList.get(guiList.indexOf(GUI.getGUI(event.getClickedInventory())) + 1).open((Player)event.getWhoClicked());
			});
			guiList.get(guiList.indexOf(gui)-1).setItem(50, fowardArrowSlot);
		}
		gui.getContents().get(45).setSlot(event -> {
			getAddCrateGui().open((Player) event.getWhoClicked());
		});
	}
	
	public void addCrateToGUI(Crate crate) {
		if (guiList.get(guiList.size()-1).getInventory().getItem(44) != null) {
			final GUI gui = getNewGuiPage(ChatColor.GOLD +"Crate Creator");
			addGuiPage(gui);
			guiList.add(gui);
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
	
	
	// ============= multi-paged GUI for displaying the contents of each crate, and all the crates
	 
	
	public GUI getNewGuiPage(String name) {
		final GUI gui = new GUI(ChatColor.GOLD + name, 6);
		addGuiPageItems(gui);
		return gui;
	}
	
	public void addGuiPageItems(GUI gui) {
		final List<String> addButtonLore = Arrays.asList(new String[]{"", ChatColor.RESET + "Click to create your item"});
		final ItemStack addCrateButton = ItemFactory.createItem("Add Item", ChatColor.RESET, Material.GREEN_WOOL, addButtonLore);
		final CustomSlot  addButtonSlot = new CustomSlot(addCrateButton, event -> {
			Bukkit.getConsoleSender().sendMessage("Oops, add button funcitonality has not been applied");
		});
		gui.setItem(45, addButtonSlot);
		gui.setItem(49, tracker.getBackArrow());;
	}
	
	// =============
	
	public GUI getInfoPage(String name, ItemStack item) {
		
		final ItemStack getItem = ItemFactory.createItem("Get Item", ChatColor.GOLD, Material.GREEN_WOOL, null);
		final CustomSlot getItemSlot = new CustomSlot(getItem, event -> {
			event.getWhoClicked().getInventory().addItem(event.getClickedInventory().getItem(4));
		});
		final ItemStack deleteItem = ItemFactory.createItem("Delete", ChatColor.GOLD, Material.BARRIER, null);
		final CustomSlot deleteItemSlot = new CustomSlot(deleteItem, event -> {
			Bukkit.getConsoleSender().sendMessage("Uh oh, something went wrong. Specific delete behaviour should be here...");
		});
		
		final GUI gui = new GUI(ChatColor.GOLD + "Info", 3);
		gui.setItem(22, tracker.getBackArrow());
		gui.setItem(12, getItemSlot);
		gui.setItem(14, deleteItemSlot);
		gui.setItem(4, new CustomSlot(item, event -> {}));
		return gui;
	}
	 
	// =============
	
	public GUI getAddCrateGui() {
		final GUI gui = new GUI(ChatColor.GOLD + "Add Crate", 3);
		
		final ItemStack crateIcon = ItemFactory.createItem("Un-named crate", ChatColor.GOLD, Material.CHEST, null);
		final CustomSlot crateIconSlot = new CustomSlot(crateIcon, event -> {});
		final List<String> colourPickerLore = Arrays.asList(new String[]{"", ChatColor.RESET + "Click to change the colour of your crate's name"});
		final ItemStack colourPicker = ItemFactory.createItem("Colour Picker", ChatColor.GOLD, Material.GREEN_WOOL, colourPickerLore);
		final CustomSlot colourPickerSlot = new CustomSlot(colourPicker, event -> {
			GUI colourPickerGui = getColourPicker(event.getClickedInventory());
			colourPickerGui.open((Player) event.getWhoClicked());
		});
		final List<String> addButtonLore = Arrays.asList(new String[]{"", ChatColor.RESET + "Click to create your item"});
		final ItemStack addCrateButton = ItemFactory.createItem("Add Item", ChatColor.RESET, Material.GREEN_WOOL, addButtonLore);
		final CustomSlot addButtonSlot = new CustomSlot(addCrateButton, event -> {
			new Crate(event.getClickedInventory().getItem(4).getItemMeta().getDisplayName(), UUID.randomUUID());
			tracker.goBack((Player) event.getWhoClicked());
		});
		final ItemStack editName = ItemFactory.createItem("Edit Name", ChatColor.GOLD, Material.PAPER, null);
		final CustomSlot editNameSlot = new CustomSlot(editName, event -> {
			final Builder builder = getNameEditor(event.getClickedInventory().getItem(4));
			builder.open((Player) event.getWhoClicked());
		});
		gui.setItem(4, crateIconSlot);
		gui.setItem(12, colourPickerSlot);
		gui.setItem(13, addButtonSlot);
		gui.setItem(14, editNameSlot);
		gui.setItem(22, tracker.getBackArrow());
		return gui;
	}
	
	private final List<ChatColor> colourList = Arrays.asList(ChatColor.RED, ChatColor.GOLD, ChatColor.YELLOW, ChatColor.GREEN, ChatColor.DARK_GREEN, ChatColor.AQUA, ChatColor.BLUE, ChatColor.DARK_BLUE, ChatColor.DARK_PURPLE, ChatColor.LIGHT_PURPLE, ChatColor.BLACK, ChatColor.DARK_GRAY, ChatColor.GRAY, ChatColor.WHITE);
	private final List<Material> materialList = Arrays.asList(Material.RED_DYE, Material.ORANGE_DYE, Material.YELLOW_DYE, Material.LIME_DYE, Material.GREEN_DYE, Material.LIGHT_BLUE_DYE, Material.CYAN_DYE, Material.BLUE_DYE, Material.PURPLE_DYE, Material.PINK_DYE, Material.BLACK_DYE, Material.GRAY_DYE, Material.LIGHT_GRAY_DYE, Material.WHITE_DYE);
	private final List<String> nameList = Arrays.asList("Red", "Orange", "Yellow", "Lime", "Green", "Aqua", "Light Blue", "Dark Blue", "Purple", "Pink", "Black", "Dark Gray", "Gray", "White");
	public GUI getColourPicker(Inventory inv) {
		final GUI gui = new GUI(ChatColor.GOLD + "Colour Picker", 3);
		
		gui.setAction(event -> {
			if (event.getSlot() < materialList.size()) {
				for (int i = 0; i < materialList.size(); i++) {
					ItemStack item = event.getClickedInventory().getItem(i);
					if (item.containsEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL)) {
						item.removeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL);
					}
				}
				event.getCurrentItem().addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
			}
		}); 
		
		final ItemStack confirmChoice = ItemFactory.createItem("Confirm Choice", ChatColor.GOLD, Material.GREEN_WOOL, null);
		final CustomSlot confirmChoiceSlot = new CustomSlot(confirmChoice, event -> {
			tracker.goBack((Player)event.getWhoClicked());
		});
		
		for (int i = 0; i < colourList.size(); i++) {
			final int itterator = i;
			final ItemStack item = ItemFactory.createItem(nameList.get(i), colourList.get(i), materialList.get(i), null);
			final CustomSlot itemSlot = new CustomSlot(item, event -> {
				item.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);	
				ItemStack inputItem = inv.getItem(4);
				ItemMeta inputMeta = inputItem.getItemMeta();
				inputMeta.setDisplayName(colourList.get(itterator) + (ChatColor.stripColor(inputMeta.getDisplayName())));
				inputItem.setItemMeta(inputMeta);
			});
			gui.setItem(i, itemSlot);
		}
		gui.setItem(22, confirmChoiceSlot);
		
		return gui;
	}
	
	// =============
	
  	public GUI getAddItemGui(Crate crate) {
		final GUI gui = new GUI(ChatColor.GOLD + "Add Item", 3);
		
		final List<String> itemSlotLore = Arrays.asList(new String[]{"", ChatColor.RESET + "Put your new item in this slot"});
		final ItemStack itemSlot = ItemFactory.createItem("Item Slot", ChatColor.RESET, Material.GRAY_STAINED_GLASS_PANE, itemSlotLore);
		final CustomSlot itemSlotSlot = new CustomSlot(itemSlot, event -> {});
		final List<String> removeAmountLore = Arrays.asList(new String[]{ChatColor.RESET + "Click for -1", ChatColor.RESET + "Shift + Click for -5"});
		final ItemStack removeAmount = ItemFactory.createItem("Remove amount", ChatColor.RESET, Material.RED_STAINED_GLASS_PANE, removeAmountLore);
		final CustomSlot removeAmountSlot = new CustomSlot(removeAmount, event ->{
			if (event.getClickedInventory().getItem(4).equals(itemSlot)) return;
			int amount;
			if (event.getClick() == ClickType.SHIFT_LEFT) {
				amount = -5;
			}
			else {
				amount = -1;
			}
			increaseAmount(event.getClickedInventory().getItem(4), amount, (Player) event.getWhoClicked()); 
		});
		final List<String> addButtonLore = Arrays.asList(new String[]{"", ChatColor.RESET + "Click to create your item"});
		final ItemStack addCrateButton = ItemFactory.createItem("Add Item", ChatColor.RESET, Material.GREEN_WOOL, addButtonLore);
		final CustomSlot  addButtonSlot = new CustomSlot(addCrateButton, event -> {
			if (event.getClickedInventory().getItem(4).equals(itemSlot)) return;
			crate.addItem(event.getClickedInventory().getItem(4), 0d);
			Player player = (Player) event.getWhoClicked();
			player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_YES, 1, 1);
			Builder weightEditor = getWeightEditor(crate.getContents().get(crate.getContents().size()-1).getWeightedItem());
			tracker.goBack(player);
			weightEditor.open(player);
		});
		final List<String> addAmountLore = Arrays.asList(new String[]{ChatColor.RESET + "Click for +1", ChatColor.RESET + "Shift + Click for +5"});
		final ItemStack addAmount = ItemFactory.createItem("Add amount", ChatColor.RESET, Material.GREEN_STAINED_GLASS_PANE, addAmountLore);
		final CustomSlot addAmountSlot = new CustomSlot(addAmount, event ->{
			if (event.getClickedInventory().getItem(4).equals(itemSlot)) return;
			int amount;
			if (event.getClick() == ClickType.SHIFT_LEFT) {
				amount = 5;
			}
			else {
				amount = 1;
			}
			increaseAmount(event.getClickedInventory().getItem(4), amount, (Player) event.getWhoClicked()); 
		});	
		
		gui.setAction(event -> {	
			if (event.getSlot() == 4) {
				Inventory inv = event.getClickedInventory();
				Player player = (Player) event.getWhoClicked();
				if (player.getItemOnCursor().getType() != Material.AIR) {
					ItemStack hold;
					if (!inv.getItem(4).equals(itemSlot)) {
						hold = event.getClickedInventory().getItem(4);
					}
					else {
						hold = null;
					}
					inv.setItem(4, player.getItemOnCursor());
					player.setItemOnCursor(hold);
				}
				else if (!inv.getItem(4).equals(itemSlot)) {
					player.setItemOnCursor(inv.getItem(4));
					inv.setItem(4, itemSlot);
				}
			}
		});
		
		gui.setItem(4, itemSlotSlot);
		gui.setItem(12, removeAmountSlot);
		gui.setItem(13, addButtonSlot);
		gui.setItem(14, addAmountSlot);
		gui.setItem(22, tracker.getBackArrow());
		
		return gui;
	}
	
	private void increaseAmount(ItemStack item, int amount, Player player) {
		if (item.getAmount() + amount > 64 || item.getAmount() + amount < 1) {
			player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
		}
		else {
			item.setAmount(item.getAmount() + amount);
		}
	}

	// =============
	
	public Builder getWeightEditor(WeightedItem weightedItem) {
		final Builder weightEditor = new Builder();
		weightEditor.plugin(CratePlugin.getInstance());
		weightEditor.title(ChatColor.GOLD + "Weight Editor");
		weightEditor.item(ItemFactory.createItem("Weight", ChatColor.RESET, Material.PAPER, null));
		weightEditor.text("Enter Weight");
		
		weightEditor.onComplete((player, text) -> { 
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
			player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1, 1);
			tracker.untrackedBack(player);
			return AnvilGUI.Response.close();	
		});
		return weightEditor;
	}
	
	public Builder getNameEditor(ItemStack item) {
		final Builder nameEditor = new Builder();
		nameEditor.plugin(CratePlugin.getInstance());
		nameEditor.title(ChatColor.GOLD + "Name Editor");
		nameEditor.item(ItemFactory.createItem("Name", ChatColor.RESET, Material.PAPER, null));
		nameEditor.text("Enter name");
		
		nameEditor.onComplete((player, text) -> {
			if (text.length() > 30) {
				return AnvilGUI.Response.text("Too long");
			}
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(ChatColor.RESET + text);
			item.setItemMeta(meta);
			player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1, 1);
			tracker.untrackedBack(player);
			return AnvilGUI.Response.close();
		});
		
		return nameEditor;
	}
}