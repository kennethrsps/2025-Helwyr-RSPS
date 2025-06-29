package com.rs.game.player;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;

import com.rs.cache.loaders.ClientScriptMap;
import com.rs.cache.loaders.ItemDefinitions;
import com.rs.cache.loaders.NPCDefinitions;
import com.rs.game.World;
import com.rs.game.item.Item;
import com.rs.game.player.content.CosmeticsHandler;
import com.rs.game.player.content.TaskTab;
import com.rs.game.player.content.clans.ClansManager;
import com.rs.game.player.controllers.DungeonController;
import com.rs.stream.OutputStream;
import com.rs.utils.Colors;
import com.rs.utils.Utils;

public class GlobalPlayerUpdater implements Serializable {

	private static final long serialVersionUID = 7655608569741626586L;

	private transient int renderEmote;
	private int title;
	private int[] lookI;
	private int[] bodyStyle;
	private byte[] colour;
	private boolean male;
	private transient boolean glowRed;
	private transient byte[] appeareanceData;
	private transient byte[] md5AppeareanceDataHash;
	private transient short transformedNpcId;
	private transient boolean hidePlayer;

	/**
	 * The cosmetic items
	 */
	public Item[] cosmeticItems;

	private transient Player player;

	public GlobalPlayerUpdater() {
		male = true;
		renderEmote = -1;
		title = -1;
		resetAppearence();
	}

	public void copyColors(short[] colors) {
		for (byte i = 0; i < this.colour.length; i = (byte) (i + 1))
			if (colors[i] != -1)
				this.colour[i] = (byte) colors[i];
	}

	public void female() {
		lookI[0] = 48; // Hair
		lookI[1] = 1000; // Beard
		lookI[2] = 57; // Torso
		lookI[3] = 65; // Arms
		lookI[4] = 68; // Bracelets
		lookI[5] = 77; // Legs
		lookI[6] = 80; // Shoes
		colour[2] = 16;
		colour[1] = 16;
		colour[0] = 3;
		male = false;
	}

