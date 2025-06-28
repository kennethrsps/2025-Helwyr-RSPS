package com.rs.game.player.actions.divination;

import com.rs.Settings;
import com.rs.game.Animation;
import com.rs.game.Region;
import com.rs.game.World;
import com.rs.game.WorldObject;
import com.rs.game.WorldTile;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Colors;
import com.rs.utils.Utils;

/**
 * Handles everything related to Divine Locations.
 * @author Zeus
 */
public class DivineObject {

	/**
	 * The Player placing the Divine location.
	 */
    protected static Player player;
    
    /**
     * Check if we can place another location.
     * @param owner The player placing.
     */
    public static void check(final Player owner) {
		WorldTasksManager.schedule(new WorldTask() {
		    @Override
		    public void run() {
				if (owner.divine > 1)
				    return;
				if (owner.divine == 0)
				    stop();
				return;
		    }  
		}, 0, 0);
    }
    
    /**
     * Checks if the Divine location can be placed.
     * @param plane The plane coord.
     * @param x The X coord.
     * @param y The Y coord.
     * @return if can be placed.
     */
    private static boolean canPlaceLocation(Player owner, int plane, int x, int y) {
    	if (!World.canMoveNPC(plane, x, y, 2)
    			|| World.getObjectWithSlot(owner, Region.OBJECT_SLOT_FLOOR) != null
				|| owner.getControlerManager().getControler() != null)
			return false;
    	if(World.getObject(new WorldTile(x, y, plane)) != null)
    		return false;
    	return true;
    }

    /**
     * Handles the actual Divine location object placement.
     * @param owner The player placing.
     * @param item The divine location item ID.
     * @param fobject the object spawn ID.
     * @param lobject the actual divine object.
     * @param lvl divination LVL required.
     */
    public static void placeDivine(final Player owner, final int item, final int fobject, final int lobject, int lvl, int skillId) {
    	if (owner.getSkills().getLevel(skillId) < lvl) {
    		owner.sendMessage("You need a "+owner.getSkills().getSkillName(skillId)+" level of "+lvl+" to place this Divine location.");
    		return;
    	}
    	WorldTile tile = new WorldTile(owner.getX() + 1, owner.getY(), owner.getPlane());
    	if (!canPlaceLocation(owner, tile.getPlane(), tile.getX(), tile.getY())) {
    		tile = new WorldTile(owner.getX() - 1, tile.getY(), tile.getPlane());
        	if (!canPlaceLocation(owner, tile.getPlane(), tile.getX(), tile.getY())) {
        		tile = new WorldTile(owner.getX(), tile.getY() + 1, tile.getPlane());
            	if (!canPlaceLocation(owner, tile.getPlane(), tile.getX(), tile.getY())) {
    	    		tile = new WorldTile(tile.getX(), owner.getY() - 1, tile.getPlane());
    	        	if (!canPlaceLocation(owner, tile.getPlane(), tile.getX(), tile.getY())) {
    					owner.sendMessage("You cannot place a Divine location here; try moving around.");
    					return;
    				}
    			}
    		}
    	}
    	final WorldTile finalTile = tile;
		WorldTasksManager.schedule(new WorldTask() {
		    int ticks;
		    @Override
		    public void run() {
				ticks++;
				if (ticks == 1) {
					owner.stopAll(true);
					owner.lock(5);
				    owner.getInventory().deleteItem(item, 1);
				    owner.setNextAnimation(new Animation(21217));
				    WorldObject divinefirststage = new WorldObject(fobject, 10, 0, finalTile.getX(), finalTile.getY(), finalTile.getPlane());
				    owner.faceObject(divinefirststage); 
				    World.spawnObjectTemporary(divinefirststage, 2700); 
				}
				if (ticks == 5) {
				    check(owner); 
				    WorldObject divinefinalstage = new WorldObject(lobject, 10, 0, finalTile.getX(), finalTile.getY(), finalTile.getPlane(), owner);
				    World.spawnTemporaryDivineObject(divinefinalstage, 40000, owner);	
				    owner.divine = lobject;
				    owner.divines = owner;
				    owner.unlock();
				    player = owner;
				    stop();
				}
				return;
		    }  
		}, 0, 0);
    }
    
