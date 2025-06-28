package com.rs.game.player;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.rs.Settings;
import com.rs.game.World;
import com.rs.game.item.Item;
import com.rs.game.npc.others.randoms.CraftingRandom;
import com.rs.game.npc.others.randoms.FarmingRandom;
import com.rs.game.npc.others.randoms.FletchingRandom;
import com.rs.game.npc.pet.Pet;
import com.rs.game.player.actions.Cooking;
import com.rs.game.player.actions.divination.DivineObject;
import com.rs.game.player.actions.summoning.Summoning;
import com.rs.game.player.actions.thieving.Thieving;
import com.rs.game.player.content.RuneCrafting;
import com.rs.game.player.content.TaskTab;
import com.rs.game.player.content.clans.ClansManager;
import com.rs.game.player.content.pet.Pets;
import com.rs.game.player.controllers.CitadelControler;
import com.rs.game.player.controllers.zombie.ZombieControler;
import com.rs.game.player.dialogue.impl.LevelUp;
import com.rs.utils.Colors;
import com.rs.utils.Logger;
import com.rs.utils.Utils;
import com.rs.utils.mysql.impl.VoteManager;

/**
 * A class used to handle all players Skills & Experience.
 * 
 * @author Zeus
 */
public final class Skills implements Serializable {

    private static final long serialVersionUID = -7086829989489745985L;

    // Constants
    public static final double MAXIMUM_EXP = 2_000_000_000.0;
    private static final int SKILL_COUNT = 27;
    private static final int COUNTER_COUNT = 3;
    private static final int TARGET_COUNT = 25;
    
    // Experience milestones
    private static final long[] EXP_MILESTONES = {
        104_273_167L, 250_000_000L, 500_000_000L, 1_000_000_000L, 2_000_000_000L
    };

    // Skill constants
    public static final int ATTACK = 0, DEFENCE = 1, STRENGTH = 2, HITPOINTS = 3, RANGE = 4, 
                            PRAYER = 5, MAGIC = 6, COOKING = 7, WOODCUTTING = 8, FLETCHING = 9, 
                            FISHING = 10, FIREMAKING = 11, CRAFTING = 12, SMITHING = 13, 
                            MINING = 14, HERBLORE = 15, AGILITY = 16, THIEVING = 17, SLAYER = 18, 
                            FARMING = 19, RUNECRAFTING = 20, HUNTER = 21, CONSTRUCTION = 22, 
                            SUMMONING = 23, DUNGEONEERING = 24, DIVINATION = 25, INVENTION = 26;

    public static final String[] SKILL_NAME = { 
        "Attack", "Defence", "Strength", "Constitution", "Ranged", "Prayer",
        "Magic", "Cooking", "Woodcutting", "Fletching", "Fishing", "Firemaking", 
        "Crafting", "Smithing", "Mining", "Herblore", "Agility", "Thieving", 
        "Slayer", "Farming", "Runecrafting", "Hunter", "Construction",
        "Summoning", "Dungeoneering", "Divination", "Invention" 
    };

    // Equipment IDs for outfit bonuses
    private static final class OutfitIds {
        // Artisan outfit
        static final int[] ARTISAN_HAT = {25185, 32281};
        static final int ARTISAN_CHEST = 25186;
        static final int ARTISAN_LEGS = 25187;
        static final int ARTISAN_BOOTS = 25188;
        static final int ARTISAN_GLOVES = 25189;
        
        // Fletcher outfit
        static final int FLETCHER_HAT = 36899;
        static final int FLETCHER_CHEST = 36898;
        static final int FLETCHER_LEGS = 36897;
        static final int FLETCHER_BOOTS = 36895;
        static final int FLETCHER_GLOVES = 36896;
        
        // Farmer outfit
        static final int[] FARMER_HAT = {31347, 34926};
        static final int FARMER_CHEST = 31346;
        static final int FARMER_LEGS = 31345;
        static final int FARMER_BOOTS = 31343;
        static final int FARMER_GLOVES = 31344;
    }

    // Instance variables
    private short[] level;
    private double[] xp;
    private double[] xpTracks;
    private boolean[] trackSkills;
    private byte[] trackSkillsIds;

    private boolean xpDisplay;
    private boolean xpPopup;
    private transient int currentCounter;

    private boolean[] enabledSkillsTargets;
    private boolean[] skillsTargetsUsingLevelMode;
    private int[] skillsTargetsValues;

    private Map<String, Boolean> spinMap;
    private transient Player player;

    // Cache for frequently used calculations - made static to avoid serialization issues
    private static final ThreadLocal<DecimalFormat> decimalFormatCache = 
        ThreadLocal.withInitial(() -> new DecimalFormat("#,###,##0"));

    public Skills() {
        initializeArrays();
        setDefaultValues();
    }

    private void initializeArrays() {
        level = new short[SKILL_COUNT];
        xp = new double[SKILL_COUNT];
        xpTracks = new double[COUNTER_COUNT];
        trackSkills = new boolean[COUNTER_COUNT];
        trackSkillsIds = new byte[COUNTER_COUNT];
        enabledSkillsTargets = new boolean[TARGET_COUNT];
        skillsTargetsUsingLevelMode = new boolean[TARGET_COUNT];
        skillsTargetsValues = new int[TARGET_COUNT];
        spinMap = new HashMap<>();
    }

