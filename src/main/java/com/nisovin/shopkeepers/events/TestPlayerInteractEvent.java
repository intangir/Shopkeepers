package com.nisovin.shopkeepers.events;


import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.block.BlockFace;

/**
 * A test event that is called when we check if something can be interacted with (Chests)
 */
public class TestPlayerInteractEvent extends PlayerInteractEvent
{
	public TestPlayerInteractEvent(Player who, Action action, ItemStack item, Block clickedBlock, BlockFace clickedFace)
	{
		super(who, action, item, clickedBlock, clickedFace);
	}
}
