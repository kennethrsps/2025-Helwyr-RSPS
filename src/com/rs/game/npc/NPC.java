package com.rs.game.npc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import com.discord.Discord;
//import com.rs.DiscordMessageHandler;
import com.rs.Settings;
import com.rs.cache.loaders.ItemDefinitions;
import com.rs.cache.loaders.NPCDefinitions;
import com.rs.cores.CoresManager;
import com.rs.game.Animation;
import com.rs.game.Entity;
import com.rs.game.ForceTalk;
import com.rs.game.Graphics;
import com.rs.game.HeadIcon;
import com.rs.game.Hit;
import com.rs.game.Hit.HitLook;
import com.rs.game.World;
import com.rs.game.WorldTile;
import com.rs.game.activites.ZombieOutpost.ZOControler;
import com.rs.game.activites.ZombieOutpost.ZOGame;
import com.rs.game.activites.ZombieOutpost.ZombieManager;
import com.rs.game.item.Item;
import com.rs.game.map.bossInstance.BossInstance;
import com.rs.game.npc.combat.NPCCombat;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.game.npc.combat.NPCCombatDefinitionsManager;
import com.rs.game.npc.corp.CorporealBeast;
import com.rs.game.npc.familiar.Familiar;
import com.rs.game.npc.godwars.armadyl.KreeArra;
import com.rs.game.npc.godwars.bandos.GeneralGraardor;
import com.rs.game.npc.godwars.saradomin.CommanderZilyana;
import com.rs.game.npc.godwars.zammorak.KrilTsutsaroth;
import com.rs.game.npc.godwars.zaros.Nex;
import com.rs.game.npc.others.TormentedDemon;
import com.rs.game.player.Player;
import com.rs.game.player.SlayerManager;
import com.rs.game.player.actions.herblore.herbicide.Herbicide;
//import com.rs.game.player.combat.NPCCombatHPCommand;
import com.rs.game.player.content.Combat;
import com.rs.game.player.content.CombatMastery;
import com.rs.game.player.content.FriendChatsManager;
import com.rs.game.player.content.Slayer.SlayerMaster;
import com.rs.game.player.content.clans.ClansManager;
import com.rs.game.player.content.clans.content.perks.ClanPerk;
//import com.rs.game.player.content.SlayerTask;
import com.rs.game.player.content.contracts.ContractHandler;
import com.rs.game.player.content.items.Bonecrusher;
import com.rs.game.player.content.items.CharmingImp;
import com.rs.game.player.content.items.CoinAccumulator;
import com.rs.game.player.content.items.Defenders;
import com.rs.game.player.content.items.RingOfWealth;
import com.rs.game.player.controllers.DTController;
import com.rs.game.player.controllers.Dungeoneering;
import com.rs.game.player.controllers.GodWars;
import com.rs.game.player.controllers.WarriorsGuild;
import com.rs.game.player.controllers.Wilderness;
import com.rs.game.route.RouteFinder;
import com.rs.game.route.strategy.FixedTileStrategy;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Colors;
import com.rs.utils.Logger;
import com.rs.utils.MapAreas;
import com.rs.utils.NPCBonuses;
import com.rs.utils.NPCCombatDefinitionsL;
import com.rs.utils.NPCDrops;
import com.rs.utils.Utils;
import com.rs.utils.npcNames;

/*import mysql.impl.NewsManager;*/

/**
 * A class holding all NPC data.
 * 
 * @author Zeus
 */
public class NPC extends Entity implements Serializable {

	/**
	 * The generated serial UID for Serializable.
	 */
	private static final long serialVersionUID = -4794678936277614443L;

	/**
	 * Integers representing NPC movement masks.
	 */
	public static int NORMAL_WALK = 0x2, WATER_WALK = 0x4, FLY_WALK = 0x8;

	/**
	 * NPC Configurations.
	 */
	protected int id;
	private WorldTile respawnTile;
	private int mapAreaNameHash;
	private boolean canBeAttackFromOutOfArea;
	private boolean randomwalk;
	private int[] bonuses; // NPC bonuses go up to 9
	private boolean spawned;
	private transient NPCCombat combat;

	public WorldTile forceWalk;
	private long lastAttackedByTarget;
	private boolean cantInteract;
	private int capDamage;
	private int lureDelay;
	private int walkType;
	private boolean cantFollowUnderCombat;
	private boolean forceAgressive;
	private int forceTargetDistance;
	private boolean forceFollowClose;
	private boolean forceMultiAttacked;

	public int ZOType;
	public int ZOScriptPause;

	private boolean noDistanceCheck;
	// npc masks
	private transient Transformation nextTransformation;
	private transient boolean changedName;
	private transient boolean changedCombatLevel;
	private transient boolean refreshHeadIcon;
	// name changing masks
	private String name;
	private int combatLevel;

	private transient boolean locked;
	private ArrayList<Entity> BossTimeUpdate;

	/**
	 * Initializes the NPC.
	 * 
	 * @param id                       The NPC ID.
	 * @param tile                     The WorldTIle.
	 * @param mapAreaNameHash          The Region area name.
	 * @param canBeAttackFromOutOfArea if Can be attacked out of Region.
	 */
	public NPC(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea) {
		this(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, false);
	}

	/*
	 * creates and adds npc
	 */
	public NPC(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea, boolean spawned) {
		super(tile);
		this.id = id;
		this.respawnTile = new WorldTile(tile);
		this.mapAreaNameHash = mapAreaNameHash;
		this.canBeAttackFromOutOfArea = canBeAttackFromOutOfArea;
		this.spawned = spawned;
		this.maxDistance = -1;
		combatLevel = -1;
		setHitpoints(getMaxHitpoints());
		setDirection(getRespawnDirection());
		setRandomWalk(getDefinitions().movementCapabilities);
		setBonuses();
		combat = new NPCCombat(this);
		capDamage = -1;
		lureDelay = 12000;
		initEntity();
		World.addNPC(this);
		World.updateEntityRegion(this);
		loadMapRegions();
		BossTimeUpdate = new ArrayList<>();
		// IMPORTANT: Apply HP modification FIRST
		//NPCCombatHPCommand.applyHPModification(this);
		// THEN set hitpoints based on (modified) max hitpoints
		setHitpoints(getMaxHitpoints());
		checkMultiArea();
	}

	public boolean canBeAttackedByAutoRetaliate() {
		return Utils.currentTimeMillis() - lastAttackedByTarget > lureDelay;
	}

	public boolean canBeAttackFromOutOfArea() {
		return canBeAttackFromOutOfArea;
	}

	/**
	 * Checks if the NPC should force aggression towards the entities around it.
	 * 
	 * @return if should be agressive.
	 */
	public boolean checkAgressivity() {
		if (!forceAgressive) {
			NPCCombatDefinitions defs = getCombatDefinitions();
			if (defs.getAgressivenessType() == NPCCombatDefinitions.PASSIVE)
				return false;
		}
		ArrayList<Entity> possibleTarget = getPossibleTargets();
		if (!possibleTarget.isEmpty()) {
			Entity target = possibleTarget.get(Utils.random(possibleTarget.size()));
			if (target instanceof Player) {
				Player player = (Player) target;
				int aggressionLevel = (getCombatLevel() * 2) + 1;
				if (player.getControlerManager().getControler() != null || forceAgressive) {
					setTarget(target);
					target.setAttackedBy(target);
					target.setFindTargetDelay(Utils.currentTimeMillis() + 5000);
					return true;
				}
				if (player.getSkills().getCombatLevel() <= aggressionLevel) {
					if ((System.currentTimeMillis() - player.toleranceTimer) < Utils.random(600000, 700000)) {
						setTarget(target);
						target.setAttackedBy(target);
						target.setFindTargetDelay(Utils.currentTimeMillis() + 5000);
						return true;
					}
					return false;
				}
				return false;
			}
			return false;
		}
		return false;
	}

	public void checkGodwarsKillcount() {
		Player killer = getMostDamageReceivedSourcePlayer();
		if (killer == null)
			return;
		if (killer.getControlerManager().getControler() instanceof GodWars)
			((GodWars) killer.getControlerManager().getControler()).handleKC(this);
	}

	public boolean containsItem(int id) {
		Item item = new Item(id);
		return containsItem(item);
	}

	public boolean containsItem(Item item) {
		Player killer = getMostDamageReceivedSourcePlayer();
		return killer.getInventory().getItems().contains(new Item(item.getId(), 1))
				|| killer.getEquipment().getItems().contains(new Item(item.getId(), 1));
	}

	public void deserialize() {
		if (combat == null)
			combat = new NPCCombat(this);
		spawn();
	}

	protected void increaseKillStatistics(Player killer, String name) {
	    // Record mastery for all NPCs
	    try {
	        if (usesCombatScript()) {
	            CombatMastery.recordBossKillSafe(killer, this);
	        } else {
	            CombatMastery.recordMonsterKill(killer, this);
	        }
	    } catch (Exception e) {
	        // Silently ignore mastery errors
	    }

	    // Existing kill statistics code
	    if (killer.increaseKillStatistics(name, false) != -1) {
	        killer.sendMessage("You've killed a total of " + Colors.red + killer.increaseKillStatistics(name, true) + ""
	                + "</col> x " + Colors.red + name + "'s</col>.", true);
	    }
	    int amount = killer.increaseKillStatistics(name, false);
	    if (amount % 100 == 0 && amount != 0) {
	        Discord.sendAchievement("[Kill Count] " + Utils.formatPlayerNameForDisplay(killer.getDisplayName())
	                + " has reached " + amount + " " + Utils.formatPlayerNameForDisplay(name) + " kills milestone!");
	        World.sendWorldMessage(Colors.gold + "<img=6> [Kill Count] </col>" + Colors.red
	                + Utils.formatPlayerNameForDisplay(killer.getDisplayName()) + "</col> has reached " + amount + " "
	                + Colors.red + Utils.formatPlayerNameForDisplay(name) + " </col>kills milestone! " + "<img=6>",
	                false);
	    }
	}

	/**
	 * Check if this NPC uses a CombatScript (manual list of boss IDs)
	 */
	private boolean usesCombatScript() {
	    int id = getId();
	    
	    // List of all NPCs that use CombatScript and may have mastery issues
	    int[] combatScriptBosses = {
	        // GWD Bosses
	        6260, 6261, 6263, 6265, // Graardor, Zilyana, Kree'arra, K'ril
	        
	        // Masuta variants
	        25589, 25590, 25591,
	        
	        // Glacor variants (if needed)
	        14301, 14302, 14303,
	        
	        // Other CombatScript bosses
	        13447, // Corporeal Beast
	        50, 49, // King Black Dragon variants
	        
	        // Add more boss IDs here as needed
	    };
	    
	    for (int bossId : combatScriptBosses) {
	        if (id == bossId) {
	            return true;
	        }
	    }
	    
	    return false;
	}

