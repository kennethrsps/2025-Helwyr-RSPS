package com.rs.game.player;

import java.io.Serializable;
import java.util.HashMap;

import com.rs.game.Animation;
import com.rs.game.Graphics;
import com.rs.game.World;
import com.rs.game.activites.duel.DuelArena;
import com.rs.game.item.Item;
import com.rs.game.player.content.interfaces.potionTimer.PotionTimers;
import com.rs.game.player.content.interfaces.potionTimer.PotionTimersInter;
import com.rs.utils.Utils;

public class AuraManager implements Serializable {

	private static final long serialVersionUID = -8860530757819448608L;

	private transient Player player;
	private transient boolean warned;
	private long activation;
	private HashMap<Integer, Long> cooldowns;

	public AuraManager() {
		cooldowns = new HashMap<Integer, Long>();
	}

	/*
	 * return seconds
	 */
	public static int getActivationTime(int aura) {
		switch (aura) {
		case 20958:
			return 600; // 10minutes
		case 22268:
			return 1200; // 20minutes
		case 22302:
		case 22895:
			return 1800; // 30minutes
		case 23862:
			return 3600;
		case 22294:
			return 7200; // 2hours
		case 20959:
			return 10800; // 3hours
		default:
			return 3600; // default 1hour
		}
	}

	public static int getCooldown(int aura) {
		switch (aura) {
		case 22294:
			return 14400; // 4hours
		case 22895:
			return 18000;
		case 20959:
		case 22302:
			return 86400; // 24hours
		default:
			return 10800; // default 3 hours - stated on
		// www.runescape.wikia.com/wiki/Aura
		}
	}

	public static int getTier(int aura) {
		switch (aura) {
		case 30792:
		case 30794:
		case 30796:
		case 30798:
		case 30800:
		case 30802:
		case 30804:
			return 5;
		case 23854:
		case 23856:
		case 23858:
		case 23860:
		case 23862:
		case 23864:
		case 23866:
		case 23868:
		case 23870:
		case 23872:
		case 23874:
		case 23876:
		case 23878:
		case 35792:
			return 4;
		case 22887:
		case 22909:
		case 22903:
		case 23911:
		case 22917:
		case 22919:
		case 22921:
		case 22923:
		case 22925:
		case 22931:
		case 22933:
		case 23844:
		case 23852:
		case 35790:
			return 3;
		case 22268:
		case 22278:
		case 22885:
		case 22929:
		case 23842:
		case 22302:
		case 22907:
		case 22901:
		case 23850:
		case 20959:
		case 22270:
		case 22272:
		case 22282:
		case 22286:
		case 22274:
		case 22276:
		case 22290:
		case 22292:
		case 22294:
		case 22296:
		case 22300:
		case 35788:
			return 2;
		default:
			return 1; // default 1
		}
	}

	protected void setPlayer(Player player) {
		this.player = player;
	}

	public void process() {
		if (!isActivated())
			return;
		if (activation - Utils.currentTimeMillis() <= 60000 && !warned) {
			warned = true;
			return;
		}
		if (Utils.currentTimeMillis() < activation)
			return;
		desactive();
		player.getGlobalPlayerUpdater().generateAppearenceData();
	}

	public void removeAura() {
		if (isActivated())
			desactive();
	}

	public void desactive() {
		activation = 0;
		warned = false;
		player.getPackets().sendGameMessage("Your aura has depleted.");
		if (!player.getPerkManager().overclocked)
			player.sendMessage("Purchase the Overclocked perk - doubles your Aura active/halve cooldown - times.",
					true);
		player.getPotiontimers().slotTimerArray[PotionTimersInter.AURA] = activation;
	}

	public long getCoolDown(int aura) {
		Long coolDown = cooldowns.get(aura);
		if (coolDown == null)
			return 0;
		return coolDown;
	}

