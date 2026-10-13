package net.minecraft.client.multiplayer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ThreadLanServerPing extends Thread {
   private static final AtomicInteger field_148658_a = new AtomicInteger(0);
   private static final Logger logger = LogManager.getLogger();
   private final String motd;
   private final DatagramSocket socket;
   private boolean isStopping = true;
   private final String address;
   private static final long a = 93191026823412L;
   private static final long e = -7047546114416832163L;

   public ThreadLanServerPing(String var1, String var2) throws IOException {
      super("LanServerPinger #" + field_148658_a.incrementAndGet());
      this.motd = var1;
      this.address = var2;
      this.setDaemon(true);
      this.socket = new DatagramSocket();
   }

   @Override
   public void run() {
      String var4 = getPingResponse(this.motd, this.address);
      byte[] var5 = var4.getBytes();

      while (!this.isInterrupted() && this.isStopping) {
         try {
            InetAddress var6 = InetAddress.getByName("224.0.2.60");
            DatagramPacket var7 = new DatagramPacket(var5, var5.length, var6, (int)e);
            this.socket.send(var7);
         } catch (IOException var9) {
            logger.warn("LanServerPinger: " + var9.getMessage());
            break;
         }

         try {
            sleep(1500L);
         } catch (InterruptedException var8) {
         }
      }
   }

   @Override
   public void interrupt() {
      super.interrupt();
      this.isStopping = false;
   }

   public static String getPingResponse(String var0, String var1) {
      return "[MOTD]" + var0 + "[/MOTD][AD]" + var1 + "[/AD]";
   }

   public static String getMotdFromPingResponse(String var0) {
      int var4 = var0.indexOf("[MOTD]");
      if (var4 < 0) {
         return "missing no";
      }

      int var5 = var0.indexOf("[/MOTD]", var4 + "[MOTD]".length());
      return var5 < var4 ? "missing no" : var0.substring(var4 + "[MOTD]".length(), var5);
   }

   public static String getAdFromPingResponse(String var0) {
      int var4 = var0.indexOf("[/MOTD]");
      if (var4 < 0) {
         return null;
      }

      int var5 = var0.indexOf("[/MOTD]", var4 + "[/MOTD]".length());
      if (var5 >= 0) {
         return null;
      }

      int var6 = var0.indexOf("[AD]", var4 + "[/MOTD]".length());
      if (var6 < 0) {
         return null;
      }

      int var7 = var0.indexOf("[/AD]", var6 + "[AD]".length());
      return var7 < var6 ? null : var0.substring(var6 + "[AD]".length(), var7);
   }
}
