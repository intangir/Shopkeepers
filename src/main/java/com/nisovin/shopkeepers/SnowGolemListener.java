package com.nisovin.shopkeepers;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Snowman;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class SnowGolemListener implements Listener {

	final ShopkeepersPlugin plugin;
	
	public SnowGolemListener(ShopkeepersPlugin plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	void onEntityInteract(PlayerInteractEntityEvent event) {
		if (event.getRightClicked() instanceof Snowman) {
			LivingEntity entity = (LivingEntity)event.getRightClicked();
			ShopkeepersPlugin.debug("Player " + event.getPlayer().getName() + " is interacting with snow golem at " + entity.getLocation());
			Shopkeeper shopkeeper = plugin.activeShopkeepers.get("entity" + entity.getEntityId());
			if (event.isCancelled()) {
				ShopkeepersPlugin.debug("  Cancelled by another plugin");
			} else if (shopkeeper != null) {
				plugin.handleShopkeeperInteraction(event.getPlayer(), shopkeeper);
				event.setCancelled(true);
			} else if (entity.hasMetadata("NPC")) {
				// ignore any interaction with citizens2 NPCs
				return;
			} else if (Settings.hireOtherSnowGolems) {
				// allow hiring of other snowgolems
				ShopkeepersPlugin.debug("  Non-shopkeeper, possible hire");
				if (plugin.handleHireOtherNpc(event.getPlayer(), entity)) {
					// hiring was successful -> prevent trading
					ShopkeepersPlugin.debug("  Non-shopkeeper, possible hire.. success -> possible trade prevented");
					event.setCancelled(true); 
				} else {
					// hiring was not successful -> no preventing of normal villager trading
					ShopkeepersPlugin.debug("  Non-shopkeeper, possible hire.. failed");
				}
			} else {
				ShopkeepersPlugin.debug("  Non-shopkeeper");
			}
		}
	}

	@EventHandler
	void onTarget(EntityTargetEvent event) {
		if (event.getEntityType() == EntityType.SNOWMAN && plugin.isShopkeeper(event.getEntity())) {
			event.setCancelled(true);
		}
	}
	
}
