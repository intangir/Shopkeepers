package com.nisovin.shopkeepers.compat.v1_9_R1;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftVillager;
import org.bukkit.craftbukkit.v1_9_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_9_R1.inventory.CraftInventoryMerchant;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantInventory;

import net.minecraft.server.v1_9_R1.*;

import com.nisovin.shopkeepers.Shopkeeper;
import com.nisovin.shopkeepers.compat.api.NMSCallProvider;

public final class NMSHandler implements NMSCallProvider {

	@Override
	public String getVersionId() {
		return "1_9_R1";
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean openTradeWindow(String name, List<org.bukkit.inventory.ItemStack[]> recipes, Player player) {
		try {
			EntityVillager villager = new EntityVillager(((CraftPlayer) player).getHandle().world, 0);
			// custom name:
			if (name != null && !name.isEmpty()) {
				villager.setCustomName(name);
			}
			// career level (to prevent trade progression):
			Field careerLevelField = EntityVillager.class.getDeclaredField("bI");
			careerLevelField.setAccessible(true);
			careerLevelField.set(villager, 10);

			// recipes:
			Field recipeListField = EntityVillager.class.getDeclaredField("trades");
			recipeListField.setAccessible(true);
			MerchantRecipeList recipeList = (MerchantRecipeList) recipeListField.get(villager);
			if (recipeList == null) {
				recipeList = new MerchantRecipeList();
				recipeListField.set(villager, recipeList);
			}
			recipeList.clear();
			for (org.bukkit.inventory.ItemStack[] recipe : recipes) {
				recipeList.add(createMerchantRecipe(recipe[0], recipe[1], recipe[2]));
			}

			// this will trigger the "create child" code of minecraft when the player is holding a spawn egg in his
			// hands,
			// but bypasses craftbukkits interact events and therefore removes the spawn egg from the players hands
			// result: we have to prevent openTradeWindow if the shopkeeper entity is being clicking with a spawn egg in
			// hands
			villager.setTradingPlayer(((CraftPlayer) player).getHandle()); // set trading player
			((CraftPlayer) player).getHandle().openTrade(villager); // open trade window
			((CraftPlayer) player).getHandle().b(StatisticList.H); // minecraft statistics

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean openTradeWindow(Shopkeeper shopkeeper, Player player) {
		return openTradeWindow(shopkeeper.getName(), shopkeeper.getRecipes(), player);
	}

	@Override
	public ItemStack[] getUsedTradingRecipe(Inventory merchantInventory) {
		try {
			InventoryMerchant handle = (InventoryMerchant) ((CraftInventoryMerchant) merchantInventory).getInventory();
			MerchantRecipe merchantRecipe = handle.getRecipe();
			ItemStack[] recipe = new ItemStack[3];
			recipe[0] = merchantRecipe.getBuyItem1() != null ? CraftItemStack.asBukkitCopy(merchantRecipe.getBuyItem1()) : null;
			recipe[1] = merchantRecipe.getBuyItem2() != null ? CraftItemStack.asBukkitCopy(merchantRecipe.getBuyItem2()) : null;
			recipe[2] = merchantRecipe.getBuyItem3() != null ? CraftItemStack.asBukkitCopy(merchantRecipe.getBuyItem3()) : null;
			return recipe;
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public void overwriteLivingEntityAI(LivingEntity entity) {
		try {
			EntityLiving mcLivingEntity = ((CraftLivingEntity) entity).getHandle();

			// make goal selector items accessible:
			Field bField = PathfinderGoalSelector.class.getDeclaredField("b");
			bField.setAccessible(true);
			Field cField = PathfinderGoalSelector.class.getDeclaredField("c");
			cField.setAccessible(true);

			// overwrite goal selector:
			Field goalsField = EntityInsentient.class.getDeclaredField("goalSelector");
			goalsField.setAccessible(true);
			PathfinderGoalSelector goals = (PathfinderGoalSelector) goalsField.get(mcLivingEntity);

			// clear old goals:
			Set<?> goals_b = (Set<?>) bField.get(goals);
			goals_b.clear();
			Set<?> goals_c = (Set<?>) cField.get(goals);
			goals_c.clear();

			// add new goals:
			goals.a(0, new PathfinderGoalFloat((EntityInsentient) mcLivingEntity));
			goals.a(1, new PathfinderGoalLookAtPlayer((EntityInsentient) mcLivingEntity, EntityHuman.class, 12.0F, 1.0F));

			// overwrite target selector:
			Field targetsField = EntityInsentient.class.getDeclaredField("targetSelector");
			targetsField.setAccessible(true);
			PathfinderGoalSelector targets = (PathfinderGoalSelector) targetsField.get(mcLivingEntity);

			// clear old goals:
			Set<?> targets_b = (Set<?>) bField.get(targets);
			targets_b.clear();
			Set<?> targets_c = (Set<?>) cField.get(targets);
			targets_c.clear();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void overwriteVillagerAI(LivingEntity villager) {
		try {
			EntityVillager mcVillagerEntity = ((CraftVillager) villager).getHandle();

			// make goal selector items accessible:
			Field bField = PathfinderGoalSelector.class.getDeclaredField("b");
			bField.setAccessible(true);
			Field cField = PathfinderGoalSelector.class.getDeclaredField("c");
			cField.setAccessible(true);

			// overwrite goal selector:
			Field goalsField = EntityInsentient.class.getDeclaredField("goalSelector");
			goalsField.setAccessible(true);
			PathfinderGoalSelector goals = (PathfinderGoalSelector) goalsField.get(mcVillagerEntity);

			// clear old goals:
			Set<?> goals_b = (Set<?>) bField.get(goals);
			goals_b.clear();
			Set<?> goals_c = (Set<?>) cField.get(goals);
			goals_c.clear();

			// add new goals:
			goals.a(0, new PathfinderGoalFloat((EntityInsentient) mcVillagerEntity));
			goals.a(1, new PathfinderGoalLookAtPlayer((EntityInsentient) mcVillagerEntity, EntityHuman.class, 12.0F, 1.0F));

			goals.a(0, new PathfinderGoalFloat(mcVillagerEntity));
			goals.a(1, new PathfinderGoalTradeWithPlayer(mcVillagerEntity));
			goals.a(1, new PathfinderGoalLookAtTradingPlayer(mcVillagerEntity));
			goals.a(2, new PathfinderGoalLookAtPlayer(mcVillagerEntity, EntityHuman.class, 12.0F, 1.0F));

			// overwrite target selector:
			Field targetsField = EntityInsentient.class.getDeclaredField("targetSelector");
			targetsField.setAccessible(true);
			PathfinderGoalSelector targets = (PathfinderGoalSelector) targetsField.get(mcVillagerEntity);

			// clear old goals:
			Set<?> targets_b = (Set<?>) bField.get(targets);
			targets_b.clear();
			Set<?> targets_c = (Set<?>) cField.get(targets);
			targets_c.clear();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public int getMaxVillagerProfession() {
		return 4;
	}

	@Override
	public void setVillagerProfession(Villager villager, int profession) {
		((CraftVillager) villager).getHandle().setProfession(profession);
	}

	@Override
	public void setEntitySilent(org.bukkit.entity.Entity entity, boolean silent) {
		Entity mcEntity = ((CraftEntity) entity).getHandle();
		mcEntity.c(silent);
	}

	@Override
	public void setNoAI(LivingEntity bukkitEntity) {
		net.minecraft.server.v1_9_R1.EntityInsentient nmsEntity = (EntityInsentient) ((CraftEntity) bukkitEntity).getHandle();
		nmsEntity.m(true);
	}

	private MerchantRecipe createMerchantRecipe(org.bukkit.inventory.ItemStack item1, org.bukkit.inventory.ItemStack item2, org.bukkit.inventory.ItemStack item3) {
		MerchantRecipe recipe = new MerchantRecipe(convertItemStack(item1), convertItemStack(item2), convertItemStack(item3));
		try {
			// max uses:
			Field maxUsesField = MerchantRecipe.class.getDeclaredField("maxUses");
			maxUsesField.setAccessible(true);
			maxUsesField.set(recipe, 10000);

			// reward exp:
			Field rewardExpField = MerchantRecipe.class.getDeclaredField("rewardExp");
			rewardExpField.setAccessible(true);
			rewardExpField.set(recipe, false);
		} catch (Exception e) {
		}
		return recipe;
	}

	private net.minecraft.server.v1_9_R1.ItemStack convertItemStack(org.bukkit.inventory.ItemStack item) {
		if (item == null) return null;
		return org.bukkit.craftbukkit.v1_9_R1.inventory.CraftItemStack.asNMSCopy(item);
	}

	private NBTTagCompound getItemTag(net.minecraft.server.v1_9_R1.ItemStack itemStack) {
		if (itemStack == null) return null;
		try {
			Field tag = itemStack.getClass().getDeclaredField("tag");
			tag.setAccessible(true);
			return (NBTTagCompound) tag.get(itemStack);
		} catch (NoSuchFieldException e) {
			return null;
		} catch (IllegalAccessException e2) {
			return null;
		}
	}

	private void setItemTag(net.minecraft.server.v1_9_R1.ItemStack itemStack, NBTTagCompound newTag) {
		if (itemStack == null) return;
		try {
			Field tag = itemStack.getClass().getDeclaredField("tag");
			tag.setAccessible(true);
			tag.set(itemStack, newTag);
		} catch (NoSuchFieldException e) {
		} catch (IllegalAccessException e2) {
		}
	}

	// TODO check if bukkit 1.9 saves item attributes on it's own now

	@Override
	public org.bukkit.inventory.ItemStack loadItemAttributesFromString(org.bukkit.inventory.ItemStack item, String data) {
		NBTTagList list = new NBTTagList();
		String[] attrs = data.split(";");
		for (String s : attrs) {
			if (!s.isEmpty()) {
				String[] attrData = s.split(",");
				NBTTagCompound attr = new NBTTagCompound();
				attr.setString("Name", attrData[0]);
				attr.setString("AttributeName", attrData[1]);
				attr.setDouble("Amount", Double.parseDouble(attrData[2]));
				attr.setInt("Operation", Integer.parseInt(attrData[3]));
				attr.setLong("UUIDLeast", Long.parseLong(attrData[4]));
				attr.setLong("UUIDMost", Long.parseLong(attrData[5]));
				// MC 1.9 addition:
				if (attrData.length >= 7) {
					attr.setString("Slot", attrData[6]);
				}
				list.add(attr);
			}
		}
		net.minecraft.server.v1_9_R1.ItemStack i = CraftItemStack.asNMSCopy(item);
		NBTTagCompound tag = this.getItemTag(i);
		if (tag == null) {
			tag = new NBTTagCompound();
			this.setItemTag(i, tag);
		}
		tag.set("AttributeModifiers", list);
		return CraftItemStack.asBukkitCopy(i);
	}

	@Override
	public String saveItemAttributesToString(org.bukkit.inventory.ItemStack item) {
		net.minecraft.server.v1_9_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
		if (nmsItem == null) return null;
		NBTTagCompound tag = this.getItemTag(nmsItem);
		if (tag == null || !tag.hasKey("AttributeModifiers")) {
			return null;
		}
		String data = "";
		NBTTagList list = tag.getList("AttributeModifiers", 10);
		for (int i = 0; i < list.size(); i++) {
			NBTTagCompound attr = list.get(i);
			data += attr.getString("Name") + ","
					+ attr.getString("AttributeName") + ","
					+ attr.getDouble("Amount") + ","
					+ attr.getInt("Operation") + ","
					+ attr.getLong("UUIDLeast") + ","
					+ attr.getLong("UUIDMost");
			// MC 1.9 addition:
			String slot = attr.getString("Slot");
			if (slot != null && !slot.isEmpty()) {
				data += "," + slot;
			}
			data += ";";
		}
		return data;
	}

	@Override
	public boolean supportsPlayerUUIDs() {
		return true;
	}

	@Override
	public UUID getUUID(OfflinePlayer player) {
		return player.getUniqueId();
	}

	@Override
	public OfflinePlayer getOfflinePlayer(UUID uuid) {
		return Bukkit.getOfflinePlayer(uuid);
	}

	@Override
	public boolean isMainHandInteraction(PlayerInteractEvent event) {
		return event.getHand() == EquipmentSlot.HAND;
	}

	@Override
	public boolean isMainHandInteraction(PlayerInteractEntityEvent event) {
		return event.getHand() == EquipmentSlot.HAND;
	}
}
