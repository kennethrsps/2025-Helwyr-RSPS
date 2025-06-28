package com.rs.game.player.actions.automation.handlers;

import com.rs.game.player.Player;

public class AutoCookingHandler implements SkillHandler {
    private static AutoCookingHandler instance;
    
    public static AutoCookingHandler getInstance() {
        if (instance == null) {
            instance = new AutoCookingHandler();
        }
        return instance;
    }
    
    @Override
    public boolean canStart(Player player) {
        player.sendMessage("Auto-cooking not implemented yet!");
        return false;
    }
    
    @Override
    public void process(Player player) {
        // TODO: Implement cooking logic
    }
    
    @Override
    public String getSkillName() {
        return "Cooking";
    }
}
