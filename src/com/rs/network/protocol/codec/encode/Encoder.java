package com.rs.network.protocol.codec.encode;

import com.rs.network.Session;

public abstract class Encoder {

    protected Session session;

    public Encoder(Session session) {
    	this.session = session;
    }
}