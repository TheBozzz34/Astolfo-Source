package net.minecraft.client.network;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ThreadLanServerPing;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LanServerDetector {
   private static final AtomicInteger field_148551_a = new AtomicInteger(0);
   private static final Logger logger = LogManager.getLogger();

   public static class LanServer {
      private String lanServerMotd;
      private String lanServerIpPort;
      private long timeLastSeen;

      public LanServer(String var1, String var2) {
         this.lanServerMotd = var1;
         this.lanServerIpPort = var2;
         this.timeLastSeen = Minecraft.getSystemTime();
      }

      public String getServerMotd() {
         return this.lanServerMotd;
      }

      public String getServerIpPort() {
         return this.lanServerIpPort;
      }

      public void updateLastSeen() {
         this.timeLastSeen = Minecraft.getSystemTime();
      }
   }

   public static class LanServerList {
      private List<LanServerDetector.LanServer> listOfLanServers = Lists.newArrayList();
      public boolean wasUpdated;

      public synchronized boolean getWasUpdated() {
         return this.wasUpdated;
      }

      public synchronized void setWasNotUpdated() {
         this.wasUpdated = false;
      }

      public synchronized List<LanServerDetector.LanServer> getLanServers() {
         return Collections.unmodifiableList(this.listOfLanServers);
      }

      public synchronized void func_77551_a(String var1, InetAddress var2) {
         String var3 = ThreadLanServerPing.getMotdFromPingResponse(var1);
         String var4 = ThreadLanServerPing.getAdFromPingResponse(var1);
         if (var4 != null) {
            var4 = var2.getHostAddress() + ":" + var4;
            boolean var5 = false;

            for (LanServerDetector.LanServer var7 : this.listOfLanServers) {
               if (var7.getServerIpPort().equals(var4)) {
                  var7.updateLastSeen();
                  var5 = true;
                  break;
               }
            }

            if (!var5) {
               this.listOfLanServers.add(new LanServerDetector.LanServer(var3, var4));
               this.wasUpdated = true;
            }
         }
      }
   }

   public static class ThreadLanServerFind extends Thread {
      private final LanServerDetector.LanServerList localServerList;
      private final InetAddress broadcastAddress;
      private final MulticastSocket socket;
      private static final long b = 42186149686971L;

      public ThreadLanServerFind(LanServerDetector.LanServerList var1) throws IOException {
         super("LanServerDetector #" + LanServerDetector.field_148551_a.incrementAndGet());
         this.localServerList = var1;
         this.setDaemon(true);
         this.socket = new MulticastSocket(4445);
         this.broadcastAddress = InetAddress.getByName("224.0.2.60");
         this.socket.setSoTimeout(5000);
         this.socket.joinGroup(this.broadcastAddress);
      }

      @Override
      public void run() {
         byte[] var3 = new byte[1024];

         while (!this.isInterrupted()) {
            DatagramPacket var4 = new DatagramPacket(var3, var3.length);

            try {
               this.socket.receive(var4);
            } catch (SocketTimeoutException var7) {
               continue;
            } catch (IOException var8) {
               LanServerDetector.logger.error("Couldn't ping server", var8);
               break;
            }

            String var5 = new String(var4.getData(), var4.getOffset(), var4.getLength());
            LanServerDetector.logger.debug(var4.getAddress() + ": " + var5);
            this.localServerList.func_77551_a(var5, var4.getAddress());
         }

         try {
            this.socket.leaveGroup(this.broadcastAddress);
         } catch (IOException var6) {
         }

         this.socket.close();
      }
   }
}
