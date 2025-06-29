package com.rs.game.player.controllers;

import com.rs.Settings;
import com.rs.game.Animation;
import com.rs.game.Entity;
import com.rs.game.ForceTalk;
import com.rs.game.WorldObject;
import com.rs.game.WorldTile;
import com.rs.game.npc.NPC;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.game.player.content.pet.Pets;
import com.rs.game.player.dialogue.Dialogue;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Colors;
import com.rs.utils.Utils;

/**
 * Handles the Dungeoneering controller.
 * 
 * @author Zeus
 */
public class Dungeoneering extends Controller {

	@Override
	public void start() {
		sendInterfaces();
	}

	@Override
	public void sendInterfaces() {
		player.getInterfaceManager().sendOverlay(988, false);
		player.getPackets().sendIComponentText(988, 0, "" + player.dungKills);
		player.getPackets().sendIComponentText(988, 1, "Kills:");
	}

	public static boolean hasPrimalHelm(Entity target) {
		if (!(target instanceof Player))
			return true;
		Player targetPlayer = (Player) target;
		int hat = targetPlayer.getEquipment().getWeaponId();
		return hat == 16711 || hat == 20824;
	}

	public static boolean hasPrimalChest(Entity target) {
		if (!(target instanceof Player))
			return true;
		Player targetPlayer = (Player) target;
		int chest = targetPlayer.getEquipment().getWeaponId();
		return chest == 17259 || chest == 16733 || chest == 20822;
	}

	public static boolean hasPrimalLegs(Entity target) {
		if (!(target instanceof Player))
			return true;
		Player targetPlayer = (Player) target;
		int legs = targetPlayer.getEquipment().getWeaponId();
		return legs == 20823 || legs == 16667 || legs == 16689;
	}

	public static boolean hasStarFire(Entity target) {
		if (!(target instanceof Player))
			return true;
		Player targetPlayer = (Player) target;
		int weapon = targetPlayer.getEquipment().getWeaponId();
		return weapon == 28095 || weapon == 28099 || weapon == 28103;
	}

	@Override
	public boolean login() {
		return false;
	}

	@Override
	public boolean logout() {
		return false;
	}

	@Override
	public void magicTeleported(int teleType) {
		player.inDungeoneering = false;
		player.StarfireBoss1 = false;
		player.StarfireBoss2 = false;
		player.getControlerManager().forceStop();
	}

	public static boolean DragonKinArea(WorldTile tile, Player player) {
		int destX = player.getX();
		int destY = player.getY();
		return (destX >= 4865 && destY >= 8961 && destX <= 5181 && destY <= 9728);
	}

	/**
	 * Gets the required kills amount to enter next room.
	 * 
	 * @return the amount as Integer.
	 */
	private int getKcRequired() {
		if (player.getPerkManager().dungeon)
			return 5;
		return 15;
	}

	private int getKcRequired2() {
		if (player.getPerkManager().dungeon)
			return 10;
		return 20;
	}

	private int getKcRequired3() {
		if (player.getPerkManager().dungeon)
			return 1;
		return 1;
	}

	private int getKcRequired4() {
		if (player.getPerkManager().dungeon)
			return 3;
		return 6;
	}

	private int getKcRequired5() {
		if (player.getPerkManager().dungeon)
			return 1000;
		return 1000;
	}

	/**
	 * Handles Dungeoneering magical barriers.
	 * 
	 * @param destX     The X coordinate to walk to.
	 * @param destY     The Y coordinate to walk to.
	 * @param requireKC if requires killcount to pass.
	 */

	private void handleDoor(int destX, int destY, boolean requireKC, int requireLVL) {
		if (requireKC) {
			if (player.getSkills().getLevel(Skills.DUNGEONEERING) < requireLVL) {
				player.sendMessage("You'll need a Dungeoneering level of " + requireLVL + " to enter this room.");
				return;
			}
			if (player.dungKills < getKcRequired()) {
				player.sendMessage("You need " + Colors.red + getKcRequired() + "</col> kills to enter this room; "
						+ "you only have " + Colors.red + player.dungKills + "</col>.");
				return;
			}
			player.dungKills = 0;
			sendInterfaces();
		}
		player.addWalkSteps(destX, destY, 2, false);
		player.lock(2);
	}

