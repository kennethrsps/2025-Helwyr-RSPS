package com.rs.game.player;

import java.io.Serializable;

import com.rs.Settings;
import com.rs.game.player.dialogue.Dialogue;
import com.rs.utils.Colors;
import com.rs.utils.Utils;

/**
 * Used to handle player-unlocked titles.
 * @author Zeus
 */
public class Titles implements Serializable {

	/**
	 * The generated serial ID.
	 */
	private static final long serialVersionUID = -8142899759527457838L;

	/**
	 * The player-class to save to.
	 */
    private transient Player player;
    
    /**
     * The player to set the class to.
     * @param player
     */
    protected void setPlayer(Player player) {
    	this.player = player;
    }
    
    /**
     * Sends the component text on interface.
     * @param componentId the componentId.
     * @param text the String to send.
     */
    private void sendString(int componentId, String text) {
    	player.getPackets().sendIComponentText(1082, componentId, " "+text);
    }
    
    /**
     * Checks if the player is a male or not.
     * @return true if Male.
     */
    private boolean isMale() {
    	if (player.getGlobalPlayerUpdater().isMale())
    		return true;
    	return false;
    }
    
    /**
     * The players username.
     * @return the Username to return.
     */
    private String username() {
    	return player.getDisplayName();
    }
    
