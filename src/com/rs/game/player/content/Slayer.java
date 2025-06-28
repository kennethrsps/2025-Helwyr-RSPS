package com.rs.game.player.content;

import java.io.Serializable;
import java.util.List;

import com.rs.game.Animation;
import com.rs.game.Entity;
import com.rs.game.World;
import com.rs.game.WorldObject;
import com.rs.game.WorldTile;
import com.rs.game.item.Item;
import com.rs.game.npc.NPC;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.game.player.SlayerManager;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.ShopsHandler;
import com.rs.utils.Utils;

public class Slayer {

	public enum SlayerMaster implements Serializable {

		SPRIA(8462, 85, 1, new int[] { 0, 0, 0 }, new int[] { 15, 50 }, SlayerTask.BANSHEE, SlayerTask.BAT,
				SlayerTask.BEAR, SlayerTask.COW, SlayerTask.BIRD, SlayerTask.CAVE_BUG, SlayerTask.CAVE_SLIME,
				SlayerTask.DWARF, SlayerTask.CRAWLING_HAND, SlayerTask.DESERT_LIZARD, SlayerTask.DWARF,
				SlayerTask.GHOST, SlayerTask.GOBLIN, SlayerTask.ICEFIEND, SlayerTask.MINOTAUR, SlayerTask.MONKEY,
				SlayerTask.SCORPION, SlayerTask.SKELETON, SlayerTask.SPIDER, SlayerTask.WOLF, SlayerTask.ZOMBIE),

		TURAEL(8480, 3, 1, new int[] { 0, 0, 0 }, new int[] { 15, 50 }, SlayerTask.BANSHEE, SlayerTask.BAT,
				SlayerTask.BEAR, SlayerTask.COW, SlayerTask.BIRD, SlayerTask.CAVE_BUG, SlayerTask.CAVE_SLIME,
				SlayerTask.DWARF, SlayerTask.CRAWLING_HAND, SlayerTask.DESERT_LIZARD, SlayerTask.DWARF,
				SlayerTask.GHOST, SlayerTask.GOBLIN, SlayerTask.ICEFIEND, SlayerTask.MINOTAUR, SlayerTask.MONKEY,
				SlayerTask.SCORPION, SlayerTask.SKELETON, SlayerTask.SPIDER, SlayerTask.WOLF, SlayerTask.ZOMBIE),

		MAZCHNA(8481, 20, 1, new int[] { 2, 5, 15 }, new int[] { 40, 85 }, SlayerTask.BANSHEE, SlayerTask.BAT,
				SlayerTask.CATABLEPON, SlayerTask.CAVE_CRAWLER, SlayerTask.COCKATRICE, SlayerTask.CRAWLING_HAND,
				SlayerTask.CYCLOPS, SlayerTask.DESERT_LIZARD, SlayerTask.DOG, SlayerTask.FLESH_CRAWLER,
				SlayerTask.GHOUL, SlayerTask.GHOST, SlayerTask.GROTWORM, SlayerTask.HILL_GIANT, SlayerTask.HOBGOBLIN,
				SlayerTask.ICE_WARRIOR, SlayerTask.KALPHITE, SlayerTask.PYREFIEND, SlayerTask.ROCKSLUG,
				SlayerTask.SKELETON, SlayerTask.VAMPYRE, SlayerTask.WOLF, SlayerTask.ZOMBIE),

		ACHTRYN(8465, 20, 1, new int[] { 2, 5, 15 }, new int[] { 40, 85 }, SlayerTask.BANSHEE, SlayerTask.BAT,
				SlayerTask.CATABLEPON, SlayerTask.CAVE_CRAWLER, SlayerTask.COCKATRICE, SlayerTask.CRAWLING_HAND,
				SlayerTask.CYCLOPS, SlayerTask.DESERT_LIZARD, SlayerTask.DOG, SlayerTask.FLESH_CRAWLER,
				SlayerTask.GHOUL, SlayerTask.GHOST, SlayerTask.GROTWORM, SlayerTask.HILL_GIANT, SlayerTask.HOBGOBLIN,
				SlayerTask.ICE_WARRIOR, SlayerTask.KALPHITE, SlayerTask.PYREFIEND, SlayerTask.ROCKSLUG,
				SlayerTask.SKELETON, SlayerTask.VAMPYRE, SlayerTask.WOLF, SlayerTask.ZOMBIE),

		VANNAKA(1597, 40, 1, new int[] { 4, 20, 60 }, new int[] { 60, 120 }, SlayerTask.ABERRANT_SPECTRE,
				SlayerTask.ANKOU, SlayerTask.BANSHEE, SlayerTask.BASILISK, SlayerTask.BLOODVELD, SlayerTask.BRINE_RAT,
				SlayerTask.COCKATRICE, SlayerTask.CROCODILE, SlayerTask.CYCLOPS, SlayerTask.DUST_DEVIL,
				SlayerTask.EARTH_WARRIOR, SlayerTask.GHOUL, SlayerTask.GREEN_DRAGON, SlayerTask.GROTWORM,
				SlayerTask.HARPIE_BUG_SWARM, SlayerTask.HILL_GIANT, SlayerTask.ICE_GIANT, SlayerTask.ICE_WARRIOR,
				SlayerTask.INFERNAL_MAGE, SlayerTask.JELLY,  SlayerTask.LESSER_DEMON,
				SlayerTask.MOSS_GIANT, SlayerTask.OGRE, SlayerTask.OTHERWORLDLY_BEING, SlayerTask.PYREFIEND,
				SlayerTask.SHADE, SlayerTask.SHADOW_WARRIOR, SlayerTask.TUROTH, SlayerTask.VAMPYRE,
				SlayerTask.WEREWOLF),

		CHAELDAR(1598, 70, 1, new int[] { 10, 50, 100 }, new int[] { 110, 170 }, SlayerTask.ABERRANT_SPECTRE,
				SlayerTask.BANSHEE, SlayerTask.BASILISK, SlayerTask.BLOODVELD, SlayerTask.BLUE_DRAGON,
				SlayerTask.BRINE_RAT, SlayerTask.BRONZE_DRAGON, SlayerTask.CAVE_CRAWLER, SlayerTask.CAVE_HORROR,
				SlayerTask.CRAWLING_HAND, SlayerTask.DAGANNOTH, SlayerTask.DUST_DEVIL, SlayerTask.ELF_WARRIOR,
				SlayerTask.FEVER_SPIDER, SlayerTask.FIRE_GIANT, SlayerTask.FUNGAL_MAGE, SlayerTask.GARGOYLE,
				SlayerTask.GRIFOLAPINE, SlayerTask.GRIFOLAROO, SlayerTask.GROTWORM, SlayerTask.HARPIE_BUG_SWARM,
				SlayerTask.JUNGLE_STRYKEWYRM, SlayerTask.INFERNAL_MAGE, SlayerTask.JELLY, 
				SlayerTask.KALPHITE, SlayerTask.KALPHITE, SlayerTask.KURASK, SlayerTask.LESSER_DEMON,
				SlayerTask.ZYGOMITE, SlayerTask.SHADOW_WARRIOR, SlayerTask.TUROTH, SlayerTask.WARPED_TORTOISE),

