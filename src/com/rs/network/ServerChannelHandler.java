package com.rs.network;

import java.net.InetSocketAddress;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import com.rs.Protocol;
import com.rs.Settings;
import com.rs.cores.CoresManager;
import com.rs.network.protocol.codec.decode.WorldPacketsDecoder;
import com.rs.stream.InputStream;
import com.rs.utils.Logger;

public final class ServerChannelHandler extends SimpleChannelHandler {

    private static ChannelGroup channels;
    private static ServerBootstrap bootstrap;

    public static int getConnectedChannelsSize() {
    	return channels == null ? 0 : channels.size();
    }

    public static final void init() {
    	new ServerChannelHandler();
    }

    public static final void shutdown() {
    	channels.close().awaitUninterruptibly();
    	bootstrap.releaseExternalResources();
    }

    /*
     * throws exeption so if cant handle channel server closes
     */
    private ServerChannelHandler() {
		channels = new DefaultChannelGroup();
		bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(
			CoresManager.serverBossChannelExecutor,
			CoresManager.serverWorkerChannelExecutor,
			CoresManager.serverWorkersCount));
		bootstrap.getPipeline().addLast("handler", this);
		bootstrap.setOption("reuseAddress", true); // reuses adress for bind
		bootstrap.setOption("child.tcpNoDelay", true);
		bootstrap.setOption("child.TcpAckFrequency", true);
		bootstrap.setOption("child.keepAlive", true);
		// CHEAP HAX MUTLI CROSS PMING LOL REMOVED
		bootstrap.bind(new InetSocketAddress(Settings.SERVER_PORT));
    }

    @Override
    public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) {
    	channels.remove(e.getChannel());
    }

    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) {
    	ctx.setAttachment(new Session(e.getChannel()));
    }

    @Override
    public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) {
		Object sessionObject = ctx.getAttachment();
		if (sessionObject != null && sessionObject instanceof Session) {
		    Session session = (Session) sessionObject;
		    if (session.getDecoder() == null)
		    	return;
		    if (session.getDecoder() instanceof WorldPacketsDecoder)
		    	session.getWorldPackets().getPlayer().finish();
		}
    }

    @Override
    public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e) {
    	channels.add(e.getChannel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent ee) throws Exception {
    	//TODO
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
		if (!(e.getMessage() instanceof ChannelBuffer))
		    return;
		Object sessionObject = ctx.getAttachment();
		if (sessionObject != null && sessionObject instanceof Session) {
		    Session session = (Session) sessionObject;
		    if (session.getDecoder() == null)
		    	return;
		    ChannelBuffer buf = (ChannelBuffer) e.getMessage();
		    buf.markReaderIndex();
		    int avail = buf.readableBytes();
		    if (avail < 1 || avail > Protocol.SV_RECEIVE_DATA_LIMIT)
		    	return;
		    byte[] buffer = new byte[avail];
		    buf.readBytes(buffer);
		    try {
		    	session.getDecoder().decode(session, new InputStream(buffer));
		    } catch (Throwable er) {
		    	Logger.handle(er);
		    }
		}
    }
}