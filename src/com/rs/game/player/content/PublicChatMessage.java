package com.rs.game.player.content;

public class PublicChatMessage {
    private String message;
    private int effects;
    
    public PublicChatMessage(String message, int effects) {
        this.message = message;
        this.effects = effects;
    }
    
    public PublicChatMessage(String message) {
        this(message, 0); // Default to no effects
    }
    
    public int getEffects() {
        return effects;
    }
    
    public String getMessage() {
        return message;
    }
}