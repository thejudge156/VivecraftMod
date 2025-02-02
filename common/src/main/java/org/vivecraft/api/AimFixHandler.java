package org.vivecraft.api;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemPacket;
import net.minecraft.server.RunningOnDifferentThreadException;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.phys.Vec3;

public class AimFixHandler extends ChannelInboundHandlerAdapter
{
    private final Connection netManager;

    public AimFixHandler(Connection netManager)
    {
        this.netManager = netManager;
    }

    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception
    {
        ServerPlayer serverplayer = ((ServerGamePacketListenerImpl)this.netManager.getPacketListener()).player;
        boolean flag = msg instanceof ServerboundUseItemPacket || msg instanceof ServerboundUseItemOnPacket || msg instanceof ServerboundPlayerActionPacket;

        if (!CommonNetworkHelper.isVive(serverplayer) || !flag || serverplayer.getServer() == null) {
            ctx.fireChannelRead(msg);
            return;
        }

        serverplayer.getServer().submit(() ->
        {
            Vec3 position = serverplayer.position();
            Vec3 positionO = new Vec3(serverplayer.xo, serverplayer.yo, serverplayer.zo);
            float xRot = serverplayer.getXRot();
            float yRot = serverplayer.getYRot();
            float yHeadRot = serverplayer.yHeadRot;
            float xRotO = serverplayer.xRotO;
            float yRotO = serverplayer.yRotO;
            float yHeadRotO = serverplayer.yHeadRotO;
            float eyeHeight = serverplayer.getEyeHeight();

            ServerVivePlayer serverviveplayer = CommonNetworkHelper.vivePlayers.get(serverplayer.getGameProfile().getId());

            if (serverviveplayer != null) {
                Vec3 pos = serverviveplayer.getControllerPos(0, serverplayer, true);
                Vec3 dir = serverviveplayer.getControllerDir(0);

                serverplayer.setPosRaw(pos.x, pos.y, pos.z);
                serverplayer.xo = pos.x;
                serverplayer.yo = pos.y;
                serverplayer.zo = pos.z;
                serverplayer.setXRot((float)Math.toDegrees(Math.asin(-dir.y)));
                serverplayer.setYRot((float)Math.toDegrees(Math.atan2(-dir.x, dir.z)));
                serverplayer.xRotO = serverplayer.getXRot();
                serverplayer.yRotO = serverplayer.yHeadRotO = serverplayer.yHeadRot = serverplayer.getYRot();
                serverplayer.eyeHeight = 0;
                serverviveplayer.offset = position.subtract(pos);
                System.out.println("AimFix " + pos.x + " " + pos.y + " " + pos.z + " " + (float)Math.toDegrees(Math.asin(-dir.y)) + " " + (float)Math.toDegrees(Math.atan2(-dir.x, dir.z)));
            }

            try {
                if (this.netManager.isConnected())
                {
                    try
                    {
                        ((Packet)msg).handle(this.netManager.getPacketListener());
                    }
                    catch (RunningOnDifferentThreadException runningondifferentthreadexception)
                    {
                    }
                }
            }
            finally {
                ReferenceCountUtil.release(msg);
            }

            serverplayer.setPosRaw(position.x, position.y, position.z);
            serverplayer.xo = positionO.x;
            serverplayer.yo = positionO.y;
            serverplayer.zo = positionO.z;
            serverplayer.setXRot(xRot);
            serverplayer.setYRot(yRot);
            serverplayer.yHeadRot = yHeadRot;
            serverplayer.xRotO = xRotO;
            serverplayer.yRotO = yRotO;
            serverplayer.yHeadRotO = yHeadRotO;
            serverplayer.eyeHeight = eyeHeight;
            if (serverviveplayer != null)
                serverviveplayer.offset = new Vec3(0.0D, 0.0D, 0.0D);
        });
    }
}