    /**
     * Opens the Title shop.
     */
    public void openShop() {
    	
    	/**
    	 * Clean out the interface.
    	 */
    	for (int i = 0; i < Utils.getInterfaceDefinitionsComponentsSize(1082); i++)
    		sendString(i, "");
    	
    	/**
    	 * Sending Essentials.
    	 */
    	player.getPackets().sendHideIComponent(1082, 159, true);
	    sendString(159, Colors.red+Settings.SERVER_NAME+" Title manager");
	    sendString(11, Colors.red+"Red = Locked"+Colors.white+".   "+Colors.green+"Green = Unlocked"+Colors.white+".<br>"
	    		+ Colors.white+"To activate a Title of your choice - press on the requirement text.");
	    sendString(41, Colors.cyan+"   - Title previews:");
	    sendString(42, Colors.cyan+"   - Requirements:");
	    
	    /**
	     * Title Previews.
	     */
	    sendString(30, Colors.gray+(isMale() ? "Ironman" : "Ironwoman")+" "+Colors.white+username());
	    sendString(32, Colors.white+username()+" "+Colors.gray+(isMale() ? "the Ironman" : "the Ironwoman"));
	    sendString(34, Colors.darkRed+(isMale() ? "HC Ironman" : "HC Ironwoman")+" "+Colors.white+username());
	    sendString(36, Colors.white+username()+" "+Colors.darkRed+(isMale() ? "the HC Ironman" : "the HC Ironwoman"));
	    sendString(38, Colors.white+username()+" "+Colors.darkRed+"the Survivor");
	    
	    sendString(49, Colors.darkRed+"Easy "+Colors.white+username());
	    sendString(51, Colors.darkRed+"Intermediate "+Colors.white+username());
	    sendString(53, Colors.darkRed+"Veteran "+Colors.white+username());
	    sendString(55, Colors.white+username()+Colors.darkRed+" the Easy");
	    sendString(57, Colors.white+username()+Colors.darkRed+" the Intermediate");
	    sendString(59, Colors.white+username()+Colors.darkRed+" the Veteran");
	    sendString(101, Colors.darkRed+"Expert "+Colors.white+username());
	    sendString(104, Colors.white+username()+Colors.darkRed+" the Expert");
	    sendString(106, Colors.rcyan+Colors.shad+" Christmas Titles");
	    
	    /** Bossing Title **/
	    //sendString(108, (player.getKillStatistics(106) >= 500 ? Colors.green : Colors.red)+"The Warden");
	    sendString(108, Colors.white+username()+Colors.green+", The Warden");
	    
	    /** Goodwill well titles **/
	    sendString(62, Colors.white+username()+Colors.brown+" the Wishful");
	    sendString(64, Colors.white+username()+Colors.brown+" the Generous");
	    sendString(66, Colors.white+username()+Colors.brown+" the Millionaire");
	    sendString(68, Colors.white+username()+Colors.gold+" the Charitable");
	    sendString(70, Colors.white+username()+Colors.orange+" the Billionaire");

	    /** Prifddinas titles **/
	    sendString(72, Colors.white+username()+"<col=FF08A0> of the Trahaearn");
	    sendString(99, Colors.white+username()+"<col=964F03> of the Hefin");

	    /** Cloak of Seasons combination **/
	    sendString(74, Colors.white+username()+Colors.green+"<shad=000000> of Seasons");

	    /** Chronicle Fragment offering **/
	    sendString(76, Colors.white+username()+Colors.green+"<shad=000000> of Guthix");
	    
	    
	    
	    
	    
	    /** Member Rank titles **/
	    sendString(190, "<col=B56A02>Bronze member "+Colors.white+username());
	    sendString(79, "<col=A3A3A3>Silver member "+Colors.white+username());
	    sendString(81, "<col=D6D600>Gold member "+Colors.white+username());
	    sendString(83, "<col=41917B>Platinum member "+Colors.white+username());
	    sendString(85, "<col=13D6D6>Diamond member "+Colors.white+username());

	    /** Special titles **/
	    sendString(88, "<col=FC0000>No-lifer "+Colors.white+username());
	    sendString(90, "<col=977EBF>Enthusiast "+Colors.white+username());
	    sendString(92, "<col=D966AF>The Maxed "+Colors.white+username());
	    sendString(94, "<col=A200FF>The Completionist "+Colors.white+username());
	    sendString(97, "<col=6200FF>The Perfectionist "+Colors.white+username());
	    
	    /**
	     * Title Description/Requirement.
	     */
	    
	 
	    sendString(31, (player.isIronMan() ? Colors.green : Colors.red)+"Be an "+(isMale() ? "Ironman" : "Ironwoman")+" to use this title.");
	    sendString(33, (player.isIronMan() ? Colors.green : Colors.red)+"Be an "+(isMale() ? "Ironman" : "Ironwoman")+" to use this title.");
	    sendString(35, (player.isHCIronMan() ? Colors.green : Colors.red)+"Be a "+(isMale() ? "HC Ironman" : "HC Ironwoman")+" to use this title.");
	    sendString(37, (player.isHCIronMan() ? Colors.green : Colors.red)+"Be a "+(isMale() ? "HC Ironman" : "HC Ironwoman")+" to use this title.");
	    sendString(39, (player.getDominionTower().getTotalScore() >= 50000 ? Colors.green+"<i>" : Colors.red)+"Get 50'000 Dominion Factor to use this title.");
	    
	    sendString(50, (player.isEasy() ? Colors.green : Colors.red)+"Be on the Easy EXP mode to use this title.");
	    sendString(52, (player.isIntermediate() ? Colors.green : Colors.red)+"Be on the Interm. EXP mode to use this title.");
	    sendString(54, (player.isVeteran() ? Colors.green : Colors.red)+"Be on the Veteran EXP mode to use this title.");
	    sendString(56, (player.isEasy() ? Colors.green : Colors.red)+"Be on the Easy EXP mode to use this title.");
	    sendString(58, (player.isIntermediate() ? Colors.green : Colors.red)+"Be on the Interm. EXP mode to use this title.");
	    sendString(60, (player.isVeteran() ? Colors.green : Colors.red)+"Be on the Veteran EXP mode to use this title.");
	    sendString(102, (player.isExpert() ? Colors.green : Colors.red)+"Be on the Expert EXP mode to use this title.");
	    sendString(105, (player.isExpert() ? Colors.green : Colors.red)+"Be on the Expert EXP mode to use this title.");
	    sendString(107, (player.hasXmasTitleUnlocked() ? Colors.green : Colors.red)+"Titles from the 2013 Christmas Event!");
	    sendString(109, (player.getKillStatistics(106) >= 500 ? Colors.green : Colors.red)+"Kill Telos, The Warden 500 time");
	    
	    
	    /** Goodwill well titles **/
	    sendString(63, (player.donatedToWell > 0 ? Colors.green : Colors.red)+"Thrown at least 1 coin into the Well.");
	    sendString(65, (player.donatedToWell >= 10000000 ? Colors.green : Colors.red)+"Thrown at least 10m coins into the Well.");
	    sendString(67, (player.donatedToWell >= 100000000 ? Colors.green : Colors.red)+"Thrown at least 100m coins into the Well.");
	    sendString(69, (player.donatedToWell >= 1000000000 ? Colors.green : Colors.red)+"Thrown at least 1b coins into the Well.");
	    sendString(71, (player.donatedToWell >= 5000000000L ? Colors.green : Colors.red)+"Thrown at least 5b coins into the Well.");
	    
	    /** Prifddinas titles **/
	    sendString(73, (player.getSerenStonesMined() >= 100 ? Colors.green : Colors.red)+"Mined a total of 100 Seren stones.");
	    sendString(100, (player.getHefinLaps() >= 200 ? Colors.green : Colors.red)+"Ran a total of 200 Hefin Agility laps");

	    /** Cloak of Seasons combination **/
	    sendString(75, (player.hasCombinedCloaks() ? Colors.green : Colors.red)+"Combined 4 seasonal cloaks.");

	    /** Chronicle Fragment offering **/
	    sendString(77, (player.hasGuthixTitleUnlocked() ? Colors.green : Colors.red)+"Exchanged a total of 100 Chronicle Fragments.");

	    /** Member Rank titles **/
	    sendString(191, (player.isBronze() ? Colors.green : Colors.red)+"Be ranked as Bronze member (20$)");
	    sendString(80, (player.isSilver() ? Colors.green : Colors.red)+"Be ranked as Silver member (50$)");
	    sendString(82, (player.isGold() ? Colors.green : Colors.red)+"Be ranked as Gold member (100$)");
	    sendString(84, (player.isPlatinum() ? Colors.green : Colors.red)+"Be ranked as Platinum member (250$)");
	    sendString(86, (player.isDiamond() ? Colors.green : Colors.red)+"Be ranked as Diamond member (500$)");
	    
	    /** Special titles **/
	    sendString(89, (Utils.getMinutesPlayed(player) >= 15000 ? Colors.green : Colors.red)+"Played for at least 250 hours");
	    sendString(91, (player.getVotes() >= 250 ? Colors.green : Colors.red)+"Voted for at least 250 times");
	    sendString(93, (player.isMax() ? Colors.green : Colors.red)+"Have claimed the Max cape");
	    sendString(95, (player.isComp() ? Colors.green : Colors.red)+"Have claimed the Completionist cape");
	    sendString(98, (player.isCompT() ? Colors.green : Colors.red)+"Have claimed the Completionist cape (t)");
	    
	    /** Now we send the actual interface with all the data **/
    	player.getInterfaceManager().sendInterface(1082);
    }
    
