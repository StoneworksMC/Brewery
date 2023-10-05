package com.dre.brewery.utility;

import com.dre.brewery.P;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Levelled;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Cauldron;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Tree;
import org.bukkit.material.Wood;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.EnumSet;
import java.util.Set;

import static com.dre.brewery.BCauldron.EMPTY;
import static com.dre.brewery.BCauldron.FULL;
import static com.dre.brewery.BCauldron.SOME;

@SuppressWarnings("JavaReflectionMemberAccess")
public class LegacyUtil {

	private static Method GET_MATERIAL;
	private static Method GET_BLOCK_TYPE_ID_AT;
	private static Method SET_DATA;

	public static boolean NewNbtVer;

	static {
		// <= 1.12.2 methods
		// These will be rarely used
		try {
			GET_MATERIAL = Material.class.getDeclaredMethod("getMaterial", int.class);
			GET_BLOCK_TYPE_ID_AT = World.class.getDeclaredMethod("getBlockTypeIdAt", Location.class);
		} catch (NoSuchMethodException | SecurityException ignored) {
		}
		try {
			SET_DATA = Class.forName(Bukkit.getServer().getClass().getPackage().getName() + ".block.CraftBlock").getDeclaredMethod("setData", byte.class);
		} catch (ClassNotFoundException | NoSuchMethodException | SecurityException ignored) {
		}

		Set<Material> planks = EnumSet.noneOf(Material.class);
		for (Material m : Material.values()) {
			if (m.name().endsWith("PLANKS")) {
				planks.add(m);
			}
		}
		planks.add(get("QUARTZ_BLOCK"));
		planks.add(get("MAGMA_BLOCK"));
		planks.add(get("PRISMARINE"));
		planks.add(get("BROWN_MUSHROOM_BLOCK"));
		planks.add(get("HORN_CORAL_BLOCK"));
		planks.add(get("SAND"));
		planks.add(get("TUBE_CORAL_BLOCK"));
		planks.add(get("MELON"));
		planks.add(get("RED_SAND"));		
		planks.add(get("BUBBLE_CORAL_BLOCK"));
		planks.add(get("RED_MUSHROOM_BLOCK"));					
		PLANKS = planks;

		Set<Material> woodStairs = EnumSet.noneOf(Material.class);
		Material[] gotStairs = {
			get("OAK_STAIRS", "WOOD_STAIRS"),
			get("SPRUCE_STAIRS", "SPRUCE_WOOD_STAIRS"),
			get("BIRCH_STAIRS", "BIRCH_WOOD_STAIRS"),
			get("JUNGLE_STAIRS", "JUNGLE_WOOD_STAIRS"),
			get("ACACIA_STAIRS"),
			get("DARK_OAK_STAIRS"),
			get("CRIMSON_STAIRS"),
			get("WARPED_STAIRS"),
			get("QUARTZ_STAIRS"),
			get("BLACKSTONE_STAIRS"),
			get("PRISMARINE_BRICK_STAIRS"),
			get("RED_NETHER_BRICK_STAIRS"),
			get("CHERRY_STAIRS"),
			get("MANGROVE_STAIRS"),
			get("BAMBOO_STAIRS"),		
			get("SMOOTH_QUARTZ_STAIRS"),	
			get("WAXED_EXPOSED_CUT_COPPER_STAIRS"),
			get("DARK_PRISMARINE_STAIRS"),
			get("SANDSTONE_STAIRS"),
			get("PURPUR_STAIRS"),
			get("SMOOTH_RED_SANDSTONE_STAIRS"),
			get("SMOOTH_SANDSTONE_STAIRS"),
			get("POLISHED_GRANITE_STAIRS")

			
		};
		for (Material stair : gotStairs) {
			if (stair != null) {
				woodStairs.add(stair);
			}
		}
		WOOD_STAIRS = woodStairs;


		Set<Material> fences = EnumSet.noneOf(Material.class);
		for (Material m : Material.values()) {
			if (m.name().endsWith("FENCE")) {
				fences.add(m);
			}
			if (m.name().endsWith("WALL")) {
				fences.add(m);
			}
		}
		FENCES = fences;
	}

	public static final Material WATER_CAULDRON = get("WATER_CAULDRON");
	public static final Material MAGMA_BLOCK = get("MAGMA_BLOCK", "MAGMA");
	public static final Material CAMPFIRE = get("CAMPFIRE");
	public static final Material SOUL_CAMPFIRE = get("SOUL_CAMPFIRE");
	public static final Material SOUL_FIRE = get("SOUL_FIRE");
	public static final Material CLOCK = get("CLOCK", "WATCH");
	public static final Set<Material> PLANKS;
	public static final Set<Material> WOOD_STAIRS;
	public static final Set<Material> FENCES;

	// Materials removed in 1.13
	public static final Material STATIONARY_LAVA = get("STATIONARY_LAVA");
	public static final Material SIGN_POST = get("SIGN_POST");
	public static final Material WOOD = get("WOOD");

