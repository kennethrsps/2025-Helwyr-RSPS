package com.rs.game.activites.soulwars;

import com.rs.game.World;
import com.rs.game.WorldObject;
import com.rs.game.WorldTile;
import com.rs.game.activites.soulwars.SoulWarsManager.PlayerType;
import com.rs.game.item.Item;
import com.rs.game.npc.NPC;
import com.rs.game.player.Skills;
import com.rs.game.player.controllers.Controller;
import com.rs.network.protocol.codec.decode.WorldPacketsDecoder;
import com.rs.utils.Utils;

/**
 * 
 * @author Savions Sw
 *
 */
public class AreaController extends Controller {
	
	private final static int[] SKILLS = { Skills.SLAYER, Skills.HITPOINTS,
			Skills.DEFENCE, Skills.STRENGTH, Skills.RANGE, Skills.ATTACK,
			Skills.PRAYER, Skills.MAGIC };
	
	private static final Item[] RARE = { new Item(995, Utils.random(1000000, 2000000)),
			// vanguard
			new Item(21473), new Item(21474), new Item(21472), new Item(21465), new Item(21466),
			// trickster
			new Item(21467), new Item(21468), new Item(21469), new Item(21470), new Item(21471),
			// battlemage
			new Item(21462), new Item(21463), new Item(21464), new Item(21475), new Item(21476),
			// capes
			new Item(15435), new Item(15434), new Item(995, Utils.random(1000000, 2000000))};
	
	private static final Item[] COMMON = { new Item(535, 23), new Item(6816, Utils.random(11, 23)),
			new Item(2860, Utils.random(18, 39)), new Item(6156, 5), new Item(533, 60), 
			new Item(6011, Utils.random(6, 8)), new Item(10041), new Item(10043), new Item(10045),
			new Item(527, Utils.random(209, 290)), new Item(537, 11), new Item(995, Utils.random(20000, 250000)),
			new Item(238, 22), new Item(10035), new Item(10037), new Item(10039), new Item(3326, Utils.random(19, 28)),
			new Item(12135, 7), new Item(12154, 7), new Item(3364, 20), new Item(6319, 12), new Item(9738, 6),
			new Item(6730, Utils.random(3, 5)), new Item(2463, 9), new Item(1754, 10), new Item(3139, 19),
			new Item(8432, 20), new Item(10047), new Item(10049), new Item(10051), new Item(6288, 10)};
	
	private final int[] CHARMS = { 12163, 12160, 12159, 12158 };
	
	@Override
	public void start() {
		player.getMusicsManager().playMusic(597);
		((AreaTask) World.soulWars.getTasks().get(PlayerType.OUTSIDE_LOBBY)).getPlayers().add(player);
		sendInterfaces();
	}
	
	@Override
	public void sendInterfaces() {
		boolean resizeable = player.getInterfaceManager().hasRezizableScreen();
		player.getInterfaceManager().sendOverlay(199, resizeable);
	}
	
	@Override
	public void forceClose() {
		boolean resizeable = player.getInterfaceManager().hasRezizableScreen();
		player.getInterfaceManager().closeOverlay(resizeable);
		((AreaTask) World.soulWars.getTasks().get(PlayerType.OUTSIDE_LOBBY)).getPlayers().remove(player);
	}
	
	@Override
	public void magicTeleported(int type) {
		forceClose();
		removeControler();
	}
	
	@Override
	public boolean processObjectClick1(WorldObject object) {
		switch(object.getId()) {
		case 42220:
			player.getControlerManager().forceStop();
			player.useStairs(-1, new WorldTile(3082, 3475, 0), 0, 1);
			return false;
		case 42029:
		case 42030:
			player.sendMessage("This option is currently disabled, please use the Green Balance Portal.");
			return false;
		//case 42029:
		//case 42030:
		case 42031:
			World.soulWars.passBarrier(PlayerType.OUTSIDE_LOBBY, player, object);
			return false;
		}
		return true;
	}
	
