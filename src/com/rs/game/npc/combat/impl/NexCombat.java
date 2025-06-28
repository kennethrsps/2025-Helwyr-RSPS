package com.rs.game.npc.combat.impl;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.rs.cache.loaders.NPCDefinitions;
import com.rs.game.Animation;
import com.rs.game.Entity;
import com.rs.game.ForceMovement;
import com.rs.game.ForceTalk;
import com.rs.game.Graphics;
import com.rs.game.Hit;
import com.rs.game.Hit.HitLook;
import com.rs.game.World;
import com.rs.game.WorldObject;
import com.rs.game.WorldTile;
import com.rs.game.activites.ZarosGodwars;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.game.npc.godwars.zaros.Nex;
import com.rs.game.npc.godwars.zaros.Nex.NexPhase;
import com.rs.game.player.Player;
import com.rs.game.player.content.BossBalancer;
import com.rs.game.player.cutscenes.NexCutScene;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Utils;
import com.rs.utils.NPCBonuses;

/**
 * Enhanced Nex Combat System with Boss Balancer Integration and Advanced Guidance
 * 
 * @author Zeus
 * @date June 02, 2025
 * @version 3.0 - Enhanced with Boss Balancer, Multi-Phase Guidance, and Anti-Safespot
 * @note Ultimate endgame raid boss with sophisticated mechanics and team coordination
 */
public class NexCombat extends CombatScript {

	/**
	 * The teleport coordinates used during nex's "no escape" attack.
	 */
	public static final WorldTile[] NO_ESCAPE_TELEPORTS = { 
		new WorldTile(2924, 5213, 0), // north
		new WorldTile(2934, 5202, 0), // east,
		new WorldTile(2924, 5192, 0), // south
		new WorldTile(2913, 5202, 0), // west
	};

	// Enhanced guidance system for raid boss
	private long lastGuidanceMessage = 0;
	private long lastPhaseGuidance = 0;
	private long lastSafespotCheck = 0;
	private static final long GUIDANCE_INTERVAL = 20000; // 20 seconds for raid boss
	private static final long PHASE_GUIDANCE_INTERVAL = 5000; // 5 seconds for phase changes
	private static final long SAFESPOT_CHECK_INTERVAL = 6000; // 6 seconds
	
	// Phase-specific guidance tracking
	private boolean[] phaseGuidanceGiven = new boolean[6]; // 5 phases + initial
	private boolean hasWarnedAboutVirus = false;
	private boolean hasWarnedAboutShadowTraps = false;
	private boolean hasWarnedAboutBloodSacrifice = false;
	private boolean hasWarnedAboutIcePrison = false;
	private boolean hasWarnedAboutNoEscape = false;
	
	// Combat tracking for advanced guidance
	private int totalAttackCount = 0;
	private int consecutiveRangedMisses = 0;
	private boolean lastAttackConnected = true;

	@Override
	public Object[] getKeys() {
		return new Object[] { "Nex" };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		final Nex nex = (Nex) npc;
		
		// Enhanced safety validation
		if (!isValidNexCombatState(nex, target)) {
			return 5;
		}
		
		// Advanced Boss Balancer Integration
		int enhancedMaxHit = getEnhancedNexMaxHit(nex);
		int bossType = getNexBossType(nex);
		int bossTier = getNexBossTier(nex);
		
		// Comprehensive Nex Guidance System
		if (target instanceof Player) {
			provideAdvancedNexGuidance(nex, (Player) target, bossType, bossTier);
			checkAndHandleNexSafespot(nex, (Player) target);
		}
		
		// Increment total attack counter
		totalAttackCount++;
		
		nex.setForceFollowClose(Utils.random(2) == 0);
		boolean hasDistance = calculateDistance(nex, target);
		nex.resetLastAttack();
		
		if (nex.isSiphioning() || nex.isFlying())
			return 0;

		// Phase-specific combat with enhanced mechanics
		switch (nex.getCurrentPhase().getPhaseValue()) {
		case 1: // Enhanced Smoke Phase
			return handleSmokePhase(nex, target, hasDistance, enhancedMaxHit);
		case 2: // Enhanced Shadow Phase
			return handleShadowPhase(nex, target, enhancedMaxHit);
		case 3: // Enhanced Blood Phase
			return handleBloodPhase(nex, target, hasDistance, enhancedMaxHit);
		case 4: // Enhanced Ice Phase
			return handleIcePhase(nex, target, hasDistance, enhancedMaxHit);
		case 5: // Enhanced Power of Zaros Phase
			return handleZarosPhase(nex, target, hasDistance, enhancedMaxHit);
		}
		return 0;
	}
	
	/**
	 * Enhanced Smoke Phase with Boss Balancer integration
	 */
	private int handleSmokePhase(Nex nex, Entity target, boolean hasDistance, int enhancedMaxHit) {
		// Phase guidance announcement
		announcePhaseGuidance(nex, 1, "SMOKE PHASE: Virus attacks, poison, and pull mechanics!");
		
		if (nex.isFirstStageAttack()) {
			sendEnhancedVirusAttack(nex, enhancedMaxHit);
			nex.setFirstStageAttack(false);
			return nex.getAttackSpeed();
		}
		
		switch (hasDistance ? Utils.random(7) : Utils.random(11)) {
		case 0:
		case 1:
		case 2:
			sendEnhancedMagicAttack(nex, false, enhancedMaxHit);
			break;
		case 3:
			sendEnhancedPullAttack(nex);
			break;
		case 4:
			if (nex.getLastVirusAttack() > Utils.currentTimeMillis())
				return 0;
			sendEnhancedVirusAttack(nex, enhancedMaxHit);
			break;
		case 5:
		case 6:
			sendEnhancedMagicAttack(nex, true, enhancedMaxHit);
			break;
		case 7:
		case 8:
		case 9:
			sendEnhancedMeleeAttack(nex, target, enhancedMaxHit);
			break;
		case 10:
			sendEnhancedNoEscape(nex, target, enhancedMaxHit);
			break;
		}
		return nex.getAttackSpeed();
	}
	
