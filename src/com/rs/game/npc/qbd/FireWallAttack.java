package com.rs.game.npc.qbd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.rs.game.Animation;
import com.rs.game.Hit;
import com.rs.game.Hit.HitLook;
import com.rs.game.player.Player;
import com.rs.game.player.content.Combat;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Utils;

/**
 * Represents the Queen Black Dragon's fire wall attack.
 * 
 * @author Emperor - improved by zeus 05.27.2025
 * 
 */
public final class FireWallAttack implements QueenAttack {
    /**
     * The wall graphic ids.
     */
    private static final int[] WALL_GRAPHIC_IDS = { 3158, 3159, 3160 };
    
    /**
     * The animation.
     */
    private static final Animation ANIMATION = new Animation(16746);
    
    // Constants for better maintainability
    private static final int WALL_START_Y = 37;
    private static final int WALL_END_Y = 19;
    private static final int WALL_DELAY_MULTIPLIER = 7;
    
    @Override
    public int attack(final QueenBlackDragon npc, final Player victim) {
        int waves = npc.getPhase();
        if (waves > 3) {
            waves = 3;
        }
        
        npc.setNextAnimation(ANIMATION);
        
        final List<Integer> wallIds = new ArrayList<Integer>();
        for (int id : WALL_GRAPHIC_IDS) {
            wallIds.add(id);
        }
        Collections.shuffle(wallIds);
        
        victim.getPackets().sendGameMessage(
                "<col=FF9900>The Queen Black Dragon takes a huge breath.</col>");
        
        final int wallCount = waves;
        
        // Start the attack sequence
        WorldTasksManager.schedule(new WorldTask() {
            @Override
            public void run() {
                startFireWallSequence(npc, victim, wallIds, wallCount);
            }
        }, 1);
        
        // Fixed typo: getTemporaryAttributes (was missing 'i')
        npc.getTemporaryAttributtes().put("fire_wall_tick_",
                npc.getTicks() + Utils.random((waves * WALL_DELAY_MULTIPLIER) + 5, 60));
        
        return 8 + (waves * 2);
    }
    
    /**
     * Starts the fire wall sequence
     */
    private void startFireWallSequence(final QueenBlackDragon npc, final Player victim, 
                                     final List<Integer> wallIds, final int wallCount) {
        for (int i = 0; i < wallCount; i++) {
            final int wallIndex = i;
            final int wallId = wallIds.get(i);
            
            WorldTasksManager.schedule(new WorldTask() {
                @Override
                public void run() {
                    launchFireWall(npc, victim, wallId);
                }
            }, (wallIndex * WALL_DELAY_MULTIPLIER) + 1);
        }
    }
    
    /**
     * Launches a single fire wall
     */
    private void launchFireWall(final QueenBlackDragon npc, final Player victim, final int wallId) {
        // Send the projectile visual effect
        victim.getPackets().sendProjectile(null,
                npc.getBase().transform(33, 38, 0),
                npc.getBase().transform(33, 19, 0), wallId,
                0, 0, 18, 46, 0, 0, 0);
        
        // Create the wall effect for both lanes (y=37 and y=38)
        createWallLane(npc, victim, wallId, WALL_START_Y);
        createWallLane(npc, victim, wallId, WALL_START_Y + 1);
    }
    
    /**
     * Creates a fire wall lane that moves from north to south
     */
    private void createWallLane(final QueenBlackDragon npc, final Player victim, 
                               final int wallId, final int startY) {
        WorldTasksManager.schedule(new FireWallTask(npc, victim, wallId, startY), 0, 0);
    }
    
    /**
     * Task that handles the movement and damage of a single fire wall lane
     */
    private class FireWallTask extends WorldTask {
        private final QueenBlackDragon npc;
        private final Player victim;
        private final int wallId;
        private int currentY;
        
        public FireWallTask(QueenBlackDragon npc, Player victim, int wallId, int startY) {
            this.npc = npc;
            this.victim = victim;
            this.wallId = wallId;
            this.currentY = startY;
        }
        
        @Override
        public void run() {
            // Safety checks
            if (npc == null || victim == null || victim.hasFinished()) {
                stop();
                return;
            }
            
            // CORRECTED LOGIC: Only damage if player is NOT in the safe path
            // (matching original behavior with the ! operator)
            if (!isPlayerInWallPath(wallId, victim, npc) && 
                victim.getY() == npc.getBase().getY() + currentY) {
                dealFireWallDamage(npc, victim);
            }
            
            // Move the wall south
            currentY--;
            
            // Stop when we reach the southern boundary (matching original logic)
            if (currentY <= WALL_END_Y) {
                stop();
            }
        }
    }
    
    /**
     * Checks if the player is in the SAFE path of the specified wall
     * Note: Players are safe when they're exactly on the wall's X coordinate
     */
    private boolean isPlayerInWallPath(int wallId, Player victim, QueenBlackDragon npc) {
        int playerX = victim.getX();
        int baseX = npc.getBase().getX();
        
        switch (wallId) {
            case 3158:
                return playerX == baseX + 28;
            case 3159:
                return playerX == baseX + 37;
            case 3160:
                return playerX == baseX + 32;
            default:
                return false;
        }
    }
    
    /**
     * Deals fire wall damage to the player, considering prayer protection
     */
    private void dealFireWallDamage(QueenBlackDragon npc, Player victim) {
        String protectionMessage = Combat.getProtectMessage(victim);
        int damage = Utils.random(200, 355);
        
        if (protectionMessage != null) {
            victim.sendMessage(protectionMessage, true);
            
            // Apply damage reduction based on protection level
            if (protectionMessage.contains("fully")) {
                damage = (int)(damage * 0.1);
            } else if (protectionMessage.contains("most")) {
                damage = (int)(damage * 0.12);
            } else if (protectionMessage.contains("some")) {
                damage = (int)(damage * 0.5);
            }
        } else {
            victim.sendMessage("You are horribly burned by the dragons fiery wall!", true);
        }
        
        victim.applyHit(new Hit(npc, damage, HitLook.REGULAR_DAMAGE));
    }
    
    @Override
    public boolean canAttack(QueenBlackDragon npc, Player victim) {
        // Fixed typo: getTemporaryAttributes
        Integer tick = (Integer) npc.getTemporaryAttributtes().get("fire_wall_tick_");
        return tick == null || tick < npc.getTicks();
    }
}