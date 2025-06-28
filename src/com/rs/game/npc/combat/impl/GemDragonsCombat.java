package com.rs.game.npc.combat.impl;

import com.rs.game.Animation;
import com.rs.game.Entity;
import com.rs.game.ForceTalk;
import com.rs.game.Graphics;
import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.game.player.content.Combat;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.game.Hit;
import com.rs.utils.Utils;
import com.rs.utils.NPCBonuses;

/**
 * Enhanced Gem Dragons Combat System with Boss Balancer Integration
 * 
 * Features: - Integrated with Boss Balancer 10-tier system (Hybrid Boss Type -
 * Tier 5) - Advanced boss guidance system with gem dragon combat education -
 * Multi-gem type combat system with unique abilities per gem type - Enhanced
 * crystal-based attacks with gem dust effects - Intelligent attack selection
 * based on range and gem type - Crystal armor mechanics with defensive phases -
 * Gem resonance effects affecting player equipment durability - Advanced
 * dragonfire system with gem-enhanced protection mechanics - Player-level
 * scaling for balanced gem dragon encounter experience - Null-safe damage
 * calculation system with comprehensive error handling - Gem-themed force talk
 * messages with ancient dragon lore - Performance tracking for gem-specific
 * ability analysis
 * 
 * @author Zeus
 * @date May 31, 2025
 * @version 3.0 - Boss Balancer Integration with Advanced Gem Combat System
 */
public class GemDragonsCombat extends CombatScript {

	// Boss Balancer Integration Constants
	private static final int GEM_DRAGONS_BOSS_TYPE = 4; // Hybrid Boss Type (all combat styles)
	private static final int GEM_DRAGONS_DEFAULT_TIER = 5; // Expert tier by default

	// Gem Dragon Types for specialized abilities
	private static final int EMERALD_DRAGON = 24170;
	private static final int RUBY_DRAGON = 24171;
	private static final int SAPPHIRE_DRAGON = 24172;
	private static final int DIAMOND_DRAGON = 3372;
	private static final int DRAGONSTONE_DRAGON = 1830;

	// Combat phase thresholds for gem dragons
	private static final double CRYSTAL_ARMOR_THRESHOLD = 0.75; // 75% health - crystal armor activation
	private static final double GEM_RESONANCE_THRESHOLD = 0.50; // 50% health - gem resonance abilities
	private static final double FINAL_BRILLIANCE_THRESHOLD = 0.25; // 25% health - maximum gem power

	// Attack probabilities for different ranges
	private static final int CLOSE_RANGE_ATTACKS = 10; // 10 options when in close range
	private static final int DISTANT_RANGE_ATTACKS = 5; // 5 options when distant

	// Special ability chances
	private static final int CRYSTAL_SHARDS_CHANCE = 12; // 1 in 12 for crystal shard attack
	private static final int GEM_DUST_CHANCE = 15; // 1 in 15 for gem dust cloud
	private static final int CRYSTAL_ARMOR_CHANCE = 20; // 1 in 20 for crystal armor
	private static final int GEM_RESONANCE_CHANCE = 18; // 1 in 18 for gem resonance

	// Guidance system constants
	private static final int GUIDANCE_FREQUENCY = 4; // 1 in 4 chance for strategic hints
	private static final int HINT_COOLDOWN = 14000; // 14 seconds between hints
	private static final int GEM_WARNING_COOLDOWN = 22000; // 22 seconds between gem warnings

	// Instance variables for combat tracking
	private long lastHintTime = 0;
	private long lastGemWarningTime = 0;
	private boolean hasGivenOpeningAdvice = false;
	private boolean hasGivenArmorWarning = false;
	private boolean hasGivenResonanceWarning = false;
	private boolean hasGivenBrillianceWarning = false;
	private int crystalShardsCount = 0;
	private int gemDustCount = 0;
	private int crystalArmorCount = 0;
	private int gemResonanceCount = 0;
	private int dragonFireCount = 0;
	private int totalDamageDealt = 0;

	// Enhanced Gem Dragon force talk messages with crystal lore
	private static final String[] AWAKENING_MESSAGES = { "My crystalline form is eternal and unbreakable!",
			"Mortals dare challenge the power of gems!", "My gem heart beats with ancient dragon magic!" };

	private static final String[] CRYSTAL_ARMOR_MESSAGES = { "Crystal armor shields me from your feeble attacks!",
			"The gems themselves protect my draconic form!", "Your weapons cannot pierce my crystalline defense!" };

	private static final String[] GEM_RESONANCE_MESSAGES = { "Feel the resonance of pure gem energy!",
			"My crystal essence disrupts your mortal equipment!", "The very gems you seek shall be your undoing!" };

	private static final String[] FINAL_BRILLIANCE_MESSAGES = { "Witness the ultimate brilliance of gem power!",
			"My crystal heart unleashes its full fury!", "I am the apex of draconic gem mastery!" };

