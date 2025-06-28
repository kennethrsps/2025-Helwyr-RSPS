package com.rs.game.npc.pet;

import com.rs.game.Graphics;
import com.rs.game.World;
import com.rs.game.WorldTile;
import com.rs.game.npc.NPC;
import com.rs.game.npc.familiar.Familiar;
import com.rs.game.player.Player;
import com.rs.game.player.content.pet.PetDetails;
import com.rs.game.player.content.pet.Pets;
import com.rs.utils.Utils;

/**
 * Represents a pet.
 * 
 * @author Emperor
 * 
 */
public final class Pet extends NPC {

    /**
     * The serial UID.
     */
    private static final long serialVersionUID = -2848843157767889742L;

    /**
     * The owner.
     */
    private final Player owner;

    /**
     * The "near" directions.
     */
    private final int[][] checkNearDirs;

    /**
     * The item id.
     */
    private final int itemId;

    /**
     * The pet details.
     */
    private final PetDetails details;

    /**
     * The growth rate of the pet.
     */
    private double growthRate;

    /**
     * The pets type.
     */
    private final Pets pet;
    
    public static transient int MAX_LEVEL = 99;

    /**
     * Constructs a new {@code Pet} {@code Object}.
     * 
     * @param id
     *            The NPC id.
     * @param itemId
     *            The item id.
     * @param owner
     *            The owner.
     * @param tile
     *            The world tile.
     */
    public Pet(int id, int itemId, Player owner, WorldTile tile, PetDetails details) {
		super(id, tile, -1, false);
		this.owner = owner;
		this.itemId = itemId;
		this.checkNearDirs = Utils.getCoordOffsetsNear(super.getSize());
		this.details = details;
		this.pet = Pets.forId(itemId);
		if (pet == Pets.TROLL_BABY && owner.getPetManager().getTrollBabyName() != null)
		    setName(owner.getPetManager().getTrollBabyName());
		this.setRun(true);
		sendMainConfigurations();
		sendFollowerDetails();
    }
    
    public double addExperience(double experience) {
		if(details.getLevel() >= MAX_LEVEL)
			return 0;
		int oldLevel = getLevelForXp();
		details.setExperience(details.getExperience() + experience);
		int newLevel = getLevelForXp();
		int levelDiff = newLevel - oldLevel;
		if(details.getLevel() > (MAX_LEVEL - 1)) 
			details.setLevel((short) MAX_LEVEL);
		if(newLevel > oldLevel) {
			details.setLevel((short) (details.getLevel() + levelDiff));
			if(newLevel == MAX_LEVEL)
				owner.getPackets().sendGameMessage("<col=E86100>Your " + getName() + " has reached it's max level!");
			else
				owner.getPackets().sendGameMessage("<col=E86100>Your " + getName() + " has reached level " + details.getLevel() + "!");
			owner.setNextGraphics(new Graphics(1765));
			setNextGraphics(new Graphics(199));
		}
		return experience;
	}

	public int getLevelForXp() {
		double exp = details.getExperience();
		int points = 0;
		int output = 0;
		for (int lvl = 1; lvl <= MAX_LEVEL; lvl++) {
			points += Math.floor(lvl + (MAX_LEVEL * (100.0 + MAX_LEVEL)) * Math.pow((MAX_LEVEL / 5.0), lvl / (MAX_LEVEL / 5.0)));
			output = (int) Math.floor(points / MAX_LEVEL * (MAX_LEVEL * 2));
			if ((output - 1) >= exp) 
				return lvl;
		}
		return MAX_LEVEL;
	}

    /**
     * Calls the pet.
     */
    public void call() {
		int size = getSize();
		WorldTile teleTile = null;
		for (int dir = 0; dir < checkNearDirs[0].length; dir++) {
		    final WorldTile tile = new WorldTile(new WorldTile(owner.getX()
			    + checkNearDirs[0][dir], owner.getY() + checkNearDirs[1][dir], owner.getPlane()));
		    if (World.canMoveNPC(tile.getPlane(), tile.getX(), tile.getY(), size)) {
		    	teleTile = tile;
		    	break;
		    }
		}
		if (teleTile == null)
		    return;
		setNextWorldTile(teleTile);
    }

    /**
     * Gets the details.
     * 
     * @return The details.
     */
    public PetDetails getDetails() {
    	return details;
    }

    /**
     * Gets the growthRate.
     * 
     * @return The growthRate.
     */
    public double getGrowthRate() {
    	return growthRate;
    }

    /**
     * Gets the item id of the pet.
     * 
     * @return The item id.
     */
    public int getItemId() {
    	return itemId;
    }

    /**
     * Grows into the next stage of this pet (if any).
     */
    public void growNextStage() {
		if (details.getStage() == 3)
		    return;
		if (pet == null)
		    return;
		int npcId = pet.getNpcId(details.getStage() + 1);
		if (npcId < 1)
		    return;
		details.setStage(details.getStage() + 1);
		int itemId = pet.getItemId(details.getStage());
		if (pet.getNpcId(details.getStage() + 1) > 0)
		    details.updateGrowth(-100.0);
		owner.getPetManager().setItemId(itemId);
		owner.getPetManager().setNpcId(npcId);
		finish();
		Pet newPet = new Pet(npcId, itemId, owner, owner, details);
		newPet.growthRate = growthRate;
		owner.setPet(newPet);
		owner.sendMessage("<col=ff0000>Your pet has grown larger.</col>");
    }

