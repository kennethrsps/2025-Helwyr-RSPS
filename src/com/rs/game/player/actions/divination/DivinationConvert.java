package com.rs.game.player.actions.divination;

import com.rs.game.Animation;
import com.rs.game.Graphics;
import com.rs.game.item.Item;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.game.player.actions.Action;

/**
 * Used to handle collected memory depositing.
 * @author Zeus 
 */
public class DivinationConvert extends Action {

    // TODO boon exp boosts

    public enum ConvertMode {
    	CONVERT_TO_ENERGY, CONVERT_TO_XP, CONVERT_TO_MORE_XP;
    }

    private MemoryInfo info;
    private boolean enriched;
    private ConvertMode mode;

    public DivinationConvert(Player player, Object[] args) {
		setMode((ConvertMode) args[0]);
		checkAll(player);
    }

    public boolean checkAll(Player player) {
		for (Item item : player.getInventory().getItems().getItems()) {
		    if (item == null)
		    	continue;
		    for (MemoryInfo i : MemoryInfo.values()) {
				if (item.getId() == i.getMemoryId()) {
				    info = i;
				    enriched = false;
				    return true;
				}
				if (item.getId() == i.getEnrichedMemoryId()) {
				    info = i;
				    enriched = true;
				    return true;
				}
		    }
		}
		return false;
    }

    @Override
    public boolean start(Player player) {
		if (!checkAll(player))
		    return false;
		return true;
    }

    @Override
    public boolean process(Player player) {
		if (!checkAll(player))
		    return false;
		return true;
    }

    @Override
    public int processWithDelay(Player player) {
		switch (mode) {
		case CONVERT_TO_ENERGY:
			if (player.getAnimations().hasPowerConversion && player.getAnimations().powerConversion) {
				player.setNextAnimation(new Animation(22858));
			    player.setNextGraphics(new Graphics(4623));
			} 
			else if (player.getAnimations().hasAgileConversion && player.getAnimations().agileConversion) {
				player.setNextAnimation(new Animation(22867));
			    player.setNextGraphics(new Graphics(4622));
			} else {
				player.setNextAnimation(new Animation(21232));
				player.setNextGraphics(new Graphics(4239));
			}
		    player.getSkills().addXp(Skills.DIVINATION, 1);
		    player.getInventory().deleteItem(enriched ? info.getEnrichedMemoryId() : info.getMemoryId(), 1);
		    player.getInventory().addItem(info.getEnergyId(), enriched ? 6 : 3);
		    break;
		case CONVERT_TO_XP:
			if (player.getAnimations().hasPowerConversion && player.getAnimations().powerConversion) {
				player.setNextAnimation(new Animation(22858));
			    player.setNextGraphics(new Graphics(4625));
			} 
			else if (player.getAnimations().hasAgileConversion && player.getAnimations().agileConversion) {
				player.setNextAnimation(new Animation(22867));
			    player.setNextGraphics(new Graphics(4627));
			} else {
			    player.setNextAnimation(new Animation(21234));
			    player.setNextGraphics(new Graphics(4240));
			}
		    player.getSkills().addXp(Skills.DIVINATION, enriched ? info.getXp() * 2 : info.getXp());
		    player.getInventory().deleteItem(enriched ? info.getEnrichedMemoryId() : info.getMemoryId(), 1);
		    break;
		case CONVERT_TO_MORE_XP:
		    if (!player.getInventory().containsItem(info.getEnergyId(), 5)) {
				setMode(ConvertMode.CONVERT_TO_XP);
				if (player.getAnimations().hasPowerConversion && player.getAnimations().powerConversion) {
					player.setNextAnimation(new Animation(22858));
				    player.setNextGraphics(new Graphics(4625));
				} 
				else if (player.getAnimations().hasAgileConversion && player.getAnimations().agileConversion) {
					player.setNextAnimation(new Animation(22867));
				    player.setNextGraphics(new Graphics(4622));
				} else {
				    player.setNextAnimation(new Animation(21234));
				    player.setNextGraphics(new Graphics(4240));
				}
				player.getSkills().addXp(Skills.DIVINATION, enriched ? info.getXp() * 2 : info.getXp());
				player.getInventory().deleteItem(enriched ? info.getEnrichedMemoryId() : info.getMemoryId(), 1);
				return 1;
		    } else {
		    	if (player.getAnimations().hasPowerConversion && player.getAnimations().powerConversion) {
					player.setNextAnimation(new Animation(22858));
				    player.setNextGraphics(new Graphics(4625));
				} 
		    	else if (player.getAnimations().hasAgileConversion && player.getAnimations().agileConversion) {
					player.setNextAnimation(new Animation(22867));
				    player.setNextGraphics(new Graphics(4622));
				} else {
				    player.setNextAnimation(new Animation(21234));
				    player.setNextGraphics(new Graphics(4240));
				}
				player.getSkills().addXp(Skills.DIVINATION, enriched ? 
						(info.getXp() * 2) + ((info.getXp() * 2) * 0.25) 
						: (info.getXp()) + ((info.getXp()) * 0.25));
		    }
			if (player.getInventory().containsItem(info.getEnrichedMemoryId(), 1))
		    	player.getInventory().deleteItem(info.getEnrichedMemoryId(), 1);
		    else
		    	player.getInventory().deleteItem(info.getMemoryId(), 1);
		    player.getInventory().deleteItem(info.getEnergyId(), 5);
		    break;
		}
		return 1;
    }

    @Override
    public void stop(Player player) {
    	setActionDelay(player, 1);
    }

    public boolean isEnriched() {
    	return enriched;
    }

    public void setEnriched(boolean enriched) {
    	this.enriched = enriched;
    }

    public MemoryInfo getInfo() {
    	return info;
    }

    public void setInfo(MemoryInfo info) {
    	this.info = info;
    }

    public ConvertMode getMode() {
    	return mode;
    }

    public void setMode(ConvertMode mode) {
    	this.mode = mode;
    }
}