	public void generateAppearenceData() {
		OutputStream stream = new OutputStream(1);
		int flag = 0;
		if (!male)
			flag |= 0x1;
		if (transformedNpcId >= 0)
			flag |= 0x2;
		if (title != 0)
			flag |= title >= 32 && title <= 37 || title == 999 || title == 40 || title == 43 || title == 45
					|| title == 47 || title == 48 || title == 49 || title == 51 || title == 53 || title == 55
					|| title == 56 || title >= 58 && title <= 63 || title == 65 || title == 72 || title == 73
					|| title == 81 || title == 83 || title == 84 || title == 88 || title >= 1500 ? 0x80 : 0x40; // after/before
		stream.writeByte(flag);
		if (title != 0) {
			String titleName = title == 25 ? "<col=c12006>Yt'Haar </col>"

					: title == 1000 ? Colors.gray + (isMale() ? "Ironman" : "Ironwoman") + "</col> "
							: title == 1500 ? Colors.gray + (isMale() ? " the Ironman" : " the Ironwoman") + "</col>"

									: title == 1001 ? Colors.darkRed + (isMale() ? "HC Ironman" : "HC Ironwoman")
											+ "</col> "
											: title == 1501 ? Colors.darkRed + (isMale() ? " the HC Ironman"
													: " the HC Ironwoman") + "</col>"

													: title == 1502 ? Colors.darkRed + " the Survivor</col>"

															: title == 1002 ? Colors.darkRed + "Easy</col> "
																	: title == 1503 ? Colors.darkRed + " the Easy</col>"

																			: title == 1003 ? Colors.darkRed
																					+ "Intermediate</col> "
																					: title == 1504 ? Colors.darkRed
																							+ " the Intermediate</col>"

																							: title == 1004
																									? Colors.darkRed
																											+ "Veteran</col> "
																									: title == 1505
																											? Colors.darkRed
																													+ " the Veteran</col>"

																											: title == 1033
																													? Colors.darkRed
																															+ "Expert</col> "
																													: title == 1518
																															? Colors.darkRed
																																	+ " the Expert</col>"

																															: title == 1506
																																	? Colors.brown
																																			+ " the Wishful</col>"
																																	: title == 1507
																																			? Colors.brown
																																					+ " the Generous</col>"
																																			: title == 1508
																																					? Colors.brown
																																							+ " the Millionaire</col>"
																																					: title == 1509
																																							? Colors.gold
																																									+ " the Charitable</col>"
																																							: title == 1510
																																									? Colors.orange
																																											+ " the Billionaire</col>"

																																									: title == 1514
																																											? Colors.green
																																													+ "<shad=000000> of Seasons</col></shad>"

																																											: title == 1515
																																													? Colors.green
																																															+ "<shad=000000> of Guthix</col></shad>"

																																													// Prifddinas
																																													// Titles
																																													: title == 1511
																																															? "<col=FF08A0> of the Trahaearn"
																																															: title == 1516
																																																	? "<col=964F03> of the Hefin"

																																																	// reaper
																																																	// titles
																																																	: title == 1512
																																																			? "<col=DF0101> the Reaper"
																																																			: title == 1513
																																																					? "<col=DF0101><shad=9D1309> the Insane Reaper"
																																																					: title == 1014
																																																							? "<col=DF0101>Final Boss</col> "
																																																							: title == 1015
																																																									? "<col=DF0101><shad=9D1309>Insane Final Boss</col></shad> "

																																																									// Member
																																																									// rank
																																																									// titles
																																																									: title == 1016
																																																											? "<col=B56A02>Bronze member</col> "
																																																											: title == 1017
																																																													? "<col=A3A3A3>Silver member</col> "
																																																													: title == 1018
																																																															? "<col=D6D600>Gold member</col> "
																																																															: title == 1019
																																																																	? "<col=41917B>Platinum member</col> "
																																																																	: title == 1020
																																																																			? "<col=13D6D6>Diamond member</col> "

																																																																			// Special
																																																																			// titles
																																																																			: title == 1021
																																																																					? "<col=FC0000>No-lifer</col> " // Online
																																																																													// time
																																																																													// above
																																																																													// 250h
																																																																					: title == 1022
																																																																							? "<col=977EBF>Enthusiast</col> " // Voted
																																																																																// at
																																																																																// least
																																																																																// 250
																																																																																// times
																																																																							: title == 1023
																																																																									? "<col=D966AF>The Maxed</col> " // Has
																																																																																		// claimed
																																																																																		// max
																																																																																		// cape
																																																																									: title == 1024
																																																																											? "<col=A200FF>The Completionist</col> " // Has
																																																																																						// claimed
																																																																																						// comp
																																																																																						// cape
																																																																											: title == 1025
																																																																													? "<col=6200FF>The Perfectionist</col> " // Has
																																																																																								// claimed
																																																																																								// trimemd
																																																																																								// comp
																																																																																								// cape

																																																																													// Player-owned-port
																																																																													// titles
																																																																													: title == 1517
																																																																															? Colors.darkRed
																																																																																	+ " the Cabin "
																																																																																	+ (isMale()
																																																																																			? "Boy"
																																																																																			: "Girl") // 1
																																																																																						// point
																																																																															: title == 1026
																																																																																	? Colors.darkRed
																																																																																			+ "Bo'sun</col> " // 400
																																																																																								// point
																																																																																	: title == 1027
																																																																																			? Colors.darkRed
																																																																																					+ "First Mate</col> " // 800
																																																																																											// point
																																																																																			: title == 1028
																																																																																					? Colors.darkRed
																																																																																							+ "Cap'n</col> " // 1200
																																																																																												// point
																																																																																					: title == 1029
																																																																																							? Colors.darkRed
																																																																																									+ "Commodore</col> " // 1600
																																																																																															// point
																																																																																							: title == 1030
																																																																																									? Colors.darkRed
																																																																																											+ "Admiral</col> " // 2000
																																																																																																// point
																																																																																									: title == 1031
																																																																																											? Colors.darkRed
																																																																																													+ "Admiral of the Fleet</col> " // 3500
																																																																																																					// point
																																																																																											: title == 1032
																																																																																													? Colors.darkRed
																																																																																															+ "Portmaster</col> " // 4500
																																																																																																					// point

																																																																																													// Christmas
																																																																																													// titles
																																																																																													: title == 2001
																																																																																															? Colors.red
																																																																																																	+ " of</col>"
																																																																																																	+ Colors.green
																																																																																																	+ " Christmas</col>"
																																																																																															: title == 2002
																																																																																																	? Colors.rcyan
																																																																																																			+ " of Winter</col>"
																																																																																																	: title == 2003
																																																																																																			? Colors.green
																																																																																																					+ " the Grinch</col>"
																																																																																																			: title == 2004
																																																																																																					? Colors.cyan
																																																																																																							+ " Frostweb</col>"
																																																																																																					:

																																																																																																					ClientScriptMap
																																																																																																							.getMap(male
																																																																																																									? 1093
																																																																																																									: 3872)
																																																																																																							.getStringValue(
																																																																																																									title);
																																																																																	stream.writeVersionedString(titleName);
		}
		stream.writeByte(player.hasSkull() ? player.getSkullId() : -1); // pk
																		// icon
		stream.writeByte(player.getPrayer().getPrayerHeadIcon()); // prayer icon
		stream.writeByte(hidePlayer ? 1 : 0);
		stream.writeBytes(getAppearenceLook());
		stream.writeString(player.getDisplayName());
		boolean pvpArea = World.isPvpArea(player);
		stream.writeByte(
				pvpArea ? player.getSkills().getCombatLevel() : player.getSkills().getCombatLevelWithSummoning());
		stream.writeByte(pvpArea ? player.getSkills().getCombatLevelWithSummoning() : 0);
		stream.writeByte(-1); // higher level acc name appears in front :P
		stream.writeByte(transformedNpcId >= 0 ? 1 : 0); // to end here else id
		// need to send more
		// data
		if (transformedNpcId >= 0) {
			NPCDefinitions defs = NPCDefinitions.getNPCDefinitions(transformedNpcId);
			stream.writeShort(defs.anInt3029);
			stream.writeShort(defs.anInt3065);
			stream.writeShort(defs.anInt3050);
			stream.writeShort(defs.anInt3042);
			stream.writeByte(defs.anInt3068);
		}

		stream.writeByte(0); // Particles customization look @ R-S if you want
								// this

		// done separated for safe because of synchronization
		byte[] appeareanceData = new byte[stream.getOffset()];
		System.arraycopy(stream.getBuffer(), 0, appeareanceData, 0, appeareanceData.length);
		byte[] md5Hash = Utils.encryptUsingMD5(appeareanceData);
		this.appeareanceData = appeareanceData;
		md5AppeareanceDataHash = md5Hash;
		TaskTab.sendTab(player);
	}

