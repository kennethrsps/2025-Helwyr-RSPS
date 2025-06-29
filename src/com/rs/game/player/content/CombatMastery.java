package com.rs.game.player.content;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Random;
import java.util.EnumSet;

import com.rs.cache.loaders.NPCDefinitions;
import com.rs.game.World;
import com.rs.game.WorldTile;
import com.rs.game.npc.NPC;
import com.rs.game.player.Player;
import com.rs.utils.Colors;

import java.util.Map;
import java.io.*;

/**
 * Enhanced NPC-Specific Combat Mastery System with Special Effects
 * Players gain accuracy bonuses and special combat abilities against specific monsters
 * Each NPC type has separate mastery progression with unique special effects
 * 
 * @author Zeus
 * @date June 10, 2025
 * @version 4.1 - Clean Production Version
 */
public class CombatMastery {
    
    private static final Map<String, CombatMasteryData> playerMasteryData = new ConcurrentHashMap<>();
    
    // Configuration constants
    private static final int MAX_MASTERY_PER_NPC = 100;
    private static final double BASE_MASTERY_PER_KILL = 1.0;
    private static final double ACCURACY_PER_MASTERY = 1.0;
    private static final double DAMAGE_PER_MASTERY = 0.5;
    
    // Mastery level thresholds
    private static final int NOVICE_THRESHOLD = 10;
    private static final int EXPERIENCED_THRESHOLD = 25;
    private static final int EXPERT_THRESHOLD = 50;
    private static final int MASTER_THRESHOLD = 75;
    private static final int GRANDMASTER_THRESHOLD = 100;
    
    private static final Random random = new Random();
    
    /**
     * Special Effects Available
     */
    public enum SpecialEffect {
        // Tier 1 (Novice - 10+ mastery)
        LUCKY_STRIKE,        // 3% chance for +50% experience
        SWIFT_RECOVERY,      // +25% HP regeneration while fighting this NPC
        
        // Tier 2 (Experienced - 25+ mastery)
        DOUBLE_ATTACK,       // 5% chance for double attack
        RESOURCE_HUNTER,     // 5% chance for double drops
        
        // Tier 3 (Expert - 50+ mastery)  
        PRECISION_STRIKE,    // 15% chance to always hit
        ARMOR_PIERCING,      // 10% chance to ignore enemy defense
        
        // Tier 4 (Master - 75+ mastery)
        EXECUTION,           // 2% chance for instant kill at low HP
        LIFE_STEAL,          // 8% chance to heal for 25% damage dealt
        
        // Tier 5 (Grandmaster - 100+ mastery)
        EVASION_MASTERY,     // 15% chance to completely avoid damage
        INTIMIDATION         // 5% chance to cause enemy to miss next 3 attacks
    }
    
    /**
     * Attack Result class for special effects
     */
    public static class AttackResult {
        public boolean hit;
        public int damage;
        public boolean doubleAttack;
        public boolean precisionStrike;
        public boolean instantKill;
        public boolean lifeSteal;
        public int lifeStolenAmount;
        public Set<SpecialEffect> triggeredEffects;
        
        public AttackResult(boolean hit, int damage) {
            this.hit = hit;
            this.damage = damage;
            this.doubleAttack = false;
            this.precisionStrike = false;
            this.instantKill = false;
            this.lifeSteal = false;
            this.lifeStolenAmount = 0;
            this.triggeredEffects = new HashSet<>();
        }
    }
    
    /**
     * Defense Result class for special effects
     */
    public static class DefenseResult {
        public int damage;
        public boolean evaded;
        public Set<SpecialEffect> triggeredEffects;
        
        public DefenseResult(int damage) {
            this.damage = damage;
            this.evaded = false;
            this.triggeredEffects = new HashSet<>();
        }
    }
    
    /**
     * Combat Mastery Data class
     */
    public static class CombatMasteryData {
        public final String playerName;
        public final Map<Integer, NPCMasteryInfo> npcMasteryMap;
        public int totalKillsAllNpcs;
        public long lastKillTime;
        
        public CombatMasteryData(String playerName) {
            this.playerName = playerName;
            this.npcMasteryMap = new ConcurrentHashMap<>();
            this.totalKillsAllNpcs = 0;
            this.lastKillTime = 0;
        }
    }
    
    /**
     * Individual NPC mastery information
     */
    public static class NPCMasteryInfo {
        public final int npcId;
        public String npcName;
        public double masteryPoints;
        public int killCount;
        public String masteryTitle;
        public long firstKillTime;
        public long lastKillTime;
        public int npcLevel;
        
        public NPCMasteryInfo(int npcId, String npcName, int npcLevel) {
            this.npcId = npcId;
            this.npcName = npcName;
            this.npcLevel = npcLevel;
            this.masteryPoints = 0.0;
            this.killCount = 0;
            this.masteryTitle = "Novice";
            this.firstKillTime = System.currentTimeMillis();
            this.lastKillTime = System.currentTimeMillis();
        }
    }
    
    // ======================================================================
    // SPECIAL EFFECTS SYSTEM
    // ======================================================================
    
    /**
     * Get available special effects for player against specific NPC
     */
    public static Set<SpecialEffect> getAvailableEffects(Player player, NPC target) {
        if (player == null || target == null) return new HashSet<>();
        
        try {
            NPCMasteryInfo npcInfo = getNPCMasteryInfo(player, target.getId());
            if (npcInfo == null) return new HashSet<>();
            
            return getEffectsForMasteryLevel(npcInfo.masteryPoints);
            
        } catch (Exception e) {
            return new HashSet<>();
        }
    }
    
