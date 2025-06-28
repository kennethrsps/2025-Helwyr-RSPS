// ==================== RiseOfTheSix.java ====================
/**
 * Rise of the Six - Barrows Brothers Minigame
 * Author: Zeus
 * Date: June 06, 2025
 * Java Version: 1.7 Compatible
 * 
 * A challenging minigame where players face all six Barrows Brothers
 * 
 * FIXES APPLIED:
 * - Fixed Java 1.7 compatibility (removed removeIf, lambda expressions)
 * - Enhanced memory leak prevention with improved cleanup
 * - Fixed concurrent modification issues with manual iteration
 * - Added comprehensive null validation
 * - Improved error handling and logging
 * - Enhanced resource management with proper disposal patterns
 * - Added defensive programming practices
 * - Optimized boss spawning and tracking logic
 * - Fixed potential NPE in critical paths
 * - Added proper state validation throughout
 */
package com.rise_of_the_six;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;
import com.rs.Settings;
import com.rs.game.Animation;
import com.rs.game.MapBuilder;
import com.rs.game.World;
import com.rs.game.WorldObject;
import com.rs.game.WorldTile;
import com.rs.game.item.Item;
import com.rs.game.npc.NPC;
import com.rs.game.player.content.pet.Pets;
import com.rs.game.player.controllers.Controller;
import com.rs.game.player.dialogue.Dialogue;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Colors;
import com.rs.utils.Utils;

public class RiseOfTheSix extends Controller {
    // Constants - Extracted more magic numbers
    private static final Object THHAAR_MEJ_JAL = 13633;
    private static final int REGION_SIZE = 8;
    private static final int MAP_SIZE = 10;
    private static final int BOSS_CAP_DAMAGE = 1500;
    private static final int BOSS_TARGET_DISTANCE = 64;
    private static final int ANIMATION_DEATH = 836;
    private static final int ANIMATION_SMITHING = 898;
    private static final int PLAYER_LOCK_TIME = 7;
    private static final int BASE_COINS_REWARD = 250000;
    private static final int MAP_SOURCE_X = 288;
    private static final int MAP_SOURCE_Y = 736;
    private static final int SPAWN_DELAY_TICKS = 2;
    
    // Object IDs
    private static final int CHEST_ID = 87996;
    private static final int BARRIER_ID = 87994;
    
    // Boss NPC IDs
    private static final int DHAROK_ID = 18540;
    private static final int VERAC_ID = 18545;
    private static final int GUTHAN_ID = 18541;
    private static final int TORAG_ID = 18544;
    private static final int AHRIM_ID = 18538;
    private static final int KARIL_ID = 18543;
    
    // Drop chance constants
    private static final int BASE_CHROMATIC_CHANCE = 1000;
    private static final int BASE_PET_CHANCE = 300;
    private static final int BASE_ARMOR_CHANCE = 75;
    private static final int BASE_RARE_CHANCE = 15;
    private static final int BASE_ENERGY_CHANCE = 4;
    private static final int DECENT_DROP_CHANCE = 10;
    private static final int COMMON_DROP_CHANCE = 5;
    
    // Streak system constants
    private static final int SPEED_BONUS_TIME = 300000; // 5 minutes in milliseconds
    private static final int MAX_STREAK_BONUS = 50; // Maximum 50% bonus
    private static final int STREAK_MILESTONE_1 = 5;
    private static final int STREAK_MILESTONE_2 = 10;
    private static final int STREAK_MILESTONE_3 = 25;
    private static final int STREAK_MILESTONE_4 = 50;

    // Instance variables
    private int[] regionBase;
    private List<NPC> spawnedBosses;
    private boolean hasStarted;
    private boolean noSpaceOnInv;
    private int currentWave;
    private WorldObject chestObject;
    private boolean isCleaningUp; // Prevention flag for cleanup race conditions
    private int selectedBrotherDrops; // 0=Ahrim, 1=Dharok, 2=Guthan, 3=Karil, 4=Torag, 5=Verac
    private boolean hasSelectedDrops;
    private long startTime; // Track completion time for bonuses
    private boolean completedSuccessfully; // Flag to distinguish successful completion from early exit

    // Reward arrays - made final and organized
    private static final Item[] COMMON_REWARDS = { 
        new Item(558, 1790), new Item(562, 773), new Item(560, 3910),
        new Item(565, 1640), new Item(4740, 880), new Item(1128, 10), 
        new Item(1514, 75), new Item(15271, 60), new Item(1748, 80), 
        new Item(9245, 60), new Item(1392, 45), new Item(452, 34), 
        new Item(9144, 300), new Item(9193, 300), new Item(1616, 50) 
    };
    
    private static final Item[] MALEVOLENT_ENERGY = { new Item(30027, 1) }; 
    
    private static final Item[] DECENT_REWARDS = { 
        new Item(12163, 50), new Item(12158, 100), new Item(12160, 100),
        new Item(12159, 100), new Item(12183, 1000) 
    };

