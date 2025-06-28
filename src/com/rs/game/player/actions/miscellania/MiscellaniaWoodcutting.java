package com.rs.game.player.actions.miscellania;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.Animation;
import com.rs.game.World;
import com.rs.game.WorldObject;
import com.rs.game.WorldTile;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.game.player.actions.Action;
import com.rs.utils.Utils;

/**
 * 
 * @author Frostbite<Abstract>
 * @contact<skype;frostbitersps><email;frostbitersps@gmail.com>
 */

public class MiscellaniaWoodcutting extends Action {

	public static enum TreeDefinitions {

		NORMAL(1, 25, 1511, 20, 4, 1341, 8, 0), // TODO

		EVERGREEN(1, 25, 1511, 20, 4, 	57931, 8, 0),

		MAPLE(45, 100, 1517, 20, 4, 31057, 72, 10),
		
		MAHOGANY(50, 105, 6332, 83, 16, 31057, 72, 10),

		TEAK(35, 70, 6333, 60, 4, -1, 51, 15),

		;

		private int level;
		private double xp;
		private int logsId;
		private int logBaseTime;
		private int logRandomTime;
		private int stumpId;
		private int respawnDelay;
		private int randomLifeProbability;

		private TreeDefinitions(int level, double xp, int logsId,
				int logBaseTime, int logRandomTime, int stumpId,
				int respawnDelay, int randomLifeProbability) {
			this.level = level;
			this.xp = xp;
			this.logsId = logsId;
			this.logBaseTime = logBaseTime;
			this.logRandomTime = logRandomTime;
			this.stumpId = stumpId;
			this.respawnDelay = respawnDelay;
			this.randomLifeProbability = randomLifeProbability;
		}

		public int getLevel() {
			return level;
		}

		public double getXp() {
			return xp;
		}

		public int getLogsId() {
			return logsId;
		}

		public int getLogBaseTime() {
			return logBaseTime;
		}

		public int getLogRandomTime() {
			return logRandomTime;
		}

		public int getStumpId() {
			return stumpId;
		}

		public int getRespawnDelay() {
			return respawnDelay;
		}

		public int getRandomLifeProbability() {
			return randomLifeProbability;
		}
	}

	public enum HatchetDefinitions {

		BRONZE(1351, 1, 1, 879),

		IRON(1349, 5, 2, 877),

		STEEL(1353, 5, 3, 875),

		BLACK(1361, 11, 4, 873),

		MITHRIL(1355, 21, 5, 871),

		ADAMANT(1357, 31, 7, 869),

		RUNE(1359, 41, 10, 867),

		DRAGON(6739, 61, 13, 2846),

		INFERNO(13661, 61, 13, 10251);

		private int itemId, levelRequried, axeTime, emoteId;

		private HatchetDefinitions(int itemId, int levelRequried, int axeTime, int emoteId) {
			this.itemId = itemId;
			this.levelRequried = levelRequried;
			this.axeTime = axeTime;
			this.emoteId = emoteId;
		}

		public int getItemId() {
			return itemId;
		}

		public int getLevelRequried() {
			return levelRequried;
		}

		public int getAxeTime() {
			return axeTime;
		}

		public int getEmoteId() {
			return emoteId;
		}
	}

	private WorldObject tree;
	private TreeDefinitions definitions;

	private int emoteId;
	private boolean usingBeaver = false;
	private int axeTime;

	public MiscellaniaWoodcutting(WorldObject tree, TreeDefinitions definitions) {
		this.tree = tree;
		this.definitions = definitions;
	}


	@Override
	public boolean start(Player player) {
		if (!checkAll(player))
			return false;
		player.getPackets().sendGameMessage(usingBeaver ? "Your beaver uses its strong teeth to chop down the tree..." : "You swing your hatchet at the tree...", true);
		setActionDelay(player, getWoodcuttingDelay(player));
		return true;
	}

	private int getWoodcuttingDelay(Player player) {
		int summoningBonus = player.getFamiliar() != null ? (player
				.getFamiliar().getId() == 6808 || player.getFamiliar().getId() == 6807) ? 10
						: 0
						: 0;
		int wcTimer = definitions.getLogBaseTime()
				- (player.getSkills().getLevel(8) + summoningBonus)
				- Utils.getRandom(axeTime);
		if (wcTimer < 1 + definitions.getLogRandomTime())
			wcTimer = 1 + Utils.getRandom(definitions.getLogRandomTime());
		wcTimer /= player.getAuraManager().getWoodcuttingAccurayMultiplier();
		return wcTimer;
	}

	private boolean checkAll(Player player) {
		if (!hasAxe(player)) {
			player.getPackets().sendGameMessage(
					"You need a hatchet to chop down this tree.");
			return false;
		}
		if (!setAxe(player)) {
			player.getPackets().sendGameMessage(
					"You dont have the required level to use that axe.");
			return false;
		}
		if (!hasWoodcuttingLevel(player))
			return false;
		if (!player.getInventory().hasFreeSlots()) {
			player.getPackets().sendGameMessage(
					"Not enough space in your inventory.");
			return false;
		}
		return true;
	}

