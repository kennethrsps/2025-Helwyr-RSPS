package com.rs.game.npc.others;

import com.rs.game.Graphics;
import com.rs.game.Hit;
import com.rs.game.WorldTile;
import com.rs.game.Hit.HitLook;
import com.rs.game.npc.NPC;
import com.rs.game.player.Player;

@SuppressWarnings("serial")
public class PartyDemon extends NPC {

	public PartyDemon(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea, boolean spawned) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		this.getCombatDefinitions().setHitpoints(15000);
		this.setHitpoints(15000);
		//this.setCapDamage(700);
		this.setForceTargetDistance(64);
		this.setForceMultiArea(true);
		this.setForceMultiAttacked(true);
		this.setForceAgressive(true);
		this.setCombatLevel(1000);
		this.setLureDelay(3000);
	}

	@Override
	public double getMagePrayerMultiplier() {
		return 0.6;
	}

	@Override
	public double getRangePrayerMultiplier() {
		return 0.6;
	}

	@Override
	public double getMeleePrayerMultiplier() {
		return 0.6;
	}

	@Override
	public void processNPC() {
		super.processNPC();
		if (isDead())
			return;
		checkReset();

	}

	public void checkReset() {
		int maxhp = getMaxHitpoints();
		if (maxhp > getHitpoints() && !isUnderCombat() && getPossibleTargets().isEmpty())
			setHitpoints(maxhp);
	}

	@Override
	public void handleIngoingHit(Hit hit) {
		super.handleIngoingHit(hit);
		if (hit.getLook() != HitLook.MELEE_DAMAGE && hit.getLook() != HitLook.RANGE_DAMAGE
				&& hit.getLook() != HitLook.MAGIC_DAMAGE)
			return;
		if (hit.getSource() != null) {
			int recoil = hit.getDamage() / 3;
			if (recoil > 0) {
				hit.getSource().applyHit(new Hit(this, recoil, HitLook.REFLECTED_DAMAGE));
				setNextGraphics(new Graphics(2180));
			}
		}
		if (hit.getSource() instanceof Player) { // Boggie
			Player player = (Player) hit.getSource();
			int id = player.getEquipment().getWeaponId();
			if ((id == 24474) && (hit.getLook() == HitLook.MELEE_DAMAGE || hit.getLook() == HitLook.RANGE_DAMAGE)
					&& hit.getDamage() > 0) {
				hit.setDamage((int) (hit.getDamage() * 2));
				player.getPackets().sendGameMessage("Your Boogie Bow dealt additional damage to Party demon.");
			}
		}
	}

}
