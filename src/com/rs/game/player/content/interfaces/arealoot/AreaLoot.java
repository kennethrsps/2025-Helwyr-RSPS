package com.rs.game.player.content.interfaces.arealoot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.cores.CoresManager;
import com.rs.game.Region;
import com.rs.game.World;
import com.rs.game.item.FloorItem;
import com.rs.game.item.Item;
import com.rs.game.player.Player;
import com.rs.game.player.dialogue.Dialogue;
import com.rs.utils.Colors;
import com.rs.utils.Logger;

/**
 * Area Loot System - Allows players to see and collect nearby ground items
 * 
 * Features:
 * - Shows items within 3 tiles of the player
 * - Filtering by monetary value
 * - Individual item pickup or bulk collection
 * - Automatic updates every 500ms
 * - Multi-area conflict prevention safeguards
 * 
 * 
 * @refactored for performance and memory management with multi-area safety
 */
public class AreaLoot {
    
    // Interface constants
    private static final int INTERFACE_ID = 3006;
    private static final int COMPONENT_CLOSE = 11;
    private static final int COMPONENT_CONTAINER = 14;
    private static final int COMPONENT_SETTINGS = 21;
    private static final int COMPONENT_CUSTOM_LOOT = 16;
    private static final int COMPONENT_LOOT_ALL = 19;
    
    // System constants
    private static final int MAX_DISTANCE = 3;
    private static final int UPDATE_INTERVAL_MS = 500;
    private static final int MAX_ITEMS_DISPLAY = 50;
    private static final String MESSAGE_PREFIX = Colors.red + "[AreaLoot]</col> ";
    
    // Multi-area safety constants
    private static final int COLLECTION_COOLDOWN_MS = 100; // Minimum time between collection attempts
    private static final int MAX_COLLECTION_ATTEMPTS_PER_SECOND = 10;
    private static final int MULTI_AREA_EXTRA_VALIDATION_DELAY = 50; // Extra delay for multi-areas
    
    // Loot filtering modes
    public enum LootFilterMode {
        ALL_ITEMS(0, "All Items"),
        COINS_ONLY(1, "Coins Only");
        
        private final int mode;
        private final String description;
        
        LootFilterMode(int mode, String description) {
            this.mode = mode;
            this.description = description;
        }
        
        public int getMode() { return mode; }
        public String getDescription() { return description; }
    }
    
    // Loot value presets - optimized for gold coin collection
    public enum LootValuePreset {
        RESET(0L, "reset (collect everything)"),
        VALUE_300(300L, "300 coins and up"),
        VALUE_1K(1000L, "1k coins and up"),
        VALUE_5K(5000L, "5k coins and up"),
        VALUE_10K(10000L, "10k coins and up");
        
        private final long value;
        private final String description;
        
        LootValuePreset(long value, String description) {
            this.value = value;
            this.description = description;
        }
        
        public long getValue() { return value; }
        public String getDescription() { return description; }
    }
    
    // Per-player cached data to prevent memory leaks
    private static final ConcurrentHashMap<String, PlayerLootData> playerLootCache = new ConcurrentHashMap<>();
    
    // Anti-spam protection for collections
    private static final ConcurrentHashMap<String, PlayerCollectionData> playerCollectionTracking = new ConcurrentHashMap<>();
    
    /**
     * Holds cached loot data for a specific player to prevent constant reallocations
     */
    private static class PlayerLootData {
        private List<FloorItem> floorItems = new ArrayList<>();
        private FloorItem[] itemArray = new FloorItem[0];
        private long lastUpdate = 0;
        
        void updateItems(List<FloorItem> newItems) {
            floorItems.clear();
            floorItems.addAll(newItems);
            itemArray = floorItems.toArray(new FloorItem[0]);
            lastUpdate = System.currentTimeMillis();
        }
        
        void clear() {
            floorItems.clear();
            itemArray = new FloorItem[0];
        }
        
        List<FloorItem> getFloorItems() { return floorItems; }
        FloorItem[] getItemArray() { return itemArray; }
        boolean needsUpdate(long currentTime) { 
            return currentTime - lastUpdate > UPDATE_INTERVAL_MS; 
        }
    }
    
    /**
     * Tracks collection attempts to prevent spam and conflicts
     */
    private static class PlayerCollectionData {
        private long lastCollectionAttempt = 0;
        private int collectionsThisSecond = 0;
        private long currentSecondStart = 0;
        