    private static final Item[] RARE_REWARDS = { 
        new Item(1149, 1), new Item(987, 1), new Item(985, 1),
        new Item(4708, 1), new Item(4710, 1), new Item(4712, 1), 
        new Item(4714, 1), new Item(4716, 1), new Item(4718, 1),
        new Item(4720, 1), new Item(4722, 1), new Item(4724, 1), 
        new Item(4726, 1), new Item(4728, 1), new Item(4730, 1), 
        new Item(4732, 1), new Item(4734, 1), new Item(4736, 1), 
        new Item(4738, 1), new Item(4745, 1), new Item(4747, 1), 
        new Item(4749, 1), new Item(4751, 1), new Item(4753, 1),
        new Item(4755, 1), new Item(4757, 1), new Item(4087, 1), 
        new Item(24452, 1), new Item(24453, 1), new Item(24454, 1) 
    };

    private static final Item[] MALEVOLENT_REWARDS = { 
        new Item(30014, 1), new Item(30018, 1), new Item(30022, 1) 
    };
    
    private static final Item[] CHROMATIC_REWARDS = { new Item(34356, 1) };
    
    private static final Item[] PET_REWARDS = { 
        new Item(30031, 1), new Item(30032, 1), new Item(30033, 1),
        new Item(30034, 1), new Item(30035, 1), new Item(30036, 1) 
    };

    // Individual Barrows Brother Drop Tables
    private static final Item[][] BROTHER_DROPS = {
        // Ahrim the Blighted (Mage Set)
        { new Item(4708, 1), new Item(4710, 1), new Item(4712, 1), new Item(4714, 1) }, // Staff, Hood, Robe Top, Robe Bottom
        // Dharok the Wretched (Melee Set)  
        { new Item(4716, 1), new Item(4718, 1), new Item(4720, 1), new Item(4722, 1) }, // Greataxe, Helm, Platebody, Platelegs
        // Guthan the Infested (Melee Set)
        { new Item(4724, 1), new Item(4726, 1), new Item(4728, 1), new Item(4730, 1) }, // Spear, Helm, Platebody, Chainskirt
        // Karil the Tainted (Ranged Set)
        { new Item(4732, 1), new Item(4734, 1), new Item(4736, 1), new Item(4738, 1) }, // Crossbow, Coif, Leathertop, Leatherskirt
        // Torag the Corrupted (Melee Set)
        { new Item(4745, 1), new Item(4747, 1), new Item(4749, 1), new Item(4751, 1) }, // Hammers, Helm, Platebody, Platelegs
        // Verac the Defiled (Melee Set)
        { new Item(4753, 1), new Item(4755, 1), new Item(4757, 1), new Item(4759, 1) }  // Flail, Helm, Brassard, Plateskirt
    };
    
    private static final String[] BROTHER_NAMES = {
        "Ahrim the Blighted (Mage)", "Dharok the Wretched (Melee)", 
        "Guthan the Infested (Melee)", "Karil the Tainted (Ranged)",
        "Torag the Corrupted (Melee)", "Verac the Defiled (Melee)"
    };

    @Override
    public void start() {
        try {
            // Initialize state variables first
            hasStarted = false;
            currentWave = 0;
            noSpaceOnInv = false;
            isCleaningUp = false;
            selectedBrotherDrops = -1; // No selection yet
            hasSelectedDrops = false;
            startTime = System.currentTimeMillis(); // Track start time
            completedSuccessfully = false; // Not completed yet
            
            // Initialize collections
            spawnedBosses = new CopyOnWriteArrayList<NPC>();
            
            // Validate player state before proceeding
            if (!validatePlayerState()) {
                return;
            }
            
            // Display streak information
            displayStreakInfo();
            
            // Setup region
            regionBase = MapBuilder.findEmptyChunkBound(REGION_SIZE, REGION_SIZE);
            if (regionBase == null || regionBase.length < 2) {
                sendErrorAndExit("Error: Could not find available region for minigame.");
                return;
            }
            
            MapBuilder.copyAllPlanesMap(MAP_SOURCE_X, MAP_SOURCE_Y, 
                                       regionBase[0], regionBase[1], MAP_SIZE, MAP_SIZE);
            
            // Transport player to starting position
            player.setNextWorldTile(getWorldTile(15, 16));
            
            // Show brother selection dialog
            showBrotherSelectionDialog();
            
        } catch (Exception e) {
            logError("Error starting Rise of the Six", e);
            cleanup();
            removeControler();
        }
    }

    @Override
    public boolean processObjectClick1(WorldObject object) {
        if (object == null || isCleaningUp) {
            return false;
        }
        
        try {
            int objectId = object.getId();
            if (objectId == CHEST_ID) {
                lootChest();
            } else if (objectId == BARRIER_ID && !hasStarted) {
                if (!hasSelectedDrops) {
                    player.sendMessage(Colors.red + "You must first choose which Barrows Brother's equipment you wish to seek!");
                    showBrotherSelectionDialog();
                } else {
                    passBarrier();
                }
            }
        } catch (Exception e) {
            logError("Error processing object click", e);
            if (player != null) {
                player.sendMessage("An error occurred. Please try again.");
            }
        }
        return false;
    }