		SUMONA(7780, 85, 35, new int[] { 12, 60, 180 }, new int[] { 120, 185 }, SlayerTask.ABERRANT_SPECTRE,
				SlayerTask.ABYSSAL_DEMON, SlayerTask.AQUANITE, SlayerTask.BANSHEE, SlayerTask.BASILISK,
				SlayerTask.BLACK_DEMON, SlayerTask.BLOODVELD, SlayerTask.BLUE_DRAGON, SlayerTask.CAVE_CRAWLER,
				SlayerTask.CAVE_HORROR, SlayerTask.DAGANNOTH, SlayerTask.DESERT_STRYKEWYRM, SlayerTask.DUST_DEVIL,
				SlayerTask.ELF_WARRIOR, SlayerTask.FUNGAL_MAGE, SlayerTask.GARGOYLE, SlayerTask.GREATER_DEMON,
				SlayerTask.GRIFOLAPINE, SlayerTask.GRIFOLAROO, SlayerTask.GROTWORM, SlayerTask.HELLHOUND,
				SlayerTask.IRON_DRAGON, SlayerTask.JUNGLE_STRYKEWYRM, SlayerTask.KALPHITE, SlayerTask.KURASK,
				SlayerTask.JADINKO, SlayerTask.NECHRYAEL, SlayerTask.RED_DRAGON, SlayerTask.SCABARAS,
				SlayerTask.SPIRITUAL_MAGE, SlayerTask.SPIRITUAL_WARRIOR, SlayerTask.TERROR_DOG, SlayerTask.TROLL,
				SlayerTask.TUROTH),

		DURADEL(8466, 100, 50, new int[] { 15, 75, 225 }, new int[] { 130, 200 }, SlayerTask.ABERRANT_SPECTRE,
				SlayerTask.ABYSSAL_DEMON, SlayerTask.AQUANITE, SlayerTask.BLACK_DEMON, SlayerTask.BLACK_DRAGON,
				SlayerTask.BLOODVELD, SlayerTask.DAGANNOTH, SlayerTask.DARK_BEAST, SlayerTask.DESERT_STRYKEWYRM,
				SlayerTask.DUST_DEVIL, SlayerTask.FIRE_GIANT, SlayerTask.FUNGAL_MAGE, SlayerTask.GANODERMIC,
				SlayerTask.GARGOYLE, SlayerTask.GORAK, SlayerTask.GREATER_DEMON, SlayerTask.GRIFALOPINE,
				SlayerTask.GRIFALOROO, SlayerTask.GROTWORM, SlayerTask.HELLHOUND, SlayerTask.ICE_STRYKEWYRM,
				SlayerTask.IRON_DRAGON, SlayerTask.JUNGLE_STRYKEWYRM, SlayerTask.KALPHITE, SlayerTask.MITHRIL_DRAGON,
				SlayerTask.JADINKO, SlayerTask.NECHRYAEL, SlayerTask.SCABARAS, SlayerTask.SKELETAL_WYVERN,
				SlayerTask.SPIRITUAL_MAGE, SlayerTask.STEEL_DRAGON, SlayerTask.SUQAH, SlayerTask.WARPED_TERRORBIRD,
				SlayerTask.WATERFIEND, SlayerTask.EDIMMU),

		LAPALOK(8467, 100, 50, new int[] { 15, 75, 225 }, new int[] { 130, 200 }, SlayerTask.ABERRANT_SPECTRE,
				SlayerTask.ABYSSAL_DEMON, SlayerTask.AQUANITE, SlayerTask.BLACK_DEMON, SlayerTask.BLACK_DRAGON,
				SlayerTask.BLOODVELD, SlayerTask.DAGANNOTH, SlayerTask.DARK_BEAST, SlayerTask.DESERT_STRYKEWYRM,
				SlayerTask.DUST_DEVIL, SlayerTask.FIRE_GIANT, SlayerTask.FUNGAL_MAGE, SlayerTask.GANODERMIC,
				SlayerTask.GARGOYLE, SlayerTask.GORAK, SlayerTask.GREATER_DEMON, SlayerTask.GRIFALOPINE,
				SlayerTask.GRIFALOROO, SlayerTask.GROTWORM, SlayerTask.HELLHOUND, SlayerTask.ICE_STRYKEWYRM,
				SlayerTask.IRON_DRAGON, SlayerTask.JUNGLE_STRYKEWYRM, SlayerTask.KALPHITE, SlayerTask.MITHRIL_DRAGON,
				SlayerTask.JADINKO, SlayerTask.NECHRYAEL, SlayerTask.SCABARAS, SlayerTask.SKELETAL_WYVERN,
				SlayerTask.SPIRITUAL_MAGE, SlayerTask.STEEL_DRAGON, SlayerTask.SUQAH, SlayerTask.WARPED_TERRORBIRD,
				SlayerTask.WATERFIEND, SlayerTask.EDIMMU),

		KURADAL(9085, 110, 75, new int[] { 18, 90, 270 }, new int[] { 120, 250 }, SlayerTask.ABERRANT_SPECTRE,
				SlayerTask.ABYSSAL_DEMON, SlayerTask.BLACK_DEMON, SlayerTask.BLACK_DRAGON, SlayerTask.BLOODVELD,
				SlayerTask.BLUE_DRAGON, SlayerTask.DAGANNOTH, SlayerTask.DARK_BEAST, SlayerTask.DESERT_STRYKEWYRM,
				SlayerTask.DUST_DEVIL, SlayerTask.FIRE_GIANT, SlayerTask.GANODERMIC, SlayerTask.GARGOYLE,
				SlayerTask.GRIFOLAPINE, SlayerTask.GRIFOLAROO, SlayerTask.GROTWORM, SlayerTask.HELLHOUND,
				SlayerTask.IRON_DRAGON, SlayerTask.JUNGLE_STRYKEWYRM, SlayerTask.KALPHITE, SlayerTask.MITHRIL_DRAGON,
				SlayerTask.JADINKO, SlayerTask.NECHRYAEL, SlayerTask.SKELETAL_WYVERN, SlayerTask.SPIRITUAL_MAGE,
				SlayerTask.STEEL_DRAGON, SlayerTask.SUQAH, SlayerTask.TERROR_DOG, SlayerTask.TZHAAR, SlayerTask.TZHAAR,
				SlayerTask.WARPED_TORTOISE, SlayerTask.WATERFIEND, SlayerTask.EDIMMU, SlayerTask.AIRUT, 
			    SlayerTask.HYDRIX_DRAGON, SlayerTask.DRAGONSTONE_DRAGON, SlayerTask.ONYX_DRAGON),
		
		SERGEANT_WADJMOS(24516, 97, 1, new int[] { 25, 90, 270 }, new int[] { 100, 200 },
                SlayerTask.CORRUPTED_SCORPION, SlayerTask.CORRUPTED_SCARAB, SlayerTask.CORRUPTED_DUST_DEVIL, 
                SlayerTask.CORRUPTED_KALPHITE_MARAUDER, SlayerTask.CORRUPTED_WORKER, SlayerTask.SALAWA_AKH,
                SlayerTask.FELINE_AKH, SlayerTask.SCARAB_AKH, SlayerTask.CROCODILE_AKH, SlayerTask.GORILLA_AKH,
                SlayerTask.IMPERIAL_WARRIOR_AKH, SlayerTask.IMPERIAL_MAGE_AKH, SlayerTask.IMPERIAL_RANGER_AKH
	            ),
				