	protected void handlePetDrop(Player killer, String name) {
		Item pet = null;
		String image = null;
		if (name.toLowerCase().equalsIgnoreCase("general graardor")) {
			if (Utils.random(killer.getPerkManager().petChanter ? 3750 : 5000) == 1) {
				pet = new Item(33806);
				image = "bandos.png";
			}
		}
		if (name.toLowerCase().equalsIgnoreCase("revenant dragon")) {
			if (Utils.random(killer.getPerkManager().petChanter ? 1000 : 1500) == 1) {
				pet = new Item(39082);
			}
		}
		if (name.toLowerCase().equalsIgnoreCase("revenant knight")) {
			if (Utils.random(killer.getPerkManager().petChanter ? 1000 : 1500) == 1) {
				pet = new Item(39081);
			}
		}
		if (name.toLowerCase().equalsIgnoreCase("revenant darkbeast")) {
			if (Utils.random(killer.getPerkManager().petChanter ? 1000 : 1500) == 1) {
				pet = new Item(39080);
			}
		}
		if (name.toLowerCase().equalsIgnoreCase("revenant ork")) {
			if (Utils.random(killer.getPerkManager().petChanter ? 1000 : 1500) == 1) {
				pet = new Item(39079);
			}
		}
		if (name.toLowerCase().equalsIgnoreCase("revenant lesser demon")) {
			if (Utils.random(killer.getPerkManager().petChanter ? 1000 : 1500) == 1) {
				pet = new Item(39078);
			}
		}
		if (name.toLowerCase().equalsIgnoreCase("revenant hellhound")) {
			if (Utils.random(killer.getPerkManager().petChanter ? 1000 : 1500) == 1) {
				pet = new Item(39077);
			}
		}
		if (name.toLowerCase().equalsIgnoreCase("revenant cyclops")) {
			if (Utils.random(killer.getPerkManager().petChanter ? 1000 : 1500) == 1) {
				pet = new Item(39076);
			}
		}
		if (name.toLowerCase().equalsIgnoreCase("revenant werewolf")) {
			if (Utils.random(killer.getPerkManager().petChanter ? 1000 : 1500) == 1) {
				pet = new Item(39075);
			}
		}
		if (name.toLowerCase().equalsIgnoreCase("revenant vampyre")) {
			if (Utils.random(killer.getPerkManager().petChanter ? 1000 : 1500) == 1) {
				pet = new Item(39074);
			}
		}
		if (name.toLowerCase().equalsIgnoreCase("revenant hobgoblin")) {
			if (Utils.random(killer.getPerkManager().petChanter ? 1000 : 1500) == 1) {
				pet = new Item(39073);
			}
		}
		if (name.toLowerCase().equalsIgnoreCase("revenant pyrefirend")) {
			if (Utils.random(killer.getPerkManager().petChanter ? 1000 : 1500) == 1) {
				pet = new Item(39072);
			}
		}
		if (name.toLowerCase().equalsIgnoreCase("revenant icefiend")) {
			if (Utils.random(killer.getPerkManager().petChanter ? 1000 : 1500) == 1) {
				pet = new Item(39071);
			}
		}
		if (name.toLowerCase().equalsIgnoreCase("revenant goblin")) {
			if (Utils.random(killer.getPerkManager().petChanter ? 1000 : 1500) == 1) {
				pet = new Item(39070);
			}
		}
		if (name.toLowerCase().equalsIgnoreCase("revenant imp")) {
			if (Utils.random(killer.getPerkManager().petChanter ? 1000 : 1500) == 1) {
				pet = new Item(39069);
			}
		}

		if (name.toLowerCase().equalsIgnoreCase("the magister")) {
			if (Utils.random(killer.getPerkManager().petChanter ? 1875 : 2500) == 1) {
				pet = new Item(40669);
				// image = "bandos.png";
			}
		}
		if (name.toLowerCase().equalsIgnoreCase("solak")) {
			if (Utils.random(killer.getPerkManager().petChanter ? 1875 : 2500) == 1) {
				pet = new Item(42843);
				// image = "bandos.png";
			}
		}
		if (name.toLowerCase().equalsIgnoreCase("telos, the warden")) {
			if (Utils.random(killer.getPerkManager().petChanter ? 1875 : 2500) == 1) {
				pet = new Item(37679);
				// image = "bandos.png";
			}
		}
		if (name.toLowerCase().equalsIgnoreCase("k'ril tsutsaroth")) {
			if (Utils.random(killer.getPerkManager().petChanter ? 3750 : 5000) == 1) {
				pet = new Item(33805);
				image = "zammy.png";
			}
		}
		if (name.toLowerCase().equalsIgnoreCase("commander zilyana")) {
			if (Utils.random(killer.getPerkManager().petChanter ? 3750 : 5000) == 1) {
				pet = new Item(33807);
				image = "sara.png";
			}
		}
		if (name.toLowerCase().equalsIgnoreCase("kree'arra")) {
			if (Utils.random(killer.getPerkManager().petChanter ? 3750 : 5000) == 1) {
				pet = new Item(33804);
				image = "arma.png";
			}
		}
		if (name.toLowerCase().equalsIgnoreCase("dagannoth prime")) {
			if (Utils.random(killer.getPerkManager().petChanter ? 1875 : 2500) == 1) {
				pet = new Item(33826);
				image = "dag.png";
			}
		}
		if (name.toLowerCase().equalsIgnoreCase("dagannoth supreme")) {
			if (Utils.random(killer.getPerkManager().petChanter ? 1875 : 2500) == 1) {
				pet = new Item(33828);
				image = "dag.png";
			}
		}
		if (name.toLowerCase().equalsIgnoreCase("dagannoth rex")) {
			if (Utils.random(killer.getPerkManager().petChanter ? 1875 : 2500) == 1) {
				pet = new Item(33827);
				image = "dag.png";
			}
		}
		if (name.toLowerCase().equalsIgnoreCase("chaos elemental")) {
			if (Utils.random(killer.getPerkManager().petChanter ? 1500 : 2000) == 1) {
				pet = new Item(33811);
				image = "ellie.png";
			}
		}
		if (name.toLowerCase().equalsIgnoreCase("kalphite queen")) {
			if (Utils.random(killer.getPerkManager().petChanter ? 1500 : 2000) == 1) {
				if (Utils.random(1) == 0) {
					pet = new Item(33816);
					image = "kq1.png";
				} else {
					pet = new Item(33817);
					image = "kq2.png";
				}
			}
		}
		if (name.equalsIgnoreCase("araxxor")) {
			int petChance = Utils.random(killer.getPerkManager().petChanter ? 4900 : 6250);
			{
				image = "araxx.png";
				switch (petChance) {
				case 1:
					pet = new Item(33748);
					break;
				case 2012:
					pet = new Item(33749);
					break;
				case 63:
					pet = new Item(33750);
					break;
				case 4874:
					pet = new Item(33751);
					break;
				case 0:
					pet = new Item(33752);
					break;
				case 3728:
					pet = new Item(33753);
					break;
				default:
					break;
				}
			}
		}
		if (name.toLowerCase().equalsIgnoreCase("corporeal beast")) {
			if (Utils.random(killer.getPerkManager().petChanter ? 1875 : 2500) == 1) {
				pet = new Item(33812);
				image = "corp.png";
			}
		}
		if (name.toLowerCase().equalsIgnoreCase("king black dragon")) {
			if (Utils.random(killer.getPerkManager().petChanter ? 1875 : 2500) == 1) {
				pet = new Item(33818);
				image = "kbd.png";
			}
		}
		if (name.toLowerCase().equalsIgnoreCase("nex")) {
			if (Utils.random(killer.getPerkManager().petChanter ? 750 : 1000) == 1) {
				pet = new Item(33808);
				image = "nex.png";
			}
		}
		if (name.toLowerCase().equalsIgnoreCase("queen black dragon")) {
			if (Utils.random(killer.getPerkManager().petChanter ? 750 : 1000) == 1) {
				pet = new Item(33825);
				image = "qbd.png";
			}
		}
		if (name.toLowerCase().equalsIgnoreCase("kalphite king")) {
			if (Utils.random(killer.getPerkManager().petChanter ? 1875 : 2500) == 1) {
				pet = new Item(33815);
				image = "kalking.png";
			}
		}
		if (name.toLowerCase().equalsIgnoreCase("vorago")) {
			if (Utils.random(killer.getPerkManager().petChanter ? 375 : 500) == 1) {
				pet = new Item(28630);
				image = "vitalis.png";
			}
			if (Utils.random(killer.getPerkManager().petChanter ? 185 : 250) == 1) {
				pet = new Item(33717);
				image = "bombii.png";
			}
		}
		if (name.toLowerCase().equalsIgnoreCase("legio primus")) {
			if (Utils.random(killer.getPerkManager().petChanter ? 1875 : 2500) == 1) {
				pet = new Item(33819);
				image = "legio.png";
			}
		}
		if (name.toLowerCase().equalsIgnoreCase("legio secundus")) {
			if (Utils.random(killer.getPerkManager().petChanter ? 1875 : 2500) == 1) {
				pet = new Item(33820);
				image = "legio.png";
			}
		}
		if (name.toLowerCase().equalsIgnoreCase("legio tertius")) {
			if (Utils.random(killer.getPerkManager().petChanter ? 1875 : 2500) == 1) {
				pet = new Item(33821);
				image = "legio.png";
			}
		}
		if (name.toLowerCase().equalsIgnoreCase("legio quartus")) {
			if (Utils.random(killer.getPerkManager().petChanter ? 1875 : 2500) == 1) {
				pet = new Item(33822);
				image = "legio.png";
			}
		}
		if (name.toLowerCase().equalsIgnoreCase("legio quintus")) {
			if (Utils.random(killer.getPerkManager().petChanter ? 1875 : 2500) == 1) {
				pet = new Item(33823);
				image = "legio.png";
			}
		}
		if (name.toLowerCase().equalsIgnoreCase("legio sextus")) {
			if (Utils.random(killer.getPerkManager().petChanter ? 1875 : 2500) == 1) {
				pet = new Item(33824);
				image = "legio.png";
			}
		}
		if (pet != null && !killer.hasItem(pet)) {
			Discord.sendAchievement("News: " + killer.getDisplayName() + " received a " + pet.getName() + " pet drop.");
			World.sendWorldMessage(Colors.orange + "<shad=000000><img=6>News: " + killer.getDisplayName()
					+ " received a " + pet.getName() + " pet drop.", false);

			killer.addItem(pet);

			/*
			 * new Thread(new NewsManager(killer, "<b><img src=\"./bin/images/news/" + image
			 * + "\" width=15> " + killer.getDisplayName() + " received a " + pet.getName()
			 * + " pet drop.")).start();
			 */
		}
	}

	private static Item[] easterDropsCommon = { new Item(12158, Utils.random(1, 4)), // gold
			new Item(12159, Utils.random(1, 3)), // green
			new Item(12160, Utils.random(1, 2)), // crimson
			new Item(12163, 1), // blue
			new Item(532, 1), // bigbones
			// bronze set
			new Item(1155, 1), new Item(1117, 1), new Item(1075, 1), new Item(1189, 1)

	};
	private static Item[] easterDropsNonCommon = {
			// rune set
			new Item(1163, 1), new Item(1127, 1), new Item(1093, 1), new Item(1201, 1),
			// dbones
			new Item(536, 1),
			// tooth half
			new Item(985, 1),
			// loop half
			new Item(987, 1),
			// fdbones
			new Item(18830, 1), };
	private static Item[] easterDropsRare = {
			// easter eggs
			new Item(7928, 1), new Item(7929, 1), new Item(7930, 1), new Item(7931, 1), new Item(7932, 1),
			new Item(7933, 1),
			// dragon set
			new Item(11335, 1), new Item(14479, 1), new Item(4087, 1), new Item(1187, 1), };
	// DarkLord droprate
	private static Item[] DarkLordCommon = { new Item(450, Utils.random(1)), new Item(537, Utils.random(5, 15)),
			new Item(450, 1), new Item(995, Utils.random(10000, 100000)), new Item(8781, 1), new Item(1401, 1),
			new Item(7060, 1), new Item(563, 1), new Item(566, 1), new Item(384, 1), new Item(4093, 1),
			new Item(1403, 1), new Item(1405, 1), new Item(4091, 1), new Item(1407, 1)

	};
	private static Item[] DarkLordNoNCommon = { new Item(1249, 1), new Item(989, 1), new Item(830, 1),
			new Item(2366, 1), new Item(5295, 1), new Item(11133, 1), new Item(1463, 1), new Item(9245, 1) };

	private static Item[] DarkLordRareDrop = { new Item(34222, 1), new Item(34223, 1), new Item(34224, 1),
			new Item(34225, 1), new Item(34226, 1), new Item(37485, 1), new Item(32703, 1) };

	private void dropnormalbones(Player player) {
		World.addGroundItem(new Item(526, 1), new WorldTile(this), player, true, 60);
	}

	/**
	 * Enhanced NPC Drop System
	 * Handles all drop mechanics for NPCs including special drops, rare items, and boss rewards
	 * 
	 * @author Zeus
	 * @since June 07, 2025
	 */

	// Drop rate constants
	private static final int EASTER_COMMON_RATE = 70;
	private static final int EASTER_UNCOMMON_RATE = 95;
	private static final int DARK_LORD_COMMON_RATE = 80;
	private static final int DARK_LORD_UNCOMMON_RATE = 98;
	private static final int REVENANT_RARE_BASE_RATE = 200;
	private static final int REVENANT_RARE_ROW_RATE = 150;
	private static final int CLUE_SCROLL_RATE = 300;
	private static final int DEFENDER_DROP_RATE = 500;
	private static final int RARE_DROP_RATE = 750;
	private static final int SLAYER_GEM_RATE = 250;
	private static final int HC_IRONMAN_RATE = 50;
	private static final int ZILYANA_BOOT_RATE = 500;
	private static final int KK_CHITIN_RATE = 74;
	private static final int NEX_EMBLEM_RATE = 59;

	// Drop timeout constant
	private static final int DROP_TIMEOUT_SECONDS = 60;

	// Special NPC IDs
	private static final int EASTER_BUNNY_ID = 1320;
	private static final int DARK_LORD_ID = 19553;
	private static final int BALLAK_ID = 10140;

	// Clue scroll item IDs
	private static final int EASY_CLUE = 2677;
	private static final int MEDIUM_CLUE = 2801;
	private static final int HARD_CLUE = 2722;
	private static final int ELITE_CLUE = 19043;

	// Special item IDs
	private static final int COINS = 995;
	private static final int MYSTERY_BOX = 6199;
	private static final int VANGUARD_BOOTS = 21476;
	private static final int RARE_DROP_TABLE = 18778;
	private static final int SLAYER_GEM = 29863;
	private static final int CASKET = 10498;
	private static final int PERFECT_CHITIN = 36163;
	private static final int ANCIENT_EMBLEM = 36159;
	private static final int RING_OF_DEVOTION = 31869;

	// Combat level thresholds
	private static final int RARE_DROP_MIN_LEVEL = 90;
	private static final int SLAYER_GEM_MIN_LEVEL = 78;
	private static final int EASY_CLUE_MAX_LEVEL = 50;
	private static final int MEDIUM_CLUE_MAX_LEVEL = 150;
	private static final int HARD_CLUE_MAX_LEVEL = 250;

	// Reward amounts
	private static final int BALLAK_REWARD_COINS = 5000000;
	private static final int BALLAK_REWARD_BOX = 1;
	private static final int DARK_LORD_DAMAGE_INCREASE = 10;
	private static final int SPECIAL_RESTORE_DIVISOR = 150;
	private static final int MAX_SPECIAL_PERCENT = 100;
	private static final int SPECIAL_RING_PROC_RATE = 50;

	/**
	 * Main drop handling method - orchestrates all drop mechanics
	 */
	public void drop() {
	    try {
	        Player killer = getMostDamageReceivedSourcePlayer();
	        if (!validateDropConditions(killer)) {
	            return;
	        }

	        // Handle special controller drops
	        if (handleControllerDrops(killer)) {
	            return;
	        }

	        // Process slayer task
	        processSlayerTask(killer);

	        // Handle special NPC drops
	        if (handleSpecialNPCDrops(killer)) {
	            return;
	        }

	        // Process regular drops
	        processRegularDrops(killer);

	    } catch (Exception e) {
	        System.err.println("Error in drop() method for NPC ID " + getId() + ": " + e.getMessage());
	        e.printStackTrace();
	    }
	}

	/**
	 * Validates basic drop conditions
	 */
	private boolean validateDropConditions(Player killer) {
	    if (killer == null) {
	        return false;
	    }

	    // Check boss instance conditions
	    if (bossInstance != null && (bossInstance.isFinished() || bossInstance.getSettings().isPractiseMode())) {
	        return false;
	    }

	    // Skip drops for very low HP NPCs
	    if (getMaxHitpoints() == 1) {
	        return false;
	    }

	    return true;
	}

