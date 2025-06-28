package com.rs.game.npc.combat.impl;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import com.rs.game.Animation;
import com.rs.game.Entity;
import com.rs.game.ForceTalk;
import com.rs.game.Graphics;
import com.rs.game.Hit;
import com.rs.game.Hit.HitLook;
import com.rs.game.World;
import com.rs.game.WorldTile;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.game.player.Player;
import com.rs.game.player.content.BossBalancer;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Utils;
import com.rs.utils.NPCBonuses;

/**
 * Clean RockCrab Combat System - Simple & Effective
 * 
 * Features:
 * - Basic combat with minimal spam
 * - Occasional special attacks
 * - Simple BossBalancer integration
 * - No excessive chatter or effects
 * 
 * @author Zeus
 * @date June 10, 2025
 * @version 3.0 - Clean & Simple Combat
 */
public class RockCrabCombat extends CombatScript {

    // Basic tracking (much simpler)
    private static final Map<Integer, Integer> crabAttackCount = new ConcurrentHashMap<Integer, Integer>();
    private static final Map<Integer, Boolean> crabInShellMode = new ConcurrentHashMap<Integer, Boolean>();
    private static final Map<Integer, Long> crabShellStartTime = new ConcurrentHashMap<Integer, Long>();
    
    // Much less frequent special attacks
    private static final int SHELL_DEFENSE_CHANCE = 35; // 3% chance (way less frequent)
    private static final int CHARGE_ATTACK_CHANCE = 50; // 2% chance
    private static final int CLAW_COMBO_CHANCE = 40;    // 2.5% chance
    
    // Timing
    private static final long SHELL_MODE_DURATION = 4000; // 4 seconds
    
    // Basic animations (no excessive effects)
    private static final int CLAW_SWIPE_ANIM = 1312;
    private static final int SHELL_DEFENSE_ANIM = 1312;
    private static final int CHARGE_ATTACK_ANIM = 1312;
    
    // Minimal graphics
    private static final int BASIC_SPLASH_GFX = 1950;
    
    // Damage multipliers
    private static final double NORMAL_ATTACK_MULTIPLIER = 1.0;
    private static final double WEAK_ATTACK_MULTIPLIER = 0.7;
    private static final double CHARGE_ATTACK_MULTIPLIER = 1.3;
    private static final double COMBO_ATTACK_MULTIPLIER = 0.8;

    @Override
    public Object[] getKeys() {
        return new Object[] { 1265, 1264, 1266, 1267, 1268 }; // Rock crab IDs
    }
    
    @Override
    public int attack(final NPC npc, final Entity target) {
        if (!isValidCombatState(npc, target)) {
            return 4;
        }
        
        final NPCCombatDefinitions defs = npc.getCombatDefinitions();
        if (defs == null) {
            return 4;
        }
        
        int npcHash = npc.getId() + npc.hashCode();
        Integer attackCount = crabAttackCount.get(npcHash);
        if (attackCount == null) attackCount = 0;
        
        // Simple boss balancer integration
        int dynamicMaxHit = getSimpleMaxHit(npc, defs);
        int bossType = getSimpleBossType(npc);
        int bossTier = getSimpleBossTier(npc);
        BossInfo bossInfo = getSimpleBossInfo(npc, dynamicMaxHit, bossType, bossTier);
        
        // Check if shell defense mode should end
        Boolean inShell = crabInShellMode.get(npcHash);
        if (inShell != null && inShell) {
            Long shellStartTime = crabShellStartTime.get(npcHash);
            if (shellStartTime != null && (System.currentTimeMillis() - shellStartTime) > SHELL_MODE_DURATION) {
                exitShellMode(npc, npcHash);
            }
        }
        
        // Shell defense attacks (weaker)
        if (inShell != null && inShell) {
            return performShellDefenseAttack(npc, target, bossInfo);
        }
        
        attackCount++;
        crabAttackCount.put(npcHash, attackCount);
        
        // Rare special attacks (much less frequent)
        if (Utils.random(CHARGE_ATTACK_CHANCE) == 0) {
            return performChargeAttack(npc, target, bossInfo);
        }
        
        if (Utils.random(CLAW_COMBO_CHANCE) == 0) {
            return performClawCombo(npc, target, bossInfo);
        }
        
        if (Utils.random(SHELL_DEFENSE_CHANCE) == 0) {
            return enterShellDefenseMode(npc, npcHash);
        }
        
        // Normal attack
        return performNormalAttack(npc, target, defs, bossInfo);
    }
    
