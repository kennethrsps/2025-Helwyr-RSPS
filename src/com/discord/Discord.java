package com.discord;

import javax.security.auth.login.LoginException;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.AccountType;

/**
 * 
 * Discord.java | 8:06:53 AM
 */
public class Discord {
	private static JDA discord = null;

	public static void startUp() {
		try {
			discord = JDABuilder.createDefault(Constants.TOKEN).build();
			discord.addEventListener(new BotListener());
		} catch (Exception e) {
			e.printStackTrace();
		}
		discord.addEventListener(new BotListener());
	}

	public static JDA getJDA() {
		return discord;
	}

	public static void sendNewmembers(String message) {
		if (discord == null) {
			System.out.println("discord error: discord is null.");
			return;
		}
		discord.getTextChannelById(Constants.NEWMEMBERS).sendMessage(message).queue();
	}

	public static void sendEventsMessage(String message) {
		if (discord == null) {
			System.out.println("discord error: discord is null.");
			return;
		}
		discord.getTextChannelById(Constants.EVENTS_CHANNEL).sendMessage(message).queue();
	}

	public static void sendAnnouncementsMessage(String message) {
		if (discord == null) {
			System.out.println("discord error: discord is null.");
			return;
		}
		discord.getTextChannelById(Constants.ANNOUNCEMENTS_CHANNEL).sendMessage(message).queue();
	}

	public static void sendAchievement(String message) {
		if (discord == null) {
			System.out.println("discord error: discord is null.");
			return;
		}
		discord.getTextChannelById(Constants.ACHIEVEMENT).sendMessage(message).queue();
	}

	public static void sendDropMessage(String message, String reciever) {
		if (discord == null) {
			System.out.println("discord error: discord is null.");
			return;
		}
		discord.getTextChannelById(Constants.NEWMEMBERS)
				.sendMessageEmbeds(new EmbedBuilder().setAuthor(reciever, null, null).setDescription(message).build())
				.queue();
	}
}