	private void handleDoor2(int destX, int destY, boolean requireKC, int requireLVL) {
		if (requireKC) {
			if (player.getSkills().getLevel(Skills.DUNGEONEERING) < requireLVL) {
				player.sendMessage("You'll need a Dungeoneering level of " + requireLVL + " to enter this room.");
				return;
			}
			if (player.dungKills < getKcRequired2()) {
				player.sendMessage("You need " + Colors.red + getKcRequired2() + "</col> kills to enter this room; "
						+ "you only have " + Colors.red + player.dungKills + "</col>.");
				return;
			}
			player.dungKills = 0;
			sendInterfaces();
		}
		player.addWalkSteps(destX, destY, 2, false);
		player.lock(2);
	}

	private void handleDoor3(int destX, int destY, boolean requireKC, int requireLVL) {
		if (requireKC) {
			if (player.getSkills().getLevel(Skills.DUNGEONEERING) < requireLVL) {
				player.sendMessage("You'll need a Dungeoneering level of " + requireLVL + " to enter this room.");
				return;
			}
			if (player.dungKills < getKcRequired3()) {
				player.sendMessage("You need " + Colors.red + getKcRequired3() + "</col> kills to enter this room; "
						+ "you only have " + Colors.red + player.dungKills + "</col>.");
				return;
			}
			player.dungKills = 0;
			sendInterfaces();
		}
		player.addWalkSteps(destX, destY, 2, false);
		player.lock(2);
	}

	private void handleDoor4(int destX, int destY, boolean requireKC, int requireLVL) {
		if (requireKC) {
			if (player.getSkills().getLevel(Skills.DUNGEONEERING) < requireLVL) {
				player.sendMessage("You'll need a Dungeoneering level of " + requireLVL + " to enter this room.");
				return;
			}
			if (player.dungKills < getKcRequired4()) {
				player.sendMessage("You need " + Colors.red + getKcRequired4() + "</col> kills to enter this room; "
						+ "you only have " + Colors.red + player.dungKills + "</col>.");
				return;
			}
			player.dungKills = 0;
			sendInterfaces();
		}
		player.addWalkSteps(destX, destY, 2, false);
		player.lock(2);
	}

	public void handleDoor5(int destX, int destY, boolean requireKC, int requireLVL) {
		if (requireKC) {
			if (player.getSkills().getLevel(Skills.DUNGEONEERING) < requireLVL) {
				player.sendMessage("You'll need a Dungeoneering level of " + requireLVL + " to enter this room.");
				return;
			}
			if (player.dungKills < getKcRequired5()) {
				player.sendMessage("You need " + Colors.red + getKcRequired5() + "</col> kills to enter this room; "
						+ "you only have " + Colors.red + player.dungKills + "</col>.");
				return;
			}
			player.dungKills = 0;
			sendInterfaces();
		}
		player.addWalkSteps(destX, destY, 2, false);
		player.lock(2);
	}

