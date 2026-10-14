package net.minecraft.entity.monster;

import com.google.common.base.Predicate;
import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityEnderman extends EntityMob {
   private static final UUID attackingSpeedBoostModifierUUID = UUID.fromString("020E0DFB-87AE-4653-9556-831010E291A0");
   private static final AttributeModifier attackingSpeedBoostModifier = new AttributeModifier(
         attackingSpeedBoostModifierUUID, "Attacking speed boost", 0.15F, 0
      )
      .setSaved(false);
   private static final Set<Block> carriableBlocks = Sets.newIdentityHashSet();
   private boolean isAggressive;
   private static final long ab = 45949421866514L;

   public EntityEnderman(World worldIn) {
      super(worldIn);
      this.setSize(0.6F, 2.9F);
      this.stepHeight = 1.0F;
      this.tasks.addTask(0, new EntityAISwimming(this));
      this.tasks.addTask(2, new EntityAIAttackOnCollide(this, 1.0, false));
      this.tasks.addTask(7, new EntityAIWander(this, 1.0));
      this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
      this.tasks.addTask(8, new EntityAILookIdle(this));
      this.tasks.addTask(10, new EntityEnderman.AIPlaceBlock(this));
      this.tasks.addTask(11, new EntityEnderman.AITakeBlock(this));
      this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
      this.targetTasks.addTask(2, new EntityEnderman.AIFindPlayer(this));
      this.targetTasks.addTask(3, new EntityAINearestAttackableTarget<>(this, EntityEndermite.class, 10, true, false, new Predicate<EntityEndermite>() {
         public boolean apply(EntityEndermite entityEndermite) {
            return entityEndermite.isSpawnedByPlayer();
         }
      }));
   }

   @Override
   public void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(40.0);
      this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.3F);
      this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(7.0);
      this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(64.0);
   }

   @Override
   public void entityInit() {
      super.entityInit();
      this.dataWatcher.addObject(16, new Short((short)0));
      this.dataWatcher.addObject(17, new Byte((byte)0));
      this.dataWatcher.addObject(18, new Byte((byte)0));
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound tagCompound) {
      super.writeEntityToNBT(tagCompound);
      IBlockState iBlockState = this.getHeldBlockState();
      tagCompound.setShort("carried", (short)Block.getIdFromBlock(iBlockState.getBlock()));
      tagCompound.setShort("carriedData", (short)iBlockState.getBlock().getMetaFromState(iBlockState));
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound tagCompund) {
      super.readEntityFromNBT(tagCompund);
      IBlockState iBlockState;
      if (tagCompund.hasKey("carried", 8)) {
         iBlockState = Block.getBlockFromName(tagCompund.getString("carried")).getStateFromMeta(tagCompund.getShort("carriedData") & 65535);
      } else {
         iBlockState = Block.getBlockById(tagCompund.getShort("carried")).getStateFromMeta(tagCompund.getShort("carriedData") & 65535);
      }

      this.setHeldBlockState(iBlockState);
   }

   private boolean shouldAttackPlayer(EntityPlayer player) {
      ItemStack itemStack = player.inventory.armorInventory[3];
      if (itemStack != null && itemStack.getItem() == Item.getItemFromBlock(Blocks.pumpkin)) {
         return false;
      }

      Vec3 vec3 = player.getLook(1.0F).normalize();
      Vec3 vec32 = new Vec3(
         this.posX - player.posX, this.getEntityBoundingBox().minY + this.height / 2.0F - (player.posY + player.getEyeHeight()), this.posZ - player.posZ
      );
      double d = vec32.lengthVector();
      vec32 = vec32.normalize();
      double e = vec3.dotProduct(vec32);
      return e > 1.0 - 0.025 / d ? player.C(this) : false;
   }

   @Override
   public float getEyeHeight() {
      return 2.55F;
   }

   @Override
   public void onLivingUpdate() {
      if (this.worldObj.isRemote) {
         for (int i = 0; i < 2; i++) {
            this.worldObj
               .spawnParticle(
                  EnumParticleTypes.PORTAL,
                  this.posX + (this.rand.nextDouble() - 0.5) * this.width,
                  this.posY + this.rand.nextDouble() * this.height - 0.25,
                  this.posZ + (this.rand.nextDouble() - 0.5) * this.width,
                  (this.rand.nextDouble() - 0.5) * 2.0,
                  -this.rand.nextDouble(),
                  (this.rand.nextDouble() - 0.5) * 2.0
               );
         }
      }

      this.Lr = false;
      super.onLivingUpdate();
   }

   @Override
   public void updateAITasks() {
      if (this.G()) {
         this.attackEntityFrom(DamageSource.drown, 1.0F);
      }

      if (this.isScreaming() && !this.isAggressive && this.rand.nextInt(100) == 0) {
         this.setScreaming(false);
      }

      if (this.worldObj.isDaytime()) {
         float f = this.getBrightness(1.0F);
         if (f > 0.5F && this.worldObj.canSeeSky(new BlockPos(this)) && this.rand.nextFloat() * 30.0F < (f - 0.4F) * 2.0F) {
            this.setAttackTarget((EntityLivingBase)null);
            this.setScreaming(false);
            this.isAggressive = false;
            this.teleportRandomly();
         }
      }

      super.updateAITasks();
   }

   public boolean teleportRandomly() {
      double d = this.posX + (this.rand.nextDouble() - 0.5) * 64.0;
      double e = this.posY + (this.rand.nextInt(64) - 32);
      double d2 = this.posZ + (this.rand.nextDouble() - 0.5) * 64.0;
      return this.teleportTo(d, e, d2);
   }

   public boolean teleportToEntity(Entity entity) {
      Vec3 vec3 = new Vec3(
         this.posX - entity.posX, this.getEntityBoundingBox().minY + this.height / 2.0F - entity.posY + entity.getEyeHeight(), this.posZ - entity.posZ
      );
      vec3 = vec3.normalize();
      double d = 16.0;
      double e = this.posX + (this.rand.nextDouble() - 0.5) * 8.0 - vec3.xCoord * d;
      double d2 = this.posY + (this.rand.nextInt(16) - 8) - vec3.yCoord * d;
      double d3 = this.posZ + (this.rand.nextDouble() - 0.5) * 8.0 - vec3.zCoord * d;
      return this.teleportTo(e, d2, d3);
   }

   public boolean teleportTo(double x, double y, double z) {
      double d = this.posX;
      double e = this.posY;
      double d2 = this.posZ;
      this.posX = x;
      this.posY = y;
      this.posZ = z;
      boolean bl = false;
      BlockPos blockPos = new BlockPos(this.posX, this.posY, this.posZ);
      if (this.worldObj.isBlockLoaded(blockPos)) {
         boolean bl2 = false;

         while (!bl2 && blockPos.getY() > 0) {
            BlockPos blockPos2 = blockPos.down();
            Block block = this.worldObj.getBlockState(blockPos2).getBlock();
            if (block.getMaterial().blocksMovement()) {
               bl2 = true;
            } else {
               this.posY--;
               blockPos = blockPos2;
            }
         }

         if (bl2) {
            super.setPositionAndUpdate(this.posX, this.posY, this.posZ);
            if (this.worldObj.getCollidingBoundingBoxes(this, this.getEntityBoundingBox()).isEmpty() && !this.worldObj.isAnyLiquid(this.getEntityBoundingBox())
               )
             {
               bl = true;
            }
         }
      }

      if (!bl) {
         this.setPosition(d, e, d2);
         return false;
      }

      short s = 128;

      for (int i = 0; i < s; i++) {
         double d3 = i / (s - 1.0);
         float f = (this.rand.nextFloat() - 0.5F) * 0.2F;
         float g = (this.rand.nextFloat() - 0.5F) * 0.2F;
         float h = (this.rand.nextFloat() - 0.5F) * 0.2F;
         double d4 = d + (this.posX - d) * d3 + (this.rand.nextDouble() - 0.5) * this.width * 2.0;
         double d5 = e + (this.posY - e) * d3 + this.rand.nextDouble() * this.height;
         double d6 = d2 + (this.posZ - d2) * d3 + (this.rand.nextDouble() - 0.5) * this.width * 2.0;
         this.worldObj.spawnParticle(EnumParticleTypes.PORTAL, d4, d5, d6, f, g, h);
      }

      this.worldObj.playSoundEffect(d, e, d2, "mob.endermen.portal", 1.0F, 1.0F);
      this.playSound("mob.endermen.portal", 1.0F, 1.0F);
      return true;
   }

   @Override
   public String getLivingSound() {
      return this.isScreaming() ? "mob.endermen.scream" : "mob.endermen.idle";
   }

   @Override
   public String getHurtSound() {
      return "mob.endermen.hit";
   }

   @Override
   public String getDeathSound() {
      return "mob.endermen.death";
   }

   @Override
   public Item getDropItem() {
      return Items.ender_pearl;
   }

   @Override
   public void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
      Item item = this.getDropItem();
      if (item != null) {
         int i = this.rand.nextInt(2 + lootingModifier);

         for (int j = 0; j < i; j++) {
            this.dropItem(item, 1);
         }
      }
   }

   public void setHeldBlockState(IBlockState state) {
      this.dataWatcher.updateObject(16, (short)(Block.getStateId(state) & 65535));
   }

   public IBlockState getHeldBlockState() {
      return Block.getStateById(this.dataWatcher.getWatchableObjectShort(16) & 65535);
   }

   @Override
   public boolean attackEntityFrom(DamageSource source, float amount) {
      if (this.isEntityInvulnerable(source)) {
         return false;
      }

      if (source.getEntity() == null || !(source.getEntity() instanceof EntityEndermite)) {
         if (!this.worldObj.isRemote) {
            this.setScreaming(true);
         }

         if (source instanceof EntityDamageSource && source.getEntity() instanceof EntityPlayer) {
            if (source.getEntity() instanceof EntityPlayerMP && ((EntityPlayerMP)source.getEntity()).theItemInWorldManager.isCreative()) {
               this.setScreaming(false);
            } else {
               this.isAggressive = true;
            }
         }

         if (source instanceof EntityDamageSourceIndirect) {
            this.isAggressive = false;

            for (int i = 0; i < 64; i++) {
               if (this.teleportRandomly()) {
                  return true;
               }
            }

            return false;
         }
      }

      boolean bl = super.attackEntityFrom(source, amount);
      if (source.isUnblockable() && this.rand.nextInt(10) != 0) {
         this.teleportRandomly();
      }

      return bl;
   }

   public boolean isScreaming() {
      return this.dataWatcher.getWatchableObjectByte(18) > 0;
   }

   public void setScreaming(boolean screaming) {
      this.dataWatcher.updateObject(18, (byte)(screaming ? 1 : 0));
   }

   static {
      carriableBlocks.add(Blocks.grass);
      carriableBlocks.add(Blocks.dirt);
      carriableBlocks.add(Blocks.sand);
      carriableBlocks.add(Blocks.gravel);
      carriableBlocks.add(Blocks.yellow_flower);
      carriableBlocks.add(Blocks.red_flower);
      carriableBlocks.add(Blocks.brown_mushroom);
      carriableBlocks.add(Blocks.red_mushroom);
      carriableBlocks.add(Blocks.tnt);
      carriableBlocks.add(Blocks.cactus);
      carriableBlocks.add(Blocks.clay);
      carriableBlocks.add(Blocks.pumpkin);
      carriableBlocks.add(Blocks.melon_block);
      carriableBlocks.add(Blocks.mycelium);
   }

   public static class AIFindPlayer extends EntityAINearestAttackableTarget {
      private EntityPlayer player;
      private int field_179450_h;
      private int field_179451_i;
      private EntityEnderman enderman;
      private static final long c = 117357182062738L;

      public AIFindPlayer(EntityEnderman entityEnderman) {
         super(entityEnderman, EntityPlayer.class, true);
         this.enderman = entityEnderman;
      }

      @Override
      public boolean shouldExecute() {
         double d = this.getTargetDistance();
         List list = this.taskOwner
            .worldObj
            .getEntitiesWithinAABB(EntityPlayer.class, this.taskOwner.getEntityBoundingBox().expand(d, 4.0, d), this.targetEntitySelector);
         Collections.sort(list, this.theNearestAttackableTargetSorter);
         if (list.isEmpty()) {
            return false;
         }

         this.player = (EntityPlayer)list.get(0);
         return true;
      }

      @Override
      public void startExecuting() {
         this.field_179450_h = 5;
         this.field_179451_i = 0;
      }

      @Override
      public void resetTask() {
         this.player = null;
         this.enderman.setScreaming(false);
         IAttributeInstance iAttributeInstance = this.enderman.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
         iAttributeInstance.removeModifier(EntityEnderman.attackingSpeedBoostModifier);
         super.resetTask();
      }

      @Override
      public boolean continueExecuting() {
         if (this.player != null) {
            if (!this.enderman.shouldAttackPlayer(this.player)) {
               return false;
            }

            this.enderman.isAggressive = true;
            this.enderman.faceEntity(this.player, 10.0F, 10.0F);
            return true;
         } else {
            return super.continueExecuting();
         }
      }

      @Override
      public void updateTask() {
         if (this.player != null) {
            if (--this.field_179450_h <= 0) {
               this.targetEntity = this.player;
               this.player = null;
               super.startExecuting();
               this.enderman.playSound("mob.endermen.stare", 1.0F, 1.0F);
               this.enderman.setScreaming(true);
               IAttributeInstance iAttributeInstance = this.enderman.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
               iAttributeInstance.applyModifier(EntityEnderman.attackingSpeedBoostModifier);
            }
         } else {
            if (this.targetEntity != null) {
               if (this.targetEntity instanceof EntityPlayer && this.enderman.shouldAttackPlayer((EntityPlayer)this.targetEntity)) {
                  if (this.targetEntity.getDistanceSqToEntity(this.enderman) < 16.0) {
                     this.enderman.teleportRandomly();
                  }

                  this.field_179451_i = 0;
               } else if (this.targetEntity.getDistanceSqToEntity(this.enderman) > 256.0
                  && this.field_179451_i++ >= 30
                  && this.enderman.teleportToEntity(this.targetEntity)) {
                  this.field_179451_i = 0;
               }
            }

            super.updateTask();
         }
      }
   }

   public static class AIPlaceBlock extends EntityAIBase {
      private EntityEnderman enderman;
      private static final long a = 841046061262L;
      private static final long c = 2187081365273446352L;

      public AIPlaceBlock(EntityEnderman entityEnderman) {
         this.enderman = entityEnderman;
      }

      @Override
      public boolean shouldExecute() {
         return !this.enderman.worldObj.getGameRules().getBoolean("mobGriefing")
            ? false
            : (this.enderman.getHeldBlockState().getBlock().getMaterial() == Material.air ? false : this.enderman.getRNG().nextInt((int)c) == 0);
      }

      @Override
      public void updateTask() {
         Random random = this.enderman.getRNG();
         World world = this.enderman.worldObj;
         int i = MathHelper.floor_double(this.enderman.posX - 1.0 + random.nextDouble() * 2.0);
         int j = MathHelper.floor_double(this.enderman.posY + random.nextDouble() * 2.0);
         int k = MathHelper.floor_double(this.enderman.posZ - 1.0 + random.nextDouble() * 2.0);
         BlockPos blockPos = new BlockPos(i, j, k);
         Block block = world.getBlockState(blockPos).getBlock();
         Block block2 = world.getBlockState(blockPos.down()).getBlock();
         if (this.func_179474_a(world, blockPos, this.enderman.getHeldBlockState().getBlock(), block, block2)) {
            world.setBlockState(blockPos, this.enderman.getHeldBlockState(), 3);
            this.enderman.setHeldBlockState(Blocks.air.getDefaultState());
         }
      }

      private boolean func_179474_a(World worldIn, BlockPos blockPos, Block block, Block block2, Block block3) {
         return !block.canPlaceBlockAt(worldIn, blockPos)
            ? false
            : (block2.getMaterial() != Material.air ? false : (block3.getMaterial() == Material.air ? false : block3.isFullCube()));
      }
   }

   public static class AITakeBlock extends EntityAIBase {
      private EntityEnderman enderman;
      private static final long a = 45316933448137L;
      private static final long c = 2421061017371934740L;

      public AITakeBlock(EntityEnderman entityEnderman) {
         this.enderman = entityEnderman;
      }

      @Override
      public boolean shouldExecute() {
         return !this.enderman.worldObj.getGameRules().getBoolean("mobGriefing")
            ? false
            : (this.enderman.getHeldBlockState().getBlock().getMaterial() != Material.air ? false : this.enderman.getRNG().nextInt((int)c) == 0);
      }

      @Override
      public void updateTask() {
         Random random = this.enderman.getRNG();
         World world = this.enderman.worldObj;
         int i = MathHelper.floor_double(this.enderman.posX - 2.0 + random.nextDouble() * 4.0);
         int j = MathHelper.floor_double(this.enderman.posY + random.nextDouble() * 3.0);
         int k = MathHelper.floor_double(this.enderman.posZ - 2.0 + random.nextDouble() * 4.0);
         BlockPos blockPos = new BlockPos(i, j, k);
         IBlockState iBlockState = world.getBlockState(blockPos);
         Block block = iBlockState.getBlock();
         if (EntityEnderman.carriableBlocks.contains(block)) {
            this.enderman.setHeldBlockState(iBlockState);
            world.setBlockState(blockPos, Blocks.air.getDefaultState());
         }
      }
   }
}
