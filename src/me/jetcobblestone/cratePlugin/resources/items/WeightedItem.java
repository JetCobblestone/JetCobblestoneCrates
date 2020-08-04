package me.jetcobblestone.cratePlugin.resources.items;

import org.bukkit.inventory.ItemStack;

public class WeightedItem {
	
	//This class is a wrapper class to link an ItemStack and a probability weight
	private ItemStack item;
	private Double weight;
	private final Double maxWeight = 1000000.0;
	private final Double minWeight = 0.00001;
	
	public WeightedItem(ItemStack item, Double weight) {	
		this.item = item;
		
		//Validation check for weight
		if (weight > maxWeight || weight < minWeight) {
			weight = 0.0;
		}
		
		this.weight = weight;
	}
	
	public double getWeight(){	
		return weight;
	}
	
	public ItemStack getItem() {
		return item;
	}
	
}