	@Override
	public boolean processObjectClick1(final WorldObject object) {
		if (object.getId() == 11005) {
			player.lock(1);
			if (object.getX() == 3978 && object.getY() == 5552) {
				player.dungKills = 0;
				player.inDungeoneering = false;
				player.StarfireBoss1 = false;
				player.StarfireBoss2 = false;
				player.addWalkSteps(3977, 5552, 2, false);
				player.getControlerManager().forceStop();
				return false;
			}
			if (object.getX() == 3989 && object.getY() == 5548) {
				if (player.getY() >= 5549)
					handleDoor(3989, 5547, true, 10);
				else
					handleDoor(3989, 5549, false, 10);
				return false;
			}
			if (object.getX() == 4004 && object.getY() == 5544) {
				if (player.getX() <= 4003)
					handleDoor(4005, 5544, true, 20);
				else
					handleDoor(4003, 5544, false, 20);
				return false;
			}
			if (object.getX() == 4016 && object.getY() == 5544) {
				if (player.getX() <= 4015)
					handleDoor(4017, 5544, true, 30);
				else
					handleDoor(4015, 5544, false, 30);
				return false;
			}
			if ((object.getX() == 4023 || object.getX() == 4024) && object.getY() == 5537) {
				if (player.getY() >= 5538)
					handleDoor(player.getX(), 5536, true, 40);
				else
					handleDoor(player.getX(), 5538, false, 40);
				return false;
			}
			if (object.getX() == 4024 && object.getY() == 5520) {
				if (player.getY() >= 5521)
					handleDoor(4024, 5519, true, 50);
				else
					handleDoor(4024, 5521, false, 50);
				return false;
			}
			if (object.getX() == 4017 && object.getY() == 5503) {
				if (player.getY() >= 5504)
					handleDoor(4017, 5502, true, 60);
				else
					handleDoor(4017, 5504, false, 60);
				return false;
			}
			if (object.getX() == 4009 && object.getY() == 5495) {
				if (player.getX() >= 4010)
					handleDoor(4008, 5495, true, 70);
				else
					handleDoor(4010, 5495, false, 70);
				return false;
			}
			if (object.getX() == 3995 && object.getY() == 5490) {
				if (player.getY() >= 5491)
					handleDoor(3995, 5489, true, 80);
				else
					handleDoor(3995, 5491, false, 80);
				return false;
			}
			if (object.getX() == 3995 && object.getY() == 5474) {
				if (player.getY() >= 5475)
					handleDoor(3995, 5473, true, 90);
				else
					handleDoor(3995, 5475, false, 90);
				return false;
			}

			if (object.getX() == 5028 && object.getY() == 9219) {
				if (player.getY() >= 9220) {
					handleDoor2(5028, 9218, true, 90);
					player.StarfireBoss1 = true;
				} else {
					handleDoor2(5028, 9220, false, 90);
				}
				return false;
			}

			if (object.getX() == 5040 && object.getY() == 9199) {
				if (player.getY() >= 9200)
					handleDoor2(5040, 9180, true, 90);
				else
					handleDoor2(5040, 9200, false, 90);
				return false;
			}

			if (object.getX() == 5040 && object.getY() == 9181) {
				if (player.getY() >= 9182)
					handleDoor3(5040, 9178, true, 90);
				else
					handleDoor3(5040, 9182, false, 90);
				return false;
			}

			if (object.getX() == 5023 && object.getY() == 9158) {
				if (player.getX() >= 5024)
					handleDoor(5022, 9158, true, 90);
				else
					handleDoor(5024, 9158, false, 90);
				return false;
			}

			if (object.getX() == 5010 && object.getY() == 9163) {
				if (player.getY() >= 9162)
					handleDoor2(5010, 9164, true, 90);
				else
					handleDoor2(5010, 9162, false, 90);
				return false;
			}
			if (object.getX() == 4998 && object.getY() == 9151) {
				if (player.getX() >= 4999)
					handleDoor2(4997, 9151, true, 90);
				else
					handleDoor2(4999, 9151, false, 90);
				return false;
			}
			// getinto elegorn
			if (object.getX() == 4984 && object.getY() == 9152) {
				if (player.getX() >= 4985) {
					player.getDialogueManager().startDialogue(new Dialogue() {

						@Override
						public void start() {
							sendNPCDialogue(25656, 1, Colors.red
									+ " Notice:</col> You will only receive Starfire Points once, you need to start again or continue to Verak Lith to receive a Starfire Points <br>"
									+ "Elegorn will give you" + Colors.red
									+ " (400 Starfire Points)</col> while Verak Lith will give you" + Colors.red
									+ " (1200 Starfire Points)");
							stage = 0;
						}

						@Override
						public void run(int interfaceId, int componentId) {
							switch (stage) {

							case 0:
								sendOptionsDialogue("Do you want to do this?", "Yes", "No");
								stage = 1;
								break;
							case 1:
								switch (componentId) {
								case OPTION_1:
									handleDoor3(4983, 9152, true, 90);
									finish();
									break;
								case OPTION_2:
									finish();
									break;
								}
								break;
							}
						}

						@Override
						public void finish() {
							player.getInterfaceManager().closeChatBoxInterface();
						}

					});
				} else {
					handleDoor3(4983, 9152, false, 90);
					player.setNextForceTalk(
							new ForceTalk("Im not a coward.... at this point there is no turning back"));
				}
				return false;
			}
			// after elegorn
			if (object.getX() == 4984 && object.getY() == 9131) {
				if (player.getX() >= 4983)
					handleDoor3(4985, 9131, true, 90);
				else
					handleDoor5(4985, 9131, false, 90);
				player.setNextForceTalk(new ForceTalk("Im not a coward.... at this point there is no turning back"));
				return false;
			}
			if (object.getX() == 5003 && object.getY() == 9126) {
				if (player.getY() >= 9127)
					handleDoor4(5003, 9125, true, 90);
				else
					handleDoor4(5003, 9127, false, 90);
				return false;
			}
			if (object.getX() == 5024 && object.getY() == 9099) {
				if (player.getY() >= 9100)
					handleDoor(5027, 9098, true, 90);
				else
					handleDoor(5024, 9100, false, 90);
				return false;
			}
			if (object.getX() == 5048 && object.getY() == 9079) {
				if (player.getX() >= 5049 || player.getX() >= 5048)
					handleDoor4(5047, 9078, true, 90);
				else
					handleDoor4(5048, 9079, false, 90);
				return false;
			}
			if (object.getX() == 5072 && object.getY() == 9069) {
				if (player.getX() >= 5071)
					handleDoor4(5073, 9069, true, 90);
				else
					handleDoor4(5071, 9069, false, 90);
				return false;
			}
			if (object.getX() == 5070 && object.getY() == 9044) {
				if (player.getY() >= 9045) {

					player.getDialogueManager().startDialogue(new Dialogue() {

						@Override
						public void start() {
							sendNPCDialogue(25695, 1, Colors.red
									+ " Notice:</col> You will only receive Starfire Points once, you need to start all over again to receive a Starfire Points <br>"
									+ "Elegorn will give you" + Colors.red
									+ " (400 Starfire Points)</col> while Verak Lith will give you" + Colors.red
									+ " (1200 Starfire Points)");
							stage = 0;
						}

						@Override
						public void run(int interfaceId, int componentId) {
							switch (stage) {

							case 0:
								sendOptionsDialogue("Do you want to do this?", "Yes", "No");
								stage = 1;
								break;
							case 1:
								switch (componentId) {
								case OPTION_1:
									handleDoor4(5070, 9043, true, 90);
									finish();
									break;
								case OPTION_2:
									finish();
									break;
								}
								break;
							}
						}

						@Override
						public void finish() {
							player.getInterfaceManager().closeChatBoxInterface();
						}

					});
				} else {
					handleDoor4(5070, 9045, false, 90);
				}
				return false;
			}
			// rune dragon
			if (object.getX() == 5051 && object.getY() == 9032) {
				if (player.getX() >= 5050)
					handleDoor4(5053, 9032, true, 90);
				else
					handleDoor4(5050, 9032, false, 90);

				return false;
			}
		}
		return true;

	}

