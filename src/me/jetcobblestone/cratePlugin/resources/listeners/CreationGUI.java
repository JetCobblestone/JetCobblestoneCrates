package me.jetcobblestone.cratePlugin.resources.listeners;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import me.jetcobblestone.cratePlugin.resources.guis.CrateCreatorGUI;
import me.jetcobblestone.cratePlugin.resources.items.Crate;
import me.jetcobblestone.cratePlugin.resources.saver.Saver;
import me.jetcobblestone.cratePlugin.resources.util.Tracker;

//This class is the listener responsible for managing the crate creator GUI
public class CreationGUI implements Listener{
	
	private final Tracker tracker = Tracker.getInstance();
	private final CrateCreatorGUI crateCreatorGUI = CrateCreatorGUI.getInstance();
	private final Saver saver = Saver.getInstance();
	private final ArrayList<Crate> crateList = saver.getCrateList();
	private final ArrayList<Inventory> inventoryList = crateCreatorGUI.getInventoryList();
	
	@EventHandler
	public void onGUIClick(InventoryClickEvent event) {
		
		//Checking if the player has clicked on an inventory showing the available crates
		for(int i = 0; i < inventoryList.size(); i++) {
			if (event.getInventory() == inventoryList.get(i)) {
				
				event.setCancelled(true);
				tracker.add((Player) event.getWhoClicked(), inventoryList.get(i));
				//Checking if they click on a crate
				for (int x = 0; x < crateList.size(); x++) {
					if (event.getCurrentItem() != null && event.getCurrentItem().equals(crateList.get(x).getItem())) {
						Inventory page = crateCreatorGUI.getCrateEditPage(crateList.get(x));
						event.getWhoClicked().openInventory(page);
						tracker.add((Player) event.getWhoClicked(), page);
					}
				}
				
			}
		}
		//Checking if they have clicked on a crate's edit page
		if(event.getClickedInventory() == crateCreatorGUI.getCrateEditPage(null)) {
			event.setCancelled(true);
			//GetItem
			if(event.getSlot() == 12) {
				event.getWhoClicked().getInventory().addItem(event.getClickedInventory().getItem(4));
			}
			//Edit
			if(event.getSlot() == 13) {
				ArrayList<Inventory> list = Crate.findCrate(event.getClickedInventory().getItem(4)).getCrateContentsGUI();
				event.getWhoClicked().openInventory(list.get(0));
				tracker.add((Player) event.getWhoClicked(), list.get(0));
			}
			//Delete
			if (event.getSlot() == 14) {
				crateCreatorGUI.removeCrateFromGUI(event.getClickedInventory().getItem(4));
				saver.getCrateList().remove(Crate.findCrate(event.getClickedInventory().getItem(4)));
				tracker.goBack((Player) event.getWhoClicked());
			}
		} 
		
		for (Crate crate : saver.getCrateList()) {
			for (int i = 0; i < crate.getCrateContentsGUI().size(); i++) {
				if (event.getClickedInventory().equals(crate.getCrateContentsGUI().get(i))) {
					event.setCancelled(true);
				}
			}
		}
		
		//Back arrow logic
		if(event.getCurrentItem() != null && event.getCurrentItem().isSimilar(tracker.getBackArrow())) {
			tracker.goBack((Player) event.getWhoClicked());
			event.setCancelled(true);
		}
		
	}
	
}
