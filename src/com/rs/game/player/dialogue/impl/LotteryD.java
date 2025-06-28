package com.rs.game.player.dialogue.impl;

import com.rs.game.player.content.Lottery;
import com.rs.game.player.content.Lottery.LotteryTicket;
import com.rs.game.player.dialogue.Dialogue;
import com.rs.utils.Utils;

public class LotteryD extends Dialogue {

	private int npcId;

	@Override
	public void start() {
		npcId = 2538;
		sendNPCDialogue(npcId, 9827, "Hello " + player.getDisplayName() + ", would you like to check the lottery?");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			stage = 0;
			sendOptionsDialogue(SEND_DEFAULT_OPTIONS_TITLE, "Buy lottery tickets", "Claim winner ticket reward",
					"Check lottery details", "Check the amount of tickets you have bought", "Nevermind.");
			break;
		case 0:
			switch (componentId) {
			case OPTION_1:
				stage = 1;
				sendNPCDialogue(npcId, 9827,
						"The one lottery ticket cost " + Utils.getFormattedNumber1(Lottery.TICKET_PRICE)
								+ ", How many tickets would you like to buy?");
				break;
			case OPTION_2:
				int ticketIndex = Lottery.getWinnerTicket(player);
				LotteryTicket ticket = Lottery.WINNER_TICKETS.size() == 0 || ticketIndex == -1 ? null
						: Lottery.WINNER_TICKETS.get(ticketIndex);
				if (ticketIndex == -1 || Lottery.WINNER_TICKETS.size() == 0) {
					stage = 2;
					sendNPCDialogue(npcId, 9827, "You don't have any winner tickets.");
					return;
				}
				if (player.getInventory().getAmountOf(995) + ticket.getWonAmount() <= 0
						|| player.getInventory().getFreeSlots() == 0) {
					stage = 4;
					sendNPCDialogue(npcId, 9827, "You don't have enough inventory space to claim your reward.");
					return;
				}
				stage = 4;
				player.getInventory().addItem(995, ticket.getWonAmount());
				Lottery.WINNER_TICKETS.remove(ticketIndex);
				sendNPCDialogue(npcId, 9827, "Congratulations! " + Utils.getFormattedNumber1(ticket.getWonAmount())
						+ " has been added to your inventory.");
				break;
			case OPTION_3:
				stage = 2;
				long milliseconds = Lottery.getLotteryCycle() - Utils.currentTimeMillis();
				int seconds = (int) (milliseconds / 1000) % 60;
				int minutes = (int) ((milliseconds / (1000 * 60)) % 60);
				int prize = Lottery.LOTTERY_TICKETS.size() * Lottery.TICKET_PRICE;
				sendNPCDialogue(npcId, 9827, "Lottery pot is :" + Utils.getFormattedNumber(prize) + " , time left: "
						+ minutes + " minutes and " + seconds + " seconds");
				break;
			case OPTION_4:
				stage = 2;
				sendNPCDialogue(npcId, 9827,
						"You have bought " + Lottery.getTicketsAmount(player) + " Lottery Tickets.");
				break;
			case OPTION_5:
				end();
				break;
			}
			break;
		case 1:
			end();
			player.getTemporaryAttributtes().put("LOTTERY_TICKET_BUY", Boolean.TRUE);
			player.getPackets().sendInputIntegerScript(true, "Enter the amount you would like to buy:");
			break;
		case 2:
			stage = 0;
			sendOptionsDialogue(SEND_DEFAULT_OPTIONS_TITLE, "Buy lottery tickets", "Claim winner ticket reward",
					"Check lottery details", "Check the amount of tickets you have bought", "Nervermind.");
			break;
		case 4:
			end();
			break;
		}
	}

	@Override
	public void finish() {
	}

}
