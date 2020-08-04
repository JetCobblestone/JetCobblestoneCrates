package me.jetcobblestone.cratePlugin;

import org.bukkit.plugin.java.JavaPlugin;

import me.jetcobblestone.cratePlugin.resources.listeners.CrateOpen;
import me.jetcobblestone.cratePlugin.resources.listeners.CreationGUI;
import me.jetcobblestone.cratePlugin.resources.saver.Saver;

public class CratePlugin extends JavaPlugin {
	
	private static CratePlugin instance;
	private Saver saver;
	
	
	public static CratePlugin getInstance() {
		return instance;
	}

	@Override
	public void onEnable() {
		instance = this;
		saver = Saver.getInstance();
		getCommand("CrateGUI").setExecutor(new me.jetcobblestone.cratePlugin.resources.commands.OpenCrateEditor());
		
		getServer().getPluginManager().registerEvents(new CrateOpen(), this);
		getServer().getPluginManager().registerEvents(new CreationGUI(), this);
		saver.loadCrates();
	}
	
	@Override
	public void onDisable() {
		saver.saveCrates();
	}

}