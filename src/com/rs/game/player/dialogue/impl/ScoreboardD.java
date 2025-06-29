package com.rs.game.player.dialogue.impl;

import com.rs.game.player.content.BossTimerManager;
import com.rs.game.player.content.WeeklyTopRanking;
import com.rs.game.player.dialogue.Dialogue;
import com.rs.game.topweeks.TopWeeklyDonates;
import com.rs.game.topweeks.TopWeeklyTime;
import com.rs.game.topweeks.TopWeeklyVoters;
import com.rs.utils.Colors;
import com.rs.utils.DTRank;
import com.rs.utils.DonationRank;
import com.rs.utils.PkRank;
import com.rs.utils.VoteHiscores;
import com.rs.utils.WealthRank;

/**
 * Handles the In-game Scoreboard, used for Hiscores.
 * 
 * @author Zeus
 */
public class ScoreboardD extends Dialogue {

	@Override
	public void start() {
		sendOptionsDialogue("Choose an Option", "Vote Hiscores", "Donator Hiscores", "Wealth top 10s",
				"Player killing Ranks table", Colors.red + "More Options");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			switch (componentId) {
			case OPTION_1:
				finish();
				VoteHiscores.showRanks(player);
				break;
			case OPTION_2:
				finish();
				DonationRank.showRanks(player);
				break;
			case OPTION_3:
				finish();
				// BossTimerManager.sendInterface(player, 0);
				WealthRank.showRanks(player);
				break;
			case OPTION_4:
				finish();
				PkRank.showRanks(player);
				break;
			case OPTION_5:
				sendOptionsDialogue("Choose an Option", "Weekly Top online Players", "Weekly Top Voters",
						"Weekly Top Donator", "Dominion Tower fighters", Colors.red + "Back");
				stage = 2;

			}
			break;
		case 2:
			switch (componentId) {
			case OPTION_1:
				finish();
//				 WeeklyTopRanking.showRanks(player, 0);
				TopWeeklyTime.openInterface(player);
				break;
			case OPTION_2:
				finish();
				// WeeklyTopRanking.showRanks(player, 1);
				TopWeeklyVoters.openInterface(player);
				break;
			case OPTION_3:
				finish();
				// WeeklyTopRanking.showRanks(player, 2); open game
				TopWeeklyDonates.openInterface(player);
				break;
			case OPTION_4:
				finish();
				DTRank.showRanks(player);
				break;
			case OPTION_5:
				sendOptionsDialogue("Choose an Option", "Vote Hiscores", "Donator Hiscores", "Wealth top 10s",
						"Player killing Ranks table", Colors.red + "More Options");
				stage = -1;
			}
			break;

		}

	}

	@Override
	public void finish() {
		player.getInterfaceManager().closeChatBoxInterface();
	}
}