    /**
     * Enter shell defense mode (no spam)
     */
    private int enterShellDefenseMode(NPC npc, int npcHash) {
        crabInShellMode.put(npcHash, true);
        crabShellStartTime.put(npcHash, System.currentTimeMillis());
        
        npc.setNextAnimation(new Animation(SHELL_DEFENSE_ANIM));
        // No force talk or excessive graphics
        
        return 5;
    }
    
    /**
     * Exit shell mode (quietly)
     */
    private void exitShellMode(NPC npc, int npcHash) {
        crabInShellMode.put(npcHash, false);
        // No fanfare, just exit quietly
    }
    
    /**
     * Shell defense attack (weaker damage)
     */
    private int performShellDefenseAttack(NPC npc, Entity target, BossInfo bossInfo) {
        npc.setNextAnimation(new Animation(CLAW_SWIPE_ANIM));
        
        int maxHit = calculateDamage(npc, bossInfo, WEAK_ATTACK_MULTIPLIER);
        int damage = Utils.random(maxHit + 1);
        Hit hit = getMeleeHit(npc, damage);
        
        delayHit(npc, 1, target, hit);
        return 5;
    }
    
    /**
     * Charge attack (stronger damage, no spam)
     */
    private int performChargeAttack(NPC npc, Entity target, BossInfo bossInfo) {
        npc.setNextAnimation(new Animation(CHARGE_ATTACK_ANIM));
        
        WorldTasksManager.schedule(new WorldTask() {
            @Override
            public void run() {
                try {
                    if (isValidTarget(target)) {
                        int maxHit = calculateDamage(npc, bossInfo, CHARGE_ATTACK_MULTIPLIER);
                        int damage = Utils.random(maxHit + 1);
                        Hit hit = getMeleeHit(npc, damage);
                        delayHit(npc, 0, target, hit);
                    }
                } catch (Exception e) {
                    System.err.println("Error in RockCrab charge attack: " + e.getMessage());
                } finally {
                    stop();
                }
            }
        }, 2);
        
        return 6;
    }
    
    /**
     * Claw combo (2 hits, no spam)
     */
    private int performClawCombo(NPC npc, Entity target, BossInfo bossInfo) {
        npc.setNextAnimation(new Animation(CLAW_SWIPE_ANIM));
        
        WorldTasksManager.schedule(new WorldTask() {
            private int comboHit = 0;
            
            @Override
            public void run() {
                try {
                    if (comboHit >= 2 || !isValidTarget(target)) {
                        stop();
                        return;
                    }
                    
                    int maxHit = calculateDamage(npc, bossInfo, COMBO_ATTACK_MULTIPLIER);
                    int damage = Utils.random(maxHit + 1);
                    Hit hit = getMeleeHit(npc, damage);
                    delayHit(npc, 0, target, hit);
                    
                    comboHit++;
                } catch (Exception e) {
                    System.err.println("Error in RockCrab combo: " + e.getMessage());
                    stop();
                }
            }
        }, 1, 2);
        
        return 7;
    }
    
    /**
     * Normal claw attack (no personality spam)
     */
    private int performNormalAttack(NPC npc, Entity target, NPCCombatDefinitions defs, BossInfo bossInfo) {
        npc.setNextAnimation(new Animation(CLAW_SWIPE_ANIM));
        
        int maxHit = calculateDamage(npc, bossInfo, NORMAL_ATTACK_MULTIPLIER);
        int damage = Utils.random(maxHit + 1);
        Hit hit = getMeleeHit(npc, damage);
        
        delayHit(npc, 1, target, hit);
        return defs.getAttackDelay();
    }
    
    /**
     * Simple max hit calculation
     */
    private int getSimpleMaxHit(NPC npc, NPCCombatDefinitions defs) {
        try {
            return defs.getMaxHit();
        } catch (Exception e) {
            return 10;
        }
    }
    
    /**
     * Simple boss type
     */
    private int getSimpleBossType(NPC npc) {
        try {
            int configuredType = readBossTypeFromFile(npc.getId());
            if (configuredType != -1) {
                return configuredType;
            }
            return 0; // Melee
        } catch (Exception e) {
            return 0;
        }
    }
    
