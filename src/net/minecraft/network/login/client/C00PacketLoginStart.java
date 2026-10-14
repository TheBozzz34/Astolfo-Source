package net.minecraft.network.login.client;

import com.mojang.authlib.GameProfile;
import java.io.IOException;
import java.util.UUID;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.INetHandlerLoginServer;

public class C00PacketLoginStart implements Packet<INetHandlerLoginServer> {
   private GameProfile profile;
   private static final long a = 88961751033287L;
   private static final long b = -707486971710144496L;

   public C00PacketLoginStart() {
   }

   public C00PacketLoginStart(GameProfile profileIn) {
      this.profile = profileIn;
   }

   @Override
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.profile = new GameProfile((UUID)null, buf.readStringFromBuffer((int)b));
   }

   @Override
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeString(this.profile.getName());
   }

   public void processPacket(INetHandlerLoginServer handler) {
      handler.processLoginStart(this);
   }

   public GameProfile getProfile() {
      return this.profile;
   }
}