	public void activate() {
		Item item = player.getEquipment().getItem(Equipment.SLOT_AURA);
		long cooldown = getCoolDown(player.getEquipment().getAuraId());
		if (item == null)
			return;
		if (item.getDefinitions().containsOption("Discard"))
			return;
		int toId = getTransformIntoAura(item.getId());
		if (toId != -1) {
			player.getEquipment().getItem(Equipment.SLOT_AURA).setId(toId);
			player.getEquipment().refresh(Equipment.SLOT_AURA);
			player.getGlobalPlayerUpdater().generateAppearenceData();
		} else {
			if (activation != 0) {
				player.sendMessage("You cannot turn off an aura. It must deplete in its own time.");
				return;
			}
			if (Utils.currentTimeMillis() <= getCoolDown(item.getId())) {
				player.sendMessage("Currently recharging. <col=ff0000>"
						+ getFormatedTime((cooldown - Utils.currentTimeMillis()) / 1000) + " until fully recharged.");
				return;
			}
			int tier = getTier(item.getId());
			activation = Utils.currentTimeMillis()
					+ (getActivationTime(item.getId()) * (player.getPerkManager().overclocked ? 2 : 1)) * 1000;
			cooldowns.put(item.getId(), (long) (Utils.currentTimeMillis()
					+ (getCooldown(item.getId()) / (player.getPerkManager().overclocked ? 2 : 1)) * 1000));
			player.setNextAnimation(new Animation(2231));
			player.getPotiontimers().slotTimerArray[PotionTimersInter.AURA] = activation;
			player.setNextGraphics(new Graphics(getActiveGraphic(tier)));
			player.getGlobalPlayerUpdater().generateAppearenceData();
		}
	}

	public int getTransformIntoAura(int aura) {
		switch (aura) {
		case 23896: // infernal
			return 23880;
		case 23880: // infernal
			return 23896;
		case 23898: // serene
			return 23882;
		case 23882: // serene
			return 23898;
		case 23900: // vernal
			return 23884;
		case 23884: // vernal
			return 23900;
		case 23902: // nocturnal
			return 23886;
		case 23886: // nocturnal
			return 23902;
		case 23904: // mystical
			return 23888;
		case 23888: // mystical
			return 23904;
		case 23906: // blazing
			return 23890;
		case 23890: // blazing
			return 23906;
		case 23908: // abyssal
			return 23892;
		case 23892: // abyssal
			return 23908;
		case 23910: // divine
			return 23894;
		case 23894: // divine
			return 23910;
		default:
			return -1;
		}
	}

	public void sendAuraRemainingTime() {
		if (!isActivated()) {
			long cooldown = getCoolDown(player.getEquipment().getAuraId());
			if (Utils.currentTimeMillis() <= cooldown) {
				player.sendMessage("Currently recharging. <col=ff0000>"
						+ getFormatedTime((cooldown - Utils.currentTimeMillis()) / 1000) + " until fully recharged.");
				return;
			}
			player.getPackets().sendGameMessage("Your aura is fully charged and ready to be used.");
			return;
		}
		player.getPackets().sendGameMessage("Aura time left: <col=ff0000>"
				+ getFormatedTime((activation - Utils.currentTimeMillis()) / 1000) + " remaining");
	}

	public String getFormatedTime(long seconds) {
		long minutes = seconds / 60;
		long hours = minutes / 60;
		minutes -= hours * 60;
		seconds -= (hours * 60 * 60) + (minutes * 60);
		String minutesString = (minutes < 10 ? "0" : "") + minutes;
		String secondsString = (seconds < 10 ? "0" : "") + seconds;
		return hours + ":" + minutesString + ":" + secondsString;
	}

	public void sendTimeRemaining(int aura) {
		long cooldown = getCoolDown(aura);
		if (cooldown < Utils.currentTimeMillis()) {
			player.getPackets().sendGameMessage("The aura has finished recharging, it is ready to use.");
			return;
		}
		player.sendMessage("Your aura is currently recharging, time left: <col=ff0000>"
				+ getFormatedTime((cooldown - Utils.currentTimeMillis()) / 1000) + ".");
	}

	public boolean ignoreDuelIsActivated() {
		return activation != 0;
	}

