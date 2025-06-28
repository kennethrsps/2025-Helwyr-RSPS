package com.rs.game.activites.goldrush;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.rs.cores.CoresManager;
import com.rs.game.World;
import com.rs.game.player.Player;
import com.rs.utils.Colors;
import com.rs.utils.Logger;
import com.rs.utils.Utils;

public class GRGame {

	public static double MULTIPLIER = 1.0;
	public static double BUST_MARK = 0;
	public static double LAST_BUST = 0;
	public static List<Player> PLAYERS = new ArrayList<Player>();
	public static boolean GAME_ACTIVE = false;
	public static long countdown = 7000;
	public static long tickTime;
	
	public static void tickGame() {
		if(!GAME_ACTIVE) {
			return;
		}
		/*if(getAmountStillPlaying() == 0) {
			GAME_ACTIVE = false;
			newGame();
			return;
		}*/
		if(tickTime < 100) {
			tickTime += getTimeIncrease() + (getTimeIncrease() / 2);
			return;
		}
		tickTime = 0;
		MULTIPLIER += (0.005 + Utils.random(0.001, 0.005));
		int intervals = (int)(MULTIPLIER / 10) * 2;
		for(int i=0;i<intervals;i++) {
			MULTIPLIER += (0.005 + Utils.random(0.001, 0.005));
		}
		if(MULTIPLIER >= BUST_MARK) {
			bust();
			GAME_ACTIVE = false;
			return;
		}
		updateMultiplier();
		checkCashingOutPlayers(false);
	}
	
	public static int getTimeIncrease() {
		double perc = (BUST_MARK / MULTIPLIER) * 100;
		return (int) perc;
	}
	
	public static void bust() {
		LAST_BUST = MULTIPLIER;
		checkCashingOutPlayers(true);
		newGame();
	}
	
	public static void checkCashingOutPlayers(boolean forceLose) {
		boolean changes = false;
		for(Player player : PLAYERS) {
			if((MULTIPLIER >= player.getGrManager().autoCashout && player.getGrManager().autoCashout != 0.0) && !player.getGrManager().cashedOut) {
				//player.errorMessage("this");
				player.getGrManager().cashout(forceLose ? false : true);
				changes = true;
			}
		}
		if(changes)
			updatePlayers();
	}
	
	public static void tickLobby() {
		if(!GAME_ACTIVE) {
			if(countdown > 0) {
				countdown -= 1000;
				for(Player player : getPlayersUsingInterface()) {
					player.getPackets().sendIComponentText(979, 75, Colors.red+"Busted @ x "+new DecimalFormat("#.###").format(LAST_BUST)+"<br>"+Colors.white+"New Game: "+(double) (countdown / 1000)+"s");
				}
			} else {
				for(Player player : PLAYERS) {
					if(World.getPlayer(player.getUsername()) != null)
						player.getPackets().sendIComponentText(979, 13, "Cash Out!");
				}
				BUST_MARK = getBustMark();
				System.err.println("bust mark: "+BUST_MARK);
				GAME_ACTIVE = true;
			}
			return;
		}
	}
	
	public static int getAmountStillPlaying() {
		int am = 0;
		for(Player player : PLAYERS) {
			if(!player.getGrManager().cashedOut) {
				am++;
			}
		}
		return am;
	}
	
	public static double[][] MARKS = {{0.1, 1}, {0.1, 1.3}, {0.3, 1.7}, {0.4, 1.95}, {0.4, 1.95}, {0.4, 1.95}, {0.4, 1.95}, {0.4, 1.95}, {0.5, 2.0}, {0.6, 2.1}, {0.7, 2.2}, {0.8, 2.3}, {1.0, 2.5}, {1.5, 2.0}, {1.5, 2.0}, {1.5, 2.0}, {1.5, 2.0}, {2.0, 3.5}, {3.0, 4.5}, {4.0, 5.5}, {5.0, 6.5}, {6.0, 7.5}, {7.0, 8.5}, {8.0, 9.5}, {9.0, 10.5}, {10.0, 12.0}, {12.0, 14.0}, {14.0, 16.0}, {16.0, 18.0}, {18.0, 20.0}, {20.0, 25.0}, {25.0, 50.0}, {50.0, 100.0}, {100.0, 162.0}};
		
