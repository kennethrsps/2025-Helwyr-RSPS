package com.rs.game.npc.combat;

import com.rs.game.Animation;
import com.rs.game.Entity;
import com.rs.game.Hit;
import com.rs.game.World;
import com.rs.game.WorldTile;
import com.rs.game.activites.ZombieOutpost.ZOGame;
import com.rs.game.npc.NPC;
import com.rs.game.npc.familiar.Familiar;
import com.rs.game.npc.godwars.zaros.Nex;
import com.rs.game.npc.pest.PestPortal;
import com.rs.game.npc.vorago.Vorago;
import com.rs.game.player.Player;
import com.rs.game.player.combat.PlayerCombat;
import com.rs.game.player.content.Combat;
import com.rs.game.player.content.BossBalancer;
import com.rs.game.player.content.BossBalancer.CombatScaling;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.MapAreas;
import com.rs.utils.Utils;

/**
 * Enhanced NPC Combat System - Full Boss Balancer Integration v3.1
 * Handles NPC combat logic with dynamic scaling based on player gear tiers
 * 
 * FEATURES:
 * - Complete Boss Balancer integration for damage scaling
 * - Dynamic boss difficulty based on player equipment
 * - Anti-farming protection for overgeared players
 * - Proper balance for undergeared players
 * - Enhanced combat accuracy and HP scaling
 * 
 * @author Zeus
 * @date June 05, 2025
 * @version 3.1 - COMPLETE BOSS BALANCER INTEGRATION
 */
public final class NPCCombat {

	private NPC npc;
	private int combatDelay;
	private Entity target;

	public NPCCombat(NPC npc) {
		this.npc = npc;
	}

	public int getCombatDelay() {
		return combatDelay;
	}

	/*
	 * returns if under combat
	 */
	public boolean process() {
		if (combatDelay > 0)
			combatDelay--;
		if (target != null) {
			if (!checkAll()) {
				removeTarget();
				return false;
			}
			if (combatDelay <= 0)
				combatDelay = combatAttack();
			return true;
		}
		return false;
	}

	/*
	 * return combatDelay
	 */
	private int combatAttack() {
		Entity target = this.target; // prevents multithread issues
		if (target == null)
			return 0;
		if (npc.isLock())
			return 0;
		// if hes frooze not gonna attack
		if (npc.getFreezeDelay() >= Utils.currentTimeMillis())
			return 0;
		// check if close to target, if not let it just walk and dont attack
		// this gametick
	
		NPCCombatDefinitions defs = npc.getCombatDefinitions();
		int attackStyle = defs.getAttackStyle();
		int maxDistance = attackStyle == NPCCombatDefinitions.MELEE || attackStyle == NPCCombatDefinitions.SPECIAL2 ? 0
				: 7;
		if (npc.getMaxDistance() != -1)
			maxDistance = npc.getMaxDistance();
		if (npc instanceof Vorago)
			maxDistance = 30;
		if ((!(npc instanceof Nex)) && !npc.clipedProjectile(target, maxDistance == 0))
			return 0;
		int distanceX = target.getX() - npc.getX();
		int distanceY = target.getY() - npc.getY();
		int size = npc.getSize();
		if (distanceX > size + maxDistance || distanceX < -1 - maxDistance || distanceY > size + maxDistance
				|| distanceY < -1 - maxDistance) {
			return 0;
		}
		addAttackedByDelay(target);
		
		// NEW: Enhanced attack execution with Boss Balancer awareness
		return executeAttackWithScaling(target, defs);
	}

