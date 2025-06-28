package com.rs.game;

import com.rs.game.player.BotPlayer;
import com.rs.game.player.actions.automation.AutoSkillingManager;
import com.rs.game.player.actions.automation.AutoSkillingManager.SkillingType;
import com.rs.game.player.actions.automation.handlers.SkillHandler;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.game.player.actions.automation.AutoSkillingManager.InventoryAction;
import com.rs.utils.Logger; // Import Logger
import com.rs.utils.Utils;

/**
 * The "brain" for a BotPlayer.
 * This class makes decisions on what the bot should do, such as skilling or banking.
 */
public class BotAIHandler {

    private enum BotState {
        IDLE,
        SKILLING,
        BANKING,
        WALKING_TO_SKILL_AREA,
        WAITING_FOR_ACTION // New state to indicate waiting for pathfinding/action to complete
    }

    private final BotPlayer bot;
    private BotState currentState;
    private SkillingType currentSkill;
    private long lastLogTime = 0; // For throttling some logs
    private static final long LOG_THROTTLE_MS = 1000; // Log once per second for repetitive messages

    public BotAIHandler(BotPlayer bot) {
        this.bot = bot;
        this.currentState = BotState.IDLE;
        // Set default skilling action for the bot
        this.bot.setSkillingInventoryAction(InventoryAction.AUTO_BANK);
        // Set default thieving mode for testing
        // You might have a dialogue to set this for actual players, but for a bot, hardcode for now
        this.bot.setAutoThievingMode("AUTO"); // Or "SPECIFIC"
        this.bot.setAutoThievingStall(-1); // -1 means let AUTO mode pick, or set to 0 for Crafting stall if testing specific
        Logger.log("BotAIHandler", "Bot " + bot.getUsername() + " initialized. Default thieving mode: " + bot.getAutoThievingMode());
    }

    private void throttledLog(String message) {
        long currentTime = Utils.currentTimeMillis();
        if (currentTime - lastLogTime > LOG_THROTTLE_MS) {
            Logger.log("BotAIHandler", message);
            lastLogTime = currentTime;
        }
    }

    // IMPORTANT: No @Override here. This method is called by BotPlayer.processEntity().
    public void process() {
        // If bot is currently performing an action or actively walking, just return.
        // The action manager or walk steps manager will handle its progress.
        if (bot.getActionManager().getAction() != null) {
            // THIS IS THE FIRST POTENTIAL RETURN. If the bot somehow has a residual action, it'll stop here.
            throttledLog("Bot " + bot.getUsername() + " is busy with action: " + bot.getActionManager().getAction().getClass().getSimpleName() + ". State: " + currentState.name());
            currentState = BotState.WAITING_FOR_ACTION;
            return;
        }
        if (bot.hasWalkSteps()) {
            // THIS IS THE SECOND POTENTIAL RETURN. If the bot somehow has residual walk steps, it'll stop here.
            throttledLog("Bot " + bot.getUsername() + " is currently walking. State: " + currentState.name());
            currentState = BotState.WAITING_FOR_ACTION;
            return;
        }

        // If we were waiting for an action/walk and it just finished, transition back to IDLE to re-evaluate
        if (currentState == BotState.WAITING_FOR_ACTION) {
            Logger.log("BotAIHandler", "Bot " + bot.getUsername() + " finished previous action/walk. Transitioning to IDLE.");
            currentState = BotState.IDLE;
            // No return here, let it proceed to IDLE logic in the same tick if possible
        }

        // Main state machine
        switch (currentState) {
            case IDLE:
                Logger.log("BotAIHandler", "Bot " + bot.getUsername() + " is IDLE. Deciding next activity...");
                decideNextActivity();
                break;
            case SKILLING:
                Logger.log("BotAIHandler", "Bot " + bot.getUsername() + " is SKILLING (" + (currentSkill != null ? currentSkill.name() : "N/A") + "). Processing...");
                processSkilling();
                break;
            case BANKING:
                Logger.log("BotAIHandler", "Bot " + bot.getUsername() + " is BANKING. Processing...");
                processBanking();
                break;
            case WALKING_TO_SKILL_AREA:
                Logger.log("BotAIHandler", "Bot " + bot.getUsername() + " is WALKING_TO_SKILL_AREA. Processing...");
                processWalkingToSkillArea();
                break;
            case WAITING_FOR_ACTION:
                // This state should ideally be handled by the initial `if` block,
                // but included here for completeness. We should not reach here if `hasWalkSteps()` or `getAction()` is true.
                // If we reach here, it implies an action *just* finished, and we transitioned to IDLE above.
                break;
        }
    }

    /**
     * Decides which skill to train next.
     */
    private void decideNextActivity() {
        currentSkill = SkillingType.THIEVING; // Hardcoded for this bot for now
        Logger.log("BotAIHandler", "Bot " + bot.getUsername() + " considering thieving. Checking if in skilling hub...");

        if (!AutoSkillingManager.isInSkillingHub(bot)) {
            currentState = BotState.WALKING_TO_SKILL_AREA;
            Logger.log("BotAIHandler", "Bot " + bot.getUsername() + " is NOT in skilling hub. Transitioning to WALKING_TO_SKILL_AREA.");
        } else {
            Logger.log("BotAIHandler", "Bot " + bot.getUsername() + " IS in skilling hub. Attempting to start skilling.");
            startSkilling();
        }
    }