	public static double getBustMark() {
		double[] mark = MARKS[Utils.random(MARKS.length)];
		double number = Utils.random(mark[0], mark[1]);
		if(number > 10) {
			if(!PLAYERS.isEmpty()) {
				number = Utils.random(8.5, 10.0);
			}
		}
		return number;
	}
	
	public static void updatePlayers() {
		String text = "";
		for(Object bet : getTopUncashed()) {
			String name = ((Map.Entry<String, Integer>) bet).getKey();
			Player player = World.getPlayer(name);
			text += Colors.green+""+Utils.formatString(name)+" - @"+player.getGrManager().betAmount+"k<br>";
		}
		for(Object bet : getTopCashed()) {
			String name = ((Map.Entry<String, Integer>) bet).getKey();
			Player player = World.getPlayer(name);
			text += Colors.red+""+Utils.formatString(name)+" x"+new DecimalFormat("#.###").format(player.getGrManager().cashoutMark)+" @"+player.getGrManager().betAmount+"k<br>";
		}
		for(Player player : getPlayersUsingInterface()) {
			player.getPackets().sendIComponentText(979, 70, text);
		}
	}
	
	public static void updateMultiplier() {
		for(Player player : getPlayersUsingInterface()) {
			player.getPackets().sendIComponentText(979, 75, Colors.green+"Increasing<br>"+Colors.white+"x "+ new DecimalFormat("#.###").format(MULTIPLIER));
		}
	}
	
	public static void updateStats() {
		for(Player player : getPlayersUsingInterface()) {
			player.getPackets().sendIComponentText(979, 96, "Players: "+PLAYERS.size());
			player.getPackets().sendIComponentText(979, 97, "Bets: "+getTotalBets()+"k");
		}
	}
	
	public static int getTotalBets() {
		int bets = 0;
		if(PLAYERS.isEmpty()) {
			return bets;
		}
		for(Player player : PLAYERS) {
			bets += player.getGrManager().betAmount;
		}
		return bets;
	}
	
	public static List<Player> getPlayersUsingInterface() {
		List<Player> players = new ArrayList<Player>();
		for(Player player : World.getPlayers()) {
			if(player.getInterfaceManager().containsInterface(979)) {
				players.add(player);
			}
		}
		return players;
	}
	
	public static void newGame() {
		MULTIPLIER = 1.0;
		GAME_ACTIVE = false;
		countdown = 7000;
		PLAYERS.clear();
	}
	
	@SuppressWarnings("unchecked")
	public static Object[] getTopUncashed() {
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		for(Player player : PLAYERS) {
			if(!player.getGrManager().cashedOut) {
				map.put(player.getUsername(), player.getGrManager().betAmount);
			}
		}
		Object[] a = map.entrySet().toArray();
		Arrays.sort(a, new Comparator() {
		    public int compare(Object o1, Object o2) {
		        return ((Map.Entry<String, Integer>) o2).getValue()
		                   .compareTo(((Map.Entry<String, Integer>) o1).getValue());
		    }
		});
		return a;
	}
	
	@SuppressWarnings("unchecked")
	public static Object[] getTopCashed() {
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		for(Player player : PLAYERS) {
			if(player.getGrManager().cashedOut) {
				map.put(player.getUsername(), player.getGrManager().betAmount);
			}
		}
		Object[] a = map.entrySet().toArray();
		Arrays.sort(a, new Comparator() {
		    public int compare(Object o1, Object o2) {
		        return ((Map.Entry<String, Integer>) o2).getValue()
		                   .compareTo(((Map.Entry<String, Integer>) o1).getValue());
		    }
		});
		return a;
	}
	
	
	public static void init() {
		Logger.log("[GoldRush]", "Initiated Gold Rush...");
		CoresManager.slowExecutor.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				try {
					updateStats();
					updatePlayers();
					tickLobby();
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, 1, 1, TimeUnit.SECONDS);
		CoresManager.slowExecutor.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				try {
					tickGame();
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, 25, 25, TimeUnit.MILLISECONDS);
	}
	
}
