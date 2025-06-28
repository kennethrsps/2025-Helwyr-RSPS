package com.rs.game.player;

import java.io.Serializable;
import java.util.ArrayList;
import com.rs.game.World;
import com.rs.utils.Colors;
import com.rs.utils.Utils;

public class MembershipHandler implements Serializable{
	
	/**
	 * @author SUMAIL
	 */
	private static final long serialVersionUID = 4611979741952592223L;
	
		public static void MembershipTimeCheck(Player p, int type){
			switch(type){
			case 1:
				if(p.getLooterPackSubLong() < System.currentTimeMillis()){
					checkStatus(p,true,false,type);
				}else{
					checkStatus(p,false,true, type);
				}
				break;
			case 2:
				if(p.getSkillerPackSubLong() < System.currentTimeMillis()){
					checkStatus(p,true,false, type);
				}else{
					checkStatus(p,false,true, type);
				}
				break;
			case 3:
				if(p.getUtilityPackSubLong() < System.currentTimeMillis()){
					checkStatus(p,true,false, type);
				}else{
					checkStatus(p,false,true, type);
				}
				break;
			case 4:
				if(p.getCombatPackSubLong() < System.currentTimeMillis()){
					checkStatus(p,true,false, type);
				}else{
					checkStatus(p,false,true, type);
				}
				break;
			case 5:
				if(p.getCompletePackSubLong() < System.currentTimeMillis()){
					checkStatus(p,true,false, type);
				}else{
					checkStatus(p,false,true, type);
				}
				break;
			}
		}

	public static void checkStatus(Player p,boolean ended, boolean reportonce, int type) {
		if(ended)
			switch(type){
			case 1:
				looterspack(p,false);
				break;
			case 2:
				skillerspack(p,false);
				break;
			case 3:
				utilitypack(p,false);
				break;
			case 4:
				combatpack(p,false);
				break;
			case 5:
				completepack(p,false);
				break;
			}
		if (reportonce){
			if(World.containsPlayer(p.getUsername())){
				switch(type){
				case 1:
					p.sendMessage(Colors.red+"[PERKS] Your Looters Perk Subscription has: "+Utils.getDaysRemaining(p.getLooterPackSubLong())+" remaining");
					break;
				case 2:
					p.sendMessage(Colors.red+"[PERKS] Your Skillers Perk Subscription has: "+Utils.getDaysRemaining(p.getSkillerPackSubLong())+" remaining");
					break;
				case 3:
					p.sendMessage(Colors.red+"[PERKS] Your Utility Perk Subscription has: "+Utils.getDaysRemaining(p.getUtilityPackSubLong())+" remaining");
					break;
				case 4:
					p.sendMessage(Colors.red+"[PERKS] Your Combatant Perk Subscription has: "+Utils.getDaysRemaining(p.getCombatPackSubLong())+" remaining");
					break;
				case 5:
					p.sendMessage(Colors.red+"[PERKS] Your Complete Perk Subscription has: "+Utils.getDaysRemaining(p.getCompletePackSubLong())+" remaining");
					break;
				}
			}
		}
	}
	
	
	public static void completepack(Player p, boolean enabled){
		if(enabled){
			p.completepack = true;
			addingCompletePerksPackage(p);
			p.setCompletePackSub(1);//month
		}else if(!enabled){
			removingCompletePerkPackage(p);
			p.completepack = false;
			p.sendMessage(Colors.red+"[PERKS] Your Complete Perk Subscription has ended.");
			p.setCompletePackSubLong(0);
		}
	}
	
	public static void combatpack(Player p, boolean enabled){
		if(enabled){
			p.combatantpack = true;
			addingCombatPerksPackage(p);
			p.setCombatPackSub(1);// 1 month
		}else if(!enabled){
			removingCombatPerkPackage(p);
			p.combatantpack = false;
			p.sendMessage(Colors.red+"[PERKS] Your Combatant Perk Subscription has ended.");
			p.setCombatPackSubLong(0);
		}
	}
	
	public static void looterspack(Player p, boolean enabled){
		if(enabled){
			p.looterspack = true;
			addingLooterPerksPackage(p);
			p.setLooterPackSub(1);//1 month
		}else if(!enabled){
			removingLooterPerkPackage(p);
			p.looterspack = false;
			p.sendMessage(Colors.red+"[PERKS] Your Looters Perk Subscription has ended.");
			p.setLooterPackSubLong(0);
		}
	}
	
	public static void skillerspack(Player p, boolean enabled){
		if(enabled){
			p.skillerspack = true;
			addingSkillersPerksPackage(p);
			p.setSkillerPackSub(1);//month
		}else if(!enabled){
			removingSkillersPerkPackage(p);
			p.skillerspack = false;
			p.sendMessage(Colors.red+"[PERKS] Your Skillers Perk Subscription has ended.");
			p.setSkillerPackSubLong(0);
		}
	}
	