	/**
	 * Handles drops for special controllers (DT, ZO)
	 */
	private boolean handleControllerDrops(Player killer) {
	    if (killer.getControlerManager() == null || killer.getControlerManager().getControler() == null) {
	        return false;
	    }

	    // Handle DT Controller
	    if (killer.getControlerManager().getControler() instanceof DTController) {
	        return true; // No drops in DT
	    }

	    // Handle ZO Controller
	    if (killer.getControlerManager().getControler() instanceof ZOControler) {
	        handleZODrop(killer);
	        return true;
	    }

	    return false;
	}

	/**
	 * Handles ZO (Zombie Outbreak) drops
	 */
	private void handleZODrop(Player killer) {
	    int points = getMaxHitpoints();
	    int reward = points + Utils.random(points);
	    
	    killer.ZOPoints += reward; // Fixed: removed duplicate line
	    killer.ZOKills++;
	    ZOGame.sendInterface(killer);
	}

	/**
	 * Processes slayer task progression
	 */
	private void processSlayerTask(Player killer) {
	    if (killer.getSlayerManager() == null) {
	        return;
	    }

	    Player otherPlayer = killer.getSlayerManager().getSocialPlayer();
	    SlayerManager manager = killer.getSlayerManager();
	    
	    if (manager.isValidTask(npcNames.getNPCName(getId()))) {
	        int otherDamage = (otherPlayer != null) ? getDamageReceived(otherPlayer) : 0;
	        manager.checkCompletedTask(getDamageReceived(killer), otherDamage);
	    }
	}

	/**
	 * Handles special NPC specific drops
	 */
	private boolean handleSpecialNPCDrops(Player killer) {
	    int npcId = getId();
	    
	    switch (npcId) {
	        case EASTER_BUNNY_ID:
	            handleEasterDrop(killer);
	            return true;
	            
	        case DARK_LORD_ID:
	            handleDarkLordDrop(killer);
	            return true;
	            
	        case BALLAK_ID:
	            handleBallakDrop(killer);
	            return true;
	            
	        default:
	            return false;
	    }
	}

	/**
	 * Handles Easter Bunny drops
	 */
	private void handleEasterDrop(Player killer) {
	    dropNormalBones(killer);
	    
	    int rng = Utils.random(100);
	    WorldTile location = new WorldTile(this);
	    
	    if (rng <= EASTER_COMMON_RATE) {
	        dropRandomItem(easterDropsCommon, location, killer);
	    } else if (rng <= EASTER_UNCOMMON_RATE) {
	        dropRandomItem(easterDropsNonCommon, location, killer);
	    } else {
	        Item drop = getRandomItem(easterDropsRare);
	        World.addGroundItem(drop, location, killer, true, DROP_TIMEOUT_SECONDS);
	        
	        if (drop.getName().contains("Easter")) {
	            announceRareDrop(killer, drop, getName());
	        }
	    }
	}

	/**
	 * Handles Dark Lord drops
	 */
	private void handleDarkLordDrop(Player killer) {
	    dropNormalBones(killer);
	    
	    int dropRate = Utils.random(100);
	    WorldTile location = new WorldTile(this);
	    
	    if (dropRate <= DARK_LORD_COMMON_RATE) {
	        dropRandomItem(DarkLordCommon, location, killer);
	    } else if (dropRate <= DARK_LORD_UNCOMMON_RATE) {
	        dropRandomItem(DarkLordNoNCommon, location, killer);
	    } else {
	        Item drop = getRandomItem(DarkLordRareDrop);
	        World.addGroundItem(drop, location, killer, true, DROP_TIMEOUT_SECONDS);
	        
	        if (drop.getName().contains("Dark Lord")) {
	            announceRareDrop(killer, drop, getName());
	            Discord.sendAchievement("News: " + killer.getDisplayName() + 
	                " received " + drop.getName() + " from " + getName() + ".");
	        }
	    }
	    
	    // Increase damage counter for Dark Lord
	    killer.setPAdamage(killer.getPAdamage() + DARK_LORD_DAMAGE_INCREASE);
	    killer.sm("The dark lord has now increased damage on you.");
	}

	/**
	 * Handles Ballak drops and rewards
	 */
	private void handleBallakDrop(Player killer) {
	    if (rewardList == null || !rewardList.contains(killer)) {
	        return;
	    }
	    
	    // Distribute rewards to all participants
	    for (Player participant : rewardList) {
	        if (participant == null) continue;
	        
	        if (participant.getInventory().getFreeSlots() > 1) {
	            participant.getInventory().addItem(COINS, BALLAK_REWARD_COINS);
	            participant.getInventory().addItem(MYSTERY_BOX, BALLAK_REWARD_BOX);
	        } else {
	            participant.getBank().addItem(COINS, BALLAK_REWARD_COINS, true);
	            participant.getBank().addItem(MYSTERY_BOX, BALLAK_REWARD_BOX, true);
	        }
	        participant.ballakdmg = 0;
	    }
	    
	    // Announce participation
	    World.sendWorldMessage("<img=7><col=FF0000>News: " + rewardList.size() + 
	        " players have participated in killing " + getName(), false);
	    rewardList.clear();
	}

	/**
	 * Processes regular drops for all NPCs
	 */
	private void processRegularDrops(Player killer) {
	    Drop[] drops = NPCDrops.getDrops(getId());
	    if (drops == null) {
	        return;
	    }

	    // Handle dungeoneering drops
	    Dungeoneering.handleDrop(killer, this);
	    
	    String npcName = getDefinitions().name.toLowerCase();
	    increaseKillStatistics(killer, npcName);
	    handlePetDrop(killer, npcName);
	    
	    // Process boss-specific drops
	    processBossDrops(killer, npcName);
	    
	    // Process general rare drops
	    processGeneralRareDrops(killer, npcName);
	    
	    // Process special equipment effects
	    processEquipmentEffects(killer);
	    
	    // Process regular drop table
	    processDropTable(killer, drops);
	    
	    // Process contracts
	    ContractHandler.checkContract(killer, getId(), this);
	}

	/**
	 * Processes boss-specific special drops
	 */
	private void processBossDrops(Player killer, String npcName) {
	    WorldTile location = new WorldTile(this);
	    
	    // Kalphite King drops
	    if (npcName.contains("kalphite king")) {
	        if (Defenders.getCurrentTier(killer, 1) && Utils.random(KK_CHITIN_RATE) == 0) {
	            sendDrop(killer, new Drop(PERFECT_CHITIN, 1, 1));
	            announceWorldDrop(killer, "perfect chitin", "Kalphite King");
	        }
	    }
	    
	    // Nex drops
	    if (npcName.contains("nex")) {
	        if (Defenders.getCurrentTier(killer, 0) && Utils.random(NEX_EMBLEM_RATE) == 0) {
	            sendDrop(killer, new Drop(ANCIENT_EMBLEM, 1, 1));
	            announceWorldDrop(killer, "ancient emblem", "Nex");
	        }
	    }
	    
	    // Commander Zilyana drops
	    if (npcName.contains("Commander Zilyana") && Utils.random(ZILYANA_BOOT_RATE) == 0) {
	        World.addGroundItem(new Item(VANGUARD_BOOTS), location, killer, true, DROP_TIMEOUT_SECONDS);
	    }
	}

	/**
	 * Processes general rare drops for all NPCs
	 */
	private void processGeneralRareDrops(Player killer, String npcName) {
	    WorldTile location = new WorldTile(this);
	    
	    // Revenant drops
	    if (npcName.contains("revenant")) {
	        int rate = RingOfWealth.checkRow(killer) ? REVENANT_RARE_ROW_RATE : REVENANT_RARE_BASE_RATE;
	        if (Utils.random(rate) == 0) {
	            int pvpItem = PVP_ITEMS[Utils.random(PVP_ITEMS.length)];
	            World.addGroundItem(new Item(pvpItem), location, killer, true, DROP_TIMEOUT_SECONDS);
	        }
	    }
	    
	    // High level NPC rare drop table
	    if (getCombatLevel() >= RARE_DROP_MIN_LEVEL && Utils.random(RARE_DROP_RATE) == 0) {
	        World.addGroundItem(new Item(RARE_DROP_TABLE), location, killer, true, DROP_TIMEOUT_SECONDS);
	    }
	    
	    // Slayer gem drops
	    int slayerLevel = Combat.getSlayerLevelForNPC(getId());
	    if (slayerLevel >= SLAYER_GEM_MIN_LEVEL && Utils.random(SLAYER_GEM_RATE) == 0) {
	        World.updateGroundItem(new Item(SLAYER_GEM), location, killer, DROP_TIMEOUT_SECONDS, 0, true);
	    }
	    
	    // Defender drops from Cyclops
	    if (isCyclops(getDefinitions().name) && Utils.random(DEFENDER_DROP_RATE) > 450) {
	        if (killer.getControlerManager().getControler() instanceof WarriorsGuild) {
	            World.updateGroundItem(new Item(whatDefender()), location, killer, DROP_TIMEOUT_SECONDS, 0, true);
	        }
	    }
	    
	    // Clue scroll drops
	    if (isClueScrollNPC(getDefinitions().name) && Utils.random(CLUE_SCROLL_RATE) <= 1) {
	        handleClueScrollDrop(killer, location);
	    }
	    
	    // HC Ironman special drops
	    if (killer.isHCIronMan() && Utils.random(HC_IRONMAN_RATE) <= 1) {
	        if (isLowLevelMonster(npcName)) {
	            World.updateGroundItem(new Item(CASKET), location, killer, DROP_TIMEOUT_SECONDS, 0, true);
	        }
	    }
	}

	/**
	 * Handles clue scroll drops
	 */
	private void handleClueScrollDrop(Player killer, WorldTile location) {
	    if (killer.getTreasureTrails().hasClueScrollItem()) {
	        return;
	    }
	    
	    killer.getTreasureTrails().resetCurrentClue();
	    int itemId = determineClueScrollTier(getCombatLevel());
	    World.updateGroundItem(new Item(itemId), location, killer, DROP_TIMEOUT_SECONDS, 0, true);
	}

	/**
	 * Determines clue scroll tier based on combat level
	 */
	private int determineClueScrollTier(int combatLevel) {
	    if (combatLevel < EASY_CLUE_MAX_LEVEL) {
	        return EASY_CLUE;
	    } else if (combatLevel < MEDIUM_CLUE_MAX_LEVEL) {
	        return MEDIUM_CLUE;
	    } else if (combatLevel < HARD_CLUE_MAX_LEVEL) {
	        return HARD_CLUE;
	    } else {
	        return ELITE_CLUE;
	    }
	}

	/**
	 * Processes special equipment effects on kill
	 */
	private void processEquipmentEffects(Player killer) {
	    // Ring of Devotion special effect
	    if (killer.getEquipment().getRingId() == RING_OF_DEVOTION) {
	        if (Utils.random(100) >= SPECIAL_RING_PROC_RATE) {
	            int toRestore = Math.max(1, getMaxHitpoints() / SPECIAL_RESTORE_DIVISOR);
	            int currentSpecial = killer.getCombatDefinitions().getSpecialAttackPercentage();
	            
	            if (toRestore <= 5 && (currentSpecial + toRestore <= MAX_SPECIAL_PERCENT)) {
	                killer.getCombatDefinitions().setSpecialAttackPercentage(currentSpecial + toRestore);
	            }
	        }
	    }
	}

	/**
	 * Processes the main drop table
	 */
	private void processDropTable(Player killer, Drop[] drops) {
	    Drop[] possibleDrops = new Drop[drops.length];
	    int possibleDropsCount = 0;
	    
	    for (Drop drop : drops) {
	        if (drop == null) continue;
	        
	        // Skip clue scrolls if player already has one
	        if (killer.getTreasureTrails().isScroll(drop.getItemId()) && 
	            killer.getTreasureTrails().hasClueScrollItem()) {
	            continue;
	        }
	        
	        // Always drop 100% rate items
	        if (drop.getRate() == 100) {
	            sendDrop(killer, drop);
	        } else {
	            // Check if item should drop based on rate
	            double dropChance = drop.getRate() + Settings.getDropQuantityRate(killer);
	            if (Utils.getRandomDouble(99) + 1 <= dropChance) {
	                possibleDrops[possibleDropsCount++] = drop;
	            }
	        }
	    }
	    
	    // Send one random drop from possible drops
	    if (possibleDropsCount > 0) {
	        Drop selectedDrop = possibleDrops[Utils.getRandom(possibleDropsCount - 1)];
	        sendDrop(killer, selectedDrop);
	    }
	}

	/**
	 * Helper method to drop random item from array
	 */
	private void dropRandomItem(Item[] items, WorldTile location, Player killer) {
	    if (items != null && items.length > 0) {
	        Item drop = getRandomItem(items);
	        World.addGroundItem(drop, location, killer, true, DROP_TIMEOUT_SECONDS);
	    }
	}

	/**
	 * Helper method to get random item from array
	 */
	private Item getRandomItem(Item[] items) {
	    if (items == null || items.length == 0) {
	        return null;
	    }
	    return items[Utils.random(items.length)];
	}

	/**
	 * Helper method to announce rare drops
	 */
	private void announceRareDrop(Player killer, Item drop, String npcName) {
	    World.sendWorldMessage(Colors.orange + "<shad=000000><img=6>News: " + 
	        killer.getDisplayName() + " received " + drop.getName() + 
	        " from " + npcName + ".", false);
	}

