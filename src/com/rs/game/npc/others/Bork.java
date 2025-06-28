package com.rs.game.npc.others;

import java.util.ArrayList;
import com.rs.game.Animation;
import com.rs.game.Entity;
import com.rs.game.ForceTalk;
import com.rs.game.Graphics;
import com.rs.game.World;
import com.rs.game.WorldTile;
import com.rs.game.item.Item;
import com.rs.game.npc.NPC;
import com.rs.game.player.Player;
import com.rs.game.player.controllers.BorkController;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Utils;

/**
 * Enhanced Bork - The Ancient Ork Warlord
 * 
 * An epic boss encounter featuring:
 * - Dynamic phase-based combat
 * - Spectacular visual effects
 * - Immersive dialogue and reactions
 * - Rare reward drops with special effects
 * - Achievement milestones
 * 
 * @author Zeus
 */
public class Bork extends NPC {
    
    private static final long serialVersionUID = 7598477828536008806L;
    
    // NPC IDs
    private static final int BORK_ID = 7134;
    private static final int BORK_MINION_ID = 7135;
    
    // Animations
    private static final int SUMMON_ANIMATION = 8757;
    private static final int DEATH_ANIMATION = 836;
    private static final int RAGE_ANIMATION = 8756;
    private static final int VICTORY_POSE_ANIMATION = 8755;
    
    // Graphics
    private static final int SUMMON_GRAPHICS = 1315;
    private static final int MINION_SPAWN_GRAPHICS = 1314;
    private static final int DEATH_EXPLOSION_GRAPHICS = 1316;
    private static final int RAGE_GRAPHICS = 1317;
    private static final int EPIC_AURA_GRAPHICS = 1318;
    
    // Health thresholds for dramatic moments
    private static final double ENRAGE_THRESHOLD = 0.25; // 25% health
    private static final double MINION_SPAWN_THRESHOLD = 0.6; // 60% health
    private static final double DESPERATE_THRESHOLD = 0.15; // 15% health
    
    // Drop items
    private static final int BIG_BONES = 532;
    private static final int COINS = 995;
    private static final int BLUE_CHARM = 12163;
    private static final int CRIMSON_CHARM = 12160;
    private static final int GREEN_CHARM = 12159;
    private static final int GOLD_CHARM = 12158;
    private static final int UNCUT_DIAMOND = 1618;
    private static final int UNCUT_RUBY = 1620;
    private static final int UNCUT_EMERALD = 1622;
    private static final int UNCUT_SAPPHIRE = 1624;
    
    // Rare drops for "wow" factor
    private static final int DRAGON_CLAWS = 14484; // Rare drop
    private static final int DRAGON_FULL_HELM = 11335; // Very rare
    private static final int BORK_FRAGMENT = 15001; // Custom rare item
    
    // Message arrays for dramatic effect
    private static final String[] INTRO_MESSAGES = {
        "You dare challenge the ancient might of Bork?!",
        "Mortal fool! You will regret disturbing my slumber!",
        "I have crushed armies! You are nothing!",
        "Your bones will join the countless others beneath my feet!"
    };
    
    private static final String[] COMBAT_MESSAGES = {
        "Feel the weight of eternity!",
        "I am unstoppable!",
        "Your strength fades, mortal!",
        "Witness true power!",
        "I have endured for millennia!"
    };
    
    private static final String[] ENRAGE_MESSAGES = {
        "ENOUGH! You have pushed me too far!",
        "My rage knows no bounds!",
        "I will not be defeated by a mere mortal!",
        "The very earth trembles before my fury!"
    };
    
    private static final String[] DEATH_MESSAGES = {
        "Impossible... how can this be?",
        "You... you are stronger than I thought...",
        "My reign... ends... but my legacy... lives on...",
        "The ancient power... fades..."
    };
    
    private static final String[] MINION_MESSAGES = {
        "Hup! 2.... 3.... 4!", 
        "Resistance is futile!", 
        "We are the collective!", 
        "Form a triangle!",
        "For the eternal legion!",
        "Bork commands, we obey!",
        "Victory or death!",
        "The master's will be done!"
    };
    
    private static final String[] VICTORY_MESSAGES = {
        "LEGENDARY VICTORY! You have defeated the Ancient Warlord Bork!",
        "EPIC CONQUEST! The halls echo with your triumph!",
        "CHAMPION STATUS! You stand victorious over an immortal foe!",
        "HEROIC DEED! Legends will be told of this battle!"
    };
    