	/**
	 * NEW: Execute attack with Boss Balancer scaling consideration
	 */
	private int executeAttackWithScaling(Entity target, NPCCombatDefinitions defs) {
		try {
			// Get base attack delay from combat scripts
			int baseDelay = CombatScriptsHandler.specialAttack(npc, target);
			
			// Apply Boss Balancer scaling adjustments if target is a player
			if (target instanceof Player) {
				Player player = (Player) target;
				CombatScaling scaling = BossBalancer.getCombatScaling(player, npc);
				
				// Adjust attack delay based on scaling (more difficult = slightly faster attacks)
				if (scaling.scalingType.equals("OVERGEARED_ANTI_FARM") && scaling.bossDamageMultiplier > 2.0) {
					baseDelay = Math.max(1, baseDelay - 1); // 1 tick faster for anti-farming
				} else if (scaling.scalingType.equals("UNDERGEARED")) {
					baseDelay = baseDelay + 1; // 1 tick slower when player is undergeared (more time to react)
				}
			}
			
			return baseDelay;
			
		} catch (Exception e) {
			System.err.println("Error in executeAttackWithScaling: " + e.getMessage());
			return CombatScriptsHandler.specialAttack(npc, target); // Fallback to normal attack
		}
	}

	protected void doDefenceEmote(Entity target) {
		/*
		 * if (target.getNextAnimation() != null) // if has att emote already
		 * return;
		 */
		target.setNextAnimationNoPriority(new Animation(Combat.getDefenceEmote(target)));
	}

	public Entity getTarget() {
		return target;
	}

	public void addAttackedByDelay(Entity target) { // prevents multithread
													// issues
		target.setAttackedBy(npc);
		target.setAttackedByDelay(Utils.currentTimeMillis() + 6000); // 6seconds
		npc.setAttackingDelay(Utils.currentTimeMillis() + 6000);
	}

	public void setTarget(Entity target) {
		this.target = target;
		if (npc.isCannotMove())
			return;
		npc.setNextFaceEntity(target);
		if (!checkAll()) {
			removeTarget();
			return;
		}
	}

