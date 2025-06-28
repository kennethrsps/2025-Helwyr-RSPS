package com.rs.game.player;

import java.io.Serializable;

import com.rs.game.Animation;
import com.rs.game.WorldTile;
import com.rs.game.player.content.FadingScreen;
import com.rs.game.player.content.Magic;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Colors;
import com.rs.utils.Utils;

/**
 * Handles everything related to Player owned ports.
 * 
 * @author Zeus.
 */
public class PlayerOwnedPort implements Serializable {

	/**
	 * The generated serial UID.
	 */
	private static final long serialVersionUID = 5060924202351034976L;

	/**
	 * The player instance.
	 */
	private transient Player player;

	/**
	 * The player instance saving to.
	 * 
	 * @param player
	 *            The player.
	 */
	protected void setPlayer(Player player) {
		this.player = player;
	}

	/**
	 * If the player is entering ports for the first time.
	 */
	public boolean firstTimer = true;

	/**
	 * Enters the ports area.
	 */
	public void enterPorts() {
		player.setNextAnimation(new Animation(23099));
		WorldTasksManager.schedule(new WorldTask() {

			@Override
			public void run() {
				FadingScreen.fade(player, 0, new Runnable() {

					@Override
					public void run() {
						player.unlock();
						player.getControlerManager().startControler("PlayerPortsController");
					}
				});
			}
		}, 0);
	}

	/**
	 * Enters the ports area.
	 */
	public void leavePorts() {
		player.setNextAnimation(new Animation(23099));
		WorldTasksManager.schedule(new WorldTask() {

			@Override
			public void run() {
				FadingScreen.fade(player, 0, new Runnable() {

					@Override
					public void run() {
						player.unlock();
						player.getControlerManager().forceStop();
						player.setNextAnimation(new Animation(-1));
						Magic.sendNormalTeleportSpell(player, 0, 0, player.getHomeTile());
					}
				});
			}
		}, 0);
	}

	/**
	 * Check if we meet the requirements to start PoP.
	 * 
	 * @return if we meet requirements.
	 */
	public boolean meetRequirements() {
		if (player.getSkills().getLevelForXp(Skills.AGILITY) < 90
				&& player.getSkills().getLevelForXp(Skills.CONSTRUCTION) < 90
				&& player.getSkills().getLevelForXp(Skills.COOKING) < 90
				&& player.getSkills().getLevelForXp(Skills.DIVINATION) < 90
				&& player.getSkills().getLevelForXp(Skills.DUNGEONEERING) < 90
				&& player.getSkills().getLevelForXp(Skills.FISHING) < 90
				&& player.getSkills().getLevelForXp(Skills.HERBLORE) < 90
				&& player.getSkills().getLevelForXp(Skills.HUNTER) < 90
				&& player.getSkills().getLevelForXp(Skills.PRAYER) < 90
				&& player.getSkills().getLevelForXp(Skills.RUNECRAFTING) < 90
				&& player.getSkills().getLevelForXp(Skills.SLAYER) < 90
				&& player.getSkills().getLevelForXp(Skills.THIEVING) < 90)
			return false;
		return true;
	}

	/**
	 * Integers holding the resource amounts.
	 */
	public int plate, chiGlobe, lacquer, chime, portScore;

	/**
	 * Booleans representing if the player has a ship.
	 */
	public boolean hasFirstShip, hasSecondShip, hasThirdShip, hasFourthShip, hasFifthShip;
	
	/**
	 * Longs representing ships voyage times.
	 */
	public long firstShipTime, secondShipTime, thirdShipTime, fourthShipTime, fifthShipTime;
	
	/**
	 * Integers representing ships voyage times that were set.
	 */
	public int firstShipVoyage, secondShipVoyage, thirdShipVoyage, fourthShipVoyage, fifthShipVoyage;
	
	/**
	 * Booleans representing if the player has claimed his rewards of voyage yet.
	 */
	public boolean firstShipReward, secondShipReward, thirdShipReward, fourthShipReward, fifthShipReward;
	
	/**
	 * Integers representing which timer option was chose for each ship.
	 */
	public int firstShipTimerOption, secondShipTimerOption, thirdShipTimerOption, 
					fourthShipTimerOption, fifthShipTimerOption;
	
