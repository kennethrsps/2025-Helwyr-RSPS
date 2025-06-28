package com.rs.game.player.content;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.rs.game.Animation;
import com.rs.game.ForceTalk;
import com.rs.game.Graphics;
import com.rs.game.World;
import com.rs.game.WorldObject;
import com.rs.game.WorldTile;
import com.rs.game.item.Item;
import com.rs.game.npc.NPC;
import com.rs.game.player.Player;

import com.rs.game.player.Skills;
import com.rs.game.player.dialogue.Dialogue;
import com.rs.utils.Logger;
import com.rs.utils.Utils;

public class LividFarm {

	private static List<Player> players;
	public static int[] PLANT_SPOTS = new int[] { 40492, 40505, 40534, 40532, 40504, 40486, 40464, 40487, 40499, 40533,
			40462, 40488, 40463, 40460, 40461 };
	public static int[] PLANT_SPOTS_CONFIGS = new int[] { 9052, 9055, 9058, 9056, 9054, 9048, 9047, 9049, 9053, 9057,
			9045, 9050, 9046, 9043, 9044 };
	public static int[] PLANT_SPOTS_TYPES = new int[] { 3, 4, 4, 4, 3, 2, 2, 2, 3, 4, 1, 2, 1, 1, 1 };
	public static int[] FENCES = new int[] { 40433, 40437, 40432, 40434, 40436, 40435 };
	public static int[] FENCES_CONFIGS = new int[] { 9037, 9041, 9036, 9038, 9040, 9039 };
	public static int PRODUCE = 40441, PRODUCE_CONFIG = 9042;

	public static int currentRound, currentProduce;
	public static int[] currentPlantSpots, currentFences;
	public static NPC Pauline;

	public static void init() {
		players = new ArrayList<Player>();
		Pauline = new NPC(13619, new WorldTile(2111, 3942, 0), -1, false);
		Pauline.setRandomWalk(0);
		GenerateRandomRound();
	}

	public static void process(Player player) {
		if (players == null) {
			System.out.println("[LividFarm] Tried to process null player!");
			return;
		}
		
		
		GenerateRandomRound();
		for (int i = 0; i < players.size(); i++) {
			if (player == null || !isAtLividFarm(player) || player.hasFinished()) {
				players.remove(i);
				if (player != null)
					player.setRoundProgress(new ArrayList<Integer>());
				continue;
			}
			player.setRoundProgress(new ArrayList<Integer>());
			sendAreaConfigs(player);
		}
		currentRound++;
		if (currentRound == 5)
			currentRound = 0;
	}

	public static void sendAreaConfigs(Player player) {
		for (int j = 0; j < PLANT_SPOTS_CONFIGS.length; j++) {
			boolean hasProgress = player.getRoundProgress() != null
					&& player.getRoundProgress().contains(PLANT_SPOTS_CONFIGS[j]);
			player.getPackets().sendConfigByFile(PLANT_SPOTS_CONFIGS[j],
					hasProgress ? (currentPlantSpots[j] == 3 ? 2 : 1): currentPlantSpots[j]);
		}
		for (int j = 0; j < FENCES_CONFIGS.length; j++) {
			boolean hasProgress = player.getRoundProgress() != null
					&& player.getRoundProgress().contains(FENCES_CONFIGS[j]);
			player.getPackets().sendConfigByFile(FENCES_CONFIGS[j], hasProgress ? 0 : currentFences[j]);
		}
		boolean hasProgress = player.getRoundProgress() != null && player.getRoundProgress().contains(PRODUCE_CONFIG);
		player.getPackets().sendConfigByFile(PRODUCE_CONFIG, hasProgress ? 0 : currentProduce);
	}

	public static void GenerateRandomRound() {
		currentPlantSpots = new int[15];
		currentFences = new int[6];
		currentProduce = 0;
		List<ArrayList<Integer>> plants = new ArrayList<ArrayList<Integer>>(3);
		for (int i = 0; i < 3; i++) {
			plants.add(new ArrayList<Integer>(5));
			for (int j = 0 + (i * 4); j < (5 + (i * 4)); j++)
				plants.get(i).add(i + j);
		}
		for (int i = 0; i < 3; i++) {
			Collections.shuffle(plants.get(i));
			plants.get(i).subList(1, 4).clear();
		}
		boolean first = false;
		for (int i = 0; i < currentPlantSpots.length; i++) {
			if (plants.get(i >= 0 && i <= 4 ? 0 : i >= 5 && i <= 9 ? 1 : 2).contains(i)) {
				currentPlantSpots[i] = first ? 0 : 3;
				first = !first;
			} else {
				currentPlantSpots[i] = Utils.random(2) == 0 ? 1 : 2;
			}
		}
		List<Integer> fences = new ArrayList<Integer>(6);
		for (int i = 0; i < 6; i++)
			fences.add(i);
		Collections.shuffle(fences);
		fences.subList(1, 5).clear();
		for (int i = 0; i < currentFences.length; i++) {
			if (fences.contains(i))
				currentFences[i] = 1;
		}
		if (currentRound == 2 || currentRound == 4) {
			currentProduce = 1;
			if (Pauline.getId() == 13620)
				Pauline.setNextNPCTransformation(13619);
		} else
			Pauline.setNextNPCTransformation(13620);
	}

