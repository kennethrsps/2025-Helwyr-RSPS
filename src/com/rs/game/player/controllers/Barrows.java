package com.rs.game.player.controllers;

import java.util.ArrayList;
import java.util.List;

import com.rs.game.Entity;
import com.rs.game.ForceTalk;
import com.rs.game.Hit;
import com.rs.game.World;
import com.rs.game.WorldObject;
import com.rs.game.WorldTile;
import com.rs.game.Hit.HitLook;
import com.rs.game.item.Item;
import com.rs.game.npc.others.BarrowsBrother;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.game.player.content.items.Defenders;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Colors;
import com.rs.utils.Utils;

public final class Barrows extends Controller {

	// they have to be handled by correct order
	// AHRIM
	// DHAROK
	// GUTHAN
	// KARIL
	// TORAG
	// VERAC
	private static enum Hills {

		AHRIM_HILL(new WorldTile(3564, 3287, 0), new WorldTile(3557, 9703, 3)),
		DHAROK_HILL(new WorldTile(3573, 3296, 0), new WorldTile(3556, 9718, 3)),
		GUTHAN_HILL(new WorldTile(3574, 3279, 0), new WorldTile(3534, 9704, 3)),
		KARIL_HILL(new WorldTile(3563, 3276, 0), new WorldTile(3546, 9684, 3)),
		TORAG_HILL(new WorldTile(3553, 3281, 0), new WorldTile(3568, 9683, 3)),
		VERAC_HILL(new WorldTile(3556, 3296, 0), new WorldTile(3578, 9706, 3));

		private WorldTile outBound;
		private WorldTile inside;

		private Hills(WorldTile outBound, WorldTile in) {
			this.outBound = outBound;
			inside = in;
		}
	}

	public static boolean digIntoGrave(final Player player) {
		for (Hills hill : Hills.values()) {
			if (player.getPlane() == hill.outBound.getPlane() && player.getX() >= hill.outBound.getX()
					&& player.getY() >= hill.outBound.getY() && player.getX() <= hill.outBound.getX() + 3
					&& player.getY() <= hill.outBound.getY() + 3) {
				player.useStairs(-1, hill.inside, 1, 2, "You've broken into a crypt.");

				WorldTasksManager.schedule(new WorldTask() {
					@Override
					public void run() {
						player.getControlerManager().startControler("Barrows");
					}
				});
				return true;
			}
		}
		return false;
	}

	private BarrowsBrother target;

	private static final Item[] COMMON_REWARDS = { new Item(558, 1795), new Item(562, 773), new Item(560, 391),
			new Item(565, 164), new Item(4740, 188) };
	private static final Item[] RING_OF_WEALTH_REWARDS = { new Item(165, 1), new Item(159, 1), new Item(141, 1),
			new Item(129, 1), new Item(385, 4) };
	// private static final Item[] CORUPTED_SIGIL = { new Item(36156, 1) };
	private static final Item[] BARROW_TOTEM = { new Item(30004, 1) };
	private static final Item[] RARE_REWARDS = { new Item(1149, 1), new Item(987, 1), new Item(985, 1) };

	Item[] BARROW_REWARDS = { new Item(4708, 1), new Item(4710, 1), new Item(4712, 1), new Item(4714, 1),
			new Item(4716, 1), new Item(4718, 1), new Item(4720, 1), new Item(4722, 1), new Item(4724, 1),
			new Item(4726, 1), new Item(4728, 1), new Item(4730, 1), new Item(4732, 1), new Item(4734, 1),
			new Item(4736, 1), new Item(4738, 1), new Item(4745, 1), new Item(4747, 1), new Item(4749, 1),
			new Item(4751, 1), new Item(4753, 1), new Item(4755, 1), new Item(4757, 1), new Item(25652, 1),
			new Item(25672, 1) };

	private int headComponentId, timer;

	@Override
	public boolean canAttack(Entity target) {
		if (target instanceof BarrowsBrother && target != this.target) {
			player.sendMessage("This isn't your target.");
			return false;
		}
		return true;
	}

	public void drop(Item item) {
		Item dropItem = new Item(item.getId(),
				Utils.random((int) (item.getDefinitions().isStackable() ? item.getAmount() * player.getDropRate()
						: item.getAmount())) + 1);
		if (dropItem.getId() == 995) {
			player.addMoney(dropItem.getAmount());
			return;
		}
		if (!player.getInventory().addItem(dropItem))
			World.updateGroundItem(dropItem, player, player, 60, 0);
	}

	private void exit(WorldTile outside) {
		player.setNextWorldTile(outside);
		leave(false);
	}

	@Override
	public void forceClose() {
		leave(true);
	}