		MOVRAN(20112, 110, 90, new int[] { 25, 90, 270 }, new int[] { 20, 30 }, SlayerTask.GENERAL_GRAARDOR,
				SlayerTask.COMMANDER_ZILYANA, SlayerTask.SUNFREET, SlayerTask.KING_BLACK_DRAGON, SlayerTask.NYMORA,
				SlayerTask.TORMENTED_DEMON, SlayerTask.CORPOREAL_BEAST, SlayerTask.KALPHITE_KING, SlayerTask.HELWYR,
				SlayerTask.VINDICTA,SlayerTask.WYVERN,SlayerTask.RIPPER_DEMON,SlayerTask.ACHERON_MAMMOTH,SlayerTask.MAGISTER,
				SlayerTask.NEX);

		private SlayerTask[] task;
		private int[] tasksRange, pointsRange;
		private int requriedCombatLevel, requiredSlayerLevel, npcId;

		private SlayerMaster(int npcId, int requriedCombatLevel, int requiredSlayerLevel, int[] pointsRange,
				int[] tasksRange, SlayerTask... task) {
			this.npcId = npcId;
			this.requriedCombatLevel = requriedCombatLevel;
			this.requiredSlayerLevel = requiredSlayerLevel;
			this.pointsRange = pointsRange;
			this.tasksRange = tasksRange;
			this.task = task;
		}

		public int getNPCId() {
			return npcId;
		}

		public int getRequiredCombatLevel() {
			return requriedCombatLevel;
		}

		public int getRequiredSlayerLevel() {
			return requiredSlayerLevel;
		}

		public SlayerTask[] getTask() {
			return task;
		}

		public int[] getTasksRange() {
			return tasksRange;
		}

		public int[] getPointsRange() {
			return pointsRange;
		}

		public static boolean startInteractionForId(Player player, int npcId, int option) {
			for (SlayerMaster master : SlayerMaster.values()) {
				if (master.getNPCId() == npcId) {
					if (option == 1)
						player.getDialogueManager().startDialogue("SlayerMasterD", npcId, master);
					else if (option == 2)
						player.getDialogueManager().startDialogue("QuickTaskD", master);
					else if (option == 3)
						ShopsHandler.openShop(player, 29);
					else if (option == 4)
						player.getSlayerManager().sendSlayerInterface(SlayerManager.BUY_INTERFACE);
					return true;
				}
			}
			return false;
		}
	}

	public enum SlayerTask implements Serializable {// 79 matches out of 117
		
		/**Sophanem Slayer NPC*
		 * Thor
		 * CORRUPTED_SCORPION CORRUPTED_SCARAB CORRUPTED_DUST_DEVIL CORRUPTED_KALPHITE_MARAUDER CORRUPTED_WORKER
		 * SALAWA_AKH FELINE_AKH SCARAB_AKH CROCODILE_AKH GORILLA_AKH IMPERIAL_WARRIOR_AKH IMPERIAL_MAGE_AKH IMPERIAL_RANGER_AKH
		 **/
		
		CORRUPTED_SCORPION(1, new Location[] { new Location("Sophanem Slayer Dungeon", new WorldTile(3291, 2709, 0)) },
				new String[] {}),
		CORRUPTED_SCARAB(1, new Location[] { new Location("Sophanem Slayer Dungeon", new WorldTile(3291, 2709, 0, 0)) },
				new String[] {}),
		CORRUPTED_DUST_DEVIL(1, new Location[] { new Location("Sophanem Slayer Dungeon", new WorldTile(3291, 2709, 0)) },
				new String[] {}),
		CORRUPTED_KALPHITE_MARAUDER(1, new Location[] { new Location("Sophanem Slayer Dungeon", new WorldTile(3291, 2709, 0)) },
				new String[] {}),
		CORRUPTED_WORKER(1, new Location[] { new Location("Sophanem Slayer Dungeon", new WorldTile(3291, 2709, 0)) },
				new String[] {}),
		SALAWA_AKH(1, new Location[] { new Location("Sophanem Slayer Dungeon", new WorldTile(3291, 2709, 0)) },
				new String[] {}),
		FELINE_AKH(1, new Location[] { new Location("Sophanem Slayer Dungeon", new WorldTile(3291, 2709, 0)) },
				new String[] {}),
		SCARAB_AKH(1, new Location[] { new Location("Sophanem Slayer Dungeon", new WorldTile(3291, 2709, 0)) },
				new String[] {}),
		CROCODILE_AKH(1, new Location[] { new Location("Sophanem Slayer Dungeon", new WorldTile(3291, 2709, 0)) },
				new String[] {}),
		GORILLA_AKH(1, new Location[] { new Location("Sophanem Slayer Dungeon", new WorldTile(3291, 2709, 0)) },
				new String[] {}),
		IMPERIAL_WARRIOR_AKH(1, new Location[] { new Location("Sophanem Slayer Dungeon", new WorldTile(3291, 2709, 0)) },
				new String[] {}),
		IMPERIAL_MAGE_AKH(1, new Location[] { new Location("Sophanem Slayer Dungeon", new WorldTile(3291, 2709, 0)) },
				new String[] {}),
		IMPERIAL_RANGER_AKH(1, new Location[] { new Location("Sophanem Slayer Dungeon", new WorldTile(3291, 2709, 0)) },
				new String[] {}),
		/**END**/
		

		MIGHTY_BANSHEE(37,
				new Location[] {
						new Location("Pollnivneach Slayer Dungeon(Mighty Banshee)", new WorldTile(3358, 9369, 0)),
						new Location("Slayer Tower(Banshee)", new WorldTile(3446, 3563, 0)) },
				new String[] { "Banshees are fearsome creatures with a mighty scream.",
						" You need something to cover your ears", "Beware of their scream.",
						"Banshees are found in the Slayer Tower." }),

		// finished
		MIGHTY_BANSEE(37,
				new Location[] { new Location("Pollnivneach Slayer Dungeon", new WorldTile(3358, 9369, 0)),
						new Location("Slayer Tower(Banshee)", new WorldTile(3446, 3563, 0)) },
				new String[] { "Banshees are fearsome creatures with a mighty scream.",
						" You need something to cover your ears", "Beware of their scream.",
						"Banshees are found in the Slayer Tower." },
				MIGHTY_BANSHEE),

		// finished
		BANSHEE(15,
				new Location[] { new Location("Pollnivneach Slayer Dungeon", new WorldTile(3358, 9369, 0)),
						new Location("Slayer Tower(Banshee)", new WorldTile(3446, 3563, 0)) },
				new String[] { "Banshees are fearsome creatures with a mighty scream.",
						" You need something to cover your ears", "Beware of their scream.",
						"Banshees are found in the Slayer Tower." },
				MIGHTY_BANSEE),

		// finished
		BAT(1, new Location[] { new Location("Near Slayer Tower(Bat)", new WorldTile(3439, 3518, 0)) },
				new String[] {}),

		AVIANSIE(1,
				new Location[] { new Location("God Wars Dungeon(Aviansie)", new WorldTile(2872, 5271, 0), "GodWars") },
				new String[] {}),

		// finished
		CHICKEN(1, new Location[] { new Location("Lumbridge(Chicken)", new WorldTile(3233, 3295, 0)) },
				new String[] {}),

		// finished
		DUCK(1, new Location[] { new Location("Lumbridge(Duck)", new WorldTile(3235, 3272, 0)) }, new String[] {}),

