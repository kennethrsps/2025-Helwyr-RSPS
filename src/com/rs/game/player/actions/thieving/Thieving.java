package com.rs.game.player.actions.thieving;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.rs.game.Animation;
import com.rs.game.ForceTalk;
import com.rs.game.World;
import com.rs.game.WorldObject;
import com.rs.game.WorldTile;
import com.rs.game.npc.NPC;
import com.rs.game.npc.others.randoms.RogueNPC;
import com.rs.game.player.Equipment;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.network.protocol.codec.decode.impl.ObjectHandler;
import com.rs.utils.Colors;
import com.rs.utils.Utils;

/**
 * Handles the Thieving Skill mechanics including stall theft, lock picking, and guard detection.
 * 
 * @author Dragonkk (original), Improved by Zeus @ 05.27.2025
 */
public class Thieving {

    // Constants for better maintainability
    private static final int THIEVING_ANIMATION_ID = 881;
    private static final double LOCK_PICK_BASE_DIFFICULTY = 40.0;
    private static final double LOCK_PICK_COMBAT_PENALTY = 50.0;
    private static final int GUARD_DETECTION_RANGE = 4;
    private static final int ROGUE_SPAWN_CHANCE = 50;
    private static final int STALL_RESPAWN_BASE_TIME = 1500;
    
    // Equipment bonuses
    private static final int THIEVING_GLOVES_BONUS = 12;
    private static final int THIEVING_CAPE_BONUS = 15;
    
    // Equipment IDs
    private static final int THIEVING_GLOVES_ID = 10075;
    private static final int THIEVING_CAPE_ID = 15349;
    
    // Rogue outfit IDs
    private static final int ROGUE_HAT_ID = 21482;
    private static final int ROGUE_CHEST_ID = 21480;
    private static final int ROGUE_LEGS_ID = 21481;
    private static final int ROGUE_BOOTS_ID = 21483;

    public enum Stalls {
        VEGETABLE(0, 2, new int[] { 1957, 1965, 1942, 1982, 1550 }, 1, 2, 10, 34381),
        CAKE(34384, 5, new int[] { 1891, 1897, 2309 }, 1, 2.5, 16, 34381),
        CRAFTING(0, 5, new int[] { 1755, 1592, 1597 }, 1, 7, 16, 34381),
        MONKEY_FOOD(0, 5, new int[] { 1963 }, 1, 7, 16, 34381),
        MONKEY_GENERAL(0, 5, new int[] { 1931, 2347, 590 }, 1, 7, 16, 34381),
        TEA_STALL(0, 5, new int[] { 712 }, 1, 7, 16, 34381),
        SILK_STALL(34383, 20, new int[] { 950 }, 1, 8, 24, 34381),
        WINE_STALL(14011, 22, new int[] { 1937, 1993, 1987, 1935, 7919 }, 1, 16, 27, 2046),
        SEED_STALL(7053, 27, new int[] { 5096, 5097, 5098, 5099, 5100, 5101, 5102, 5103, 5105 }, 30, 11, 10, 2047),
        FUR_STALL(34387, 35, new int[] { 6814, 958 }, 1, 15, 36, 34381),
        FISH_STALL(0, 42, new int[] { 331, 359, 377 }, 1, 16, 42, 34381),
        CROSSBOW_STALL(0, 49, new int[] { 877, 9420, 9440 }, 1, 11, 52, 34381),
        SILVER_STALL(0, 50, new int[] { 442 }, 1, 30, 54, 34381),
        SPICE_STALL(34386, 65, new int[] { 2007 }, 1, 80, 81, 34381),
        MAGIC_STALL(0, 65, new int[] { 556, 557, 554, 555, 563 }, 30, 80, 100, 34381),
        SCIMITAR_STALL(0, 65, new int[] { 1323 }, 1, 80, 100, 34381);

        private final int[] possibleItems;
        private final int requiredLevel;
        private final int maxAmount;
        private final int objectId;
        private final int replaceObjectId;
        private final double experienceReward;
        private final double respawnTimeSeconds;