	public byte[] getAppearenceLook() {
		return getAppearenceLook(player.getEquipment().getCosmeticPreviewItems() != null
				? player.getEquipment().getCosmeticPreviewItems().getItems()
				: player.getEquipment().getCosmeticItems().getItems(), bodyStyle);
	}

	public byte[] getAppearenceLook(Item[] cosmetics, int[] look) {
		OutputStream stream = new OutputStream();

		if (transformedNpcId >= 0) {
			stream.writeShort(-1); // 65535 tells it a npc
			stream.writeShort(transformedNpcId);
			stream.writeByte(0);
		} else {
			Item[] items = new Item[15];

			boolean[] skipLook = new boolean[items.length];
			for (int index = 0; index < items.length; index++) {
				Item item = player.getEquipment().isCanDisplayCosmetic() ? cosmetics[index] : null;

				if (index == Equipment.SLOT_AURA && player.getEquipment().isCanDisplayCosmetic()
						&& cosmetics[index] != null && player.getAuraManager().isActivated())
					item = null;
				if ((index == 3 || index == 5) && item != null
						&& player.getEquipment().getCosmeticItems().getItems() == cosmetics) {
					Item originalWeapon = player.getEquipment().getItem(index);
					if (originalWeapon == null || originalWeapon.getDefinitions().getRenderAnimId() != item
							.getDefinitions().getRenderAnimId()) {
						item = null;
					}
				}
				if (!(player.getControlerManager().getControler() instanceof DungeonController)
						&& player.getEquipment().isCanDisplayCosmetic() && index == Equipment.SLOT_SHIELD) {
					Item weapon = cosmetics[Equipment.SLOT_WEAPON] != null ? cosmetics[Equipment.SLOT_WEAPON]
							: player.getEquipment().getItems().get(Equipment.SLOT_WEAPON);
					if (weapon != null && Equipment.isTwoHandedWeapon(weapon))
						item = null;
				}
				/*if (player.getEquipment().isCanDisplayCosmetic() && player.getEquipment().getHiddenSlots()[index])
					item = new Item(CosmeticsHandler.HIDDEN_ITEMS_SLOT[index]);*/
				if (item == null)
					item = player.getEquipment().getItems().get(index);
				
				if (item != null) {
					items[index] = item;
					int skipSlotLook = item.getDefinitions().getEquipLookHideSlot();
					if (skipSlotLook != -1)
						skipLook[skipSlotLook] = true;
					int skipSlotLook2 = item.getDefinitions().getEquipLookHideSlot2();
					if (skipSlotLook2 != -1)
						skipLook[skipSlotLook2] = true;
				}
			}
			for (int index = 0; index < items.length; index++) {
				if (Equipment.DISABLED_SLOTS[index] != 0)
					continue;
				if (glowRed) {
					if (index == 0) {
						stream.writeShort(16384 + 2910);
						continue;
					}
					if (index == 1) {
						stream.writeShort(16384 + 14641);
						continue;
					}
					if (index == Equipment.SLOT_LEGS) {
						stream.writeShort(16384 + 2908);
						continue;
					}
					if (index == Equipment.SLOT_HANDS) {
						stream.writeShort(16384 + 2912);
						continue;
					}
					if (index == Equipment.SLOT_FEET) {
						stream.writeShort(16384 + 2904);
						continue;
					}
				}
				if (items[index] != null && items[index].getDefinitions().equipSlot != -1) {
					stream.writeShort(16384 + items[index].getId());
					continue;
				}
				if (!skipLook[index]) {
					int bodyStylendex = -1;

					switch (index) {
					case 4:
						bodyStylendex = 2;
						break;
					case 6:
						bodyStylendex = 3;
						break;
					case 7:
						bodyStylendex = 5;
						break;
					case 8:
						bodyStylendex = 0;
						break;
					case 9:
						bodyStylendex = 4;
						break;
					case 10:
						bodyStylendex = 6;
						break;
					case 11:
						bodyStylendex = 1;
						break;
					}
					if (bodyStylendex != -1 && lookI[bodyStylendex] > 0) {
						stream.writeShort(0x100 + lookI[bodyStylendex]);
						continue;
					}
				}
				stream.writeByte(0);
			}

			OutputStream streamModify = new OutputStream();
			int modifyFlag = 0;
			int slotIndex = -1;
			ItemModify[] modify = generateItemModify(items, cosmetics);
			for (int index = 0; index < items.length; index++) {
				if (Equipment.DISABLED_SLOTS[index] != 0)
					continue;
				slotIndex++;
				ItemModify im = modify[index];
				if (im == null)
					continue;
				modifyFlag |= 1 << slotIndex;
				int itemFlag = 0;
				OutputStream streamItem = new OutputStream();
				if (im.maleModelId1 != -1 || im.femaleModelId1 != -1) {
					itemFlag |= 0x1;
					streamItem.writeBigSmart(im.maleModelId1);
					streamItem.writeBigSmart(im.femaleModelId1);
					if (im.maleModelId2 != -2 || im.femaleModelId2 != -2) {
						streamItem.writeBigSmart(im.maleModelId2);
						streamItem.writeBigSmart(im.femaleModelId2);
					}
					if (im.maleModelId3 != -2 || im.femaleModelId3 != -2) {
						streamItem.writeBigSmart(im.maleModelId3);
						streamItem.writeBigSmart(im.femaleModelId3);
					}
				}
				if (im.colors != null) {
					itemFlag |= 0x4;
					streamItem.writeShort(0 | 1 << 4 | 2 << 8 | 3 << 12);
					for (int i = 0; i < 4; i++)
						streamItem.writeShort(im.colors[i]);
				}
				if (im.textures != null) {
					itemFlag |= 0x8;
					streamItem.writeByte(0 | 1 << 4);
					for (int i = 0; i < 2; i++)
						streamItem.writeShort(im.textures[i]);
				}
				streamModify.writeByte(itemFlag);
				streamModify.writeBytes(streamItem.getBuffer(), 0, streamItem.getOffset());
			}
			stream.writeShort(modifyFlag);
			stream.writeBytes(streamModify.getBuffer(), 0, streamModify.getOffset());
		}
		for (int index = 0; index < colour.length; index++)
			stream.writeByte(colour[index]);

		stream.writeShort(getRenderEmote());
		byte[] data = new byte[stream.getOffset()];
		System.arraycopy(stream.getBuffer(), 0, data, 0, data.length);
		return data;
	}

