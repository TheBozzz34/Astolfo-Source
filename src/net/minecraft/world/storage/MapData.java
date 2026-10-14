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

   public MapData(String mapname) {
      super(mapname);
   }

   public void calculateMapCenter(double x, double z, int mapScale) {
      int i = 128 * (1 << mapScale);
      int j = MathHelper.floor_double((x + 64.0) / i);
      int k = MathHelper.floor_double((z + 64.0) / i);
      this.xCenter = j * i + i / 2 - 64;
      this.zCenter = k * i + i / 2 - 64;
   }

   @Override
   public void readFromNBT(NBTTagCompound nbt) {
      this.dimension = nbt.getByte("dimension");
      this.xCenter = nbt.getInteger("xCenter");
      this.zCenter = nbt.getInteger("zCenter");
      this.scale = nbt.getByte("scale");
      this.scale = (byte)MathHelper.clamp_int(this.scale, 0, 4);
      short s = nbt.getShort("width");
      short s2 = nbt.getShort("height");
      if (s == 128 && s2 == 128) {
         this.colors = nbt.getByteArray("colors");
      } else {
         byte[] bytes = nbt.getByteArray("colors");
         this.colors = new byte[16384];
         int i = (128 - s) / 2;
         int j = (128 - s2) / 2;

         for (int k = 0; k < s2; k++) {
            int l = k + j;
            if (l >= 0 || l < 128) {
               for (int m = 0; m < s; m++) {
                  int n = m + i;
                  if (n >= 0 || n < 128) {
                     this.colors[n + l * 128] = bytes[m + k * s];
                  }
               }
            }
         }
      }
   }

   @Override
   public void writeToNBT(NBTTagCompound nbt) {
      nbt.setByte("dimension", this.dimension);
      nbt.setInteger("xCenter", this.xCenter);
      nbt.setInteger("zCenter", this.zCenter);
      nbt.setByte("scale", this.scale);
      nbt.setShort("width", (short)128);
      nbt.setShort("height", (short)128);
      nbt.setByteArray("colors", this.colors);
   }

   public void updateVisiblePlayers(EntityPlayer player, ItemStack mapStack) {
      if (!this.playersHashMap.containsKey(player)) {
         MapData.MapInfo mapInfo = new MapData.MapInfo(player);
         this.playersHashMap.put(player, mapInfo);
         this.playersArrayList.add(mapInfo);
      }

      if (!player.inventory.hasItemStack(mapStack)) {
         this.mapDecorations.remove(player.getName());
      }

      for (int i = 0; i < this.playersArrayList.size(); i++) {
         MapData.MapInfo mapInfo2 = this.playersArrayList.get(i);
         if (!mapInfo2.entityplayerObj.isDead && (mapInfo2.entityplayerObj.inventory.hasItemStack(mapStack) || mapStack.isOnItemFrame())) {
            if (!mapStack.isOnItemFrame() && mapInfo2.entityplayerObj.dimension == this.dimension) {
               this.updateDecorations(
                  0,
                  mapInfo2.entityplayerObj.worldObj,
                  mapInfo2.entityplayerObj.getName(),
                  mapInfo2.entityplayerObj.posX,
                  mapInfo2.entityplayerObj.posZ,
                  mapInfo2.entityplayerObj.rotationYaw
               );
            }
         } else {
            this.playersHashMap.remove(mapInfo2.entityplayerObj);
            this.playersArrayList.remove(mapInfo2);
         }
      }

      if (mapStack.isOnItemFrame()) {
         EntityItemFrame entityItemFrame = mapStack.getItemFrame();
         BlockPos blockPos = entityItemFrame.getHangingPosition();
         this.updateDecorations(1, player.worldObj, "frame-" + entityItemFrame.getEntityId(), blockPos.getX(), blockPos.getZ(), entityItemFrame.facingDirection.getHorizontalIndex() * 90);
      }

      if (mapStack.hasTagCompound() && mapStack.getTagCompound().hasKey("Decorations", 9)) {
         NBTTagList nbtTagList = mapStack.getTagCompound().getTagList("Decorations", 10);

         for (int j = 0; j < nbtTagList.tagCount(); j++) {
            NBTTagCompound nbtTagCompound = nbtTagList.getCompoundTagAt(j);
            if (!this.mapDecorations.containsKey(nbtTagCompound.getString("id"))) {
               this.updateDecorations(
                  nbtTagCompound.getByte("type"), player.worldObj, nbtTagCompound.getString("id"), nbtTagCompound.getDouble("x"), nbtTagCompound.getDouble("z"), nbtTagCompound.getDouble("rot")
               );
            }
         }
      }
   }

   private void updateDecorations(int typeIn, World worldIn, String entityIdentifier, double worldX, double worldZ, double rotation) {
      int i = 1 << this.scale;
      float f = (float)(worldX - this.xCenter) / i;
      float g = (float)(worldZ - this.zCenter) / i;
      byte b = (byte)(f * 2.0F + 0.5);
      byte b2 = (byte)(g * 2.0F + 0.5);
      byte b3 = 63;
      byte b4;
      if (f >= -b3 && g >= -b3 && f <= b3 && g <= b3) {
         rotation += rotation < 0.0 ? -8.0 : 8.0;
         b4 = (byte)(rotation * 16.0 / 360.0);
         if (this.dimension < 0) {
            int j = (int)(worldIn.getWorldInfo().getWorldTime() / 10L);
            b4 = (byte)(j * j * 34187121 + j * 121 >> 15 & 15);
         }
      } else {
         if (Math.abs(f) >= 320.0F || Math.abs(g) >= 320.0F) {
            this.mapDecorations.remove(entityIdentifier);
            return;
         }

         typeIn = 6;
         b4 = 0;
         if (f <= -b3) {
            b = (byte)(b3 * 2 + 2.5);
         }

         if (g <= -b3) {
            b2 = (byte)(b3 * 2 + 2.5);
         }

         if (f >= b3) {
            b = (byte)(b3 * 2 + 1);
         }

         if (g >= b3) {
            b2 = (byte)(b3 * 2 + 1);
         }
      }

      this.mapDecorations.put(entityIdentifier, new Vec4b((byte)typeIn, b, b2, b4));
   }

   public Packet getMapPacket(ItemStack mapStack, World worldIn, EntityPlayer player) {
      MapData.MapInfo mapInfo = this.playersHashMap.get(player);
      return mapInfo == null ? null : mapInfo.getPacket(mapStack);
   }

   public void updateMapData(int x, int y) {
      super.markDirty();

      for (MapData.MapInfo mapInfo : this.playersArrayList) {
         mapInfo.update(x, y);
      }
   }

   public MapData.MapInfo getMapInfo(EntityPlayer player) {
      MapData.MapInfo mapInfo = this.playersHashMap.get(player);
      if (mapInfo == null) {
         mapInfo = new MapData.MapInfo(player);
         this.playersHashMap.put(player, mapInfo);
         this.playersArrayList.add(mapInfo);
      }

      return mapInfo;
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

      public MapInfo(EntityPlayer player) {
         this.entityplayerObj = player;
      }

      public Packet getPacket(ItemStack stack) {
         if (this.field_176105_d) {
            this.field_176105_d = false;
            return new S34PacketMaps(
               stack.getMetadata(),
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
               ? new S34PacketMaps(stack.getMetadata(), MapData.this.scale, MapData.this.mapDecorations.values(), MapData.this.colors, 0, 0, 0, 0)
               : null;
         }
      }

      public void update(int x, int y) {
         if (this.field_176105_d) {
            this.minX = Math.min(this.minX, x);
            this.minY = Math.min(this.minY, y);
            this.maxX = Math.max(this.maxX, x);
            this.maxY = Math.max(this.maxY, y);
         } else {
            this.field_176105_d = true;
            this.minX = x;
            this.minY = y;
            this.maxX = x;
            this.maxY = y;
         }
      }
   }
}