	public boolean isActivated() {
		if (player.getControlerManager().getControler() != null
				&& player.getControlerManager().getControler() instanceof DuelArena)
			return false;
		return activation != 0;
	}

	public int getAuraModelId2() {
		int aura = player.getEquipment().getAuraId();
		switch (aura) {
		case 22905: // Corruption. *no eyes
			return 16449;
		case 22899: // Salvation. *no eyes
			return 16465;
		case 23848: // Harmony. *no eyes
			return 68605;
		case 22907: // Greater corruption. *no eyes
			return 16464;
		case 22901: // Greater salvation. *no eyes
			return 16524;
		case 23850: // Greater harmony. *no eyes
			return 68610;
		case 22909: // Master corruption. * eyes*
			if (player.getGlobalPlayerUpdater().isMale())
				return 16429;
			if (!player.getGlobalPlayerUpdater().isMale())
				return 16510;
		case 22903: // Master salvation. * eyes*
			if (player.getGlobalPlayerUpdater().isMale())
				return 16450;
			if (!player.getGlobalPlayerUpdater().isMale())
				return 16427;
		case 23852: // Master harmony. * eyes*
			if (player.getGlobalPlayerUpdater().isMale())
				return 68607;
			if (!player.getGlobalPlayerUpdater().isMale())
				return 68609;
		case 23874: // Supreme corruption. * eyes*
			if (player.getGlobalPlayerUpdater().isMale())
				return 68615;
			if (!player.getGlobalPlayerUpdater().isMale())
				return 68614;
		case 23876: // Supreme salvation. * eyes*
			if (player.getGlobalPlayerUpdater().isMale())
				return 68611;
			if (!player.getGlobalPlayerUpdater().isMale())
				return 68608;
		case 23854: // Supreme harmony. * eyes*
			if (player.getGlobalPlayerUpdater().isMale())
				return 68613;
			if (!player.getGlobalPlayerUpdater().isMale())
				return 68606;
		case 22889: // Aegis.
			return 83308;
		default:
			return -1;
		}
	}

	public int getAuraModelId() {
		Item weapon = player.getEquipment().getItem(Equipment.SLOT_WEAPON);
		if (weapon == null || player.getCombatDefinitions().isSheathe())
			return 8719;
		String name = weapon.getDefinitions().getName().toLowerCase();
		if (name.contains("dagger"))
			return 8724;
		if (name.contains("whip"))
			return 8725;
		// if (name.contains("staff") || name.contains("battlestaff")
		// || name.contains("spear"))
		// return 9014;
		if (name.contains("scimitar"))
			return 8957;
		if (name.contains("cbow") || name.contains("crossbow") || name.contains("c'bow"))
			return 8912;
		if (name.contains("shortbow") || name.contains("longbow") || name.equals("dark bow")) {
			if (name.contains("noxious"))
				return 8719;
			return 8944;
		}
		if (name.contains("2h sword") || name.contains("godsword"))
			return 8773;
		if (name.contains("sword") || name.contains("korasi") || name.contains("longsword") || name.contains("rapier"))
			return 8963;
		return 8719;
	}

	public int getActiveGraphic(int tier) {
		if (tier == 2)
			return 370;
		if (tier == 3)
			return 1764;
		if (tier == 4)
			return 1763;
		return 370;
	}

	public boolean hasPoisonPurge() {
		if (!isActivated())
			return false;
		int aura = player.getEquipment().getAuraId();
		return aura == 20958 || aura == 22268 || aura == 22917 || aura == 23862;
	}

	public double getMagicAccurayMultiplier() {
		if (!isActivated() || World.isPvpArea(player))
			return 1;
		int aura = player.getEquipment().getAuraId();
		if (aura == 20962)
			return 1.03;
		if (aura == 22270)
			return 1.05;
		if (aura == 22919)
			return 1.07;
		if (aura == 23864)
			return 1.1;
		return 1;
	}

	public double getMeleeAccuracyMultiplier() {
		if (!isActivated() || World.isPvpArea(player))
			return 1;
		int aura = player.getEquipment().getAuraId();
		if (aura == 35786)
			return 1.03;
		if (aura == 35788)
			return 1.05;
		if (aura == 35790)
			return 1.07;
		if (aura == 35792)
			return 1.1;
		return 1;
	}