        Stalls(int objectId, int requiredLevel, int[] possibleItems, int maxAmount, 
               double respawnTimeSeconds, double experienceReward, int replaceObjectId) {
            this.objectId = objectId;
            this.requiredLevel = requiredLevel;
            this.possibleItems = possibleItems.clone(); // Defensive copy
            this.maxAmount = maxAmount;
            this.respawnTimeSeconds = respawnTimeSeconds;
            this.experienceReward = experienceReward;
            this.replaceObjectId = replaceObjectId;
        }

        // Getters with improved naming
        public int getMaxAmount() { return maxAmount; }
        public double getExperienceReward() { return experienceReward; }
        public int getRandomItem() { 
            return possibleItems[ThreadLocalRandom.current().nextInt(possibleItems.length)]; 
        }
        public int getRequiredLevel() { return requiredLevel; }
        public int getObjectId() { return objectId; }
        public int getReplaceObjectId() { return replaceObjectId; }
        public double getRespawnTimeSeconds() { return respawnTimeSeconds; }
        
        // Get stall by object ID
        public static Stalls getByObjectId(int objectId) {
            for (Stalls stall : values()) {
                if (stall.getObjectId() == objectId) {
                    return stall;
                }
            }
            return null;
        }
    }

    /**
     * Checks for nearby guards and handles detection logic
     */
    public static void checkGuards(Player player) {
        if (player.getPerkManager().sleightOfHand) {
            return; // Skip guard detection if player has sleight of hand perk
        }

        NPC nearestGuard = findNearestGuard(player);
        if (nearestGuard != null) {
            handleGuardDetection(player, nearestGuard);
        }
    }

    /**
     * Finds the nearest guard within detection range
     */
    private static NPC findNearestGuard(Player player) {
        NPC nearestGuard = null;
        int shortestDistance = Integer.MAX_VALUE;

        for (int regionId : player.getMapRegionsIds()) {
            List<Integer> npcIndexes = World.getRegion(regionId).getNPCsIndexes();
            if (npcIndexes == null) continue;

            for (int npcIndex : npcIndexes) {
                NPC npc = World.getNPCs().get(npcIndex);
                if (!isValidGuard(npc, player)) continue;

                int distance = Utils.getDistance(npc.getX(), npc.getY(), 
                                               player.getX(), player.getY());
                if (distance < shortestDistance) {
                    nearestGuard = npc;
                    shortestDistance = distance;
                }
            }
        }
        return nearestGuard;
    }

    /**
     * Checks if an NPC is a valid guard for detection
     */
    private static boolean isValidGuard(NPC npc, Player player) {
        return npc != null && 
               isGuard(npc.getId()) && 
               !npc.isUnderCombat() && 
               !npc.isDead() && 
               npc.withinDistance(player, GUARD_DETECTION_RANGE) && 
               npc.clipedProjectile(player, true);
    }

    /**
     * Handles guard detection and response
     */
    private static void handleGuardDetection(Player player, NPC guard) {
        guard.setNextForceTalk(new ForceTalk("Hey, what do you think you are doing!"));
        player.sendMessage("Purchase the Sleight of Hand perk to never get caught.", true);
        guard.setTarget(player);
    }

    /**
     * Calculates thieving success chance bonus from equipment
     */
    private static int getEquipmentBonus(Player player) {
        int bonus = 0;
        if (player.getEquipment().getItem(Equipment.SLOT_HANDS) != null && 
            player.getEquipment().getItem(Equipment.SLOT_HANDS).getId() == THIEVING_GLOVES_ID) {
            bonus += THIEVING_GLOVES_BONUS;
        }
        if (player.getEquipment().getItem(Equipment.SLOT_CAPE) != null &&
            player.getEquipment().getItem(Equipment.SLOT_CAPE).getId() == THIEVING_CAPE_ID) {
            bonus += THIEVING_CAPE_BONUS;
        }
        return bonus;
    }

