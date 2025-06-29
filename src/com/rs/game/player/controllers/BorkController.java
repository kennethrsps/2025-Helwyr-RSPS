package com.rs.game.player.controllers;

import java.util.TimerTask;

import com.rs.Settings;
import com.rs.cores.CoresManager;
import com.rs.game.Animation;
import com.rs.game.ForceTalk;
import com.rs.game.Graphics;
import com.rs.game.Hit;
import com.rs.game.Hit.HitLook;
import com.rs.game.MapInstance;
import com.rs.game.World;
import com.rs.game.WorldObject;
import com.rs.game.WorldTile;
import com.rs.game.npc.NPC;
import com.rs.game.npc.others.Bork;
import com.rs.game.player.MusicsManager;
import com.rs.game.player.Player;
import com.rs.game.player.content.FadingScreen;
import com.rs.game.player.content.contracts.ContractHandler;
import com.rs.game.player.cutscenes.Cutscene;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Colors;
import com.rs.utils.Logger;
import com.rs.utils.Utils;

/**
 * Handles the Bork's Controller for boss fight mechanics.
 * This controller manages the entire Bork encounter including:
 * - Entry restrictions based on member tier
 * - Instance creation and management
 * - NPC spawning and interactions
 * - Cutscenes and special effects
 * - Combat mechanics including earthquake effects
 * 
 * @author Zeus
 */
public class BorkController extends Controller {

    // Constants
    private static final WorldTile OUTSIDE_LOCATION = new WorldTile(3143, 5545, 0);
    
    // Object IDs
    private static final int EXIT_PORTAL_ID = 77745;
    
    // NPC IDs
    private static final int SUROK_MAGIS_INITIAL = 7136;
    private static final int SUROK_MAGIS_RETURN = 7137;
    
    // Map Instance coordinates
    private static final int MAP_CHUNK_X = 385;
    private static final int MAP_CHUNK_Y = 690;
    
    // Instance tile positions
    private static final WorldTile PLAYER_START_POSITION = new WorldTile(25, 17, 0);
    private static final WorldTile SUROK_SPAWN_POSITION = new WorldTile(22, 20, 0);
    private static final WorldTile BORK_SPAWN_POSITION = new WorldTile(12, 15, 0);
    
    // Animations
    private static final int TELEPORT_ANIMATION = 17803;
    private static final int SUROK_TELEPORT_ANIMATION = 8939;
    
    // Graphics
    private static final int SUROK_TELEPORT_GRAPHICS = 1576;
    
    // Cutscene interfaces
    private static final int INTRO_CUTSCENE = 692;
    private static final int MINION_CUTSCENE = 691;
    private static final int DEATH_CUTSCENE = 693;
    
    // Timing constants
    private static final int INTRO_CUTSCENE_DURATION = 10000;
    private static final int MINION_CUTSCENE_DURATION = 4200;
    private static final int DEATH_CUTSCENE_DURATION = 5400;
    private static final int EARTHQUAKE_DAMAGE_INTERVAL = 30;
    private static final int MAX_EARTHQUAKE_DAMAGE = 499;
    
    // Music
    private static final int BORK_MUSIC = 587;
    
    // Cooldown times in hours by member tier
    private static final int DIAMOND_COOLDOWN_HOURS = 1;
    private static final int PLATINUM_COOLDOWN_HOURS = 2;
    private static final int GOLD_COOLDOWN_HOURS = 3;
    private static final int SILVER_COOLDOWN_HOURS = 4;
    private static final int BRONZE_COOLDOWN_HOURS = 5;
    private static final int DEFAULT_COOLDOWN_HOURS = 6;
    
    // Instance variables
    private MapInstance instance;
    private NPC surokMagis;
    private Bork bork;
    private boolean earthquake;
    private int earthquakeTimer;

    /**
     * Attempts to enter the Bork encounter, checking cooldown restrictions.
     * Cooldown varies based on player's membership tier.
     */
    public static void enterBork(Player player) {
        if (!canEnterBork(player)) {
            player.getDialogueManager().startDialogue("SimpleMessage", 
                "The portal appears to have stopped working for now. Perhaps you should return later?");
            return;
        }
        player.getControlerManager().startControler("BorkController");
    }
    