	public boolean checkAll() {
		Entity target = this.target; // prevents multithread issues
		if (target == null)
			return false;
		if(ZOGame.withinArea(target)) {
			return false;
		}
		if (npc.isDead() || npc.hasFinished() || npc.isForceWalking() || target.isDead() || target.hasFinished()
				|| npc.getPlane() != target.getPlane())
			return false;
		if (npc.isCannotMove())
			return false;
		if (npc.getFreezeDelay() >= Utils.currentTimeMillis())
			return true; // if freeze cant move ofc
		int distanceX = npc.getX() - npc.getRespawnTile().getX();
		int distanceY = npc.getY() - npc.getRespawnTile().getY();
		int size = npc.getSize();
		int maxDistance;
		if (npc.getMaxDistance() != -1)
			maxDistance = npc.getMaxDistance();
		if (!npc.isNoDistanceCheck() && !npc.isCantFollowUnderCombat()) {
			maxDistance = 16;
			if (!(npc instanceof Familiar)) {

				if (npc.getMapAreaNameHash() != -1) {
					// if out his area
					if (!MapAreas.isAtArea(npc.getMapAreaNameHash(), npc) || (!npc.canBeAttackFromOutOfArea()
							&& !MapAreas.isAtArea(npc.getMapAreaNameHash(), target))) {
						npc.forceWalkRespawnTile();
						return false;
					}
				} else if (distanceX > size + maxDistance || distanceX < -1 - maxDistance
						|| distanceY > size + maxDistance || distanceY < -1 - maxDistance) {
					// if more than 16 distance from respawn place
					npc.forceWalkRespawnTile();
					return false;
				}
			}
			maxDistance = 16;
			distanceX = target.getX() - npc.getX();
			distanceY = target.getY() - npc.getY();
			if (distanceX > size + maxDistance || distanceX < -1 - maxDistance || distanceY > size + maxDistance
					|| distanceY < -1 - maxDistance)
				return false; // if target distance higher 16
		} else {
			distanceX = target.getX() - npc.getX();
			distanceY = target.getY() - npc.getY();
		}
		// checks for no multi area :)
		if (npc instanceof Familiar) {
			Familiar familiar = (Familiar) npc;
			if (!familiar.canAttack(target))
				return false;
		} else {
			if (!npc.isForceMultiAttacked()) {
				if (!target.isAtMultiArea() || !npc.isAtMultiArea()) {
					if (npc.getAttackedBy() != target && npc.getAttackedByDelay() > Utils.currentTimeMillis())
						return false;
					if (target.getAttackedBy() != npc && target.getAttackedByDelay() > Utils.currentTimeMillis())
						return false;
				}
			}
		}
		if (!npc.isCantFollowUnderCombat()) {
			// if is under
			int targetSize = target.getSize();
			if (distanceX < size && distanceX > -targetSize && distanceY < size && distanceY > -targetSize
					&& !target.hasWalkSteps()) {
				npc.resetWalkSteps();
				if (!npc.addWalkSteps(target.getX() + 1, npc.getY())) {
					npc.resetWalkSteps();
					if (!npc.addWalkSteps(target.getX() - size, npc.getY())) {
						npc.resetWalkSteps();
						if (!npc.addWalkSteps(npc.getX(), target.getY() + 1)) {
							npc.resetWalkSteps();
							if (!npc.addWalkSteps(npc.getX(), target.getY() - size)) {
								return true;
							}
						}
					}
				}
				return true;
			}
			if (npc.getCombatDefinitions().getAttackStyle() == NPCCombatDefinitions.MELEE && targetSize == 1
					&& size == 1 && Math.abs(npc.getX() - target.getX()) == 1
					&& Math.abs(npc.getY() - target.getY()) == 1 && !target.hasWalkSteps()) {

				if (!npc.addWalkSteps(target.getX(), npc.getY(), 1))
					npc.addWalkSteps(npc.getX(), target.getY(), 1);
				return true;
			}

			int attackStyle = npc.getCombatDefinitions().getAttackStyle();
			if (npc instanceof Nex) {
				Nex nex = (Nex) npc;
				maxDistance = nex.isForceFollowClose() ? 0 : 7;
				if (!nex.isFlying()
						&& (!npc.clipedProjectile(target, maxDistance == 0 && !forceCheckClipAsRange(target)))
						|| !Utils.isOnRange(npc.getX(), npc.getY(), size, target.getX(), target.getY(), targetSize,
								maxDistance)) {
					npc.resetWalkSteps();
					if (!Utils.isOnRange(npc.getX(), npc.getY(), size, target.getX(), target.getY(), targetSize, 10)) {
						int[][] dirs = Utils.getCoordOffsetsNear(size);
						for (int dir = 0; dir < dirs[0].length; dir++) {
							final WorldTile tile = new WorldTile(new WorldTile(target.getX() + dirs[0][dir],
									target.getY() + dirs[1][dir], target.getPlane()));
							if (World.isTileFree(tile.getPlane(), tile.getX(), tile.getY(), size)) {
								npc.setNextAnimation(new Animation(17408));
								npc.setNextWorldTile(tile);
								nex.setFlying(false);
								return true;
							}
						}
					} else
						npc.calcFollow(target, 2, true, npc.isIntelligentRouteFinder());
					return true;
				} else
					// if doesnt need to move more stop moving
					npc.resetWalkSteps();
			} else {
				maxDistance = npc.isForceFollowClose() ? 0
						: (attackStyle == NPCCombatDefinitions.MELEE || attackStyle == NPCCombatDefinitions.SPECIAL2)
								? 0 : (npc.getId() == 16698 || npc.getId() == 16699) ? 9 : 7;
				if (npc.getMaxDistance() != -1)
					maxDistance = npc.getMaxDistance();
				npc.resetWalkSteps();
				// is far from target, moves to it till can attack
				if ((!npc.clipedProjectile(target, maxDistance == 0)) || distanceX > size + maxDistance
						|| distanceX < -1 - maxDistance || distanceY > size + maxDistance
						|| distanceY < -1 - maxDistance) {
					if (!npc.addWalkStepsInteract(target.getX(), target.getY(), 2, size, true) && combatDelay < 3)
						combatDelay = 3;
					return true;
				}
				// if under target, moves

			}
		}
		return true;
	}

	public void addCombatDelay(int delay) {
		combatDelay += delay;
	}

	public void setCombatDelay(int delay) {
		combatDelay = delay;
	}

	public boolean underCombat() {
		return target != null;
	}

	public void removeTarget() {
		this.target = null;
		npc.setNextFaceEntity(null);
	}

	public void reset() {
		combatDelay = 0;
		target = null;
	}