    /**
     * Get available special effects by NPC ID (no NPC object needed)
     */
    public static Set<SpecialEffect> getAvailableEffectsById(Player player, int npcId) {
        if (player == null) return new HashSet<>();
        
        try {
            NPCMasteryInfo npcInfo = getNPCMasteryInfo(player, npcId);
            if (npcInfo == null) return new HashSet<>();
            
            return getEffectsForMasteryLevel(npcInfo.masteryPoints);
            
        } catch (Exception e) {
            return new HashSet<>();
        }
    }
    
    /**
     * Helper method to get effects for a mastery level
     */
    private static Set<SpecialEffect> getEffectsForMasteryLevel(double masteryPoints) {
        Set<SpecialEffect> effects = EnumSet.noneOf(SpecialEffect.class);
        
        if (masteryPoints >= NOVICE_THRESHOLD) {
            effects.add(SpecialEffect.LUCKY_STRIKE);
            effects.add(SpecialEffect.SWIFT_RECOVERY);
        }
        
        if (masteryPoints >= EXPERIENCED_THRESHOLD) {
            effects.add(SpecialEffect.DOUBLE_ATTACK);
            effects.add(SpecialEffect.RESOURCE_HUNTER);
        }
        
        if (masteryPoints >= EXPERT_THRESHOLD) {
            effects.add(SpecialEffect.PRECISION_STRIKE);
            effects.add(SpecialEffect.ARMOR_PIERCING);
        }
        
        if (masteryPoints >= MASTER_THRESHOLD) {
            effects.add(SpecialEffect.EXECUTION);
            effects.add(SpecialEffect.LIFE_STEAL);
        }
        
        if (masteryPoints >= GRANDMASTER_THRESHOLD) {
            effects.add(SpecialEffect.EVASION_MASTERY);
            effects.add(SpecialEffect.INTIMIDATION);
        }
        
        return effects;
    }
    
    /**
     * Process attack special effects - MAIN METHOD FOR COMBAT INTEGRATION
     */
    public static AttackResult processAttackSpecialEffects(Player player, NPC target, int baseDamage, boolean baseHit) {
        AttackResult result = new AttackResult(baseHit, baseDamage);
        
        if (player == null || target == null) return result;
        
        try {
            Set<SpecialEffect> availableEffects = getAvailableEffects(player, target);
            if (availableEffects.isEmpty()) return result;
            
            for (SpecialEffect effect : availableEffects) {
                processIndividualAttackEffect(player, target, result, effect);
            }
            
            return result;
            
        } catch (Exception e) {
            return result;
        }
    }
    
    private static void processIndividualAttackEffect(Player player, NPC target, AttackResult result, SpecialEffect effect) {
        try {
            switch (effect) {
                case DOUBLE_ATTACK:
                    if (random.nextDouble() < 0.05) {
                        result.damage *= 2;
                        result.doubleAttack = true;
                        result.triggeredEffects.add(effect);
                        player.sendMessage(Colors.shad + Colors.orange + "DOUBLE ATTACK: " + Colors.white + 
                                         "Strike hits twice for double damage!" + Colors.eshad);
                    }
                    break;
                    
                case PRECISION_STRIKE:
                    if (!result.hit && random.nextDouble() < 0.15) {
                        result.hit = true;
                        result.precisionStrike = true;
                        result.triggeredEffects.add(effect);
                        player.sendMessage(Colors.shad + Colors.yellow + "PRECISION STRIKE: " + Colors.white + 
                                         "Perfect aim guarantees this hit!" + Colors.eshad);
                    }
                    break;
                    
                case EXECUTION:
                    if (result.hit && target.getHitpoints() < (target.getMaxHitpoints() * 0.2)) {
                        if (random.nextDouble() < 0.02) {
                            result.damage = target.getHitpoints();
                            result.instantKill = true;
                            result.triggeredEffects.add(effect);
                            player.sendMessage(Colors.shad + Colors.darkRed + "EXECUTION: " + Colors.white + 
                                             "Masterful strike finishes weakened foe!" + Colors.eshad);
                        }
                    }
                    break;
                    
                case LIFE_STEAL:
                    if (result.hit && random.nextDouble() < 0.08) {
                        int healAmount = (int)(result.damage * 0.25);
                        if (healAmount > 0) {
                            result.lifeSteal = true;
                            result.lifeStolenAmount = healAmount;
                            result.triggeredEffects.add(effect);
                            player.heal(healAmount);
                            player.sendMessage(Colors.shad + Colors.green + "LIFE STEAL: " + Colors.white + 
                                             "Absorbed " + healAmount + " health from enemy!" + Colors.eshad);
                        }
                    }
                    break;
                    
                case ARMOR_PIERCING:
                    if (result.hit && random.nextDouble() < 0.10) {
                        result.damage = (int)(result.damage * 1.5);
                        result.triggeredEffects.add(effect);
                        player.sendMessage(Colors.shad + Colors.orange + "ARMOR PIERCE: " + Colors.white + 
                                         "Attack bypasses defenses (+50% damage)!" + Colors.eshad);
                    }
                    break;
                    
                case LUCKY_STRIKE:
                    if (result.hit && random.nextDouble() < 0.03) {
                        result.triggeredEffects.add(effect);
                        player.sendMessage(Colors.shad + Colors.gold + "LUCKY STRIKE: " + Colors.white + 
                                         "Fortune smiles - bonus experience gained!" + Colors.eshad);
                    }
                    break;
            }
        } catch (Exception e) {
            // Continue silently
        }
    }
    
    /**
     * Process defense special effects - MAIN METHOD FOR COMBAT INTEGRATION
     */
    public static DefenseResult processDefenseSpecialEffects(Player player, NPC attacker, int damage) {
        DefenseResult result = new DefenseResult(damage);
        
        if (player == null || attacker == null) return result;
        
        try {
            Set<SpecialEffect> availableEffects = getAvailableEffects(player, attacker);
            if (availableEffects.isEmpty()) return result;
            
            for (SpecialEffect effect : availableEffects) {
                processIndividualDefenseEffect(player, attacker, result, effect);
            }
            
            return result;
            
        } catch (Exception e) {
            return result;
        }
    }
    
