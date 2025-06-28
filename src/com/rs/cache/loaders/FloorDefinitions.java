package com.rs.cache.loaders;

import java.util.concurrent.ConcurrentHashMap;

import com.rs.cache.Cache;
import com.rs.stream.InputStream;

public final class FloorDefinitions {// underlay defs

	private static final ConcurrentHashMap<Integer, FloorDefinitions> defs = new ConcurrentHashMap<Integer, FloorDefinitions>();

	public int anInt6001;
	public int anInt6002;
	public int anInt6003;
	public boolean aBool6004;
	public boolean aBool6005;
	public int anInt6006 = 0;
	public int anInt6007;
	public int anInt6008;
	public int anInt6009;
	private int id;

	public static final FloorDefinitions getFloorDefinitions(int id) {
		FloorDefinitions script = defs.get(id);
		if (script != null)// open new txt document
			return script;
		byte[] data = Cache.STORE.getIndexes()[2].getFile(1, id);
		script = new FloorDefinitions();
		script.id = id;
		if (data != null)
			script.readValueLoop(new InputStream(data));
		defs.put(id, script);
		return script;

	}

	private FloorDefinitions() {
		anInt6003 = -1;
		anInt6009 = -1;
		aBool6004 = true;
		aBool6005 = true;
	}

	private void readValueLoop(InputStream stream) {
		for (;;) {
			int opcode = stream.readUnsignedByte();
			if (opcode == 0)
				break;
			readValues(stream, opcode);
		}
	}

	void method7486(int i) {
		int red = (i >> 16 & 0xff);
		int green = (i >> 8 & 0xff);
		int blue = (i & 0xff);
		anInt6001 = red;
		anInt6007 = green;
		anInt6002 = blue;
	}

	private void readValues(InputStream stream, int i) {
		if (1 == i) {
			((FloorDefinitions) this).anInt6006 = stream.read24BitInt();
			method7486(anInt6006);
		} else if (i == 2) {
			anInt6009 = stream.readUnsignedShort();
			if (65535 == anInt6009)
				anInt6009 = -1;
		} else if (3 == i)
			anInt6003 = (stream.readUnsignedShort() << 2);
		else if (i == 4)
			aBool6004 = false;
		else if (i == 5)
			aBool6005 = false;
	}

	public int getId() {
		return id;
	}

}
