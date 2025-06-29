package com.rs.game.player.content;

import java.io.IOException;

import com.rs.Settings;
import com.rs.cache.loaders.NPCDefinitions;
import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.player.Player;
import com.rs.utils.Colors;
import com.rs.utils.Utils;


/**
 * 
 * @author paolo
 *
 */
public class BossTimerManager {
    /**
     * variables
     */
	private Player player;
	private int currentIndexValue = 0;
	private long StartTime = 0;
	private long EndTime = 0;
    /**
     * constructor
     * @param player
     */
	public BossTimerManager(Player player) {
		this.player = player;
	}
    /**
     * sets the kill
     * @param npc
     * @return
     */
	public boolean checkNpc(NPC npc) {
		if (npc == null)
			return false;
		StartTime = 0;
		EndTime = 0;
		if (isBoss(npc)) {
			StartTime = Utils.currentTimeMillis();
			int id = npc.getId();
			currentIndexValue = getIndexValue(id);
		}
		return false;
	}
    /**
     * handles the time check
     * @param npc
     */
	public void recieveDeath(NPC npc) {
		/*if(player.getRights() == 2) //no admins xd xp
			return;*/
		if (isBoss(npc)) {
			EndTime = Utils.currentTimeMillis();
			long timeTaken = EndTime - StartTime;
			long oldTime = player.FastestTime[currentIndexValue];
			if (timeTaken < player.FastestTime[currentIndexValue] || player.FastestTime[currentIndexValue] == 0) {
				player.FastestTime[currentIndexValue] = timeTaken;
				player.sendMessage("New personal Best for " + Colors.red + npc.getName() + ".");
				player.sendMessage("Your new time to beat is " + Colors.red + Utils.formatTime(timeTaken) + ".");
				player.sendMessage("Your old time was " + Colors.red + Utils.formatTime(oldTime)+ ".");
			} else {
				player.sendMessage("You killed " + Colors.red + npc.getName() + "</col> in " +Colors.red +Utils.formatTime(timeTaken) + "</col> seconds.");
			}
			if (timeTaken < World.FastestTime[currentIndexValue] || World.FastestTime[currentIndexValue] == 0) {
				if (World.playerLeader[currentIndexValue] == null) {
					World.sendWorldMessage(Colors.green + "[BossTimer] </col>"+Colors.red+""+player.getUsername() + " </col> has set a new server record for killing " + Colors.red + npc.getName() + " </col> his time was " + Utils.formatTime(timeTaken), false);
					player.addMoney(5000000);
				} else {
					World.sendWorldMessage(Colors.green + "[BossTimer] </col>"+Colors.red+""+ player.getUsername() + "</col> has set a new server record for killing "+ Colors.red + npc.getName() + " </col>his time was " + Utils.formatTime(timeTaken) + ". He beat "+Colors.red + World.playerLeader[currentIndexValue] + "'s </col> time of "+Colors.red + Utils.formatTime(World.FastestTime[currentIndexValue]), false);
					player.addMoney(5000000);
				}
				World.FastestTime[currentIndexValue] = timeTaken;
				World.playerLeader[currentIndexValue] = player.getUsername();
				try {
					World.saveBossTimes();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
    /**
     * whenever you want to reset the timers call this
     */
	public static void setWorldIndexes(){
		for(int i = 0; i < Settings.BOSS_IDS.length; i ++){
		World.FastestTime[i] = 1000000000;
		World.playerLeader[i] = "Not killed yet.";
		}
	}
	/**
	 * gets the index of the boss
	 * @param id
	 * @return
	 */
	private int getIndexValue(int id) {
		int retVal = 0;
		for (int i = 1; i < Settings.BOSS_IDS.length; ++i) {
			if (id == Settings.BOSS_IDS[i])
				return i;
		}
		return retVal;
	}
    /**
     * checks if its a boss
     * @param npc
     * @return
     */
	public boolean isBoss(NPC npc) {
		for (int i = 0; i < Settings.BOSS_IDS.length; ++i) {
			if (npc.getId() == Settings.BOSS_IDS[i]) {
				return true;
			}
		}
		return false;
	}
	/**
	 * sends the interface
	 * @param player
	 */
	public static void sendInterface(Player player, int page){
		player.getInterfaceManager().sendInterface(3024);
		player.getPackets().sendIComponentText(3024, 4, "      Boss name");
		player.getPackets().sendIComponentText(3024, 5, " Record holder");
		player.getPackets().sendIComponentText(3024, 6, "   Time");
		player.getPackets().sendIComponentText(3024, 7, " Your time");
		NPCDefinitions def;
		int mult = 8;
		int count = 0 + mult * page;
		
		for(int i = 9 ;i <= 38; i+=4){
			
			def = NPCDefinitions.getNPCDefinitions(Settings.BOSS_IDS[count]);
			player.getPackets().sendIComponentText(3024, i-1, ""+def.getName());
			player.getPackets().sendIComponentText(3024, i, ""+World.playerLeader[count]);
			player.getPackets().sendIComponentText(3024, i+1, ""+(World.FastestTime[count] == 1000000000 ? "/" : Utils.formatTime(World.FastestTime[count])));
			player.getPackets().sendIComponentText(3024, i+2, ""+Utils.formatTime(player.FastestTime[count]));
			if((count < Settings.BOSS_IDS.length -1))
			count++;
		}
	}
}
