package com.rs.game.player.dialogue.impl;

import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.game.player.dialogue.Dialogue;
import com.rs.game.player.actions.automation.AutoSkillingManager;
import com.rs.game.player.actions.automation.AutoSkillingManager.SkillingType;
import com.rs.game.player.actions.automation.AutoSkillingManager.AutoSkillingState;
import com.rs.game.player.actions.automation.AutoSkillingManager.InventoryAction;
import com.rs.game.player.actions.Woodcutting.TreeDefinitions;
import com.rs.game.player.actions.mining.Mining.RockDefinitions;
import com.rs.game.player.actions.Fishing.FishingSpots;
import com.rs.game.player.actions.automation.handlers.AutoThievingHandler;

public class AutoSkillingDialogue extends Dialogue {

    @Override
    public void start() {
        try {
            // Initialize fields if needed
            if (player.getAutoSkillingState() == null) {
                player.setAutoSkillingState(AutoSkillingState.STOPPED);
            }
            if (player.getSkillingInventoryAction() == null) {
                player.setSkillingInventoryAction(InventoryAction.AUTO_BANK);
            }
            
            // AUTO-FIX: Reset stale state after server restart
            if (player.getAutoSkillingState() != AutoSkillingState.STOPPED) {
                if (player.getActionManager().getActionDelay() == 0) {
                    player.setAutoSkillingState(AutoSkillingState.STOPPED);
                    player.sendMessage("Auto-skilling state reset after server restart.");
                }
            }
            
            // Check if already auto-skilling (AFTER stale state reset)
            if (player.getAutoSkillingState() != AutoSkillingState.STOPPED) {
                String currentSkill = "Unknown";
                if (player.getAutoSkillingType() != null) {
                    currentSkill = player.getAutoSkillingType().name().toLowerCase();
                }
                
                // Show active skilling options
                sendOptionsDialogue("Auto-Skilling Active: " + currentSkill, 
                    "Stop Auto-Skilling", 
                    "Check Status", 
                    "Settings", 
                    "Close");
                stage = 10; // Active skilling menu stage
                return;
            }
            
            // Main menu - choose skill (NOW WITH THIEVING)
            sendOptionsDialogue("Auto-Skilling Hub", 
                "Woodcutting", 
                "Mining", 
                "Fishing", 
                "Thieving",
                "Settings & Status");
            stage = -1; // Main menu stage
            
        } catch (Exception e) {
            player.sendMessage("Error opening dialogue: " + e.getMessage());
            end();
        }
    }

