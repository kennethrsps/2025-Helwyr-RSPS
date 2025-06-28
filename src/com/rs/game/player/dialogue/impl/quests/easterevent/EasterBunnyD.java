package com.rs.game.player.dialogue.impl.quests.easterevent;

import java.awt.Color;

import com.rs.Settings;
import com.rs.game.Animation;
import com.rs.game.Graphics;
import com.rs.game.World;
import com.rs.game.WorldTile;
import com.rs.game.player.dialogue.Dialogue;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Colors;

public class EasterBunnyD extends Dialogue{
	
	private int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		if(player.getEasterStage() == 0){
		sendNPCDialogue(npcId, 9827, "It looks like everyone is excited for Easter but they do not know that I"
					+ " came unprepared and I don't have much time left. Would you like to lend me some help?"
					+ " I'll give you easterrrrriffic rewards!??");
		}else if(player.getEasterStage() == 1 && npcId == 7197){//bunny
			sendNPCDialogue(npcId, 9827, "Please help me gather all of each easter eggs from my companions."
					+ " Please go talk to my Paele, he is located at the Lumbridge Castle.");
			phase = 1000;
		}else if(player.getEasterStage() == 1 && npcId == 7890){//guardsman peale
			sendNPCDialogue(npcId, 9827, "Oh! An adventurer of Helwyr. What brings you here?");
			phase = 100;
		}else if(player.getEasterStage() == 2 && npcId == 7890){//guardsman peale
			sendNPCDialogue(npcId,9827,"It seems that you haven't found it yet, i think i was on the second floor a while ago, "
					+ "please help me find it and I will give you an easter egg in exchange");
			phase = 104;
		}else if(player.getEasterStage() == 3 && npcId == 7890){//guardsman peale
			sendNPCDialogue(npcId,9827,"Are you going to help me now? "
					+ "I somehow misplaced something inside the Lumbridge Castle, please "
					+"help me find it and I will give you an easter egg in exchange");
			phase = 104;
		}else if(player.getEasterStage() == 4 && npcId == 7890 && !player.getInventory().containsItem(20475,1)){//guardsman peale
				sendNPCDialogue(npcId,9827,"You dont have the item!");
				phase = 1000;
		}else if(player.getEasterStage() == 4 && npcId == 7890 && player.getInventory().containsItem(20475,1)){//guardsman peale
			sendNPCDialogue(npcId,9827,"Thank you! This is the *item* that I've been searching for, thank you!");
			player.getInventory().deleteItem(20475, 1);
			player.setEasterStage(5);
			phase = 109;
		}else if(player.getEasterStage() == 5 && npcId == 7197 && player.getInventory().containsItem(11027,1)){//bunny
			sendNPCDialogue(npcId, 9827, "Thank you! We are one step closer toward Easter. "
					+ "Now, please go talk to Charlie. He is located at Varrock, he "
					+ "is such a religious guy.");
			player.getInventory().deleteItem(11027,1);
			player.setEasterStage(6);
			phase = 1000;
		}else if(player.getEasterStage() == 6 && npcId == 7197){//bunny
			sendNPCDialogue(npcId, 9827, "We are one step closer towards Easter! "
					+ "Now, please go talk to Charlie the Tramp. He is located at Varrock, he "
					+ "is such a religious guy.");
			phase = 1000;
		}else if(player.getEasterStage() == 6 && npcId == 641){//charlie
			sendNPCDialogue(npcId, 9827, "What brings you here fellow adventurer?");
			phase = 110;
		}else if(player.getEasterStage() == 7 && npcId == 641){//charlie
			sendNPCDialogue(npcId, 9827, "My egg can be found somewhere inside the Varrock Palace. Please help me find it?");
			phase = 112;
		}else if(player.getEasterStage() == 8 && npcId == 641 && player.getInventory().containsItem(11023,1)){//charlie
			sendNPCDialogue(npcId, 9827, "Finally, you found my egg!");
			player.getInventory().deleteItem(11023,1);
			player.getInventory().addItem(11028, 1);
			player.sendMessage("Charlie handed you an easter egg");
			player.setEasterStage(9);
			phase = 1000;
		}else if(player.getEasterStage() == 9 && npcId == 7197 && player.getInventory().containsItem(11028,1)){//bunny
			sendNPCDialogue(npcId, 9827, "We're getting there! Few more eggs to go");
			player.getInventory().deleteItem(11028,1);
			player.setEasterStage(10);
			phase = 115;
		}else if(player.getEasterStage() == 10 && npcId == 7197){//bunny
			sendNPCDialogue(npcId, 9827, "Please go talk to the musician. He's located at Falador");
			phase = 1000;
		}else if(player.getEasterStage() == 10 && npcId == 5442){//musiscian
			sendNPCDialogue(npcId, 9827, "Well, hello there fellow adventurer! How can I help you?");
			phase = 116;
		}else if(player.getEasterStage() == 11 && npcId == 5442){//musiscian
			sendNPCDialogue(npcId, 9827, "I hid it somewhere that symbolizes this place. Good Luck!");
			phase = 1000;
		}else if(player.getEasterStage() == 12 && npcId == 7197 && player.getInventory().containsItem(11029,1)){//bunny
			sendNPCDialogue(npcId, 9827, "Almost there!");
			player.getInventory().deleteItem(11029, 1);
			player.setEasterStage(13);
			phase = 122;
		}else if(player.getEasterStage() == 13 && npcId == 7197){//bunny
			sendNPCDialogue(npcId, 9827, "Please go talk to Morgan at Draynor Village!");
			phase = 1000;
		}else if(player.getEasterStage() == 13 && npcId == 755){//morgan
			sendPlayerDialogue(9827, "Hello Morgan i was sent by Mr Bunny");
			phase = 124;
		}else if(player.getEasterStage() == 14 && npcId == 755 //morgan
				&& (!player.getInventory().containsItem(1944, 5)//egg
				|| !player.getInventory().containsItem(1933, 5)//pot of flour
				|| !player.getInventory().containsItem(1927, 5))){//bucket of milk
			sendNPCDialogue(npcId, 9827, "Please help me get five eggs, five pots of flour and five buckets of milk. Then, i'll give you an easter egg in return.");
			phase = 1000;
		}else if(player.getEasterStage() == 14 && npcId == 755 //morgan
				&& player.getInventory().containsItem(1944, 5)//egg
				&& player.getInventory().containsItem(1933, 5)//pot of flour
				&& player.getInventory().containsItem(1927, 5)){//bucket of milk
			sendPlayerDialogue(9827, "Here are all the things you need.");
			player.getInventory().deleteItem(1944, 5);
			player.getInventory().deleteItem(1933, 5);
			player.getInventory().deleteItem(1927, 5);
			player.setEasterStage(15);
			phase = 130;
		}else if(player.getEasterStage() == 15 && npcId == 7197 && player.getInventory().containsItem(11030, 1)){//bunny
			sendNPCDialogue(npcId, 9827, "Finally! I now have all the stuffs needed for the preparation of Easter "
					+ "and it's all because of you. As promised, i opened the easter event portal as token of my gratitude."
					+ " just talk to me anytime your ready! . Happy Easter!!");
			player.getInventory().deleteItem(11030, 1);
			player.setEasterStage(16);
			player.getPackets().sendMusicEffect(320);
			player.setNextGraphics(new Graphics(1765));
			World.sendWorldMessage(Colors.purple+"[EASTER] "+player.getUsername()+" has just finished Easter Egg event Storyline! Portal to Wonderland has been secretly opened!", false);
			phase = 1000;
		}else if(player.getEasterStage() == 16 && npcId == 7197){//bunny FINISHED
			sendNPCDialogue(npcId, 9827, "Would you like to enter to the easter event portal?");
			phase = 131;
		}
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch(phase){
		case -1:
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "Yes!", "No");
			phase = 0;
		break;
		