	private ItemModify[] generateItemModify(Item[] items, Item[] cosmetics) {
		ItemModify[] modify = new ItemModify[items.length];
		for (int slotId = 0; slotId < modify.length; slotId++) {
			if (items[slotId] != null && items[slotId] == cosmetics[slotId]) {
				int[] colors = new int[4];
				colors[0] = player.getEquipment().getCostumeColor();
				colors[1] = colors[0] + 12;
				colors[2] = colors[1] + 12;
				colors[3] = colors[2] + 12;
				setItemModifyColor(items[slotId], slotId, modify, colors);
			} else {
				int id = items[slotId] == null ? -1 : items[slotId].getId();
				if (id == 32152 || id == 32153 || id == 20768 || id == 20770 || id == 20772 || id == 20767
						|| id == 20769 || id == 20771)
					setItemModifyColor(items[slotId], slotId, modify,
							id == 32151 || id == 20768 || id == 20767 ? player.getMaxedCapeCustomized()
									: player.getCompletionistCapeCustomized());
				else if (id == 20708 || id == 20709) {
					ClansManager manager = player.getClanManager();
					if (manager == null)
						continue;
					int[] colors = manager.getClan().getMottifColors();
					setItemModifyColor(items[slotId], slotId, modify, colors);
					setItemModifyTexture(items[slotId], slotId, modify,
							new short[] { (short) ClansManager.getMottifTexture(manager.getClan().getMottifTop()),
									(short) ClansManager.getMottifTexture(manager.getClan().getMottifBottom()) });
				} else if (player.getAuraManager().isActivated() && slotId == Equipment.SLOT_AURA) {
					int auraId = player.getEquipment().getAuraId();
					if (auraId == -1)
						continue;
					int modelId = player.getAuraManager().getAuraModelId();
					int modelId2 = player.getAuraManager().getAuraModelId2();
					setItemModifyModel(items[slotId], slotId, modify, modelId, modelId, modelId2, modelId2, -1, -1);
				}
			}
		}
		return modify;
	}