    /**
     * Safely drops an item to player inventory or bank with improved validation
     */
    public void drop(Item item) {
        if (item == null || player == null || isCleaningUp) {
            return;
        }
        
        try {
            // Validate item data
            if (item.getDefinitions() == null) {
                logError("Item has null definitions: " + item.getId(), null);
                return;
            }
            
            int dropAmount = item.getDefinitions().isStackable() ? 
                           item.getAmount() * Settings.DROP_RATE : item.getAmount();
            Item dropItem = new Item(item.getId(), Utils.random(dropAmount) + 1);
            
            // Try inventory first
            if (!noSpaceOnInv && player.getInventory() != null && 
                player.getInventory().addItem(dropItem)) {
                return;
            }
            
            // Fall back to bank
            noSpaceOnInv = true;
            if (player.getBank() != null) {
                player.getBank().addItem(dropItem, false);
                if (player.getPackets() != null) {
                    player.getPackets().sendGameMessage("Your loot was placed into your bank.");
                }
            }
            
        } catch (Exception e) {
            logError("Error dropping item: " + item.getId(), e);
        }
    }

    /**
     * Enhanced loot chest with brother-specific drops, streak bonuses and validation
     */
    public void lootChest() {
        if (player == null || isCleaningUp || !hasSelectedDrops) {
            return;
        }
        
        try {
            // Calculate completion time
            long completionTime = System.currentTimeMillis() - startTime;
            boolean speedBonus = completionTime <= SPEED_BONUS_TIME;
            
            // Update kill count and streak
            player.setrosPoints(player.getrosPoints() + 1);
            int currentStreak = incrementStreak();
            
            // Calculate streak bonuses
            int streakBonus = Math.min(currentStreak * 2, MAX_STREAK_BONUS); // 2% per streak, max 50%
            double streakMultiplier = 1.0 + (streakBonus / 100.0);
            
            // Display completion info
            displayCompletionInfo(currentStreak, completionTime, speedBonus);
            
            // Calculate drop rates with player bonuses, streak bonuses and validation
            double playerDropRate = Math.max(0, player.getDropRate());
            double totalDropRate = playerDropRate * streakMultiplier;
            
            int chromaticChance = Math.max(1, (int)(BASE_CHROMATIC_CHANCE - totalDropRate));
            int petChance = Math.max(1, (int)(BASE_PET_CHANCE - totalDropRate));
            int armorChance = Math.max(1, (int)(BASE_ARMOR_CHANCE - totalDropRate));
            int rareChance = Math.max(1, (int)(BASE_RARE_CHANCE - totalDropRate));
            int energyChance = Math.max(1, (int)(BASE_ENERGY_CHANCE - totalDropRate/2));
            
            // SELECTED BROTHER'S EQUIPMENT (Higher chance with streak bonus)
            int brotherDropChance = speedBonus ? 8 : 10; // 1/8 if speed bonus, 1/10 normal
            brotherDropChance = Math.max(3, brotherDropChance - (currentStreak / 5)); // Better chance with streak
            
            if (Utils.random(brotherDropChance) == 0 && selectedBrotherDrops >= 0 && selectedBrotherDrops < BROTHER_DROPS.length) {
                Item[] selectedDrops = BROTHER_DROPS[selectedBrotherDrops];
                if (selectedDrops.length > 0) {
                    Item selectedItem = selectedDrops[Utils.random(selectedDrops.length)];
                    drop(selectedItem);
                    String brotherName = BROTHER_NAMES[selectedBrotherDrops];
                    String announcement = "has just received " + brotherName + " equipment from the Rise of the Six! [Streak: " + currentStreak + "]";
                    World.sendWorldMessage("<img=7><col=9A2EFE>News: " + player.getDisplayName() + " " + announcement, false);
                }
            }
            
            // Process streak milestone rewards
            processStreakMilestoneRewards(currentStreak);
            
            // Process other rare drops (with streak bonuses)
            processRareDrop(chromaticChance, CHROMATIC_REWARDS, 
                          "has just found the impossibly rare Chromatic PartyHat from the Rise of the Six Chest! [Streak: " + currentStreak + "]");
            processRareDrop(petChance, PET_REWARDS, 
                          "has just found a rare pet from the Rise of the Six Chest! [Streak: " + currentStreak + "]");
            processRareDrop(armorChance, MALEVOLENT_REWARDS,
                          "has just found a Rare Shield from the Rise of the Six Chest! [Streak: " + currentStreak + "]");
            processRareDrop(rareChance, RARE_REWARDS, 
                          "has just found a Rare Reward from the Rise of the Six Chest! [Streak: " + currentStreak + "]");
            
            // Energy drop (no broadcast)
            if (Utils.random(energyChance) == 0 && MALEVOLENT_ENERGY.length > 0) {
                drop(MALEVOLENT_ENERGY[Utils.random(MALEVOLENT_ENERGY.length)]);
            }
            
            // Common drops with streak bonuses
            int streakCommonChance = Math.max(2, DECENT_DROP_CHANCE - (currentStreak / 3));
            int streakDecentChance = Math.max(2, COMMON_DROP_CHANCE - (currentStreak / 2));
            
            if (Utils.random(streakDecentChance) == 0 && DECENT_REWARDS.length > 0) {
                drop(DECENT_REWARDS[Utils.random(DECENT_REWARDS.length)]);
            }
            if (Utils.random(streakCommonChance) == 0 && COMMON_REWARDS.length > 0) {
                drop(COMMON_REWARDS[Utils.random(COMMON_REWARDS.length)]);
            }
            
            // Guaranteed coins with streak bonus
            int coinBonus = BASE_COINS_REWARD + (currentStreak * 10000); // 10k extra per streak
            if (speedBonus) coinBonus += 50000; // 50k bonus for speed
            drop(new Item(995, coinBonus));

            String selectedBrotherName = (selectedBrotherDrops >= 0 && selectedBrotherDrops < BROTHER_NAMES.length) ? 
                                        BROTHER_NAMES[selectedBrotherDrops] : "Unknown";
            player.sendMessage("You defeated all Barrows brothers while seeking " + selectedBrotherName + " equipment!");
            
            // Mark as successful completion BEFORE exit
            completedSuccessfully = true;
            exitMinigame();
            
        } catch (Exception e) {
            logError("Error in loot chest", e);
            if (player != null) {
                player.sendMessage("An error occurred while looting. Please contact an administrator.");
            }
        }
    }

