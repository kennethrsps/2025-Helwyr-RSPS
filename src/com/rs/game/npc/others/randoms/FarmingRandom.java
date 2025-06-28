package com.rs.game.npc.others.randoms;

import com.rs.game.Animation;
import com.rs.game.ForceTalk;
import com.rs.game.WorldTile;
import com.rs.game.item.Item;
import com.rs.game.npc.NPC;
import com.rs.game.player.Player;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Utils;
/**
 * @author Zeus
 * @date 05.27.2025
 */
/**
 * FarmingRandom NPC that follows players and gives rewards based on produce gathered.
 * Spawns near a target player and offers rewards for farming achievements.
 */
public class FarmingRandom extends NPC {
    
    private static final long serialVersionUID = -4326730097044497911L;
    
    // Constants
    private static final int NPC_ID = 2170;
    private static final long TIMEOUT_DURATION = 60000; // 60 seconds
    private static final int FOLLOW_DISTANCE = 2;
    private static final int MAX_REWARD_MONEY = 250000;
    private static final int UNLOCK_DELAY_TICKS = 2;
    private static final int PLAYER_LOCK_DURATION = 3;
    
    // Reward thresholds and corresponding item IDs
    private static final int[] PRODUCE_THRESHOLDS = {1000, 2000, 3000, 4000, 5000, 25000};
    private static final int[] REWARD_ITEM_IDS = {31347, 31346, 31345, 31344, 31342, 34922};
    private static final int[] CHECK_ITEM_IDS = {31347, 31346, 31345, 31343, 31342, 34922};
    private static final int[] ALTERNATE_CHECK_IDS = {34926, -1, -1, -1, -1, 34926}; // -1 means no alternate
    
    // Instance variables
    private final Player target;
    private final long createTime;
    private boolean stop;

    /**
     * Creates a new FarmingRandom NPC.
     * @param tile The spawn location
     * @param target The target player to follow
     */
    public FarmingRandom(WorldTile tile, Player target) {
        super(NPC_ID, tile, -1, true, true);
        this.target = target;
        this.createTime = Utils.currentTimeMillis();
        this.stop = false;
        
        initializeNPC();
    }
    
    /**
     * Initializes the NPC with starting behavior.
     */
    private void initializeNPC() {
        setNextForceTalk(new ForceTalk("Hey, " + target.getDisplayName() + ", talk to me."));
        setRun(true);
    }

    /**
     * Gives the player rewards based on their produce gathered.
     * @param player The player to give rewards to
     */
    public void giveReward(final Player player) {
        if (!validatePlayer(player)) {
            return;
        }
        
        preparePlayerForReward(player);
        giveProduceRewards(player);
        giveMoneyReward(player);
        finishInteraction(player);
    }
    
    /**
     * Validates if the player can receive rewards.
     * @param player The player to validate
     * @return true if player is valid for rewards
     */
    private boolean validatePlayer(Player player) {
        player.stopAll(true, false, true);
        player.setNextAnimation(new Animation(-1));
        
        if (player != target || player.isLocked()) {
            player.getDialogueManager().startDialogue("SimpleNPCMessage", 
                    1051, "I don't have time for chit-chats.");
            return false;
        }
        return true;
    }
    
    /**
     * Prepares the player for receiving rewards.
     * @param player The player to prepare
     */
    private void preparePlayerForReward(Player player) {
        stop = true;
        player.lock(PLAYER_LOCK_DURATION);
    }
    
    /**
     * Gives produce-based rewards to the player.
     * @param player The player to give rewards to
     */
    private void giveProduceRewards(Player player) {
        int produceGathered = player.getProduceGathered();
        
        for (int i = 0; i < PRODUCE_THRESHOLDS.length; i++) {
            if (produceGathered >= PRODUCE_THRESHOLDS[i]) {
                giveRewardIfNotOwned(player, i);
            }
        }
    }
    
    /**
     * Gives a specific reward if the player doesn't already own it.
     * @param player The player to give the reward to
     * @param rewardIndex The index of the reward in the arrays
     */
    private void giveRewardIfNotOwned(Player player, int rewardIndex) {
        Item checkItem = new Item(CHECK_ITEM_IDS[rewardIndex]);
        boolean hasMainItem = player.hasItem(checkItem);
        boolean hasAlternateItem = false;
        
        // Check for alternate item if it exists
        if (ALTERNATE_CHECK_IDS[rewardIndex] != -1) {
            hasAlternateItem = player.hasItem(new Item(ALTERNATE_CHECK_IDS[rewardIndex]));
        }
        
        if (!hasMainItem && !hasAlternateItem) {
            player.addItem(new Item(REWARD_ITEM_IDS[rewardIndex], 1));
        }
    }
    