		// finished
		BIRD(1, new Location[] { new Location("Crash island(Bird)", new WorldTile(2892, 2726, 0)),
				new Location("Lumbridge(Duck)", new WorldTile(3235, 3272, 0)),
				new Location("Lumbridge(Chicken)", new WorldTile(3233, 3295, 0)),
				new Location("God Wars Dungeon(Aviansie)", new WorldTile(2872, 5271, 0), "GodWars") },
				new String[] { "Birds are a type of species found throughout Matrix in different forms.",
						"It's recomended that you bring range weapons to fight these monsters.",
						"Avansies are the strongest and most widely known type of bird.",
						"Chickens are great for a fast task." },
				AVIANSIE, CHICKEN, DUCK),

		// finished
		BEAR(1, new Location[] { new Location("South Of Varrock(Bear)", new WorldTile(3273, 3335, 0)) },
				new String[] {}),

		// finished
		CAVE_BUG(7, new Location[] { new Location("Lumbridge Swamp Cave(Cave bug)", new WorldTile(3196, 9553, 0)) },
				new String[] {}),

		// finished
		CAVE_SLIME(17,
				new Location[] { new Location("Lumbridge Swamp Cave(Cave Slime)", new WorldTile(3196, 9553, 0)) },
				new String[] {}),

		// finished
		COW(1, new Location[] { new Location("Lumbridge(Cow)", new WorldTile(3251, 3266, 0)) }, new String[] {}),

		// finished
		ZOMBIE_HAND(5,
				new Location[] { new Location("Meiyerditch Dungeon(Zombie Hand)", new WorldTile(3621, 9754, 0)) },
				new String[] {}),

		// finished
		SKELETAL_HAND(5,
				new Location[] { new Location("Meiyerditch Dungeon(Skeletal Hand)", new WorldTile(3621, 9764, 0)) },
				new String[] {}),

		// finished
		CRAWLING_HAND(5, new Location[] { new Location("Slayer Tower(Crawling Hand)", new WorldTile(3427, 3537, 0)) },
				new String[] {}, ZOMBIE_HAND, SKELETAL_HAND),

		// finished
		DWARF(1, new Location[] { new Location("Keldagrim(Dwarf)", new WorldTile(2837, 10140, 0)) }, new String[] {}),

		// finished
		LIZARD(22, new Location[] { new Location("Kharidian Desert(Lizard)", new WorldTile(3296, 2941, 0)) }, null),

		// finished
		DESERT_LIZARD(22,
				new Location[] { new Location("Desert(Desert Lizard)", new WorldTile(3425, 3017, 0)),
						new Location("Kharidian Desert(Lizard)", new WorldTile(3296, 2941, 0)) },
				new String[] {}, LIZARD),

		// finished
		REVENANT(1,
				new Location[] { new Location("Wilderness (Revenant)", new WorldTile(3120, 10121, 0), "Wilderness") },
				new String[] {}),

		// finished
		GHOST(1, new Location[] { new Location("Varrock Sewers(Ghost)", new WorldTile(3245, 9916, 0)),
				new Location("Wilderness (Revenant)", new WorldTile(3120, 10121, 0), "Wilderness") }, new String[] {},
				REVENANT),

		// finished
		GOBLIN(1, new Location[] { new Location("Lumbridge(Gobline)", new WorldTile(3251, 3266, 0)) }, new String[] {}),

		// finished
		ICEFIEND(1, new Location[] { new Location("Ice Mountain(Icefiend)", new WorldTile(3010, 3475, 0)) },
				new String[] {}),

		// finished
		MINOTAUR(1, new Location[] { new Location("Stronghold of Security(Minotaur)", new WorldTile(1861, 5226, 0)) },
				new String[] {}),

		// finished
		MONKEY(1, new Location[] { new Location("Karamja(Monkey)", new WorldTile(2833, 3190, 0)) }, new String[] {}),

		// finished
		SCORPION(1, new Location[] { new Location("Karamja(Scorpion)", new WorldTile(2845, 3161, 0)) },
				new String[] {}),

		// finished
		SKELETON(1,
				new Location[] { new Location("Varrock Sewers(Skeleton)", new WorldTile(3280, 9904, 0)),
						new Location("Meiyerditch Dungeon(Skeletal Hand)", new WorldTile(3621, 9764, 0)) },
				new String[] {}, SKELETAL_HAND),

		// finished
		SPIDER(1, new Location[] { new Location("Taverly Dungeon(Poison Spider)", new WorldTile(2871, 9794, 0)) },
				new String[] {}),

		// finished
		WOLF(1, new Location[] { new Location("White Wolf Mountain(White Wolf)", new WorldTile(2859, 3481, 0)) },
				new String[] {}),

		// finished
		ZOMBIE(1, new Location[] { new Location("Drynor Sewers(Zombie)", new WorldTile(3085, 9672, 0)) },
				new String[] {}),

		// finished
		CATABLEPON(1,
				new Location[] { new Location("Stronghold of Security(Catablepon)", new WorldTile(2141, 5263, 0)) },
				new String[] {}),

		// finished
		CAVE_CRAWLER(10,
				new Location[] {
						new Location("Fremennik Slayer Dungeon(Cave Crawler)", new WorldTile(2808, 10002, 0)) },
				new String[] {}),

		// finished
		DOG(1, new Location[] { new Location("Brimhaven Dungeon(Dog)", new WorldTile(2659, 9524, 0)) },
				new String[] {}),

		// done
		FLESH_CRAWLER(1,
				new Location[] { new Location("Stronghold of Security(Flesh crawler)", new WorldTile(2005, 5238, 0)) },
				new String[] {}),

		// finished
		HOBGOBLIN(1, new Location[] { new Location("Near Crafting Guild", new WorldTile(2918, 3268, 0)) },
				new String[] {}),

		// finished
		KALPHITE(1, new Location[] { new Location("Kalphite Dungeon", new WorldTile(3421, 9510, 0)) }, new String[] {}),

		// finished
		ROCKSLUG(20,
				new Location[] { new Location("Fremennik Slayer Dungeon(Rock Slug)", new WorldTile(2778, 10013, 0)) },
				new String[] {}),

		// finished
		ROCK_SLUG(20,
				new Location[] { new Location("Fremennik Slayer Dungeon(Rock Slug)", new WorldTile(2778, 10013, 0)) },
				new String[] {}, ROCKSLUG),
		// finished
		ABERRANT_SPECTRE(60,
				new Location[] { new Location("Slayer Tower(Aberrant Spectre)", new WorldTile(3421, 3542, 1)) },
				new String[] {}),

		// finished
		ANKOU(1, new Location[] { new Location("Slayer Tower(Ankou)", new WorldTile(3444, 3559, 2)) }, new String[] {}),

		// finished
		BASILISK(40,
				new Location[] { new Location("Fremennik Slayer Dungeon(Basilisk)", new WorldTile(2746, 9998, 0)),
						new Location("Pollnivneach Slayer Dungeon(Basilisk)", new WorldTile(3316, 4312, 0)) },
				new String[] {}),

		// finished
		BLOODVELD(50,
				new Location[] { new Location("God Wars Dungeon(Bloodveld)", new WorldTile(2887, 5350, 0), "GodWars"),
						new Location("Slayer tower(Bloodveld)", new WorldTile(3418, 3541, 2)),
						new Location("Meiyerditch Dungeon(Bloodveld)", new WorldTile(3585, 9738, 0)) },
				new String[] {}),

		// finished
		BRINE_RAT(47, new Location[] { new Location("Brine Rat Cavern(Brine Rat)", new WorldTile(2718, 10133, 0)) },
				new String[] {}),