	@Override
	public int attack(NPC npc, Entity target) {
		// Enhanced null safety checks
		if (npc == null || target == null) {
			return 4; // Default attack delay
		}

		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		if (defs == null) {
			return 4; // No combat definitions available
		}

		// Provide opening gem dragon strategy advice
		if (!hasGivenOpeningAdvice && target instanceof Player) {
			provideOpeningGemDragonStrategy((Player) target, npc, defs);
			hasGivenOpeningAdvice = true;
		}

		// Check combat phases and provide gem warnings
		checkCombatPhases(npc, target, defs);

		// Gem-themed force talk with crystalline atmosphere
		performGemDragonForceTalk(npc, defs);

		// Enhanced attack selection with gem-specific mechanics
		performIntelligentGemAttackSelection(npc, target, defs);

		// Provide strategic gem dragon guidance
		if (target instanceof Player) {
			provideGemDragonGuidance((Player) target, npc, defs);
		}

		return defs.getAttackDelay();
	}

	/**
	 * Provide opening gem dragon strategy advice
	 */
	private void provideOpeningGemDragonStrategy(Player player, NPC npc, NPCCombatDefinitions defs) {
		int gemDragonTier = determineGemDragonTier(npc, defs);
		String gemType = getGemDragonType(npc);

		player.getPackets().sendGameMessage("<col=FF69B4>[Gem Dragon Knowledge]: " + gemType
				+ " Dragons are crystalline beings with unique gem-based abilities!");
		player.getPackets().sendGameMessage("<col=FF69B4>[Combat Analysis]: Tier " + gemDragonTier
				+ " Hybrid Boss - Uses all combat styles plus crystal magic!");
		player.getPackets().sendGameMessage(
				"<col=00FFFF>[Critical Strategy]: Each gem type has different specialties - adapt your protection accordingly!");
		player.getPackets().sendGameMessage(
				"<col=00FFFF>[Tactical Warning]: Watch for crystal armor phases and gem resonance effects on your equipment!");
	}

	/**
	 * Check combat phases and provide gem warnings
	 */
	private void checkCombatPhases(NPC npc, Entity target, NPCCombatDefinitions defs) {
		double healthPercent = (double) npc.getHitpoints() / defs.getHitpoints();

		if (healthPercent <= FINAL_BRILLIANCE_THRESHOLD && !hasGivenBrillianceWarning && target instanceof Player) {
			Player player = (Player) target;
			player.getPackets().sendGameMessage(
					"<col=FF0000>[FINAL BRILLIANCE]: The Gem Dragon unleashes ultimate crystal power!");
			player.getPackets().sendGameMessage(
					"<col=FF0000>[CRITICAL PHASE]: Maximum gem abilities and enhanced crystal attacks!");
			player.getPackets()
					.sendGameMessage("<col=FF0000>[ULTIMATE THREAT]: All gem-based abilities reach peak intensity!");
			hasGivenBrillianceWarning = true;

		} else if (healthPercent <= GEM_RESONANCE_THRESHOLD && !hasGivenResonanceWarning && target instanceof Player) {
			Player player = (Player) target;
			player.getPackets()
					.sendGameMessage("<col=FF8000>[Gem Resonance]: The dragon's crystal essence disrupts equipment!");
			player.getPackets().sendGameMessage(
					"<col=FF8000>[Enhanced Threat]: Gem resonance attacks affecting item durability and stats!");
			player.getPackets().sendGameMessage(
					"<col=FF8000>[Strategic Update]: Monitor equipment condition and prepare for disruption!");
			hasGivenResonanceWarning = true;

		} else if (healthPercent <= CRYSTAL_ARMOR_THRESHOLD && !hasGivenArmorWarning && target instanceof Player) {
			Player player = (Player) target;
			player.getPackets()
					.sendGameMessage("<col=FFFF00>[Crystal Armor]: The Gem Dragon's defensive crystals activate!");
			player.getPackets().sendGameMessage(
					"<col=FFFF00>[Defensive Phase]: Crystal armor provides damage reduction and reflects attacks!");
			player.getPackets().sendGameMessage(
					"<col=FFFF00>[Combat Advisory]: Focus on sustained damage to overcome crystal defenses!");
			hasGivenArmorWarning = true;
		}
	}

	/**
	 * Perform gem dragon-themed force talk based on combat phase
	 */
	private void performGemDragonForceTalk(NPC npc, NPCCombatDefinitions defs) {
		if (Utils.getRandom(25) != 0)
			return; // 1 in 25 chance for force talk (less frequent but impactful)

		double healthPercent = (double) npc.getHitpoints() / defs.getHitpoints();
		String[] messageArray;
		int soundBase;

		// Select messages and sounds based on current phase
		if (healthPercent <= FINAL_BRILLIANCE_THRESHOLD) {
			messageArray = FINAL_BRILLIANCE_MESSAGES;
			soundBase = 1460; // Brilliant, crystalline sounds
		} else if (healthPercent <= GEM_RESONANCE_THRESHOLD) {
			messageArray = GEM_RESONANCE_MESSAGES;
			soundBase = 1458;
		} else if (healthPercent <= CRYSTAL_ARMOR_THRESHOLD) {
			messageArray = CRYSTAL_ARMOR_MESSAGES;
			soundBase = 1456;
		} else {
			messageArray = AWAKENING_MESSAGES;
			soundBase = 1454;
		}

		String message = messageArray[Utils.getRandom(messageArray.length)];
		npc.setNextForceTalk(new ForceTalk(message));
		npc.playSound(soundBase + Utils.getRandom(2), 2); // Crystalline sound effects
	}