	/**
	 * Helper method to announce world drops
	 */
	private void announceWorldDrop(Player killer, String itemName, String npcName) {
	    World.sendWorldMessage(Colors.cyan + "<shad=000000><img=6>News: " + 
	        killer.getDisplayName() + " received a " + itemName + " from " + npcName + "!", false);
	    Discord.sendAchievement("News: " + killer.getDisplayName() + 
	        " received a " + itemName + " from " + npcName + "!");
	}

	/**
	 * Helper method to check if NPC is low level monster
	 */
	private boolean isLowLevelMonster(String npcName) {
	    return npcName.contains("crab") || npcName.contains("crawling hand") || 
	           npcName.contains("banshee") || npcName.contains("slug");
	}

	/**
	 * Fixed method name (was dropnormalbones)
	 */
	private void dropNormalBones(Player killer) {
	    // Implementation for dropping bones
	    // This method should be implemented based on your bone dropping logic
	}

	/**
	 * Enhanced NPC Drop Sending System
	 * Handles all drop mechanics including loot sharing, special effects, and rare drops
	 * 
	 * @author Zeus
	 * @since June 07, 2025
	 */

	// Drop filtering constants
	private static final int CHAOS_RUNES_ID = 618;
	private static final int COINS_ID = 995;
	private static final int NULL_ITEM_ID = 0;
	private static final int DRAGONSTONE_ID = 11286;
	private static final int SPECIAL_ITEM_ID = 31203;

	// Clue scroll constants
	private static final int EASY_CLUE_ID = 2677;
	private static final int MEDIUM_CLUE_ID = 2801;
	private static final int HARD_CLUE_ID = 2722;
	private static final int ELITE_CLUE_ID = 19043;
	private static final int CLUE_GRAPHICS_ID = 1274;

	// Clue scroll drop rates
	private static final int EASY_CLUE_RATE = 1000;
	private static final int MEDIUM_CLUE_RATE = 2000;
	private static final int HARD_CLUE_RATE = 4000;
	private static final int ELITE_CLUE_RATE = 8000;

	// Special ring constants
	private static final int LUCK_OF_DWARVES_ID = 39812;
	private static final int RING_OF_WEALTH_ID = 2572;
	private static final int HAZELMERE_SIGNET_ID = 39814;
	private static final int HAZELMERE_DROP_RATE_LOD = 200000;
	private static final int HAZELMERE_DROP_RATE_ROW = 400000;
	private static final int HAZELMERE_PROC_RATE = 200;
	private static final int HAZELMERE_PERK_RATE = 400;

	// Dragon egg constants
	private static final int GREEN_DRAGON_EGG = 12473;
	private static final int BLUE_DRAGON_EGG = 12471;
	private static final int RED_DRAGON_EGG = 12469;
	private static final int BLACK_DRAGON_EGG = 12475;
	private static final int DRAGON_TRAINER_RATE = 1000;
	private static final int NORMAL_DRAGON_RATE = 1500;

	// Special item constants
	private static final int HERB_BOX_ID = 3062;
	private static final int HERBIVORE_RATE = 150;
	private static final int BONECRUSHER_ID = 18337;
	private static final int HERBICIDE_ID = 19675;

	// Drop effects constants
	private static final int LOOT_BEAM_GRAPHICS = 7;
	private static final int HAZELMERE_GRAPHICS = 5596;
	private static final int HAZELMERE_PROC_GRAPHICS = 5599;
	private static final int DROP_TIMEOUT = 60;
	private static final int LOOTSHARE_TIMEOUT = 180;
	private static final int MIN_LOOTSHARE_VALUE = 1000000;
	private static final double PET_COLLECTION_MULTIPLIER = 1.5;
	private static final int PET_COLLECTION_THRESHOLD = 100000;

	// Special drop filters
	private static final int DRAGONSTONE_FILTER_RATE = 75;
	private static final int SPECIAL_ITEM_FILTER_RATE = 90;

	/**
	 * Main method to send drop to player with all special effects and mechanics
	 */
	protected void sendDrop(final Player player, Drop drop) {
	    if (!validateDropConditions(player, drop)) {
	        return;
	    }

	    final WorldTile dropTile = createDropTile();
	    final String dropName = getDropName(drop);

	    // Process boss timer updates
	    processBossTimerUpdates();

	    // Apply drop filters
	    if (!passesDropFilters(drop)) {
	        return;
	    }

	    // Handle special item processing
	    processSpecialItems(player, dropTile);

	    // Calculate final item amount and handle loot sharing
	    Item finalItem = calculateDropAmount(drop);
	    if (handleLootSharing(player, finalItem, dropName, dropTile)) {
	        return;
	    }

	    // Process clue scroll drops
	    processClueScrollDrops(player, dropTile);

	    // Process special ring effects
	    processSpecialRings(player, drop, dropTile);

	    // Process dragon egg drops
	    processDragonEggDrops(player, dropTile);

	    // Handle the main drop
	    handleMainDrop(player, drop, finalItem, dropName, dropTile);
	}

	/**
	 * Validates basic drop conditions
	 */
	private boolean validateDropConditions(Player player, Drop drop) {
	    return player != null && drop != null && drop.getItemId() > 0;
	}

	/**
	 * Creates the drop tile location
	 */
	private WorldTile createDropTile() {
	    return new WorldTile(getCoordFaceX(getSize()), getCoordFaceY(getSize()), getPlane());
	}

	/**
	 * Gets the display name for the dropped item
	 */
	private String getDropName(Drop drop) {
	    return ItemDefinitions.getItemDefinitions(drop.getItemId()).getName().toLowerCase();
	}

	/**
	 * Processes boss timer updates (fixed memory leak)
	 */
	private void processBossTimerUpdates() {
	    if (BossTimeUpdate == null) {
	        return;
	    }

	    ArrayList<Entity> possibleTargets = getPossibleTargets();
	    if (possibleTargets == null) {
	        return;
	    }

	    // Fixed: Use proper iterator pattern to avoid memory leaks
	    Iterator<Entity> iterator = BossTimeUpdate.iterator();
	    while (iterator.hasNext()) {
	        Entity entity = iterator.next();
	        if (entity instanceof Player && possibleTargets.contains(entity)) {
	            Player player = (Player) entity;
	            if (player.getBossTimerManager() != null) {
	                player.getBossTimerManager().recieveDeath(this);
	            }
	            iterator.remove();
	        }
	    }
	}

	/**
	 * Applies drop filtering logic
	 */
	private boolean passesDropFilters(Drop drop) {
	    int itemId = drop.getItemId();
	    
	    // Block specific items
	    if (itemId == CHAOS_RUNES_ID || itemId == COINS_ID || itemId == NULL_ITEM_ID) {
	        return false;
	    }
	    
	    // Apply probability filters for special items
	    if (itemId == DRAGONSTONE_ID && Utils.random(100) < DRAGONSTONE_FILTER_RATE) {
	        return false;
	    }
	    
	    if (itemId == SPECIAL_ITEM_ID && Utils.random(100) < SPECIAL_ITEM_FILTER_RATE) {
	        return false;
	    }
	    
	    // Block clue scrolls (handled separately)
	    String dropName = getDropName(drop);
	    if (dropName.contains("clue")) {
	        return false;
	    }
	    
	    return true;
	}

	/**
	 * Processes special items and effects
	 */
	private void processSpecialItems(Player player, WorldTile dropTile) {
	    // Handle charming imp
	    CharmingImp.handleCharmDrops(player, this);
	    
	    // Handle herbivore perk
	    if (player.getPerkManager() != null && player.getPerkManager().herbivore) {
	        if (Utils.getRandom(HERBIVORE_RATE) <= 1) {
	            World.updateGroundItem(new Item(HERB_BOX_ID), dropTile, player, DROP_TIMEOUT, 0, true);
	            player.sendMessage("You've received a Herb Box drop thanks to your Herbivore perk.", true);
	        }
	    }
	}

	/**
	 * Calculates the final drop amount with bonuses
	 */
	private Item calculateDropAmount(Drop drop) {
	    int bonus = 1;
	    boolean isStackable = ItemDefinitions.getItemDefinitions(drop.getItemId()).isStackable();
	    
	    if (isStackable) {
	        int amount = (drop.getMinAmount() * Settings.DROP_RATE * bonus) + 
	                    Utils.getRandom(drop.getExtraAmount() * Settings.DROP_RATE * bonus);
	        return new Item(drop.getItemId(), amount);
	    } else {
	        int amount = drop.getMinAmount() + Utils.getRandom(drop.getExtraAmount());
	        return new Item(drop.getItemId(), amount);
	    }
	}

	/**
	 * Handles loot sharing mechanics
	 */
	private boolean handleLootSharing(Player player, Item item, String dropName, WorldTile dropTile) {
	    if (!player.isToogleLootShare()) {
	        return false;
	    }
	    
	    FriendChatsManager fc = player.getCurrentFriendChat();
	    if (fc == null) {
	        return false;
	    }
	    
	    List<Player> eligiblePlayers = getEligibleLootSharePlayers(fc, player);
	    if (eligiblePlayers.isEmpty()) {
	        return false;
	    }
	    
	    // Handle high-value item splitting
	    if (item.getDefinitions().getTipitPrice() >= MIN_LOOTSHARE_VALUE) {
	        distributeCoins(eligiblePlayers, item, dropName);
	        return true;
	    }
	    
	    // Handle random player selection for regular items
	    Player luckyPlayer = selectRandomPlayer(eligiblePlayers);
	    distributeLoot(luckyPlayer, eligiblePlayers, item, dropName, dropTile);
	    return true;
	}

	/**
	 * Gets eligible players for loot sharing
	 */
	private List<Player> getEligibleLootSharePlayers(FriendChatsManager fc, Player referencePlayer) {
	    List<Player> eligiblePlayers = new ArrayList<Player>();
	    
	    for (Player player : fc.getPlayers()) {
	        if (player != null && player.isToogleLootShare() && 
	            player.getRegionId() == referencePlayer.getRegionId()) {
	            eligiblePlayers.add(player);
	        }
	    }
	    
	    return eligiblePlayers;
	}

	/**
	 * Distributes coins for high-value items
	 */
	private void distributeCoins(List<Player> players, Item item, String dropName) {
	    int totalValue = item.getDefinitions().getTipitPrice();
	    int sharePerPlayer = totalValue / players.size();
	    
	    for (Player player : players) {
	        if (player.getInventory() != null) {
	            player.getInventory().addItemMoneyPouch(new Item(COINS_ID, sharePerPlayer));
	            player.sendMessage(String.format(
	                "<col=115b0d>You received: %dx coins from a split of the item %s.</col>",
	                sharePerPlayer, dropName));
	        }
	    }
	}

	/**
	 * Selects random player for item distribution
	 */
	private Player selectRandomPlayer(List<Player> players) {
	    if (players.isEmpty()) {
	        return null;
	    }
	    return players.get(Utils.random(players.size()));
	}

	/**
	 * Distributes loot to selected player
	 */
	private void distributeLoot(Player luckyPlayer, List<Player> allPlayers, Item item, String dropName, WorldTile dropTile) {
	    if (luckyPlayer == null) {
	        return;
	    }
	    
	    World.addGroundItem(item, dropTile, luckyPlayer, true, LOOTSHARE_TIMEOUT);
	    luckyPlayer.sendMessage(String.format(
	        "<col=115b0d>You received: %dx %s.</col>", item.getAmount(), dropName));
	    
	    // Notify other players
	    for (Player player : allPlayers) {
	        if (player != null && !player.equals(luckyPlayer)) {
	            player.sendMessage(String.format("%s received: %dx %s.", 
	                luckyPlayer.getDisplayName(), item.getAmount(), dropName));
	        }
	    }
	}

	/**
	 * Processes clue scroll drops with consolidated logic
	 */
	private void processClueScrollDrops(Player player, WorldTile dropTile) {
	    ClueScrollDrop[] clueScrolls = {
	        new ClueScrollDrop(EASY_CLUE_ID, EASY_CLUE_RATE, "Easy"),
	        new ClueScrollDrop(MEDIUM_CLUE_ID, MEDIUM_CLUE_RATE, "Medium"),
	        new ClueScrollDrop(HARD_CLUE_ID, HARD_CLUE_RATE, "Hard"),
	        new ClueScrollDrop(ELITE_CLUE_ID, ELITE_CLUE_RATE, "Elite")
	    };
	    
	    for (ClueScrollDrop clue : clueScrolls) {
	        if (Utils.getRandom(clue.rate) <= 1) {
	            World.updateGroundItem(new Item(clue.itemId), dropTile, player, DROP_TIMEOUT, 0, true);
	            player.sendMessage("You've found a " + clue.difficulty + " clue scroll.", true);
	            World.sendGraphics(player, new Graphics(CLUE_GRAPHICS_ID), dropTile);
	            break; // Only drop one clue scroll
	        }
	    }
	}

	/**
	 * Helper class for clue scroll data
	 */
	private static class ClueScrollDrop {
	    final int itemId;
	    final int rate;
	    final String difficulty;
	    
	    ClueScrollDrop(int itemId, int rate, String difficulty) {
	        this.itemId = itemId;
	        this.rate = rate;
	        this.difficulty = difficulty;
	    }
	}

	/**
	 * Processes special ring effects
	 */
	private void processSpecialRings(Player player, Drop drop, WorldTile dropTile) {
	    if (player.getEquipment() == null) {
	        return;
	    }
	    
	    int ringId = player.getEquipment().getRingId();
	    
	    // Handle Hazelmere's signet ring drops
	    processHazelmereDrop(player, ringId, dropTile);
	    
	    // Handle Hazelmere's signet ring effects
	    processHazelmereEffects(player, ringId, drop, dropTile);
	}