        boolean canAttemptCollection(long currentTime) {
            // Reset counter if we're in a new second
            if (currentTime - currentSecondStart >= 1000) {
                currentSecondStart = currentTime;
                collectionsThisSecond = 0;
            }
            
            // Check if enough time has passed since last attempt
            if (currentTime - lastCollectionAttempt < COLLECTION_COOLDOWN_MS) {
                return false;
            }
            
            // Check if we've exceeded max attempts per second
            if (collectionsThisSecond >= MAX_COLLECTION_ATTEMPTS_PER_SECOND) {
                return false;
            }
            
            return true;
        }
        
        void recordCollectionAttempt(long currentTime) {
            lastCollectionAttempt = currentTime;
            collectionsThisSecond++;
        }
    }
    
    /**
     * Opens the area loot interface for the player
     */
    public static void openInterface(Player player) {
        if (player == null) return;
        
        boolean isResizable = player.getInterfaceManager().isResizableScreen();
        player.getInterfaceManager().sendOverlay(INTERFACE_ID, !isResizable);
        
        // Initialize player data if not exists
        playerLootCache.putIfAbsent(player.getUsername(), new PlayerLootData());
        playerCollectionTracking.putIfAbsent(player.getUsername(), new PlayerCollectionData());
        
        // Force initial update
        processLootUpdate(player);
    }
    
    /**
     * Closes the area loot interface for the player
     */
    public static void closeInterface(Player player) {
        if (player == null) return;
        
        boolean isResizable = player.getInterfaceManager().isResizableScreen();
        player.getInterfaceManager().closeOverlay(!isResizable);
        
        // Clean up player data to prevent memory leaks
        cleanupPlayerData(player.getUsername());
    }
    
    /**
     * Handles button interactions in the area loot interface
     */
    public static void handleButtons(Player player, int componentId, int slotId, int itemId) {
        if (player == null) return;
        
        switch (componentId) {
            case COMPONENT_CLOSE:
                closeInterface(player);
                break;
                
            case COMPONENT_SETTINGS:
                showCustomLootDialog(player);
                break;
                
            case COMPONENT_CUSTOM_LOOT:
                collectCustomValueLoot(player);
                break;
                
            case COMPONENT_LOOT_ALL:
                collectAllLoot(player);
                break;
                
            case COMPONENT_CONTAINER:
                collectSpecificItem(player, slotId);
                break;
        }
    }
    
    /**
     * Checks if player is in a high-conflict area (multi-combat, popular training spots, etc.)
     */
    private static boolean isHighConflictArea(Player player) {
        try {
            // Check if in wilderness multi-combat area
            if (player.getControlerManager().getControler() != null) {
                String controllerName = player.getControlerManager().getControler().getClass().getSimpleName();
                if (controllerName.contains("Wilderness") || controllerName.contains("Multi")) {
                    return true;
                }
            }
            
            // Check region for known high-traffic areas
            int regionId = player.getRegionId();
            
            // Common high-conflict areas (add more as needed)
            int[] highConflictRegions = {
                12342, 12598, 12854, // Wilderness areas
                13154, 13410,        // Popular training spots
                12845, 12589,        // Boss areas
                // Add more region IDs as needed
            };
            
            for (int conflictRegion : highConflictRegions) {
                if (regionId == conflictRegion) {
                    return true;
                }
            }
            
            // Check if multiple players are nearby (potential for conflict)
            long nearbyPlayers = World.getPlayers().stream()
                    .filter(p -> p != null && !p.equals(player))
                    .filter(p -> player.withinDistance(p, MAX_DISTANCE + 2))
                    .count();
            
            return nearbyPlayers >= 3; // 3+ other players nearby = high conflict potential
            
        } catch (Exception e) {
            Logger.handle(e);
            return true; // Default to safe mode if we can't determine
        }
    }
    