	/**
	 * Handles ship Alpha's returning.
	 * @return if returned successfully.
	 */
	public boolean handleFirstShipReward() {
		firstShipReward = true;
		firstShipVoyage = 0;
		int random = Utils.random(100);
		int resources = 1;
		if (firstShipTimerOption == 2) {
			random = Utils.random(80);
			resources = 3;
		}
		else if (firstShipTimerOption == 3) {
			random = Utils.random(60);
			resources = 9;
		}
		else if (firstShipTimerOption == 4) {
			random = Utils.random(40);
			resources = 22;
		}
		else if (firstShipTimerOption == 5) {
			random = Utils.random(20);
			resources = 45;
		}
		if (random < (player.getPerkManager().portsMaster ? 8 : 10)) { //10% chance of returning unsuccessfully
			chime += resources / (player.getPerkManager().portsMaster ? 2 : 3);
			return false;
		}
		if (player.getPerkManager().portsMaster)
			resources *= 1.15;
		chime += resources; //always return with some chime
		portScore += Utils.random(resources) + Utils.random(resources); //increase players port score
		int resource = Utils.random(6);
		switch (resource) {
		case 0:
		case 1:
			plate += resources;
			break;
		case 2:
		case 3:
			chiGlobe += resources;
			break;
		case 4:
		case 5:
			lacquer += resources;
			break;
		default: //a chance of gaining a lot of chime instead of resources
			chime += (resources * 10);
			break;
		}
		return true;
	}
	
	/**
	 * Handles ship Beta's returning.
	 * @return if returned successfully.
	 */
	public boolean handleSecondShipReward() {
		secondShipReward = true;
		secondShipVoyage = 0;
		int random = Utils.random(100);
		int resources = 1;
		if (secondShipTimerOption == 2) {
			random = Utils.random(80);
			resources = 3;
		}
		else if (secondShipTimerOption == 3) {
			random = Utils.random(60);
			resources = 9;
		}
		else if (secondShipTimerOption == 4) {
			random = Utils.random(40);
			resources = 22;
		}
		else if (secondShipTimerOption == 5) {
			random = Utils.random(20);
			resources = 45;
		}
		if (random < 10) { //10% chance of returning unsuccessfully
			chime += resources / 3;
			return false;
		}
		chime += resources; //always return with some chime
		portScore += Utils.random(resources) + Utils.random(resources); //increase players port score
		int resource = Utils.random(3);
		switch (resource) {
		case 0:
			plate += resources;
			break;
		case 1:
			chiGlobe += resources;
			break;
		case 2:
			lacquer += resources;
			break;
		case 3: //a chance of gaining a lot of chime instead of resources
			chime += (resources * 10);
			break;
		}
		return true;
	}
	
	/**
	 * Handles ship Gamma's returning.
	 * @return if returned successfully.
	 */
	public boolean handleThirdShipReward() {
		thirdShipReward = true;
		thirdShipVoyage = 0;
		int random = Utils.random(100);
		int resources = 1;
		if (thirdShipTimerOption == 2) {
			random = Utils.random(80);
			resources = 3;
		}
		else if (thirdShipTimerOption == 3) {
			random = Utils.random(60);
			resources = 9;
		}
		else if (thirdShipTimerOption == 4) {
			random = Utils.random(40);
			resources = 22;
		}
		else if (thirdShipTimerOption == 5) {
			random = Utils.random(20);
			resources = 45;
		}
		if (random < 10) { //10% chance of returning unsuccessfully
			chime += resources / 3;
			return false;
		}
		chime += resources; //always return with some chime
		portScore += Utils.random(resources) + Utils.random(resources); //increase players port score
		int resource = Utils.random(3);
		switch (resource) {
		case 0:
			plate += resources;
			break;
		case 1:
			chiGlobe += resources;
			break;
		case 2:
			lacquer += resources;
			break;
		case 3: //a chance of gaining a lot of chime instead of resources
			chime += (resources * 10);
			break;
		}
		return true;
	}
	
	/**
	 * Handles ship Delta's returning.
	 * @return if returned successfully.
	 */
	public boolean handleFourthShipReward() {
		fourthShipReward = true;
		fourthShipVoyage = 0;
		int random = Utils.random(100);
		int resources = 1;
		if (fourthShipTimerOption == 2) {
			random = Utils.random(80);
			resources = 3;
		}
		else if (fourthShipTimerOption == 3) {
			random = Utils.random(60);
			resources = 9;
		}
		else if (fourthShipTimerOption == 4) {
			random = Utils.random(40);
			resources = 22;
		}
		else if (fourthShipTimerOption == 5) {
			random = Utils.random(20);
			resources = 45;
		}
		if (random < 10) { //10% chance of returning unsuccessfully
			chime += resources / 3;
			return false;
		}
		chime += resources; //always return with some chime
		portScore += Utils.random(resources) + Utils.random(resources); //increase players port score
		int resource = Utils.random(3);
		switch (resource) {
		case 0:
			plate += resources;
			break;
		case 1:
			chiGlobe += resources;
			break;
		case 2:
			lacquer += resources;
			break;
		case 3: //a chance of gaining a lot of chime instead of resources
			chime += (resources * 10);
			break;
		}
		return true;
	}
	