    @Override
    public void run(int interfaceId, int componentId) {
        try {
            // Main skill selection menu
            if (stage == -1) { 
                if (componentId == OPTION_1) { // Woodcutting
                    handleWoodcuttingStart();
                }
                else if (componentId == OPTION_2) { // Mining
                    handleMiningStart();
                }
                else if (componentId == OPTION_3) { // Fishing
                    handleFishingStart(); 
                }
                else if (componentId == OPTION_4) { // Thieving - NEW
                    handleThievingStart();
                }
                else if (componentId == OPTION_5) { // Settings & Status (moved to OPTION_5)
                    showStatusAndSettings();
                }
            }
            // Woodcutting mode selection
            else if (stage == 0) { 
                if (componentId == OPTION_1) { // Automatic Tree Cutting
                    player.getTemporaryAttributtes().put("woodcutting_mode", "AUTO");
                    showWoodcuttingConfirmation();
                }
                else if (componentId == OPTION_2) { // Specific Tree Cutting
                    player.getTemporaryAttributtes().put("woodcutting_mode", "SPECIFIC");
                    showTreeSelection();
                }
                else if (componentId == OPTION_3) { // Back to main menu
                    start();
                }
            }
            // Tree selection menu
            else if (stage == 5) { 
                TreeDefinitions selectedTree = null;
                if (componentId == OPTION_1) { 
                    selectedTree = TreeDefinitions.NORMAL;
                }
                else if (componentId == OPTION_2) { 
                    selectedTree = TreeDefinitions.OAK;
                }
                else if (componentId == OPTION_3) { 
                    selectedTree = TreeDefinitions.MAPLE;
                }
                else if (componentId == OPTION_4) { 
                    selectedTree = TreeDefinitions.YEW;
                }
                else if (componentId == OPTION_5) { // Back to mode selection
                    player.getTemporaryAttributtes().remove("selected_tree");
                    handleWoodcuttingStart();
                    return;
                }
                
                if (selectedTree != null) {
                    int playerLevel = player.getSkills().getLevel(Skills.WOODCUTTING);
                    if (playerLevel >= selectedTree.getLevel()) {
                        player.getTemporaryAttributtes().put("selected_tree", selectedTree);
                        showWoodcuttingConfirmation();
                    } else {
                        sendDialogue("Insufficient Level<br><br>You need level " + selectedTree.getLevel() + 
                                   " Woodcutting to cut " + selectedTree.name() + " trees.<br><br>Your level: " + playerLevel);
                        stage = 99;
                    }
                }
            }
            // Woodcutting confirmation
            else if (stage == 1) { 
                if (componentId == OPTION_1) { // Yes, start woodcutting
                    startWoodcuttingWithSettings();
                }
                else if (componentId == OPTION_2) { // No, cancel
                    cleanupWoodcuttingAttributes();
                    start();
                }
            }
            // Mining mode selection
            else if (stage == 20) { 
                if (componentId == OPTION_1) { // Automatic Rock Mining
                    player.getTemporaryAttributtes().put("mining_mode", "AUTO");
                    showMiningConfirmation();
                }
                else if (componentId == OPTION_2) { // Specific Rock Mining
                    player.getTemporaryAttributtes().put("mining_mode", "SPECIFIC");
                    showRockSelection();
                }
                else if (componentId == OPTION_3) { // Back to main menu
                    start();
                }
            }
            // Rock selection menu (basic rocks)
            else if (stage == 25) { 
                RockDefinitions selectedRock = null;
                if (componentId == OPTION_1) { 
                    selectedRock = RockDefinitions.Copper_Ore;
                }
                else if (componentId == OPTION_2) { 
                    selectedRock = RockDefinitions.Tin_Ore;
                }
                else if (componentId == OPTION_3) { 
                    selectedRock = RockDefinitions.Iron_Ore;
                }
                else if (componentId == OPTION_4) { 
                    selectedRock = RockDefinitions.Coal_Ore;
                }
                else if (componentId == OPTION_5) { // More rocks
                    showAdvancedRockSelection();
                    return;
                }
                
                if (selectedRock != null) {
                    int playerLevel = player.getSkills().getLevel(Skills.MINING);
                    if (playerLevel >= selectedRock.getLevel()) {
                        player.getTemporaryAttributtes().put("selected_rock", selectedRock);
                        showMiningConfirmation();
                    } else {
                        sendDialogue("Insufficient Level<br><br>You need level " + selectedRock.getLevel() + 
                                   " Mining to mine " + selectedRock.name() + " rocks.<br><br>Your level: " + playerLevel);
                        stage = 99;
                    }
                }
            }
            // Advanced rock selection menu 1
            else if (stage == 26) { 
                RockDefinitions selectedRock = null;
                if (componentId == OPTION_1) { 
                    selectedRock = RockDefinitions.Silver_Ore;
                }
                else if (componentId == OPTION_2) { 
                    selectedRock = RockDefinitions.Gold_Ore;
                }
                else if (componentId == OPTION_3) { 
                    selectedRock = RockDefinitions.Mithril_Ore;
                }
                else if (componentId == OPTION_4) { // More rocks
                    showAdvancedRockSelection2();
                    return;
                }
                else if (componentId == OPTION_5) { // Back to basic rocks
                    showRockSelection();
                    return;
                }
                
                if (selectedRock != null) {
                    int playerLevel = player.getSkills().getLevel(Skills.MINING);
                    if (playerLevel >= selectedRock.getLevel()) {
                        player.getTemporaryAttributtes().put("selected_rock", selectedRock);
                        showMiningConfirmation();
                    } else {
                        sendDialogue("Insufficient Level<br><br>You need level " + selectedRock.getLevel() + 
                                   " Mining to mine " + selectedRock.name() + " rocks.<br><br>Your level: " + playerLevel);
                        stage = 99;
                    }
                }
            }
            // Advanced rock selection menu 2
            else if (stage == 27) { 
                RockDefinitions selectedRock = null;
                if (componentId == OPTION_1) { 
                    selectedRock = RockDefinitions.Adamant_Ore;
                }
                else if (componentId == OPTION_2) { 
                    selectedRock = RockDefinitions.Runite_Ore;
                }
                else if (componentId == OPTION_3) { 
                    selectedRock = RockDefinitions.GEM_ROCK;
                }
                else if (componentId == OPTION_4) { // Back to previous advanced rocks
                    showAdvancedRockSelection();
                    return;
                }
                else if (componentId == OPTION_5) { // Back to mode selection
                    player.getTemporaryAttributtes().remove("selected_rock");
                    handleMiningStart();
                    return;
                }
                
                if (selectedRock != null) {
                    int playerLevel = player.getSkills().getLevel(Skills.MINING);
                    if (playerLevel >= selectedRock.getLevel()) {
                        player.getTemporaryAttributtes().put("selected_rock", selectedRock);
                        showMiningConfirmation();
                    } else {
                        sendDialogue("Insufficient Level<br><br>You need level " + selectedRock.getLevel() + 
                                   " Mining to mine " + selectedRock.name() + " rocks.<br><br>Your level: " + playerLevel);
                        stage = 99;
                    }
                }
            }
            // Mining confirmation
            else if (stage == 21) { 
                if (componentId == OPTION_1) { // Yes, start mining
                    startMiningWithSettings();
                }
                else if (componentId == OPTION_2) { // No, cancel
                    cleanupMiningAttributes();
                    start();
                }
            }
            // Fishing mode selection
            else if (stage == 30) {
                if (componentId == OPTION_1) { // Automatic Spot Fishing
                    player.getTemporaryAttributtes().put("fishing_mode", "AUTO");
                    showFishingConfirmation();
                }
                else if (componentId == OPTION_2) { // Specific Spot Fishing
                    player.getTemporaryAttributtes().put("fishing_mode", "SPECIFIC");
                    showSpotSelection();
                }
                else if (componentId == OPTION_3) { // Back to main menu
                    start();
                }
            }
            // Fishing spot selection menu (basic spots)
            else if (stage == 35) {
                FishingSpots selectedSpot = null;
                if (componentId == OPTION_1) { // Net fishing
                    selectedSpot = FishingSpots.NET;
                }
                else if (componentId == OPTION_2) { // Lure fishing
                    selectedSpot = FishingSpots.LURE2;
                }
                else if (componentId == OPTION_3) { // Cage fishing
                    selectedSpot = FishingSpots.CAGE2;
                }
                else if (componentId == OPTION_4) { // Harpoon fishing
                    selectedSpot = FishingSpots.HARPOON;
                }
                else if (componentId == OPTION_5) { // More spots
                    showAdvancedSpotSelection();
                    return;
                }
                
                if (selectedSpot != null) {
                    int playerLevel = player.getSkills().getLevel(Skills.FISHING);
                    int requiredLevel = getSpotLevel(selectedSpot);
                    if (playerLevel >= requiredLevel) {
                        player.getTemporaryAttributtes().put("selected_spot", selectedSpot);
                        showFishingConfirmation();
                    } else {
                        sendDialogue("Insufficient Level<br><br>You need level " + requiredLevel + 
                                   " Fishing for " + selectedSpot.name() + " spots.<br><br>Your level: " + playerLevel);
                        stage = 99;
                    }
                }
            }
            // Advanced fishing spot selection
            else if (stage == 36) {
                FishingSpots selectedSpot = null;
                if (componentId == OPTION_1) { // Cavefish
                    selectedSpot = FishingSpots.CAVEFISH_SHOAL;
                }
                else if (componentId == OPTION_2) { // Rocktail
                    selectedSpot = FishingSpots.ROCKTAIL_SHOAL;
                }
                else if (componentId == OPTION_3) { // Back to basic spots
                    showSpotSelection();
                    return;
                }
                else if (componentId == OPTION_4) { // Back to mode selection
                    player.getTemporaryAttributtes().remove("selected_spot");
                    handleFishingStart();
                    return;
                }
                
                if (selectedSpot != null) {
                    int playerLevel = player.getSkills().getLevel(Skills.FISHING);
                    int requiredLevel = getSpotLevel(selectedSpot);
                    if (playerLevel >= requiredLevel) {
                        player.getTemporaryAttributtes().put("selected_spot", selectedSpot);
                        showFishingConfirmation();
                    } else {
                        sendDialogue("Insufficient Level<br><br>You need level " + requiredLevel + 
                                   " Fishing for " + selectedSpot.name() + " spots.<br><br>Your level: " + playerLevel);
                        stage = 99;
                    }
                }
            }
            // Fishing confirmation
            else if (stage == 31) {
                if (componentId == OPTION_1) { // Yes, start fishing
                    startFishingWithSettings();
                }
                else if (componentId == OPTION_2) { // No, cancel
                    cleanupFishingAttributes();
                    start();
                }
            }
            // ==================== NEW THIEVING HANDLERS ====================
            // Thieving mode selection
            else if (stage == 40) { // NEW stage for Thieving mode selection
                if (componentId == OPTION_1) { // Automatic Stall Thieving
                    player.getTemporaryAttributtes().put("thieving_mode", "AUTO");
                    showThievingConfirmation();
                }
                else if (componentId == OPTION_2) { // Specific Stall Thieving
                    player.getTemporaryAttributtes().put("thieving_mode", "SPECIFIC");
                    showStallSelection();
                }
                else if (componentId == OPTION_3) { // Back to main menu
                    start();
                }
            }
            // Thieving stall selection menu
            else if (stage == 45) { // NEW stage for Thieving Stall selection
                int selectedStallIndex = -1;
                if (componentId == OPTION_1) { // Crafting stall
                    selectedStallIndex = 0;
                }
                else if (componentId == OPTION_2) { // Food stall
                    selectedStallIndex = 1;
                }
                else if (componentId == OPTION_3) { // General stall
                    selectedStallIndex = 2;
                }
                else if (componentId == OPTION_4) { // Magic stall
                    selectedStallIndex = 3;
                }
                else if (componentId == OPTION_5) { // Scimitar stall OR Back
                    // Check if player can access Scimitar stall
                    int playerLevel = player.getSkills().getLevel(Skills.THIEVING);
                    if (playerLevel >= 95) {
                        selectedStallIndex = 4; // Scimitar stall
                    } else {
                        // Back to mode selection
                        player.getTemporaryAttributtes().remove("selected_stall_index");
                        handleThievingStart();
                        return;
                    }
                }
                
                if (selectedStallIndex != -1) {
                    // Check if player has required level
                    int playerLevel = player.getSkills().getLevel(Skills.THIEVING);
                    int requiredLevel = getStallLevel(selectedStallIndex);
                    String stallName = getStallName(selectedStallIndex);
                    
                    if (playerLevel >= requiredLevel) {
                        player.getTemporaryAttributtes().put("selected_stall_index", selectedStallIndex);
                        showThievingConfirmation();
                    } else {
                        sendDialogue("Insufficient Level<br><br>You need level " + requiredLevel + 
                                   " Thieving for " + stallName + " stalls.<br><br>Your level: " + playerLevel);
                        stage = 99;
                    }
                }
            }
            // Thieving confirmation
            else if (stage == 41) { // NEW stage for Thieving confirmation
                if (componentId == OPTION_1) { // Yes, start thieving
                    startThievingWithSettings();
                }
                else if (componentId == OPTION_2) { // No, cancel
                    cleanupThievingAttributes();
                    start();
                }
            }
            // Thieving settings menu
            else if (stage == 42) { // NEW stage for Thieving settings
                if (componentId == OPTION_1) { // Toggle stop on rogue
                    boolean currentSetting = player.isStopOnRogue();
                    player.setStopOnRogue(!currentSetting);
                    player.sendMessage("Stop on rogue: " + (player.isStopOnRogue() ? "Enabled" : "Disabled"));
                    showThievingSettings();
                }
                else if (componentId == OPTION_2) { // Back to confirmation
                    String mode = (String) player.getTemporaryAttributtes().get("thieving_mode");
                    if ("SPECIFIC".equals(mode)) {
                        showStallSelection(); // Go back to stall selection
                    } else {
                        showThievingConfirmation(); // Go back to confirmation
                    }
                }
            }
            // ==================== SHARED HANDLERS ====================
            // Status and settings menu
            else if (stage == 2) { 
                if (componentId == OPTION_1) { // Change inventory action
                    showInventorySettings();
                }
                else if (componentId == OPTION_2) { // View detailed status
                    showDetailedStatus();
                }
                else if (componentId == OPTION_3) { // Developer options or hub info
                    if (player.isDeveloper()) {
                        player.setAutoSkillingUsedTime(0);
                        player.setAutoSkillingLastReset(0);
                        player.sendMessage("Daily auto-skilling timer reset!");
                        showStatusAndSettings();
                    } else {
                        showHubInfo();
                    }
                }
                else if (componentId == OPTION_4) { // Back to main
                    start();
                }
            }
            // Inventory settings
            else if (stage == 3) { 
                if (componentId == OPTION_1) { // Auto Bank
                    player.setSkillingInventoryAction(InventoryAction.AUTO_BANK);
                    player.sendMessage("Inventory action set to: Auto Bank");
                    showInventorySettings();
                }
                else if (componentId == OPTION_2) { // Auto Drop
                    player.setSkillingInventoryAction(InventoryAction.AUTO_DROP);
                    player.sendMessage("Inventory action set to: Auto Drop");
                    showInventorySettings();
                }
                else if (componentId == OPTION_3) { // Stop When Full
                    player.setSkillingInventoryAction(InventoryAction.STOP_WHEN_FULL);
                    player.sendMessage("Inventory action set to: Stop When Full");
                    showInventorySettings();
                }
                else if (componentId == OPTION_4) { // Back to settings
                    showStatusAndSettings();
                }
            }
            // Active skilling menu
            else if (stage == 10) { 
                if (componentId == OPTION_1) { // Stop auto-skilling
                    AutoSkillingManager.stopAutoSkilling(player);
                    sendDialogue("Auto-skilling stopped successfully!<br><br>You can start again anytime from this dialogue.");
                    stage = 99;
                }
                else if (componentId == OPTION_2) { // Check status
                    showDetailedStatus();
                }
                else if (componentId == OPTION_3) { // Settings
                    showStatusAndSettings();
                }
                else if (componentId == OPTION_4) { // Close
                    end();
                }
            }
            // End stage - any click closes
            else if (stage == 99) { 
                end();
            }
        } catch (Exception e) {
            player.sendMessage("Dialogue error: " + e.getMessage());
            end();
        }
    }
    