	private void setItemModifyModel(Item item, int slotId, ItemModify[] modify, int maleModelId1, int femaleModelId1,
			int maleModelId2, int femaleModelId2, int maleModelId3, int femaleModelId3) {
		ItemDefinitions defs = item.getDefinitions();
		if (defs.getMaleWornModelId1() == -1 || defs.getFemaleWornModelId1() == -1)
			return;
		if (modify[slotId] == null)
			modify[slotId] = new ItemModify();
		modify[slotId].maleModelId1 = maleModelId1;
		modify[slotId].femaleModelId1 = femaleModelId1;
		if (defs.getMaleWornModelId2() != -1 || defs.getFemaleWornModelId2() != -1) {
			modify[slotId].maleModelId2 = maleModelId2;
			modify[slotId].femaleModelId2 = femaleModelId2;
		}
		if (defs.getMaleWornModelId3() != -1 || defs.getFemaleWornModelId3() != -1) {
			modify[slotId].maleModelId2 = maleModelId3;
			modify[slotId].femaleModelId2 = femaleModelId3;
		}
	}

	private void setItemModifyTexture(Item item, int slotId, ItemModify[] modify, short[] textures) {
		ItemDefinitions defs = item.getDefinitions();
		if (defs.originalTextureColors == null || defs.originalTextureColors.length != textures.length)
			return;
		if (Arrays.equals(textures, defs.originalTextureColors))
			return;
		if (modify[slotId] == null)
			modify[slotId] = new ItemModify();
		modify[slotId].textures = textures;
	}

