package me.jetcobblestone.cratePlugin.resources.util;

import java.util.List;
import java.util.Random;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import me.jetcobblestone.cratePlugin.CratePlugin;
import me.jetcobblestone.cratePlugin.resources.items.Crate;

public class CrateScroller{	
	
	private ScrollerSetting settings;
	
	public CrateScroller(ScrollerSetting settings){
		this.settings = settings;
	}
	
	public void roll(Crate crate, Player player) {
		
		Inventory inv = settings.getInv(crate.getName());
		List<Row> rowList = settings.getRowList();
		
		settings.runOnOpen(player, crate);
		player.openInventory(inv);

		new BukkitRunnable() {
			
			int timer = 0;
			int delay = settings.getDelay();
			int duration = settings.getDuration() + new Random().nextInt(settings.getRandomDurationModifier());
			int durationReferenence = duration;
			
			@Override
			public void run() {
				if (duration == 0) {
					settings.runOnFinish(player, crate);
					for (Row row : rowList) {
						if (inv.getItem((row.getRow()*9) + 4) != null) {
							player.getInventory().addItem(inv.getItem((row.getRow()*9) + 4));
						}
					}
					cancel();
				}

				if (timer == delay) {
					settings.runOnTick(player, crate);
					timer = 0;
					if ((double) duration / (double) durationReferenence < 0.4) {
						delay++;
					}
					for (Row row : rowList) {
						int low = row.getRow() * 9;
						int high = row.getRow() * 9;
						int modifier = 1;
						if (row.getDirection()) {high += 8;}
						else {low += 8; modifier = -1;}
						
						for(int i = low; i != high; i += modifier) {
							if (i == high-modifier) {inv.setItem(high, (crate.randomItem()));}	
							if (inv.getItem(i+modifier) == null) continue;
							inv.setItem(i, inv.getItem(i+modifier));
						}
					}
				}
				timer++;
				duration--;
			}
			//runnable
		}.runTaskTimer(CratePlugin.getInstance(), settings.getInitialDelay(), 1l);
	}
}