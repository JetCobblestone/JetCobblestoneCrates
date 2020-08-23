package me.jetcobblestone.cratePlugin.resources.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import me.jetcobblestone.cratePlugin.resources.guis.GUI;

//This class is the listener responsible for managing the crate creator GUI
public class CreationGUI implements Listener{
	
	@EventHandler
	public void onGUIClick(InventoryClickEvent event) {
		
		if (event.getCurrentItem() == null) return;
		if (GUI.getGUI(event.getClickedInventory()) != null) {
			event.setCancelled(true);
			GUI gui = GUI.getGUI(event.getClickedInventory());
			gui.clickItem(event.getSlot(), event);
		}
	}
}