    private static void processIndividualDefenseEffect(Player player, NPC attacker, DefenseResult result, SpecialEffect effect) {
        try {
            switch (effect) {
                case EVASION_MASTERY:
                    if (random.nextDouble() < 0.15) {
                        result.damage = 0;
                        result.evaded = true;
                        result.triggeredEffects.add(effect);
                        player.sendMessage(Colors.shad + Colors.cyan + "EVASION MASTERY: " + Colors.white + 
                                         "Gracefully dodged all incoming damage!" + Colors.eshad);
                    }
                    break;
                    
                case SWIFT_RECOVERY:
                    if (random.nextDouble() < 0.02) {
                        result.triggeredEffects.add(effect);
                        player.sendMessage(Colors.lightGray + "Swift Recovery: Enhanced healing active" + Colors.eshad);
                    }
                    break;
            }
        } catch (Exception e) {
            // Continue silently
        }
    }
    
    public static boolean checkSpecialAttackInterrupt(Player player, NPC attacker) {
        if (player == null || attacker == null) return false;
        
        try {
            Set<SpecialEffect> availableEffects = getAvailableEffects(player, attacker);
            
            if (availableEffects.contains(SpecialEffect.INTIMIDATION)) {
                if (random.nextDouble() < 0.05) {
                    player.sendMessage(Colors.shad + Colors.purple + "INTIMIDATION: " + Colors.white + 
                                     "Your presence disrupts enemy's special attack!" + Colors.eshad);
                    return true;
                }
            }
            
            return false;
            
        } catch (Exception e) {
            return false;
        }
    }
    
    // ======================================================================
    // MASTERY SYSTEM
    // ======================================================================
    
    /**
     * Record a monster kill and update NPC-specific mastery
     */
    public static void recordMonsterKill(Player player, NPC monster) {
        if (player == null || monster == null) return;
        
        try {
            String playerKey = player.getDisplayName().toLowerCase();
            int npcId = monster.getId();
            int monsterLevel = getMonsterLevel(monster);
            String monsterName = getMonsterName(monster);
            
            CombatMasteryData masteryData = playerMasteryData.computeIfAbsent(playerKey, 
                k -> new CombatMasteryData(player.getDisplayName()));
            
            NPCMasteryInfo npcInfo = masteryData.npcMasteryMap.computeIfAbsent(npcId,
                k -> new NPCMasteryInfo(npcId, monsterName, monsterLevel));
            
            double masteryGained = calculateMasteryGain(monsterLevel);
            double oldMasteryPoints = npcInfo.masteryPoints;
            double newMasteryPoints = Math.min(oldMasteryPoints + masteryGained, MAX_MASTERY_PER_NPC);
            
            npcInfo.masteryPoints = newMasteryPoints;
            npcInfo.killCount++;
            npcInfo.lastKillTime = System.currentTimeMillis();
            npcInfo.npcName = monsterName;
            npcInfo.npcLevel = monsterLevel;
            
            masteryData.totalKillsAllNpcs++;
            masteryData.lastKillTime = System.currentTimeMillis();
            
            checkNPCMasteryProgression(player, monster, npcInfo, oldMasteryPoints, newMasteryPoints, masteryGained);
            sendMasteryProgressMessage(player, monster, npcInfo, masteryGained);
            saveMasteryData(playerKey, masteryData);
            
        } catch (Exception e) {
            // Continue silently
        }
    }
    
    private static void sendMasteryProgressMessage(Player player, NPC monster, NPCMasteryInfo npcInfo, double masteryGained) {
        try {
            if (npcInfo.killCount <= 5 || npcInfo.killCount % 5 == 0) {
                String title = getNPCMasteryTitle(npcInfo.masteryPoints);
                
                player.sendMessage(Colors.green + npcInfo.npcName + " defeated! " + 
                                 "Mastery: " + Colors.yellow + title + Colors.white + " (" + 
                                 npcInfo.killCount + " kills)" + Colors.eshad);
                
                if (npcInfo.killCount % 10 == 0 || npcInfo.killCount <= 3) {
                    double accuracy = getAccuracyBonusVsNPC(npcInfo.masteryPoints);
                    double damage = getDamageBonusVsNPC(npcInfo.masteryPoints);
                    Set<SpecialEffect> effects = getAvailableEffectsById(player, npcInfo.npcId);
                    
                    player.sendMessage(Colors.lightGray + "Bonuses: +" + String.format("%.0f", accuracy) + 
                                     " accuracy, +" + String.format("%.1f", damage) + "% damage, " + 
                                     effects.size() + " special effects" + Colors.eshad);
                }
            }
            
        } catch (Exception e) {
            // Continue silently
        }
    }
    
    private static String getMonsterName(NPC monster) {
        try {
            NPCDefinitions def = NPCDefinitions.getNPCDefinitions(monster.getId());
            if (def != null && def.getName() != null) {
                return def.getName();
            }
            return "Unknown Monster";
        } catch (Exception e) {
            return "Monster #" + monster.getId();
        }
    }
    
    private static int getMonsterLevel(NPC monster) {
        try {
            NPCDefinitions def = NPCDefinitions.getNPCDefinitions(monster.getId());
            if (def != null && def.combatLevel > 0) {
                return def.combatLevel;
            }
            
            try {
                if (monster.getCombatLevel() > 0) {
                    return monster.getCombatLevel();
                }
            } catch (Exception e) {
                // Continue
            }
            
            return Math.max(1, (monster.getId() % 100) + 1);
        } catch (Exception e) {
            return 1;
        }
    }
    