    /**
     * Validates that a floor item still exists and can be collected
     */
    private static boolean validateItemForCollection(Player player, FloorItem item) {
        if (item == null) return false;
        
        try {
            // Check if item still exists in the world
            Region region = World.getRegion(player.getRegionId());
            if (region == null) return false;
            
            List<FloorItem> currentItems = region.getGroundItems();
            if (currentItems == null || !currentItems.contains(item)) {
                return false;
            }
            
            // Check if player can still reach the item
            if (!player.withinDistance(item.getTile(), MAX_DISTANCE)) {
                return false;
            }
            
            // Check ownership and visibility
            if (item.isInvisible() && !item.getOwner().equalsIgnoreCase(player.getUsername())) {
                return false;
            }
            
            // Additional validation for high-conflict areas
            if (isHighConflictArea(player)) {
                // Add small delay to prevent race conditions in multi-areas
                try {
                    Thread.sleep(MULTI_AREA_EXTRA_VALIDATION_DELAY);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return false;
                }
                
                // Re-check existence after delay
                currentItems = region.getGroundItems();
                if (currentItems == null || !currentItems.contains(item)) {
                    return false;
                }
            }
            
            return true;
            
        } catch (Exception e) {
            Logger.handle(e);
            return false;
        }
    }
    
    /**
     * Checks if player can attempt collection (rate limiting)
     */
    private static boolean canPlayerCollect(Player player) {
        PlayerCollectionData collectionData = playerCollectionTracking.get(player.getUsername());
        if (collectionData == null) {
            collectionData = new PlayerCollectionData();
            playerCollectionTracking.put(player.getUsername(), collectionData);
        }
        
        long currentTime = System.currentTimeMillis();
        
        if (!collectionData.canAttemptCollection(currentTime)) {
            player.sendMessage(MESSAGE_PREFIX + "Please wait before collecting more items");
            return false;
        }
        
        // Record the attempt
        collectionData.recordCollectionAttempt(currentTime);
        return true;
    }
    
    /**
     * Collects items based on the player's custom value setting and filter mode
     */
    private static void collectCustomValueLoot(Player player) {
        if (!canPlayerCollect(player)) return;
        
        PlayerLootData data = playerLootCache.get(player.getUsername());
        if (data == null) {
            player.sendMessage(MESSAGE_PREFIX + "No loot data found - try refreshing interface");
            return;
        }
        
        if (data.getFloorItems().isEmpty()) {
            player.sendMessage(MESSAGE_PREFIX + "No items found in area");
            return;
        }
        
        // Get filter mode (default to ALL_ITEMS if not set)
        int filterMode = player.arealootFilterMode;
        String filterDesc = (filterMode == 1) ? "coins only" : "all items";
        
        // Check if in high-conflict area and warn player
        boolean isHighConflict = isHighConflictArea(player);
        if (isHighConflict) {
            player.sendMessage(MESSAGE_PREFIX + "Multi-area detected - using extra safety checks");
        }
        
        int collectedCount = 0;
        int totalItems = data.getFloorItems().size();
        int eligibleItems = 0;
        int failedValidation = 0;
        long totalValueCollected = 0;
        
        List<FloorItem> itemsToRemove = new ArrayList<>();
        
        // First pass: identify eligible items
        for (FloorItem item : data.getFloorItems()) {
            if (item == null) continue;
            
            // Apply filter mode
            if (filterMode == 1 && item.getId() != 995) { // Coins only mode
                continue; // Skip non-coin items
            }
            
            long itemValue = getItemValue(item);
            
            if (itemValue >= player.arealootValue) {
                eligibleItems++;
                itemsToRemove.add(item);
            }
        }
        
        if (itemsToRemove.isEmpty()) {
            player.sendMessage(MESSAGE_PREFIX + "No " + filterDesc + " meet your value criteria of " + formatValue(player.arealootValue) + "+");
            return;
        }
        
        // Second pass: validate and collect items
        for (FloorItem item : itemsToRemove) {
            // Validate item before collection attempt
            if (!validateItemForCollection(player, item)) {
                failedValidation++;
                continue;
            }
            
            if (World.removeGroundItem(player, item)) {
                collectedCount++;
                totalValueCollected += getItemValue(item);
            }
        }
        
        // Provide feedback with actual values collected
        if (collectedCount > 0) {
            if (filterMode == 1) {
                // For coins only, show the total amount collected
                player.sendMessage(MESSAGE_PREFIX + "Collected " + formatValue(totalValueCollected) + " coins");
            } else {
                // For all items, show count and total value
                player.sendMessage(MESSAGE_PREFIX + "Collected " + collectedCount + " items worth " 
                        + formatValue(totalValueCollected) + " total");
            }
        }
        
        if (failedValidation > 0) {
            player.sendMessage(MESSAGE_PREFIX + failedValidation + " items were already taken by other players");
        }
        
        if (collectedCount == 0) {
            player.sendMessage(MESSAGE_PREFIX + "No items could be collected (may have been taken by others)");
        }
        
        processLootUpdate(player);
    }
    