    private void setDefaultValues() {
        Arrays.fill(level, (short) 1);
        Arrays.fill(xp, 0.0);
        Arrays.fill(trackSkillsIds, (byte) 30);
        
        // Set default levels
        level[HITPOINTS] = 10;
        xp[HITPOINTS] = 1155.0;
        level[HERBLORE] = 3;
        xp[HERBLORE] = 175.0;
        
        xpPopup = true;
        trackSkills[0] = true;
    }

    public void addSkillXpRefresh(int skill, double xp) {
        if (!isValidSkill(skill)) return;
        
        this.xp[skill] += xp;
        level[skill] = (short) getLevelForXp(skill);
    }

    private double getXPRates(int skill, double exp) {
        if (player == null) return 1.0;
        
        if (player.isVeteran()) return Settings.VET_XP;
        if (player.isIntermediate()) return Settings.INTERM_XP;
        if (player.isEasy()) return Settings.EASY_XP;
        if (player.isExpert()) return Settings.EXPERT_XP;
        return Settings.IRONMAN_XP;
    }

    public double xpNoBonus(int skill, double exp) {
        double multiplier = getXPRates(skill, exp) * ((World.isWellActive() || World.isWeekend()) ? 2.0 : 1.0);
        return exp / multiplier;
    }

    public double artisansBonus() {
        if (player == null) return 1.0;
        
        double xpBoost = 1.0;
        int hatId = player.getEquipment().getHatId();
        int chestId = player.getEquipment().getChestId();
        int legsId = player.getEquipment().getLegsId();
        int bootsId = player.getEquipment().getBootsId();
        int glovesId = player.getEquipment().getGlovesId();

        // Individual piece bonuses
        if (hatId == OutfitIds.ARTISAN_HAT[0]) xpBoost *= 1.01;
        if (hatId == OutfitIds.ARTISAN_HAT[1]) xpBoost *= 1.03;
        if (chestId == OutfitIds.ARTISAN_CHEST) xpBoost *= 1.01;
        if (legsId == OutfitIds.ARTISAN_LEGS) xpBoost *= 1.01;
        if (bootsId == OutfitIds.ARTISAN_BOOTS) xpBoost *= 1.01;
        if (glovesId == OutfitIds.ARTISAN_GLOVES) xpBoost *= 1.01;

        // Full set bonuses
        boolean hasFullSet = (chestId == OutfitIds.ARTISAN_CHEST && 
                             legsId == OutfitIds.ARTISAN_LEGS && 
                             bootsId == OutfitIds.ARTISAN_BOOTS && 
                             glovesId == OutfitIds.ARTISAN_GLOVES);

        if (hasFullSet) {
            if (hatId == OutfitIds.ARTISAN_HAT[0]) xpBoost *= 1.01;
            else if (hatId == OutfitIds.ARTISAN_HAT[1]) xpBoost *= 1.03;
        }

        return xpBoost;
    }

    public double fletchersBonus() {
        if (player == null) return 1.0;
        
        double xpBoost = 1.0;
        int hatId = player.getEquipment().getHatId();
        int chestId = player.getEquipment().getChestId();
        int legsId = player.getEquipment().getLegsId();
        int bootsId = player.getEquipment().getBootsId();
        int glovesId = player.getEquipment().getGlovesId();

        // Individual piece bonuses
        if (hatId == OutfitIds.FLETCHER_HAT) xpBoost *= 1.01;
        if (chestId == OutfitIds.FLETCHER_CHEST) xpBoost *= 1.01;
        if (legsId == OutfitIds.FLETCHER_LEGS) xpBoost *= 1.01;
        if (bootsId == OutfitIds.FLETCHER_BOOTS) xpBoost *= 1.01;
        if (glovesId == OutfitIds.FLETCHER_GLOVES) xpBoost *= 1.01;

        // Full set bonus
        if (hatId == OutfitIds.FLETCHER_HAT && chestId == OutfitIds.FLETCHER_CHEST && 
            legsId == OutfitIds.FLETCHER_LEGS && bootsId == OutfitIds.FLETCHER_BOOTS && 
            glovesId == OutfitIds.FLETCHER_GLOVES) {
            xpBoost *= 1.01;
        }

        return xpBoost;
    }

    public double farmersBonus() {
        if (player == null) return 1.0;
        
        double xpBoost = 1.0;
        int hatId = player.getEquipment().getHatId();
        int chestId = player.getEquipment().getChestId();
        int legsId = player.getEquipment().getLegsId();
        int bootsId = player.getEquipment().getBootsId();
        int glovesId = player.getEquipment().getGlovesId();

        // Individual piece bonuses
        if (hatId == OutfitIds.FARMER_HAT[0]) xpBoost *= 1.01;
        if (hatId == OutfitIds.FARMER_HAT[1]) xpBoost *= 1.03;
        if (chestId == OutfitIds.FARMER_CHEST) xpBoost *= 1.01;
        if (legsId == OutfitIds.FARMER_LEGS) xpBoost *= 1.01;
        if (bootsId == OutfitIds.FARMER_BOOTS) xpBoost *= 1.01;
        if (glovesId == OutfitIds.FARMER_GLOVES) xpBoost *= 1.01;

        // Full set bonuses
        boolean hasFullSetBase = (chestId == OutfitIds.FARMER_CHEST && 
                                 legsId == OutfitIds.FARMER_LEGS && 
                                 bootsId == OutfitIds.FARMER_BOOTS && 
                                 glovesId == OutfitIds.FARMER_GLOVES);

        if (hasFullSetBase) {
            if (hatId == OutfitIds.FARMER_HAT[0]) xpBoost *= 1.01;
            else if (hatId == OutfitIds.FARMER_HAT[1]) xpBoost *= 1.03;
        }

        return xpBoost;
    }

