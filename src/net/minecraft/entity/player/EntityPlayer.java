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

   public EntityPlayer(World worldIn, GameProfile gameProfileIn) {
      super(worldIn);
      this.entityUniqueID = getUUID(gameProfileIn);
      this.gameProfile = gameProfileIn;
      this.inventoryContainer = new ContainerPlayer(this.inventory, !worldIn.isRemote, this);
      this.openContainer = this.inventoryContainer;
      BlockPos blockPos = worldIn.getSpawnPoint();
      this.setLocationAndAngles(blockPos.getX() + 0.5, blockPos.getY() + 1, blockPos.getZ() + 0.5, 0.0F, 0.0F);
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
         ItemStack itemStack = this.inventory.getCurrentItem();
         if (itemStack == this.itemInUse) {
            if (this.itemInUseCount <= 25 && this.itemInUseCount % 4 == 0) {
               this.updateItemUse(itemStack, 5);
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
      double d = this.posX - this.chasingPosX;
      double e = this.posY - this.chasingPosY;
      double d2 = this.posZ - this.chasingPosZ;
      double d3 = 10.0;
      if (d > d3) {
         this.prevChasingPosX = this.chasingPosX = this.posX;
      }

      if (d2 > d3) {
         this.prevChasingPosZ = this.chasingPosZ = this.posZ;
      }

      if (e > d3) {
         this.prevChasingPosY = this.chasingPosY = this.posY;
      }

      if (d < -d3) {
         this.prevChasingPosX = this.chasingPosX = this.posX;
      }

      if (d2 < -d3) {
         this.prevChasingPosZ = this.chasingPosZ = this.posZ;
      }

      if (e < -d3) {
         this.prevChasingPosY = this.chasingPosY = this.posY;
      }

      this.chasingPosX += d * 0.25;
      this.chasingPosZ += d2 * 0.25;
      this.chasingPosY += e * 0.25;
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

      double d4 = MathHelper.clamp_double(this.posX, -2.9999999E7, 2.9999999E7);
      double d5 = MathHelper.clamp_double(this.posZ, -2.9999999E7, 2.9999999E7);
      if (d4 != this.posX || d5 != this.posZ) {
         this.setPosition(d4, this.posY, d5);
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
   public void playSound(String name, float volume, float pitch) {
      this.worldObj.playSoundToNearExcept(this, name, volume, pitch);
   }

   public void updateItemUse(ItemStack itemStackIn, int i) {
      if (itemStackIn.getItemUseAction() == EnumAction.DRINK) {
         this.playSound("random.drink", 0.5F, this.worldObj.rand.nextFloat() * 0.1F + 0.9F);
      }

      if (itemStackIn.getItemUseAction() == EnumAction.EAT) {
         for (int j = 0; j < i; j++) {
            Vec3 vec3 = new Vec3((this.rand.nextFloat() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, 0.0);
            vec3 = vec3.rotatePitch(-this.rotationPitch * (float) Math.PI / 180.0F);
            vec3 = vec3.rotateYaw(-this.rotationYaw * (float) Math.PI / 180.0F);
            double d = -this.rand.nextFloat() * 0.6 - 0.3;
            Vec3 vec32 = new Vec3((this.rand.nextFloat() - 0.5) * 0.3, d, 0.6);
            vec32 = vec32.rotatePitch(-this.rotationPitch * (float) Math.PI / 180.0F);
            vec32 = vec32.rotateYaw(-this.rotationYaw * (float) Math.PI / 180.0F);
            vec32 = vec32.addVector(this.posX, this.posY + this.getEyeHeight(), this.posZ);
            if (itemStackIn.getHasSubtypes()) {
               this.worldObj
                  .spawnParticle(
                     EnumParticleTypes.ITEM_CRACK,
                     vec32.xCoord,
                     vec32.yCoord,
                     vec32.zCoord,
                     vec3.xCoord,
                     vec3.yCoord + 0.05,
                     vec3.zCoord,
                     Item.getIdFromItem(itemStackIn.getItem()),
                     itemStackIn.getMetadata()
                  );
            } else {
               this.worldObj
                  .spawnParticle(
                     EnumParticleTypes.ITEM_CRACK,
                     vec32.xCoord,
                     vec32.yCoord,
                     vec32.zCoord,
                     vec3.xCoord,
                     vec3.yCoord + 0.05,
                     vec3.zCoord,
                     Item.getIdFromItem(itemStackIn.getItem())
                  );
            }
         }

         this.playSound("random.eat", 0.5F + 0.5F * this.rand.nextInt(2), (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
      }
   }

   public void onItemUseFinish() {
      if (this.itemInUse != null) {
         this.updateItemUse(this.itemInUse, 16);
         int i = this.itemInUse.stackSize;
         ItemStack itemStack = this.itemInUse.onItemUseFinish(this.worldObj, this);
         if (itemStack != this.itemInUse || itemStack != null && itemStack.stackSize != i) {
            this.inventory.mainInventory[this.inventory.currentItem] = itemStack;
            if (itemStack.stackSize == 0) {
               this.inventory.mainInventory[this.inventory.currentItem] = null;
            }
         }

         this.clearItemInUse();
      }
   }

   @Override
   public void handleStatusUpdate(byte id) {
      if (id == 9) {
         this.onItemUseFinish();
      } else if (id == 23) {
         this.hasReducedDebug = false;
      } else if (id == 22) {
         this.hasReducedDebug = true;
      } else {
         super.handleStatusUpdate(id);
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
         double d = this.posX;
         double e = this.posY;
         double d2 = this.posZ;
         float f = this.rotationYaw;
         float g = this.rotationPitch;
         super.updateRidden();
         this.prevCameraYaw = this.cameraYaw;
         this.cameraYaw = 0.0F;
         this.addMountedMovementStat(this.posX - d, this.posY - e, this.posZ - d2);
         if (this.ridingEntity instanceof EntityPig) {
            this.rotationPitch = g;
            this.rotationYaw = f;
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
      IAttributeInstance iAttributeInstance = this.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
      if (!this.worldObj.isRemote) {
         iAttributeInstance.setBaseValue(this.capabilities.getWalkSpeed());
      }

      this.jumpMovementFactor = this.speedInAir;
      if (this.isSprinting()) {
         this.jumpMovementFactor = (float)(this.jumpMovementFactor + this.speedInAir * 0.3);
      }

      this.setAIMoveSpeed((float)iAttributeInstance.getAttributeValue());
      float f = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
      float g = (float)(Math.atan(-this.motionY * 0.2F) * 15.0);
      if (f > 0.1F) {
         f = 0.1F;
      }

      if ((!this.onGround || this.getHealth() <= 0.0F)
         && this == Minecraft.getMinecraft().thePlayer
         && (
            !Astolfo.INSTANCE.getModuleManager().get(Flight.class).isEnabled()
               || !Flight.bobbing.getValue() && Astolfo.INSTANCE.getModuleManager().get(Flight.class).isEnabled()
         )) {
         f = 0.0F;
      }

      if (this.onGround || this.getHealth() <= 0.0F) {
         g = 0.0F;
      }

      this.cameraYaw = this.cameraYaw + (f - this.cameraYaw) * 0.4F;
      this.cameraPitch = this.cameraPitch + (g - this.cameraPitch) * 0.8F;
      if (this.getHealth() > 0.0F && !this.isSpectator()) {
         AxisAlignedBB axisAlignedBB = null;
         if (this.ridingEntity != null && !this.ridingEntity.isDead) {
            axisAlignedBB = this.getEntityBoundingBox().union(this.ridingEntity.getEntityBoundingBox()).expand(1.0, 0.0, 1.0);
         } else {
            axisAlignedBB = this.getEntityBoundingBox().expand(1.0, 0.5, 1.0);
         }

         List list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, axisAlignedBB);

         for (int i = 0; i < list.size(); i++) {
            Entity entity = (Entity)list.get(i);
            if (!entity.isDead) {
               this.e(entity);
            }
         }
      }
   }

   private void e(Entity entity) {
      entity.onCollideWithPlayer(this);
   }

   public int getScore() {
      return this.dataWatcher.getWatchableObjectInt(18);
   }

   public void setScore(int i) {
      this.dataWatcher.updateObject(18, i);
   }

   public void addScore(int i) {
      int j = this.getScore();
      this.dataWatcher.updateObject(18, j + i);
   }

   @Override
   public void onDeath(DamageSource cause) {
      super.onDeath(cause);
      this.setSize(0.2F, 0.2F);
      this.setPosition(this.posX, this.posY, this.posZ);
      this.motionY = 0.1F;
      if (this.getName().equals("Notch")) {
         this.dropItem(new ItemStack(Items.apple, 1), true, false);
      }

      if (!this.worldObj.getGameRules().getBoolean("keepInventory")) {
         this.inventory.dropAllItems();
      }

      if (cause != null) {
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
   public void addToPlayerScore(Entity entityIn, int amount) {
      this.addScore(amount);
      Collection collection = this.getWorldScoreboard().getObjectivesFromCriteria(IScoreObjectiveCriteria.totalKillCount);
      if (entityIn instanceof EntityPlayer) {
         this.triggerAchievement(StatList.playerKillsStat);
         collection.addAll(this.getWorldScoreboard().getObjectivesFromCriteria(IScoreObjectiveCriteria.playerKillCount));
         collection.addAll(this.func_175137_e(entityIn));
      } else {
         this.triggerAchievement(StatList.mobKillsStat);
      }

      for (Object object : collection) {
         ScoreObjective scoreObjective = (ScoreObjective)object;
         Score score = this.getWorldScoreboard().getValueFromObjective(this.getName(), scoreObjective);
         score.func_96648_a();
      }
   }

   private Collection<ScoreObjective> func_175137_e(Entity entity) {
      ScorePlayerTeam scorePlayerTeam = this.getWorldScoreboard().getPlayersTeam(this.getName());
      if (scorePlayerTeam != null) {
         int i = scorePlayerTeam.getChatFormat().getColorIndex();
         if (i >= 0 && i < IScoreObjectiveCriteria.field_178793_i.length) {
            for (ScoreObjective scoreObjective : this.getWorldScoreboard().getObjectivesFromCriteria(IScoreObjectiveCriteria.field_178793_i[i])) {
               Score score = this.getWorldScoreboard().getValueFromObjective(entity.getName(), scoreObjective);
               score.func_96648_a();
            }
         }
      }

      ScorePlayerTeam scorePlayerTeam2 = this.getWorldScoreboard().getPlayersTeam(entity.getName());
      if (scorePlayerTeam2 != null) {
         int j = scorePlayerTeam2.getChatFormat().getColorIndex();
         if (j >= 0 && j < IScoreObjectiveCriteria.field_178792_h.length) {
            return this.getWorldScoreboard().getObjectivesFromCriteria(IScoreObjectiveCriteria.field_178792_h[j]);
         }
      }

      return Lists.newArrayList();
   }

   public EntityItem dropOneItem(boolean dropAll) {
      return this.dropItem(
         this.inventory
            .decrStackSize(this.inventory.currentItem, dropAll && this.inventory.getCurrentItem() != null ? this.inventory.getCurrentItem().stackSize : 1),
         false,
         true
      );
   }

   public EntityItem dropPlayerItemWithRandomChoice(ItemStack itemStackIn, boolean unused) {
      return this.dropItem(itemStackIn, false, false);
   }

   public EntityItem dropItem(ItemStack droppedItem, boolean dropAround, boolean traceItem) {
      if (droppedItem == null) {
         return null;
      }

      if (droppedItem.stackSize == 0) {
         return null;
      }

      double d = this.posY - 0.3F + this.getEyeHeight();
      EntityItem entityItem = new EntityItem(this.worldObj, this.posX, d, this.posZ, droppedItem);
      entityItem.setPickupDelay(40);
      if (traceItem) {
         entityItem.setThrower(this.getName());
      }

      if (dropAround) {
         float f = this.rand.nextFloat() * 0.5F;
         float g = this.rand.nextFloat() * (float) Math.PI * 2.0F;
         entityItem.motionX = -MathHelper.sin(g) * f;
         entityItem.motionZ = MathHelper.cos(g) * f;
         entityItem.motionY = 0.2F;
      } else {
         float h = 0.3F;
         entityItem.motionX = -MathHelper.sin(this.rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float) Math.PI) * h;
         entityItem.motionZ = MathHelper.cos(this.rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float) Math.PI) * h;
         entityItem.motionY = -MathHelper.sin(this.rotationPitch / 180.0F * (float) Math.PI) * h + 0.1F;
         float f2 = this.rand.nextFloat() * (float) Math.PI * 2.0F;
         h = 0.02F * this.rand.nextFloat();
         entityItem.motionX = entityItem.motionX + Math.cos(f2) * h;
         entityItem.motionY = entityItem.motionY + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.1F;
         entityItem.motionZ = entityItem.motionZ + Math.sin(f2) * h;
      }

      this.joinEntityItemWithWorld(entityItem);
      if (traceItem) {
         this.triggerAchievement(StatList.dropStat);
      }

      return entityItem;
   }

   public void joinEntityItemWithWorld(EntityItem itemIn) {
      this.worldObj.spawnEntityInWorld(itemIn);
   }

   public float getToolDigEfficiency(Block block) {
      float f = this.inventory.getStrVsBlock(block);
      if (f > 1.0F) {
         int i = EnchantmentHelper.getEfficiencyModifier(this);
         ItemStack itemStack = this.inventory.getCurrentItem();
         if (i > 0 && itemStack != null) {
            f += i * i + 1;
         }
      }

      if (this.isPotionActive(Potion.digSpeed)) {
         f *= 1.0F + (this.getActivePotionEffect(Potion.digSpeed).getAmplifier() + 1) * 0.2F;
      }

      if (this.isPotionActive(Potion.digSlowdown)) {
         float g = 1.0F;
         switch (this.getActivePotionEffect(Potion.digSlowdown).getAmplifier()) {
            case 0:
               g = 0.3F;
               break;
            case 1:
               g = 0.09F;
               break;
            case 2:
               g = 0.0027F;
               break;
            case 3:
            default:
               g = 8.1E-4F;
         }

         f *= g;
      }

      if (this.isInsideOfMaterial(Material.water) && !EnchantmentHelper.getAquaAffinityModifier(this)) {
         f /= 5.0F;
      }

      if (!this.onGround) {
         f /= 5.0F;
      }

      return f;
   }

   public boolean canHarvestBlock(Block blockToHarvest) {
      return this.inventory.canHeldItemHarvest(blockToHarvest);
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound tagCompund) {
      super.readEntityFromNBT(tagCompund);
      this.entityUniqueID = getUUID(this.gameProfile);
      NBTTagList nbtTagList = tagCompund.getTagList("Inventory", 10);
      this.inventory.readFromNBT(nbtTagList);
      this.inventory.currentItem = tagCompund.getInteger("SelectedItemSlot");
      this.sleeping = tagCompund.getBoolean("Sleeping");
      this.sleepTimer = tagCompund.getShort("SleepTimer");
      this.experience = tagCompund.getFloat("XpP");
      this.experienceLevel = tagCompund.getInteger("XpLevel");
      this.experienceTotal = tagCompund.getInteger("XpTotal");
      this.xpSeed = tagCompund.getInteger("XpSeed");
      if (this.xpSeed == 0) {
         this.xpSeed = this.rand.nextInt();
      }

      this.setScore(tagCompund.getInteger("Score"));
      if (this.sleeping) {
         this.playerLocation = new BlockPos(this);
         this.wakeUpPlayer(true, true, false);
      }

      if (tagCompund.hasKey("SpawnX", 99) && tagCompund.hasKey("SpawnY", 99) && tagCompund.hasKey("SpawnZ", 99)) {
         this.spawnChunk = new BlockPos(tagCompund.getInteger("SpawnX"), tagCompund.getInteger("SpawnY"), tagCompund.getInteger("SpawnZ"));
         this.spawnForced = tagCompund.getBoolean("SpawnForced");
      }

      this.foodStats.readNBT(tagCompund);
      this.capabilities.readCapabilitiesFromNBT(tagCompund);
      if (tagCompund.hasKey("EnderItems", 9)) {
         NBTTagList nbtTagList2 = tagCompund.getTagList("EnderItems", 10);
         this.theInventoryEnderChest.loadInventoryFromNBT(nbtTagList2);
      }
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound tagCompound) {
      super.writeEntityToNBT(tagCompound);
      tagCompound.setTag("Inventory", this.inventory.writeToNBT(new NBTTagList()));
      tagCompound.setInteger("SelectedItemSlot", this.inventory.currentItem);
      tagCompound.setBoolean("Sleeping", this.sleeping);
      tagCompound.setShort("SleepTimer", (short)this.sleepTimer);
      tagCompound.setFloat("XpP", this.experience);
      tagCompound.setInteger("XpLevel", this.experienceLevel);
      tagCompound.setInteger("XpTotal", this.experienceTotal);
      tagCompound.setInteger("XpSeed", this.xpSeed);
      tagCompound.setInteger("Score", this.getScore());
      if (this.spawnChunk != null) {
         tagCompound.setInteger("SpawnX", this.spawnChunk.getX());
         tagCompound.setInteger("SpawnY", this.spawnChunk.getY());
         tagCompound.setInteger("SpawnZ", this.spawnChunk.getZ());
         tagCompound.setBoolean("SpawnForced", this.spawnForced);
      }

      this.foodStats.writeNBT(tagCompound);
      this.capabilities.writeCapabilitiesToNBT(tagCompound);
      tagCompound.setTag("EnderItems", this.theInventoryEnderChest.saveInventoryToNBT());
      ItemStack itemStack = this.inventory.getCurrentItem();
      if (itemStack != null && itemStack.getItem() != null) {
         tagCompound.setTag("SelectedItem", itemStack.writeToNBT(new NBTTagCompound()));
      }
   }

   @Override
   public boolean attackEntityFrom(DamageSource source, float amount) {
      if (this.isEntityInvulnerable(source)) {
         return false;
      }

      if (this.capabilities.disableDamage && !source.canHarmInCreative()) {
         return false;
      }

      this.entityAge = 0;
      if (this.getHealth() <= 0.0F) {
         return false;
      }

      if (this.isPlayerSleeping() && !this.worldObj.isRemote) {
         this.wakeUpPlayer(true, true, false);
      }

      if (source.isDifficultyScaled()) {
         if (this.worldObj.getDifficulty() == EnumDifficulty.PEACEFUL) {
            amount = 0.0F;
         }

         if (this.worldObj.getDifficulty() == EnumDifficulty.EASY) {
            amount = amount / 2.0F + 1.0F;
         }

         if (this.worldObj.getDifficulty() == EnumDifficulty.HARD) {
            amount = amount * 3.0F / 2.0F;
         }
      }

      if (amount == 0.0F) {
         return false;
      }

      Entity entity = source.getEntity();
      if (entity instanceof EntityArrow && ((EntityArrow)entity).shootingEntity != null) {
         entity = ((EntityArrow)entity).shootingEntity;
      }

      return super.attackEntityFrom(source, amount);
   }

   public boolean canAttackPlayer(EntityPlayer other) {
      Team team = this.getTeam();
      Team team2 = other.getTeam();
      return team == null ? true : (!team.isSameTeam(team2) ? true : team.getAllowFriendlyFire());
   }

   @Override
   public void damageArmor(float f) {
      this.inventory.damageArmor(f);
   }

   @Override
   public int getTotalArmorValue() {
      return this.inventory.getTotalArmorValue();
   }

   public float getArmorVisibility() {
      int i = 0;

      for (ItemStack itemStack : this.inventory.armorInventory) {
         if (itemStack != null) {
            i++;
         }
      }

      return (float)i / this.inventory.armorInventory.length;
   }

   @Override
   public void damageEntity(DamageSource damageSrc, float damageAmount) {
      if (!this.isEntityInvulnerable(damageSrc)) {
         if (!damageSrc.isUnblockable() && this.isBlocking() && damageAmount > 0.0F) {
            damageAmount = (1.0F + damageAmount) * 0.5F;
         }

         damageAmount = this.applyArmorCalculations(damageSrc, damageAmount);
         damageAmount = this.applyPotionDamageCalculations(damageSrc, damageAmount);
         float f = damageAmount;
         damageAmount = Math.max(damageAmount - this.getAbsorptionAmount(), 0.0F);
         this.setAbsorptionAmount(this.getAbsorptionAmount() - (f - damageAmount));
         if (damageAmount != 0.0F) {
            this.addExhaustion(damageSrc.getHungerDamage());
            float g = this.getHealth();
            this.setHealth(this.getHealth() - damageAmount);
            this.getCombatTracker().trackDamage(damageSrc, g, damageAmount);
            if (damageAmount < 3.4028235E37F) {
               this.addStat(StatList.damageTakenStat, Math.round(damageAmount * 10.0F));
            }
         }
      }
   }

   public void openEditSign(TileEntitySign signTile) {
   }

   public void openEditCommandBlock(CommandBlockLogic cmdBlockLogic) {
   }

   public void displayVillagerTradeGui(IMerchant villager) {
   }

   public void displayGUIChest(IInventory chestInventory) {
   }

   public void displayGUIHorse(EntityHorse horse, IInventory horseInventory) {
   }

   public void displayGui(IInteractionObject guiOwner) {
   }

   public void displayGUIBook(ItemStack bookStack) {
   }

   public boolean interactWith(Entity targetEntity) {
      if (this.isSpectator()) {
         if (targetEntity instanceof IInventory) {
            this.displayGUIChest((IInventory)targetEntity);
         }

         return false;
      } else {
         ItemStack itemStack = this.getCurrentEquippedItem();
         ItemStack itemStack2 = itemStack != null ? itemStack.copy() : null;
         if (!targetEntity.interactFirst(this)) {
            if (itemStack != null && targetEntity instanceof EntityLivingBase) {
               if (this.capabilities.isCreativeMode) {
                  itemStack = itemStack2;
               }

               if (itemStack.interactWithEntity(this, (EntityLivingBase)targetEntity)) {
                  if (itemStack.stackSize <= 0 && !this.capabilities.isCreativeMode) {
                     this.destroyCurrentEquippedItem();
                  }

                  return true;
               }
            }

            return false;
         } else {
            if (itemStack != null && itemStack == this.getCurrentEquippedItem()) {
               if (itemStack.stackSize <= 0 && !this.capabilities.isCreativeMode) {
                  this.destroyCurrentEquippedItem();
               } else if (itemStack.stackSize < itemStack2.stackSize && this.capabilities.isCreativeMode) {
                  itemStack.stackSize = itemStack2.stackSize;
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

   public void attackTargetEntityWithCurrentItem(Entity targetEntity) {
      if (targetEntity.canAttackWithItem() && !targetEntity.hitByEntity(this)) {
         float f = (float)this.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue();
         int i = 0;
         float g = 0.0F;
         if (targetEntity instanceof EntityLivingBase) {
            g = EnchantmentHelper.getModifierForCreature(this.getHeldItem(), ((EntityLivingBase)targetEntity).getCreatureAttribute());
         } else {
            g = EnchantmentHelper.getModifierForCreature(this.getHeldItem(), EnumCreatureAttribute.UNDEFINED);
         }

         i += EnchantmentHelper.getKnockbackModifier(this);
         if (this.isSprinting()) {
            i++;
         }

         if (f > 0.0F || g > 0.0F) {
            boolean bl = this.fallDistance > 0.0F
               && !this.onGround
               && !this.isOnLadder()
               && !this.isInWater()
               && !this.isPotionActive(Potion.blindness)
               && this.ridingEntity == null
               && targetEntity instanceof EntityLivingBase;
            if (bl && f > 0.0F) {
               f *= 1.5F;
            }

            f += g;
            boolean bl2 = false;
            int j = EnchantmentHelper.getFireAspectModifier(this);
            if (targetEntity instanceof EntityLivingBase && j > 0 && !targetEntity.isBurning()) {
               bl2 = true;
               targetEntity.setFire(1);
            }

            double d = targetEntity.motionX;
            double e = targetEntity.motionY;
            double d2 = targetEntity.motionZ;
            boolean bl3 = targetEntity.attackEntityFrom(DamageSource.causePlayerDamage(this), f);
            if (bl3) {
               if (i > 0 && !Astolfo.INSTANCE.getModuleManager().get(KeepSprint.class).isEnabled()) {
                  targetEntity.addVelocity(
                     -MathHelper.sin(this.rotationYaw * (float) Math.PI / 180.0F) * i * 0.5F,
                     0.1,
                     MathHelper.cos(this.rotationYaw * (float) Math.PI / 180.0F) * i * 0.5F
                  );
                  this.motionX *= 0.6;
                  this.motionZ *= 0.6;
                  this.setSprinting(false);
               }

               if (targetEntity instanceof EntityPlayerMP && targetEntity.velocityChanged) {
                  ((EntityPlayerMP)targetEntity).playerNetServerHandler.sendPacket(new S12PacketEntityVelocity(targetEntity));
                  targetEntity.velocityChanged = false;
                  targetEntity.motionX = d;
                  targetEntity.motionY = e;
                  targetEntity.motionZ = d2;
               }

               if (bl) {
                  this.L(targetEntity);
               }

               if (g > 0.0F) {
                  this.x(targetEntity);
               }

               if (f >= 18.0F) {
                  this.triggerAchievement(AchievementList.overkill);
               }

               this.setLastAttacker(targetEntity);
               if (targetEntity instanceof EntityLivingBase) {
                  EnchantmentHelper.applyThornEnchantments((EntityLivingBase)targetEntity, this);
               }

               EnchantmentHelper.applyArthropodEnchantments(this, targetEntity);
               ItemStack itemStack = this.getCurrentEquippedItem();
               Entity entity = targetEntity;
               if (targetEntity instanceof EntityDragonPart) {
                  IEntityMultiPart iEntityMultiPart = ((EntityDragonPart)targetEntity).entityDragonObj;
                  if (iEntityMultiPart instanceof EntityLivingBase) {
                     entity = (EntityLivingBase)iEntityMultiPart;
                  }
               }

               if (itemStack != null && entity instanceof EntityLivingBase) {
                  itemStack.hitEntity((EntityLivingBase)entity, this);
                  if (itemStack.stackSize <= 0) {
                     this.destroyCurrentEquippedItem();
                  }
               }

               if (targetEntity instanceof EntityLivingBase) {
                  this.addStat(StatList.damageDealtStat, Math.round(f * 10.0F));
                  if (j > 0) {
                     targetEntity.setFire(j * 4);
                  }
               }

               this.addExhaustion(0.3F);
            } else if (bl2) {
               targetEntity.extinguish();
            }
         }
      }
   }

   public void L(Entity entity) {
   }

   public void x(Entity entity) {
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

   public EntityPlayer.EnumStatus trySleep(BlockPos bedLocation) {
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

         if (Math.abs(this.posX - bedLocation.getX()) > 3.0 || Math.abs(this.posY - bedLocation.getY()) > 2.0 || Math.abs(this.posZ - bedLocation.getZ()) > 3.0
            )
          {
            return EntityPlayer.EnumStatus.TOO_FAR_AWAY;
         }

         double d = 8.0;
         double e = 5.0;
         List list = this.worldObj
            .getEntitiesWithinAABB(
               EntityMob.class,
               new AxisAlignedBB(
                  bedLocation.getX() - d,
                  bedLocation.getY() - e,
                  bedLocation.getZ() - d,
                  bedLocation.getX() + d,
                  bedLocation.getY() + e,
                  bedLocation.getZ() + d
               )
            );
         if (!list.isEmpty()) {
            return EntityPlayer.EnumStatus.NOT_SAFE;
         }
      }

      if (this.E()) {
         this.mountEntity((Entity)null);
      }

      this.setSize(0.2F, 0.2F);
      if (this.worldObj.isBlockLoaded(bedLocation)) {
         EnumFacing enumFacing = this.worldObj.getBlockState(bedLocation).getValue(BlockDirectional.FACING);
         float f = 0.5F;
         float g = 0.5F;
         switch (enumFacing) {
            case SOUTH:
               g = 0.9F;
               break;
            case NORTH:
               g = 0.1F;
               break;
            case WEST:
               f = 0.1F;
               break;
            case EAST:
               f = 0.9F;
         }

         this.func_175139_a(enumFacing);
         this.setPosition(bedLocation.getX() + f, bedLocation.getY() + 0.6875F, bedLocation.getZ() + g);
      } else {
         this.setPosition(bedLocation.getX() + 0.5F, bedLocation.getY() + 0.6875F, bedLocation.getZ() + 0.5F);
      }

      this.sleeping = true;
      this.sleepTimer = 0;
      this.playerLocation = bedLocation;
      this.motionX = this.motionZ = this.motionY = 0.0;
      if (!this.worldObj.isRemote) {
         this.worldObj.updateAllPlayersSleepingFlag();
      }

      return EntityPlayer.EnumStatus.OK;
   }

   private void func_175139_a(EnumFacing enumFacing) {
      this.renderOffsetX = 0.0F;
      this.renderOffsetZ = 0.0F;
      switch (enumFacing) {
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

   public void wakeUpPlayer(boolean immediately, boolean updateWorldFlag, boolean setSpawn) {
      this.setSize(0.6F, 1.8F);
      IBlockState iBlockState = this.worldObj.getBlockState(this.playerLocation);
      if (this.playerLocation != null && iBlockState.getBlock() == Blocks.bed) {
         this.worldObj.setBlockState(this.playerLocation, iBlockState.withProperty(BlockBed.OCCUPIED, false), 4);
         BlockPos blockPos = BlockBed.getSafeExitLocation(this.worldObj, this.playerLocation, 0);
         if (blockPos == null) {
            blockPos = this.playerLocation.up();
         }

         this.setPosition(blockPos.getX() + 0.5F, blockPos.getY() + 0.1F, blockPos.getZ() + 0.5F);
      }

      this.sleeping = false;
      if (!this.worldObj.isRemote && updateWorldFlag) {
         this.worldObj.updateAllPlayersSleepingFlag();
      }

      this.sleepTimer = immediately ? 0 : 100;
      if (setSpawn) {
         this.setSpawnPoint(this.playerLocation, false);
      }
   }

   private boolean isInBed() {
      return this.worldObj.getBlockState(this.playerLocation).getBlock() == Blocks.bed;
   }

   public static BlockPos getBedSpawnLocation(World worldIn, BlockPos bedLocation, boolean forceSpawn) {
      Block block = worldIn.getBlockState(bedLocation).getBlock();
      if (block != Blocks.bed) {
         if (!forceSpawn) {
            return null;
         }

         boolean bl = block.canSpawnInBlock();
         boolean bl2 = worldIn.getBlockState(bedLocation.up()).getBlock().canSpawnInBlock();
         return bl && bl2 ? bedLocation : null;
      } else {
         return BlockBed.getSafeExitLocation(worldIn, bedLocation, 0);
      }
   }

   public float getBedOrientationInDegrees() {
      if (this.playerLocation != null) {
         EnumFacing enumFacing = this.worldObj.getBlockState(this.playerLocation).getValue(BlockDirectional.FACING);
         switch (enumFacing) {
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

   public void addChatComponentMessage(IChatComponent chatComponent) {
   }

   public BlockPos getBedLocation() {
      return this.spawnChunk;
   }

   public boolean S() {
      return this.spawnForced;
   }

   public void setSpawnPoint(BlockPos pos, boolean forced) {
      if (pos != null) {
         this.spawnChunk = pos;
         this.spawnForced = forced;
      } else {
         this.spawnChunk = null;
         this.spawnForced = false;
      }
   }

   public void triggerAchievement(StatBase achievementIn) {
      this.addStat(achievementIn, 1);
   }

   public void addStat(StatBase stat, int amount) {
   }

   public void func_175145_a(StatBase statBase) {
   }

   @Override
   public void jump() {
      JumpEvent jumpEvent = new JumpEvent();
      Astolfo.INSTANCE.getEventBus().post(jumpEvent);
      if (!jumpEvent.isCancelled()) {
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
   public void moveEntityWithHeading(float strafe, float forward) {
      double d = this.posX;
      double e = this.posY;
      double d2 = this.posZ;
      if (this.capabilities.isFlying && this.ridingEntity == null) {
         double d3 = this.motionY;
         float f = this.jumpMovementFactor;
         this.jumpMovementFactor = this.capabilities.getFlySpeed() * (this.isSprinting() ? 2 : 1);
         super.moveEntityWithHeading(strafe, forward);
         this.motionY = d3 * 0.6;
         this.jumpMovementFactor = f;
      } else {
         super.moveEntityWithHeading(strafe, forward);
      }

      this.addMovementStat(this.posX - d, this.posY - e, this.posZ - d2);
   }

   @Override
   public float getAIMoveSpeed() {
      return (float)this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getAttributeValue();
   }

   public void addMovementStat(double d, double d2, double d3) {
      if (this.ridingEntity == null) {
         if (this.isInsideOfMaterial(Material.water)) {
            int i = Math.round(MathHelper.sqrt_double(d * d + d2 * d2 + d3 * d3) * 100.0F);
            if (i > 0) {
               this.addStat(StatList.distanceDoveStat, i);
               this.addExhaustion(0.015F * i * 0.01F);
            }
         } else if (this.isInWater()) {
            int j = Math.round(MathHelper.sqrt_double(d * d + d3 * d3) * 100.0F);
            if (j > 0) {
               this.addStat(StatList.distanceSwumStat, j);
               this.addExhaustion(0.015F * j * 0.01F);
            }
         } else if (this.isOnLadder()) {
            if (d2 > 0.0) {
               this.addStat(StatList.distanceClimbedStat, (int)Math.round(d2 * 100.0));
            }
         } else if (this.onGround) {
            int k = Math.round(MathHelper.sqrt_double(d * d + d3 * d3) * 100.0F);
            if (k > 0) {
               this.addStat(StatList.distanceWalkedStat, k);
               if (this.isSprinting()) {
                  this.addStat(StatList.distanceSprintedStat, k);
                  this.addExhaustion(0.099999994F * k * 0.01F);
               } else {
                  if (this.isSneaking()) {
                     this.addStat(StatList.distanceCrouchedStat, k);
                  }

                  this.addExhaustion(0.01F * k * 0.01F);
               }
            }
         } else {
            int l = Math.round(MathHelper.sqrt_double(d * d + d3 * d3) * 100.0F);
            if (l > 25) {
               this.addStat(StatList.distanceFlownStat, l);
            }
         }
      }
   }

   private void addMountedMovementStat(double d, double d2, double d3) {
      if (this.ridingEntity != null) {
         int i = Math.round(MathHelper.sqrt_double(d * d + d2 * d2 + d3 * d3) * 100.0F);
         if (i > 0) {
            if (this.ridingEntity instanceof EntityMinecart) {
               this.addStat(StatList.distanceByMinecartStat, i);
               if (this.startMinecartRidingCoordinate == null) {
                  this.startMinecartRidingCoordinate = new BlockPos(this);
               } else if (this.startMinecartRidingCoordinate
                     .distanceSq(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ))
                  >= 1000000.0) {
                  this.triggerAchievement(AchievementList.onARail);
               }
            } else if (this.ridingEntity instanceof EntityBoat) {
               this.addStat(StatList.distanceByBoatStat, i);
            } else if (this.ridingEntity instanceof EntityPig) {
               this.addStat(StatList.distanceByPigStat, i);
            } else if (this.ridingEntity instanceof EntityHorse) {
               this.addStat(StatList.distanceByHorseStat, i);
            }
         }
      }
   }

   @Override
   public void fall(float distance, float damageMultiplier) {
      if (!this.capabilities.allowFlying) {
         if (distance >= 2.0F) {
            this.addStat(StatList.distanceFallenStat, (int)Math.round(distance * 100.0));
         }

         super.fall(distance, damageMultiplier);
      }
   }

   @Override
   public void resetHeight() {
      if (!this.isSpectator()) {
         super.resetHeight();
      }
   }

   @Override
   public String getFallSoundString(int damageValue) {
      return damageValue > 4 ? "game.player.hurt.fall.big" : "game.player.hurt.fall.small";
   }

   @Override
   public void onKillEntity(EntityLivingBase entityLivingIn) {
      if (entityLivingIn instanceof IMob) {
         this.triggerAchievement(AchievementList.killEnemy);
      }

      EntityList.EntityEggInfo entityEggInfo = EntityList.entityEggs.get(EntityList.getEntityID(entityLivingIn));
      if (entityEggInfo != null) {
         this.triggerAchievement(entityEggInfo.field_151512_d);
      }
   }

   @Override
   public void setInWeb() {
      if (!this.capabilities.isFlying) {
         super.setInWeb();
      }
   }

   @Override
   public ItemStack getCurrentArmor(int slotIn) {
      return this.inventory.armorItemInSlot(slotIn);
   }

   public void addExperience(int amount) {
      this.addScore(amount);
      int i = Integer.MAX_VALUE - this.experienceTotal;
      if (amount > i) {
         amount = i;
      }

      this.experience = this.experience + (float)amount / this.xpBarCap();

      for (this.experienceTotal += amount; this.experience >= 1.0F; this.experience = this.experience / this.xpBarCap()) {
         this.experience = (this.experience - 1.0F) * this.xpBarCap();
         this.addExperienceLevel(1);
      }
   }

   public int getXPSeed() {
      return this.xpSeed;
   }

   public void removeExperienceLevel(int levels) {
      this.experienceLevel -= levels;
      if (this.experienceLevel < 0) {
         this.experienceLevel = 0;
         this.experience = 0.0F;
         this.experienceTotal = 0;
      }

      this.xpSeed = this.rand.nextInt();
   }

   public void addExperienceLevel(int levels) {
      this.experienceLevel += levels;
      if (this.experienceLevel < 0) {
         this.experienceLevel = 0;
         this.experience = 0.0F;
         this.experienceTotal = 0;
      }

      if (levels > 0 && this.experienceLevel % 5 == 0 && this.lastXPSound < this.ticksExisted - 100.0F) {
         float f = this.experienceLevel > 30 ? 1.0F : this.experienceLevel / 30.0F;
         this.worldObj.playSoundAtEntity(this, "random.levelup", f * 0.75F, 1.0F);
         this.lastXPSound = this.ticksExisted;
      }
   }

   public int xpBarCap() {
      return this.experienceLevel >= 30
         ? 112 + (this.experienceLevel - 30) * 9
         : (this.experienceLevel >= 15 ? 37 + (this.experienceLevel - 15) * 5 : 7 + this.experienceLevel * 2);
   }

   public void addExhaustion(float f) {
      if (!this.capabilities.disableDamage && !this.worldObj.isRemote) {
         this.foodStats.addExhaustion(f);
      }
   }

   public FoodStats getFoodStats() {
      return this.foodStats;
   }

   public boolean canEat(boolean ignoreHunger) {
      return (ignoreHunger || this.foodStats.needFood()) && !this.capabilities.disableDamage;
   }

   public boolean shouldHeal() {
      return this.getHealth() > 0.0F && this.getHealth() < this.getMaxHealth();
   }

   public void setItemInUse(ItemStack stack, int duration) {
      if (stack != this.itemInUse) {
         this.itemInUse = stack;
         this.itemInUseCount = duration;
         if (!this.worldObj.isRemote) {
            this.setEating(true);
         }
      }
   }

   public boolean isAllowEdit() {
      return this.capabilities.allowEdit;
   }

   public boolean canPlayerEdit(BlockPos blockPos, EnumFacing enumFacing, ItemStack itemStack) {
      if (this.capabilities.allowEdit) {
         return true;
      }

      if (itemStack == null) {
         return false;
      }

      BlockPos blockPos2 = blockPos.offset(enumFacing.getOpposite());
      Block block = this.worldObj.getBlockState(blockPos2).getBlock();
      return itemStack.canPlaceOn(block) || itemStack.canEditBlocks();
   }

   @Override
   public int getExperiencePoints(EntityPlayer player) {
      if (this.worldObj.getGameRules().getBoolean("keepInventory")) {
         return 0;
      }

      int i = this.experienceLevel * 7;
      return i > 100 ? 100 : i;
   }

   @Override
   public boolean isPlayer() {
      return true;
   }

   @Override
   public boolean getAlwaysRenderNameTagForRender() {
      return true;
   }

   public void clonePlayer(EntityPlayer oldPlayer, boolean respawnFromEnd) {
      if (respawnFromEnd) {
         this.inventory.copyInventory(oldPlayer.inventory);
         this.setHealth(oldPlayer.getHealth());
         this.foodStats = oldPlayer.foodStats;
         this.experienceLevel = oldPlayer.experienceLevel;
         this.experienceTotal = oldPlayer.experienceTotal;
         this.experience = oldPlayer.experience;
         this.setScore(oldPlayer.getScore());
         this.lastPortalPos = oldPlayer.lastPortalPos;
         this.lastPortalVec = oldPlayer.lastPortalVec;
         this.teleportDirection = oldPlayer.teleportDirection;
      } else if (this.worldObj.getGameRules().getBoolean("keepInventory")) {
         this.inventory.copyInventory(oldPlayer.inventory);
         this.experienceLevel = oldPlayer.experienceLevel;
         this.experienceTotal = oldPlayer.experienceTotal;
         this.experience = oldPlayer.experience;
         this.setScore(oldPlayer.getScore());
      }

      this.xpSeed = oldPlayer.xpSeed;
      this.theInventoryEnderChest = oldPlayer.theInventoryEnderChest;
      this.getDataWatcher().updateObject(10, oldPlayer.getDataWatcher().getWatchableObjectByte(10));
   }

   @Override
   public boolean canTriggerWalking() {
      return !this.capabilities.isFlying;
   }

   public void sendPlayerAbilities() {
   }

   public void setGameType(WorldSettings.GameType gameType) {
   }

   @Override
   public String getName() {
      return this.gameProfile.getName();
   }

   public InventoryEnderChest getInventoryEnderChest() {
      return this.theInventoryEnderChest;
   }

   @Override
   public ItemStack getEquipmentInSlot(int slotIn) {
      return slotIn == 0 ? this.inventory.getCurrentItem() : this.inventory.armorInventory[slotIn - 1];
   }

   @Override
   public ItemStack getHeldItem() {
      return this.inventory.getCurrentItem();
   }

   @Override
   public void setCurrentItemOrArmor(int slotIn, ItemStack stack) {
      this.inventory.armorInventory[slotIn] = stack;
   }

   @Override
   public boolean isInvisibleToPlayer(EntityPlayer player) {
      if (!this.isInvisible()) {
         return false;
      }

      if (player.isSpectator()) {
         return false;
      }

      Team team = this.getTeam();
      return team == null || player == null || player.getTeam() != team || !team.getSeeFriendlyInvisiblesEnabled();
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
      ChatComponentText chatComponentText = new ChatComponentText(ScorePlayerTeam.formatPlayerName(this.getTeam(), this.getName()));
      chatComponentText.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/msg " + this.getName() + " "));
      chatComponentText.getChatStyle().setChatHoverEvent(this.getHoverEvent());
      chatComponentText.getChatStyle().setInsertion(this.getName());
      return chatComponentText;
   }

   @Override
   public float getEyeHeight() {
      float f = 1.62F;
      if (this.isPlayerSleeping()) {
         f = 0.2F;
      }

      if (this.isSneaking()) {
         f -= 0.08F;
      }

      return f;
   }

   @Override
   public void setAbsorptionAmount(float amount) {
      if (amount < 0.0F) {
         amount = 0.0F;
      }

      this.getDataWatcher().updateObject(17, amount);
   }

   @Override
   public float getAbsorptionAmount() {
      return this.getDataWatcher().getWatchableObjectFloat(17);
   }

   public static UUID getUUID(GameProfile profile) {
      UUID uuid = profile.getId();
      if (uuid == null) {
         uuid = getOfflineUUID(profile.getName());
      }

      return uuid;
   }

   public static UUID getOfflineUUID(String username) {
      return UUID.nameUUIDFromBytes(("OfflinePlayer:" + username).getBytes(Charsets.UTF_8));
   }

   public boolean canOpen(LockCode code) {
      if (code.isEmpty()) {
         return true;
      }

      ItemStack itemStack = this.getCurrentEquippedItem();
      return itemStack != null && itemStack.hasDisplayName() ? itemStack.getDisplayName().equals(code.getLock()) : false;
   }

   public boolean isWearing(EnumPlayerModelParts enumPlayerModelParts) {
      return Astolfo.INSTANCE.getUserManager().getUser(this.getName()) != null
         ? true
         : (this.getDataWatcher().getWatchableObjectByte(10) & enumPlayerModelParts.getPartMask()) == enumPlayerModelParts.getPartMask();
   }

   @Override
   public boolean sendCommandFeedback() {
      return MinecraftServer.getServer().worldServers[0].getGameRules().getBoolean("sendCommandFeedback");
   }

   @Override
   public boolean replaceItemInInventory(int inventorySlot, ItemStack itemStackIn) {
      if (inventorySlot >= 0 && inventorySlot < this.inventory.mainInventory.length) {
         this.inventory.setInventorySlotContents(inventorySlot, itemStackIn);
         return true;
      }

      int i = inventorySlot - 100;
      if (i >= 0 && i < this.inventory.armorInventory.length) {
         int j = i + 1;
         if (itemStackIn != null && itemStackIn.getItem() != null) {
            if (itemStackIn.getItem() instanceof ItemArmor) {
               if (EntityLiving.getArmorPosition(itemStackIn) != j) {
                  return false;
               }
            } else if (j != 4 || itemStackIn.getItem() != Items.skull && !(itemStackIn.getItem() instanceof ItemBlock)) {
               return false;
            }
         }

         this.inventory.setInventorySlotContents(i + this.inventory.mainInventory.length, itemStackIn);
         return true;
      } else {
         int k = inventorySlot - 200;
         if (k >= 0 && k < this.theInventoryEnderChest.getSizeInventory()) {
            this.theInventoryEnderChest.setInventorySlotContents(k, itemStackIn);
            return true;
         } else {
            return false;
         }
      }
   }

   public boolean w() {
      return this.hasReducedDebug;
   }

   public void setReducedDebug(boolean reducedDebug) {
      this.hasReducedDebug = reducedDebug;
   }

   public enum EnumChatVisibility {
      FULL(0, "options.chat.visibility.full"),
      SYSTEM(1, "options.chat.visibility.system"),
      HIDDEN(2, "options.chat.visibility.hidden");

      private static final EntityPlayer.EnumChatVisibility[] ID_LOOKUP = new EntityPlayer.EnumChatVisibility[values().length];
      private final int chatVisibility;
      private final String resourceKey;

      EnumChatVisibility(int id, String resourceKey) {
         this.chatVisibility = id;
         this.resourceKey = resourceKey;
      }

      public int getChatVisibility() {
         return this.chatVisibility;
      }

      public static EntityPlayer.EnumChatVisibility getEnumChatVisibility(int id) {
         return ID_LOOKUP[id % ID_LOOKUP.length];
      }

      public String getResourceKey() {
         return this.resourceKey;
      }

      static {
         for (EntityPlayer.EnumChatVisibility enumChatVisibility : values()) {
            ID_LOOKUP[enumChatVisibility.chatVisibility] = enumChatVisibility;
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