	private boolean hasWoodcuttingLevel(Player player) {
		if (definitions.getLevel() > player.getSkills().getLevel(8)) {
			player.getPackets().sendGameMessage(
					"You need a woodcutting level of " + definitions.getLevel()
					+ " to chop down this tree.");
			return false;
		}
		return true;
	}

	public static int getAxeAnim(Player player) {
		int level = player.getSkills().getLevel(8);
		int weaponId = player.getEquipment().getWeaponId();
		if (weaponId != -1) {
			switch (weaponId) {
			case 6739: // dragon axe
				if (level >= 61) {
					return 2846;
				}
				break;
			case 1359: // rune axe
				if (level >= 41) {
					return 867;
				}
				break;
			case 1357: // adam axe
				if (level >= 31) {
					return 869;
				}
				break;
			case 1355: // mit axe
				if (level >= 21) {
					return 871;
				}
				break;
			case 1361: // black axe
				if (level >= 11) {
					return 873;
				}
				break;
			case 1353: // steel axe
				if (level >= 6) {
					return 875;
				}
				break;
			case 1349: // iron axe
				return 877;

			case 1351: // bronze axe
				return 879;

			case 13661: // Inferno adze
				if (level >= 61) {
					return 10251;
				}
				break;
			}
		}
		if (player.getInventory().containsOneItem(6739)) {
			if (level >= 61) {
				return 2846;
			}
		}
		if (player.getInventory().containsOneItem(1359)) {
			if (level >= 41) {
				return 867;
			}
		}
		if (player.getInventory().containsOneItem(1357)) {
			if (level >= 31) {
				return 869;
			}
		}
		if (player.getInventory().containsOneItem(1355)) {
			if (level >= 21) {
				return 871;
			}
		}
		if (player.getInventory().containsOneItem(1361)) {
			if (level >= 11) {
				return 873;
			}
		}
		if (player.getInventory().containsOneItem(1353)) {
			if (level >= 6) {
				return 875;
			}
		}
		if (player.getInventory().containsOneItem(1349)) {
			return 877;
		}
		if (player.getInventory().containsOneItem(1351)) {
			return 879;
		}
		if (player.getInventory().containsOneItem(13661)) {
			if (level >= 61) {
				return 10251;
			}
		}
		return -1;
	}

	private boolean setAxe(Player player) {
		int level = player.getSkills().getLevel(8);
		int weaponId = player.getEquipment().getWeaponId();
		if (weaponId != -1) {
			switch (weaponId) {
			case 6739: // dragon axe
				if (level >= 61) {
					emoteId = 2846;
					axeTime = 13;
					return true;
				}
				break;
			case 1359: // rune axe
				if (level >= 41) {
					emoteId = 867;
					axeTime = 10;
					return true;
				}
				break;
			case 1357: // adam axe
				if (level >= 31) {
					emoteId = 869;
					axeTime = 7;
					return true;
				}
				break;
			case 1355: // mit axe
				if (level >= 21) {
					emoteId = 871;
					axeTime = 5;
					return true;
				}
				break;
			case 1361: // black axe
				if (level >= 11) {
					emoteId = 873;
					axeTime = 4;
					return true;
				}
				break;
			case 1353: // steel axe
				if (level >= 6) {
					emoteId = 875;
					axeTime = 3;
					return true;
				}
				break;
			case 1349: // iron axe
				emoteId = 877;
				axeTime = 2;
				return true;
			case 1351: // bronze axe
				emoteId = 879;
				axeTime = 1;
				return true;
			case 13661: // Inferno adze
				if (level >= 61) {
					emoteId = 10251;
					axeTime = 13;
					return true;
				}
				break;
			}
		}
		if (player.getInventory().containsOneItem(6739)) {
			if (level >= 61) {
				emoteId = 2846;
				axeTime = 13;
				return true;
			}
		}
		if (player.getInventory().containsOneItem(1359)) {
			if (level >= 41) {
				emoteId = 867;
				axeTime = 10;
				return true;
			}
		}
		if (player.getInventory().containsOneItem(1357)) {
			if (level >= 31) {
				emoteId = 869;
				axeTime = 7;
				return true;
			}
		}
		if (player.getInventory().containsOneItem(1355)) {
			if (level >= 21) {
				emoteId = 871;
				axeTime = 5;
				return true;
			}
		}
		if (player.getInventory().containsOneItem(1361)) {
			if (level >= 11) {
				emoteId = 873;
				axeTime = 4;
				return true;
			}
		}
		if (player.getInventory().containsOneItem(1353)) {
			if (level >= 6) {
				emoteId = 875;
				axeTime = 3;
				return true;
			}
		}
		if (player.getInventory().containsOneItem(1349)) {
			emoteId = 877;
			axeTime = 2;
			return true;
		}
		if (player.getInventory().containsOneItem(1351)) {
			emoteId = 879;
			axeTime = 1;
			return true;
		}
		if (player.getInventory().containsOneItem(13661)) {
			if (level >= 61) {
				emoteId = 10251;
				axeTime = 13;
				return true;
			}
		}
		if (player.getInventory().containsOneItem(14100)) {
			if (level >= 40) {
				emoteId = 10607;
				axeTime = 8;
				return true;
			}
		}
		return false;

	}