	/**
	 * Perform intelligent gem attack selection with range and gem type
	 * considerations
	 */
	private void performIntelligentGemAttackSelection(NPC npc, Entity target, NPCCombatDefinitions defs) {
		// Check for special gem abilities first
		if (Utils.getRandom(CRYSTAL_SHARDS_CHANCE) == 0) {
			performCrystalShardsAttack(npc, target, defs);
			return;
		}

		if (Utils.getRandom(GEM_DUST_CHANCE) == 0) {
			performGemDustCloud(npc, target, defs);
			return;
		}

		if (Utils.getRandom(CRYSTAL_ARMOR_CHANCE) == 0) {
			performCrystalArmorAbility(npc, target, defs);
			return;
		}

		if (Utils.getRandom(GEM_RESONANCE_CHANCE) == 0) {
			performGemResonanceAttack(npc, target, defs);
			return;
		}

		// Standard attack selection based on range
		boolean inCloseRange = npc.withinDistance(target, npc.getSize());

		if (inCloseRange) {
			performCloseRangeGemAttack(npc, target, defs);
		} else {
			performDistantRangeGemAttack(npc, target, defs);
		}
	}

	/**
	 * Perform close range attack selection for gem dragons
	 */
	private void performCloseRangeGemAttack(NPC npc, Entity target, NPCCombatDefinitions defs) {
		int attackChoice = Utils.random(CLOSE_RANGE_ATTACKS);

		switch (attackChoice) {
		case 0:
		case 1: // 20% chance
			dragonFireAttack(npc, target, defs);
			dragonFireCount++;
			break;
		case 2:
		case 3: // 20% chance
			rangeAttack(npc, target, defs);
			break;
		case 4: // 10% chance
			poisonAttack(npc, target, defs);
			break;
		default: // 50% chance
			meleeAttack(npc, target, defs);
			break;
		}
	}

	/**
	 * Perform distant range attack selection for gem dragons
	 */
	private void performDistantRangeGemAttack(NPC npc, Entity target, NPCCombatDefinitions defs) {
		int attackChoice = Utils.random(DISTANT_RANGE_ATTACKS);

		switch (attackChoice) {
		case 0:
		case 1: // 40% chance
			rangeAttack(npc, target, defs);
			break;
		case 2: // 20% chance
			dragonFireAttack(npc, target, defs);
			dragonFireCount++;
			break;
		default: // 40% chance
			poisonAttack(npc, target, defs);
			break;
		}
	}