    public boolean isCombatSkill(int skill) {
        return skill >= ATTACK && skill <= MAGIC;
    }

    public void addXp(int skill, double exp, boolean forceRSXP) {
        addXp(skill, exp, forceRSXP, true);
    }

    public void addXp(int skill, double exp) {
        addXp(skill, exp, false);
    }

    public double getModifiedXP(int skill, double exp) {
        if (player == null) return exp;
        
        exp *= getXPRates(skill, exp);
        boolean insideDung = player.getDungManager().isInside();
        
        if (insideDung) {
            exp /= 2.0;
        } else {
            exp = applyEquipmentBonuses(skill, exp);
        }

        exp = applyPlayerBonuses(exp);
        exp = applyWorldBonuses(exp);
        exp = applySpecialBonuses(skill, exp);

        if (player.customEXP != 0) {
            exp *= player.customEXP;
        }

        return exp;
    }

    private double applyEquipmentBonuses(int skill, double exp) {
        switch (skill) {
            case DIVINATION:
                return exp * DivineObject.divinationSuit(player);
            case THIEVING:
                return exp * Thieving.outfitBoost(player);
            case RUNECRAFTING:
                return exp * RuneCrafting.runecrafterSuit(player);
            case SUMMONING:
                return exp * Summoning.shamanSuit(player);
            case COOKING:
                return exp * Cooking.chefsSuit(player);
            case FLETCHING:
                exp *= fletchersBonus();
                if (Utils.random(750) == 0) {
                    new FletchingRandom(player, player);
                }
                return exp;
            case CRAFTING:
                exp *= artisansBonus();
                if (Utils.random(750) == 0) {
                    new CraftingRandom(player, player);
                }
                return exp;
            case FARMING:
                exp *= farmersBonus();
                if (Utils.random(250) == 0) {
                    new FarmingRandom(player, player);
                }
                return exp;
            default:
                return exp;
        }
    }

    private double applyPlayerBonuses(double exp) {
        if (player.hasBonusEXP()) {
            try {
                double percentage = Double.parseDouble(getPercentage());
                exp *= (percentage / 100.0) + 1.0;
                if (Settings.SUPERLOG) {
                    Logger.log("[VOTE BOOK XP]: " + player.getDisplayName() + 
                              "'s modifier: " + (percentage / 100.0));
                }
            } catch (NumberFormatException e) {
                Logger.log("Invalid percentage format in bonus XP calculation");
            }
        }

        if (player.isDoubleXp()) {
            try {
                double percentage = Double.parseDouble(getPercentage());
                exp *= (percentage / 100.0) + 1.0;
            } catch (NumberFormatException e) {
                Logger.log("Invalid percentage format in double XP calculation");
            }
        }

        if (player.getAuraManager().usingWisdom()) {
            exp *= 1.25;
        }

        if (player.isDonator()) {
            exp *= (player.getMoneySpent() / 100.0) + 1.0;
        }

        return exp;
    }

    private double applyWorldBonuses(double exp) {
        if (player.getQuadExp() > Utils.currentTimeMillis()) {
            return exp * 4.0;
        }

        if (World.isWellActive() || World.isWeekend()) {
            exp *= 2.0;
            exp += (exp / 100.0) * 10.0; // Additional 10% bonus
        } else if (VoteManager.gotDXP()) {
            exp += exp; // Double XP
        }

        if (player.getDailyTaskManager().hasDoubleXpActivated()) {
            exp *= 1.25;
        }

        return exp;
    }

    private double applySpecialBonuses(int skill, double exp) {
        // Perk bonuses
        if (skill == HERBLORE && player.getPerkManager().herbivore) {
            exp *= 1.25;
        }
        if (skill == PRAYER && player.getPerkManager().prayerBetrayer) {
            exp *= 1.25;
        }
        if (skill == DIVINATION && player.getPerkManager().masterDiviner) {
            exp *= 1.25;
        }
        if (skill == HUNTER && player.getPerkManager().huntsman) {
            exp *= 1.25;
        }

        // Controller bonuses/penalties
        if (player.getControlerManager().getControler() instanceof ZombieControler) {
            exp *= 0.5;
        }

        return exp;
    }

    public String getPercentage() {
        if (player == null) return "25";
        
        if (player.isSponsor()) return Settings.expBoosts[6][1];
        if (player.isDiamond()) return Settings.expBoosts[5][1];
        if (player.isPlatinum()) return Settings.expBoosts[4][1];
        if (player.isGold()) return Settings.expBoosts[3][1];
        if (player.isSilver()) return Settings.expBoosts[2][1];
        if (player.isBronze()) return Settings.expBoosts[1][1];
        return "25";
    }

    public void addXp(int skill, double exp, boolean forceRSXP, boolean xpShare) {
        if (player == null || !isValidSkill(skill) || player.isXpLocked()) {
            return;
        }

        if (isCombatSkill(skill) && player.nearDummy()) {
            return;
        }

        // Apply clan experience
        applyClanExperience(skill, exp);

        // Pet experience
        if (player.getPet() != null) {
            player.getPet().addExperience(exp / 6.0);
        }

        // Pet bonuses
        exp = applyPetBonuses(skill, exp);

        // Special skill handling
        if (skill == AGILITY) {
            Pets.checkSkillingPet(player, 38075);
            player.getAchManager().addKeyAmount("agility", 1);
        }

        // Modify XP if not forcing RS rates
        if (!forceRSXP) {
            exp = getModifiedXP(skill, exp);
        }

        // Track XP
        player.getControlerManager().trackXP(skill, (int) exp);

        // Share XP if enabled
        if (xpShare) {
            player.getXpSharing().sendXPShare(skill, exp);
        }

        // Apply the experience
        applyExperience(skill, exp);
    }

