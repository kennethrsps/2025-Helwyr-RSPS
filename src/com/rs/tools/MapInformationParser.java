package com.rs.tools;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import com.rs.utils.Logger;

/**
 * @author Zeus
 * Adopted to Ordinance 718/RS3
 * @category Used to parse missing item definitions information from a .txt file,
 *           dumped from an #876 cache.
 */

public class MapInformationParser {
	
	private static final HashMap<Integer,HashMap<Object,Object>> DATA_MAP = new HashMap<Integer,HashMap<Object,Object>>();
	
	public static void init() {
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader("./data/gsmapdump.txt"));
			String line;
			Object key = null, value = null, script = null;
			while((line = reader.readLine()) != null) {
				String intval = line.substring(7, line.indexOf(" "));
				try {
					script = Integer.valueOf(intval);
				} catch (NumberFormatException e) {
					Logger.handle(e);
				}
				DATA_MAP.put((Integer)script,new HashMap<Object,Object>());
				String lineData = line.substring(line.indexOf("{")+1, line.indexOf("}"));
				String[] data = lineData.split(", ");
				for (int i = 0; i < data.length; i++) {
					String[] mapdata = data[i].split("=");
					if (mapdata.length != 2)
						continue;
					key = mapdata[0];
					value = mapdata[1];
					if (key != null && value != null) {
						HashMap<Object,Object> map = DATA_MAP.get((Integer) script);
						if (map != null)
							map.put(key, value);
					}
				}
			}
			reader.close();
			System.out.println("[" + MapInformationParser.class.getName() + "] loaded CSMap. Map Size:="+DATA_MAP.size());
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static HashMap<Integer, HashMap<Object,Object>> getMap() {
		return DATA_MAP;
	}
	
	
	/**
	 * Prevents instantiation
	 */
	private MapInformationParser() {
		
	}
	
}