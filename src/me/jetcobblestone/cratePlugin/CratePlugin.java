package me.jetcobblestone.cratePlugin;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import me.jetcobblestone.cratePlugin.resources.guis.CrateCreatorGUI;
import me.jetcobblestone.cratePlugin.resources.items.Crate;
import me.jetcobblestone.cratePlugin.resources.listeners.CrateOpen;
import me.jetcobblestone.cratePlugin.resources.listeners.CreationGUI;
import me.jetcobblestone.cratePlugin.resources.saver.Saver;
import me.jetcobblestone.cratePlugin.resources.util.ItemFactory;
import me.jetcobblestone.cratePlugin.resources.util.Row;
import me.jetcobblestone.cratePlugin.resources.util.ScrollerSetting;


public class CratePlugin extends JavaPlugin {
	
	private static CratePlugin instance;
	private Saver saver;
	
	
	public static CratePlugin getInstance() {
		return instance;
	}

	@Override
	public void onEnable() {
		instance = this;
		CrateCreatorGUI.getInstance();
		saver = Saver.getInstance();
		
		getCommand("CrateGUI").setExecutor(new me.jetcobblestone.cratePlugin.resources.commands.OpenCrateEditor());
		
		getServer().getPluginManager().registerEvents(new CrateOpen(), this);
		getServer().getPluginManager().registerEvents(new CreationGUI(), this);
		
		saver.loadCrates();
		
		Crate crate = Crate.fromUUIS(UUID.fromString("d46aa48e-929a-42db-ba0d-545116811d32"));
		crate.setSettings(getTestSetting(crate));
	}
	
	@Override
	public void onDisable() {
		saver.saveCrates();
	}
	
	public ScrollerSetting getTestSetting(Crate inputCrate) {
			
		final Inventory inv = Bukkit.createInventory(null, 54, inputCrate.getName());
		final ItemStack pane1 = ItemFactory.createItem(" ", ChatColor.DARK_PURPLE, Material.PURPLE_STAINED_GLASS_PANE, null);
		final ItemStack pane2 = ItemFactory.createItem(" ", ChatColor.LIGHT_PURPLE, Material.PINK_STAINED_GLASS_PANE, null);
		final ItemStack rewardHopper = ItemFactory.createItem("Reward", ChatColor.GOLD, Material.HOPPER, null);
		for (int i = 0; i < inv.getSize(); i++) {
			if (i % 2 == 0) {inv.setItem(i, pane1);}
			else {inv.setItem(i, pane2);}
		}
		inv.setItem(49, rewardHopper);
		
		final ScrollerSetting.Effect onOpen = (player, crate) -> {
			for (Player broadcast : Bukkit.getOnlinePlayers()) {
				broadcast.playSound(broadcast.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 10f, 1f);
			}
			Bukkit.broadcastMessage(ChatColor.AQUA + player.getName() + ChatColor.GOLD + " is opening a " + crate.getName());
		};
		
		ScrollerSetting.Effect onTick = (player, crate)-> {player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 0.5f, 1f);};
		
		ScrollerSetting.Effect onFinish = (player, crate) -> {
			player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
		};
		
		List<Row> rowList = Arrays.asList(new Row(1, true), new Row(2, false), new Row(3, true), new Row(4, false));
		int initialDelay = 40;
		int delay = 2;
		int duration = 200;
		int randomDurationModifier = 100;
			
		ScrollerSetting test = new ScrollerSetting(inv, onOpen, onTick, onFinish, rowList, initialDelay, delay, duration, randomDurationModifier);
		return test;
	}
}
