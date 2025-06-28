package com.rs.game.npc.combat;

public class NPCCombatDefinitions {

    public static final int MELEE = 0;
    public static final int RANGE = 1;
    public static final int MAGE = 2;
    public static final int SPECIAL = 3;
    public static final int SPECIAL2 = 4; // follows no distance
    public static final int PASSIVE = 0;
    public static final int AGRESSIVE = 1;

    public static final int RANGE_FOLLOW = 3;
    public static final int MAGE_FOLLOW = 4;

    private int hitpoints;
    private int attackAnim;
    private int defenceAnim;
    private int deathAnim;
    private int attackDelay;
    private int deathDelay;
    private int respawnDelay;
    private int maxHit;
    private int attackStyle;
    private int attackGfx;
    private int attackProjectile;
    private int agressivenessType;

    public NPCCombatDefinitions(int hitpoints, int attackAnim, int defenceAnim,
	    int deathAnim, int attackDelay, int deathDelay, int respawnDelay,
	    int maxHit, int attackStyle, int attackGfx, int attackProjectile,
	    int agressivenessType) {
	this.hitpoints = hitpoints;
	this.attackAnim = attackAnim;
	this.defenceAnim = defenceAnim;
	this.deathAnim = deathAnim;
	this.attackDelay = attackDelay;
	this.deathDelay = deathDelay;
	this.respawnDelay = respawnDelay;
	this.maxHit = maxHit;
	this.attackStyle = attackStyle;
	this.attackGfx = attackGfx;
	this.attackProjectile = attackProjectile;
	this.agressivenessType = agressivenessType;
    }

    public int getAgressivenessType() {
	return agressivenessType;
    }

    public int getAttackDelay() {
    	return attackDelay;
    }
    
    public void setAttackDelay(int delay) {
    	this.attackDelay = delay;
    }

    public int getAttackEmote() {
	return attackAnim;
    }

    public int getAttackGfx() {
	return attackGfx;
    }

    public int getAttackProjectile() {
	return attackProjectile;
    }

    public int getAttackStyle() {
	return attackStyle;
    }

    public int getDeathDelay() {
	return deathDelay;
    }

    public int getDeathEmote() {
	return deathAnim;
    }

    public int getDefenceEmote() {
	return defenceAnim;
    }

    public int getHitpoints() {
	return hitpoints;
    }

    public int getMaxHit() {
	return maxHit;
    }

    public int getRespawnDelay() {
    	return respawnDelay;
    }

    public void setHitpoints(int amount) {
	this.hitpoints = amount;
    }
 // Add these methods to your NPCCombatDefinitions class
    public void setMaxHit(int maxHit) {
        this.maxHit = maxHit;
    }

    public void setAttackStyle(int attackStyle) {
        this.attackStyle = attackStyle;
    }

    public void setAgressivenessType(int agressivenessType) {
        this.agressivenessType = agressivenessType;
    }

    public NPCCombatDefinitions copy() {
        return new NPCCombatDefinitions(
            this.hitpoints, this.attackAnim, this.defenceAnim, this.deathAnim,
            this.attackDelay, this.deathDelay, this.respawnDelay, this.maxHit,
            this.attackStyle, this.attackGfx, this.attackProjectile, this.agressivenessType
        );
    }
}