    /**
     * Sets the players title.
     * @param titleId the Title to set.
     */
    private void setTitle(int titleId) {
    	player.getGlobalPlayerUpdater().setTitle(titleId);
    	player.getGlobalPlayerUpdater().generateAppearenceData();
    	player.sendMessage("Your title has been successfully changed.");
    }
    
    /**
     * Handles the Title shop buttons.
     * @param componentId the interfaces buttonId.
     */
    public void handleShop(int componentId) {
    	player.getInterfaceManager().closeChatBoxInterface();
    	switch (componentId) {
    	case 31: /** Ironman after **/
    		if (!player.isIronMan()) {
    			player.getDialogueManager().startDialogue("SimpleMessage", 
    					"This title is only available for "+(isMale() ? "Ironman" : "Ironwoman")+" accounts.");
    			return;
    		}
    		setTitle(1000);
    		break;
    	case 33: /** Ironman before **/
    		if (!player.isIronMan()) {
    			player.getDialogueManager().startDialogue("SimpleMessage", 
    					"This title is only available for "+(isMale() ? "Ironman" : "Ironwoman")+" accounts.");
    			return;
    		}
    		setTitle(1500);
    		break;
    	case 35: /** HC Ironman after **/
    		if (!player.isHCIronMan()) {
    			player.getDialogueManager().startDialogue("SimpleMessage", 
    					"This title is only available for "+(isMale() ? "Ironman" : "Ironwoman")+" accounts.");
    			return;
    		}
    		setTitle(1001);
    		break;
    	case 37: /** HC Ironman before **/
    		if (!player.isHCIronMan()) {
    			player.getDialogueManager().startDialogue("SimpleMessage", 
    					"This title is only available for "+(isMale() ? "Ironman" : "Ironwoman")+" accounts.");
    			return;
    		}
    		setTitle(1501);
    		break;
    	case 39: /** the Survivor **/
    		if (player.getDominionTower().getTotalScore() < 50000) {
    			player.getDialogueManager().startDialogue("SimpleMessage", 
    					"You have to have a Dominion Factor of at least 50'000 to use this title.");
    			return;
    		}
    		setTitle(1502);
    		break;
    	case 50: /** Easy before **/
    		if (!player.isEasy()) {
    			player.getDialogueManager().startDialogue("SimpleMessage", 
    					"You have to be on the Easy EXP game mode to use this title.");
    			return;
    		}
    		setTitle(1002);
    		break;
    	case 52: /** Intermediate before **/
    		if (!player.isIntermediate()) {
    			player.getDialogueManager().startDialogue("SimpleMessage", 
    					"You have to be on the Intermediate EXP game mode to use this title.");
    			return;
    		}
    		setTitle(1003);
    		break;
    	case 54: /** Veteran before **/
    		if (!player.isVeteran()) {
    			player.getDialogueManager().startDialogue("SimpleMessage", 
    					"You have to be on the Veteran EXP game mode to use this title.");
    			return;
    		}
    		setTitle(1004);
    		break;
    	case 56: /** Easy after **/
    		if (!player.isEasy()) {
    			player.getDialogueManager().startDialogue("SimpleMessage", 
    					"You have to be on the Easy EXP game mode to use this title.");
    			return;
    		}
    		setTitle(1503);
    		break;
    	case 58: /** Intermediate after **/
    		if (!player.isIntermediate()) {
    			player.getDialogueManager().startDialogue("SimpleMessage", 
    					"You have to be on the Easy EXP game mode to use this title.");
    			return;
    		}
    		setTitle(1504);
    		break;
    	case 60: /** Veteran after **/
    		if (!player.isVeteran()) {
    			player.getDialogueManager().startDialogue("SimpleMessage", 
    					"You have to be on the Easy EXP game mode to use this title.");
    			return;
    		}
    		setTitle(1505);
    		break;
    	case 63: /** Wishful after **/
    		if (player.donatedToWell < 1) {
    			player.getDialogueManager().startDialogue("SimpleMessage", 
    					"You have to donate at least 1 coin to the Well of Good Will.");
    			return;
    		}
    		setTitle(1506);
    		break;
    	case 65: /** Generous after **/
    		if (player.donatedToWell < 10000000) {
    			player.getDialogueManager().startDialogue("SimpleMessage", 
    					"You have to donate at least 10m coins to the Well of Good Will.");
    			return;
    		}
    		setTitle(1507);
    		break;
    	case 67: /** Millionaire after **/
    		if (player.donatedToWell < 100000000) {
    			player.getDialogueManager().startDialogue("SimpleMessage", 
    					"You have to donate at least 100m coins to the Well of Good Will.");
    			return;
    		}
    		setTitle(1508);
    		break;
    	case 69: /** Charitable after **/
    		if (player.donatedToWell < 1000000000) {
    			player.getDialogueManager().startDialogue("SimpleMessage", 
    					"You have to donate at least 1b coins to the Well of Good Will.");
    			return;
    		}
    		setTitle(1509);
    		break;
    	case 71: /** Billionaire after **/
    		if (player.donatedToWell < 5000000000L) {
    			player.getDialogueManager().startDialogue("SimpleMessage", 
    					"You have to donate at least 5b coins to the Well of Good Will.");
    			return;
    		}
    		setTitle(1510);
    		break;
    	case 73: /** Trahaern after **/
    		if (player.getSerenStonesMined() < 100) {
    			player.getDialogueManager().startDialogue("SimpleMessage", 
    					"You have to Mine a total of 100 Seren stones at Prifddinas.");
    			return;
    		}
    		setTitle(1511);
    		break;
    	case 75: /** Seasons after **/
    		if (!player.hasCombinedCloaks()) {
    			player.getDialogueManager().startDialogue("SimpleMessage", 
    					"You have to combine 4 season cloaks. These cloaks can be received "
    					+ "by opening the Crystal chest.");
    			return;
    		}
    		setTitle(1514);
    		break;
    	case 77: /** Guthix after **/
    		if (!player.hasGuthixTitleUnlocked()) {
    			player.getDialogueManager().startDialogue("SimpleMessage", 
    					"You have to offer at least a total of 100 Chronicle Fragments at May Stormbrewer "
    					+ "located in Draynor Village.");
    			return;
    		}
    		setTitle(1515);
    		break;
    	case 191: /** Bronze member before **/
    		if (!player.isBronze()) {
    			player.getDialogueManager().startDialogue("SimpleMessage", 
    					"You have to be ranked as a Bronze member in order to use this title. To rank up to "
    					+ "bronze you have to spend at least 20$ from our ;;store page.");
    			return;
    		}
    		setTitle(1016);
    		break;
    	case 80: /** Silver member before **/
    		if (!player.isSilver()) {
    			player.getDialogueManager().startDialogue("SimpleMessage", 
    					"You have to be ranked as a Silver member in order to use this title. To rank up to "
    					+ "silver you have to spend at least 50$ from our ;;store page.");
    			return;
    		}
    		setTitle(1017);
    		break;
    	case 82: /** Gold member before **/
    		if (!player.isGold()) {
    			player.getDialogueManager().startDialogue("SimpleMessage", 
    					"You have to be ranked as a Gold member in order to use this title. To rank up to "
    					+ "gold you have to spend at least 100$ from our ;;store page.");
    			return;
    		}
    		setTitle(1018);
    		break;
    	case 84: /** Platinum member before **/
    		if (!player.isPlatinum()) {
    			player.getDialogueManager().startDialogue("SimpleMessage", 
    					"You have to be ranked as a Platinum member in order to use this title. To rank up to "
    					+ "platinum you have to spend at least 250$ from our ;;store page.");
    			return;
    		}
    		setTitle(1019);
    		break;
    	case 86: /** Diamond member before **/
    		if (!player.isDiamond()) {
    			player.getDialogueManager().startDialogue("SimpleMessage", 
    					"You have to be ranked as a Diamond member in order to use this title. To rank up to "
    					+ "diamond you have to spend at least 500$ from our ;;store page.");
    			return;
    		}
    		setTitle(1020);
    		break;
    	case 89: /** No-lifer before **/
    		if (Utils.getMinutesPlayed(player) < 15000) {
    			player.getDialogueManager().startDialogue("SimpleMessage", 
    					"You have to have an online time of at least 250 hours.");
    			return;
    		}
    		setTitle(1021);
    		break;
    	case 91: /** Enthusiast before **/
    		if (player.getVotes() < 250) {
    			player.getDialogueManager().startDialogue("SimpleMessage", 
    					"You have to have voted for at least 250 times in total.");
    			return;
    		}
    		setTitle(1022);
    		break;
    	case 93: /** The Maxed before **/
    		if (!player.isMax()) {
    			player.getDialogueManager().startDialogue("SimpleMessage", 
    					"You have to claim the Max cape at least once from the Cape stand at home.");
    			return;
    		}
    		setTitle(1023);
    		break;
    	case 95: /** The Maxed before **/
    		if (!player.isComp()) {
    			player.getDialogueManager().startDialogue("SimpleMessage", 
    					"You have to claim the Completionist cape at least once from the Cape "
    					+ "stand at home.");
    			return;
    		}
    		setTitle(1024);
    		break;
    	case 98: /** The Perfectionist before **/
    		if (!player.isCompT()) {
    			player.getDialogueManager().startDialogue("SimpleMessage", 
    					"You have to claim the trimmed Completionist cape at least once from the "
    					+ "Cape stand at home.");
    			return;
    		}
    		setTitle(1025);
    		break;
    	case 100: /** the Hefin after **/
    		if (player.getHefinLaps() < 200) {
    			player.getDialogueManager().startDialogue("SimpleMessage", 
    					"You will have to complete a total of 200 Prifddinas Hefin district "
    					+ "Agility course laps.");
    			return;
    		}
    		setTitle(1516);
    		break;
    	case 102: /** Expert before **/
    		if (!player.isExpert()) {
    			player.getDialogueManager().startDialogue("SimpleMessage", 
    					"You have to be on the Expert EXP game mode to use this title.");
    			return;
    		}
    		setTitle(1033);
    		break;
    	case 105: /** the Expert after **/
    		if (!player.isExpert()) {
    			player.getDialogueManager().startDialogue("SimpleMessage", 
    					"You have to be on the Expert EXP game mode to use this title.");
    			return;
    		}
    		setTitle(1518);
    		break;
    	case 107: /** Christmas **/
            player.closeInterfaces();
            if (!player.hasXmasTitleUnlocked()) {
                player.getDialogueManager().startDialogue("SimpleMessage",
                        "You must have activated at least one title from the Christmas event.");
                return;
            }
              ((Player) player).getDialogueManager().startDialogue(new Dialogue() {
 
                   @Override
                   public void start() {
                       sendOptionsDialogue("Christmas titles", "of Christmas", "of Winter", "the Grinch", "Frostweb");
                               stage = 0;
                   
                   }
 
                   @Override
                   public void run(int interfaceId, int componentId) {
                    switch(stage) {
                    case 0:
                        switch(componentId) {
                        case OPTION_1:
                        	if(player.xmasTitle1)
                        		setTitle(2001);
                        	else
                        		player.sendMessage("You don't have that title!");
                        	finish();
                        	break;
                        case OPTION_2:
                        	if(player.xmasTitle2)
                        		setTitle(2002);
                        	else
                        		player.sendMessage("You don't have that title!");
                        	finish();
                        case OPTION_3:
                        	if(player.xmasTitle3)
                        		setTitle(2003);
                        	else
                        		player.sendMessage("You don't have that title!");
                        	finish();
                        case OPTION_4:
                        	if(player.xmasTitle4)
                        		setTitle(2004);
                        	else
                        		player.sendMessage("You don't have that title!");
                            finish();
                            break;
                           
                        case OPTION_5:
                            finish();
                            break;
                        }
                        break;
                    }
                   
                   }
 
                   @Override
                   public void finish() {
                    player.getInterfaceManager().closeChatBoxInterface();
                   
                   }
                   
                  });
            break;
    	}
    }
}