package com.rs.game.player.content.clans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.player.Player;
import com.rs.game.player.content.clans.citadels.CitadelNPCS;
import com.rs.game.player.content.clans.citadels.CitadelObjects;
import com.rs.game.player.content.clans.citadels.CitadelResources;
import com.rs.game.player.content.clans.citadels.ClanCitadel;
import com.rs.game.player.content.clans.citadels.actions.plots.PlotStage;
import com.rs.game.player.content.clans.content.ClanBank;
import com.rs.game.player.content.clans.content.ClanResources;
import com.rs.game.player.content.clans.content.ClanRevenue;
import com.rs.game.player.content.clans.content.achievements.ClanAchievements;
import com.rs.game.player.content.clans.content.perks.ClanPerk;
import com.rs.game.player.content.clans.level.ClanLevel;
import com.rs.game.player.content.clans.level.ExperienceType;

public class Clan implements Serializable {

	public static final int RECRUIT = 0, ADMIN = 100, DEPUTY_OWNER = 125, LEADER = 126, MAX_MEMBERS = 15;


	private static final long serialVersionUID = 4062231702422979939L;

	private transient String clanName;
	public String cmHex = "ff0000";
	public String clanMessage;
	private String clanLeaderUsername;
	private List<ClanMember> members;
	private List<String> bannedUsers;
	private int timeZone;
	private boolean recruiting;
	private boolean isClanTime;
	private int worldId;
	private int clanFlag;
	private boolean guestsInChatCanEnter;
	private boolean guestsInChatCanTalk;
	private String threadId;
	private String motto;
	private int mottifTop, mottifBottom;
	private int[] mottifColors;
	private int minimumRankForKick;
	private ClanBank clanBank;
	private ClanCitadel clanCitadel;
	private PlotStage plotStage;
	private CitadelResources citadelResources;
	private CitadelObjects citadelObjects;
	private CitadelNPCS citadelNpcs;
	private ClanAchievements clanAchievements;
	private ClanLevel level;
	private ClanPerk perks;
	private ExperienceType experienceType;
	private ClanResources resources;
	private ClanRevenue revenue;
	

	public Clan(String clanName, Player leader) {
		this.members = new ArrayList<ClanMember>();
		this.bannedUsers = new ArrayList<String>();
		setClanLeaderUsername(addMember(leader, LEADER));
		init(clanName);
		this.clanBank = new ClanBank();
		this.clanCitadel = new ClanCitadel(this);
		this.citadelResources = new CitadelResources();
		this.plotStage = new PlotStage();
		this.citadelObjects = new CitadelObjects(this);
		this.citadelNpcs = new CitadelNPCS(this);
		this.clanAchievements = new ClanAchievements();
		this.level = new ClanLevel(this);
		this.perks = new ClanPerk();
		this.experienceType = new ExperienceType();
		this.resources = new ClanResources(this);
		this.revenue = new ClanRevenue();
		setDefaults();
	}

	public void setDefaults() {
		recruiting = true;
		guestsInChatCanEnter = true;
		guestsInChatCanTalk = true;
		worldId = 1;
		mottifColors = Arrays.copyOf(ItemDefinitions.getItemDefinitions(20709).originalModelColors, 4);
	}

	public void init(String clanName) {
		this.clanName = clanName;
		this.plotStage = new PlotStage();
		if (this.clanBank == null)
			this.clanBank = new ClanBank();
		if(this.clanCitadel == null)
			this.clanCitadel = new ClanCitadel(this);
		if(this.citadelResources == null)
			this.citadelResources = new CitadelResources();
		if(this.citadelObjects == null)
			this.citadelObjects = new CitadelObjects(this);
		if(this.citadelNpcs == null)
			this.citadelNpcs = new CitadelNPCS(this);
		if(this.clanAchievements == null)
			this.clanAchievements = new ClanAchievements();
		if(this.level == null)
			this.level = new ClanLevel(this);
		if(this.perks == null)
			this.perks = new ClanPerk();
		if(this.experienceType == null)
			this.experienceType = new ExperienceType();
		if(this.resources == null)
			this.resources = new ClanResources(this);
		if(this.revenue == null)
			this.revenue = new ClanRevenue();
	}

	public ClanMember getMemberByName(String username) {
		for (ClanMember member : members) {
			if (member.getUsername().toLowerCase().equals(username.toLowerCase())) {
				return member;
			}
		}
		return null;
	}

	public ClanMember addMember(Player player, int rank) {
		ClanMember member = new ClanMember(player.getUsername(), rank);
		members.add(member);
		return member;
	}


	public void setClanLeaderUsername(ClanMember member) {
		clanLeaderUsername = member.getUsername();
	}

	public int getMemberId(ClanMember member) {
		return members.indexOf(member);
	}

