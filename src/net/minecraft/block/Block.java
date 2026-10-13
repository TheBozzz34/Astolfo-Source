package net.minecraft.block;

import bypass.Astolfo;
import bypass.event.CancellableEvent;
import bypass.event.render.BlockOpaqueEvent;
import bypass.module.ModuleManager;
import bypass.module.visuals.Xray;
import bypass.module.visuals.XrayHypixelMode;
import java.util.List;
import java.util.Random;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ObjectIntIdentityMap;
import net.minecraft.util.RegistryNamespacedDefaultedByKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class Block {
   private static final ResourceLocation AIR_ID = new ResourceLocation("air");
   public static final RegistryNamespacedDefaultedByKey<ResourceLocation, Block> blockRegistry = new RegistryNamespacedDefaultedByKey<>(AIR_ID);
   public static final ObjectIntIdentityMap<IBlockState> BLOCK_STATE_IDS = new ObjectIntIdentityMap<>();
   private CreativeTabs displayOnCreativeTab;
   public static final Block.SoundType soundTypeStone = new Block.SoundType("stone", 1.0F, 1.0F);
   public static final Block.SoundType soundTypeWood = new Block.SoundType("wood", 1.0F, 1.0F);
   public static final Block.SoundType soundTypeGravel = new Block.SoundType("gravel", 1.0F, 1.0F);
   public static final Block.SoundType soundTypeGrass = new Block.SoundType("grass", 1.0F, 1.0F);
   public static final Block.SoundType soundTypePiston = new Block.SoundType("stone", 1.0F, 1.0F);
   public static final Block.SoundType soundTypeMetal = new Block.SoundType("stone", 1.0F, 1.5F);
   public static final Block.SoundType soundTypeGlass = new Block.SoundType("stone", 1.0F, 1.0F) {
      private static final long a = 54295512307956L;

      @Override
      public String getBreakSound() {
         return "dig.glass";
      }

      @Override
      public String getPlaceSound() {
         return "step.stone";
      }
   };
   public static final Block.SoundType soundTypeCloth = new Block.SoundType("cloth", 1.0F, 1.0F);
   public static final Block.SoundType soundTypeSand = new Block.SoundType("sand", 1.0F, 1.0F);
   public static final Block.SoundType soundTypeSnow = new Block.SoundType("snow", 1.0F, 1.0F);
   public static final Block.SoundType soundTypeLadder = new Block.SoundType("ladder", 1.0F, 1.0F) {
      private static final long a = 98372374212397L;

      @Override
      public String getBreakSound() {
         return "dig.wood";
      }
   };
   public static final Block.SoundType soundTypeAnvil = new Block.SoundType("anvil", 0.3F, 1.0F) {
      private static final long a = 28254367381931L;

      @Override
      public String getBreakSound() {
         return "dig.stone";
      }

      @Override
      public String getPlaceSound() {
         return "random.anvil_land";
      }
   };
   public static final Block.SoundType SLIME_SOUND = new Block.SoundType("slime", 1.0F, 1.0F) {
      private static final long a = 14049040404977L;

      @Override
      public String getBreakSound() {
         return "mob.slime.big";
      }

      @Override
      public String getPlaceSound() {
         return "mob.slime.big";
      }

      @Override
      public String getStepSound() {
         return "mob.slime.small";
      }
   };
   public boolean fullBlock;
   public int lightOpacity;
   public boolean translucent;
   public int lightValue;
   public boolean useNeighborBrightness;
   public float blockHardness;
   public float blockResistance;
   public boolean enableStats = true;
   public boolean needsRandomTick;
   public boolean isBlockContainer;
   public double minX;
   public double minY;
   public double minZ;
   public double maxX;
   public double maxY;
   public double maxZ;
   public Block.SoundType stepSound = soundTypeStone;
   public float blockParticleGravity = 1.0F;
   public final Material blockMaterial;
   public final MapColor blockMapColor;
   public float slipperiness = 0.6F;
   public final BlockState blockState;
   private IBlockState defaultBlockState;
   private String unlocalizedName;
   private static final long ab = 31167430580484L;

   public static int getIdFromBlock(Block var0) {
      return blockRegistry.getIDForObject(var0);
   }

   public static int getStateId(IBlockState var0) {
      Block var3 = var0.getBlock();
      return getIdFromBlock(var3) + (var3.getMetaFromState(var0) << 12);
   }

   public static Block getBlockById(int var0) {
      return blockRegistry.getObjectById(var0);
   }

   public static IBlockState getStateById(int var0) {
      int var3 = var0 & 4095;
      int var4 = var0 >> 12 & 15;
      return getBlockById(var3).getStateFromMeta(var4);
   }

   public static Block getBlockFromItem(Item var0) {
      return var0 instanceof ItemBlock ? ((ItemBlock)var0).getBlock() : null;
   }

   public static Block getBlockFromName(String var0) {
      ResourceLocation var4 = new ResourceLocation(var0);
      if (blockRegistry.containsKey(var4)) {
         return blockRegistry.getObject(var4);
      }

      try {
         return blockRegistry.getObjectById(Integer.parseInt(var0));
      } catch (NumberFormatException var6) {
         return null;
      }
   }

   public boolean isFullBlock() {
      return this.fullBlock;
   }

   public int getLightOpacity() {
      return this.lightOpacity;
   }

   public boolean isTranslucent() {
      return this.translucent;
   }

   public int getLightValue() {
      return this.lightValue;
   }

   public boolean getUseNeighborBrightness() {
      return this.useNeighborBrightness;
   }

   public Material getMaterial() {
      return this.blockMaterial;
   }

   public MapColor getMapColor(IBlockState var1) {
      return this.blockMapColor;
   }

   public IBlockState getStateFromMeta(int var1) {
      return this.getDefaultState();
   }

   public int getMetaFromState(IBlockState var1) {
      if (var1 != null && !var1.getPropertyNames().isEmpty()) {
         throw new IllegalArgumentException("Don't know how to convert " + var1 + " back into data...");
      } else {
         return 0;
      }
   }

   public IBlockState getActualState(IBlockState var1, IBlockAccess var2, BlockPos var3) {
      return var1;
   }

   public Block(Material var1, MapColor var2) {
      this.blockMaterial = var1;
      this.blockMapColor = var2;
      this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
      this.fullBlock = this.isOpaqueCube();
      this.lightOpacity = this.isOpaqueCube() ? 255 : 0;
      this.translucent = !var1.blocksLight();
      this.blockState = this.createBlockState();
      this.setDefaultState(this.blockState.getBaseState());
   }

   public Block(Material var1) {
      this(var1, var1.getMaterialMapColor());
   }

   public Block setStepSound(Block.SoundType var1) {
      this.stepSound = var1;
      return this;
   }

   public Block setLightOpacity(int var1) {
      this.lightOpacity = var1;
      return this;
   }

   public Block setLightLevel(float var1) {
      this.lightValue = (int)(15.0F * var1);
      return this;
   }

   public Block setResistance(float var1) {
      this.blockResistance = var1 * 3.0F;
      return this;
   }

   public boolean isBlockNormalCube() {
      return this.blockMaterial.blocksMovement() && this.isFullCube();
   }

   public boolean isNormalCube() {
      return this.blockMaterial.isOpaque() && this.isFullCube() && !this.canProvidePower();
   }

   public boolean isVisuallyOpaque() {
      BlockOpaqueEvent var4 = new BlockOpaqueEvent();
      Astolfo.INSTANCE.getEventBus().post(var4);
      return var4.isCancelled() ? false : this.blockMaterial.blocksMovement() && this.isFullCube();
   }

   public boolean isFullCube() {
      return !ModuleManager.s.isEnabled() || Xray.isEnabledOre(this);
   }

   public boolean isPassable(IBlockAccess var1, BlockPos var2) {
      return !this.blockMaterial.blocksMovement();
   }

   public int getRenderType() {
      return 3;
   }

   public boolean isReplaceable(World var1, BlockPos var2) {
      return false;
   }

   public Block setHardness(float var1) {
      this.blockHardness = var1;
      if (this.blockResistance < var1 * 5.0F) {
         this.blockResistance = var1 * 5.0F;
      }

      return this;
   }

   public Block setBlockUnbreakable() {
      this.setHardness(-1.0F);
      return this;
   }

   public float getBlockHardness(World var1, BlockPos var2) {
      return this.blockHardness;
   }

   public Block setTickRandomly(boolean var1) {
      this.needsRandomTick = var1;
      return this;
   }

   public boolean getTickRandomly() {
      return this.needsRandomTick;
   }

   public boolean hasTileEntity() {
      return this.isBlockContainer;
   }

   public final void setBlockBounds(float var1, float var2, float var3, float var4, float var5, float var6) {
      this.minX = var1;
      this.minY = var2;
      this.minZ = var3;
      this.maxX = var4;
      this.maxY = var5;
      this.maxZ = var6;
   }

   public int getMixedBrightnessForBlock(IBlockAccess var1, BlockPos var2) {
      if (ModuleManager.s.isEnabled() && Xray.isEnabledOre(this)) {
         return 200;
      } else {
         Block var6 = var1.getBlockState(var2).getBlock();
         int var7 = var1.getCombinedLight(var2, var6.getLightValue());
         if (var7 == 0 && var6 instanceof BlockSlab) {
            var2 = var2.down();
            var6 = var1.getBlockState(var2).getBlock();
            return var1.getCombinedLight(var2, var6.getLightValue());
         } else {
            return var7;
         }
      }
   }

   public boolean shouldSideBeRendered(IBlockAccess var1, BlockPos var2, EnumFacing var3) {
      if (ModuleManager.s.isEnabled() && Xray.isEnabledOre(this)) {
         if (Xray.hypixel.getValue() == XrayHypixelMode.FALSE) {
            return true;
         }

         for (EnumFacing var10 : EnumFacing.VALUES) {
            if (Xray.revealedOres.contains(var2.offset(var10))) {
               return true;
            }

            if (this.D(var1, var2.offset(var10), var10)) {
               return true;
            }
         }
      }

      switch (var3) {
         case DOWN:
            if (this.minY > 0.0) {
               return true;
            }
            break;
         case UP:
            if (this.maxY < 1.0) {
               return true;
            }
            break;
         case NORTH:
            if (this.minZ > 0.0) {
               return true;
            }
            break;
         case SOUTH:
            if (this.maxZ < 1.0) {
               return true;
            }
            break;
         case WEST:
            if (this.minX > 0.0) {
               return true;
            }
            break;
         case EAST:
            if (this.maxX < 1.0) {
               return true;
            }
      }

      return !var1.getBlockState(var2).getBlock().isOpaqueCube();
   }

   public boolean D(IBlockAccess var1, BlockPos var2, EnumFacing var3) {
      return var3 == EnumFacing.DOWN && this.minY > 0.0
         ? true
         : (
            var3 == EnumFacing.UP && this.maxY < 1.0
               ? true
               : (
                  var3 == EnumFacing.NORTH && this.minZ > 0.0
                     ? true
                     : (
                        var3 == EnumFacing.SOUTH && this.maxZ < 1.0
                           ? true
                           : (
                              var3 == EnumFacing.WEST && this.minX > 0.0
                                 ? true
                                 : (var3 == EnumFacing.EAST && this.maxX < 1.0 ? true : !var1.getBlockState(var2).getBlock().isOpaqueCube())
                           )
                     )
               )
         );
   }

   public boolean isBlockSolid(IBlockAccess var1, BlockPos var2, EnumFacing var3) {
      return var1.getBlockState(var2).getBlock().getMaterial().isSolid();
   }

   public AxisAlignedBB getSelectedBoundingBox(World var1, BlockPos var2) {
      return new AxisAlignedBB(
         var2.getX() + this.minX, var2.getY() + this.minY, var2.getZ() + this.minZ, var2.getX() + this.maxX, var2.getY() + this.maxY, var2.getZ() + this.maxZ
      );
   }

   public void addCollisionBoxesToList(World var1, BlockPos var2, IBlockState var3, AxisAlignedBB var4, List<AxisAlignedBB> var5, Entity var6) {
      AxisAlignedBB var10 = this.getCollisionBoundingBox(var1, var2, var3);
      if (var6 == Minecraft.getMinecraft().thePlayer) {
         Block.CollisionBoxEvent var11 = new Block.CollisionBoxEvent(var10, this, var2.getX(), var2.getY(), var2.getZ());
         Astolfo.INSTANCE.getEventBus().post(var11);
         var10 = var11.getBoundingBox();
      }

      if (var10 != null && var4.intersectsWith(var10)) {
         var5.add(var10);
      }
   }

   public AxisAlignedBB getCollisionBoundingBox(World var1, BlockPos var2, IBlockState var3) {
      return new AxisAlignedBB(
         var2.getX() + this.minX, var2.getY() + this.minY, var2.getZ() + this.minZ, var2.getX() + this.maxX, var2.getY() + this.maxY, var2.getZ() + this.maxZ
      );
   }

   public boolean isOpaqueCube() {
      return true;
   }

   public boolean canCollideCheck(IBlockState var1, boolean var2) {
      return this.isCollidable();
   }

   public boolean isCollidable() {
      return true;
   }

   public void randomTick(World var1, BlockPos var2, IBlockState var3, Random var4) {
      this.updateTick(var1, var2, var3, var4);
   }

   public void updateTick(World var1, BlockPos var2, IBlockState var3, Random var4) {
   }

   public void randomDisplayTick(World var1, BlockPos var2, IBlockState var3, Random var4) {
   }

   public void onBlockDestroyedByPlayer(World var1, BlockPos var2, IBlockState var3) {
   }

   public void onNeighborBlockChange(World var1, BlockPos var2, IBlockState var3, Block var4) {
   }

   public int tickRate(World var1) {
      return 10;
   }

   public void onBlockAdded(World var1, BlockPos var2, IBlockState var3) {
   }

   public void breakBlock(World var1, BlockPos var2, IBlockState var3) {
   }

   public int quantityDropped(Random var1) {
      return 1;
   }

   public Item getItemDropped(IBlockState var1, Random var2, int var3) {
      return Item.getItemFromBlock(this);
   }

   public float getPlayerRelativeBlockHardness(EntityPlayer var1, World var2, BlockPos var3) {
      float var7 = this.getBlockHardness(var2, var3);
      return var7 < 0.0F
         ? 0.0F
         : (!var1.canHarvestBlock(this) ? var1.getToolDigEfficiency(this) / var7 / 100.0F : var1.getToolDigEfficiency(this) / var7 / 30.0F);
   }

   public final void dropBlockAsItem(World var1, BlockPos var2, IBlockState var3, int var4) {
      this.dropBlockAsItemWithChance(var1, var2, var3, 1.0F, var4);
   }

   public void dropBlockAsItemWithChance(World var1, BlockPos var2, IBlockState var3, float var4, int var5) {
      if (!var1.isRemote) {
         int var9 = this.quantityDroppedWithBonus(var5, var1.rand);

         for (int var10 = 0; var10 < var9; var10++) {
            if (var1.rand.nextFloat() <= var4) {
               Item var11 = this.getItemDropped(var3, var1.rand, var5);
               if (var11 != null) {
                  spawnAsEntity(var1, var2, new ItemStack(var11, 1, this.damageDropped(var3)));
               }
            }
         }
      }
   }

   public static void spawnAsEntity(World var0, BlockPos var1, ItemStack var2) {
      if (!var0.isRemote && var0.getGameRules().getBoolean("doTileDrops")) {
         float var6 = 0.5F;
         double var7 = var0.rand.nextFloat() * var6 + (1.0F - var6) * 0.5;
         double var9 = var0.rand.nextFloat() * var6 + (1.0F - var6) * 0.5;
         double var11 = var0.rand.nextFloat() * var6 + (1.0F - var6) * 0.5;
         EntityItem var13 = new EntityItem(var0, var1.getX() + var7, var1.getY() + var9, var1.getZ() + var11, var2);
         var13.setDefaultPickupDelay();
         var0.spawnEntityInWorld(var13);
      }
   }

   public void dropXpOnBlockBreak(World var1, BlockPos var2, int var3) {
      if (!var1.isRemote) {
         while (var3 > 0) {
            int var7 = EntityXPOrb.getXPSplit(var3);
            var3 -= var7;
            var1.spawnEntityInWorld(new EntityXPOrb(var1, var2.getX() + 0.5, var2.getY() + 0.5, var2.getZ() + 0.5, var7));
         }
      }
   }

   public int damageDropped(IBlockState var1) {
      return 0;
   }

   public float getExplosionResistance(Entity var1) {
      return this.blockResistance / 5.0F;
   }

   public MovingObjectPosition collisionRayTrace(World var1, BlockPos var2, Vec3 var3, Vec3 var4) {
      this.setBlockBoundsBasedOnState(var1, var2);
      var3 = var3.addVector(-var2.getX(), -var2.getY(), -var2.getZ());
      var4 = var4.addVector(-var2.getX(), -var2.getY(), -var2.getZ());
      Vec3 var8 = var3.getIntermediateWithXValue(var4, this.minX);
      Vec3 var9 = var3.getIntermediateWithXValue(var4, this.maxX);
      Vec3 var10 = var3.getIntermediateWithYValue(var4, this.minY);
      Vec3 var11 = var3.getIntermediateWithYValue(var4, this.maxY);
      Vec3 var12 = var3.getIntermediateWithZValue(var4, this.minZ);
      Vec3 var13 = var3.getIntermediateWithZValue(var4, this.maxZ);
      if (!this.isVecInsideYZBounds(var8)) {
         var8 = null;
      }

      if (!this.isVecInsideYZBounds(var9)) {
         var9 = null;
      }

      if (!this.isVecInsideXZBounds(var10)) {
         var10 = null;
      }

      if (!this.isVecInsideXZBounds(var11)) {
         var11 = null;
      }

      if (!this.isVecInsideXYBounds(var12)) {
         var12 = null;
      }

      if (!this.isVecInsideXYBounds(var13)) {
         var13 = null;
      }

      Vec3 var14 = null;
      if (var8 != null && (var14 == null || var3.squareDistanceTo(var8) < var3.squareDistanceTo(var14))) {
         var14 = var8;
      }

      if (var9 != null && (var14 == null || var3.squareDistanceTo(var9) < var3.squareDistanceTo(var14))) {
         var14 = var9;
      }

      if (var10 != null && (var14 == null || var3.squareDistanceTo(var10) < var3.squareDistanceTo(var14))) {
         var14 = var10;
      }

      if (var11 != null && (var14 == null || var3.squareDistanceTo(var11) < var3.squareDistanceTo(var14))) {
         var14 = var11;
      }

      if (var12 != null && (var14 == null || var3.squareDistanceTo(var12) < var3.squareDistanceTo(var14))) {
         var14 = var12;
      }

      if (var13 != null && (var14 == null || var3.squareDistanceTo(var13) < var3.squareDistanceTo(var14))) {
         var14 = var13;
      }

      if (var14 == null) {
         return null;
      }

      EnumFacing var15 = null;
      if (var14 == var8) {
         var15 = EnumFacing.WEST;
      }

      if (var14 == var9) {
         var15 = EnumFacing.EAST;
      }

      if (var14 == var10) {
         var15 = EnumFacing.DOWN;
      }

      if (var14 == var11) {
         var15 = EnumFacing.UP;
      }

      if (var14 == var12) {
         var15 = EnumFacing.NORTH;
      }

      if (var14 == var13) {
         var15 = EnumFacing.SOUTH;
      }

      return new MovingObjectPosition(var14.addVector(var2.getX(), var2.getY(), var2.getZ()), var15, var2);
   }

   private boolean isVecInsideYZBounds(Vec3 var1) {
      return var1 == null ? false : var1.yCoord >= this.minY && var1.yCoord <= this.maxY && var1.zCoord >= this.minZ && var1.zCoord <= this.maxZ;
   }

   private boolean isVecInsideXZBounds(Vec3 var1) {
      return var1 == null ? false : var1.xCoord >= this.minX && var1.xCoord <= this.maxX && var1.zCoord >= this.minZ && var1.zCoord <= this.maxZ;
   }

   private boolean isVecInsideXYBounds(Vec3 var1) {
      return var1 == null ? false : var1.xCoord >= this.minX && var1.xCoord <= this.maxX && var1.yCoord >= this.minY && var1.yCoord <= this.maxY;
   }

   public void onBlockDestroyedByExplosion(World var1, BlockPos var2, Explosion var3) {
   }

   public EnumWorldBlockLayer getBlockLayer() {
      return EnumWorldBlockLayer.SOLID;
   }

   public boolean canReplace(World var1, BlockPos var2, EnumFacing var3, ItemStack var4) {
      return this.canPlaceBlockOnSide(var1, var2, var3);
   }

   public boolean canPlaceBlockOnSide(World var1, BlockPos var2, EnumFacing var3) {
      return this.canPlaceBlockAt(var1, var2);
   }

   public boolean canPlaceBlockAt(World var1, BlockPos var2) {
      return var1.getBlockState(var2).getBlock().blockMaterial.isReplaceable();
   }

   public boolean onBlockActivated(World var1, BlockPos var2, IBlockState var3, EntityPlayer var4, EnumFacing var5, float var6, float var7, float var8) {
      return false;
   }

   public void onEntityCollidedWithBlock(World var1, BlockPos var2, Entity var3) {
   }

   public IBlockState onBlockPlaced(World var1, BlockPos var2, EnumFacing var3, float var4, float var5, float var6, int var7, EntityLivingBase var8) {
      return this.getStateFromMeta(var7);
   }

   public void onBlockClicked(World var1, BlockPos var2, EntityPlayer var3) {
   }

   public Vec3 modifyAcceleration(World var1, BlockPos var2, Entity var3, Vec3 var4) {
      return var4;
   }

   public void setBlockBoundsBasedOnState(IBlockAccess var1, BlockPos var2) {
   }

   public final double getBlockBoundsMinX() {
      return this.minX;
   }

   public final double getBlockBoundsMaxX() {
      return this.maxX;
   }

   public final double getBlockBoundsMinY() {
      return this.minY;
   }

   public final double getBlockBoundsMaxY() {
      return this.maxY;
   }

   public final double getBlockBoundsMinZ() {
      return this.minZ;
   }

   public final double getBlockBoundsMaxZ() {
      return this.maxZ;
   }

   public int getBlockColor() {
      return 16777215;
   }

   public int getRenderColor(IBlockState var1) {
      return 16777215;
   }

   public int colorMultiplier(IBlockAccess var1, BlockPos var2, int var3) {
      return 16777215;
   }

   public final int colorMultiplier(IBlockAccess var1, BlockPos var2) {
      return this.colorMultiplier(var1, var2, 0);
   }

   public int getWeakPower(IBlockAccess var1, BlockPos var2, IBlockState var3, EnumFacing var4) {
      return 0;
   }

   public boolean canProvidePower() {
      return false;
   }

   public void onEntityCollidedWithBlock(World var1, BlockPos var2, IBlockState var3, Entity var4) {
   }

   public int getStrongPower(IBlockAccess var1, BlockPos var2, IBlockState var3, EnumFacing var4) {
      return 0;
   }

   public void setBlockBoundsForItemRender() {
   }

   public void harvestBlock(World var1, EntityPlayer var2, BlockPos var3, IBlockState var4, TileEntity var5) {
      var2.triggerAchievement(StatList.mineBlockStatArray[getIdFromBlock(this)]);
      var2.addExhaustion(0.025F);
      if (this.canSilkHarvest() && EnchantmentHelper.getSilkTouchModifier(var2)) {
         ItemStack var10 = this.createStackedBlock(var4);
         if (var10 != null) {
            spawnAsEntity(var1, var3, var10);
         }
      } else {
         int var9 = EnchantmentHelper.getFortuneModifier(var2);
         this.dropBlockAsItem(var1, var3, var4, var9);
      }
   }

   public boolean canSilkHarvest() {
      return this.isFullCube() && !this.isBlockContainer;
   }

   public ItemStack createStackedBlock(IBlockState var1) {
      int var5 = 0;
      Item var6 = Item.getItemFromBlock(this);
      if (var6 != null && var6.getHasSubtypes()) {
         var5 = this.getMetaFromState(var1);
      }

      return new ItemStack(var6, 1, var5);
   }

   public int quantityDroppedWithBonus(int var1, Random var2) {
      return this.quantityDropped(var2);
   }

   public void onBlockPlacedBy(World var1, BlockPos var2, IBlockState var3, EntityLivingBase var4, ItemStack var5) {
   }

   public boolean canSpawnInBlock() {
      return !this.blockMaterial.isSolid() && !this.blockMaterial.isLiquid();
   }

   public Block setUnlocalizedName(String var1) {
      this.unlocalizedName = var1;
      return this;
   }

   public String getLocalizedName() {
      return StatCollector.translateToLocal(this.getUnlocalizedName() + ".name");
   }

   public String getUnlocalizedName() {
      return "tile." + this.unlocalizedName;
   }

   public boolean onBlockEventReceived(World var1, BlockPos var2, IBlockState var3, int var4, int var5) {
      return false;
   }

   public boolean getEnableStats() {
      return this.enableStats;
   }

   public Block disableStats() {
      this.enableStats = false;
      return this;
   }

   public int getMobilityFlag() {
      return this.blockMaterial.getMaterialMobility();
   }

   public float getAmbientOcclusionLightValue() {
      return this.isBlockNormalCube() ? 0.2F : 1.0F;
   }

   public void onFallenUpon(World var1, BlockPos var2, Entity var3, float var4) {
      var3.fall(var4, 1.0F);
   }

   public void onLanded(World var1, Entity var2) {
      var2.motionY = 0.0;
   }

   public Item getItem(World var1, BlockPos var2) {
      return Item.getItemFromBlock(this);
   }

   public int getDamageValue(World var1, BlockPos var2) {
      return this.damageDropped(var1.getBlockState(var2));
   }

   public void getSubBlocks(Item var1, CreativeTabs var2, List<ItemStack> var3) {
      var3.add(new ItemStack(var1, 1, 0));
   }

   public CreativeTabs getCreativeTabToDisplayOn() {
      return this.displayOnCreativeTab;
   }

   public Block setCreativeTab(CreativeTabs var1) {
      this.displayOnCreativeTab = var1;
      return this;
   }

   public void onBlockHarvested(World var1, BlockPos var2, IBlockState var3, EntityPlayer var4) {
   }

   public void fillWithRain(World var1, BlockPos var2) {
   }

   public boolean isFlowerPot() {
      return false;
   }

   public boolean requiresUpdates() {
      return true;
   }

   public boolean canDropFromExplosion(Explosion var1) {
      return true;
   }

   public boolean isAssociatedBlock(Block var1) {
      return this == var1;
   }

   public static boolean isEqualTo(Block var0, Block var1) {
      return var0 != null && var1 != null ? (var0 == var1 ? true : var0.isAssociatedBlock(var1)) : false;
   }

   public boolean hasComparatorInputOverride() {
      return false;
   }

   public int getComparatorInputOverride(World var1, BlockPos var2) {
      return 0;
   }

   public IBlockState getStateForEntityRender(IBlockState var1) {
      return var1;
   }

   public BlockState createBlockState() {
      return new BlockState(this);
   }

   public BlockState getBlockState() {
      return this.blockState;
   }

   public final void setDefaultState(IBlockState var1) {
      this.defaultBlockState = var1;
   }

   public final IBlockState getDefaultState() {
      return this.defaultBlockState;
   }

   public Block.EnumOffsetType getOffsetType() {
      return Block.EnumOffsetType.NONE;
   }

   @Override
   public String toString() {
      return "Block{" + blockRegistry.getNameForObject(this) + "}";
   }

   public static void registerBlocks() {
      registerBlock(0, AIR_ID, new BlockAir().setUnlocalizedName("air"));
      registerBlock(1, "stone", new BlockStone().setHardness(1.5F).setResistance(10.0F).setStepSound(soundTypePiston).setUnlocalizedName("stone"));
      registerBlock(2, "grass", new BlockGrass().setHardness(0.6F).setStepSound(soundTypeGrass).setUnlocalizedName("grass"));
      registerBlock(3, "dirt", new BlockDirt().setHardness(0.5F).setStepSound(soundTypeGravel).setUnlocalizedName("dirt"));
      Block var4 = new Block(Material.rock)
         .setHardness(2.0F)
         .setResistance(10.0F)
         .setStepSound(soundTypePiston)
         .setUnlocalizedName("stonebrick")
         .setCreativeTab(CreativeTabs.tabBlock);
      registerBlock(4, "cobblestone", var4);
      Block var5 = new BlockPlanks().setHardness(2.0F).setResistance(5.0F).setStepSound(soundTypeWood).setUnlocalizedName("wood");
      registerBlock(5, "planks", var5);
      registerBlock(6, "sapling", new BlockSapling().setHardness(0.0F).setStepSound(soundTypeGrass).setUnlocalizedName("sapling"));
      registerBlock(
         7,
         "bedrock",
         new Block(Material.rock)
            .setBlockUnbreakable()
            .setResistance(6000000.0F)
            .setStepSound(soundTypePiston)
            .setUnlocalizedName("bedrock")
            .disableStats()
            .setCreativeTab(CreativeTabs.tabBlock)
      );
      registerBlock(
         8, "flowing_water", new BlockDynamicLiquid(Material.water).setHardness(100.0F).setLightOpacity(3).setUnlocalizedName("water").disableStats()
      );
      registerBlock(9, "water", new BlockStaticLiquid(Material.water).setHardness(100.0F).setLightOpacity(3).setUnlocalizedName("water").disableStats());
      registerBlock(10, "flowing_lava", new BlockDynamicLiquid(Material.lava).setHardness(100.0F).setLightLevel(1.0F).setUnlocalizedName("lava").disableStats());
      registerBlock(11, "lava", new BlockStaticLiquid(Material.lava).setHardness(100.0F).setLightLevel(1.0F).setUnlocalizedName("lava").disableStats());
      registerBlock(12, "sand", new BlockSand().setHardness(0.5F).setStepSound(soundTypeSand).setUnlocalizedName("sand"));
      registerBlock(13, "gravel", new BlockGravel().setHardness(0.6F).setStepSound(soundTypeGravel).setUnlocalizedName("gravel"));
      registerBlock(14, "gold_ore", new BlockOre().setHardness(3.0F).setResistance(5.0F).setStepSound(soundTypePiston).setUnlocalizedName("oreGold"));
      registerBlock(15, "iron_ore", new BlockOre().setHardness(3.0F).setResistance(5.0F).setStepSound(soundTypePiston).setUnlocalizedName("oreIron"));
      registerBlock(16, "coal_ore", new BlockOre().setHardness(3.0F).setResistance(5.0F).setStepSound(soundTypePiston).setUnlocalizedName("oreCoal"));
      registerBlock(17, "log", new BlockOldLog().setUnlocalizedName("log"));
      registerBlock(18, "leaves", new BlockOldLeaf().setUnlocalizedName("leaves"));
      registerBlock(19, "sponge", new BlockSponge().setHardness(0.6F).setStepSound(soundTypeGrass).setUnlocalizedName("sponge"));
      registerBlock(20, "glass", new BlockGlass(Material.glass, false).setHardness(0.3F).setStepSound(soundTypeGlass).setUnlocalizedName("glass"));
      registerBlock(21, "lapis_ore", new BlockOre().setHardness(3.0F).setResistance(5.0F).setStepSound(soundTypePiston).setUnlocalizedName("oreLapis"));
      registerBlock(
         22,
         "lapis_block",
         new Block(Material.iron, MapColor.lapisColor)
            .setHardness(3.0F)
            .setResistance(5.0F)
            .setStepSound(soundTypePiston)
            .setUnlocalizedName("blockLapis")
            .setCreativeTab(CreativeTabs.tabBlock)
      );
      registerBlock(23, "dispenser", new BlockDispenser().setHardness(3.5F).setStepSound(soundTypePiston).setUnlocalizedName("dispenser"));
      Block var6 = new BlockSandStone().setStepSound(soundTypePiston).setHardness(0.8F).setUnlocalizedName("sandStone");
      registerBlock(24, "sandstone", var6);
      registerBlock(25, "noteblock", new BlockNote().setHardness(0.8F).setUnlocalizedName("musicBlock"));
      registerBlock(26, "bed", new BlockBed().setStepSound(soundTypeWood).setHardness(0.2F).setUnlocalizedName("bed").disableStats());
      registerBlock(27, "golden_rail", new BlockRailPowered().setHardness(0.7F).setStepSound(soundTypeMetal).setUnlocalizedName("goldenRail"));
      registerBlock(28, "detector_rail", new BlockRailDetector().setHardness(0.7F).setStepSound(soundTypeMetal).setUnlocalizedName("detectorRail"));
      registerBlock(29, "sticky_piston", new BlockPistonBase(true).setUnlocalizedName("pistonStickyBase"));
      registerBlock(30, "web", new BlockWeb().setLightOpacity(1).setHardness(4.0F).setUnlocalizedName("web"));
      registerBlock(31, "tallgrass", new BlockTallGrass().setHardness(0.0F).setStepSound(soundTypeGrass).setUnlocalizedName("tallgrass"));
      registerBlock(32, "deadbush", new BlockDeadBush().setHardness(0.0F).setStepSound(soundTypeGrass).setUnlocalizedName("deadbush"));
      registerBlock(33, "piston", new BlockPistonBase(false).setUnlocalizedName("pistonBase"));
      registerBlock(34, "piston_head", new BlockPistonExtension().setUnlocalizedName("pistonBase"));
      registerBlock(35, "wool", new BlockColored(Material.cloth).setHardness(0.8F).setStepSound(soundTypeCloth).setUnlocalizedName("cloth"));
      registerBlock(36, "piston_extension", new BlockPistonMoving());
      registerBlock(37, "yellow_flower", new BlockYellowFlower().setHardness(0.0F).setStepSound(soundTypeGrass).setUnlocalizedName("flower1"));
      registerBlock(38, "red_flower", new BlockRedFlower().setHardness(0.0F).setStepSound(soundTypeGrass).setUnlocalizedName("flower2"));
      Block var7 = new BlockMushroom().setHardness(0.0F).setStepSound(soundTypeGrass).setLightLevel(0.125F).setUnlocalizedName("mushroom");
      registerBlock(39, "brown_mushroom", var7);
      Block var8 = new BlockMushroom().setHardness(0.0F).setStepSound(soundTypeGrass).setUnlocalizedName("mushroom");
      registerBlock(40, "red_mushroom", var8);
      registerBlock(
         41,
         "gold_block",
         new Block(Material.iron, MapColor.goldColor)
            .setHardness(3.0F)
            .setResistance(10.0F)
            .setStepSound(soundTypeMetal)
            .setUnlocalizedName("blockGold")
            .setCreativeTab(CreativeTabs.tabBlock)
      );
      registerBlock(
         42,
         "iron_block",
         new Block(Material.iron, MapColor.ironColor)
            .setHardness(5.0F)
            .setResistance(10.0F)
            .setStepSound(soundTypeMetal)
            .setUnlocalizedName("blockIron")
            .setCreativeTab(CreativeTabs.tabBlock)
      );
      registerBlock(
         43,
         "double_stone_slab",
         new BlockDoubleStoneSlab().setHardness(2.0F).setResistance(10.0F).setStepSound(soundTypePiston).setUnlocalizedName("stoneSlab")
      );
      registerBlock(
         44, "stone_slab", new BlockHalfStoneSlab().setHardness(2.0F).setResistance(10.0F).setStepSound(soundTypePiston).setUnlocalizedName("stoneSlab")
      );
      Block var9 = new Block(Material.rock, MapColor.redColor)
         .setHardness(2.0F)
         .setResistance(10.0F)
         .setStepSound(soundTypePiston)
         .setUnlocalizedName("brick")
         .setCreativeTab(CreativeTabs.tabBlock);
      registerBlock(45, "brick_block", var9);
      registerBlock(46, "tnt", new BlockTNT().setHardness(0.0F).setStepSound(soundTypeGrass).setUnlocalizedName("tnt"));
      registerBlock(47, "bookshelf", new BlockBookshelf().setHardness(1.5F).setStepSound(soundTypeWood).setUnlocalizedName("bookshelf"));
      registerBlock(
         48,
         "mossy_cobblestone",
         new Block(Material.rock)
            .setHardness(2.0F)
            .setResistance(10.0F)
            .setStepSound(soundTypePiston)
            .setUnlocalizedName("stoneMoss")
            .setCreativeTab(CreativeTabs.tabBlock)
      );
      registerBlock(49, "obsidian", new BlockObsidian().setHardness(50.0F).setResistance(2000.0F).setStepSound(soundTypePiston).setUnlocalizedName("obsidian"));
      registerBlock(50, "torch", new BlockTorch().setHardness(0.0F).setLightLevel(0.9375F).setStepSound(soundTypeWood).setUnlocalizedName("torch"));
      registerBlock(51, "fire", new BlockFire().setHardness(0.0F).setLightLevel(1.0F).setStepSound(soundTypeCloth).setUnlocalizedName("fire").disableStats());
      registerBlock(52, "mob_spawner", new BlockMobSpawner().setHardness(5.0F).setStepSound(soundTypeMetal).setUnlocalizedName("mobSpawner").disableStats());
      registerBlock(
         53, "oak_stairs", new BlockStairs(var5.getDefaultState().withProperty(BlockPlanks.VARIANT, BlockPlanks.EnumType.OAK)).setUnlocalizedName("stairsWood")
      );
      registerBlock(54, "chest", new BlockChest(0).setHardness(2.5F).setStepSound(soundTypeWood).setUnlocalizedName("chest"));
      registerBlock(
         55, "redstone_wire", new BlockRedstoneWire().setHardness(0.0F).setStepSound(soundTypeStone).setUnlocalizedName("redstoneDust").disableStats()
      );
      registerBlock(56, "diamond_ore", new BlockOre().setHardness(3.0F).setResistance(5.0F).setStepSound(soundTypePiston).setUnlocalizedName("oreDiamond"));
      registerBlock(
         57,
         "diamond_block",
         new Block(Material.iron, MapColor.diamondColor)
            .setHardness(5.0F)
            .setResistance(10.0F)
            .setStepSound(soundTypeMetal)
            .setUnlocalizedName("blockDiamond")
            .setCreativeTab(CreativeTabs.tabBlock)
      );
      registerBlock(58, "crafting_table", new BlockWorkbench().setHardness(2.5F).setStepSound(soundTypeWood).setUnlocalizedName("workbench"));
      registerBlock(59, "wheat", new BlockCrops().setUnlocalizedName("crops"));
      Block var10 = new BlockFarmland().setHardness(0.6F).setStepSound(soundTypeGravel).setUnlocalizedName("farmland");
      registerBlock(60, "farmland", var10);
      registerBlock(
         61,
         "furnace",
         new BlockFurnace(false).setHardness(3.5F).setStepSound(soundTypePiston).setUnlocalizedName("furnace").setCreativeTab(CreativeTabs.tabDecorations)
      );
      registerBlock(
         62, "lit_furnace", new BlockFurnace(true).setHardness(3.5F).setStepSound(soundTypePiston).setLightLevel(0.875F).setUnlocalizedName("furnace")
      );
      registerBlock(63, "standing_sign", new BlockStandingSign().setHardness(1.0F).setStepSound(soundTypeWood).setUnlocalizedName("sign").disableStats());
      registerBlock(64, "wooden_door", new BlockDoor(Material.wood).setHardness(3.0F).setStepSound(soundTypeWood).setUnlocalizedName("doorOak").disableStats());
      registerBlock(65, "ladder", new BlockLadder().setHardness(0.4F).setStepSound(soundTypeLadder).setUnlocalizedName("ladder"));
      registerBlock(66, "rail", new BlockRail().setHardness(0.7F).setStepSound(soundTypeMetal).setUnlocalizedName("rail"));
      registerBlock(67, "stone_stairs", new BlockStairs(var4.getDefaultState()).setUnlocalizedName("stairsStone"));
      registerBlock(68, "wall_sign", new BlockWallSign().setHardness(1.0F).setStepSound(soundTypeWood).setUnlocalizedName("sign").disableStats());
      registerBlock(69, "lever", new BlockLever().setHardness(0.5F).setStepSound(soundTypeWood).setUnlocalizedName("lever"));
      registerBlock(
         70,
         "stone_pressure_plate",
         new BlockPressurePlate(Material.rock, BlockPressurePlate.Sensitivity.MOBS)
            .setHardness(0.5F)
            .setStepSound(soundTypePiston)
            .setUnlocalizedName("pressurePlateStone")
      );
      registerBlock(71, "iron_door", new BlockDoor(Material.iron).setHardness(5.0F).setStepSound(soundTypeMetal).setUnlocalizedName("doorIron").disableStats());
      registerBlock(
         72,
         "wooden_pressure_plate",
         new BlockPressurePlate(Material.wood, BlockPressurePlate.Sensitivity.EVERYTHING)
            .setHardness(0.5F)
            .setStepSound(soundTypeWood)
            .setUnlocalizedName("pressurePlateWood")
      );
      registerBlock(
         73,
         "redstone_ore",
         new BlockRedstoneOre(false)
            .setHardness(3.0F)
            .setResistance(5.0F)
            .setStepSound(soundTypePiston)
            .setUnlocalizedName("oreRedstone")
            .setCreativeTab(CreativeTabs.tabBlock)
      );
      registerBlock(
         74,
         "lit_redstone_ore",
         new BlockRedstoneOre(true).setLightLevel(0.625F).setHardness(3.0F).setResistance(5.0F).setStepSound(soundTypePiston).setUnlocalizedName("oreRedstone")
      );
      registerBlock(75, "unlit_redstone_torch", new BlockRedstoneTorch(false).setHardness(0.0F).setStepSound(soundTypeWood).setUnlocalizedName("notGate"));
      registerBlock(
         76,
         "redstone_torch",
         new BlockRedstoneTorch(true)
            .setHardness(0.0F)
            .setLightLevel(0.5F)
            .setStepSound(soundTypeWood)
            .setUnlocalizedName("notGate")
            .setCreativeTab(CreativeTabs.tabRedstone)
      );
      registerBlock(77, "stone_button", new BlockButtonStone().setHardness(0.5F).setStepSound(soundTypePiston).setUnlocalizedName("button"));
      registerBlock(78, "snow_layer", new BlockSnow().setHardness(0.1F).setStepSound(soundTypeSnow).setUnlocalizedName("snow").setLightOpacity(0));
      registerBlock(79, "ice", new BlockIce().setHardness(0.5F).setLightOpacity(3).setStepSound(soundTypeGlass).setUnlocalizedName("ice"));
      registerBlock(80, "snow", new BlockSnowBlock().setHardness(0.2F).setStepSound(soundTypeSnow).setUnlocalizedName("snow"));
      registerBlock(81, "cactus", new BlockCactus().setHardness(0.4F).setStepSound(soundTypeCloth).setUnlocalizedName("cactus"));
      registerBlock(82, "clay", new BlockClay().setHardness(0.6F).setStepSound(soundTypeGravel).setUnlocalizedName("clay"));
      registerBlock(83, "reeds", new BlockReed().setHardness(0.0F).setStepSound(soundTypeGrass).setUnlocalizedName("reeds").disableStats());
      registerBlock(84, "jukebox", new BlockJukebox().setHardness(2.0F).setResistance(10.0F).setStepSound(soundTypePiston).setUnlocalizedName("jukebox"));
      registerBlock(
         85,
         "fence",
         new BlockFence(Material.wood, BlockPlanks.EnumType.OAK.getMapColor())
            .setHardness(2.0F)
            .setResistance(5.0F)
            .setStepSound(soundTypeWood)
            .setUnlocalizedName("fence")
      );
      Block var11 = new BlockPumpkin().setHardness(1.0F).setStepSound(soundTypeWood).setUnlocalizedName("pumpkin");
      registerBlock(86, "pumpkin", var11);
      registerBlock(87, "netherrack", new BlockNetherrack().setHardness(0.4F).setStepSound(soundTypePiston).setUnlocalizedName("hellrock"));
      registerBlock(88, "soul_sand", new BlockSoulSand().setHardness(0.5F).setStepSound(soundTypeSand).setUnlocalizedName("hellsand"));
      registerBlock(
         89, "glowstone", new BlockGlowstone(Material.glass).setHardness(0.3F).setStepSound(soundTypeGlass).setLightLevel(1.0F).setUnlocalizedName("lightgem")
      );
      registerBlock(90, "portal", new BlockPortal().setHardness(-1.0F).setStepSound(soundTypeGlass).setLightLevel(0.75F).setUnlocalizedName("portal"));
      registerBlock(91, "lit_pumpkin", new BlockPumpkin().setHardness(1.0F).setStepSound(soundTypeWood).setLightLevel(1.0F).setUnlocalizedName("litpumpkin"));
      registerBlock(92, "cake", new BlockCake().setHardness(0.5F).setStepSound(soundTypeCloth).setUnlocalizedName("cake").disableStats());
      registerBlock(
         93, "unpowered_repeater", new BlockRedstoneRepeater(false).setHardness(0.0F).setStepSound(soundTypeWood).setUnlocalizedName("diode").disableStats()
      );
      registerBlock(
         94, "powered_repeater", new BlockRedstoneRepeater(true).setHardness(0.0F).setStepSound(soundTypeWood).setUnlocalizedName("diode").disableStats()
      );
      registerBlock(
         95, "stained_glass", new BlockStainedGlass(Material.glass).setHardness(0.3F).setStepSound(soundTypeGlass).setUnlocalizedName("stainedGlass")
      );
      registerBlock(
         96, "trapdoor", new BlockTrapDoor(Material.wood).setHardness(3.0F).setStepSound(soundTypeWood).setUnlocalizedName("trapdoor").disableStats()
      );
      registerBlock(97, "monster_egg", new BlockSilverfish().setHardness(0.75F).setUnlocalizedName("monsterStoneEgg"));
      Block var12 = new BlockStoneBrick().setHardness(1.5F).setResistance(10.0F).setStepSound(soundTypePiston).setUnlocalizedName("stonebricksmooth");
      registerBlock(98, "stonebrick", var12);
      registerBlock(
         99,
         "brown_mushroom_block",
         new BlockHugeMushroom(Material.wood, MapColor.dirtColor, var7).setHardness(0.2F).setStepSound(soundTypeWood).setUnlocalizedName("mushroom")
      );
      registerBlock(
         100,
         "red_mushroom_block",
         new BlockHugeMushroom(Material.wood, MapColor.redColor, var8).setHardness(0.2F).setStepSound(soundTypeWood).setUnlocalizedName("mushroom")
      );
      registerBlock(
         101,
         "iron_bars",
         new BlockPane(Material.iron, true).setHardness(5.0F).setResistance(10.0F).setStepSound(soundTypeMetal).setUnlocalizedName("fenceIron")
      );
      registerBlock(102, "glass_pane", new BlockPane(Material.glass, false).setHardness(0.3F).setStepSound(soundTypeGlass).setUnlocalizedName("thinGlass"));
      Block var13 = new BlockMelon().setHardness(1.0F).setStepSound(soundTypeWood).setUnlocalizedName("melon");
      registerBlock(103, "melon_block", var13);
      registerBlock(104, "pumpkin_stem", new BlockStem(var11).setHardness(0.0F).setStepSound(soundTypeWood).setUnlocalizedName("pumpkinStem"));
      registerBlock(105, "melon_stem", new BlockStem(var13).setHardness(0.0F).setStepSound(soundTypeWood).setUnlocalizedName("pumpkinStem"));
      registerBlock(106, "vine", new BlockVine().setHardness(0.2F).setStepSound(soundTypeGrass).setUnlocalizedName("vine"));
      registerBlock(
         107,
         "fence_gate",
         new BlockFenceGate(BlockPlanks.EnumType.OAK).setHardness(2.0F).setResistance(5.0F).setStepSound(soundTypeWood).setUnlocalizedName("fenceGate")
      );
      registerBlock(108, "brick_stairs", new BlockStairs(var9.getDefaultState()).setUnlocalizedName("stairsBrick"));
      registerBlock(
         109,
         "stone_brick_stairs",
         new BlockStairs(var12.getDefaultState().withProperty(BlockStoneBrick.VARIANT, BlockStoneBrick.EnumType.DEFAULT))
            .setUnlocalizedName("stairsStoneBrickSmooth")
      );
      registerBlock(110, "mycelium", new BlockMycelium().setHardness(0.6F).setStepSound(soundTypeGrass).setUnlocalizedName("mycel"));
      registerBlock(111, "waterlily", new BlockLilyPad().setHardness(0.0F).setStepSound(soundTypeGrass).setUnlocalizedName("waterlily"));
      Block var14 = new BlockNetherBrick()
         .setHardness(2.0F)
         .setResistance(10.0F)
         .setStepSound(soundTypePiston)
         .setUnlocalizedName("netherBrick")
         .setCreativeTab(CreativeTabs.tabBlock);
      registerBlock(112, "nether_brick", var14);
      registerBlock(
         113,
         "nether_brick_fence",
         new BlockFence(Material.rock, MapColor.netherrackColor)
            .setHardness(2.0F)
            .setResistance(10.0F)
            .setStepSound(soundTypePiston)
            .setUnlocalizedName("netherFence")
      );
      registerBlock(114, "nether_brick_stairs", new BlockStairs(var14.getDefaultState()).setUnlocalizedName("stairsNetherBrick"));
      registerBlock(115, "nether_wart", new BlockNetherWart().setUnlocalizedName("netherStalk"));
      registerBlock(116, "enchanting_table", new BlockEnchantmentTable().setHardness(5.0F).setResistance(2000.0F).setUnlocalizedName("enchantmentTable"));
      registerBlock(117, "brewing_stand", new BlockBrewingStand().setHardness(0.5F).setLightLevel(0.125F).setUnlocalizedName("brewingStand"));
      registerBlock(118, "cauldron", new BlockCauldron().setHardness(2.0F).setUnlocalizedName("cauldron"));
      registerBlock(119, "end_portal", new BlockEndPortal(Material.portal).setHardness(-1.0F).setResistance(6000000.0F));
      registerBlock(
         120,
         "end_portal_frame",
         new BlockEndPortalFrame()
            .setStepSound(soundTypeGlass)
            .setLightLevel(0.125F)
            .setHardness(-1.0F)
            .setUnlocalizedName("endPortalFrame")
            .setResistance(6000000.0F)
            .setCreativeTab(CreativeTabs.tabDecorations)
      );
      registerBlock(
         121,
         "end_stone",
         new Block(Material.rock, MapColor.sandColor)
            .setHardness(3.0F)
            .setResistance(15.0F)
            .setStepSound(soundTypePiston)
            .setUnlocalizedName("whiteStone")
            .setCreativeTab(CreativeTabs.tabBlock)
      );
      registerBlock(
         122,
         "dragon_egg",
         new BlockDragonEgg().setHardness(3.0F).setResistance(15.0F).setStepSound(soundTypePiston).setLightLevel(0.125F).setUnlocalizedName("dragonEgg")
      );
      registerBlock(
         123,
         "redstone_lamp",
         new BlockRedstoneLight(false)
            .setHardness(0.3F)
            .setStepSound(soundTypeGlass)
            .setUnlocalizedName("redstoneLight")
            .setCreativeTab(CreativeTabs.tabRedstone)
      );
      registerBlock(124, "lit_redstone_lamp", new BlockRedstoneLight(true).setHardness(0.3F).setStepSound(soundTypeGlass).setUnlocalizedName("redstoneLight"));
      registerBlock(
         125, "double_wooden_slab", new BlockDoubleWoodSlab().setHardness(2.0F).setResistance(5.0F).setStepSound(soundTypeWood).setUnlocalizedName("woodSlab")
      );
      registerBlock(
         126, "wooden_slab", new BlockHalfWoodSlab().setHardness(2.0F).setResistance(5.0F).setStepSound(soundTypeWood).setUnlocalizedName("woodSlab")
      );
      registerBlock(127, "cocoa", new BlockCocoa().setHardness(0.2F).setResistance(5.0F).setStepSound(soundTypeWood).setUnlocalizedName("cocoa"));
      registerBlock(
         128,
         "sandstone_stairs",
         new BlockStairs(var6.getDefaultState().withProperty(BlockSandStone.TYPE, BlockSandStone.EnumType.SMOOTH)).setUnlocalizedName("stairsSandStone")
      );
      registerBlock(129, "emerald_ore", new BlockOre().setHardness(3.0F).setResistance(5.0F).setStepSound(soundTypePiston).setUnlocalizedName("oreEmerald"));
      registerBlock(
         130,
         "ender_chest",
         new BlockEnderChest().setHardness(22.5F).setResistance(1000.0F).setStepSound(soundTypePiston).setUnlocalizedName("enderChest").setLightLevel(0.5F)
      );
      registerBlock(131, "tripwire_hook", new BlockTripWireHook().setUnlocalizedName("tripWireSource"));
      registerBlock(132, "tripwire", new BlockTripWire().setUnlocalizedName("tripWire"));
      registerBlock(
         133,
         "emerald_block",
         new Block(Material.iron, MapColor.emeraldColor)
            .setHardness(5.0F)
            .setResistance(10.0F)
            .setStepSound(soundTypeMetal)
            .setUnlocalizedName("blockEmerald")
            .setCreativeTab(CreativeTabs.tabBlock)
      );
      registerBlock(
         134,
         "spruce_stairs",
         new BlockStairs(var5.getDefaultState().withProperty(BlockPlanks.VARIANT, BlockPlanks.EnumType.SPRUCE)).setUnlocalizedName("stairsWoodSpruce")
      );
      registerBlock(
         135,
         "birch_stairs",
         new BlockStairs(var5.getDefaultState().withProperty(BlockPlanks.VARIANT, BlockPlanks.EnumType.BIRCH)).setUnlocalizedName("stairsWoodBirch")
      );
      registerBlock(
         136,
         "jungle_stairs",
         new BlockStairs(var5.getDefaultState().withProperty(BlockPlanks.VARIANT, BlockPlanks.EnumType.JUNGLE)).setUnlocalizedName("stairsWoodJungle")
      );
      registerBlock(137, "command_block", new BlockCommandBlock().setBlockUnbreakable().setResistance(6000000.0F).setUnlocalizedName("commandBlock"));
      registerBlock(138, "beacon", new BlockBeacon().setUnlocalizedName("beacon").setLightLevel(1.0F));
      registerBlock(139, "cobblestone_wall", new BlockWall(var4).setUnlocalizedName("cobbleWall"));
      registerBlock(140, "flower_pot", new BlockFlowerPot().setHardness(0.0F).setStepSound(soundTypeStone).setUnlocalizedName("flowerPot"));
      registerBlock(141, "carrots", new BlockCarrot().setUnlocalizedName("carrots"));
      registerBlock(142, "potatoes", new BlockPotato().setUnlocalizedName("potatoes"));
      registerBlock(143, "wooden_button", new BlockButtonWood().setHardness(0.5F).setStepSound(soundTypeWood).setUnlocalizedName("button"));
      registerBlock(144, "skull", new BlockSkull().setHardness(1.0F).setStepSound(soundTypePiston).setUnlocalizedName("skull"));
      registerBlock(145, "anvil", new BlockAnvil().setHardness(5.0F).setStepSound(soundTypeAnvil).setResistance(2000.0F).setUnlocalizedName("anvil"));
      registerBlock(146, "trapped_chest", new BlockChest(1).setHardness(2.5F).setStepSound(soundTypeWood).setUnlocalizedName("chestTrap"));
      registerBlock(
         147,
         "light_weighted_pressure_plate",
         new BlockPressurePlateWeighted(Material.iron, 15, MapColor.goldColor)
            .setHardness(0.5F)
            .setStepSound(soundTypeWood)
            .setUnlocalizedName("weightedPlate_light")
      );
      registerBlock(
         148,
         "heavy_weighted_pressure_plate",
         new BlockPressurePlateWeighted(Material.iron, 150).setHardness(0.5F).setStepSound(soundTypeWood).setUnlocalizedName("weightedPlate_heavy")
      );
      registerBlock(
         149,
         "unpowered_comparator",
         new BlockRedstoneComparator(false).setHardness(0.0F).setStepSound(soundTypeWood).setUnlocalizedName("comparator").disableStats()
      );
      registerBlock(
         150,
         "powered_comparator",
         new BlockRedstoneComparator(true).setHardness(0.0F).setLightLevel(0.625F).setStepSound(soundTypeWood).setUnlocalizedName("comparator").disableStats()
      );
      registerBlock(151, "daylight_detector", new BlockDaylightDetector(false));
      registerBlock(
         152,
         "redstone_block",
         new BlockCompressedPowered(Material.iron, MapColor.tntColor)
            .setHardness(5.0F)
            .setResistance(10.0F)
            .setStepSound(soundTypeMetal)
            .setUnlocalizedName("blockRedstone")
            .setCreativeTab(CreativeTabs.tabRedstone)
      );
      registerBlock(
         153,
         "quartz_ore",
         new BlockOre(MapColor.netherrackColor).setHardness(3.0F).setResistance(5.0F).setStepSound(soundTypePiston).setUnlocalizedName("netherquartz")
      );
      registerBlock(154, "hopper", new BlockHopper().setHardness(3.0F).setResistance(8.0F).setStepSound(soundTypeMetal).setUnlocalizedName("hopper"));
      Block var15 = new BlockQuartz().setStepSound(soundTypePiston).setHardness(0.8F).setUnlocalizedName("quartzBlock");
      registerBlock(155, "quartz_block", var15);
      registerBlock(
         156,
         "quartz_stairs",
         new BlockStairs(var15.getDefaultState().withProperty(BlockQuartz.VARIANT, BlockQuartz.EnumType.DEFAULT)).setUnlocalizedName("stairsQuartz")
      );
      registerBlock(157, "activator_rail", new BlockRailPowered().setHardness(0.7F).setStepSound(soundTypeMetal).setUnlocalizedName("activatorRail"));
      registerBlock(158, "dropper", new BlockDropper().setHardness(3.5F).setStepSound(soundTypePiston).setUnlocalizedName("dropper"));
      registerBlock(
         159,
         "stained_hardened_clay",
         new BlockColored(Material.rock).setHardness(1.25F).setResistance(7.0F).setStepSound(soundTypePiston).setUnlocalizedName("clayHardenedStained")
      );
      registerBlock(
         160, "stained_glass_pane", new BlockStainedGlassPane().setHardness(0.3F).setStepSound(soundTypeGlass).setUnlocalizedName("thinStainedGlass")
      );
      registerBlock(161, "leaves2", new BlockNewLeaf().setUnlocalizedName("leaves"));
      registerBlock(162, "log2", new BlockNewLog().setUnlocalizedName("log"));
      registerBlock(
         163,
         "acacia_stairs",
         new BlockStairs(var5.getDefaultState().withProperty(BlockPlanks.VARIANT, BlockPlanks.EnumType.ACACIA)).setUnlocalizedName("stairsWoodAcacia")
      );
      registerBlock(
         164,
         "dark_oak_stairs",
         new BlockStairs(var5.getDefaultState().withProperty(BlockPlanks.VARIANT, BlockPlanks.EnumType.DARK_OAK)).setUnlocalizedName("stairsWoodDarkOak")
      );
      registerBlock(165, "slime", new BlockSlime().setUnlocalizedName("slime").setStepSound(SLIME_SOUND));
      registerBlock(166, "barrier", new BlockBarrier().setUnlocalizedName("barrier"));
      registerBlock(
         167,
         "iron_trapdoor",
         new BlockTrapDoor(Material.iron).setHardness(5.0F).setStepSound(soundTypeMetal).setUnlocalizedName("ironTrapdoor").disableStats()
      );
      registerBlock(
         168, "prismarine", new BlockPrismarine().setHardness(1.5F).setResistance(10.0F).setStepSound(soundTypePiston).setUnlocalizedName("prismarine")
      );
      registerBlock(
         169,
         "sea_lantern",
         new BlockSeaLantern(Material.glass).setHardness(0.3F).setStepSound(soundTypeGlass).setLightLevel(1.0F).setUnlocalizedName("seaLantern")
      );
      registerBlock(
         170, "hay_block", new BlockHay().setHardness(0.5F).setStepSound(soundTypeGrass).setUnlocalizedName("hayBlock").setCreativeTab(CreativeTabs.tabBlock)
      );
      registerBlock(171, "carpet", new BlockCarpet().setHardness(0.1F).setStepSound(soundTypeCloth).setUnlocalizedName("woolCarpet").setLightOpacity(0));
      registerBlock(
         172, "hardened_clay", new BlockHardenedClay().setHardness(1.25F).setResistance(7.0F).setStepSound(soundTypePiston).setUnlocalizedName("clayHardened")
      );
      registerBlock(
         173,
         "coal_block",
         new Block(Material.rock, MapColor.blackColor)
            .setHardness(5.0F)
            .setResistance(10.0F)
            .setStepSound(soundTypePiston)
            .setUnlocalizedName("blockCoal")
            .setCreativeTab(CreativeTabs.tabBlock)
      );
      registerBlock(174, "packed_ice", new BlockPackedIce().setHardness(0.5F).setStepSound(soundTypeGlass).setUnlocalizedName("icePacked"));
      registerBlock(175, "double_plant", new BlockDoublePlant());
      registerBlock(
         176,
         "standing_banner",
         new BlockBanner.BlockBannerStanding().setHardness(1.0F).setStepSound(soundTypeWood).setUnlocalizedName("banner").disableStats()
      );
      registerBlock(
         177, "wall_banner", new BlockBanner.BlockBannerHanging().setHardness(1.0F).setStepSound(soundTypeWood).setUnlocalizedName("banner").disableStats()
      );
      registerBlock(178, "daylight_detector_inverted", new BlockDaylightDetector(true));
      Block var16 = new BlockRedSandstone().setStepSound(soundTypePiston).setHardness(0.8F).setUnlocalizedName("redSandStone");
      registerBlock(179, "red_sandstone", var16);
      registerBlock(
         180,
         "red_sandstone_stairs",
         new BlockStairs(var16.getDefaultState().withProperty(BlockRedSandstone.TYPE, BlockRedSandstone.EnumType.SMOOTH))
            .setUnlocalizedName("stairsRedSandStone")
      );
      registerBlock(
         181,
         "double_stone_slab2",
         new BlockDoubleStoneSlabNew().setHardness(2.0F).setResistance(10.0F).setStepSound(soundTypePiston).setUnlocalizedName("stoneSlab2")
      );
      registerBlock(
         182, "stone_slab2", new BlockHalfStoneSlabNew().setHardness(2.0F).setResistance(10.0F).setStepSound(soundTypePiston).setUnlocalizedName("stoneSlab2")
      );
      registerBlock(
         183,
         "spruce_fence_gate",
         new BlockFenceGate(BlockPlanks.EnumType.SPRUCE)
            .setHardness(2.0F)
            .setResistance(5.0F)
            .setStepSound(soundTypeWood)
            .setUnlocalizedName("spruceFenceGate")
      );
      registerBlock(
         184,
         "birch_fence_gate",
         new BlockFenceGate(BlockPlanks.EnumType.BIRCH).setHardness(2.0F).setResistance(5.0F).setStepSound(soundTypeWood).setUnlocalizedName("birchFenceGate")
      );
      registerBlock(
         185,
         "jungle_fence_gate",
         new BlockFenceGate(BlockPlanks.EnumType.JUNGLE)
            .setHardness(2.0F)
            .setResistance(5.0F)
            .setStepSound(soundTypeWood)
            .setUnlocalizedName("jungleFenceGate")
      );
      registerBlock(
         186,
         "dark_oak_fence_gate",
         new BlockFenceGate(BlockPlanks.EnumType.DARK_OAK)
            .setHardness(2.0F)
            .setResistance(5.0F)
            .setStepSound(soundTypeWood)
            .setUnlocalizedName("darkOakFenceGate")
      );
      registerBlock(
         187,
         "acacia_fence_gate",
         new BlockFenceGate(BlockPlanks.EnumType.ACACIA)
            .setHardness(2.0F)
            .setResistance(5.0F)
            .setStepSound(soundTypeWood)
            .setUnlocalizedName("acaciaFenceGate")
      );
      registerBlock(
         188,
         "spruce_fence",
         new BlockFence(Material.wood, BlockPlanks.EnumType.SPRUCE.getMapColor())
            .setHardness(2.0F)
            .setResistance(5.0F)
            .setStepSound(soundTypeWood)
            .setUnlocalizedName("spruceFence")
      );
      registerBlock(
         189,
         "birch_fence",
         new BlockFence(Material.wood, BlockPlanks.EnumType.BIRCH.getMapColor())
            .setHardness(2.0F)
            .setResistance(5.0F)
            .setStepSound(soundTypeWood)
            .setUnlocalizedName("birchFence")
      );
      registerBlock(
         190,
         "jungle_fence",
         new BlockFence(Material.wood, BlockPlanks.EnumType.JUNGLE.getMapColor())
            .setHardness(2.0F)
            .setResistance(5.0F)
            .setStepSound(soundTypeWood)
            .setUnlocalizedName("jungleFence")
      );
      registerBlock(
         191,
         "dark_oak_fence",
         new BlockFence(Material.wood, BlockPlanks.EnumType.DARK_OAK.getMapColor())
            .setHardness(2.0F)
            .setResistance(5.0F)
            .setStepSound(soundTypeWood)
            .setUnlocalizedName("darkOakFence")
      );
      registerBlock(
         192,
         "acacia_fence",
         new BlockFence(Material.wood, BlockPlanks.EnumType.ACACIA.getMapColor())
            .setHardness(2.0F)
            .setResistance(5.0F)
            .setStepSound(soundTypeWood)
            .setUnlocalizedName("acaciaFence")
      );
      registerBlock(
         193, "spruce_door", new BlockDoor(Material.wood).setHardness(3.0F).setStepSound(soundTypeWood).setUnlocalizedName("doorSpruce").disableStats()
      );
      registerBlock(
         194, "birch_door", new BlockDoor(Material.wood).setHardness(3.0F).setStepSound(soundTypeWood).setUnlocalizedName("doorBirch").disableStats()
      );
      registerBlock(
         195, "jungle_door", new BlockDoor(Material.wood).setHardness(3.0F).setStepSound(soundTypeWood).setUnlocalizedName("doorJungle").disableStats()
      );
      registerBlock(
         196, "acacia_door", new BlockDoor(Material.wood).setHardness(3.0F).setStepSound(soundTypeWood).setUnlocalizedName("doorAcacia").disableStats()
      );
      registerBlock(
         197, "dark_oak_door", new BlockDoor(Material.wood).setHardness(3.0F).setStepSound(soundTypeWood).setUnlocalizedName("doorDarkOak").disableStats()
      );
      blockRegistry.validateKey();

      for (Block var18 : blockRegistry) {
         if (var18.blockMaterial == Material.air) {
            var18.useNeighborBrightness = false;
         } else {
            boolean var19 = false;
            boolean var20 = var18 instanceof BlockStairs;
            boolean var21 = var18 instanceof BlockSlab;
            boolean var22 = var18 == var10;
            boolean var23 = var18.translucent;
            boolean var24 = var18.lightOpacity == 0;
            if (var20 || var21 || var22 || var23 || var24) {
               var19 = true;
            }

            var18.useNeighborBrightness = var19;
         }
      }

      for (Block var26 : blockRegistry) {
         for (IBlockState var28 : var26.getBlockState().getValidStates()) {
            int var29 = blockRegistry.getIDForObject(var26) << 4 | var26.getMetaFromState(var28);
            BLOCK_STATE_IDS.put(var28, var29);
         }
      }
   }

   private static void registerBlock(int var0, ResourceLocation var1, Block var2) {
      blockRegistry.register(var0, var1, var2);
   }

   private static void registerBlock(int var0, String var1, Block var2) {
      registerBlock(var0, new ResourceLocation(var1), var2);
   }

   public static class CollisionBoxEvent extends CancellableEvent {
      private final Block block;
      public AxisAlignedBB boundingBox;
      private final double x;
      private final double y;
      private final double z;

      public CollisionBoxEvent(AxisAlignedBB var1, Block var2, double var3, double var5, double var7) {
         this.block = var2;
         this.boundingBox = var1;
         this.x = var3;
         this.y = var5;
         this.z = var7;
      }

      public AxisAlignedBB getBoundingBox() {
         return this.boundingBox;
      }

      public void setBoundingBox(AxisAlignedBB var1) {
         this.boundingBox = var1;
      }

      public Block getBlock() {
         return this.block;
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
   }

   public enum EnumOffsetType {
      NONE,
      XZ,
      XYZ;
   }

   public static class SoundType {
      public final String soundName;
      public final float volume;
      public final float frequency;
      private static final long b = 26835569858520L;

      public SoundType(String var1, float var2, float var3) {
         this.soundName = var1;
         this.volume = var2;
         this.frequency = var3;
      }

      public float getVolume() {
         return this.volume;
      }

      public float getFrequency() {
         return this.frequency;
      }

      public String getBreakSound() {
         return "dig." + this.soundName;
      }

      public String getStepSound() {
         return "step." + this.soundName;
      }

      public String getPlaceSound() {
         return this.getBreakSound();
      }
   }
}
