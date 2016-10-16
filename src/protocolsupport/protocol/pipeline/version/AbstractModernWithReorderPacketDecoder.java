package protocolsupport.protocol.pipeline.version;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;
import protocolsupport.api.unsafe.Connection;
import protocolsupport.protocol.legacyremapper.LegacyAnimatePacketReorderer;
import protocolsupport.protocol.packet.middle.ServerBoundMiddlePacket;
import protocolsupport.protocol.serializer.ProtocolSupportPacketDataSerializer;
import protocolsupport.protocol.storage.SharedStorage;
import protocolsupport.utils.netty.ChannelUtils;

public class AbstractModernWithReorderPacketDecoder extends AbstractPacketDecoder  {

	public AbstractModernWithReorderPacketDecoder(Connection connection, SharedStorage sharedstorage) {
		super(connection, sharedstorage);
	}

	private final ProtocolSupportPacketDataSerializer serializer = new ProtocolSupportPacketDataSerializer(null, connection.getVersion());
	private final LegacyAnimatePacketReorderer animateReorderer = new LegacyAnimatePacketReorderer();

	@Override
	public void decode(ChannelHandlerContext ctx, ByteBuf input, List<Object> list) throws Exception {
		if (!input.isReadable()) {
			return;
		}
		serializer.setBuf(input);
		ServerBoundMiddlePacket packetTransformer = registry.getTransformer(ctx.channel().attr(ChannelUtils.CURRENT_PROTOCOL_KEY).get(), serializer.readVarInt());
		packetTransformer.readFromClientData(serializer);
		if (serializer.isReadable()) {
			throw new DecoderException("Did not read all data from packet " + packetTransformer.getClass().getName() + ", bytes left: " + serializer.readableBytes());
		}
		addPackets(animateReorderer.orderPackets(packetTransformer.toNative()), list);
	}

}