		// finished
		COCKATRICE(25,
				new Location[] { new Location("Fremennik Slayer Dungeon(Basilisk)", new WorldTile(2803, 10027, 0)) },
				new String[] {}),

		// finished
		CROCODILE(1, new Location[] { new Location("Kharidian Desert(Crocodile)", new WorldTile(3303, 2905, 0)) },
				new String[] {}),

		// finished
		CYCLOPS(1,
				new Location[] { new Location("God Wars Dungeon(Cyclops)", new WorldTile(2855, 5353, 0), "GodWars") },
				new String[] {}),

		// finished
		CYCLOPSE(1,
				new Location[] { new Location("God Wars Dungeon(Cyclops)", new WorldTile(2855, 5353, 0), "GodWars") },
				new String[] {}, CYCLOPS),

		// finished
		DUST_DEVIL(65, new Location[] { new Location("Smoke Dungeon(Dust Devil)", new WorldTile(3206, 9379, 0)) },
				new String[] {}),

		// finished
		EARTH_WARRIOR(1,
				new Location[] { new Location("Wilderness Underground(Earth warrior)", new WorldTile(3121, 9970, 0),
						"Wilderness") },
				new String[] {}),

		// finished
		GHOUL(1, new Location[] { new Location("North Of Canifis(Ghoul)", new WorldTile(3422, 3502, 0)) },
				new String[] {}),

		// finished
		GREEN_DRAGON(1,
				new Location[] {
						new Location("Eastern Wilderness(Green Dragon)", new WorldTile(2993, 3603, 0), "Wilderness") },
				new String[] {}),

		// finished
		GROTWORM(1, new Location[] { new Location("Grotworm Lair (Grotworm)", new WorldTile(3159, 4279, 3)) },
				new String[] {}),

		HARPIE_BUG_SWARM(33,
				new Location[] { new Location("East Of The Jogre Dungeon(Harpie bug)", new WorldTile(2871, 3104, 0)) },
				new String[] {}),

		// finished
		HILL_GIANT(1, new Location[] { new Location("East Varrock Dungeon(Hill Giant)", new WorldTile(3117, 9852, 0)) },
				new String[] {}),

		// finished
		ICE_GIANT(1, new Location[] { new Location("Asgarnia Ice Dungeon(Ice Giant)", new WorldTile(2973, 3908, 0)) },
				new String[] {}),

		// finished
		ICE_WARRIOR(1,
				new Location[] { new Location("Asgarnia Ice Dungeon(Ice Warrior)", new WorldTile(3033, 9581, 0)) },
				new String[] {}),

		// finished
		INFERNAL_MAGE(45, new Location[] { new Location("Slayer tower(Infernal Mage)", new WorldTile(3429, 3534, 0)) },
				new String[] {}),

		// finished
		JELLY(52, new Location[] { new Location("Fremennik Slayer Dungeon(Jelly)", new WorldTile(2716, 10031, 0)) },
				new String[] {}),

		// finished
		JUNGLE_HORROR(1,
				new Location[] { new Location("Mos Le'Harmless(Jungle Horror)", new WorldTile(3723, 2972, 0)) },
				new String[] {}),

		// finished
		LESSER_DEMON(1, new Location[] { new Location("Karamja Dungeon(Lesser Demon)", new WorldTile(2837, 9601, 0)) },
				new String[] {}),

		// finished
		MOSS_GIANT(1, new Location[] { new Location("Cranador(Moss Giant)", new WorldTile(2840, 3246, 0)) },
				new String[] {}),

		// finished
		OGRE(1, new Location[] { new Location("Feldip Hills(Ogre)", new WorldTile(2561, 2999, 0)) }, new String[] {}),

		// finished
		OTHERWORLDLY_BEING(1, new Location[] { new Location("South-West Of Zanaris", new WorldTile(2393, 4432, 0)) },
				new String[] {}),

		// finished
		PYREFIEND(30,
				new Location[] { new Location("Fremennik Slayer Dungeon(Pyrefiend)", new WorldTile(2765, 10016, 0)) },
				new String[] {}),

		// finished
		SHADE(1, new Location[] { new Location("Stronghold of Security(Shade)", new WorldTile(2361, 5214, 0)) },
				new String[] {}),

		// finished
		SHADOW_WARRIOR(1,
				new Location[] {
						new Location("Basement Of The Legends' Guild(Shadow Warrior)", new WorldTile(2705, 9763, 0)) },
				new String[] {}),

		// finished
		TUROTH(55, new Location[] { new Location("Fremennik Slayer Dungeon(Turoth)", new WorldTile(2711, 10012, 0)) },
				new String[] {}),

		// finished
		VAMPYRE(1, new Location[] { new Location("Haunted Woods(Vampyre)", new WorldTile(3593, 3504, 0)) },
				new String[] {}),

		// finished
		WEREWOLF(1,
				new Location[] { new Location("Canafis(Werewolf)", new WorldTile(3504, 3485, 0)),
						new Location("God Wars Dungeon(Werewolf)", new WorldTile(2887, 5350, 0), "GodWars") },
				new String[] {}),

		// finished
		BLUE_DRAGON(1, new Location[] { new Location("Taverly Dungeon(Blue dragon)", new WorldTile(2923, 9803, 0)) },
				new String[] {}),

		// finished
		BRONZE_DRAGON(1,
				new Location[] { new Location("Brimhaven Dungeon(Bronze Dragon)", new WorldTile(2723, 9487, 0)) },
				new String[] {}),

		// finished
		CAVE_HORROR(58, new Location[] { new Location("Mos Le'Harmless(Cave Horror)", new WorldTile(3775, 9446, 0)) },
				new String[] {}),

		DAGANNOTH(1, new Location[] { new Location("Waterbirth island(Dagannoth)", new WorldTile(2445, 10146, 0)) },
				new String[] {}),

		ELF_WARRIOR(1, new Location[] { new Location("Elf Camp(Elf Warrior)", new WorldTile(2205, 3253, 0)) },
				new String[] {}),

		// completed
		FEVER_SPIDER(42,
				new Location[] { new Location("Braindeath Island(Fever Spider)", new WorldTile(2151, 5084, 0)) },
				new String[] {}),

		// completed
		FIRE_GIANT(1, new Location[] { new Location("Brimhaven Dungeon(Fire Giant)", new WorldTile(2633, 9589, 0)) },
				new String[] {}),

		FUNGAL_MAGE(1, new Location[] { new Location("Polypore Dungeon", new WorldTile(4624, 5456, 3)) },
				new String[] {}),

		// finished
		GARGOYLE(75, new Location[] { new Location("Slayer tower", new WorldTile(3429, 3534, 0)) }, new String[] {}),

		// finished
		GRIFOLAPINE(88, new Location[] { new Location("Polypore Dungeon", new WorldTile(4624, 5456, 3)) },
				new String[] {}),

		// finished
		GRIFOLAROO(82, new Location[] { new Location("Polypore Dungeon", new WorldTile(4624, 5456, 3)) },
				new String[] {}),

		GRIFALOPINE(88, new Location[] { new Location("Polypore Dungeon", new WorldTile(4624, 5456, 3)) },
				new String[] {}, GRIFOLAPINE),

		GRIFALOROO(82, new Location[] { new Location("Polypore Dungeon", new WorldTile(4624, 5456, 3)) },
				new String[] {}, GRIFOLAROO),