	public int getAndIncreaseHeadIndex() {
		Integer head = (Integer) player.getTemporaryAttributtes().remove("BarrowsHead");
		if (head == null || head == player.getKilledBarrowBrothers().length - 1)
			head = 0;
		player.getTemporaryAttributtes().put("BarrowsHead", head + 1);
		return player.getKilledBarrowBrothers()[head] ? head : -1;
	}

	public int getRandomBrother() {
		List<Integer> bros = new ArrayList<Integer>();
		for (int i = 0; i < Hills.values().length; i++) {
			if (player.getKilledBarrowBrothers()[i] || player.getHiddenBrother() == i)
				continue;
			bros.add(i);
		}
		if (bros.isEmpty())
			return -1;
		return bros.get(Utils.random(bros.size()));
	};

	public int getSarcophagusId(int objectId) {
		switch (objectId) {
		case 66017:
			return 0;
		case 63177:
			return 1;
		case 66020:
			return 2;
		case 66018:
			return 3;
		case 66019:
			return 4;
		case 66016:
			return 5;
		default:
			return -1;
		}
	}

	private void leave(boolean logout) {
		if (target != null)
			target.finish(); // target also calls removing hint icon at remove
		if (!logout) {
			player.getPackets().sendMiniMapStatus(0); // unblacks minimap
			if (player.getHiddenBrother() == -1)
				player.getPackets().sendStopCameraShake();
			else
				player.getInterfaceManager().closeOverlay(player.getInterfaceManager().hasRezizableScreen());
			removeControler();
		}
	}

	public void loadData() {
		resetHeadTimer();
		for (int i = 0; i < player.getKilledBarrowBrothers().length; i++)
			sendBrotherSlain(i, player.getKilledBarrowBrothers()[i]);
		sendCreaturesSlainCount(player.getBarrowsKillCount());
		player.getPackets().sendMiniMapStatus(2); // blacks minimap
	}

	@Override
	public boolean login() {
		if (player.getHiddenBrother() == -1)
			player.getPackets().sendCameraShake(3, 25, 50, 25, 50);
		loadData();
		sendInterfaces();
		return false;
	}

	@Override
	public boolean logout() {
		leave(true);
		return false;
	}

	@Override
	public void magicTeleported(int type) {
		leave(false);
	}

	@Override
	public void process() {
		if (timer > 0) {
			timer--;
			return;
		}
		if (headComponentId == 0) {
			if (player.getHiddenBrother() == -1) {
				player.applyHit(new Hit(player, Utils.random(50) + 1, HitLook.REGULAR_DAMAGE));
				resetHeadTimer();
				return;
			}
			int headIndex = getAndIncreaseHeadIndex();
			if (headIndex == -1) {
				resetHeadTimer();
				return;
			}
			headComponentId = 9 + Utils.random(2);
			player.getPackets().sendItemOnIComponent(24, headComponentId, 4761 + headIndex, 0);
			player.getPackets().sendIComponentAnimation(9810, 24, headComponentId);
			int activeLevel = player.getPrayer().getPrayerpoints();
			if (activeLevel > 0) {
				int level = player.getSkills().getLevelForXp(Skills.PRAYER) * 10;
				player.getPrayer().drainPrayer(level / 6);
			}
			timer = 3;
		} else {
			player.getPackets().sendItemOnIComponent(24, headComponentId, -1, 0);
			headComponentId = 0;
			resetHeadTimer();
		}
	}

