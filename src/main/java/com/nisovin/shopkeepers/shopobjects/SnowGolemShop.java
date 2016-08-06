package com.nisovin.shopkeepers.shopobjects;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class SnowGolemShop extends LivingEntityShop {

	@Override
	public void save(ConfigurationSection config) {
		super.save(config);
		config.set("object", "snowgolem");
	}

	@Override
	protected EntityType getEntityType() {
		return EntityType.SNOWMAN;
	}

	@Override
	public ItemStack getTypeItem() {
		return null;
	}

	@Override
	public void cycleType() {
	}
}
