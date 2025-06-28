package com.rs.game.player;

import java.io.Serializable;

/**
 * Handles everything related to Animation Overrides.
 * @author Zeus.
 */
public class AnimationOverrides implements Serializable {

	/**
	 * The generated serial UID.
	 */
	private static final long serialVersionUID = -773529284927927947L;
	
	/**
	 * The player instance.
	 */
	@SuppressWarnings("unused")
	private transient Player player;

	/**
	 * The player instance saving to.
	 * @param player The player.
	 */
	protected void setPlayer(Player player) {
		this.player = player;
	}

	/**
	 * Representing all available overrides.
	 */
	public boolean battleCry, enhancedPotion, lumberjackWc, deepFishing, zenResting,
		karateFletch, ironSmith, chiMining, samuraiCook, roundHouseWc,
		blastMining, strongResting, arcaneSmelt, arcaneResting, strongWc,
		strongMining, arcaneFishing, strongBurial, arcaneCook, powerDivination,
		powerConversion, agileDivination, agileConversion, sinisterSlumber, armWarrior,
		eneResting, crystalResting, headMining, sandWalk, sadWalk, angryWalk, proudWalk,
		happyWalk, barbarianWalk, revenantWalk;

	/**
	 * Representing all available unlocked overrides.
	 */
	public boolean hasBattleCry, hasEnhancedPotion, hasLumberjackWc, hasDeepFishing, hasZenResting,
		hasKarateFletch, hasIronSmith, hasChiMining, hasSamuraiCook, hasRoundHouseWc, 
		hasBlastMining, hasStrongResting, hasArcaneSmelt, hasArcaneResting, hasStrongWc,
		hasStrongMining, hasArcaneFishing, hasStrongBurial, hasArcaneCook, hasPowerDivination,
		hasPowerConversion, hasAgileDivination, hasAgileConversion, hasSinisterSlumber, hasArmWarrior,
		hasEneResting, hasCrystalResting, hasHeadMining, hasSandWalk, hasSadWalk, hasAngryWalk, hasProudWalk,
		hasHappyWalk, hasBarbarianWalk, hasRevenantWalk;
}