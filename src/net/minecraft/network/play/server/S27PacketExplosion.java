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

   public S27PacketExplosion(double var1, double var3, double var5, float var7, List<BlockPos> var8, Vec3 var9) {
      this.posX = var1;
      this.posY = var3;
      this.posZ = var5;
      this.strength = var7;
      this.affectedBlockPositions = Lists.newArrayList(var8);
      if (var9 != null) {
         this.field_149152_f = (float)var9.xCoord;
         this.field_149153_g = (float)var9.yCoord;
         this.field_149159_h = (float)var9.zCoord;
      }
   }

   @Override
   public void readPacketData(PacketBuffer var1) throws IOException {
      this.posX = var1.readFloat();
      this.posY = var1.readFloat();
      this.posZ = var1.readFloat();
      this.strength = var1.readFloat();
      int var5 = var1.readInt();
      this.affectedBlockPositions = Lists.newArrayListWithCapacity(var5);
      int var6 = (int)this.posX;
      int var7 = (int)this.posY;
      int var8 = (int)this.posZ;

      for (int var9 = 0; var9 < var5; var9++) {
         int var10 = var1.readByte() + var6;
         int var11 = var1.readByte() + var7;
         int var12 = var1.readByte() + var8;
         this.affectedBlockPositions.add(new BlockPos(var10, var11, var12));
      }

      this.field_149152_f = var1.readFloat();
      this.field_149153_g = var1.readFloat();
      this.field_149159_h = var1.readFloat();
   }

   @Override
   public void writePacketData(PacketBuffer var1) throws IOException {
      var1.writeFloat((float)this.posX);
      var1.writeFloat((float)this.posY);
      var1.writeFloat((float)this.posZ);
      var1.writeFloat(this.strength);
      var1.writeInt(this.affectedBlockPositions.size());
      int var5 = (int)this.posX;
      int var6 = (int)this.posY;
      int var7 = (int)this.posZ;

      for (BlockPos var9 : this.affectedBlockPositions) {
         int var10 = var9.getX() - var5;
         int var11 = var9.getY() - var6;
         int var12 = var9.getZ() - var7;
         var1.writeByte(var10);
         var1.writeByte(var11);
         var1.writeByte(var12);
      }

      var1.writeFloat(this.field_149152_f);
      var1.writeFloat(this.field_149153_g);
      var1.writeFloat(this.field_149159_h);
   }

   public void processPacket(INetHandlerPlayClient var1) {
      var1.handleExplosion(this);
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
