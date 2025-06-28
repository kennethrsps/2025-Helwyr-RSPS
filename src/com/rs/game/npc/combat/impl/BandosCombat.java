package com.rs.game.npc.combat.impl;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

import com.rs.game.Animation;
import com.rs.game.Entity;
import com.rs.game.Graphics;
import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.game.player.Player;
import com.rs.utils.Utils;
import com.rs.utils.NPCBonuses;
import com.rs.utils.Logger;

/**
 * Enhanced Bandos Combat - MULTI-STYLE WAR GOD WITH BATTLE GUIDANCE
 * 
 * @author Zeus
 * @date May 31, 2025
 * @version 2.2 - FIXED Null References + Enhanced Battle Guidance Features:
 *          Tier Integration, Multi-Style Education, Distance-Based Combat
 */
public class BandosCombat extends CombatScript {

	// Enhanced education system for multi-style combat mechanics
	private static final Map<String, Long> playerLastCombatTip = new ConcurrentHashMap<String, Long>();
	private static final Map<String, Integer> playerCombatTipStage = new ConcurrentHashMap<String, Integer>();
	private static final Map<String, Long> playerLastGuidance = new ConcurrentHashMap<String, Long>();
	private static final Map<Integer, Long> bossLastTierAnnouncement = new ConcurrentHashMap<Integer, Long>();
	private static final Map<String, Integer> playerCombatStage = new ConcurrentHashMap<String, Integer>();

	private static final long COMBAT_TIP_COOLDOWN = 35000; // 35 seconds between combat tips
	private static final long GUIDANCE_COOLDOWN = 40000; // 40 seconds between guidance messages
	private static final long TIER_ANNOUNCEMENT_COOLDOWN = 300000; // 5 minutes between tier announcements
	private static final int MAX_COMBAT_TIPS_PER_FIGHT = 5; // More tips for complex multi-style boss

	// Boss stats cache for multi-style war god
	private static final Map<Integer, WarGodBossStats> warGodStatsCache = new ConcurrentHashMap<Integer, WarGodBossStats>();

	@Override
	public int attack(NPC npc, Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();

		// Get balanced war god boss stats
		WarGodBossStats bossStats = getBalancedWarGodStats(npc);

		// Announce tier at the beginning (visible to all players)
		announceBossTier(npc, bossStats);

		// Provide dynamic battle guidance
		if (target instanceof Player) {
			provideDynamicBattleGuidance((Player) target, npc, bossStats);
		}

		// Distance-based attack selection with tier-aware probabilities
		if (npc.withinDistance(target, npc.getSize())) {
			executeCloseRangeAttack(npc, target, bossStats);
		} else {
			executeLongRangeAttack(npc, target, bossStats);
		}

		return defs.getAttackDelay();
	}

	/**
	 * Provide dynamic battle guidance based on combat situation
	 */
	private void provideDynamicBattleGuidance(Player player, NPC npc, WarGodBossStats bossStats) {
		String username = player.getUsername();
		long currentTime = System.currentTimeMillis();

		// Check guidance cooldown
		Long lastGuidance = playerLastGuidance.get(username);
		if (lastGuidance != null && (currentTime - lastGuidance) < GUIDANCE_COOLDOWN) {
			return;
		}

		// Get combat stage
		Integer combatStage = playerCombatStage.get(username);
		if (combatStage == null)
			combatStage = 0;

		// Calculate boss health percentage
		int currentHp = npc.getHitpoints();
		int maxHp = bossStats.hitpoints;
		double hpPercentage = (double) currentHp / maxHp;

		String guidanceMessage = null;

		// Phase-based guidance
		if (combatStage == 0) {
			// Opening phase
			guidanceMessage = getBossOpeningGuidance(bossStats);
			playerCombatStage.put(username, 1);
		} else if (hpPercentage <= 0.75 && combatStage == 1) {
			// 75% HP phase
			guidanceMessage = getBossPhaseGuidance(bossStats, "75%");
			playerCombatStage.put(username, 2);
		} else if (hpPercentage <= 0.50 && combatStage == 2) {
			// 50% HP phase
			guidanceMessage = getBossPhaseGuidance(bossStats, "50%");
			playerCombatStage.put(username, 3);
		} else if (hpPercentage <= 0.25 && combatStage == 3) {
			// 25% HP phase - urgent warnings
			guidanceMessage = getBossPhaseGuidance(bossStats, "25%");
			playerCombatStage.put(username, 4);
		} else if (hpPercentage <= 0.10 && combatStage == 4) {
			// Final phase - critical warnings
			guidanceMessage = getBossPhaseGuidance(bossStats, "10%");
			playerCombatStage.put(username, 5);
		}

		// Send guidance message as NPC speech
		if (guidanceMessage != null) {
			sendBossGuidance(player, npc, guidanceMessage, bossStats);
			playerLastGuidance.put(username, currentTime);
		}
	}