	/**
	 * Processes Hazelmere's signet ring drops
	 */
	private void processHazelmereDrop(Player player, int ringId, WorldTile dropTile) {
	    int dropRate = 0;
	    
	    if (ringId == LUCK_OF_DWARVES_ID) {
	        dropRate = HAZELMERE_DROP_RATE_LOD;
	    } else if (ringId == RING_OF_WEALTH_ID) {
	        dropRate = HAZELMERE_DROP_RATE_ROW;
	    }
	    
	    if (dropRate > 0 && Utils.getRandom(dropRate) <= 1) {
	        World.updateGroundItem(new Item(HAZELMERE_SIGNET_ID), dropTile, player, DROP_TIMEOUT, 0, true);
	        World.sendGraphics(player, new Graphics(HAZELMERE_GRAPHICS), dropTile);
	        player.sendMessage("You've received a Hazelmere's signet ring.", true);
	    }
	}

	/**
	 * Processes Hazelmere's signet ring and perk effects
	 */
	private void processHazelmereEffects(Player player, int ringId, Drop drop, WorldTile dropTile) {
	    boolean hasHazelmereRing = (ringId == HAZELMERE_SIGNET_ID);
	    boolean hasHazelmereePerk = (player.getPerkManager() != null && player.getPerkManager().HazelmereLuck);
	    
	    if (!hasHazelmereRing && !hasHazelmereePerk) {
	        return;
	    }
	    
	    int procRate = hasHazelmereRing ? HAZELMERE_PROC_RATE : HAZELMERE_PERK_RATE;
	    
	    if (Utils.getRandom(procRate) <= 1) {
	        int amount = drop.getMinAmount() + Utils.getRandom(drop.getExtraAmount());
	        World.updateGroundItem(new Item(drop.getItemId(), amount), dropTile, player, DROP_TIMEOUT, 0, true);
	        World.sendGraphics(player, new Graphics(HAZELMERE_PROC_GRAPHICS), dropTile);
	        player.sendMessage("You feel the luck of Hazelmere shine.", true);
	    }
	}

	/**
	 * Processes dragon egg drops with consolidated logic
	 */
	private void processDragonEggDrops(Player player, WorldTile dropTile) {
	    String npcName = getName();
	    if (npcName == null) {
	        return;
	    }
	    
	    boolean hasDragonTrainer = (player.getPerkManager() != null && player.getPerkManager().dragonTrainer);
	    int dropRate = hasDragonTrainer ? DRAGON_TRAINER_RATE : NORMAL_DRAGON_RATE;
	    
	    DragonEggDrop eggDrop = getDragonEggDrop(npcName);
	    if (eggDrop != null && Utils.getRandom(dropRate) == 0) {
	        if (!player.hasItem(new Item(eggDrop.itemId))) {
	            World.updateGroundItem(new Item(eggDrop.itemId, 1), dropTile, player, DROP_TIMEOUT, 0, true);
	            player.sendMessage("This " + eggDrop.dragonType + 
	                " dragon was carrying an egg which hatched when dropped.", true);
	        }
	    }
	}

	/**
	 * Gets dragon egg drop data based on NPC name
	 */
	private DragonEggDrop getDragonEggDrop(String npcName) {
	    String lowerName = npcName.toLowerCase();
	    
	    if (lowerName.equals("green dragon")) {
	        return new DragonEggDrop(GREEN_DRAGON_EGG, "green");
	    } else if (lowerName.equals("blue dragon")) {
	        return new DragonEggDrop(BLUE_DRAGON_EGG, "blue");
	    } else if (lowerName.equals("red dragon")) {
	        return new DragonEggDrop(RED_DRAGON_EGG, "red");
	    } else if (lowerName.equals("black dragon") || 
	               (lowerName.equals("king black dragon") && !lowerName.contains("queen"))) {
	        return new DragonEggDrop(BLACK_DRAGON_EGG, "black");
	    }
	    
	    return null;
	}

	/**
	 * Helper class for dragon egg data
	 */
	private static class DragonEggDrop {
	    final int itemId;
	    final String dragonType;
	    
	    DragonEggDrop(int itemId, String dragonType) {
	        this.itemId = itemId;
	        this.dragonType = dragonType;
	    }
	}

	/**
	 * Handles the main drop with all effects
	 */
	private void handleMainDrop(Player player, Drop drop, Item finalItem, String dropName, WorldTile dropTile) {
	    // Handle special item effects
	    if (handleSpecialItemEffects(player, finalItem)) {
	        return;
	    }
	    
	    // Handle pet collection
	    if (handlePetCollection(player, drop, finalItem)) {
	        return;
	    }
	    
	    // Handle coin accumulator
	    processCoinAccumulator(player);
	    
	    // Handle loot beam
	    handleLootBeam(player, finalItem, dropTile);
	    
	    // Drop the item(s)
	    dropItems(player, drop, finalItem, dropTile);
	    
	    // Handle rare drop announcements
	    handleRareDropAnnouncement(player, dropName, finalItem);
	}

	/**
	 * Handles special item effects (bonecrusher, herbicide)
	 */
	private boolean handleSpecialItemEffects(Player player, Item item) {
	    if (player.getInventory() == null) {
	        return false;
	    }
	    
	    // Handle bonecrusher
	    if (player.getInventory().containsItem(BONECRUSHER_ID, 1)) {
	        if (Bonecrusher.handleDrop(player, item)) {
	            return true;
	        }
	    }
	    
	    // Handle herbicide
	    if (player.getInventory().containsItem(HERBICIDE_ID, 1)) {
	        if (Herbicide.handleDrop(player, item)) {
	            return true;
	        }
	    }
	    
	    return false;
	}

	/**
	 * Handles pet collection mechanics
	 */
	private boolean handlePetCollection(Player player, Drop drop, Item item) {
	    if (player.getPetManager() == null || !player.getPetManager().isReceivePet()) {
	        return false;
	    }
	    
	    double threshold = player.setLootBeam * PET_COLLECTION_MULTIPLIER;
	    if (item.getDefinitions().getValue() >= threshold && player.setLootBeam > PET_COLLECTION_THRESHOLD) {
	        int amount = drop.getMinAmount() + Utils.getRandom(drop.getExtraAmount());
	        player.getBank().addItem(new Item(drop.getItemId(), amount), false);
	        player.sm("<col=ff8c38>Your legendary pet has collected your drop of 50% greater than " +
	                 "your loot beam threshold, and has sent it to your bank.");
	        return true;
	    }
	    
	    return false;
	}

	/**
	 * Processes coin accumulator with balanced formula and proper scaling
	 */
	private void processCoinAccumulator(Player player) {
	    // Validate inputs
	    if (player == null) {
	        return;
	    }
	    
	    // Calculate base coin amount with balanced, scalable formula
	    int combatLevel = Math.max(1, Math.min(getCombatLevel(), 1000)); // Cap at 1000 for safety
	    double dropRate = Settings.getDropQuantityRate(player);
	    double baseMultiplier = Math.max(1.0, dropRate); // Keep as double to preserve fractional multipliers
	    
	    // Use more reasonable random range (5-25 instead of 2-100 for better consistency)
	    int randomFactor = Utils.random(5, 25);
	    
	    // Progressive scaling: higher level NPCs give proportionally more coins
	    double levelScaling = 1.0 + (combatLevel / 200.0); // Gradual scaling up to 6x at level 1000
	    
	    // Calculate with overflow protection (keep calculation in double for precision)
	    double baseCalculation = combatLevel * randomFactor * baseMultiplier;
	    double scaledCalculation = baseCalculation * levelScaling;
	    
	    // Apply reasonable bounds based on combat level
	    int minCoins = Math.max(1, combatLevel / 10); // Minimum scales with level
	    int maxCoins = Math.min(combatLevel * 50, 25000); // Maximum scales but caps at 25k
	    
	    // Convert to int with proper rounding and clamp to range
	    int coins = (int) Math.round(scaledCalculation);
	    coins = Math.max(minCoins, Math.min(coins, maxCoins));
	    
	    // Process with coin accumulator system
	    CoinAccumulator.handleCoinAccumulator(player, this, coins);
	}

	/**
	 * Handles loot beam effect (fixed infinite loop bug)
	 */
	private void handleLootBeam(Player player, Item item, final WorldTile dropTile) {
	    if (item.getDefinitions().getValue() < player.setLootBeam) {
	        return;
	    }
	    
	    // Send initial loot beam
	    World.sendGraphics(player, new Graphics(LOOT_BEAM_GRAPHICS), dropTile);
	    player.sm("<col=ff8c38>A rainbow of wealth came from one of your items.");
	    
	    // Schedule additional beams (fixed the broken loop)
	    WorldTasksManager.schedule(new WorldTask() {
	        private int beamCount = 0;
	        
	        @Override
	        public void run() {
	            beamCount++;
	            if (beamCount <= 2) {
	                World.sendGraphics(player, new Graphics(LOOT_BEAM_GRAPHICS), dropTile);
	            } else {
	                stop();
	            }
	        }
	    }, 1, 1);
	}

	/**
	 * Drops the actual items
	 */
	private void dropItems(Player player, Drop drop, Item finalItem, WorldTile dropTile) {
	    int amount = drop.getMinAmount() + Utils.getRandom(drop.getExtraAmount());
	    
	    if (Settings.doubleDrop) {
	        // Drop two items for double drop event
	        World.updateGroundItem(new Item(drop.getItemId(), amount), dropTile, player, DROP_TIMEOUT, 0, true);
	        World.updateGroundItem(new Item(drop.getItemId(), amount), dropTile, player, DROP_TIMEOUT, 0, true);
	    } else {
	        // Drop single item
	        World.updateGroundItem(new Item(drop.getItemId(), amount), dropTile, player, DROP_TIMEOUT, 0, true);
	    }
	}

	/**
	 * Handles rare drop announcements with consolidated logic
	 */
	private void handleRareDropAnnouncement(Player player, String dropName, Item item) {
	    if (!isRareDropItem(dropName)) {
	        return;
	    }
	    
	    String message = formatDropMessage(dropName);
	    boolean isDoubleDrop = Settings.doubleDrop;
	    
	    String announcement = Colors.orange + "<shad=000000><img=6>News: " + 
	                         player.getDisplayName() + " received " + 
	                         (isDoubleDrop ? "a double drop of " : "a drop of ") + 
	                         message + " from " + getName() + ".";
	    
	    World.sendWorldMessage(announcement, false);
	}

	/**
	 * Checks if item is considered a rare drop for announcements
	 */
	private boolean isRareDropItem(String dropName) {
	    String[] rareItems = {
	        "pernix", "torva", "virtus", "ascension", "bandos", "hilt", "sirenic",
	        "armadyl", "spirit shield", "fire warrior", "saradomin", "dragon claw",
	        "dragon full", "dragon pick", "zaryte", "boogie", "high armour",
	        "thalassia's revenge", "rage of hyu-ji", "winds of waiko", "steadf",
	        "glaiven", "ragef", "hiss", "murmur", "whisper", "ascension grip",
	        "drygore", "razor", "subjugation", "draconic", "crest", "zamorak",
	        "dragon kite", "dragon rider lance", "scythe", "seismic", "elixi",
	        "sigil", "zamorakian spear", "cywir", "vanguard", "dormant", "magister",
	        "anima", "achto", "retro", "raptor", "ripper", "wyvern crossbow",
	        "gloves of passage", "hazelmere", "chest", "imperium core",
	        "wand of the praesul", "cinderbane", "blightbound", "staff of light",
	        "hydrix", "luck of dwarves"
	    };
	    
	    for (String rareItem : rareItems) {
	        if (dropName.contains(rareItem)) {
	            // Additional filters for specific items
	            if (rareItem.equals("armadyl") && (dropName.contains("rune") || dropName.contains("shard"))) {
	                continue;
	            }
	            if (rareItem.equals("saradomin") && dropName.contains("brew")) {
	                continue;
	            }
	            if (rareItem.equals("zamorak") && (dropName.contains("wine") || dropName.contains("brew"))) {
	                continue;
	            }
	            return true;
	        }
	    }
	    
	    return false;
	}

	/**
	 * Formats the drop message for announcements
	 */
	private String formatDropMessage(String dropName) {
	    if (dropName.contains("gloves") || dropName.contains("boots")) {
	        return "a pair of " + dropName;
	    }
	    
	    if (dropName.contains("chaps") || dropName.contains("tassets")) {
	        return dropName;
	    }
	    
	    return Utils.getAorAn(dropName) + " " + dropName;
	}

	@Override
	public void finish() {
		if (hasFinished())
			return;
		setFinished(true);
		World.updateEntityRegion(this);
		World.removeNPC(this);
	}

	public void forceWalkRespawnTile() {
		setForceWalk(respawnTile);
	}

	public int[] getBonuses() {
		return bonuses;
	}

	public int getCapDamage() {
		return capDamage;
	}

	public NPCCombat getCombat() {
		return combat;
	}

	public NPCCombatDefinitions getCombatDefinitions() {
	    // First check if there are custom boss definitions from boss balancer
	    NPCCombatDefinitions custom = NPCCombatDefinitionsManager.getCombatDefinitions(getId());
	    if (custom != null) {
	        return custom; // Use custom boss stats
	    }
	    
	    // If we have a stored combat definition, use it
	    if (combatDefinitions != null) {
	        return combatDefinitions;
	    }
	    
	    // Otherwise return original combat definitions
	    return NPCCombatDefinitionsL.getNPCCombatDefinitions(id);
	}

	public int getCombatLevel() {
		return combatLevel >= 0 ? combatLevel : getDefinitions().combatLevel;
	}

	public int getCustomCombatLevel() {
		return combatLevel;
	}

	public Item getDefender() {
		int id = 8844;
		if (containsItem(8850) || containsItem(20072)) {
			id = 20072;
		} else if (containsItem(8849) || containsItem(8850)) {
			id = 8850;
		} else if (containsItem(8848)) {
			id = 8849;
		} else if (containsItem(8847)) {
			id = 8848;
		} else if (containsItem(8846)) {
			id = 8847;
		} else if (containsItem(8845)) {
			id = 8846;
		} else if (containsItem(8844)) {
			id = 8845;
		} else {
			id = 8844;
		}
		return new Item(id);
	}

