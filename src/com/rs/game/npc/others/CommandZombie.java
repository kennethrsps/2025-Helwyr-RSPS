package com.rs.game.npc.others;

import com.rs.game.World;
import com.rs.game.WorldTile;
import com.rs.game.item.Item;
import com.rs.game.npc.Drop;
import com.rs.game.npc.NPC;
import com.rs.game.player.Player;

@SuppressWarnings("serial")
public class CommandZombie extends NPC {
	private Item Rareitem;

	public CommandZombie(int id, Item Rareitem, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea,
			boolean spawned) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		this.Rareitem = Rareitem;
	}

	@Override
	public void drop() {
		Player killer = getMostDamageReceivedSourcePlayer();
		if (killer == null)
			return;
		super.drop();
		if (Rareitem != null) {
			World.sendNews(
					killer.getDisplayName() + " has recived " + Rareitem.getAmount() + " x " + Rareitem.getName() + ".",
					1);
			this.sendDrop(killer, new Drop(Rareitem.getId(), Rareitem.getAmount(), Rareitem.getAmount()));
		}
	}

}