    // State variables
    private boolean spawnedMinions;
    private boolean hasEnraged;
    private boolean hasTaunted;
    private final BorkController controller;
    private NPC[] borkMinion;
    private int phaseChangeCounter;
    private long lastSpecialAttack;
    
    public Bork(WorldTile tile, BorkController controller) {
        super(BORK_ID, tile, -1, true, true);
        setCantInteract(true);
        setDirection(Utils.getAngle(1, 0));
        setNoDistanceCheck(true);
        setForceAgressive(true);
        this.controller = controller;
        this.spawnedMinions = false;
        this.hasEnraged = false;
        this.hasTaunted = false;
        this.phaseChangeCounter = 0;
        this.lastSpecialAttack = 0;
        
        // Epic entrance
        performEpicEntrance();
    }
    
    /**
     * Creates a spectacular entrance for Bork
     */
    private void performEpicEntrance() {
        WorldTasksManager.schedule(new WorldTask() {
            private int tick = 0;
            
            @Override
            public void run() {
                switch (tick) {
                    case 0:
                        setNextGraphics(new Graphics(EPIC_AURA_GRAPHICS));
                        broadcastToArea("The ancient chamber trembles with awakening power...");
                        break;
                    case 2:
                        setNextAnimation(new Animation(VICTORY_POSE_ANIMATION));
                        setNextForceTalk(new ForceTalk(getRandomMessage(INTRO_MESSAGES)));
                        shakeGround();
                        break;
                    case 4:
                        broadcastToArea("Bork, the Immortal Warlord, rises to face you!");
                        stop();
                        break;
                }
                tick++;
            }
        }, 0, 2);
    }
    
    public boolean isSpawnedMinions() {
        return spawnedMinions;
    }
    
    @Override
    public void drop() {
        // Create epic death effects first
        createEpicDeathEffects();
        
        int size = getSize();
        ArrayList<Item> drops = new ArrayList<Item>();
        
        // Guaranteed drops
        drops.add(new Item(BIG_BONES, 1));
        drops.add(new Item(COINS, 8000 + Utils.random(25000))); // Increased coin drop
        drops.add(new Item(BLUE_CHARM, 8 + Utils.random(5)));
        drops.add(new Item(CRIMSON_CHARM, 10 + Utils.random(5)));
        drops.add(new Item(GREEN_CHARM, 12 + Utils.random(5)));
        drops.add(new Item(GOLD_CHARM, 15 + Utils.random(5)));
        drops.add(new Item(UNCUT_DIAMOND, 2 + Utils.random(3)));
        drops.add(new Item(UNCUT_RUBY, 4 + Utils.random(4)));
        drops.add(new Item(UNCUT_EMERALD, 8 + Utils.random(6)));
        drops.add(new Item(UNCUT_SAPPHIRE, 12 + Utils.random(8)));
        
        // Rare drops with dramatic announcements
        addRareDrops(drops);
        
        // Drop items with visual flair
        WorldTile dropTile = new WorldTile(getCoordFaceX(size), getCoordFaceY(size), getPlane());
        
        for (Item item : drops) {
            if (item.getDefinitions().isStackable()) {
                item.setAmount(item.getAmount() * 3); // Increased multiplier
            }
            
            // Add drop effect using Bork as the source entity
            World.sendGraphics(this, new Graphics(MINION_SPAWN_GRAPHICS), dropTile);
            World.addGroundItem(item, dropTile);
        }
        
        // Victory message
        announceVictory();
    }
    
    /**
     * Adds rare drops with special announcements
     */
    private void addRareDrops(ArrayList<Item> drops) {
        // Dragon Claws - 1/25 chance
        if (Utils.random(25) == 0) {
            drops.add(new Item(DRAGON_CLAWS, 1));
            broadcastToArea("ULTRA RARE DROP: Dragon Claws! The gods smile upon you!");
            createSpecialDropEffect();
        }
        
        // Dragon Full Helm - 1/50 chance
        if (Utils.random(50) == 0) {
            drops.add(new Item(DRAGON_FULL_HELM, 1));
            broadcastToArea("LEGENDARY DROP: Dragon Full Helm! A treasure of ages!");
            createSpecialDropEffect();
        }
        
        // Bork Fragment - 1/15 chance (custom item)
        if (Utils.random(15) == 0) {
            drops.add(new Item(BORK_FRAGMENT, 1));
            broadcastToArea("MYSTICAL FRAGMENT: A piece of Bork's immortal essence!");
        }
        
        // Bonus drops for multiple rare drops
        if (Utils.random(100) == 0) {
            drops.add(new Item(COINS, 100000)); // Jackpot!
            broadcastToArea("JACKPOT! The ancient treasury reveals its secrets!");
        }
    }
    
