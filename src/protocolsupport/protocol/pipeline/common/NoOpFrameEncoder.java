package protocolsupport.protocol.pipeline.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import protocolsupport.protocol.pipeline.IPacketPrepender;

public class NoOpFrameEncoder implements IPacketPrepender {

	@Override
	public void prepend(ChannelHandlerContext ctx, ByteBuf input, ByteBuf output) {
		int readableBytes = input.readableBytes();
		output.ensureWritable(readableBytes);
		output.writeBytes(input, input.readerIndex(), readableBytes);
	}

	@Override
	public ByteBuf allocBuffer(ChannelHandlerContext ctx, ByteBuf in, boolean preferDirect) {
		return ctx.alloc().heapBuffer(in.readableBytes());
	}

}
