package me.jetcobblestone.cratePlugin.resources.items;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import me.jetcobblestone.cratePlugin.CratePlugin;
import me.jetcobblestone.cratePlugin.resources.guis.CrateCreatorGUI;
import me.jetcobblestone.cratePlugin.resources.guis.GUI;
import me.jetcobblestone.cratePlugin.resources.saver.Saver;
import me.jetcobblestone.cratePlugin.resources.util.CrateScroller;
import me.jetcobblestone.cratePlugin.resources.util.CustomSlot;
import me.jetcobblestone.cratePlugin.resources.util.ItemFactory;
import me.jetcobblestone.cratePlugin.resources.util.ScrollerSetting;
import me.jetcobblestone.cratePlugin.resources.util.Tracker;


public class Crate {
	/**
	 * ================== Fields ==================
	 */
	
	private ItemStack crateItemStack;
	private String name;
	private GUI infoPage;
	private CustomSlot crateSlot;
	private UUID uuid;
	private CrateScroller scroller = new CrateScroller(new ScrollerSetting());
		
	//Identifier to tell if the ItemStack is a crate
	private static final NamespacedKey key = new NamespacedKey(CratePlugin.getInstance(), "Crate");
	
	//List for storing all the items in the crate
	private final ArrayList<WeightedItemSlot> contentsList;
	
	//List for storing the GUIs for displaying all the items in the crate
	private final ArrayList<GUI> crateContentsGuiList = new ArrayList<GUI>();
	
	private static final CrateCreatorGUI crateCreatorGUI = CrateCreatorGUI.getInstance();
	private static final Saver saver = Saver.getInstance();
	private static final Tracker tracker = Tracker.getInstance();
	
	private final ItemStack contentsIcon = ItemFactory.createItem("Contents", ChatColor.GOLD, Material.PAPER, null);
	private final CustomSlot contentsIconSlot = new CustomSlot(contentsIcon, event ->{
		crateContentsGuiList.get(0).open((Player) event.getWhoClicked());
	});
	
	/**
	 * ================== Behaviour ==================
	 */
	
	//Crate object functions
	public Crate(String name, UUID uuid){
		this.name = name;
		this.uuid = uuid;
		contentsList = new ArrayList<WeightedItemSlot>(); 
		
		makeCrateItemStack();
		generateInfoPage();
		getNewContentsPage();

		saver.getCrateList().add(this);
		crateCreatorGUI.addCrateToGUI(this);
	}
	
	private void generateInfoPage(){
		infoPage = crateCreatorGUI.getInfoPage(ChatColor.GOLD + "Crate Info - " + name, crateItemStack);
		infoPage.getContents().get(14).setSlot(event -> {
			Player player = (Player) event.getWhoClicked();
			delete();
			tracker.goBack(player);
		});
		infoPage.setItem(13, contentsIconSlot);
	}
	
	private void delete() {
		saver.getCrateList().remove(this);
		for (GUI gui : crateCreatorGUI.getGuiList()) {gui.clear();};
		for (Crate crate : Saver.getInstance().getCrateList()) {
			crateCreatorGUI.addCrateToGUI(crate);
		}
		crateCreatorGUI.updateInventoryList(crateCreatorGUI.getGuiList());
		for (GUI gui : crateCreatorGUI.getGuiList()) {
			gui.getContents().get(45).setSlot(event -> {
				crateCreatorGUI.getAddCrateGui().open((Player) event.getWhoClicked());
			});
		}
		//To-do: close all the sub inventories of the crate
	}
	
	//Crate contents functions
	private void makeCrateItemStack() {	
		//This function makes an ItemStack for the crate object which players can interact with in game.
		List<String> lore = new ArrayList<String>();
		lore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "Right click to get a reward!");
		crateItemStack = ItemFactory.createItem(name, ChatColor.RESET, Material.CHEST, lore);
		ItemMeta crateMeta = crateItemStack.getItemMeta();
		crateMeta.getPersistentDataContainer().set(Crate.key, PersistentDataType.DOUBLE, Math.PI);
		crateItemStack.setItemMeta(crateMeta);
		