	public static void utilitypack(Player p, boolean enabled) {
		if(enabled){
			p.utilitypack = true;
			addingUtilityPerksPackage(p);
			p.setUtilityPackSub(1);//1month
		}else if(!enabled){
			removingUtilityPerkPackage(p);
			p.utilitypack = false;
			p.sendMessage(Colors.red+"[PERKS] Your Utility Perk Subscription has ended.");
			p.setUtilityPackSubLong(0);
		}
	}
	
	private static void removingCompletePerkPackage(Player p) {
		if(p.nonPermaCPerks == null){
			return;
		}
		if(p.nonPermaCPerks.isEmpty()){
			return;
		}
		for(int i = 0 ; i < p.nonPermaCPerks.size(); i++){
			if(p.nonPermaCPerks.get(i).toString().contains("familiarExpert")){
				p.getPerkManager().familiarExpert = false;
				p.nonPermaCPerks.remove(p.nonPermaCPerks.get(i));
			}
			if(p.nonPermaCPerks.get(i).toString().contains("petmaster")){
				p.getPerkManager().petmaster = false;
				p.nonPermaCPerks.remove(p.nonPermaCPerks.get(i));
			}
			if(p.nonPermaCPerks.get(i).toString().contains("basher")){
				p.getPerkManager().basher = false;
				p.nonPermaCPerks.remove(p.nonPermaCPerks.get(i));
			}
			if(p.nonPermaCPerks.get(i).toString().contains("butcher")){
				p.getPerkManager().butcher = false;
				p.nonPermaCPerks.remove(p.nonPermaCPerks.get(i));
			}
			if(p.nonPermaCPerks.get(i).toString().contains("lifeSteal")){
				p.getPerkManager().lifeSteal = false;
				p.nonPermaCPerks.remove(p.nonPermaCPerks.get(i));
			}
	    	if(p.nonPermaCPerks.get(i).toString().contains("regenerator")){
				p.getPerkManager().regenerator = false;
				p.nonPermaCPerks.remove(p.nonPermaCPerks.get(i));
			}
		if(p.nonPermaCPerks.get(i).toString().contains("chargeBefriender")){
			p.getPerkManager().chargeBefriender = false;
			p.nonPermaCPerks.remove(p.nonPermaCPerks.get(i));
		}
		if(p.nonPermaCPerks.get(i).toString().contains("prayerBetrayer")){
				p.getPerkManager().prayerBetrayer = false;
				p.nonPermaCPerks.remove(p.nonPermaCPerks.get(i));
			}
		if(p.nonPermaCPerks.get(i).toString().contains("avasSecret")){
				p.getPerkManager().avasSecret = false;
				p.nonPermaCPerks.remove(p.nonPermaCPerks.get(i));
			}
		if(p.nonPermaCPerks.get(i).toString().contains("dragonTrainer")){
				p.getPerkManager().dragonTrainer = false;
				p.nonPermaCPerks.remove(p.nonPermaCPerks.get(i));
			}
		if(p.nonPermaCPerks.get(i).toString().contains("gwdSpecialist")){
				p.getPerkManager().gwdSpecialist = false;
				p.nonPermaCPerks.remove(p.nonPermaCPerks.get(i));
				}
		if(p.nonPermaCPerks.get(i).toString().contains("dungeon")){
			p.getPerkManager().dungeon = false;
			p.nonPermaCPerks.remove(p.nonPermaCPerks.get(i));
			}
		if(p.nonPermaCPerks.get(i).toString().contains("perslaysion")){
			p.getPerkManager().perslaysion = false;
			p.nonPermaCPerks.remove(p.nonPermaCPerks.get(i));
			}
		if(p.nonPermaCPerks.get(i).toString().contains("bankCommand")){
			p.getPerkManager().bankCommand = false;
			p.nonPermaCPerks.remove(p.nonPermaCPerks.get(i));
		}
		if(p.nonPermaCPerks.get(i).toString().contains("staminaBoost")){
				p.getPerkManager().staminaBoost = false;
				p.nonPermaCPerks.remove(p.nonPermaCPerks.get(i));
			}
		if(p.nonPermaCPerks.get(i).toString().contains("overclocked")){
				p.getPerkManager().overclocked = false;
				p.nonPermaCPerks.remove(p.nonPermaCPerks.get(i));
			}
		if(p.nonPermaCPerks.get(i).toString().contains("elfFiend")){
				p.getPerkManager().elfFiend = false;
				p.nonPermaCPerks.remove(p.nonPermaCPerks.get(i));
			}
		if(p.nonPermaCPerks.get(i).toString().contains("miniGamer")){
				p.getPerkManager().miniGamer = false;
				p.nonPermaCPerks.remove(p.nonPermaCPerks.get(i));
			}
		if(p.nonPermaCPerks.get(i).toString().contains("portsMaster")){
				p.getPerkManager().portsMaster = false;
				p.nonPermaCPerks.remove(p.nonPermaCPerks.get(i));
				}
		if(p.nonPermaCPerks.get(i).toString().contains("investigator")){
			p.getPerkManager().investigator = false;
			p.nonPermaCPerks.remove(p.nonPermaCPerks.get(i));
				}
			if(p.nonPermaCPerks.get(i).toString().contains("greenThumb")){
				p.getPerkManager().greenThumb = false;
				p.nonPermaCPerks.remove(p.nonPermaCPerks.get(i));
			}
		if(p.nonPermaCPerks.get(i).toString().contains("unbreakableForge")){
				p.getPerkManager().unbreakableForge = false;
				p.nonPermaCPerks.remove(p.nonPermaCPerks.get(i));
			}
		if(p.nonPermaCPerks.get(i).toString().contains("sleightOfHand")){
				p.getPerkManager().sleightOfHand = false;
				p.nonPermaCPerks.remove(p.nonPermaCPerks.get(i));
			}
		if(p.nonPermaCPerks.get(i).toString().contains("herbivore")){
				p.getPerkManager().herbivore = false;
				p.nonPermaCPerks.remove(p.nonPermaCPerks.get(i));
			}
		if(p.nonPermaCPerks.get(i).toString().contains("masterFisherman")){
				p.getPerkManager().masterFisherman = false;
				p.nonPermaCPerks.remove(p.nonPermaCPerks.get(i));
			}
		if(p.nonPermaCPerks.get(i).toString().contains("delicateCraftsman")){
				p.getPerkManager().delicateCraftsman = false;
				p.nonPermaCPerks.remove(p.nonPermaCPerks.get(i));
				}
		if(p.nonPermaCPerks.get(i).toString().contains("masterChef")){
			p.getPerkManager().masterChef = false;
			p.nonPermaCPerks.remove(p.nonPermaCPerks.get(i));
			}
		if(p.nonPermaCPerks.get(i).toString().contains("masterDiviner")){
			p.getPerkManager().masterDiviner = false;
			p.nonPermaCPerks.remove(p.nonPermaCPerks.get(i));
			}
		if(p.nonPermaCPerks.get(i).toString().contains("quarryMaster")){
			p.getPerkManager().quarryMaster = false;
			p.nonPermaCPerks.remove(p.nonPermaCPerks.get(i));
			}
		if(p.nonPermaCPerks.get(i).toString().contains("masterFledger")){
			p.getPerkManager().masterFledger = false;
			p.nonPermaCPerks.remove(p.nonPermaCPerks.get(i));
			}
		if(p.nonPermaCPerks.get(i).toString().contains("thePiromaniac")){
			p.getPerkManager().thePiromaniac = false;
			p.nonPermaCPerks.remove(p.nonPermaCPerks.get(i));
			}
		if(p.nonPermaCPerks.get(i).toString().contains("huntsman")){
			p.getPerkManager().huntsman = false;
			p.nonPermaCPerks.remove(p.nonPermaCPerks.get(i));
			}
		if(p.nonPermaCPerks.get(i).toString().contains("divineDoubler")){
			p.getPerkManager().divineDoubler = false;
			p.nonPermaCPerks.remove(p.nonPermaCPerks.get(i));
			}
		if(p.nonPermaCPerks.get(i).toString().contains("imbuedFocus")){
			p.getPerkManager().imbuedFocus = false;
			p.nonPermaCPerks.remove(p.nonPermaCPerks.get(i));
			}
		if(p.nonPermaCPerks.get(i).toString().contains("alchemicSmith")){
			p.getPerkManager().alchemicSmith = false;
			p.nonPermaCPerks.remove(p.nonPermaCPerks.get(i));
				}
		if(p.nonPermaCPerks.get(i).toString().contains("birdMan")){
			p.getPerkManager().birdMan = false;
			p.nonPermaCPerks.remove(p.nonPermaCPerks.get(i));
		}
		if(p.nonPermaCPerks.get(i).toString().contains("charmCollector")){
			p.getPerkManager().charmCollector = false;
			p.nonPermaCPerks.remove(p.nonPermaCPerks.get(i));
		}
		if(p.nonPermaCPerks.get(i).toString().contains("coinCollector")){
				p.getPerkManager().coinCollector = false;
				p.nonPermaCPerks.remove(p.nonPermaCPerks.get(i));
			}
		if(p.nonPermaCPerks.get(i).toString().contains("keyExpert")){
				p.getPerkManager().keyExpert = false;
				p.nonPermaCPerks.remove(p.nonPermaCPerks.get(i));
			}
		if(p.nonPermaCPerks.get(i).toString().contains("petChanter")){
				p.getPerkManager().petChanter = false;
				p.nonPermaCPerks.remove(p.nonPermaCPerks.get(i));
			}
		if(p.nonPermaCPerks.get(i).toString().contains("petLoot")){
				p.getPerkManager().petLoot = false;
				p.nonPermaCPerks.remove(p.nonPermaCPerks.get(i));
				}
			}
		}

