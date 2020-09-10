package me.jetcobblestone.cratePlugin.resources.util;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.jetcobblestone.cratePlugin.resources.items.Crate;

public class ScrollerSetting {
	
	//Intended for playing sounds but it could be used for other effects like spawning a firework on win
	public interface Effect {
		public void run(Player player, Crate crate);
	}
	
	private Inventory inv;
	private Effect onOpen;
	private Effect onTick;
	private Effect onFinish;
	private List<Row> rowList;
	private long initialDelay;
	private int delay;
	private int duration;
	private int randomDurationModifier;
	
	public ScrollerSetting (Inventory inv, Effect onOpen, Effect onTick, Effect onFinish, List<Row> rowList, long initialDelay, int delay, int duration, int randomDurationModifier){
		this.inv = inv;
		this.rowList = rowList;
		this.onOpen = onOpen;
		this.onTick = onTick;
		this.onFinish = onFinish;
		this.initialDelay = initialDelay;
		this.delay = delay;
		this.duration = duration;
		this.randomDurationModifier = randomDurationModifier;
	}
	
	public ScrollerSetting (){
		//Default settings
		inv = Bukkit.createInventory(null, 27, "");
		final ItemStack glass1 = ItemFactory.createItem(" ", ChatColor.RESET, Material.ORANGE_STAINED_GLASS_PANE, null);
		final ItemStack glass2 = ItemFactory.createItem(" ", ChatColor.RESET, Material.YELLOW_STAINED_GLASS_PANE, null);
		final ItemStack hopper = ItemFactory.createItem("Reward", ChatColor.GOLD, Material.HOPPER, null);
			
		for (int i = 0; i <= 26; i += 2) {
			if (i < 9 || i > 17) {
				inv.setItem(i, glass1);
			}
		}
		for (int i = 1; i <= 26; i += 2) {
			if (i < 9 || i > 17) {
				inv.setItem(i, glass2);
			}
		}
		inv.setItem(22, hopper);
		
		onOpen = (player, crate) -> {player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 0.5f, 1.0f);};
		onTick = (player, crate) -> {player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 0.5f, 0.7f);};
		onFinish = (player, crate) -> {player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1f, 1f);};
		
		rowList = Arrays.asList(new Row(1, true));
		initialDelay = 20l;
		delay = 1;
		duration = 100;
		randomDurationModifier = 50;
		
	}

	//Runners
	public void runOnOpen(Player player, Crate crate) {onOpen.run(player, crate);}
	public void runOnTick(Player player, Crate crate) {onTick.run(player, crate);}
	public void runOnFinish(Player player, Crate crate) {onFinish.run(player, crate);}
	
	//Getters + Setters
	public Inventory getInv(String name) {
		Inventory clone = Bukkit.createInventory(null, inv.getSize(), name);
		for (int i = 0; i < inv.getSize(); i++) {
			if (inv.getItem(i) == null) continue;
			clone.setItem(i, inv.getItem(i).clone());
		}
		return clone;
		}

	public void setInv(Inventory inv) {this.inv = inv;}

	public List<Row> getRowList() {return rowList;}

	public void setRowList (List<Row> rowList) {this.rowList = rowList;}

	public long getInitialDelay() {return initialDelay;}

	public void setInitialDelay(long initialDelay) {this.initialDelay = initialDelay;}

	public int getDelay() {return delay;}

	public void setDelay(int delay) {this.delay = delay;}

	public int getDuration() {return duration;}

	public void setDuration(int duration) {this.duration = duration;}

	public int getRandomDurationModifier() {return randomDurationModifier;}

	public void setRandomDurationModifier(int randomDurationModifier) {this.randomDurationModifier = randomDurationModifier;}
	
	public void setOnOpen(Effect onOpen) {this.onOpen = onOpen;}

	public void setOnTick(Effect onTick) {this.onTick = onTick;}

	public void setOnFinish(Effect onFinish) {this.onFinish = onFinish;}
}