    /**
     * Checks if player can enter Bork based on cooldown timer.
     */
    private static boolean canEnterBork(Player player) {
        if (Settings.DEBUG) {
            return true;
        }
        
        long lastBorkTime = player.getLastBork();
        int cooldownHours = getCooldownHours(player);
        long cooldownMillis = cooldownHours * 60 * 60 * 1000L;
        
        return Utils.currentTimeMillis() - lastBorkTime > cooldownMillis;
    }
    
    /**
     * Gets the cooldown hours based on player's membership tier.
     */
    private static int getCooldownHours(Player player) {
        if (player.isDiamond()) return DIAMOND_COOLDOWN_HOURS;
        if (player.isPlatinum()) return PLATINUM_COOLDOWN_HOURS;
        if (player.isGold()) return GOLD_COOLDOWN_HOURS;
        if (player.isSilver()) return SILVER_COOLDOWN_HOURS;
        if (player.isBronze()) return BRONZE_COOLDOWN_HOURS;
        return DEFAULT_COOLDOWN_HOURS;
    }

    @Override
    public void start() {
        enterBorkInstance();
    }

    @Override
    public boolean processObjectClick1(WorldObject object) {
        if (object.getId() == EXIT_PORTAL_ID) {
            exitInstance(ExitType.NORMAL_EXIT);
            return false;
        }
        return true;
    }

    @Override
    public boolean processNPCClick1(NPC npc) {
        if (isInvulnerableSurokMagis(npc)) {
            player.getPackets().sendGameMessage("Your attack has no effect.");
            return false;
        }
        return true;
    }
    
    /**
     * Checks if the NPC is an invulnerable Surok Magis.
     */
    private boolean isInvulnerableSurokMagis(NPC npc) {
        return npc.getId() == SUROK_MAGIS_INITIAL || npc.getId() == SUROK_MAGIS_RETURN;
    }

    /**
     * Handles entering the Bork instance.
     */
    private void enterBorkInstance() {
        instance = new MapInstance(MAP_CHUNK_X, MAP_CHUNK_Y);
        player.lock();
        player.setNextAnimation(new Animation(TELEPORT_ANIMATION));
        
        final long fadeTime = FadingScreen.fade(player);
        instance.load(this::onInstanceLoaded);
    }
    
    /**
     * Called when the map instance has finished loading.
     */
    private void onInstanceLoaded() {
        final long fadeTime = System.currentTimeMillis(); // Get current time for fade
        FadingScreen.unfade(player, fadeTime, this::setupBorkEncounter);
    }
    
    /**
     * Sets up the Bork encounter after the instance loads.
     */
    private void setupBorkEncounter() {
        // Position player
        player.setNextWorldTile(instance.getTile(PLAYER_START_POSITION.getX(), PLAYER_START_POSITION.getY()));
        
        // Spawn NPCs
        spawnSurokMagis();
        spawnBork();
        
        // Start intro cutscene
        sendCutscene(INTRO_CUTSCENE, INTRO_CUTSCENE_DURATION);
        
        // Configure multi-combat area
        player.setForceMultiArea(true);
        bork.setForceMultiArea(true);
        surokMagis.setCantInteract(true);
    }
    
    /**
     * Spawns Surok Magis based on whether player has fought Bork before.
     */
    private void spawnSurokMagis() {
        int npcId = player.getLastBork() != 0 ? SUROK_MAGIS_RETURN : SUROK_MAGIS_INITIAL;
        WorldTile spawnTile = instance.getTile(SUROK_SPAWN_POSITION.getX(), SUROK_SPAWN_POSITION.getY());
        surokMagis = World.spawnNPC(npcId, spawnTile, -1, true, true);
    }
    
    /**
     * Spawns the Bork boss.
     */
    private void spawnBork() {
        WorldTile spawnTile = instance.getTile(BORK_SPAWN_POSITION.getX(), BORK_SPAWN_POSITION.getY());
        bork = new Bork(spawnTile, this);
    }

