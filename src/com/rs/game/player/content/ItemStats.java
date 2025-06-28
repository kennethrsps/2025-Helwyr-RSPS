package com.rs.game.player.content;

/**
 * Data structure to hold item bonus stats
 * Place this in your game/item package
 */
public class ItemStats {
    
    // Attack bonuses
    public int stabAttack = 0;
    public int slashAttack = 0;
    public int crushAttack = 0;
    public int magicAttack = 0;
    public int rangedAttack = 0;
    
    // Defense bonuses
    public int stabDefense = 0;
    public int slashDefense = 0;
    public int crushDefense = 0;
    public int magicDefense = 0;
    public int rangedDefense = 0;
    public int summoningDefense = 0;
    
    // Absorption bonuses
    public int meleeAbsorption = 0;
    public int magicAbsorption = 0;
    public int rangedAbsorption = 0;
    
    // Other bonuses
    public int strengthBonus = 0;
    public int rangedStrength = 0;
    public int prayerBonus = 0;
    public int magicDamage = 0;
    
    /**
     * Constructor
     */
    public ItemStats() {
        // Default values already set above
    }
    
    /**
     * Copy constructor
     */
    public ItemStats(ItemStats other) {
        this.stabAttack = other.stabAttack;
        this.slashAttack = other.slashAttack;
        this.crushAttack = other.crushAttack;
        this.magicAttack = other.magicAttack;
        this.rangedAttack = other.rangedAttack;
        
        this.stabDefense = other.stabDefense;
        this.slashDefense = other.slashDefense;
        this.crushDefense = other.crushDefense;
        this.magicDefense = other.magicDefense;
        this.rangedDefense = other.rangedDefense;
        this.summoningDefense = other.summoningDefense;
        
        this.meleeAbsorption = other.meleeAbsorption;
        this.magicAbsorption = other.magicAbsorption;
        this.rangedAbsorption = other.rangedAbsorption;
        
        this.strengthBonus = other.strengthBonus;
        this.rangedStrength = other.rangedStrength;
        this.prayerBonus = other.prayerBonus;
        this.magicDamage = other.magicDamage;
    }
    
    /**
     * Reset all stats to zero
     */
    public void reset() {
        stabAttack = slashAttack = crushAttack = magicAttack = rangedAttack = 0;
        stabDefense = slashDefense = crushDefense = magicDefense = rangedDefense = summoningDefense = 0;
        meleeAbsorption = magicAbsorption = rangedAbsorption = 0;
        strengthBonus = rangedStrength = prayerBonus = magicDamage = 0;
    }
    
    /**
     * Get total offensive power
     */
    public int getTotalOffensive() {
        return stabAttack + slashAttack + crushAttack + magicAttack + rangedAttack + 
               strengthBonus + rangedStrength + magicDamage;
    }
    
    /**
     * Get total defensive power
     */
    public int getTotalDefensive() {
        return stabDefense + slashDefense + crushDefense + magicDefense + rangedDefense + 
               meleeAbsorption + magicAbsorption + rangedAbsorption;
    }
}