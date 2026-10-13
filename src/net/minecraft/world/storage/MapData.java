package net.minecraft.world.storage;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S34PacketMaps;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec4b;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;

public class MapData extends WorldSavedData {
   public int xCenter;
   public int zCenter;
   public byte dimension;
   public byte scale;
   public byte[] colors = new byte[16384];
   public List<MapData.MapInfo> playersArrayList = Lists.newArrayList();
   private Map<EntityPlayer, MapData.MapInfo> playersHashMap = Maps.newHashMap();
   public Map<String, Vec4b> mapDecorations = Maps.newLinkedHashMap();
   private static final long a = 89955657118791L;

   public MapData(String var1) {
      super(var1);
   }

   public void calculateMapCenter(double var1, double var3, int var5) {
      int var8 = 128 * (1 << var5);
      int var9 = MathHelper.floor_double((var1 + 64.0) / var8);
      int var10 = MathHelper.floor_double((var3 + 64.0) / var8);
      this.xCenter = var9 * var8 + var8 / 2 - 64;
      this.zCenter = var10 * var8 + var8 / 2 - 64;
   }

   @Override
   public void readFromNBT(NBTTagCompound var1) {
      this.dimension = var1.getByte("dimension");
      this.xCenter = var1.getInteger("xCenter");
      this.zCenter = var1.getInteger("zCenter");
      this.scale = var1.getByte("scale");
      this.scale = (byte)MathHelper.clamp_int(this.scale, 0, 4);
      short var5 = var1.getShort("width");
      short var6 = var1.getShort("height");
      if (var5 == 128 && var6 == 128) {
         this.colors = var1.getByteArray("colors");
      } else {
         byte[] var7 = var1.getByteArray("colors");
         this.colors = new byte[16384];
         int var8 = (128 - var5) / 2;
         int var9 = (128 - var6) / 2;

         for (int var10 = 0; var10 < var6; var10++) {
            int var11 = var10 + var9;
            if (var11 >= 0 || var11 < 128) {
               for (int var12 = 0; var12 < var5; var12++) {
                  int var13 = var12 + var8;
                  if (var13 >= 0 || var13 < 128) {
                     this.colors[var13 + var11 * 128] = var7[var12 + var10 * var5];
                  }
               }
            }
         }
      }
   }

   @Override
   public void writeToNBT(NBTTagCompound var1) {
      var1.setByte("dimension", this.dimension);
      var1.setInteger("xCenter", this.xCenter);
      var1.setInteger("zCenter", this.zCenter);
      var1.setByte("scale", this.scale);
      var1.setShort("width", (short)128);
      var1.setShort("height", (short)128);
      var1.setByteArray("colors", this.colors);
   }

   public void updateVisiblePlayers(EntityPlayer var1, ItemStack var2) {
      if (!this.playersHashMap.containsKey(var1)) {
         MapData.MapInfo var6 = new MapData.MapInfo(var1);
         this.playersHashMap.put(var1, var6);
         this.playersArrayList.add(var6);
      }

      if (!var1.inventory.hasItemStack(var2)) {
         this.mapDecorations.remove(var1.getName());
      }

      for (int var9 = 0; var9 < this.playersArrayList.size(); var9++) {
         MapData.MapInfo var7 = this.playersArrayList.get(var9);
         if (!var7.entityplayerObj.isDead && (var7.entityplayerObj.inventory.hasItemStack(var2) || var2.isOnItemFrame())) {
            if (!var2.isOnItemFrame() && var7.entityplayerObj.dimension == this.dimension) {
               this.updateDecorations(
                  0,
                  var7.entityplayerObj.worldObj,
                  var7.entityplayerObj.getName(),
                  var7.entityplayerObj.posX,
                  var7.entityplayerObj.posZ,
                  var7.entityplayerObj.rotationYaw
               );
            }
         } else {
            this.playersHashMap.remove(var7.entityplayerObj);
            this.playersArrayList.remove(var7);
         }
      }

      if (var2.isOnItemFrame()) {
         EntityItemFrame var10 = var2.getItemFrame();
         BlockPos var12 = var10.getHangingPosition();
         this.updateDecorations(1, var1.worldObj, "frame-" + var10.getEntityId(), var12.getX(), var12.getZ(), var10.facingDirection.getHorizontalIndex() * 90);
      }

      if (var2.hasTagCompound() && var2.getTagCompound().hasKey("Decorations", 9)) {
         NBTTagList var11 = var2.getTagCompound().getTagList("Decorations", 10);

         for (int var13 = 0; var13 < var11.tagCount(); var13++) {
            NBTTagCompound var8 = var11.getCompoundTagAt(var13);
            if (!this.mapDecorations.containsKey(var8.getString("id"))) {
               this.updateDecorations(
                  var8.getByte("type"), var1.worldObj, var8.getString("id"), var8.getDouble("x"), var8.getDouble("z"), var8.getDouble("rot")
               );
            }
         }
      }
   }