    private void applyClanExperience(int skill, double exp) {
        ClansManager manager = player.getClanManager();
        if (manager == null || isCombatSkill(skill)) return;

        int clanExp = calculateClanExp(exp);
        if (player.getControlerManager().getControler() instanceof CitadelControler) {
            clanExp = (int) (exp / 10.0);
        }

        manager.getClan().getExperienceType().addDefaultLevelingExp(player, clanExp);
    }

    private int calculateClanExp(double exp) {
        if (player.isEasy() || player.isIntermediate()) {
            return (int) (exp / 30.0);
        }
        if (player.isVeteran() || player.isExpert() || 
           (player.isIronMan() && player.isHCIronMan())) {
            return (int) (exp / 20.0);
        }
        return 0;
    }

    private double applyPetBonuses(int skill, double exp) {
        if (player.getPetManager().isCombatPet() && isCombatSkill(skill)) {
            exp += addPetExperience(exp);
        }
        if (player.getPetManager().isSkillingPet() && !isCombatSkill(skill)) {
            exp += addPetExperience(exp);
        }
        return exp;
    }

    private void applyExperience(int skill, double exp) {
        int oldLevel = getLevelForXp(skill);
        long oldXP = (long) xp[skill];

        xp[skill] = Math.min(xp[skill] + exp, MAXIMUM_EXP);

        // Update XP trackers
        updateXpTrackers(skill, exp);

        // Check for milestone achievements
        checkMilestones(skill, oldXP);

        // Handle level ups
        int newLevel = getLevelForXp(skill);
        if (newLevel > oldLevel) {
            handleLevelUp(skill, newLevel, oldLevel);
        }

        refresh(skill);
        handleSkillShards(skill);
        handleXpDisplay(skill, exp);
    }

    private void updateXpTrackers(int skill, double exp) {
        for (int i = 0; i < trackSkills.length; i++) {
            if (trackSkills[i] && shouldTrackSkill(i, skill)) {
                xpTracks[i] += exp;
                refreshCounterXp(i);
            }
        }
    }

    private boolean shouldTrackSkill(int trackerId, int skill) {
        return trackSkillsIds[trackerId] == 30 || // Track all
               (trackSkillsIds[trackerId] == 29 && isCombatSkill(skill)) || // Track combat
               trackSkillsIds[trackerId] == getCounterSkill(skill); // Track specific
    }

    private void checkMilestones(int skill, long oldXP) {
        if (skill == DUNGEONEERING) return; // Skip dungeoneering milestones
        
        long currentXP = (long) xp[skill];
        
        for (int i = 0; i < EXP_MILESTONES.length; i++) {
            long milestone = EXP_MILESTONES[i];
            if (oldXP < milestone && currentXP >= milestone) {
                switch (i) {
                    case 0: LevelUp.send104m(player, skill); break;
                    case 1: LevelUp.send250m(player, skill); break;
                    case 2: LevelUp.send500m(player, skill); break;
                    case 3: LevelUp.send1000m(player, skill); break;
                    case 4: LevelUp.send2000m(player, skill); break;
                }
            }
        }
    }

    private void handleLevelUp(int skill, int newLevel, int oldLevel) {
        int levelDiff = newLevel - oldLevel;
        level[skill] += levelDiff;
        
        player.getDialogueManager().startDialogue("LevelUp", skill);
        
        if (skill == SUMMONING || isCombatSkill(skill)) {
            player.getGlobalPlayerUpdater().generateAppearenceData();
            
            if (skill == HITPOINTS) {
                player.heal(levelDiff * 10);
            } else if (skill == PRAYER) {
                player.getPrayer().restorePrayer(levelDiff * 10);
            }
        }
        
        player.getQuestManager().checkCompleted();
    }

    private void handleXpDisplay(int skill, double exp) {
        if (skill == HITPOINTS) return;
        
        boolean insideDung = player.getDungManager().isInside();
        if (insideDung) return;
        
        if (player.getAuraManager().usingWisdom()) {
            player.getPackets().sendConfig(2044, (int) (exp * 10.0) / 4);
        } else if (World.isWeekend() || World.isWellActive()) {
            player.getPackets().sendConfig(2044, (int) (exp * 10.0) / 2);
        }
    }

    public double addPetExperience(double exp) {
        Pet pet = player.getPet();
        if (pet == null) return 0.0;
        
        double petBonus = 1.0 + (pet.getDetails().getLevel() * 0.025);
        return exp * petBonus;
    }

    public int drainLevel(int skill, int drain) {
        if (!isValidSkill(skill)) return drain;
        
        int currentLevel = level[skill];
        int drainLeft = Math.max(0, drain - currentLevel);
        
        level[skill] = (short) Math.max(0, currentLevel - drain);
        refresh(skill);
        
        return drainLeft;
    }

    public void drainSummoning(int amt) {
        int currentLevel = getLevel(SUMMONING);
        if (currentLevel > 0) {
            set(SUMMONING, Math.max(0, currentLevel - amt));
        }
    }