	public NPCDefinitions getDefinitions() {
		return NPCDefinitions.getNPCDefinitions(id);
	}

	public int getForceTargetDistance() {
		return forceTargetDistance;
	}

	public int getId() {
		return id;
	}

	public int getLureDelay() {
		return lureDelay;
	}

	@Override
	public double getMagePrayerMultiplier() {
		return 0;
	}

	public int getMapAreaNameHash() {
		return mapAreaNameHash;
	}

	public int getMaxHit() {
		return getCombatDefinitions().getMaxHit();
	}

	public int getMaxHit(int attackStyle) {
		return getCombatDefinitions().getMaxHit();
	}

	@Override
	public int getMaxHitpoints() {
	    // First check for boss balancer custom definitions
	    NPCCombatDefinitions customDefs = NPCCombatDefinitionsManager.getCombatDefinitions(getId());
	    if (customDefs != null) {
	        return customDefs.getHitpoints();  // Use boss balancer HP
	    }
	    
	    // Then check NPCCombatHPCommand
		/*
		 * if (NPCCombatHPCommand.hasHPModification(getId())) { return
		 * NPCCombatHPCommand.getModifiedMaxHP(getId()); }
		 */
	    if (ZombieManager.isZombie(this)) {
	        return 100 * ZOGame.waveCount;
	    }
	    
	    return getCombatDefinitions().getHitpoints();
	}

	@Override
	public double getMeleePrayerMultiplier() {
		return 0;
	}

	public WorldTile getMiddleWorldTile() {
		int size = getSize();
		return new WorldTile(getCoordFaceX(size), getCoordFaceY(size), getPlane());
	}

	public String getName() {
		return name != null ? name : getDefinitions().name;
	}

	public Transformation getNextTransformation() {
		return nextTransformation;
	}

	public ArrayList<Entity> getPossibleTargets(boolean checkNPCs, boolean checkPlayers) {
		int size = getSize();
		int agroRatio = 1;
		ArrayList<Entity> possibleTarget = new ArrayList<Entity>();
		for (int regionId : getMapRegionsIds()) {
			if (checkPlayers) {
				List<Integer> playerIndexes = World.getRegion(regionId).getPlayerIndexes();
				if (playerIndexes != null) {
					for (int playerIndex : playerIndexes) {
						Player player = World.getPlayers().get(playerIndex);
						if (player == null || player.isDead() || player.hasFinished() || !player.isRunning()
						/* || player.getGlobalPlayerUpdater().isHidden() */
								|| !Utils.isOnRange(getX(), getY(), size, player.getX(), player.getY(),
										player.getSize(), forceTargetDistance > 0 ? forceTargetDistance : agroRatio)
								|| (!forceMultiAttacked && (!isAtMultiArea() || !player.isAtMultiArea())
										&& (player.getAttackedBy() != this
												&& (player.getAttackedByDelay() > Utils.currentTimeMillis()
														|| player.getFindTargetDelay() > Utils.currentTimeMillis())))
								|| !clipedProjectile(player, false) || (!forceAgressive && !Wilderness.isAtWild(this)
										&& player.getSkills().getCombatLevelWithSummoning() >= getCombatLevel() * 2))
							continue;
						possibleTarget.add(player);
					}
				}
			}
			if (checkNPCs) {
				List<Integer> npcsIndexes = World.getRegion(regionId).getNPCsIndexes();
				if (npcsIndexes != null) {
					for (int npcIndex : npcsIndexes) {
						NPC npc = World.getNPCs().get(npcIndex);
						if (npc == null || npc == this || npc.isDead() || npc.hasFinished()
								|| !Utils.isOnRange(getX(), getY(), size, npc.getX(), npc.getY(), npc.getSize(),
										forceTargetDistance > 0 ? forceTargetDistance : agroRatio)
								|| !npc.getDefinitions().hasAttackOption()
								|| ((!isAtMultiArea() || !npc.isAtMultiArea()) && npc.getAttackedBy() != this
										&& npc.getAttackedByDelay() > Utils.currentTimeMillis())
								|| !clipedProjectile(npc, false))
							continue;
						possibleTarget.add(npc);
					}
				}
			}
		}
		return possibleTarget;
	}

	public ArrayList<Entity> getPossibleTargets() {
		return getPossibleTargets(false, true);
	}

	@Override
	public double getRangePrayerMultiplier() {
		return 0;
	}

	public int getRespawnDirection() {
		NPCDefinitions definitions = getDefinitions();
		if (definitions.contrast << 32 != 0 && definitions.respawnDirection > 0 && definitions.respawnDirection <= 8)
			return (4 + definitions.respawnDirection) << 11;
		return 0;
	}

	public WorldTile getRespawnTile() {
		return respawnTile;
	}

	@Override
	public int getSize() {
		return getDefinitions().size;
	}

	@Override
	public void handleIngoingHit(final Hit hit) {
		if (capDamage != -1 && hit.getDamage() > capDamage)
			hit.setDamage(capDamage);
		if (hit.getLook() != HitLook.MELEE_DAMAGE && hit.getLook() != HitLook.RANGE_DAMAGE
				&& hit.getLook() != HitLook.MAGIC_DAMAGE)
			return;
		Entity source = hit.getSource();
		if (source == null)
			return;
		if (getName().equalsIgnoreCase("Barrelchest") && hit.getLook() != HitLook.MELEE_DAMAGE) {
			hit.setDamage(0);
			if (hit.getSource() instanceof Player) {
				((Player) source).sm("You need melee to hit this boss.");
			}
		}
		if (source instanceof Player) {
			final Player p2 = (Player) source;
			if (BossTimeUpdate == null) {
				BossTimeUpdate = new ArrayList<>();
			}
			if (getHitpoints() > getMaxHitpoints() - getMaxHitpoints() / 12) {
				if (!BossTimeUpdate.contains(source)) {
					if (((Player) source).getEquipment().getWeaponId() != 25202) {
						BossTimeUpdate.add(source);
					}
				}
			}
			if (p2.getPrayer().hasPrayersOn()) {
				if (p2.getPrayer().usingPrayer(1, 18))
					sendSoulSplit(hit, p2);
				if (hit.getDamage() == 0)
					return;
				if (!p2.getPrayer().isBoostedLeech()) {
					if (hit.getLook() == HitLook.MELEE_DAMAGE) {
						if (p2.getPrayer().usingPrayer(1, 19) || p2.getPrayer().usingPrayer(1, 20)
								|| p2.getPrayer().usingPrayer(1, 21) || p2.getPrayer().usingPrayer(1, 22)
								|| p2.getPrayer().usingPrayer(1, 23) || p2.getPrayer().usingPrayer(1, 24)) {
							int type = p2.getPrayer().usingPrayer(1, 19) ? 0
									: p2.getPrayer().usingPrayer(1, 20) ? 1
											: p2.getPrayer().usingPrayer(1, 21) ? 2
													: p2.getPrayer().usingPrayer(1, 22) ? 3
															: p2.getPrayer().usingPrayer(1, 23) ? 4 : 5;
							p2.getPrayer().increaseTurmoilBonus(this, type);
							p2.getPrayer().setBoostedLeech(true);
							return;
						} else if (p2.getPrayer().usingPrayer(1, 1)) { // sap
							// att
							if (Utils.getRandom(4) == 0) {
								if (p2.getPrayer().reachedMax(0)) {
									p2.getPackets().sendGameMessage(
											"Your opponent has been weakened so much that your sap curse has no effect.",
											true);
								} else {
									p2.getPrayer().increaseLeechBonus(0);
									p2.getPackets().sendGameMessage(
											"Your curse drains Attack from the enemy, boosting your Attack.", true);
								}
								p2.setNextAnimation(new Animation(12569));
								p2.setNextGraphics(new Graphics(2214));
								p2.getPrayer().setBoostedLeech(true);
								World.sendProjectile(p2, this, 2215, 35, 35, 20, 5, 0, 0);
								WorldTasksManager.schedule(new WorldTask() {
									@Override
									public void run() {
										setNextGraphics(new Graphics(2216));
									}
								}, 1);
								return;
							}
						} else {
							if (p2.getPrayer().usingPrayer(1, 10)) {
								if (Utils.getRandom(7) == 0) {
									if (p2.getPrayer().reachedMax(3)) {
										p2.getPackets().sendGameMessage(
												"Your opponent has been weakened so much that your leech curse has no effect.",
												true);
									} else {
										p2.getPrayer().increaseLeechBonus(3);
										p2.getPackets().sendGameMessage(
												"Your curse drains Attack from the enemy, boosting your Attack.", true);
									}
									p2.setNextAnimation(new Animation(12575));
									p2.getPrayer().setBoostedLeech(true);
									World.sendProjectile(p2, this, 2231, 35, 35, 20, 5, 0, 0);
									WorldTasksManager.schedule(new WorldTask() {

										@Override
										public void run() {
											setNextGraphics(new Graphics(2232));
										}
									}, 1);
									return;
								}
							}
							if (p2.getPrayer().usingPrayer(1, 14)) {
								if (Utils.getRandom(7) == 0) {
									if (p2.getPrayer().reachedMax(7)) {
										p2.getPackets().sendGameMessage(
												"Your opponent has been weakened so much that your leech curse has no effect.",
												true);
									} else {
										p2.getPrayer().increaseLeechBonus(7);
										p2.getPackets().sendGameMessage(
												"Your curse drains Strength from the enemy, boosting your Strength.",
												true);
									}
									p2.setNextAnimation(new Animation(12575));
									p2.getPrayer().setBoostedLeech(true);
									World.sendProjectile(p2, this, 2248, 35, 35, 20, 5, 0, 0);
									WorldTasksManager.schedule(new WorldTask() {
										@Override
										public void run() {
											setNextGraphics(new Graphics(2250));
										}
									}, 1);
									return;
								}
							}

						}
					}
					if (hit.getLook() == HitLook.RANGE_DAMAGE) {
						if (p2.getPrayer().usingPrayer(1, 2)) { // sap range
							if (Utils.getRandom(4) == 0) {
								if (p2.getPrayer().reachedMax(1)) {
									p2.getPackets().sendGameMessage(
											"Your opponent has been weakened so much that your sap curse has no effect.",
											true);
								} else {
									p2.getPrayer().increaseLeechBonus(1);
									p2.getPackets().sendGameMessage(
											"Your curse drains Range from the enemy, boosting your Range.", true);
								}
								p2.setNextAnimation(new Animation(12569));
								p2.setNextGraphics(new Graphics(2217));
								p2.getPrayer().setBoostedLeech(true);
								World.sendProjectile(p2, this, 2218, 35, 35, 20, 5, 0, 0);
								WorldTasksManager.schedule(new WorldTask() {

									@Override
									public void run() {
										setNextGraphics(new Graphics(2219));
									}
								}, 1);
								return;
							}
						} else if (p2.getPrayer().usingPrayer(1, 11)) {
							if (Utils.getRandom(7) == 0) {
								if (p2.getPrayer().reachedMax(4)) {
									p2.getPackets().sendGameMessage(
											"Your opponent has been weakened so much that your leech curse has no effect.",
											true);
								} else {
									p2.getPrayer().increaseLeechBonus(4);
									p2.getPackets().sendGameMessage(
											"Your curse drains Range from the enemy, boosting your Range.", true);
								}
								p2.setNextAnimation(new Animation(12575));
								p2.getPrayer().setBoostedLeech(true);
								World.sendProjectile(p2, this, 2236, 35, 35, 20, 5, 0, 0);
								WorldTasksManager.schedule(new WorldTask() {
									@Override
									public void run() {
										setNextGraphics(new Graphics(2238));
									}
								});
								return;
							}
						}
					}
					if (hit.getLook() == HitLook.MAGIC_DAMAGE) {
						if (p2.getPrayer().usingPrayer(1, 3)) { // sap mage
							if (Utils.getRandom(4) == 0) {
								if (p2.getPrayer().reachedMax(2)) {
									p2.getPackets().sendGameMessage(
											"Your opponent has been weakened so much that your sap curse has no effect.",
											true);
								} else {
									p2.getPrayer().increaseLeechBonus(2);
									p2.getPackets().sendGameMessage(
											"Your curse drains Magic from the enemy, boosting your Magic.", true);
								}
								p2.setNextAnimation(new Animation(12569));
								p2.setNextGraphics(new Graphics(2220));
								p2.getPrayer().setBoostedLeech(true);
								World.sendProjectile(p2, this, 2221, 35, 35, 20, 5, 0, 0);
								WorldTasksManager.schedule(new WorldTask() {

									@Override
									public void run() {
										setNextGraphics(new Graphics(2222));
									}
								}, 1);
								return;
							}
						} else if (p2.getPrayer().usingPrayer(1, 12)) {
							if (Utils.getRandom(7) == 0) {
								if (p2.getPrayer().reachedMax(5)) {
									p2.getPackets().sendGameMessage(
											"Your opponent has been weakened so much that your leech curse has no effect.",
											true);
								} else {
									p2.getPrayer().increaseLeechBonus(5);
									p2.getPackets().sendGameMessage(
											"Your curse drains Magic from the enemy, boosting your Magic.", true);
								}
								p2.setNextAnimation(new Animation(12575));
								p2.getPrayer().setBoostedLeech(true);
								World.sendProjectile(p2, this, 2240, 35, 35, 20, 5, 0, 0);
								WorldTasksManager.schedule(new WorldTask() {
									@Override
									public void run() {
										setNextGraphics(new Graphics(2242));
									}
								}, 1);
								return;
							}
						}
					}

					// overall

					if (p2.getPrayer().usingPrayer(1, 13)) { // leech defence
						if (Utils.getRandom(10) == 0) {
							if (p2.getPrayer().reachedMax(6)) {
								p2.getPackets().sendGameMessage(
										"Your opponent has been weakened so much that your leech curse has no effect.",
										true);
							} else {
								p2.getPrayer().increaseLeechBonus(6);
								p2.getPackets().sendGameMessage(
										"Your curse drains Defence from the enemy, boosting your Defence.", true);
							}
							p2.setNextAnimation(new Animation(12575));
							p2.getPrayer().setBoostedLeech(true);
							World.sendProjectile(p2, this, 2244, 35, 35, 20, 5, 0, 0);
							WorldTasksManager.schedule(new WorldTask() {

								@Override
								public void run() {
									setNextGraphics(new Graphics(2246));
								}

							}, 1);
							return;
						}
					}
				}
			}
		}

	}

