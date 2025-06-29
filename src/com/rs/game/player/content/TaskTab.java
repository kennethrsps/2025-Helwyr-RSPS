package com.rs.game.player.content;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.rs.Settings;
import com.rs.cache.loaders.ItemDefinitions;
import com.rs.cache.loaders.NPCDefinitions;
import com.rs.game.World;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.utils.Colors;
import com.rs.utils.Utils;
import com.rs.utils.mysql.impl.VoteManager;

/**
 * @author Zeus
 * @date 05.27.2025
 */
public class TaskTab {

    // Thread-safe date formatter using ThreadLocal to avoid memory leaks
    private static final ThreadLocal<SimpleDateFormat> DATE_FORMATTER = 
        ThreadLocal.withInitial(() -> new SimpleDateFormat("hh:mm:ss a"));

    /**
     * Sends the actual Noticeboard tab.
     * 
     * @param player The player to send to.
     */
    public static void sendTab(final Player player) {
        if (player == null) {
            return; // Guard against null player
        }

        final int rights = player.getRights();
        final String pName = player.getUsername();
        
        // Initialize variables
        String title = Colors.cyan + "Player";
        String gameMode = null;
        String status = null;
        String npcName = null;
        String slayerTask = null;

        // Get contract NPC name safely
        if (player.getContract() != null) {
            try {
                npcName = NPCDefinitions.getNPCDefinitions(player.getContract().getNpcId()).getName().toLowerCase();
            } catch (Exception e) {
                npcName = "Unknown"; // Fallback for invalid NPC ID
            }
        }

        // Get slayer task safely
        if (player.getSlayerManager().getCurrentTask() != null) {
            slayerTask = player.getSlayerManager().getCurrentTask().getName();
        }

        // Determine game mode
        gameMode = determineGameMode(player);
        
        // Determine title based on player rights and special status
        title = determinePlayerTitle(player, rights, pName);
        
        // Determine player status
        status = determinePlayerStatus(player);

        // Get daily task type safely
        String taskType = "";
        if (player.getDailyTaskManager().getCurrentTask() != null) {
            taskType = player.getDailyTaskManager().getCurrentTask().toString();
        }

        // Build the display text
        String text = buildDisplayText(player, title, gameMode, status, npcName, slayerTask, taskType);

        // Send the tab content
        player.getPackets().sendIComponentText(930, 10, Colors.white + Settings.SERVER_NAME + " Noticeboard</col>");
        player.getPackets().sendIComponentText(930, 16, text);
    }

    /**
     * Determines the player's game mode based on their status.
     */
    private static String determineGameMode(Player player) {
        if (player.isExpert()) {
            return Colors.cyan + "Expert (x" + Settings.EXPERT_XP + " XP)";
        }
        if (player.isVeteran()) {
            return Colors.cyan + "Veteran (x" + Settings.VET_XP + " XP)";
        }
        if (player.isIntermediate()) {
            return Colors.cyan + "Intermed. (x" + Settings.INTERM_XP + " XP)";
        }
        if (player.isEasy()) {
            return Colors.cyan + "Easy (x" + Settings.EASY_XP + " XP)";
        }
        if (player.isHCIronMan()) {
            return Colors.cyan + "HC Ironman (x" + Settings.IRONMAN_XP + " XP)";
        }
        if (player.isIronMan()) {
            return Colors.cyan + "Ironman (x" + Settings.IRONMAN_XP + " XP)";
        }
        return null;
    }

    /**
     * Determines the player's title based on their rights and status.
     */
    private static String determinePlayerTitle(Player player, int rights, String pName) {
        // Check for owner first
        if ("Zeus".equalsIgnoreCase(pName)) {
            return "<col=D400FF>Owner</col>";
        }
        
        // Check by rights level
        if (rights == 2) {
            return "<col=1589FF>Administrator</col>";
        }
        if (rights == 1 || player.isMod()) {
            return "<col=B5B5B5>Moderator</col>";
        }
        if (rights == 13 || player.isSupport()) {
            return "<col=83A6F2>Support</col>";
        }
        
        // Check special roles
        if (player.isWiki()) {
            return "<col=B5B5B5>Wiki</col>";
        }
        if (player.isCommunityManager()) {
            return Colors.blue + "Community Manager";
        }
        if (player.isForumManager()) {
            return Colors.green + "Forum Manager";
        }
        
        return Colors.cyan + "Player";
    }