	private static void addingCompletePerksPackage(Player p) {
		if(p.nonPermaCPerks == null ){
			p.nonPermaCPerks = new ArrayList<String>();
		}
		if(p.getPerkManager().familiarExpert){
		}else{
			p.nonPermaCPerks.add("familiarExpert");
			p.getPerkManager().familiarExpert = true;
		}
		if(p.getPerkManager().chargeBefriender){
		}else{
			p.nonPermaCPerks.add("chargeBefriender");
			p.getPerkManager().chargeBefriender = true;
		}
		if(p.getPerkManager().prayerBetrayer){
		}else{
			p.nonPermaCPerks.add("prayerBetrayer");
			p.getPerkManager().prayerBetrayer = true;
		}
		if(p.getPerkManager().avasSecret){
		}else{
			p.nonPermaCPerks.add("avasSecret");
			p.getPerkManager().avasSecret = true;
		}
		if(p.getPerkManager().dragonTrainer){
		}else{
			p.nonPermaCPerks.add("dragonTrainer");
			p.getPerkManager().dragonTrainer = true;
		}
		if(p.getPerkManager().gwdSpecialist){
		}else{
			p.nonPermaCPerks.add("gwdSpecialist");
			p.getPerkManager().gwdSpecialist = true;
		}
		if(p.getPerkManager().dungeon){
		}else{
			p.nonPermaCPerks.add("dungeon");
			p.getPerkManager().dungeon = true;
			}
		if(p.getPerkManager().perslaysion){
		}else{
			p.nonPermaCPerks.add("perslaysion");
			p.getPerkManager().perslaysion = true;
			}
		if(p.getPerkManager().bankCommand){
		}else{
			p.nonPermaCPerks.add("bankCommand");
			p.getPerkManager().bankCommand = true;
		}
		if(p.getPerkManager().staminaBoost){
		}else{
			p.nonPermaCPerks.add("staminaBoost");
			p.getPerkManager().staminaBoost = true;
		}
		if(p.getPerkManager().overclocked){
		}else{
			p.nonPermaCPerks.add("overclocked");
			p.getPerkManager().overclocked = true;
			p.sendMessage("overclocked added");
		}
		if(p.getPerkManager().elfFiend){
		}else{
			p.nonPermaCPerks.add("elfFiend");
			p.getPerkManager().elfFiend = true;
		}
		if(p.getPerkManager().miniGamer){
		}else{
			p.nonPermaCPerks.add("miniGamer");
			p.getPerkManager().miniGamer = true;
		}
		if(p.getPerkManager().portsMaster){
		}else{
			p.nonPermaCPerks.add("portsMaster");
			p.getPerkManager().portsMaster = true;
		}
		if(p.getPerkManager().investigator){
		}else{
			p.nonPermaCPerks.add("investigator");
			p.getPerkManager().investigator = true;
			}
		if(p.getPerkManager().greenThumb){
		}else{
			p.nonPermaCPerks.add("greenThumb");
			p.getPerkManager().greenThumb = true;
		}
		if(p.getPerkManager().unbreakableForge){
		}else{
			p.nonPermaCPerks.add("unbreakableForge");
			p.getPerkManager().unbreakableForge = true;
		}
		if(p.getPerkManager().sleightOfHand){
		}else{
			p.nonPermaCPerks.add("sleightOfHand");
			p.getPerkManager().sleightOfHand = true;
		}
		if(p.getPerkManager().herbivore){
		}else{
			p.nonPermaCPerks.add("herbivore");
			p.getPerkManager().herbivore = true;
		}
		if(p.getPerkManager().masterFisherman){
		}else{
			p.nonPermaCPerks.add("masterFisherman");
			p.getPerkManager().masterFisherman = true;
		}
		if(p.getPerkManager().delicateCraftsman){
		}else{
			p.nonPermaCPerks.add("delicateCraftsman");
			p.getPerkManager().delicateCraftsman = true;
		}
		if(p.getPerkManager().masterChef){
		}else{
			p.nonPermaCPerks.add("masterChef");
			p.getPerkManager().masterChef = true;
		}
		if(p.getPerkManager().masterDiviner){
		}else{
			p.nonPermaCPerks.add("masterDiviner");
			p.getPerkManager().masterDiviner = true;
		}
		if(p.getPerkManager().quarryMaster){
		}else{
			p.nonPermaCPerks.add("quarryMaster");
			p.getPerkManager().quarryMaster = true;
		}
		if(p.getPerkManager().masterFledger){
		}else{
			p.nonPermaCPerks.add("masterFledger");
			p.getPerkManager().masterFledger = true;
		}
		if(p.getPerkManager().thePiromaniac){
		}else{
			p.nonPermaCPerks.add("thePiromaniac");
			p.getPerkManager().thePiromaniac = true;
		}
		if(p.getPerkManager().huntsman){
		}else{
			p.nonPermaCPerks.add("huntsman");
			p.getPerkManager().huntsman = true;
		}
		if(p.getPerkManager().divineDoubler){
		}else{
			p.nonPermaCPerks.add("divineDoubler");
			p.getPerkManager().divineDoubler = true;
		}
		if(p.getPerkManager().imbuedFocus){
		}else{
			p.nonPermaCPerks.add("imbuedFocus");
			p.getPerkManager().imbuedFocus = true;
		}
		if(p.getPerkManager().alchemicSmith){
		}else{
			p.nonPermaCPerks.add("alchemicSmith");
			p.getPerkManager().alchemicSmith = true;
		}
		if(p.getPerkManager().birdMan){
		}else{
			p.nonPermaCPerks.add("birdMan");
			p.getPerkManager().birdMan = true;
		}
		if(p.getPerkManager().regenerator){
		}else{
			p.nonPermaCPerks.add("regenerator");
			p.getPerkManager().regenerator = true;
		}
		if(p.getPerkManager().lifeSteal){
		}else{
			p.nonPermaCPerks.add("lifeSteal");
			p.getPerkManager().lifeSteal = true;
		}
		if(p.getPerkManager().petmaster){
		}else{
			p.nonPermaCPerks.add("petmaster");
			p.getPerkManager().petmaster = true;
		}
		if(p.getPerkManager().basher){
		}else{
			p.nonPermaCPerks.add("basher");
			p.getPerkManager().basher = true;
		}
		if(p.getPerkManager().butcher){
		}else{
			p.nonPermaCPerks.add("butcher");
			p.getPerkManager().butcher = true;
		}
		if(p.getPerkManager().charmCollector){
		}else{
			p.nonPermaCPerks.add("charmCollector");
			p.getPerkManager().charmCollector = true;
		}
		if(p.getPerkManager().coinCollector){
		}else{
			p.nonPermaCPerks.add("coinCollector");
			p.getPerkManager().coinCollector = true;
		}
		if(p.getPerkManager().keyExpert){
		}else{
			p.nonPermaCPerks.add("keyExpert");
			p.getPerkManager().keyExpert = true;
		}
		if(p.getPerkManager().petChanter){
		}else{
			p.nonPermaCPerks.add("petChanter");
			p.getPerkManager().petChanter = true;
		}
		if(p.getPerkManager().petLoot){
		}else{
			p.nonPermaCPerks.add("petLoot");
			p.getPerkManager().petLoot = true;
			}
		}

	
	private static void removingCombatPerkPackage(Player p) {
		if(p.nonPermaCombatantPerks == null){
			return;
		}
		if(p.nonPermaCombatantPerks.isEmpty()){
			return;
		}
		for(int i = 0 ; i < p.nonPermaCombatantPerks.size(); i++){
			if(p.nonPermaCombatantPerks.get(i).toString().contains("familiarExpert")){
				p.getPerkManager().familiarExpert = false;
				p.nonPermaCombatantPerks.remove(p.nonPermaCombatantPerks.get(i));
			}
		if(p.nonPermaCombatantPerks.get(i).toString().contains("chargeBefriender")){
				p.getPerkManager().chargeBefriender = false;
				p.nonPermaCombatantPerks.remove(p.nonPermaCombatantPerks.get(i));
			}
		if(p.nonPermaCombatantPerks.get(i).toString().contains("prayerBetrayer")){
				p.getPerkManager().prayerBetrayer = false;
				p.nonPermaCombatantPerks.remove(p.nonPermaCombatantPerks.get(i));
			}
		if(p.nonPermaCombatantPerks.get(i).toString().contains("avasSecret")){
				p.getPerkManager().avasSecret = false;
				p.nonPermaCombatantPerks.remove(p.nonPermaCombatantPerks.get(i));
			}
		if(p.nonPermaCombatantPerks.get(i).toString().contains("dragonTrainer")){
				p.getPerkManager().dragonTrainer = false;
				p.nonPermaCombatantPerks.remove(p.nonPermaCombatantPerks.get(i));
			}
		if(p.nonPermaCombatantPerks.get(i).toString().contains("gwdSpecialist")){
				p.getPerkManager().gwdSpecialist = false;
				p.nonPermaCombatantPerks.remove(p.nonPermaCombatantPerks.get(i));
				}
		if(p.nonPermaCombatantPerks.get(i).toString().contains("dungeon")){
			p.getPerkManager().dungeon = false;
			p.nonPermaCombatantPerks.remove(p.nonPermaCombatantPerks.get(i));
			}
		if(p.nonPermaCombatantPerks.get(i).toString().contains("perslaysion")){
			p.getPerkManager().perslaysion = false;
			p.nonPermaCombatantPerks.remove(p.nonPermaCombatantPerks.get(i));
			}
		}
	}