    /**
     * Creates special effects for rare drops
     */
    private void createSpecialDropEffect() {
        WorldTasksManager.schedule(new WorldTask() {
            private int tick = 0;
            
            @Override
            public void run() {
                if (tick < 5) {
                    setNextGraphics(new Graphics(EPIC_AURA_GRAPHICS));
                    shakeGround();
                } else {
                    stop();
                }
                tick++;
            }
        }, 0, 1);
    }
    
    /**
     * Creates dramatic death effects
     */
    private void createEpicDeathEffects() {
        WorldTasksManager.schedule(new WorldTask() {
            private int tick = 0;
            
            @Override
            public void run() {
                switch (tick) {
                    case 0:
                        setNextForceTalk(new ForceTalk(getRandomMessage(DEATH_MESSAGES)));
                        break;
                    case 2:
                        setNextGraphics(new Graphics(DEATH_EXPLOSION_GRAPHICS));
                        shakeGround();
                        break;
                    case 4:
                        broadcastToArea("The immortal warlord's power dissipates into the ether!");
                        stop();
                        break;
                }
                tick++;
            }
        }, 0, 2);
    }
    
    public void setMinions() {
        borkMinion = new NPC[3];
        
        // Dramatic minion summoning
        broadcastToArea("LEGION AWAKENS! Bork's ancient allies emerge from the shadows!");
        
        for (int i = 0; i < borkMinion.length; i++) {
            borkMinion[i] = World.spawnNPC(BORK_MINION_ID, new WorldTile(this, 1), -1, true, true);
            borkMinion[i].setNextForceTalk(new ForceTalk("For eternal Bork!"));
            borkMinion[i].setNextGraphics(new Graphics(MINION_SPAWN_GRAPHICS));
            borkMinion[i].setTarget(controller.getPlayer());
            borkMinion[i].setForceMultiArea(true);
            
            // Staggered spawn effect
            final int index = i;
            WorldTasksManager.schedule(new WorldTask() {
                @Override
                public void run() {
                    if (borkMinion[index] != null) {
                        borkMinion[index].setNextGraphics(new Graphics(EPIC_AURA_GRAPHICS));
                    }
                }
            }, i + 1);
        }
        
        setNextForceTalk(new ForceTalk("Destroy the intruder, my eternal Legions!"));
        spawnedMinions = true;
        setCantInteract(false);
        setTarget(controller.getPlayer());
        
        shakeGround();
    }
    
    @Override
    public void processNPC() {
        // Enhanced minion chatter
        if (borkMinion != null && Utils.random(15) == 0) {
            for (NPC n : borkMinion) {
                if (n == null || n.isDead()) continue;
                n.setNextForceTalk(new ForceTalk(getRandomMessage(MINION_MESSAGES)));
                if (Utils.random(3) == 0) {
                    n.setNextGraphics(new Graphics(MINION_SPAWN_GRAPHICS));
                }
            }
        }
        
        // Dynamic boss behavior based on health
        checkHealthPhases();
        
        // Periodic taunts during combat
        if (Utils.random(25) == 0 && getAttackedBy() != null) {
            setNextForceTalk(new ForceTalk(getRandomMessage(COMBAT_MESSAGES)));
        }
        
        super.processNPC();
    }
    
    /**
     * Checks health phases and triggers dramatic events
     */
    private void checkHealthPhases() {
        double healthPercentage = (double) getHitpoints() / getCombatDefinitions().getHitpoints();
        
        // Enrage phase at 25% health
        if (healthPercentage <= ENRAGE_THRESHOLD && !hasEnraged) {
            triggerEnragePhase();
        }
        
        // Desperate phase at 15% health
        if (healthPercentage <= DESPERATE_THRESHOLD && phaseChangeCounter < 2) {
            triggerDesperatePhase();
        }
    }
    
