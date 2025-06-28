package com.rs.game.activites.resourcegather;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class SkillingEventSkillObject {

	public int skillId;
	public String skillName;
	public long timeStarted;
	public List<SkillingEventObject> eventList;
	
	public SkillingEventObject getPlayerEvent(String name) {
		for(SkillingEventObject event : eventList) {
			if(event.player.equals(name)) {
				return event;
			}
		}
		return null;
	}
	
	public SkillingEventObject[] getTop5Events() {
		SkillingEventObject[] events = new SkillingEventObject[5];
		if(eventList.isEmpty()) {
			return events;
		}
		int size = eventList.size();
		if(size > 0) {
			double xpToBeat = 0;
			SkillingEventObject winningEvent = null;
			for(SkillingEventObject event : eventList) {
				double xp = event.getXpTotal();
				if(xp > xpToBeat) {
					xpToBeat = xp;
					winningEvent = event;
				}
			}
			if(winningEvent != null)
				events[0] = winningEvent;
		}
		if(size > 1) {
			double xpToBeat = 0;
			SkillingEventObject winningEvent = null;
			for(SkillingEventObject event : eventList) {
				if(event == events[0]) {
					continue;
				}
				double xp = event.getXpTotal();
				if(xp > xpToBeat) {
					xpToBeat = xp;
					winningEvent = event;
				}
			}
			if(winningEvent != null)
				events[1] = winningEvent;
		}
		if(size > 2) {
			double xpToBeat = 0;
			SkillingEventObject winningEvent = null;
			for(SkillingEventObject event : eventList) {
				if(event == events[0]) {
					continue;
				}
				if(event == events[1]) {
					continue;
				}
				double xp = event.getXpTotal();
				if(xp > xpToBeat) {
					xpToBeat = xp;
					winningEvent = event;
				}
			}
			if(winningEvent != null)
				events[2] = winningEvent;
		}
		if(size > 3) {
			double xpToBeat = 0;
			SkillingEventObject winningEvent = null;
			for(SkillingEventObject event : eventList) {
				if(event == events[0]) {
					continue;
				}
				if(event == events[1]) {
					continue;
				}
				if(event == events[2]) {
					continue;
				}
				double xp = event.getXpTotal();
				if(xp > xpToBeat) {
					xpToBeat = xp;
					winningEvent = event;
				}
			}
			if(winningEvent != null)
				events[3] = winningEvent;
		}
		if(size > 4) {
			double xpToBeat = 0;
			SkillingEventObject winningEvent = null;
			for(SkillingEventObject event : eventList) {
				if(event == events[0]) {
					continue;
				}
				if(event == events[1]) {
					continue;
				}
				if(event == events[2]) {
					continue;
				}
				if(event == events[3]) {
					continue;
				}
				double xp = event.getXpTotal();
				if(xp > xpToBeat) {
					xpToBeat = xp;
					winningEvent = event;
				}
			}
			if(winningEvent != null)
				events[4] = winningEvent;
		}
		return events;
	}
	
	public SkillingEventObject[] getTop5Eventsold() {
		SkillingEventObject[] events = new SkillingEventObject[5];
		if(eventList.isEmpty()) {
			return events;
		}
		if(eventList.size() > 0)
			events[0] = eventList.get(0);
		if(eventList.size() > 1)
			events[1] = eventList.get(0);
		if(eventList.size() > 2)
			events[2] = eventList.get(0);
		if(eventList.size() > 3)
			events[3] = eventList.get(0);
		if(eventList.size() > 4)
			events[4] = eventList.get(0);
		for(SkillingEventObject event : eventList) {
			if(event.getXpTotal() > events[0].getXpTotal()) {
				events[1] = events[0];
				events[0] = event;
				continue;
			}
			if(eventList.size() > 1) {
				if(event.getXpTotal() > events[1].getXpTotal()) {
					events[2] = events[1];
					events[1] = event;
					continue;
				}
			}
			if(eventList.size() > 2) {
				if(event.getXpTotal() > events[2].getXpTotal()) {
					events[3] = events[2];
					events[2] = event;
					continue;
				}
			}
			if(eventList.size() > 3) {
				if(event.getXpTotal() > events[3].getXpTotal()) {
					events[4] = events[3];
					events[3] = event;
					continue;
				}
			}
			if(eventList.size() > 4) {
				if(event.getXpTotal() > events[4].getXpTotal()) {
					events[5] = events[4];
					events[4] = event;
					continue;
				}
			}
		}
		return events;
	}
	
	public SkillingEventSkillObject(int skill, String name, long time) {
		this.skillId = skill;
		this.skillName = name;
		this.timeStarted = time + 3600000;// 1 hour
		this.eventList = new CopyOnWriteArrayList<SkillingEventObject>();
	}
	
}
