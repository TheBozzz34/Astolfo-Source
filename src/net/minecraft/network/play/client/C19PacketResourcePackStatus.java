package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C19PacketResourcePackStatus implements Packet<INetHandlerPlayServer> {
   private String hash;
   private C19PacketResourcePackStatus.Action status;
   private static final long a = 62083493440996L;

   public C19PacketResourcePackStatus() {
   }

   public C19PacketResourcePackStatus(String var1, C19PacketResourcePackStatus.Action var2) {
      if (var1.length() > 40) {
         var1 = var1.substring(0, 40);
      }

      this.hash = var1;
      this.status = var2;
   }

   @Override
   public void readPacketData(PacketBuffer var1) throws IOException {
      this.hash = var1.readStringFromBuffer(40);
      this.status = var1.readEnumValue(C19PacketResourcePackStatus.Action.class);
   }

   @Override
   public void writePacketData(PacketBuffer var1) throws IOException {
      var1.writeString(this.hash);
      var1.writeEnumValue(this.status);
   }

   public void processPacket(INetHandlerPlayServer var1) {
      var1.handleResourcePackStatus(this);
   }

   public enum Action {
      SUCCESSFULLY_LOADED,
      DECLINED,
      FAILED_DOWNLOAD,
      ACCEPTED;
   }
}