		// finished
		JUNGLE_STRYKEWYRM(73,
				new Location[] { new Location("North of Mobilising Armies", new WorldTile(2468, 2917, 0)) },
				new String[] {}),

		// finished
		KURASK(70, new Location[] { new Location("Fremennik Slayer Dungeon(Kurask)", new WorldTile(2710, 9993, 0)) },
				new String[] {}),

		// finished
		FUNGI(57, new Location[] { new Location("Zanaris", new WorldTile(2405, 4388, 0)) }, new String[] {}),

		// finished
		ZYGOMITE(57, new Location[] { new Location("Zanaris", new WorldTile(2405, 4388, 0)) }, new String[] {}, FUNGI),

		// finished
		WARPED_TORTOISE(56,
				new Location[] { new Location("Poison Waste Slayer Dungeon", new WorldTile(1996, 4207, 1)) },
				new String[] {}),

		// finished
		ABYSSAL_DEMON(85, new Location[] { new Location("Slayer tower(Abyssal Demon)", new WorldTile(3429, 3534, 0)) },
				new String[] {}),

		// finished
		AQUANITE(78,
				new Location[] { new Location("Fremennik Slayer Dungeon(Aquanite)", new WorldTile(2710, 9993, 0)) },
				new String[] {}),

		// finished
		BLACK_DEMON(1,
				new Location[] { new Location("Taverly Dungeon(Black Demon)", new WorldTile(2871, 9794, 0)),
						new Location("Brimhaven Dungeon(Black Demon)", new WorldTile(2723, 9487, 0)) },
				new String[] {}),

		// finished
		DESERT_STRYKEWYRM(77,
				new Location[] { new Location("East Of Al-Kharid(Desert Strykeyrm)", new WorldTile(3354, 3159, 0)) },
				new String[] {}),

		// finished
		GREATER_DEMON(1,
				new Location[] { new Location("Brimhaven Dungeon(Greater demon)", new WorldTile(2638, 9510, 2)) },
				new String[] {}),

		// finished
		HELLHOUND(1, new Location[] { new Location("Taverly Dungeon(Hellhound)", new WorldTile(2872, 9819, 0)) },
				new String[] {}),

		// finished
		IRON_DRAGON(1, new Location[] { new Location("Brimhaven Dungeon(Iron Dragon)", new WorldTile(2709, 9473, 0)) },
				new String[] {}),

		JADINKO(91, new Location[] { new Location("Jadinko Lair(Jadinko)", new WorldTile(3012, 9274, 0)) },
				new String[] {}),

		// finished
		NECHRYAEL(80, new Location[] { new Location("Slayer tower(Nechryael)", new WorldTile(3429, 3534, 0)) },
				new String[] {}),

		// finished
		RED_DRAGON(1, new Location[] { new Location("Brimhaven Dungeon(Red Dragon)", new WorldTile(2690, 9508, 0)) },
				new String[] {}),

		LOCUST(1, new Location[] { new Location("Sophanem", new WorldTile(3305, 2781, 0)) }, new String[] {}),
		SCABARAS(1, new Location[] { new Location("Sophanem", new WorldTile(3305, 2781, 0)) }, new String[] {}),
		SCARAB(1, new Location[] { new Location("Sophanem", new WorldTile(3305, 2781, 0)) }, new String[] {}),

		// finished
		SPIRITUAL_MAGE(83,
				new Location[] {
						new Location("God Wars Dungeon(Spiritual Mage)", new WorldTile(2887, 5350, 0), "GodWars") },
				new String[] {}),

		// finished
		SPIRITUAL_WARRIOR(68,
				new Location[] {
						new Location("God Wars Dungeon(Spiritual Warrior)", new WorldTile(2887, 5350, 0), "GodWars") },
				new String[] {}),

		// finished
		TERROR_DOG(40, new Location[] { new Location("Tarn's Lair(Terror Dog)", new WorldTile(3149, 4644, 0)) },
				new String[] {}),

		// a stupid troll at death plateau
		ROCK(1, new Location[] { new Location("Death Plateau(Rock)", new WorldTile(2846, 3592, 0)) }, new String[] {}),

		TROLL(1, new Location[] { new Location("Death Plateau(Troll)", new WorldTile(2846, 3592, 0)) }, new String[] {},
				ROCK),

		// finished
		BLACK_DRAGON(1, new Location[] { new Location("Taverly Dungeon(Black Dragon)", new WorldTile(2836, 9819, 0)) },
				new String[] {}),

		// finished
		DARK_BEAST(90, new Location[] { new Location("Forinthry Dungeon(Wildy)", new WorldTile(3102, 10138, 0)) },
				new String[] {}),

		// finished
		GANODERMIC(95, new Location[] { new Location("Polypore Dungeon(Ganodermic)", new WorldTile(4624, 5456, 3)) },
				new String[] {}),

		// finished
		GORAK(1, new Location[] { new Location("Gorak Plane(Gorak)", new WorldTile(3038, 5347, 0)),
				new Location("God Wars Dungeon(Gorak)", new WorldTile(2887, 5350, 0), "GodWars") }, new String[] {}),

		ICE_STRYKEWYRM(93, new Location[] { new Location("Ice cave(Ice Strykewyrm)", new WorldTile(3428, 5652, 0)) },
				new String[] {}),

		// finished
		MITHRIL_DRAGON(1,
				new Location[] { new Location("Ancient cavern(Mithril Dragon)", new WorldTile(1778, 5343, 1)) },
				new String[] {}),

		// finished
		SKELETAL_WYVERN(72,
				new Location[] { new Location("Asgarnia Ice Dungeon(Skeletal Wyvern)", new WorldTile(3056, 9555, 0)) },
				new String[] {}),

		// finished
		STEEL_DRAGON(1,
				new Location[] { new Location("Brimhaven Dungeon(Steel Dragon)", new WorldTile(2709, 9473, 0)) },
				new String[] {}),

		// finished
		SUQAH(1, new Location[] { new Location("Lunar Island(Suqah)", new WorldTile(2127, 3875, 0)) }, new String[] {}),

		// finished
		WARPED_TERRORBIRD(56,
				new Location[] { new Location("Poison Waste Slayer Dungeon", new WorldTile(1996, 4207, 1)) },
				new String[] {}),

		// finished
		WATERFIEND(1, new Location[] { new Location("Ancient cavern", new WorldTile(1770, 5362, 0)) }, new String[] {}),

		// finished
		TZHAAR(1, new Location[] { new Location("Tzhaar City", new WorldTile(4672, 5151, 0)) }, new String[] {}),

