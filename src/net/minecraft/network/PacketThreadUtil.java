package net.minecraft.network;

import net.minecraft.util.IThreadListener;

public class PacketThreadUtil {
   private static final long a = 684888048675L;

   public static <T extends INetHandler> void checkThreadAndEnqueue(final Packet<T> var0, final T var1, IThreadListener var2) throws ThreadQuickExitException {
      if (!var2.isCallingFromMinecraftThread()) {
         var2.addScheduledTask(new Runnable() {
            @Override
            public void run() {
               var0.processPacket(var1);
            }
         });
         throw ThreadQuickExitException.INSTANCE;
      }
   }
}
