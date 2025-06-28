package com.rs.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Iterator;

import com.rs.game.item.Item;
import com.rs.game.player.Player;
import com.rs.game.player.content.Shop;

/**
 * Used to handle Shops.
 * @author Zeus
 */
public class ShopsHandler {

    private static final HashMap<Integer, Shop> handledShops = new HashMap<Integer, Shop>();

    private static final String PACKED_PATH = "data/items/packedShops.s";

    private static final String UNPACKED_PATH = "data/items/unpackedShops.txt";

    public static void addShop(int key, Shop shop) {
    	handledShops.put(Integer.valueOf(key), shop);
    }

    public static Shop getShop(int key) {
    	return handledShops.get(Integer.valueOf(key));
    }

    public static void init() {
    	File file = new File(PACKED_PATH);

    	if (file.exists()) {
    		file.delete();
    	}
    	loadUnpackedShops();
    	loadPackedShops();
    }

    private static void loadPackedShops() {
    	try {
    		RandomAccessFile in = new RandomAccessFile(PACKED_PATH, "r");
    		FileChannel channel = in.getChannel();
    		int key;
    		String name;
    		int money;
    		boolean generalStore;
    		Item items[];
    		for (ByteBuffer buffer = channel.map(java.nio.channels.FileChannel.MapMode.READ_ONLY, 0L, channel.size()); 
    				buffer.hasRemaining(); addShop(key, new Shop(name, money, items, generalStore))) {
    			key = buffer.getInt();
    			name = readAlexString(buffer);
    			money = buffer.getShort() & 0xffff;
    			generalStore = buffer.get() == 1;
    			items = new Item[buffer.get() & 0xff];
    			for (int i = 0; i < items.length; i++) {
    				items[i] = new Item(buffer.getShort() & 0xffff, buffer.getInt());
    			}
    		}
    		channel.close();
    		in.close();
    	} catch (FileNotFoundException e) {
    		e.printStackTrace();
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    }

    public static void loadUnpackedShops() {
    	try {
    		@SuppressWarnings("resource")
    		BufferedReader in = new BufferedReader(new FileReader(UNPACKED_PATH));
    		DataOutputStream out = new DataOutputStream(new FileOutputStream(PACKED_PATH));
    		do {
    			String line = in.readLine();
    			if (line == null)
    				break;
    			if (!line.startsWith("//")) {
    				String splitedLine[] = line.split(" - ", 3);
    				if (splitedLine.length != 3)
    					throw new RuntimeException((new StringBuilder("Invalid list for shop line: ")).append(line).toString());
    				String splitedInform[] = splitedLine[0].split(" ", 3);
    				if (splitedInform.length != 3)
    					throw new RuntimeException((new StringBuilder("Invalid list for shop line: ")).append(line).toString());
    				String splitedItems[] = splitedLine[2].split(" ");
    				int key = Integer.valueOf(splitedInform[0]).intValue();
    				int money = Integer.valueOf(splitedInform[1]).intValue();
    				boolean generalStore = Boolean.valueOf(splitedInform[2]).booleanValue();
    				Item items[] = new Item[splitedItems.length / 2];
    				int count = 0;
    				for (int i = 0; i < items.length; i++)
    					items[i] = new Item(Integer.valueOf(splitedItems[count++]).intValue(), 
    							Integer.valueOf(splitedItems[count++]).intValue());

    				out.writeInt(key);
    				writeAlexString(out, splitedLine[1]);
    				out.writeShort(money);
    				out.writeBoolean(generalStore);
    				out.writeByte(items.length);
    				Item aitem[];
    				int k = (aitem = items).length;
    				for (int j = 0; j < k; j++) {
    					Item item = aitem[j];

    					out.writeShort(item.getId());
    					out.writeInt(item.getAmount());
    				}

    				addShop(key, new Shop(splitedLine[1], money, items, generalStore));
    			}
    		} while (true);
    		in.close();
    		out.close();
    	} catch (FileNotFoundException e) {
    		e.printStackTrace();
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    }

    public static boolean openShop(Player player, int key) {
    	Shop shop = getShop(key);
    /*	if (player.isHCIronMan() && key < 40 
    			&& key != 27 && key != 16 && key != 9 && key != 31 && key != 32 && key != 14) {
    		player.sendMessage("Hardcore ironmen cannot use this shops.");
    		return false;
    	}*/
		if (shop == null)
		    return false;
		else {
			shop.addPlayer(player);
			return true;
		}
    }

    public static String readAlexString(ByteBuffer buffer) {
    	int count = buffer.get() & 0xfff;
    	byte bytes[] = new byte[count];
    	buffer.get(bytes, 0, count);
    	return new String(bytes);
    }

    public static void restoreShops() {
    	Shop shop;
    	for (Iterator<Shop> iterator = handledShops.values().iterator(); 
    			iterator.hasNext(); 
    			shop.restoreItems())
    		shop = iterator.next();
    }

    public static void writeAlexString(DataOutputStream out, String string) throws IOException {
    	byte bytes[] = string.getBytes();
    	out.writeByte(bytes.length);
    	out.write(bytes);
    }
}