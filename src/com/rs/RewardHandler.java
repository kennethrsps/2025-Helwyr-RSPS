/*
 * package com.rs;
 * 
 * import java.util.Calendar;
 * 
 * import com.rs.game.World; import com.rs.game.item.Item; import
 * com.rs.game.player.DayOfWeekManager; import com.rs.game.player.Player; import
 * com.rs.utils.Colors; import com.rs.utils.Utils; import
 * com.rs.utils.VoteHiscores; import com.rs.utils.mysql.impl.VoteManager; import
 * com.rspserver.motivote.MotivoteHandler; import com.rspserver.motivote.Vote;
 * 
 * 
 * public class RewardHandler extends MotivoteHandler<Vote> {
 * 
 * @Override public void onCompletion(Vote reward) { String Name =
 * reward.username().toLowerCase(); String name2 = Name.replaceAll(" ", "_");
 * Player p = World.getPlayer(name2); int day = -1; if
 * (DayOfWeekManager.WORLD_CALENDAR != null) day =
 * DayOfWeekManager.WORLD_CALENDAR.get(Calendar.DAY_OF_WEEK); boolean
 * doubleReward = day == Calendar.FRIDAY || day == Calendar.SATURDAY || day ==
 * Calendar.SUNDAY; if (p != null) { reward.complete(); VoteManager.VOTES++;
 * p.setVotes(p.getVotes() + 1); p.setVoteCountWeekly(p.getVoteCountWeekly() +
 * 1);
 * p.sendMessage("We've recorded a vote for your account, enjoy your reward; " +
 * "(total votes: " + Colors.red + Utils.getFormattedNumber(p.getVotes()) +
 * "</col>).", true); if (doubleReward)
 * p.sendMessage("You have recieved double reward because its weekend!");
 * p.addItem(new Item(11640, doubleReward ? 2 : 1));
 *//** SoF tickets **/
/*
 * if (Utils.random(100) < 33 && !p.isIronMan() && !p.isHCIronMan())
 * p.addItem(new Item(24154, doubleReward ? 2 : 1));
 * 
 * p.setVotePoints(p.getVotePoints() + (doubleReward ? 2 : 1));
 * World.setLastVoter(p.getDisplayName()); VoteHiscores.checkRank(p);
 * 
 * if (VoteManager.VOTES % 5 == 0 && VoteManager.VOTES > 0) {
 * World.sendWorldMessage( "<img=6><col=008FB2>Total of [" + Colors.red +
 * Utils.getFormattedNumber(VoteManager.VOTES) +
 * "<col=008FB2>] votes have been claimed! " +
 * "Vote now using the ;;vote command!", false);
 * 
 *//** Custom Vote Party every 100 claimed votes **/
/*
 * if (VoteManager.VOTES % 100 == 0) { VoteManager.PARTIES++;
 * World.edelarParty();
 *//** Partying for 4 seconds **//*
									 * int players = World.getPlayers().size();
									 * World.sendWorldMessage("<img=6><col=008FB2>[Vote Party] " + (players == 1 ?
									 * "You" : "You and " + (players - 1) + " others") + " " + "have received " +
									 * (VoteManager.PARTIES == 1 ? "an extra vote book" : VoteManager.PARTIES +
									 * " extra vote books") + "!", false); World.addItemsAll(new Item(11640,
									 * VoteManager.PARTIES)); } } } } }
									 */