	/**
	 * Enhanced Shadow Phase with Boss Balancer integration
	 */
	private int handleShadowPhase(Nex nex, Entity target, int enhancedMaxHit) {
		// Phase guidance announcement
		announcePhaseGuidance(nex, 2, "SHADOW PHASE: Shadow traps, embrace darkness, and ranged attacks!");
		
		if (nex.isFirstStageAttack()) {
			nex.setFirstStageAttack(false);
			if (Utils.random(6) == 0)
				return sendEnhancedShadowTraps(nex, enhancedMaxHit);
			else
				sendEnhancedEmbraceDarkness(nex);
		}
		
		switch (Utils.random(4)) {
		case 0:
			return sendEnhancedShadowTraps(nex, enhancedMaxHit);
		case 1:
		case 2:
			sendEnhancedRangeAttack(nex, enhancedMaxHit);
			break;
		default:
			if (Utils.random(5) == 0)
				sendEnhancedEmbraceDarkness(nex);
			else
				sendEnhancedRangeAttack(nex, enhancedMaxHit);
		}
		return nex.getAttackSpeed();
	}
	
	/**
	 * Enhanced Blood Phase with Boss Balancer integration
	 */
	private int handleBloodPhase(Nex nex, Entity target, boolean hasDistance, int enhancedMaxHit) {
		// Phase guidance announcement
		announcePhaseGuidance(nex, 3, "BLOOD PHASE: Siphon healing, blood sacrifice, and life steal!");
		
		if (nex.getNexAttack() == 0)
			sendEnhancedSipionAttack(nex, target, enhancedMaxHit);
		else if (nex.getNexAttack() >= 1 && nex.getNexAttack() <= 4)
			sendEnhancedNormalBloodAttack(hasDistance, nex, target, enhancedMaxHit);
		else if (nex.getNexAttack() == 5)
			return sendEnhancedBloodSacrifice(nex, target, enhancedMaxHit);
		else if (nex.getNexAttack() >= 6 && nex.getNexAttack() <= 10)
			sendEnhancedNormalBloodAttack(hasDistance, nex, target, enhancedMaxHit);
		else if (nex.getNexAttack() == 11) {
			nex.resetNexAttack();
			sendEnhancedSipionAttack(nex, target, enhancedMaxHit);
		}
		nex.incrementNexAttack();
		return nex.getAttackSpeed();
	}
	
	/**
	 * Enhanced Ice Phase with Boss Balancer integration
	 */
	private int handleIcePhase(Nex nex, Entity target, boolean hasDistance, int enhancedMaxHit) {
		// Phase guidance announcement
		announcePhaseGuidance(nex, 4, "ICE PHASE: Ice prison, barricades, and freezing attacks!");
		
		if (nex.getNexAttack() == 0)
			sendEnhancedIcePrison(nex, true, enhancedMaxHit);
		else if (nex.getNexAttack() >= 1 && nex.getNexAttack() <= 5 || 
				 nex.getNexAttack() >= 7 && nex.getNexAttack() <= 9)
			sendEnhancedNormalIceAttacks(hasDistance, nex, target, enhancedMaxHit);
		else if (nex.getNexAttack() == 6)
			sendEnhancedIceBarricade(nex, enhancedMaxHit);
		
		if (nex.getNexAttack() == 10) {
			nex.resetNexAttack();
			sendEnhancedIcePrison(nex, true, enhancedMaxHit);
		}
		nex.incrementNexAttack();
		return nex.getAttackSpeed();
	}
	
	/**
	 * Enhanced Power of Zaros Phase with Boss Balancer integration
	 */
	private int handleZarosPhase(Nex nex, Entity target, boolean hasDistance, int enhancedMaxHit) {
		// Phase guidance announcement
		announcePhaseGuidance(nex, 5, "ZAROS PHASE: Soul split, enhanced attacks, and ultimate power!");
		
		switch (hasDistance ? Utils.random(3) : Utils.random(11) + 3) {
		case 0:
		case 1:
			sendEnhancedMagicAttack(nex, false, enhancedMaxHit);
			break;
		case 2:
			sendEnhancedMagicAttack(nex, true, enhancedMaxHit);
			break;
		case 3:
		case 4:
		case 5:
		case 6:
		case 7:
		case 8:
		case 9:
		case 10:
		case 11:
		case 12:
			sendEnhancedMeleeAttack(nex, target, enhancedMaxHit);
			break;
		case 13:
			sendEnhancedNoEscape(nex, target, enhancedMaxHit);
			break;
		}
		return nex.getAttackSpeed();
	}

	/**
	 * ADVANCED NEX GUIDANCE SYSTEM: Comprehensive raid boss guidance
	 */
	private void provideAdvancedNexGuidance(Nex nex, Player player, int bossType, int bossTier) {
		long currentTime = System.currentTimeMillis();
		
		// Initial raid encounter guidance
		if (!phaseGuidanceGiven[0]) {
			phaseGuidanceGiven[0] = true;
			announceToRaid(nex, "<col=ff0000>[NEX RAID] The Ancient Prison of Nex has been breached!");
			announceToRaid(nex, "<col=ffff00>[Raid Guide] ULTIMATE ENDGAME BOSS: Multi-phase mechanics, team coordination required!");
			announceToRaid(nex, "<col=00ff00>[Strategy] Phase-specific tactics, prayer switching, and positioning crucial!");
			lastGuidanceMessage = currentTime;
			return;
		}
		
		// Phase-specific warnings and guidance
		NexPhase currentPhase = nex.getCurrentPhase();
		providePhaseSpecificGuidance(nex, player, currentPhase);
		
		// Advanced periodic guidance
		if (currentTime - lastGuidanceMessage >= GUIDANCE_INTERVAL) {
			lastGuidanceMessage = currentTime;
			provideAdvancedRaidGuidance(nex, player, bossType, bossTier, currentPhase);
		}
	}
	