	public double getBerserkerBoost(boolean defenceBoost) {
		if (!isActivated() || World.isPvpArea(player))
			return 1;
		return player.getEquipment().getAuraId() != 22897 ? 1 : defenceBoost ? 0.85 : 1.1;
	}

	public boolean getDarkMagicBoost() {
		if (!isActivated() || World.isPvpArea(player))
			return false;
		return player.getEquipment().getAuraId() == 22891;
	}

	public boolean hasAncestorSpirits() {
		if (!isActivated())
			return false;
		return player.getEquipment().getAuraId() == 22895;
	}

	public double getRangeAccurayMultiplier() {
		if (!isActivated() || World.isPvpArea(player))
			return 1;
		int aura = player.getEquipment().getAuraId();
		if (aura == 20967)
			return 1.03;
		if (aura == 22272)
			return 1.05;
		if (aura == 22921)
			return 1.07;
		if (aura == 23866)
			return 1.1;
		return 1;
	}

	public double getWoodcuttingAccurayMultiplier() {
		if (!isActivated())
			return 1;
		int aura = player.getEquipment().getAuraId();
		if (aura == 22280)
			return 1.025;
		if (aura == 22282)
			return 1.05;
		if (aura == 22915)
			return 1.075;
		if (aura == 23860)
			return 1.1;
		if (aura == 30796)
			return 1.25;
		return 1;
	}

	public double getMiningAccurayMultiplier() {
		if (!isActivated())
			return 1;
		int aura = player.getEquipment().getAuraId();
		if (aura == 22284)
			return 1.025;
		if (aura == 22286)
			return 1.05;
		if (aura == 22913)
			return 1.075;
		if (aura == 23858)
			return 1.1;
		if (aura == 30800)
			return 1.25;
		return 1;
	}

	public boolean usingRegeneration() {
		if (!isActivated())
			return false;
		return player.getEquipment().getAuraId() == 22893;
	}

	public double getFishingAccurayMultiplier() {
		if (!isActivated())
			return 1;
		int aura = player.getEquipment().getAuraId();
		if (aura == 20966)
			return 1.025;
		if (aura == 22274)
			return 1.05;
		if (aura == 22923)
			return 1.075;
		if (aura == 23868)
			return 1.1;
		if (aura == 30794)
			return 1.25;
		return 1;
	}

	public double getPrayerPotsRestoreMultiplier() {
		if (!isActivated())
			return 1;
		int aura = player.getEquipment().getAuraId();
		if (aura == 20965)
			return 1.03;
		if (aura == 22276)
			return 1.05;
		if (aura == 22925)
			return 1.07;
		if (aura == 23870)
			return 1.1;
		return 1;
	}

	public boolean isUsingSurefooted() {
		if (!isActivated())
			return false;
		return player.getEquipment().getAuraId() == 20964;
	}

	public double getHunterMultiplier() {
		if (!isActivated())
			return 1;
		int aura = player.getEquipment().getAuraId();
		if (aura == 22927)
			return 1.03;
		if (aura == 22929)
			return 1.05;
		if (aura == 22931)
			return 1.07;
		if (aura == 23872)
			return 1.1;
		if (aura == 30802)
			return 1.15;
		return 1;
	}

	public double getThievingAccurayMultiplier() {
		if (!isActivated())
			return 1;
		int aura = player.getEquipment().getAuraId();
		if (aura == 22288)
			return 1.025;
		if (aura == 22290)
			return 1.05;
		if (aura == 22911)
			return 1.075;
		if (aura == 23856)
			return 1.1;
		if (aura == 30798)
			return 1.25;
		return 1;
	}

	public double getDefenceMultiplier() {
		if (!isActivated())
			return 1;
		int aura = player.getEquipment().getAuraId();
		if (aura == 22889)
			return 1.05;
		return 1;
	}