    /**
     * Handles leaving the Bork instance.
     */
    private void exitInstance(ExitType exitType) {
        player.setForceMultiArea(false);
        player.stopAll();
        
        switch (exitType) {
            case LOGOUT:
                player.setLocation(OUTSIDE_LOCATION);
                break;
            case TELEPORT_OR_DEATH:
                player.lock(3);
                resetPlayerState();
                break;
            case NORMAL_EXIT:
                player.useStairs(TELEPORT_ANIMATION, OUTSIDE_LOCATION, 2, 3);
                resetPlayerState();
                break;
        }
        
        if (exitType != ExitType.LOGOUT) {
            removeControler();
        }
        
        cleanupInstance();
    }
    
    /**
     * Resets player state when exiting.
     */
    private void resetPlayerState() {
        player.getMusicsManager().reset();
        if (earthquake) {
            player.getPackets().sendStopCameraShake();
        }
    }
    
    /**
     * Cleans up the map instance.
     */
    private void cleanupInstance() {
        if (instance != null) {
            instance.destroy(null);
        }
    }

    @Override
    public boolean logout() {
        exitInstance(ExitType.LOGOUT);
        return true;
    }

    @Override
    public boolean login() {
        player.setNextWorldTile(OUTSIDE_LOCATION);
        return true; // shouldn't happen
    }

    @Override
    public void magicTeleported(int type) {
        exitInstance(ExitType.TELEPORT_OR_DEATH);
    }

    @Override
    public boolean sendDeath() {
        player.lock(8);
        player.stopAll();
        
        WorldTasksManager.schedule(new DeathSequenceTask(), 0, 1);
        return false;
    }

    /**
     * Starts the combat phase of the encounter.
     */
    public void startFight() {
        player.unlock();
        bork.setCantInteract(false);
        surokMagis.setTarget(player);
    }

    /**
     * Initiates the earthquake sequence.
     */
    public void startEarthquake() {
        player.unlock();
        player.getPackets().sendGameMessage("Something is shaking the whole cavern! You should get out of here quick!");
        player.getPackets().sendCameraShake(3, 12, 25, 12, 25);
        earthquake = true;
        earthquakeTimer = EARTHQUAKE_DAMAGE_INTERVAL;
    }

    @Override
    public void process() {
        if (!earthquake) {
            return;
        }
        
        if (earthquakeTimer > 0) {
            earthquakeTimer--;
            return;
        }
        
        // Deal earthquake damage
        int damage = Utils.random(MAX_EARTHQUAKE_DAMAGE) + 1;
        player.applyHit(new Hit(player, damage, HitLook.REGULAR_DAMAGE));
        earthquakeTimer = EARTHQUAKE_DAMAGE_INTERVAL;
    }

    /**
     * Triggers the minion spawning sequence.
     */
    public void spawnMinions() {
        player.getPackets().sendGameMessage("Bork strikes the ground with his axe.");
        sendCutscene(MINION_CUTSCENE, MINION_CUTSCENE_DURATION);
    }

    /**
     * Handles Bork's death and cleanup.
     */
    public void killBork() {
        // Handle contract completion
        ContractHandler.checkContract(player, bork.getId(), bork);
        bork = null;
        
        // Start death cutscene
        sendCutscene(DEATH_CUTSCENE, DEATH_CUTSCENE_DURATION);
        
        // Update Surok Magis state
        surokMagis.setCantInteract(true);
        surokMagis.setNextFaceEntity(null);
        surokMagis.setNextWorldTile(instance.getTile(SUROK_SPAWN_POSITION.getX(), SUROK_SPAWN_POSITION.getY()));
        
        // Update player statistics
        updatePlayerProgress();
    }
    
    /**
     * Updates player progress after killing Bork.
     */
    private void updatePlayerProgress() {
        player.setLastBork(Utils.currentTimeMillis());
        int killCount = player.increaseKillStatistics("bork", true);
        String message = "You've killed the Bork! Total Bork kills: " + 
                        Colors.red + Utils.getFormattedNumber(killCount) + "</col>.";
        player.sendMessage(message);
    }

    /**
     * Sends a cutscene to the player.
     */
    public void sendCutscene(final int interfaceId, long duration) {
        player.lock();
        player.getMusicsManager().forcePlayMusic(BORK_MUSIC);
        player.getInterfaceManager().sendInterface(interfaceId);
        player.getPackets().sendBlackOut(2);
        
        CoresManager.fastExecutor.schedule(new CutsceneTask(interfaceId), duration);
    }
    