	@Override
	public boolean processNPCClick1(NPC npc) {
		if (npc.getId() == 8526) {
			sendShop();
			return false;
		}
		return true;
	}
	
	@Override
	public boolean processNPCClick2(NPC npc) {
		if (npc.getId() == 8526) {
			sendShop();
			return false;
		}
		return true;
	}
	
	@Override
	public boolean processButtonClick(int interfaceId, int componentId, int slotId, int packetId) {
		if (interfaceId == 276) {
			switch(componentId) {
			case 8:
				switch(packetId) {
				case WorldPacketsDecoder.ACTION_BUTTON1_PACKET:
					player.sendMessage("For every 2 zeals you exchange, you get a random reward!");
					break;
				case WorldPacketsDecoder.ACTION_BUTTON2_PACKET:
					if (checkZeals(2, false)) {
						if (!player.getInventory().hasFreeSlots()) {
							player.sendMessage("You don't have any space to obain your special reward!");
							return false;
						}
						checkZeals(2, true);
						Item reward = COMMON[Utils.random(COMMON.length)];
						if (Utils.random(100) <= 1)
							reward = RARE[Utils.random(RARE.length)];
						if (reward.getId() == 995)
							player.addMoney(reward.getAmount());
						else
							player.getInventory().addItem(reward);
					}
					break;
				}
				break;
			case 24:
			case 25:
			case 26:
			case 27:
				switch(packetId) {
				case WorldPacketsDecoder.ACTION_BUTTON1_PACKET:
					player.sendMessage("For every 2 zeals you exchange, you receive a random amount of charms.");
					break;
				case WorldPacketsDecoder.ACTION_BUTTON2_PACKET:
					Item reward = new Item(CHARMS[componentId - 24], 1 + Utils.random(10, 20));
					if (checkZeals(2, false) && player.getInventory().addItem(reward))
						checkZeals(2, true);
					break;
				}
				break;
			case 32:
			case 33:
			case 34:
			case 35:
			case 36:
			case 37:
			case 38:
			case 39:
				switch(packetId) {
				case WorldPacketsDecoder.ACTION_BUTTON1_PACKET:
					player.sendMessage("For each zeal you exchange, you receive " + calculateSkillExperience(componentId, 1) + " experience in " + Skills.SKILL_NAME[SKILLS[componentId - 32]].toLowerCase() + ".");
					break;
				case WorldPacketsDecoder.ACTION_BUTTON2_PACKET:
					exchangeZealsForXp(componentId, 1);
					break;
				case WorldPacketsDecoder.ACTION_BUTTON3_PACKET:
					exchangeZealsForXp(componentId, 10);
					break;
				case WorldPacketsDecoder.ACTION_BUTTON4_PACKET:
					exchangeZealsForXp(componentId, 100);
					break;
				}
				break;
			case 6: //creeping hand pet
				switch(packetId) {
				case WorldPacketsDecoder.ACTION_BUTTON1_PACKET:
					player.sendMessage("For 5 zeals you exchange, you receive a Creeping hand pet.");
					break;
				case WorldPacketsDecoder.ACTION_BUTTON2_PACKET:
					if (checkZeals(5, false) && player.getInventory().addItem(14652, 1)) {
						checkZeals(5, true);
					}
					break;
				}
				break;
			case 14: //minitrice
				switch(packetId) {
				case WorldPacketsDecoder.ACTION_BUTTON1_PACKET:
					player.sendMessage("For 25 zeals you exchange, you receive a Minitrice pet.");
					break;
				case WorldPacketsDecoder.ACTION_BUTTON2_PACKET:
					if (checkZeals(25, false) && player.getInventory().addItem(14653, 1)) {
						checkZeals(25, true);
					}
					break;
				}
				break;
			case 10: //baby basilisk
				switch(packetId) {
				case WorldPacketsDecoder.ACTION_BUTTON1_PACKET:
					player.sendMessage("For 40 zeals you exchange, you receive a Baby basilisk pet.");
					break;
				case WorldPacketsDecoder.ACTION_BUTTON2_PACKET:
					if (checkZeals(40, false) && player.getInventory().addItem(14654, 1)) {
						checkZeals(40, true);
					}
					break;
				}
				break;
			case 12: //kurask
				switch(packetId) {
				case WorldPacketsDecoder.ACTION_BUTTON1_PACKET:
					player.sendMessage("For 70 zeals you exchange, you receive a Baby kurask pet.");
					break;
				case WorldPacketsDecoder.ACTION_BUTTON2_PACKET:
					if (checkZeals(70, false) && player.getInventory().addItem(14655, 1)) {
						checkZeals(70, true);
					}
					break;
				}
				break;
			case 16: //abyssal minion
				switch(packetId) {
				case WorldPacketsDecoder.ACTION_BUTTON1_PACKET:
					player.sendMessage("For 85 zeals you exchange, you receive a Baby abyssal pet.");
					break;
				case WorldPacketsDecoder.ACTION_BUTTON2_PACKET:
					if (checkZeals(85, false) && player.getInventory().addItem(14651, 1)) {
						checkZeals(85, true);
					}
					break;
				}
				break;
			case 81: //tzrek-jed
				switch(packetId) {
				case WorldPacketsDecoder.ACTION_BUTTON1_PACKET:
					player.sendMessage("For 100 zeals you exchange, you receive a Tzrek-Jed pet.");
					break;
				case WorldPacketsDecoder.ACTION_BUTTON2_PACKET:
					if (checkZeals(100, false) && player.getInventory().addItem(21512, 1)) {
						checkZeals(100, true);
					}
					break;
				}
				break;
			}
		}
		return true;
	}
	
