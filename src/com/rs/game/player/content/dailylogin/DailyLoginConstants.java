package com.rs.game.player.content.dailylogin;

import com.rs.game.item.Item;

public class DailyLoginConstants {
	
	public static enum LoginRewards{
		WEEK1(new Item[] {
				new Item(995,200000),//DAY 1
				new Item(995,200000),//DAY 2
				new Item(995,100000),//DAY 3
				new Item(995,200000),//DAY 1
				new Item(995,400000),//DAY 2
				new Item(995,400000),//DAY 3
				new Item(995,500000),//DAY 1
				new Item(995,5000000)//bonus
		}),
		WEEK2(new Item[] {
				new Item(995,200000),//DAY 1
				new Item(995,200000),//DAY 2
				new Item(995,100000),//DAY 3
				new Item(995,200000),//DAY 1
				new Item(995,400000),//DAY 2
				new Item(995,400000),//DAY 3
				new Item(995,500000),//DAY 1
				new Item(995,5000000)//bonus
		}),
		WEEK3(new Item[] {
				new Item(995,200000),//DAY 1
				new Item(995,200000),//DAY 2
				new Item(995,100000),//DAY 3
				new Item(995,200000),//DAY 1
				new Item(995,400000),//DAY 2
				new Item(995,400000),//DAY 3
				new Item(995,500000),//DAY 1
				new Item(995,5000000)//bonus
		}),
		WEEK4(new Item[] {
				new Item(995,200000),//DAY 1
				new Item(995,200000),//DAY 2
				new Item(995,100000),//DAY 3
				new Item(995,200000),//DAY 1
				new Item(995,400000),//DAY 2
				new Item(995,400000),//DAY 3
				new Item(995,500000),//DAY 1
				new Item(995,5000000)//bonus
		}),
		WEEK5(new Item[] {
				new Item(995,200000),//DAY 1
				new Item(995,200000),//DAY 2
				new Item(995,100000),//DAY 3
				new Item(995,200000),//DAY 1
				new Item(995,400000),//DAY 2
				new Item(995,400000),//DAY 3
				new Item(995,500000),//DAY 1
				new Item(995,5000000)//bonus
		}),
		WEEK6(new Item[] {
				new Item(995,200000),//DAY 1
				new Item(995,200000),//DAY 2
				new Item(995,100000),//DAY 3
				new Item(995,200000),//DAY 1
				new Item(995,400000),//DAY 2
				new Item(995,400000),//DAY 3
				new Item(995,500000),//DAY 1
				new Item(995,5000000)//bonus
		}),
		;
		private Item[] rewards;
		LoginRewards(Item[] rewards){
			this.setRewards(rewards);
		}
		public Item[] getRewards() {
			return rewards;
		}
		public void setRewards(Item[] rewards) {
			this.rewards = rewards;
		}
	}
	
}
