package com.rs.game.npc.familiar;

import com.rs.game.Animation;
import com.rs.game.Graphics;
import com.rs.game.WorldTile;
import com.rs.game.npc.familiar.Familiar;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.game.player.actions.summoning.Summoning.Pouches;
import com.rs.utils.Utils;

public class Steeltitan extends Familiar {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6377458256826528627L;

	public Steeltitan(Player owner, Pouches pouch, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea) {
		super(owner, pouch, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
		setNextAnimation(new Animation(8188));
	}

	@Override
	public int getSpecialAmount() {
		return 12;
	}

	@Override
	public String getSpecialName() {
		return "Steel of Legends";
	}

	@Override
	public String getSpecialDescription() {
		return "Defence boost only applies to melee attacks. Scroll initiates attack on opponent, hitting four times, with either ranged or melee, depending on the distance to the target";
	}

	@Override
	public SpecialAttack getSpecialAttack() {
		return SpecialAttack.CLICK;
	}

	@Override
	public int getBOBSize() {
		return 0;
	}

	@Override
	public boolean submitSpecial(Object object) {
		getOwner().getSkills().addXp(Skills.SUMMONING, Utils.random(4, 7));
		getOwner().setNextAnimation(new Animation(7660));
		setNextAnimation(new Animation(8190));
		setNextGraphics(new Graphics(1449));
		titanSpec = true;
		return true;
	}
}