    private static double calculateMasteryGain(int monsterLevel) {
        return 1.0;
    }
    
    private static void checkNPCMasteryProgression(Player player, NPC monster, NPCMasteryInfo npcInfo, 
            double oldMasteryPoints, double newMasteryPoints, double masteryGained) {
        try {
            String oldTitle = getNPCMasteryTitle(oldMasteryPoints);
            String newTitle = getNPCMasteryTitle(newMasteryPoints);

            if (!oldTitle.equals(newTitle)) {
                player.sendMessage("");
                player.sendMessage(Colors.shad + Colors.gold + " MASTERY LEVEL UP " + Colors.eshad);
                player.sendMessage(Colors.shad + Colors.white + "You are now " + Colors.yellow + newTitle + 
                                 Colors.white + " at fighting " + Colors.cyan + npcInfo.npcName + Colors.white + "!" + Colors.eshad);

                try {
                    Set<SpecialEffect> newEffects = getAvailableEffectsById(player, npcInfo.npcId);
                    Set<SpecialEffect> oldEffects = getEffectsForMasteryLevel(oldMasteryPoints);
                    Set<SpecialEffect> unlockedEffects = new HashSet<>(newEffects);
                    unlockedEffects.removeAll(oldEffects);

                    if (!unlockedEffects.isEmpty()) {
                        player.sendMessage(Colors.shad + Colors.green + " New Abilities Unlocked: " + Colors.white + 
                                         unlockedEffects.size() + " special effects!" + Colors.eshad);

                        for (SpecialEffect effect : unlockedEffects) {
                            String effectName = effect.name().replace("_", " ").toLowerCase();
                            String description = getEffectDescription(effect);
                            player.sendMessage(Colors.white + "• " + effectName + ": " + description + Colors.eshad);
                        }
                    } else {
                        String tierMessage = getTierUnlockedMessage(newMasteryPoints, oldMasteryPoints);
                        if (!tierMessage.isEmpty()) {
                            player.sendMessage(Colors.shad + Colors.green + " New Abilities Unlocked!" + Colors.eshad);
                            player.sendMessage(Colors.white + tierMessage + Colors.eshad);
                        }
                    }

                } catch (Exception effectError) {
                    String basicTierInfo = getBasicTierInfo(newMasteryPoints);
                    if (!basicTierInfo.isEmpty()) {
                        player.sendMessage(Colors.shad + Colors.green + " New Abilities Unlocked!" + Colors.eshad);
                        player.sendMessage(Colors.white + basicTierInfo + Colors.eshad);
                    }
                }

                player.sendMessage("");
                npcInfo.masteryTitle = newTitle;
                awardNPCMasteryMilestoneBonus(player, npcInfo, newMasteryPoints);
            }

        } catch (Exception e) {
            // Continue silently
        }
    }
    
    private static String getTierUnlockedMessage(double newMastery, double oldMastery) {
        if (oldMastery < NOVICE_THRESHOLD && newMastery >= NOVICE_THRESHOLD) {
            return "Novice Tier: Lucky Strike (3% bonus XP), Swift Recovery (+25% healing)";
        } else if (oldMastery < EXPERIENCED_THRESHOLD && newMastery >= EXPERIENCED_THRESHOLD) {
            return "Experienced Tier: Double Attack (5% chance), Resource Hunter (5% double drops)";
        } else if (oldMastery < EXPERT_THRESHOLD && newMastery >= EXPERT_THRESHOLD) {
            return "Expert Tier: Precision Strike (15% never miss), Armor Piercing (10% +50% damage)";
        } else if (oldMastery < MASTER_THRESHOLD && newMastery >= MASTER_THRESHOLD) {
            return "Master Tier: Execution (2% instant kill), Life Steal (8% heal from damage)";
        } else if (oldMastery < GRANDMASTER_THRESHOLD && newMastery >= GRANDMASTER_THRESHOLD) {
            return "Grandmaster Tier: Evasion Mastery (15% dodge), Intimidation (5% disrupt specials)";
        }
        return "";
    }

    private static String getBasicTierInfo(double masteryPoints) {
        if (masteryPoints >= GRANDMASTER_THRESHOLD) {
            return "Grandmaster: 10 special combat effects, massive bonuses!";
        } else if (masteryPoints >= MASTER_THRESHOLD) {
            return "Master: 8 special combat effects, major bonuses!";
        } else if (masteryPoints >= EXPERT_THRESHOLD) {
            return "Expert: 6 special combat effects, significant bonuses!";
        } else if (masteryPoints >= EXPERIENCED_THRESHOLD) {
            return "Experienced: 4 special combat effects, good bonuses!";
        } else if (masteryPoints >= NOVICE_THRESHOLD) {
            return "Novice: 2 special combat effects, basic bonuses!";
        }
        return "";
    }
   
    public static String getNPCMasteryTitle(double masteryPoints) {
        if (masteryPoints >= GRANDMASTER_THRESHOLD) return "Grandmaster";
        if (masteryPoints >= MASTER_THRESHOLD) return "Master";
        if (masteryPoints >= EXPERT_THRESHOLD) return "Expert";
        if (masteryPoints >= EXPERIENCED_THRESHOLD) return "Experienced";
        if (masteryPoints >= NOVICE_THRESHOLD) return "Novice";
        return "Beginner";
    }
    
