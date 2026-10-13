package net.minecraft.server.network;

import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.handshake.INetHandlerHandshakeServer;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.server.S00PacketDisconnect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

public class NetHandlerHandshakeTCP implements INetHandlerHandshakeServer {
   private final MinecraftServer server;
   private final NetworkManager networkManager;
   private static final long a = 135785117131426L;

   public NetHandlerHandshakeTCP(MinecraftServer var1, NetworkManager var2) {
      this.server = var1;
      this.networkManager = var2;
   }

   @Override
   public void processHandshake(C00Handshake var1) {
      switch (var1.getRequestedState()) {
         case LOGIN:
            this.networkManager.setConnectionState(EnumConnectionState.LOGIN);
            if (var1.getProtocolVersion() > 47) {
               ChatComponentText var5 = new ChatComponentText("Outdated server! I'm still on 1.8.8");
               this.networkManager.sendPacket(new S00PacketDisconnect(var5));
               this.networkManager.closeChannel(var5);
            } else if (var1.getProtocolVersion() < 47) {
               ChatComponentText var6 = new ChatComponentText("Outdated client! Please use 1.8.8");
               this.networkManager.sendPacket(new S00PacketDisconnect(var6));
               this.networkManager.closeChannel(var6);
            } else {
               this.networkManager.setNetHandler(new NetHandlerLoginServer(this.server, this.networkManager));
            }
            break;
         case STATUS:
            this.networkManager.setConnectionState(EnumConnectionState.STATUS);
            this.networkManager.setNetHandler(new NetHandlerStatusServer(this.server, this.networkManager));
            break;
         default:
            throw new UnsupportedOperationException("Invalid intention " + var1.getRequestedState());
      }
   }

   @Override
   public void onDisconnect(IChatComponent var1) {
   }
}