	public List<ClanMember> getMembers() {
		return members;
	}

	public List<ClanMember> getMembers(String string) {
		return members;
	}

	public List<String> getBannedUsers() {
		return bannedUsers;
	}

	public Clan getClanName(String clanName) {
		this.clanName = clanName;
		return this;
	}

	public String getClanName() {
		return clanName;
	}

	public int getTimeZone() {
		return timeZone;
	}

	public boolean isRecruiting() {
		return recruiting;
	}

	public void switchRecruiting() {
		recruiting = !recruiting;
	}

	public void setTimeZone(int gameTime) {
		this.timeZone = gameTime;
	}

	public int getMinimumRankForKick() {
		return minimumRankForKick;
	}

	public void setMinimumRankForKick(int minimumRankForKick) {
		this.minimumRankForKick = minimumRankForKick;
	}

	public String getThreadId() {
		return threadId;
	}

	public void setThreadId(String threadId) {
		this.threadId = threadId;
	}

	public void getClanMessageHex(Player player) {
		player.getPackets().sendInputNameScript("Set your clan message hex color...");
		player.getTemporaryAttributtes().put("clanmessage_hex", true);
	}

	public boolean isGuestsInChatCanEnter() {
		return guestsInChatCanEnter;
	}

	public void switchGuestsInChatCanEnter() {
		this.guestsInChatCanEnter = !guestsInChatCanEnter;
	}

	public String getMotto() {
		return motto;
	}

	public void setMotto(String motto) {
		this.motto = motto;
	}

	public boolean isClanTime() {
		return isClanTime;
	}

	public void switchClanTime() {
		isClanTime = !isClanTime;
	}

	public int getWorldId() {
		return worldId;
	}

	public void setWorldId(int worldId) {
		this.worldId = worldId;
	}

	public int getClanFlag() {
		return clanFlag;
	}

	public void setClanFlag(int clanFlag) {
		this.clanFlag = clanFlag;
	}

	public String getClanLeaderUsername() {
		return clanLeaderUsername;
	}

	public boolean isGuestsInChatCanTalk() {
		return guestsInChatCanTalk;
	}

	public int getMottifTop() {
		return mottifTop;
	}

	public void setMottifTop(int mottifTop) {
		this.mottifTop = mottifTop;
	}

	public int getMottifBottom() {
		return mottifBottom;
	}

	public void setMottifBottom(int mottifBottom) {
		this.mottifBottom = mottifBottom;
	}

	public int[] getMottifColors() {
		return mottifColors;
	}

	public void setMottifColours(int[] mottifColors) {
		this.mottifColors = mottifColors;
	}

	public void switchGuestsInChatCanTalk() {
		guestsInChatCanTalk = !guestsInChatCanTalk;
	}

	/**
	 * Clan Message
	 */

	public String setClanMessage(String value) {
		return clanMessage = value;
	}


	public String getClanMessage() {
		return clanMessage;

	}

	/**
	 * Hex For Clan Message
	 */

	public String setClanMessageHex(String value) {
		return cmHex = value;
	}

	public String getCmHex() {
		return cmHex;
	}

	/**
	 * Clan Citadels
	 */

	public ClanCitadel getClanCitadel() {
		return clanCitadel;
	}

	public void setClanCitadel(ClanCitadel clanCitadel) {
		this.clanCitadel = clanCitadel;
	}
	
	/**
	 * Plot Stage
	 */
	
	public PlotStage getPlotStage() {
		return plotStage;
	}
	
	/**
	 * Citadel Objects | Npcs
	 */
	
	public CitadelObjects getCitadelObjects() {
		return citadelObjects;
	}
	
	public CitadelNPCS getCitadelNpcs() {
		return citadelNpcs;
	}
	
	/**
	 * Clan Banks
	 */

	public void setClanBank(ClanBank clanBank) {
		this.clanBank = clanBank;
	}
	public ClanBank getClanBank() {
		return clanBank;
	}

	/**
	 * Clan Achievements
	 */

	public ClanAchievements getClanAchievements() {
		return clanAchievements;
	}

	/**
	 * Clan Level
	 */

	public ClanLevel getClanLevel() {
		return level;
	}

	/**
	 * Experience Type
	 */

	public ExperienceType getExperienceType() {
		return experienceType;
	}


	/**
	 * Clan Perks
	 */
	public ClanPerk getClanPerks() {
		return perks;
	}

	/**
	 * Citadel Resources
	 */

	public CitadelResources getCitadelResources() {
		return citadelResources;
	}
	
	/**
	 * Clan Resources
	 */

	public ClanResources getClanResources() {
		return resources;
	}

	/**
	 * Clan Revenue
	 */

	public ClanRevenue getClanRevenue() {
		return revenue;
	}
}
