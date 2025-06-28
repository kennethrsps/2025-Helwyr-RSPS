/*
 * package com.rs.game.player.dialogue.impl;
 * 
 * import com.rs.game.player.Player; import com.rs.game.player.Skills; import
 * com.rs.game.player.dialogue.Dialogue; import
 * com.rs.game.player.actions.automation.AutoThievingManager; import
 * com.rs.game.player.actions.automation.AutoThievingManager.InventoryAction;
 * 
 * public class AutoThievingDialogue extends Dialogue {
 * 
 * @Override public void start() { sendOptionsDialogue("Auto-Thieving System",
 * "Start Auto-Thieving", "Stop Auto-Thieving", "Check Status", "Settings");
 * stage = -1; }
 * 
 * @Override public void run(int interfaceId, int componentId) { if (stage ==
 * -1) { // Main menu if (componentId == OPTION_1) { // Start handleStart(); }
 * if (componentId == OPTION_2) { // Stop handleStop(); } if (componentId ==
 * OPTION_3) { // Status showStatus(); } if (componentId == OPTION_4) { //
 * Settings showSettings(); } } else if (stage == 1) { // Settings menu if
 * (componentId == OPTION_1) { // Toggle auto-progress
 * player.setAutoProgressStalls(!player.isAutoProgressStalls());
 * player.sendMessage("Auto-progress stalls: " + (player.isAutoProgressStalls()
 * ? "ON" : "OFF")); showSettings(); // Refresh settings } if (componentId ==
 * OPTION_2) { // Toggle stop on rogue
 * player.setStopOnRogue(!player.isStopOnRogue());
 * player.sendMessage("Stop on rogue: " + (player.isStopOnRogue() ? "ON" :
 * "OFF")); showSettings(); // Refresh settings } if (componentId == OPTION_3) {
 * // Inventory settings showInventorySettings(); } if (componentId == OPTION_4)
 * { // Back to main start(); } } else if (stage == 2) { // Confirmation dialogs
 * if (componentId == OPTION_1) { // Yes if
 * (player.getTemporaryAttributtes().containsKey("confirming_start")) {
 * player.getTemporaryAttributtes().remove("confirming_start");
 * 
 * // Force reset state and start
 * player.setAutoThievingState(AutoThievingManager.AutoThievingState.STOPPED);
 * AutoThievingManager.startAutoThieving(player);
 * 
 * sendDialogue("Auto-thieving started successfully! Close this dialogue to continue."
 * ); stage = 99; // End stage } else if
 * (player.getTemporaryAttributtes().containsKey("confirming_stop")) {
 * player.getTemporaryAttributtes().remove("confirming_stop");
 * AutoThievingManager.stopAutoThieving(player);
 * 
 * sendDialogue("Auto-thieving stopped! Close this dialogue to continue.");
 * stage = 99; // End stage } } if (componentId == OPTION_2) { // No
 * player.getTemporaryAttributtes().remove("confirming_start");
 * player.getTemporaryAttributtes().remove("confirming_stop"); start(); // Back
 * to main menu } } else if (stage == 3) { // Inventory settings if (componentId
 * == OPTION_1) { // Auto Bank
 * player.setThievingInventoryAction(InventoryAction.AUTO_BANK);
 * player.sendMessage("Inventory action set to: Auto Bank");
 * showInventorySettings(); } if (componentId == OPTION_2) { // Auto Drop
 * player.setThievingInventoryAction(InventoryAction.AUTO_DROP);
 * player.sendMessage("Inventory action set to: Auto Drop");
 * showInventorySettings(); } if (componentId == OPTION_3) { // Stop When Full
 * player.setThievingInventoryAction(InventoryAction.STOP_WHEN_FULL);
 * player.sendMessage("Inventory action set to: Stop When Full");
 * showInventorySettings(); } if (componentId == OPTION_4) { // Back to settings
 * showSettings(); } } else if (stage == 99) { // End stage - any click closes
 * end(); } }
 * 
 * 
 * private void handleStart() { // FIRST CHECK - Safety check before anything
 * else if (!AutoThievingManager.isInHomeArea(player)) {
 * sendDialogue("Auto-thieving can only be used in the home area!"); stage = 99;
 * // End dialogue return; }
 * 
 * // SECOND CHECK - Daily time limit if
 * (!AutoThievingManager.hasTimeRemaining(player)) {
 * sendDialogue("You have reached your daily 1-hour auto-thieving limit!<br><br>Try again tomorrow."
 * ); stage = 99; return; }
 * 
 * // Force reset the state first
 * player.setAutoThievingState(AutoThievingManager.AutoThievingState.STOPPED);
 * 
 * // Check if already running (should be false now) if
 * (player.getAutoThievingState() !=
 * AutoThievingManager.AutoThievingState.STOPPED) {
 * sendDialogue("Auto-thieving is already running! Use 'Stop' to stop it first."
 * ); stage = 99; return; }
 * 
 * // Check level requirement int thievingLevel =
 * player.getSkills().getLevel(Skills.THIEVING); if (thievingLevel < 1) {
 * sendDialogue("You need at least level 1 Thieving to use auto-thieving!");
 * stage = 99; return; }
 * 
 * // Find best stall int bestStall =
 * AutoThievingManager.getCurrentBestStall(player); if (bestStall == -1) {
 * sendDialogue("No suitable stalls found for your thieving level (" +
 * thievingLevel + ")!"); stage = 99; return; }
 * 
 * // Show remaining time in confirmation int remainingMinutes =
 * AutoThievingManager.getRemainingTimeMinutes(player); String stallName =
 * AutoThievingManager.STALL_NAMES[bestStall];
 * sendOptionsDialogue("Start auto-thieving at " + stallName +
 * " stall?<br><br>Time remaining today: " + remainingMinutes + " minutes",
 * "Yes, start auto-thieving", "No, cancel"); stage = 2;
 * player.getTemporaryAttributtes().put("confirming_start", true); }
 * 
 * private void handleStop() { if (player.getAutoThievingState() ==
 * AutoThievingManager.AutoThievingState.STOPPED) {
 * sendDialogue("Auto-thieving is not currently running!"); stage = 99; return;
 * }
 * 
 * sendOptionsDialogue("Stop auto-thieving?", "Yes, stop it",
 * "No, keep running"); stage = 2;
 * player.getTemporaryAttributtes().put("confirming_stop", true); }
 * 
 * // Updated showStatus method: private void showStatus() { String status =
 * player.getAutoThievingState().toString(); String info =
 * "Auto-Thieving Status:<br><br>"; info += "Current State: " + status + "<br>";
 * 
 * if (player.getAutoThievingState() !=
 * AutoThievingManager.AutoThievingState.STOPPED) { String stallName =
 * AutoThievingManager.STALL_NAMES[player.getAutoThievingStall()]; info +=
 * "Current Stall: " + stallName + "<br>"; }
 * 
 * int currentLevel = player.getSkills().getLevel(Skills.THIEVING); int
 * currentXP = (int) player.getSkills().getXp(Skills.THIEVING); int nextLevelXP
 * = (int) Skills.getXPForLevel(currentLevel + 1);
 * 
 * info += "Thieving Level: " + currentLevel + "<br>"; info +=
 * "XP to next level: " + (nextLevelXP - currentXP) + "<br><br>";
 * 
 * // Show daily time limit info int remainingMinutes =
 * AutoThievingManager.getRemainingTimeMinutes(player); info +=
 * "Daily Time Limit: 60 minutes<br>"; info += "Time Remaining: " +
 * remainingMinutes + " minutes<br><br>";
 * 
 * info += "Settings:<br>"; info += "Auto-progress: " +
 * (player.isAutoProgressStalls() ? "ON" : "OFF") + "<br>"; info +=
 * "Stop on rogue: " + (player.isStopOnRogue() ? "ON" : "OFF") + "<br>"; info +=
 * "Inventory action: " +
 * getInventoryActionString(player.getThievingInventoryAction());
 * 
 * sendDialogue(info); stage = 99; }
 * 
 * private void showSettings() { String autoProgressStatus =
 * player.isAutoProgressStalls() ? "ON" : "OFF"; String stopOnRogueStatus =
 * player.isStopOnRogue() ? "ON" : "OFF";
 * 
 * sendOptionsDialogue("Auto-Thieving Settings", "Auto-progress stalls: " +
 * autoProgressStatus, "Stop on rogue: " + stopOnRogueStatus,
 * "Inventory settings", "Back to main menu"); stage = 1; }
 * 
 * private void showInventorySettings() { InventoryAction currentAction =
 * player.getThievingInventoryAction(); String bankStatus = currentAction ==
 * InventoryAction.AUTO_BANK ? " (CURRENT)" : ""; String dropStatus =
 * currentAction == InventoryAction.AUTO_DROP ? " (CURRENT)" : ""; String
 * stopStatus = currentAction == InventoryAction.STOP_WHEN_FULL ? " (CURRENT)" :
 * "";
 * 
 * sendOptionsDialogue("When inventory is full:", "Auto Bank" + bankStatus,
 * "Auto Drop" + dropStatus, "Stop When Full" + stopStatus, "Back to settings");
 * stage = 3; }
 * 
 * private String getInventoryActionString(InventoryAction action) { switch
 * (action) { case AUTO_BANK: return "Auto Bank"; case AUTO_DROP: return
 * "Auto Drop"; case STOP_WHEN_FULL: return "Stop When Full"; default: return
 * "Unknown"; } }
 * 
 * @Override public void finish() { // Clean up temporary attributes
 * player.getTemporaryAttributtes().remove("confirming_start");
 * player.getTemporaryAttributtes().remove("confirming_stop"); } }
 */