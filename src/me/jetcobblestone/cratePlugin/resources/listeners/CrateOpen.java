package me.jetcobblestone.cratePlugin.resources.listeners;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import me.jetcobblestone.cratePlugin.resources.items.Crate;

//This listener will detect when a player right clicks an ItemStack belonging to a crate in game.

 public class CrateOpen implements Listener{
	
	private final ArrayList<Player> crateOpenList = new ArrayList<Player>();
	
	@EventHandler
	public void onCrateClick(PlayerInteractEvent event) {
		
		Player commander = event.getPlayer();
		if (commander.getInventory().getItemInMainHand().hasItemMeta() == false) {return;}
		if (commander.getInventory().getItemInMainHand().getItemMeta().getPersistentDataContainer().isEmpty()) return;
		if (commander.getInventory().getItemInMainHand().getItemMeta().getPersistentDataContainer().has(Crate.getKey(), PersistentDataType.DOUBLE)) {
			if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
				event.setCancelled(true);
				
				ItemStack crateInHand = commander.getInventory().getItemInMainHand();
				Crate crate = Crate.findCrate(crateInHand);
				
				crateInHand.setAmount(crateInHand.getAmount() -1);
				if (crate == null) return;
				
				crateOpenList.add(commander);
				crate.open(commander);
				commander.updateInventory();
				
				return;
			}
		}
	}
	
	@EventHandler
	public void onCrateInventoryClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		if (crateOpenList.contains(player)) {		
			if (event.getClickedInventory() != player.getInventory()) {
				event.setCancelled(true);
				return;
			}
			if (event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT || event.getClick() == ClickType.DOUBLE_CLICK) {
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onCrateInventoryClose(InventoryCloseEvent event) {
		Player player = (Player) event.getPlayer();
		
		if (crateOpenList.contains(player)) {
			crateOpenList.remove(player);
		}
	}
}