	/**
	 * Send boss guidance as NPC speech
	 */
	private void sendBossGuidance(Player player, NPC npc, String message, WarGodBossStats bossStats) {
		// Send as both NPC dialogue and colored message
		String tierPrefix = getTierPrefix(bossStats.tier);
		String fullMessage = tierPrefix + " Graardor: " + message;

		// Send as overhead text from the war god
		player.sendMessage("<col=8B4513>" + fullMessage + "</col>", true);

		// For high tier bosses, add additional warning formatting
		if (bossStats.tier >= 8) {
			player.sendMessage("<col=FF1493>>>> LEGENDARY WAR DEITY <<<</col>", true);
		}
	}

	/**
	 * Get boss opening guidance
	 */
	private String getBossOpeningGuidance(WarGodBossStats bossStats) {
		if (bossStats.tier <= 3) {
			return "You face a war captain in battle. Prepare for multi-style combat!";
		} else if (bossStats.tier <= 6) {
			return "I am a general of war! You will learn the meaning of tactical combat!";
		} else if (bossStats.tier <= 8) {
			return "An elite war god awakens! I master all forms of combat!";
		} else {
			return "I AM THE DIVINE GOD OF WAR! ALL COMBAT STYLES BOW TO MY MIGHT!";
		}
	}

	/**
	 * Get boss phase guidance
	 */
	private String getBossPhaseGuidance(WarGodBossStats bossStats, String phase) {
		switch (phase) {
		case "75%":
			if (bossStats.tier <= 5) {
				return "You fight with honor, but my war tactics are superior!";
			} else {
				return "Impressive combat skills, but I am just getting started!";
			}

		case "50%":
			if (bossStats.tier <= 5) {
				return "Now you witness the true art of war! Adapt or perish!";
			} else {
				return "My multi-style mastery will overwhelm you completely!";
			}

		case "25%":
			if (bossStats.tier <= 5) {
				return "My combat prowess reaches its peak! Feel my wrath!";
			} else {
				return "You have awakened my divine fury! Prepare for devastation!";
			}

		case "10%":
			if (bossStats.tier <= 5) {
				return "Impossible! But I will not yield in honorable combat!";
			} else {
				return "I AM THE ETERNAL WAR GOD! COMBAT ITSELF CANNOT DEFEAT ME!";
			}
		}
		return null;
	}

	/**
	 * Get tier prefix for messages
	 */
	private String getTierPrefix(int tier) {
		if (tier <= 3)
			return "War Captain";
		else if (tier <= 5)
			return "War General";
		else if (tier <= 7)
			return "Elite War God";
		else if (tier <= 9)
			return "Legendary War Deity";
		else
			return "Divine War God";
	}

	/**
	 * Announce boss tier to all players in the area
	 */
	private void announceBossTier(NPC npc, WarGodBossStats bossStats) {
		int npcId = npc.getId();
		long currentTime = System.currentTimeMillis();

		// Check if we recently announced for this boss instance
		Long lastAnnouncement = bossLastTierAnnouncement.get(npcId + npc.hashCode());
		if (lastAnnouncement != null && (currentTime - lastAnnouncement) < TIER_ANNOUNCEMENT_COOLDOWN) {
			return; // Too soon for another announcement
		}

		// Find all nearby players for the announcement
		for (Entity entity : npc.getPossibleTargets()) {
			if (entity instanceof Player) {
				Player player = (Player) entity;

				// Simple tier announcement
				String tierName = getBossTierName(bossStats.tier);
				String balanceStatus = bossStats.isBalanced ? "Balanced" : "Estimated";

				player.sendMessage("<col=8B4513>General Graardor storms the battlefield! " + tierName + " ("
						+ balanceStatus + ")</col>", true);

				// Additional warning for high tiers
				if (bossStats.tier >= 7) {
					player.sendMessage("<col=FF6B35>Warning: Elite war god with devastating multi-style combat!</col>",
							true);
				}
			}
		}

		// Update announcement tracking
		bossLastTierAnnouncement.put(npcId + npc.hashCode(), currentTime);
	}