    /**
     * Main method to handle stall thieving
     */
    public static void handleStalls(final Player player, final WorldObject object) {
        // Validate player state
        if (!canPerformThieving(player)) {
            return;
        }

        Stalls stall = Stalls.getByObjectId(object.getId());
        if (stall == null) {
            return; // Not a valid thieving stall
        }

        // Check requirements
        if (!meetsRequirements(player, stall)) {
            return;
        }

        // Start thieving animation and process
        initiateThieving(player, object, stall);
    }

    /**
     * Checks if player can perform thieving action
     */
    private static boolean canPerformThieving(Player player) {
        if (player.getAttackedBy() != null && player.getAttackedByDelay() > Utils.currentTimeMillis()) {
            player.sendMessage("You can't do this while you're under combat.");
            return false;
        }
        return true;
    }

    /**
     * Checks if player meets the requirements for thieving from a stall
     */
    private static boolean meetsRequirements(Player player, Stalls stall) {
        if (player.getSkills().getLevel(Skills.THIEVING) < stall.getRequiredLevel()) {
            player.sendMessage("You need a thieving level of " + stall.getRequiredLevel() + 
                             " to steal from this.", true);
            return false;
        }

        if (!player.getInventory().hasFreeSlots()) {
            player.sendMessage("Inventory full. To make more room, sell, drop or bank something.", true);
            return false;
        }

        return true;
    }

    /**
     * Initiates the thieving process with animation and task scheduling
     */
    private static void initiateThieving(Player player, WorldObject object, Stalls stall) {
        player.setNextAnimation(new Animation(THIEVING_ANIMATION_ID));
        player.lock(2);

        WorldTasksManager.schedule(new ThievingTask(player, object, stall), 0, 0);
    }

    /**
     * Custom WorldTask for handling thieving completion
     */
    private static class ThievingTask extends WorldTask {
        private final Player player;
        private final WorldObject object;
        private final Stalls stall;
        private boolean itemsGiven = false;

        public ThievingTask(Player player, WorldObject object, Stalls stall) {
            this.player = player;
            this.object = object;
            this.stall = stall;
        }

        @Override
        public void run() {
            if (!World.containsObjectWithId(object, object.getId())) {
                stop();
                return;
            }

            if (!itemsGiven) {
                processSuccessfulTheft();
                itemsGiven = true;
            } else {
                respawnStall();
                stop();
            }
        }

        private void processSuccessfulTheft() {
            // Give items and experience
            int itemId = stall.getRandomItem();
            int amount = ThreadLocalRandom.current().nextInt(1, stall.getMaxAmount() + 1);
            
            player.getInventory().addItem(itemId, amount);
            
            // Apply experience with outfit bonus
            double experience = stall.getExperienceReward() * outfitBoost(player);
            player.getSkills().addXp(Skills.THIEVING, experience);

            // Update statistics
            player.addTimesStolen();
            player.sendMessage("You've successfully stolen from this stall; times thieved: " +
                             Colors.red + Utils.getFormattedNumber(player.getTimesStolen()) + "</col>.", true);

            // Check for guard detection
            checkGuards(player);

            // Handle clan revenue (commented out as in original)
            handleClanRevenue();

            // Random rogue spawn
            handleRogueSpawn();
        }

        private void handleClanRevenue() {
            // Add additional item as in original logic
            int bonusItemId = stall.getRandomItem();
            int bonusAmount = ThreadLocalRandom.current().nextInt(1, stall.getMaxAmount() + 1);
            player.getInventory().addItem(bonusItemId, bonusAmount);
        }

        private void handleRogueSpawn() {
            if (ThreadLocalRandom.current().nextInt(ROGUE_SPAWN_CHANCE) == 0) {
                new RogueNPC(new WorldTile(player), player);
                player.sendMessage("<col=ff0000>A Rogue appears out of nowhere.");
            }
        }

        private void respawnStall() {
            WorldObject emptyStall = new WorldObject(stall.getReplaceObjectId(), 10, 
                                                   object.getRotation(), object.getX(), 
                                                   object.getY(), object.getPlane());
            int respawnTime = (int) (STALL_RESPAWN_BASE_TIME * stall.getRespawnTimeSeconds());
            World.spawnObjectTemporary(emptyStall, respawnTime);
        }
    }

