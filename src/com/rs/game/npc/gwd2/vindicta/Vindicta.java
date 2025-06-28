package com.rs.game.npc.gwd2.vindicta;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.rs.cores.CoresManager;
import com.rs.cores.FixedLengthRunnable;
import com.rs.game.Animation;
import com.rs.game.Entity;
import com.rs.game.Graphics;
import com.rs.game.Hit;
import com.rs.game.Hit.HitLook;
import com.rs.game.WorldTile;
import com.rs.game.activities.instances.VindictaInstance;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.impl.gwd2.VindictaCombat;
import com.rs.game.player.Player;
import com.rs.utils.Utils;



public class Vindicta extends NPC {

	private static final long serialVersionUID = 3958941320672345882L;
	
	private int phase;
	private int gorvekPhase;
	public VindictaInstance instance;
	public List<WorldTile[]> tileSets = new ArrayList<WorldTile[]>();
	private List<Player> hitPlayers = new ArrayList<Player>();
	private List<WorldTile> safeTiles = new ArrayList<WorldTile>();
	private boolean hurricane;
	
	public static final int[][] CORNERS = new int[][] {
		{ 34, 34 }, { 34, 9 }, { 17, 9 }, { 17, 34 }
	};
	
	@Override
	public boolean canWalkNPC(int toX, int toY) {
		return true;
	}

	@Override
	public void handleIngoingHit(Hit hit) {
		if (getCapDamage() != -1 && hit.getDamage() > getCapDamage())
			hit.setDamage(getCapDamage());
		super.handleIngoingHit(hit);
		//if (hit.getLook() != HitLook.MELEE_DAMAGE && hit.getLook() != HitLook.RANGE_DAMAGE && hit.getLook() != HitLook.MAGIC_DAMAGE) {
		//	HeartOfGielinor.refreshHealth(instance, getHitpoints() - hit.getDamage(), getMaxHitpoints());
		//	return;
		//}
		//handlePrayers(hit);
		//HeartOfGielinor.refreshHealth(instance, getHitpoints() - hit.getDamage(), getMaxHitpoints());
	}

	public boolean performedHurricane() {
		return hurricane;
	}
	
	public void setGorvekPhase(int phase) {
		gorvekPhase = phase;
	}
	
	public int getGorvekPhase() {
		return gorvekPhase;
	}
	
	public void setHasPerformedHurricane() {
		hurricane = true;
	}
	
	public void addSafeTile(WorldTile t) {
		safeTiles.add(t);
	}
	
	public void removeSafeTile(WorldTile t) {
		safeTiles.remove(t);
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

	public Vindicta(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea, boolean spawned,
			VindictaInstance instance) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		setIntelligentRouteFinder(true);
		setForceTargetDistance(50);
		setForceAgressive(true);
		setRun(true);
		this.setNoDistanceCheck(true);
		this.instance = instance;
	}

	@Override
	public void sendDeath(final Entity source) {
		super.sendDeath(source);
		for (int s = 0; s < tileSets.size(); s++) {
			if (tileSets.get(s) == null)
				continue;
			for (int i = 0; i < tileSets.get(s).length; i++) {
				if (tileSets.get(s)[i] != null) {
					for (Player p : instance.getPlayers())
						p.getPackets().sendGraphics(new Graphics(-1), tileSets.get(s)[i]);
				}
			}
		}
		tileSets.clear();
	}
	
	@Override
	public int getCapDamage() {
		return 1250;
	}