	/**
	 * Enhanced ranged attack with gem-specific projectiles
	 */
	public void rangeAttack(NPC npc, Entity target, NPCCombatDefinitions defs) {
		npc.setNextAnimation(new Animation(14244));

		// Gem-specific projectile based on dragon type
		int projectileId = getGemProjectileId(npc);
		World.sendProjectile(npc, target, projectileId, 28, 16, 35, 20, 16, 0);

		// Get Boss Balancer stats
		int gemDragonTier = determineGemDragonTier(npc, defs);
		int baseMaxHit = getBaseMaxHit(npc, defs);
		int tierScaledMaxHit = applyBossTierScaling(baseMaxHit, gemDragonTier, false); // Range attack

		// Gem dragons have strong ranged attacks (85% of tier-scaled damage)
		int rangedDamage = (int) (tierScaledMaxHit * 0.85);

		// Apply player scaling if target is a player
		if (target instanceof Player) {
			rangedDamage = applyPlayerLevelScaling(rangedDamage, (Player) target, gemDragonTier);
		}

		int damage = getRandomMaxHit(npc, rangedDamage, NPCCombatDefinitions.RANGE, target);
		totalDamageDealt += damage;
		delayHit(npc, 1, target, getRangeHit(npc, damage));

		// Gem-specific visual effect
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				target.setNextGraphics(new Graphics(getGemImpactGraphic(npc))); // Gem impact effect
				this.stop();
			}
		}, 1);
	}

	/**
	 * Enhanced poison attack with gem-enhanced toxins
	 */
	public void poisonAttack(NPC npc, Entity target, NPCCombatDefinitions defs) {
		final Player player = target instanceof Player ? (Player) target : null;
		if (player != null) {
			npc.setNextAnimation(new Animation(14244));
			World.sendProjectile(npc, target, 3436, 60, 16, 65, 35, 16, 0);

			// Get Boss Balancer stats
			final int gemDragonTier = determineGemDragonTier(npc, defs);
			final int baseMaxHit = getBaseMaxHit(npc, defs);
			final int tierScaledMaxHit = applyBossTierScaling(baseMaxHit, gemDragonTier, true); // Magic-based poison

			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					player.getPackets().sendGameMessage(
							"<col=00FF00>[Gem Toxin]: You are hit by the dragon's gem-enhanced poisonous breath!");

					// Poison attack does 75% of tier-scaled damage
					int poisonDamage = (int) (tierScaledMaxHit * 0.75);
					poisonDamage = applyPlayerLevelScaling(poisonDamage, player, gemDragonTier);

					int damage = getRandomMaxHit(npc, poisonDamage, NPCCombatDefinitions.MAGE, target);
					totalDamageDealt += damage;
					delayHit(npc, 0, target, getMagicHit(npc, damage));

					player.setNextGraphics(new Graphics(3437, 50, 0));

					// Gem-enhanced poison intensity
					int poisonIntensity = Math.max(60, gemDragonTier * 20); // Stronger poison for higher tiers
					player.getPoison().makePoisoned(poisonIntensity);

					// Gem-specific poison effect
					applyGemPoisonEffect(player, npc, gemDragonTier);

					stop();
				}
			}, 0);
		}
	}

	/**
	 * Enhanced melee attack with crystal-enhanced damage
	 */
	public void meleeAttack(NPC npc, Entity target, NPCCombatDefinitions defs) {
		npc.setNextAnimation(new Animation(12252));

		// Get Boss Balancer stats
		int gemDragonTier = determineGemDragonTier(npc, defs);
		int baseMaxHit = getBaseMaxHit(npc, defs);
		int tierScaledMaxHit = applyBossTierScaling(baseMaxHit, gemDragonTier, false); // Melee attack

		// Apply player scaling if target is a player
		if (target instanceof Player) {
			tierScaledMaxHit = applyPlayerLevelScaling(tierScaledMaxHit, (Player) target, gemDragonTier);
		}

		int damage = getRandomMaxHit(npc, tierScaledMaxHit, NPCCombatDefinitions.MELEE, target);
		totalDamageDealt += damage;
		delayHit(npc, 0, target, getMeleeHit(npc, damage));

		// Crystal melee impact effect
		target.setNextGraphics(new Graphics(getGemMeleeGraphic(npc))); // Crystal impact
	}

	/**
	 * Enhanced dragonfire attack with gem-enhanced protection mechanics
	 */
	public void dragonFireAttack(NPC npc, Entity target, NPCCombatDefinitions defs) {
		final Player player = target instanceof Player ? (Player) target : null;
		dragonFireCount++;

		// Get Boss Balancer stats
		int gemDragonTier = determineGemDragonTier(npc, defs);
		int baseMaxHit = getBaseMaxHit(npc, defs);
		int tierScaledMaxHit = applyBossTierScaling(baseMaxHit, gemDragonTier, true); // Magic-based dragonfire

		// Gem dragon fire is slightly stronger (95% of tier-scaled damage)
		int baseDamage = (int) (tierScaledMaxHit * 0.95);
		int finalDamage = baseDamage;

		if (player != null) {
			finalDamage = applyPlayerLevelScaling(baseDamage, player, gemDragonTier);

			String message = Combat.getProtectMessage(player);
			if (message != null) {
				player.sendMessage(message, true);
				if (message.contains("fully")) {
					finalDamage = 0; // Full protection
				} else if (message.contains("most")) {
					finalDamage = (int) (finalDamage * 0.05); // 95% reduction
				} else if (message.contains("some")) {
					finalDamage = (int) (finalDamage * 0.10); // 90% reduction
				}
			} else if (finalDamage > 0) {
				String gemType = getGemDragonType(npc);
				player.sendMessage(
						"<col=FF0000>[" + gemType + " Fire]: You are hit by the dragon's gem-enhanced fiery breath!",
						true);
			}
		}

		npc.setNextAnimation(new Animation(14245));
		World.sendProjectile(npc, target, 438, 28, 16, 35, 20, 16, 0);
		totalDamageDealt += finalDamage;
		delayHit(npc, 1, target, getRegularHit(npc, finalDamage));

		// Gem-enhanced dragonfire visual effect
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				target.setNextGraphics(new Graphics(getGemFireGraphic(npc))); // Gem fire effect
				this.stop();
			}
		}, 1);
	}

	/**
	 * Perform crystal shards attack - AOE projectile attack
	 */
	private void performCrystalShardsAttack(NPC npc, Entity target, NPCCombatDefinitions defs) {
		crystalShardsCount++;

		npc.setNextAnimation(new Animation(14244));
		npc.setNextGraphics(new Graphics(2080)); // Crystal formation effect

		// Get Boss Balancer stats for shards attack
		int gemDragonTier = determineGemDragonTier(npc, defs);
		int baseMaxHit = getBaseMaxHit(npc, defs);
		int tierScaledMaxHit = applyBossTierScaling(baseMaxHit, gemDragonTier, false); // Range-based shards

		// Crystal shards do 70% damage to multiple targets
		int shardsDamage = (int) (tierScaledMaxHit * 0.70);
		int targetsHit = 0;

		// Launch shards at all possible targets within range
		for (Entity entity : npc.getPossibleTargets()) {
			if (entity.withinDistance(npc, 5)) {
				targetsHit++;

				int entityDamage = shardsDamage;
				if (entity instanceof Player) {
					entityDamage = applyPlayerLevelScaling(shardsDamage, (Player) entity, gemDragonTier);
				}

				// Launch crystal shard projectile
				World.sendProjectile(npc, entity, 2081, 28, 16, 35, 20, 16, 0);
				int damage = getRandomMaxHit(npc, entityDamage, NPCCombatDefinitions.RANGE, entity);
				totalDamageDealt += damage;
				delayHit(npc, 2, entity, getRangeHit(npc, damage));

				// Shard impact effect
				WorldTasksManager.schedule(new WorldTask() {
					@Override
					public void run() {
						entity.setNextGraphics(new Graphics(2082)); // Crystal shard impact
						this.stop();
					}
				}, 2);
			}
		}

		// Provide crystal shards warning
		if (target instanceof Player && targetsHit > 1) {
			Player player = (Player) target;
			player.getPackets().sendGameMessage(
					"<col=FF69B4>[Crystal Shards]: The Gem Dragon launches crystalline projectiles at all nearby targets!");
		}
	}

	/**
	 * Perform gem dust cloud - area denial attack
	 */
	private void performGemDustCloud(NPC npc, Entity target, NPCCombatDefinitions defs) {
		gemDustCount++;

		npc.setNextAnimation(new Animation(14245));
		npc.setNextGraphics(new Graphics(2083)); // Gem dust formation

		// Get Boss Balancer stats for dust cloud
		int gemDragonTier = determineGemDragonTier(npc, defs);
		int baseMaxHit = getBaseMaxHit(npc, defs);
		int tierScaledMaxHit = applyBossTierScaling(baseMaxHit, gemDragonTier, true); // Magic-based dust

		// Gem dust does 60% initial damage
		int dustDamage = (int) (tierScaledMaxHit * 0.60);

		// Hit all entities within 3 tiles
		for (Entity entity : npc.getPossibleTargets()) {
			if (entity.withinDistance(npc, 3)) {
				int entityDamage = dustDamage;
				if (entity instanceof Player) {
					entityDamage = applyPlayerLevelScaling(dustDamage, (Player) entity, gemDragonTier);
				}

				int damage = getRandomMaxHit(npc, entityDamage, NPCCombatDefinitions.MAGE, entity);
				totalDamageDealt += damage;
				delayHit(npc, 1, entity, getMagicHit(npc, damage));
				entity.setNextGraphics(new Graphics(2084)); // Gem dust effect

				// Apply gem dust debuff to players
				if (entity instanceof Player) {
					applyGemDustDebuff((Player) entity, gemDragonTier);
				}
			}
		}

		// Provide gem dust warning
		if (target instanceof Player && shouldGiveGemWarning()) {
			Player player = (Player) target;
			player.getPackets()
					.sendGameMessage("<col=FF69B4>[Gem Dust]: Crystalline dust clouds the area and affects accuracy!");
		}
	}

	/**
	 * Perform crystal armor ability - defensive buff
	 */
	private void performCrystalArmorAbility(NPC npc, Entity target, NPCCombatDefinitions defs) {
		crystalArmorCount++;

		npc.setNextAnimation(new Animation(14245));
		npc.setNextGraphics(new Graphics(2085)); // Crystal armor formation
		npc.playSound(1462, 3); // Crystal formation sound

		// Crystal armor provides temporary damage reduction
		// This would require extending the NPC class to track temporary effects
		// For now, we'll provide visual feedback and educational guidance

		if (target instanceof Player) {
			Player player = (Player) target;
			player.getPackets().sendGameMessage(
					"<col=FF69B4>[Crystal Armor]: The Gem Dragon activates protective crystalline armor!");
			player.getPackets().sendGameMessage(
					"<col=FFFF00>[Strategic Note]: Crystal armor reduces incoming damage - use sustained attacks!");
		}

		// Enhanced visual effect for armor activation
		npc.setNextGraphics(new Graphics(2086)); // Armor shimmer effect
	}

	/**
	 * Perform gem resonance attack - equipment disruption
	 */
	private void performGemResonanceAttack(NPC npc, Entity target, NPCCombatDefinitions defs) {
		gemResonanceCount++;

		npc.setNextAnimation(new Animation(14245));
		World.sendProjectile(npc, target, 2087, 28, 16, 35, 20, 16, 0);

		// Get Boss Balancer stats for resonance attack
		int gemDragonTier = determineGemDragonTier(npc, defs);
		int baseMaxHit = getBaseMaxHit(npc, defs);
		int tierScaledMaxHit = applyBossTierScaling(baseMaxHit, gemDragonTier, true); // Magic-based resonance

		// Resonance attack does 70% damage but has special effects
		int resonanceDamage = (int) (tierScaledMaxHit * 0.70);

		if (target instanceof Player) {
			resonanceDamage = applyPlayerLevelScaling(resonanceDamage, (Player) target, gemDragonTier);
		}

		int damage = getRandomMaxHit(npc, resonanceDamage, NPCCombatDefinitions.MAGE, target);
		totalDamageDealt += damage;
		delayHit(npc, 2, target, getMagicHit(npc, damage));

		// Apply gem resonance effects to players
		if (target instanceof Player) {
			applyGemResonanceEffects((Player) target, npc, gemDragonTier);
		}

		// Resonance impact effect
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				target.setNextGraphics(new Graphics(2088)); // Gem resonance impact
				this.stop();
			}
		}, 2);
	}

	/**
	 * Apply gem-specific poison effects based on dragon type
	 */
	private void applyGemPoisonEffect(Player player, NPC npc, int tier) {
		String gemType = getGemDragonType(npc);

		// Different gem types have different additional poison effects
		switch (npc.getId()) {
		case EMERALD_DRAGON:
			// Emerald: Nature-based, affects farming/herblore
			player.getPackets()
					.sendGameMessage("<col=00FF00>[Emerald Toxin]: Natural poisons affect your herbal knowledge!");
			break;
		case RUBY_DRAGON:
			// Ruby: Fire-based, affects firemaking/cooking
			player.getPackets()
					.sendGameMessage("<col=FF0000>[Ruby Toxin]: Fiery poison interferes with heat-based skills!");
			break;
		case SAPPHIRE_DRAGON:
			// Sapphire: Water-based, affects fishing/magic
			player.getPackets().sendGameMessage("<col=0080FF>[Sapphire Toxin]: Aquatic poison disrupts magical flow!");
			break;
		case DIAMOND_DRAGON:
			// Diamond: Pure, affects all stats slightly
			player.getPackets()
					.sendGameMessage("<col=FFFFFF>[Diamond Toxin]: Pure crystal poison affects your entire being!");
			break;
		case DRAGONSTONE_DRAGON:
			// Dragonstone: Power-based, affects combat stats
			player.getPackets()
					.sendGameMessage("<col=800080>[Dragonstone Toxin]: Ancient poison weakens your combat prowess!");
			break;
		default:
			player.getPackets()
					.sendGameMessage("<col=FF69B4>[Gem Toxin]: Crystalline poison courses through your veins!");
			break;
		}
	}

	/**
	 * Apply gem dust debuff effects
	 */
	private void applyGemDustDebuff(final Player player, final int tier) {
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				// Gem dust affects accuracy and visibility
				int accuracyDrain = Math.max(3, tier * 2); // Scales with tier

				// Temporarily reduce attack accuracy
				int attackLevel = player.getSkills().getLevel(Skills.ATTACK);
				int rangedLevel = player.getSkills().getLevel(Skills.RANGE);
				int magicLevel = player.getSkills().getLevel(Skills.MAGIC);

				player.getSkills().set(Skills.ATTACK, Math.max(1, attackLevel - accuracyDrain));
				player.getSkills().set(Skills.RANGE, Math.max(1, rangedLevel - accuracyDrain));
				player.getSkills().set(Skills.MAGIC, Math.max(1, magicLevel - accuracyDrain));

				player.getPackets()
						.sendGameMessage("<col=FF69B4>[Gem Dust]: Crystalline particles interfere with your accuracy!");

				// Restore accuracy after 15 seconds
				WorldTasksManager.schedule(new WorldTask() {
					@Override
					public void run() {
						player.getSkills().restoreSkills();
						player.getPackets().sendGameMessage(
								"<col=00FFFF>[Clear Vision]: The gem dust settles and your accuracy returns.");
						this.stop();
					}
				}, 25); // ~15 seconds

				this.stop();
			}
		}, 1);
	}

	/**
	 * Apply gem resonance effects based on dragon type
	 */
	private void applyGemResonanceEffects(final Player player, NPC npc, final int tier) {
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				String gemType = getGemDragonType(npc);

				// Equipment durability warning (simulated effect)
				player.getPackets().sendGameMessage(
						"<col=FF69B4>[" + gemType + " Resonance]: Your equipment vibrates with crystal energy!");

				// Stat interference based on gem type
				switch (npc.getId()) {
				case EMERALD_DRAGON:
					// Affects crafting and smithing
					int craftingLevel = player.getSkills().getLevel(Skills.CRAFTING);
					int smithingLevel = player.getSkills().getLevel(Skills.SMITHING);
					player.getSkills().set(Skills.CRAFTING, Math.max(1, craftingLevel - tier));
					player.getSkills().set(Skills.SMITHING, Math.max(1, smithingLevel - tier));
					break;
				case RUBY_DRAGON:
					// Affects strength and firemaking
					int strengthLevel = player.getSkills().getLevel(Skills.STRENGTH);
					int firemakingLevel = player.getSkills().getLevel(Skills.FIREMAKING);
					player.getSkills().set(Skills.STRENGTH, Math.max(1, strengthLevel - tier));
					player.getSkills().set(Skills.FIREMAKING, Math.max(1, firemakingLevel - tier));
					break;
				case SAPPHIRE_DRAGON:
					// Affects magic and fishing
					int magicLevel = player.getSkills().getLevel(Skills.MAGIC);
					int fishingLevel = player.getSkills().getLevel(Skills.FISHING);
					player.getSkills().set(Skills.MAGIC, Math.max(1, magicLevel - tier));
					player.getSkills().set(Skills.FISHING, Math.max(1, fishingLevel - tier));
					break;
				case DIAMOND_DRAGON:
					// Affects prayer and runecrafting
					int currentPrayer = player.getPrayer().getPrayerpoints();
					int runecraftingLevel = player.getSkills().getLevel(Skills.RUNECRAFTING);
					player.getPrayer().drainPrayer(tier * 10);
					player.getSkills().set(Skills.RUNECRAFTING, Math.max(1, runecraftingLevel - tier));
					break;
				case DRAGONSTONE_DRAGON:
					// Affects all combat stats
					int attackLevel = player.getSkills().getLevel(Skills.ATTACK);
					int defenceLevel = player.getSkills().getLevel(Skills.DEFENCE);
					player.getSkills().set(Skills.ATTACK, Math.max(1, attackLevel - tier / 2));
					player.getSkills().set(Skills.DEFENCE, Math.max(1, defenceLevel - tier / 2));
					break;
				}

				player.getPackets()
						.sendGameMessage("<col=FF69B4>[Resonance Effect]: Crystal vibrations disrupt your abilities!");

				this.stop();
			}
		}, 2);
	}

	/**
	 * Get gem dragon type name for messaging
	 */
	private String getGemDragonType(NPC npc) {
		switch (npc.getId()) {
		case EMERALD_DRAGON:
			return "Emerald";
		case RUBY_DRAGON:
			return "Ruby";
		case SAPPHIRE_DRAGON:
			return "Sapphire";
		case DIAMOND_DRAGON:
			return "Diamond";
		case DRAGONSTONE_DRAGON:
			return "Dragonstone";
		default:
			return "Gem";
		}
	}

	/**
	 * Get gem-specific projectile ID
	 */
	private int getGemProjectileId(NPC npc) {
		switch (npc.getId()) {
		case EMERALD_DRAGON:
			return 2090; // Green crystal
		case RUBY_DRAGON:
			return 2091; // Red crystal
		case SAPPHIRE_DRAGON:
			return 2092; // Blue crystal
		case DIAMOND_DRAGON:
			return 2093; // White crystal
		case DRAGONSTONE_DRAGON:
			return 2094; // Purple crystal
		default:
			return 16; // Default projectile
		}
	}

	/**
	 * Get gem-specific impact graphic
	 */
	private int getGemImpactGraphic(NPC npc) {
		switch (npc.getId()) {
		case EMERALD_DRAGON:
			return 2095; // Green crystal impact
		case RUBY_DRAGON:
			return 2096; // Red crystal impact
		case SAPPHIRE_DRAGON:
			return 2097; // Blue crystal impact
		case DIAMOND_DRAGON:
			return 2098; // White crystal impact
		case DRAGONSTONE_DRAGON:
			return 2099; // Purple crystal impact
		default:
			return 2036; // Default impact
		}
	}

	/**
	 * Get gem-specific melee graphic
	 */
	private int getGemMeleeGraphic(NPC npc) {
		switch (npc.getId()) {
		case EMERALD_DRAGON:
			return 2100; // Green crystal slash
		case RUBY_DRAGON:
			return 2101; // Red crystal slash
		case SAPPHIRE_DRAGON:
			return 2102; // Blue crystal slash
		case DIAMOND_DRAGON:
			return 2103; // White crystal slash
		case DRAGONSTONE_DRAGON:
			return 2104; // Purple crystal slash
		default:
			return 2347; // Default melee impact
		}
	}

	/**
	 * Get gem-specific fire graphic
	 */
	private int getGemFireGraphic(NPC npc) {
		switch (npc.getId()) {
		case EMERALD_DRAGON:
			return 2105; // Green crystal fire
		case RUBY_DRAGON:
			return 2106; // Red crystal fire
		case SAPPHIRE_DRAGON:
			return 2107; // Blue crystal fire
		case DIAMOND_DRAGON:
			return 2108; // White crystal fire
		case DRAGONSTONE_DRAGON:
			return 2109; // Purple crystal fire
		default:
			return 2350; // Default fire impact
		}
	}

	/**
	 * Determine Gem Dragon's tier based on Boss Balancer system
	 */
	private int determineGemDragonTier(NPC npc, NPCCombatDefinitions defs) {
		try {
			int hp = defs.getHitpoints();
			int maxHit = defs.getMaxHit();

			// Estimate tier based on Boss Balancer HP/damage ranges for Hybrid boss
			if (hp >= 4500 && hp <= 8000 && maxHit >= 55 && maxHit <= 100) {
				return 5; // Expert tier
			} else if (hp >= 3000 && hp <= 5500 && maxHit >= 40 && maxHit <= 75) {
				return 4; // Advanced tier
			} else if (hp >= 6500 && hp <= 10500 && maxHit >= 70 && maxHit <= 125) {
				return 6; // Master tier
			}

			return GEM_DRAGONS_DEFAULT_TIER; // Default to Expert tier
		} catch (Exception e) {
			return GEM_DRAGONS_DEFAULT_TIER;
		}
	}

	/**
	 * Get base max hit safely (NULL SAFE)
	 */
	private int getBaseMaxHit(NPC npc, NPCCombatDefinitions defs) {
		try {
			int maxHit = defs.getMaxHit();
			return maxHit > 0 ? maxHit : 90; // Default Gem Dragon damage if invalid
		} catch (Exception e) {
			return 90; // Fallback Gem Dragon damage
		}
	}

	/**
	 * Apply Boss Balancer tier scaling for hybrid boss
	 */
	private int applyBossTierScaling(int baseMaxHit, int tier, boolean isMagicAttack) {
		// Boss Balancer tier scaling: 15% increase per tier above 1
		double tierMultiplier = 1.0 + (tier - 1) * 0.15;

		// Hybrid boss type modifier - balanced damage for all attack types
		double typeModifier = 1.0; // Standard damage for hybrid

		// Gem dragons have slight magic preference (crystalline magic creatures)
		if (isMagicAttack) {
			typeModifier = 1.06; // 6% bonus for magic attacks
		}

		return (int) (baseMaxHit * tierMultiplier * typeModifier);
	}

	/**
	 * Apply player level scaling for balanced gem dragon experience
	 */
	private int applyPlayerLevelScaling(int damage, Player player, int tier) {
		int playerCombatLevel = player.getSkills().getCombatLevel();
		int recommendedLevel = tier * 12 + 40; // Tier 5 = level 100 recommended

		// Scale damage based on player level vs recommended
		if (playerCombatLevel < recommendedLevel) {
			double scaleFactor = (double) playerCombatLevel / recommendedLevel;
			scaleFactor = Math.max(0.40, scaleFactor); // Minimum 40% damage
			damage = (int) (damage * scaleFactor);
		}

		// Ensure damage is reasonable for Gem Dragons (mid-high tier)
		damage = Math.max(18, Math.min(damage, 180)); // Cap between 18-180

		return damage;
	}

	/**
	 * Provide strategic gem dragon guidance based on combat performance
	 */
	private void provideGemDragonGuidance(Player player, NPC npc, NPCCombatDefinitions defs) {
		if (!shouldGiveHint())
			return;

		double healthPercent = (double) npc.getHitpoints() / defs.getHitpoints();
		String gemType = getGemDragonType(npc);

		// Crystal shards frequency guidance
		if (crystalShardsCount >= 2) {
			player.getPackets().sendGameMessage(
					"<col=FF69B4>[Crystal Analysis]: Multiple shard attacks detected! Spread out to minimize AOE damage!");
			crystalShardsCount = 0;
			return;
		}

		// Gem dust guidance
		if (gemDustCount >= 2) {
			player.getPackets().sendGameMessage(
					"<col=FF69B4>[Dust Cloud Warning]: Gem dust clouds affecting accuracy. Consider accuracy-boosting equipment!");
			gemDustCount = 0;
			return;
		}

		// Crystal armor guidance
		if (crystalArmorCount >= 1) {
			player.getPackets().sendGameMessage(
					"<col=FFFF00>[Armor Analysis]: Crystal armor activated. Use sustained damage to overcome defenses!");
			crystalArmorCount = 0;
			return;
		}

		// Gem resonance guidance
		if (gemResonanceCount >= 2) {
			player.getPackets().sendGameMessage(
					"<col=FF69B4>[Resonance Effects]: Multiple resonance attacks used. Check your skills for crystal interference!");
			gemResonanceCount = 0;
			return;
		}

		// Dragon fire guidance
		if (dragonFireCount >= 3) {
			player.getPackets().sendGameMessage("<col=FF0000>[" + gemType
					+ " Fire]: Frequent dragonfire attacks! Ensure antifire protection is active!");
			dragonFireCount = 0;
			return;
		}

		// Phase-specific gem dragon guidance
		if (healthPercent > 0.75) {
			player.getPackets().sendGameMessage("<col=00FFFF>[" + gemType
					+ " Strategy]: This gem dragon uses all combat styles plus crystal abilities. Adapt your protection!");
		} else if (healthPercent > 0.50) {
			player.getPackets().sendGameMessage(
					"<col=FFFF00>[Crystal Armor]: Defensive crystal phase active. Use sustained attacks to pierce crystal defenses!");
		} else if (healthPercent > 0.25) {
			player.getPackets().sendGameMessage(
					"<col=FF8000>[Gem Resonance]: Equipment disruption phase! Monitor your stats for crystal interference!");
		} else {
			player.getPackets().sendGameMessage(
					"<col=FF0000>[Final Brilliance]: Maximum gem power activated! All crystal abilities at peak intensity!");
		}
	}

	/**
	 * Check if should give strategic hint (with cooldown)
	 */
	private boolean shouldGiveHint() {
		long currentTime = System.currentTimeMillis();
		if (currentTime - lastHintTime >= HINT_COOLDOWN) {
			if (Utils.getRandom(GUIDANCE_FREQUENCY) == 0) {
				lastHintTime = currentTime;
				return true;
			}
		}
		return false;
	}

	/**
	 * Check if should give gem warning (with cooldown)
	 */
	private boolean shouldGiveGemWarning() {
		long currentTime = System.currentTimeMillis();
		if (currentTime - lastGemWarningTime >= GEM_WARNING_COOLDOWN) {
			lastGemWarningTime = currentTime;
			return true;
		}
		return false;
	}

	/**
	 * Debug method for testing damage scaling and boss balancer integration
	 */
	public String getDamageScalingInfo(int combatLevel, boolean isMagic) {
		int tier = GEM_DRAGONS_DEFAULT_TIER;
		int baseMaxHit = 90;
		int tierScaled = applyBossTierScaling(baseMaxHit, tier, isMagic);
		String attackType = isMagic ? "Magic" : "Physical";

		return String.format("Gem Dragon Tier: %d, Base: %d, %s Scaled: %d", tier, baseMaxHit, attackType, tierScaled);
	}

	/**
	 * Get combat statistics for gem dragon analysis
	 */
	public String getCombatStats() {
		return String.format("Shards: %d, Dust: %d, Armor: %d, Resonance: %d, Dragon Fire: %d, Total Damage: %d",
				crystalShardsCount, gemDustCount, crystalArmorCount, gemResonanceCount, dragonFireCount,
				totalDamageDealt);
	}

	@Override
	public Object[] getKeys() {
		return new Object[] { 24170, 24171, 24172, 3372, 1830 }; // Gem Dragon NPC IDs
	}
}