	@Override
	public boolean sendDeath() {
		WorldTasksManager.schedule(new WorldTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0) {
					player.setNextAnimation(new Animation(836));
					player.sendMessage("Oh dear, you have died.");
				}
				if (loop == 3) {
					if (!DragonKinArea(player, player)) {
						player.setNextWorldTile(new WorldTile(3972, 5553, 0));
					} else {
						player.setNextWorldTile(new WorldTile(5023, 9259, 0));
					}
					player.setNextAnimation(new Animation(-1));
					player.getControlerManager().forceStop();
					player.getPackets().sendMusicEffect(90);
					player.dungKills = 0;
					player.inDungeoneering = false;
					player.StarfireBoss1 = false;
					player.StarfireBoss2 = false;
					player.reset();
					stop();
				}
				loop++;
			}
		}, 0, 1);
		return false;
	}

	@Override
	public void forceClose() {
		player.getInterfaceManager().closeOverlay(false);
		player.dungKills = 0;
	}

	/**
	 * Used to handle NPC killing in the dungeon.
	 * 
	 * @param player The killer.
	 * @param npcId  The NPCId.
	 * @param xp     The amount of exp to give.
	 * @param tokens The amount of tokens to give.
	 */
	public static void handleDrop(Player player, NPC npc) {
		switch (npc.getName().toLowerCase()) {
		case "dungeon rat":
			handleReward(player, Utils.random(5, 30), Utils.random(10, 40));
			break;
		case "dungeon spider":
			handleReward(player, Utils.random(15, 50), Utils.random(20, 80));
			break;
		case "skeleton":
			handleReward(player, Utils.random(25, 90), Utils.random(40, 160));
			break;
		case "armoured zombie":
			handleReward(player, Utils.random(45, 170), Utils.random(80, 320));
			break;
		case "ghost":
			handleReward(player, Utils.random(85, 250), Utils.random(150, 500));
			break;
		case "earth warrior":
			handleReward(player, Utils.random(125, 270), Utils.random(250, 700));
			break;
		case "icefiend":
			handleReward(player, Utils.random(180, 400), Utils.random(350, 900));
			break;
		case "ice warrior":
			handleReward(player, Utils.random(250, 600), Utils.random(450, 1200));
			break;
		case "ice elemental":
			handleReward(player, Utils.random(350, 800), Utils.random(550, 1300));
			break;
		case "skeletal warrior":
			handleReward(player, Utils.random(1000, 3500), Utils.random(550, 5000));
			break;
		case "skeletal minion":
			handleReward(player, Utils.random(450, 1000), Utils.random(650, 1500));
			break;
		/**
		 * Dragonkin lab
		 */
		case "green dragon":
			DragonKinReward(player, Utils.random(125, 200), Utils.random(200, 400));
			break;
		case "red dragon":
			DragonKinReward(player, Utils.random(125, 200), Utils.random(200, 400));
			break;
		case "king black dragon":
			DragonKinReward(player, Utils.random(1000, 3500), Utils.random(550, 5000));
			break;
		case "celestial dragon":
			DragonKinReward(player, Utils.random(1000, 2500), Utils.random(1500, 5000));
			break;
		case "black demon":
			DragonKinReward(player, Utils.random(200, 350), Utils.random(300, 550));
			break;
		case "brutal green dragon":
			DragonKinReward(player, Utils.random(200, 325), Utils.random(300, 550));
			break;
		case "elegorn the celestial":
			DragonKinReward(player, Utils.random(1500, 4000), Utils.random(2000, 6000));
			break;
		case "verak lith":
			DragonKinReward(player, Utils.random(5000, 9000), Utils.random(7000, 15000));
			break;
		case "sunfreet":
			DragonKinReward(player, Utils.random(1000, 2500), Utils.random(510, 4000));
			break;
		case "rune dragon":
			DragonKinReward(player, Utils.random(1000, 3500), Utils.random(550, 800));
			break;
		case "dragonstone dragon":
			DragonKinReward(player, Utils.random(1000, 3500), Utils.random(700, 1000));
			break;
		case "onyx dragon":
			DragonKinReward(player, Utils.random(1000, 3500), Utils.random(550, 5000));
			break;
		case "hydrix dragon":
			DragonKinReward(player, Utils.random(1000, 3500), Utils.random(550, 5000));
			break;

		}
	}

	/**
	 * Used to handle NPC rewards in the dungeon.
	 * 
	 * @param player The killer.
	 * @param xp     The amount of exp to give.
	 * @param tokens The amount of tokens to give.
	 */
	private static void handleReward(Player player, int xp, int tokens) {
		if (!(player.getControlerManager().getControler() instanceof Dungeoneering))
			return;
		player.setDungeoneeringTokens((int) (player.getDungeoneeringTokens()
				+ ((player.getPerkManager().dungeon ? 1.25 : 1) * tokens * (Settings.DUNGEONEERING_WEEKEND ? 2 : 1))));
		player.sm("Dungeoneering tokens received: " + Colors.red
				+ ((player.getPerkManager().dungeon ? 1.25 : 1) * tokens * (Settings.DUNGEONEERING_WEEKEND ? 2 : 1)));
		player.getSkills().addXp(Skills.DUNGEONEERING,
				(player.getPerkManager().dungeon ? 1.25 : 1) * xp * (Settings.DUNGEONEERING_WEEKEND ? 2 : 1));
		Pets.checkSkillingPet(player, 38085);
		player.dungKills++;
		player.getInterfaceManager().sendOverlay(988, false);
		player.getPackets().sendIComponentText(988, 0, "" + player.dungKills);
		player.getPackets().sendIComponentText(988, 1, "Kills:");
	}

	private static void DragonKinReward(Player player, int xp, int tokens) {
		if (!(player.getControlerManager().getControler() instanceof Dungeoneering))
			return;
		player.setElitePoints((int) (player.getElitePoints()
				+ ((player.getPerkManager().dungeon ? 1.25 : 1) * tokens * (Settings.DUNGEONEERING_WEEKEND ? 2 : 1))));
		player.sm(Colors.red + "[Elite Dungeoneering]</col> tokens received: " + Colors.red
				+ ((player.getPerkManager().dungeon ? 1.25 : 1) * tokens * (Settings.DUNGEONEERING_WEEKEND ? 2 : 1)));
		player.getSkills().addXp(Skills.DUNGEONEERING,
				(player.getPerkManager().dungeon ? 1.25 : 1) * xp * (Settings.DUNGEONEERING_WEEKEND ? 2 : 1));
		Pets.checkSkillingPet(player, 38085);
		player.dungKills++;
		player.getInterfaceManager().sendOverlay(988, false);
		player.getPackets().sendIComponentText(988, 0, "" + player.dungKills);
		player.getPackets().sendIComponentText(988, 1, "Kills:");
	}
}