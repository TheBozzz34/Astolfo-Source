package net.minecraft.network.login.server;

import com.mojang.authlib.GameProfile;
import java.io.IOException;
import java.util.UUID;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.INetHandlerLoginClient;

public class S02PacketLoginSuccess implements Packet<INetHandlerLoginClient> {
   private GameProfile profile;
   private static final long a = 56560286477498L;

   public S02PacketLoginSuccess() {
   }

   public S02PacketLoginSuccess(GameProfile var1) {
      this.profile = var1;
   }

   @Override
   public void readPacketData(PacketBuffer var1) throws IOException {
      String var6 = var1.readStringFromBuffer(36);
      String var7 = var1.readStringFromBuffer(16);
      UUID var8 = UUID.fromString(var6);
      this.profile = new GameProfile(var8, var7);
   }

   @Override
   public void writePacketData(PacketBuffer var1) throws IOException {
      UUID var5 = this.profile.getId();
      var1.writeString(var5 == null ? "" : var5.toString());
      var1.writeString(this.profile.getName());
   }

   public void processPacket(INetHandlerLoginClient var1) {
      var1.handleLoginSuccess(this);
   }

   public GameProfile getProfile() {
      return this.profile;
   }
}
