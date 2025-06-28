package com.rs.game.player.content.pet;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.Animation;
import com.rs.game.ForceTalk;
import com.rs.game.item.Item;
import com.rs.game.npc.pet.Pet;
import com.rs.game.player.Player;
import com.rs.game.player.content.ItemConstants;

/**
 * The pet manager.
 * 
 * @author Emperor
 * 
 */
public final class PetManager implements Serializable {

	/**
	 * The serial UID.
	 */
	private static final long serialVersionUID = -3379270918966667109L;

	/**
	 * The pet details mapping, sorted by item id.
	 */
	private final Map<Integer, PetDetails> petDetails = new HashMap<Integer, PetDetails>();

	/**
	 * The player.
	 */
	private Player player;

	/**
	 * The current NPC id.
	 */
	private int npcId;

	/**
	 * The current item id.
	 */
	private int itemId;

	/**
	 * The troll baby's name (if any).
	 */
	private String trollBabyName;

	/**
	 * Constructs a new {@code PetManager} {@code Object}.
	 */
	public PetManager() {
		/*
		 * empty.
		 */
	}

	/**
	 * Makes the pet eat.
	 * 
	 * @param foodId
	 *            The food item id.
	 * @param npc
	 *            The pet NPC.
	 */
	public void eat(int foodId, Pet npc) {
		if (npc != player.getPet()) {
			player.getPackets().sendGameMessage("This isn't your pet!");
			return;
		}
		if(player.getPetManager().isAlchemyPet()) {
			player.getDialogueManager().startDialogue("FireDrakeLegendaryPet", new Item(foodId));
			return;
		}
		Pets pets = Pets.forId(itemId);
		if (pets == null)
			return;
		if (pets == Pets.TROLL_BABY) {
			if (!ItemConstants.isTradeable(new Item(foodId))) {
				player.sendMessage("Your troll baby won't eat this item.");
				return;
			}
			trollBabyName = ItemDefinitions.getItemDefinitions(foodId).getName();
			npc.setName(trollBabyName);
			npc.setNextForceTalk(new ForceTalk("YUM! Me likes " + trollBabyName  + "!"));
			npc.needMasksUpdate();
			player.getInventory().deleteItem(foodId, 1);
			player.sendMessage("Your pet happily eats the " + ItemDefinitions.getItemDefinitions(foodId) .getName() + ".");
			return;
		}
		for (int food : pets.getFood()) {
			if (food == foodId) {
				player.getInventory().deleteItem(food, 1);
				player.sendMessage("Your pet happily eats the " + ItemDefinitions.getItemDefinitions(food).getName() + ".");
				player.setNextAnimation(new Animation(827));
				npc.getDetails().updateHunger(-15.0);
				return;
			}
		}
		player.getPackets().sendGameMessage("Nothing interesting happens.");
	}

	/**
	 * Gets the itemId.
	 * 
	 * @return The itemId.
	 */
	public int getItemId() {
		return itemId;
	}

	/**
	 * Gets the npcId.
	 * 
	 * @return The npcId.
	 */
	public int getNpcId() {
		return npcId;
	}

	/**
	 * Gets the player.
	 * 
	 * @return The player.
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * Gets the trollBabyName.
	 * 
	 * @return The trollBabyName.
	 */
	public String getTrollBabyName() {
		return trollBabyName;
	}

	@SuppressWarnings("incomplete-switch")
	public boolean isLegendaryPet() {
		if (getPlayer().getPet() == null)
			return false;
		switch (getPlayer().getPet().getPetType()) {
		case PROTOTYPE_COLOSSUS:
		case FIRE_DRAKE:
		case SHADOW_DRAKE:
		case BLOODPOUNCER:
		case SKYPOUNCER:
		case WARBORN_BEHEMOTH:
		case RORY_THE_REINDEER:
		case DRAGON_WOLF:
			return true;
		}
		return false;
	}
	
	/**
	 * This pets abilities reflects the players consitution (Giving the player hitpoints, and saving him from death).
	 * @return
	 */
	public boolean isConstitutionPet() {
		if (getPlayer().getPet() == null)
			return false;
		return getPlayer().getPet().getPetType() == Pets.BLOODPOUNCER;
	}
	
	/**
	 * This pets abilities reflects retrieing items from bank, npcs, etc
	 * @return
	 */
	public boolean isReceivePet() {
		if (getPlayer().getPet() == null)
			return false;
		return getPlayer().getPet().getPetType() == Pets.SKYPOUNCER;
	}
	
	/**
	 * This pet aids their master in combat attacks.
	 * @return
	 */
	public boolean isAttackingPet() {
		if (getPlayer().getPet() == null)
			return false;
		return getPlayer().getPet().getPetType() == Pets.DRAGON_WOLF;
	}
	
