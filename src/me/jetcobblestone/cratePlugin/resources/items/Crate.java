package me.jetcobblestone.cratePlugin.resources.items;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import me.jetcobblestone.cratePlugin.CratePlugin;
import me.jetcobblestone.cratePlugin.resources.guis.CrateCreatorGUI;
import me.jetcobblestone.cratePlugin.resources.guis.CrateScrollGUI;
import me.jetcobblestone.cratePlugin.resources.guis.GUI;
import me.jetcobblestone.cratePlugin.resources.saver.Saver;
import me.jetcobblestone.cratePlugin.resources.util.CustomSlot;
import me.jetcobblestone.cratePlugin.resources.util.ItemFactory;
import me.jetcobblestone.cratePlugin.resources.util.Tracker;


public class Crate {
	/**
	 * ================== Fields ==================
	 */
	
	private ItemStack crateItemStack;
	private String name;
	private GUI infoPage;
	private CustomSlot crateSlot;
		
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
	public Crate(String name){
		this.name = name;
		makeCrateItemStack();
		generateInfoPage();

		contentsList = new ArrayList<WeightedItemSlot>(); 

		saver.getCrateList().add(this);
		crateCreatorGUI.addCrateToGUI(this);

		crateContentsGuiList.add(crateCreatorGUI.getNewGuiPage("Crate Contents"));
		
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
		//To-do: close all the sub inventories of the crate
	}
	
	//Crate contents functions
	private void makeCrateItemStack() {	
		//This function makes an ItemStack for the crate object which players can interact with in game.
		List<String> lore = new ArrayList<String>();
		lore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "Right click to get a reward!");
		crateItemStack = ItemFactory.createItem(name, ChatColor.GOLD, Material.CHEST, lore);
		ItemMeta crateMeta = crateItemStack.getItemMeta();
		crateMeta.getPersistentDataContainer().set(Crate.key, PersistentDataType.DOUBLE, Math.PI);
		crateItemStack.setItemMeta(crateMeta);
		
		crateSlot = new CustomSlot(crateItemStack, event -> {
			Player player = (Player) event.getWhoClicked();
			infoPage.open(player);
		});
	}
	
	public CustomSlot getSlot() {
		return crateSlot;
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
			crateCreatorGUI.getNewGuiPage("Contents");
		}
		crateContentsGuiList.get(crateContentsGuiList.size() - 1).add(slot);
	}
	
	public void deleteSlot(WeightedItemSlot weightedSlot) {
		contentsList.remove(weightedSlot);
		for (GUI gui : crateContentsGuiList) {gui.clear();};
		updateContentsGUI();
		crateCreatorGUI.updateInventoryList(crateContentsGuiList);
	}
	
	public void updateContentsGUI() {
		for (int i = 0; i < contentsList.size(); i++) {
			addSlot(contentsList.get(i).getSlot());
		}
	}
	
	public GUI getNewContentsPage(){
		GUI gui = crateCreatorGUI.getNewGuiPage(ChatColor.GOLD + "Contents");
		gui.getContents().get(44).setSlot(event -> {
			crateCreatorGUI.getAddItemGui().open((Player) event.getWhoClicked());
		});
		return gui;
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
		
		Inventory inventory = CrateScrollGUI.getInstance().getNewGUI(name);
		
		player.sendMessage(ChatColor.GOLD + "Opening a " + ChatColor.AQUA + name);
		player.openInventory(inventory);
		player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 0.5f, 1.0f);
		
		
		new BukkitRunnable() {
			
			int timer = 0;
			int delay = -50 - new Random().nextInt(150);
			
			@Override
			public void run() {
				timer++;
				if (timer > delay) {
					delay++;
					timer = -3;
					for(int itemIndex = 9; itemIndex < 17; itemIndex++) {
						
						if (inventory.getItem(itemIndex+1) != null) {
							inventory.setItem(itemIndex, inventory.getItem(itemIndex+1));
						}
						if (itemIndex == 16) {
							inventory.setItem(17, (randomItem()));
						}	
					}
					player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 0.5f, 0.7f);	
				}
				
				
				if (delay == 10) {
					if (inventory.getItem(13).getItemMeta().getDisplayName() == "") {
						player.sendMessage(ChatColor.GOLD + "You won " + ChatColor.AQUA + "" + inventory.getItem(13).getType().name().toLowerCase());
					}
					else {
						player.sendMessage(ChatColor.GOLD + "You won " + ChatColor.AQUA + "" + inventory.getItem(13).getItemMeta().getDisplayName());
					}
					
					player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1f, 1f);
					player.getInventory().addItem(inventory.getItem(13));
					
					cancel();
				}
			}
			//runnable
		}.runTaskTimer(CratePlugin.getInstance(), 20l, 1l);
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
	
	public static NamespacedKey getKey() {
		return key;
	}
	
	public GUI getInfoGUI() {
		return infoPage;
	}
	
	public ArrayList<GUI> getCrateContentsGUI(){
		return crateContentsGuiList;
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
	
}