	public static boolean HandleLividFarmObjects(Player player, WorldObject object, int option) {
		if (object.getId() == 40444) {
			if (option == -1)
				return false;
			player.setRouteEvent(new RouteEvent(object, new Runnable() {
				@Override
				public void run() {
					player.faceObject(object);
					player.getInventory().addItem(20702, option == 2 ? 5 : 1);
				}
			}));
			return true;
		}
		for (int i = 0; i < PLANT_SPOTS.length; i++) {
			if (PLANT_SPOTS[i] == object.getId()) {
				player.faceObject(object);
				if (option == -1) {
					boolean hasProgress = player.getRoundProgress() != null
							&& player.getRoundProgress().contains(PLANT_SPOTS_CONFIGS[i]);
					String name = currentPlantSpots[i] == 3 ? (hasProgress ? "Livid" : "Diseased livid")
							: currentPlantSpots[i] == 0 ? (hasProgress ? "Fertilised patch" : "Empty patch")
									: currentPlantSpots[i] == 1 ? ("Fertilised patch") : "Livid";
					player.getPackets().sendGameMessage("It's a " + name + ".");
					player.getPackets().sendResetMinimapFlag();
					return true;
				}
				if (currentPlantSpots[i] == 3) {// cure
					player.getDialogueManager().startDialogue(new Dialogue() {
						int index;

						@Override
						public void start() {
							index = (int) parameters[0];
							player.getInterfaceManager().sendInventoryInterface(1081);
						}

						@Override
						public void run(int interfaceId, int componentId) {
							player.closeInterfaces();
							int selectedPlant = componentId - 2;
							int points = player.getSkills().getLevel(Skills.MAGIC) >= 66 ? 20 : 10;
							if (PLANT_SPOTS_TYPES[index] == selectedPlant) {
								if (!Magic.checkSpellRequirements(player, 1, true, Magic.ASTRAL_RUNE, 1,
										Magic.EARTH_RUNE, 8))
									return;
								player.getPackets().sendConfigByFile(PLANT_SPOTS_CONFIGS[index], 2);
								player.getRoundProgress().add(PLANT_SPOTS_CONFIGS[index]);
								player.setLividFarmProduce(player.getLividFarmProduce() + points);
								if (player.getSkills().getLevel(Skills.MAGIC) >= 66)
									player.getSkills().addXp(Skills.MAGIC, 60);
								player.getSkills().addXp(Skills.FARMING, 91.8);
								player.getPackets().sendGameMessage("You cure the plant!", true);
							}
							if (PLANT_SPOTS_TYPES[index] != selectedPlant)
								player.getPackets().sendGameMessage("You choose the wrong cure for this plant.");
							player.lock(1);
							player.setNextAnimation(new Animation(4411));
							player.setNextGraphics(new Graphics(748));
						}

						@Override
						public void finish() {

						}
					}, i);
				} else if (currentPlantSpots[i] == 0) {
					if (!Magic.checkSpellRequirements(player, 1, true, Magic.ASTRAL_RUNE, 3, Magic.EARTH_RUNE, 15, 561,
							2))
						return true;
					int points = player.getSkills().getLevel(Skills.MAGIC) >= 83 ? 20 : 10;
					player.lock(1);
					player.setNextAnimation(new Animation(4413));
					World.sendGraphics(player, new Graphics(724),
							new WorldTile(object.getX(), object.getY(), object.getPlane()));
					player.setLividFarmProduce(player.getLividFarmProduce() + points);
					player.getPackets().sendConfigByFile(PLANT_SPOTS_CONFIGS[i], 1);
					player.getRoundProgress().add(PLANT_SPOTS_CONFIGS[i]);
					if (player.getSkills().getLevel(Skills.MAGIC) >= 83)
						player.getSkills().addXp(Skills.MAGIC, 87);
					player.getSkills().addXp(Skills.FARMING, 91.8);
					player.getPackets().sendGameMessage("You fertilise the soil!", true);
				}
				return true;
			}
		}
		for (int i = 0; i < FENCES.length; i++) {
			if (FENCES[i] == object.getId()) {
				player.faceObject(object);
				if (option == -1) {
					boolean hasProgress = player.getRoundProgress() != null
							&& player.getRoundProgress().contains(FENCES_CONFIGS[i]);
					String name = currentFences[i] == 1 ? (hasProgress ? "Fence" : "Broken fence") : "Fence";
					player.getPackets().sendGameMessage("It's a " + name + ".");
					player.getPackets().sendResetMinimapFlag();
					return true;
				}
				if (currentFences[i] == 1) {
					final int index = i;
					player.setRouteEvent(new RouteEvent(object, new Runnable() {
						@Override
						public void run() {
							if (!player.getInventory().containsItem(20703, 1)) {
								player.getPackets().sendGameMessage("You don't have enough fencepost to fix that");
								return;
							}
							int points = player.getSkills().getLevel(Skills.MAGIC) >= 86 ? 20 : 10;
							player.getInventory().deleteItem(20703, 1);
							player.setNextAnimation(new Animation(898));
							player.getPackets().sendConfigByFile(FENCES_CONFIGS[index], 0);
							player.getRoundProgress().add(FENCES_CONFIGS[index]);
							player.setLividFarmProduce(player.getLividFarmProduce() + points);
							if (player.getSkills().getLevel(Skills.MAGIC) >= 86)
								player.getSkills().addXp(Skills.MAGIC, 90);
							player.getSkills().addXp(Skills.CONSTRUCTION, 54.6);
							player.getPackets().sendGameMessage("You fix the broken fence.", true);
						}
					}));
				}
				return true;
			}
		}
		if (object.getId() == PRODUCE) {
			if (option == -1) {
				boolean hasProgress = player.getRoundProgress() != null
						&& player.getRoundProgress().contains(PRODUCE_CONFIG);
				String name = currentProduce == 1 ? (hasProgress ? "Produce pile (empty)" : "Produce pile (full)")
						: "Produce pile (empty)";
				player.getPackets().sendGameMessage("It's a " + name + ".");
				player.getPackets().sendResetMinimapFlag();
				return true;
			}
			player.setRouteEvent(new RouteEvent(object, new Runnable() {
				@Override
				public void run() {
					player.faceObject(object);
					if (currentProduce == 1) {
						if (player.getInventory().getFreeSlots() < 10) {
							player.getPackets().sendGameMessage("You need atleast 10 free slots to pick the produce.");
							return;
						}
						player.getInventory().addItem(20704, 10);
						player.getPackets().sendConfigByFile(PRODUCE_CONFIG, 0);
						player.getRoundProgress().add(PRODUCE_CONFIG);
						player.getPackets().sendGameMessage("You take the produce from the pile.", true);

					}
				}
			}));
			return true;
		}
		if (object.getId() == 40443) {
			player.setRouteEvent(new RouteEvent(object, new Runnable() {
				@Override
				public void run() {
					player.faceObject(object);
					if (!player.getInventory().containsItem(20705, 1)) {
						player.getPackets().sendGameMessage("You don't have anything to deposit");
						return;
					}
					int points = (player.getSkills().getLevel(Skills.MAGIC) >= 80 ? 120 : 60)
							* player.getInventory().getAmountOf(20705);
					player.getInventory().deleteItem(20705, player.getInventory().getAmountOf(20705));
					player.setLividFarmProduce(player.getLividFarmProduce() + points);
					if (player.getSkills().getLevel(Skills.MAGIC) >= 86)
						player.getSkills().addXp(Skills.MAGIC, 83);
					player.getSkills().addXp(Skills.CRAFTING, 270);
					player.getPackets().sendGameMessage("You deposit the plant bunch into the wagon", true);
				}
			}));
			return true;
		}
		return false;
	}