    // ==================== THIEVING METHODS (NEW) ====================
    
    private void handleThievingStart() {
        try {
            // FIRST CHECK - Safety check before anything else
            if (!AutoSkillingManager.isInSkillingHub(player)) {
                sendDialogue("Auto-thieving can only be used in the skilling hub!<br><br>Hub location: 1375, 5669");
                stage = 99;
                return;
            }
            
            // Check thieving level
            int thievingLevel = player.getSkills().getLevel(Skills.THIEVING);
            if (thievingLevel < 1) {
                sendDialogue("Insufficient Level<br><br>You need at least level 1 Thieving to use auto-thieving!");
                stage = 99;
                return;
            }
            
            // Check remaining time
            int remainingMinutes = AutoSkillingManager.getRemainingTimeMinutes(player);
            if (remainingMinutes <= 0) {
                sendDialogue("Daily Limit Reached<br><br>You have used your daily 4-hour auto-skilling limit!<br><br>Try again tomorrow.");
                stage = 99;
                return;
            }
            
            // Show mode selection for thieving
            sendOptionsDialogue("Choose Thieving Mode<br><br>" +
                               "Automatic: Automatically upgrades to best stalls when you level up<br><br>" +
                               "Specific: Thieve only the stall type you select",
                "Automatic Stall Thieving",
                "Specific Stall Thieving", 
                "Back to Main Menu");
            stage = 40; // Thieving mode selection stage
            
        } catch (Exception e) {
            sendDialogue("Error checking requirements:<br>" + e.getMessage());
            stage = 99;
        }
    }
    
