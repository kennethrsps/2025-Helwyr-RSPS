package com.rs.network.protocol.codec.encode;

import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;

/**
 * Created by yak.
 */
public class OutStream {

	private static final byte FIXED_TYPE = 0, VAR_BYTE_TYPE = 1, VAR_SHORT_TYPE = 2;

	private static final int[] BIT_MASK = new int[32];

	static {
		for (int i = 0; i < 32; i++)
			BIT_MASK[i] = (1 << i) - 1;
	}

	private byte[] payload;

	private int offset;

	private int type;

	private int bitPosition;

	private int opcodeStart;

	public OutStream(int size) {
		payload = new byte[size];
	}

	public OutStream sendFixedPacket(int opcode) {
		sendPacket(opcode, FIXED_TYPE);
		return this;
	}

	public OutStream sendVarBytePacket(int opcode) {
		sendPacket(opcode, VAR_BYTE_TYPE);
		return this;
	}

	public OutStream sendVarShortPacket(int opcode) {
		sendPacket(opcode, VAR_SHORT_TYPE);
		return this;
	}

	private void sendPacket(int opcode, int type) {
		if (opcode < 128) {
			addByte(opcode);
		} else {
			addByte(128);
			addByte(opcode);
		}
		switch (this.type = type) {
		case VAR_BYTE_TYPE:
			addByte(0);
			opcodeStart = (offset - 1);
			break;
		case VAR_SHORT_TYPE:
			addShort(0);
			opcodeStart = (offset - 2);
			break;
		}
	}

	public ChannelFuture write(final Channel channel) {
		switch (type) {
		case VAR_BYTE_TYPE:
			addByte(offset - (opcodeStart + 2) + 1, opcodeStart);
			break;
		case VAR_SHORT_TYPE:
			int size = offset - (opcodeStart + 2);
			addByte(size >> 8, opcodeStart++);
			addByte(size, opcodeStart);
			break;
		}
		type = 0;
		if (!channel.isConnected())
			return null;
		return channel.write(ChannelBuffers.wrappedBuffer(payload, 0, offset));
	}

	public void checkCapacityPosition(int position) {
		if (position >= payload.length) {
			byte[] newBuffer = new byte[position * 2];
			System.arraycopy(payload, 0, newBuffer, 0, payload.length);
			payload = newBuffer;
		}
	}

	public OutStream addBytes(byte[] bytes, int srcOffset, int srcLength) {
		checkCapacityPosition(offset + srcLength - srcOffset);
		System.arraycopy(bytes, srcOffset, payload, offset, srcLength);
		offset += (srcLength - srcOffset);
		return this;
	}

	public OutStream addReversedBytes(byte[] bytes, int srcOffset, int srcLength) {
		for (int i = (srcOffset + srcLength - 1); i >= srcOffset; i--)
			addByte(bytes[i]);
		return this;
	}

	public OutStream addByte(int i, int position) {
		checkCapacityPosition(position);
		payload[position] = (byte) i;
		return this;
	}

	public OutStream addByte(int i) {
		return addByte(i, offset++);
	}

	public OutStream addByteA(int i) {
		addByte(i + 128);
		return this;
	}

	public OutStream addByteC(int i) {
		addByte(-i);
		return this;
	}

	public OutStream addByteS(int i) {
		addByte(128 - i);
		return this;
	}

	public OutStream addShort(int i) {
		addByte(i >> 8);
		addByte(i);
		return this;
	}

	public OutStream addShortA(int i) {
		addByte(i >> 8);
		addByte(i + 128);
		return this;
	}

	public OutStream addLEShort(int i) {
		addByte(i);
		addByte(i >> 8);
		return this;
	}

	public OutStream addLEShortA(int i) {
		addByte(i + 128);
		addByte(i >> 8);
		return this;
	}

	public OutStream addTriByte(int i) {
		addByte(i >> 16);
		addByte(i >> 8);
		addByte(i);
		return this;
	}

	public OutStream addInt(int i) {
		addByte(i >> 24);
		addByte(i >> 16);
		addByte(i >> 8);
		addByte(i);
		return this;
	}

	public OutStream addInt1(int i) {
		addByte(i >> 8);
		addByte(i);
		addByte(i >> 24);
		addByte(i >> 16);
		return this;
	}

	public OutStream addInt2(int i) {
		addByte(i >> 16);
		addByte(i >> 24);
		addByte(i);
		addByte(i >> 8);
		return this;
	}

	public OutStream addLEInt(int i) {
		addByte(i);
		addByte(i >> 8);
		addByte(i >> 16);
		addByte(i >> 24);
		return this;
	}

	public void addBigSmart(int i) {
		if (i >= Short.MAX_VALUE)
			addInt(i - Integer.MAX_VALUE - 1);
		else {
			addShort(i >= 0 ? i : 32767);
		}
	}

	public OutStream addSmart(int i) {
		if (i >= 128) {
			addShort(i + 32768);
		} else {
			addByte(i);
		}
		return this;
	}

	public OutStream addLong(long l) {
		addByte((int) (l >> 56));
		addByte((int) (l >> 48));
		addByte((int) (l >> 40));
		addByte((int) (l >> 32));
		addByte((int) (l >> 24));
		addByte((int) (l >> 16));
		addByte((int) (l >> 8));
		addByte((int) l);
		return this;
	}

	public OutStream addString(String s) {
		checkCapacityPosition(offset + s.length() + 1);
		System.arraycopy(s.getBytes(), 0, payload, offset, s.length());
		offset += s.length();
		addByte(0);
		return this;
	}

	public OutStream addJAGString(String s) {
		addByte(0);
		addString(s);
		return this;
	}

	public OutStream addJAGString2(String s) {
		addString(s);
		addByte(10);
		return this;
	}

	public OutStream initBitAccess() {
		bitPosition = offset * 8;
		return this;
	}

	public OutStream finishBitAccess() {
		offset = (bitPosition + 7) / 8;
		return this;
	}

	public int getBitPosition(int i) {
		return 8 * i - bitPosition;
	}

	public OutStream addBits(int numBits, int value) {
		int bytePos = bitPosition >> 3;
		int bitOffset = 8 - (bitPosition & 7);
		bitPosition += numBits;
		for (; numBits > bitOffset; bitOffset = 8) {
			checkCapacityPosition(bytePos);
			payload[bytePos] &= ~BIT_MASK[bitOffset];
			payload[bytePos++] |= value >> numBits - bitOffset & BIT_MASK[bitOffset];
			numBits -= bitOffset;
		}
		checkCapacityPosition(bytePos);
		if (numBits == bitOffset) {
			payload[bytePos] &= ~BIT_MASK[bitOffset];
			payload[bytePos] |= value & BIT_MASK[bitOffset];
		} else {
			payload[bytePos] &= ~(BIT_MASK[numBits] << bitOffset - numBits);
			payload[bytePos] |= (value & BIT_MASK[numBits]) << bitOffset - numBits;
		}
		return this;
	}

	public void skip(int skip) {
		offset += skip;
	}

	public byte[] getPayload() {
		return payload;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

}
