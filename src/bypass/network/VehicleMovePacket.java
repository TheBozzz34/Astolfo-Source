package bypass.network;

import java.io.IOException;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class VehicleMovePacket implements Packet<INetHandlerPlayServer> {
   private double M;
   private double J;
   private double l;
   private float a;
   private float R;

   public VehicleMovePacket() {
   }

   public VehicleMovePacket(Entity var1) {
      this.M = var1.posX;
      this.J = var1.posY;
      this.l = var1.posZ;
      this.a = var1.rotationYaw;
      this.R = var1.rotationPitch;
   }

   public VehicleMovePacket(double var1, double var3, double var5, float var7, float var8) {
      this.M = var1;
      this.J = var3;
      this.l = var5;
      this.a = var7;
      this.R = var8;
   }

   @Override
   public void readPacketData(PacketBuffer var1) throws IOException {
      this.M = var1.readDouble();
      this.J = var1.readDouble();
      this.l = var1.readDouble();
      this.a = var1.readFloat();
      this.R = var1.readFloat();
   }

   @Override
   public void writePacketData(PacketBuffer var1) throws IOException {
      var1.writeDouble(this.M);
      var1.writeDouble(this.J);
      var1.writeDouble(this.l);
      var1.writeFloat(this.a);
      var1.writeFloat(this.R);
   }

   public void processPacket(INetHandlerPlayServer var1) {
   }

   public double l() {
      return this.M;
   }

   public double k() {
      return this.J;
   }

   public double z() {
      return this.l;
   }

   public float K() {
      return this.a;
   }

   public float J() {
      return this.R;
   }
}
