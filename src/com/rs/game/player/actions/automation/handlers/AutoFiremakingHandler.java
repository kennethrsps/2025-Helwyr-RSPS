package com.rs.game.player.actions.automation.handlers;

import com.rs.game.player.Player;

public class AutoFiremakingHandler implements SkillHandler {
    private static AutoFiremakingHandler instance;
    
    public static AutoFiremakingHandler getInstance() {
        if (instance == null) {
            instance = new AutoFiremakingHandler();
        }
        return instance;
    }
    
    @Override
    public boolean canStart(Player player) {
        player.sendMessage("Auto-firemaking not implemented yet!");
        return false;
    }
    
    @Override
    public void process(Player player) {
        // TODO: Implement firemaking logic
    }
    
    @Override
    public String getSkillName() {
        return "Firemaking";
    }
}