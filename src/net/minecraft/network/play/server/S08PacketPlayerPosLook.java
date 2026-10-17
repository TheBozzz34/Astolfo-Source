package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.EnumSet;
import java.util.Set;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S08PacketPlayerPosLook implements Packet<INetHandlerPlayClient> {
   private double x;
   private double y;
   private double z;
   private float yaw;
   private float pitch;
   private Set<S08PacketPlayerPosLook.EnumFlags> field_179835_f;

   public S08PacketPlayerPosLook() {
   }

   public S08PacketPlayerPosLook(double xIn, double yIn, double zIn, float yawIn, float pitchIn, Set<S08PacketPlayerPosLook.EnumFlags> set) {
      this.x = xIn;
      this.y = yIn;
      this.z = zIn;
      this.yaw = yawIn;
      this.pitch = pitchIn;
      this.field_179835_f = set;
   }

   @Override
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.x = buf.readDouble();
      this.y = buf.readDouble();
      this.z = buf.readDouble();
      this.yaw = buf.readFloat();
      this.pitch = buf.readFloat();
      this.field_179835_f = S08PacketPlayerPosLook.EnumFlags.func_180053_a(buf.readUnsignedByte());
   }

   @Override
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeDouble(this.x);
      buf.writeDouble(this.y);
      buf.writeDouble(this.z);
      buf.writeFloat(this.yaw);
      buf.writeFloat(this.pitch);
      buf.writeByte(S08PacketPlayerPosLook.EnumFlags.func_180056_a(this.field_179835_f));
   }

   public void processPacket(INetHandlerPlayClient handler) {
      handler.handlePlayerPosLook(this);
   }

   public double getX() {
      return this.x;
   }

   public double getY() {
      return this.y;
   }

   public double getZ() {
      return this.z;
   }

   public float getYaw() {
      return this.yaw;
   }

   public float getPitch() {
      return this.pitch;
   }

   public void setYaw(float f) {
      this.yaw = f;
   }

   public void setPitch(float f) {
      this.pitch = f;
   }

   public Set<S08PacketPlayerPosLook.EnumFlags> func_179834_f() {
      return this.field_179835_f;
   }

   public enum EnumFlags {
      X(0),
      Y(1),
      Z(2),
      Y_ROT(3),
      X_ROT(4);

      private int field_180058_f;

      EnumFlags(int j) {
         this.field_180058_f = j;
      }

      private int func_180055_a() {
         return 1 << this.field_180058_f;
      }

      private boolean func_180054_b(int i) {
         return (i & this.func_180055_a()) == this.func_180055_a();
      }

      public static Set<S08PacketPlayerPosLook.EnumFlags> func_180053_a(int i) {
         EnumSet enumSet = EnumSet.noneOf(S08PacketPlayerPosLook.EnumFlags.class);

         for (S08PacketPlayerPosLook.EnumFlags enumFlags : values()) {
            if (enumFlags.func_180054_b(i)) {
               enumSet.add(enumFlags);
            }
         }

         return enumSet;
      }

      public static int func_180056_a(Set<S08PacketPlayerPosLook.EnumFlags> set) {
         int i = 0;

         for (S08PacketPlayerPosLook.EnumFlags enumFlags : set) {
            i |= enumFlags.func_180055_a();
         }

         return i;
      }
   }
}
