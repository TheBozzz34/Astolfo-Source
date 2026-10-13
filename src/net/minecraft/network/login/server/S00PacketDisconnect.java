package net.minecraft.network.login.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.INetHandlerLoginClient;
import net.minecraft.util.IChatComponent;

public class S00PacketDisconnect implements Packet<INetHandlerLoginClient> {
   private IChatComponent reason;

   public S00PacketDisconnect() {
   }

   public S00PacketDisconnect(IChatComponent var1) {
      this.reason = var1;
   }

   @Override
   public void readPacketData(PacketBuffer var1) throws IOException {
      this.reason = var1.readChatComponent();
   }

   @Override
   public void writePacketData(PacketBuffer var1) throws IOException {
      var1.writeChatComponent(this.reason);
   }

   public void processPacket(INetHandlerLoginClient var1) {
      var1.handleDisconnect(this);
   }

   public IChatComponent func_149603_c() {
      return this.reason;
   }
}
