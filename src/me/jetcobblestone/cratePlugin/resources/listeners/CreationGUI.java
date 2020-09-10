package me.jetcobblestone.cratePlugin.resources.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

import me.jetcobblestone.cratePlugin.resources.guis.GUI;

//This class is the listener responsible for managing the crate creator GUI
public class CreationGUI implements Listener{
	
	@EventHandler
	public void onGUIClick(InventoryClickEvent event) {
		if (GUI.getGUI(event.getClickedInventory()) != null) {
			event.setCancelled(true);
			GUI gui = GUI.getGUI(event.getClickedInventory());
			gui.clickItem(event.getSlot(), event);
			gui.runGuiAction(event);
			return;
		}
		if (event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT || event.getClick() == ClickType.DOUBLE_CLICK) {
			if (GUI.getGUI(event.getView().getTopInventory()) != null) {
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onDrag(InventoryDragEvent event) {
		if (GUI.getGUI(event.getInventory()) != null) {
			event.setCancelled(true);
		}
	}
}