		GENERAL_GRAARDOR(1, new Location[] { new Location("GodWars Dungeon", new WorldTile(2882, 5311, 0)) },
				new String[] {}),
		COMMANDER_ZILYANA(1, new Location[] { new Location("GodWars Dungeon", new WorldTile(2882, 5311, 0)) },
				new String[] {}),
		SUNFREET(1, new Location[] { new Location("SUNFREET Dungeon", new WorldTile(3536, 5191, 0)) }, new String[] {}),
		KING_BLACK_DRAGON(1, new Location[] { new Location("KBD Lair", new WorldTile(3049, 3516, 0)) },
				new String[] {}),
		TORMENTED_DEMON(1, new Location[] { new Location("TORMENTED DEMON DUNGEON", new WorldTile(2571, 5735, 0)) },
				new String[] {}),
		CORPOREAL_BEAST(1, new Location[] { new Location("Corporeal Beast Lair", new WorldTile(2946, 4383, 2)) },
				new String[] {}),
		KALPHITE_KING(1, new Location[] { new Location("KALPHITE KING Lair", new WorldTile(2974, 1654, 0)) },
				new String[] {}),
		HELWYR(1, new Location[] { new Location("GODWARS 2 Dungeon", new WorldTile(4323, 860, 0)) }, new String[] {}),
		VINDICTA(1, new Location[] { new Location("GODWARS 2 Dungeon", new WorldTile(4323, 860, 0)) }, new String[] {}),
		AIRUT(92, new Location[] { new Location("Airut Dungeon", new WorldTile(1645, 5315, 0)) }, new String[] {}),
		WYVERN(96, new Location[] { new Location("Whyvern cavern", new WorldTile(5158, 7536, 0)) }, new String[] {}),
		HYDRIX_DRAGON(101, new Location[] { new Location("Gem dragon dungeon", new WorldTile(3296, 9824, 0)) }, new String[] {}),
		ONYX_DRAGON(98, new Location[] { new Location("Gem dragon dungeon", new WorldTile(3296, 9824, 0)) }, new String[] {}),
		DRAGONSTONE_DRAGON(95, new Location[] { new Location("Gem dragon dungeon", new WorldTile(3296, 9824, 0)) }, new String[] {}),
		//TELOS(1, new Location[] { new Location("Telos, The Warden", new WorldTile(4762, 5135, 0)) }, new String[] {}),
		NYMORA(1, new Location[] { new Location("GODWARS 2 Dungeon (Twin Furies)", new WorldTile(4323, 860, 0)) }, new String[] {}),
		ACHERON_MAMMOTH(96, new Location[] { new Location("ICEBERG", new WorldTile(3424, 4378, 0)) }, new String[] {}),
		RIPPER_DEMON(96, new Location[] { new Location("RIPPER DEMON CAVE", new WorldTile(5152, 7584, 0)) }, new String[] {}),
		MAGISTER(115, new Location[] { new Location("Zamorakian Crypt", new WorldTile(2642, 5989, 0)) }, new String[] {}),
		NEX(1, new Location[] { new Location("Godwar Dungeon", new WorldTile(2882, 5311, 0)) }, new String[] {}),
		
		EDIMMU(90, new Location[] { new Location("Edimmu Reasource Dungeon", new WorldTile(1364, 4635, 0)) },
				new String[] {});

		private Location[] locations;
		private String[] tips;
		private SlayerTask[] alternatives;
		private int levelRequried;

		private SlayerTask(int levelRequried, Location[] locations, String[] tips, SlayerTask... alternatives) {
			this.levelRequried = levelRequried;
			this.locations = locations;
			this.tips = tips;
			this.alternatives = alternatives;
		}

		public Location[] getLocations() {
			return locations;
		}

		public String[] getTips() {
			return tips;
		}

		public SlayerTask[] getAlternatives() {
			return alternatives;
		}

		public int getLevelRequried() {
			return levelRequried;
		}

		public String getName() {
			return Utils.formatString(toString());
		}
	}

	public static boolean canAttackNPC(int slayerLevel, String name) {
		return slayerLevel >= getLevelRequirement(name);
	}

	public static Location[] getCustomeLocations(SlayerTask task) {
		if (task == null)
			return null;
		switch (task) {
		case ABYSSAL_DEMON:
			return new Location[] { new Location("Slayer tower (Abyssal demon", new WorldTile(3414, 3550, 2)) };
		case SKELETON:
			return new Location[] { new Location("Varrock Sewers(Skeleton)", new WorldTile(3280, 9904, 0)),
					new Location("Meiyerditch Dungeon(Skeletal Hand)", new WorldTile(3614, 9763, 0)) };
		case CRAWLING_HAND:
			return new Location[] { new Location("Slayer Tower(Crawling Hand)", new WorldTile(3423, 3529, 0)) };
		case GHOUL:
			return new Location[] { new Location("North Of Canifis(Ghoul)", new WorldTile(3383, 3483, 0)) };
		case FIRE_GIANT:
			return new Location[] { new Location("Waterfall Dungeon(Fire Giant)", new WorldTile(2568, 9897, 0)) };
		case INFERNAL_MAGE:
			return new Location[] { new Location("Slayer tower(Infernal Mage)", new WorldTile(3416, 3565, 1)) };
		case CYCLOPS:
			return new Location[] { new Location("God Wars Dungeon(Cyclops)", new WorldTile(2855, 5353, 0), "GodWars"),
					new Location("Warriors Guild(Cyclops)", new WorldTile(2879, 3542, 0)) };
		case BLOODVELD:
			return new Location[] {
					new Location("God Wars Dungeon(Bloodveld)", new WorldTile(2887, 5350, 0), "GodWars"),
					new Location("Slayer tower(Bloodveld)", new WorldTile(3418, 3541, 1)),
					new Location("Meiyerditch Dungeon(Bloodveld)", new WorldTile(3585, 9738, 0)) };
		case DARK_BEAST:
			return new Location[] {
					new Location("Forinthry Dungeon(Wildy)", new WorldTile(3102, 10138, 0), "Wilderness"),
					new Location("Kuradal's Dungeon(Agressive)", new WorldTile(1651, 5281, 0)) };
		case NECHRYAEL:
			return new Location[] { new Location("Slayer tower(Nechryael)", new WorldTile(3415, 3566, 2)) };
		default:
			break;
		}
		return null;
	}

	public static int getLevelRequirement(String name) {
		for (SlayerTask task : SlayerTask.values()) {
			if (name.toLowerCase().contains(task.toString().replace("_", " ").toLowerCase())) {
				return task.getLevelRequried();
			}
		}
		return 1;
	}

	public static boolean hasNosepeg(Entity target) {
		if (!(target instanceof Player))
			return true;
		Player targetPlayer = (Player) target;
		int hat = targetPlayer.getEquipment().getHatId();
		return hat == 4168 || hasSlayerHelmet(target);
	}

	public static boolean hasEarmuffs(Entity target) {
		if (!(target instanceof Player))
			return true;
		Player targetPlayer = (Player) target;
		int hat = targetPlayer.getEquipment().getHatId();
		return hat == 4166 || hat == 13277 || hasSlayerHelmet(target);
	}

	public static boolean hasMask(Entity target) {
		if (!(target instanceof Player))
			return true;
		Player targetPlayer = (Player) target;
		int hat = targetPlayer.getEquipment().getHatId();
		return hat == 1506 || hat == 4164 || hat == 13277 || hasSlayerHelmet(target);
	}

	public static boolean hasWitchWoodIcon(Entity target) {
		if (!(target instanceof Player))
			return true;
		Player targetPlayer = (Player) target;
		int hat = targetPlayer.getEquipment().getAmuletId();
		return hat == 8923;
	}

	public static boolean hasSlayerHelmet(Entity target) {
		if (!(target instanceof Player))
			return true;
		Player targetPlayer = (Player) target;
		int hat = targetPlayer.getEquipment().getHatId();
		return hat == 13263 || hat == 14636 || hat == 14637 || hasFullSlayerHelmet(target)
				|| hasSpecialSlayerHelmet(target);
	}

	public static boolean hasFullSlayerHelmet(Entity target) {
		if (!(target instanceof Player))
			return true;
		Player targetPlayer = (Player) target;
		int hat = targetPlayer.getEquipment().getHatId();
		return hat == 15492 || hat == 15496 || hat == 15497 || hat == 30686 || (hat >= 22528 && hat <= 22550);
	}

