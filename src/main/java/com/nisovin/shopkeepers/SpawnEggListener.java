package com.nisovin.shopkeepers;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.nisovin.shopkeepers.compat.NMSManager;

/*
 * Just converts the old spawn eggs into the new shopcreation item when they try to use one
 */

class SpawnEggListener implements Listener {

	private final ShopkeepersPlugin plugin;

	SpawnEggListener(ShopkeepersPlugin plugin) {
		this.plugin = plugin;
	}

	@EventHandler(ignoreCancelled = true)
	void onPlayerInteract(PlayerInteractEvent event) {
		if(event.getMaterial() == Material.MONSTER_EGG && !event.getPlayer().isOp() && event.getPlayer().getGameMode() != GameMode.CREATIVE) {
			// this magic string is apparently a villager egg for some reason..
			if(event.getItem().toString().contains("internal=H4sIAAAAAAAAAONiYOBi4HTNK8ksqQxJTOdgYMpMYeAIy8zJSUxPLWJgAADlEwUxIAAAAA==")) {
				event.setCancelled(true);

				if(!NMSManager.getProvider().isMainHandInteraction(event)) {
					return;
				}

				final Player player = event.getPlayer();
				final ItemStack newItems = Settings.createCreationItem();
				int amount = event.getItem().getAmount();
				newItems.setAmount(amount);

				Utils.sendMessage(player, "replacing " + amount + " villager egg(s) with " + amount + " " + Settings.shopCreationItemName + "(s)");
				
				Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
					@Override
					public void run() {
						player.setItemInHand(newItems);
					}
				}, 1);
			}
		}
	}
}
