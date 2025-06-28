package com.rs.game.activites;

import java.util.concurrent.TimeUnit;

import com.rs.cores.CoresManager;
import com.rs.game.World;
import com.rs.game.WorldTile;
import com.rs.game.item.Item;
import com.rs.utils.Utils;

/**
 * Handles the extra Easter Event.
 * @author Zeus
 */
public class EasterEvent {

    public static int[] EGG = { 7928, 7929, 7930, 7931, 7932, 7933 };

    public static String droppedArea;

    public static int getX, getY, spawnedEgg;

    /**
     * We init the auto-event.
     */
    public static void initEvent() {
		CoresManager.slowExecutor.scheduleWithFixedDelay(new Runnable() {
		    @Override
		    public void run() {
				//if (World.getPlayers().size() <= 3)
				//    return;
				int randomPlaces = Utils.random(21);
				if (randomPlaces == 20) {
				    setDroppedArea("Monastery");
				    getX = 3049;
				    getY = 3498;
				} else if (randomPlaces == 19) {
				    setDroppedArea("Barbarian Village");
				    getX = 3089;
				    getY = 3408;
				} else if (randomPlaces == 18) {
				    setDroppedArea("Draynor Manor");
				    getX = 3109;
				    getY = 3340;
				} else if (randomPlaces == 17) {
				    setDroppedArea("Wizards' Tower");
				    getX = 3117;
				    getY = 3169;
				} else if (randomPlaces == 16) {
				    setDroppedArea("Clan camp");
				    getX = 2970;
				    getY = 3274;
				} else if (randomPlaces == 15) {
				    setDroppedArea("Champions' Guild");
				    getX = 3208;
				    getY = 3355;
				} else if (randomPlaces == 14) {
				    setDroppedArea("Seers' Village");
				    getX = 2697;
				    getY = 3470;
				} else if (randomPlaces == 13) {
				    setDroppedArea("Camelot Castle");
				    getX = 2761;
				    getY = 3488;
				} else if (randomPlaces == 12) {
				    setDroppedArea("Taverly");
				    getX = 2931;
				    getY = 3461;
				} else if (randomPlaces == 11) {
				    setDroppedArea("Cooking Guild");
				    getX = 3142;
				    getY = 3441;
				} else if (randomPlaces == 10) {
				    setDroppedArea("Grand Exchange");
				    getX = 3156;
				    getY = 3502;
				} else if (randomPlaces == 9) {
				    setDroppedArea("Draynor Village");
				    getX = 3081;
				    getY = 3250;
				} else if (randomPlaces == 8) {
				    setDroppedArea("Port Sarim Bar");
				    getX = 3053;
				    getY = 3257;
				} else if (randomPlaces == 7) {
				    setDroppedArea("Al Kharid");
				    getX = 3284;
				    getY = 3183;
				} else if (randomPlaces == 6) {
				    setDroppedArea("Burthorpe");
				    getX = 2901;
				    getY = 3523;
				} else if (randomPlaces == 5) {
				    setDroppedArea("Bandits camp");
				    getX = 3174;
				    getY = 2980;
				} else if (randomPlaces == 4) {
				    setDroppedArea("Falador park");
				    getX = 2991;
				    getY = 3384;
				} else if (randomPlaces == 3) {
				    setDroppedArea("Lumbridge kitchen");
				    getX = 3207;
				    getY = 3214;
				} else if (randomPlaces == 2) {
				    setDroppedArea("Varrock east bank");
				    getX = 3256;
				    getY = 3425;
				} else if (randomPlaces == 1) {
				    setDroppedArea("Body altar");
				    getX = 3053;
				    getY = 3440;
				} else if (randomPlaces == 0) {
				    setDroppedArea("Ice mountain");
				    getX = 3009;
				    getY = 3500;
				}
				spawnedEgg = EGG[Utils.random(EGG.length)];
				World.sendWorldMessage("<img=7><col=E0246F>Easter Event: An egg has been spotted at "
						+ getDroppedArea() + ".", false);
				World.addGroundItem(new Item(spawnedEgg, 1), getSpawn());
			}
		}, Utils.random(1700, 1900), Utils.random(3500, 3700), TimeUnit.SECONDS);
    }

    public static String getDroppedArea() {
    	return droppedArea;
    }

    /**
     * Gets the randomized spawn tile.
     * @return The Worldtile.
     */
    public static WorldTile getSpawn() {
    	WorldTile centerTile = new WorldTile(getX, getY, 0);
    	for (int trycount = 0; trycount < 10; trycount++) {
    		centerTile = new WorldTile(centerTile, 15);
		    if (World.canMoveNPC(centerTile.getPlane(), centerTile.getX(), centerTile.getY(), 1))
		    	break;
		}
    	return centerTile;
    }

    public static void setDroppedArea(String droppedLocation) {
    	droppedArea = droppedLocation;
    }
}