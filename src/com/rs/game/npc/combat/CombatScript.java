package com.rs.game.npc.combat;

import com.rs.game.Entity;
import com.rs.game.Hit;
import com.rs.game.Hit.HitLook;
import com.rs.game.npc.NPC;
import com.rs.game.npc.familiar.Steeltitan;
import com.rs.game.player.CombatDefinitions;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.game.player.combat.PlayerCombat;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Utils;

public abstract class CombatScript {

	public static void delayHit(NPC npc, int delay, final Entity target, final Hit... hits) {
		npc.getCombat().addAttackedByDelay(target);
		WorldTasksManager.schedule(new WorldTask() {

			@Override
			public void run() {
				for (Hit hit : hits) {
					NPC npc = (NPC) hit.getSource();
					if (npc.isDead() || npc.hasFinished() || target.isDead() || target.hasFinished())
						return;
					target.applyHit(hit);
					npc.getCombat().doDefenceEmote(target);
					if (target instanceof Player) {
						Player p2 = (Player) target;
						// p2.closeInterfaces();
						if (p2.getCombatDefinitions().isAutoRelatie() && !p2.getActionManager().hasSkillWorking()
								&& !p2.hasWalkSteps())
							p2.getActionManager().setAction(new PlayerCombat(npc));
					} else {
						NPC n = (NPC) target;
						if (!n.isUnderCombat() || n.canBeAttackedByAutoRetaliate())
							n.setTarget(npc);
					}
				}
			}
		}, delay);
	}

	public static Hit getPoisonHit(NPC npc, int damage) {
		return new Hit(npc, damage, HitLook.POISON_DAMAGE);
	}
	
	public static Hit getMagicHit(NPC npc, int damage) {
		return new Hit(npc, damage, HitLook.MAGIC_DAMAGE);
	}

	public static Hit getMeleeHit(NPC npc, int damage) {
		return new Hit(npc, damage, HitLook.MELEE_DAMAGE);
	}

	// Replace the getRandomMaxHit method in your CombatScript class with this safe version:

	public static int getRandomMaxHit(NPC npc, int maxHit, int attackStyle, Entity target) {
	    int[] bonuses = npc.getBonuses();
	    double att = 0;
	    
	    // SAFE BONUS ACCESS - prevents ArrayIndexOutOfBoundsException
	    if (bonuses != null && bonuses.length >= 5) {
	        // Your NPCBonuses system uses indices 0-4 for attack bonuses
	        if (attackStyle == NPCCombatDefinitions.RANGE) {
	            att = bonuses[4]; // ranged attack (index 4)
	        } else if (attackStyle == NPCCombatDefinitions.MAGE) {
	            att = bonuses[3]; // magic attack (index 3)  
	        } else {
	            // For melee, use the highest of stab/slash/crush
	            att = Math.max(bonuses[0], Math.max(bonuses[1], bonuses[2]));
	        }
	    }
	    
	    double def;
	    if (target instanceof Player) {
	        Player p2 = (Player) target;
	        def = p2.getSkills().getLevel(Skills.DEFENCE);
	        
	        // SAFE PLAYER BONUS ACCESS
	        int[] playerBonuses = p2.getCombatDefinitions().getBonuses();
	        if (playerBonuses != null) {
	            // Use safe array access for player bonuses too
	            int defenseBonus = 0;
	            if (attackStyle == NPCCombatDefinitions.RANGE && playerBonuses.length > 9) {
	                defenseBonus = playerBonuses[9]; // ranged defense
	            } else if (attackStyle == NPCCombatDefinitions.MAGE && playerBonuses.length > 8) {
	                defenseBonus = playerBonuses[8]; // magic defense
	            } else if (playerBonuses.length > 7) {
	                // Use highest melee defense for melee attacks
	                int stabDef = playerBonuses.length > 5 ? playerBonuses[5] : 0;
	                int slashDef = playerBonuses.length > 6 ? playerBonuses[6] : 0;
	                int crushDef = playerBonuses.length > 7 ? playerBonuses[7] : 0;
	                defenseBonus = Math.max(stabDef, Math.max(slashDef, crushDef));
	            }
	            def += (2 * defenseBonus);
	        }
	        
	        def *= p2.getPrayer().getDefenceMultiplier();
	        if (attackStyle == NPCCombatDefinitions.MELEE) {
	            if (p2.getFamiliar() instanceof Steeltitan)
	                def *= 1.15;
	        }
	    } else {
	        NPC n = (NPC) target;
	        int[] targetBonuses = n.getBonuses();
	        def = 0;
	        
	        // SAFE TARGET NPC BONUS ACCESS
	        if (targetBonuses != null && targetBonuses.length >= 10) {
	            if (attackStyle == NPCCombatDefinitions.RANGE) {
	                def = targetBonuses[9]; // ranged defense (index 9)
	            } else if (attackStyle == NPCCombatDefinitions.MAGE) {
	                def = targetBonuses[8]; // magic defense (index 8)
	            } else {
	                // Use highest melee defense
	                def = Math.max(targetBonuses[5], Math.max(targetBonuses[6], targetBonuses[7]));
	            }
	        }
	        def *= 2;
	    }
	    
	    // Ensure def is never 0 to avoid division by zero
	    if (def <= 0) def = 1;
	    
	    double prob = att / def;
	    if (prob > 0.90) // max, 90% prob hit so even lvl 138 can miss at lvl 3
	        prob = 0.90;
	    else if (prob < 0.05) // minimum 5% so even lvl 3 can hit lvl 138
	        prob = 0.05;
	    if (prob < Math.random())
	        return 0;
	    return Utils.getRandom(maxHit);
	}

	// Helper method to get highest melee defense
	private static int getHighestMeleeDefense(int[] bonuses) {
	    return Math.max(bonuses[CombatDefinitions.STAB_DEF], 
	           Math.max(bonuses[CombatDefinitions.SLASH_DEF], 
	                   bonuses[CombatDefinitions.CRUSH_DEF]));
	}

	public static Hit getRangeHit(NPC npc, int damage) {
		return new Hit(npc, damage, HitLook.RANGE_DAMAGE);
	}

	public static Hit getRegularHit(NPC npc, int damage) {
		return new Hit(npc, damage, HitLook.REGULAR_DAMAGE);
	}

	/*
	 * Returns Move Delay
	 */
	public abstract int attack(NPC npc, Entity target);

	/*
	 * Returns ids and names
	 */
	public abstract Object[] getKeys();

	/**
	 * Sends sound.
	 * 
	 * @param soundId
	 *            the Sound ID.
	 * @param player
	 *            The player.
	 * @param target
	 *            The target.
	 */
	public void playSound(int soundId, Player player, Entity target) {
		if (soundId == -1)
			return;
		player.getPackets().sendSound(soundId, 0, 1);
		if (target instanceof Player) {
			Player p2 = (Player) target;
			p2.getPackets().sendSound(soundId, 0, 1);
		}
	}

	public static int getMaxHit(NPC npc, int maxHit, int attackStyle, Entity target) {
		return getRandomMaxHit(npc, maxHit, attackStyle, target);
	}

	public static int getMaxHit(NPC npc, int attackType, Entity target) {
		return getRandomMaxHit(npc, npc.getMaxHit(), attackType, target);
	}
}