	@Override
	public boolean processObjectClick1(WorldObject object) {
		if (object.getId() >= 6702 && object.getId() <= 6707) {
			WorldTile out = Hills.values()[object.getId() - 6702].outBound;
			// cant make a perfect middle since 3/ 2 wont make a real integer
			// number or wahtever u call it..
			exit(new WorldTile(out.getX() + 1, out.getY() + 1, out.getPlane()));
			return false;
		} else if (object.getId() == 10284) {
			if (player.getHiddenBrother() == -1) {// reached chest
				player.sendMessage("You found nothing.");
				return false;
			}
			if (!player.getKilledBarrowBrothers()[player.getHiddenBrother()])
				sendTarget(2025 + player.getHiddenBrother(), player);
			if (target != null) {
				player.sendMessage("You found nothing.");
				return false;
			}
			sendReward();
			player.getPackets().sendCameraShake(3, 12, 25, 12, 25);
			player.getInterfaceManager().closeOverlay(player.getInterfaceManager().hasRezizableScreen());
			player.getPackets().sendSpawnedObject(new WorldObject(6775, 10, 0, 3551, 9695, 0));
			player.resetBarrows();
			player.incrementBarrowsRunsDone();
			player.sendMessage("You open the chest and find some treasure; barrows runs done: " + Colors.red
					+ Utils.getFormattedNumber(player.getBarrowsRunsDone()) + "</col>.");
			return false;
		} else if (object.getId() >= 6716 && object.getId() <= 6749) {
			WorldTile walkTo;
			if (object.getRotation() == 0)
				walkTo = new WorldTile(object.getX() + 5, object.getY(), 0);
			else if (object.getRotation() == 1)
				walkTo = new WorldTile(object.getX(), object.getY() - 5, 0);
			else if (object.getRotation() == 2)
				walkTo = new WorldTile(object.getX() - 5, object.getY(), 0);
			else
				walkTo = new WorldTile(object.getX(), object.getY() + 5, 0);
			if (!World.isNotCliped(walkTo.getPlane(), walkTo.getX(), walkTo.getY(), 1))
				return false;
			player.addWalkSteps(walkTo.getX(), walkTo.getY(), -1, false);
			player.lock(6);
			if (player.getHiddenBrother() != -1) {
				int brother = getRandomBrother();
				if (brother != -1)
					sendTarget(2025 + brother, walkTo);
			}
			return false;
		} else {
			int sarcoId = getSarcophagusId(object.getId());
			if (sarcoId != -1) {
				if (sarcoId == player.getHiddenBrother())
					player.getDialogueManager().startDialogue("BarrowsD");
				else if (target != null || player.getKilledBarrowBrothers()[sarcoId])
					player.sendMessage("You found nothing.");
				else
					sendTarget(2025 + sarcoId, player);
				return false;
			}
		}
		return true;
	}

	public void resetHeadTimer() {
		timer = 20 + Utils.random(6);
	}

	public void sendBrotherSlain(int index, boolean slain) {
		player.getPackets().sendConfigByFile(457 + index, slain ? 1 : 0);
	}

	public void sendCreaturesSlainCount(int count) {
		player.getPackets().sendConfigByFile(464, count);
	}

	@Override
	public boolean sendDeath() {
		leave(false);
		return true;
	}

	@Override
	public void sendInterfaces() {
		if (player.getHiddenBrother() != -1)
			player.getInterfaceManager().sendOverlay(24, player.getInterfaceManager().hasRezizableScreen());
	}

	// 4% prob barrows reward per bro
	// 1% per 10kills.
	public void sendReward() {
		double percentage = 0;
		int defModifier = player.getBarrowsKillCount() > 6 ? 6 : player.getBarrowsKillCount();
		int defender = 5 * (defModifier > 1 ? defModifier - 1 : 0);
		for (boolean died : player.getKilledBarrowBrothers()) {
			if (died)
				percentage += 4;
		}
		percentage += (player.getBarrowsKillCount() / 10);
		if (percentage >= Math.random() * 100)
			drop(BARROW_REWARDS[Utils.random(BARROW_REWARDS.length)]);

		if (player.getEquipment().getRingId() == 2572) {
			if (Utils.random(6) == 0)
				drop(RARE_REWARDS[Utils.random(RARE_REWARDS.length)]);
		} else {
			if (Utils.random(9) == 0)
				drop(RARE_REWARDS[Utils.random(RARE_REWARDS.length)]);
		}
		if (Defenders.getCurrentTier(player, 0) && Utils.random(4 - defender) == 0) {
			drop(new Item(36156, 1));
			player.sendMessage(Colors.cyan + "You have received a corruption sigil!", false);
		}

		if (Utils.random(3) != 0) {
			drop(COMMON_REWARDS[Utils.random(COMMON_REWARDS.length)]);
		}
		if (player.getEquipment().getRingId() == 2572)
			drop(RING_OF_WEALTH_REWARDS[Utils.random(RING_OF_WEALTH_REWARDS.length)]);
		drop(new Item(995, Utils.random(50000, 1000000)));
		drop(BARROW_TOTEM[Utils.random(BARROW_TOTEM.length)]);
	}

	public void sendTarget(int id, WorldTile tile) {
		if (target != null)
			target.disapear();
		target = new BarrowsBrother(id, tile, this);
		target.setTarget(player);
		target.setNextForceTalk(new ForceTalk("You dare disturb my rest!"));
		player.getHintIconsManager().addHintIcon(target, 1, -1, false);
	}

	public void setBrotherSlained(int index) {
		player.getKilledBarrowBrothers()[index] = true;
		sendBrotherSlain(index, true);
	}

	@Override
	public void start() {
		if (player.getHiddenBrother() == -1)
			player.setHiddenBrother(Utils.random(Hills.values().length));
		loadData();
		sendInterfaces();
	}

	public void targetDied() {
		player.getHintIconsManager().removeUnsavedHintIcon();
		setBrotherSlained(target.getId() - 2025);
		target = null;
	}

	public void targetFinishedWithoutDie() {
		player.getHintIconsManager().removeUnsavedHintIcon();
		target = null;
	}
}