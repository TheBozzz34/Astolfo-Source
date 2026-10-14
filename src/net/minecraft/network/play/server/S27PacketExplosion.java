package net.minecraft.network.play.server;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.List;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;

public class S27PacketExplosion implements Packet<INetHandlerPlayClient> {
   private double posX;
   private double posY;
   private double posZ;
   private float strength;
   private List<BlockPos> affectedBlockPositions;
   public float field_149152_f;
   public float field_149153_g;
   public float field_149159_h;
   private static final long a = 137137977545448L;

   public S27PacketExplosion() {
   }

   public S27PacketExplosion(double d, double y, double z, float strengthIn, List<BlockPos> affectedBlocksIn, Vec3 vec3) {
      this.posX = d;
      this.posY = y;
      this.posZ = z;
      this.strength = strengthIn;
      this.affectedBlockPositions = Lists.newArrayList(affectedBlocksIn);
      if (vec3 != null) {
         this.field_149152_f = (float)vec3.xCoord;
         this.field_149153_g = (float)vec3.yCoord;
         this.field_149159_h = (float)vec3.zCoord;
      }
   }

   @Override
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.posX = buf.readFloat();
      this.posY = buf.readFloat();
      this.posZ = buf.readFloat();
      this.strength = buf.readFloat();
      int i = buf.readInt();
      this.affectedBlockPositions = Lists.newArrayListWithCapacity(i);
      int j = (int)this.posX;
      int k = (int)this.posY;
      int l = (int)this.posZ;

      for (int m = 0; m < i; m++) {
         int n = buf.readByte() + j;
         int i2 = buf.readByte() + k;
         int i3 = buf.readByte() + l;
         this.affectedBlockPositions.add(new BlockPos(n, i2, i3));
      }

      this.field_149152_f = buf.readFloat();
      this.field_149153_g = buf.readFloat();
      this.field_149159_h = buf.readFloat();
   }

   @Override
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeFloat((float)this.posX);
      buf.writeFloat((float)this.posY);
      buf.writeFloat((float)this.posZ);
      buf.writeFloat(this.strength);
      buf.writeInt(this.affectedBlockPositions.size());
      int i = (int)this.posX;
      int j = (int)this.posY;
      int k = (int)this.posZ;

      for (BlockPos blockPos : this.affectedBlockPositions) {
         int l = blockPos.getX() - i;
         int m = blockPos.getY() - j;
         int n = blockPos.getZ() - k;
         buf.writeByte(l);
         buf.writeByte(m);
         buf.writeByte(n);
      }

      buf.writeFloat(this.field_149152_f);
      buf.writeFloat(this.field_149153_g);
      buf.writeFloat(this.field_149159_h);
   }

   public void processPacket(INetHandlerPlayClient handler) {
      handler.handleExplosion(this);
   }

   public float func_149149_c() {
      return this.field_149152_f;
   }

   public float func_149144_d() {
      return this.field_149153_g;
   }

   public float func_149147_e() {
      return this.field_149159_h;
   }

   public double getX() {
      return this.posX;
   }

   public double getY() {
      return this.posY;
   }

   public double getZ() {
      return this.posZ;
   }

   public float getStrength() {
      return this.strength;
   }

   public List<BlockPos> getAffectedBlockPositions() {
      return this.affectedBlockPositions;
   }
}