    private void showStallSelection() {
        try {
            int playerLevel = player.getSkills().getLevel(Skills.THIEVING);
            
            String craftingOption = playerLevel >= 1 ? "Crafting Stall (Lv 1)" : "Crafting Stall (Lv 1) [LOCKED]";
            String foodOption = playerLevel >= 30 ? "Food Stall (Lv 30)" : "Food Stall (Lv 30) [LOCKED]";
            String generalOption = playerLevel >= 65 ? "General Stall (Lv 65)" : "General Stall (Lv 65) [LOCKED]";
            String magicOption = playerLevel >= 85 ? "Magic Stall (Lv 85)" : "Magic Stall (Lv 85) [LOCKED]";
            String scimitarOrBackOption;
            
            if (playerLevel >= 95) {
                scimitarOrBackOption = "Scimitar Stall (Lv 95)";
            } else {
                scimitarOrBackOption = "Back to Mode Selection";
            }
            
            sendOptionsDialogue("Select Stall Type<br><br>Your Thieving Level: " + playerLevel,
                craftingOption,
                foodOption,
                generalOption,
                magicOption,
                scimitarOrBackOption);
            stage = 45; // Stall selection stage
            
        } catch (Exception e) {
            sendDialogue("Error showing stall selection: " + e.getMessage());
            stage = 99;
        }
    }
    
    private void showThievingConfirmation() {
        try {
            // Ensure inventory action is set
            if (player.getSkillingInventoryAction() == null) {
                player.setSkillingInventoryAction(InventoryAction.AUTO_BANK);
            }
            
            // Calculate remaining time for display
            int remainingMinutes = AutoSkillingManager.getRemainingTimeMinutes(player);
            int hours = remainingMinutes / 60;
            int minutes = remainingMinutes % 60;
            String timeDisplay = hours > 0 ? hours + "h " + minutes + "m" : minutes + "m";
            
            // Retrieve temporary attributes for thieving mode and selected stall
            String mode = (String) player.getTemporaryAttributtes().get("thieving_mode");
            Integer selectedStallIndex = (Integer) player.getTemporaryAttributtes().get("selected_stall_index");
            
            String info = "Start Auto-Thieving?<br><br>";
            info += "Your Level: " + player.getSkills().getLevel(Skills.THIEVING) + "<br>";
            info += "Time Remaining: " + timeDisplay + "<br>";
            info += "When Full: " + getInventoryActionString(player.getSkillingInventoryAction()) + "<br>";
            info += "Stop on Rogue: " + (player.isStopOnRogue() ? "Yes" : "No") + "<br>";
            
            if ("AUTO".equals(mode)) {
                info += "Mode: Automatic (upgrades stalls)<br><br>";
                info += "The system will find the best stalls for your level and upgrade automatically!";
            } else if ("SPECIFIC".equals(mode) && selectedStallIndex != null) {
                String stallName = getStallName(selectedStallIndex);
                info += "Mode: Specific Stall<br>";
                info += "Target: " + stallName + " Stall<br><br>";
                info += "The system will only thieve from " + stallName + " stalls (no auto-upgrade).";
            }
            
            sendOptionsDialogue(info,
                "Yes, start auto-thieving",
                "No, cancel");
            stage = 41; // Thieving confirmation stage
            
        } catch (Exception e) {
            sendDialogue("Error showing confirmation: " + e.getMessage());
            stage = 99;
        }
    }
    
    private void showThievingSettings() {
        try {
            String rogueStatus = player.isStopOnRogue() ? "Enabled" : "Disabled";
            
            sendOptionsDialogue("Thieving Settings<br><br>Stop on Rogue: " + rogueStatus,
                "Toggle Stop on Rogue",
                "Back");
            stage = 42; // Thieving settings stage
            
        } catch (Exception e) {
            sendDialogue("Error showing thieving settings: " + e.getMessage());
            stage = 99;
        }
    }
    
    private void startThievingWithSettings() {
        try {
            String mode = (String) player.getTemporaryAttributtes().get("thieving_mode");
            Integer selectedStallIndex = (Integer) player.getTemporaryAttributtes().get("selected_stall_index");
            
            // Set player attributes based on mode
            if ("AUTO".equals(mode)) {
                player.setAutoThievingMode("AUTO");
                player.setAutoThievingStall(-1); // Let handler determine best stall
            } else if ("SPECIFIC".equals(mode) && selectedStallIndex != null) {
                player.setAutoThievingMode("SPECIFIC");
                player.setAutoThievingStall(selectedStallIndex);
            }
            
            // Clean up temporary attributes
            cleanupThievingAttributes();
            
            // Force reset state before starting
            player.setAutoSkillingState(AutoSkillingState.STOPPED);
            
            // Start auto-thieving via the manager
            AutoSkillingManager.startAutoSkilling(player, SkillingType.THIEVING);
            
            String successMessage = "Auto-thieving started successfully!<br><br>";
            if ("AUTO".equals(mode)) {
                successMessage += "Mode: Automatic stall upgrading<br>";
            } else if (selectedStallIndex != null) {
                successMessage += "Mode: Specific stall (" + getStallName(selectedStallIndex) + ")<br>";
            }
            successMessage += "<br>Close this dialogue to continue.";
            
            sendDialogue(successMessage);
            stage = 99; // End stage after success message
            
        } catch (Exception e) {
            sendDialogue("Error starting auto-thieving:<br>" + e.getMessage());
            stage = 99;
        }
    }
    
    private void cleanupThievingAttributes() {
        // Remove temporary attributes specific to thieving dialogue flow
        if (player.getTemporaryAttributtes() != null) {
            player.getTemporaryAttributtes().remove("confirming_thieving");
            player.getTemporaryAttributtes().remove("thieving_mode");
            player.getTemporaryAttributtes().remove("selected_stall_index");
        }
    }
    
    /**
     * Get level requirement for thieving stall
     */
    private int getStallLevel(int stallIndex) {
        switch (stallIndex) {
            case 0: return 1;   // Crafting
            case 1: return 30;  // Food
            case 2: return 65;  // General
            case 3: return 85;  // Magic
            case 4: return 95;  // Scimitar
            default: return 1;
        }
    }
    
    /**
     * Get name for thieving stall
     */
    private String getStallName(int stallIndex) {
        switch (stallIndex) {
            case 0: return "Crafting";
            case 1: return "Food";
            case 2: return "General";
            case 3: return "Magic";
            case 4: return "Scimitar";
            default: return "Unknown";
        }
    }
    
    // ==================== WOODCUTTING METHODS ====================
    