	public boolean hasChangedCombatLevel() {
		return changedCombatLevel;
	}

	public boolean hasDefender() {
		if (containsItem(8844) || containsItem(8845) || containsItem(8846) || containsItem(8847) || containsItem(8848)
				|| containsItem(8849) || containsItem(8850) || containsItem(20072)) {
			return true;
		}
		return false;
	}

	public boolean hasForceWalk() {
		return forceWalk != null;
	}

	public boolean hasRandomWalk() {
		return randomwalk;
	}

	public boolean isCantFollowUnderCombat() {
		return cantFollowUnderCombat;
	}

	public boolean isCantInteract() {
		return cantInteract;
	}

	public boolean isClueScrollNPC(String npcName) {
		switch (npcName) {
		case "'Rum'-pumped crab":
		case "Aberrant spectre":
		case "Abyssal demon":
		case "Abyssal leech":
		case "Air elemental":
		case "Ancient mage":
		case "Ancient ranger":
		case "Ankou":
		case "Armoured zombie":
		case "Arrg":
		case "Aviansie":
		case "Bandit":
		case "Banshee":
		case "Barbarian":
		case "Barbarian woman":
		case "Basilisk":
		case "Black Guard":
		case "Black Guard Berserker":
		case "Black Guard crossbowdwarf":
		case "Black Heather":
		case "Black Knight":
		case "Black Knight Titan":
		case "Black demon":
		case "Black dragon":
		case "Blood reaver":
		case "Bloodveld":
		case "Blue dragon":
		case "Bork":
		case "Brine rat":
		case "Bronze dragon":
		case "Brutal green dragon":
		case "Catablepon":
		case "Cave bug":
		case "Cave crawler":
		case "Cave horror":
		case "Cave slime":
		case "Chaos Elemental":
		case "Chaos druid":
		case "Chaos druid warrior":
		case "Chaos dwarf":
		case "Chaos dwarf hand cannoneer":
		case "Chaos dwogre":
		case "Cockatrice":
		case "Cockroach drone":
		case "Cockroach soldier":
		case "Cockroach worker":
		case "Columbarium":
		case "Columbarium key":
		case "Commander Zilyana":
		case "Corporeal Beast":
		case "Crawling Hand":
		case "Cyclops":
		case "Cyclossus":
		case "Dagannoth":
		case "Dagannoth Prime":
		case "Dagannoth Rex":
		case "Dagannoth Supreme":
		case "Dagannoth guardian":
		case "Dagannoth spawn":
		case "Dark beast":
		case "Desert Lizard":
		case "Desert strykewyrm":
		case "Dried zombie":
		case "Dust devil":
		case "Dwarf":
		case "Earth elemental":
		case "Earth warrior":
		case "Elf warrior":
		case "Elite Black Knight":
		case "Elite Dark Ranger":
		case "Elite Khazard guard":
		case "Exiled Kalphite Queen":
		case "Exiled kalphite guardian":
		case "Exiled kalphite marauder":
		case "Ferocious barbarian spirit":
		case "Fire elemental":
		case "Fire giant":
		case "Flesh Crawler":
		case "Forgotten Archer":
		case "Forgotten Mage":
		case "Forgotten Warrior":
		case "Frog":
		case "Frost dragon":
		case "Ganodermic beast":
		case "Gargoyle":
		case "General Graardor":
		case "General malpractitioner":
		case "Ghast":
		case "Ghostly warrior":
		case "Giant Mole":
		case "Giant ant soldier":
		case "Giant ant worker":
		case "Giant rock crab":
		case "Giant skeleton":
		case "Giant wasp":
		case "Glacor":
		case "Glod":
		case "Gnoeals":
		case "Goblin statue":
		case "Gorak":
		case "Greater demon":
		case "Greater reborn mage":
		case "Greater reborn ranger":
		case "Greater reborn warrior":
		case "Green dragon":
		case "Grotworm":
		case "Haakon the Champion":
		case "Harold":
		case "Harpie Bug Swarm":
		case "Hill giant":
		case "Hobgoblin":
		case "Ice giant":
		case "Ice strykewyrm":
		case "Ice troll":
		case "Ice troll female":
		case "Ice troll male":
		case "Ice troll runt":
		case "Ice warrior":
		case "Icefiend":
		case "Iron dragon":
		case "Jelly":
		case "Jogre":
		case "Jungle horror":
		case "Jungle strykewyrm":
		case "K'ril Tsutsaroth":
		case "Kalphite Guardian":
		case "Kalphite King":
		case "Kalphite Queen":
		case "Kalphite Soldier":
		case "Kalphite Worker":
		case "Killerwatt":
		case "King Black Dragon":
		case "Kraka":
		case "Kree'arra":
		case "Kurask":
		case "Lanzig":
		case "Lesser demon":
		case "Lesser reborn mage":
		case "Lesser reborn ranger":
		case "Lesser reborn warrior":
		case "Lizard":
		case "Locust lancer":
		case "Locust ranger":
		case "Locust rider":
		case "Mature grotworm":
		case "Mighty banshee":
		case "Minotaur":
		case "Mithril dragon":
		case "Molanisk":
		case "Moss giant":
		case "Mountain troll":
		case "Mummy":
		case "Mutated bloodveld":
		case "Mutated jadinko male":
		case "Mutated zygomite":
		case "Nechryael":
		case "Nex":
		case "Ogre":
		case "Ogre statue":
		case "Ork statue":
		case "Otherworldly being":
		case "Ourg statue":
		case "Paladin":
		case "Pee Hat":
		case "Pirate":
		case "Pyrefiend":
		case "Queen Black Dragon":
		case "Red dragon":
		case "Rock lobster":
		case "Rockslug":
		case "Salarin the Twisted":
		case "Scabaras lancer":
		case "Scarab mage":
		case "Sea Snake Hatchling":
		case "Shadow warrior":
		case "Skeletal Wyvern":
		case "Skeletal miner":
		case "Skeleton":
		case "Skeleton fremennik":
		case "Skeleton thug":
		case "Skeleton warlord":
		case "Small Lizard":
		case "Soldier":
		case "Sorebones":
		case "Speedy Keith":
		case "Spiritual mage":
		case "Spiritual warrior":
		case "Steel dragon":
		case "Stick":
		case "Suqah":
		case "Terror dog":
		case "Thrower Troll":
		case "Thug":
		case "Tortured soul":
		case "Trade floor guard":
		case "Tribesman":
		case "Troll general":
		case "Troll spectator":
		case "Tstanon Karlak":
		case "Turoth":
		case "Tyras guard":
		case "TzHaar-Hur":
		case "TzHaar-Ket":
		case "TzHaar-Mej":
		case "TzHaar-Xil":
		case "Undead troll":
		case "Vampyre":
		case "Vyre corpse":
		case "Vyrelady":
		case "Vyrelord":
		case "Vyrewatch":
		case "Wallasalki":
		case "Warped terrorbird":
		case "Warped tortoise":
		case "Warrior":
		case "Water elemental":
		case "Waterfiend":
		case "Werewolf":
		case "White Knight":
		case "WildyWyrm":
		case "Yeti":
		case "Yuri":
		case "Zakl'n Gritch":
		case "Zombie":
		case "Zombie hand":
		case "Zombie swab":
			return true;
		}
		return false;
	}

	public boolean isCyclops(String npcName) {
		switch (npcName) {
		case "Cyclops":
			return true;
		}
		return false;
	}

	public boolean isFamiliar() {
		return this instanceof Familiar;
	}

	public boolean isForceAgressive() {
		return forceAgressive;
	}

	public boolean isForceFollowClose() {
		return forceFollowClose;
	}

	public boolean isForceMultiAttacked() {
		return forceMultiAttacked;
	}

	public boolean isForceWalking() {
		return forceWalk != null;
	}

	/**
	 * Gets the locked.
	 * 
	 * @return The locked.
	 */
	public boolean isLocked() {
		return locked;
	}

	public boolean isNoDistanceCheck() {
		return noDistanceCheck;
	}

	public boolean isSpawned() {
		return spawned;
	}

	public boolean isUnderCombat() {
		return combat.underCombat();
	}

	@Override
	public boolean needMasksUpdate() {
		return super.needMasksUpdate() || refreshHeadIcon || nextTransformation != null || changedCombatLevel
				|| changedName;
	}

	public void setNextNPCTransformation(int id) {
		setNPC(id);
		nextTransformation = new Transformation(id);
		if (getCustomCombatLevel() != -1)
			changedCombatLevel = true;
		if (getCustomName() != null)
			changedName = true;
	}

	public String getCustomName() {
		return name;
	}

	@Override
	public void processEntity() {
		super.processEntity();
		processNPC();
	}

	/**
	 * We init custom NPC settings.
	 */
	public void loadNPCSettings() {
		if (id == 6892) {
			setName("Pet Manager");
			setRandomWalk(0);
		}
		for (int npcId : Settings.NON_WALKING_NPCS) {
			if (npcId == id) {
				setRandomWalk(0);
				break;
			}
		}
		for (SlayerMaster master : SlayerMaster.values()) {
			if (master == null)
				continue;
			if (master.getNPCId() == id) {
				setRandomWalk(0);
				break;
			}
		}
		for (int npcId : Settings.FORCE_WALKING_NPCS) {
			if (npcId == id) {
				setRandomWalk(NORMAL_WALK);
				break;
			}
		}
		if (id == 6139) {
			setName(Settings.SERVER_NAME + "'s welcomer");
			setRandomWalk(0);
		}
	}

	public void processNPC() {
		if (isDead() || locked || isLock())
			return;
		loadNPCSettings();
		if (!combat.process()) {
			if (!isForceWalking()) {
				if (!cantInteract) {
					if (!checkAgressivity()) {
						if (getFreezeDelay() < Utils.currentTimeMillis()) {
							if (!ZombieManager.isZombie(this)) {
								if (!hasWalkSteps() && (walkType & NORMAL_WALK) != 0) {
									boolean can = false;
									for (int i = 0; i < 2; i++) {
										if (Math.random() * 1000.0 < 100.0) {
											can = true;
											break;
										}
									}
									if (can) {
										int moveX = (int) Math.round(Math.random() * 10.0 - 5.0);
										int moveY = (int) Math.round(Math.random() * 10.0 - 5.0);
										resetWalkSteps();
										if (getMapAreaNameHash() != -1) {
											if (!MapAreas.isAtArea(getMapAreaNameHash(), this)) {
												forceWalkRespawnTile();
												return;
											}
											addWalkSteps(getX() + moveX, getY() + moveY, 5, (walkType & FLY_WALK) == 0);
										} else
											addWalkSteps(respawnTile.getX() + moveX, respawnTile.getY() + moveY, 5,
													(walkType & FLY_WALK) == 0);
									}
								}
							}
						}
					}
				}
			}
		}

		if (isBoss(this) && BossTimeUpdate.size() > 0) {
			ArrayList<Entity> PossibleTargs = this.getPossibleTargets();
			for (Iterator<Entity> iter = BossTimeUpdate.iterator(); iter.hasNext();) {
				Entity e = iter.next();
				if (e instanceof Player) {
					if (!PossibleTargs.contains(e)) {
						iter.remove();
					}
				}
			}
		}
		if (isForceWalking()) {
			if (getFreezeDelay() < Utils.currentTimeMillis()) {
				if (getX() != forceWalk.getX() || getY() != forceWalk.getY()) {
					if (!hasWalkSteps()) {
						int steps = RouteFinder.findRoute(RouteFinder.WALK_ROUTEFINDER, getX(), getY(), getPlane(),
								getSize(), new FixedTileStrategy(forceWalk.getX(), forceWalk.getY()), true);
						int[] bufferX = RouteFinder.getLastPathBufferX();
						int[] bufferY = RouteFinder.getLastPathBufferY();
						for (int i = steps - 1; i >= 0; i--) {
							if (!addWalkSteps(bufferX[i], bufferY[i], 25, true))
								break;
						}
					}
					if (!hasWalkSteps()) {
						setNextWorldTile(new WorldTile(forceWalk));
						forceWalk = null;
					}
				} else
					forceWalk = null;
			}
		}
		if (ZombieManager.isZombie(this)) {
			setName("Wave " + ZOGame.waveCount + " zombie");
		}
		if (id == 2998 && Utils.random(50) == 0)
			setNextForceTalk(new ForceTalk("Welcome to Casino!"));
		if (id == 162 && Utils.random(20) == 0)
			setNextForceTalk(new ForceTalk(gnomeTrainerForceTalk[Utils.random(gnomeTrainerForceTalk.length)]));
		if (id == 15786 && Utils.random(20) == 0)
			setNextForceTalk(new ForceTalk(trialAnnouncerForceTalk[Utils.random(trialAnnouncerForceTalk.length)]));
	}

	public void removeTarget() {
		if (combat.getTarget() == null)
			return;
		combat.removeTarget();
	}

	@Override
	public void reset() {
		super.reset();
		setDirection(getRespawnDirection());
		combat.reset();
		bonuses = NPCBonuses.getBonuses(id); // back to real bonuses
		forceWalk = null;
	}

