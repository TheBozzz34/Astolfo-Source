package net.minecraft.entity.item;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EntityFallingBlock extends Entity {
   private IBlockState fallTile;
   public int fallTime;
   public boolean shouldDropItem = true;
   private boolean canSetAsBlock;
   private boolean hurtEntities;
   private int fallHurtMax = 40;
   private float fallHurtAmount = 2.0F;
   public NBTTagCompound tileEntityData;
   private static final long ab = 68513339873660L;

   public EntityFallingBlock(World var1) {
      super(var1);
   }

   public EntityFallingBlock(World var1, double var2, double var4, double var6, IBlockState var8) {
      super(var1);
      this.fallTile = var8;
      this.preventEntitySpawning = true;
      this.setSize(0.98F, 0.98F);
      this.setPosition(var2, var4, var6);
      this.motionX = 0.0;
      this.motionY = 0.0;
      this.motionZ = 0.0;
      this.prevPosX = var2;
      this.prevPosY = var4;
      this.prevPosZ = var6;
   }

   @Override
   public boolean canTriggerWalking() {
      return false;
   }

   @Override
   public void entityInit() {
   }

   @Override
   public boolean canBeCollidedWith() {
      return !this.isDead;
   }

   @Override
   public void onUpdate() {
      Block var4 = this.fallTile.getBlock();
      if (var4.getMaterial() == Material.air) {
         this.setDead();
      } else {
         this.prevPosX = this.posX;
         this.prevPosY = this.posY;
         this.prevPosZ = this.posZ;
         if (this.fallTime++ == 0) {
            BlockPos var5 = new BlockPos(this);
            if (this.worldObj.getBlockState(var5).getBlock() == var4) {
               this.worldObj.setBlockToAir(var5);
            } else if (!this.worldObj.isRemote) {
               this.setDead();
               return;
            }
         }

         this.motionY -= 0.04F;
         this.moveEntity(this.motionX, this.motionY, this.motionZ);
         this.motionX *= 0.98F;
         this.motionY *= 0.98F;
         this.motionZ *= 0.98F;
         if (!this.worldObj.isRemote) {
            BlockPos var11 = new BlockPos(this);
            if (this.onGround) {
               this.motionX *= 0.7F;
               this.motionZ *= 0.7F;
               this.motionY *= -0.5;
               if (this.worldObj.getBlockState(var11).getBlock() != Blocks.piston_extension) {
                  this.setDead();
                  if (!this.canSetAsBlock) {
                     if (this.worldObj.canBlockBePlaced(var4, var11, true, EnumFacing.UP, (Entity)null, (ItemStack)null)
                        && !BlockFalling.canFallInto(this.worldObj, var11.down())
                        && this.worldObj.setBlockState(var11, this.fallTile, 3)) {
                        if (var4 instanceof BlockFalling) {
                           ((BlockFalling)var4).onEndFalling(this.worldObj, var11);
                        }

                        if (this.tileEntityData != null && var4 instanceof ITileEntityProvider) {
                           TileEntity var6 = this.worldObj.getTileEntity(var11);
                           if (var6 != null) {
                              NBTTagCompound var7 = new NBTTagCompound();
                              var6.writeToNBT(var7);

                              for (String var9 : this.tileEntityData.getKeySet()) {
                                 NBTBase var10 = this.tileEntityData.getTag(var9);
                                 if (!var9.equals("x") && !var9.equals("y") && !var9.equals("z")) {
                                    var7.setTag(var9, var10.copy());
                                 }
                              }

                              var6.readFromNBT(var7);
                              var6.markDirty();
                           }
                        }
                     } else if (this.shouldDropItem && this.worldObj.getGameRules().getBoolean("doEntityDrops")) {
                        this.entityDropItem(new ItemStack(var4, 1, var4.damageDropped(this.fallTile)), 0.0F);
                     }
                  }
               }
            } else if (this.fallTime > 100 && !this.worldObj.isRemote && (var11.getY() < 1 || var11.getY() > 256) || this.fallTime > 600) {
               if (this.shouldDropItem && this.worldObj.getGameRules().getBoolean("doEntityDrops")) {
                  this.entityDropItem(new ItemStack(var4, 1, var4.damageDropped(this.fallTile)), 0.0F);
               }

               this.setDead();
            }
         }
      }
   }

   @Override
   public void fall(float var1, float var2) {
      Block var6 = this.fallTile.getBlock();
      if (this.hurtEntities) {
         int var7 = MathHelper.ceiling_float_int(var1 - 1.0F);
         if (var7 > 0) {
            ArrayList var8 = Lists.newArrayList(this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox()));
            boolean var9 = var6 == Blocks.anvil;
            DamageSource var10 = var9 ? DamageSource.anvil : DamageSource.fallingBlock;

            for (Object var12$raw : var8) {
               Entity var12 = (Entity)var12$raw;
               var12.attackEntityFrom(var10, Math.min(MathHelper.floor_float(var7 * this.fallHurtAmount), this.fallHurtMax));
            }

            if (var9 && this.rand.nextFloat() < 0.05F + var7 * 0.05) {
               int var13 = this.fallTile.getValue(BlockAnvil.DAMAGE);
               if (++var13 > 2) {
                  this.canSetAsBlock = true;
               } else {
                  this.fallTile = this.fallTile.withProperty(BlockAnvil.DAMAGE, var13);
               }
            }
         }
      }
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound var1) {
      Block var5 = this.fallTile != null ? this.fallTile.getBlock() : Blocks.air;
      ResourceLocation var6 = Block.blockRegistry.getNameForObject(var5);
      var1.setString("Block", var6 == null ? "" : var6.toString());
      var1.setByte("Data", (byte)var5.getMetaFromState(this.fallTile));
      var1.setByte("Time", (byte)this.fallTime);
      var1.setBoolean("DropItem", this.shouldDropItem);
      var1.setBoolean("HurtEntities", this.hurtEntities);
      var1.setFloat("FallHurtAmount", this.fallHurtAmount);
      var1.setInteger("FallHurtMax", this.fallHurtMax);
      if (this.tileEntityData != null) {
         var1.setTag("TileEntityData", this.tileEntityData);
      }
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound var1) {
      int var5 = var1.getByte("Data") & 255;
      if (var1.hasKey("Block", 8)) {
         this.fallTile = Block.getBlockFromName(var1.getString("Block")).getStateFromMeta(var5);
      } else if (var1.hasKey("TileID", 99)) {
         this.fallTile = Block.getBlockById(var1.getInteger("TileID")).getStateFromMeta(var5);
      } else {
         this.fallTile = Block.getBlockById(var1.getByte("Tile") & 255).getStateFromMeta(var5);
      }

      this.fallTime = var1.getByte("Time") & 255;
      Block var6 = this.fallTile.getBlock();
      if (var1.hasKey("HurtEntities", 99)) {
         this.hurtEntities = var1.getBoolean("HurtEntities");
         this.fallHurtAmount = var1.getFloat("FallHurtAmount");
         this.fallHurtMax = var1.getInteger("FallHurtMax");
      } else if (var6 == Blocks.anvil) {
         this.hurtEntities = true;
      }

      if (var1.hasKey("DropItem", 99)) {
         this.shouldDropItem = var1.getBoolean("DropItem");
      }

      if (var1.hasKey("TileEntityData", 10)) {
         this.tileEntityData = var1.getCompoundTag("TileEntityData");
      }

      if (var6 == null || var6.getMaterial() == Material.air) {
         this.fallTile = Blocks.sand.getDefaultState();
      }
   }

   public World getWorldObj() {
      return this.worldObj;
   }

   public void setHurtEntities(boolean var1) {
      this.hurtEntities = var1;
   }

   @Override
   public boolean canRenderOnFire() {
      return false;
   }

   @Override
   public void addEntityCrashInfo(CrashReportCategory var1) {
      super.addEntityCrashInfo(var1);
      if (this.fallTile != null) {
         Block var5 = this.fallTile.getBlock();
         var1.addCrashSection("Immitating block ID", Block.getIdFromBlock(var5));
         var1.addCrashSection("Immitating block data", var5.getMetaFromState(this.fallTile));
      }
   }

   public IBlockState getBlock() {
      return this.fallTile;
   }
}