    /**
     * Collects all visible loot items
     */
    private static void collectAllLoot(Player player) {
        if (!canPlayerCollect(player)) return;
        
        PlayerLootData data = playerLootCache.get(player.getUsername());
        if (data == null || data.getFloorItems().isEmpty()) {
            player.sendMessage(MESSAGE_PREFIX + "No items found to collect");
            return;
        }
        
        boolean isHighConflict = isHighConflictArea(player);
        if (isHighConflict) {
            player.sendMessage(MESSAGE_PREFIX + "Multi-area detected - using careful collection mode");
        }
        
        int collectedCount = 0;
        int failedValidation = 0;
        long totalValueCollected = 0;
        List<FloorItem> itemsToCollect = new ArrayList<>(data.getFloorItems());
        
        for (FloorItem item : itemsToCollect) {
            if (!validateItemForCollection(player, item)) {
                failedValidation++;
                continue;
            }
            
            if (World.removeGroundItem(player, item)) {
                collectedCount++;
                totalValueCollected += getItemValue(item);
            }
        }
        
        if (collectedCount > 0) {
            player.sendMessage(MESSAGE_PREFIX + "Collected " + collectedCount + " items worth " 
                    + formatValue(totalValueCollected) + " total");
        }
        
        if (failedValidation > 0) {
            player.sendMessage(MESSAGE_PREFIX + failedValidation + " items were already taken by other players");
        }
        
        if (collectedCount == 0) {
            player.sendMessage(MESSAGE_PREFIX + "No items could be collected");
        }
        
        processLootUpdate(player);
    }
    
    /**
     * Collects a specific item by slot index
     */
    private static void collectSpecificItem(Player player, int slotId) {
        if (!canPlayerCollect(player)) return;
        
        PlayerLootData data = playerLootCache.get(player.getUsername());
        if (data == null || slotId < 0 || slotId >= data.getFloorItems().size()) {
            player.sendMessage(MESSAGE_PREFIX + "Invalid item selection");
            return;
        }
        
        FloorItem item = data.getFloorItems().get(slotId);
        
        if (!validateItemForCollection(player, item)) {
            player.sendMessage(MESSAGE_PREFIX + "Item no longer available (may have been taken by another player)");
            processLootUpdate(player); // Refresh the interface
            return;
        }
        
        if (World.removeGroundItem(player, item)) {
            String itemName = ItemDefinitions.getItemDefinitions(item.getId()).getName();
            long itemValue = getItemValue(item);
            
            if (item.getId() == 995) {
                // For coins, show the amount
                player.sendMessage(MESSAGE_PREFIX + "Collected " + formatValue(itemValue) + " " + itemName.toLowerCase());
            } else {
                // For items, show name and value
                player.sendMessage(MESSAGE_PREFIX + "Collected " + itemName + " (worth " + formatValue(itemValue) + ")");
            }
        } else {
            player.sendMessage(MESSAGE_PREFIX + "Could not collect item (taken by another player)");
        }
        
        processLootUpdate(player);
    }
    
    /**
     * Starts the area loot updater task
     */
    public static void startUpdater() {
        // Safety check for CoresManager initialization
        if (CoresManager.slowExecutor == null) {
            System.out.println("AreaLoot: CoresManager not initialized yet, skipping updater start");
            return;
        }
        
        try {
            CoresManager.slowExecutor.scheduleWithFixedDelay(() -> {
                try {
                    updateAllPlayersLoot();
                } catch (Throwable e) {
                    Logger.handle(e);
                }
            }, 0, UPDATE_INTERVAL_MS, TimeUnit.MILLISECONDS);
            
            System.out.println("AreaLoot updater started successfully!");
        } catch (Exception e) {
            System.out.println("Failed to start AreaLoot updater: " + e.getMessage());
            Logger.handle(e);
        }
    }
    