		case 0:
			switch(componentId){
			case OPTION_1:
				player.setEasterStage(1);
				sendNPCDialogue(npcId, 9827, "Great! Please help me gather all of each easter eggs from my companions."
						+ " Please go talk to Peale, he is located at the Lumbridge Castle.");
				break;
				default:
					end();
					break;
			}
			break;
		case 100:
			sendPlayerDialogue(9827, "I was sent by Mr Bunny to gather easter eggs");
			phase = 101;
			break;
		case 101:
			sendNPCDialogue(npcId, 9827, "Oh, sure! It's just that there's a little wee bit problem");
			phase = 102;
			break;
		case 102:
			sendPlayerDialogue(9827, "What is it?");
			phase = 103;
			break;
		case 103:
			sendNPCDialogue(npcId,9827,"I somehow misplaced something inside the Lumbridge Castle, please  "
					+"help me find it and I will give you an easter egg in exchange");
			phase = 104;
			break;
		case 104:
			if(player.getEasterStage() == 3){
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "Alright!", "No, i am still busy.");
			}else{
				sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "Alright!", "No, i have some errands to do");
			}
			phase = 105;
			break;
		case 105:
			switch(componentId){
			case OPTION_1:
				sendNPCDialogue(npcId, 9827, "Thank You! by the way, i was on the third floor a while ago, come back as soon as you find it!");
				player.setEasterStage(2);
				break;
			case OPTION_2:
				sendNPCDialogue(npcId, 9827, "Alright! come back when your ready!");
				player.setEasterStage(3);
				break;
				default:
					end();
					break;
			}
			break;
		case 109:
			sendNPCDialogue(npcId, 9827, "Here take it, good luck with your adventure!");
			player.getInventory().addItem(11027, 1);
			player.sendMessage("Peale handed you an easter egg");
			phase = 1000;
			break;
		case 110:
			sendPlayerDialogue(9827,"I came to help Mr Bunny, he said that you can help me get an easter egg");
			phase = 111;
			break;
		case 111:
			sendNPCDialogue(npcId, 9827,"I'd love to help but I somehow mislaid my egg somewhere inside the Varrock Palace. Please help me find it?");
			phase = 112;
			break;
		case 112:
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "Okay!", "No, i gotta run.");
			phase = 113;
			break;
		case 113:
			switch(componentId){
			case OPTION_1:
				sendNPCDialogue(npcId, 9827,"Cheers!");
				player.setEasterStage(7);
				end();
				break;
			case OPTION_2:
				end();
				break;
			}
			break;
		case 114:
			player.getInventory().deleteItem(11023,1);
			player.getInventory().addItem(11028, 1);
			player.sendMessage("Charlie handed you an easter egg, you should go back to Mr Bunny.");
			player.setEasterStage(9);
			end();
			break;
		case 115:
			sendNPCDialogue(npcId, 9827,"Now, please go talk to the musician. He's located at Falador");
			phase = 1000;
			break;
		case 116:
			sendPlayerDialogue(9827,"Mr Bunny sent me here to help him");
			phase = 117;
			break;
		case 117: 
			sendNPCDialogue(npcId, 9827,"Oh, I see. It seems like he need more easter eggs.");
			phase = 118;
			break;
		case 118:
			sendPlayerDialogue(9827,"Yes");
			phase = 119;
			break;
		case 119:
			sendNPCDialogue(npcId, 9827,"If you want the easter egg, you need to find it somewhere inside Falador.");
			phase = 120;
			break;
		case 120:
			sendPlayerDialogue(9827,"Can you give me a clue?");
			phase = 121;
			break;
		case 121:
			sendNPCDialogue(npcId, 9827,"It ain't that hard, I hid it somewhere that symbolizes this place. I hope you find it.");
			player.setEasterStage(11);
			phase = 1000;
			break;
		case 122:
			sendNPCDialogue(npcId, 9827,"Please go talk to Morgan at Draynor Village."
					+ " He will help you find the fourth easter egg. ");
			phase = 123;
			break;
		case 123:
			sendPlayerDialogue(9827,"Alright, this task is tiring. Welp, it's for Easter.");
			phase = 1000;
			break;
		case 124:
			sendNPCDialogue(npcId, 9827,"What is it that you need?");
			phase = 125;
			break;
		case 125:
			sendPlayerDialogue(9827,"He told me to get an easter egg from you");
			phase = 126;
			break;
		case 126:
			sendNPCDialogue(npcId, 9827,"I can give you one, but I have some errands for you. Are you up for it?");
			phase = 127;
			break;
		case 127:
			sendPlayerDialogue(9827,"Yes, I came all the way from here!");
			phase = 128;
			break;
		case 128:
			sendNPCDialogue(npcId, 9827,"If you say so, please help me get five eggs, "
					+ "five pots of flour and five buckets of milk. Then, i'll give you an easter egg in return.");
			phase = 129;
			break;
		case 129:
			sendPlayerDialogue(9827,"Okay! These people kept giving me tasks. Well i get to excited when its easter!");
			player.setEasterStage(14);
			phase = 1000;
			break;
		case 130:
			sendNPCDialogue(npcId, 9827,"Thanks! Here's your easter egg, you should go back to Mr Bunny, Good Luck!");
			player.getInventory().addItem(11030, 1);
			phase = 1000;
			break;
		case 131:
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "Yes! to PvM Easter!", "Yes, to skillers Easter!", "No, Maybe later.");
			phase = 132;
			break;
		case 132:
			switch(componentId){
			case OPTION_1:
				WorldTasksManager.schedule(new WorldTask() {
					int loop;
					@Override
					public void run() {
						if (loop == 0) {
							player.setNextAnimation(new Animation(17317));
							player.setNextGraphics(new Graphics(3311));
							player.setNextGraphics(new Graphics(3310));
						} else if (loop == 9) {
							player.setNextWorldTile(new WorldTile(2466,5344,0));//pvm
						} else if (loop == 10) {
						player.setNextAnimation(new Animation(-1));
						player.setNextGraphics(new Graphics(3019));
						} else if (loop == 13) {
						player.setNextGraphics(new Graphics(-1));
						stop();
						}
						loop++;
				  		}
			      	}, 0, 1);
				end();
				break;
			case OPTION_2:
				WorldTasksManager.schedule(new WorldTask() {
					int loop;
					@Override
					public void run() {
						if (loop == 0) {
							player.setNextAnimation(new Animation(17317));
							player.setNextGraphics(new Graphics(3311));
							player.setNextGraphics(new Graphics(3310));
						} else if (loop == 9) {
							player.setNextWorldTile(new WorldTile(2467, 5351,0));//skillers
						} else if (loop == 10) {
						player.setNextAnimation(new Animation(-1));
						player.setNextGraphics(new Graphics(3019));
						} else if (loop == 13) {
						player.setNextGraphics(new Graphics(-1));
						stop();
						}
						loop++;
				  		}
			      	}, 0, 1);
				end();
				break;
			case OPTION_3:
				end();
				break;
			}
			break;
		case 1000:
			end();
			break;
			default:
				end();
				break;
		}
		
	}

	@Override
	public void finish() {
		
	}

}