    /**
     * Resets Divination Divine location creation limit.
     * @param player The player's limit to reset.
     */
    private static void resetDivineCreation(Player player) {
    	long timeVariation = Utils.currentTimeMillis() - player.lastCreationTime;
		if (timeVariation < (24 * 60 * 60 * 1000)) //24 hours
	    	return;
    	player.lastCreationTime = Utils.currentTimeMillis();
    	if (player.created)
    		player.sendMessage(Colors.darkRed+"Your Divination Divine location creation limit has been reset.");
    	player.createdToday = 0;
		player.created = false;
    }
    
    /**
     * Resets Divination Divine location gather limit.
     * @param player The player's limit to reset.
     */
    public static void resetGatherLimit(Player player) {
    	resetDivineCreation(player);
    	if ((Utils.currentTimeMillis() - player.lastGatherLimit) < (24 * 60 * 60 * 1000)) //24 hours
    		return;
    	player.lastGatherLimit = Utils.currentTimeMillis();
    	if (player.gathered > 0)
    		player.sendMessage(Colors.darkRed+"Your Divination Divine location gather limit has been reset.");
    	player.gathered = 0;
    }

    /**
     * Checks how much more produce can we gather.
     * @param player The Player.
     * @return amount left we can acquire.
     */
	public static int checkPercentage(Player player) {
		int charges = player.gathered;
		int limitModifier = player.getPerkManager().divineDoubler ? 2 : 1;
		int maxCharges = (player.getSkills().getLevelForXp(Skills.DIVINATION) + 16) * limitModifier;
		int percentage = charges * 100 / maxCharges;
		if (Settings.DEBUG)
			System.out.println("Gather acquired: "+percentage+"%; " + charges +" out of "+maxCharges+".");
		return percentage;
	}
    
    /**
     * XP modifier by wearing items.
     * @param player The player.
     * @return the XP modifier.
     */
    public static double divinationSuit(Player player) {
    	double xpBoost = 1.0;
    	if (DivinationHarvest.hasDivinationOutfit(player))
    		xpBoost *= 1.07;
    	if (DivinationHarvest.hasElderDivinationOutfit(player))
    		xpBoost *= 1.1;
    	if (player.getEquipment().getHatId() == 29865)
    		xpBoost *= 1.01;
    	if (player.getEquipment().getHatId() == 32279)
    		xpBoost *= 1.05;
    	if (player.getEquipment().getChestId() == 29866)
    		xpBoost *= 1.01;
    	if (player.getEquipment().getLegsId() == 29867)
    		xpBoost *= 1.01;
    	if (player.getEquipment().getBootsId() == 29868)
    		xpBoost *= 1.01;
    	if (player.getEquipment().getGlovesId() == 29869)
    		xpBoost *= 1.01;
    	if (player.getEquipment().getHatId() == 29865
    			&& player.getEquipment().getChestId() == 29866
    			&& player.getEquipment().getLegsId() == 29867
    			&& player.getEquipment().getBootsId() == 29868
    			&& player.getEquipment().getGlovesId() == 29869)
    		xpBoost *= 1.01;
    	if (player.getEquipment().getHatId() == 32279
    			&& player.getEquipment().getChestId() == 29866
    			&& player.getEquipment().getLegsId() == 29867
    			&& player.getEquipment().getBootsId() == 29868
    			&& player.getEquipment().getGlovesId() == 29869)
    		xpBoost *= 1.05;
    	return xpBoost;
    }
    
    /**
     * Checks if we can harvest more resources..
     * @param player The player harvesting.
     * @return if the player can harvest or not.
     */
    public static boolean canHarvest(Player player) {
		if (checkPercentage(player) >= 100) {
			player.sendMessage(Colors.darkRed+"You have already gathered your limit from Divine locations today.");
			player.getActionManager().forceStop();
			return false;
		}
    	return true;
    }
    
    /**
     * Handles the actual resource harvesting.
     * @param player The player harvesting.
     */
    public static void handleHarvest(Player player) {
    	player.gathered ++;
		if (checkPercentage(player) == 25)
			player.sendMessage(Colors.darkRed+"You have used 25% of your daily gathering limit for Divine locations.", true);
		if (checkPercentage(player) == 50)
			player.sendMessage(Colors.darkRed+"You have used 50% of your daily gathering limit for Divine locations.");
		if (checkPercentage(player) == 75)
			player.sendMessage(Colors.darkRed+"You have used 75% of your daily gathering limit for Divine locations.", true);
    }
}