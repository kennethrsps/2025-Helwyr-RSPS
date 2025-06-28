package com.rs.tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.rs.Settings;
import com.rs.cache.Cache;
import com.rs.cache.loaders.ItemDefinitions;
import com.rs.cache.loaders.NPCDefinitions;
import com.rs.cache.loaders.ObjectDefinitions;
import com.rs.utils.Utils;

public class CompleteDump {

    /**
     * 
     * @param args
     * @throws IOException
     */

    public static void main(String[] args) throws IOException {

		try {
		    Cache.init();
		} catch (IOException e) {
		    e.printStackTrace();
		}
	
		for (int i = 0; i < 8; i++) {
		    if (i == 0) {
		    	File file = new File(Settings.REPOSITORY + "objectList.txt");
		    	if (file.exists())
		    		file.delete();
		    	else
		    		file.createNewFile();
		    	BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		    	writer.append("//Version = " + Settings.REVISION + "/" + Settings.SUB_REVISION + "\n");
		    	writer.flush();
		    	for (int id = 0; id < Utils.getObjectDefinitionsSize(); id++) {
		    		ObjectDefinitions def = ObjectDefinitions.getObjectDefinitions(id);
		    		if (def.name.equalsIgnoreCase("null"))
		    			System.out.println("Skipping null object " + id);
		    		else {
					writer.append(id + " - " + def.name);
					writer.newLine();
					System.out.println(id + " - " + def.name);
					writer.flush();
		    		}
		    	}
		    	writer.close();
		    } else if (i == 2) {
		    	File file = new File(Settings.REPOSITORY + "itemList.txt"); // = new
		    	if (file.exists())
		    		file.delete();
		    	else
		    		file.createNewFile();
		    	BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		    	writer.append("//Version = " + Settings.REVISION + "/" + Settings.SUB_REVISION + "\n");
		    	writer.flush();
		    	for (int id = 0; id < Utils.getItemDefinitionsSize(); id++) {
		    		ItemDefinitions def = ItemDefinitions.getItemDefinitions(id);
		    		if (def.getName().equals("null"))
		    			System.out.println("Skipping null item " + id);
		    		else {
			    		writer.append(id + " - " + def.getName());
			    		writer.newLine();
			    		writer.flush();
		    		}
		    	}
		    	writer.close();
		    } else if (i == 3) {
		    	File file = new File(Settings.REPOSITORY + "npcList.txt");
		    	if (file.exists())
		    		file.delete();
		    	else
		    		file.createNewFile();
		    	BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		    	writer.append("//Version = " + Settings.REVISION + "/" + Settings.SUB_REVISION + "\n");
		    	writer.flush();
		    	for (int id = 0; id < Utils.getNPCDefinitionsSize(); id++) {
		    		NPCDefinitions def = NPCDefinitions.getNPCDefinitions(id);
		    		writer.append(id + " - " + def.name);
		    		writer.newLine();
		    		System.out.println(id + " - " + def.name);
		    		writer.flush();
		    	}
		    	writer.close();
		    }
		}
    }
}