	private void setItemModifyColor(Item item, int slotId, ItemModify[] modify, int[] colors) {
		ItemDefinitions defs = item.getDefinitions();
		if (defs.originalModelColors == null || defs.originalModelColors.length != colors.length)
			return;
		if (Arrays.equals(colors, defs.originalModelColors))
			return;
		if (modify[slotId] == null)
			modify[slotId] = new ItemModify();
		modify[slotId].colors = colors;
	}

	private static class ItemModify {

		private int[] colors;
		private short[] textures;
		private int maleModelId1;
		private int femaleModelId1;
		private int maleModelId2;
		private int femaleModelId2;
		private int maleModelId3;
		private int femaleModelId3;

		private ItemModify() {
			maleModelId1 = femaleModelId1 = -1;
			maleModelId2 = femaleModelId2 = -2;
			maleModelId3 = femaleModelId3 = -2;
		}
	}

	public byte[] getAppeareanceData() {
		return appeareanceData;
	}

	public int getBeardStyle() {
		return lookI[1];
	}

	public int getFacialHair() {
		return lookI[1];
	}

	public int getHairColor() {
		return colour[0];
	}

	public int getHairStyle() {
		return lookI[0];
	}

	public byte[] getMD5AppeareanceDataHash() {
		return md5AppeareanceDataHash;
	}

	public int getRenderEmote() {
		Item[] cosmetics = player.getEquipment().getCosmeticItems().getItems();
		if (renderEmote >= 0)
			return renderEmote;
		if (transformedNpcId >= 0) {
			NPCDefinitions defs = NPCDefinitions.getNPCDefinitions(transformedNpcId);
			HashMap<Integer, Object> data = defs.clientScriptData;
			if (data != null && !data.containsKey(2805))
				return defs.renderEmote;
		}
		if (!player.getCombatDefinitions().isCombatStance() && transformedNpcId < 0) {
			if (player.getAnimations().sandWalk)
				return 3388;
			else if (player.getAnimations().sadWalk)
				return 3531;
			else if (player.getAnimations().angryWalk)
				return 3529;
			else if (player.getAnimations().proudWalk)
				return 3528;
			else if (player.getAnimations().happyWalk)
				return 3527;
			else if (player.getAnimations().barbarianWalk)
				return 3525;
			else if (player.getAnimations().revenantWalk)
				return 3600;
			if (player.getCombatDefinitions().isSheathe())
				return 2699;
		}
		if ((cosmetics[Equipment.SLOT_WEAPON] != null && player.getEquipment().isCanDisplayCosmetic())) {
			return player.getEquipment().getWeaponRenderEmote();
		}
		return player.getEquipment().getWeaponStance();
	}

