package bypass.network;

import java.io.IOException;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class VehicleMovePacket implements Packet<INetHandlerPlayServer> {
   private double x;
   private double y;
   private double z;
   private float yaw;
   private float pitch;

   public VehicleMovePacket() {
   }

   public VehicleMovePacket(Entity entity) {
      this.x = entity.posX;
      this.y = entity.posY;
      this.z = entity.posZ;
      this.yaw = entity.rotationYaw;
      this.pitch = entity.rotationPitch;
   }

   public VehicleMovePacket(double d, double d2, double d3, float f, float g) {
      this.x = d;
      this.y = d2;
      this.z = d3;
      this.yaw = f;
      this.pitch = g;
   }

   @Override
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.x = buf.readDouble();
      this.y = buf.readDouble();
      this.z = buf.readDouble();
      this.yaw = buf.readFloat();
      this.pitch = buf.readFloat();
   }

   @Override
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeDouble(this.x);
      buf.writeDouble(this.y);
      buf.writeDouble(this.z);
      buf.writeFloat(this.yaw);
      buf.writeFloat(this.pitch);
   }

   public void processPacket(INetHandlerPlayServer iNetHandlerPlayServer) {
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
}
