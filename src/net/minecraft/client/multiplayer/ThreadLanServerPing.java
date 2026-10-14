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

   public ThreadLanServerPing(String string, String string2) throws IOException {
      super("LanServerPinger #" + field_148658_a.incrementAndGet());
      this.motd = string;
      this.address = string2;
      this.setDaemon(true);
      this.socket = new DatagramSocket();
   }

   @Override
   public void run() {
      String string = getPingResponse(this.motd, this.address);
      byte[] bytes = string.getBytes();

      while (!this.isInterrupted() && this.isStopping) {
         try {
            InetAddress inetAddress = InetAddress.getByName("224.0.2.60");
            DatagramPacket datagramPacket = new DatagramPacket(bytes, bytes.length, inetAddress, 4445);
            this.socket.send(datagramPacket);
         } catch (IOException ioException) {
            logger.warn("LanServerPinger: " + ioException.getMessage());
            break;
         }

         try {
            sleep(1500L);
         } catch (InterruptedException interruptedException) {
         }
      }
   }

   @Override
   public void interrupt() {
      super.interrupt();
      this.isStopping = false;
   }

   public static String getPingResponse(String string, String string2) {
      return "[MOTD]" + string + "[/MOTD][AD]" + string2 + "[/AD]";
   }

   public static String getMotdFromPingResponse(String string) {
      int i = string.indexOf("[MOTD]");
      if (i < 0) {
         return "missing no";
      }

      int j = string.indexOf("[/MOTD]", i + "[MOTD]".length());
      return j < i ? "missing no" : string.substring(i + "[MOTD]".length(), j);
   }

   public static String getAdFromPingResponse(String string) {
      int i = string.indexOf("[/MOTD]");
      if (i < 0) {
         return null;
      }

      int j = string.indexOf("[/MOTD]", i + "[/MOTD]".length());
      if (j >= 0) {
         return null;
      }

      int k = string.indexOf("[AD]", i + "[/MOTD]".length());
      if (k < 0) {
         return null;
      }

      int l = string.indexOf("[/AD]", k + "[AD]".length());
      return l < k ? null : string.substring(k + "[AD]".length(), l);
   }
}
