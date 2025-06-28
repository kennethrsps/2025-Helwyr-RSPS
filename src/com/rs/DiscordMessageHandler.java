//package com.rs;
//
//
//import com.rs.game.World;
//import com.rs.game.player.Player;
//import com.rs.game.tasks.WorldTask;
//import com.rs.game.tasks.WorldTasksManager;
//import com.rs.utils.Logger;
//
//import sx.blah.discord.api.ClientBuilder;
//import sx.blah.discord.api.IDiscordClient;
//import sx.blah.discord.api.events.EventSubscriber;
//import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
//import sx.blah.discord.handle.obj.IChannel;
//import sx.blah.discord.util.DiscordException;
//import sx.blah.discord.util.EmbedBuilder;
//import sx.blah.discord.util.RequestBuffer;
//
//public class DiscordMessageHandler{
//	
//	private static IDiscordClient cli;
//	
//	public static void init(){
//		cli = getBuiltDiscordClient("c4875f260c4fb83d758fb4e09eca2bcf50004b6e281a5f3fec7c5b88a3a829c4");
//		cli.getDispatcher().registerListener(new DiscordMessageHandler());
//	    cli.login();
//	    loginMessage();
//	}
//	
//	 public static IDiscordClient getBuiltDiscordClient(String token){
//	        return new ClientBuilder()
//	                .withToken(token)
//	                .build();
//
//	    }
//	
//	private static void loginMessage() {
//		WorldTasksManager.schedule(new WorldTask() {
//			@Override
//			public void run() {
//				try {
//					if(cli.getShardCount() != -1) {
//					sendMessage("ingame-live-feed",":white_check_mark: Helwyr is ONLINE :white_check_mark:");
//					stop();
//					}
//				} catch (Throwable e) {
//					Logger.handle(e);
//				}
//			}
//			
//		},5, 1);
//		
//	}
//
//	String BOT_PREFIX = ";;";
//	
//	@EventSubscriber
//	public void onMessageReceived(MessageReceivedEvent event){
//		if(event.getMessage().getContent().startsWith(BOT_PREFIX) &&!event.getChannel().getName().equalsIgnoreCase("bot-commands") && !(event.getAuthor().getName().equalsIgnoreCase("zeus"))) {
//			sendMessagetoDiscord(event,"You are only allowed to use a command at #bot-commands");
//			return;
//		}
//		if(event.getMessage().getContent().startsWith(BOT_PREFIX + "commands"))
//	        	sendMessagetoDiscord(event,"Commands Available: ping,players,totalexp");
//        if(event.getMessage().getContent().startsWith(BOT_PREFIX + "ping"))
//        	sendMessagetoDiscord(event,"Pong");
//        if(event.getMessage().getContent().startsWith(BOT_PREFIX + "players"))
//        	sendMessagetoDiscord(event, "Currently Online Players: "+World.getPlayersOnline());      
//        
//        if(event.getMessage().getContent().startsWith(BOT_PREFIX + "totalexp")){
//        	if(!World.isOnline(event.getAuthor().getName())) {
//        		sendMessagetoDiscord(event,"You need to be ingame to check your total exp.");
//        		return;
//        	}
//	        for(Player p : World.getPlayers()) {
//	        	if(p.getUsername().equalsIgnoreCase(event.getAuthor().getName())) {
//		        	sendMessagetoDiscord(event,p.getDisplayName()+" total exp is "+p.getSkills().getTotalXp());
//		        	break;
//	        	}
//	        }
//        }
//    }
//
//	private void sendMessagetoDiscord(MessageReceivedEvent event, String message) {
//		sendMessage(event.getChannel(), message);
//	}
//	
//	
//	public static void sendMessage(String channelName, String message) {
//		for(IChannel channel : cli.getChannels()) {
//			if(channel.getName().equalsIgnoreCase(channelName)) {
//				sendMessage(channel, message);
//				break;
//			}
//		}
//	}
//	
//	public static void sendMessage(IChannel channel, String message){
//       RequestBuffer.request(() -> {
//            try{
//                channel.sendMessage(message);
//            } catch (DiscordException e){
//                System.err.println("Message could not be sent with error: ");
//                e.printStackTrace();
//            }
//        });
//
//    }
//	
//	public static void embedMessage(Player player) {
//		 EmbedBuilder builder = new EmbedBuilder();
//
//		    builder.appendField("COMMANDS", "99", true);
//		    builder.appendField("Description", "millionsofexp9999", true);
//		    builder.withAuthorName("LEVEL-UP: "+player.getDisplayName());
//		    builder.withColor(255, 0, 0);
//		    
//		    for(IChannel channel : cli.getChannels()) {
//				if(channel.getName().equalsIgnoreCase("ingame-live-feed")) {
//					RequestBuffer.request(() -> channel.sendMessage(builder.build()));
//					break;
//				}
//			}
//		    
//	}
//	
//	
//
//}