    public int getCombatLevel() {
        int attack = getLevelForXp(ATTACK);
        int defence = getLevelForXp(DEFENCE);
        int strength = getLevelForXp(STRENGTH);
        int hp = getLevelForXp(HITPOINTS);
        int prayer = getLevelForXp(PRAYER);
        int ranged = getLevelForXp(RANGE);
        int magic = getLevelForXp(MAGIC);

        double base = (defence + hp + Math.floor(prayer / 2.0)) * 0.25 + 1.0;
        double melee = (attack + strength) * 0.325;
        double ranger = Math.floor(ranged * 1.5) * 0.325;
        double mage = Math.floor(magic * 1.5) * 0.325;

        return (int) (base + Math.max(melee, Math.max(ranger, mage)));
    }

    public int getCombatLevelWithSummoning() {
        return getCombatLevel() + getSummoningCombatLevel();
    }

    public int getSummoningCombatLevel() {
        return getLevelForXp(SUMMONING) / 8;
    }

    public int getCounterSkill(int skill) {
        // Simplified mapping - could use array or Map for better performance
        switch (skill) {
            case ATTACK: return 0;
            case STRENGTH: return 1;
            case DEFENCE: return 4;
            case RANGE: return 2;
            case HITPOINTS: return 5;
            case PRAYER: return 6;
            case AGILITY: return 7;
            case HERBLORE: return 8;
            case THIEVING: return 9;
            case CRAFTING: return 10;
            case MINING: return 12;
            case SMITHING: return 13;
            case FISHING: return 14;
            case COOKING: return 15;
            case FIREMAKING: return 16;
            case WOODCUTTING: return 17;
            case SLAYER: return 19;
            case FARMING: return 20;
            case CONSTRUCTION: return 21;
            case HUNTER: return 22;
            case SUMMONING: return 23;
            case DUNGEONEERING: return 24;
            case MAGIC: return 3;
            case FLETCHING: return 18;
            case RUNECRAFTING: return 11;
            default: return -1;
        }
    }

    public int getLevel(int skill) {
        return isValidSkill(skill) ? level[skill] : 0;
    }

    public int getLevelForXp(int skill) {
        if (!isValidSkill(skill)) return 1;
        
        double exp = xp[skill];
        int maxLevel = (skill == DUNGEONEERING || skill == SLAYER) ? 120 : 99;
        
        for (int lvl = 1; lvl <= maxLevel; lvl++) {
            if (getXPForLevel(lvl) > exp) {
                return lvl - 1;
            }
        }
        
        return maxLevel;
    }

    public String getSkillName(int skill) {
        return isValidSkill(skill) ? SKILL_NAME[skill] : "Null";
    }

    public int getTotalLevel() {
        int total = 0;
        for (int i = 0; i < SKILL_COUNT; i++) {
            total += getLevelForXp(i);
        }
        return total;
    }

    public double[] getXp() {
        return Arrays.copyOf(xp, xp.length); // Defensive copy
    }

    public double getXp(int skill) {
        return isValidSkill(skill) ? xp[skill] : 0.0;
    }

    public long getTotalXp() {
        long total = 0;
        for (double skillXp : xp) {
            total += (long) skillXp;
        }
        return total;
    }

    public String getTotalXpFormatted() {
        try {
            return decimalFormatCache.get().format(getTotalXp());
        } catch (Exception e) {
            // Fallback to simple string conversion if formatter fails
            return String.valueOf(getTotalXp());
        }
    }

    public int getHighestSkillLevel() {
        int maxLevel = 1;
        for (int skill = 0; skill < level.length; skill++) {
            int skillLevel = getLevelForXp(skill);
            if (skillLevel > maxLevel) {
                maxLevel = skillLevel;
            }
        }
        return maxLevel;
    }

    // Legacy compatibility methods - keeping original method signatures
    public boolean hasRequiriments(int... skills) {
        return hasRequirements(skills);
    }

    public boolean hasRequirements(int... requirements) {
        if (requirements.length % 2 != 0) {
            throw new IllegalArgumentException("Requirements must be in pairs of skill,level");
        }
        
        for (int i = 0; i < requirements.length; i += 2) {
            int skillId = requirements[i];
            int requiredLevel = requirements[i + 1];
            
            if (!isValidSkill(skillId) || getLevelForXp(skillId) < requiredLevel) {
                return false;
            }
        }
        return true;
    }

    // Initialization and setup methods
    public void init() {
        ensureArraysInitialized();
        
        for (int skill = 0; skill < level.length; skill++) {
            refresh(skill);
        }
        
        sendXPDisplay();
        refreshEnabledSkillsTargets();
        refreshUsingLevelTargets();
        refreshSkillsTargetsValues();
    }

    private void ensureArraysInitialized() {
        if (enabledSkillsTargets == null) {
            enabledSkillsTargets = new boolean[TARGET_COUNT];
        }
        if (skillsTargetsUsingLevelMode == null) {
            skillsTargetsUsingLevelMode = new boolean[TARGET_COUNT];
        }
        if (skillsTargetsValues == null) {
            skillsTargetsValues = new int[TARGET_COUNT];
        }
        if (spinMap == null) {
            spinMap = new HashMap<>();
        }
    }

    public void setPlayer(Player player) {
        this.player = player;
        migrateSkillArraysIfNeeded();
        ensureArraysInitialized();
    }

