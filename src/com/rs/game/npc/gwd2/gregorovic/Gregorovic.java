package com.rs.game.npc.gwd2.gregorovic;

import java.util.ArrayList;
import java.util.List;

import com.rs.Settings;
import com.rs.game.Animation;
import com.rs.game.Entity;
import com.rs.game.Graphics;
import com.rs.game.Hit;
import com.rs.game.WorldTile;
import com.rs.game.Hit.HitLook;
import com.rs.game.activities.instances.GregorovicInstance;
import com.rs.game.npc.Drop;
import com.rs.game.npc.NPC;
import com.rs.game.player.Player;
import com.rs.utils.NPCDrops;
import com.rs.utils.Utils;





public class Gregorovic extends NPC {

	private static final long serialVersionUID = -3292021187841555323L;
	private int phase, boostedDamage, lastSwitch;
	private GregorovicInstance instance;
	
	private ArrayList<WorldTile> tiles = new ArrayList<WorldTile>();
	private Shadow[] shadows = new Shadow[5];
	private List<Spirit> spirits = new ArrayList<Spirit>();
	private int maniaBuff;

	private final WorldTile[] from;
	private final WorldTile[] to;
	
	@Override
	public boolean canWalkNPC(int toX, int toY) {
		return true;
	}
	
	@Override
	public double getMeleePrayerMultiplier() {
		return 0.5;
	}
	
	@Override
	public double getRangePrayerMultiplier() {
		return 0.5;
	}
	
	@Override
	public double getMagePrayerMultiplier() {
		return 1;
	}
	
	@Override
	public void handleIngoingHit(Hit hit) {
		if (getCapDamage() != -1 && hit.getDamage() > getCapDamage())
			hit.setDamage(getCapDamage());
		super.handleIngoingHit(hit);
	}
	
	@Override
	public int getCapDamage() {
		return 1250;
	}

	public WorldTile[] getFrom() {
		return from;
	}

	public WorldTile[] getTo() {
		return to;
	}
	
	public void boostDamage() {
		boostedDamage++;
	}
	
	public double getDamageBoost() {
		return (double) (1 + (boostedDamage / 10f));
	}
	
	@Override
	public void drop() {}

	public Gregorovic(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea, boolean spawned, GregorovicInstance instance) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		this.instance = instance;
		setRun(true);
		setIntelligentRouteFinder(true);
		setForceTargetDistance(50);
		setForceMultiArea(true);
		setRangedBonuses(1000);
		from = new WorldTile[] { getInstance().getWorldTile(32, 37), getInstance().getWorldTile(44, 55), getInstance().getWorldTile(55, 37) };
		to = new WorldTile[] { getInstance().getWorldTile(35, 40), getInstance().getWorldTile(43, 51), getInstance().getWorldTile(52, 40) };
	}

	@Override
	public int getMaxDistance() {
		return 3;
	}

	@Override
	public void processNPC() {
		if (getHitpoints() <= 14000 && getShadows()[0] == null && !isDead()) {
			for (int i = 0; i < 2; i++) 
				getShadows()[i] = new Shadow(22444, new WorldTile(instance.getWorldTile(Utils.random(33, 54), Utils.random(33, 54))), -1, true, true);
		} else if (getHitpoints() <= 6000 && getShadows()[3] == null && !isDead()) {
			for (int i = 2; i < 5; i++)
				getShadows()[i] = new Shadow(22444, new WorldTile(instance.getWorldTile(Utils.random(33, 54), Utils.random(33, 54))), -1, true, true);
		}
		if (lastSwitch > 8) {
			for (Shadow s : shadows) {
				if (s == null || s.isDead() || s.hasFinished())
					continue;
				if (Utils.random(2) == 1) {
					final WorldTile shadow = new WorldTile(s);
					final WorldTile greg = new WorldTile(this);
					s.setNextWorldTile(greg);
					setNextWorldTile(shadow);
					setNextGraphics(new Graphics(6137));
					s.setNextGraphics(new Graphics(6137));
					lastSwitch = 0;
					break;
				}
			}
			
		}
		lastSwitch++;
		super.processNPC();
	}

	@Override
	public void sendDeath(final Entity source) {
		for (Shadow s : getShadows()) {
			if (s != null && !s.hasFinished() && !s.isDead())
				s.sendDeath(source);
		}
		for (Spirit s : getSpirits()) {
			if (s != null && !s.hasFinished() && !s.isDead())
				s.sendDeath(source);
		}
		super.sendDeath(source);
	}
	
	/**
	 * Skips the regular attacks to the next special attack.
	 */
	public void skipBasicAttacks() {
		if (getPhase() < 4)
			setPhase(3);
		else if (getPhase() < 8)
			setPhase(7);
		else if (getPhase() < 12)
			setPhase(11);
	}

	@Override
	public void spawn() {
		super.spawn();
		setNextAnimation(new Animation(28223));
	}

	public int getPhase() {
		return phase;
	}

	public void nextPhase() {
		phase++;
	}

	public void setPhase(int phase) {
		this.phase = phase;
	}

	public GregorovicInstance getInstance() {
		return instance;
	}
	
	public Shadow[] getShadows() {
		return shadows;
	}

	public ArrayList<WorldTile> getTiles() {
		return tiles;
	}

	public int getManiaBuff() {
		return maniaBuff;
	}

	public void setManiaBuff(int buff) {
		this.maniaBuff = buff;
	}
	
	public List<Spirit> getSpirits() {
		return spirits;
	}

	public void resetShadows() {
		shadows = new Shadow[5];
	}

}