	private static void addingCombatPerksPackage(Player p) {
		if(p.nonPermaCombatantPerks == null )
			p.nonPermaCombatantPerks = new ArrayList<String>();
		if(p.getPerkManager().familiarExpert){
		}else{
			p.nonPermaCombatantPerks.add("familiarExpert");
			p.getPerkManager().familiarExpert = true;
		}
		if(p.getPerkManager().chargeBefriender){
		}else{
			p.nonPermaCombatantPerks.add("chargeBefriender");
			p.getPerkManager().chargeBefriender = true;
		}
		if(p.getPerkManager().prayerBetrayer){
		}else{
			p.nonPermaCombatantPerks.add("prayerBetrayer");
			p.getPerkManager().prayerBetrayer = true;
		}
		if(p.getPerkManager().avasSecret){
		}else{
			p.nonPermaCombatantPerks.add("avasSecret");
			p.getPerkManager().avasSecret = true;
		}
		if(p.getPerkManager().dragonTrainer){
		}else{
			p.nonPermaCombatantPerks.add("dragonTrainer");
			p.getPerkManager().dragonTrainer = true;
		}
		if(p.getPerkManager().gwdSpecialist){
		}else{
			p.nonPermaCombatantPerks.add("gwdSpecialist");
			p.getPerkManager().gwdSpecialist = true;
		}
		if(p.getPerkManager().dungeon){
		}else{
			p.nonPermaCombatantPerks.add("dungeon");
			p.getPerkManager().dungeon = true;
			}
		if(p.getPerkManager().perslaysion){
		}else{
			p.nonPermaCombatantPerks.add("perslaysion");
			p.getPerkManager().perslaysion = true;
			}
		}


