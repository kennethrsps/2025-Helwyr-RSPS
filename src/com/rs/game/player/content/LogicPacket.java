package com.rs.game.player.content;

import com.rs.stream.InputStream;

public class LogicPacket {

    private int id;
    byte[] data;

    public LogicPacket(int id, int size, InputStream stream) {
	this.id = id;
	data = new byte[size];
	stream.getBytes(data, 0, size);
    }

    public byte[] getData() {
	return data;
    }

    public int getId() {
	return id;
    }

}
