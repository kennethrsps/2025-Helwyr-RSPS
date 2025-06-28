package com.rs.game.player.content;

import java.io.Serializable;

import com.rs.game.WorldTile;
import com.rs.game.player.Player;
/**
 * 
 * @author paolo
 *
 */
public class TeleportSystem  implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8384677836033599518L;
	
	public static final int INTERFACE_ID = 3056;
	/**
	 * here can you add the locations
	 *
	 */
	public enum Teleports{
		
		Agility(new TeleportLocation[] {
			new TeleportLocation("Gnome agility",new WorldTile(2468,3437,0)),
			new TeleportLocation("Barbarian agility",new WorldTile(2552,3557,0)),
			new TeleportLocation("Wildernes agility",new WorldTile(2998,3933,0)),
			new TeleportLocation("Pyramid agility",new WorldTile(3358, 2828, 0))
		}),
		Fishing(new TeleportLocation[] {
				new TeleportLocation("Low level",new WorldTile(3087,3232,0)),
				new TeleportLocation("Catherby",new WorldTile(2841,3432,0)),
				new TeleportLocation("Fishing guild",new WorldTile(2591,3420,0)),
				new TeleportLocation("Living rock caverns",new WorldTile(3655,5114,0))
			}),
		Hunter(new TeleportLocation[] {
				new TeleportLocation("Fieldip Hills",new WorldTile(2526, 2916, 0)),
				new TeleportLocation("Falconry",new WorldTile(2362, 3623, 0))
			}),
		Farming(new TeleportLocation[] {
				new TeleportLocation("Farm Site",new WorldTile(3052, 3304, 0))
			}),
		Fletching(new TeleportLocation[] {
				new TeleportLocation("Fletching Shop",new WorldTile(4304, 873, 0)),
				new TeleportLocation("Woodcuting Area",new WorldTile(2726, 3477, 0))
			}),
		Runecrafting(new TeleportLocation[] {
							
				new TeleportLocation("Classic Altar",new WorldTile(2598, 3157, 0))
			}),
		Smithing(new TeleportLocation[] {
				new TeleportLocation("Edgeville",new WorldTile(3107,3499,0))
			}),
		Summoning(new TeleportLocation[] {
				new TeleportLocation("Summoning area",new WorldTile(2923, 3449, 0)),
			}),
		Thieving(new TeleportLocation[] {
				new TeleportLocation("Home stalls",new WorldTile(4307,862,0))
			}),
		Woodcutting(new TeleportLocation[] {
				new TeleportLocation("Jungle",new WorldTile(2817, 3083, 0)),
				new TeleportLocation("Seer's Village",new WorldTile(2726, 3477, 0))
			}),
		Firemaking(new TeleportLocation[] {
				new TeleportLocation("Home Fire",new WorldTile(4314,869,0)),
			}),
		Dungeoneering(new TeleportLocation[] {
				new TeleportLocation("RS Dungeoneering",new WorldTile(3449, 3727, 0)),
				new TeleportLocation("Custom Dungeon",new WorldTile(3972, 5561, 0))
			}),
		Crafting(new TeleportLocation[] {
				new TeleportLocation("Home Crafting shop",new WorldTile(4305,872,0))
			}),
		Cooking(new TeleportLocation[] {
				new TeleportLocation("Home Fireplace",new WorldTile(4314,869,0))
			}),
		Construction(new TeleportLocation[] {
				new TeleportLocation("House Portal",new WorldTile(4326,865,0))
			}),
		Combat(new TeleportLocation[] {
				new TeleportLocation("Rocktails",new WorldTile(2710, 3710, 0)),
				new TeleportLocation("Dwarf Battlefield",new WorldTile(1519, 4704, 0))
			}),
		Prayer(new TeleportLocation[] {
				new TeleportLocation("Altar",new WorldTile(4316,853,0))
			}),
		Mining(new TeleportLocation[] {
				new TeleportLocation("Starter",new WorldTile(3300,3304,0)),
				new TeleportLocation("Granite mine",new WorldTile(3170,2913,0)),
				new TeleportLocation("Lrc",new WorldTile(3655,5114,0)),
				new TeleportLocation("Red Sandstones",new WorldTile(2590, 2880, 0)),
				new TeleportLocation("Pure Essence",new WorldTile(2932, 4821, 0))
			});
		
		
		private TeleportLocation locations[];
		
		Teleports(TeleportLocation[] loc){
			this.setLocations(loc);
		}

		/**
		 * @return the locations
		 */
		public TeleportLocation[] getLocations() {
			return locations;
		}

		/**
		 * @param locations the locations to set
		 */
		public void setLocations(TeleportLocation locations[]) {
			this.locations = locations;
		}
		
	}
	
	public static void handleButtons(int buttonId,int packetId, Player player){
		if(buttonId == 27){
			sendTeleports(Teleports.Agility,player);
		} else if(buttonId == 61){
			sendTeleports(Teleports.Mining,player);
		} else if(buttonId == 34){
			sendTeleports(Teleports.Combat,player);
		}else if(buttonId == 37){
			sendTeleports(Teleports.Construction,player);
		}else if(buttonId == 40){
			sendTeleports(Teleports.Cooking,player);
		}else if(buttonId == 43){
			sendTeleports(Teleports.Crafting,player);
		}else if(buttonId == 46){
			sendTeleports(Teleports.Dungeoneering,player);
		} else if(buttonId == 49){
			sendTeleports(Teleports.Farming,player);
		}else if(buttonId == 52){
			sendTeleports(Teleports.Firemaking,player);
		}else if(buttonId == 55){
			sendTeleports(Teleports.Fishing,player);
		}else if(buttonId == 58){
			sendTeleports(Teleports.Hunter,player);
		}else if(buttonId == 64){
			sendTeleports(Teleports.Prayer,player);
		}else if(buttonId == 67){
			sendTeleports(Teleports.Runecrafting,player);
		}else if(buttonId == 70){
			sendTeleports(Teleports.Smithing,player);
		}else if(buttonId == 73){
			sendTeleports(Teleports.Summoning,player);
		}else if(buttonId == 76){
			sendTeleports(Teleports.Thieving,player);
		}else if(buttonId == 79){
			sendTeleports(Teleports.Woodcutting,player);
		}else if(buttonId == 82){
			sendTeleports(Teleports.Fletching,player);
		}
				
		else if(buttonId >= 84 && buttonId <= 90){
			if(packetId == 14)
				selectTeleport(player, buttonId);
			else if(packetId == 67)
				addToFavorite(player,player.lastTeleport, buttonId);
		} else if( buttonId == 95){
			teleport(player,player.lastTeleport);
		} else if(buttonId >= 115 && buttonId <= 119){
			if(packetId == 14)
				teleport(player,player.favorite_teleport.get(-115 + buttonId));
			else if(packetId == 67)
				removeFavorite(player, buttonId);
		}
	}
	
	private static void removeFavorite(Player player, int buttonId) {
		player.favorite_teleport.remove(-115 + buttonId);
		player.sm("Succesfully deleted from your favorite list.");
		for(int i = 115; i <= 117; i ++)
			player.getPackets().sendIComponentText(3056, i,"");
		sendFavorite(player);
		
	}

	private static void addToFavorite(Player player, TeleportLocation lastTeleport, int buttonId) {
		if(player.favorite_teleport.size() >=3){
			player.sm("Your favorite list is full, remove a favorite from your list.");
			return;
		}
		player.favorite_teleport.add(player.clickTeleport.locations[-84 + buttonId]);
		player.sm("Succesfully added to your favorite list.");
		for(int i = 115; i <= 117; i ++)
			player.getPackets().sendIComponentText(3056, i, "");
		sendFavorite(player);
		
	}

	public static void sendTeleports(Teleports tele, Player player){
		cleanTeleports(player);
		player.clickTeleport = tele;
		int counter = 84;
		for(TeleportLocation loc : tele.getLocations()){
			player.getPackets().sendIComponentText(3056, counter, counter - 83+"."+loc.getName());
			counter++;
		}
	}
	
	public static void sendFavorite(Player player){
		int counter = 115;
		for(TeleportLocation loc : player.favorite_teleport){
			if(loc == null)
				continue;
			player.getPackets().sendIComponentText(INTERFACE_ID, counter, loc.getName());
			counter++;
		}
	}
	public static void sendPrevious(Player player){
		if(player.lastTeleport != null)
			player.getPackets().sendIComponentText(INTERFACE_ID, 95, player.lastTeleport.getName());
	}
	
	public static void cleanTeleports(Player player){
		for(int i = 84; i <= 90 ;i ++){
			player.getPackets().sendIComponentText(3056, i, "");
		}
		for(int i = 115; i <= 117; i ++)
			player.getPackets().sendIComponentText(3056, i, "");
		player.getPackets().sendIComponentText(3056, 95, "");
	}
	/**
	 * teleports the player
	 * @param player
	 * @param Tele
	 */
	public static void teleport(Player player, TeleportLocation tele){
		if(player.isLocked())
			return;
		if(tele == null) //shouldn't happen
			return;
		player.lastTeleport = tele;
		Magic.sendNormalTeleportSpell(player, 0, 0, tele.getLocation());
         
	}
	
	public static void selectTeleport(Player player,  int compId){
		teleport(player,player.clickTeleport.locations[-(84 - compId)]);
	}
	/**
	 * sends the interface
	 * @param player
	 */
	public static void sendInterface(Player player){
		player.getInterfaceManager().sendInterface(INTERFACE_ID);
		player.getPackets().sendIComponentText(3056, 92, "Helwyr");
		cleanTeleports(player);
		sendFavorite(player);
		sendPrevious(player);
		
	}
	/**
	 * small class represnting a location
	 * @author paolo
	 *
	 */
	public static class TeleportLocation implements Serializable{
		
		/**
		 * 
		 */
		private static final long serialVersionUID = -3813121369422774691L;
		
		private String name;
		private WorldTile location;
		
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public WorldTile getLocation() {
			return location;
		}

		public void setLocation(WorldTile location) {
			this.location = location;
		}

		public TeleportLocation(String name, WorldTile location) {
			this.name = name;
			this.location = location;
		}
		
		
	}

}