	public static boolean HandleLividFarmNpc(Player player, NPC npc, int option) {
		if (npc.getId() == 13619 || npc.getId() == 13620) {
			if (option == 1) {
				player.setRouteEvent(new RouteEvent(npc, new Runnable() {
					@Override
					public void run() {
						player.faceEntity(npc);
						player.getDialogueManager().startDialogue("PaulinePolaris", npc.getId());
					}
				}));
			} else if (option == 2) {
				player.setRouteEvent(new RouteEvent(npc, new Runnable() {
					@Override
					public void run() {
						player.faceEntity(npc);
						player.getPackets().sendIComponentText(1083, 201, player.getLividFarmProduce() + "");
						player.getInterfaceManager().sendInterface(1083);

					}
				}));
			} else if (option == 3) {
				player.faceEntity(npc);
				if (npc.getId() == 13619) {
					player.getPackets().sendGameMessage("Pauline is already feeling good.");
					return true;
				}
				if (player.getRoundProgress().contains(npc.getId())) {
					player.getPackets().sendGameMessage("You have already encouraged Pauline.");
					return true;
				}
				player.getDialogueManager().startDialogue(new Dialogue() {
					public String[][] paulineE = {
							{ "Your doing a slow job.", "You're not doing a fantastic job.",
									"You're doing a fantastic job.", "You're doing okay, I suppose." },
							{ "Come on, you're doing so well.", "Come on or I'm going home.",
									"Come on, you're rubbish!", "Come on and help, lazy." },
							{ "Keep going! I'm not doing this.", "Keep going! We can do this.",
									"Keep away! I'll do this.", "Today would be nice!" },
							{ "Lokar won't appreciate this.", "Lokar will really appreciate this.",
									"I don't know why you bother", "Hello? Anybody there?" },
							{ "Everyone's working except you.", "Care to join me!",
									"Look at all the produce being made.", "Wakey wakey." },
							{ "Lazybones!", "Slow-coach!", "Sleepy!", "Extraordinary!" } };
					int index;

					@Override
					public void start() {
						index = Utils.random(paulineE.length);
						sendOptionsDialogue("CHOOSE A LINE TO ENCOURAGE PAULINE", paulineE[index]);
					}

					@Override
					public void run(int interfaceId, int componentId) {
						int selectedOption = componentId == OPTION_1 ? 0
								: componentId == OPTION_2 ? 1
										: componentId == OPTION_3 ? 2 : componentId == OPTION_4 ? 3 : 4;
						int correctOption = index == 0 || index == 4 ? 2
								: index == 1 ? 0 : index == 2 || index == 3 ? 1 : 3;
						end();
						player.setNextForceTalk(new ForceTalk(paulineE[index][selectedOption]));
						player.setNextAnimation(new Animation(4411));
						if (correctOption == selectedOption) {
							player.getRoundProgress().add(13620);
							Pauline.setNextGraphics(new Graphics(725, 0, 100));
							Pauline.setNextForceTalk(new ForceTalk("Thank You!"));
						}

					}

					@Override
					public void finish() {
					}
				});
			}
			return true;
		}
		return false;
	}