    /**
     * Determines the player's status based on their donor/special status.
     */
    private static String determinePlayerStatus(Player player) {
        if (player.isDev()) {
            return "<shad=0000FF><col=00BFFF>Dev</shad>";
        }
        if (player.isYoutube()) {
            return "<shad=ff0000><col=000000>Youtube</shad>";
        }
        if (player.isSponsor()) {
            return "<shad=FFD700><col=FF8C00>Sponsor</shad>";
        }
        if (player.isDiamond()) {
            return "<col=00FFFF>Diamond";
        }
        if (player.isPlatinum()) {
            return "<col=008000>Platinum";
        }
        if (player.isDicer()) {
            return "<col=e6e600>Dicer";
        }
        if (player.isGold()) {
            return "<col=e6e600>Gold";
        }
        if (player.isSilver()) {
            return "<col=ffffff>Silver";
        }
        if (player.isBronze()) {
            return "<col=8B4513>Bronze";
        }
        return null;
    }

    /**
     * Builds the complete display text for the tab.
     */
    private static String buildDisplayText(Player player, String title, String gameMode, 
                                         String status, String npcName, String slayerTask, String taskType) {
        StringBuilder text = new StringBuilder();
        
        // Server information section
        text.append(Colors.red).append("-- Server --<br>");
        text.append(Colors.white).append("- Server time: ").append(Colors.green).append(getCurrentTime()).append("<br>");
        text.append(Colors.white).append("- Players online: ").append(Colors.green).append(World.getPlayersOnline()).append("<br>");
        text.append(Colors.white).append("- Double XP: ").append(Colors.green);
        
        if (World.isWeekend()) {
            text.append("Active (weeknd)");
        } else if (World.isWellActive()) {
            text.append("Active (well)");
        } else {
            text.append(Colors.red).append("Not Active");
        }
        text.append("<br>");
        
        text.append(Colors.white).append("- Vote party total: ").append(Colors.green).append(VoteManager.VOTES).append("<br>");
        
        if (VoteManager.gotDXP()) {
            String xpBonus = (World.isWellActive() || World.isWeekend()) ? "+10%" : "+100%";
            text.append(Colors.white).append("- Party XP ").append(xpBonus).append(": ");
            text.append(Utils.formatTime(VoteManager.PARTY_DXP - Utils.currentTimeMillis()).replace("00:", ""));
            text.append("<br>");
        }
        
        if (World.getLastVoter() != null) {
            text.append("<br>").append(Colors.white).append(" -- Last voter: ");
            text.append(Colors.green).append(World.getLastVoter()).append("<br>");
        }

        // Player information section
        text.append("<br>").append(Colors.red).append("-- Player --<br>");
        text.append(Colors.white).append("- Name: ").append(Colors.green).append(Utils.formatString(player.getDisplayName())).append("<br>");
        text.append(Colors.white).append("- Rank: ").append(title);
        
        if (gameMode != null) {
            text.append("<br>").append(Colors.white).append("- Mode: ").append(gameMode);
        }
        
        if (status != null && (player.isBronze() || player.isYoutube())) {
            text.append("<br>").append(Colors.white).append("- Status: ").append(status);
        }
        
        text.append("<br>").append(Colors.white).append("- Time played: ");
        text.append(Colors.green).append(Utils.getTimePlayed(player.getTimePlayed())).append("<br>");
        text.append(Colors.white).append("- Store Credits: ").append(Colors.green).append(player.getReferralPoints());
        
        if (player.isDoubleXp()) {
            text.append("<br>").append(Colors.white).append("- Exp Boost: ");
            text.append(Colors.green).append(Utils.formatTime(player.getDoubleXpTimer()));
        }

        // Membership status section
        if (player.Subscribed()) {
            text.append("<br><br>").append(Colors.red).append(" -- MemberShip Status --<br>");
            
            if (player.looterspack) {
                text.append(Colors.white).append("- Looters Perk: ").append(Colors.green);
                text.append(Utils.getDaysRemaining(player.getLooterPackSubLong())).append("<br>");
            }
            if (player.skillerspack) {
                text.append(Colors.white).append("- Skillers Perk: ").append(Colors.green);
                text.append(Utils.getDaysRemaining(player.getSkillerPackSubLong())).append("<br>");
            }
            if (player.utilitypack) {
                text.append(Colors.white).append("- Utility Perk: ").append(Colors.green);
                text.append(Utils.getDaysRemaining(player.getUtilityPackSubLong())).append("<br>");
            }
            if (player.combatantpack) {
                text.append(Colors.white).append("- Combat Perk: ").append(Colors.green);
                text.append(Utils.getDaysRemaining(player.getCombatPackSubLong())).append("<br>");
            }
            if (player.completepack) {
                text.append(Colors.white).append("- Complete Perk: ").append(Colors.green);
                text.append(Utils.getDaysRemaining(player.getCompletePackSubLong())).append("<br>");
            }
        }

        // Divination section
        text.append("<br><br>").append(Colors.orange).append(" -- Divination --<br>");
        text.append(Colors.white).append(" - Level: ").append(Colors.green);
        text.append(player.getSkills().getLevelForXp(Skills.DIVINATION)).append("<br>");
        text.append(Colors.white).append(" - Exp: ").append(Colors.green);
        text.append(Utils.getFormattedNumber((int) player.getSkills().getXp(Skills.DIVINATION)));

        // Statistics section
        appendStatisticsSection(text, player);

        // XP Sharing
        if (player.getXpSharing() != null && player.getXpSharing().getReceiverName() != null 
            && !player.getXpSharing().getReceiverName().isEmpty()) {
            text.append(Colors.white).append("- XP Share: ").append(player.getXpSharing().getReceiverName()).append("<br>");
        }

        // PvP Information section
        appendPvPSection(text, player);

        // Slayer Information section
        appendSlayerSection(text, player, slayerTask, npcName);

        // Daily Task section
        appendDailyTaskSection(text, player, taskType);

        // Vorago section
        text.append("<br><br>").append(Colors.red).append("-- Vorago --<br>");
        text.append(Colors.white).append("- rotation: ").append(Colors.green);
        text.append(Settings.VORAGO_ROTATION_NAMES[Settings.VORAGO_ROTATION]).append("<br><br>");

        // Player Owned Ports section
        appendPortsSection(text, player);

        return text.toString();
    }