	public static boolean hasAxe(Player player) {
		if (player.getInventory().containsItemsToolBelt(1351, 1349, 1353, 1355, 1357,
				1361, 1359, 6739, 13661))
			return true;
		if(player.getInventory().containsItemToolBelt(1351))
			return true;
		int weaponId = player.getEquipment().getWeaponId();
		if (weaponId == -1)
			return false;
		switch (weaponId) {
		case 1351:// Bronze Axe
		case 1349:// Iron Axe
		case 1353:// Steel Axe
		case 1361:// Black Axe
		case 1355:// Mithril Axe
		case 1357:// Adamant Axe
		case 1359:// Rune Axe
		case 6739:// Dragon Axe
		case 13661: // Inferno adze
		case 14100:
			return true;
		default:
			return false;
		}

	}

	@Override
	public boolean process(Player player) {
		player.setNextAnimation(new Animation(usingBeaver ? 1 : emoteId));
		return checkTree(player);
	}

	private boolean usedDeplateAurora;

	@Override
	public int processWithDelay(Player player) {
		addLog(player);
		if (!usedDeplateAurora && (1 + Math.random()) < player.getAuraManager().getChanceNotDepleteMN_WC()) {
			usedDeplateAurora = true;
		} else if (Utils.getRandom(definitions.getRandomLifeProbability()) == 0) {
			long time = definitions.respawnDelay * 600;
				World.spawnTemporaryObject(new WorldObject(definitions.getStumpId(), tree.getType(), tree.getRotation(), tree.getX(), tree.getY(), tree.getPlane()), time);
			if (tree.getPlane() < 3) {
				WorldObject object = World.getStandartObject(new WorldTile(tree.getX() - 1, tree.getY() - 1, tree.getPlane() + 1));

				if (object == null) {
					object = World.getStandartObject(new WorldTile(tree.getX(), tree
							.getY() - 1, tree.getPlane() + 1));
					if (object == null) {
						object = World.getStandartObject(new WorldTile(tree.getX() - 1,
								tree.getY(), tree.getPlane() + 1));
						if (object == null) {
							object = World.getStandartObject(new WorldTile(tree.getX(),
									tree.getY(), tree.getPlane() + 1));
						}
					}
				}

				if (object != null)
					World.removeTemporaryObject(object, time, false);
			}
			player.setNextAnimation(new Animation(-1));
			return -1;
		}
		if (!player.getInventory().hasFreeSlots()) {
			player.setNextAnimation(new Animation(-1));
			player.getPackets().sendGameMessage(
					"Not enough space in your inventory.");
			return -1;
		}
		return getWoodcuttingDelay(player);
	}


	private void addLog(Player player) {
		double xpBoost = 1.00;
		if (player.getEquipment().getChestId() == 10939)
			xpBoost += 0.008;
		if (player.getEquipment().getLegsId() == 10940)
			xpBoost += 0.006;
		if (player.getEquipment().getHatId() == 10941)
			xpBoost += 0.004;
		if (player.getEquipment().getBootsId() == 10933)
			xpBoost += 0.002;
		if (player.getEquipment().getChestId() == 10939
				&& player.getEquipment().getLegsId() == 10940
				&& player.getEquipment().getHatId() == 10941
				&& player.getEquipment().getBootsId() == 10933)
			xpBoost += 0.005;
		player.getSkills().addXp(8, definitions.getXp() * xpBoost);

		String logName = ItemDefinitions
				.getItemDefinitions(definitions.getLogsId()).getName()
				.toLowerCase();
		if(player.getEquipment().getWeaponId() == 13661 || player.getInventory().containsItemToolBelt(13661, 1)) {
			if(Utils.random(3) == 0) {
				player.getSkills().addXp(Skills.WOODCUTTING, definitions.getXp() / 1.5);
				player.getPackets().sendGameMessage("Your Inferno Adze Incenerates the log for firemaking xp.");
				return;
			}
		}
		if(player.getThrone().getReputation() < 100.0) {
			player.getThrone().addReputation((definitions == TreeDefinitions.MAPLE ? .25 : .50));
		} else {
			player.getInventory().addItem(definitions.getLogsId(), 1);
			player.getPackets().sendGameMessage("You get some " + logName + ".", true);
		}
	}

	private boolean checkTree(Player player) {
		return World.containsObjectWithId(tree, tree.getId());
	}

	@Override
	public void stop(Player player) {
		setActionDelay(player, 3);
	}


	public static HatchetDefinitions getHatchet(Player player, boolean dungeoneering) {
		for (int i = dungeoneering ? 10 : HatchetDefinitions.values().length - 1; i >= (dungeoneering ? 0 : 11); i--) { // from
			HatchetDefinitions def = HatchetDefinitions.values()[i];
			if (player.getInventory().containsItemToolBelt(def.itemId)
					|| player.getEquipment().getWeaponId() == def.itemId) {
				if (player.getSkills().getLevel(Skills.WOODCUTTING) >= def.levelRequried)
					return def;
			}
		}
		return null;
	}

}