	/**
	 * Handles ship Delta's returning.
	 * @return if returned successfully.
	 */
	public boolean handleFifthShipReward() {
		fifthShipReward = true;
		fifthShipVoyage = 0;
		int random = Utils.random(100);
		int resources = 1;
		if (fifthShipTimerOption == 2) {
			random = Utils.random(80);
			resources = 3;
		}
		else if (fifthShipTimerOption == 3) {
			random = Utils.random(60);
			resources = 9;
		}
		else if (fifthShipTimerOption == 4) {
			random = Utils.random(40);
			resources = 22;
		}
		else if (fifthShipTimerOption == 5) {
			random = Utils.random(20);
			resources = 45;
		}
		if (random < 10) { //10% chance of returning unsuccessfully
			chime += resources / 3;
			return false;
		}
		chime += resources; //always return with some chime
		portScore += Utils.random(resources) + Utils.random(resources); //increase players port score
		int resource = Utils.random(3);
		switch (resource) {
		case 0:
			plate += resources;
			break;
		case 1:
			chiGlobe += resources;
			break;
		case 2:
			lacquer += resources;
			break;
		case 3: //a chance of gaining a lot of chime instead of resources
			chime += (resources * 10);
			break;
		}
		return true;
	}
	
	/**
	 * Checks if ship Alpha has returned from a voyage yet.
	 * @return if the ship has returned.
	 */
	public boolean hasFirstShipReturned() {
		long timeVariation = Utils.currentTimeMillis() - firstShipTime;
		if (timeVariation < firstShipVoyage)
    		return false;
		return true;
	}
	
	/**
	 * Checks how much minutes left until voyage for ship Alpha ends.
	 * @return minutes as String.
	 */
	public String getFirstVoyageTimeLeft() {
		long toWait = firstShipVoyage - (Utils.currentTimeMillis() - firstShipTime);
		return Integer.toString(Utils.millisecsToMinutes(toWait));
	}
	
	public int getFirstVoyageMinsLeft() {
		return Utils.millisecsToMinutes(firstShipVoyage - (Utils.currentTimeMillis() - firstShipTime));
	}
	
	/**
	 * Checks if ship Beta has returned from a voyage yet.
	 * @return if the ship has returned.
	 */
	public boolean hasSecondShipReturned() {
		long timeVariation = Utils.currentTimeMillis() - secondShipTime;
		if (timeVariation < secondShipVoyage)
    		return false;
		return true;
	}
	
	/**
	 * Checks how much minutes left until voyage for ship Beta ends.
	 * @return minutes as String.
	 */
	public String getSecondVoyageTimeLeft() {
		long toWait = secondShipVoyage - (Utils.currentTimeMillis() - secondShipTime);
		return Integer.toString(Utils.millisecsToMinutes(toWait));
	}
	
	public int getSecondVoyageMinsLeft() {
		return Utils.millisecsToMinutes(secondShipVoyage - (Utils.currentTimeMillis() - secondShipTime));
	}
	
	/**
	 * Checks if ship Gamma has returned from a voyage yet.
	 * @return if the ship has returned.
	 */
	public boolean hasThirdShipReturned() {
		long timeVariation = Utils.currentTimeMillis() - thirdShipTime;
		if (timeVariation < thirdShipVoyage)
    		return false;
		return true;
	}
	
	/**
	 * Checks how much minutes left until voyage for ship Gamma ends.
	 * @return minutes as String.
	 */
	public String getThirdVoyageTimeLeft() {
		long toWait = thirdShipVoyage - (Utils.currentTimeMillis() - thirdShipTime);
		return Integer.toString(Utils.millisecsToMinutes(toWait));
	}
	
	public int getThirdVoyageMinsLeft() {
		return Utils.millisecsToMinutes(thirdShipVoyage - (Utils.currentTimeMillis() - thirdShipTime));
	}
	