	public static boolean handleItemOption1(Player player, Item item) {
		if (item.getId() == 20702) {
			if (!Magic.checkSpellRequirements(player, 1, true, Magic.NATURE_RUNE, 1, Magic.ASTRAL_RUNE, 2,
					Magic.EARTH_RUNE, 15))
				return true;
			player.lock(3);
			player.setNextAnimation(new Animation(6298));
			player.setNextGraphics(new Graphics(1063, 0, 50));
			player.getInventory().deleteItem(item);
			player.getInventory().addItem(20703, 1);
			player.getPackets().sendGameMessage("You made the lumber into a fencepost.", true);
			return true;
		}
		if (item.getId() == 20704) {
			if (!player.getInventory().containsItem(item.getId(), 5)) {
				player.getPackets().sendGameMessage("You need atleast 5 Livid Plants to make them into a bunch.");
				return true;
			}
			if (!Magic.checkSpellRequirements(player, 1, true, Magic.ASTRAL_RUNE, 2, Magic.EARTH_RUNE, 10,
					Magic.WATER_RUNE, 5))
				return true;
			player.lock(1);
			player.setNextGraphics(new Graphics(728, 0, 100));
			player.setNextAnimation(new Animation(4412));
			player.getInventory().deleteItem(20704, 5);
			player.getInventory().addItem(20705, 1);
			player.getPackets().sendGameMessage("You stung 5 Livid Plants into a bunch.", true);
			return true;
		}
		return false;
	}

	public static void EnterLividFarm(Player player) {
		if (players.contains(player))
			return;
		players.add(player);
		sendAreaConfigs(player);
	}

	public static boolean isAtLividFarm(Player player) {
		return (player.getX() >= 2108 && player.getX() <= 2114 && player.getY() >= 3940 && player.getY() <= 3941)
				|| (player.getX() >= 2097 && player.getX() <= 2119 && player.getY() >= 3942 && player.getY() <= 3950)
				|| (player.getX() >= 2108 && player.getX() <= 2119 && player.getY() >= 3951 && player.getY() <= 3952);
	}

	public static List<Player> getPlayers() {
		return players;
	}
	
	
}