    /**
     * Alternative method to start updater after server is fully loaded
     */
    public static void startUpdaterDelayed() {
        // Schedule with a small delay to ensure all systems are loaded
        new Thread(() -> {
            try {
                Thread.sleep(5000); // Wait 5 seconds
                startUpdater();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }
    
    /**
     * Updates loot for all players with the interface open
     */
    private static void updateAllPlayersLoot() {
        World.getPlayers().parallelStream()
                .filter(player -> player != null && player.getInterfaceManager().containsInterface(INTERFACE_ID))
                .forEach(AreaLoot::processLootUpdate);
        
        // Clean up disconnected players periodically
        cleanupDisconnectedPlayers();
    }
    
    /**
     * Updates the loot display for a specific player
     */
    private static void processLootUpdate(Player player) {
        if (player == null || !player.getInterfaceManager().containsInterface(INTERFACE_ID)) {
            return;
        }
        
        PlayerLootData data = playerLootCache.get(player.getUsername());
        if (data == null) {
            data = new PlayerLootData();
            playerLootCache.put(player.getUsername(), data);
        }
        
        // Skip update if too frequent
        long currentTime = System.currentTimeMillis();
        if (!data.needsUpdate(currentTime)) {
            return;
        }
        
        try {
            Region region = World.getRegion(player.getRegionId());
            if (region == null) {
                return;
            }
            
            List<FloorItem> nearbyItems = getNearbyItems(player, region);
            data.updateItems(nearbyItems);
            
            // Update interface
            player.getPackets().sendItems(106, data.getItemArray());
            player.getPackets().sendUnlockIComponentOptionSlots(INTERFACE_ID, COMPONENT_CONTAINER, 0, MAX_ITEMS_DISPLAY, 0);
            player.getPackets().sendInterSetItemsOptionsScript(INTERFACE_ID, COMPONENT_CONTAINER, 106, 4, 15, "Take");
            
        } catch (Exception e) {
            Logger.handle(e);
        }
    }
    
    /**
     * Gets nearby floor items for the player with additional filtering for multi-areas
     */
    private static List<FloorItem> getNearbyItems(Player player, Region region) {
        List<FloorItem> groundItems = region.getGroundItems();
        if (groundItems == null || groundItems.isEmpty()) {
            return Collections.emptyList();
        }
        
        boolean isHighConflict = isHighConflictArea(player);
        
        return groundItems.stream()
                .filter(item -> item != null)
                .filter(item -> !item.isInvisible() || item.getOwner().equalsIgnoreCase(player.getUsername()))
                .filter(item -> player.withinDistance(item.getTile(), MAX_DISTANCE))
                .filter(item -> {
                    // In high-conflict areas, add extra validation
                    if (isHighConflict) {
                        try {
                            // Quick ownership double-check for multi-areas
                            return item.getOwner() == null || 
                                   item.getOwner().isEmpty() || 
                                   item.getOwner().equalsIgnoreCase(player.getUsername());
                        } catch (Exception e) {
                            return false;
                        }
                    }
                    return true;
                })
                .limit(MAX_ITEMS_DISPLAY)
                .collect(Collectors.toList());
    }
    
    /**
     * Shows the custom loot value dialog
     */
    static void showCustomLootDialog(Player player) {
        player.getDialogueManager().startDialogue(new Dialogue() {
            @Override
            public void start() {
                sendOptionsDialogue("AreaLoot Filter Mode", "All Items", "Coins Only", "Value Settings", "Back");
            }
            
            @Override
            public void run(int interfaceId, int componentId) {
                if (stage == -1) { // Main filter mode menu
                    switch (componentId) {
                        case OPTION_1: // All Items
                            player.arealootFilterMode = LootFilterMode.ALL_ITEMS.getMode();
                            player.sendMessage(MESSAGE_PREFIX + "Filter mode set to: " + LootFilterMode.ALL_ITEMS.getDescription());
                            player.sendMessage("DEBUG: Filter mode = " + player.arealootFilterMode);
                            end();
                            break;
                        case OPTION_2: // Coins Only
                            player.arealootFilterMode = LootFilterMode.COINS_ONLY.getMode();
                            player.sendMessage(MESSAGE_PREFIX + "Filter mode set to: " + LootFilterMode.COINS_ONLY.getDescription());
                            player.sendMessage("DEBUG: Filter mode = " + player.arealootFilterMode);
                            end();
                            break;
                        case OPTION_3: // Value Settings
                            stage = 1; // Move to value settings menu
                            String currentMode = (player.arealootFilterMode == 1) ? "Coins Only" : "All Items";
                            sendOptionsDialogue("Value Settings (" + currentMode + ")", "Everything", "300+ value", "1k+ value", "5k+ value", "More Values");
                            break;
                        case OPTION_4: // Back/Close
                            end();
                            break;
                    }
                } else if (stage == 1) { // Value settings menu
                    switch (componentId) {
                        case OPTION_1: // Everything
                            player.arealootValue = LootValuePreset.RESET.getValue();
                            player.sendMessage(MESSAGE_PREFIX + "Value filter: " + LootValuePreset.RESET.getDescription());
                            player.sendMessage("DEBUG: Set arealootValue to " + player.arealootValue);
                            end();
                            break;
                        case OPTION_2: // 300+ value
                            player.arealootValue = LootValuePreset.VALUE_300.getValue();
                            player.sendMessage(MESSAGE_PREFIX + "Value filter: " + LootValuePreset.VALUE_300.getDescription());
                            player.sendMessage("DEBUG: Set arealootValue to " + player.arealootValue);
                            end();
                            break;
                        case OPTION_3: // 1k+ value
                            player.arealootValue = LootValuePreset.VALUE_1K.getValue();
                            player.sendMessage(MESSAGE_PREFIX + "Value filter: " + LootValuePreset.VALUE_1K.getDescription());
                            player.sendMessage("DEBUG: Set arealootValue to " + player.arealootValue);
                            end();
                            break;
                        case OPTION_4: // 5k+ value
                            player.arealootValue = LootValuePreset.VALUE_5K.getValue();
                            player.sendMessage(MESSAGE_PREFIX + "Value filter: " + LootValuePreset.VALUE_5K.getDescription());
                            player.sendMessage("DEBUG: Set arealootValue to " + player.arealootValue);
                            end();
                            break;
                        case OPTION_5: // More Values
                            stage = 2;
                            sendOptionsDialogue("Higher Values", "10k+ value", "25k+ value", "50k+ value", "100k+ value", "Back");
                            break;
                    }
                } else if (stage == 2) { // Higher values menu
                    switch (componentId) {
                        case OPTION_1: // 10k+ value
                            player.arealootValue = LootValuePreset.VALUE_10K.getValue();
                            player.sendMessage(MESSAGE_PREFIX + "Value filter: " + LootValuePreset.VALUE_10K.getDescription());
                            player.sendMessage("DEBUG: Set arealootValue to " + player.arealootValue);
                            end();
                            break;
                        case OPTION_2: // 25k+ value
                            player.arealootValue = 25000L;
                            player.sendMessage(MESSAGE_PREFIX + "Value filter: 25k value and up");
                            player.sendMessage("DEBUG: Set arealootValue to " + player.arealootValue);
                            end();
                            break;
                        case OPTION_3: // 50k+ value
                            player.arealootValue = 50000L;
                            player.sendMessage(MESSAGE_PREFIX + "Value filter: 50k value and up");
                            player.sendMessage("DEBUG: Set arealootValue to " + player.arealootValue);
                            end();
                            break;
                        case OPTION_4: // 100k+ value
                            player.arealootValue = 100000L;
                            player.sendMessage(MESSAGE_PREFIX + "Value filter: 100k value and up");
                            player.sendMessage("DEBUG: Set arealootValue to " + player.arealootValue);
                            end();
                            break;
                        case OPTION_5: // Back
                            stage = 1;
                            String currentMode = (player.arealootFilterMode == 1) ? "Coins Only" : "All Items";
                            sendOptionsDialogue("Value Settings (" + currentMode + ")", "Everything", "300+ value", "1k+ value", "5k+ value", "More Values");
                            break;
                    }
                }
            }
            
            @Override
            public void finish() {
                // Dialog cleanup if needed
            }
        });
    }
    
    /**
     * Alternative method to get coin value when FloorItem.getAmount() might be incorrect
     */
    private static long getCoinValueAlternative(FloorItem item) {
        if (item == null || item.getId() != 995) {
            return getItemValue(item);
        }
        
        try {
            // Method 1: Standard FloorItem amount
            int standardAmount = item.getAmount();
            
            // Method 2: Try casting to Item if possible
            int itemCastAmount = standardAmount;
            try {
                if (item instanceof Item) {
                    itemCastAmount = ((Item) item).getAmount();
                }
            } catch (Exception e) {
                // Ignore casting errors
            }
            
            // Method 3: Check if FloorItem has alternative amount methods
            int reflectionAmount = standardAmount;
            try {
                // Try reflection to access potential private fields
                java.lang.reflect.Field amountField = item.getClass().getDeclaredField("amount");
                amountField.setAccessible(true);
                Object amountObj = amountField.get(item);
                if (amountObj instanceof Integer) {
                    reflectionAmount = (Integer) amountObj;
                }
            } catch (Exception e) {
                // Ignore reflection errors
            }
            
            // Use the highest value found (most likely to be correct)
            int bestAmount = Math.max(Math.max(standardAmount, itemCastAmount), reflectionAmount);
            
            return Math.max(bestAmount, 1); // Ensure at least 1
            
        } catch (Exception e) {
            Logger.handle(e);
            return 1;
        }
    }
    
    /**
     * Gets the monetary value of an item (handles coins specially)
     */
    private static long getItemValue(int itemId, int amount) {
        try {
            // Special case for coins - use the actual stack amount
            if (itemId == 995) { // Coins item ID
                long coinValue = Math.max(amount, 1); // Ensure at least 1 coin value
                return coinValue;
            }
            
            // For other items, use definition value multiplied by amount
            ItemDefinitions itemDef = ItemDefinitions.getItemDefinitions(itemId);
            if (itemDef != null) {
                long baseValue = itemDef.getValue();
                long totalValue = baseValue * Math.max(amount, 1);
                return totalValue;
            }
            
            return 0;
        } catch (Exception e) {
            Logger.handle(e);
            return 0;
        }
    }
    
    /**
     * Gets the monetary value of an item (single item version with enhanced coin detection)
     */
    private static long getItemValue(FloorItem item) {
        if (item == null) return 0;
        
        // For coins, use the alternative detection method
        if (item.getId() == 995) {
            return getCoinValueAlternative(item);
        }
        
        // For non-coins, use standard logic
        try {
            int itemId = item.getId();
            int amount = item.getAmount();
            return getItemValue(itemId, amount);
        } catch (Exception e) {
            Logger.handle(e);
            return 0;
        }
    }
    
    /**
     * Formats a value for display (e.g., 1000000 -> "1M")
     */
    private static String formatValue(long value) {
        if (value >= 1000000000L) {
            return (value / 1000000000L) + "B";
        } else if (value >= 1000000L) {
            return (value / 1000000L) + "M";
        } else if (value >= 1000L) {
            return (value / 1000L) + "K";
        }
        return String.valueOf(value);
    }
    
    /**
     * Cleans up player data to prevent memory leaks
     */
    private static void cleanupPlayerData(String username) {
        PlayerLootData data = playerLootCache.remove(username);
        if (data != null) {
            data.clear();
        }
        
        // Also clean up collection tracking
        playerCollectionTracking.remove(username);
    }
    
    /**
     * Periodically cleans up data for disconnected players
     */
    private static void cleanupDisconnectedPlayers() {
        if (playerLootCache.size() > 100) { // Only clean if cache is getting large
            List<String> connectedPlayers = World.getPlayers().stream()
                    .filter(p -> p != null)
                    .map(Player::getUsername)
                    .collect(Collectors.toList());
            
            playerLootCache.keySet().retainAll(connectedPlayers);
            playerCollectionTracking.keySet().retainAll(connectedPlayers);
        }
    }
    
    /**
     * Shuts down the area loot system and cleans up resources
     */
    public static void shutdown() {
        playerLootCache.clear();
        playerCollectionTracking.clear();
    }
    
    /**
     * Toggles the area loot interface for the player
     */
    public static void toggleInterface(Player player) {
        if (player == null) return;
        
        if (player.getInterfaceManager().containsInterface(INTERFACE_ID)) {
            closeInterface(player);
        } else {
            openInterface(player);
        }
    }
    
    /**
     * Checks if the player currently has the area loot interface open
     */
    public static boolean hasInterfaceOpen(Player player) {
        return player != null && player.getInterfaceManager().containsInterface(INTERFACE_ID);
    }
    
    // Legacy method names for backward compatibility
    @Deprecated
    public static void openInter(Player player) {
        openInterface(player);
    }
    
    @Deprecated
    public static void updater() {
        startUpdater();
    }
    
    // Make INTERFACE_ID accessible for external code
    public static int getInterfaceId() {
        return INTERFACE_ID;
    }
}