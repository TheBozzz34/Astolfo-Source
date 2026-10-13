package net.minecraft.entity.monster;

import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAICreeperSwell;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntityCreeper extends EntityMob {
   private int lastActiveTime;
   private int timeSinceIgnited;
   private int fuseTime = 30;
   private int explosionRadius = 3;
   private int field_175494_bm = 0;
   private static final long ab = 137188823183303L;

   public EntityCreeper(World var1) {
      super(var1);
      this.tasks.addTask(1, new EntityAISwimming(this));
      this.tasks.addTask(2, new EntityAICreeperSwell(this));
      this.tasks.addTask(3, new EntityAIAvoidEntity<>(this, EntityOcelot.class, 6.0F, 1.0, 1.2));
      this.tasks.addTask(4, new EntityAIAttackOnCollide(this, 1.0, false));
      this.tasks.addTask(5, new EntityAIWander(this, 0.8));
      this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
      this.tasks.addTask(6, new EntityAILookIdle(this));
      this.targetTasks.addTask(1, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, true));
      this.targetTasks.addTask(2, new EntityAIHurtByTarget(this, false));
   }

   @Override
   public void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.25);
   }

   @Override
   public int getMaxFallHeight() {
      return this.getAttackTarget() == null ? 3 : 3 + (int)(this.getHealth() - 1.0F);
   }

   @Override
   public void fall(float var1, float var2) {
      super.fall(var1, var2);
      this.timeSinceIgnited = (int)(this.timeSinceIgnited + var1 * 1.5F);
      if (this.timeSinceIgnited > this.fuseTime - 5) {
         this.timeSinceIgnited = this.fuseTime - 5;
      }
   }

   @Override
   public void entityInit() {
      super.entityInit();
      this.dataWatcher.addObject(16, (byte)-1);
      this.dataWatcher.addObject(17, (byte)0);
      this.dataWatcher.addObject(18, (byte)0);
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound var1) {
      super.writeEntityToNBT(var1);
      if (this.dataWatcher.getWatchableObjectByte(17) == 1) {
         var1.setBoolean("powered", true);
      }

      var1.setShort("Fuse", (short)this.fuseTime);
      var1.setByte("ExplosionRadius", (byte)this.explosionRadius);
      var1.setBoolean("ignited", this.hasIgnited());
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound var1) {
      super.readEntityFromNBT(var1);
      this.dataWatcher.updateObject(17, (byte)(var1.getBoolean("powered") ? 1 : 0));
      if (var1.hasKey("Fuse", 99)) {
         this.fuseTime = var1.getShort("Fuse");
      }

      if (var1.hasKey("ExplosionRadius", 99)) {
         this.explosionRadius = var1.getByte("ExplosionRadius");
      }

      if (var1.getBoolean("ignited")) {
         this.ignite();
      }
   }

   @Override
   public void onUpdate() {
      if (this.isEntityAlive()) {
         this.lastActiveTime = this.timeSinceIgnited;
         if (this.hasIgnited()) {
            this.setCreeperState(1);
         }

         int var4 = this.getCreeperState();
         if (var4 > 0 && this.timeSinceIgnited == 0) {
            this.playSound("creeper.primed", 1.0F, 0.5F);
         }

         this.timeSinceIgnited += var4;
         if (this.timeSinceIgnited < 0) {
            this.timeSinceIgnited = 0;
         }

         if (this.timeSinceIgnited >= this.fuseTime) {
            this.timeSinceIgnited = this.fuseTime;
            this.explode();
         }
      }

      super.onUpdate();
   }

   @Override
   public String getHurtSound() {
      return "mob.creeper.say";
   }

   @Override
   public String getDeathSound() {
      return "mob.creeper.death";
   }

   @Override
   public void onDeath(DamageSource var1) {
      super.onDeath(var1);
      if (var1.getEntity() instanceof EntitySkeleton) {
         int var5 = Item.getIdFromItem(Items.record_13);
         int var6 = Item.getIdFromItem(Items.record_wait);
         int var7 = var5 + this.rand.nextInt(var6 - var5 + 1);
         this.dropItem(Item.getItemById(var7), 1);
      } else if (var1.getEntity() instanceof EntityCreeper
         && var1.getEntity() != this
         && ((EntityCreeper)var1.getEntity()).getPowered()
         && ((EntityCreeper)var1.getEntity()).isAIEnabled()) {
         ((EntityCreeper)var1.getEntity()).func_175493_co();
         this.entityDropItem(new ItemStack(Items.skull, 1, 4), 0.0F);
      }
   }

   @Override
   public boolean attackEntityAsMob(Entity var1) {
      return true;
   }

   public boolean getPowered() {
      return this.dataWatcher.getWatchableObjectByte(17) == 1;
   }

   public float getCreeperFlashIntensity(float var1) {
      return (this.lastActiveTime + (this.timeSinceIgnited - this.lastActiveTime) * var1) / (this.fuseTime - 2);
   }

   @Override
   public Item getDropItem() {
      return Items.gunpowder;
   }

   public int getCreeperState() {
      return this.dataWatcher.getWatchableObjectByte(16);
   }

   public void setCreeperState(int var1) {
      this.dataWatcher.updateObject(16, (byte)var1);
   }

   @Override
   public void onStruckByLightning(EntityLightningBolt var1) {
      super.onStruckByLightning(var1);
      this.dataWatcher.updateObject(17, (byte)1);
   }

   @Override
   public boolean interact(EntityPlayer var1) {
      ItemStack var5 = var1.inventory.getCurrentItem();
      if (var5 != null && var5.getItem() == Items.flint_and_steel) {
         this.worldObj.playSoundEffect(this.posX + 0.5, this.posY + 0.5, this.posZ + 0.5, "fire.ignite", 1.0F, this.rand.nextFloat() * 0.4F + 0.8F);
         var1.swingItem();
         if (!this.worldObj.isRemote) {
            this.ignite();
            var5.damageItem(1, var1);
            return true;
         }
      }

      return super.interact(var1);
   }

   private void explode() {
      if (!this.worldObj.isRemote) {
         boolean var4 = this.worldObj.getGameRules().getBoolean("mobGriefing");
         float var5 = this.getPowered() ? 2.0F : 1.0F;
         this.worldObj.createExplosion(this, this.posX, this.posY, this.posZ, this.explosionRadius * var5, var4);
         this.setDead();
      }
   }

   public boolean hasIgnited() {
      return this.dataWatcher.getWatchableObjectByte(18) != 0;
   }

   public void ignite() {
      this.dataWatcher.updateObject(18, (byte)1);
   }

   public boolean isAIEnabled() {
      return this.field_175494_bm < 1 && this.worldObj.getGameRules().getBoolean("doMobLoot");
   }

   public void func_175493_co() {
      this.field_175494_bm++;
   }
}
