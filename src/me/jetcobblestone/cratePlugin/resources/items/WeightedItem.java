package me.jetcobblestone.cratePlugin.resources.items;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.decimal4j.util.DoubleRounder;

import me.jetcobblestone.cratePlugin.resources.guis.CrateCreatorGUI;
import me.jetcobblestone.cratePlugin.resources.guis.GUI;
import me.jetcobblestone.cratePlugin.resources.util.CustomSlot;
import me.jetcobblestone.cratePlugin.resources.util.ItemFactory;
import me.jetcobblestone.cratePlugin.resources.util.Tracker;
import net.wesjd.anvilgui.AnvilGUI.Builder;

public class WeightedItem {
	
	//This class is a wrapper class to link an ItemStack and a probability weight
	private ItemStack item;
	private GUI infoGui;
	private Crate crate;
	private Double weight;
	
	public WeightedItem(ItemStack item, Double weight, Crate crate) {	
		this.item = item;
		infoGui = CrateCreatorGUI.getInstance().getInfoPage("Item info", item);
		if (infoGui.getContents().get(14) == crate.getInfoGUI().getContents().get(14)) {
			Bukkit.getConsoleSender().sendMessage("FUCK");
		}
		infoGui.getContents().get(14).setSlot(event -> {
			Player player = (Player) event.getWhoClicked();
			Tracker.getInstance().goBack(player);
		});
		if (crate != null) {
			this.crate = crate;
		}
		this.weight = DoubleRounder.round(weight, 5);
	}
	
	public double getWeight(){	
		return weight;
	}
	
	public void setWeight(Double weight){
		this.weight = DoubleRounder.round(weight, 5);
		updateWeight();
	}
	
	public ItemStack getItem() {
		return item;
	}
	
	public GUI getInfoGUI() {
		return infoGui;
	}
	
	public void updateWeight() {
		ArrayList<String> lore = new ArrayList<String>();
		lore.add(ChatColor.RESET + "This item has a weight of " + weight);
		lore.add(ChatColor.RESET +  "This item has a " + Double.toString(DoubleRounder.round((weight/crate.getTotalWeight() * 100),5)) + "% chance of being rolled.");
		lore.add("");
		lore.add(ChatColor.GOLD + "Click to edit weight");
		final CustomSlot weightCounterSlot = new CustomSlot(ItemFactory.createItem("Weight", ChatColor.GOLD, Material.PAPER, lore), event -> {
			Player player = (Player) event.getWhoClicked();
			Builder weightEditor = CrateCreatorGUI.getInstance().getWeightEditor(player, this);
			weightEditor.open(player);
		});
		infoGui.setItem(13, weightCounterSlot);
		Tracker.getInstance().refreshInventory(infoGui.getInventory());
	}
}