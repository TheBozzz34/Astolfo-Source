package net.minecraft.realms;

import net.minecraft.client.multiplayer.ServerAddress;

public class RealmsServerAddress {
   private final String host;
   private final int port;

   public RealmsServerAddress(String hostIn, int portIn) {
      this.host = hostIn;
      this.port = portIn;
   }

   public String getHost() {
      return this.host;
   }

   public int getPort() {
      return this.port;
   }

   public static RealmsServerAddress parseString(String string) {
      ServerAddress serverAddress = ServerAddress.fromString(string);
      return new RealmsServerAddress(serverAddress.getIP(), serverAddress.getPort());
   }
}
