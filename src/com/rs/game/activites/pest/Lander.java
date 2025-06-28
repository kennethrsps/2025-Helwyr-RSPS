package com.rs.game.activites.pest;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

import com.rs.Settings;
import com.rs.cores.CoresManager;
import com.rs.game.WorldTile;
import com.rs.game.activites.pest.PestControl.PestData;
import com.rs.game.player.Player;

public class Lander {

    public static enum LanderRequirement {

	NOVICE(0, 40, new WorldTile(2661, 2639, 0),
		new WorldTile(2657, 2639, 0)),

	INTERMEDIATE(1, 70, new WorldTile(2641, 2644, 0), new WorldTile(2644,
		2644, 0)),

	VETERAN(2, 100, new WorldTile(2635, 2653, 0), new WorldTile(2638, 2653,
		0));

	private static Map<Integer, LanderRequirement> landers = new HashMap<Integer, LanderRequirement>();

	static {
	    for (LanderRequirement lander : LanderRequirement.values())
		landers.put(lander.getId(), lander);
	}

	public static LanderRequirement forId(int id) {
	    return landers.get(id);
	}

	int id, requirement, reward;
	int[] pests;
	WorldTile tile, exit;

	private LanderRequirement(int id, int requirement, WorldTile tile,
		WorldTile exit) {
	    this.id = id;
	    this.requirement = requirement;
	    this.tile = tile;
	    this.exit = exit;
	}

	public WorldTile getExitTile() {
	    return exit;
	}

	public int getId() {
	    return id;
	}

	public int getRequirement() {
	    return requirement;
	}

	public WorldTile getWorldTile() {
	    return tile;
	}
    }

    public class LobbyTimer extends TimerTask {

	private int seconds = TIME;

	public int getMinutes() {
	    return seconds / 60;
	}

	@Override
	public void run() {
	    if (seconds == 0 && lobby.size() >= 5 || lobby.size() >= AUTO_GAME)
		passPlayersToGame();
	    else if (seconds == 0)
		seconds = TIME;
	    else if (lobby.size() == 0) {
		cancel();
		return;
	    }
	    seconds--;
	    if (seconds % 30 == 0)
		refreshLanderInterface();
	}
    }

    public static Lander[] landers = new Lander[3];

    private static final int AUTO_GAME = Settings.DEBUG ? 1 : 10;
    private static final int TIME = 150;

    public static boolean canEnter(Player player, int landerIndex) {
	Lander lander = landers[landerIndex];
	if (player.getSkills().getCombatLevelWithSummoning() < lander
		.getLanderRequierment().requirement) {
	    player.getDialogueManager().startDialogue(
		    "SimpleMessage",
		    "You need a combat level of "
			    + lander.getLanderRequierment().getRequirement()
			    + " or more to enter in boat.");
	    return false;
	} else if (player.getPet() != null || player.getFamiliar() != null) {
	    player.getPackets()
		    .sendGameMessage(
			    "You can't take a follower into the lander, there isn't enough room!");
	    return false;
	}
	lander.enterLander(player);
	return true;
    }

    public static Lander[] getLanders() {
	return landers;
    }

    private List<Player> lobby = Collections
	    .synchronizedList(new LinkedList<Player>());

    private LobbyTimer timer;

    private LanderRequirement landerRequirement;

    static {
	for (int i = 0; i < landers.length; i++)
	    landers[i] = new Lander(LanderRequirement.forId(i));
    }

    public Lander(LanderRequirement landerRequirement) {
	this.landerRequirement = landerRequirement;
    }

    public void add(Player player) {
	lobby.add(player);
	refreshLanderInterface();
    }

    public void enterLander(Player player) {
	if (lobby.size() == 0)
	    CoresManager.fastExecutor.schedule(timer = new LobbyTimer(), 1000,
		    1000);
	player.getControlerManager().startControler("PestControlLobby",
		landerRequirement.getId());
	add(player);
	player.useStairs(-1, landerRequirement.getWorldTile(), 1, 2,
		"You board the lander.");
    }

    public void exitLander(Player player) {
	player.useStairs(-1, landerRequirement.getExitTile(), 1, 2,
		"You leave the lander.");
	remove(player);
    }

    public LanderRequirement getLanderRequierment() {
	return landerRequirement;
    }

    public List<Player> getPlayers() {
	return lobby;
    }

    public LobbyTimer getTimer() {
	return timer;
    }

    private void passPlayersToGame() {
	final List<Player> playerList = new LinkedList<Player>();
	playerList.addAll(Collections.synchronizedList(lobby));
	lobby.clear();
	if (playerList.size() > AUTO_GAME) {
	    for (int index = AUTO_GAME; index < playerList.size(); index++) {
		Player player = playerList.get(index);
		if (player == null) {
		    playerList.remove(index);
		    continue;
		}
		player.getPackets().sendGameMessage(
			"You have received priority over other players.");
		playerList.remove(index);
		lobby.add(player);
	    }
	}
	new PestControl(playerList, PestData.valueOf(landerRequirement.name()))
		.create();
    }

    private void refreshLanderInterface() {
	for (Player teamPlayer : lobby)
	    teamPlayer.getControlerManager().getControler().sendInterfaces();
    }

    public void remove(Player player) {
	lobby.remove(player);
	refreshLanderInterface();
    }

    @Override
    public String toString() {
	return landerRequirement.name().toLowerCase();
    }
}