	/**
	 * Provide phase-specific guidance and warnings
	 */
	private void providePhaseSpecificGuidance(Nex nex, Player player, NexPhase currentPhase) {
		// Virus attack warning
		if (currentPhase == NexPhase.SMOKE && !hasWarnedAboutVirus) {
			hasWarnedAboutVirus = true;
			announceToRaid(nex, "<col=9900ff>[Virus Alert] Targets furthest players and spreads! Stay separated!");
			player.sendMessage("<col=ffff00>Smoke Guide: Pull attack drags furthest player and disables prayers!");
		}
		
		// Shadow traps warning
		if (currentPhase == NexPhase.SHADOW && !hasWarnedAboutShadowTraps) {
			hasWarnedAboutShadowTraps = true;
			announceToRaid(nex, "<col=6600ff>[Shadow Alert] Traps spawn on player positions! Keep moving!");
			player.sendMessage("<col=ffff00>Shadow Guide: Embrace Darkness affects visibility - stay close to team!");
		}
		
		// Blood sacrifice warning
		if (currentPhase == NexPhase.BLOOD && !hasWarnedAboutBloodSacrifice) {
			hasWarnedAboutBloodSacrifice = true;
			announceToRaid(nex, "<col=cc0000>[Blood Alert] Sacrifice marks a player - RUN when targeted!");
			player.sendMessage("<col=ffff00>Blood Guide: Siphon spawns Blood Reavers - kill them quickly!");
		}
		
		// Ice prison warning
		if (currentPhase == NexPhase.ICE && !hasWarnedAboutIcePrison) {
			hasWarnedAboutIcePrison = true;
			announceToRaid(nex, "<col=00ccff>[Ice Alert] Prison traps players in 3x3 ice! Don't stand in center!");
			player.sendMessage("<col=ffff00>Ice Guide: Barricade disables prayers - destroy ice walls quickly!");
		}
		
		// No Escape warning
		if (!hasWarnedAboutNoEscape && totalAttackCount > 20) {
			hasWarnedAboutNoEscape = true;
			announceToRaid(nex, "<col=ff6600>[Ultimate Alert] NO ESCAPE attack pulls all players! Massive damage incoming!");
			player.sendMessage("<col=ff0000>ULTIMATE WARNING: No Escape is devastating - prepare for heavy damage!");
		}
	}
	
	/**
	 * Provide advanced raid-level guidance
	 */
	private void provideAdvancedRaidGuidance(Nex nex, Player player, int bossType, int bossTier, NexPhase currentPhase) {
		double hpPercentage = (double) nex.getHitpoints() / nex.getMaxHitpoints();
		
		if (hpPercentage < 0.2) {
			announceToRaid(nex, "<col=ff0000>[Critical Phase] Nex below 20% HP - maximum aggression!");
		} else if (hpPercentage < 0.5) {
			announceToRaid(nex, "<col=ffff00>[High Intensity] Nex below 50% HP - increased special frequency!");
		}
		
		// Tier-specific advanced guidance
		if (bossTier >= 8) {
			String[] advancedTips = {
				"LEGENDARY NEX: Enhanced damage and mechanics - coordinate defensive abilities!",
				"Use Vengeance and damage boosting abilities during DPS phases!",
				"Prayer switching essential - Magic/Melee protection alternation!",
				"Team positioning crucial - spread for AOE, stack for healing!",
				"Monitor phase transitions - prepare for incoming special attacks!"
			};
			announceToRaid(nex, "<col=00ff00>[Advanced Strategy] " + advancedTips[Utils.random(advancedTips.length)]);
		}
	}
	
	/**
	 * ADVANCED ANTI-SAFESPOT SYSTEM: Nex-specific countermeasures
	 */
	private void checkAndHandleNexSafespot(Nex nex, Player player) {
		long currentTime = System.currentTimeMillis();
		
		if (currentTime - lastSafespotCheck < SAFESPOT_CHECK_INTERVAL) {
			return;
		}
		lastSafespotCheck = currentTime;
		
		// Detect complex safespotting patterns
		if (isNexSafespotting(nex, player)) {
			handleNexSafespotting(nex, player);
		}
	}
	
	/**
	 * Detect Nex-specific safespotting
	 */
	private boolean isNexSafespotting(Nex nex, Player player) {
		// Multiple criteria for raid boss safespot detection
		boolean tooFarFromRaid = !player.withinDistance(nex, 15);
		boolean consecutiveMisses = consecutiveRangedMisses >= 4;
		boolean notInRaidGroup = !ZarosGodwars.getPlayers().contains(player);
		boolean isolatedFromTeam = true;
		
		// Check if player is isolated from team
		for (Player p : ZarosGodwars.getPlayers()) {
			if (p != player && p.withinDistance(player, 5)) {
				isolatedFromTeam = false;
				break;
			}
		}
		
		return (tooFarFromRaid && consecutiveMisses) || notInRaidGroup || 
			   (isolatedFromTeam && !lastAttackConnected);
	}
	
	/**
	 * Handle Nex safespotting with raid-appropriate countermeasures
	 */
	private void handleNexSafespotting(Nex nex, Player player) {
		announceToRaid(nex, "<col=ff0000>[Anti-Safespot] Nex detects cowardly tactics!");
		
		NexPhase currentPhase = nex.getCurrentPhase();
		
		switch (currentPhase) {
		case SMOKE:
			// Forced virus spread to safespotter
			player.sendMessage("<col=9900ff>The virus seeks out hidden enemies!");
			sendEnhancedVirusAttack(nex, getEnhancedNexMaxHit(nex));
			break;
			
		case SHADOW:
			// Shadow traps at safespot location
			player.sendMessage("<col=6600ff>Shadows engulf your hiding spot!");
			createSafespotShadowTrap(nex, player);
			break;
			
		case BLOOD:
			// Forced blood sacrifice targeting
			player.sendMessage("<col=cc0000>Your blood calls to Nex from afar!");
			sendEnhancedBloodSacrifice(nex, player, getEnhancedNexMaxHit(nex));
			break;
			
		case ICE:
			// Ice prison at safespot
			player.sendMessage("<col=00ccff>Ice forms around your cowardly position!");
			createSafespotIcePrison(nex, player);
			break;
			
		case ZAROS:
			// Ultimate No Escape
			player.sendMessage("<col=ff6600>The power of Zaros compels your presence!");
			sendEnhancedNoEscape(nex, player, getEnhancedNexMaxHit(nex));
			break;
		}
		
		// Reset consecutive misses after countermeasure
		consecutiveRangedMisses = 0;
	}