    private void handleWoodcuttingStart() {
        try {
            if (!AutoSkillingManager.isInSkillingHub(player)) {
                sendDialogue("Auto-woodcutting can only be used in the skilling hub!<br><br>Hub location: 1375, 5669");
                stage = 99;
                return;
            }
            
            int woodcuttingLevel = player.getSkills().getLevel(Skills.WOODCUTTING);
            if (woodcuttingLevel < 1) {
                sendDialogue("Insufficient Level<br><br>You need at least level 1 Woodcutting to use auto-woodcutting!");
                stage = 99;
                return;
            }
            
            int remainingMinutes = AutoSkillingManager.getRemainingTimeMinutes(player);
            if (remainingMinutes <= 0) {
                sendDialogue("Daily Limit Reached<br><br>You have used your daily 4-hour auto-skilling limit!<br><br>Try again tomorrow.");
                stage = 99;
                return;
            }
            
            sendOptionsDialogue("Choose Woodcutting Mode<br><br>" +
                               "Automatic: Automatically upgrades to best trees when you level up<br><br>" +
                               "Specific: Cut only the tree type you select",
                "Automatic Tree Cutting",
                "Specific Tree Cutting", 
                "Back to Main Menu");
            stage = 0;
            
        } catch (Exception e) {
            sendDialogue("Error checking requirements:<br>" + e.getMessage());
            stage = 99;
        }
    }
    
    private void showTreeSelection() {
        try {
            int playerLevel = player.getSkills().getLevel(Skills.WOODCUTTING);
            
            String normalOption = playerLevel >= 1 ? "Normal Trees (Lv 1)" : "Normal Trees (Lv 1) [LOCKED]";
            String oakOption = playerLevel >= 15 ? "Oak Trees (Lv 15)" : "Oak Trees (Lv 15) [LOCKED]";
            String mapleOption = playerLevel >= 45 ? "Maple Trees (Lv 45)" : "Maple Trees (Lv 45) [LOCKED]";
            String yewOption = playerLevel >= 60 ? "Yew Trees (Lv 60)" : "Yew Trees (Lv 60) [LOCKED]";
            
            sendOptionsDialogue("Select Tree Type<br><br>Your Woodcutting Level: " + playerLevel,
                normalOption,
                oakOption,
                mapleOption,
                yewOption,
                "Back to Mode Selection");
            stage = 5;
            
        } catch (Exception e) {
            sendDialogue("Error showing tree selection: " + e.getMessage());
            stage = 99;
        }
    }
    
    private void showWoodcuttingConfirmation() {
        try {
            if (player.getSkillingInventoryAction() == null) {
                player.setSkillingInventoryAction(InventoryAction.AUTO_BANK);
            }
            
            int remainingMinutes = AutoSkillingManager.getRemainingTimeMinutes(player);
            int hours = remainingMinutes / 60;
            int minutes = remainingMinutes % 60;
            String timeDisplay = hours > 0 ? hours + "h " + minutes + "m" : minutes + "m";
            
            String mode = (String) player.getTemporaryAttributtes().get("woodcutting_mode");
            TreeDefinitions selectedTree = (TreeDefinitions) player.getTemporaryAttributtes().get("selected_tree");
            
            String info = "Start Auto-Woodcutting?<br><br>";
            info += "Your Level: " + player.getSkills().getLevel(Skills.WOODCUTTING) + "<br>";
            info += "Time Remaining: " + timeDisplay + "<br>";
            info += "When Full: " + getInventoryActionString(player.getSkillingInventoryAction()) + "<br>";
            
            if ("AUTO".equals(mode)) {
                info += "Mode: Automatic (upgrades trees)<br><br>";
                info += "The system will find the best trees for your level and upgrade automatically!";
            } else if ("SPECIFIC".equals(mode) && selectedTree != null) {
                info += "Mode: Specific Tree<br>";
                info += "Target: " + selectedTree.name() + " Trees<br><br>";
                info += "The system will only cut " + selectedTree.name() + " trees (no auto-upgrade).";
            }
            
            sendOptionsDialogue(info,
                "Yes, start auto-woodcutting",
                "No, cancel");
            stage = 1;
            
        } catch (Exception e) {
            sendDialogue("Error showing confirmation: " + e.getMessage());
            stage = 99;
        }
    }
    
    private void startWoodcuttingWithSettings() {
        try {
            String mode = (String) player.getTemporaryAttributtes().get("woodcutting_mode");
            TreeDefinitions selectedTree = (TreeDefinitions) player.getTemporaryAttributtes().get("selected_tree");
            
            if ("AUTO".equals(mode)) {
                player.setAutoWoodcuttingMode("AUTO");
                player.setAutoWoodcuttingTree(null);
            } else if ("SPECIFIC".equals(mode) && selectedTree != null) {
                player.setAutoWoodcuttingMode("SPECIFIC");
                player.setAutoWoodcuttingTree(selectedTree);
            }
            
            cleanupWoodcuttingAttributes();
            player.setAutoSkillingState(AutoSkillingState.STOPPED);
            AutoSkillingManager.startAutoSkilling(player, SkillingType.WOODCUTTING);
            
            String successMessage = "Auto-woodcutting started successfully!<br><br>";
            if ("AUTO".equals(mode)) {
                successMessage += "Mode: Automatic tree upgrading<br>";
            } else if (selectedTree != null) {
                successMessage += "Mode: Specific tree (" + selectedTree.name() + ")<br>";
            }
            successMessage += "<br>Close this dialogue to continue.";
            
            sendDialogue(successMessage);
            stage = 99;
            
        } catch (Exception e) {
            sendDialogue("Error starting auto-woodcutting:<br>" + e.getMessage());
            stage = 99;
        }
    }
    
    private void cleanupWoodcuttingAttributes() {
        if (player.getTemporaryAttributtes() != null) {
            player.getTemporaryAttributtes().remove("confirming_woodcutting");
            player.getTemporaryAttributtes().remove("woodcutting_mode");
            player.getTemporaryAttributtes().remove("selected_tree");
        }
    }
    
    // ==================== MINING METHODS ====================
    
    private void handleMiningStart() {
        try {
            if (!AutoSkillingManager.isInSkillingHub(player)) {
                sendDialogue("Auto-mining can only be used in the skilling hub!<br><br>Hub location: 1375, 5669");
                stage = 99;
                return;
            }
            
            int miningLevel = player.getSkills().getLevel(Skills.MINING);
            if (miningLevel < 1) {
                sendDialogue("Insufficient Level<br><br>You need at least level 1 Mining to use auto-mining!");
                stage = 99;
                return;
            }
            
            int remainingMinutes = AutoSkillingManager.getRemainingTimeMinutes(player);
            if (remainingMinutes <= 0) {
                sendDialogue("Daily Limit Reached<br><br>You have used your daily 4-hour auto-skilling limit!<br><br>Try again tomorrow.");
                stage = 99;
                return;
            }
            
            sendOptionsDialogue("Choose Mining Mode<br><br>" +
                               "Automatic: Automatically upgrades to best rocks when you level up<br><br>" +
                               "Specific: Mine only the rock type you select",
                "Automatic Rock Mining",
                "Specific Rock Mining", 
                "Back to Main Menu");
            stage = 20;
            
        } catch (Exception e) {
            sendDialogue("Error checking requirements:<br>" + e.getMessage());
            stage = 99;
        }
    }
    
