package net.minecraft.network;

import net.minecraft.util.IThreadListener;

public class PacketThreadUtil {
   private static final long a = 684888048675L;

   public static <T extends INetHandler> void checkThreadAndEnqueue(final Packet<T> packet, final T iNetHandler, IThreadListener iThreadListener) throws ThreadQuickExitException {
      if (!iThreadListener.isCallingFromMinecraftThread()) {
         iThreadListener.addScheduledTask(new Runnable() {
            @Override
            public void run() {
               packet.processPacket(iNetHandler);
            }
         });
         throw ThreadQuickExitException.INSTANCE;
      }
   }
}
