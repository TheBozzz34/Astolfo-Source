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

   public VehicleMovePacket(Entity entity) {
      this.M = entity.posX;
      this.J = entity.posY;
      this.l = entity.posZ;
      this.a = entity.rotationYaw;
      this.R = entity.rotationPitch;
   }

   public VehicleMovePacket(double d, double d2, double d3, float f, float g) {
      this.M = d;
      this.J = d2;
      this.l = d3;
      this.a = f;
      this.R = g;
   }

   @Override
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.M = buf.readDouble();
      this.J = buf.readDouble();
      this.l = buf.readDouble();
      this.a = buf.readFloat();
      this.R = buf.readFloat();
   }

   @Override
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeDouble(this.M);
      buf.writeDouble(this.J);
      buf.writeDouble(this.l);
      buf.writeFloat(this.a);
      buf.writeFloat(this.R);
   }

   public void processPacket(INetHandlerPlayServer iNetHandlerPlayServer) {
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