    /**
     * Appends the statistics section to the text.
     */
    private static void appendStatisticsSection(StringBuilder text, Player player) {
        text.append("<br><br>").append(Colors.red).append("-- Statistics --<br>");
        text.append(Colors.white).append("- Donated: ").append(Colors.green).append("$");
        text.append(Utils.getFormattedNumber(player.getMoneySpent())).append("<br>");
        text.append(Colors.white).append("- Helwyr Coins: ").append(Colors.green);
        text.append(Utils.getFormattedNumber(player.getHelwyrCoins())).append("<br>");
        text.append(Colors.white).append("- Quest Points: ").append(Colors.green).append(player.getQuestPoints()).append("<br>");
        text.append(Colors.white).append("- PvM Points: ").append(Colors.green);
        text.append(Utils.formatNumber(player.getPVMPoints())).append("<br>");
        text.append(Colors.white).append("- Prestige Points: ").append(Colors.green);
        text.append(Utils.formatNumber(player.prestigePoints)).append("<br>");
        text.append(Colors.white).append("- Vote points: ").append(Colors.green);
        text.append(Utils.getFormattedNumber(player.getVotePoints())).append("<br>");
        text.append(Colors.red).append("- Halloween points: ").append(Colors.green);
        text.append(Utils.getFormattedNumber(player.getHweenPoints())).append("<br>");
        text.append(Colors.white).append("- Quest points: ").append(Colors.green);
        text.append(Utils.getFormattedNumber(player.getQuestPoints())).append("<br>");
        text.append(Colors.white).append("- Loyalty points: ").append(Colors.green);
        text.append(Utils.getFormattedNumber(player.getLoyaltyPoints())).append("<br>");
        text.append(Colors.white).append("- Trivia points: ").append(Colors.green);
        text.append(Utils.getFormattedNumber(player.getTriviaPoints())).append("<br>");
        text.append(Colors.white).append("- PC points: ").append(Colors.green);
        text.append(Utils.getFormattedNumber(player.getPestPoints())).append("<br>");
        text.append(Colors.white).append("- SW zeals: ").append(Colors.green);
        text.append(Utils.getFormattedNumber(player.getZeals())).append("<br>");
        text.append(Colors.white).append("- Dung tokens: ").append(Colors.green);
        text.append(Utils.getFormattedNumber(player.getDungeoneeringTokens())).append("<br>");
        text.append(Colors.white).append("- DT Kills: ").append(Colors.green);
        text.append(Utils.getFormattedNumber(player.getDominionTower().getKilledBossesCount())).append("<br>");
        text.append(Colors.white).append("- Tusken points: ").append(Colors.green);
        text.append(Utils.getFormattedNumber(player.getTuskenPoints())).append("<br>");
        text.append(Colors.white).append("- Elite Dungeon point: ").append(Colors.green);
        text.append(Utils.getFormattedNumber(player.getElitePoints())).append("<br>");
        text.append(Colors.white).append("- Starfire points: ").append(Colors.green);
        text.append(Utils.getFormattedNumber(player.getStarfirePoints()));
    }

