package com.Tusken_raid;


import java.util.List;
import com.rs.Settings;
import com.rs.game.Animation;
import com.rs.game.MapBuilder;
import com.rs.game.World;
import com.rs.game.WorldObject;
import com.rs.game.WorldTile;
import com.rs.game.item.Item;
import com.rs.game.npc.NPC;
import com.rs.game.player.content.pet.Pets;
import com.rs.game.player.controllers.Controller;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Colors;
import com.rs.utils.Utils;

public class tuskenraid extends Controller {
	private static final Object THHAAR_MEJ_JAL = 13633;

	private int[] regionBase;
	public WorldTile base;

	public boolean spawned;
	protected NPC bossNPC;

	private int WaveId;

	public int Chest = 100221; //18804;
	public int Barrier = 97765;

	public int Durzag = 21335;
	public int Tuz = 21336;
	public int Krar = 21337;
	public int Airut = 18621;


	private static final Item[] COMMUM_REWARDS = { new Item(1748, 150), new Item(1128, 25), new Item(2364, 100),
			new Item(1514, 200), new Item(1516, 300), new Item(1128, 10), new Item(1514, 75), new Item(15271, 60),
			new Item(1748, 80), new Item(9245, 60), new Item(1392, 45), new Item(452, 34), new Item(9144, 300),
			new Item(9193, 300), new Item(1616, 50) };

	private static final Item[] DECENT_REWARDS = { new Item(12163, 50), new Item(12158, 100), new Item(12160, 100),
			new Item(12159, 100), new Item(12183, 1000) };

	private static final Item[] RARE_REWARDS = { new Item(30213, 1), new Item(29863, 2), new Item(29863, 3),
			new Item(29863, 4) };

	private static final Item[] ATCHO_REWARDS = { new Item(35159, 1), new Item(35161, 1), new Item(35163, 1), new Item(35165, 1)
			, new Item(35167, 1), new Item(35174, 1), new Item(35176, 1), new Item(35178, 1), new Item(35180, 1)
			, new Item(35182, 1), new Item(35189, 1), new Item(35191, 1), new Item(35193, 1), new Item(35195, 1)
			, new Item(35197, 1) };
	private static final Item[] OUTFIT_REWARDS = { new Item(34860, 1),new Item(34863, 1),new Item(34866, 1),new Item(34869, 1),new Item(34872, 1),new Item(34875, 1) };
	private static final Item[] PET_REWARDS = { new Item(35217, 1), new Item(35219, 1), new Item(39674, 1) };   

	@Override
	public void start() {
		// = RegionBuilder.findEmptyChunkBound(8, 8);
		// RegionBuilder.copyAllPlanesMap(418, , regionChucks[0], regionChucks[1], 8);
		regionBase = MapBuilder.findEmptyChunkBound(8, 8);
		MapBuilder.copyAllPlanesMap(537, 103, regionBase[0], regionBase[1], 8, 8);   // 536, 101
		player.setNextWorldTile(getWorldTile(19, 2));  //14, 15
		player.getInventory().deleteItem(29941, 1);
		player.sm("As you enter the Tusken Beast sense your hostility");
		player.sm("Enter the barrier to begin.");
		hasStarted = false;
		WaveId = 0;
	}

	@Override
	public boolean processObjectClick1(WorldObject object) {
		if (object.getId() == Chest) {
			lootChest();
		}
		if (object.getId() == Barrier && hasStarted == false) {
			passBarrier();
		}
		return false;
	}

	private boolean noSpaceOnInv;
	private boolean hasStarted;

	public void drop(Item item) {
		Item dropItem = new Item(item.getId(),		
				Utils.random(
						item.getDefinitions().isStackable() ? item.getAmount() * Settings.DROP_RATE : item.getAmount())
						+ 1);
		if (!noSpaceOnInv && player.getInventory().addItem(dropItem))
			return;
		noSpaceOnInv = true;
		player.getBank().addItem(dropItem, false);
		player.getPackets().sendGameMessage("Your loot was placed into your bank.");
	}

	public void lootChest() {
		player.setTRPoints(player.getTRPoints() + 1);
		player.sm(Colors.red+"[tusken raid] </col>"+Colors.green+ "Kill count:</col> "+ player.getTRPoints());
		int outfit = 100 + (int) player.getDropRate();
		int pet = 150 + (int) player.getDropRate();
		int armor = 100 + (int) player.getDropRate();
		int rare = 35 + (int) player.getDropRate();

		if (Utils.random(outfit) == 0) {
			World.sendWorldMessage(
					"<img=7><col=9A2EFE>News: " + player.getDisplayName()
							+ " has just found a rare Outfit piece from the Beastmaster Durzag Chest!",
					false);
			drop(OUTFIT_REWARDS[Utils.random(OUTFIT_REWARDS.length)]);
		}
		if (Utils.random(pet) == 0) {
			World.sendWorldMessage("<img=2><col=9A2EFE>News: " + player.getDisplayName()
					+ " has just found a rare pet from the Beastmaster Durzag Chest!", false);
			drop(PET_REWARDS[Utils.random(PET_REWARDS.length)]);
		}
		if (Utils.random(armor) == 0) {
			World.sendWorldMessage("<img=2><col=9A2EFE>News: " + player.getDisplayName()
					+ " has just found a  Achto Armor from the Beastmaster Durzag Chest!", false);
			drop(ATCHO_REWARDS[Utils.random(ATCHO_REWARDS.length)]);
		}
		if (Utils.random(rare) == 0) {
			World.sendWorldMessage("<img=2><col=9A2EFE>News: " + player.getDisplayName()
					+ " has just found a  Rare Reward from the Beastmaster Durzag Chest!", false);
			drop(RARE_REWARDS[Utils.random(RARE_REWARDS.length)]);

		}
		if (Utils.random(10) == 0)
			drop(DECENT_REWARDS[Utils.random(DECENT_REWARDS.length)]);
		if (Utils.random(5) == 0)
			drop(COMMUM_REWARDS[Utils.random(COMMUM_REWARDS.length)]);
		drop(new Item(995, 300000));		
		player.setTuskenPoints(player.getTuskenPoints() + 6);
		if (player.getPerkManager().miniGamer) {
			player.setTuskenPoints(player.getTuskenPoints() + 6);	
		}
		player.sm("You managed to slay all the Tusken beast and escape with some loot.");
		player.setNextWorldTile(new WorldTile(Settings.RESPAWN_PLAYER_LOCATION));
		player.setForceMultiArea(false);
		removeControler();
	}