	private static void removingUtilityPerkPackage(Player p) {
		if(p.nonPermaUtilityPerks == null){
			return;
		}
		if(p.nonPermaUtilityPerks.isEmpty()){
			return;
		}
		for(int i = 0 ; i < p.nonPermaUtilityPerks.size(); i++){
			if(p.nonPermaUtilityPerks.get(i).toString().contains("bankCommand")){
				p.getPerkManager().bankCommand = false;
				p.nonPermaUtilityPerks.remove(p.nonPermaUtilityPerks.get(i));
			}
		if(p.nonPermaUtilityPerks.get(i).toString().contains("staminaBoost")){
				p.getPerkManager().staminaBoost = false;
				p.nonPermaUtilityPerks.remove(p.nonPermaUtilityPerks.get(i));
			}
		if(p.nonPermaUtilityPerks.get(i).toString().contains("overclocked")){
				p.getPerkManager().overclocked = false;
				p.nonPermaUtilityPerks.remove(p.nonPermaUtilityPerks.get(i));
			}
		if(p.nonPermaUtilityPerks.get(i).toString().contains("elfFiend")){
				p.getPerkManager().elfFiend = false;
				p.nonPermaUtilityPerks.remove(p.nonPermaUtilityPerks.get(i));
			}
		if(p.nonPermaUtilityPerks.get(i).toString().contains("miniGamer")){
				p.getPerkManager().miniGamer = false;
				p.nonPermaUtilityPerks.remove(p.nonPermaUtilityPerks.get(i));
				p.sendMessage("miniGamer removed");
			}
		if(p.nonPermaUtilityPerks.get(i).toString().contains("portsMaster")){
				p.getPerkManager().portsMaster = false;
				p.nonPermaUtilityPerks.remove(p.nonPermaUtilityPerks.get(i));
				}
		if(p.nonPermaUtilityPerks.get(i).toString().contains("investigator")){
			p.getPerkManager().investigator = false;
			p.nonPermaUtilityPerks.remove(p.nonPermaUtilityPerks.get(i));
			}
		}
	}