    /**
     * Appends the PvP section to the text.
     */
    private static void appendPvPSection(StringBuilder text, Player player) {
        text.append("<br><br>").append(Colors.red).append("-- PvP Information --<br>");
        text.append(Colors.white).append("- Pk Points: ").append(Colors.green);
        text.append(Utils.getFormattedNumber(player.getPkPoints())).append("<br>");
        text.append(Colors.white).append("- Killstreak Points: ").append(Colors.green);
        text.append(Utils.getFormattedNumber(player.getTotalKillStreakPoints())).append("<br>");
        text.append(Colors.white).append("- Kills: ").append(Colors.green);
        text.append(Utils.getFormattedNumber(player.getKillCount())).append("<br>");
        text.append(Colors.white).append("- Deaths: ").append(Colors.green);
        text.append(Utils.getFormattedNumber(player.getDeathCount())).append("<br>");
        text.append(Colors.white).append("- Killstreak: ").append(Colors.green);
        text.append(Utils.getFormattedNumber(player.getKillStreak()));
    }

    /**
     * Appends the Slayer section to the text.
     */
    private static void appendSlayerSection(StringBuilder text, Player player, String slayerTask, String npcName) {
        text.append("<br><br>").append(Colors.red).append("-- Slayer Information --");
        
        if (player.getSlayerManager().getCurrentTask() != null) {
            text.append("<br>").append(Colors.white).append("- Task: ").append(Colors.green).append(slayerTask);
            text.append("<br>").append(Colors.white).append("- Kills Left: ").append(Colors.green);
            text.append(Utils.getFormattedNumber(player.getSlayerManager().getCount()));
        }
        
        text.append("<br>").append(Colors.white).append("- Slayer Points: ").append(Colors.green);
        text.append(Utils.getFormattedNumber(player.getSlayerManager().getSlayerPoints()));
        text.append("<br>").append(Colors.white).append("- Special Slayer Points: ").append(Colors.green);
        text.append(Utils.getFormattedNumber(player.getSlayerManager().getSlayerPoints2()));
        
        if (player.getContract() != null && !player.getContract().hasCompleted()) {
            text.append("<br>").append(Colors.white).append("- Contract: <br>").append(Colors.green);
            text.append(npcName != null ? npcName : "Unknown").append("' <br>");
            text.append(Colors.white).append("- Kills Left: ").append(Colors.green);
            text.append(Utils.getFormattedNumber(player.getContract().getKillAmount())).append("<br>");
            text.append(Colors.white).append("- Reward Amount: ").append(Colors.green);
            text.append(Utils.getFormattedNumber(player.getContract().getRewardAmount()));
        }
        
        text.append("<br>").append(Colors.white).append("- Reaper Points: ").append(Colors.green);
        text.append(Utils.getFormattedNumber(player.getReaperPoints())).append("<br>");
        text.append(Colors.white).append("- Total kills: ").append(Colors.green);
        text.append(Utils.getFormattedNumber(player.getTotalKills())).append("<br>");
        text.append(Colors.white).append("- Completed Contracts: ").append(Colors.green);
        text.append(Utils.getFormattedNumber(player.getTotalContract()));
    }