	/**
	 * ENHANCED: Main hit application method with complete Boss Balancer integration
	 */
	public void delayHit(NPC npc, int delay, final Entity target, final Hit... hits) {
		npc.getCombat().addAttackedByDelay(target);
		WorldTasksManager.schedule(new WorldTask() {

			@Override
			public void run() {
				for (Hit hit : hits) {
					NPC npcSource = (NPC) hit.getSource();
					if (npcSource.isDead() || npcSource.hasFinished() || target.isDead() || target.hasFinished())
						return;
					
					// ENHANCED: Apply comprehensive Boss Balancer scaling
					applyCompleteBossBalancerScaling(hit, npcSource, target);
					
					// Apply the hit (now with scaled damage if applicable)
					target.applyHit(hit);
					npcSource.getCombat().doDefenceEmote(target);
					
					// Handle target response
					if (target instanceof Player) {
						Player p2 = (Player) target;
						// Auto-retaliate check
						if (p2.getCombatDefinitions().isAutoRelatie() && !p2.getActionManager().hasSkillWorking()
								&& !p2.hasWalkSteps())
							p2.getActionManager().setAction(new PlayerCombat(npcSource));
					} else {
						NPC n = (NPC) target;
						if (!n.isUnderCombat() || n.canBeAttackedByAutoRetaliate())
							n.setTarget(npcSource);
					}

				}
				this.stop();
			}

		}, delay);
	}

	/**
	 * ENHANCED: Complete Boss Balancer scaling integration
	 * This method applies all aspects of boss balancer scaling to hits
	 */
	public static void applyCompleteBossBalancerScaling(Hit hit, NPC npc, Entity target) {
		if (!(target instanceof Player)) {
			return; // Only scale for player targets
		}
		
		try {
			Player player = (Player) target;
			
			// Get comprehensive scaling from Boss Balancer
			CombatScaling scaling = BossBalancer.getCombatScaling(player, npc);
			
			int originalDamage = hit.getDamage();
			
			// Apply damage scaling using new Boss Balancer methods
			int scaledDamage = BossBalancer.applyBossScaling(originalDamage, player, npc);
			
			// Update the hit with scaled damage
			hit.setDamage(scaledDamage);
			
			// Apply special effects based on scaling type
			applyScalingEffects(hit, npc, player, scaling);
			
			// Optional: Debug logging for balance testing (remove in production)
			logScalingDebugInfo(npc, player, originalDamage, scaledDamage, scaling);
			
		} catch (Exception e) {
			System.err.println("Error applying complete BossBalancer scaling: " + e.getMessage());
			// If BossBalancer fails, continue with original damage
		}
	}

	/**
	 * NEW: Apply special effects based on scaling type
	 */
	private static void applyScalingEffects(Hit hit, NPC npc, Player player, CombatScaling scaling) {
		try {
			// Enhanced effects for anti-farming
			if (scaling.scalingType.equals("OVERGEARED_ANTI_FARM") && scaling.bossDamageMultiplier > 3.0) {
				// Add visual effect for extreme anti-farming
				WorldTasksManager.schedule(new WorldTask() {
					@Override
					public void run() {
						player.setNextGraphics(new com.rs.game.Graphics(2108, 0, 100)); // Red warning effect
					}
				}, 1);
				
				// Optional: Add message for extreme scaling (with cooldown)
				if (scaling.bossDamageMultiplier > 4.0) {
					BossBalancer.sendWarningWithCooldown(player, 
						"<col=ff0000>EXTREME ANTI-FARMING: +" + 
						(int)((scaling.bossDamageMultiplier - 1.0) * 100) + "% boss difficulty!");
				}
			}
			
			// Enhanced effects for undergeared players
			else if (scaling.scalingType.equals("UNDERGEARED")) {
				// Add visual indicator that boss is harder
				WorldTasksManager.schedule(new WorldTask() {
					@Override
					public void run() {
						player.setNextGraphics(new com.rs.game.Graphics(2113, 0, 100)); // Yellow warning effect
					}
				}, 1);
			}
			
			// Balanced fight confirmation
			else if (scaling.scalingType.equals("BALANCED") && scaling.bossDamageMultiplier < 1.0) {
				// Good equipment makes fight easier - subtle positive effect
				if (hit.getDamage() > 0 && Utils.random(10) == 0) { // 10% chance
					WorldTasksManager.schedule(new WorldTask() {
						@Override
						public void run() {
							player.setNextGraphics(new com.rs.game.Graphics(2109, 0, 100)); // Blue positive effect
						}
					}, 1);
				}
			}
			
		} catch (Exception e) {
			System.err.println("Error applying scaling effects: " + e.getMessage());
		}
	}

