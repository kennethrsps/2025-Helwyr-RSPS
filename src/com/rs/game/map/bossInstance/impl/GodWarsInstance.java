package com.rs.game.map.bossInstance.impl;

import com.rs.game.WorldTile;
import com.rs.game.map.bossInstance.BossInstance;
import com.rs.game.map.bossInstance.BossInstanceHandler;
import com.rs.game.map.bossInstance.InstanceSettings;
import com.rs.game.npc.godwars.GodWarMinion;
import com.rs.game.npc.godwars.armadyl.KreeArra;
import com.rs.game.npc.godwars.bandos.GeneralGraardor;
import com.rs.game.npc.godwars.saradomin.CommanderZilyana;
import com.rs.game.npc.godwars.zammorak.KrilTsutsaroth;
import com.rs.game.npc.nexangel.NexAngel;
import com.rs.game.player.Player;
import com.rs.game.player.controllers.GodWars;

public class GodWarsInstance extends BossInstance {

	public GodWarsInstance(Player owner, InstanceSettings settings) {
		super(owner, settings);
	}

	@Override
	public int[] getMapPos() {
		switch (getBoss()) {
		case Armadyl:
			return new int[] { 351, 659 };
		case Bandos:
			return new int[] { 355, 668 };
		case Saradomin:
			return new int[] { 362, 654 };
		case Zamorak:
			return new int[] { 362, 664 };
		case NexAngel:
			return new int[] { 362, 664 };
		default:
			return null;
		}
	}

	@Override
	public int[] getMapSize() {
		return new int[] { 1, 1 };
	}

	@Override
	public void enterInstance(Player player, boolean login) {
		synchronized (BossInstanceHandler.LOCK) {
			if (!login) {
				player.useStairs(-1, getTile(isPublic() ? getSettings().getBoss().getInsideTile()
						: getSettings().getBoss().getOutsideTile()), 0, 2);
			}
			getPlayers().add(player);
			int spawnSpeed = getSettings().getSpawnSpeed();
			if (!isPublic()) {
				player.getPackets().sendGameMessage("Welcome to this session against <col=00FFFF><shad=000000>"
						+ getInstanceName() + "</shad></col> "
						+ (spawnSpeed == BossInstance.STANDARD ? "(Standard)"
								: spawnSpeed == BossInstance.FAST ? "(Fast)" : "(Fastest)")
						+ ". This arena will expire in " + (getSettings().getTimeRemaining() / 60000) + " minutes.");
				player.setForceMultiArea(true);
			}
			if (player.getControlerManager().getControler() == null
					|| !(player.getControlerManager().getControler() instanceof GodWars))
				player.getControlerManager().startControler("GodWars");
			GodWars controler = (GodWars) player.getControlerManager().getControler();
			controler.setBossInstance(this);
			player.setLastBossInstanceKey(getOwner() == null ? null : getOwner().getUsername());
		}
	}

	@Override
	public void leaveInstance(Player player, int type) {
		synchronized (BossInstanceHandler.LOCK) {
			if (type == EXITED) {
				player.useStairs(-1, getTile(getSettings().getBoss().getOutsideTile()), 0, 2);
				if (isPublic()) {
					if (player.getControlerManager().getControler() == null
							|| !(player.getControlerManager().getControler() instanceof GodWars))
						player.getControlerManager().startControler("GodWars");
					GodWars controler = (GodWars) player.getControlerManager().getControler();
					controler.setBossInstance(null);
				}
			} else if (type == LOGGED_OUT) {
				player.setLocation(getSettings().getBoss().getOutsideTile());
			}
			player.setForceMultiArea(false);
			player.getMusicsManager().reset();
			if (isPublic() || type == TELEPORTED || type == LOGGED_OUT) {
				if (player.getControlerManager().getControler() != null
						&& (player.getControlerManager().getControler() instanceof GodWars)) {
					GodWars controler = (GodWars) player.getControlerManager().getControler();
					controler.setBossInstance(null);
				}
				getPlayers().remove(player);
				if (getPlayers().isEmpty()) // public version
					finish();
			}
		}
	}

