package com.rs.game.player.controllers;

import java.util.HashMap;

import com.Tusken_raid.tuskenraid;
import com.rise_of_the_six.RiseOfTheSix;
import com.rs.game.activites.BrimhavenAgility;
import com.rs.game.activites.PuroPuro;
import com.rs.game.activites.ZombieOutpost.ZOControler;
import com.rs.game.activites.clanwars.FfaZone;
import com.rs.game.activites.clanwars.RequestController;
import com.rs.game.activites.clanwars.WarControler;
import com.rs.game.activites.creations.StealingCreationGame;
import com.rs.game.activites.creations.StealingCreationLobby;
import com.rs.game.activites.duel.DuelArena;
import com.rs.game.activites.duel.DuelControler;
import com.rs.game.activites.soulwars.AreaController;
import com.rs.game.activites.soulwars.GameController;
import com.rs.game.activites.soulwars.LobbyController;
import com.rs.game.player.content.ports.PlayerPortsController;
import com.rs.game.player.content.xmas.XmasController;
import com.rs.game.player.controllers.ArtisansWorkShopControler;
import com.rs.game.player.controllers.bossInstance.*;
import com.rs.game.player.controllers.castlewars.CastleWarsPlaying;
import com.rs.game.player.controllers.castlewars.CastleWarsWaiting;
import com.rs.game.player.controllers.fightpits.FightPitsArena;
import com.rs.game.player.controllers.fightpits.FightPitsLobby;
import com.rs.game.player.controllers.pestcontrol.PestControlGame;
import com.rs.game.player.controllers.pestcontrol.PestControlLobby;
import com.rs.game.player.controllers.zombie.ZombieControler;
import com.rs.game.player.controllers.zombie.ZombieLobbyControler;
import com.rs.utils.Logger;

public class ControllerHandler {

	private static final HashMap<Object, Class<Controller>> handledControlers = new HashMap<Object, Class<Controller>>();

