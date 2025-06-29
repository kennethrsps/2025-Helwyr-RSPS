package com.rs.game.player.content.grandExchange;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

import com.rs.utils.FileUtilities;
import com.rs.utils.Logger;

/**
 * @author Arno
 */
public class UnlimitedGEReader {

	private static ArrayList<Integer> items = new ArrayList<Integer>();
	private final static String TXT_PATH = "./data/grandExchange/unlimitedItems.txt";
	private static FileReader fr;
	
	
	public static void init() {
		try {
			initReaders();
			readToStoreCollection();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private static void readToStoreCollection() throws Exception {
		   for (String lines : FileUtilities.readFile(TXT_PATH)) {
			items.add(Integer.parseInt(lines));
			//System.out.println("[GEReader] : Added item #"+Integer.parseInt(lines)+ " to items<int>");
		}
		Logger.log("Initiated " + items.size() + " unlimited items...");
	}

	private static void initReaders() throws Exception {
			fr = new FileReader(TXT_PATH);
			new BufferedReader(fr);
	}
	
	public static ArrayList<Integer> getLimitedItems() {
		return items;
	}
	
	public static void reloadLimiteditems() {
		try {
			items.clear();
			reloadReaders();
			readToStoreCollection();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private static void reloadReaders() throws Exception {
		fr = null;
		initReaders();
		
	}
	
	public static boolean itemIsLimited(int itemId) {
		for (int i = 0; i < items.size(); i++) {
			if (itemId == items.get(i)) {
				return true;
			}
		}
		return false;
	}
}