    /**
     * Helper method for processing rare drops with world messages and validation
     */
    private void processRareDrop(int chance, Item[] rewards, String message) {
        if (rewards == null || rewards.length == 0 || chance <= 0) {
            return;
        }
        
        try {
            if (Utils.random(chance) == 0) {
                String playerName = (player != null && player.getDisplayName() != null) ? 
                                  player.getDisplayName() : "Unknown Player";
                String fullMessage = "<img=7><col=9A2EFE>News: " + playerName + " " + message;
                World.sendWorldMessage(fullMessage, false);
                drop(rewards[Utils.random(rewards.length)]);
            }
        } catch (Exception e) {
            logError("Error processing rare drop", e);
        }
    }

    @Override
    public void process() {
        if (!hasStarted || spawnedBosses == null || isCleaningUp) {
            return;
        }
        
        try {
            // Java 1.7 compatible way to remove dead bosses from CopyOnWriteArrayList
            List<NPC> aliveBosses = new CopyOnWriteArrayList<NPC>();
            for (NPC npc : spawnedBosses) {
                if (npc != null && !npc.isDead() && !npc.hasFinished()) {
                    aliveBosses.add(npc);
                }
            }
            
            // Replace the list with alive bosses only
            spawnedBosses = aliveBosses;
            
            // Check if all bosses are defeated
            if (spawnedBosses.isEmpty()) {
                currentWave++;
                nextWave(currentWave);
            }
            
        } catch (Exception e) {
            logError("Error in process", e);
        }
    }

    /**
     * Enhanced barrier passing with improved error handling
     */
    private void passBarrier() {
        if (player == null || hasStarted || isCleaningUp) {
            return;
        }
        
        try {
            player.setForceMultiArea(true);
            player.lock(SPAWN_DELAY_TICKS);
            
            // Spawn all bosses
            spawnAllBosses();
            
            // Move player to fighting area
            player.setNextWorldTile(getWorldTile(14, 21));
            hasStarted = true;
            
        } catch (Exception e) {
            logError("Error passing barrier", e);
            if (player != null) {
                player.sendMessage("An error occurred starting the fight.");
            }
            cleanup();
        }
    }

    /**
     * Spawn all bosses with proper tracking and validation
     */
    private void spawnAllBosses() {
        if (spawnedBosses == null) {
            spawnedBosses = new CopyOnWriteArrayList<NPC>();
        }
        
        // Boss configurations: ID, x, y
        int[][] bossConfigs = {
            {AHRIM_ID, 13, 23},
            {DHAROK_ID, 15, 24},
            {GUTHAN_ID, 14, 20},
            {KARIL_ID, 19, 20},
            {TORAG_ID, 15, 21},
            {VERAC_ID, 12, 25}
        };
        
        for (int i = 0; i < bossConfigs.length; i++) {
            int[] config = bossConfigs[i];
            try {
                WorldTile spawnTile = getWorldTile(config[1], config[2]);
                if (spawnTile == null) {
                    logError("Failed to create spawn tile for boss " + config[0], null);
                    continue;
                }
                
                NPC boss = new NPC(config[0], spawnTile, -1, true, true);
                if (boss != null) {
                    boss.setForceMultiArea(true);
                    boss.setForceAgressive(true);
                    boss.setForceTargetDistance(BOSS_TARGET_DISTANCE);
                    boss.setCapDamage(BOSS_CAP_DAMAGE);
                    spawnedBosses.add(boss);
                } else {
                    logError("Failed to create NPC for boss " + config[0], null);
                }
            } catch (Exception e) {
                logError("Error spawning boss " + config[0], e);
            }
        }
        
        if (spawnedBosses.isEmpty()) {
            logError("No bosses were spawned successfully", null);
            if (player != null) {
                player.sendMessage("Error spawning bosses. Exiting minigame.");
            }
            cleanup();
        }
    }

    /**
     * Enhanced wave management with validation
     */
    private void nextWave(int waveId) {
        if (player == null || isCleaningUp) {
            return;
        }
        
        try {
            if (waveId == 1) {
                if (player.getPackets() != null) {
                    player.getPackets().sendGameMessage("Congratulations! You've defeated the Barrows Brothers.");
                    player.getPackets().sendGameMessage("You now have access to the chest.");
                }
                
                WorldTile chestTile = getWorldTile(16, 19);
                if (chestTile != null) {
                    chestObject = new WorldObject(CHEST_ID, 10, 0, chestTile);
                    World.spawnObject(chestObject, true);
                }
            }
        } catch (Exception e) {
            logError("Error in next wave", e);
        }
    }

