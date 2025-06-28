package com.rs.cache.loaders;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import com.rs.cache.Cache;
import com.rs.stream.InputStream;
import com.rs.utils.Utils;

public class AreaDefinitions {

	private static final ConcurrentHashMap<Integer, AreaDefinitions> areaDefs = new ConcurrentHashMap<Integer, AreaDefinitions>();
	public int id;
	public int spriteId = -1;
	public int backgroundSpriteId;
	public String name;
	public int anInt2646;
	public int anInt2647;
	public int anInt2663;
	public boolean aBool2640;
	public boolean aBool2655;
	public int configbyfileId;
	public int configId;
	public int anInt2659;
	public int anInt2660;
	public String[] aStringArray2656 = new String[5];
	public int[] anIntArray2668;
	public int anInt2673;
	public int[] anIntArray2681;
	public byte[] aByteArray2682;
	public boolean aBool2684;
	public String aString2649;
	public int anInt2658;
	public int type;
	public int configByFileId2;
	public int configId2;
	public int anInt2677;
	public int anInt2674;
	public int anInt2653;
	public int anInt2652;
	public int anInt2644;
	public int anInt2675;
	public int anInt2676;
	public int anInt2641;
	public int anInt2678;
	public int newSpriteId = -1;
	public int[] anIntArray2667;
	public int anInt2683;
	public int[] aClass278_2679;
	public int[] aClass290_2666;
	public HashMap<Integer, Object> clientScriptData;

	public static void main(String[] args) throws IOException {
		Cache.init();
		System.out.println(Utils.getAreaDefinitionsSize());
		for (int i = 0; i < Utils.getAreaDefinitionsSize(); i++) {
			AreaDefinitions defs = AreaDefinitions.getAreaDefinitions(i);
			if ((defs.anIntArray2667 != null && (defs.anIntArray2667[1] == 3458 || defs.anIntArray2667[0] == 3458
					|| defs.anIntArray2667[2] == 3458))) {
				System.out.println(defs);
			}
		}
		AreaDefinitions oldDefs = AreaDefinitions.getAreaDefinitions(581);
		System.out.println(oldDefs);
	}

	@Override
	public String toString() {
		return "AreaDefinitions [id=" + id + ", spriteId=" + spriteId + ", backgroundSpriteId=" + backgroundSpriteId
				+ ", name=" + name + ", anInt2646=" + anInt2646 + ", anInt2647=" + anInt2647 + ", anInt2663="
				+ anInt2663 + ", aBool2640=" + aBool2640 + ", aBool2655=" + aBool2655 + ", configbyfileId="
				+ configbyfileId + ", configId=" + configId + ", anInt2659=" + anInt2659 + ", anInt2660=" + anInt2660
				+ ", aStringArray2656=" + Arrays.toString(aStringArray2656) + ", anIntArray2668="
				+ Arrays.toString(anIntArray2668) + ", anInt2673=" + anInt2673 + ", anIntArray2681="
				+ Arrays.toString(anIntArray2681) + ", aByteArray2682=" + Arrays.toString(aByteArray2682)
				+ ", aBool2684=" + aBool2684 + ", aString2649=" + aString2649 + ", anInt2658=" + anInt2658 + ", type="
				+ type + ", configByFileId2=" + configByFileId2 + ", configId2=" + configId2 + ", anInt2677="
				+ anInt2677 + ", anInt2674=" + anInt2674 + ", anInt2653=" + anInt2653 + ", anInt2652=" + anInt2652
				+ ", anInt2644=" + anInt2644 + ", anInt2675=" + anInt2675 + ", anInt2676=" + anInt2676 + ", anInt2641="
				+ anInt2641 + ", anInt2678=" + anInt2678 + ", newSpriteId=" + newSpriteId + ", anIntArray2667="
				+ Arrays.toString(anIntArray2667) + ", anInt2683=" + anInt2683 + ", aClass278_2679="
				+ Arrays.toString(aClass278_2679) + ", aClass290_2666=" + Arrays.toString(aClass290_2666)
				+ ", clientScriptData=" + clientScriptData + "]";
	}

	public static final AreaDefinitions getAreaDefinitions(int areaId) {
		try {
			AreaDefinitions defs = areaDefs.get(areaId);
			if (defs != null)
				return defs;
			byte[] data = Cache.STORE.getIndexes()[2].getFile(36, areaId);
			defs = new AreaDefinitions();
			defs.id = areaId;
			if (data != null)
				defs.readValueLoop(new InputStream(data));
			areaDefs.put(areaId, defs);
			return defs;
		} catch (Throwable t) {
			t.printStackTrace();
			return null;
		}
	}

	private void readValueLoop(InputStream stream) {
		for (;;) {
			int opcode = stream.readUnsignedByte();
			if (opcode == 0)
				break;
			readValues(stream, opcode);
		}
	}

