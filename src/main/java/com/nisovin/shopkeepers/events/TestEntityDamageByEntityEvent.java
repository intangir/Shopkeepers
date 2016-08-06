package com.nisovin.shopkeepers.events;


import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 * A test event that is called when we check if something can be interacted with (Chests)
 */
public class TestEntityDamageByEntityEvent extends EntityDamageByEntityEvent
{
	public TestEntityDamageByEntityEvent(Entity damager, Entity damagee, EntityDamageEvent.DamageCause cause, double damage)
	{
		super(damager, damagee, cause, damage);
	}
}