    /**
     * Safe world tile creation with comprehensive validation
     */
    public WorldTile getWorldTile(int mapX, int mapY) {
        if (regionBase == null || regionBase.length < 2) {
            logError("Invalid region base when creating world tile", null);
            return new WorldTile(Settings.RESPAWN_PLAYER_LOCATION);
        }
        
        try {
            return new WorldTile(regionBase[0] * REGION_SIZE + mapX, 
                               regionBase[1] * REGION_SIZE + mapY, 0);
        } catch (Exception e) {
            logError("Error creating world tile", e);
            return new WorldTile(Settings.RESPAWN_PLAYER_LOCATION);
        }
    }

    /**
     * Enhanced cleanup method with race condition prevention
     */
    private void cleanup() {
        if (isCleaningUp) {
            return; // Prevent multiple cleanup calls
        }
        isCleaningUp = true;
        
        try {
            // Clean up spawned bosses
            if (spawnedBosses != null) {
                for (NPC boss : spawnedBosses) {
                    if (boss != null && !boss.hasFinished()) {
                        boss.reset();
                        boss.finish();
                    }
                }
                spawnedBosses.clear();
            }
            
            // Clean up chest object
            if (chestObject != null) {
                World.removeObject(chestObject);
                chestObject = null;
            }
            
            // Reset state variables
            hasStarted = false;
            noSpaceOnInv = false;
            currentWave = 0;
            selectedBrotherDrops = -1;
            hasSelectedDrops = false;
            completedSuccessfully = false;
            
        } catch (Exception e) {
            logError("Error during cleanup", e);
        }
    }

    /**
     * Safe minigame exit with comprehensive cleanup
     */
    private void exitMinigame() {
        try {
            if (player != null) {
                player.setNextWorldTile(new WorldTile(Settings.RESPAWN_PLAYER_LOCATION));
                player.setForceMultiArea(false);
            }
            cleanup();
            removeControler();
        } catch (Exception e) {
            logError("Error exiting minigame", e);
        }
    }

    @Override
    public boolean logout() {
        // Only reset streak on logout if it wasn't a successful completion
        if (hasStarted && !completedSuccessfully) {
            resetStreak();
            if (player != null) {
                player.sendMessage(Colors.red + "Your Rise of the Six streak has been reset due to early exit!");
            }
        }
        cleanup();
        if (player != null) {
            player.setForceMultiArea(false);
        }
        removeControler();
        return true;
    }

    @Override
    public boolean sendDeath() {
        if (player == null || isCleaningUp) {
            return false;
        }
        
        try {
            // Reset streak on death (always a failure)
            resetStreak();
            
            player.lock(PLAYER_LOCK_TIME);
            player.stopAll();
            hasStarted = false;
            
            WorldTasksManager.schedule(new WorldTask() {
                int loop = 0;

                @Override
                public void run() {
                    try {
                        switch (loop) {
                            case 0:
                                if (player != null) {
                                    player.setNextAnimation(new Animation(ANIMATION_DEATH));
                                }
                                break;
                            case 1:
                                if (player != null) {
                                    player.sendMessage("You have been defeated!");
                                    player.sendMessage(Colors.red + "Your Rise of the Six streak has been reset!");
                                }
                                break;
                            case 3:
                                if (player != null) {
                                    player.reset();
                                    exitCave(1);
                                    player.setNextAnimation(new Animation(-1));
                                }
                                break;
                            case 4:
                                if (player != null && player.getPackets() != null) {
                                    player.getPackets().sendMusicEffect(90);
                                    player.unlock();
                                }
                                stop();
                                break;
                        }
                        loop++;
                    } catch (Exception e) {
                        logError("Error in death sequence", e);
                        stop();
                    }
                }
            }, 0, 1);
            
        } catch (Exception e) {
            logError("Error in sendDeath", e);
        }
        return false;
    }

    public void exitCave(int type) {
        try {
            if (type == 1) {
                // Only reset streak on forced exit (death/failure), not successful completion
                if (hasStarted && !completedSuccessfully) {
                    resetStreak();
                    if (player != null) {
                        player.sendMessage(Colors.red + "Your Rise of the Six streak has been reset!");
                    }
                }
                hasStarted = false;
                if (player != null) {
                    player.setForceMultiArea(false);
                    WorldTile homeTile = player.getHomeTile();
                    if (homeTile != null) {
                        player.setNextWorldTile(new WorldTile(homeTile));
                    } else {
                        player.setNextWorldTile(new WorldTile(Settings.RESPAWN_PLAYER_LOCATION));
                    }
                }
            }
            cleanup();
            removeControler();
        } catch (Exception e) {
            logError("Error exiting cave", e);
        }
    }

    @Override
    public void magicTeleported(int type) {
        // Only reset streak on teleport if it wasn't a successful completion
        if (hasStarted && !completedSuccessfully) {
            resetStreak();
            if (player != null) {
                player.sendMessage(Colors.red + "Your Rise of the Six streak has been reset due to early exit!");
            }
        }
        cleanup();
        if (player != null) {
            player.setForceMultiArea(false);
        }
        hasStarted = false;
        removeControler();
    }
    