	public static boolean hasSpecialSlayerHelmet(Entity target) {
		if (!(target instanceof Player))
			return true;
		Player targetPlayer = (Player) target;
		int hat = targetPlayer.getEquipment().getHatId();
		return hat == 30656 || hat == 30686 || hat == 30716 || hat == 40377;
	}

	public static boolean hasSlayerKhopesh(Entity target) {
		if (!(target instanceof Player))
			return true;
		Player targetPlayer = (Player) target;
		int mainhand = targetPlayer.getEquipment().getWeaponId();
		return mainhand == 40655 || mainhand == 42524 || mainhand == 42527 || mainhand == 42530
				|| mainhand == 42533 || mainhand == 42091 || mainhand == 40312 || mainhand == 42091;
		
	}

	public static boolean hasSlayerTwang(Entity target) {
		if (!(target instanceof Player))
			return true;
		Player targetPlayer = (Player) target;
		int mainhand = targetPlayer.getEquipment().getWeaponId();
		return mainhand == 37125;
	}

	public static boolean hasSlayerOffTwang(Entity target) {
		if (!(target instanceof Player))
			return true;
		Player targetPlayer = (Player) target;
		int offhand = targetPlayer.getEquipment().getWeaponId();
		return offhand == 37126;	
	}

	public static boolean hasSlayerRaptor(Entity target) {
		if (!(target instanceof Player))
			return true;
		Player targetPlayer = (Player) target;
		int hat = targetPlayer.getEquipment().getHatId();
		int chest = targetPlayer.getEquipment().getChestId();
		int legs = targetPlayer.getEquipment().getLegsId();
		int shieldId = targetPlayer.getEquipment().getShieldId();
		return hat == 36035 || chest == 36036 || legs == 36037 || shieldId == 36040;
		
	}

	public static boolean hasoffhandKhopesh(Entity target) {
		if (!(target instanceof Player))
			return true;
		Player targetPlayer = (Player) target;
		int offhand = targetPlayer.getEquipment().getShieldId();
		return offhand == 40659 || offhand == 42094 || offhand == 42536 || offhand == 42539
				|| offhand == 42542 || offhand == 42545 || offhand == 40316;
	}

	public static boolean hasReflectiveEquipment(Entity target) {
		if (!(target instanceof Player))
			return true;
		Player targetPlayer = (Player) target;
		int shieldId = targetPlayer.getEquipment().getShieldId();
		return shieldId == 4156;
	}

	public static boolean hasSpinyHelmet(Entity target) {
		if (!(target instanceof Player))
			return true;
		Player targetPlayer = (Player) target;
		int hat = targetPlayer.getEquipment().getHatId();
		return hat == 4551 || hasSlayerHelmet(target);
	}

	public static boolean isUsingBell(final Player player) {
		player.lock(3);
		player.setNextAnimation(new Animation(6083));
		List<WorldObject> objects = World.getRegion(player.getRegionId()).getAllObjects();
		if (objects == null)
			return false;
		for (final WorldObject object : objects) {
			if (!object.withinDistance(player, 3) || object.getId() != 22545)
				continue;
			player.getPackets().sendGameMessage("The bell re-sounds loudly throughout the cavern.");
			WorldTasksManager.schedule(new WorldTask() {

				@Override
				public void run() {
					NPC npc = World.spawnNPC(5751, player, -1, true);
					npc.getCombat().setTarget(player);
					WorldObject o = new WorldObject(object);
					o.setId(22544);
					World.spawnObjectTemporary(o, 30000);
				}
			}, 1);
			return true;
		}
		return false;
	}

	private static final int[] SLAYER_HELMET_PARTS = { 8921, 4166, 4164, 4551, 4168 };
	private static final int[] FULL_SLAYER_HELMET_PARTS = { 13263, 15490, 15488 };

	public static boolean createSlayerHelmet(Player player, int itemUsed, int itemUsedWith) {
		if (itemUsed == itemUsedWith)
			return false;
		boolean firstCycle = false, secondCycle = false, full = false;
		for (int parts : SLAYER_HELMET_PARTS) {
			if (itemUsed == parts)
				firstCycle = true;
			if (itemUsedWith == parts)
				secondCycle = true;
		}
		if (!firstCycle || !secondCycle) {
			firstCycle = false;
			secondCycle = false;
			for (int parts : FULL_SLAYER_HELMET_PARTS) {
				if (itemUsed == parts)
					firstCycle = true;
				if (itemUsedWith == parts)
					secondCycle = true;
			}
			full = true;
		}
		if (firstCycle && secondCycle) {
			if (!player.getSlayerManager().hasLearnedSlayerHelmet()) {
				player.getPackets().sendGameMessage(
						"You don't know what to do with these parts. You should talk to an expert, perhaps they know how to assemble these parts.");
				return false;
			} else if (player.getSkills().getLevel(Skills.CRAFTING) < 55) {
				player.getPackets()
						.sendGameMessage("You need a Crafting level of 55 in order to assemble a slayer helmet.");
				return false;
			}
			for (int parts : (full ? FULL_SLAYER_HELMET_PARTS : SLAYER_HELMET_PARTS))
				if (!player.getInventory().containsItem(parts, 1))
					return false;
			for (int parts : (full ? FULL_SLAYER_HELMET_PARTS : SLAYER_HELMET_PARTS))
				player.getInventory().deleteItem(parts, 1);
			player.getInventory().addItem(new Item(full ? 15492 : FULL_SLAYER_HELMET_PARTS[0], 1));
			player.getPackets().sendGameMessage(
					full ? "You attach two parts to your slayer helmet." : "You combine all parts of the helmet.");
			return true;
		}
		return false;
	}

	public static void dissasembleSlayerHelmet(Player player, boolean full) {
		if (!(player.getInventory().getFreeSlots() >= (full ? 2 : 4))) {
			player.getPackets()
					.sendGameMessage("You don't have enough space in your inventory to dissassemble the helmet.");
			return;
		}
		player.getInventory().deleteItem(full ? 15492 : 13263, 1);
		if (full) {
			for (int parts : FULL_SLAYER_HELMET_PARTS)
				player.getInventory().addItemDrop(parts, 1);
		} else {
			for (int parts : SLAYER_HELMET_PARTS)
				player.getInventory().addItemDrop(parts, 1);
		}
	}

	public static boolean isSlayerHelmet(Item item) {
		return item.getName().toLowerCase().contains("slayer helm");
	}

	public static class Location implements Serializable {

		private static final long serialVersionUID = -6297054028111499899L;

		private String name;
		private int x, y, plane;
		private String controler = null;

		public Location(String name, WorldTile tile) {
			this.name = name;
			this.x = tile.getX();
			this.y = tile.getY();
			this.plane = tile.getPlane();
		}

		public Location(String name, WorldTile tile, String controler) {
			this.name = name;
			this.x = tile.getX();
			this.y = tile.getY();
			this.plane = tile.getPlane();
			this.controler = controler;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getX() {
			return x;
		}

		public void setX(int x) {
			this.x = x;
		}

		public int getY() {
			return y;
		}

		public void setY(int y) {
			this.y = y;
		}

		public int getPlane() {
			return plane;
		}

		public void setPlane(int plane) {
			this.plane = plane;
		}

		public WorldTile getTile() {
			return new WorldTile(x, y, plane);
		}

		public String getControler() {
			return controler;
		}

	}
}