    /**
     * Handles cutscene completion logic.
     */
    private void handleCutsceneCompletion(int interfaceId) {
        player.getInterfaceManager().closeScreenInterface();
        
        switch (interfaceId) {
            case INTRO_CUTSCENE:
                handleIntroCutsceneEnd();
                break;
            case DEATH_CUTSCENE:
                handleDeathCutsceneEnd();
                break;
            case MINION_CUTSCENE:
                handleMinionCutsceneEnd();
                break;
        }
    }
    
    /**
     * Handles the end of the intro cutscene.
     */
    private void handleIntroCutsceneEnd() {
        player.resetReceivedDamage();
        player.getDialogueManager().startDialogue("SurokMagis", surokMagis.getId(), this);
        player.getPackets().sendBlackOut(0);
    }
    
    /**
     * Handles the end of the death cutscene.
     */
    private void handleDeathCutsceneEnd() {
        setupDeathCutsceneCamera();
        surokMagis.setNextFaceWorldTile(instance.getTile(22, 15));
        surokMagis.setNextForceTalk(new ForceTalk("Zamorak, avenge me!"));
        
        WorldTasksManager.schedule(new SurokTeleportTask(), 6, 3);
    }
    
    /**
     * Sets up camera for death cutscene.
     */
    private void setupDeathCutsceneCamera() {
        WorldTile lookTo = instance.getTile(22, 22);
        WorldTile posTile = instance.getTile(22, 15);
        
        player.getPackets().sendCameraLook(
            Cutscene.getX(player, lookTo.getX()), 
            Cutscene.getY(player, lookTo.getY()), 
            1000
        );
        player.getPackets().sendCameraPos(
            Cutscene.getX(player, posTile.getX()), 
            Cutscene.getY(player, posTile.getY()), 
            1500
        );
    }
    
    /**
     * Handles the end of the minion cutscene.
     */
    private void handleMinionCutsceneEnd() {
        bork.setMinions();
        player.unlock();
        player.getPackets().sendBlackOut(0);
    }
    
    // Inner classes for better organization
    
    /**
     * Enum for exit types to improve code clarity.
     */
    private enum ExitType {
        LOGOUT, TELEPORT_OR_DEATH, NORMAL_EXIT
    }
    
    /**
     * Task for handling death sequence.
     */
    private class DeathSequenceTask extends WorldTask {
        private int loop;

        @Override
        public void run() {
            switch (loop) {
                case 0:
                    player.setNextAnimation(player.getDeathAnimation());
                    break;
                case 1:
                    player.getPackets().sendGameMessage("Oh dear, you have died.");
                    break;
                case 3:
                    exitInstance(ExitType.TELEPORT_OR_DEATH);
                    player.getControlerManager().startControler("DeathEvent");
                    break;
                case 4:
                    player.getPackets().sendMusicEffect(MusicsManager.DEATH_MUSIC_EFFECT);
                    stop();
                    return;
            }
            loop++;
        }
    }
    
    /**
     * Task for handling cutscene completion.
     */
    private class CutsceneTask extends TimerTask {
        private final int interfaceId;
        
        public CutsceneTask(int interfaceId) {
            this.interfaceId = interfaceId;
        }
        
        @Override
        public void run() {
            try {
                handleCutsceneCompletion(interfaceId);
            } catch (Throwable e) {
                Logger.handle(e);
            }
        }
    }
    
    /**
     * Task for handling Surok Magis teleportation sequence.
     */
    private class SurokTeleportTask extends WorldTask {
        private boolean teleported;

        @Override
        public void run() {
            if (!teleported) {
                surokMagis.setNextAnimation(new Animation(SUROK_TELEPORT_ANIMATION));
                surokMagis.setNextGraphics(new Graphics(SUROK_TELEPORT_GRAPHICS));
                teleported = true;
            } else {
                surokMagis.finish();
                surokMagis = null;
                player.getDialogueManager().startDialogue("SurokMagisT", BorkController.this);
                player.getPackets().sendBlackOut(0);
                player.getPackets().sendResetCamera();
                stop();
            }
        }
    }
}