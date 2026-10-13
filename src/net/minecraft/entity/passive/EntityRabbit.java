package net.minecraft.entity.passive;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCarrot;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAIMoveToBlock;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityJumpHelper;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

public class EntityRabbit extends EntityAnimal {
   private EntityRabbit.AIAvoidEntity<EntityWolf> aiAvoidWolves;
   private int field_175540_bm = 0;
   private int field_175535_bn = 0;
   private boolean field_175536_bo = false;
   private boolean field_175537_bp = false;
   private int currentMoveTypeDuration = 0;
   private EntityRabbit.EnumMoveType moveType = EntityRabbit.EnumMoveType.HOP;
   private int carrotTicks = 0;
   private EntityPlayer field_175543_bt = null;
   private static final long gb = 69724169035756L;

   public EntityRabbit(World var1) {
      super(var1);
      this.setSize(0.6F, 0.7F);
      this.jumpHelper = new EntityRabbit.RabbitJumpHelper(this);
      this.moveHelper = new EntityRabbit.RabbitMoveHelper(this);
      ((PathNavigateGround)this.getNavigator()).setAvoidsWater(true);
      this.navigator.setHeightRequirement(2.5F);
      this.tasks.addTask(1, new EntityAISwimming(this));
      this.tasks.addTask(1, new EntityRabbit.AIPanic(this, 1.33));
      this.tasks.addTask(2, new EntityAITempt(this, 1.0, Items.carrot, false));
      this.tasks.addTask(2, new EntityAITempt(this, 1.0, Items.golden_carrot, false));
      this.tasks.addTask(2, new EntityAITempt(this, 1.0, Item.getItemFromBlock(Blocks.yellow_flower), false));
      this.tasks.addTask(3, new EntityAIMate(this, 0.8));
      this.tasks.addTask(5, new EntityRabbit.AIRaidFarm(this));
      this.tasks.addTask(5, new EntityAIWander(this, 0.6));
      this.tasks.addTask(11, new EntityAIWatchClosest(this, EntityPlayer.class, 10.0F));
      this.aiAvoidWolves = new EntityRabbit.AIAvoidEntity<>(this, EntityWolf.class, 16.0F, 1.33, 1.33);
      this.tasks.addTask(4, this.aiAvoidWolves);
      this.setMovementSpeed(0.0);
   }

   @Override
   public float getJumpUpwardsMotion() {
      return this.moveHelper.isUpdating() && this.moveHelper.getY() > this.posY + 0.5 ? 0.5F : this.moveType.func_180074_b();
   }

   public void setMoveType(EntityRabbit.EnumMoveType var1) {
      this.moveType = var1;
   }

   public float func_175521_o(float var1) {
      return this.field_175535_bn == 0 ? 0.0F : (this.field_175540_bm + var1) / this.field_175535_bn;
   }

   public void setMovementSpeed(double var1) {
      this.getNavigator().setSpeed(var1);
      this.moveHelper.setMoveTo(this.moveHelper.getX(), this.moveHelper.getY(), this.moveHelper.getZ(), var1);
   }

   public void setJumping(boolean var1, EntityRabbit.EnumMoveType var2) {
      super.j(var1);
      if (!var1) {
         if (this.moveType == EntityRabbit.EnumMoveType.ATTACK) {
            this.moveType = EntityRabbit.EnumMoveType.HOP;
         }
      } else {
         this.setMovementSpeed(1.5 * var2.getSpeed());
         this.playSound(this.getJumpingSound(), this.getSoundVolume(), ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F) * 0.8F);
      }

