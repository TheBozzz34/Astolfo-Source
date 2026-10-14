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

   private static void addMapping(Class<? extends TileEntity> cl, String id) {
      if (nameToClassMap.containsKey(id)) {
         throw new IllegalArgumentException("Duplicate id: " + id);
      }

      nameToClassMap.put(id, cl);
      classToNameMap.put(cl, id);
   }

   public World getWorld() {
      return this.worldObj;
   }

   public void setWorldObj(World worldIn) {
      this.worldObj = worldIn;
   }

   public boolean hasWorldObj() {
      return this.worldObj != null;
   }

   public void readFromNBT(NBTTagCompound compound) {
      this.pos = new BlockPos(compound.getInteger("x"), compound.getInteger("y"), compound.getInteger("z"));
   }

   public void writeToNBT(NBTTagCompound compound) {
      String string = classToNameMap.get(this.getClass());
      if (string == null) {
         throw new RuntimeException(this.getClass() + " is missing a mapping! This is a bug!");
      }

      compound.setString("id", string);
      compound.setInteger("x", this.pos.getX());
      compound.setInteger("y", this.pos.getY());
      compound.setInteger("z", this.pos.getZ());
   }

   public static TileEntity createAndLoadEntity(NBTTagCompound nbt) {
      TileEntity tileEntity = null;

      try {
         Class class2 = nameToClassMap.get(nbt.getString("id"));
         if (class2 != null) {
            tileEntity = (TileEntity)class2.newInstance();
         }
      } catch (Exception exception) {
         exception.printStackTrace();
      }

      if (tileEntity != null) {
         tileEntity.readFromNBT(nbt);
      } else {
         logger.warn("Skipping BlockEntity with id " + nbt.getString("id"));
      }

      return tileEntity;
   }

   public int getBlockMetadata() {
      if (this.blockMetadata == -1) {
         IBlockState iBlockState = this.worldObj.getBlockState(this.pos);
         this.blockMetadata = iBlockState.getBlock().getMetaFromState(iBlockState);
      }

      return this.blockMetadata;
   }

   public void markDirty() {
      if (this.worldObj != null) {
         IBlockState iBlockState = this.worldObj.getBlockState(this.pos);
         this.blockMetadata = iBlockState.getBlock().getMetaFromState(iBlockState);
         this.worldObj.markChunkDirty(this.pos, this);
         if (this.getBlockType() != Blocks.air) {
            this.worldObj.updateComparatorOutputLevel(this.pos, this.getBlockType());
         }
      }
   }

   public double getDistanceSq(double x, double y, double z) {
      double d = this.pos.getX() + 0.5 - x;
      double e = this.pos.getY() + 0.5 - y;
      double d2 = this.pos.getZ() + 0.5 - z;
      return d * d + e * e + d2 * d2;
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

   public boolean receiveClientEvent(int id, int typeIn) {
      return false;
   }

   public void updateContainingBlockInfo() {
      this.blockType = null;
      this.blockMetadata = -1;
   }

   public void addInfoToCrashReport(CrashReportCategory reportCategory) {
      reportCategory.addCrashSectionCallable("Name", new Callable<String>() {
         private static final long a = 121457672360804L;

         public String call() throws Exception {
            return TileEntity.classToNameMap.get(TileEntity.this.getClass()) + " // " + TileEntity.this.getClass().getCanonicalName();
         }
      });
      if (this.worldObj != null) {
         CrashReportCategory.addBlockInfo(reportCategory, this.pos, this.getBlockType(), this.getBlockMetadata());
         reportCategory.addCrashSectionCallable(
            "Actual block type",
            new Callable<String>() {
               private static final long a = 23433353352408L;

               public String call() throws Exception {
                  int i = Block.getIdFromBlock(TileEntity.this.worldObj.getBlockState(TileEntity.this.pos).getBlock());

                  try {
                     return String.format(
                        "ID #%d (%s // %s)", i, Block.getBlockById(i).getUnlocalizedName(), Block.getBlockById(i).getClass().getCanonicalName()
                     );
                  } catch (Throwable throwable) {
                     return "ID #" + i;
                  }
               }
            }
         );
         reportCategory.addCrashSectionCallable("Actual block data value", new Callable<String>() {
            private static final long a = 36818990185491L;

            public String call() throws Exception {
               IBlockState iBlockState = TileEntity.this.worldObj.getBlockState(TileEntity.this.pos);
               int j = iBlockState.getBlock().getMetaFromState(iBlockState);
               if (j < 0) {
                  return "Unknown? (Got " + j + ")";
               }

               String string = String.format("%4s", Integer.toBinaryString(j)).replace(" ", "0");
               return String.format("%1$d / 0x%1$X / 0b%2$s", j, string);
            }
         });
      }
   }

   public void setPos(BlockPos posIn) {
      this.pos = posIn;
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