	private static void addingUtilityPerksPackage(Player p) {
		if(p.nonPermaUtilityPerks == null ){
			p.nonPermaUtilityPerks = new ArrayList<String>();
		}
		if(p.getPerkManager().bankCommand){
		}else{
			p.nonPermaUtilityPerks.add("bankCommand");
			p.getPerkManager().bankCommand = true;
		}
		if(p.getPerkManager().staminaBoost){
		}else{
			p.nonPermaUtilityPerks.add("staminaBoost");
			p.getPerkManager().staminaBoost = true;
		}
		if(p.getPerkManager().overclocked){
		}else{
			p.nonPermaUtilityPerks.add("overclocked");
			p.getPerkManager().overclocked = true;
		}
		if(p.getPerkManager().elfFiend){
		}else{
			p.nonPermaUtilityPerks.add("elfFiend");
			p.getPerkManager().elfFiend = true;
		}
		if(p.getPerkManager().miniGamer){
		}else{
			p.nonPermaUtilityPerks.add("miniGamer");
			p.getPerkManager().miniGamer = true;
		}
		if(p.getPerkManager().portsMaster){
		}else{
			p.nonPermaUtilityPerks.add("portsMaster");
			p.getPerkManager().portsMaster = true;
		}
		if(p.getPerkManager().investigator){
		}else{
			p.nonPermaUtilityPerks.add("investigator");
			p.getPerkManager().investigator = true;
			}
		}