    /**
     * Locks the orb.
     */
    public void lockOrb() {
    	owner.getPackets().sendHideIComponent(747, 9, true);
    }

    /**
     * Picks up the pet.
     */
    public void pickup() {
		owner.getInventory().addItem(itemId, 1);
		owner.setPet(null);
		owner.getPetManager().setNpcId(-1);
		owner.getPetManager().setItemId(-1);
		switchOrb(false);
		owner.getPackets().closeInterface(
			owner.getInterfaceManager().hasRezizableScreen() ? 98 : 212);
		owner.getPackets().sendIComponentSettings(747, 17, 0, 0, 0);
		owner.getInterfaceManager().openGameTab(4);
		finish();
    }

    @Override
    public void processNPC() {
		unlockOrb();
		if (pet == Pets.TROLL_BABY || pet.getFood().length > 0) {
		    details.updateHunger(0.025);
		    owner.getPackets().sendConfigByFile(4286, (int) details.getHunger());
		}
		if (details.getHunger() >= 90.0 && details.getHunger() < 99.9)
		    owner.sendMessage("<col=ff0000>Your pet is starving, feed it before it runs off.</col>");
		else if (details.getHunger() == 100.0) {
		    owner.getPetManager().setNpcId(-1);
		    owner.getPetManager().setItemId(-1);
		    owner.setPet(null);
		    owner.getPetManager().removeDetails(itemId);
		    owner.sendMessage("Your pet has ran away to find some food!");
		    switchOrb(false);
		    owner.getPackets().closeInterface(owner.getInterfaceManager().hasRezizableScreen() ? 98 : 212);
		    owner.getPackets().sendIComponentSettings(747, 17, 0, 0, 0);
		    finish();
		    return;
		}
		if (growthRate > 0.000) {
		    details.updateGrowth(growthRate);
		    owner.getPackets().sendConfigByFile(4285, (int) details.getGrowth());
		    if (details.getGrowth() == 100.0)
		    	growNextStage();
		}
		if (!withinDistance(owner, 12)) {
		    call();
		    return;
		}
		sendFollow();
    }

    /**
     * Follows the owner.
     */
    private void sendFollow() {
		if (getLastFaceEntity() != owner.getClientIndex())
		    setNextFaceEntity(owner);
		if (isFrozen())
		    return;
		int size = getSize();
		int targetSize = owner.getSize();
		if (Utils.colides(getX(), getY(), size, owner.getX(), owner.getY(),
			targetSize) && !owner.hasWalkSteps()) {
		    resetWalkSteps();
		    if (!addWalkSteps(owner.getX() + targetSize, getY())) {
		    	resetWalkSteps();
		    	if (!addWalkSteps(owner.getX() - size, getY())) {
		    		resetWalkSteps();
		    		if (!addWalkSteps(getX(), owner.getY() + targetSize)) {
		    			resetWalkSteps();
		    			if (!addWalkSteps(getX(), owner.getY() - size))
		    				return;
		    		}
		    	}
		    }
		    return;
		}
		resetWalkSteps();
		if (!clipedProjectile(owner, true) || !Utils.isOnRange(getX(), getY(), size, owner.getX(), owner.getY(), targetSize, 0))
		    calcFollow(owner, 2, true, false);
    }

    /**
     * Sends the follower details.
     */
    public void sendFollowerDetails() {
		owner.getPackets().sendConfigByFile(4285, (int) details.getGrowth());
		owner.getPackets().sendConfigByFile(4286, (int) details.getHunger());
		boolean res = owner.getInterfaceManager().hasRezizableScreen();
		owner.getPackets().sendInterface(true, res ? 746 : 548, res ? 126 : 186, 662);
		unlock();
		owner.getPackets().sendGlobalConfig(168, 8);// tab id
		owner.getInterfaceManager().openGameTab(4);
    }

    /**
     * Sends the main configurations for the Pet interface (+ summoning orb).
     */
    public void sendMainConfigurations() {
		switchOrb(true);
		owner.getPackets().sendConfig(448, itemId);// configures
		owner.getPackets().sendConfig(1160, 243269632); // sets npc emote
		owner.getPackets().sendGlobalConfig(1436, 0);
		unlockOrb(); // temporary
    }

    /**
     * Sets the growthRate.
     * 
     * @param growthRate
     *            The growthRate to set.
     */
    public void setGrowthRate(double growthRate) {
    	this.growthRate = growthRate;
    }

    /**
     * Switch the Summoning orb state.
     * 
     * @param enable
     *            If the orb should be enabled.
     */
    public void switchOrb(boolean enable) {
		owner.getPackets().sendConfig(1174, enable ? getId() : 0);
		if (enable) {
		    unlock();
		    return;
		}
		lockOrb();
    }

    /**
     * Unlocks the interfaces.
     */
    public void unlock() {
    	owner.getPackets().sendHideIComponent(747, 9, false);
    }

    /**
     * Unlocks the orb.
     */
    public void unlockOrb() {
    	owner.getPackets().sendHideIComponent(747, 9, false);
    	Familiar.sendLeftClickOption(owner);
    }
    
    public Pets getPetType() {
    	return pet;
    }
}