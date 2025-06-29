package com.rs.tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.rs.cache.Cache;
import com.rs.game.player.Player;
import com.rs.utils.SerializableFilesManager;
import com.rs.utils.Utils;

public class EconomyChecker {
	
	public static String[] ITEMS = { "null" };
	
	private static long playersChecked, playersFailed, playersFound;

    public static void main(String[] args) throws ClassNotFoundException, IOException {
        Cache.init();
        File file = new File("CoinCheck.txt");
    	if (file.exists())
    	    file.delete();
    	else
    	    file.createNewFile();
    	BufferedWriter writer = new BufferedWriter(new FileWriter(file));
    	writer.flush();
        File[] chars = new File("data/playersaves/characters").listFiles();
        for (File acc : chars) {
            try {
                Player player = (Player) SerializableFilesManager.loadSerializedFile(acc);
                
                if (player.money >= 250000000 
                		|| player.getInventory().getNumberOf(995) >= 250000000 
                		|| player.getBank().getItem(995) != null && player.getBank().containsItem(995, 250000000)
                		) {
                	String message = acc.getName()+" contains; in pouch: "+Utils.getFormattedNumber(player.money)+"; "
                			+ "in inventory: "+Utils.getFormattedNumber(player.getInventory().getNumberOf(995))+"; "
        					+ "in bank: "+Utils.getFormattedNumber(player.getBank().getNumberOf(995));
                	System.err.println(message);
                	writer.append(message);
            	    writer.newLine();
            	    writer.flush();
            	    playersFound ++;
                }
				
                playersChecked ++;
                SerializableFilesManager.storeSerializableClass(player, acc);
            } catch (Throwable e) {
                e.printStackTrace();
                System.out.println("failed: " + acc.getName());
                playersFailed ++;
            }
        }
        String message = "Players checked: "+playersChecked+"; failed: "+playersFailed+"; found: "+playersFound
        		+" with high wealth.";
        System.out.println("Done. "+message);
	    writer.newLine();
	    writer.flush();
        writer.append(message);
	    writer.flush();
        writer.close();
    }
}