	public double getChanceNotDepleteMN_WC() {
		if (!isActivated())
			return 1;
		int aura = player.getEquipment().getAuraId();
		if (aura == 22292)
			return 1.1;
		return 1;
	}

	public boolean usingEquilibrium() {
		if (!isActivated() || World.isPvpArea(player))
			return false;
		int aura = player.getEquipment().getAuraId();
		return aura == 22294;
	}

	public boolean usingPenance() {
		if (!isActivated())
			return false;
		int aura = player.getEquipment().getAuraId();
		return aura == 22300;
	}

	public int getDivinationEnrichment() {
		if (!isActivated())
			return 0;
		int aura = player.getEquipment().getAuraId();
		if (aura == 30784)
			return 1;
		if (aura == 30786)
			return 2;
		if (aura == 30788)
			return 3;
		if (aura == 30790)
			return 4;
		if (aura == 30792)
			return 5;
		return 0;
	}

	public double getPrayerMultiplier() {
		if (!isActivated())
			return 1;
		int aura = player.getEquipment().getAuraId();
		switch (aura) {
		case 22905: // Corruption.
		case 22899: // Salvation.
		case 23848: // Harmony.
			return 1.01;
		case 22907: // Greater corruption.
		case 22901: // Greater salvation.
		case 23850: // Greater harmony.
			return 1.015;
		case 22909: // Master corruption.
		case 22903: // Master salvation.
		case 23852: // Master harmony.
			return 1.02;
		case 23874: // Supreme corruption.
		case 23876: // Supreme salvation.
		case 23854: // Supreme harmony.
			return 1.025;
		}
		return 1.0;
	}

	public double getPrayerRestoration() {
		if (!isActivated())
			return 0;
		int aura = player.getEquipment().getAuraId();
		switch (aura) {
		case 22905: // Corruption.
		case 22899: // Salvation.
		case 23848: // Harmony.
			return 0.03;
		case 22907: // Greater corruption.
		case 22901: // Greater salvation.
		case 23850: // Greater harmony.
			return 0.05;
		case 22909: // Master corruption.
		case 22903: // Master salvation.
		case 23852: // Master harmony.
			return 0.07;
		case 23874: // Supreme corruption.
		case 23876: // Supreme salvation.
		case 23854: // Supreme harmony.
			return 0.1;
		}
		return 0;
	}

	public void checkSuccefulHits(int damage) {
		if (!isActivated() || World.isPvpArea(player))
			return;
		int aura = player.getEquipment().getAuraId();
		if (aura == 22296)
			useInspiration();
		else if (aura == 22298)
			useVampyrism(damage);
	}

	public void useVampyrism(int damage) {
		int heal = (int) (damage * 0.15);
		if (heal > 0)
			player.heal(heal);
	}

	public int getKnockoutChance() {
		if (!isActivated() || World.isPvpArea(player))
			return 0;
		int aura = player.getEquipment().getAuraId();
		if (aura == 20961)
			return 3;
		if (aura == 22933)
			return 5;
		return 0;
	}

	public void useInspiration() {
		Integer atts = (Integer) player.getTemporaryAttributtes().get("InspirationAura");
		if (atts == null)
			atts = 0;
		atts++;
		if (atts == 5) {
			atts = 0;
			player.getCombatDefinitions().restoreSpecialAttack(1);
		}
		player.getTemporaryAttributtes().put("InspirationAura", atts);
	}

	public boolean usingWisdom() {
		if (!isActivated())
			return false;
		int aura = player.getEquipment().getAuraId();
		return aura == 22302;
	}

	public boolean isWingedAura(int aura) {
		switch (aura) {
		case 22905: // Corruption.
		case 22899: // Salvation.
		case 23848: // Harmony.
		case 22907: // Greater corruption.
		case 22901: // Greater salvation.
		case 23850: // Greater harmony.
		case 22909: // Master corruption.
		case 22903: // Master salvation.
		case 23852: // Master harmony.
		case 23874: // Supreme corruption.
		case 23876: // Supreme salvation.
		case 23854: // Supreme harmony.
		case 22889: // Aegis
			return true;
		}
		return false;
	}
}