		crateSlot = new CustomSlot(crateItemStack, event -> {
			Player player = (Player) event.getWhoClicked();
			infoPage.open(player);
		});
	}
	
	public void addItem(ItemStack item, Double weight) {
		final WeightedItem weightedItem = new WeightedItem(item, weight, this);
		final CustomSlot itemSlot = new CustomSlot(item, event ->{
			Player player = (Player) event.getWhoClicked();
			weightedItem.updateWeight();
			weightedItem.getInfoGUI().open(player);
		});
		final WeightedItemSlot weightedItemSlot = new WeightedItemSlot(weightedItem, itemSlot);
		contentsList.add(weightedItemSlot);
		addSlot(itemSlot);

	}
	
	public void addSlot(CustomSlot slot) {
		if(crateContentsGuiList.get(crateContentsGuiList.size() - 1).getInventory().getItem(44) != null) {
			getNewContentsPage();
		}
		crateContentsGuiList.get(crateContentsGuiList.size() - 1).add(slot);
	}
	
	public void deleteSlot(WeightedItemSlot weightedSlot) {
		contentsList.remove(weightedSlot);
		for (GUI gui : crateContentsGuiList) {gui.clear();};
		updateContentsGUI();
		crateCreatorGUI.updateInventoryList(crateContentsGuiList);
		for (GUI gui : crateContentsGuiList) {
			crateCreatorGUI.addGuiPageItems(gui);
			addContentGuiItems(gui);
		}
	}
	
	public void updateContentsGUI() {
		for (int i = 0; i < contentsList.size(); i++) {
			addSlot(contentsList.get(i).getSlot());
		}
	}
	
	public void getNewContentsPage(){
		GUI gui = crateCreatorGUI.getNewGuiPage(ChatColor.GOLD + "Contents");
		crateContentsGuiList.add(gui);
		addContentGuiItems(gui);
	}
	
	private void addContentGuiItems(GUI gui) {
		gui.getContents().get(45).setSlot(event -> {
			crateCreatorGUI.getAddItemGui(this).open((Player) event.getWhoClicked());
		});
		if (gui != crateContentsGuiList.get(0)) {
			final ItemStack forwardArrow = ItemFactory.createItem("Next Page", ChatColor.RESET, Material.ARROW, null);
			final CustomSlot fowardArrowSlot = new CustomSlot(forwardArrow, event-> {
				crateContentsGuiList.get(crateContentsGuiList.indexOf(GUI.getGUI(event.getClickedInventory())) + 1).open((Player)event.getWhoClicked());
			});
			crateContentsGuiList.get(crateContentsGuiList.indexOf(gui)-1).setItem(50, fowardArrowSlot);
		}
	}
	
	//Opening crate behaviour
	public ItemStack randomItem() {
		/**This method returns a random ItemStack from the contentsList. The chance of any one item being chosen is based on it's weight.
		The way the probability works is by multiplying the total weight of all the WeightedItems in the crate by a random value from 0.0 - 1.0.
		This effectively gets a percentage of the total weight. It will then iterate through the contents of the list, subtracting the weight of each
		item from the total weight. If the weight drops to 0 or below, it will will return the item who's weight was just subtracted from the total.**/
		
		double totalWeight = getTotalWeight();
		double random = Math.random() * totalWeight;
		for (int i = 0; i <= contentsList.size(); i++)
		{
		    random -= contentsList.get(i).getWeightedItem().getWeight();
		    
		    if (random <= 0.0d)
		    {
		        return contentsList.get(i).getWeightedItem().getItem();
		    }
		}
		return null;
	}
	
	public void open(Player player) {
		
		//This method is responsible for the scrolling items animation and the actual opening of the crate.
		
		if (contentsList.isEmpty() == true) {
			player.sendMessage(ChatColor.DARK_RED + "Sorry, there's nothing in this crate!");
			return;
		}
		scroller.roll(this, player);
	}

	//Getters
	public String getName() {
		return name;
	}
	
	public ArrayList<WeightedItemSlot> getContents(){
		return contentsList;
	}
	
	public double getTotalWeight(){
		double totalWeight = 0.0;
		for (WeightedItemSlot item: contentsList) {
			totalWeight += item.getWeightedItem().getWeight();
		}
		return totalWeight;
	}
	
	public GUI getInfoGUI() {
		return infoPage;
	}
	
	public ArrayList<GUI> getCrateContentsGUI(){
		return crateContentsGuiList;
	}
	
	public CustomSlot getSlot() {
		return crateSlot;
	}
	
	public UUID getUUID() {
		return uuid;
	}
	
	//Setters
	public void setSettings(ScrollerSetting settings) {
		this.scroller = new CrateScroller(settings);
	}
	
	//Static utility
	public static Crate findCrate(ItemStack inputCrate) {
		for (Crate crate: saver.getCrateList()) {
			if (crate.crateItemStack.isSimilar(inputCrate)) {
				return crate;
			}
		}
		
		return null;
	}	

	public static Crate fromUUIS(UUID inputUuid) {
		for (Crate crate: saver.getCrateList()) {
			if (crate.getUUID().equals(inputUuid)) {
				return crate;
			}
		}
		return null;
	}

	public static NamespacedKey getKey() {
		return key;
	}
	
}
