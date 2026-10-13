package net.minecraft.entity.item;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class EntityPainting extends EntityHanging {
   public EntityPainting.EnumArt art;
   private static final long bb = 54355535786641L;

   public EntityPainting(World var1) {
      super(var1);
   }

   public EntityPainting(World var1, BlockPos var2, EnumFacing var3) {
      super(var1, var2);
      ArrayList var7 = Lists.newArrayList();

      for (EntityPainting.EnumArt var11 : EntityPainting.EnumArt.values()) {
         this.art = var11;
         this.updateFacingWithBoundingBox(var3);
         if (this.onValidSurface()) {
            var7.add(var11);
         }
      }

      if (!var7.isEmpty()) {
         this.art = (EntityPainting.EnumArt)var7.get(this.rand.nextInt(var7.size()));
      }

      this.updateFacingWithBoundingBox(var3);
   }

   public EntityPainting(World var1, BlockPos var2, EnumFacing var3, String var4) {
      this(var1, var2, var3);

      for (EntityPainting.EnumArt var11 : EntityPainting.EnumArt.values()) {
         if (var11.title.equals(var4)) {
            this.art = var11;
            break;
         }
      }

      this.updateFacingWithBoundingBox(var3);
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound var1) {
      var1.setString("Motive", this.art.title);
      super.writeEntityToNBT(var1);
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound var1) {
      String var5 = var1.getString("Motive");

      for (EntityPainting.EnumArt var9 : EntityPainting.EnumArt.values()) {
         if (var9.title.equals(var5)) {
            this.art = var9;
         }
      }

      if (this.art == null) {
         this.art = EntityPainting.EnumArt.KEBAB;
      }

      super.readEntityFromNBT(var1);
   }

   @Override
   public int getWidthPixels() {
      return this.art.sizeX;
   }

   @Override
   public int getHeightPixels() {
      return this.art.sizeY;
   }

   @Override
   public void onBroken(Entity var1) {
      if (this.worldObj.getGameRules().getBoolean("doEntityDrops")) {
         if (var1 instanceof EntityPlayer) {
            EntityPlayer var5 = (EntityPlayer)var1;
            if (var5.capabilities.isCreativeMode) {
               return;
            }
         }

         this.entityDropItem(new ItemStack(Items.painting), 0.0F);
      }
   }

   @Override
   public void setLocationAndAngles(double var1, double var3, double var5, float var7, float var8) {
      BlockPos var9 = this.hangingPosition.add(var1 - this.posX, var3 - this.posY, var5 - this.posZ);
      this.setPosition(var9.getX(), var9.getY(), var9.getZ());
   }

   @Override
   public void setPositionAndRotation2(double var1, double var3, double var5, float var7, float var8, int var9, boolean var10) {
      BlockPos var11 = this.hangingPosition.add(var1 - this.posX, var3 - this.posY, var5 - this.posZ);
      this.setPosition(var11.getX(), var11.getY(), var11.getZ());
   }

   public enum EnumArt {
      KEBAB("Kebab", 16, 16, 0, 0),
      AZTEC("Aztec", 16, 16, 16, 0),
      ALBAN("Alban", 16, 16, 32, 0),
      AZTEC_2("Aztec2", 16, 16, 48, 0),
      BOMB("Bomb", 16, 16, 64, 0),
      PLANT("Plant", 16, 16, 80, 0),
      WASTELAND("Wasteland", 16, 16, 96, 0),
      POOL("Pool", 32, 16, 0, 32),
      COURBET("Courbet", 32, 16, 32, 32),
      SEA("Sea", 32, 16, 64, 32),
      SUNSET("Sunset", 32, 16, 96, 32),
      CREEBET("Creebet", 32, 16, 128, 32),
      WANDERER("Wanderer", 16, 32, 0, 64),
      GRAHAM("Graham", 16, 32, 16, 64),
      MATCH("Match", 32, 32, 0, 128),
      BUST("Bust", 32, 32, 32, 128),
      STAGE("Stage", 32, 32, 64, 128),
      VOID("Void", 32, 32, 96, 128),
      SKULL_AND_ROSES("SkullAndRoses", 32, 32, 128, 128),
      WITHER("Wither", 32, 32, 160, 128),
      FIGHTERS("Fighters", 64, 32, 0, 96),
      POINTER("Pointer", 64, 64, 0, 192),
      PIGSCENE("Pigscene", 64, 64, 64, 192),
      BURNING_SKULL("BurningSkull", 64, 64, 128, 192),
      SKELETON("Skeleton", 64, 48, 192, 64),
      DONKEY_KONG("DonkeyKong", 64, 48, 192, 112);

      public static final int field_180001_A = "SkullAndRoses".length();
      public final String title;
      public final int sizeX;
      public final int sizeY;
      public final int offsetX;
      public final int offsetY;

      EnumArt(String var3, int var4, int var5, int var6, int var7) {
         this.title = var3;
         this.sizeX = var4;
         this.sizeY = var5;
         this.offsetX = var6;
         this.offsetY = var7;
      }
   }
}