	/**
	 * NEW: Enhanced logging for balance testing and debugging
	 */
	private static void logScalingDebugInfo(NPC npc, Player player, int originalDamage, int scaledDamage, CombatScaling scaling) {
		// Only log significant scaling changes or for testing purposes
		if (Math.abs(originalDamage - scaledDamage) > 5 || System.getProperty("bossbalancer.debug") != null) {
			
			String npcName = "Unknown";
			try {
				com.rs.cache.loaders.NPCDefinitions npcDef = com.rs.cache.loaders.NPCDefinitions.getNPCDefinitions(npc.getId());
				if (npcDef != null && npcDef.getName() != null) {
					npcName = npcDef.getName();
				}
			} catch (Exception e) {
				npcName = "NPC_" + npc.getId();
			}
			
			System.out.println(String.format(
				"[BossBalancer] %s(T%d) vs %s(T%d): %d->%d dmg (%.2fx, %s) | Equipment: %s",
				npcName, scaling.bossTier,
				player.getUsername(), scaling.playerTier,
				originalDamage, scaledDamage,
				scaling.bossDamageMultiplier,
				scaling.scalingType,
				BossBalancer.isDualWielding(player) ? "Dual-Wield" : "Single"
			));
		}
	}

	/**
	 * NEW: Get effective NPC max hit with Boss Balancer scaling
	 * This can be used by combat scripts to preview damage ranges
	 */
	public static int getScaledMaxHit(NPC npc, Player player) {
		try {
			NPCCombatDefinitions defs = npc.getCombatDefinitions();
			if (defs == null) {
				return 10; // Default
			}
			
			int baseMaxHit = defs.getMaxHit();
			return BossBalancer.applyBossMaxHitScaling(baseMaxHit, player, npc);
			
		} catch (Exception e) {
			System.err.println("Error getting scaled max hit: " + e.getMessage());
			return npc.getCombatDefinitions() != null ? npc.getCombatDefinitions().getMaxHit() : 10;
		}
	}

	/**
	 * NEW: Get effective NPC HP with Boss Balancer scaling
	 * This can be used when spawning NPCs or resetting HP
	 */
	public static int getScaledHitpoints(NPC npc, Player player) {
		try {
			NPCCombatDefinitions defs = npc.getCombatDefinitions();
			if (defs == null) {
				return 100; // Default
			}
			
			int baseHp = defs.getHitpoints();
			return BossBalancer.applyBossHpScaling(baseHp, player, npc);
			
		} catch (Exception e) {
			System.err.println("Error getting scaled HP: " + e.getMessage());
			return npc.getCombatDefinitions() != null ? npc.getCombatDefinitions().getHitpoints() : 100;
		}
	}

	/**
	 * NEW: Check if NPC should have enhanced effects based on player scaling
	 */
	public static boolean shouldApplyEnhancedEffects(NPC npc, Player player) {
		try {
			CombatScaling scaling = BossBalancer.getCombatScaling(player, npc);
			
			// Enhanced effects for significant scaling differences
			return scaling.bossDamageMultiplier > 1.5 || scaling.bossDamageMultiplier < 0.8;
			
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * LEGACY: Simplified method for backward compatibility
	 * @deprecated Use applyCompleteBossBalancerScaling instead
	 */
	@Deprecated
	public static void applyBossBalancerScaling(Hit hit, NPC npc, Entity target) {
		applyCompleteBossBalancerScaling(hit, npc, target);
	}

	private boolean forceCheckClipAsRange(Entity target) {
		return target instanceof PestPortal;
	}
}