    /**
     * Appends the Daily Task section to the text.
     */
    private static void appendDailyTaskSection(StringBuilder text, Player player, String taskType) {
        text.append("<br><br>").append(Colors.red).append("-- Daily Task --<br>");
        text.append(Colors.white).append("- Task Type: ").append(Colors.green).append(taskType).append("<br>");
        text.append(Colors.white).append("- Task: ").append(Colors.green);
        
        try {
            String itemName = ItemDefinitions.getItemDefinitions(player.getDailyTaskManager().getProductId()).getName();
            text.append(itemName);
        } catch (Exception e) {
            text.append("Unknown Item");
        }
        
        text.append("<br>").append(Colors.white).append("- Task Count: ").append(Colors.green);
        text.append(player.getDailyTaskManager().getAmountLeft());
    }

    /**
     * Appends the Player Owned Ports section to the text.
     */
    private static void appendPortsSection(StringBuilder text, Player player) {
        text.append(Colors.red).append("-- Player Owned Ports --<br>");
        
        // Ship Alpha
        text.append(Colors.white).append("- Ship 'Alpha' : ");
        if (!player.getPorts().hasFirstShip) {
            text.append(Colors.red).append("Locked.");
        } else if (!player.getPorts().hasFirstShipReturned()) {
            text.append(Colors.red).append("Minutes Left: ").append(player.getPorts().getFirstVoyageTimeLeft()).append("</col>.");
        } else if (!player.getPorts().firstShipReward) {
            text.append(Colors.green).append("Ready to Claim</col>.");
        } else {
            text.append(Colors.green).append("Ready to Deploy.");
        }
        text.append("<br>");
        
        // Ship Beta
        text.append(Colors.white).append("- Ship 'Beta' : ");
        if (!player.getPorts().hasSecondShip) {
            text.append(Colors.red).append("Locked.");
        } else if (!player.getPorts().hasSecondShipReturned()) {
            text.append(Colors.red).append("Minutes Left: ").append(player.getPorts().getSecondVoyageTimeLeft()).append("</col>.");
        } else if (!player.getPorts().secondShipReward) {
            text.append(Colors.green).append("Ready to Claim</col>.");
        } else {
            text.append(Colors.green).append("Ready to Deploy.");
        }
        text.append("<br>");
        
        // Ship Gamma
        text.append(Colors.white).append("- Ship 'Gamma' : ");
        if (!player.getPorts().hasThirdShip) {
            text.append(Colors.red).append("Locked.");
        } else if (!player.getPorts().hasThirdShipReturned()) {
            text.append(Colors.red).append("Minutes Left: ").append(player.getPorts().getThirdVoyageTimeLeft()).append("</col>.");
        } else if (!player.getPorts().thirdShipReward) {
            text.append(Colors.green).append("Ready to Claim</col>.");
        } else {
            text.append(Colors.green).append("Ready to Deploy.");
        }
        text.append("<br>");
        
        // Ship Delta
        text.append(Colors.white).append("- Ship 'Delta' : ");
        if (!player.getPorts().hasFourthShip) {
            text.append(Colors.red).append("Locked.");
        } else if (!player.getPorts().hasFourthShipReturned()) {
            text.append(Colors.red).append("Minutes Left: ").append(player.getPorts().getFourthVoyageTimeLeft()).append("</col>.");
        } else if (!player.getPorts().fourthShipReward) {
            text.append(Colors.green).append("Ready to Claim</col>.");
        } else {
            text.append(Colors.green).append("Ready to Deploy.");
        }
        text.append("<br>");
        
        // Ship Epsilon
        text.append(Colors.white).append("- Ship 'Epsilon' : ");
        if (!player.getPorts().hasFifthShip) {
            text.append(Colors.red).append("Locked.");
        } else if (!player.getPorts().hasFifthShipReturned()) {
            text.append(Colors.red).append("Minutes Left: ").append(player.getPorts().getFifthVoyageTimeLeft()).append("</col>.");
        } else if (!player.getPorts().fifthShipReward) {
            text.append(Colors.green).append("Ready to Claim</col>.");
        } else {
            text.append(Colors.green).append("Ready to Deploy.");
        }
    }

    /**
     * Gets the current systems time in a thread-safe manner.
     * 
     * @return The formatted time.
     */
    private static String getCurrentTime() {
        Calendar cal = Calendar.getInstance();
        return DATE_FORMATTER.get().format(cal.getTime());
    }
}