	// Enhanced attack methods with Boss Balancer integration
	
	private void sendEnhancedVirusAttack(Nex nex, int enhancedMaxHit) {
		nex.addVirusAttackDelay(45 + Utils.random(15));
		nex.setNextForceTalk(new ForceTalk("Let the enhanced virus flow through you!"));
		nex.playSoundEffect(3296);
		nex.setNextAnimation(new Animation(17414));
		nex.setNextGraphics(new Graphics(3375));
		
		announceToRaid(nex, "<col=9900ff>[Virus Alert] Enhanced viral outbreak! Affects furthest players!");
		
		for (Entity entity : nex.getPossibleTargets()) {
			if (!entity.withinDistance(nex.getFarthestTarget(), 2))
				continue;
			entity.setNextForceTalk(new ForceTalk("*Cough*"));
			
			// Enhanced virus damage with Boss Balancer scaling
			int virusDamage = calculateEnhancedNexDamage(nex, enhancedMaxHit, 0.3); // 30% of max hit
			entity.applyHit(new Hit(nex, virusDamage, HitLook.POISON_DAMAGE));
			entity.getTemporaryAttributtes().put("nex_infected", true);
			
			if (entity instanceof Player) {
				Player player = (Player) entity;
				player.sendMessage("<col=9900ff>Virus Guide: Stay away from infected players to prevent spread!");
			}
		}
		nex.playSound(3296, 2);
	}
	
	private void sendEnhancedMagicAttack(final Nex nex, final boolean secondAttack, int enhancedMaxHit) {
		for (final Entity t : nex.getPossibleTargets()) {
			nex.setNextAnimation(new Animation(17413));
			nex.setNextGraphics(new Graphics(1214));
			World.sendProjectile(nex, t, 3371, 25, 25, 32, 0, 0, 0);
			
			// Enhanced magic damage calculation
			final int damage = calculateEnhancedNexDamage(nex, enhancedMaxHit, 1.0);
			Hit hit = getMagicHit(nex, damage);
			delayHit(nex, 2, t, hit);
			
			if (nex.getCurrentPhase() == NexPhase.ZAROS)
				sendSoulSplit(hit, nex, t);
				
			if (damage > 0) {
				WorldTasksManager.schedule(new WorldTask() {
					@Override
					public void run() {
						t.setNextGraphics(new Graphics(3373));
						if (nex.getCurrentPhase() == NexPhase.SMOKE && !secondAttack) {
							if (damage > 0 && Utils.random(4) == 0) // Increased poison chance
								t.getPoison().makePoisoned(100); // Stronger poison
						}
					}
				}, 2);
			}
		}
		lastAttackConnected = true;
	}
	
	private void sendEnhancedRangeAttack(final Nex nex, int enhancedMaxHit) {
		nex.setNextAnimation(new Animation(17413));
		
		announceToRaid(nex, "<col=ff6600>[Range Alert] Distance-based damage! Stay close for less damage!");
		
		for (final Entity t : nex.getPossibleTargets()) {
			int distance = Utils.getDistance(t.getX(), t.getY(), nex.getX(), nex.getY());
			int baseDamage;
			
			if (distance <= 10)
				baseDamage = enhancedMaxHit - (distance * enhancedMaxHit / 15); // Enhanced scaling
			else
				baseDamage = (int) (enhancedMaxHit * 0.6) + Utils.random(enhancedMaxHit / 4);
				
			World.sendProjectile(nex, t, 380, 25, 25, 32, 0, 0, 0);
			
			boolean canHit = !nex.clipedProjectile(t, true);
			if (canHit) {
				delayHit(nex, 2, t, getRangeHit(nex, baseDamage));
				lastAttackConnected = true;
				consecutiveRangedMisses = 0;
			} else {
				lastAttackConnected = false;
				consecutiveRangedMisses++;
			}
		}
	}
	
	private void sendEnhancedMeleeAttack(Nex nex, Entity target, int enhancedMaxHit) {
		nex.setNextAnimation(new Animation(17453));
		
		// Enhanced melee damage calculation
		int meleeDamage = calculateEnhancedNexDamage(nex, enhancedMaxHit, 1.0);
		Hit hit = getMeleeHit(nex, meleeDamage);
		delayHit(nex, 0, target, hit);
		
		if (nex.getCurrentPhase() == NexPhase.ZAROS)
			sendSoulSplit(hit, nex, target);
			
		lastAttackConnected = true;
	}

	// Enhanced special attacks (keeping original logic but adding Boss Balancer scaling)
	
	private void sendEnhancedPullAttack(final Nex nex) {
		Entity target = nex.getFarthestTarget();
		if (target instanceof Player) {
			final Player player = (Player) target;
			
			announceToRaid(nex, "<col=ffff00>[Pull Alert] " + player.getDisplayName() + " is being pulled into combat!");
			player.sendMessage("<col=ff0000>Pull Guide: You've been targeted! Prayers will be disabled!");
			
			player.lock(3);
			player.getActionManager().setActionDelay(10);
			player.resetWalkSteps();
			player.setNextAnimation(new Animation(-1));
			
			WorldTasksManager.schedule(new WorldTask() {
				int ticks;
				
				@Override
				public void run() {
					ticks++;
					
					if (ticks == 1) {
						player.setNextAnimation(new Animation(14388));
						player.setNextGraphics(new Graphics(2767));
						player.setNextForceMovement(new ForceMovement(nex, 2, Utils.getMoveDirection(
								nex.getCoordFaceX(player.getSize()) - player.getX(),
								nex.getCoordFaceY(player.getSize()) - player.getY())));
						nex.setTarget(player);
					} else if (ticks == 3) {
						player.setNextWorldTile(nex);
						player.sendMessage("You've been injured and you cannot use "
								+ (player.getPrayer().isAncientCurses() ? "protective curses"
										: "protective prayers") + "!");
						player.setPrayerDelay(Utils.getRandom(25000) + 8000); // Enhanced prayer delay
						player.resetWalkSteps();
						int delay = 6 + Utils.random(6); // Enhanced stun duration
						player.getActionManager().setActionDelay(delay);
						player.addFreezeDelay(delay * 1000, true);
						this.stop();
					}
				}
			}, 0, 0);
		}
	}
	