    private void startSkilling() {
        bot.setAutoSkillingType(currentSkill);
        Logger.log("BotAIHandler", "Bot " + bot.getUsername() + " attempting to start skilling: " + currentSkill.name() + ". Checking canStart...");

        // Ensure currentSkill is not null (safety check)
        if (currentSkill == null) {
            Logger.log("BotAIHandler", "Bot " + bot.getUsername() + " currentSkill is null. Cannot start skilling. Reverting to IDLE.");
            currentState = BotState.IDLE;
            return;
        }

        SkillHandler handler = AutoSkillingManager.getHandler(currentSkill);
        if (handler == null) {
             Logger.log("BotAIHandler", "Bot " + bot.getUsername() + " no handler found for " + currentSkill.name() + ". Reverting to IDLE.");
             currentState = BotState.IDLE;
             return;
        }

        if (handler.canStart(bot)) {
            currentState = BotState.SKILLING;
            Logger.log("BotAIHandler", "Bot " + bot.getUsername() + " CAN start " + currentSkill.name() + ". Transitioning to SKILLING.");
            // Call processSkilling immediately to try and initiate the action
            processSkilling();
        } else {
            Logger.log("BotAIHandler", "Bot " + bot.getUsername() + " CANNOT start " + currentSkill.name() + ". Reverting to IDLE.");
            currentState = BotState.IDLE;
            // The canStart method (in AutoThievingHandler) should send player messages if it fails.
            // If it continuously fails, the bot will stay IDLE and spam these logs.
        }
    }

    /**
     * Manages the skilling process, checking for full inventory.
     */
    private void processSkilling() {
        if (!bot.getInventory().hasFreeSlots()) {
            currentState = BotState.BANKING;
            Logger.log("BotAIHandler", "Bot " + bot.getUsername() + " inventory full. Transitioning to BANKING.");
            return;
        }

        // This is where the actual skilling action should be initiated/continued by the specific handler
        Logger.log("BotAIHandler", "Bot " + bot.getUsername() + " inventory not full. Asking AutoSkillingManager to process " + currentSkill.name() + ".");
        SkillHandler handler = AutoSkillingManager.getHandler(currentSkill);
        if (handler != null) {
            handler.process(bot); // This call should trigger actual thieving attempts
        } else {
            Logger.log("BotAIHandler", "Bot " + bot.getUsername() + " no handler found during processSkilling. Reverting to IDLE.");
            currentState = BotState.IDLE;
        }
    }

    /**
     * Manages the banking process.
     */
    private void processBanking() {
        if (bot.getInterfaceManager().containsBankInterface()) {
            Logger.log("BotAIHandler", "Bot " + bot.getUsername() + " bank interface open. Depositing all.");
            bot.getBank().depositAllInventory(true);
            // Add a small delay for the deposit animation/process to complete before closing
            WorldTasksManager.schedule(new WorldTask() {
                @Override
                public void run() {
                    bot.closeInterfaces();
                    currentState = BotState.WALKING_TO_SKILL_AREA;
                    Logger.log("BotAIHandler", "Bot " + bot.getUsername() + " deposited. Transitioning to WALKING_TO_SKILL_AREA.");
                }
            }, 1); // 1 game tick delay
            return;
        }

        Logger.log("BotAIHandler", "Bot " + bot.getUsername() + " bank interface not open. Checking if at bank...");
        if (AutoSkillingManager.isAtBank(bot)) {
            Logger.log("BotAIHandler", "Bot " + bot.getUsername() + " is at bank. Opening bank.");
            bot.getBank().openPlayerBank();
        } else {
            Logger.log("BotAIHandler", "Bot " + bot.getUsername() + " is not at bank. Starting banking sequence.");
            AutoSkillingManager.startBankingSequence(bot);
            currentState = BotState.WAITING_FOR_ACTION; // Wait for walk to bank
        }
    }

    /**
     * Manages walking back to the main skilling area.
     */
    private void processWalkingToSkillArea() {
        if (AutoSkillingManager.isInSkillingHub(bot)) {
            currentState = BotState.IDLE; // Once in hub, re-evaluate from IDLE
            Logger.log("BotAIHandler", "Bot " + bot.getUsername() + " arrived at skilling hub. Transitioning to IDLE.");
            return;
        }

        // Only try to walk if not already walking
        if (!bot.hasWalkSteps()) {
            Logger.log("BotAIHandler", "Bot " + bot.getUsername() + " has no walk steps. Asking AutoSkillingManager to walk to hub.");
            AutoSkillingManager.walkToSkillingHubCenter(bot);
            currentState = BotState.WAITING_FOR_ACTION; // Wait for walk to complete
        }
    }
}