    /**
     * Simple boss tier
     */
    private int getSimpleBossTier(NPC npc) {
        try {
            int configuredTier = readBossTierFromFile(npc.getId());
            if (configuredTier != -1) {
                return configuredTier;
            }
            
            NPCCombatDefinitions defs = npc.getCombatDefinitions();
            int hp = defs.getHitpoints();
            int maxHit = defs.getMaxHit();
            
            if (hp <= 500 && maxHit <= 15) return 1;
            else if (hp <= 1000 && maxHit <= 25) return 2;
            else if (hp <= 2000 && maxHit <= 35) return 3;
            else return 4;
            
        } catch (Exception e) {
            return 1;
        }
    }
    
    /**
     * Calculate damage with boss balancer scaling
     */
    private int calculateDamage(NPC npc, BossInfo bossInfo, double multiplier) {
        if (npc == null || bossInfo == null) {
            return 8;
        }
        
        try {
            int baseMaxHit = Math.max(bossInfo.maxHit, 1);
            
            if (bossInfo.bonuses != null && bossInfo.bonuses.length >= 5) {
                int maxOffensiveBonus = 0;
                for (int i = 0; i < Math.min(5, bossInfo.bonuses.length); i++) {
                    maxOffensiveBonus = Math.max(maxOffensiveBonus, bossInfo.bonuses[i]);
                }
                
                double bonusMultiplier = 1.0 + (maxOffensiveBonus / 1000.0);
                baseMaxHit = (int) (baseMaxHit * bonusMultiplier);
            }
            
            int modifiedHit = (int) (baseMaxHit * multiplier);
            return Math.max(modifiedHit, 1);
            
        } catch (Exception e) {
            return 8;
        }
    }
    
    /**
     * Get boss information (simplified)
     */
    private BossInfo getSimpleBossInfo(NPC npc, int dynamicMaxHit, int bossType, int bossTier) {
        BossInfo info = new BossInfo();
        
        try {
            NPCCombatDefinitions defs = npc.getCombatDefinitions();
            if (defs != null) {
                info.maxHit = dynamicMaxHit;
                info.hitpoints = defs.getHitpoints();
                info.attackStyle = defs.getAttackStyle();
                info.attackDelay = defs.getAttackDelay();
            }

            int[] bonuses = NPCBonuses.getBonuses(npc.getId());
            if (bonuses != null && bonuses.length >= 10) {
                info.bonuses = bonuses;
                info.maxBonus = getMaxBonus(bonuses);
            }

            info.estimatedTier = bossTier;
            info.estimatedType = bossType;
            info.currentHitpoints = npc.getHitpoints();
            
        } catch (Exception e) {
            System.err.println("Error getting boss info: " + e.getMessage());
        }
        
        return info;
    }
    
    // ==================== UTILITY METHODS ====================
    
    private boolean isValidCombatState(NPC npc, Entity target) {
        if (npc == null || target == null) {
            return false;
        }
        
        try {
            return !npc.isDead() && !npc.hasFinished() && 
                   !target.isDead() && !target.hasFinished() &&
                   npc.getCombatDefinitions() != null;
        } catch (Exception e) {
            return false;
        }
    }
    
    private boolean isValidTarget(Entity target) {
        if (target == null) return false;
        
        try {
            return !target.isDead() && !target.hasFinished();
        } catch (Exception e) {
            return false;
        }
    }
    
    private int readBossTypeFromFile(int npcId) {
        try {
            java.io.File bossFile = new java.io.File("data/npcs/bosses/" + npcId + ".txt");
            if (!bossFile.exists()) {
                return -1;
            }
            
            java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(bossFile));
            String line;
            
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("bossType=")) {
                    reader.close();
                    return Integer.parseInt(line.substring(9));
                }
            }
            reader.close();
            
        } catch (Exception e) {
            // Silent fail
        }
        
        return -1;
    }
    
    private int readBossTierFromFile(int npcId) {
        try {
            java.io.File bossFile = new java.io.File("data/npcs/bosses/" + npcId + ".txt");
            if (!bossFile.exists()) {
                return -1;
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
            // Silent fail
        }
        
        return -1;
    }
    
    private int getMaxBonus(int[] bonuses) {
        if (bonuses == null || bonuses.length == 0) return 50;
        
        int max = 0;
        for (int bonus : bonuses) {
            if (bonus > max) max = bonus;
        }
        return Math.max(max, 10);
    }
    
    /**
     * Simple boss information container
     */
    private static class BossInfo {
        int maxHit = 10;
        int hitpoints = 500;
        int currentHitpoints = 500;
        int attackStyle = NPCCombatDefinitions.MELEE;
        int attackDelay = 4;
        int[] bonuses = new int[10];
        int maxBonus = 50;
        int estimatedTier = 1;
        int estimatedType = 0;
    }
}