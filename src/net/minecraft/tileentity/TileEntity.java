package net.minecraft.tileentity;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.concurrent.Callable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockJukebox;
import net.minecraft.block.state.IBlockState;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class TileEntity {
   private static final Logger logger = LogManager.getLogger();
   private static Map<String, Class<? extends TileEntity>> nameToClassMap = Maps.newHashMap();
   private static Map<Class<? extends TileEntity>, String> classToNameMap = Maps.newHashMap();
   public World worldObj;
   public BlockPos pos = BlockPos.ORIGIN;
   public boolean tileEntityInvalid;
   private int blockMetadata = -1;
   public Block blockType;
   private static final long C = 45356191427277L;

   private static void addMapping(Class<? extends TileEntity> var0, String var1) {
      if (nameToClassMap.containsKey(var1)) {
         throw new IllegalArgumentException("Duplicate id: " + var1);
      }

      nameToClassMap.put(var1, var0);
      classToNameMap.put(var0, var1);
   }

   public World getWorld() {
      return this.worldObj;
   }

   public void setWorldObj(World var1) {
      this.worldObj = var1;
   }

   public boolean hasWorldObj() {
      return this.worldObj != null;
   }

   public void readFromNBT(NBTTagCompound var1) {
      this.pos = new BlockPos(var1.getInteger("x"), var1.getInteger("y"), var1.getInteger("z"));
   }

   public void writeToNBT(NBTTagCompound var1) {
      String var5 = classToNameMap.get(this.getClass());
      if (var5 == null) {
         throw new RuntimeException(this.getClass() + " is missing a mapping! This is a bug!");
      }

      var1.setString("id", var5);
      var1.setInteger("x", this.pos.getX());
      var1.setInteger("y", this.pos.getY());
      var1.setInteger("z", this.pos.getZ());
   }

   public static TileEntity createAndLoadEntity(NBTTagCompound var0) {
      TileEntity var4 = null;

      try {
         Class var5 = nameToClassMap.get(var0.getString("id"));
         if (var5 != null) {
            var4 = (TileEntity)var5.newInstance();
         }
      } catch (Exception var6) {
         var6.printStackTrace();
      }

      if (var4 != null) {
         var4.readFromNBT(var0);
      } else {
         logger.warn("Skipping BlockEntity with id " + var0.getString("id"));
      }

      return var4;
   }

   public int getBlockMetadata() {
      if (this.blockMetadata == -1) {
         IBlockState var4 = this.worldObj.getBlockState(this.pos);
         this.blockMetadata = var4.getBlock().getMetaFromState(var4);
      }

      return this.blockMetadata;
   }

   public void markDirty() {
      if (this.worldObj != null) {
         IBlockState var4 = this.worldObj.getBlockState(this.pos);
         this.blockMetadata = var4.getBlock().getMetaFromState(var4);
         this.worldObj.markChunkDirty(this.pos, this);
         if (this.getBlockType() != Blocks.air) {
            this.worldObj.updateComparatorOutputLevel(this.pos, this.getBlockType());
         }
      }
   }

   public double getDistanceSq(double var1, double var3, double var5) {
      double var7 = this.pos.getX() + 0.5 - var1;
      double var9 = this.pos.getY() + 0.5 - var3;
      double var11 = this.pos.getZ() + 0.5 - var5;
      return var7 * var7 + var9 * var9 + var11 * var11;
   }

   public double getMaxRenderDistanceSquared() {
      return 4096.0;
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public Block getBlockType() {
      if (this.blockType == null) {
         this.blockType = this.worldObj.getBlockState(this.pos).getBlock();
      }

      return this.blockType;
   }

   public Packet getDescriptionPacket() {
      return null;
   }

   public boolean isInvalid() {
      return this.tileEntityInvalid;
   }

   public void invalidate() {
      this.tileEntityInvalid = true;
   }

   public void validate() {
      this.tileEntityInvalid = false;
   }

   public boolean receiveClientEvent(int var1, int var2) {
      return false;
   }

   public void updateContainingBlockInfo() {
      this.blockType = null;
      this.blockMetadata = -1;
   }

   public void addInfoToCrashReport(CrashReportCategory var1) {
      var1.addCrashSectionCallable("Name", new Callable<String>() {
         private static final long a = 121457672360804L;

         public String call() throws Exception {
            return TileEntity.classToNameMap.get(TileEntity.this.getClass()) + " // " + TileEntity.this.getClass().getCanonicalName();
         }
      });
      if (this.worldObj != null) {
         CrashReportCategory.addBlockInfo(var1, this.pos, this.getBlockType(), this.getBlockMetadata());
         var1.addCrashSectionCallable(
            "Actual block type",
            new Callable<String>() {
               private static final long a = 23433353352408L;

               public String call() throws Exception {
                  int var3 = Block.getIdFromBlock(TileEntity.this.worldObj.getBlockState(TileEntity.this.pos).getBlock());

                  try {
                     return String.format(
                        "ID #%d (%s // %s)", var3, Block.getBlockById(var3).getUnlocalizedName(), Block.getBlockById(var3).getClass().getCanonicalName()
                     );
                  } catch (Throwable var5) {
                     return "ID #" + var3;
                  }
               }
            }
         );
         var1.addCrashSectionCallable("Actual block data value", new Callable<String>() {
            private static final long a = 36818990185491L;

            public String call() throws Exception {
               IBlockState var3 = TileEntity.this.worldObj.getBlockState(TileEntity.this.pos);
               int var4 = var3.getBlock().getMetaFromState(var3);
               if (var4 < 0) {
                  return "Unknown? (Got " + var4 + ")";
               }

               String var5 = String.format("%4s", Integer.toBinaryString(var4)).replace(" ", "0");
               return String.format("%1$d / 0x%1$X / 0b%2$s", var4, var5);
            }
         });
      }
   }

   public void setPos(BlockPos var1) {
      this.pos = var1;
   }

   public boolean func_183000_F() {
      return false;
   }

   static {
      addMapping(TileEntityFurnace.class, "Furnace");
      addMapping(TileEntityChest.class, "Chest");
      addMapping(TileEntityEnderChest.class, "EnderChest");
      addMapping(BlockJukebox.TileEntityJukebox.class, "RecordPlayer");
      addMapping(TileEntityDispenser.class, "Trap");
      addMapping(TileEntityDropper.class, "Dropper");
      addMapping(TileEntitySign.class, "Sign");
      addMapping(TileEntityMobSpawner.class, "MobSpawner");
      addMapping(TileEntityNote.class, "Music");
      addMapping(TileEntityPiston.class, "Piston");
      addMapping(TileEntityBrewingStand.class, "Cauldron");
      addMapping(TileEntityEnchantmentTable.class, "EnchantTable");
      addMapping(TileEntityEndPortal.class, "Airportal");
      addMapping(TileEntityCommandBlock.class, "Control");
      addMapping(TileEntityBeacon.class, "Beacon");
      addMapping(TileEntitySkull.class, "Skull");
      addMapping(TileEntityDaylightDetector.class, "DLDetector");
      addMapping(TileEntityHopper.class, "Hopper");
      addMapping(TileEntityComparator.class, "Comparator");
      addMapping(TileEntityFlowerPot.class, "FlowerPot");
      addMapping(TileEntityBanner.class, "Banner");
   }
}