    private void showRockSelection() {
        try {
            int playerLevel = player.getSkills().getLevel(Skills.MINING);
            
            String copperOption = playerLevel >= 1 ? "Copper Rocks (Lv 1)" : "Copper Rocks (Lv 1) [LOCKED]";
            String tinOption = playerLevel >= 1 ? "Tin Rocks (Lv 1)" : "Tin Rocks (Lv 1) [LOCKED]";
            String ironOption = playerLevel >= 15 ? "Iron Rocks (Lv 15)" : "Iron Rocks (Lv 15) [LOCKED]";
            String coalOption = playerLevel >= 30 ? "Coal Rocks (Lv 30)" : "Coal Rocks (Lv 30) [LOCKED]";
            
            sendOptionsDialogue("Select Rock Type<br><br>Your Mining Level: " + playerLevel,
                copperOption,
                tinOption,
                ironOption,
                coalOption,
                "More Rocks →");
            stage = 25;
            
        } catch (Exception e) {
            sendDialogue("Error showing rock selection: " + e.getMessage());
            stage = 99;
        }
    }
    
    private void showAdvancedRockSelection() {
        try {
            int playerLevel = player.getSkills().getLevel(Skills.MINING);
            
            String silverOption = playerLevel >= 20 ? "Silver Rocks (Lv 20)" : "Silver Rocks (Lv 20) [LOCKED]";
            String goldOption = playerLevel >= 40 ? "Gold Rocks (Lv 40)" : "Gold Rocks (Lv 40) [LOCKED]";
            String mithrilOption = playerLevel >= 55 ? "Mithril Rocks (Lv 55)" : "Mithril Rocks (Lv 55) [LOCKED]";
            
            sendOptionsDialogue("Select Rock Type<br><br>Your Mining Level: " + playerLevel,
                silverOption,
                goldOption,
                mithrilOption,
                "More Rocks →",
                "← Back to Basic Rocks");
            stage = 26;
            
        } catch (Exception e) {
            sendDialogue("Error showing advanced rock selection: " + e.getMessage());
            stage = 99;
        }
    }
    
    private void showAdvancedRockSelection2() {
        try {
            int playerLevel = player.getSkills().getLevel(Skills.MINING);
            
            String adamantOption = playerLevel >= 70 ? "Adamant Rocks (Lv 70)" : "Adamant Rocks (Lv 70) [LOCKED]";
            String runiteOption = playerLevel >= 85 ? "Runite Rocks (Lv 85)" : "Runite Rocks (Lv 85) [LOCKED]";
            String gemOption = playerLevel >= 40 ? "Gem Rocks (Lv 40)" : "Gem Rocks (Lv 40) [LOCKED]";
            
            sendOptionsDialogue("Select Rock Type<br><br>Your Mining Level: " + playerLevel,
                adamantOption,
                runiteOption,
                gemOption,
                "← Back to Previous",
                "← Back to Mode Selection");
            stage = 27;
            
        } catch (Exception e) {
            sendDialogue("Error showing advanced rock selection 2: " + e.getMessage());
            stage = 99;
        }
    }
    
    private void showMiningConfirmation() {
        try {
            if (player.getSkillingInventoryAction() == null) {
                player.setSkillingInventoryAction(InventoryAction.AUTO_BANK);
            }
            
            int remainingMinutes = AutoSkillingManager.getRemainingTimeMinutes(player);
            int hours = remainingMinutes / 60;
            int minutes = remainingMinutes % 60;
            String timeDisplay = hours > 0 ? hours + "h " + minutes + "m" : minutes + "m";
            
            String mode = (String) player.getTemporaryAttributtes().get("mining_mode");
            RockDefinitions selectedRock = (RockDefinitions) player.getTemporaryAttributtes().get("selected_rock");
            
            String info = "Start Auto-Mining?<br><br>";
            info += "Your Level: " + player.getSkills().getLevel(Skills.MINING) + "<br>";
            info += "Time Remaining: " + timeDisplay + "<br>";
            info += "When Full: " + getInventoryActionString(player.getSkillingInventoryAction()) + "<br>";
            
            if ("AUTO".equals(mode)) {
                info += "Mode: Automatic (upgrades rocks)<br><br>";
                info += "The system will find the best rocks for your level and upgrade automatically!";
            } else if ("SPECIFIC".equals(mode) && selectedRock != null) {
                info += "Mode: Specific Rock<br>";
                info += "Target: " + selectedRock.name() + " Rocks<br><br>";
                info += "The system will only mine " + selectedRock.name() + " rocks (no auto-upgrade).";
            }
            
            sendOptionsDialogue(info,
                "Yes, start auto-mining",
                "No, cancel");
            stage = 21;
            
        } catch (Exception e) {
            sendDialogue("Error showing confirmation: " + e.getMessage());
            stage = 99;
        }
    }
    
    private void startMiningWithSettings() {
        try {
            String mode = (String) player.getTemporaryAttributtes().get("mining_mode");
            RockDefinitions selectedRock = (RockDefinitions) player.getTemporaryAttributtes().get("selected_rock");
            
            if ("AUTO".equals(mode)) {
                player.setAutoMiningMode("AUTO");
                player.setAutoMiningRock(null);
            } else if ("SPECIFIC".equals(mode) && selectedRock != null) {
                player.setAutoMiningMode("SPECIFIC");
                player.setAutoMiningRock(selectedRock);
            }
            
            cleanupMiningAttributes();
            player.setAutoSkillingState(AutoSkillingState.STOPPED);
            AutoSkillingManager.startAutoSkilling(player, SkillingType.MINING);
            
            String successMessage = "Auto-mining started successfully!<br><br>";
            if ("AUTO".equals(mode)) {
                successMessage += "Mode: Automatic rock upgrading<br>";
            } else if (selectedRock != null) {
                successMessage += "Mode: Specific rock (" + selectedRock.name() + ")<br>";
            }
            successMessage += "<br>Close this dialogue to continue.";
            
            sendDialogue(successMessage);
            stage = 99;
            
        } catch (Exception e) {
            sendDialogue("Error starting auto-mining:<br>" + e.getMessage());
            stage = 99;
        }
    }
    
    private void cleanupMiningAttributes() {
        if (player.getTemporaryAttributtes() != null) {
            player.getTemporaryAttributtes().remove("confirming_mining");
            player.getTemporaryAttributtes().remove("mining_mode");
            player.getTemporaryAttributtes().remove("selected_rock");
        }
    }
    
    // ==================== FISHING METHODS ====================
    
