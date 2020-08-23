package me.jetcobblestone.cratePlugin.resources.util;

import org.bukkit.event.inventory.InventoryClickEvent;

public interface Action {
	public void run(InventoryClickEvent event);
}
