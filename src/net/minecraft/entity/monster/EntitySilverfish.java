package net.minecraft.entity.monster;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSilverfish;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class EntitySilverfish extends EntityMob {
   private EntitySilverfish.AISummonSilverfish summonSilverfish;
   private static final long ab = 121298672216908L;

   public EntitySilverfish(World worldIn) {
      super(worldIn);
      this.setSize(0.4F, 0.3F);
      this.tasks.addTask(1, new EntityAISwimming(this));
      this.tasks.addTask(3, this.summonSilverfish = new EntitySilverfish.AISummonSilverfish(this));
      this.tasks.addTask(4, new EntityAIAttackOnCollide(this, EntityPlayer.class, 1.0, false));
      this.tasks.addTask(5, new EntitySilverfish.AIHideInStone(this));
      this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
      this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, true));
   }

   @Override
   public double getYOffset() {
      return 0.2;
   }

   @Override
   public float getEyeHeight() {
      return 0.1F;
   }

   @Override
   public void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(8.0);
      this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.25);
      this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(1.0);
   }

   @Override
   public boolean canTriggerWalking() {
      return false;
   }

   @Override
   public String getLivingSound() {
      return "mob.silverfish.say";
   }

   @Override
   public String getHurtSound() {
      return "mob.silverfish.hit";
   }

   @Override
   public String getDeathSound() {
      return "mob.silverfish.kill";
   }

   @Override
   public boolean attackEntityFrom(DamageSource source, float amount) {
      if (this.isEntityInvulnerable(source)) {
         return false;
      }

      if (source instanceof EntityDamageSource || source == DamageSource.magic) {
         this.summonSilverfish.func_179462_f();
      }

      return super.attackEntityFrom(source, amount);
   }

   @Override
   public void playStepSound(BlockPos pos, Block blockIn) {
      this.playSound("mob.silverfish.step", 0.15F, 1.0F);
   }

   @Override
   public Item getDropItem() {
      return null;
   }

   @Override
   public void onUpdate() {
      this.renderYawOffset = this.rotationYaw;
      super.onUpdate();
   }

   @Override
   public float getBlockPathWeight(BlockPos pos) {
      return this.worldObj.getBlockState(pos.down()).getBlock() == Blocks.stone ? 10.0F : super.getBlockPathWeight(pos);
   }

   @Override
   public boolean isValidLightLevel() {
      return true;
   }

   @Override
   public boolean getCanSpawnHere() {
      if (super.getCanSpawnHere()) {
         EntityPlayer entityPlayer = this.worldObj.getClosestPlayerToEntity(this, 5.0);
         return entityPlayer == null;
      } else {
         return false;
      }
   }

   @Override
   public EnumCreatureAttribute getCreatureAttribute() {
      return EnumCreatureAttribute.ARTHROPOD;
   }

   public static class AIHideInStone extends EntityAIWander {
      private final EntitySilverfish silverfish;
      private EnumFacing facing;
      private boolean field_179484_c;
      private static final long c = 56520869211918L;

      public AIHideInStone(EntitySilverfish silverfishIn) {
         super(silverfishIn, 1.0, 10);
         this.silverfish = silverfishIn;
         this.setMutexBits(1);
      }

      @Override
      public boolean shouldExecute() {
         if (this.silverfish.getAttackTarget() != null) {
            return false;
         }

         if (!this.silverfish.getNavigator().noPath()) {
            return false;
         }

         Random random2 = this.silverfish.getRNG();
         if (random2.nextInt(10) == 0) {
            this.facing = EnumFacing.random(random2);
            BlockPos blockPos = new BlockPos(this.silverfish.posX, this.silverfish.posY + 0.5, this.silverfish.posZ).offset(this.facing);
            IBlockState iBlockState = this.silverfish.worldObj.getBlockState(blockPos);
            if (BlockSilverfish.canContainSilverfish(iBlockState)) {
               this.field_179484_c = true;
               return true;
            }
         }

         this.field_179484_c = false;
         return super.shouldExecute();
      }

      @Override
      public boolean continueExecuting() {
         return this.field_179484_c ? false : super.continueExecuting();
      }

      @Override
      public void startExecuting() {
         if (!this.field_179484_c) {
            super.startExecuting();
         } else {
            World world = this.silverfish.worldObj;
            BlockPos blockPos = new BlockPos(this.silverfish.posX, this.silverfish.posY + 0.5, this.silverfish.posZ).offset(this.facing);
            IBlockState iBlockState = world.getBlockState(blockPos);
            if (BlockSilverfish.canContainSilverfish(iBlockState)) {
               world.setBlockState(
                  blockPos, Blocks.monster_egg.getDefaultState().withProperty(BlockSilverfish.VARIANT, BlockSilverfish.EnumType.forModelBlock(iBlockState)), 3
               );
               this.silverfish.spawnExplosionParticle();
               this.silverfish.setDead();
            }
         }
      }
   }

   public static class AISummonSilverfish extends EntityAIBase {
      private EntitySilverfish silverfish;
      private int field_179463_b;
      private static final long a = 85565148347182L;

      public AISummonSilverfish(EntitySilverfish silverfishIn) {
         this.silverfish = silverfishIn;
      }

      public void func_179462_f() {
         if (this.field_179463_b == 0) {
            this.field_179463_b = 20;
         }
      }

      @Override
      public boolean shouldExecute() {
         return this.field_179463_b > 0;
      }

      @Override
      public void updateTask() {
         this.field_179463_b--;
         if (this.field_179463_b <= 0) {
            World world = this.silverfish.worldObj;
            Random random = this.silverfish.getRNG();
            BlockPos blockPos = new BlockPos(this.silverfish);

            for (int i = 0; i <= 5 && i >= -5; i = (byte)(i <= 0 ? 1 - i : 0 - i)) {
               for (int j = 0; j <= 10 && j >= -10; j = (byte)(j <= 0 ? 1 - j : 0 - j)) {
                  for (int k = 0; k <= 10 && k >= -10; k = (byte)(k <= 0 ? 1 - k : 0 - k)) {
                     BlockPos blockPos2 = blockPos.add(j, i, k);
                     IBlockState iBlockState = world.getBlockState(blockPos2);
                     if (iBlockState.getBlock() == Blocks.monster_egg) {
                        if (world.getGameRules().getBoolean("mobGriefing")) {
                           world.destroyBlock(blockPos2, true);
                        } else {
                           world.setBlockState(blockPos2, iBlockState.getValue(BlockSilverfish.VARIANT).getModelBlock(), 3);
                        }

                        if (random.nextBoolean()) {
                           return;
                        }
                     }
                  }
               }
            }
         }
      }
   }
}