    private void handleFishingStart() {
        try {
            if (!AutoSkillingManager.isInSkillingHub(player)) {
                sendDialogue("Auto-fishing can only be used in the skilling hub!<br><br>Hub location: 1375, 5669");
                stage = 99;
                return;
            }
            
            int fishingLevel = player.getSkills().getLevel(Skills.FISHING);
            if (fishingLevel < 1) {
                sendDialogue("Insufficient Level<br><br>You need at least level 1 Fishing to use auto-fishing!");
                stage = 99;
                return;
            }
            
            int remainingMinutes = AutoSkillingManager.getRemainingTimeMinutes(player);
            if (remainingMinutes <= 0) {
                sendDialogue("Daily Limit Reached<br><br>You have used your daily 4-hour auto-skilling limit!<br><br>Try again tomorrow.");
                stage = 99;
                return;
            }
            
            sendOptionsDialogue("Choose Fishing Mode<br><br>" +
                               "Automatic: Automatically upgrades to best spots when you level up<br><br>" +
                               "Specific: Fish only the spot type you select",
                "Automatic Spot Fishing",
                "Specific Spot Fishing", 
                "Back to Main Menu");
            stage = 30;
            
        } catch (Exception e) {
            sendDialogue("Error checking requirements:<br>" + e.getMessage());
            stage = 99;
        }
    }
    
    private void showSpotSelection() {
        try {
            int playerLevel = player.getSkills().getLevel(Skills.FISHING);
            
            String netOption = playerLevel >= 1 ? "Net Spots (Lv 1)" : "Net Spots (Lv 1) [LOCKED]";
            String lureOption = playerLevel >= 20 ? "Lure Spots (Lv 20)" : "Lure Spots (Lv 20) [LOCKED]";
            String cageOption = playerLevel >= 40 ? "Cage Spots (Lv 40)" : "Cage Spots (Lv 40) [LOCKED]";
            String harpoonOption = playerLevel >= 35 ? "Harpoon Spots (Lv 35)" : "Harpoon Spots (Lv 35) [LOCKED]";
            
            sendOptionsDialogue("Select Fishing Spot<br><br>Your Fishing Level: " + playerLevel,
                netOption,
                lureOption,
                cageOption,
                harpoonOption,
                "More Spots →");
            stage = 35;
            
        } catch (Exception e) {
            sendDialogue("Error showing spot selection: " + e.getMessage());
            stage = 99;
        }
    }
    
    private void showAdvancedSpotSelection() {
        try {
            int playerLevel = player.getSkills().getLevel(Skills.FISHING);
            
            String cavefishOption = playerLevel >= 85 ? "Cavefish Spots (Lv 85)" : "Cavefish Spots (Lv 85) [LOCKED]";
            String rocktailOption = playerLevel >= 90 ? "Rocktail Spots (Lv 90)" : "Rocktail Spots (Lv 90) [LOCKED]";
            
            sendOptionsDialogue("Select Fishing Spot<br><br>Your Fishing Level: " + playerLevel,
                cavefishOption,
                rocktailOption,
                "← Back to Basic Spots",
                "← Back to Mode Selection");
            stage = 36;
            
        } catch (Exception e) {
            sendDialogue("Error showing advanced spot selection: " + e.getMessage());
            stage = 99;
        }
    }
    
    private void showFishingConfirmation() {
        try {
            if (player.getSkillingInventoryAction() == null) {
                player.setSkillingInventoryAction(InventoryAction.AUTO_BANK);
            }
            
            int remainingMinutes = AutoSkillingManager.getRemainingTimeMinutes(player);
            int hours = remainingMinutes / 60;
            int minutes = remainingMinutes % 60;
            String timeDisplay = hours > 0 ? hours + "h " + minutes + "m" : minutes + "m";
            
            String mode = (String) player.getTemporaryAttributtes().get("fishing_mode");
            FishingSpots selectedSpot = (FishingSpots) player.getTemporaryAttributtes().get("selected_spot");
            
            String info = "Start Auto-Fishing?<br><br>";
            info += "Your Level: " + player.getSkills().getLevel(Skills.FISHING) + "<br>";
            info += "Time Remaining: " + timeDisplay + "<br>";
            info += "When Full: " + getInventoryActionString(player.getSkillingInventoryAction()) + "<br>";
            
            if ("AUTO".equals(mode)) {
                info += "Mode: Automatic (upgrades spots)<br><br>";
                info += "The system will find the best fishing spots for your level and upgrade automatically!";
            } else if ("SPECIFIC".equals(mode) && selectedSpot != null) {
                info += "Mode: Specific Spot<br>";
                info += "Target: " + selectedSpot.name() + " Spots<br><br>";
                info += "The system will only fish at " + selectedSpot.name() + " spots (no auto-upgrade).";
            }
            
            sendOptionsDialogue(info,
                "Yes, start auto-fishing",
                "No, cancel");
            stage = 31;
            
        } catch (Exception e) {
            sendDialogue("Error showing confirmation: " + e.getMessage());
            stage = 99;
        }
    }
    
    private void startFishingWithSettings() {
        try {
            String mode = (String) player.getTemporaryAttributtes().get("fishing_mode");
            FishingSpots selectedSpot = (FishingSpots) player.getTemporaryAttributtes().get("selected_spot");
            
            if ("AUTO".equals(mode)) {
                player.setAutoFishingMode("AUTO");
                player.setAutoFishingSpot(null);
            } else if ("SPECIFIC".equals(mode) && selectedSpot != null) {
                player.setAutoFishingMode("SPECIFIC");
                player.setAutoFishingSpot(selectedSpot);
            }
            
            cleanupFishingAttributes();
            player.setAutoSkillingState(AutoSkillingState.STOPPED);
            AutoSkillingManager.startAutoSkilling(player, SkillingType.FISHING);
            
            String successMessage = "Auto-fishing started successfully!<br><br>";
            if ("AUTO".equals(mode)) {
                successMessage += "Mode: Automatic spot upgrading<br>";
            } else if (selectedSpot != null) {
                successMessage += "Mode: Specific spot (" + selectedSpot.name() + ")<br>";
            }
            successMessage += "<br>Close this dialogue to continue.";
            
            sendDialogue(successMessage);
            stage = 99;
            
        } catch (Exception e) {
            sendDialogue("Error starting auto-fishing:<br>" + e.getMessage());
            stage = 99;
        }
    }
    
    private void cleanupFishingAttributes() {
        if (player.getTemporaryAttributtes() != null) {
            player.getTemporaryAttributtes().remove("confirming_fishing");
            player.getTemporaryAttributtes().remove("fishing_mode");
            player.getTemporaryAttributtes().remove("selected_spot");
        }
    }
    
    /**
     * Get level requirement for fishing spot
     */
    private int getSpotLevel(FishingSpots spot) {
        switch (spot) {
            case NET:
                return 1;
            case LURE2:
                return 20;
            case HARPOON:
                return 35;
            case CAGE2:
                return 40;
            case CAVEFISH_SHOAL:
                return 85;
            case ROCKTAIL_SHOAL:
                return 90;
            default:
                return 1;
        }
    }
    
    // ==================== SHARED METHODS ====================
    