    /**
     * Checks if an NPC ID corresponds to a guard
     */
    public static boolean isGuard(int npcId) {
        return npcId == 32 || npcId == 21 || npcId == 2256 || npcId == 23;
    }

    /**
     * Handles door lock picking mechanics
     */
    public static boolean pickDoor(Player player, WorldObject object) {
        // Initialize numb fingers counter if not present
        if (player.getTemporaryAttributtes().get("numbFingers") == null) {
            player.getTemporaryAttributtes().put("numbFingers", 0);
        }

        int thievingLevel = player.getSkills().getLevel(Skills.THIEVING);
        int equipmentBonus = getEquipmentBonus(player);
        int failurePenalty = (Integer) player.getTemporaryAttributtes().get("numbFingers");
        
        // Calculate success chance
        boolean isInCombat = player.getAttackedByDelay() > 0;
        double baseSuccessChance = calculateLockPickSuccess(thievingLevel, equipmentBonus, failurePenalty, isInCombat);
        
        // Apply aura multiplier
        double finalSuccessChance = baseSuccessChance / player.getAuraManager().getThievingAccurayMultiplier();

        if (finalSuccessChance < (isInCombat ? LOCK_PICK_COMBAT_PENALTY : LOCK_PICK_BASE_DIFFICULTY) 
            && !player.getPerkManager().sleightOfHand) {
            handleLockPickFailure(player, failurePenalty);
            return false;
        }

        handleLockPickSuccess(player, object);
        return true;
    }

    /**
     * Calculates lock picking success rate
     */
    private static double calculateLockPickSuccess(int thievingLevel, int equipmentBonus, 
                                                 int failurePenalty, boolean isInCombat) {
        int adjustedLevel = ThreadLocalRandom.current().nextInt(thievingLevel + (equipmentBonus - failurePenalty)) + 1;
        double ratio = adjustedLevel / (double)(ThreadLocalRandom.current().nextInt(45, 51) + 1);
        return Math.round(ratio * thievingLevel);
    }

    /**
     * Handles lock picking failure
     */
    private static void handleLockPickFailure(Player player, int failurePenalty) {
        player.sendMessage("You fail to unlock the door and your hands begin to numb down.", true);
        player.sendMessage("Purchase the Sleight of Hand perk to never fail picking locks.", true);
        player.getTemporaryAttributtes().put("numbFingers", failurePenalty + 1);
    }

    /**
     * Handles successful lock picking
     */
    private static void handleLockPickSuccess(Player player, WorldObject object) {
        player.sendMessage("You successfully unlock the door.");
        int doorOpenTime = 1500 + ThreadLocalRandom.current().nextInt(1001);
        ObjectHandler.handleDoor(player, object, doorOpenTime);
    }

    /**
     * Calculates XP bonus from wearing rogue outfit pieces
     */
    public static double outfitBoost(Player player) {
        double xpBoost = 1.0;
        
        // Individual piece bonuses (1% each)
        if (player.getEquipment().getHatId() == ROGUE_HAT_ID) xpBoost *= 1.01;
        if (player.getEquipment().getChestId() == ROGUE_CHEST_ID) xpBoost *= 1.01;
        if (player.getEquipment().getLegsId() == ROGUE_LEGS_ID) xpBoost *= 1.01;
        if (player.getEquipment().getBootsId() == ROGUE_BOOTS_ID) xpBoost *= 1.01;
        
        // Full set bonus (additional 1%)
        if (hasFullRogueOutfit(player)) {
            xpBoost *= 1.01;
        }
        
        return xpBoost;
    }

    /**
     * Checks if player is wearing the complete rogue outfit
     */
    private static boolean hasFullRogueOutfit(Player player) {
        return player.getEquipment().getHatId() == ROGUE_HAT_ID &&
               player.getEquipment().getChestId() == ROGUE_CHEST_ID &&
               player.getEquipment().getLegsId() == ROGUE_LEGS_ID &&
               player.getEquipment().getBootsId() == ROGUE_BOOTS_ID;
    }
}