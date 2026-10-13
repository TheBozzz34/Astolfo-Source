package net.minecraft.entity.player;

import bypass.Astolfo;
import bypass.event.player.JumpEvent;
import bypass.module.combat.KeepSprint;
import bypass.module.movement.Flight;
import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.command.server.CommandBlockLogic;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.boss.EntityDragonPart;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.event.ClickEvent;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryEnderChest;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.potion.Potion;
import net.minecraft.scoreboard.IScoreObjectiveCriteria;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.FoodStats;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.IInteractionObject;
import net.minecraft.world.LockCode;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;

public abstract class EntityPlayer extends EntityLivingBase {
   public InventoryPlayer inventory = new InventoryPlayer(this);
   private InventoryEnderChest theInventoryEnderChest = new InventoryEnderChest();
   public Container inventoryContainer;
   public Container openContainer;
   public FoodStats foodStats = new FoodStats();
   public int flyToggleTimer;
   public float prevCameraYaw;
   public float cameraYaw;
   public int xpCooldown;
   public double prevChasingPosX;
   public double prevChasingPosY;
   public double prevChasingPosZ;
   public double chasingPosX;
   public double chasingPosY;
   public double chasingPosZ;
   public boolean sleeping;
   public BlockPos playerLocation;
   private int sleepTimer;
   public float renderOffsetX;
   public float renderOffsetY;
   public float renderOffsetZ;
   private BlockPos spawnChunk;
   private boolean spawnForced;
   private BlockPos startMinecartRidingCoordinate;
   public PlayerCapabilities capabilities = new PlayerCapabilities();
   public int experienceLevel;
   public int experienceTotal;
   public float experience;
   private int xpSeed;
   private ItemStack itemInUse;
   private int itemInUseCount;
   public float speedOnGround = 0.1F;
   public float speedInAir = 0.02F;
   private int lastXPSound;
   private final GameProfile gameProfile;
   private boolean hasReducedDebug = false;
   public EntityFishHook fishEntity;
   private static final long cb = 4340354591003L;

   public EntityPlayer(World var1, GameProfile var2) {
      super(var1);
      this.entityUniqueID = getUUID(var2);
      this.gameProfile = var2;
      this.inventoryContainer = new ContainerPlayer(this.inventory, !var1.isRemote, this);
      this.openContainer = this.inventoryContainer;
      BlockPos var5 = var1.getSpawnPoint();
      this.setLocationAndAngles(var5.getX() + 0.5, var5.getY() + 1, var5.getZ() + 0.5, 0.0F, 0.0F);
      this.unused180 = 180.0F;
      this.fireResistance = 20;
   }