    private void showStatusAndSettings() {
        try {
            if (player.getSkillingInventoryAction() == null) {
                player.setSkillingInventoryAction(InventoryAction.AUTO_BANK);
            }
            
            String currentAction = getInventoryActionString(player.getSkillingInventoryAction());
            String thirdOption = player.isDeveloper() ? "Reset Timer (Dev)" : "Hub Information";
            
            sendOptionsDialogue("Auto-Skilling Settings",
                "Inventory: " + currentAction,
                "View Detailed Status",
                thirdOption,
                "Back to Main Menu");
            stage = 2;
        } catch (Exception e) {
            sendDialogue("Error showing settings: " + e.getMessage());
            stage = 99;
        }
    }
    
    private void showInventorySettings() {
        try {
            if (player.getSkillingInventoryAction() == null) {
                player.setSkillingInventoryAction(InventoryAction.AUTO_BANK);
            }
            
            InventoryAction currentAction = player.getSkillingInventoryAction();
            String bankStatus = currentAction == InventoryAction.AUTO_BANK ? " [SELECTED]" : "";
            String dropStatus = currentAction == InventoryAction.AUTO_DROP ? " [SELECTED]" : "";
            String stopStatus = currentAction == InventoryAction.STOP_WHEN_FULL ? " [SELECTED]" : "";
            
            sendOptionsDialogue("When inventory is full:",
                "Auto Bank" + bankStatus,
                "Auto Drop" + dropStatus,
                "Stop When Full" + stopStatus,
                "Back to Settings");
            stage = 3;
        } catch (Exception e) {
            sendDialogue("Error showing inventory settings: " + e.getMessage());
            stage = 99;
        }
    }
    
    private void showDetailedStatus() {
        try {
            String info = "Auto-Skilling Status<br><br>";
            
            AutoSkillingState state = player.getAutoSkillingState();
            if (state == null) {
                state = AutoSkillingState.STOPPED;
                player.setAutoSkillingState(state);
            }
            info += "Current State: " + state.toString() + "<br>";
            
            if (state != AutoSkillingState.STOPPED) {
                SkillingType skill = player.getAutoSkillingType();
                String skillName = (skill != null) ? skill.name().toLowerCase() : "Unknown";
                info += "Current Skill: " + skillName + "<br>";
                
                // Show woodcutting specific info
                if (skill == SkillingType.WOODCUTTING) {
                    String mode = player.getAutoWoodcuttingMode();
                    TreeDefinitions tree = player.getAutoWoodcuttingTree();
                    
                    if (mode != null) {
                        info += "Mode: " + mode + "<br>";
                    }
                    if (tree != null) {
                        info += "Target Tree: " + tree.name() + "<br>";
                    }
                }
                
                // Show mining specific info
                if (skill == SkillingType.MINING) {
                    String mode = player.getAutoMiningMode();
                    RockDefinitions rock = player.getAutoMiningRock();
                    
                    if (mode != null) {
                        info += "Mode: " + mode + "<br>";
                    }
                    if (rock != null) {
                        info += "Target Rock: " + rock.name() + "<br>";
                    }
                }
                
                // Show fishing specific info
                if (skill == SkillingType.FISHING) {
                    String mode = player.getAutoFishingMode();
                    FishingSpots spot = player.getAutoFishingSpot();
                    
                    if (mode != null) {
                        info += "Mode: " + mode + "<br>";
                    }
                    if (spot != null) {
                        info += "Target Spot: " + spot.name() + "<br>";
                    }
                }
                
                // Show thieving specific info - NEW
                if (skill == SkillingType.THIEVING) {
                    String mode = player.getAutoThievingMode();
                    int stallIndex = player.getAutoThievingStall();
                    
                    if (mode != null) {
                        info += "Mode: " + mode + "<br>";
                    }
                    if (stallIndex != -1) {
                        info += "Target Stall: " + getStallName(stallIndex) + "<br>";
                    }
                    info += "Stop on Rogue: " + (player.isStopOnRogue() ? "Yes" : "No") + "<br>";
                }
            }
            
            // Time info
            int remainingMinutes = AutoSkillingManager.getRemainingTimeMinutes(player);
            int usedMinutes = (4 * 60) - remainingMinutes;
            
            int usedHours = usedMinutes / 60;
            int usedMins = usedMinutes % 60;
            int remainHours = remainingMinutes / 60;
            int remainMins = remainingMinutes % 60;
            
            String usedDisplay = usedHours > 0 ? usedHours + "h " + usedMins + "m" : usedMins + "m";
            String remainDisplay = remainHours > 0 ? remainHours + "h " + remainMins + "m" : remainMins + "m";
            
            info += "Time Used: " + usedDisplay + "<br>";
            info += "Time Remaining: " + remainDisplay + "<br><br>";
            
            // Settings
            info += "Settings:<br>";
            
            if (player.getSkillingInventoryAction() == null) {
                player.setSkillingInventoryAction(InventoryAction.AUTO_BANK);
            }
            info += "Inventory Action: " + getInventoryActionString(player.getSkillingInventoryAction()) + "<br><br>";
            
            // Location info
            info += "Location Info:<br>";
            info += "Hub: 1375, 5669 (90x90 area)<br>";
            info += "Bank: 1375, 5688 (north side)<br>";
            info += "You: " + player.getX() + ", " + player.getY();
            
            sendDialogue(info);
            stage = 99;
        } catch (Exception e) {
            sendDialogue("Error showing status: " + e.getMessage());
            stage = 99;
        }
    }
    
    private void showHubInfo() {
        try {
            String info = "Skilling Hub Information<br><br>";
            info += "Hub Location:<br>";
            info += "• Center: 1375, 5669<br>";
            info += "• Size: 90x90 tiles<br>";
            info += "• Boundaries: X(1330-1420), Y(5624-5714)<br><br>";
            
            info += "Available Skills:<br>";
            info += "• Woodcutting: Full 90x90 area<br>";
            info += "• Mining: Full 90x90 area<br>";
            info += "• Fishing: Full 90x90 area<br>";
            info += "• Thieving: Stalls at 1382-1390, 5674-5675<br>";
            info += "• Bank: North side (1375, 5688)<br><br>";
            
            info += "Your Location: " + player.getX() + ", " + player.getY() + "<br>";
            info += "In Hub: " + (AutoSkillingManager.isInSkillingHub(player) ? "Yes" : "No");
            
            sendDialogue(info);
            stage = 99;
        } catch (Exception e) {
            sendDialogue("Error showing hub info: " + e.getMessage());
            stage = 99;
        }
    }
    
    private String getInventoryActionString(InventoryAction action) {
        if (action == null) {
            return "Auto Bank";
        }
        
        switch (action) {
            case AUTO_BANK:
                return "Auto Bank";
            case AUTO_DROP:
                return "Auto Drop";
            case STOP_WHEN_FULL:
                return "Stop When Full";
            default:
                return "Auto Bank";
        }
    }

    @Override
    public void finish() {
        // Clean up all temporary attributes when dialogue closes
        cleanupWoodcuttingAttributes();
        cleanupMiningAttributes();
        cleanupFishingAttributes();
        cleanupThievingAttributes(); // NEW: Ensure thieving attributes are also cleaned
    }
}