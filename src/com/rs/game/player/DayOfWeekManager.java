package com.rs.game.player;

import java.io.Serializable;
import java.util.Calendar;
import java.util.TimeZone;

import com.rs.game.player.content.WeeklyTopRanking;

public class DayOfWeekManager implements Serializable {
	public static Calendar WORLD_CALENDAR;

	private static final long serialVersionUID = -2974262334575995608L;
	private transient Player player;
	private int savedDay;

	public DayOfWeekManager() {
		savedDay = -1;
	}

	public void init() {
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("EST"));
		if (savedDay != calendar.get(Calendar.DAY_OF_WEEK)) {
			handleNewDay();
			if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY)
				handleNewWeek();
			savedDay = calendar.get(Calendar.DAY_OF_WEEK);
		}
	}

	private void handleNewWeek() {
		player.resetWeeklyVariables();
	}

	public void process() {
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("EST"));
		if (savedDay != calendar.get(Calendar.DAY_OF_WEEK)) {
			handleNewDay();
			if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY)
				handleNewWeek();
			savedDay = calendar.get(Calendar.DAY_OF_WEEK);
		}
	}

	private void handleNewDay() {
		player.getDailyTaskManager().getNewTask(false);
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public int getSavedDay() {
		return savedDay;
	}

	public static void processWorldCalendar() {
		if (WORLD_CALENDAR == null) {
			WORLD_CALENDAR = Calendar.getInstance(TimeZone.getTimeZone("EST"));
		}
		if (WORLD_CALENDAR.get(Calendar.DAY_OF_WEEK) != Calendar.getInstance(TimeZone.getTimeZone("EST"))
				.get(Calendar.DAY_OF_WEEK)) {
			WORLD_CALENDAR = Calendar.getInstance(TimeZone.getTimeZone("EST"));
		}
		//WeeklyTopRanking.process();
	}

}