	private void sendEnhancedNoEscape(final Nex nex, final Entity target, int enhancedMaxHit) {
		nex.setNextForceTalk(new ForceTalk("There is..."));
		nex.playSoundEffect(3294);
		nex.setCantInteract(true);
		
		announceToRaid(nex, "<col=ff0000>[ULTIMATE ALERT] NO ESCAPE INCOMING! Prepare for massive damage!");
		
		final int index = Utils.random(NO_ESCAPE_TELEPORTS.length);
		final WorldTile dir = NO_ESCAPE_TELEPORTS[index];
		final WorldTile center = new WorldTile(2924, 5202, 0);
		
		WorldTasksManager.schedule(new WorldTask() {
			private int count;

			@Override
			public void run() {
				if (count == 0) {
					nex.setNextAnimation(new Animation(17411));
					nex.setNextGraphics(new Graphics(1216));
				} else if (count == 1) {
					nex.setNextWorldTile(dir);
					nex.setNextForceTalk(new ForceTalk("NO ESCAPE!"));
					nex.playSoundEffect(3292);
					nex.setNextForceMovement(new ForceMovement(dir, 1, center, 3, 
							index == 3 ? 1 : index == 2 ? 0 : index == 1 ? 3 : 2));
					
					for (Entity entity : nex.calculatePossibleTargets(center, dir, index == 0 || index == 2)) {
						if (entity instanceof Player) {
							final Player player = (Player) entity;
							player.setAttackedBy(null);
							player.stopAll();
							player.getCutscenesManager().play(new NexCutScene(dir, index));
							
							// Enhanced No Escape damage with Boss Balancer scaling
							int noEscapeDamage = calculateEnhancedNexDamage(nex, enhancedMaxHit, 1.8); // 180% damage
							Hit hit = new Hit(nex, noEscapeDamage, HitLook.REGULAR_DAMAGE);
							
							if (nex.getCurrentPhase() == NexPhase.ZAROS)
								sendSoulSplit(hit, nex, entity);
								
							player.applyHit(hit);
							player.setNextAnimation(new Animation(10070));
							player.setNextForceMovement(new ForceMovement(center, 2, 
									index == 3 ? 3 : index == 2 ? 2 : index == 1 ? 1 : 0));
							
							WorldTasksManager.schedule(new WorldTask() {
								@Override
								public void run() {
									player.setNextWorldTile(center);
								}
							}, 2);
						}
					}
				} else if (count == 3) {
					nex.setNextWorldTile(center);
				} else if (count == 4) {
					nex.setCantInteract(false);
					nex.setTarget(target);
					stop();
					return;
				}
				count++;
			}
		}, 0, 1);
	}

	// Enhanced Boss Balancer integration methods
	
	/**
	 * Get enhanced max hit using Boss Balancer for Nex
	 */
	private int getEnhancedNexMaxHit(Nex nex) {
		try {
			int baseMaxHit = nex.getCombatDefinitions().getMaxHit();
			
			// Apply Boss Balancer bonuses for all attack styles
			int[] bonuses = NPCBonuses.getBonuses(nex.getId());
			if (bonuses != null && bonuses.length >= 5) {
				int maxOffensiveBonus = 0;
				for (int i = 0; i < 5; i++) {
					maxOffensiveBonus = Math.max(maxOffensiveBonus, bonuses[i]);
				}
				
				// Nex gets higher bonus scaling as ultimate raid boss
				double bonusMultiplier = 1.0 + (maxOffensiveBonus * 0.002); // 0.2% per bonus point
				baseMaxHit = (int) (baseMaxHit * bonusMultiplier);
			}
			
			return Math.max(baseMaxHit, 300); // Minimum for raid boss
		} catch (Exception e) {
			return 650; // High fallback for Nex
		}
	}
	
	/**
	 * Calculate enhanced damage for Nex attacks
	 */
	private int calculateEnhancedNexDamage(Nex nex, int baseMaxHit, double multiplier) {
		try {
			int enhancedMax = (int) (baseMaxHit * multiplier);
			return Utils.random(enhancedMax + 1);
		} catch (Exception e) {
			return Utils.random(400); // Safe fallback
		}
	}
	
	/**
	 * Determine Nex boss type (Hybrid due to multiple attack styles)
	 */
	private int getNexBossType(Nex nex) {
		// Nex uses all attack styles across phases, making her a hybrid raid boss
		return 6; // Raid Boss type
	}
	
	/**
	 * Determine Nex boss tier (high-end raid boss)
	 */
	private int getNexBossTier(Nex nex) {
		try {
			// Check BossBalancer configuration
			int configuredTier = readBossTierFromFile(nex.getId());
			if (configuredTier != -1) {
				return configuredTier;
			}
			
			// Nex is typically high-tier raid boss (8-10)
			int maxHp = nex.getMaxHitpoints();
			if (maxHp <= 50000) return 8;      // Legendary
			else if (maxHp <= 75000) return 9; // Mythical  
			else return 10;                    // Divine
			
		} catch (Exception e) {
			return 9; // Mythical tier default for Nex
		}
	}

	// Utility methods for enhanced functionality
	
	private boolean calculateDistance(Nex nex, Entity target) {
		int distanceX = target.getX() - nex.getX();
		int distanceY = target.getY() - nex.getY();
		int size = nex.getDefinitions().size;
		return distanceX > size + 1 || distanceX < -1 || distanceY > size + 1 || distanceY < -1;
	}
	