	public static final Controller getControler(Object key) {
		if (key instanceof Controller)
			return (Controller) key;
		Class<Controller> classC = handledControlers.get(key);
		if (classC == null)
			return null;
		try {
			return classC.newInstance();
		} catch (Throwable e) {
			Logger.handle(e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static final void init() {
		try {
			Class<Controller> value1 = (Class<Controller>) Class.forName(Wilderness.class.getCanonicalName());
			handledControlers.put("Wilderness", value1);
			Class<Controller> value2 = (Class<Controller>) Class.forName(Kalaboss.class.getCanonicalName());
			handledControlers.put("Kalaboss", value2);
			Class<Controller> value4 = (Class<Controller>) Class.forName(GodWars.class.getCanonicalName());
			handledControlers.put("GodWars", value4);
			Class<Controller> value5 = (Class<Controller>) Class.forName(ZGDController.class.getCanonicalName());
			handledControlers.put("ZGDController", value5);
			Class<Controller> value9 = (Class<Controller>) Class.forName(DuelArena.class.getCanonicalName());
			handledControlers.put("DuelArena", value9);
			Class<Controller> value10 = (Class<Controller>) Class.forName(DuelControler.class.getCanonicalName());
			handledControlers.put("DuelControler", value10);
			Class<Controller> value11 = (Class<Controller>) Class.forName(CorpBeastController.class.getCanonicalName());
			handledControlers.put("CorpBeastController", value11);
			Class<Controller> value14 = (Class<Controller>) Class.forName(DTController.class.getCanonicalName());
			handledControlers.put("DTControler", value14);
			Class<Controller> value15 = (Class<Controller>) Class.forName(JailController.class.getCanonicalName());
			handledControlers.put("JailController", value15);
			Class<Controller> value17 = (Class<Controller>) Class.forName(CastleWarsPlaying.class.getCanonicalName());
			handledControlers.put("CastleWarsPlaying", value17);
			Class<Controller> value18 = (Class<Controller>) Class.forName(CastleWarsWaiting.class.getCanonicalName());
			handledControlers.put("CastleWarsWaiting", value18);
			handledControlers.put("clan_wars_request",
					(Class<Controller>) Class.forName(RequestController.class.getCanonicalName()));
			handledControlers.put("clan_war", (Class<Controller>) Class.forName(WarControler.class.getCanonicalName()));
			handledControlers.put("clan_wars_ffa", (Class<Controller>) Class.forName(FfaZone.class.getCanonicalName()));
			handledControlers.put("NomadsRequiem",
					(Class<Controller>) Class.forName(NomadsRequiem.class.getCanonicalName()));

			handledControlers.put("DungeonControler",
					(Class<Controller>) Class.forName(DungeonController.class.getCanonicalName()));
			handledControlers.put("BorkController",
					(Class<Controller>) Class.forName(BorkController.class.getCanonicalName()));
			handledControlers.put("BrimhavenAgility",
					(Class<Controller>) Class.forName(BrimhavenAgility.class.getCanonicalName()));
			handledControlers.put("FightCavesControler",
					(Class<Controller>) Class.forName(FightCaves.class.getCanonicalName()));
			handledControlers.put("ImpossibleJadControler",
					(Class<Controller>) Class.forName(ImpossibleJad.class.getCanonicalName()));
			handledControlers.put("FightKilnControler",
					(Class<Controller>) Class.forName(FightKiln.class.getCanonicalName()));
			handledControlers.put("FightPitsLobby",
					(Class<Controller>) Class.forName(FightPitsLobby.class.getCanonicalName()));
			handledControlers.put("FightPitsArena",
					(Class<Controller>) Class.forName(FightPitsArena.class.getCanonicalName()));
			handledControlers.put("PestControlGame",
					(Class<Controller>) Class.forName(PestControlGame.class.getCanonicalName()));
			handledControlers.put("PestControlLobby",
					(Class<Controller>) Class.forName(PestControlLobby.class.getCanonicalName()));
			handledControlers.put("Barrows", (Class<Controller>) Class.forName(Barrows.class.getCanonicalName()));

			handledControlers.put("Falconry", (Class<Controller>) Class.forName(Falconry.class.getCanonicalName()));
			handledControlers.put("QueenBlackDragonControler",
					(Class<Controller>) Class.forName(QueenBlackDragonController.class.getCanonicalName()));
			handledControlers.put("RuneSpanControler",
					(Class<Controller>) Class.forName(RunespanController.class.getCanonicalName()));
			handledControlers.put("DeathEvent", (Class<Controller>) Class.forName(DeathEvent.class.getCanonicalName()));
			handledControlers.put("SorceressGarden",
					(Class<Controller>) Class.forName(SorceressGarden.class.getCanonicalName()));
			handledControlers.put("CrucibleControler",
					(Class<Controller>) Class.forName(CrucibleController.class.getCanonicalName()));
			handledControlers.put("StealingCreationsGame",
					(Class<Controller>) Class.forName(StealingCreationGame.class.getCanonicalName()));
			handledControlers.put("StealingCreationsLobby",
					(Class<Controller>) Class.forName(StealingCreationLobby.class.getCanonicalName()));
			handledControlers.put("PyramidPlunder",
					(Class<Controller>) Class.forName(PyramidPlunder.class.getCanonicalName()));
			/*handledControlers.put("InstancedPVPControler",
					(Class<Controller>) Class.forName(InstancedPVPControler.class.getCanonicalName()));
*/
			handledControlers.put("Dungeoneering",
					(Class<Controller>) Class.forName(Dungeoneering.class.getCanonicalName()));

			handledControlers.put("PuroPuro", (Class<Controller>) Class.forName(PuroPuro.class.getCanonicalName()));
			handledControlers.put("WarriorsGuild",
					(Class<Controller>) Class.forName(WarriorsGuild.class.getCanonicalName()));
			handledControlers.put("XmasController",
					(Class<Controller>) Class.forName(XmasController.class.getCanonicalName()));
			handledControlers.put("AreaController",
					(Class<Controller>) Class.forName(AreaController.class.getCanonicalName()));
			handledControlers.put("GameController",
					(Class<Controller>) Class.forName(GameController.class.getCanonicalName()));
			handledControlers.put("LobbyController",
					(Class<Controller>) Class.forName(LobbyController.class.getCanonicalName()));
			handledControlers.put("HouseController",
					(Class<Controller>) Class.forName(HouseController.class.getCanonicalName()));
			handledControlers.put("BossInstanceController",
					(Class<Controller>) Class.forName(BossInstanceController.class.getCanonicalName()));
			handledControlers.put("CorporealBeastInstanceController",
					(Class<Controller>) Class.forName(CorporealBeastInstanceController.class.getCanonicalName()));
			handledControlers.put("DagannothKingsInstanceController",
					(Class<Controller>) Class.forName(DagannothKingsInstanceController.class.getCanonicalName()));
			handledControlers.put("KalphiteQueenInstanceController",
					(Class<Controller>) Class.forName(KalphiteQueenInstanceController.class.getCanonicalName()));
			handledControlers.put("AraxyteHyveController",
					(Class<Controller>) Class.forName(AraxyteHyveController.class.getCanonicalName()));
			
			
			handledControlers.put("RiseOfTheSix",
					(Class<Controller>) Class.forName(RiseOfTheSix.class.getCanonicalName()));
			handledControlers.put("tuskenraid",
					(Class<Controller>) Class.forName(tuskenraid.class.getCanonicalName()));
			/*
			 * handledControlers.put("VoragoController", (Class<Controller>)
			 * Class.forName(VoragoController.class.getCanonicalName()));
			 * handledControlers.put("VoragoLobbyController", (Class<Controller>)
			 * Class.forName(VoragoLobbyController.class.getCanonicalName()));
			 */
			handledControlers.put("PlayerPortsController",
					(Class<Controller>) Class.forName(PlayerPortsController.class.getCanonicalName()));
			handledControlers.put("DamageArea", (Class<Controller>) Class.forName(DamageArea.class.getCanonicalName()));
			handledControlers.put("ZombieControler",
					(Class<Controller>) Class.forName(ZombieControler.class.getCanonicalName()));
			handledControlers.put("ZombieLobbyControler",
					(Class<Controller>) Class.forName(ZombieLobbyControler.class.getCanonicalName()));
			handledControlers.put("ArtisansWorkShopControler",
					(Class<Controller>) Class.forName(ArtisansWorkShopControler.class.getCanonicalName()));
			handledControlers.put("PartyDemonController",
					(Class<Controller>) Class.forName(PartyDemonController.class.getCanonicalName()));
			handledControlers.put("VoragoInstanceController",
					(Class<Controller>) Class.forName(VoragoInstanceController.class.getCanonicalName()));
			handledControlers.put("ZOControler",
					(Class<Controller>) Class.forName(ZOControler.class.getCanonicalName()));
			handledControlers.put("CitadelControler",
					(Class<Controller>) Class.forName(CitadelControler.class.getCanonicalName()));
		} catch (Throwable e) {
			Logger.handle(e);
		}
	}

	public static final void reload() {
		handledControlers.clear();
		init();
	}
}