   private void updateDecorations(int var1, World var2, String var3, double var4, double var6, double var8) {
      int var13 = 1 << this.scale;
      float var14 = (float)(var4 - this.xCenter) / var13;
      float var15 = (float)(var6 - this.zCenter) / var13;
      byte var16 = (byte)(var14 * 2.0F + 0.5);
      byte var17 = (byte)(var15 * 2.0F + 0.5);
      byte var18 = 63;
      byte var19;
      if (var14 >= -var18 && var15 >= -var18 && var14 <= var18 && var15 <= var18) {
         var8 += var8 < 0.0 ? -8.0 : 8.0;
         var19 = (byte)(var8 * 16.0 / 360.0);
         if (this.dimension < 0) {
            int var20 = (int)(var2.getWorldInfo().getWorldTime() / 10L);
            var19 = (byte)(var20 * var20 * 34187121 + var20 * 121 >> 15 & 15);
         }
      } else {
         if (Math.abs(var14) >= 320.0F || Math.abs(var15) >= 320.0F) {
            this.mapDecorations.remove(var3);
            return;
         }

         var1 = 6;
         var19 = 0;
         if (var14 <= -var18) {
            var16 = (byte)(var18 * 2 + 2.5);
         }

         if (var15 <= -var18) {
            var17 = (byte)(var18 * 2 + 2.5);
         }

         if (var14 >= var18) {
            var16 = (byte)(var18 * 2 + 1);
         }

         if (var15 >= var18) {
            var17 = (byte)(var18 * 2 + 1);
         }
      }

      this.mapDecorations.put(var3, new Vec4b((byte)var1, var16, var17, var19));
   }

   public Packet getMapPacket(ItemStack var1, World var2, EntityPlayer var3) {
      MapData.MapInfo var7 = this.playersHashMap.get(var3);
      return var7 == null ? null : var7.getPacket(var1);
   }

   public void updateMapData(int var1, int var2) {
      super.markDirty();

      for (MapData.MapInfo var7 : this.playersArrayList) {
         var7.update(var1, var2);
      }
   }

   public MapData.MapInfo getMapInfo(EntityPlayer var1) {
      MapData.MapInfo var5 = this.playersHashMap.get(var1);
      if (var5 == null) {
         var5 = new MapData.MapInfo(var1);
         this.playersHashMap.put(var1, var5);
         this.playersArrayList.add(var5);
      }

      return var5;
   }

   public class MapInfo {
      public final EntityPlayer entityplayerObj;
      private boolean field_176105_d = true;
      private int minX = 0;
      private int minY = 0;
      private int maxX = 127;
      private int maxY = 127;
      private int field_176109_i;
      public int field_82569_d;
      private static final long a = 102825963181528L;

      public MapInfo(EntityPlayer var2) {
         this.entityplayerObj = var2;
      }

      public Packet getPacket(ItemStack var1) {
         if (this.field_176105_d) {
            this.field_176105_d = false;
            return new S34PacketMaps(
               var1.getMetadata(),
               MapData.this.scale,
               MapData.this.mapDecorations.values(),
               MapData.this.colors,
               this.minX,
               this.minY,
               this.maxX + 1 - this.minX,
               this.maxY + 1 - this.minY
            );
         } else {
            return this.field_176109_i++ % 5 == 0
               ? new S34PacketMaps(var1.getMetadata(), MapData.this.scale, MapData.this.mapDecorations.values(), MapData.this.colors, 0, 0, 0, 0)
               : null;
         }
      }

      public void update(int var1, int var2) {
         if (this.field_176105_d) {
            this.minX = Math.min(this.minX, var1);
            this.minY = Math.min(this.minY, var2);
            this.maxX = Math.max(this.maxX, var1);
            this.maxY = Math.max(this.maxY, var2);
         } else {
            this.field_176105_d = true;
            this.minX = var1;
            this.minY = var2;
            this.maxX = var1;
            this.maxY = var2;
         }
      }
   }
}
