package me.jetcobblestone.cratePlugin.resources.util;

import java.lang.reflect.Method;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;


public class CustomSlot {
	ItemStack item;
	Method effect;
	Action action;
	
	public CustomSlot(ItemStack item, Action action){
		this.item = item;
		this.action = action;
	}
	
	public void click(InventoryClickEvent event) {
		action.run(event);
	}
	
	public ItemStack getItem() {
		return item;
	}
	
	public void setSlot(Action action) {
		this.action = action;
	}
	
	public CustomSlot getDeadCopy() {
		CustomSlot copy = new CustomSlot(item, event -> {});
		return copy;
	}
	
	public CustomSlot clone() {
		CustomSlot clone = new CustomSlot(item.clone(), action);
		return clone;
	}
	
	public Action getAction() {
		return action;
	}
}