	@Override
	public void process() {
		if (spawned) {
			List<Integer> npcsInArea = World.getRegion(player.getRegionId()).getNPCsIndexes();
			if (npcsInArea == null || npcsInArea.isEmpty()) {
				spawned = false;
				WaveId += 1;
				nextWave(WaveId);
				System.out.println("next");
			}
		}

	}

	private void passBarrier() {
		if (player.getFamiliar() != null || player.getPet() != null || Pets.hasPet(player)) {
			player.getDialogueManager().startDialogue("SimpleNPCMessage", THHAAR_MEJ_JAL,
					"Pets and Familiars are not allowed in Minigames or your account will Reset to 0!"
							+ " Please Dismiss your pet/familiar and remove your pet/pouches in your inventory");
			return;
		}
		player.setForceMultiArea(true);
		player.lock(2);
		tuskenBros1();
		tuskenBros2();
		tuskenBros3();
		//tuskenBros4();
		//tuskenBros5();
		player.setNextWorldTile(getWorldTile(19, 33));  //14, 21  17, 50
		hasStarted = true;

	}

	public void tuskenBros1() {
		bossNPC = new NPC(Durzag, getWorldTile(19, 38), -1, true, true);   //26, 52
		bossNPC.setForceMultiArea(true);
		bossNPC.setForceAgressive(true);
		bossNPC.setForceTargetDistance(64);
		spawned = true;
	}

	public void tuskenBros2() {
		bossNPC = new NPC(Tuz, getWorldTile(15, 36), -1, true, true);
		bossNPC.setForceMultiArea(true);
		bossNPC.setForceAgressive(true);
		bossNPC.setForceTargetDistance(64);
		spawned = true;
	}

	public void tuskenBros3() {
		bossNPC = new NPC(Krar, getWorldTile(23, 36), -1, true, true);  //26, 48
		bossNPC.setForceMultiArea(true);
		bossNPC.setForceAgressive(true);
		bossNPC.setForceTargetDistance(64);
		spawned = true;
	}
    public void tuskenBros4() {
		bossNPC = new NPC(Airut, getWorldTile(26, 59), -1, true, true);
		bossNPC.setForceMultiArea(true);
		bossNPC.setForceAgressive(true);
		spawned = true;
	}
	public void tuskenBros5() {
		bossNPC = new NPC(Airut, getWorldTile(26, 45), -1, true, true);
		bossNPC.setForceMultiArea(true);
		bossNPC.setForceAgressive(true);
		spawned = true;
	}


	private void nextWave(int waveid) {
		if (waveid == 1) {
			player.getPackets().sendGameMessage("Congratulations! You've defeated the Tusken beast.");
			player.getPackets().sendGameMessage("You now have access to the chest.");
			World.spawnObject(new WorldObject(Chest, 10, 3, getWorldTile(19, 40)), true);
			return;
		}
	}

	public WorldTile getWorldTile(int mapX, int mapY) {
		return new WorldTile(regionBase[0] * 8 + mapX, regionBase[1] * 8 + mapY, 0);
	}

	@Override
	public boolean logout() {
		player.setForceMultiArea(false);
		removeControler();
		hasStarted = false;
		return true;
	}

	@Override
	public boolean sendDeath() {
		player.lock(7);
		player.stopAll();
		hasStarted = false;
		WorldTasksManager.schedule(new WorldTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0) {
					player.setNextAnimation(new Animation(836));
				} else if (loop == 1) {
					player.sendMessage("You have been defeated!");
				} else if (loop == 3) {
					player.reset();
					exitCave(1);
					player.setNextAnimation(new Animation(-1));
				} else if (loop == 4) {
					player.getPackets().sendMusicEffect(90);
					player.unlock();
					stop();
				}
				loop++;
			}
		}, 0, 1);
		return false;
	}

	public void exitCave(int type) {
		if (type == 1) {
			hasStarted = false;
			player.setForceMultiArea(false);
			player.setNextWorldTile(new WorldTile(player.getHomeTile()));
		}
		removeControler();
	}

	@Override
	public void magicTeleported(int type) {
		player.setForceMultiArea(false);
		hasStarted = false;
		removeControler();
	}
}