	/**
	 * Get balanced war god boss stats with caching - FIXED to read actual balanced
	 * tier
	 */
	private WarGodBossStats getBalancedWarGodStats(NPC npc) {
		int npcId = npc.getId();

		// Check cache first
		WarGodBossStats cached = warGodStatsCache.get(npcId);
		if (cached != null && System.currentTimeMillis() - cached.timestamp < 300000) { // 5 min cache
			return cached;
		}

		WarGodBossStats stats = new WarGodBossStats();

		try {
			// Try to read tier from boss files first
			stats.tier = readTierFromBossFile(npcId);

			// If no boss file found, estimate from combat stats
			if (stats.tier == -1) {
				stats.tier = estimateWarGodTierFromStats(npc.getCombatDefinitions());
			}

			stats.maxHit = npc.getCombatDefinitions().getMaxHit();
			stats.hitpoints = npc.getCombatDefinitions().getHitpoints();

			// Get balanced bonuses from NPCBonuses system
			int[] bonuses = NPCBonuses.getBonuses(npcId);
			if (bonuses != null && bonuses.length >= 10) {
				stats.attackBonuses = new int[] { bonuses[0], bonuses[1], bonuses[2], bonuses[3], bonuses[4] };
				stats.defenseBonuses = new int[] { bonuses[5], bonuses[6], bonuses[7], bonuses[8], bonuses[9] };
				stats.maxBonus = getMaxBonus(bonuses);
				stats.isBalanced = true;
			} else {
				// Fallback: estimate war god appropriate bonuses
				stats.attackBonuses = estimateWarGodAttackBonuses(stats.tier);
				stats.defenseBonuses = estimateWarGodDefenseBonuses(stats.tier);
				stats.maxBonus = getMaxBonus(stats.attackBonuses);
				stats.isBalanced = false;
			}

			// Calculate war god specific stats (multi-style combat)
			stats.meleeMaxHit = calculateWarGodDamage(stats.maxHit, stats.attackBonuses[1], 1.0); // Slash-based
			stats.meleeKickMaxHit = calculateWarGodDamage(stats.maxHit, stats.attackBonuses[2], 1.4); // Crush kick
																										// (enhanced)
			stats.magicMaxHit = calculateWarGodDamage(stats.maxHit, stats.attackBonuses[3], 1.2); // Magic enhanced
			stats.rangedMaxHit = calculateWarGodDamage(stats.maxHit, stats.attackBonuses[4], 1.1); // Ranged slightly
																									// enhanced

			// Tier-based attack probabilities
			stats.kickAttackChance = Math.min(25, 5 + (stats.tier * 2)); // 7% at tier 1, 25% at tier 10
			stats.magicAttackChance = Math.min(30, 10 + (stats.tier * 2)); // 12% at tier 1, 30% at tier 10
			stats.multiHitChance = Math.min(40, 15 + (stats.tier * 2)); // 17% at tier 1, 40% at tier 10

			stats.timestamp = System.currentTimeMillis();
			warGodStatsCache.put(npcId, stats);

		} catch (Exception e) {
			Logger.handle(e);
			// Safe fallback values for war god boss
			stats.tier = 6; // Bandos is typically high-tier
			stats.maxHit = 300;
			stats.meleeMaxHit = 300;
			stats.meleeKickMaxHit = 420; // 40% enhanced
			stats.magicMaxHit = 360; // 20% enhanced
			stats.rangedMaxHit = 330; // 10% enhanced
			stats.hitpoints = 15000;
			stats.attackBonuses = new int[] { 600, 800, 750, 650, 700 }; // Melee focused
			stats.defenseBonuses = new int[] { 700, 700, 700, 600, 650 };
			stats.maxBonus = 800;
			stats.kickAttackChance = 17;
			stats.magicAttackChance = 22;
			stats.multiHitChance = 27;
			stats.isBalanced = false;
		}

		return stats;
	}