	/**
	 * This pet allows the user to alch items for a higher price (automatically) from npcs, note items, etc
	 * @return
	 */
	public boolean isAlchemyPet() {
		if (getPlayer().getPet() == null)
			return false;
		return getPlayer().getPet().getPetType() == Pets.FIRE_DRAKE;
	}
	
	/**
	 * This pet allows the user to teleport to set locations
	 * @return
	 */
	public boolean isSkillingPet() {
		if (getPlayer().getPet() == null)
			return false;
		return getPlayer().getPet().getPetType() == Pets.RORY_THE_REINDEER;
	}
	
	/**
	 * This pet aids the player in combat, (does not attack) (grants bonuses, etc)
	 * @return
	 */
	public boolean isCombatPet() {
		if (getPlayer().getPet() == null)
			return false;
		return getPlayer().getPet().getPetType() == Pets.WARBORN_BEHEMOTH;
	}

	/**
	 * This pet aids with player prayer (granting additional points, and increasing prayer bonuses)
	 * @return
	 */
	public boolean isPrayerPet() {
		if (getPlayer().getPet() == null)
			return false;
		return getPlayer().getPet().getPetType() == Pets.PROTOTYPE_COLOSSUS;
	}

	/**
	 * THis is an ultimate pet, which contains a % of each ability from every other pet (does not attack).
	 * @return
	 */
	public boolean isUltimatePet() {
		if (getPlayer().getPet() == null)
			return false;
		return getPlayer().getPet().getPetType() == Pets.SHADOW_DRAKE;
	}

	/**
	 * Checks if the player has the requirements for the pet.
	 * 
	 * @param pet
	 *            The pet.
	 * @return {@code True} if so.
	 */
	private boolean hasRequirements(Pets pet) {
		switch (pet) {
		case TZREK_JAD:
			return true;
		case SARADOMIN_OWL:
		case GUTHIX_RAPTOR:
		case ZAMORAK_HAWK:
		case VULTURE_1:
		case VULTURE_2:
		case VULTURE_3:
		case VULTURE_4:
		case VULTURE_5:
		case CHAMELEON:
			return true;
		case BABY_DRAGON_1:
		case BABY_DRAGON_2:
		case BABY_DRAGON_3:
		case SEARING_FLAME:
		case GLOWING_EMBER:
		case TWISTED_FIRESTARTER:
		case WARMING_FLAME:
			return true;
		case ABYSSAL_MINION:
			break;
		case BABY_BASILISK:
			break;
		case BABY_DRAGON:
			break;
		case BABY_KURASK:
			break;
		case BROAV:
			break;
		case BULLDOG:
			break;
		case BULLDOG_1:
			break;
		case BULLDOG_2:
			break;
		case CAT:
			break;
		case CAT_1:
			break;
		case CAT_2:
			break;
		case CAT_3:
			break;
		case CAT_4:
			break;
		case CAT_5:
			break;
		case CAT_7:
			break;
		case CLOCKWORK_CAT:
			break;
		case CREEPING_HAND:
			break;
		case CUTE_PHOENIX_EGGLING:
			break;
		case DALMATIAN:
			break;
		case DALMATIAN_1:
			break;
		case DALMATIAN_2:
			break;
		case EX_EX_PARROT:
			break;
		case GECKO:
			break;
		case GECKO_1:
			break;
		case GECKO_2:
			break;
		case GECKO_3:
			break;
		case GECKO_4:
			break;
		case GIANT_CRAB:
			break;
		case GIANT_CRAB_1:
			break;
		case GIANT_CRAB_2:
			break;
		case GIANT_CRAB_3:
			break;
		case GIANT_CRAB_4:
			break;
		case GREYHOUND:
			break;
		case GREYHOUND_1:
			break;
		case GREYHOUND_2:
			break;
		case HELLCAT:
			break;
		case LABRADOR:
			break;
		case LABRADOR_1:
			break;
		case LABRADOR_2:
			break;
		case MEAN_PHOENIX_EGGLING:
			break;
		case MINITRICE:
			break;
		case MONKEY:
			break;
		case MONKEY_1:
			break;
		case MONKEY_2:
			break;
		case MONKEY_3:
			break;
		case MONKEY_4:
			break;
		case MONKEY_5:
			break;
		case MONKEY_6:
			break;
		case MONKEY_7:
			break;
		case MONKEY_8:
			break;
		case MONKEY_9:
			break;
		case PENGUIN:
			break;
		case PENGUIN_1:
			break;
		case PENGUIN_2:
			break;
		case PLATYPUS:
			break;
		case PLATYPUS_1:
			break;
		case PLATYPUS_2:
			break;
		case RACCOON:
			break;
		case RACCOON_1:
			break;
		case RACCOON_2:
			break;
		case RAVEN:
			break;
		case RAVEN_1:
			break;
		case RAVEN_2:
			break;
		case RAVEN_3:
			break;
		case RAVEN_4:
			break;
		case RAVEN_5:
			break;
		case RUNE_GUARDIAN:
			break;
		case RUNE_GUARDIAN_1:
			break;
		case RUNE_GUARDIAN_10:
			break;
		case RUNE_GUARDIAN_11:
			break;
		case RUNE_GUARDIAN_12:
			break;
		case RUNE_GUARDIAN_13:
			break;
		case RUNE_GUARDIAN_2:
			break;
		case RUNE_GUARDIAN_3:
			break;
		case RUNE_GUARDIAN_4:
			break;
		case RUNE_GUARDIAN_5:
			break;
		case RUNE_GUARDIAN_6:
			break;
		case RUNE_GUARDIAN_7:
			break;
		case RUNE_GUARDIAN_8:
			break;
		case RUNE_GUARDIAN_9:
			break;
		case SHEEPDOG:
			break;
		case SHEEPDOG_1:
			break;
		case SHEEPDOG_2:
			break;
		case SNEAKER_PEEPER:
			break;
		case SQUIRREL:
			break;
		case SQUIRREL_1:
			break;
		case SQUIRREL_2:
			break;
		case SQUIRREL_3:
			break;
		case SQUIRREL_4:
			break;
		case TERRIER:
			break;
		case TERRIER_1:
			break;
		case TERRIER_2:
			break;
		case TOOTH_CREATURE:
			break;
		case TROLL_BABY:
			break;
		case VULTURE:
			break;
		default:
			break;
		}
		return true;
	}