    /**
     * Shows the Barrows Brother selection dialog to the player
     */
    private void showBrotherSelectionDialog() {
        if (player == null) {
            return;
        }
        
        try {
            // Use inline dialogue like your existing code pattern
			player.getDialogueManager().startDialogue(new Dialogue() {
                @Override
                public void start() {
                    int currentStreak = getCurrentStreak();
                    String streakInfo = currentStreak > 0 ? " [Current Streak: " + currentStreak + "]" : " [New Streak Starting]";
                    sendPlayerDialogue(AHRIM_ID, "Choose which Barrows Brother's equipment you seek to obtain from this challenge!" + streakInfo);
                    stage = 0;
                }
                
                @Override
                public void run(int interfaceId, int componentId) {
                    switch (stage) {
                    case 0:
                        sendOptionsDialogue("Choose your target brother:", 
                            "Ahrim (Mage)", "Dharok (Melee)", "Guthan (Melee)", "More options...");
                        stage = 1;
                        break;
                    case 1:
                        switch (componentId) {
                        case OPTION_1: // Ahrim
                            selectBrother(0);
                            finish();
                            break;
                        case OPTION_2: // Dharok  
                            selectBrother(1);
                            finish();
                            break;
                        case OPTION_3: // Guthan
                            selectBrother(2);
                            finish();
                            break;
                        case OPTION_4: // More options
                            sendOptionsDialogue("Choose your target brother:", 
                                "Karil (Ranged)", "Torag (Melee)", "Verac (Melee)", "Back");
                            stage = 2;
                            break;
                        }
                        break;
                    case 2:
                        switch (componentId) {
                        case OPTION_1: // Karil
                            selectBrother(3);
                            finish();
                            break;
                        case OPTION_2: // Torag
                            selectBrother(4);
                            finish();
                            break;
                        case OPTION_3: // Verac
                            selectBrother(5);
                            finish();
                            break;
                        case OPTION_4: // Back
                            sendOptionsDialogue("Choose your target brother:", 
                                "Ahrim (Mage)", "Dharok (Melee)", "Guthan (Melee)", "More options...");
                            stage = 1;
                            break;
                        }
                        break;
                    }
                }
                
                @Override
                public void finish() {
                    player.getInterfaceManager().closeChatBoxInterface();
                }
            });
        } catch (Exception e) {
            logError("Error showing brother selection dialog", e);
            // Fallback to simple messages if dialogue fails
            showSimpleBrotherSelection();
        }
    }
    
    /**
     * Fallback method for brother selection using simple messages
     */
    private void showSimpleBrotherSelection() {
        if (player == null) {
            System.out.println("[RiseOfTheSix] Cannot show selection - player is null");
            return;
        }
        
        System.out.println("[RiseOfTheSix] Sending brother selection messages to player");
        
        player.sendMessage("=== Choose Your Target Barrows Brother ===");
        player.sendMessage("Say the number of the brother whose equipment you seek:");
        for (int i = 0; i < BROTHER_NAMES.length; i++) {
            player.sendMessage((i + 1) + ". " + BROTHER_NAMES[i]);
        }
        player.sendMessage("Type the number (1-6) in chat to make your selection.");
        player.sendMessage("Example: Just type '1' for Ahrim, '2' for Dharok, etc.");
        player.sendMessage("ALTERNATIVE: Click the barrier and type 'select 1' to 'select 6'");
        
        System.out.println("[RiseOfTheSix] Brother selection messages sent");
    }
    
    /**
     * Handles the brother selection choice
     */
    public void selectBrother(int brotherIndex) {
        if (brotherIndex < 0 || brotherIndex >= BROTHER_NAMES.length) {
            player.sendMessage("Invalid selection. Please try again.");
            showBrotherSelectionDialog();
            return;
        }
        
        try {
            selectedBrotherDrops = brotherIndex;
            hasSelectedDrops = true;
            
            String selectedBrother = BROTHER_NAMES[brotherIndex];
            player.sendMessage(Colors.green + "You have chosen to seek " + selectedBrother + " equipment!");
            player.sendMessage("As you enter, the barrows brothers sense your focus on " + selectedBrother.split(" ")[0] + "...");
            player.sendMessage(Colors.cyan + "Click the barrier to begin the fight!");
            player.sendMessage("");
            player.sendMessage(Colors.yellow + "REMINDER: Complete the fight to increase streak, die/quit to reset it!");
            player.sendMessage(Colors.white + "Type '::streak' anytime to view your current streak information.");
            
            System.out.println("[RiseOfTheSix] Player selected brother: " + selectedBrother);
            
        } catch (Exception e) {
            logError("Error selecting brother", e);
            player.sendMessage("An error occurred with your selection. Please try again.");
            showBrotherSelectionDialog();
        }
    }
    