      this.field_175536_bo = var1;
   }

   public void doMovementAction(EntityRabbit.EnumMoveType var1) {
      this.setJumping(true, var1);
      this.field_175535_bn = var1.func_180073_d();
      this.field_175540_bm = 0;
   }

   public boolean func_175523_cj() {
      return this.field_175536_bo;
   }

   @Override
   public void entityInit() {
      super.entityInit();
      this.dataWatcher.addObject(18, (byte)0);
   }

   @Override
   public void updateAITasks() {
      if (this.moveHelper.getSpeed() > 0.8) {
         this.setMoveType(EntityRabbit.EnumMoveType.SPRINT);
      } else if (this.moveType != EntityRabbit.EnumMoveType.ATTACK) {
         this.setMoveType(EntityRabbit.EnumMoveType.HOP);
      }

      if (this.currentMoveTypeDuration > 0) {
         this.currentMoveTypeDuration--;
      }

      if (this.carrotTicks > 0) {
         this.carrotTicks = this.carrotTicks - this.rand.nextInt(3);
         if (this.carrotTicks < 0) {
            this.carrotTicks = 0;
         }
      }

      if (this.onGround) {
         if (!this.field_175537_bp) {
            this.setJumping(false, EntityRabbit.EnumMoveType.NONE);
            this.func_175517_cu();
         }

         if (this.getRabbitType() == 99 && this.currentMoveTypeDuration == 0) {
            EntityLivingBase var4 = this.getAttackTarget();
            if (var4 != null && this.getDistanceSqToEntity(var4) < 16.0) {
               this.calculateRotationYaw(var4.posX, var4.posZ);
               this.moveHelper.setMoveTo(var4.posX, var4.posY, var4.posZ, this.moveHelper.getSpeed());
               this.doMovementAction(EntityRabbit.EnumMoveType.ATTACK);
               this.field_175537_bp = true;
            }
         }

         EntityRabbit.RabbitJumpHelper var7 = (EntityRabbit.RabbitJumpHelper)this.jumpHelper;
         if (!var7.getIsJumping()) {
            if (this.moveHelper.isUpdating() && this.currentMoveTypeDuration == 0) {
               PathEntity var5 = this.navigator.getPath();
               Vec3 var6 = new Vec3(this.moveHelper.getX(), this.moveHelper.getY(), this.moveHelper.getZ());
               if (var5 != null && var5.getCurrentPathIndex() < var5.getCurrentPathLength()) {
                  var6 = var5.getPosition(this);
               }

               this.calculateRotationYaw(var6.xCoord, var6.zCoord);
               this.doMovementAction(this.moveType);
            }
         } else if (!var7.func_180065_d()) {
            this.func_175518_cr();
         }
      }

      this.field_175537_bp = this.onGround;
   }

   @Override
   public void spawnRunningParticles() {
   }

   private void calculateRotationYaw(double var1, double var3) {
      this.rotationYaw = (float)(MathHelper.atan2(var3 - this.posZ, var1 - this.posX) * 180.0 / Math.PI) - 90.0F;
   }

   private void func_175518_cr() {
      ((EntityRabbit.RabbitJumpHelper)this.jumpHelper).func_180066_a(true);
   }

   private void func_175520_cs() {
      ((EntityRabbit.RabbitJumpHelper)this.jumpHelper).func_180066_a(false);
   }

   private void updateMoveTypeDuration() {
      this.currentMoveTypeDuration = this.getMoveTypeDuration();
   }

   private void func_175517_cu() {
      this.updateMoveTypeDuration();
      this.func_175520_cs();
   }

   @Override
   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (this.field_175540_bm != this.field_175535_bn) {
         if (this.field_175540_bm == 0 && !this.worldObj.isRemote) {
            this.worldObj.setEntityState(this, (byte)1);
         }

         this.field_175540_bm++;
      } else if (this.field_175535_bn != 0) {
         this.field_175540_bm = 0;
         this.field_175535_bn = 0;
      }
   }

   @Override
   public void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(10.0);
      this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.3F);
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound var1) {
      super.writeEntityToNBT(var1);
      var1.setInteger("RabbitType", this.getRabbitType());
      var1.setInteger("MoreCarrotTicks", this.carrotTicks);
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound var1) {
      super.readEntityFromNBT(var1);
      this.setRabbitType(var1.getInteger("RabbitType"));
      this.carrotTicks = var1.getInteger("MoreCarrotTicks");
   }

   public String getJumpingSound() {
      return "mob.rabbit.hop";
   }

   @Override
   public String getLivingSound() {
      return "mob.rabbit.idle";
   }

   @Override
   public String getHurtSound() {
      return "mob.rabbit.hurt";
   }

   @Override
   public String getDeathSound() {
      return "mob.rabbit.death";
   }

   @Override
   public boolean attackEntityAsMob(Entity var1) {
      if (this.getRabbitType() == 99) {
         this.playSound("mob.attack", 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
         return var1.attackEntityFrom(DamageSource.causeMobDamage(this), 8.0F);
      } else {
         return var1.attackEntityFrom(DamageSource.causeMobDamage(this), 3.0F);
      }
   }

   @Override
   public int getTotalArmorValue() {
      return this.getRabbitType() == 99 ? 8 : super.getTotalArmorValue();
   }

   @Override
   public boolean attackEntityFrom(DamageSource var1, float var2) {
      return this.isEntityInvulnerable(var1) ? false : super.attackEntityFrom(var1, var2);
   }

   @Override
   public void addRandomDrop() {
      this.entityDropItem(new ItemStack(Items.rabbit_foot, 1), 0.0F);
   }

   @Override
   public void dropFewItems(boolean var1, int var2) {
      int var6 = this.rand.nextInt(2) + this.rand.nextInt(1 + var2);

      for (int var7 = 0; var7 < var6; var7++) {
         this.dropItem(Items.rabbit_hide, 1);
      }

      var6 = this.rand.nextInt(2);

      for (int var9 = 0; var9 < var6; var9++) {
         if (this.isBurning()) {
            this.dropItem(Items.cooked_rabbit, 1);
         } else {
            this.dropItem(Items.rabbit, 1);
         }
      }
   }

   private boolean isRabbitBreedingItem(Item var1) {
      return var1 == Items.carrot || var1 == Items.golden_carrot || var1 == Item.getItemFromBlock(Blocks.yellow_flower);
   }

   public EntityRabbit createChild(EntityAgeable var1) {
      EntityRabbit var5 = new EntityRabbit(this.worldObj);
      if (var1 instanceof EntityRabbit) {
         var5.setRabbitType(this.rand.nextBoolean() ? this.getRabbitType() : ((EntityRabbit)var1).getRabbitType());
      }

      return var5;
   }

   @Override
   public boolean isBreedingItem(ItemStack var1) {
      return var1 != null && this.isRabbitBreedingItem(var1.getItem());
   }

   public int getRabbitType() {
      return this.dataWatcher.getWatchableObjectByte(18);
   }

   public void setRabbitType(int var1) {
      if (var1 == 99) {
         this.tasks.removeTask(this.aiAvoidWolves);
         this.tasks.addTask(4, new EntityRabbit.AIEvilAttack(this));
         this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
         this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, true));
         this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityWolf.class, true));
         if (!this.hasCustomName()) {
            this.setCustomNameTag(StatCollector.translateToLocal("entity.KillerBunny.name"));
         }
      }

      this.dataWatcher.updateObject(18, (byte)var1);
   }

   @Override
   public IEntityLivingData onInitialSpawn(DifficultyInstance var1, IEntityLivingData var2) {
      var2 = super.onInitialSpawn(var1, var2);
      int var6 = this.rand.nextInt(6);
      boolean var7 = false;
      if (var2 instanceof EntityRabbit.RabbitTypeData) {
         var6 = ((EntityRabbit.RabbitTypeData)var2).typeData;
         var7 = true;
      } else {
         var2 = new EntityRabbit.RabbitTypeData(var6);
      }

      this.setRabbitType(var6);
      if (var7) {
         this.setGrowingAge(-24000);
      }

      return var2;
   }

   private boolean isCarrotEaten() {
      return this.carrotTicks == 0;
   }

   public int getMoveTypeDuration() {
      return this.moveType.getDuration();
   }

   public void createEatingParticles() {
      this.worldObj
         .spawnParticle(
            EnumParticleTypes.BLOCK_DUST,
            this.posX + this.rand.nextFloat() * this.width * 2.0F - this.width,
            this.posY + 0.5 + this.rand.nextFloat() * this.height,
            this.posZ + this.rand.nextFloat() * this.width * 2.0F - this.width,
            0.0,
            0.0,
            0.0,
            Block.getStateId(Blocks.carrots.getStateFromMeta(7))
         );
      this.carrotTicks = 100;
   }

   @Override
   public void handleStatusUpdate(byte var1) {
      if (var1 == 1) {
         this.createRunningParticles();
         this.field_175535_bn = 10;
         this.field_175540_bm = 0;
      } else {
         super.handleStatusUpdate(var1);
      }
   }

   public static class AIAvoidEntity<T extends Entity> extends EntityAIAvoidEntity<T> {
      private EntityRabbit entityInstance;

      public AIAvoidEntity(EntityRabbit var1, Class<T> var2, float var3, double var4, double var6) {
         super(var1, var2, var3, var4, var6);
         this.entityInstance = var1;
      }

      @Override
      public void updateTask() {
         super.updateTask();
      }
   }

   public static class AIEvilAttack extends EntityAIAttackOnCollide {
      public AIEvilAttack(EntityRabbit var1) {
         super(var1, EntityLivingBase.class, 1.4, true);
      }

      @Override
      public double func_179512_a(EntityLivingBase var1) {
         return 4.0F + var1.width;
      }
   }

   public static class AIPanic extends EntityAIPanic {
      private EntityRabbit theEntity;

      public AIPanic(EntityRabbit var1, double var2) {
         super(var1, var2);
         this.theEntity = var1;
      }

      @Override
      public void updateTask() {
         super.updateTask();
         this.theEntity.setMovementSpeed(this.speed);
      }
   }

   public static class AIRaidFarm extends EntityAIMoveToBlock {
      private final EntityRabbit rabbit;
      private boolean field_179498_d;
      private boolean field_179499_e = false;
      private static final long a = 83914751035641L;

      public AIRaidFarm(EntityRabbit var1) {
         super(var1, 0.7F, 16);
         this.rabbit = var1;
      }

      @Override
      public boolean shouldExecute() {
         if (this.runDelay <= 0) {
            if (!this.rabbit.worldObj.getGameRules().getBoolean("mobGriefing")) {
               return false;
            }

            this.field_179499_e = false;
            this.field_179498_d = this.rabbit.isCarrotEaten();
         }

         return super.shouldExecute();
      }

      @Override
      public boolean continueExecuting() {
         return this.field_179499_e && super.continueExecuting();
      }

      @Override
      public void startExecuting() {
         super.startExecuting();
      }

      @Override
      public void resetTask() {
         super.resetTask();
      }

      @Override
      public void updateTask() {
         super.updateTask();
         this.rabbit
            .getLookHelper()
            .setLookPosition(
               this.destinationBlock.getX() + 0.5,
               this.destinationBlock.getY() + 1,
               this.destinationBlock.getZ() + 0.5,
               10.0F,
               this.rabbit.getVerticalFaceSpeed()
            );
         if (this.getIsAboveDestination()) {
            World var4 = this.rabbit.worldObj;
            BlockPos var5 = this.destinationBlock.up();
            IBlockState var6 = var4.getBlockState(var5);
            Block var7 = var6.getBlock();
            if (this.field_179499_e && var7 instanceof BlockCarrot && var6.getValue(BlockCarrot.AGE) == 7) {
               var4.setBlockState(var5, Blocks.air.getDefaultState(), 2);
               var4.destroyBlock(var5, true);
               this.rabbit.createEatingParticles();
            }

            this.field_179499_e = false;
            this.runDelay = 10;
         }
      }

      @Override
      public boolean shouldMoveTo(World var1, BlockPos var2) {
         Block var6 = var1.getBlockState(var2).getBlock();
         if (var6 == Blocks.farmland) {
            var2 = var2.up();
            IBlockState var7 = var1.getBlockState(var2);
            var6 = var7.getBlock();
            if (var6 instanceof BlockCarrot && var7.getValue(BlockCarrot.AGE) == 7 && this.field_179498_d && !this.field_179499_e) {
               this.field_179499_e = true;
               return true;
            }
         }

         return false;
      }
   }

   public enum EnumMoveType {
      NONE(0.0F, 0.0F, 30, 1),
      HOP(0.8F, 0.2F, 20, 10),
      STEP(1.0F, 0.45F, 14, 14),
      SPRINT(1.75F, 0.4F, 1, 8),
      ATTACK(2.0F, 0.7F, 7, 8);

      private final float speed;
      private final float field_180077_g;
      private final int duration;
      private final int field_180085_i;

      EnumMoveType(float var3, float var4, int var5, int var6) {
         this.speed = var3;
         this.field_180077_g = var4;
         this.duration = var5;
         this.field_180085_i = var6;
      }

      public float getSpeed() {
         return this.speed;
      }

      public float func_180074_b() {
         return this.field_180077_g;
      }

      public int getDuration() {
         return this.duration;
      }

      public int func_180073_d() {
         return this.field_180085_i;
      }
   }

   public class RabbitJumpHelper extends EntityJumpHelper {
      private EntityRabbit theEntity;
      private boolean field_180068_d = false;
      private static final long a = 132088604175563L;

      public RabbitJumpHelper(EntityRabbit var2) {
         super(var2);
         this.theEntity = var2;
      }

      public boolean getIsJumping() {
         return this.isJumping;
      }

      public boolean func_180065_d() {
         return this.field_180068_d;
      }

      public void func_180066_a(boolean var1) {
         this.field_180068_d = var1;
      }

      @Override
      public void doJump() {
         if (this.isJumping) {
            this.theEntity.doMovementAction(EntityRabbit.EnumMoveType.STEP);
            this.isJumping = false;
         }
      }
   }

   public static class RabbitMoveHelper extends EntityMoveHelper {
      private EntityRabbit theEntity;
      private static final long b = 127796420395451L;

      public RabbitMoveHelper(EntityRabbit var1) {
         super(var1);
         this.theEntity = var1;
      }

      @Override
      public void onUpdateMoveHelper() {
         if (this.theEntity.onGround && !this.theEntity.func_175523_cj()) {
            this.theEntity.setMovementSpeed(0.0);
         }

         super.onUpdateMoveHelper();
      }
   }

   public static class RabbitTypeData implements IEntityLivingData {
      public int typeData;

      public RabbitTypeData(int var1) {
         this.typeData = var1;
      }
   }
}