    private void migrateSkillArraysIfNeeded() {
        // Migrate old skill arrays to new size if needed
        if (xp.length < SKILL_COUNT) {
            xp = Arrays.copyOf(xp, SKILL_COUNT);
            level = Arrays.copyOf(level, SKILL_COUNT);
            
            // Set default levels for new skills
            if (xp.length >= 26 && level[DIVINATION] == 0) {
                level[DIVINATION] = 1;
            }
            if (xp.length >= 27 && level[INVENTION] == 0) {
                level[INVENTION] = 1;
            }
        }

        // Initialize XP counter arrays if they don't exist
        if (xpTracks == null) {
            xpPopup = true;
            xpTracks = new double[COUNTER_COUNT];
            trackSkills = new boolean[COUNTER_COUNT];
            trackSkillsIds = new byte[COUNTER_COUNT];
            trackSkills[0] = true;
            Arrays.fill(trackSkillsIds, (byte) 30);
        }
    }

    // Skill management methods
    public void refresh(int skill) {
        if (!isValidSkill(skill) || player == null) return;
        
        // Special handling for new skills
        if (skill == DIVINATION || skill == INVENTION) {
            TaskTab.sendTab(player);
            player.getPackets().sendIComponentText(320, 155, 
                "<col=ff8c00>" + getLevelForXp(DIVINATION));
            player.getPackets().sendIComponentText(320, 156, 
                "<col=ff8c00>" + getLevelForXp(DIVINATION));
        } else {
            player.getPackets().sendSkillLevel(skill);
        }
        
        player.getGlobalPlayerUpdater().generateAppearenceData();
    }

    public void set(int skill, int newLevel) {
        if (isValidSkill(skill)) {
            level[skill] = (short) Math.max(0, newLevel);
            refresh(skill);
        }
    }

    public void setXp(int skill, double exp) {
        if (isValidSkill(skill)) {
            xp[skill] = Math.max(0, Math.min(exp, MAXIMUM_EXP));
            refresh(skill);
        }
    }

    public void updateLevel(int skill, int levels) {
        if (isValidSkill(skill)) {
            level[skill] = (short) Math.max(0, level[skill] + levels);
            refresh(skill);
        }
    }

    // Reset methods
    public void resetAllSkills() {
        Arrays.fill(xp, 0.0);
        Arrays.fill(level, (short) 1);
        
        // Set defaults
        level[HITPOINTS] = 10;
        xp[HITPOINTS] = 1155.0;
        
        for (int skill = 0; skill < level.length; skill++) {
            refresh(skill);
        }
    }

    public void resetSkillNoRefresh(int skill) {
        if (isValidSkill(skill)) {
            xp[skill] = 0.0;
            level[skill] = 1;
        }
    }

    public void restoreSkills() {
        for (int skill = 0; skill < level.length; skill++) {
            level[skill] = (short) getLevelForXp(skill);
            refresh(skill);
        }
    }

    public void restoreNewSkills() {
        if (level.length > DIVINATION) {
            level[DIVINATION] = (short) getLevelForXp(DIVINATION);
            refresh(DIVINATION);
        }
        if (level.length > INVENTION) {
            level[INVENTION] = (short) getLevelForXp(INVENTION);
            refresh(INVENTION);
        }
    }

    public void restoreSummoning() {
        level[SUMMONING] = (short) getLevelForXp(SUMMONING);
        refresh(SUMMONING);
    }

    // XP Counter methods
    public void handleSetupXPCounter(int componentId) {
        switch (componentId) {
            case 18:
                player.getInterfaceManager().closeXPDisplay();
                break;
            case 22:
            case 23:
            case 24:
                setCurrentCounter(componentId - 22);
                break;
            case 27:
                switchTrackCounter();
                break;
            case 61:
                resetCounterXP();
                break;
            default:
                if (componentId >= 31 && componentId <= 57) {
                    handleCounterSkillSelection(componentId);
                }
                break;
        }
    }

    private void handleCounterSkillSelection(int componentId) {
        int skill;
        switch (componentId) {
            case 33: skill = 4; break;
            case 34: skill = 2; break;
            case 35: skill = 3; break;
            case 42: skill = 18; break;
            case 49: skill = 11; break;
            default: 
                skill = componentId >= 56 ? componentId - 27 : componentId - 31;
                break;
        }
        setCounterSkill(skill);
    }

    public void setCounterSkill(int skill) {
        if (currentCounter >= 0 && currentCounter < xpTracks.length) {
            xpTracks[currentCounter] = 0.0;
            trackSkillsIds[currentCounter] = (byte) skill;
            player.getPackets().sendConfigByFile(10440 + currentCounter, 
                trackSkillsIds[currentCounter] + 1);
            refreshCounterXp(currentCounter);
        }
    }

    public void setCurrentCounter(int counter) {
        if (counter >= 0 && counter < COUNTER_COUNT && counter != currentCounter) {
            currentCounter = counter;
            refreshCurrentCounter();
        }
    }

    public void refreshCounterXp(int counter) {
        if (counter >= 0 && counter < xpTracks.length && player != null) {
            int config = counter == 0 ? 1801 : 2474 + counter;
            player.getPackets().sendConfig(config, (int) (xpTracks[counter] * 10.0));
        }
    }

    public void refreshCurrentCounter() {
        if (player != null) {
            player.getPackets().sendConfig(2478, currentCounter + 1);
        }
    }

