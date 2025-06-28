package com.rs.game.player.content.clans.citadels;

import java.io.Serializable;

import com.rs.cores.CoresManager;
import com.rs.game.Animation;
import com.rs.game.Graphics;
import com.rs.game.World;
import com.rs.game.WorldObject;
import com.rs.game.WorldTile;
import com.rs.game.player.Player;
import com.rs.game.player.actions.Woodcutting;
import com.rs.game.player.actions.Woodcutting.TreeDefinitions;
import com.rs.game.player.actions.mining.Mining;
import com.rs.game.player.actions.mining.Mining.RockDefinitions;
import com.rs.game.player.content.FadingScreen;
import com.rs.game.player.content.clans.Clan;
import com.rs.game.player.content.clans.ClansManager;
import com.rs.game.player.content.clans.citadels.actions.plots.Loom;
import com.rs.game.player.content.clans.citadels.actions.plots.Loom.LoomConstants;
import com.rs.game.player.content.clans.citadels.actions.plots.furnace.EmptyMould;
import com.rs.game.player.content.clans.citadels.actions.plots.furnace.EmptyMould.MouldConstants;
import com.rs.game.player.content.clans.citadels.actions.plots.furnace.Shoveling;
import com.rs.game.player.content.clans.citadels.actions.plots.furnace.Shoveling.ShovelConstants;
import com.rs.game.player.content.clans.citadels.actions.plots.furnace.WaterTrough;
import com.rs.game.player.content.clans.citadels.actions.plots.furnace.WaterTrough.TroughConstants;
import com.rs.utils.Logger;

/**
 * 
 * @author Frostbite
 *
 *<frostbitersps@gmail.com><skype@frostbitersps>
 */

public class CitadelObjects implements Serializable {


	private static final long serialVersionUID = -3835919658537174842L;
	
private Clan clan;
	
	public CitadelObjects(Clan clan) {
		this.clan = clan;
	}
	
	public void initalizeObjects(Player clan) {
		spawn(clan, 17927, 3, 125, 90);
		spawn(clan, 17927, 1, 128, 90);
	}

	public WorldTile getWorldTile(int mapX, int mapY) {
		return new WorldTile(clan.getClanCitadel().getBoundChuncks()[0] * 0 + mapX, clan.getClanCitadel().getBoundChuncks()[1] * 0 + mapY, 0);
	}
	
	public WorldTile getWorldTile(int mapX, int mapY, int mapH) {
		return new WorldTile(clan.getClanCitadel().getBoundChuncks()[0] * 0 + mapX, clan.getClanCitadel().getBoundChuncks()[1] * 0 + mapY, mapH);
	}
	
	public void spawn(Player player, int id, int rotation, int mapX, int mapY) {
		World.spawnObject(new WorldObject(id, 10, rotation, player.getClanManager().getClan().getClanCitadel().getBoundChuncks()[0] * 0 + mapX, player.getClanManager().getClan().getClanCitadel().getBoundChuncks()[1] * 0 + mapY, 0), true);
	}
	
	public void spawn(Player player, int id, int mapX, int mapY, int rotation, int mapH) {
		World.spawnObject(new WorldObject(id, 10, rotation, player.getClanManager().getClan().getClanCitadel().getBoundChuncks()[0] * 0 + mapX, player.getClanManager().getClan().getClanCitadel().getBoundChuncks()[1] * 0 + mapY, mapH), true);
	}
	
	public Clan getClan() {
		return clan;
	}
	
//	public static void handleObjectConfigs(final Player player, final WorldObject object, OutputStream stream) {
//		int hash = 0;
//		int slotFlag = -1;
//		if (object.getId() == 60068) {
//			ClansManager manager = player.getClanManager();
//			if (manager == null)
//				continue;
//			int[] colors = manager.getClan().getMottifColors();
//			ItemDefinitions defs = ItemDefinitions.getItemDefinitions(20709);
//			boolean modifyColor = !Arrays.equals(colors, defs.originalModelColors);
//			int bottom = manager.getClan().getMottifBottom();
//			int top = manager.getClan().getMottifTop();
//			if (bottom == 0 && top == 0 && !modifyColor)
//				continue;
//			hash |= 1 << slotFlag;
//			stream.writeByte((modifyColor ? 0x4 : 0) | (bottom != 0 || top != 0 ? 0x8 : 0));
//			if (modifyColor) {
//				int slots = 0 | 1 << 4 | 2 << 8 | 3 << 12;
//				stream.writeShort(slots);
//				for (int i = 0; i < 4; i++)
//					stream.writeShort(colors[i]);
//			}
//			if (bottom != 0 || top != 0) {
//				int slots = 0 | 1 << 4;
//				stream.writeByte(slots);
//				stream.writeShort(ClansManager.getMottifTexture(top));
//				stream.writeShort(ClansManager.getMottifTexture(bottom));
//			}
//		}
//		
//	}