	/**
	 * Checks if ship Delta has returned from a voyage yet.
	 * @return if the ship has returned.
	 */
	public boolean hasFourthShipReturned() {
		long timeVariation = Utils.currentTimeMillis() - fourthShipTime;
		if (timeVariation < fourthShipVoyage)
    		return false;
		return true;
	}
	
	/**
	 * Checks how much minutes left until voyage for ship Delta ends.
	 * @return minutes as String.
	 */
	public String getFourthVoyageTimeLeft() {
		long toWait = fourthShipVoyage - (Utils.currentTimeMillis() - fourthShipTime);
		return Integer.toString(Utils.millisecsToMinutes(toWait));
	}
	
	public int getFourthVoyageMinsLeft() {
		return Utils.millisecsToMinutes(fourthShipVoyage - (Utils.currentTimeMillis() - fourthShipTime));
	}
	
	/**
	 * Checks if ship Epsilon has returned from a voyage yet.
	 * @return if the ship has returned.
	 */
	public boolean hasFifthShipReturned() {
		long timeVariation = Utils.currentTimeMillis() - fifthShipTime;
		if (timeVariation < fifthShipVoyage)
    		return false;
		return true;
	}
	
	/**
	 * Checks how much minutes left until voyage for ship Epsilon ends.
	 * @return minutes as String.
	 */
	public String getFifthVoyageTimeLeft() {
		long toWait = fifthShipVoyage - (Utils.currentTimeMillis() - fifthShipTime);
		return Integer.toString(Utils.millisecsToMinutes(toWait));
	}
	
	public int getFifthVoyageMinsLeft() {
		return Utils.millisecsToMinutes(fifthShipVoyage - (Utils.currentTimeMillis() - fifthShipTime));
	}
	
	/**
	 * Opens the noticeboard interface.
	 */
	public void openNoticeboard() {
		player.getInterfaceManager().sendInterface(1331);
		player.getPackets().sendIComponentText(1331, 2, "Plate: "+Utils.getFormattedNumber(plate)+"; "
				+ "Lacquer: "+Utils.getFormattedNumber(lacquer)
				+ "<br>Chi Globe: "+Utils.getFormattedNumber(chiGlobe)+ "; "
				+ "Chime: "+Utils.getFormattedNumber(chime)
				+ "<br>Ports Score: "+Utils.getFormattedNumber(portScore)+ "; "
				+ "<br>Ship 'Alpha' - "+(!player.getPorts().hasFirstShip ? Colors.red+"Locked." : (!player.getPorts().hasFirstShipReturned() ? Colors.red+"Minutes Left: "+player.getPorts().getFirstVoyageTimeLeft()+"</col>." : (!player.getPorts().firstShipReward ? Colors.green+"Ready to Claim</col>." : Colors.green+"Ready to Deploy.")))
				+ "<br>Ship 'Beta' - "+(!player.getPorts().hasSecondShip ? Colors.red+"Locked." : (!player.getPorts().hasSecondShipReturned() ? Colors.red+"Minutes Left: "+player.getPorts().getSecondVoyageTimeLeft()+"</col>." : (!player.getPorts().secondShipReward ? Colors.green+"Ready to Claim</col>." : Colors.green+"Ready to Deploy.")))
				+ "<br>Ship 'Gamma' - "+(!player.getPorts().hasThirdShip ? Colors.red+"Locked." : (!player.getPorts().hasThirdShipReturned() ? Colors.red+"Minutes Left: "+player.getPorts().getThirdVoyageTimeLeft()+"</col>." : (!player.getPorts().thirdShipReward ? Colors.green+"Ready to Claim</col>." : Colors.green+"Ready to Deploy.")))
				+ "<br>Ship 'Delta' - "+(!player.getPorts().hasFourthShip ? Colors.red+"Locked." : (!player.getPorts().hasFourthShipReturned() ? Colors.red+"Minutes Left: "+player.getPorts().getFourthVoyageTimeLeft()+"</col>." : (!player.getPorts().fourthShipReward ? Colors.green+"Ready to Claim</col>." : Colors.green+"Ready to Deploy.")))
				+ "<br>Ship 'Epsilon' - "+(!player.getPorts().hasFifthShip ? Colors.red+"Locked." : (!player.getPorts().hasFifthShipReturned() ? Colors.red+"Minutes Left: "+player.getPorts().getFifthVoyageTimeLeft()+"</col>." : (!player.getPorts().fifthShipReward ? Colors.green+"Ready to Claim</col>." : Colors.green+"Ready to Deploy."))));
	}
}