	@Override
	public void loadMapInstance() {
		switch (getBoss()) {
		case NexAngel:
			NexAngel nexAngel = new NexAngel(24004, getTile(new WorldTile(2848, 1824, 1)), -1, true, false);
			nexAngel.setBossInstance(this);
			nexAngel.nexangelMinions[0] = new GodWarMinion(24010, getTile(new WorldTile(2839, 1815, 1)), -1, true, false);
			nexAngel.nexangelMinions[1] = new GodWarMinion(24011, getTile(new WorldTile(2839, 1833, 1)), -1, true, false);
			nexAngel.nexangelMinions[2] = new GodWarMinion(24012, getTile(new WorldTile(2857, 1815, 1)), -1, true, false);
			nexAngel.nexangelMinions[3] = new GodWarMinion(24013, getTile(new WorldTile(2857, 1833, 1)), -1, true, false);
			for (int i = 0; i < nexAngel.nexangelMinions.length; i++) {
				GodWarMinion npc = nexAngel.nexangelMinions[i];
				if (npc == null)
					continue;
				npc.setBossInstance(this);
			}
			break;
		case Armadyl:
			KreeArra kreeArra = new KreeArra(6222, getTile(new WorldTile(2832, 5302, 0)), -1, true, false);
			kreeArra.setBossInstance(this);
			kreeArra.armadylMinions[0] = new GodWarMinion(6223, getTile(new WorldTile(2838, 5303, 0)), -1, true, false);
			kreeArra.armadylMinions[1] = new GodWarMinion(6225, getTile(new WorldTile(2828, 5299, 0)), -1, true, false);
			kreeArra.armadylMinions[2] = new GodWarMinion(6227, getTile(new WorldTile(2833, 5297, 0)), -1, true, false);
			for (int i = 0; i < kreeArra.armadylMinions.length; i++) {
				GodWarMinion npc = kreeArra.armadylMinions[i];
				if (npc == null)
					continue;
				npc.setBossInstance(this);
			}
			break;
		case Bandos:
			GeneralGraardor generalGraardor = new GeneralGraardor(6260, getTile(new WorldTile(2870, 5369, 0)), -1, true,
					false);
			generalGraardor.setBossInstance(this);
			generalGraardor.graardorMinions[0] = new GodWarMinion(6261, getTile(new WorldTile(2864, 5360, 0)), -1, true,
					false);
			generalGraardor.graardorMinions[1] = new GodWarMinion(6263, getTile(new WorldTile(2872, 5353, 0)), -1, true,
					false);
			generalGraardor.graardorMinions[2] = new GodWarMinion(6265, getTile(new WorldTile(2867, 5361, 0)), -1, true,
					false);
			for (int i = 0; i < generalGraardor.graardorMinions.length; i++) {
				GodWarMinion npc = generalGraardor.graardorMinions[i];
				if (npc == null)
					continue;
				npc.setBossInstance(this);
			}
			break;
		case Saradomin:
			CommanderZilyana commanderZilyana = new CommanderZilyana(6247, getTile(new WorldTile(2924, 5250, 0)), -1,
					true, false);
			commanderZilyana.setBossInstance(this);
			commanderZilyana.commanderMinions[0] = new GodWarMinion(6250, getTile(new WorldTile(2916, 5253, 0)), -1,
					true, false);
			commanderZilyana.commanderMinions[1] = new GodWarMinion(6252, getTile(new WorldTile(2926, 5250, 0)), -1,
					true, false);
			commanderZilyana.commanderMinions[2] = new GodWarMinion(6248, getTile(new WorldTile(2928, 5252, 0)), -1,
					true, false);
			for (int i = 0; i < commanderZilyana.commanderMinions.length; i++) {
				GodWarMinion npc = commanderZilyana.commanderMinions[i];
				if (npc == null)
					continue;
				npc.setBossInstance(this);
			}
			break;
		case Zamorak:
			KrilTsutsaroth krilTsutsaroth = new KrilTsutsaroth(6203, getTile(new WorldTile(2926, 5324, 0)), -1, true,
					false);
			krilTsutsaroth.setBossInstance(this);
			krilTsutsaroth.zamorakMinions[0] = new GodWarMinion(6204, getTile(new WorldTile(2919, 5327, 0)), -1, true,
					false);
			krilTsutsaroth.zamorakMinions[1] = new GodWarMinion(6206, getTile(new WorldTile(2930, 5326, 0)), -1, true,
					false);
			krilTsutsaroth.zamorakMinions[2] = new GodWarMinion(6208, getTile(new WorldTile(2927, 5320, 0)), -1, true,
					false);
			for (int i = 0; i < krilTsutsaroth.zamorakMinions.length; i++) {
				GodWarMinion npc = krilTsutsaroth.zamorakMinions[i];
				if (npc == null)
					continue;
				npc.setBossInstance(this);
			}

			break;
		default:
			break;
		}
	}

}