	public int getSize() {
		if (transformedNpcId >= 0)
			return NPCDefinitions.getNPCDefinitions(transformedNpcId).size;
		return 1;
	}

	public int getSkinColor() {
		return colour[4];
	}

	public int getTopStyle() {
		return lookI[2];
	}

	public boolean isFemale() {
		return !male;
	}

	public boolean isGlowRed() {
		return glowRed;
	}

	public boolean isHidden() {
		return hidePlayer;
	}

	public boolean isMale() {
		return male;
	}

	public void male() {
		lookI[0] = 3; // Hair
		lookI[1] = 14; // Beard
		lookI[2] = 18; // Torso
		lookI[3] = 26; // Arms
		lookI[4] = 34; // Bracelets
		lookI[5] = 38; // Legs
		lookI[6] = 42; // Shoes~
		colour[2] = 16;
		colour[1] = 16;
		colour[0] = 3;
		male = true;
	}

	public void resetAppearence() {
		lookI = new int[7];
		colour = new byte[10];
		if (cosmeticItems == null)
			cosmeticItems = new Item[14];
		male();
	}

	public void setArmsStyle(int i) {
		lookI[3] = i;
	}

	public void setBeardStyle(int i) {
		lookI[1] = i;
	}

	public void setColor(int i, int i2) {
		colour[i] = (byte) i2;
	}

	public void setFacialHair(int i) {
		lookI[1] = i;
	}

	public void setGlowRed(boolean glowRed) {
		this.glowRed = glowRed;
		generateAppearenceData();
	}

	public void setHairColor(int color) {
		colour[0] = (byte) color;
	}

	public void setHairStyle(int i) {
		lookI[0] = i;
	}

	public void setLegsColor(int color) {
		colour[2] = (byte) color;
	}

	public void setLegsStyle(int i) {
		lookI[5] = i;
	}

	public void setLook(int i, int i2) {
		lookI[i] = i2;
	}

	public void setLooks(short[] look) {
		for (byte i = 0; i < this.lookI.length; i = (byte) (i + 1))
			if (look[i] != -1)
				this.lookI[i] = look[i];
	}

	public void setMale(boolean male) {
		this.male = male;
	}

	public void setPlayer(Player player) {
		this.player = player;
		transformedNpcId = -1;
		renderEmote = -1;
		if (lookI == null || cosmeticItems == null)
			resetAppearence();
	}

	public void setRenderEmote(int id) {
		this.renderEmote = id;
		generateAppearenceData();
	}

	public void setSkinColor(int color) {
		colour[4] = (byte) color;
	}

	public void setTitle(int title) {
		this.title = title;
		generateAppearenceData();
	}

	public int getTitle() {
		return title;
	}

	public void setTopColor(int color) {
		colour[1] = (byte) color;
	}

	public void setTopStyle(int i) {
		lookI[2] = i;
	}

	public void setWristsStyle(int i) {
		lookI[4] = i;
	}

	public void switchHidden() {
		hidePlayer = !hidePlayer;
		generateAppearenceData();
	}

	public void transformIntoNPC(int id) {
		transformedNpcId = (short) id;
		generateAppearenceData();
	}

	public int getTransformedNpcId() {
		return transformedNpcId;
	}

	public static String getTitle(boolean male, int title) {
		return title == 0 ? null : ClientScriptMap.getMap(male ? 1093 : 3872).getStringValue(title);
	}

	public boolean isNPC() {
		return transformedNpcId != -1;
	}
}