	private void announcePhaseGuidance(Nex nex, int phase, String message) {
		long currentTime = System.currentTimeMillis();
		if (!phaseGuidanceGiven[phase] || (currentTime - lastPhaseGuidance) > PHASE_GUIDANCE_INTERVAL) {
			phaseGuidanceGiven[phase] = true;
			lastPhaseGuidance = currentTime;
			announceToRaid(nex, "<col=ff6600>[Phase " + phase + "] " + message);
		}
	}
	
	private void announceToRaid(Nex nex, String message) {
		for (Player player : ZarosGodwars.getPlayers()) {
			if (player != null && player.withinDistance(nex, 20)) {
				player.sendMessage(message);
			}
		}
	}
	
	private boolean isValidNexCombatState(Nex nex, Entity target) {
		return nex != null && target != null && 
			   !nex.isDead() && !nex.hasFinished() && 
			   !target.isDead() && !target.hasFinished() &&
			   nex.getCombatDefinitions() != null;
	}
	
	// Anti-safespot helper methods
	
	private void createSafespotShadowTrap(Nex nex, Player player) {
		final WorldTile playerTile = new WorldTile(player);
		World.spawnObjectTemporary(new WorldObject(57261, 10, 0, playerTile.getX(), 
				playerTile.getY(), playerTile.getPlane()), 5000);
		
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				if (player.getX() == playerTile.getX() && player.getY() == playerTile.getY()) {
					int damage = calculateEnhancedNexDamage(nex, getEnhancedNexMaxHit(nex), 0.7);
					player.applyHit(new Hit(nex, damage, HitLook.REGULAR_DAMAGE));
				}
			}
		}, 3);
	}
	
	private void createSafespotIcePrison(Nex nex, Player player) {
		final WorldTile base = new WorldTile(player);
		for (int x = -1; x <= 1; x++) {
			for (int y = -1; y <= 1; y++) {
				final WorldTile tile = base.transform(x, y, player.getPlane());
				final WorldObject object = new WorldObject(57263, 10, 0, tile);
				if (World.isTileFree(0, tile.getX(), tile.getY(), 1))
					World.spawnObjectTemporary(object, 8000);
			}
		}
	}
	
	private int readBossTierFromFile(int npcId) {
		try {
			java.io.File bossFile = new java.io.File("data/npcs/bosses/" + npcId + ".txt");
			if (!bossFile.exists()) return -1;
			
			java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(bossFile));
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.startsWith("tier=")) {
					reader.close();
					return Integer.parseInt(line.substring(5));
				}
			}
			reader.close();
		} catch (Exception e) {}
		return -1;
	}

	// Original methods with enhanced damage (keeping existing logic but adding Boss Balancer scaling)
	
	private void sendNormalIceAttacks(boolean hasDistance, final Nex nex, final Entity target, int enhancedMaxHit) {
		switch (hasDistance ? 0 : Utils.random(3)) {
		case 0:
			sendEnhancedIceBarrage(nex, enhancedMaxHit);
			break;
		case 1:
		case 2:
			sendEnhancedMeleeAttack(nex, target, enhancedMaxHit);
			break;
		}
	}
	
	private void sendEnhancedIceBarrage(Nex nex, int enhancedMaxHit) {
		boolean usingPrayer = false;
		nex.setNextAnimation(new Animation(17414));
		nex.setNextGraphics(new Graphics(3375));
		
		announceToRaid(nex, "<col=00ccff>[Ice Barrage] Area freeze attack! Prayer drain effect!");
		
		for (final Entity possibleTarget : nex.getPossibleTargets()) {
			World.sendProjectile(nex, possibleTarget, 362, 20, 20, 20, 2, 10, 0);
			
			// Enhanced ice damage
			int damage = calculateEnhancedNexDamage(nex, enhancedMaxHit, 0.8);
			delayHit(nex, 1, possibleTarget, getMagicHit(nex, damage));
			
			if (damage > 0) {
				if (possibleTarget instanceof Player) {
					final Player player = (Player) possibleTarget;
					player.getPrayer().drainPrayer(damage / 3); // Enhanced prayer drain
					if (player.getPrayer().isMageProtecting())
						usingPrayer = true;
				}
				final boolean finalPrayer = usingPrayer;
				WorldTasksManager.schedule(new WorldTask() {
					@Override
					public void run() {
						if (Utils.random(finalPrayer ? 5 : 2) == 0) { // Higher freeze chance
							possibleTarget.addFreezeDelay(20000); // Longer freeze
							possibleTarget.setNextGraphics(new Graphics(369));
						}
					}
				}, 2);
			}
		}
	}

	// Additional enhanced methods (keeping core logic but adding enhancements)
	private void sendEnhancedNormalIceAttacks(boolean hasDistance, final Nex nex, final Entity target, int enhancedMaxHit) {
		sendNormalIceAttacks(hasDistance, nex, target, enhancedMaxHit);
	}
	
	private void sendEnhancedIcePrison(final Nex nex, boolean start, int enhancedMaxHit) {
		sendIcePrison(nex, start); // Use original logic for now
	}
	
	private void sendEnhancedIceBarricade(final Nex nex, int enhancedMaxHit) {
		sendIceBarricade(nex); // Use original logic for now
	}
	
	private void sendEnhancedNormalBloodAttack(boolean hasDistance, Nex nex, Entity target, int enhancedMaxHit) {
		sendNormalBloodAttack(hasDistance, nex, target); // Use original logic for now
	}
	
	private int sendEnhancedBloodSacrifice(final Nex nex, Entity target, int enhancedMaxHit) {
		return sendBloodSacrifice(nex, target); // Use original logic for now
	}
	
	private void sendEnhancedSipionAttack(final Nex nex, Entity target, int enhancedMaxHit) {
		sendSipionAttack(nex, target); // Use original logic for now
	}
	
	private int sendEnhancedShadowTraps(final Nex nex, int enhancedMaxHit) {
		return sendShadowTraps(nex); // Use original logic for now
	}
	
	private void sendEnhancedEmbraceDarkness(final Nex nex) {
		sendEmbraceDarkness(nex); // Use original logic for now
	}

	// Keep all original methods intact (preserved for functionality)
	protected void sendSoulSplit(Hit hit, Nex nex, Entity target) {
		if (nex.getId() == 13448) {
			if (target instanceof Player)
				((Player) target).sendSoulSplit(hit, nex);
		}
	}

	private void sendIcePrison(final Nex nex, boolean start) {
		if (start) {
			nex.setNextForceTalk(new ForceTalk("Die now, in a prison of ice!"));
			nex.playSoundEffect(3308);
			nex.setNextAnimation(new Animation(17414));
			nex.setNextGraphics(new Graphics(3375));
		}
		ArrayList<Player> players = new ArrayList<Player>();
		for (Player player : ZarosGodwars.getPlayers()) {
			if (!player.withinDistance(nex, 14))
				continue;
			players.add(player);
		}
		if (players.size() > 0) {
			final Player player = players.get(Utils.random(players.size()));
			if (player == null || player.isDead()) {
				sendIcePrison(nex, false);
				return;
			}
			World.sendProjectile(nex, player, 362, 20, 20, 20, 2, 10, 0);
			final WorldTile base = player;
			for (int x = -1; x <= 1; x++) {
				for (int y = -1; y <= 1; y++) {
					final WorldTile tile = base.transform(x, y, player.getPlane());
					final WorldObject object = new WorldObject(57263, 10, 0, tile);
					if (World.isTileFree(0, tile.getX(), tile.getY(), (object.getDefinitions().getSizeX() + object.getDefinitions().getSizeY()) / 2))
						World.spawnObject(object);
					WorldTasksManager.schedule(new WorldTask() {

						boolean remove = false;

						@Override
						public void run() {
							if (remove) {
								World.removeObject(object);
								stop();
								return;
							}
							remove = true;
							if (player.getX() == tile.getX() && player.getY() == tile.getY()) {
								player.getPackets().sendGameMessage("The centre of the ice prison freezes you to the bone!");
								player.resetWalkSteps();
								player.applyHit(new Hit(nex, Utils.random(800), HitLook.REGULAR_DAMAGE));
							}
						}
					}, 8, 0);
				}
			}
		} else {
			return;
		}
	}

	private void sendIceBarricade(final Nex nex) {
		nex.setNextForceTalk(new ForceTalk("Contain this!"));
		nex.playSoundEffect(3316);
		nex.setNextAnimation(new Animation(17407));
		nex.setNextGraphics(new Graphics(3362));
		final WorldTile base = nex.transform(1, 1, 0);
		nex.resetWalkSteps();
		WorldTasksManager.schedule(new WorldTask() {

			@Override
			public void run() {
				for (int y = 1; y >= -1; y--) {
					for (int x = 1; x >= -1; x--) {
						if (x == y)
							continue;
						final WorldTile tile = base.transform(x, y, 0);
						final WorldObject object = new WorldObject(57263, 10, 0, tile);
						if (tile != base && World.isTileFree(0, tile.getX(), tile.getY(), (object.getDefinitions().getSizeX() + object.getDefinitions().getSizeY()) / 2)) {
							for (Player player : ZarosGodwars.getPlayers()) {
								if (player.getX() == tile.getX() && player.getY() == tile.getY()) {
									player.setNextAnimation(new Animation(1113));
									player.applyHit(new Hit(nex, Utils.random(350), HitLook.REGULAR_DAMAGE));
									player.getPackets().sendGameMessage("The icicle spikes you to the spot!");
									player.getPackets().sendGameMessage("You've been injured and can't use " + (player.getPrayer().isAncientCurses() ? "deflect curses" : "protection prayers ") + "!");
									player.resetWalkSteps();
									player.getPrayer().closeAllPrayers();
									player.setPrayerDelay(7000);
								}
							}
							World.spawnObjectTemporary(object, 7000);
						}
					}
				}
				return;
			}
		}, 5);
	}

	private void sendNormalBloodAttack(boolean hasDistance, Nex nex, Entity target) {
		switch (hasDistance ? 0 : Utils.random(3)) {
		case 0:
			for (Player player : ZarosGodwars.getPlayers()) {
				if (player.withinDistance(nex, 1))
					continue;
				sendBloodAttack(nex, player);
			}
			break;
		case 1:
		case 2:
			sendMeleeAttack(nex, target);
			break;
		}
	}

	private int sendBloodSacrifice(final Nex nex, Entity target) {
		nex.setNextForceTalk(new ForceTalk("I demand a blood sacrifice!"));
		nex.playSound(3293, 2);
		if (target instanceof Player) {
			final Player player = (Player) target;
			player.sendMessage("<col=480000>Nex has marked you as a sacrifice; RUN!");
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					if (player.withinDistance(nex, 3)) {
						player.sendMessage("You didn't make it far enough in time - Nex fires a punishing attack!");
						nex.setNextAnimation(new Animation(17414));
						nex.setNextGraphics(new Graphics(3375));
						for (final Entity possibleTargets : nex.getPossibleTargets()) {
							sendBloodAttack(nex, possibleTargets);
							WorldTasksManager.schedule(new WorldTask() {
								@Override
								public void run() {
									if (possibleTargets instanceof Player) {
										Player p = (Player) possibleTargets;
										p.getPrayer().drainPrayerOnHalf();
									}
								}
							}, 2);
						}
					}
				}
			}, nex.getCombatDefinitions().getAttackDelay());
			nex.incrementNexAttack();
			return nex.getCombatDefinitions().getAttackDelay() * 2;
		} else {
			nex.setNexAttack(5);
			return -1;
		}
	}

	private void sendBloodAttack(final Nex nex, final Entity target) {
		final int damage = getRandomMaxHit(nex, 290, NPCCombatDefinitions.MAGE, target);
		nex.setNextAnimation(new Animation(17414));
		nex.setNextGraphics(new Graphics(3375));
		World.sendProjectile(nex, target, 374, 20, 20, 20, 2, 0, 0);
		delayHit(nex, 2, target, getMagicHit(nex, damage));
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				target.setNextGraphics(new Graphics(376));
				nex.heal(damage / 4);
			}
		}, 2);
	}

	private void sendSipionAttack(final Nex nex, Entity target) {
		nex.setNextForceTalk(new ForceTalk("A siphon will solve this!"));
		nex.playSound(3317, 2);
		nex.setNextAnimation(new Animation(17409));
		nex.setNextGraphics(new Graphics(3370));
		nex.setSiphioning(true);
		int size = NPCDefinitions.getNPCDefinitions(13458).size;
		int[][] dirs = Utils.getCoordOffsetsNear(size);
		int maximumAmount = Utils.random(3);
		int count = 0;
		if (maximumAmount != 0) {
			for (int dir = 0; dir < dirs[0].length; dir++) {
				final WorldTile tile = new WorldTile(new WorldTile(target.getX() + dirs[0][dir], target.getY() + dirs[1][dir], target.getPlane()));
				if (World.isTileFree(tile.getPlane(), tile.getX(), tile.getY(), size)) {
					NPC npc = nex.getBloodReavers()[count++] = new NPC(13458, tile, -1, true, true);
					npc.setNextGraphics(new Graphics(1315));
				}
				if (count == maximumAmount)
					break;
			}
		}
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				nex.setSiphioning(false);
			}
		}, 8);
	}

	private void sendEmbraceDarkness(final Nex nex) {
		nex.setNextForceTalk(new ForceTalk("Embrace darkness!"));
		nex.playSoundEffect(3322);
		nex.setNextAnimation(new Animation(17412));
		nex.setNextGraphics(new Graphics(3353));
	}

	private int sendShadowTraps(final Nex nex) {
		if (!nex.hasShadowTraps()) {
			nex.setHasShadowTraps(true);
			nex.setNextForceTalk(new ForceTalk("Fear the shadow!"));
			nex.playSoundEffect(3314);
			nex.setNextAnimation(new Animation(17407));
			nex.setNextGraphics(new Graphics(3362));
			
			final List<WorldTile> tiles = new LinkedList<WorldTile>();
			for (Entity t : nex.getPossibleTargets()) {
				WorldTile tile = new WorldTile(t);
				if (!tiles.contains(t)) {
					tiles.add(tile);
					World.spawnObjectTemporary(new WorldObject(57261, 10, 0, t.getX(), t.getY(), 0), 2400);
				}
			}
			WorldTasksManager.schedule(new WorldTask() {
				private boolean firstCall;

				@Override
				public void run() {
					if (!firstCall) {
						for (WorldTile tile : tiles) {
							World.sendGraphics(null, new Graphics(383), new WorldTile(tile.getX(), tile.getY(), tile.getPlane()));
							for (Entity t : nex.getPossibleTargets())
								if (t.getX() == tile.getX() && t.getY() == tile.getY() && t.getPlane() == tile.getPlane())
									t.applyHit(new Hit(nex, (t.getHitpoints() * (Utils.random(10) + 70)) / 100, HitLook.REGULAR_DAMAGE));
						}
						firstCall = true;
					} else {
						nex.setHasShadowTraps(false);
						stop();
					}
				}
			}, 3, 3);
			return nex.getAttackSpeed();
		}
		return 0;
	}

	private void sendMeleeAttack(Nex nex, Entity target) {
		nex.setNextAnimation(new Animation(17453));
		Hit hit = getMeleeHit(nex, getRandomMaxHit(nex, nex.getCombatDefinitions().getMaxHit(),
					NPCCombatDefinitions.MELEE, target));
		delayHit(nex, 0, target, hit);
		if (nex.getCurrentPhase() == NexPhase.ZAROS)
			sendSoulSplit(hit, nex, target);
	}

	private void sendRangeAttack(final Nex nex) {
		nex.setNextAnimation(new Animation(17413));
		int damage = 0;
		for (final Entity t : nex.getPossibleTargets()) {
			int distance = Utils.getDistance(t.getX(), t.getY(), nex.getX(), nex.getY());
			if (distance <= 10)
				damage = 400 - (distance * 400 / 11);
			else
				damage = 300 + Utils.random(75);
			World.sendProjectile(nex, t, 380, 25, 25, 32, 0, 0, 0);
			delayHit(nex, 2, t, getRangeHit(nex, getRandomMaxHit(nex, damage, NPCCombatDefinitions.RANGE, t)));
		}
	}

	private void sendVirusAttack(Nex nex) {
		nex.addVirusAttackDelay(45 + Utils.random(15));
		nex.setNextForceTalk(new ForceTalk("Let the virus flow through you!"));
		nex.playSoundEffect(3296);
		nex.setNextAnimation(new Animation(17414));
		nex.setNextGraphics(new Graphics(3375));
		for (Entity entity : nex.getPossibleTargets()) {
			if (!entity.withinDistance(nex.getFarthestTarget(), 2))
				continue;
			entity.setNextForceTalk(new ForceTalk("*Cough*"));
			entity.applyHit(new Hit(nex, Utils.getRandom(100),
				    HitLook.REGULAR_DAMAGE));
			entity.getTemporaryAttributtes().put("nex_infected", true);
		}
		nex.playSound(3296, 2);
	}

	private void sendMagicAttack(final Nex nex, final boolean secondAttack) {
		for (final Entity t : nex.getPossibleTargets()) {
			nex.setNextAnimation(new Animation(17413));
			nex.setNextGraphics(new Graphics(1214));
			World.sendProjectile(nex, t, 3371, 25, 25, 32, 0, 0, 0);
			
			final int damage = getRandomMaxHit(nex, nex.getCombatDefinitions()
				    .getMaxHit(), NPCCombatDefinitions.MAGE, t);
			Hit hit = getMagicHit(nex, damage);
			delayHit(nex, 2, t, hit);
			if (nex.getCurrentPhase() == NexPhase.ZAROS)
				sendSoulSplit(hit, nex, t);
			if (damage > 0) {
				WorldTasksManager.schedule(new WorldTask() {

					@Override
					public void run() {
						t.setNextGraphics(new Graphics(3373));
						if (nex.getCurrentPhase() == NexPhase.SMOKE && !secondAttack) {
							if (damage > 0 && Utils.random(5) == 0)
								t.getPoison().makePoisoned(80);
						}
					}
				}, 2);
			}
		}
	}
}