    /**
     * Gives money reward to the player.
     * @param player The player to give money to
     */
    private void giveMoneyReward(Player player) {
        player.addMoney(Utils.random(MAX_REWARD_MONEY));
    }
    
    /**
     * Finishes the interaction with farewell messages and cleanup.
     * @param player The player to finish interaction with
     */
    private void finishInteraction(final Player player) {
        setNextForceTalk(new ForceTalk("See you later, " + target.getDisplayName() + "!"));
        player.sendMessage("<col=ff0000>The Farmer gives you a reward before leaving.");
        
        // Schedule cleanup task with weak reference to prevent memory leaks
        WorldTasksManager.schedule(new WorldTask() {
            @Override
            public void run() {
                if (player != null && !player.hasFinished()) {
                    player.unlock();
                }
                stop = false;
                finish();
                stop();
            }
        }, UNLOCK_DELAY_TICKS);
    }

    @Override
    public void processNPC() {
        if (shouldFinish()) {
            finish();
            return;
        }
        
        sendFollow(target);
        handleRandomChatter();
    }
    
    /**
     * Checks if the NPC should finish (timeout or target finished).
     * @return true if NPC should finish
     */
    private boolean shouldFinish() {
        return target.hasFinished() || (createTime + TIMEOUT_DURATION < Utils.currentTimeMillis());
    }
    
    /**
     * Handles random chatter to get player's attention.
     */
    private void handleRandomChatter() {
        if (stop) {
            return;
        }
        
        int random = Utils.random(50);
        String playerName = target.getDisplayName();
        
        if (random <= 2) {
            setNextForceTalk(new ForceTalk(playerName + " talk to me!"));
        } else if (random >= 48) {
            setNextForceTalk(new ForceTalk("Talk to me, " + playerName + "!"));
        }
    }

    @Override
    public boolean withinDistance(Player tile, int distance) {
        return tile == target && super.withinDistance(tile, distance);
    }
    
    /**
     * Makes the NPC follow the target player.
     * @param player The player to follow
     */
    private void sendFollow(Player player) {
        if (!withinDistance(player, FOLLOW_DISTANCE)) {
            setNextWorldTile(player);
        }
        
        updateFacing(player);
        
        if (isFrozen()) {
            return;
        }
        
        handleCollisionAvoidance(player);
        handlePathfinding(player);
    }
    
    /**
     * Updates the NPC's facing direction toward the player.
     * @param player The player to face
     */
    private void updateFacing(Player player) {
        if (getLastFaceEntity() != player.getClientIndex()) {
            setNextFaceEntity(player);
        }
    }
    
    /**
     * Handles collision avoidance when too close to player.
     * @param player The player to avoid colliding with
     */
    private void handleCollisionAvoidance(Player player) {
        int size = getSize();
        int targetSize = player.getSize();
        
        boolean isColliding = Utils.colides(getX(), getY(), size, 
                                          player.getX(), player.getY(), targetSize);
        
        if (isColliding && !player.hasWalkSteps()) {
            tryAvoidanceMovement(player, size, targetSize);
        }
    }
    
    /**
     * Tries different movement directions to avoid collision.
     * @param player The player to avoid
     * @param size This NPC's size
     * @param targetSize The target player's size
     */
    private void tryAvoidanceMovement(Player player, int size, int targetSize) {
        resetWalkSteps();
        
        // Try moving to different positions around the player
        if (addWalkSteps(player.getX() + targetSize, getY()) ||
            addWalkSteps(player.getX() - size, getY()) ||
            addWalkSteps(getX(), player.getY() + targetSize) ||
            addWalkSteps(getX(), player.getY() - size)) {
            return; // Successfully found a path
        }
        
        // If no movement worked, reset and continue
        resetWalkSteps();
    }
    
    /**
     * Handles pathfinding to follow the player.
     * @param player The player to follow
     */
    private void handlePathfinding(Player player) {
        resetWalkSteps();
        
        int size = getSize();
        int targetSize = player.getSize();
        boolean canSeePlayer = clipedProjectile(player, true);
        boolean inRange = Utils.isOnRange(getX(), getY(), size, 
                                        player.getX(), player.getY(), targetSize, 0);
        
        if (!canSeePlayer || !inRange) {
            calcFollow(player, FOLLOW_DISTANCE, true, false);
        }
    }
}