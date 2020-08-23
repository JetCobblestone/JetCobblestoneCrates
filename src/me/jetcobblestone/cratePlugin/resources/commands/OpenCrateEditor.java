package me.jetcobblestone.cratePlugin.resources.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.jetcobblestone.cratePlugin.resources.guis.CrateCreatorGUI;
import me.jetcobblestone.cratePlugin.resources.guis.GUI;
import me.jetcobblestone.cratePlugin.resources.util.Tracker;

//This is a command to open the crate editor
public class OpenCrateEditor implements CommandExecutor{
	
	Tracker tracker = Tracker.getInstance();
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String Label, String[] args) {
		
		Player player = (Player) sender;
		
		tracker.clear(player);
		
		if(CrateCreatorGUI.getInstance().getGuiList().isEmpty() == false) {
			GUI gui = CrateCreatorGUI.getInstance().getGuiList().get(0);
			gui.open(player);
		}
		return true;
	}
}
