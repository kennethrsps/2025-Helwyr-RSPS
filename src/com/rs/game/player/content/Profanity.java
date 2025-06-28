package com.rs.game.player.content;

public class Profanity {

	public static final String[] WORDS = {"ddos", "retard","dickhead","nigger","vagina","penis"
	 ,"scape","SCAPE" ,"www" ,"WWW" ,".com" ,"COM"
	,"net","NET",".org",".ORG","DDOS" ,"RETARD" ,"COCK" ,"VAGINA" ,"PENIS" ,"rsps","RSPS",".co.uk",".CO.UK"
	};//etc ok
	
	public static String processProfanity(final String msg) {
		String message = msg;
		for(String word : WORDS) {
			if(message.toLowerCase().contains(word.toLowerCase())) {
				String stars = getStars(word);
				message = message.replaceAll(word, stars);
			}
		}
		return message;
	}
	
	public static String getStars(String word) {
		String stars = "";
		int chars = word.length();
		for(int i=0;i<chars;i++) {
			stars += "*";
		}
		return stars;
	}
	
}