	private static void removingSkillersPerkPackage(Player p) {
		if(p.nonPermaSkillersPerks == null){
			return;
		}
		if(p.nonPermaSkillersPerks.isEmpty()){
			return;
		}
		for(int i = 0 ; i < p.nonPermaSkillersPerks.size(); i++){
			if(p.nonPermaSkillersPerks.get(i).toString().contains("greenThumb")){
				p.getPerkManager().greenThumb = false;
				p.nonPermaSkillersPerks.remove(p.nonPermaSkillersPerks.get(i));
			}
		if(p.nonPermaSkillersPerks.get(i).toString().contains("unbreakableForge")){
				p.getPerkManager().unbreakableForge = false;
				p.nonPermaSkillersPerks.remove(p.nonPermaSkillersPerks.get(i));
			}
		if(p.nonPermaSkillersPerks.get(i).toString().contains("sleightOfHand")){
				p.getPerkManager().sleightOfHand = false;
				p.nonPermaSkillersPerks.remove(p.nonPermaSkillersPerks.get(i));
			}
		if(p.nonPermaSkillersPerks.get(i).toString().contains("herbivore")){
				p.getPerkManager().herbivore = false;
				p.nonPermaSkillersPerks.remove(p.nonPermaSkillersPerks.get(i));
			}
		if(p.nonPermaSkillersPerks.get(i).toString().contains("masterFisherman")){
				p.getPerkManager().masterFisherman = false;
				p.nonPermaSkillersPerks.remove(p.nonPermaSkillersPerks.get(i));
			}
		if(p.nonPermaSkillersPerks.get(i).toString().contains("delicateCraftsman")){
				p.getPerkManager().delicateCraftsman = false;
				p.nonPermaSkillersPerks.remove(p.nonPermaSkillersPerks.get(i));
				}
		if(p.nonPermaSkillersPerks.get(i).toString().contains("masterChef")){
			p.getPerkManager().masterChef = false;
			p.nonPermaSkillersPerks.remove(p.nonPermaSkillersPerks.get(i));
			}
		if(p.nonPermaSkillersPerks.get(i).toString().contains("masterDiviner")){
			p.getPerkManager().masterDiviner = false;
			p.nonPermaSkillersPerks.remove(p.nonPermaSkillersPerks.get(i));
			}
		if(p.nonPermaSkillersPerks.get(i).toString().contains("quarryMaster")){
			p.getPerkManager().quarryMaster = false;
			p.nonPermaSkillersPerks.remove(p.nonPermaSkillersPerks.get(i));
			}
		if(p.nonPermaSkillersPerks.get(i).toString().contains("masterFledger")){
			p.getPerkManager().masterFledger = false;
			p.nonPermaSkillersPerks.remove(p.nonPermaSkillersPerks.get(i));
			}
		if(p.nonPermaSkillersPerks.get(i).toString().contains("thePiromaniac")){
			p.getPerkManager().thePiromaniac = false;
			p.nonPermaSkillersPerks.remove(p.nonPermaSkillersPerks.get(i));
			}
		if(p.nonPermaSkillersPerks.get(i).toString().contains("huntsman")){
			p.getPerkManager().huntsman = false;
			p.nonPermaSkillersPerks.remove(p.nonPermaSkillersPerks.get(i));
			}
		if(p.nonPermaSkillersPerks.get(i).toString().contains("divineDoubler")){
			p.getPerkManager().divineDoubler = false;
			p.nonPermaSkillersPerks.remove(p.nonPermaSkillersPerks.get(i));
			}
		if(p.nonPermaSkillersPerks.get(i).toString().contains("imbuedFocus")){
			p.getPerkManager().imbuedFocus = false;
			p.nonPermaSkillersPerks.remove(p.nonPermaSkillersPerks.get(i));
			}
		if(p.nonPermaSkillersPerks.get(i).toString().contains("alchemicSmith")){
			p.getPerkManager().alchemicSmith = false;
			p.nonPermaSkillersPerks.remove(p.nonPermaSkillersPerks.get(i));
				}
			}
		}

