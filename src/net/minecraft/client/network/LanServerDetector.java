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

      public LanServer(String motd, String address) {
         this.lanServerMotd = motd;
         this.lanServerIpPort = address;
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

      public synchronized void func_77551_a(String string, InetAddress inetAddress) {
         String string2 = ThreadLanServerPing.getMotdFromPingResponse(string);
         String string3 = ThreadLanServerPing.getAdFromPingResponse(string);
         if (string3 != null) {
            string3 = inetAddress.getHostAddress() + ":" + string3;
            boolean bl = false;

            for (LanServerDetector.LanServer lanServer : this.listOfLanServers) {
               if (lanServer.getServerIpPort().equals(string3)) {
                  lanServer.updateLastSeen();
                  bl = true;
                  break;
               }
            }

            if (!bl) {
               this.listOfLanServers.add(new LanServerDetector.LanServer(string2, string3));
               this.wasUpdated = true;
            }
         }
      }
   }

   public static class ThreadLanServerFind extends Thread {
      private final LanServerDetector.LanServerList localServerList;
      private final InetAddress broadcastAddress;
      private final MulticastSocket socket;

      public ThreadLanServerFind(LanServerDetector.LanServerList lanServerList) throws IOException {
         super("LanServerDetector #" + LanServerDetector.field_148551_a.incrementAndGet());
         this.localServerList = lanServerList;
         this.setDaemon(true);
         this.socket = new MulticastSocket(4445);
         this.broadcastAddress = InetAddress.getByName("224.0.2.60");
         this.socket.setSoTimeout(5000);
         this.socket.joinGroup(this.broadcastAddress);
      }

      @Override
      public void run() {
         byte[] bytes = new byte[1024];

         while (!this.isInterrupted()) {
            DatagramPacket datagramPacket = new DatagramPacket(bytes, bytes.length);

            try {
               this.socket.receive(datagramPacket);
            } catch (SocketTimeoutException socketTimeoutException) {
               continue;
            } catch (IOException ioException) {
               LanServerDetector.logger.error("Couldn't ping server", ioException);
               break;
            }

            String string = new String(datagramPacket.getData(), datagramPacket.getOffset(), datagramPacket.getLength());
            LanServerDetector.logger.debug(datagramPacket.getAddress() + ": " + string);
            this.localServerList.func_77551_a(string, datagramPacket.getAddress());
         }

         try {
            this.socket.leaveGroup(this.broadcastAddress);
         } catch (IOException ioException2) {
         }

         this.socket.close();
      }
   }
}