    private static void awardNPCMasteryMilestoneBonus(Player player, NPCMasteryInfo npcInfo, double masteryPoints) {
        try {
            if (masteryPoints >= GRANDMASTER_THRESHOLD) {
                player.sendMessage("<col=00ff00>Grandmaster vs " + npcInfo.npcName + ": +50% damage, +25% accuracy, 10 special effects!</col>");
            } else if (masteryPoints >= MASTER_THRESHOLD) {
                player.sendMessage("<col=00ff00>Master vs " + npcInfo.npcName + ": +37.5% damage, +20% accuracy, 8 special effects!</col>");
            } else if (masteryPoints >= EXPERT_THRESHOLD) {
                player.sendMessage("<col=00ff00>Expert vs " + npcInfo.npcName + ": +25% damage, +15% accuracy, 6 special effects!</col>");
            } else if (masteryPoints >= EXPERIENCED_THRESHOLD) {
                player.sendMessage("<col=00ff00>Experienced vs " + npcInfo.npcName + ": +12.5% damage, +10% accuracy, 4 special effects!</col>");
            } else if (masteryPoints >= NOVICE_THRESHOLD) {
                player.sendMessage("<col=00ff00>Novice vs " + npcInfo.npcName + ": +5% damage, +5% accuracy, 2 special effects!</col>");
            }
        } catch (Exception e) {
            // Continue silently
        }
    }
    
    // ======================================================================
    // BONUS CALCULATIONS
    // ======================================================================
    
    public static double getAccuracyBonusVsNPC(double masteryPoints) {
        return masteryPoints * ACCURACY_PER_MASTERY;
    }
    
    public static double getDamageBonusVsNPC(double masteryPoints) {
        return masteryPoints * DAMAGE_PER_MASTERY;
    }
    
    public static double getAccuracyBonusVsTarget(Player player, NPC target) {
        if (player == null || target == null) return 0.0;
        
        try {
            String playerKey = player.getDisplayName().toLowerCase();
            CombatMasteryData masteryData = playerMasteryData.get(playerKey);
            
            if (masteryData != null) {
                NPCMasteryInfo npcInfo = masteryData.npcMasteryMap.get(target.getId());
                if (npcInfo != null) {
                    return getAccuracyBonusVsNPC(npcInfo.masteryPoints);
                }
            }
            
            return 0.0;
        } catch (Exception e) {
            return 0.0;
        }
    }
    
    public static double getDamageMultiplierVsTarget(Player player, NPC target) {
        if (player == null || target == null) return 1.0;
        
        try {
            String playerKey = player.getDisplayName().toLowerCase();
            CombatMasteryData masteryData = playerMasteryData.get(playerKey);
            
            if (masteryData != null) {
                NPCMasteryInfo npcInfo = masteryData.npcMasteryMap.get(target.getId());
                if (npcInfo != null) {
                    double bonusPercentage = getDamageBonusVsNPC(npcInfo.masteryPoints);
                    return 1.0 + (bonusPercentage / 100.0);
                }
            }
            
            return 1.0;
        } catch (Exception e) {
            return 1.0;
        }
    }
    
    public static NPCMasteryInfo getNPCMasteryInfo(Player player, int npcId) {
        if (player == null) return null;
        
        try {
            String playerKey = player.getDisplayName().toLowerCase();
            CombatMasteryData masteryData = playerMasteryData.get(playerKey);
            
            if (masteryData != null) {
                return masteryData.npcMasteryMap.get(npcId);
            }
            
            return null;
        } catch (Exception e) {
            return null;
        }
    }
    
    // ======================================================================
    // DISPLAY METHODS
    // ======================================================================
    
    public static void displayCombatMastery(Player player) {
        if (player == null) return;
        
        try {
            String playerKey = player.getDisplayName().toLowerCase();
            CombatMasteryData masteryData = playerMasteryData.get(playerKey);
            
            if (masteryData != null && !masteryData.npcMasteryMap.isEmpty()) {
                player.sendMessage("<col=ffff00>Enhanced Combat Mastery Information</col>");
                player.sendMessage("<col=ffffff>Total Kills (All NPCs): " + masteryData.totalKillsAllNpcs + "</col>");
                player.sendMessage("<col=ffffff>NPCs with Mastery: " + masteryData.npcMasteryMap.size() + "</col>");
                
                int totalEffects = 0;
                for (NPCMasteryInfo npcInfo : masteryData.npcMasteryMap.values()) {
                    try {
                        totalEffects += getAvailableEffectsById(player, npcInfo.npcId).size();
                    } catch (Exception e) {
                        // Continue
                    }
                }
                player.sendMessage("<col=ffffff>Total Special Effects: " + totalEffects + "</col>");
                
                player.sendMessage("<col=ffaa00>--- TOP MASTERED NPCs ---</col>");
                
                masteryData.npcMasteryMap.values().stream()
                    .sorted((a, b) -> Double.compare(b.masteryPoints, a.masteryPoints))
                    .limit(10)
                    .forEach(npcInfo -> {
                        String title = getNPCMasteryTitle(npcInfo.masteryPoints);
                        double accuracy = getAccuracyBonusVsNPC(npcInfo.masteryPoints);
                        double damage = getDamageBonusVsNPC(npcInfo.masteryPoints);
                        
                        int effectCount = 0;
                        try {
                            effectCount = getAvailableEffectsById(player, npcInfo.npcId).size();
                        } catch (Exception e) {
                            // Continue
                        }
                        
                        player.sendMessage("<col=ffffff>" + npcInfo.npcName + " (" + title + "): " + 
                                         npcInfo.killCount + " kills, " + String.format("%.1f", npcInfo.masteryPoints) + " pts | " +
                                         "+" + String.format("%.1f", accuracy) + " acc, +" + String.format("%.1f", damage) + "% dmg | " +
                                         effectCount + " effects</col>");
                    });
                
                player.sendMessage("<col=66ff66>Use ;;mastery effects <npcId> to see special abilities!</col>");
                
            } else {
                player.sendMessage("<col=ff9900>No combat mastery data found. Fight some monsters to begin building mastery and unlock special effects!</col>");
            }
            
        } catch (Exception e) {
            player.sendMessage("Error displaying mastery information: " + e.getMessage());
        }
    }
    