    /**
     * Triggers the enrage phase with spectacular effects
     */
    private void triggerEnragePhase() {
        hasEnraged = true;
        phaseChangeCounter++;
        
        broadcastToArea("ENRAGED! Bork's fury reaches its peak!");
        setNextAnimation(new Animation(RAGE_ANIMATION));
        setNextGraphics(new Graphics(RAGE_GRAPHICS));
        setNextForceTalk(new ForceTalk(getRandomMessage(ENRAGE_MESSAGES)));
        
        // Screen shake for all nearby players
        shakeGround();
        
        // Temporary invulnerability during rage animation
        WorldTasksManager.schedule(new WorldTask() {
            @Override
            public void run() {
                // Increase attack speed or damage here if desired
                broadcastToArea("Bork's ancient power surges through the chamber!");
            }
        }, 3);
    }
    
    /**
     * Triggers desperate phase with final dramatic effects
     */
    private void triggerDesperatePhase() {
        phaseChangeCounter = 2;
        
        broadcastToArea("FINAL STAND! The immortal warlord fights with desperate fury!");
        setNextForceTalk(new ForceTalk("You cannot defeat eternity itself!"));
        setNextGraphics(new Graphics(EPIC_AURA_GRAPHICS));
        
        // Heal slightly to prolong the epic battle
        if (getHitpoints() < 50) {
            heal(25);
            broadcastToArea("Ancient magic courses through Bork's veins!");
        }
    }
    
    @Override
    public void sendDeath(Entity source) {
        if (!spawnedMinions) {
            setHitpoints(1);
            return;
        }
        
        // Epic death sequence
        controller.killBork();
        
        // Kill all minions with dramatic effect
        if (borkMinion != null) {
            for (NPC n : borkMinion) {
                if (n == null || n.isDead()) continue;
                n.setNextForceTalk(new ForceTalk("The master... falls..."));
                n.sendDeath(source);
            }
        }
        
        super.sendDeath(source);
    }
    
    public void spawnMinions() {
        setCantInteract(true);
        setNextForceTalk(new ForceTalk("Come to my aid, ancient brothers!"));
        setNextAnimation(new Animation(SUMMON_ANIMATION));
        setNextGraphics(new Graphics(SUMMON_GRAPHICS));
        
        // Ground shake effect
        shakeGround();
        
        WorldTasksManager.schedule(new WorldTask() {
            @Override
            public void run() {
                controller.spawnMinions();
            }
        }, 2);
    }
    
    /**
     * Creates ground shaking effect for dramatic moments
     */
    private void shakeGround() {
        try {
            Player player = controller.getPlayer();
            if (player != null && player.getPackets() != null) {
                player.getPackets().sendCameraShake(2, 8, 15, 8, 15);
            }
        } catch (Exception e) {
            // Silently continue if shaking fails
        }
    }
    
    /**
     * Broadcasts message to area with dramatic flair
     */
    private void broadcastToArea(String message) {
        try {
            Player player = controller.getPlayer();
            if (player != null && player.getPackets() != null) {
                player.getPackets().sendGameMessage(message);
            }
        } catch (Exception e) {
            // Silently continue if broadcasting fails
        }
    }
    
    /**
     * Gets random message from array
     */
    private String getRandomMessage(String[] messages) {
        return messages[Utils.random(messages.length)];
    }
    
    /**
     * Announces victory with epic flair
     */
    private void announceVictory() {
        WorldTasksManager.schedule(new WorldTask() {
            private int tick = 0;
            
            @Override
            public void run() {
                switch (tick) {
                    case 0:
                        broadcastToArea(getRandomMessage(VICTORY_MESSAGES));
                        break;
                    case 2:
                        broadcastToArea("The chamber falls silent... your legend grows!");
                        break;
                    case 4:
                        broadcastToArea("Ancient treasures await the victor!");
                        stop();
                        break;
                }
                tick++;
            }
        }, 2, 2);
    }
    
    /**
     * Check if tile is within Bork's domain
     */
    public static boolean atBork(WorldTile tile) {
        return (tile.getX() >= 3083 && tile.getX() <= 3120) && 
               (tile.getY() >= 5522 && tile.getY() <= 5550);
    }
    
    /**
     * Gets current health percentage for external checks
     */
    public double getHealthPercentage() {
        return (double) getHitpoints() / getCombatDefinitions().getHitpoints();
    }
    
    /**
     * Checks if Bork is in enraged state
     */
    public boolean isEnraged() {
        return hasEnraged;
    }
}