	/**
	 * Read tier from boss file created by BossBalancer
	 */
	private int readTierFromBossFile(int npcId) {
		try {
			java.io.File bossFile = new java.io.File("data/npcs/bosses/" + npcId + ".txt");
			if (!bossFile.exists()) {
				return -1; // No boss file found
			}

			java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(bossFile));
			String line;

			while ((line = reader.readLine()) != null) {
				if (line.startsWith("tier=")) {
					reader.close();
					return Integer.parseInt(line.substring(5));
				}
			}
			reader.close();

		} catch (Exception e) {
			// File reading failed, will fall back to estimation
		}

		return -1; // Couldn't read tier from file
	}

	/**
	 * Execute close range attack selection with tier-aware probabilities
	 */
	private void executeCloseRangeAttack(NPC npc, Entity target, WarGodBossStats bossStats) {
		int attackRoll = Utils.random(100);

		if (attackRoll < bossStats.kickAttackChance) {
			executeEnhancedMeleeKickAttack(npc, target, bossStats);
		} else if (attackRoll < bossStats.kickAttackChance + bossStats.magicAttackChance) {
			executeEnhancedMageAttack(npc, target, bossStats);
		} else {
			executeEnhancedMeleeAttack(npc, target, bossStats);
		}
	}

	/**
	 * Execute long range attack selection with tier-aware probabilities
	 */
	private void executeLongRangeAttack(NPC npc, Entity target, WarGodBossStats bossStats) {
		int attackRoll = Utils.random(100);

		if (attackRoll < 40) {
			executeEnhancedMageAttack(npc, target, bossStats);
		} else if (attackRoll < 70) {
			executeEnhancedRangeAttack(npc, target, bossStats);
		} else {
			// Close-in melee attack
			executeEnhancedMeleeAttack(npc, target, bossStats);
		}
	}

	/**
	 * Execute enhanced melee attack with balanced damage - FIXED NULL REFERENCE
	 */
	private void executeEnhancedMeleeAttack(NPC npc, Entity target, WarGodBossStats bossStats) {
		npc.setNextAnimation(new Animation(10961));

		// FIXED: Calculate balanced melee damage with proper NPC reference
		int meleeDamage = calculateBalancedMeleeDamage(npc, bossStats, target);

		World.sendGraphics(npc, new Graphics(373), target);

		// Multi-hit chance for higher tiers
		if (Utils.random(100) < bossStats.multiHitChance) {
			// Double hit for higher tier bosses
			delayHit(npc, 1, target, getMeleeHit(npc, meleeDamage));
			delayHit(npc, 2, target, getMeleeHit(npc, (int) (meleeDamage * 0.8))); // Second hit slightly weaker

			// Provide education about multi-hit
			if (target instanceof Player && Utils.random(5) == 0) { // 20% chance
				provideMultiHitEducation((Player) target, npc, bossStats);
			}
		} else {
			// Single hit
			delayHit(npc, 1, target, getMeleeHit(npc, meleeDamage));
		}

		// Provide melee education
		if (target instanceof Player && Utils.random(6) == 0) { // 16% chance
			provideCombatEducation((Player) target, npc, "MELEE", bossStats);
		}
	}

	/**
	 * Execute enhanced melee kick attack with balanced damage - FIXED NULL
	 * REFERENCE
	 */
	private void executeEnhancedMeleeKickAttack(NPC npc, Entity target, WarGodBossStats bossStats) {
		npc.setNextAnimation(new Animation(27250));

		// FIXED: Calculate balanced kick damage with proper NPC reference
		int kickDamage = calculateBalancedKickDamage(npc, bossStats, target);

		World.sendGraphics(npc, new Graphics(373), target);
		delayHit(npc, 1, target, getMeleeHit(npc, kickDamage));

		// Provide kick education
		if (target instanceof Player && Utils.random(4) == 0) { // 25% chance
			provideCombatEducation((Player) target, npc, "KICK", bossStats);
		}
	}

	/**
	 * Execute enhanced magic attack with balanced damage - FIXED NULL REFERENCE
	 */
	private void executeEnhancedMageAttack(NPC npc, Entity target, WarGodBossStats bossStats) {
		npc.setNextAnimation(new Animation(18364));
		World.sendGraphics(npc, new Graphics(3564), npc);
		World.sendProjectile(npc, target, 3565, 41, 16, 41, 35, 16, 0);

		// FIXED: Calculate balanced magic damage with proper NPC reference
		int magicDamage = calculateBalancedMagicDamage(npc, bossStats, target);

		delayHit(npc, 0, target, getMagicHit(npc, magicDamage));

		// Provide magic education
		if (target instanceof Player && Utils.random(5) == 0) { // 20% chance
			provideCombatEducation((Player) target, npc, "MAGIC", bossStats);
		}
	}

	/**
	 * Execute enhanced ranged attack with balanced damage - FIXED NULL REFERENCE
	 */
	private void executeEnhancedRangeAttack(NPC npc, Entity target, WarGodBossStats bossStats) {
		npc.setNextAnimation(new Animation(29006));
		World.sendProjectile(npc, target, 6000, 41, 16, 41, 35, 16, 0);

		// FIXED: Calculate balanced ranged damage with proper NPC reference
		int rangedDamage = calculateBalancedRangedDamage(npc, bossStats, target);

		delayHit(npc, 0, target, getRangeHit(npc, rangedDamage));

		// Provide ranged education
		if (target instanceof Player && Utils.random(5) == 0) { // 20% chance
			provideCombatEducation((Player) target, npc, "RANGED", bossStats);
		}
	}

	/**
	 * FIXED: Calculate balanced melee damage with proper NPC reference
	 */
	private int calculateBalancedMeleeDamage(NPC npc, WarGodBossStats bossStats, Entity target) {
		// FIXED: Pass the actual NPC instead of null
		int baseDamage = getRandomMaxHit(npc, bossStats.meleeMaxHit, NPCCombatDefinitions.MELEE, target);

		// Apply protection calculations if target is player
		if (target instanceof Player) {
			Player player = (Player) target;
			if (player.getPrayer().usingPrayer(0, 18) || player.getPrayer().usingPrayer(1, 8)) {
				baseDamage = (int) (baseDamage * 0.6); // 40% reduction with protect from melee
			}
		}

		return Math.max(1, baseDamage);
	}

	/**
	 * FIXED: Calculate balanced kick damage with proper NPC reference
	 */
	private int calculateBalancedKickDamage(NPC npc, WarGodBossStats bossStats, Entity target) {
		// FIXED: Pass the actual NPC instead of null
		int baseDamage = getRandomMaxHit(npc, bossStats.meleeKickMaxHit, NPCCombatDefinitions.MELEE, target);

		// Apply protection calculations if target is player
		if (target instanceof Player) {
			Player player = (Player) target;
			if (player.getPrayer().usingPrayer(0, 18) || player.getPrayer().usingPrayer(1, 8)) {
				baseDamage = (int) (baseDamage * 0.6); // 40% reduction with protect from melee
			}
		}

		return Math.max(1, baseDamage);
	}

	/**
	 * FIXED: Calculate balanced magic damage with proper NPC reference
	 */
	private int calculateBalancedMagicDamage(NPC npc, WarGodBossStats bossStats, Entity target) {
		// FIXED: Pass the actual NPC instead of null
		int baseDamage = getRandomMaxHit(npc, bossStats.magicMaxHit, NPCCombatDefinitions.MAGE, target);

		// Apply protection calculations if target is player
		if (target instanceof Player) {
			Player player = (Player) target;
			if (player.getPrayer().usingPrayer(0, 17) || player.getPrayer().usingPrayer(1, 7)) {
				baseDamage = (int) (baseDamage * 0.6); // 40% reduction with protect from magic
			}
		}

		return Math.max(1, baseDamage);
	}

	/**
	 * FIXED: Calculate balanced ranged damage with proper NPC reference
	 */
	private int calculateBalancedRangedDamage(NPC npc, WarGodBossStats bossStats, Entity target) {
		// FIXED: Pass the actual NPC instead of null
		int baseDamage = getRandomMaxHit(npc, bossStats.rangedMaxHit, NPCCombatDefinitions.RANGE, target);

		// Apply protection calculations if target is player
		if (target instanceof Player) {
			Player player = (Player) target;
			if (player.getPrayer().usingPrayer(0, 16) || player.getPrayer().usingPrayer(1, 6)) {
				baseDamage = (int) (baseDamage * 0.6); // 40% reduction with protect from missiles
			}
		}

		return Math.max(1, baseDamage);
	}

	/**
	 * Calculate war god damage based on attack bonus and modifier
	 */
	private int calculateWarGodDamage(int baseMaxHit, int attackBonus, double modifier) {
		int damage = (int) (baseMaxHit * modifier);

		// Apply attack bonus scaling
		if (attackBonus > 0) {
			damage = (int) (damage * (1.0 + (attackBonus * 0.0008))); // 0.08% per bonus point
		}

		return Math.max(1, damage);
	}

	/**
	 * Provide combat education based on attack style
	 */
	private void provideCombatEducation(Player player, NPC npc, String attackStyle, WarGodBossStats bossStats) {
		String username = player.getUsername();
		long currentTime = System.currentTimeMillis();

		// Check cooldown
		Long lastTip = playerLastCombatTip.get(username);
		if (lastTip != null && (currentTime - lastTip) < COMBAT_TIP_COOLDOWN) {
			return;
		}

		// Check tip stage
		Integer tipStage = playerCombatTipStage.get(username);
		if (tipStage == null)
			tipStage = 0;
		if (tipStage >= MAX_COMBAT_TIPS_PER_FIGHT)
			return;

		String tipMessage = getCombatTip(bossStats.tier, attackStyle, tipStage);
		if (tipMessage != null) {
			player.sendMessage("<col=8B4513>War Tactics: " + tipMessage + "</col>", true);

			playerLastCombatTip.put(username, currentTime);
			playerCombatTipStage.put(username, tipStage + 1);
		}
	}

	/**
	 * Provide multi-hit education
	 */
	private void provideMultiHitEducation(Player player, NPC npc, WarGodBossStats bossStats) {
		String tipMessage = "Graardor strikes multiple times! Higher tiers have increased multi-hit chance.";
		player.sendMessage("<col=8B4513>War Tactics: " + tipMessage + "</col>", true);
	}

	/**
	 * Get combat tip based on tier, attack style, and stage
	 */
	private String getCombatTip(int tier, String attackStyle, int stage) {
		if (stage == 0) {
			if (attackStyle.equals("MELEE")) {
				if (tier <= 4) {
					return "Graardor uses powerful melee attacks! Use Protect from Melee when close.";
				} else {
					return "Elite Graardor's melee hits devastatingly hard! Prayer and armor essential.";
				}
			} else if (attackStyle.equals("KICK")) {
				if (tier <= 4) {
					return "Graardor's kick attack deals enhanced damage! Watch for the animation.";
				} else {
					return "Elite Graardor's kick is devastating! This enhanced melee bypasses defenses.";
				}
			} else if (attackStyle.equals("MAGIC")) {
				if (tier <= 4) {
					return "Graardor uses magic attacks at range! Switch to Protect from Magic.";
				} else {
					return "Elite Graardor's magic is overwhelming! Use magic defense gear.";
				}
			} else if (attackStyle.equals("RANGED")) {
				return "Graardor uses ranged attacks at distance! Protect from Missiles helps.";
			}
		} else if (stage == 1) {
			return "Graardor adapts combat style by distance. Stay close for melee, distant for magic/ranged.";
		} else if (stage == 2) {
			return "Higher tier Graardor uses multiple combat styles more frequently. Quick prayer switching!";
		} else if (stage == 3) {
			return "Graardor can multi-hit with melee attacks. Higher tiers have increased double strike chance.";
		} else if (stage == 4) {
			return "War god mastery: Graardor excels at all combat styles. Be ready to adapt constantly!";
		}
		return null;
	}

	/**
	 * Get boss tier name for announcements
	 */
	private String getBossTierName(int tier) {
		switch (tier) {
		case 1:
			return "Tier 1 War Captain";
		case 2:
			return "Tier 2 War Captain";
		case 3:
			return "Tier 3 Battle Commander";
		case 4:
			return "Tier 4 War General";
		case 5:
			return "Tier 5 Elite General";
		case 6:
			return "Tier 6 War God Avatar";
		case 7:
			return "Tier 7 Elite War God";
		case 8:
			return "Tier 8 Legendary War Master";
		case 9:
			return "Tier 9 Mythical War Deity";
		case 10:
			return "Tier 10 Divine God of War";
		default:
			return "Unknown Tier War God";
		}
	}

	/**
	 * Estimate war god tier from combat stats
	 */
	private int estimateWarGodTierFromStats(NPCCombatDefinitions defs) {
		int hp = defs.getHitpoints();
		int maxHit = defs.getMaxHit();

		// War god bosses typically mid-high tier
		if (hp <= 3200 && maxHit <= 50)
			return 3; // Battle Commander
		if (hp <= 6000 && maxHit <= 80)
			return 4; // War General
		if (hp <= 10500 && maxHit <= 125)
			return 5; // Elite General
		if (hp <= 17000 && maxHit <= 185)
			return 6; // War God Avatar (typical Bandos)
		if (hp <= 25500 && maxHit <= 260)
			return 7; // Elite War God
		if (hp <= 36000 && maxHit <= 350)
			return 8; // Legendary War Master
		if (hp <= 50000 && maxHit <= 460)
			return 9; // Mythical War Deity
		return 10; // Divine God of War
	}

	/**
	 * Estimate war god attack bonuses for tier
	 */
	private int[] estimateWarGodAttackBonuses(int tier) {
		int[] tierMins = { 10, 80, 150, 250, 400, 600, 850, 1150, 1500, 1900 };
		int[] tierMaxs = { 75, 145, 240, 390, 590, 840, 1140, 1490, 1890, 2500 };

		int baseStat = (tierMins[tier - 1] + tierMaxs[tier - 1]) / 2;

		// War god bosses have enhanced melee bonuses, good all-around combat
		return new int[] { baseStat, // stab
				(int) (baseStat * 1.3), // slash (primary melee - enhanced)
				(int) (baseStat * 1.2), // crush (kick attacks - enhanced)
				baseStat, // magic (balanced)
				(int) (baseStat * 1.1) // ranged (slightly enhanced)
		};
	}

	/**
	 * Estimate war god defense bonuses for tier
	 */
	private int[] estimateWarGodDefenseBonuses(int tier) {
		int[] tierMins = { 10, 80, 150, 250, 400, 600, 850, 1150, 1500, 1900 };
		int[] tierMaxs = { 75, 145, 240, 390, 590, 840, 1140, 1490, 1890, 2500 };

		int baseStat = (tierMins[tier - 1] + tierMaxs[tier - 1]) / 2;

		// War god bosses have strong overall defenses
		return new int[] { baseStat, baseStat, baseStat, baseStat, baseStat };
	}

	/**
	 * Get maximum bonus from array
	 */
	private int getMaxBonus(int[] bonuses) {
		int max = 0;
		for (int bonus : bonuses) {
			if (bonus > max)
				max = bonus;
		}
		return max;
	}

	@Override
	public Object[] getKeys() {
		return new Object[] { 25125, 18506 }; // Bandos NPC IDs
	}

	/**
	 * War God Boss stats container class
	 */
	private static class WarGodBossStats {
		public int tier;
		public int maxHit;
		public int meleeMaxHit;
		public int meleeKickMaxHit;
		public int magicMaxHit;
		public int rangedMaxHit;
		public int hitpoints;
		public int[] attackBonuses;
		public int[] defenseBonuses;
		public int maxBonus;
		public int kickAttackChance;
		public int magicAttackChance;
		public int multiHitChance;
		public boolean isBalanced;
		public long timestamp;
	}
}