    public static void displaySpecialEffects(Player player, int npcId) {
        if (player == null) return;
        
        try {
            Set<SpecialEffect> effects = getAvailableEffectsById(player, npcId);
            NPCMasteryInfo npcInfo = getNPCMasteryInfo(player, npcId);
            
            String npcName = npcInfo != null ? npcInfo.npcName : "NPC #" + npcId;
            String masteryTitle = npcInfo != null ? getNPCMasteryTitle(npcInfo.masteryPoints) : "None";
            double masteryPoints = npcInfo != null ? npcInfo.masteryPoints : 0.0;
            
            player.sendMessage("<col=ffff00>========== SPECIAL EFFECTS VS " + npcName.toUpperCase() + " ==========</col>");
            player.sendMessage("<col=ffffff>Mastery Level: " + masteryTitle + " (" + String.format("%.1f", masteryPoints) + " points)</col>");
            player.sendMessage("<col=ffffff>Available Effects: " + effects.size() + "</col>");
            
            if (effects.isEmpty()) {
                player.sendMessage("<col=ffaa00>No special effects available. Fight more " + npcName + "s to unlock abilities!</col>");
                
                player.sendMessage("<col=66ff66>--- UNLOCKABLE EFFECTS ---</col>");
                if (masteryPoints < NOVICE_THRESHOLD) {
                    player.sendMessage("<col=ffffff>At " + NOVICE_THRESHOLD + " mastery: Lucky Strike, Swift Recovery</col>");
                }
                if (masteryPoints < EXPERIENCED_THRESHOLD) {
                    player.sendMessage("<col=ffffff>At " + EXPERIENCED_THRESHOLD + " mastery: Double Attack, Resource Hunter</col>");
                }
                if (masteryPoints < EXPERT_THRESHOLD) {
                    player.sendMessage("<col=ffffff>At " + EXPERT_THRESHOLD + " mastery: Precision Strike, Armor Piercing</col>");
                }
                if (masteryPoints < MASTER_THRESHOLD) {
                    player.sendMessage("<col=ffffff>At " + MASTER_THRESHOLD + " mastery: Execution, Life Steal</col>");
                }
                if (masteryPoints < GRANDMASTER_THRESHOLD) {
                    player.sendMessage("<col=ffffff>At " + GRANDMASTER_THRESHOLD + " mastery: Evasion Mastery, Intimidation</col>");
                }
            } else {
                player.sendMessage("<col=00ff00>--- ACTIVE EFFECTS ---</col>");
                for (SpecialEffect effect : effects) {
                    String description = getEffectDescription(effect);
                    player.sendMessage("<col=ffffff>" + effect.name().replace("_", " ") + ": " + description + "</col>");
                }
            }
            
            player.sendMessage("<col=ffff00>========================================</col>");
            
        } catch (Exception e) {
            player.sendMessage("Error displaying special effects: " + e.getMessage());
        }
    }
    
    private static String getEffectDescription(SpecialEffect effect) {
        switch (effect) {
            case LUCKY_STRIKE: return "3% chance for +50% experience";
            case SWIFT_RECOVERY: return "+25% HP regeneration during combat";
            case DOUBLE_ATTACK: return "5% chance to attack twice";
            case RESOURCE_HUNTER: return "5% chance for double drops";
            case PRECISION_STRIKE: return "15% chance to never miss";
            case ARMOR_PIERCING: return "10% chance to ignore enemy defense (+50% damage)";
            case EXECUTION: return "2% chance for instant kill when enemy <20% HP";
            case LIFE_STEAL: return "8% chance to heal for 25% of damage dealt";
            case EVASION_MASTERY: return "15% chance to completely avoid damage";
            case INTIMIDATION: return "5% chance to disrupt enemy special attacks";
            default: return "Unknown effect";
        }
    }
    
    public static void displayNPCMastery(Player player, int npcId) {
        if (player == null) return;
        
        try {
            NPCMasteryInfo npcInfo = getNPCMasteryInfo(player, npcId);
            
            if (npcInfo != null) {
                String title = getNPCMasteryTitle(npcInfo.masteryPoints);
                double accuracy = getAccuracyBonusVsNPC(npcInfo.masteryPoints);
                double damage = getDamageBonusVsNPC(npcInfo.masteryPoints);
                
                Set<SpecialEffect> effects = new HashSet<>();
                try {
                    effects = getAvailableEffectsById(player, npcId);
                } catch (Exception e) {
                    // Continue
                }
                
                player.sendMessage("<col=ffff00>Enhanced Mastery vs " + npcInfo.npcName + "</col>");
                player.sendMessage("<col=ffffff>Level: " + title + "</col>");
                player.sendMessage("<col=ffffff>Kills: " + npcInfo.killCount + "</col>");
                player.sendMessage("<col=ffffff>Mastery Points: " + String.format("%.1f", npcInfo.masteryPoints) + " / " + MAX_MASTERY_PER_NPC + "</col>");
                player.sendMessage("<col=ffffff>Accuracy Bonus: +" + String.format("%.1f", accuracy) + "</col>");
                player.sendMessage("<col=ffffff>Damage Bonus: +" + String.format("%.1f", damage) + "%</col>");
                player.sendMessage("<col=ffffff>Special Effects: " + effects.size() + " active</col>");
                
                if (!effects.isEmpty()) {
                    player.sendMessage("<col=00ff00>--- ACTIVE SPECIAL EFFECTS ---</col>");
                    for (SpecialEffect effect : effects) {
                        String description = getEffectDescription(effect);
                        player.sendMessage("<col=ffffff>" + effect.name().replace("_", " ") + ": " + description + "</col>");
                    }
                }
                
                double nextMilestone = getNextNPCMilestone(npcInfo.masteryPoints);
                if (nextMilestone > 0) {
                    double pointsNeeded = nextMilestone - npcInfo.masteryPoints;
                    player.sendMessage("<col=ffaa00>Next Milestone: " + String.format("%.1f", pointsNeeded) + 
                                     " points to " + getNPCMasteryTitle(nextMilestone) + "</col>");
                }
                
            } else {
                player.sendMessage("<col=ff9900>No mastery data found for this NPC. Fight it to begin building mastery and unlock special effects!</col>");
            }
            
        } catch (Exception e) {
            player.sendMessage("Error displaying NPC mastery: " + e.getMessage());
        }
    }
    