	private void readValues(InputStream buffer, int opcode) {
		if (1 == opcode)
			spriteId = buffer.readBigSmart();
		else if (opcode == 2)
			backgroundSpriteId = buffer.readBigSmart();
		else if (3 == opcode) {
			name = buffer.readString();
		} else if (4 == opcode)
			anInt2646 = buffer.read24BitInt();
		else if (5 == opcode)
			anInt2647 = buffer.read24BitInt();
		else if (opcode == 6)
			anInt2663 = buffer.readUnsignedByte();
		else if (7 == opcode) {
			int i_2_ = buffer.readUnsignedByte();
			if (0 == (i_2_ & 0x1))
				aBool2640 = false;
			if ((i_2_ & 0x2) == 2)
				aBool2655 = true;
		} else if (8 == opcode)
			buffer.readUnsignedByte();
		else if (9 == opcode) {
			configbyfileId = buffer.readUnsignedShort();
			if (65535 == configbyfileId)
				configbyfileId = -1;
			configId = buffer.readUnsignedShort();
			if (configId == 65535)
				configId = -1;
			anInt2659 = buffer.readInt();
			anInt2660 = buffer.readInt();
		} else if (opcode >= 10 && opcode <= 14)
			aStringArray2656[opcode - 10] = buffer.readString();
		else if (opcode == 15) {
			int i_3_ = buffer.readUnsignedByte();
			anIntArray2668 = new int[2 * i_3_];
			for (int i_4_ = 0; i_4_ < 2 * i_3_; i_4_++)
				anIntArray2668[i_4_] = buffer.readShort();
			anInt2673 = buffer.readInt();
			int i_5_ = buffer.readUnsignedByte();
			anIntArray2681 = new int[i_5_];
			for (int i_6_ = 0; i_6_ < anIntArray2681.length; i_6_++)
				anIntArray2681[i_6_] = buffer.readInt();
			aByteArray2682 = new byte[i_3_];
			for (int i_7_ = 0; i_7_ < i_3_; i_7_++)
				aByteArray2682[i_7_] = (byte) buffer.readByte();
		} else if (opcode == 16)
			aBool2684 = false;
		else if (17 == opcode)
			aString2649 = buffer.readString();
		else if (18 == opcode)
			anInt2658 = buffer.readBigSmart();
		else if (19 == opcode)
			type = buffer.readUnsignedShort();
		else if (20 == opcode) {
			configByFileId2 = buffer.readUnsignedShort();
			if (configByFileId2 == 65535)
				configByFileId2 = -1;
			configId2 = buffer.readUnsignedShort();
			if (configId2 == 65535)
				configId2 = -1;
			anInt2677 = buffer.readInt();
			anInt2674 = buffer.readInt();
		} else if (opcode == 21)
			anInt2653 = buffer.readInt();
		else if (22 == opcode)
			anInt2652 = buffer.readInt();
		else if (opcode == 23) {
			anInt2644 = buffer.readUnsignedByte();
			anInt2675 = buffer.readUnsignedByte();
			anInt2676 = buffer.readUnsignedByte();
		} else if (opcode == 24) {
			anInt2641 = buffer.readShort();
			anInt2678 = buffer.readShort();
		} else if (25 == opcode) {// new
			newSpriteId = buffer.readBigSmart();
		} else if (opcode == 26 || opcode == 27) {
			configbyfileId = buffer.readUnsignedShort();
			if (65535 == (configbyfileId)) {
				configbyfileId = -1;
			}
			configId = buffer.readUnsignedShort();
			if (65535 == (configId)) {
				configId = -1;
			}
			int i_15_ = -1;
			if (27 == opcode) {
				i_15_ = buffer.readUnsignedShort();
				if (65535 == i_15_) {
					i_15_ = -1;
				}
			}
			int i_16_ = buffer.readUnsignedByte();
			anIntArray2667 = new int[i_16_ + 2];// transforms.
			for (int i_17_ = 0; i_17_ <= i_16_; i_17_++) {
				anIntArray2667[i_17_] = buffer.readUnsignedShort();
				if (anIntArray2667[i_17_] == 65535) {
					anIntArray2667[i_17_] = -1;
				}
			}
			anIntArray2667[i_16_ + 1] = i_15_;
		} else if (opcode == 28) {
			anInt2683 = buffer.readUnsignedByte();
		} else if (29 == opcode) {
			aClass278_2679 = new int[2];
			switch (buffer.readUnsignedByte()) {
			case 0:
				aClass278_2679 = new int[] { 0, 2 };
				break;
			case 1:
				aClass278_2679 = new int[] { 1, 1 };
				break;
			case 2:
				aClass278_2679 = new int[] { 2, 0 };
				break;
			}
		} else if (opcode == 30) {
			aClass290_2666 = new int[2];
			switch (buffer.readUnsignedByte()) {
			case 0:
				aClass290_2666 = new int[] { 0, 0 };
				break;
			case 1:
				aClass290_2666 = new int[] { 1, 2 };
				break;
			case 2:
				aClass290_2666 = new int[] { 2, 1 };
				break;
			}
		} else if (opcode == 249) {
			int length = buffer.readUnsignedByte();
			if (clientScriptData == null)
				clientScriptData = new HashMap<Integer, Object>(length);
			for (int index = 0; index < length; index++) {
				boolean stringInstance = buffer.readUnsignedByte() == 1;
				int key = buffer.read24BitInt();
				Object value = stringInstance ? buffer.readString() : buffer.readInt();
				clientScriptData.put(key, value);
			}
		}
	}
}
