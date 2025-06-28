package com.rs.game.player;

import com.rs.game.BotAIHandler;
import com.rs.game.World;
import com.rs.game.WorldTile;
import com.rs.game.player.content.Trade;

/**
 * Represents a bot-controlled Player. This class is now much simpler and does not
 * override a non-existent init method. The AI Handler is attached by the BotManager
 * after all initialization is complete.
 */
public class BotPlayer extends Player {

    private transient BotAIHandler aiHandler;
    // It's assumed that 'isBot' field and its getter/setter exist in the Player.java class.
    // If not, you would need to add them here and implement them.
    // private transient boolean isBot;

    /**
     * This constructor calls the Player(String, String) constructor, which is
     * confirmed to exist in your Player.java file.
     *
     * IMPORTANT: This constructor immediately sets the bot's starting location
     * and flags to bypass the tutorial.
     */
    public BotPlayer() {
        super("bot_password", "bot_mac_address"); // Call the parent Player constructor
        this.familiar = null;
        // --- Start of immediate bot-specific initialization ---
        // These steps are crucial for bypassing the tutorial and setting up the bot correctly.

        // Set the bot's initial world tile immediately to bypass the tutorial spawn.
        // REPLACE these coordinates with your actual desired bot spawn location outside the tutorial.
        // Example: a skilling hub or a safe main-game area.
        this.setNextWorldTile(new WorldTile(1375, 5669, 0)); // Example: Set to a non-tutorial hub

        // Mark the player as a bot immediately. This flag is used in LoginManager.sendLogin.
        this.setBot(true);

        // Mark the tutorial as completed. This is crucial for StarterTutorialD to bypass.
        this.setHasCompleted(true);

        // Mark the player as logged in. This can prevent certain login-related interfaces
        // or states from being triggered that expect a human interaction.
        this.setLogedIn();
        // --- End of immediate bot-specific initialization ---
    }

    /**
     * This method is called by the BotManager to attach the AI brain
     * after the bot has been fully constructed and initialized.
     * @param handler The AI Handler to be attached to this bot.
     */
    public void attachAIHandler(BotAIHandler handler) {
        this.aiHandler = handler;
        this.setBot(true); // Redundant if already set in constructor, but harmless.
    }

    /**
     * The main processing tick for the bot.
     * It runs the normal player processing and then the AI logic.
     */
    @Override // <--- This annotation MUST be present
    public void processEntity() {
        // --- ADDED THIS LINE FOR DEBUGGING ---
        System.out.println("DEBUG: BotPlayer " + getUsername() + " processEntity() called. aiHandler is null: " + (aiHandler == null));
        // --- END DEBUG LINE ---

        super.processEntity(); // Process movement, etc.
        if (aiHandler != null) {
            aiHandler.process(); // Process AI decision-making
        }
    }

    /**
     * Overrides the logout method to prevent bots from being logged out by normal means.
     * Bots should only be removed via the BotManager.
     */
    @Override
    public void logout(boolean lobby) {
        // Bots do not log out. They are removed.
        System.out.println("Attempted to log out bot " + getUsername() + ". Use BotManager.removeBot() instead.");
    }

    /**
     * A custom finish method for bots to ensure they are removed correctly.
     * Corrected to use getGlobalPlayerUpdater() which contains the title information.
     */
    @Override
    public void finish() {
        if (hasFinished())
            return;
        setFinished(true);
        stopAll(true, true, true);
        if (getGlobalPlayerUpdater().getTitle() == 0) // Assuming getGlobalPlayerUpdater() returns an object with getTitle()
            getGlobalPlayerUpdater().setTitle(0); // Assuming setTitle() exists on that object
        if (getTrade() != null)
            getTrade().closeTrade(Trade.CloseTradeStage.CANCEL);
        if (getClanManager() != null)
            getClanManager().disconnect(this, false);
        if (getGuestClanManager() != null)
            getGuestClanManager().disconnect(this, true);
        World.removePlayer(this);
        System.out.println("Bot " + getUsername() + " has been finished and removed from the world.");
    }

    // --- Assuming these methods are in Player.java. If not, uncomment and add them here ---
    /*
    @Override
    public boolean isBot() {
        return isBot;
    }

    @Override
    public void setBot(boolean isBot) {
        this.isBot = isBot;
    }
    */
}