	/**
	 * Initializes the pet manager.
	 */
	public void init() {
		if (npcId > 0 && itemId > 0) {
			spawnPet(itemId, false);
		}
	}

	/**
	 * Removes the details for this pet.
	 * 
	 * @param npcId
	 *            The item id of the pet.
	 */
	public void removeDetails(int itemId) {
		Pets pets = Pets.forId(itemId);
		if (pets == null) {
			return;
		}
		petDetails.remove(pets.getBabyItemId());
	}

	/**
	 * Sets the itemId.
	 * 
	 * @param itemId
	 *            The itemId to set.
	 */
	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	/**
	 * Sets the npcId.
	 * 
	 * @param npcId
	 *            The npcId to set.
	 */
	public void setNpcId(int npcId) {
		this.npcId = npcId;
	}

	/**
	 * Sets the player.
	 * 
	 * @param player
	 *            The player to set.
	 */
	public void setPlayer(Player player) {
		this.player = player;
	}

	/**
	 * Sets the trollBabyName.
	 * 
	 * @param trollBabyName
	 *            The trollBabyName to set.
	 */
	public void setTrollBabyName(String trollBabyName) {
		this.trollBabyName = trollBabyName;
	}

	/**
	 * Spawns a pet.
	 * 
	 * @param itemId
	 *            The item id.
	 * @param deleteItem
	 *            If the item should be removed.
	 * @return {@code True} if we were dealing with a pet item id.
	 */
	public boolean spawnPet(int itemId, boolean deleteItem) {
		Pets pets = Pets.forId(itemId);
		if (pets == null) {
			return false;
		}
		if (player.getPet() != null || player.getFamiliar() != null) {
			player.getPackets().sendGameMessage("You already have a follower.");
			return true;
		}
		if (!hasRequirements(pets)) {
			return true;
		}
		int baseItemId = pets.getBabyItemId();
		PetDetails details = petDetails.get(baseItemId);
		if (details == null) {
			details = new PetDetails(pets.getGrowthRate() == 0.0 ? 100.0 : 0.0);
			petDetails.put(baseItemId, details);
		}
		int id = pets.getItemId(details.getStage());
		if (itemId != id) {
			player.getPackets().sendGameMessage("This is not the right pet, grow the pet correctly.");
			return true;
		}
		int npcId = pets.getNpcId(details.getStage());
		if (npcId > 0) {
			Pet pet = new Pet(npcId, itemId, player, player, details);
			this.npcId = npcId;
			this.itemId = itemId;
			pet.setGrowthRate(pets.getGrowthRate());
			player.setPet(pet);
			if (deleteItem) {
				player.setNextAnimation(new Animation(827));
				player.getInventory().deleteItem(itemId, 1);
			}
			return true;
		}
		return true;
	}

}