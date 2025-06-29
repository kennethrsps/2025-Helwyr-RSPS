package com.rs.game.player.content.interfaces.potionTimer;

import com.rs.game.player.Player;
import com.rs.utils.Utils;

/**
 * @author Era || Oct 9, 2021 
 *
 */
public class PotionTimersInter {
	
	 static int INTER = 3005;
	 static int arrowLeft = 1;
	 static int arrowRight = 6;
	 public static int[] slotArray = {12,18,24,30,36,42,48,54,60,66,72,78};
	 static boolean hasActiveTimer;
	 static int[] posX = {374,340,306,272,238,204,170,136,102,68,34,0};
	 public static int VENG = 0, OVL = 1, ANTIFIRE = 2, ANTIPOISON = 3, QUADXP = 4, RENEWAL = 5, AURA = 6, SPEC = 7;
    public static void process(Player player) {
        for(int cids : slotArray) {
            player.getPackets().sendHideIComponent(INTER, cids, true);
        }
        player.getPackets().sendHideIComponent(INTER, arrowRight, true);
        player.getPackets().sendHideIComponent(INTER, arrowLeft, true);
        int slotPosition = 0;
        int activeCount = 0;
        for(long timers : player.getPotiontimers().slotTimerArray) {
            if(timers > Utils.currentTimeMillis() && !player.getPotiontimers().hideSlots) {
                //player.sm("slotArray[activeCount]="+slotArray[activeCount]);
               // player.sm(" Utils.formatTimeLeft(slotTimerArray[activeCount])=" +Utils.formatTimeLeft(slotTimerArray[activeCount]));
                player.getPackets().sendHideIComponent(INTER, slotArray[activeCount],false);
                player.getPackets().sendMoveIComponent(INTER, slotArray[activeCount], posX[slotPosition]-5, 0);
                player.getPackets().sendIComponentText(INTER, slotArray[activeCount]+5, Utils.formatTimeLeft(player.getPotiontimers().slotTimerArray[activeCount]));
                player.getPackets().sendMoveIComponent(INTER, arrowLeft, posX[slotPosition]+13, 3);
                player.getPackets().sendMoveIComponent(INTER, 11, 30, 0);
                slotPosition ++;
            }
            if(timers == 999 && !player.getPotiontimers().hideSlots) {
        	   player.getPackets().sendHideIComponent(INTER, slotArray[activeCount],false);
               player.getPackets().sendMoveIComponent(INTER, slotArray[activeCount], posX[slotPosition]-5, 0);
               player.getPackets().sendIComponentText(INTER, slotArray[activeCount]+5, "Book");
               player.getPackets().sendMoveIComponent(INTER, arrowLeft, posX[slotPosition]+13, 3);
               player.getPackets().sendMoveIComponent(INTER, 11, 30, 0);
               slotPosition ++;
            }
            activeCount++;
        }
        for(long timers : player.getPotiontimers().slotTimerArray) {
            if(timers > Utils.currentTimeMillis() || timers == 999) {
                hasActiveTimer = true;
                break;
            }else {
                hasActiveTimer = false;
            }
        }
        if(hasActiveTimer && !player.getPotiontimers().hideSlots) {
            player.getPackets().sendHideIComponent(INTER, arrowLeft, false);
        }
        if(hasActiveTimer && player.getPotiontimers().hideSlots) {
            player.getPackets().sendHideIComponent(INTER, arrowRight, false);
        }
    }


    public static void handleButtons(Player player, int componentId, int slotId, int slotId2, int packetId) {
        if(componentId == arrowLeft) {
        	player.getPotiontimers().hideSlots = true;
        	process(player);
        }
        if(componentId == arrowRight) {
        	player.getPotiontimers().hideSlots = false;
        	process(player);
        }
    }
}

