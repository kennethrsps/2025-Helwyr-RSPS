package com.rs.network.protocol.codec.decode;

import com.rs.network.Session;
import com.rs.stream.InputStream;

public abstract class Decoder {

    public Session session;

    public Decoder(Session session) {
    	this.session = session;
    }

    public abstract void decode(Session session, InputStream stream);

}