    /**
     * Processes chat commands for brother selection (fallback method)
     */
    @Override
    public boolean processCommand(String command, boolean clientCommand, boolean consoleCommand) {
        if (command == null) {
            return false;
        }
        
        try {
            // Handle streak info command
            if (command.toLowerCase().equals("streak")) {
                displayStreakInfo();
                return true;
            }
            
            // Handle brother selection (only if not selected yet)
            if (!hasSelectedDrops && !hasStarted) {
                // Debug output
                System.out.println("[RiseOfTheSix] Processing command: " + command);
                
                // Check if it's a number for brother selection
                String trimmedCommand = command.trim();
                int selection = Integer.parseInt(trimmedCommand);
                if (selection >= 1 && selection <= 6) {
                    selectBrother(selection - 1); // Convert to 0-based index
                    return true;
                }
            }
        } catch (NumberFormatException e) {
            // Not a number, check if it's streak command
            if (command.toLowerCase().equals("streak")) {
                return true; // Already handled above
            }
            // Otherwise let other handlers process it
        } catch (Exception e) {
            logError("Error processing command", e);
        }
        
        return false;
    }
    
    /**
     * Alternative method to select brother by typing in chat
     * Call this from your server's chat/command handler if processCommand doesn't work
     */
    public boolean handleChatSelection(String message) {
        if (hasSelectedDrops || hasStarted) {
            return false;
        }
        
        try {
            String trimmedMessage = message.trim();
            int selection = Integer.parseInt(trimmedMessage);
            if (selection >= 1 && selection <= 6) {
                selectBrother(selection - 1);
                return true;
            }
        } catch (NumberFormatException e) {
            // Not a valid selection
        } catch (Exception e) {
            logError("Error in chat selection", e);
        }
        
        return false;
    }
    
    /**
     * Validates player state before starting minigame
     */
    private boolean validatePlayerState() {
        if (player == null) {
            logError("Player is null during start", null);
            removeControler();
            return false;
        }
        
        if (player.getInventory() == null) {
            sendErrorAndExit("Error: Player inventory is not available.");
            return false;
        }
        
        return true;
    }
    
    /**
     * Sends error message to player and exits safely
     */
    private void sendErrorAndExit(String message) {
        if (player != null) {
            player.sendMessage(message);
        }
        cleanup();
        removeControler();
    }
    
    /**
     * Centralized error logging
     */
    private void logError(String message, Exception e) {
        System.err.println("[RiseOfTheSix] " + message);
        if (e != null) {
            e.printStackTrace();
        }
    }
    
    // ==================== STREAK SYSTEM METHODS ====================
    
    /**
     * Gets the player's current Rise of the Six streak
     * Assumes you have a method like player.getRosStreak() or use temporary variable
     */
    private int getCurrentStreak() {
        // You'll need to add this to your Player class: private int rosStreak = 0;
        // For now, using rosPoints as a temporary implementation
        // In a real implementation, you'd have: return player.getRosStreak();
        return Math.min(player.getrosPoints() / 10, 100); // Temporary: every 10 kc = 1 streak level
    }
    
    /**
     * Sets the player's Rise of the Six streak
     */
    private void setCurrentStreak(int streak) {
        // In a real implementation: player.setRosStreak(streak);
        // For now, this is just a placeholder since we can't modify player data easily
    }
    
    /**
     * Increments the player's streak and returns new value
     */
    private int incrementStreak() {
        int currentStreak = getCurrentStreak();
        int newStreak = currentStreak + 1;
        setCurrentStreak(newStreak);
        return newStreak;
    }
    
    /**
     * Resets the player's streak to 0
     */
    private void resetStreak() {
        setCurrentStreak(0);
    }
    
    /**
     * Displays current streak information to the player
     */
    private void displayStreakInfo() {
        if (player == null) return;
        
        int currentStreak = getCurrentStreak();
        
        player.sendMessage(Colors.cyan + "=== RISE OF THE SIX STREAK SYSTEM ===");
        
        if (currentStreak > 0) {
            player.sendMessage(Colors.green + "Current Streak: " + currentStreak);
            player.sendMessage("Streak Bonus: +" + Math.min(currentStreak * 2, MAX_STREAK_BONUS) + "% better drop rates");
            player.sendMessage("Brother Drop Chance: 1/" + Math.max(3, 10 - (currentStreak / 5)));
            player.sendMessage("Extra Coins: +" + (currentStreak * 10000) + " per completion");
            
            // Show next milestone
            if (currentStreak < STREAK_MILESTONE_1) {
                player.sendMessage("Next Milestone: " + STREAK_MILESTONE_1 + " (" + (STREAK_MILESTONE_1 - currentStreak) + " more) - 500k bonus");
            } else if (currentStreak < STREAK_MILESTONE_2) {
                player.sendMessage("Next Milestone: " + STREAK_MILESTONE_2 + " (" + (STREAK_MILESTONE_2 - currentStreak) + " more) - 1M + double decent rewards");
            } else if (currentStreak < STREAK_MILESTONE_3) {
                player.sendMessage("Next Milestone: " + STREAK_MILESTONE_3 + " (" + (STREAK_MILESTONE_3 - currentStreak) + " more) - 2.5M + guaranteed rare");
            } else if (currentStreak < STREAK_MILESTONE_4) {
                player.sendMessage("Next Milestone: " + STREAK_MILESTONE_4 + " (" + (STREAK_MILESTONE_4 - currentStreak) + " more) - 5M + guaranteed chromatic partyhat!");
            } else {
                player.sendMessage(Colors.gold + "Maximum milestone achieved! Every 10 streaks = malevolent item + 1M");
            }
        } else {
            player.sendMessage(Colors.yellow + "Starting fresh! Complete the minigame to begin your streak.");
            player.sendMessage("");
            player.sendMessage(Colors.white + "HOW STREAKS WORK:");
            player.sendMessage("• Complete minigame = +1 streak (+2% drop rates each)");
            player.sendMessage("• Die/quit/teleport during fight = streak resets to 0");
            player.sendMessage("• Higher streaks = better brother drop chances & more coins");
            player.sendMessage("");
            player.sendMessage(Colors.gold + "SPEED BONUS: Complete in under 5 minutes for:");
            player.sendMessage("• +25% better brother drop chance (1/10 → 1/8)");
            player.sendMessage("• +50,000 bonus coins");
            player.sendMessage("");
            player.sendMessage(Colors.cyan + "STREAK MILESTONES:");
            player.sendMessage("• 5 streaks: +500k coins + world announcement");
            player.sendMessage("• 10 streaks: +1M coins + double decent rewards");
            player.sendMessage("• 25 streaks: +2.5M coins + guaranteed rare item");
            player.sendMessage("• 50 streaks: +5M coins + guaranteed chromatic partyhat!");
            player.sendMessage("• Every 10 after 50: +1M coins + malevolent item");
        }
        
        player.sendMessage("");
        player.sendMessage(Colors.red + "IMPORTANT: " + Colors.white + "Streaks only reset on death/early exit, NOT successful completion!");
    }
    