	public static boolean handleOption1(final Player player, final WorldObject object) {
		player.faceObject(object);
		ClansManager manager = player.getClanManager();
			if(object.getId() == 59463 && object.getX() == 2958 && object.getY() == 3289) {
				if(manager == null) {
					player.getDialogueManager().startDialogue("SimpleMessage", "You must be in a clan to access the Clan Citadel.");
				}
				if(!(manager == null) && player.getClanManager().getClan().getClanCitadel().isCitadelGenerated() == true) {
					player.lock(2);
					player.getClanManager().getClan().getClanCitadel().enterClanCitadel(player);

				}
				if(!(manager == null) && player.getClanManager().getClan().getClanCitadel().isCitadelGenerated() == false) {
					final long time = FadingScreen.fade(player);
					player.setNextGraphics(new Graphics(536));
					player.lock(3);
					CoresManager.slowExecutor.execute(new Runnable() {
						@Override
						public void run() {
							try {
								FadingScreen.unfade(player, time, new Runnable() {
									@Override
									public void run() {
										player.getClanManager().getClan().getClanCitadel().enterClanCitadel(player);
									}
								});
							} catch (Throwable e) {
								Logger.handle(e);
							}
						}

					});
				}
			}

		/**
		 * Clan Objects
		 */
		/**
		 * Mining
		 */
		switch(object.getId()) {

		/**
		 * Mining
		 */
		case 24981:
		case 24982:
			player.getActionManager().setAction(new Mining(object, RockDefinitions.STONE_CLIPPINGS));
			break;

			/**
			 * Loom
			 */

		case 15335:
			if(player.getX() != object.getX() + 3 && player.getY() != object.getY() && object.getRotation() == 0) {
				player.getPackets().sendGameMessage("You must be infront of the loom in order to use it.");
				return false;
			}
			if(player.getX() != object.getX() - 3 && player.getY() != object.getY() &&  object.getRotation() == 2) {
				player.getPackets().sendGameMessage("You must be infront of the loom in order to use it.");
				return false;
			}
			player.getActionManager().setAction(new Loom(object, LoomConstants.STAGE_1));
			break;

		case 15338:
			if(player.getX() != object.getX() + 3 && player.getY() != object.getY() && object.getRotation() == 0) {
				player.getPackets().sendGameMessage("You must be infront of the loom in order to use it.");
				return false;
			}
			if(player.getX() != object.getX() - 3 && player.getY() != object.getY() &&  object.getRotation() == 2) {
				player.getPackets().sendGameMessage("You must be infront of the loom in order to use it.");
				return false;
			}
			player.getActionManager().setAction(new Loom(object, LoomConstants.STAGE_2));
			break;

		case 15339:
			if(player.getX() != object.getX() + 3 && player.getY() != object.getY() && object.getRotation() == 0) {
				player.getPackets().sendGameMessage("You must be infront of the loom in order to use it.");
				return false;
			}
			if(player.getX() != object.getX() - 3 && player.getY() != object.getY() &&  object.getRotation() == 2) {
				player.getPackets().sendGameMessage("You must be infront of the loom in order to use it.");
				return false;
			}
			player.getActionManager().setAction(new Loom(object, LoomConstants.STAGE_3));
			break;

		case 15340:
			if(player.getX() != object.getX() + 3 && player.getY() != object.getY() && object.getRotation() == 0) {
				player.getPackets().sendGameMessage("You must be infront of the loom in order to use it.");
				return false;
			}
			if(player.getX() != object.getX() - 3 && player.getY() != object.getY() &&  object.getRotation() == 2) {
				player.getPackets().sendGameMessage("You must be infront of the loom in order to use it.");
				return false;
			}
			player.getActionManager().setAction(new Loom(object, LoomConstants.STAGE_4));
			break;

			/**
			 * End of loom
			 */

			/**
			 * Furnace
			 */

		case 26450:
			if(player.getX() == object.getX() + 1 && player.getY() == object.getY() + 1 && object.getRotation() == 0) {
				player.getActionManager().setAction(new Shoveling(object, ShovelConstants.STAGE_1));
				player.faceObject(new WorldObject(26450, 10, 0, object.getX(), object.getY() + 1, 0));
			}
			if(player.getX() == object.getX() - 1 && player.getY() == object.getY() && object.getRotation() == 2) {
				player.getActionManager().setAction(new Shoveling(object, ShovelConstants.STAGE_1));
			}
			break;
			
		case 26343:
			if(player.getY() == object.getY() + 1) {
				player.getActionManager().setAction(new EmptyMould(object, MouldConstants.STAGE_1));
				player.faceObject(new WorldObject(MouldConstants.STAGE_1.getCurrentObject(), 10, 2, player.getX(), player.getY() - 1, 0));
			}
			break;
			
		case 26331:
			if(player.getY() == object.getY() + 1) {
				player.getActionManager().setAction(new WaterTrough(object, TroughConstants.GOLD_STAGE_3));
			}
			break;

		case 26443:
			player.faceObject(object);
			player.setNextAnimation(new Animation(6631));
			break;

			/**
			 * End of furnace
			 */

			/**
			 * Woodcutting
			 */
		case 18874:
		case 18883:
		case 18901:
		case 18907:
		case 18920:
		case 18921:
			player.getActionManager().setAction(new Woodcutting(object, TreeDefinitions.ROOT_CHIPPINGS));
			break;
		}
		return true;


	}

}
