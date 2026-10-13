package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.util.BlockPos;
import org.apache.commons.lang3.StringUtils;

public class C14PacketTabComplete implements Packet<INetHandlerPlayServer> {
   private String message;
   private BlockPos targetBlock;
   private static final long a = 135972400040345L;

   public C14PacketTabComplete() {
   }

   public C14PacketTabComplete(String var1) {
      this(var1, (BlockPos)null);
   }

   public C14PacketTabComplete(String var1, BlockPos var2) {
      this.message = var1;
      this.targetBlock = var2;
   }

   @Override
   public void readPacketData(PacketBuffer var1) throws IOException {
      this.message = var1.readStringFromBuffer(32767);
      boolean var4 = var1.readBoolean();
      if (var4) {
         this.targetBlock = var1.readBlockPos();
      }
   }

   @Override
   public void writePacketData(PacketBuffer var1) throws IOException {
      var1.writeString(StringUtils.substring(this.message, 0, 32767));
      boolean var5 = this.targetBlock != null;
      var1.writeBoolean(var5);
      if (var5) {
         var1.writeBlockPos(this.targetBlock);
      }
   }

   public void processPacket(INetHandlerPlayServer var1) {
      var1.processTabComplete(this);
   }

   public String getMessage() {
      return this.message;
   }

   public void x(String var1) {
      this.message = var1;
   }

   public BlockPos getTargetBlock() {
      return this.targetBlock;
   }
}