   @Override
   public void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getAttributeMap().registerAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(1.0);
      this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.1F);
   }

   @Override
   public void entityInit() {
      super.entityInit();
      this.dataWatcher.addObject(16, (byte)0);
      this.dataWatcher.addObject(17, 0.0F);
      this.dataWatcher.addObject(18, 0);
      this.dataWatcher.addObject(10, (byte)0);
   }

   public ItemStack getItemInUse() {
      return this.itemInUse;
   }

   public int p() {
      return this.itemInUseCount;
   }

   public boolean isUsingItem() {
      return this.itemInUse != null;
   }

   public int n() {
      return this.isUsingItem() ? this.itemInUse.getMaxItemUseDuration() - this.itemInUseCount : 0;
   }

   public void stopUsingItem() {
      if (this.itemInUse != null) {
         this.itemInUse.onPlayerStoppedUsing(this.worldObj, this, this.itemInUseCount);
      }

      this.clearItemInUse();
   }

   public void clearItemInUse() {
      this.itemInUse = null;
      this.itemInUseCount = 0;
      if (!this.worldObj.isRemote) {
         this.setEating(false);
      }
   }

   public boolean isBlocking() {
      return this.isUsingItem() && this.itemInUse.getItem().getItemUseAction(this.itemInUse) == EnumAction.BLOCK;
   }

   @Override
   public void onUpdate() {
      this.noClip = this.isSpectator();
      if (this.isSpectator()) {
         this.onGround = false;
      }

      if (this.itemInUse != null) {
         ItemStack var5 = this.inventory.getCurrentItem();
         if (var5 == this.itemInUse) {
            if (this.itemInUseCount <= 25 && this.itemInUseCount % 4 == 0) {
               this.updateItemUse(var5, 5);
            }

            if (--this.itemInUseCount == 0 && !this.worldObj.isRemote) {
               this.onItemUseFinish();
            }
         } else {
            this.clearItemInUse();
         }
      }

      if (this.xpCooldown > 0) {
         this.xpCooldown--;
      }

      if (this.isPlayerSleeping()) {
         this.sleepTimer++;
         if (this.sleepTimer > 100) {
            this.sleepTimer = 100;
         }

         if (!this.worldObj.isRemote) {
            if (!this.isInBed()) {
               this.wakeUpPlayer(true, true, false);
            } else if (this.worldObj.isDaytime()) {
               this.wakeUpPlayer(false, true, true);
            }
         }
      } else if (this.sleepTimer > 0) {
         this.sleepTimer++;
         if (this.sleepTimer >= 110) {
            this.sleepTimer = 0;
         }
      }

      super.onUpdate();
      if (!this.worldObj.isRemote && this.openContainer != null && !this.openContainer.canInteractWith(this)) {
         this.closeScreen();
         this.openContainer = this.inventoryContainer;
      }

      if (this.isBurning() && this.capabilities.disableDamage) {
         this.extinguish();
      }

      this.prevChasingPosX = this.chasingPosX;
      this.prevChasingPosY = this.chasingPosY;
      this.prevChasingPosZ = this.chasingPosZ;
      double var18 = this.posX - this.chasingPosX;
      double var7 = this.posY - this.chasingPosY;
      double var9 = this.posZ - this.chasingPosZ;
      double var11 = 10.0;
      if (var18 > var11) {
         this.prevChasingPosX = this.chasingPosX = this.posX;
      }

      if (var9 > var11) {
         this.prevChasingPosZ = this.chasingPosZ = this.posZ;
      }

      if (var7 > var11) {
         this.prevChasingPosY = this.chasingPosY = this.posY;
      }

      if (var18 < -var11) {
         this.prevChasingPosX = this.chasingPosX = this.posX;
      }

      if (var9 < -var11) {
         this.prevChasingPosZ = this.chasingPosZ = this.posZ;
      }

      if (var7 < -var11) {
         this.prevChasingPosY = this.chasingPosY = this.posY;
      }

      this.chasingPosX += var18 * 0.25;
      this.chasingPosZ += var9 * 0.25;
      this.chasingPosY += var7 * 0.25;
      if (this.ridingEntity == null) {
         this.startMinecartRidingCoordinate = null;
      }

      if (!this.worldObj.isRemote) {
         this.foodStats.onUpdate(this);
         this.triggerAchievement(StatList.minutesPlayedStat);
         if (this.isEntityAlive()) {
            this.triggerAchievement(StatList.timeSinceDeathStat);
         }
      }

      double var14 = MathHelper.clamp_double(this.posX, -2.9999999E7, 2.9999999E7);
      double var16 = MathHelper.clamp_double(this.posZ, -2.9999999E7, 2.9999999E7);
      if (var14 != this.posX || var16 != this.posZ) {
         this.setPosition(var14, this.posY, var16);
      }
   }

   @Override
   public int getMaxInPortalTime() {
      return this.capabilities.disableDamage ? 0 : 80;
   }

   @Override
   public String getSwimSound() {
      return "game.player.swim";
   }

   @Override
   public String getSplashSound() {
      return "game.player.swim.splash";
   }

   @Override
   public int getPortalCooldown() {
      return 10;
   }

   @Override
   public void playSound(String var1, float var2, float var3) {
      this.worldObj.playSoundToNearExcept(this, var1, var2, var3);
   }

   public void updateItemUse(ItemStack var1, int var2) {
      if (var1.getItemUseAction() == EnumAction.DRINK) {
         this.playSound("random.drink", 0.5F, this.worldObj.rand.nextFloat() * 0.1F + 0.9F);
      }

      if (var1.getItemUseAction() == EnumAction.EAT) {
         for (int var6 = 0; var6 < var2; var6++) {
            Vec3 var7 = new Vec3((this.rand.nextFloat() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, 0.0);
            var7 = var7.rotatePitch(-this.rotationPitch * (float) Math.PI / 180.0F);
            var7 = var7.rotateYaw(-this.rotationYaw * (float) Math.PI / 180.0F);
            double var8 = -this.rand.nextFloat() * 0.6 - 0.3;
            Vec3 var10 = new Vec3((this.rand.nextFloat() - 0.5) * 0.3, var8, 0.6);
            var10 = var10.rotatePitch(-this.rotationPitch * (float) Math.PI / 180.0F);
            var10 = var10.rotateYaw(-this.rotationYaw * (float) Math.PI / 180.0F);
            var10 = var10.addVector(this.posX, this.posY + this.getEyeHeight(), this.posZ);
            if (var1.getHasSubtypes()) {
               this.worldObj
                  .spawnParticle(
                     EnumParticleTypes.ITEM_CRACK,
                     var10.xCoord,
                     var10.yCoord,
                     var10.zCoord,
                     var7.xCoord,
                     var7.yCoord + 0.05,
                     var7.zCoord,
                     Item.getIdFromItem(var1.getItem()),
                     var1.getMetadata()
                  );
            } else {
               this.worldObj
                  .spawnParticle(
                     EnumParticleTypes.ITEM_CRACK,
                     var10.xCoord,
                     var10.yCoord,
                     var10.zCoord,
                     var7.xCoord,
                     var7.yCoord + 0.05,
                     var7.zCoord,
                     Item.getIdFromItem(var1.getItem())
                  );
            }
         }

         this.playSound("random.eat", 0.5F + 0.5F * this.rand.nextInt(2), (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
      }
   }

   public void onItemUseFinish() {
      if (this.itemInUse != null) {
         this.updateItemUse(this.itemInUse, 16);
         int var4 = this.itemInUse.stackSize;
         ItemStack var5 = this.itemInUse.onItemUseFinish(this.worldObj, this);
         if (var5 != this.itemInUse || var5 != null && var5.stackSize != var4) {
            this.inventory.mainInventory[this.inventory.currentItem] = var5;
            if (var5.stackSize == 0) {
               this.inventory.mainInventory[this.inventory.currentItem] = null;
            }
         }

         this.clearItemInUse();
      }
   }

   @Override
   public void handleStatusUpdate(byte var1) {
      if (var1 == 9) {
         this.onItemUseFinish();
      } else if (var1 == 23) {
         this.hasReducedDebug = false;
      } else if (var1 == 22) {
         this.hasReducedDebug = true;
      } else {
         super.handleStatusUpdate(var1);
      }
   }

   @Override
   public boolean isMovementBlocked() {
      return this.getHealth() <= 0.0F || this.isPlayerSleeping();
   }

   public void closeScreen() {
      this.openContainer = this.inventoryContainer;
   }

   @Override
   public void updateRidden() {
      if (!this.worldObj.isRemote && this.isSneaking()) {
         this.mountEntity((Entity)null);
         this.v(false);
      } else {
         double var4 = this.posX;
         double var6 = this.posY;
         double var8 = this.posZ;
         float var10 = this.rotationYaw;
         float var11 = this.rotationPitch;
         super.updateRidden();
         this.prevCameraYaw = this.cameraYaw;
         this.cameraYaw = 0.0F;
         this.addMountedMovementStat(this.posX - var4, this.posY - var6, this.posZ - var8);
         if (this.ridingEntity instanceof EntityPig) {
            this.rotationPitch = var11;
            this.rotationYaw = var10;
            this.renderYawOffset = ((EntityPig)this.ridingEntity).renderYawOffset;
         }
      }
   }

   @Override
   public void preparePlayerToSpawn() {
      this.setSize(0.6F, 1.8F);
      super.preparePlayerToSpawn();
      this.setHealth(this.getMaxHealth());
      this.deathTime = 0;
   }

   @Override
   public void updateEntityActionState() {
      super.updateEntityActionState();
      this.L();
      this.rotationYawHead = this.rotationYaw;
   }

   @Override
   public void onLivingUpdate() {
      long var1 = cb ^ 57524029105315L;
      long var3 = var1 ^ 910696673291L;
      if (this.flyToggleTimer > 0) {
         this.flyToggleTimer--;
      }

      if (this.worldObj.getDifficulty() == EnumDifficulty.PEACEFUL && this.worldObj.getGameRules().getBoolean("naturalRegeneration")) {
         if (this.getHealth() < this.getMaxHealth() && this.ticksExisted % 20 == 0) {
            this.heal(1.0F);
         }

         if (this.foodStats.needFood() && this.ticksExisted % 10 == 0) {
            this.foodStats.setFoodLevel(this.foodStats.getFoodLevel() + 1);
         }
      }

      this.inventory.Y();
      this.prevCameraYaw = this.cameraYaw;
      super.onLivingUpdate();
      IAttributeInstance var6 = this.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
      if (!this.worldObj.isRemote) {
         var6.setBaseValue(this.capabilities.getWalkSpeed());
      }

      this.jumpMovementFactor = this.speedInAir;
      if (this.isSprinting()) {
         this.jumpMovementFactor = (float)(this.jumpMovementFactor + this.speedInAir * 0.3);
      }

      this.setAIMoveSpeed((float)var6.getAttributeValue());
      float var7 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
      float var8 = (float)(Math.atan(-this.motionY * 0.2F) * 15.0);
      if (var7 > 0.1F) {
         var7 = 0.1F;
      }

      if ((!this.onGround || this.getHealth() <= 0.0F)
         && this == Minecraft.getMinecraft().thePlayer
         && (
            !Astolfo.INSTANCE.getModuleManager().get(Flight.class).isEnabled()
               || !Flight.bobbing.getValue() && Astolfo.INSTANCE.getModuleManager().get(Flight.class).isEnabled()
         )) {
         var7 = 0.0F;
      }

      if (this.onGround || this.getHealth() <= 0.0F) {
         var8 = 0.0F;
      }

      this.cameraYaw = this.cameraYaw + (var7 - this.cameraYaw) * 0.4F;
      this.cameraPitch = this.cameraPitch + (var8 - this.cameraPitch) * 0.8F;
      if (this.getHealth() > 0.0F && !this.isSpectator()) {
         AxisAlignedBB var9 = null;
         if (this.ridingEntity != null && !this.ridingEntity.isDead) {
            var9 = this.getEntityBoundingBox().union(this.ridingEntity.getEntityBoundingBox()).expand(1.0, 0.0, 1.0);
         } else {
            var9 = this.getEntityBoundingBox().expand(1.0, 0.5, 1.0);
         }

         List var10 = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, var9);

         for (int var11 = 0; var11 < var10.size(); var11++) {
            Entity var12 = (Entity)var10.get(var11);
            if (!var12.isDead) {
               this.e(var12);
            }
         }
      }
   }

   private void e(Entity var1) {
      var1.onCollideWithPlayer(this);
   }

   public int getScore() {
      return this.dataWatcher.getWatchableObjectInt(18);
   }

   public void setScore(int var1) {
      this.dataWatcher.updateObject(18, var1);
   }

   public void addScore(int var1) {
      int var4 = this.getScore();
      this.dataWatcher.updateObject(18, var4 + var1);
   }

   @Override
   public void onDeath(DamageSource var1) {
      super.onDeath(var1);
      this.setSize(0.2F, 0.2F);
      this.setPosition(this.posX, this.posY, this.posZ);
      this.motionY = 0.1F;
      if (this.getName().equals("Notch")) {
         this.dropItem(new ItemStack(Items.apple, 1), true, false);
      }

      if (!this.worldObj.getGameRules().getBoolean("keepInventory")) {
         this.inventory.dropAllItems();
      }

      if (var1 != null) {
         this.motionX = -MathHelper.cos((this.attackedAtYaw + this.rotationYaw) * (float) Math.PI / 180.0F) * 0.1F;
         this.motionZ = -MathHelper.sin((this.attackedAtYaw + this.rotationYaw) * (float) Math.PI / 180.0F) * 0.1F;
      } else {
         this.motionX = this.motionZ = 0.0;
      }

      this.triggerAchievement(StatList.deathsStat);
      this.func_175145_a(StatList.timeSinceDeathStat);
   }

   @Override
   public String getHurtSound() {
      return "game.player.hurt";
   }

   @Override
   public String getDeathSound() {
      return "game.player.die";
   }

   @Override
   public void addToPlayerScore(Entity var1, int var2) {
      this.addScore(var2);
      Collection var6 = this.getWorldScoreboard().getObjectivesFromCriteria(IScoreObjectiveCriteria.totalKillCount);
      if (var1 instanceof EntityPlayer) {
         this.triggerAchievement(StatList.playerKillsStat);
         var6.addAll(this.getWorldScoreboard().getObjectivesFromCriteria(IScoreObjectiveCriteria.playerKillCount));
         var6.addAll(this.func_175137_e(var1));
      } else {
         this.triggerAchievement(StatList.mobKillsStat);
      }

      for (Object var8$raw : var6) {
         ScoreObjective var8 = (ScoreObjective)var8$raw;
         Score var9 = this.getWorldScoreboard().getValueFromObjective(this.getName(), var8);
         var9.func_96648_a();
      }
   }

   private Collection<ScoreObjective> func_175137_e(Entity var1) {
      ScorePlayerTeam var5 = this.getWorldScoreboard().getPlayersTeam(this.getName());
      if (var5 != null) {
         int var6 = var5.getChatFormat().getColorIndex();
         if (var6 >= 0 && var6 < IScoreObjectiveCriteria.field_178793_i.length) {
            for (ScoreObjective var8 : this.getWorldScoreboard().getObjectivesFromCriteria(IScoreObjectiveCriteria.field_178793_i[var6])) {
               Score var9 = this.getWorldScoreboard().getValueFromObjective(var1.getName(), var8);
               var9.func_96648_a();
            }
         }
      }

      ScorePlayerTeam var10 = this.getWorldScoreboard().getPlayersTeam(var1.getName());
      if (var10 != null) {
         int var11 = var10.getChatFormat().getColorIndex();
         if (var11 >= 0 && var11 < IScoreObjectiveCriteria.field_178792_h.length) {
            return this.getWorldScoreboard().getObjectivesFromCriteria(IScoreObjectiveCriteria.field_178792_h[var11]);
         }
      }

      return Lists.newArrayList();
   }

   public EntityItem dropOneItem(boolean var1) {
      return this.dropItem(
         this.inventory
            .decrStackSize(this.inventory.currentItem, var1 && this.inventory.getCurrentItem() != null ? this.inventory.getCurrentItem().stackSize : 1),
         false,
         true
      );
   }

   public EntityItem dropPlayerItemWithRandomChoice(ItemStack var1, boolean var2) {
      return this.dropItem(var1, false, false);
   }

   public EntityItem dropItem(ItemStack var1, boolean var2, boolean var3) {
      if (var1 == null) {
         return null;
      }

      if (var1.stackSize == 0) {
         return null;
      }

      double var7 = this.posY - 0.3F + this.getEyeHeight();
      EntityItem var9 = new EntityItem(this.worldObj, this.posX, var7, this.posZ, var1);
      var9.setPickupDelay(40);
      if (var3) {
         var9.setThrower(this.getName());
      }

      if (var2) {
         float var10 = this.rand.nextFloat() * 0.5F;
         float var11 = this.rand.nextFloat() * (float) Math.PI * 2.0F;
         var9.motionX = -MathHelper.sin(var11) * var10;
         var9.motionZ = MathHelper.cos(var11) * var10;
         var9.motionY = 0.2F;
      } else {
         float var12 = 0.3F;
         var9.motionX = -MathHelper.sin(this.rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float) Math.PI) * var12;
         var9.motionZ = MathHelper.cos(this.rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float) Math.PI) * var12;
         var9.motionY = -MathHelper.sin(this.rotationPitch / 180.0F * (float) Math.PI) * var12 + 0.1F;
         float var14 = this.rand.nextFloat() * (float) Math.PI * 2.0F;
         var12 = 0.02F * this.rand.nextFloat();
         var9.motionX = var9.motionX + Math.cos(var14) * var12;
         var9.motionY = var9.motionY + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.1F;
         var9.motionZ = var9.motionZ + Math.sin(var14) * var12;
      }

      this.joinEntityItemWithWorld(var9);
      if (var3) {
         this.triggerAchievement(StatList.dropStat);
      }

      return var9;
   }

   public void joinEntityItemWithWorld(EntityItem var1) {
      this.worldObj.spawnEntityInWorld(var1);
   }

   public float getToolDigEfficiency(Block var1) {
      float var5 = this.inventory.getStrVsBlock(var1);
      if (var5 > 1.0F) {
         int var6 = EnchantmentHelper.getEfficiencyModifier(this);
         ItemStack var7 = this.inventory.getCurrentItem();
         if (var6 > 0 && var7 != null) {
            var5 += var6 * var6 + 1;
         }
      }

      if (this.isPotionActive(Potion.digSpeed)) {
         var5 *= 1.0F + (this.getActivePotionEffect(Potion.digSpeed).getAmplifier() + 1) * 0.2F;
      }

      if (this.isPotionActive(Potion.digSlowdown)) {
         float var8 = 1.0F;
         switch (this.getActivePotionEffect(Potion.digSlowdown).getAmplifier()) {
            case 0:
               var8 = 0.3F;
               break;
            case 1:
               var8 = 0.09F;
               break;
            case 2:
               var8 = 0.0027F;
               break;
            case 3:
            default:
               var8 = 8.1E-4F;
         }

         var5 *= var8;
      }

      if (this.isInsideOfMaterial(Material.water) && !EnchantmentHelper.getAquaAffinityModifier(this)) {
         var5 /= 5.0F;
      }

      if (!this.onGround) {
         var5 /= 5.0F;
      }

      return var5;
   }

   public boolean canHarvestBlock(Block var1) {
      return this.inventory.canHeldItemHarvest(var1);
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound var1) {
      super.readEntityFromNBT(var1);
      this.entityUniqueID = getUUID(this.gameProfile);
      NBTTagList var5 = var1.getTagList("Inventory", 10);
      this.inventory.readFromNBT(var5);
      this.inventory.currentItem = var1.getInteger("SelectedItemSlot");
      this.sleeping = var1.getBoolean("Sleeping");
      this.sleepTimer = var1.getShort("SleepTimer");
      this.experience = var1.getFloat("XpP");
      this.experienceLevel = var1.getInteger("XpLevel");
      this.experienceTotal = var1.getInteger("XpTotal");
      this.xpSeed = var1.getInteger("XpSeed");
      if (this.xpSeed == 0) {
         this.xpSeed = this.rand.nextInt();
      }

      this.setScore(var1.getInteger("Score"));
      if (this.sleeping) {
         this.playerLocation = new BlockPos(this);
         this.wakeUpPlayer(true, true, false);
      }

      if (var1.hasKey("SpawnX", 99) && var1.hasKey("SpawnY", 99) && var1.hasKey("SpawnZ", 99)) {
         this.spawnChunk = new BlockPos(var1.getInteger("SpawnX"), var1.getInteger("SpawnY"), var1.getInteger("SpawnZ"));
         this.spawnForced = var1.getBoolean("SpawnForced");
      }

      this.foodStats.readNBT(var1);
      this.capabilities.readCapabilitiesFromNBT(var1);
      if (var1.hasKey("EnderItems", 9)) {
         NBTTagList var6 = var1.getTagList("EnderItems", 10);
         this.theInventoryEnderChest.loadInventoryFromNBT(var6);
      }
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound var1) {
      super.writeEntityToNBT(var1);
      var1.setTag("Inventory", this.inventory.writeToNBT(new NBTTagList()));
      var1.setInteger("SelectedItemSlot", this.inventory.currentItem);
      var1.setBoolean("Sleeping", this.sleeping);
      var1.setShort("SleepTimer", (short)this.sleepTimer);
      var1.setFloat("XpP", this.experience);
      var1.setInteger("XpLevel", this.experienceLevel);
      var1.setInteger("XpTotal", this.experienceTotal);
      var1.setInteger("XpSeed", this.xpSeed);
      var1.setInteger("Score", this.getScore());
      if (this.spawnChunk != null) {
         var1.setInteger("SpawnX", this.spawnChunk.getX());
         var1.setInteger("SpawnY", this.spawnChunk.getY());
         var1.setInteger("SpawnZ", this.spawnChunk.getZ());
         var1.setBoolean("SpawnForced", this.spawnForced);
      }

      this.foodStats.writeNBT(var1);
      this.capabilities.writeCapabilitiesToNBT(var1);
      var1.setTag("EnderItems", this.theInventoryEnderChest.saveInventoryToNBT());
      ItemStack var5 = this.inventory.getCurrentItem();
      if (var5 != null && var5.getItem() != null) {
         var1.setTag("SelectedItem", var5.writeToNBT(new NBTTagCompound()));
      }
   }

   @Override
   public boolean attackEntityFrom(DamageSource var1, float var2) {
      if (this.isEntityInvulnerable(var1)) {
         return false;
      }

      if (this.capabilities.disableDamage && !var1.canHarmInCreative()) {
         return false;
      }

      this.entityAge = 0;
      if (this.getHealth() <= 0.0F) {
         return false;
      }

      if (this.isPlayerSleeping() && !this.worldObj.isRemote) {
         this.wakeUpPlayer(true, true, false);
      }

      if (var1.isDifficultyScaled()) {
         if (this.worldObj.getDifficulty() == EnumDifficulty.PEACEFUL) {
            var2 = 0.0F;
         }

         if (this.worldObj.getDifficulty() == EnumDifficulty.EASY) {
            var2 = var2 / 2.0F + 1.0F;
         }

         if (this.worldObj.getDifficulty() == EnumDifficulty.HARD) {
            var2 = var2 * 3.0F / 2.0F;
         }
      }

      if (var2 == 0.0F) {
         return false;
      }

      Entity var6 = var1.getEntity();
      if (var6 instanceof EntityArrow && ((EntityArrow)var6).shootingEntity != null) {
         var6 = ((EntityArrow)var6).shootingEntity;
      }

      return super.attackEntityFrom(var1, var2);
   }

   public boolean canAttackPlayer(EntityPlayer var1) {
      Team var5 = this.getTeam();
      Team var6 = var1.getTeam();
      return var5 == null ? true : (!var5.isSameTeam(var6) ? true : var5.getAllowFriendlyFire());
   }

   @Override
   public void damageArmor(float var1) {
      this.inventory.damageArmor(var1);
   }

   @Override
   public int getTotalArmorValue() {
      return this.inventory.getTotalArmorValue();
   }

   public float getArmorVisibility() {
      int var4 = 0;

      for (ItemStack var8 : this.inventory.armorInventory) {
         if (var8 != null) {
            var4++;
         }
      }

      return (float)var4 / this.inventory.armorInventory.length;
   }

   @Override
   public void damageEntity(DamageSource var1, float var2) {
      if (!this.isEntityInvulnerable(var1)) {
         if (!var1.isUnblockable() && this.isBlocking() && var2 > 0.0F) {
            var2 = (1.0F + var2) * 0.5F;
         }

         var2 = this.applyArmorCalculations(var1, var2);
         var2 = this.applyPotionDamageCalculations(var1, var2);
         float var6 = var2;
         var2 = Math.max(var2 - this.getAbsorptionAmount(), 0.0F);
         this.setAbsorptionAmount(this.getAbsorptionAmount() - (var6 - var2));
         if (var2 != 0.0F) {
            this.addExhaustion(var1.getHungerDamage());
            float var7 = this.getHealth();
            this.setHealth(this.getHealth() - var2);
            this.getCombatTracker().trackDamage(var1, var7, var2);
            if (var2 < 3.4028235E37F) {
               this.addStat(StatList.damageTakenStat, Math.round(var2 * 10.0F));
            }
         }
      }
   }

   public void openEditSign(TileEntitySign var1) {
   }

   public void openEditCommandBlock(CommandBlockLogic var1) {
   }

   public void displayVillagerTradeGui(IMerchant var1) {
   }

   public void displayGUIChest(IInventory var1) {
   }

   public void displayGUIHorse(EntityHorse var1, IInventory var2) {
   }

   public void displayGui(IInteractionObject var1) {
   }

   public void displayGUIBook(ItemStack var1) {
   }

   public boolean interactWith(Entity var1) {
      if (this.isSpectator()) {
         if (var1 instanceof IInventory) {
            this.displayGUIChest((IInventory)var1);
         }

         return false;
      } else {
         ItemStack var5 = this.getCurrentEquippedItem();
         ItemStack var6 = var5 != null ? var5.copy() : null;
         if (!var1.interactFirst(this)) {
            if (var5 != null && var1 instanceof EntityLivingBase) {
               if (this.capabilities.isCreativeMode) {
                  var5 = var6;
               }

               if (var5.interactWithEntity(this, (EntityLivingBase)var1)) {
                  if (var5.stackSize <= 0 && !this.capabilities.isCreativeMode) {
                     this.destroyCurrentEquippedItem();
                  }

                  return true;
               }
            }

            return false;
         } else {
            if (var5 != null && var5 == this.getCurrentEquippedItem()) {
               if (var5.stackSize <= 0 && !this.capabilities.isCreativeMode) {
                  this.destroyCurrentEquippedItem();
               } else if (var5.stackSize < var6.stackSize && this.capabilities.isCreativeMode) {
                  var5.stackSize = var6.stackSize;
               }
            }

            return true;
         }
      }
   }

   public ItemStack getCurrentEquippedItem() {
      return this.inventory.getCurrentItem();
   }

   public void destroyCurrentEquippedItem() {
      this.inventory.setInventorySlotContents(this.inventory.currentItem, (ItemStack)null);
   }

   @Override
   public double getYOffset() {
      return -0.35;
   }

   public void attackTargetEntityWithCurrentItem(Entity var1) {
      long var2 = cb ^ 13779141946963L;
      long var4 = var2 ^ 61630202133243L;
      if (var1.canAttackWithItem() && !var1.hitByEntity(this)) {
         float var7 = (float)this.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue();
         int var8 = 0;
         float var9 = 0.0F;
         if (var1 instanceof EntityLivingBase) {
            var9 = EnchantmentHelper.getModifierForCreature(this.getHeldItem(), ((EntityLivingBase)var1).getCreatureAttribute());
         } else {
            var9 = EnchantmentHelper.getModifierForCreature(this.getHeldItem(), EnumCreatureAttribute.UNDEFINED);
         }

         var8 += EnchantmentHelper.getKnockbackModifier(this);
         if (this.isSprinting()) {
            var8++;
         }

         if (var7 > 0.0F || var9 > 0.0F) {
            boolean var10 = this.fallDistance > 0.0F
               && !this.onGround
               && !this.isOnLadder()
               && !this.isInWater()
               && !this.isPotionActive(Potion.blindness)
               && this.ridingEntity == null
               && var1 instanceof EntityLivingBase;
            if (var10 && var7 > 0.0F) {
               var7 *= 1.5F;
            }

            var7 += var9;
            boolean var11 = false;
            int var12 = EnchantmentHelper.getFireAspectModifier(this);
            if (var1 instanceof EntityLivingBase && var12 > 0 && !var1.isBurning()) {
               var11 = true;
               var1.setFire(1);
            }

            double var13 = var1.motionX;
            double var15 = var1.motionY;
            double var17 = var1.motionZ;
            boolean var19 = var1.attackEntityFrom(DamageSource.causePlayerDamage(this), var7);
            if (var19) {
               if (var8 > 0 && !Astolfo.INSTANCE.getModuleManager().get(KeepSprint.class).isEnabled()) {
                  var1.addVelocity(
                     -MathHelper.sin(this.rotationYaw * (float) Math.PI / 180.0F) * var8 * 0.5F,
                     0.1,
                     MathHelper.cos(this.rotationYaw * (float) Math.PI / 180.0F) * var8 * 0.5F
                  );
                  this.motionX *= 0.6;
                  this.motionZ *= 0.6;
                  this.setSprinting(false);
               }

               if (var1 instanceof EntityPlayerMP && var1.velocityChanged) {
                  ((EntityPlayerMP)var1).playerNetServerHandler.sendPacket(new S12PacketEntityVelocity(var1));
                  var1.velocityChanged = false;
                  var1.motionX = var13;
                  var1.motionY = var15;
                  var1.motionZ = var17;
               }

               if (var10) {
                  this.L(var1);
               }

               if (var9 > 0.0F) {
                  this.x(var1);
               }

               if (var7 >= 18.0F) {
                  this.triggerAchievement(AchievementList.overkill);
               }

               this.setLastAttacker(var1);
               if (var1 instanceof EntityLivingBase) {
                  EnchantmentHelper.applyThornEnchantments((EntityLivingBase)var1, this);
               }

               EnchantmentHelper.applyArthropodEnchantments(this, var1);
               ItemStack var20 = this.getCurrentEquippedItem();
               Entity var21 = var1;
               if (var1 instanceof EntityDragonPart) {
                  IEntityMultiPart var22 = ((EntityDragonPart)var1).entityDragonObj;
                  if (var22 instanceof EntityLivingBase) {
                     var21 = (EntityLivingBase)var22;
                  }
               }

               if (var20 != null && var21 instanceof EntityLivingBase) {
                  var20.hitEntity((EntityLivingBase)var21, this);
                  if (var20.stackSize <= 0) {
                     this.destroyCurrentEquippedItem();
                  }
               }

               if (var1 instanceof EntityLivingBase) {
                  this.addStat(StatList.damageDealtStat, Math.round(var7 * 10.0F));
                  if (var12 > 0) {
                     var1.setFire(var12 * 4);
                  }
               }

               this.addExhaustion(0.3F);
            } else if (var11) {
               var1.extinguish();
            }
         }
      }
   }

   public void L(Entity var1) {
   }

   public void x(Entity var1) {
   }

   public void respawnPlayer() {
   }

   @Override
   public void setDead() {
      super.setDead();
      this.inventoryContainer.onContainerClosed(this);
      if (this.openContainer != null) {
         this.openContainer.onContainerClosed(this);
      }
   }

   @Override
   public boolean isEntityInsideOpaqueBlock() {
      return !this.sleeping && super.isEntityInsideOpaqueBlock();
   }

   public boolean v() {
      return false;
   }

   public GameProfile getGameProfile() {
      return this.gameProfile;
   }

   public EntityPlayer.EnumStatus trySleep(BlockPos var1) {
      if (!this.worldObj.isRemote) {
         if (this.isPlayerSleeping() || !this.isEntityAlive()) {
            return EntityPlayer.EnumStatus.OTHER_PROBLEM;
         }

         if (!this.worldObj.provider.isSurfaceWorld()) {
            return EntityPlayer.EnumStatus.NOT_POSSIBLE_HERE;
         }

         if (this.worldObj.isDaytime()) {
            return EntityPlayer.EnumStatus.NOT_POSSIBLE_NOW;
         }

         if (Math.abs(this.posX - var1.getX()) > 3.0 || Math.abs(this.posY - var1.getY()) > 2.0 || Math.abs(this.posZ - var1.getZ()) > 3.0) {
            return EntityPlayer.EnumStatus.TOO_FAR_AWAY;
         }

         double var5 = 8.0;
         double var7 = 5.0;
         List var9 = this.worldObj
            .getEntitiesWithinAABB(
               EntityMob.class,
               new AxisAlignedBB(var1.getX() - var5, var1.getY() - var7, var1.getZ() - var5, var1.getX() + var5, var1.getY() + var7, var1.getZ() + var5)
            );
         if (!var9.isEmpty()) {
            return EntityPlayer.EnumStatus.NOT_SAFE;
         }
      }

      if (this.E()) {
         this.mountEntity((Entity)null);
      }

      this.setSize(0.2F, 0.2F);
      if (this.worldObj.isBlockLoaded(var1)) {
         EnumFacing var10 = this.worldObj.getBlockState(var1).getValue(BlockDirectional.FACING);
         float var6 = 0.5F;
         float var11 = 0.5F;
         switch (var10) {
            case SOUTH:
               var11 = 0.9F;
               break;
            case NORTH:
               var11 = 0.1F;
               break;
            case WEST:
               var6 = 0.1F;
               break;
            case EAST:
               var6 = 0.9F;
         }

         this.func_175139_a(var10);
         this.setPosition(var1.getX() + var6, var1.getY() + 0.6875F, var1.getZ() + var11);
      } else {
         this.setPosition(var1.getX() + 0.5F, var1.getY() + 0.6875F, var1.getZ() + 0.5F);
      }

      this.sleeping = true;
      this.sleepTimer = 0;
      this.playerLocation = var1;
      this.motionX = this.motionZ = this.motionY = 0.0;
      if (!this.worldObj.isRemote) {
         this.worldObj.updateAllPlayersSleepingFlag();
      }

      return EntityPlayer.EnumStatus.OK;
   }

   private void func_175139_a(EnumFacing var1) {
      this.renderOffsetX = 0.0F;
      this.renderOffsetZ = 0.0F;
      switch (var1) {
         case SOUTH:
            this.renderOffsetZ = -1.8F;
            break;
         case NORTH:
            this.renderOffsetZ = 1.8F;
            break;
         case WEST:
            this.renderOffsetX = 1.8F;
            break;
         case EAST:
            this.renderOffsetX = -1.8F;
      }
   }

   public void wakeUpPlayer(boolean var1, boolean var2, boolean var3) {
      this.setSize(0.6F, 1.8F);
      IBlockState var7 = this.worldObj.getBlockState(this.playerLocation);
      if (this.playerLocation != null && var7.getBlock() == Blocks.bed) {
         this.worldObj.setBlockState(this.playerLocation, var7.withProperty(BlockBed.OCCUPIED, false), 4);
         BlockPos var8 = BlockBed.getSafeExitLocation(this.worldObj, this.playerLocation, 0);
         if (var8 == null) {
            var8 = this.playerLocation.up();
         }

         this.setPosition(var8.getX() + 0.5F, var8.getY() + 0.1F, var8.getZ() + 0.5F);
      }

      this.sleeping = false;
      if (!this.worldObj.isRemote && var2) {
         this.worldObj.updateAllPlayersSleepingFlag();
      }

      this.sleepTimer = var1 ? 0 : 100;
      if (var3) {
         this.setSpawnPoint(this.playerLocation, false);
      }
   }

   private boolean isInBed() {
      return this.worldObj.getBlockState(this.playerLocation).getBlock() == Blocks.bed;
   }

   public static BlockPos getBedSpawnLocation(World var0, BlockPos var1, boolean var2) {
      Block var6 = var0.getBlockState(var1).getBlock();
      if (var6 != Blocks.bed) {
         if (!var2) {
            return null;
         }

         boolean var7 = var6.canSpawnInBlock();
         boolean var8 = var0.getBlockState(var1.up()).getBlock().canSpawnInBlock();
         return var7 && var8 ? var1 : null;
      } else {
         return BlockBed.getSafeExitLocation(var0, var1, 0);
      }
   }

   public float getBedOrientationInDegrees() {
      if (this.playerLocation != null) {
         EnumFacing var4 = this.worldObj.getBlockState(this.playerLocation).getValue(BlockDirectional.FACING);
         switch (var4) {
            case SOUTH:
               return 90.0F;
            case NORTH:
               return 270.0F;
            case WEST:
               return 0.0F;
            case EAST:
               return 180.0F;
         }
      }

      return 0.0F;
   }

   @Override
   public boolean isPlayerSleeping() {
      return this.sleeping;
   }

   public boolean isPlayerFullyAsleep() {
      return this.sleeping && this.sleepTimer >= 100;
   }

   public int c() {
      return this.sleepTimer;
   }

   public void addChatComponentMessage(IChatComponent var1) {
   }

   public BlockPos getBedLocation() {
      return this.spawnChunk;
   }

   public boolean S() {
      return this.spawnForced;
   }

   public void setSpawnPoint(BlockPos var1, boolean var2) {
      if (var1 != null) {
         this.spawnChunk = var1;
         this.spawnForced = var2;
      } else {
         this.spawnChunk = null;
         this.spawnForced = false;
      }
   }

   public void triggerAchievement(StatBase var1) {
      this.addStat(var1, 1);
   }

   public void addStat(StatBase var1, int var2) {
   }

   public void func_175145_a(StatBase var1) {
   }

   @Override
   public void jump() {
      JumpEvent var4 = new JumpEvent();
      Astolfo.INSTANCE.getEventBus().post(var4);
      if (!var4.isCancelled()) {
         super.jump();
      }

      this.triggerAchievement(StatList.jumpStat);
      if (this.isSprinting()) {
         this.addExhaustion(0.8F);
      } else {
         this.addExhaustion(0.2F);
      }
   }

   @Override
   public void moveEntityWithHeading(float var1, float var2) {
      double var6 = this.posX;
      double var8 = this.posY;
      double var10 = this.posZ;
      if (this.capabilities.isFlying && this.ridingEntity == null) {
         double var12 = this.motionY;
         float var14 = this.jumpMovementFactor;
         this.jumpMovementFactor = this.capabilities.getFlySpeed() * (this.isSprinting() ? 2 : 1);
         super.moveEntityWithHeading(var1, var2);
         this.motionY = var12 * 0.6;
         this.jumpMovementFactor = var14;
      } else {
         super.moveEntityWithHeading(var1, var2);
      }

      this.addMovementStat(this.posX - var6, this.posY - var8, this.posZ - var10);
   }

   @Override
   public float getAIMoveSpeed() {
      return (float)this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getAttributeValue();
   }

   public void addMovementStat(double var1, double var3, double var5) {
      if (this.ridingEntity == null) {
         if (this.isInsideOfMaterial(Material.water)) {
            int var10 = Math.round(MathHelper.sqrt_double(var1 * var1 + var3 * var3 + var5 * var5) * 100.0F);
            if (var10 > 0) {
               this.addStat(StatList.distanceDoveStat, var10);
               this.addExhaustion(0.015F * var10 * 0.01F);
            }
         } else if (this.isInWater()) {
            int var11 = Math.round(MathHelper.sqrt_double(var1 * var1 + var5 * var5) * 100.0F);
            if (var11 > 0) {
               this.addStat(StatList.distanceSwumStat, var11);
               this.addExhaustion(0.015F * var11 * 0.01F);
            }
         } else if (this.isOnLadder()) {
            if (var3 > 0.0) {
               this.addStat(StatList.distanceClimbedStat, (int)Math.round(var3 * 100.0));
            }
         } else if (this.onGround) {
            int var12 = Math.round(MathHelper.sqrt_double(var1 * var1 + var5 * var5) * 100.0F);
            if (var12 > 0) {
               this.addStat(StatList.distanceWalkedStat, var12);
               if (this.isSprinting()) {
                  this.addStat(StatList.distanceSprintedStat, var12);
                  this.addExhaustion(0.099999994F * var12 * 0.01F);
               } else {
                  if (this.isSneaking()) {
                     this.addStat(StatList.distanceCrouchedStat, var12);
                  }

                  this.addExhaustion(0.01F * var12 * 0.01F);
               }
            }
         } else {
            int var13 = Math.round(MathHelper.sqrt_double(var1 * var1 + var5 * var5) * 100.0F);
            if (var13 > 25) {
               this.addStat(StatList.distanceFlownStat, var13);
            }
         }
      }
   }

   private void addMountedMovementStat(double var1, double var3, double var5) {
      if (this.ridingEntity != null) {
         int var10 = Math.round(MathHelper.sqrt_double(var1 * var1 + var3 * var3 + var5 * var5) * 100.0F);
         if (var10 > 0) {
            if (this.ridingEntity instanceof EntityMinecart) {
               this.addStat(StatList.distanceByMinecartStat, var10);
               if (this.startMinecartRidingCoordinate == null) {
                  this.startMinecartRidingCoordinate = new BlockPos(this);
               } else if (this.startMinecartRidingCoordinate
                     .distanceSq(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ))
                  >= 1000000.0) {
                  this.triggerAchievement(AchievementList.onARail);
               }
            } else if (this.ridingEntity instanceof EntityBoat) {
               this.addStat(StatList.distanceByBoatStat, var10);
            } else if (this.ridingEntity instanceof EntityPig) {
               this.addStat(StatList.distanceByPigStat, var10);
            } else if (this.ridingEntity instanceof EntityHorse) {
               this.addStat(StatList.distanceByHorseStat, var10);
            }
         }
      }
   }

   @Override
   public void fall(float var1, float var2) {
      if (!this.capabilities.allowFlying) {
         if (var1 >= 2.0F) {
            this.addStat(StatList.distanceFallenStat, (int)Math.round(var1 * 100.0));
         }

         super.fall(var1, var2);
      }
   }

   @Override
   public void resetHeight() {
      if (!this.isSpectator()) {
         super.resetHeight();
      }
   }

   @Override
   public String getFallSoundString(int var1) {
      return var1 > 4 ? "game.player.hurt.fall.big" : "game.player.hurt.fall.small";
   }

   @Override
   public void onKillEntity(EntityLivingBase var1) {
      if (var1 instanceof IMob) {
         this.triggerAchievement(AchievementList.killEnemy);
      }

      EntityList.EntityEggInfo var5 = EntityList.entityEggs.get(EntityList.getEntityID(var1));
      if (var5 != null) {
         this.triggerAchievement(var5.field_151512_d);
      }
   }

   @Override
   public void setInWeb() {
      if (!this.capabilities.isFlying) {
         super.setInWeb();
      }
   }

   @Override
   public ItemStack getCurrentArmor(int var1) {
      return this.inventory.armorItemInSlot(var1);
   }

   public void addExperience(int var1) {
      this.addScore(var1);
      int var5 = Integer.MAX_VALUE - this.experienceTotal;
      if (var1 > var5) {
         var1 = var5;
      }

      this.experience = this.experience + (float)var1 / this.xpBarCap();

      for (this.experienceTotal += var1; this.experience >= 1.0F; this.experience = this.experience / this.xpBarCap()) {
         this.experience = (this.experience - 1.0F) * this.xpBarCap();
         this.addExperienceLevel(1);
      }
   }

   public int getXPSeed() {
      return this.xpSeed;
   }

   public void removeExperienceLevel(int var1) {
      this.experienceLevel -= var1;
      if (this.experienceLevel < 0) {
         this.experienceLevel = 0;
         this.experience = 0.0F;
         this.experienceTotal = 0;
      }

      this.xpSeed = this.rand.nextInt();
   }

   public void addExperienceLevel(int var1) {
      this.experienceLevel += var1;
      if (this.experienceLevel < 0) {
         this.experienceLevel = 0;
         this.experience = 0.0F;
         this.experienceTotal = 0;
      }

      if (var1 > 0 && this.experienceLevel % 5 == 0 && this.lastXPSound < this.ticksExisted - 100.0F) {
         float var5 = this.experienceLevel > 30 ? 1.0F : this.experienceLevel / 30.0F;
         this.worldObj.playSoundAtEntity(this, "random.levelup", var5 * 0.75F, 1.0F);
         this.lastXPSound = this.ticksExisted;
      }
   }

   public int xpBarCap() {
      return this.experienceLevel >= 30
         ? 112 + (this.experienceLevel - 30) * 9
         : (this.experienceLevel >= 15 ? 37 + (this.experienceLevel - 15) * 5 : 7 + this.experienceLevel * 2);
   }

   public void addExhaustion(float var1) {
      if (!this.capabilities.disableDamage && !this.worldObj.isRemote) {
         this.foodStats.addExhaustion(var1);
      }
   }

   public FoodStats getFoodStats() {
      return this.foodStats;
   }

   public boolean canEat(boolean var1) {
      return (var1 || this.foodStats.needFood()) && !this.capabilities.disableDamage;
   }

   public boolean shouldHeal() {
      return this.getHealth() > 0.0F && this.getHealth() < this.getMaxHealth();
   }

   public void setItemInUse(ItemStack var1, int var2) {
      if (var1 != this.itemInUse) {
         this.itemInUse = var1;
         this.itemInUseCount = var2;
         if (!this.worldObj.isRemote) {
            this.setEating(true);
         }
      }
   }

   public boolean isAllowEdit() {
      return this.capabilities.allowEdit;
   }

   public boolean canPlayerEdit(BlockPos var1, EnumFacing var2, ItemStack var3) {
      if (this.capabilities.allowEdit) {
         return true;
      }

      if (var3 == null) {
         return false;
      }

      BlockPos var7 = var1.offset(var2.getOpposite());
      Block var8 = this.worldObj.getBlockState(var7).getBlock();
      return var3.canPlaceOn(var8) || var3.canEditBlocks();
   }

   @Override
   public int getExperiencePoints(EntityPlayer var1) {
      if (this.worldObj.getGameRules().getBoolean("keepInventory")) {
         return 0;
      }

      int var5 = this.experienceLevel * 7;
      return var5 > 100 ? 100 : var5;
   }

   @Override
   public boolean isPlayer() {
      return true;
   }

   @Override
   public boolean getAlwaysRenderNameTagForRender() {
      return true;
   }

   public void clonePlayer(EntityPlayer var1, boolean var2) {
      if (var2) {
         this.inventory.copyInventory(var1.inventory);
         this.setHealth(var1.getHealth());
         this.foodStats = var1.foodStats;
         this.experienceLevel = var1.experienceLevel;
         this.experienceTotal = var1.experienceTotal;
         this.experience = var1.experience;
         this.setScore(var1.getScore());
         this.lastPortalPos = var1.lastPortalPos;
         this.lastPortalVec = var1.lastPortalVec;
         this.teleportDirection = var1.teleportDirection;
      } else if (this.worldObj.getGameRules().getBoolean("keepInventory")) {
         this.inventory.copyInventory(var1.inventory);
         this.experienceLevel = var1.experienceLevel;
         this.experienceTotal = var1.experienceTotal;
         this.experience = var1.experience;
         this.setScore(var1.getScore());
      }

      this.xpSeed = var1.xpSeed;
      this.theInventoryEnderChest = var1.theInventoryEnderChest;
      this.getDataWatcher().updateObject(10, var1.getDataWatcher().getWatchableObjectByte(10));
   }

   @Override
   public boolean canTriggerWalking() {
      return !this.capabilities.isFlying;
   }

   public void sendPlayerAbilities() {
   }

   public void setGameType(WorldSettings.GameType var1) {
   }

   @Override
   public String getName() {
      return this.gameProfile.getName();
   }

   public InventoryEnderChest getInventoryEnderChest() {
      return this.theInventoryEnderChest;
   }

   @Override
   public ItemStack getEquipmentInSlot(int var1) {
      return var1 == 0 ? this.inventory.getCurrentItem() : this.inventory.armorInventory[var1 - 1];
   }

   @Override
   public ItemStack getHeldItem() {
      return this.inventory.getCurrentItem();
   }

   @Override
   public void setCurrentItemOrArmor(int var1, ItemStack var2) {
      this.inventory.armorInventory[var1] = var2;
   }

   @Override
   public boolean isInvisibleToPlayer(EntityPlayer var1) {
      if (!this.isInvisible()) {
         return false;
      }

      if (var1.isSpectator()) {
         return false;
      }

      Team var5 = this.getTeam();
      return var5 == null || var1 == null || var1.getTeam() != var5 || !var5.getSeeFriendlyInvisiblesEnabled();
   }

   public abstract boolean isSpectator();

   @Override
   public ItemStack[] getInventory() {
      return this.inventory.armorInventory;
   }

   @Override
   public boolean isPushedByWater() {
      return !this.capabilities.isFlying;
   }

   public Scoreboard getWorldScoreboard() {
      return this.worldObj.getScoreboard();
   }

   @Override
   public Team getTeam() {
      return this.getWorldScoreboard().getPlayersTeam(this.getName());
   }

   @Override
   public IChatComponent getDisplayName() {
      ChatComponentText var3 = new ChatComponentText(ScorePlayerTeam.formatPlayerName(this.getTeam(), this.getName()));
      var3.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/msg " + this.getName() + " "));
      var3.getChatStyle().setChatHoverEvent(this.getHoverEvent());
      var3.getChatStyle().setInsertion(this.getName());
      return var3;
   }

   @Override
   public float getEyeHeight() {
      float var4 = 1.62F;
      if (this.isPlayerSleeping()) {
         var4 = 0.2F;
      }

      if (this.isSneaking()) {
         var4 -= 0.08F;
      }

      return var4;
   }

   @Override
   public void setAbsorptionAmount(float var1) {
      if (var1 < 0.0F) {
         var1 = 0.0F;
      }

      this.getDataWatcher().updateObject(17, var1);
   }

   @Override
   public float getAbsorptionAmount() {
      return this.getDataWatcher().getWatchableObjectFloat(17);
   }

   public static UUID getUUID(GameProfile var0) {
      UUID var4 = var0.getId();
      if (var4 == null) {
         var4 = getOfflineUUID(var0.getName());
      }

      return var4;
   }

   public static UUID getOfflineUUID(String var0) {
      return UUID.nameUUIDFromBytes(("OfflinePlayer:" + var0).getBytes(Charsets.UTF_8));
   }

   public boolean canOpen(LockCode var1) {
      if (var1.isEmpty()) {
         return true;
      }

      ItemStack var5 = this.getCurrentEquippedItem();
      return var5 != null && var5.hasDisplayName() ? var5.getDisplayName().equals(var1.getLock()) : false;
   }

   public boolean isWearing(EnumPlayerModelParts var1) {
      long var2 = cb ^ 1222546604171L;
      long var4 = var2 ^ 110721784228202L;
      return Astolfo.INSTANCE.getUserManager().g(this.getName()) != null
         ? true
         : (this.getDataWatcher().getWatchableObjectByte(10) & var1.getPartMask()) == var1.getPartMask();
   }

   @Override
   public boolean sendCommandFeedback() {
      return MinecraftServer.getServer().worldServers[0].getGameRules().getBoolean("sendCommandFeedback");
   }

   @Override
   public boolean replaceItemInInventory(int var1, ItemStack var2) {
      if (var1 >= 0 && var1 < this.inventory.mainInventory.length) {
         this.inventory.setInventorySlotContents(var1, var2);
         return true;
      }

      int var6 = var1 - 100;
      if (var6 >= 0 && var6 < this.inventory.armorInventory.length) {
         int var8 = var6 + 1;
         if (var2 != null && var2.getItem() != null) {
            if (var2.getItem() instanceof ItemArmor) {
               if (EntityLiving.getArmorPosition(var2) != var8) {
                  return false;
               }
            } else if (var8 != 4 || var2.getItem() != Items.skull && !(var2.getItem() instanceof ItemBlock)) {
               return false;
            }
         }

         this.inventory.setInventorySlotContents(var6 + this.inventory.mainInventory.length, var2);
         return true;
      } else {
         int var7 = var1 - 200;
         if (var7 >= 0 && var7 < this.theInventoryEnderChest.getSizeInventory()) {
            this.theInventoryEnderChest.setInventorySlotContents(var7, var2);
            return true;
         } else {
            return false;
         }
      }
   }

   public boolean w() {
      return this.hasReducedDebug;
   }

   public void setReducedDebug(boolean var1) {
      this.hasReducedDebug = var1;
   }

   public enum EnumChatVisibility {
      FULL(0, "options.chat.visibility.full"),
      SYSTEM(1, "options.chat.visibility.system"),
      HIDDEN(2, "options.chat.visibility.hidden");

      private static final EntityPlayer.EnumChatVisibility[] ID_LOOKUP = new EntityPlayer.EnumChatVisibility[values().length];
      private final int chatVisibility;
      private final String resourceKey;

      EnumChatVisibility(int var3, String var4) {
         this.chatVisibility = var3;
         this.resourceKey = var4;
      }

      public int getChatVisibility() {
         return this.chatVisibility;
      }

      public static EntityPlayer.EnumChatVisibility getEnumChatVisibility(int var0) {
         return ID_LOOKUP[var0 % ID_LOOKUP.length];
      }

      public String getResourceKey() {
         return this.resourceKey;
      }

      static {
         for (EntityPlayer.EnumChatVisibility var14 : values()) {
            ID_LOOKUP[var14.chatVisibility] = var14;
         }
      }
   }

   public enum EnumStatus {
      OK,
      NOT_POSSIBLE_HERE,
      NOT_POSSIBLE_NOW,
      TOO_FAR_AWAY,
      OTHER_PROBLEM,
      NOT_SAFE;
   }
}