	private void exchangeZealsForXp(int componentId, final int zeals) {
		if (checkZeals(zeals, false)) {
			final int xp = calculateSkillExperience(componentId, zeals == 10 ? 11 : zeals == 100 ? 110 : zeals);
			checkZeals(zeals, true);
			player.getPackets().sendConfigByFile(5827, player.getZeals());
			player.getTemporaryAttributtes().put("soul_wars_shop_xp", Boolean.TRUE);
			player.getSkills().addXp(SKILLS[componentId - 32], xp * 0.5);
		}
	}
	
	private int calculateSkillExperience(int componentId, int zeals) {
		final int skill = SKILLS[componentId - 32];
		final int playerLevel = player.getSkills().getLevelForXp(skill);
		double base = 0;
		switch(skill) {
		case Skills.HITPOINTS:
		case Skills.ATTACK:
		case Skills.STRENGTH:
		case Skills.DEFENCE:
			base = Math.floor(Math.pow(playerLevel, 2) / 600) * 525;
			break;
		case Skills.RANGE:
		case Skills.MAGIC:
			base = Math.floor(Math.pow(playerLevel, 2) / 600) * 480;
			break;
		case Skills.PRAYER:
			base = Math.floor(Math.pow(playerLevel, 2) / 600) * 270;
			break;
		default:
			if (playerLevel <= 30)
				base = Math.floor(Math.pow(1.1048, playerLevel) * 6.788);
			else
				base = (Math.floor(Math.pow(playerLevel, 2) / 349) + 1) * 45;
		}
		return (int) base * zeals;
	}
	
	private boolean checkZeals(int zeals, boolean remove) {
		if (player.getZeals() < zeals) {
			player.sendMessage("You don't have " + (player.getZeals() == 0 ? "any" : "enough") + " zeals to spend.");
			return false;
		} 
		if (remove) {
			player.setZeals(player.getZeals() - zeals);
			player.getPackets().sendConfigByFile(5827, player.getZeals());
		}
		return true;
	}
	
	@Override
	public boolean login() {
		start();
		return false;
	}

	@Override
	public boolean logout() {
		return false;
	}
	
	public void sendShop() {
		player.getInterfaceManager().sendInterface(276);
		player.getPackets().sendConfigByFile(5827, player.getZeals());
	}
}