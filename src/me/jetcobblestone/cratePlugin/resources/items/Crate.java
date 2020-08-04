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
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import me.jetcobblestone.cratePlugin.CratePlugin;
import me.jetcobblestone.cratePlugin.resources.guis.CrateCreatorGUI;
import me.jetcobblestone.cratePlugin.resources.guis.CrateScrollGUI;
import me.jetcobblestone.cratePlugin.resources.saver.Saver;
import me.jetcobblestone.cratePlugin.resources.util.ItemFactory;
import me.jetcobblestone.cratePlugin.resources.util.Tracker;


public class Crate {
	
	//Identifier to tell if the ItemStack is a crate
	private static final NamespacedKey key = new NamespacedKey(CratePlugin.getInstance(), "Crate");
	
	private ItemStack crate;
	private String name;
	private final ItemStack addButton = ItemFactory.createItem("Add Item", ChatColor.RESET, Material.GREEN_WOOL, null);
	
	//List for storing all the items in the crate
	private final ArrayList<WeightedItem> contentsList;
	
	//List for storing the GUI for displaying all the items in the crate
	private final ArrayList<Inventory> crateContentsGUI_List;
	
	private static final CrateCreatorGUI crateCreatorGUI = CrateCreatorGUI.getInstance();
	private static final Tracker tracker = Tracker.getInstance();	
	private static final Saver saver = Saver.getInstance();
	
	public Crate(String name, ArrayList<WeightedItem> contents){
		
		this.name = name;
		this.crate = makeCrateItemStack();
		
		//If the user has not input a list for the contents, it will make an empty one
		if (contents == null) {
			contents = new ArrayList<WeightedItem>();
		}
		
		contentsList = contents;
		
		saver.getCrateList().add(this);
		crateCreatorGUI.addCrateToGUI(crate);
		
		crateContentsGUI_List = new ArrayList<Inventory>();
		
		if (crateContentsGUI_List.isEmpty()) {
			crateCreatorGUI.getPage("Contents", crateContentsGUI_List);
			crateContentsGUI_List.get(0).setItem(45, addButton);
			crateContentsGUI_List.get(0).setItem(48, tracker.getBackArrow());
		}
		if (contentsList.isEmpty() == false) {
			for (int i = 0; i < contentsList.size(); i++) {
				addItemToContentsGUI(contentsList.get(i).getItem());
			}
		}
	}
	
	private ItemStack makeCrateItemStack() {
		
		//This function makes an ItemStack for the crate object which players can interact with in game.
		
		ItemStack crateStack = new ItemStack(Material.CHEST);
		ItemMeta crateMeta = crateStack.getItemMeta();
		crateMeta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + name);
		
		List<String> crateLore = new ArrayList<String>();
		crateLore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "Right click to get a reward!");
		
		crateMeta.setLore(crateLore);
		
		crateMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

		crateMeta.getPersistentDataContainer().set(Crate.key, PersistentDataType.DOUBLE, Math.PI);
		crateStack.setItemMeta(crateMeta);
		
		return crateStack;
	}
	
	public ItemStack getItem() {
		return crate;
	}
	
	public String getName() {
		return name;
	}
	
	public ArrayList<WeightedItem> getContents(){
		
		return contentsList;
	}
	
	public void addItem(ItemStack item, Double weight) {
		WeightedItem weightedItem = new WeightedItem(item, weight);
		contentsList.add(weightedItem);
		addItemToContentsGUI(item);
	}
	
	public double getTotalWeight(){
		double totalWeight = 0.0;
		for (WeightedItem item: contentsList) {
			totalWeight += item.getWeight();
		}
		return totalWeight;
	}
	
	public ItemStack randomItem() {
		//This method returns a random ItemStack from the contentsList. The chance of any one item being chosen is based on it's weight.
		/**The way the probability works is by multiplying the total weight of all the WeightedItems in the crate by a random value from 0.0 - 1.0.
		This effectively gets a percentage of the total weight. It will then iterate through the contents of the list, subtracting the weight of each
		item from the total weight. If the weight drops to 0 or below, it will will return the item who's weight was just subtracted from the total.**/
		
		double totalWeight = getTotalWeight();
		double random = Math.random() * totalWeight;
		for (int i = 0; i <= contentsList.size(); i++)
		{
		    random -= contentsList.get(i).getWeight();
		    
		    if (random <= 0.0d)
		    {
		        return contentsList.get(i).getItem();
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
		
		CrateScrollGUI scrollGUI = new CrateScrollGUI(name);
		Inventory inventory = scrollGUI.getInventory();
		
		player.sendMessage(ChatColor.GOLD + "Opening a " + ChatColor.AQUA + name);
		player.openInventory(inventory);
		player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 0.5f, 1.0f);
		
		
		new BukkitRunnable() {
			
			int timer = 0;
			int delay = -50 - new Random().nextInt(150);;
			
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

	public static Crate findCrate(ItemStack inputCrate) {
		for (Crate crate: saver.getCrateList()) {
			if (inputCrate.isSimilar(inputCrate)) {
				return crate;
			}
		}
		
		return null;
	}
	
	public static NamespacedKey getKey() {
		return key;
	}
	
	public ArrayList<Inventory> getCrateContentsGUI(){
		return crateContentsGUI_List;
	}
	
	public void addItemToContentsGUI(ItemStack item) {
		if(crateContentsGUI_List.get(crateContentsGUI_List.size() - 1).getItem(44) != null) {
			crateCreatorGUI.getPage("Contents", crateContentsGUI_List);
			crateContentsGUI_List.get(crateContentsGUI_List.size() - 1).setItem(45, addButton);
		}
		crateContentsGUI_List.get(crateContentsGUI_List.size() - 1).addItem(item);
		
	}
	
}
