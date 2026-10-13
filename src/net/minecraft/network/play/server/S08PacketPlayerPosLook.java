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

   public S08PacketPlayerPosLook(double var1, double var3, double var5, float var7, float var8, Set<S08PacketPlayerPosLook.EnumFlags> var9) {
      this.x = var1;
      this.y = var3;
      this.z = var5;
      this.yaw = var7;
      this.pitch = var8;
      this.field_179835_f = var9;
   }

   @Override
   public void readPacketData(PacketBuffer var1) throws IOException {
      this.x = var1.readDouble();
      this.y = var1.readDouble();
      this.z = var1.readDouble();
      this.yaw = var1.readFloat();
      this.pitch = var1.readFloat();
      this.field_179835_f = S08PacketPlayerPosLook.EnumFlags.func_180053_a(var1.readUnsignedByte());
   }

   @Override
   public void writePacketData(PacketBuffer var1) throws IOException {
      var1.writeDouble(this.x);
      var1.writeDouble(this.y);
      var1.writeDouble(this.z);
      var1.writeFloat(this.yaw);
      var1.writeFloat(this.pitch);
      var1.writeByte(S08PacketPlayerPosLook.EnumFlags.func_180056_a(this.field_179835_f));
   }

   public void processPacket(INetHandlerPlayClient var1) {
      var1.handlePlayerPosLook(this);
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

   public void P(float var1) {
      this.yaw = var1;
   }

   public void B(float var1) {
      this.pitch = var1;
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
      private static final long a = 82444587340078L;

      EnumFlags(int var3) {
         this.field_180058_f = var3;
      }

      private int func_180055_a() {
         return 1 << this.field_180058_f;
      }

      private boolean func_180054_b(int var1) {
         return (var1 & this.func_180055_a()) == this.func_180055_a();
      }

      public static Set<S08PacketPlayerPosLook.EnumFlags> func_180053_a(int var0) {
         EnumSet var4 = EnumSet.noneOf(S08PacketPlayerPosLook.EnumFlags.class);

         for (S08PacketPlayerPosLook.EnumFlags var8 : values()) {
            if (var8.func_180054_b(var0)) {
               var4.add(var8);
            }
         }

         return var4;
      }

      public static int func_180056_a(Set<S08PacketPlayerPosLook.EnumFlags> var0) {
         int var4 = 0;

         for (S08PacketPlayerPosLook.EnumFlags var6 : var0) {
            var4 |= var6.func_180055_a();
         }

         return var4;
      }
   }
}