	private static void addingSkillersPerksPackage(Player p) {
		if(p.nonPermaSkillersPerks == null ){
			p.nonPermaSkillersPerks = new ArrayList<String>();
			p.sendMessage("nonPermaSkillersPerks list was null");
		}
		if(p.getPerkManager().greenThumb){
		}else{
			p.nonPermaSkillersPerks.add("greenThumb");
			p.getPerkManager().greenThumb = true;
		}
		if(p.getPerkManager().unbreakableForge){
		}else{
			p.nonPermaSkillersPerks.add("unbreakableForge");
			p.getPerkManager().unbreakableForge = true;
		}
		if(p.getPerkManager().sleightOfHand){
		}else{
			p.nonPermaSkillersPerks.add("sleightOfHand");
			p.getPerkManager().sleightOfHand = true;
		}
		if(p.getPerkManager().herbivore){
		}else{
			p.nonPermaSkillersPerks.add("herbivore");
			p.getPerkManager().herbivore = true;
		}
		if(p.getPerkManager().masterFisherman){
		}else{
			p.nonPermaSkillersPerks.add("masterFisherman");
			p.getPerkManager().masterFisherman = true;
		}
		if(p.getPerkManager().delicateCraftsman){
		}else{
			p.nonPermaSkillersPerks.add("delicateCraftsman");
			p.getPerkManager().delicateCraftsman = true;
		}
		if(p.getPerkManager().masterChef){
		}else{
			p.nonPermaSkillersPerks.add("masterChef");
			p.getPerkManager().masterChef = true;
		}
		if(p.getPerkManager().masterDiviner){
		}else{
			p.nonPermaSkillersPerks.add("masterDiviner");
			p.getPerkManager().masterDiviner = true;
		}
		if(p.getPerkManager().quarryMaster){
		}else{
			p.nonPermaSkillersPerks.add("quarryMaster");
			p.getPerkManager().quarryMaster = true;
		}
		if(p.getPerkManager().masterFledger){
		}else{
			p.nonPermaSkillersPerks.add("masterFledger");
			p.getPerkManager().masterFledger = true;
		}
		if(p.getPerkManager().thePiromaniac){
		}else{
			p.nonPermaSkillersPerks.add("thePiromaniac");
			p.getPerkManager().thePiromaniac = true;
		}
		if(p.getPerkManager().huntsman){
		}else{
			p.nonPermaSkillersPerks.add("huntsman");
			p.getPerkManager().huntsman = true;
		}
		if(p.getPerkManager().divineDoubler){
		}else{
			p.nonPermaSkillersPerks.add("divineDoubler");
			p.getPerkManager().divineDoubler = true;
		}
		if(p.getPerkManager().imbuedFocus){
		}else{
			p.nonPermaSkillersPerks.add("imbuedFocus");
			p.getPerkManager().imbuedFocus = true;
		}
		if(p.getPerkManager().alchemicSmith){
		}else{
			p.nonPermaSkillersPerks.add("alchemicSmith");
			p.getPerkManager().alchemicSmith = true;
		}
		
	}

	private static void removingLooterPerkPackage(Player p) {
		if(p.nonPermaLootersPerks == null){
			return;
		}
		if(p.nonPermaLootersPerks.isEmpty()){
			return;
		}
		for(int i = 0 ; i < p.nonPermaLootersPerks.size(); i++){
			if(p.nonPermaLootersPerks.get(i).toString().contains("birdMan")){
				p.getPerkManager().birdMan = false;
				p.nonPermaLootersPerks.remove(p.nonPermaLootersPerks.get(i));
			}
		if(p.nonPermaLootersPerks.get(i).toString().contains("charmCollector")){
				p.getPerkManager().charmCollector = false;
				p.nonPermaLootersPerks.remove(p.nonPermaLootersPerks.get(i));
			}
		if(p.nonPermaLootersPerks.get(i).toString().contains("coinCollector")){
				p.getPerkManager().coinCollector = false;
				p.nonPermaLootersPerks.remove(p.nonPermaLootersPerks.get(i));
			}
		if(p.nonPermaLootersPerks.get(i).toString().contains("keyExpert")){
				p.getPerkManager().keyExpert = false;
				p.nonPermaLootersPerks.remove(p.nonPermaLootersPerks.get(i));
			}
		if(p.nonPermaLootersPerks.get(i).toString().contains("petChanter")){
				p.getPerkManager().petChanter = false;
				p.nonPermaLootersPerks.remove(p.nonPermaLootersPerks.get(i));
			}
		if(p.nonPermaLootersPerks.get(i).toString().contains("petLoot")){
				p.getPerkManager().petLoot = false;
				p.nonPermaLootersPerks.remove(p.nonPermaLootersPerks.get(i));
				}
			}
		}

	private static void addingLooterPerksPackage(Player p) {
		if(p.nonPermaLootersPerks == null ){
			p.nonPermaLootersPerks = new ArrayList<String>();
		}
		if(p.getPerkManager().birdMan){
		}else{
			p.nonPermaLootersPerks.add("birdMan");
			p.getPerkManager().birdMan = true;
		}
		if(p.getPerkManager().charmCollector){
		}else{
			p.nonPermaLootersPerks.add("charmCollector");
			p.getPerkManager().charmCollector = true;
		}
		if(p.getPerkManager().coinCollector){
		}else{
			p.nonPermaLootersPerks.add("coinCollector");
			p.getPerkManager().coinCollector = true;
		}
		if(p.getPerkManager().keyExpert){
		}else{
			p.nonPermaLootersPerks.add("keyExpert");
			p.getPerkManager().keyExpert = true;
		}
		if(p.getPerkManager().petChanter){
		}else{
			p.nonPermaLootersPerks.add("petChanter");
			p.getPerkManager().petChanter = true;
		}
		if(p.getPerkManager().petLoot){
		}else{
			p.nonPermaLootersPerks.add("petLoot");
			p.getPerkManager().petLoot = true;
		}
	}
	
	

}
