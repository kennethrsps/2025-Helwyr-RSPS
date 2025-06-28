package com.rs.game.npc.ballak;

import com.rs.game.Hit;
import com.rs.game.WorldTile;
import com.rs.game.Hit.HitLook;
import com.rs.game.npc.NPC;
import com.rs.game.player.Player;

@SuppressWarnings("serial")
public class BallakDefs extends NPC {

	public BallakDefs(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea, boolean spawned) {
		super(10140, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		getCombatDefinitions().setHitpoints(25000);
		setHitpoints(25000);
		//setCapDamage(1000);
		setForceTargetDistance(1);
		setForceMultiArea(true);
		setForceMultiAttacked(true);
		setForceAgressive(true);
		setCombatLevel(250);
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
		if (hit.getSource() instanceof Player) {
			Player p = (Player) hit.getSource();
			p.ballakdmg += hit.getDamage();
			int recoil = (int) (hit.getDamage() * 0.2);
			if (recoil > 0) {
				p.applyHit(new Hit(this, recoil, HitLook.REFLECTED_DAMAGE));
			}
			if (p.ballakdmg > 5000) {
				if (rewardList.contains(p))
					return;
				rewardList.add(p);
				p.sm("You have dealt enough damage to receive a reward from " + getName());
			}
		}
	}

}
