package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.MathHelper;
import org.apache.commons.lang3.Validate;

public class S29PacketSoundEffect implements Packet<INetHandlerPlayClient> {
   private String soundName;
   private int posX;
   private int posY = Integer.MAX_VALUE;
   private int posZ;
   private float soundVolume;
   private int soundPitch;
   private static final long a = 82740009192734L;

   public S29PacketSoundEffect() {
   }

   public S29PacketSoundEffect(String var1, double var2, double var4, double var6, float var8, float var9) {
      Validate.notNull(var1, "name");
      this.soundName = var1;
      this.posX = (int)(var2 * 8.0);
      this.posY = (int)(var4 * 8.0);
      this.posZ = (int)(var6 * 8.0);
      this.soundVolume = var8;
      this.soundPitch = (int)(var9 * 63.0F);
      var9 = MathHelper.clamp_float(var9, 0.0F, 255.0F);
   }

   @Override
   public void readPacketData(PacketBuffer var1) throws IOException {
      this.soundName = var1.readStringFromBuffer(256);
      this.posX = var1.readInt();
      this.posY = var1.readInt();
      this.posZ = var1.readInt();
      this.soundVolume = var1.readFloat();
      this.soundPitch = var1.readUnsignedByte();
   }

   @Override
   public void writePacketData(PacketBuffer var1) throws IOException {
      var1.writeString(this.soundName);
      var1.writeInt(this.posX);
      var1.writeInt(this.posY);
      var1.writeInt(this.posZ);
      var1.writeFloat(this.soundVolume);
      var1.writeByte(this.soundPitch);
   }

   public String getSoundName() {
      return this.soundName;
   }

   public double getX() {
      return this.posX / 8.0F;
   }

   public double getY() {
      return this.posY / 8.0F;
   }

   public double getZ() {
      return this.posZ / 8.0F;
   }

   public float getVolume() {
      return this.soundVolume;
   }

   public float getPitch() {
      return this.soundPitch / 63.0F;
   }

   public void processPacket(INetHandlerPlayClient var1) {
      var1.handleSoundEffect(this);
   }
}
