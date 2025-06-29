package com.rs.game.player.bot.definition;


import com.rs.game.player.content.Pots;

/**
 * Created by Valkyr on 19/05/2016.
 */
public enum ItemsDefinition {

   
    ROCKTAILS(15272, 16),
    SHARKS(385, 16),
    VARROCK_TAB(8007, 1),
    LUMBRIDGE_TAB(8008, 1),
    FALADOR_TAB(8009, 1),
    CAMELOT_TAB(8010, 1),
    // Runes
    AIR_RUNE(556, 1000),
    EARTH_RUNE(557, 1000),
    FIRE_RUNE(554, 1000),
    DEATH_RUNE(560, 1000),
    BLOOD_RUNE(565, 1000),
    WATER_RUNE(555, 1000),
    SOUL_RUNE(566, 1000),
    ASTRAL_RUNE(9075, 1000),
    DRAGON_HATCHET(6739, 1);

    private final int id;
    private final int amount;

    ItemsDefinition(int id, int amount) {
        this.id = id;
        this.amount = amount;
    }

    public int getId() {
        return id;
    }

    public int getAmount() {
        return amount;
    }
}