	/**
	 * checks if the npc is a boss from the settings array
	 * 
	 * @param npc
	 * @return
	 */
	private boolean isBoss(NPC npc) {
		for (int bossId : Settings.BOSS_IDS) {
			if (npc.getId() == bossId) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void resetMasks() {
		super.resetMasks();
		nextTransformation = null;
		changedCombatLevel = false;
		changedName = false;
		refreshHeadIcon = false;
	}

	@Override
	public void sendDeath(final Entity source) {
		final NPCCombatDefinitions defs = getCombatDefinitions();
		resetWalkSteps();
		combat.removeTarget();
		setNextAnimation(null);
		WorldTasksManager.schedule(new WorldTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0)
					setNextAnimation(new Animation(defs.getDeathEmote()));
				else if (loop >= defs.getDeathDelay()) {
					if (source instanceof Player) {
						Player player = (Player) source;
						player.getControlerManager().processNPCDeath(NPC.this);
					}
					drop();
					BossTimeUpdate = new ArrayList<>();
					reset();
					setLocation(respawnTile);
					finish();
					if (!isSpawned())
						setRespawnTask();
					stop();
					if (ZombieManager.isZombie(NPC.this)) {
						ZombieManager.sendZombieDeath(NPC.this);
					}
				}
				loop++;
			}
		}, 0, 1);
	}

	
	/**
	 * Sends Soulsplit - Player to NPC.
	 * 
	 * @param hit  The Hit.
	 * @param user The Entity (Player) using SoulSplit.
	 */
	public void sendSoulSplit(final Hit hit, final Entity user) {
		final NPC target = this;
		if (hit.getDamage() > 0) {
			World.sendProjectile(user, this, 2263, 11, 11, 20, 5, 0, 0);
		}
		Player player = (Player) user;
		if (player.isDonator()) {
			user.heal(hit.getDamage() / 4);
		} else {
			user.heal(hit.getDamage() / 5);
		}
		double heal = 5.0;
		/** Amulet of Souls **/
		if (((Player) user).getEquipment().getAmuletId() == 31875 && Utils.random(100) >= 50)
			heal -= Utils.random(1.25, 2.5); // We reduce since it divides
												// damage by heal
		user.heal((int) (hit.getDamage() / heal));
		WorldTasksManager.schedule(new WorldTask() {

			@Override
			public void run() {
				setNextGraphics(new Graphics(2264));
				if (hit.getDamage() > 0)
					World.sendProjectile(target, user, 2263, 11, 11, 20, 5, 0, 0);
			}
		}, 1);
	}

	@Override
	public void setAttackedBy(Entity target) {
		super.setAttackedBy(target);
		if (target == combat.getTarget() && !(combat.getTarget() instanceof Familiar))
			lastAttackedByTarget = Utils.currentTimeMillis();
	}

	public void tripleBonuses() {
		for (int i = 0; i < bonuses.length; i++)
			this.bonuses[i] = bonuses[i] * 3;
	}

	public void resetBonuses() {
		this.bonuses = NPCBonuses.getBonuses(this.getId());
	}

	public void setBonuses() {
		bonuses = NPCBonuses.getBonuses(id);
		if (bonuses == null) {
			bonuses = new int[10];
			int level = getCombatLevel();
			for (int i = 0; i < bonuses.length; i++)
				bonuses[i] = level;
		}
	}

	public void setCanBeAttackFromOutOfArea(boolean b) {
		canBeAttackFromOutOfArea = b;
	}

	public void setCantFollowUnderCombat(boolean canFollowUnderCombat) {
		this.cantFollowUnderCombat = canFollowUnderCombat;
	}

	public void setCantInteract(boolean cantInteract) {
		this.cantInteract = cantInteract;
		if (cantInteract)
			combat.reset();
	}

	public void setCapDamage(int capDamage) {
		this.capDamage = capDamage;
	}

	public void setCombatLevel(int level) {
		combatLevel = getDefinitions().combatLevel == level ? -1 : level;
		changedCombatLevel = true;
	}

	public void setForceAgressive(boolean forceAgressive) {
		this.forceAgressive = forceAgressive;
	}

	public void setForceFollowClose(boolean forceFollowClose) {
		this.forceFollowClose = forceFollowClose;
	}

	public void setForceMultiAttacked(boolean forceMultiAttacked) {
		this.forceMultiAttacked = forceMultiAttacked;
	}

	public void setForceTargetDistance(int forceTargetDistance) {
		this.forceTargetDistance = forceTargetDistance;
	}

	public void setForceWalk(WorldTile tile) {
		resetWalkSteps();
		forceWalk = tile;
	}

	/**
	 * Sets the locked.
	 * 
	 * @param locked The locked to set.
	 */
	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	public void setLureDelay(int lureDelay) {
		this.lureDelay = lureDelay;
	}

	// test
	// end

	public void setName(String string) {
		this.name = getDefinitions().name.equals(string) ? null : string;
		changedName = true;
	}

	public boolean hasChangedName() {
		return changedName;
	}

	public void setNoDistanceCheck(boolean noDistanceCheck) {
		this.noDistanceCheck = noDistanceCheck;
	}

	public void setNPC(int id) {
		this.id = id;
		setBonuses();
	}

	public void setRandomWalk(int forceRandomWalk) {
		this.walkType = forceRandomWalk;
	}

	public void setRespawnTask() {
		if (bossInstance != null && (bossInstance.isFinished()
				|| (!bossInstance.isPublic() && !bossInstance.getSettings().hasTimeRemaining())))
			return;
		if (!hasFinished()) {
			reset();
			setLocation(respawnTile);
			finish();
		}
		long respawnDelay = getCombatDefinitions().getRespawnDelay() * 600;
		if (getId() == 6898)
			respawnDelay = 600;
		if (getName().toLowerCase().contains("impling"))
			respawnDelay *= 3;
		if (bossInstance != null)
			respawnDelay /= bossInstance.getSettings().getSpawnSpeed();
		if (Settings.DEBUG)
			System.out.println("Respawn task initiated: [" + getName() + "]; time: [" + respawnDelay + "].");
		CoresManager.slowExecutor.schedule(new Runnable() {
			@Override
			public void run() {
				try {
					spawn();
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, respawnDelay, TimeUnit.MILLISECONDS);
	}

	public void setSpawned(boolean spawned) {
		this.spawned = spawned;
	}

	public void setTarget(Entity entity) {
		if (isForceWalking()) // if force walk not gonna get target
			return;
		if ((this instanceof GeneralGraardor) || (this instanceof KrilTsutsaroth) || (this instanceof CommanderZilyana)
				|| (this instanceof KreeArra) || (this instanceof com.rs.game.npc.vorago.Vorago)
				|| (this instanceof CorporealBeast) || (this instanceof Nex) || (this instanceof TormentedDemon)) {
			if (entity instanceof Familiar) {
				if (((Familiar) entity).getOwner() != null) {
					combat.setTarget(((Familiar) entity).getOwner());
					lastAttackedByTarget = Utils.currentTimeMillis();
					return;
				}
			}
		}
		combat.setTarget(entity);
		lastAttackedByTarget = Utils.currentTimeMillis();
	}

	public void spawn() {
		setFinished(false);
		World.addNPC(this);
		setLastRegionId(0);
		World.updateEntityRegion(this);
		loadMapRegions();
		checkMultiArea();
	}

	@Override
	public String toString() {
		return getDefinitions().name + " - " + id + " - " + getX() + " " + getY() + " " + getPlane();
	}

	public void transformIntoNPC(int id) {
		setNPC(id);
		nextTransformation = new Transformation(id);
	}

	public int whatDefender() {
		int id = 8844;
		if (containsItem(8850) || containsItem(20072)) {
			id = 20072;
		} else if (containsItem(8849) || containsItem(8850)) {
			id = 8850;
		} else if (containsItem(8848)) {
			id = 8849;
		} else if (containsItem(8847)) {
			id = 8848;
		} else if (containsItem(8846)) {
			id = 8847;
		} else if (containsItem(8845)) {
			id = 8846;
		} else if (containsItem(8844)) {
			id = 8845;
		} else {
			id = 8844;
		}
		return id;
	}

	public boolean withinDistance(Player tile, int distance) {
		return super.withinDistance(tile, distance);
	}

	private boolean intelligentRouteFinder;

	public boolean isIntelligentRouteFinder() {
		return intelligentRouteFinder;
	}

	public void setIntelligentRouteFinder(boolean intelligentRouteFinder) {
		this.intelligentRouteFinder = intelligentRouteFinder;
	}

	private transient BossInstance bossInstance; // if its a instance npc

	public void setBossInstance(BossInstance instance) {
		bossInstance = instance;
	}

	public BossInstance getBossInstance() {
		return bossInstance;
	}

	/**
	 * Head Icons.
	 */
	public HeadIcon[] getIcons() {
		return new HeadIcon[0];
	}

	public void requestIconRefresh() {
		refreshHeadIcon = true;
	}

	public boolean isRefreshHeadIcon() {
		return refreshHeadIcon;
	}

	private String[] gnomeTrainerForceTalk = { "That's it, straight up!", "Come on scaredy cat get across that rope!",
			"My granny can move faster than you!", "Move it, move it, move it!" };

	private String[] trialAnnouncerForceTalk = { "Welcome to the world of " + Settings.SERVER_NAME + "!",
			"Fear Botany Bay, citizens!", "Fear the wild...", "Hmm... Who will it be today?",
			"Beware! You may be next.", "Who will be the next victim?", "The wild is a ruthless place!",
			"Muhahaha.. who's next?", "The wild has NO remorse!", "I can smell the blood from here... Ooh.",
			"Another death; another blood stain!" };

	private int[] PVP_ITEMS = { 13887, 13893, 13899, 13905, 13911, 13917, 13923, 13929, 13884, 13890, 13896, 13902,
			13908, 13914, 13920, 13926, 13870, 13873, 13876, 13879, 13882, 13944, 13947, 13950, 13858, 13861, 13864,
			13867, 13938, 13941, 13944, 13947, 13950, 13953, 13958, 13961, 13964, 13967, 13970, 13973, 13976, 13979,
			13982, 13985, 13988, 14876, 14877, 14878, 14879, 14880, 14881, 14882, 14883, 14884, 14885, 14886, 14887,
			14888, 14889, 14890, 14891, 14892, 13845, 13846, 13847, 13848, 13849, 13850, 13851, 13852, 13853, 13854,
			13855, 13856, 13857 };

	/**
	 * Exclusively used for the Impetuous Impulses minigame.
	 */
	public void setRespawnTaskImpling() {
		if (!hasFinished()) {
			reset();
			setLocation(respawnTile);
			finish();
			if (Settings.DEBUG)
				System.out.println("Finishing NPC: [" + toString() + "].");
		}
		/*
		 * id = FlyingEntities.values()[Utils.random(FlyingEntities.values().length)]
		 * .getNpcId(); setLocation(new WorldTile(Utils.random(2558 + 3, 2626 - 3),
		 * Utils.random(4285 + 3, 4354 - 3), 0)); long respawnDelay =
		 * getCombatDefinitions().getRespawnDelay() * 600;
		 */
		if (id == 7420) {
			setLocation(new WorldTile(respawnTile));
		} else
			setLocation(new WorldTile(Utils.random(2558 + 3, 2626 - 3), Utils.random(4285 + 3, 4354 - 3), 0));
		long respawnDelay = 1000 * 30;
		if (Settings.DEBUG)
			System.out.println("Respawn task initiated: [" + toString() + "]; time: [" + respawnDelay + "].");
		CoresManager.slowExecutor.schedule(new Runnable() {
			@Override
			public void run() {
				try {
					spawn();
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, respawnDelay, TimeUnit.MILLISECONDS);
	}

	private transient boolean cantSetTargetAutoRelatio;

	public boolean isCantSetTargetAutoRelatio() {
		return cantSetTargetAutoRelatio;
	}

	public void setCantSetTargetAutoRelatio(boolean cantSetTargetAutoRelatio) {
		this.cantSetTargetAutoRelatio = cantSetTargetAutoRelatio;
	}

	public int getAttackSpeed() {
		return getCombatDefinitions().getAttackDelay();
	}

	public void setRespawnTile(WorldTile respawnTile) {
		this.respawnTile = respawnTile;
	}

	public int getAttackStyle() {
		return getCombatDefinitions().getAttackStyle();
	}

	@Override
	public boolean canMove(int dir) {
		return true;
	}

	private boolean cannotMove;

	public boolean isCannotMove() {
		return cannotMove;
	}

	public void setCannotMove(boolean canMove) {
		this.cannotMove = canMove;
	}

	public void setRangedBonuses(int level) {
		bonuses[4] = level;
	}

	public int getBonus(int index) {
		return bonuses[index];
	}

	private int maxDistance;

	public int getMaxDistance() {
		return maxDistance;
	}

	private transient long lock;

	public void lock(long time) {
		lock = Utils.currentTimeMillis() + time;
	}

	/**
	 * Gets the locked.
	 *
	 * @return The locked.
	 */
	public boolean isLock() {
		return lock > Utils.currentTimeMillis();
	}

	/**
	 * Sets the locked.
	 *
	 * @param locked The locked to set.
	 */
	public void setLock(boolean lock) {
		this.lock = lock ? Integer.MAX_VALUE : 0;
	}

	public void setBonuses(int[] bonuses) {
		this.bonuses = bonuses;
	}

	public void setBonus(int index, int level) {
		bonuses[index] = level;
	}
	private transient NPCCombatDefinitions combatDefinitions;
	public final static ArrayList<Player> rewardList = new ArrayList<Player>();

	public static int[] noResetCombat = { 17149, 17150, 17151, 17152, 17153, 17154 };

	public void setCombatDefinitions(NPCCombatDefinitions combatDefinitions) {
    this.combatDefinitions = combatDefinitions;
}


}