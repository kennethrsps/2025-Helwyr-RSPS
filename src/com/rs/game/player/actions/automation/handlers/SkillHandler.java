
package com.rs.game.player.actions.automation.handlers;

import com.rs.game.player.Player;

/**
 * Base interface for all auto-skilling handlers
 */
public interface SkillHandler {
    /**
     * Check if player can start this skill
     * @param player The player
     * @return true if can start, false otherwise
     */
    boolean canStart(Player player);
    
    /**
     * Process the skill-specific logic
     * @param player The player
     */
    void process(Player player);
    
    /**
     * Get the skill name
     * @return The skill name
     */
    String getSkillName();
}