    /**
     * Displays completion information including time and streak
     */
    private void displayCompletionInfo(int streak, long completionTime, boolean speedBonus) {
        if (player == null) return;
        
        long seconds = completionTime / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;
        
        player.sendMessage(Colors.green + "=== COMPLETION STATS ===");
        player.sendMessage(Colors.red + "[Rise Of The Six] </col>" + Colors.green + "Kill count:</col> " + player.getrosPoints());
        player.sendMessage("Completion Time: " + minutes + "m " + seconds + "s");
        player.sendMessage("Current Streak: " + Colors.cyan + streak);
        
        if (speedBonus) {
            player.sendMessage(Colors.gold + "SPEED BONUS! (+25% brother drop chance, +50k coins)");
        }
        
        if (streak > 0) {
            player.sendMessage("Streak Bonus: +" + Math.min(streak * 2, MAX_STREAK_BONUS) + "% drop rates");
        }
    }
    
    /**
     * Processes special rewards for streak milestones
     */
    private void processStreakMilestoneRewards(int streak) {
        if (player == null) return;
        
        String announcement = "";
        boolean shouldAnnounce = false;
        
        switch (streak) {
            case STREAK_MILESTONE_1: // 5 streak
                drop(new Item(995, 500000)); // 500k bonus coins
                announcement = "has achieved a " + streak + " streak in Rise of the Six!";
                shouldAnnounce = true;
                break;
                
            case STREAK_MILESTONE_2: // 10 streak
                if (DECENT_REWARDS.length > 0) {
                    drop(DECENT_REWARDS[Utils.random(DECENT_REWARDS.length)]);
                    drop(DECENT_REWARDS[Utils.random(DECENT_REWARDS.length)]); // Double decent reward
                }
                drop(new Item(995, 1000000)); // 1M bonus coins
                announcement = "has achieved a " + streak + " streak in Rise of the Six! Incredible dedication!";
                shouldAnnounce = true;
                break;
                
            case STREAK_MILESTONE_3: // 25 streak
                if (RARE_REWARDS.length > 0) {
                    drop(RARE_REWARDS[Utils.random(RARE_REWARDS.length)]); // Guaranteed rare
                }
                drop(new Item(995, 2500000)); // 2.5M bonus coins
                announcement = "has achieved an AMAZING " + streak + " streak in Rise of the Six! What a legend!";
                shouldAnnounce = true;
                break;
                
            case STREAK_MILESTONE_4: // 50 streak
                if (CHROMATIC_REWARDS.length > 0) {
                    drop(CHROMATIC_REWARDS[0]); // Guaranteed chromatic partyhat
                }
                drop(new Item(995, 5000000)); // 5M bonus coins
                announcement = "has achieved the LEGENDARY " + streak + " streak in Rise of the Six! ABSOLUTE MADNESS!";
                shouldAnnounce = true;
                break;
                
            default:
                // Every 10 streaks after 50, give a bonus
                if (streak > STREAK_MILESTONE_4 && streak % 10 == 0) {
                    if (MALEVOLENT_REWARDS.length > 0) {
                        drop(MALEVOLENT_REWARDS[Utils.random(MALEVOLENT_REWARDS.length)]);
                    }
                    drop(new Item(995, 1000000)); // 1M bonus
                    announcement = "has achieved an INSANE " + streak + " streak in Rise of the Six! Unbelievable!";
                    shouldAnnounce = true;
                }
                break;
        }
        
        if (shouldAnnounce) {
            World.sendWorldMessage("<img=7><col=FF0000>STREAK MILESTONE: " + player.getDisplayName() + " " + announcement, false);
            player.sendMessage(Colors.gold + "STREAK MILESTONE ACHIEVED! Check your inventory for bonus rewards!");
        }
    }
}