package me.jetcobblestone.cratePlugin.resources.items;

import me.jetcobblestone.cratePlugin.resources.util.CustomSlot;

public class WeightedItemSlot {

	private WeightedItem weightedItem;
	private CustomSlot slot;
	
	public WeightedItemSlot(WeightedItem weightedItem, CustomSlot slot) {
		this.weightedItem = weightedItem;
		this.slot = slot;
	}
	
	public WeightedItem getWeightedItem() {
		return weightedItem;
	}
	
	public CustomSlot getSlot() {
		return slot;
	}

}