    public void resetCounterXP() {
        if (currentCounter >= 0 && currentCounter < xpTracks.length) {
            xpTracks[currentCounter] = 0.0;
            refreshCounterXp(currentCounter);
        }
    }

    public void switchTrackCounter() {
        if (currentCounter >= 0 && currentCounter < trackSkills.length && player != null) {
            trackSkills[currentCounter] = !trackSkills[currentCounter];
            player.getPackets().sendConfigByFile(10444 + currentCounter, 
                trackSkills[currentCounter] ? 1 : 0);
        }
    }

    public void sendXPDisplay() {
        if (player == null) return;
        
        for (int i = 0; i < Math.min(trackSkills.length, COUNTER_COUNT); i++) {
            player.getPackets().sendConfigByFile(10444 + i, trackSkills[i] ? 1 : 0);
            player.getPackets().sendConfigByFile(10440 + i, trackSkillsIds[i] + 1);
            refreshCounterXp(i);
        }
    }

    // Interface methods
    public void sendInterfaces() {
        if (player == null) return;
        
        if (xpDisplay) {
            player.getInterfaceManager().sendXPDisplay();
        }
        if (xpPopup) {
            player.getInterfaceManager().sendXPPopup();
        }
    }

    public void setupXPCounter() {
        if (player != null) {
            player.getInterfaceManager().sendXPDisplay(1214);
        }
    }

    public void switchXPDisplay() {
        if (player == null) return;
        
        xpDisplay = !xpDisplay;
        if (xpDisplay) {
            player.getInterfaceManager().sendXPDisplay();
        } else {
            player.getInterfaceManager().closeXPDisplay();
        }
    }

    public void switchXPPopup() {
        if (player == null) return;
        
        xpPopup = !xpPopup;
        player.sendMessage("XP pop-ups are now " + (xpPopup ? "en" : "dis") + "abled.");
        
        if (xpPopup) {
            player.getInterfaceManager().sendXPPopup();
        } else {
            player.getInterfaceManager().closeXPPopup();
        }
    }

    // Skill targets methods
    public int getTargetIdByComponentId(int componentId) {
        // Component ID to target ID mapping
        switch (componentId) {
            case 150: return 0;  // Attack
            case 9: return 1;    // Strength
            case 40: return 2;   // Range
            case 71: return 3;   // Magic
            case 22: return 4;   // Defence
            case 145: return 5;  // Constitution
            case 58: return 6;   // Prayer
            case 15: return 7;   // Agility
            case 28: return 8;   // Herblore
            case 46: return 9;   // Thieving
            case 64: return 10;  // Crafting
            case 84: return 11;  // Runecrafting
            case 140: return 12; // Mining
            case 135: return 13; // Smithing
            case 34: return 14;  // Fishing
            case 52: return 15;  // Cooking
            case 130: return 16; // Firemaking
            case 125: return 17; // Woodcutting
            case 77: return 18;  // Fletching
            case 90: return 19;  // Slayer
            case 96: return 20;  // Farming
            case 102: return 21; // Construction
            case 108: return 22; // Hunter
            case 114: return 23; // Summoning
            case 120: return 24; // Dungeoneering
            default: return -1;
        }
    }

    public int getSkillIdByTargetId(int targetId) {
        // Direct mapping for most skills
        switch (targetId) {
            case 0: return ATTACK;
            case 1: return STRENGTH;
            case 2: return RANGE;
            case 3: return MAGIC;
            case 4: return DEFENCE;
            case 5: return HITPOINTS;
            case 6: return PRAYER;
            case 7: return AGILITY;
            case 8: return HERBLORE;
            case 9: return THIEVING;
            case 10: return CRAFTING;
            case 11: return RUNECRAFTING;
            case 12: return MINING;
            case 13: return SMITHING;
            case 14: return FISHING;
            case 15: return COOKING;
            case 16: return FIREMAKING;
            case 17: return WOODCUTTING;
            case 18: return FLETCHING;
            case 19: return SLAYER;
            case 20: return FARMING;
            case 21: return CONSTRUCTION;
            case 22: return HUNTER;
            case 23: return SUMMONING;
            case 24: return DUNGEONEERING;
            default: return -1;
        }
    }

    public void refreshEnabledSkillsTargets() {
        if (player != null && enabledSkillsTargets != null) {
            int value = Utils.get32BitValue(enabledSkillsTargets, true);
            player.getPackets().sendConfig(1966, value);
        }
    }

    public void refreshUsingLevelTargets() {
        if (player != null && skillsTargetsUsingLevelMode != null) {
            int value = Utils.get32BitValue(skillsTargetsUsingLevelMode, true);
            player.getPackets().sendConfig(1968, value);
        }
    }

    public void refreshSkillsTargetsValues() {
        if (player == null || skillsTargetsValues == null) return;
        
        for (int i = 0; i < Math.min(skillsTargetsValues.length, TARGET_COUNT); i++) {
            player.getPackets().sendConfig(1969 + i, skillsTargetsValues[i]);
        }
    }

    public void setSkillTargetEnabled(int id, boolean enabled) {
        if (id >= 0 && id < enabledSkillsTargets.length) {
            enabledSkillsTargets[id] = enabled;
            refreshEnabledSkillsTargets();
        }
    }

    public void setSkillTargetUsingLevelMode(int id, boolean using) {
        if (id >= 0 && id < skillsTargetsUsingLevelMode.length) {
            skillsTargetsUsingLevelMode[id] = using;
            refreshUsingLevelTargets();
        }
    }