    private static double getNextNPCMilestone(double currentMasteryPoints) {
        if (currentMasteryPoints < NOVICE_THRESHOLD) return NOVICE_THRESHOLD;
        if (currentMasteryPoints < EXPERIENCED_THRESHOLD) return EXPERIENCED_THRESHOLD;
        if (currentMasteryPoints < EXPERT_THRESHOLD) return EXPERT_THRESHOLD;
        if (currentMasteryPoints < MASTER_THRESHOLD) return MASTER_THRESHOLD;
        if (currentMasteryPoints < GRANDMASTER_THRESHOLD) return GRANDMASTER_THRESHOLD;
        return 0;
    }
    
    // ======================================================================
    // COMMAND HANDLER
    // ======================================================================
    
    public static void handleMasteryCommand(Player player, String[] cmd) {
        if (player == null) return;
        
        try {
            if (cmd.length > 1) {
                String subcommand = cmd[1].toLowerCase();
                
                if ("stats".equals(subcommand) || "info".equals(subcommand)) {
                    displayCombatMastery(player);
                } else if ("effects".equals(subcommand)) {
                    if (cmd.length > 2) {
                        try {
                            int npcId = Integer.parseInt(cmd[2]);
                            displaySpecialEffects(player, npcId);
                        } catch (NumberFormatException e) {
                            player.sendMessage("Invalid NPC ID. Usage: ;;mastery effects <npcId>");
                        }
                    } else {
                        NPC target = findNearestTarget(player);
                        if (target != null) {
                            displaySpecialEffects(player, target.getId());
                        } else {
                            player.sendMessage("No nearby NPC found. Usage: ;;mastery effects <npcId>");
                        }
                    }
                } else if ("npc".equals(subcommand)) {
                    if (cmd.length > 2) {
                        try {
                            int npcId = Integer.parseInt(cmd[2]);
                            displayNPCMastery(player, npcId);
                        } catch (NumberFormatException e) {
                            player.sendMessage("Invalid NPC ID. Usage: ;;mastery npc <npcId>");
                        }
                    } else {
                        player.sendMessage("Usage: ;;mastery npc <npcId>");
                    }
                } else if ("reset".equals(subcommand) && player.getRights() >= 2) {
                    resetMastery(player);
                } else {
                    player.sendMessage("Usage: ;;mastery [stats|effects|npc <id>]");
                    player.sendMessage("  stats - Show your combat mastery summary");
                    player.sendMessage("  effects - Show special effects for nearest NPC");
                    player.sendMessage("  effects <id> - Show special effects for specific NPC");
                    player.sendMessage("  npc <id> - Show mastery for specific NPC");
                }
            } else {
                displayCombatMastery(player);
            }
        } catch (Exception e) {
            player.sendMessage("Error in mastery command: " + e.getMessage());
        }
    }
    