	public void addFires(WorldTile[] tiles) {
		final List<WorldTile> t = new ArrayList<WorldTile>();
		try {
			for (int i = 0; i < tiles.length; i++) {
				WorldTile dest = tiles[i];
				if (dest == null)
					continue;
				loop: for (int x = 0; x < tileSets.size(); x++) {
					for (int a = 0; a < tileSets.get(x).length; a++) {
						if (tileSets.get(x)[a] != null && tileSets.get(x)[a].getTileHash() == dest.getTileHash()) {
							getInstance().getPlayers().forEach(p -> p.getPackets().sendGraphics(new Graphics(-1), dest));
							break loop;
						}
					}
				}
				for (int x = safeTiles.size() - 1; x > 0; x--)
					if (safeTiles.get(x).getTileHash() == dest.getTileHash())
						safeTiles.remove(x);
				t.add(dest);
			}
			tileSets.add(tiles);
		} catch (Exception e) {}
		CoresManager.getServiceProvider().scheduleFixedLengthTask(new FixedLengthRunnable() {
			private int ticks;
			@Override
			public boolean repeat() {
				try {
					if (t.isEmpty()) 
						return false;
					if (ticks % 25 == 0) {
						getInstance().getPlayers().forEach(p -> p.getPackets().sendGraphics(new Graphics(6112), t.remove(0)));
					}
					ticks++;
				} catch (Exception e) {
					e.printStackTrace();
				}
				return true;
			}
			
		}, 0, 1, TimeUnit.MILLISECONDS);

	}

	public void removeFires(WorldTile[] tiles) {
		tileSets.remove(tiles);
	}
	
	@Override
	public void spawn(){
		super.spawn();
		setNextNPCTransformation(22459);
	}

	private void rideOnDragon() {
		NPC gorvek = getInstance().getGorvek();
		gorvek.setNextWorldTile(new WorldTile(getX(), getY(), 1));
		gorvek.setNextAnimation(new Animation(28276));
		setNextAnimation(new Animation(28263));
		setNextNPCTransformation(getInstance().isHardMode() ? 22462 : 22460);
		setRun(true);
		setPhase(0);
		gorvek.setNextWorldTile(new WorldTile(getInstance().getWorldTile(63, 62)));
	}
	
	private final void checkForceAttack() {
        if (getId() != 22463 && hurricane && getInstance().getPlayers().size() == 1 && getCombat().getTarget() != null && getCombat().getTarget().getDistance(this) > getSize() && getCombat().getTarget().getX() <= getInstance().getWorldTile(39, 38).getX()) {
			if (getTemporaryAttributtes().get("rangedDelay") != null) {
				long delay = (long) getTemporaryAttributtes().get("rangedDelay");
				if (delay > Utils.currentTimeMillis())
					return;
			}
			if (getId() == 22459) {
				if (getPhase() != 2 && getPhase() != 6) {
					if (getPhase() < 2)
						setPhase(2);
					else
						setPhase(6);
				}
			} else {
				if (getPhase() != 1 && getPhase() != 3) {
					if (getPhase() < 1)
						setPhase(1);
					else
						setPhase(3);
				}
			}
			resetWalkSteps();
			setFreezeDelay(VindictaCombat.rangedAttack(this, getCombat().getTarget()) - 2);
			getTemporaryAttributtes().put("rangedDelay", Utils.currentTimeMillis() + 3000);
		}
	}

	@Override
	public void processNPC() {
		checkReset();
		super.processNPC();
		hitPlayers.clear();
		checkForceAttack();
		instance.getPlayers().forEach(p -> {
			for (int i = 0; i < tileSets.size(); i++) {
				if (tileSets.get(i) == null)
					continue;
				loop : for (int x = 0; x < tileSets.get(i).length; x++)
					if (tileSets.get(i)[x] != null && p.withinDistance(tileSets.get(i)[x], 1)) {
						for (WorldTile t : safeTiles)
							if (p.getTileHash() == t.getTileHash()) 
								continue loop;
						if (!hitPlayers.contains(p))
							hitPlayers.add(p);
					}
			}
		});
		hitPlayers.forEach(p -> p.applyHit(new Hit(null, getInstance().isHardMode() ? Utils.random(100, 200) : Utils.random(50, 75), HitLook.MAGIC_DAMAGE)));
		if (this.getId() == 22460 && getHitpoints() <= 5000 || getId() == 22461 && getHitpoints() <= 5000)
			rideOnDragon();
	}
	
	public void checkReset() {
		int maxhp = getMaxHitpoints();
		if (maxhp > getHitpoints() && !isUnderCombat() && getPossibleTargets().isEmpty())
			setHitpoints(maxhp);
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

	public VindictaInstance getInstance() {
		return instance;
	}
}