	private static Material get(String name) {
		try {
			return Material.valueOf(name);
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	private static Material get(String newName, String oldName) {
		try {
			return Material.valueOf(P.use1_13 ? newName : oldName);
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	public static boolean isWoodPlanks(Material type) {
		return (WOOD != null && type == WOOD) || PLANKS.contains(type);
	}

	public static boolean isWoodStairs(Material type) {
		return WOOD_STAIRS.contains(type);
	}

	public static boolean isFence(Material type) {
		return FENCES.contains(type);
	}

	public static boolean isSign(Material type) {
		return type.name().endsWith("SIGN") || (!P.use1_13 && type == SIGN_POST);
	}

	public static boolean isCauldronHeatsource(Block block) {
		Material type = block.getType();
		return type != null && (type == Material.FIRE || type == SOUL_FIRE || type == MAGMA_BLOCK || litCampfire(block) || isLava(type));
	}

	// LAVA and STATIONARY_LAVA are merged as of 1.13
	public static boolean isLava(Material type) {
		return type == Material.LAVA || (!P.use1_13 && type == STATIONARY_LAVA);
	}

	public static boolean litCampfire(Block block) {
		if (block.getType() == CAMPFIRE || block.getType() == SOUL_CAMPFIRE) {
			BlockData data = block.getBlockData();
			if (data instanceof org.bukkit.block.data.Lightable) {
				return ((org.bukkit.block.data.Lightable) data).isLit();
			}
		}
		return false;
	}

	public static boolean isBottle(Material type) {
		if (type == Material.POTION) return true;
		if (!P.use1_9) return false;
		if (type == Material.LINGERING_POTION || type == Material.SPLASH_POTION) return true;
		if (!P.use1_13) return false;
		if (type == Material.EXPERIENCE_BOTTLE) return true;
		if (type.name().equals("DRAGON_BREATH")) return true;
		return type.name().equals("HONEY_BOTTLE");
	}

	public static boolean areStairsInverted(Block block) {
		if (!P.use1_13) {
			@SuppressWarnings("deprecation")
			MaterialData data = block.getState().getData();
			return data instanceof org.bukkit.material.Stairs && (((org.bukkit.material.Stairs) data).isInverted());
		} else {
			BlockData data = block.getBlockData();
			return data instanceof org.bukkit.block.data.type.Stairs && ((org.bukkit.block.data.type.Stairs) data).getHalf() == org.bukkit.block.data.type.Stairs.Half.TOP;
		}
	}

	public static byte getWoodType(Block wood) throws NoSuchFieldError, NoClassDefFoundError {

		if (P.use1_13 || isWoodStairs(wood.getType())) {
			String material = wood.getType().name();
			//Uncomment line below to see barrel types in console when they are made. Helps with Debugging barrels that arent working
			//P.p.errorLog(material); 
			if (material.startsWith("OAK")) {
				return 2;
			} else if (material.startsWith("SPRUCE")) {
				return 4;
			} else if (material.startsWith("BIRCH")) {
				return 1;
			} else if (material.startsWith("JUNGLE")) {
				return 3;
			} else if (material.startsWith("ACACIA")) {
				return 5;
			} else if (material.startsWith("DARK_OAK")) {
				return 6;
			} else if (material.startsWith("CRIMSON")) {
				return 7;
			} else if (material.startsWith("WARPED")) {
				return 8;
			} else if (material.startsWith("QUARTZ")) {
				return 9;
			} else if (material.startsWith("GILDED")) {
				return 10;
			} else if (material.startsWith("BLACKSTONE_STAIRS")) {
				return 10;
			} else if (material.startsWith("PRISMARINE")) {
				return 11;
			} else if (material.startsWith("PRISMARINE_BRICK_STAIRS")) {
				return 11;
			} else if (material.startsWith("SAND")) {
				return 12;
			} else if (material.startsWith("SMOOTH_SANDSTONE_STAIRS")) {
				return 12;
			} else if (material.startsWith("MAGMA_BLOCK")) {
				return 13;
			} else if (material.startsWith("RED_NETHER_BRICK_STAIRS")) {
				return 13;
			} else if (material.startsWith("BROWN_MUSHROOM_BLOCK")) {
				return 14;
			} else if (material.startsWith("WAXED_EXPOSED_CUT_COPPER_STAIRS")) {
				return 14;
			} else if (material.startsWith("RED_MUSHROOM_BLOCK")) {
				return 15;
			} else if (material.startsWith("SMOOTH_QUARTZ_STAIRS")) {
				return 15;
			} else if (material.startsWith("MELON")) {
				return 16;
			} else if (material.startsWith("POLISHED_GRANITE_STAIRS")) {
				return 16;
			} else if (material.startsWith("TUBE_CORAL_BLOCK")) {
				return 17;
			} else if (material.startsWith("DARK_PRISMARINE_STAIRS")) {
				return 17;
			} else if (material.startsWith("HORN_CORAL_BLOCK")) {
				return 18;
			} else if (material.startsWith("SANDSTONE_STAIRS")) {
				return 18;
			} else if (material.startsWith("BUBBLE_CORAL_BLOCK")) {
				return 19;
			} else if (material.startsWith("PURPUR_STAIRS")) {
				return 19;
			} else if (material.startsWith("RED_SAND")) {
				return 20;
			} else if (material.startsWith("SMOOTH_RED_SANDSTONE_STAIRS")) {
				return 20;
			} else if (material.startsWith("MANGROVE")) {
				return 21;
			} else if (material.startsWith("CHERRY")) {
				return 22;
			} else if (material.startsWith("BAMBOO")) {
				return 23;
			} else {
				return 0;
			}

		} else {
			@SuppressWarnings("deprecation")
			MaterialData data = wood.getState().getData();
			TreeSpecies woodType;
			if (data instanceof Tree) {
				woodType = ((Tree) data).getSpecies();
			} else if (data instanceof Wood) {
				woodType = ((Wood) data).getSpecies();
			} else {
				return 0;
			}

			switch (woodType) {
				case GENERIC:
					return 2;
				case REDWOOD:
					return 4;
				case BIRCH:
					return 1;
				case JUNGLE:
					return 3;
				case ACACIA:
					return 5;
				case DARK_OAK:
					return 6;
				default:
					return 0;
			}
		}
	}

	/**
	 * Test if this Material Type is a Cauldron filled with water, or any cauldron in 1.16 and lower
	 */
	public static boolean isWaterCauldron(Material type) {
		return WATER_CAULDRON != null ? type == WATER_CAULDRON : type == Material.CAULDRON;
	}

	/**
	 * Get The Fill Level of a Cauldron Block, 0 = empty, 1 = something in, 2 = full
	 *
	 * @return 0 = empty, 1 = something in, 2 = full
	 */
	public static byte getFillLevel(Block block) {
		if (!isWaterCauldron(block.getType())) {
			return EMPTY;
		}

		if (P.use1_13) {
			Levelled cauldron = ((Levelled) block.getBlockData());
			if (cauldron.getLevel() == 0) {
				return EMPTY;
			} else if (cauldron.getLevel() == cauldron.getMaximumLevel()) {
				return FULL;
			} else {
				return SOME;
			}

		} else {
			Cauldron cauldron = (Cauldron) block.getState().getData();
			if (cauldron.isEmpty()) {
				return EMPTY;
			} else if (cauldron.isFull()) {
				return FULL;
			} else {
				return SOME;
			}
		}
	}

	/**
	 * only used to convert a very old Datafile or config from a very old version
	 */
	public static Material getMaterial(int id) {
		try {
			return GET_MATERIAL != null ? (Material) GET_MATERIAL.invoke(null, id) : null;
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			return null;
		}
	}

	// Only used for very old versions of LogBlock
	public static int getBlockTypeIdAt(Location location) {
		try {
			return GET_BLOCK_TYPE_ID_AT != null ? (int) GET_BLOCK_TYPE_ID_AT.invoke(location.getWorld(), location) : 0;
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			return 0;
		}
	}

	// Setting byte data to blocks for older versions
	public static void setData(Block block, byte data) {
		try {
			SET_DATA.invoke(block, data);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ignored) {
		}
	}

	/**
	 * MC 1.13 uses a different NBT API than the newer versions..
	 * We decide here which to use, the new or the old
	 *
	 * @return true if we can use nbt at all
	 */
	public static boolean initNbt() {
		try {
			Class.forName("org.bukkit.persistence.PersistentDataContainer");
			NewNbtVer = true;
			return true;
		} catch (ClassNotFoundException e) {
			try {
				Class.forName("org.bukkit.inventory.meta.tags.CustomItemTagContainer");
				NewNbtVer = false;
				return true;
			} catch (ClassNotFoundException ex) {
				NewNbtVer = false;
				return false;
			}
		}
	}

	@SuppressWarnings("deprecation")
	public static void writeBytesItem(byte[] bytes, ItemMeta meta, NamespacedKey key) {
		if (NewNbtVer) {
			meta.getPersistentDataContainer().set(key, org.bukkit.persistence.PersistentDataType.BYTE_ARRAY, bytes);
		} else {
			meta.getCustomTagContainer().setCustomTag(key, org.bukkit.inventory.meta.tags.ItemTagType.BYTE_ARRAY, bytes);
		}
	}

	@SuppressWarnings("deprecation")
	public static byte[] readBytesItem(ItemMeta meta, NamespacedKey key) {
		if (NewNbtVer) {
			return meta.getPersistentDataContainer().get(key, org.bukkit.persistence.PersistentDataType.BYTE_ARRAY);
		} else {
			return meta.getCustomTagContainer().getCustomTag(key, org.bukkit.inventory.meta.tags.ItemTagType.BYTE_ARRAY);
		}
	}

	@SuppressWarnings("deprecation")
	public static boolean hasBytesItem(ItemMeta meta, NamespacedKey key) {
		if (NewNbtVer) {
			return meta.getPersistentDataContainer().has(key, org.bukkit.persistence.PersistentDataType.BYTE_ARRAY);
		} else {
			return meta.getCustomTagContainer().hasCustomTag(key, org.bukkit.inventory.meta.tags.ItemTagType.BYTE_ARRAY);
		}
	}

}