    private static NPC findNearestTarget(Player player) {
        try {
            WorldTile playerTile = player.getWorldTile();
            if (playerTile == null) return null;
            
            for (int x = playerTile.getX() - 10; x <= playerTile.getX() + 10; x++) {
                for (int y = playerTile.getY() - 10; y <= playerTile.getY() + 10; y++) {
                    try {
                        for (NPC npc : World.getNPCs()) {
                            if (npc != null && npc.getWorldTile() != null && 
                                npc.getWorldTile().getX() == x && npc.getWorldTile().getY() == y) {
                                return npc;
                            }
                        }
                    } catch (Exception e) {
                        break;
                    }
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
    
    private static void resetMastery(Player player) {
        try {
            String playerKey = player.getDisplayName().toLowerCase();
            playerMasteryData.remove(playerKey);
            
            File playerFile = new File("data/players/mastery/" + playerKey + "_npc_mastery.txt");
            if (playerFile.exists()) {
                playerFile.delete();
            }
            
            player.sendMessage("<col=ff9900>Enhanced NPC-specific mastery data has been reset.</col>");
        } catch (Exception e) {
            player.sendMessage("Error resetting mastery: " + e.getMessage());
        }
    }
    
    // ======================================================================
    // FILE I/O
    // ======================================================================
    
    private static void saveMasteryData(String playerKey, CombatMasteryData data) {
        try {
            File masteryDir = new File("data/players/mastery/");
            if (!masteryDir.exists()) {
                masteryDir.mkdirs();
            }
            
            File playerFile = new File(masteryDir, playerKey + "_npc_mastery.txt");
            try (PrintWriter writer = new PrintWriter(new FileWriter(playerFile))) {
                writer.println("# Enhanced NPC-Specific Combat Mastery Data for " + data.playerName);
                writer.println("# Format: totalKills:lastKillTime");
                writer.println(data.totalKillsAllNpcs + ":" + data.lastKillTime);
                
                writer.println("# NPC Mastery Data (npcId:npcName:npcLevel:masteryPoints:killCount:masteryTitle:firstKill:lastKill)");
                for (NPCMasteryInfo npcInfo : data.npcMasteryMap.values()) {
                    writer.println("npc:" + npcInfo.npcId + ":" + npcInfo.npcName + ":" + npcInfo.npcLevel + ":" + 
                                 npcInfo.masteryPoints + ":" + npcInfo.killCount + ":" + npcInfo.masteryTitle + ":" + 
                                 npcInfo.firstKillTime + ":" + npcInfo.lastKillTime);
                }
            }
        } catch (Exception e) {
            // Continue silently
        }
    }
    
    public static void loadMasteryData(String playerName) {
        if (playerName == null) return;
        
        try {
            String playerKey = playerName.toLowerCase();
            File playerFile = new File("data/players/mastery/" + playerKey + "_npc_mastery.txt");
            
            if (!playerFile.exists()) {
                return;
            }
            
            CombatMasteryData data = new CombatMasteryData(playerName);
            
            try (BufferedReader reader = new BufferedReader(new FileReader(playerFile))) {
                String line;
                boolean readMainData = false;
                
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("#") || line.trim().isEmpty()) {
                        continue;
                    }
                    
                    if (!readMainData && !line.startsWith("npc:")) {
                        String[] parts = line.split(":");
                        if (parts.length >= 2) {
                            data.totalKillsAllNpcs = Integer.parseInt(parts[0]);
                            data.lastKillTime = Long.parseLong(parts[1]);
                            readMainData = true;
                        }
                    } else if (line.startsWith("npc:")) {
                        String[] parts = line.split(":");
                        if (parts.length >= 9) {
                            int npcId = Integer.parseInt(parts[1]);
                            String npcName = parts[2];
                            int npcLevel = Integer.parseInt(parts[3]);
                            double masteryPoints = Double.parseDouble(parts[4]);
                            int killCount = Integer.parseInt(parts[5]);
                            String masteryTitle = parts[6];
                            long firstKillTime = Long.parseLong(parts[7]);
                            long lastKillTime = Long.parseLong(parts[8]);
                            
                            NPCMasteryInfo npcInfo = new NPCMasteryInfo(npcId, npcName, npcLevel);
                            npcInfo.masteryPoints = masteryPoints;
                            npcInfo.killCount = killCount;
                            npcInfo.masteryTitle = masteryTitle;
                            npcInfo.firstKillTime = firstKillTime;
                            npcInfo.lastKillTime = lastKillTime;
                            
                            data.npcMasteryMap.put(npcId, npcInfo);
                        }
                    }
                }
            }
            
            playerMasteryData.put(playerKey, data);
            
        } catch (Exception e) {
            // Continue silently
        }
    }
    
    public static void onPlayerLogin(Player player) {
        if (player != null) {
            loadMasteryData(player.getDisplayName());
        }
    }
    
    public static void onPlayerLogout(Player player) {
        if (player != null) {
            String playerKey = player.getDisplayName().toLowerCase();
            CombatMasteryData data = playerMasteryData.get(playerKey);
            if (data != null) {
                saveMasteryData(playerKey, data);
            }
        }
    }
    
    // ======================================================================
    // MAIN INTEGRATION METHODS
    // ======================================================================
    
    public static double getAccuracyBonusForCombat(Player player, NPC target) {
        return getAccuracyBonusVsTarget(player, target);
    }
    
    public static double getDamageMultiplierForCombat(Player player, NPC target) {
        return getDamageMultiplierVsTarget(player, target);
    }
    
    public static void recordBossKillSafe(Player player, NPC monster) {
        if (player == null || monster == null) {
            return;
        }
        
        try {
            recordMonsterKill(player, monster);
        } catch (Exception e) {
            try {
                String playerKey = player.getDisplayName().toLowerCase();
                int npcId = monster.getId();
                String monsterName = monster.getName();
                if (monsterName == null) monsterName = "Boss #" + npcId;
                
                CombatMasteryData masteryData = playerMasteryData.get(playerKey);
                if (masteryData == null) {
                    masteryData = new CombatMasteryData(player.getDisplayName());
                    playerMasteryData.put(playerKey, masteryData);
                }
                
                NPCMasteryInfo npcInfo = masteryData.npcMasteryMap.get(npcId);
                if (npcInfo == null) {
                    npcInfo = new NPCMasteryInfo(npcId, monsterName, 100);
                    masteryData.npcMasteryMap.put(npcId, npcInfo);
                }
                
                npcInfo.masteryPoints = Math.min(npcInfo.masteryPoints + 1.0, MAX_MASTERY_PER_NPC);
                npcInfo.killCount++;
                npcInfo.lastKillTime = System.currentTimeMillis();
                npcInfo.npcName = monsterName;
                npcInfo.npcLevel = 100;
                
                masteryData.totalKillsAllNpcs++;
                masteryData.lastKillTime = System.currentTimeMillis();
                
                String title = getNPCMasteryTitle(npcInfo.masteryPoints);
                player.sendMessage("<col=66ff66>" + monsterName + " defeated! " + 
                                 "Mastery: " + title + " (" + npcInfo.killCount + " kills)</col>");
                
                if (npcInfo.killCount == 10 || npcInfo.killCount == 25 || npcInfo.killCount == 50 || 
                    npcInfo.killCount == 75 || npcInfo.killCount == 100) {
                    player.sendMessage("<col=ffff00>" + monsterName.toUpperCase() + " MASTERY LEVEL UP!</col>");
                    player.sendMessage("<col=00ff00>You are now " + title + " with special effects!</col>");
                }
                
                try {
                    saveMasteryData(playerKey, masteryData);
                } catch (Exception saveError) {
                    // Continue silently
                }
                
            } catch (Exception e2) {
                // Continue silently
            }
        }
    }
}