    public void setSkillTargetValue(int skillId, int value) {
        if (skillId >= 0 && skillId < skillsTargetsValues.length) {
            skillsTargetsValues[skillId] = value;
            refreshSkillsTargetsValues();
        }
    }

    public void setSkillTarget(boolean usingLevel, int skillId, int target) {
        setSkillTargetEnabled(skillId, true);
        setSkillTargetUsingLevelMode(skillId, usingLevel);
        setSkillTargetValue(skillId, target);
    }

    // Utility methods
    private boolean isValidSkill(int skill) {
        return skill >= 0 && skill < SKILL_COUNT;
    }

    public static int getXPForLevel(int level) {
        if (level <= 1) return 0;
        
        int points = 0;
        for (int lvl = 1; lvl < level; lvl++) {
            points += Math.floor(lvl + 300.0 * Math.pow(2.0, lvl / 7.0));
        }
        return (int) Math.floor(points / 4.0);
    }

    // Skill shard handling - Expert Skillcapes
    private void handleSkillShards(int skill) {
        if (player == null || player.getDungManager().isInside()) return;
        
        if (Utils.random(100) > 50) return; // 50% chance to not give shard immediately
        
        int skillLevel = getLevelForXp(skill);
        if (skillLevel < 99) return;
        
        checkCombatantCape(skill);
        checkArtisanCape(skill);
        checkGathererCape(skill);
        checkSupportCape(skill);
    }

    private void checkCombatantCape(int skill) {
        if (player.hasItem(new Item(32053))) return; // Already has combatant's cape
        
        Item shard = null;
        switch (skill) {
            case ATTACK: shard = new Item(32069); break;
            case STRENGTH: shard = new Item(32070); break;
            case DEFENCE: shard = new Item(32071); break;
            case HITPOINTS: shard = new Item(32074); break;
            case RANGE: shard = new Item(32075); break;
            case PRAYER: shard = new Item(32073); break;
            case MAGIC: shard = new Item(32076); break;
            case SUMMONING: shard = new Item(32072); break;
        }
        
        if (shard != null) {
            addShard(shard);
        }
    }

    private void checkArtisanCape(int skill) {
        if (player.hasItem(new Item(32054))) return; // Already has artisan's cape
        
        Item shard = null;
        switch (skill) {
            case CRAFTING: shard = new Item(32082); break;
            case CONSTRUCTION: shard = new Item(32083); break;
            case FIREMAKING: shard = new Item(32079); break;
            case FLETCHING: shard = new Item(32080); break;
            case HERBLORE: shard = new Item(32081); break;
            case SMITHING: shard = new Item(32084); break;
            case COOKING: shard = new Item(32077); break;
            case RUNECRAFTING: shard = new Item(32078); break;
        }
        
        if (shard != null) {
            addShard(shard);
        }
    }

    private void checkGathererCape(int skill) {
        if (player.hasItem(new Item(32052))) return; // Already has gatherer's cape
        
        Item shard = null;
        switch (skill) {
            case DIVINATION: shard = new Item(32066); break;
            case FARMING: shard = new Item(32067); break;
            case FISHING: shard = new Item(32063); break;
            case MINING: shard = new Item(32065); break;
            case HUNTER: shard = new Item(32068); break;
            case WOODCUTTING: shard = new Item(32064); break;
        }
        
        if (shard != null) {
            addShard(shard);
        }
    }

    private void checkSupportCape(int skill) {
        if (player.hasItem(new Item(32055))) return; // Already has support cape
        
        Item shard = null;
        switch (skill) {
            case AGILITY: shard = new Item(32087); break;
            case DUNGEONEERING: shard = new Item(32085); break;
            case SLAYER: shard = new Item(32088); break;
            case THIEVING: shard = new Item(32086); break;
        }
        
        if (shard != null) {
            addShard(shard);
        }
    }

    private void addShard(Item item) {
        if (!player.hasItem(item)) {
            player.addItem(item);
            player.sendMessage(Colors.red + "You've found " + Utils.getAorAn(item.getName()) + 
                             " " + item.getName() + "!");
        }
    }

    // Getters for compatibility (deprecated methods removed)
    public void passLevels(Player p) {
        if (p != null && p.getSkills() != null) {
            this.level = Arrays.copyOf(p.getSkills().level, p.getSkills().level.length);
            this.xp = Arrays.copyOf(p.getSkills().xp, p.getSkills().xp.length);
        }
    }

    public Map<String, Boolean> getSpinMap() {
        return spinMap != null ? new HashMap<>(spinMap) : new HashMap<>();
    }

    public void setSpinMap(Map<String, Boolean> spinMap) {
        this.spinMap = spinMap != null ? new HashMap<>(spinMap) : new HashMap<>();
    }

    // Legacy method for compatibility
    public String getTotalXp(Player player) {
        if (player == null || player.getSkills() == null) {
            return "0";
        }
        
        try {
            return player.getSkills().getTotalXpFormatted();
        } catch (Exception e) {
            // Fallback calculation if the main method fails
            try {
                double totalxp = 0;
                double[] xpArray = player.getSkills().getXp();
                if (xpArray != null) {
                    for (double xp : xpArray) {
                        totalxp += xp;
                    }
                }
                return new DecimalFormat("#,###,##0").format(totalxp);
            } catch (Exception fallbackException) {
                return "0";
            }
        }
    }

    public int getTotalLevel(Player player) {
        return player != null ? player.getSkills().getTotalLevel() : 0;
    }
    
         
}