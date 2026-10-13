package net.minecraft.entity.passive;

import java.util.Random;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.INpc;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIFollowGolem;
import net.minecraft.entity.ai.EntityAIHarvestFarmland;
import net.minecraft.entity.ai.EntityAILookAtTradePlayer;
import net.minecraft.entity.ai.EntityAIMoveIndoors;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAIOpenDoor;
import net.minecraft.entity.ai.EntityAIPlay;
import net.minecraft.entity.ai.EntityAIRestrictOpenDoor;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITradePlayer;
import net.minecraft.entity.ai.EntityAIVillagerInteract;
import net.minecraft.entity.ai.EntityAIVillagerMate;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityAIWatchClosest2;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.StatList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Tuple;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.village.Village;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

public class EntityVillager extends EntityAgeable implements IMerchant, INpc {
   private int randomTickDivider;
   private boolean isMating;
   private boolean isPlaying;
   public Village villageObj;
   private EntityPlayer buyingPlayer;
   private MerchantRecipeList buyingList;
   private int timeUntilReset;
   private boolean needsInitilization;
   private boolean isWillingToMate;
   private int wealth;
   private String lastBuyingPlayer;
   private int careerId;
   private int careerLevel;
   private boolean isLookingForHome;
   private boolean areAdditionalTasksSet;
   private InventoryBasic villagerInventory = new InventoryBasic("Items", false, 8);
   private static final EntityVillager.ITradeList[][][][] DEFAULT_TRADE_LIST_MAP = new EntityVillager.ITradeList[][][][]{
      {
            {
                  {
                        new EntityVillager.EmeraldForItems(Items.wheat, new EntityVillager.PriceInfo(18, 22)),
                        new EntityVillager.EmeraldForItems(Items.potato, new EntityVillager.PriceInfo(15, 19)),
                        new EntityVillager.EmeraldForItems(Items.carrot, new EntityVillager.PriceInfo(15, 19)),
                        new EntityVillager.ListItemForEmeralds(Items.bread, new EntityVillager.PriceInfo(-4, -2))
                  },
                  {
                        new EntityVillager.EmeraldForItems(Item.getItemFromBlock(Blocks.pumpkin), new EntityVillager.PriceInfo(8, 13)),
                        new EntityVillager.ListItemForEmeralds(Items.pumpkin_pie, new EntityVillager.PriceInfo(-3, -2))
                  },
                  {
                        new EntityVillager.EmeraldForItems(Item.getItemFromBlock(Blocks.melon_block), new EntityVillager.PriceInfo(7, 12)),
                        new EntityVillager.ListItemForEmeralds(Items.apple, new EntityVillager.PriceInfo(-5, -7))
                  },
                  {
                        new EntityVillager.ListItemForEmeralds(Items.cookie, new EntityVillager.PriceInfo(-6, -10)),
                        new EntityVillager.ListItemForEmeralds(Items.cake, new EntityVillager.PriceInfo(1, 1))
                  }
            },
            {
                  {
                        new EntityVillager.EmeraldForItems(Items.string, new EntityVillager.PriceInfo(15, 20)),
                        new EntityVillager.EmeraldForItems(Items.coal, new EntityVillager.PriceInfo(16, 24)),
                        new EntityVillager.ItemAndEmeraldToItem(
                           Items.fish, new EntityVillager.PriceInfo(6, 6), Items.cooked_fish, new EntityVillager.PriceInfo(6, 6)
                        )
                  },
                  {new EntityVillager.ListEnchantedItemForEmeralds(Items.fishing_rod, new EntityVillager.PriceInfo(7, 8))}
            },
            {
                  {
                        new EntityVillager.EmeraldForItems(Item.getItemFromBlock(Blocks.wool), new EntityVillager.PriceInfo(16, 22)),
                        new EntityVillager.ListItemForEmeralds(Items.shears, new EntityVillager.PriceInfo(3, 4))
                  },
                  {
                        new EntityVillager.ListItemForEmeralds(new ItemStack(Item.getItemFromBlock(Blocks.wool), 1, 0), new EntityVillager.PriceInfo(1, 2)),
                        new EntityVillager.ListItemForEmeralds(new ItemStack(Item.getItemFromBlock(Blocks.wool), 1, 1), new EntityVillager.PriceInfo(1, 2)),
                        new EntityVillager.ListItemForEmeralds(new ItemStack(Item.getItemFromBlock(Blocks.wool), 1, 2), new EntityVillager.PriceInfo(1, 2)),
                        new EntityVillager.ListItemForEmeralds(new ItemStack(Item.getItemFromBlock(Blocks.wool), 1, 3), new EntityVillager.PriceInfo(1, 2)),
                        new EntityVillager.ListItemForEmeralds(new ItemStack(Item.getItemFromBlock(Blocks.wool), 1, 4), new EntityVillager.PriceInfo(1, 2)),
                        new EntityVillager.ListItemForEmeralds(new ItemStack(Item.getItemFromBlock(Blocks.wool), 1, 5), new EntityVillager.PriceInfo(1, 2)),
                        new EntityVillager.ListItemForEmeralds(new ItemStack(Item.getItemFromBlock(Blocks.wool), 1, 6), new EntityVillager.PriceInfo(1, 2)),
                        new EntityVillager.ListItemForEmeralds(new ItemStack(Item.getItemFromBlock(Blocks.wool), 1, 7), new EntityVillager.PriceInfo(1, 2)),
                        new EntityVillager.ListItemForEmeralds(new ItemStack(Item.getItemFromBlock(Blocks.wool), 1, 8), new EntityVillager.PriceInfo(1, 2)),
                        new EntityVillager.ListItemForEmeralds(new ItemStack(Item.getItemFromBlock(Blocks.wool), 1, 9), new EntityVillager.PriceInfo(1, 2)),
                        new EntityVillager.ListItemForEmeralds(new ItemStack(Item.getItemFromBlock(Blocks.wool), 1, 10), new EntityVillager.PriceInfo(1, 2)),
                        new EntityVillager.ListItemForEmeralds(new ItemStack(Item.getItemFromBlock(Blocks.wool), 1, 11), new EntityVillager.PriceInfo(1, 2)),
                        new EntityVillager.ListItemForEmeralds(new ItemStack(Item.getItemFromBlock(Blocks.wool), 1, 12), new EntityVillager.PriceInfo(1, 2)),
                        new EntityVillager.ListItemForEmeralds(new ItemStack(Item.getItemFromBlock(Blocks.wool), 1, 13), new EntityVillager.PriceInfo(1, 2)),
                        new EntityVillager.ListItemForEmeralds(new ItemStack(Item.getItemFromBlock(Blocks.wool), 1, 14), new EntityVillager.PriceInfo(1, 2)),
                        new EntityVillager.ListItemForEmeralds(new ItemStack(Item.getItemFromBlock(Blocks.wool), 1, 15), new EntityVillager.PriceInfo(1, 2))
                  }
            },
            {
                  {
                        new EntityVillager.EmeraldForItems(Items.string, new EntityVillager.PriceInfo(15, 20)),
                        new EntityVillager.ListItemForEmeralds(Items.arrow, new EntityVillager.PriceInfo(-12, -8))
                  },
                  {
                        new EntityVillager.ListItemForEmeralds(Items.bow, new EntityVillager.PriceInfo(2, 3)),
                        new EntityVillager.ItemAndEmeraldToItem(
                           Item.getItemFromBlock(Blocks.gravel), new EntityVillager.PriceInfo(10, 10), Items.flint, new EntityVillager.PriceInfo(6, 10)
                        )
                  }
            }
      },
      {
            {
                  {new EntityVillager.EmeraldForItems(Items.paper, new EntityVillager.PriceInfo(24, 36)), new EntityVillager.ListEnchantedBookForEmeralds()},
                  {
                        new EntityVillager.EmeraldForItems(Items.book, new EntityVillager.PriceInfo(8, 10)),
                        new EntityVillager.ListItemForEmeralds(Items.compass, new EntityVillager.PriceInfo(10, 12)),
                        new EntityVillager.ListItemForEmeralds(Item.getItemFromBlock(Blocks.bookshelf), new EntityVillager.PriceInfo(3, 4))
                  },
                  {
                        new EntityVillager.EmeraldForItems(Items.written_book, new EntityVillager.PriceInfo(2, 2)),
                        new EntityVillager.ListItemForEmeralds(Items.clock, new EntityVillager.PriceInfo(10, 12)),
                        new EntityVillager.ListItemForEmeralds(Item.getItemFromBlock(Blocks.glass), new EntityVillager.PriceInfo(-5, -3))
                  },
                  {new EntityVillager.ListEnchantedBookForEmeralds()},
                  {new EntityVillager.ListEnchantedBookForEmeralds()},
                  {new EntityVillager.ListItemForEmeralds(Items.name_tag, new EntityVillager.PriceInfo(20, 22))}
            }
      },
      {
            {
                  {
                        new EntityVillager.EmeraldForItems(Items.rotten_flesh, new EntityVillager.PriceInfo(36, 40)),
                        new EntityVillager.EmeraldForItems(Items.gold_ingot, new EntityVillager.PriceInfo(8, 10))
                  },
                  {
                        new EntityVillager.ListItemForEmeralds(Items.redstone, new EntityVillager.PriceInfo(-4, -1)),
                        new EntityVillager.ListItemForEmeralds(
                           new ItemStack(Items.dye, 1, EnumDyeColor.BLUE.getDyeDamage()), new EntityVillager.PriceInfo(-2, -1)
                        )
                  },
                  {
                        new EntityVillager.ListItemForEmeralds(Items.ender_eye, new EntityVillager.PriceInfo(7, 11)),
                        new EntityVillager.ListItemForEmeralds(Item.getItemFromBlock(Blocks.glowstone), new EntityVillager.PriceInfo(-3, -1))
                  },
                  {new EntityVillager.ListItemForEmeralds(Items.experience_bottle, new EntityVillager.PriceInfo(3, 11))}
            }
      },
      {
            {
                  {
                        new EntityVillager.EmeraldForItems(Items.coal, new EntityVillager.PriceInfo(16, 24)),
                        new EntityVillager.ListItemForEmeralds(Items.iron_helmet, new EntityVillager.PriceInfo(4, 6))
                  },
                  {
                        new EntityVillager.EmeraldForItems(Items.iron_ingot, new EntityVillager.PriceInfo(7, 9)),
                        new EntityVillager.ListItemForEmeralds(Items.iron_chestplate, new EntityVillager.PriceInfo(10, 14))
                  },
                  {
                        new EntityVillager.EmeraldForItems(Items.diamond, new EntityVillager.PriceInfo(3, 4)),
                        new EntityVillager.ListEnchantedItemForEmeralds(Items.diamond_chestplate, new EntityVillager.PriceInfo(16, 19))
                  },
                  {
                        new EntityVillager.ListItemForEmeralds(Items.chainmail_boots, new EntityVillager.PriceInfo(5, 7)),
                        new EntityVillager.ListItemForEmeralds(Items.chainmail_leggings, new EntityVillager.PriceInfo(9, 11)),
                        new EntityVillager.ListItemForEmeralds(Items.chainmail_helmet, new EntityVillager.PriceInfo(5, 7)),
                        new EntityVillager.ListItemForEmeralds(Items.chainmail_chestplate, new EntityVillager.PriceInfo(11, 15))
                  }
            },
            {
                  {
                        new EntityVillager.EmeraldForItems(Items.coal, new EntityVillager.PriceInfo(16, 24)),
                        new EntityVillager.ListItemForEmeralds(Items.iron_axe, new EntityVillager.PriceInfo(6, 8))
                  },
                  {
                        new EntityVillager.EmeraldForItems(Items.iron_ingot, new EntityVillager.PriceInfo(7, 9)),
                        new EntityVillager.ListEnchantedItemForEmeralds(Items.iron_sword, new EntityVillager.PriceInfo(9, 10))
                  },
                  {
                        new EntityVillager.EmeraldForItems(Items.diamond, new EntityVillager.PriceInfo(3, 4)),
                        new EntityVillager.ListEnchantedItemForEmeralds(Items.diamond_sword, new EntityVillager.PriceInfo(12, 15)),
                        new EntityVillager.ListEnchantedItemForEmeralds(Items.diamond_axe, new EntityVillager.PriceInfo(9, 12))
                  }
            },
            {
                  {
                        new EntityVillager.EmeraldForItems(Items.coal, new EntityVillager.PriceInfo(16, 24)),
                        new EntityVillager.ListEnchantedItemForEmeralds(Items.iron_shovel, new EntityVillager.PriceInfo(5, 7))
                  },
                  {
                        new EntityVillager.EmeraldForItems(Items.iron_ingot, new EntityVillager.PriceInfo(7, 9)),
                        new EntityVillager.ListEnchantedItemForEmeralds(Items.iron_pickaxe, new EntityVillager.PriceInfo(9, 11))
                  },
                  {
                        new EntityVillager.EmeraldForItems(Items.diamond, new EntityVillager.PriceInfo(3, 4)),
                        new EntityVillager.ListEnchantedItemForEmeralds(Items.diamond_pickaxe, new EntityVillager.PriceInfo(12, 15))
                  }
            }
      },
      {
            {
                  {
                        new EntityVillager.EmeraldForItems(Items.porkchop, new EntityVillager.PriceInfo(14, 18)),
                        new EntityVillager.EmeraldForItems(Items.chicken, new EntityVillager.PriceInfo(14, 18))
                  },
                  {
                        new EntityVillager.EmeraldForItems(Items.coal, new EntityVillager.PriceInfo(16, 24)),
                        new EntityVillager.ListItemForEmeralds(Items.cooked_porkchop, new EntityVillager.PriceInfo(-7, -5)),
                        new EntityVillager.ListItemForEmeralds(Items.cooked_chicken, new EntityVillager.PriceInfo(-8, -6))
                  }
            },
            {
                  {
                        new EntityVillager.EmeraldForItems(Items.leather, new EntityVillager.PriceInfo(9, 12)),
                        new EntityVillager.ListItemForEmeralds(Items.leather_leggings, new EntityVillager.PriceInfo(2, 4))
                  },
                  {new EntityVillager.ListEnchantedItemForEmeralds(Items.leather_chestplate, new EntityVillager.PriceInfo(7, 12))},
                  {new EntityVillager.ListItemForEmeralds(Items.saddle, new EntityVillager.PriceInfo(8, 10))}
            }
      }
   };
   private static final long eb = 139823961718593L;

   public EntityVillager(World var1) {
      this(var1, 0);
   }

   public EntityVillager(World var1, int var2) {
      super(var1);
      this.setProfession(var2);
      this.setSize(0.6F, 1.8F);
      ((PathNavigateGround)this.getNavigator()).setBreakDoors(true);
      ((PathNavigateGround)this.getNavigator()).setAvoidsWater(true);
      this.tasks.addTask(0, new EntityAISwimming(this));
      this.tasks.addTask(1, new EntityAIAvoidEntity<>(this, EntityZombie.class, 8.0F, 0.6, 0.6));
      this.tasks.addTask(1, new EntityAITradePlayer(this));
      this.tasks.addTask(1, new EntityAILookAtTradePlayer(this));
      this.tasks.addTask(2, new EntityAIMoveIndoors(this));
      this.tasks.addTask(3, new EntityAIRestrictOpenDoor(this));
      this.tasks.addTask(4, new EntityAIOpenDoor(this, true));
      this.tasks.addTask(5, new EntityAIMoveTowardsRestriction(this, 0.6));
      this.tasks.addTask(6, new EntityAIVillagerMate(this));
      this.tasks.addTask(7, new EntityAIFollowGolem(this));
      this.tasks.addTask(9, new EntityAIWatchClosest2(this, EntityPlayer.class, 3.0F, 1.0F));
      this.tasks.addTask(9, new EntityAIVillagerInteract(this));
      this.tasks.addTask(9, new EntityAIWander(this, 0.6));
      this.tasks.addTask(10, new EntityAIWatchClosest(this, EntityLiving.class, 8.0F));
      this.setCanPickUpLoot(true);
   }

   private void setAdditionalAItasks() {
      if (!this.areAdditionalTasksSet) {
         this.areAdditionalTasksSet = true;
         if (this.isChild()) {
            this.tasks.addTask(8, new EntityAIPlay(this, 0.32));
         } else if (this.getProfession() == 0) {
            this.tasks.addTask(6, new EntityAIHarvestFarmland(this, 0.6));
         }
      }
   }

   @Override
   public void onGrowingAdult() {
      if (this.getProfession() == 0) {
         this.tasks.addTask(8, new EntityAIHarvestFarmland(this, 0.6));
      }

      super.onGrowingAdult();
   }

   @Override
   public void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.5);
   }

   @Override
   public void updateAITasks() {
      if (--this.randomTickDivider <= 0) {
         BlockPos var4 = new BlockPos(this);
         this.worldObj.getVillageCollection().addToVillagerPositionList(var4);
         this.randomTickDivider = 70 + this.rand.nextInt(50);
         this.villageObj = this.worldObj.getVillageCollection().getNearestVillage(var4, 32);
         if (this.villageObj == null) {
            this.detachHome();
         } else {
            BlockPos var5 = this.villageObj.getCenter();
            this.setHomePosAndDistance(var5, (int)(this.villageObj.getVillageRadius() * 1.0F));
            if (this.isLookingForHome) {
               this.isLookingForHome = false;
               this.villageObj.setDefaultPlayerReputation(5);
            }
         }
      }

      if (!this.isTrading() && this.timeUntilReset > 0) {
         this.timeUntilReset--;
         if (this.timeUntilReset <= 0) {
            if (this.needsInitilization) {
               for (MerchantRecipe var7 : this.buyingList) {
                  if (var7.isRecipeDisabled()) {
                     var7.increaseMaxTradeUses(this.rand.nextInt(6) + this.rand.nextInt(6) + 2);
                  }
               }

               this.populateBuyingList();
               this.needsInitilization = false;
               if (this.villageObj != null && this.lastBuyingPlayer != null) {
                  this.worldObj.setEntityState(this, (byte)14);
                  this.villageObj.setReputationForPlayer(this.lastBuyingPlayer, 1);
               }
            }

            this.addPotionEffect(new PotionEffect(Potion.regeneration.id, 200, 0));
         }
      }

      super.updateAITasks();
   }

   @Override
   public boolean interact(EntityPlayer var1) {
      ItemStack var5 = var1.inventory.getCurrentItem();
      boolean var6 = var5 != null && var5.getItem() == Items.spawn_egg;
      if (!var6 && this.isEntityAlive() && !this.isTrading() && !this.isChild()) {
         if (!this.worldObj.isRemote && (this.buyingList == null || this.buyingList.size() > 0)) {
            this.setCustomer(var1);
            var1.displayVillagerTradeGui(this);
         }

         var1.triggerAchievement(StatList.timesTalkedToVillagerStat);
         return true;
      } else {
         return super.interact(var1);
      }
   }

   @Override
   public void entityInit() {
      super.entityInit();
      this.dataWatcher.addObject(16, 0);
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound var1) {
      super.writeEntityToNBT(var1);
      var1.setInteger("Profession", this.getProfession());
      var1.setInteger("Riches", this.wealth);
      var1.setInteger("Career", this.careerId);
      var1.setInteger("CareerLevel", this.careerLevel);
      var1.setBoolean("Willing", this.isWillingToMate);
      if (this.buyingList != null) {
         var1.setTag("Offers", this.buyingList.getRecipiesAsTags());
      }

      NBTTagList var5 = new NBTTagList();

      for (int var6 = 0; var6 < this.villagerInventory.getSizeInventory(); var6++) {
         ItemStack var7 = this.villagerInventory.getStackInSlot(var6);
         if (var7 != null) {
            var5.appendTag(var7.writeToNBT(new NBTTagCompound()));
         }
      }

      var1.setTag("Inventory", var5);
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound var1) {
      super.readEntityFromNBT(var1);
      this.setProfession(var1.getInteger("Profession"));
      this.wealth = var1.getInteger("Riches");
      this.careerId = var1.getInteger("Career");
      this.careerLevel = var1.getInteger("CareerLevel");
      this.isWillingToMate = var1.getBoolean("Willing");
      if (var1.hasKey("Offers", 10)) {
         NBTTagCompound var5 = var1.getCompoundTag("Offers");
         this.buyingList = new MerchantRecipeList(var5);
      }

      NBTTagList var8 = var1.getTagList("Inventory", 10);

      for (int var6 = 0; var6 < var8.tagCount(); var6++) {
         ItemStack var7 = ItemStack.loadItemStackFromNBT(var8.getCompoundTagAt(var6));
         if (var7 != null) {
            this.villagerInventory.func_174894_a(var7);
         }
      }

      this.setCanPickUpLoot(true);
      this.setAdditionalAItasks();
   }

   @Override
   public boolean canDespawn() {
      return false;
   }

   @Override
   public String getLivingSound() {
      return this.isTrading() ? "mob.villager.haggle" : "mob.villager.idle";
   }

   @Override
   public String getHurtSound() {
      return "mob.villager.hit";
   }

   @Override
   public String getDeathSound() {
      return "mob.villager.death";
   }

   public void setProfession(int var1) {
      this.dataWatcher.updateObject(16, var1);
   }

   public int getProfession() {
      return Math.max(this.dataWatcher.getWatchableObjectInt(16) % 5, 0);
   }

   public boolean isMating() {
      return this.isMating;
   }

   public void setMating(boolean var1) {
      this.isMating = var1;
   }

   public void setPlaying(boolean var1) {
      this.isPlaying = var1;
   }

   public boolean isPlaying() {
      return this.isPlaying;
   }

   @Override
   public void setRevengeTarget(EntityLivingBase var1) {
      super.setRevengeTarget(var1);
      if (this.villageObj != null && var1 != null) {
         this.villageObj.addOrRenewAgressor(var1);
         if (var1 instanceof EntityPlayer) {
            byte var5 = -1;
            if (this.isChild()) {
               var5 = -3;
            }

            this.villageObj.setReputationForPlayer(var1.getName(), var5);
            if (this.isEntityAlive()) {
               this.worldObj.setEntityState(this, (byte)13);
            }
         }
      }
   }

   @Override
   public void onDeath(DamageSource var1) {
      if (this.villageObj != null) {
         Entity var5 = var1.getEntity();
         if (var5 != null) {
            if (var5 instanceof EntityPlayer) {
               this.villageObj.setReputationForPlayer(var5.getName(), -2);
            } else if (var5 instanceof IMob) {
               this.villageObj.endMatingSeason();
            }
         } else {
            EntityPlayer var6 = this.worldObj.getClosestPlayerToEntity(this, 16.0);
            if (var6 != null) {
               this.villageObj.endMatingSeason();
            }
         }
      }

      super.onDeath(var1);
   }

   @Override
   public void setCustomer(EntityPlayer var1) {
      this.buyingPlayer = var1;
   }

   @Override
   public EntityPlayer getCustomer() {
      return this.buyingPlayer;
   }

   public boolean isTrading() {
      return this.buyingPlayer != null;
   }

   public boolean getIsWillingToMate(boolean var1) {
      if (!this.isWillingToMate && var1 && this.func_175553_cp()) {
         boolean var5 = false;

         for (int var6 = 0; var6 < this.villagerInventory.getSizeInventory(); var6++) {
            ItemStack var7 = this.villagerInventory.getStackInSlot(var6);
            if (var7 != null) {
               if (var7.getItem() == Items.bread && var7.stackSize >= 3) {
                  var5 = true;
                  this.villagerInventory.decrStackSize(var6, 3);
               } else if ((var7.getItem() == Items.potato || var7.getItem() == Items.carrot) && var7.stackSize >= 12) {
                  var5 = true;
                  this.villagerInventory.decrStackSize(var6, 12);
               }
            }

            if (var5) {
               this.worldObj.setEntityState(this, (byte)18);
               this.isWillingToMate = true;
               break;
            }
         }
      }

      return this.isWillingToMate;
   }

   public void setIsWillingToMate(boolean var1) {
      this.isWillingToMate = var1;
   }

   @Override
   public void useRecipe(MerchantRecipe var1) {
      var1.incrementToolUses();
      this.livingSoundTime = -this.getTalkInterval();
      this.playSound("mob.villager.yes", this.getSoundVolume(), this.getSoundPitch());
      int var5 = 3 + this.rand.nextInt(4);
      if (var1.getToolUses() == 1 || this.rand.nextInt(5) == 0) {
         this.timeUntilReset = 40;
         this.needsInitilization = true;
         this.isWillingToMate = true;
         if (this.buyingPlayer != null) {
            this.lastBuyingPlayer = this.buyingPlayer.getName();
         } else {
            this.lastBuyingPlayer = null;
         }

         var5 += 5;
      }

      if (var1.getItemToBuy().getItem() == Items.emerald) {
         this.wealth = this.wealth + var1.getItemToBuy().stackSize;
      }

      if (var1.getRewardsExp()) {
         this.worldObj.spawnEntityInWorld(new EntityXPOrb(this.worldObj, this.posX, this.posY + 0.5, this.posZ, var5));
      }
   }

   @Override
   public void verifySellingItem(ItemStack var1) {
      if (!this.worldObj.isRemote && this.livingSoundTime > -this.getTalkInterval() + 20) {
         this.livingSoundTime = -this.getTalkInterval();
         if (var1 != null) {
            this.playSound("mob.villager.yes", this.getSoundVolume(), this.getSoundPitch());
         } else {
            this.playSound("mob.villager.no", this.getSoundVolume(), this.getSoundPitch());
         }
      }
   }

   @Override
   public MerchantRecipeList getRecipes(EntityPlayer var1) {
      if (this.buyingList == null) {
         this.populateBuyingList();
      }

      return this.buyingList;
   }

   private void populateBuyingList() {
      EntityVillager.ITradeList[][][] var4 = DEFAULT_TRADE_LIST_MAP[this.getProfession()];
      if (this.careerId != 0 && this.careerLevel != 0) {
         this.careerLevel++;
      } else {
         this.careerId = this.rand.nextInt(var4.length) + 1;
         this.careerLevel = 1;
      }

      if (this.buyingList == null) {
         this.buyingList = new MerchantRecipeList();
      }

      int var5 = this.careerId - 1;
      int var6 = this.careerLevel - 1;
      EntityVillager.ITradeList[][] var7 = var4[var5];
      if (var6 >= 0 && var6 < var7.length) {
         EntityVillager.ITradeList[] var8 = var7[var6];

         for (EntityVillager.ITradeList var12 : var8) {
            var12.modifyMerchantRecipeList(this.buyingList, this.rand);
         }
      }
   }

   @Override
   public void setRecipes(MerchantRecipeList var1) {
   }

   @Override
   public IChatComponent getDisplayName() {
      String var4 = this.getCustomNameTag();
      if (var4 != null && var4.length() > 0) {
         ChatComponentText var7 = new ChatComponentText(var4);
         var7.getChatStyle().setChatHoverEvent(this.getHoverEvent());
         var7.getChatStyle().setInsertion(this.getUniqueID().toString());
         return var7;
      }

      if (this.buyingList == null) {
         this.populateBuyingList();
      }

      String var5 = null;
      switch (this.getProfession()) {
         case 0:
            if (this.careerId == 1) {
               var5 = "farmer";
            } else if (this.careerId == 2) {
               var5 = "fisherman";
            } else if (this.careerId == 3) {
               var5 = "shepherd";
            } else if (this.careerId == 4) {
               var5 = "fletcher";
            }
            break;
         case 1:
            var5 = "librarian";
            break;
         case 2:
            var5 = "cleric";
            break;
         case 3:
            if (this.careerId == 1) {
               var5 = "armor";
            } else if (this.careerId == 2) {
               var5 = "weapon";
            } else if (this.careerId == 3) {
               var5 = "tool";
            }
            break;
         case 4:
            if (this.careerId == 1) {
               var5 = "butcher";
            } else if (this.careerId == 2) {
               var5 = "leather";
            }
      }

      if (var5 != null) {
         ChatComponentTranslation var6 = new ChatComponentTranslation("entity.Villager." + var5);
         var6.getChatStyle().setChatHoverEvent(this.getHoverEvent());
         var6.getChatStyle().setInsertion(this.getUniqueID().toString());
         return var6;
      } else {
         return super.getDisplayName();
      }
   }

   @Override
   public float getEyeHeight() {
      float var3 = 1.62F;
      if (this.isChild()) {
         var3 = (float)(var3 - 0.81);
      }

      return var3;
   }

   @Override
   public void handleStatusUpdate(byte var1) {
      if (var1 == 12) {
         this.spawnParticles(EnumParticleTypes.HEART);
      } else if (var1 == 13) {
         this.spawnParticles(EnumParticleTypes.VILLAGER_ANGRY);
      } else if (var1 == 14) {
         this.spawnParticles(EnumParticleTypes.VILLAGER_HAPPY);
      } else {
         super.handleStatusUpdate(var1);
      }
   }

   private void spawnParticles(EnumParticleTypes var1) {
      for (int var5 = 0; var5 < 5; var5++) {
         double var6 = this.rand.nextGaussian() * 0.02;
         double var8 = this.rand.nextGaussian() * 0.02;
         double var10 = this.rand.nextGaussian() * 0.02;
         this.worldObj
            .spawnParticle(
               var1,
               this.posX + this.rand.nextFloat() * this.width * 2.0F - this.width,
               this.posY + 1.0 + this.rand.nextFloat() * this.height,
               this.posZ + this.rand.nextFloat() * this.width * 2.0F - this.width,
               var6,
               var8,
               var10
            );
      }
   }

   @Override
   public IEntityLivingData onInitialSpawn(DifficultyInstance var1, IEntityLivingData var2) {
      var2 = super.onInitialSpawn(var1, var2);
      this.setProfession(this.worldObj.rand.nextInt(5));
      this.setAdditionalAItasks();
      return var2;
   }

   public void setLookingForHome() {
      this.isLookingForHome = true;
   }

   public EntityVillager createChild(EntityAgeable var1) {
      EntityVillager var2 = new EntityVillager(this.worldObj);
      var2.onInitialSpawn(this.worldObj.getDifficultyForLocation(new BlockPos(var2)), (IEntityLivingData)null);
      return var2;
   }

   @Override
   public boolean allowLeashing() {
      return false;
   }

   @Override
   public void onStruckByLightning(EntityLightningBolt var1) {
      if (!this.worldObj.isRemote && !this.isDead) {
         EntityWitch var5 = new EntityWitch(this.worldObj);
         var5.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
         var5.onInitialSpawn(this.worldObj.getDifficultyForLocation(new BlockPos(var5)), (IEntityLivingData)null);
         var5.setNoAI(this.isAIDisabled());
         if (this.hasCustomName()) {
            var5.setCustomNameTag(this.getCustomNameTag());
            var5.setAlwaysRenderNameTag(this.getAlwaysRenderNameTag());
         }

         this.worldObj.spawnEntityInWorld(var5);
         this.setDead();
      }
   }

   public InventoryBasic getVillagerInventory() {
      return this.villagerInventory;
   }

   @Override
   public void updateEquipmentIfNeeded(EntityItem var1) {
      ItemStack var5 = var1.getEntityItem();
      Item var6 = var5.getItem();
      if (this.canVillagerPickupItem(var6)) {
         ItemStack var7 = this.villagerInventory.func_174894_a(var5);
         if (var7 == null) {
            var1.setDead();
         } else {
            var5.stackSize = var7.stackSize;
         }
      }
   }

   private boolean canVillagerPickupItem(Item var1) {
      return var1 == Items.bread || var1 == Items.potato || var1 == Items.carrot || var1 == Items.wheat || var1 == Items.wheat_seeds;
   }

   public boolean func_175553_cp() {
      return this.hasEnoughItems(1);
   }

   public boolean canAbondonItems() {
      return this.hasEnoughItems(2);
   }

   public boolean func_175557_cr() {
      boolean var4 = this.getProfession() == 0;
      return var4 ? !this.hasEnoughItems(5) : !this.hasEnoughItems(1);
   }

   private boolean hasEnoughItems(int var1) {
      boolean var5 = this.getProfession() == 0;

      for (int var6 = 0; var6 < this.villagerInventory.getSizeInventory(); var6++) {
         ItemStack var7 = this.villagerInventory.getStackInSlot(var6);
         if (var7 != null) {
            if (var7.getItem() == Items.bread && var7.stackSize >= 3 * var1
               || var7.getItem() == Items.potato && var7.stackSize >= 12 * var1
               || var7.getItem() == Items.carrot && var7.stackSize >= 12 * var1) {
               return true;
            }

            if (var5 && var7.getItem() == Items.wheat && var7.stackSize >= 9 * var1) {
               return true;
            }
         }
      }

      return false;
   }

   public boolean isFarmItemInInventory() {
      for (int var4 = 0; var4 < this.villagerInventory.getSizeInventory(); var4++) {
         ItemStack var5 = this.villagerInventory.getStackInSlot(var4);
         if (var5 != null && (var5.getItem() == Items.wheat_seeds || var5.getItem() == Items.potato || var5.getItem() == Items.carrot)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean replaceItemInInventory(int var1, ItemStack var2) {
      if (super.replaceItemInInventory(var1, var2)) {
         return true;
      } else {
         int var6 = var1 - 300;
         if (var6 >= 0 && var6 < this.villagerInventory.getSizeInventory()) {
            this.villagerInventory.setInventorySlotContents(var6, var2);
            return true;
         } else {
            return false;
         }
      }
   }

   public static class EmeraldForItems implements EntityVillager.ITradeList {
      public Item sellItem;
      public EntityVillager.PriceInfo price;
      private static final long a = 14188117664471L;

      public EmeraldForItems(Item var1, EntityVillager.PriceInfo var2) {
         this.sellItem = var1;
         this.price = var2;
      }

      @Override
      public void modifyMerchantRecipeList(MerchantRecipeList var1, Random var2) {
         int var6 = 1;
         if (this.price != null) {
            var6 = this.price.getPrice(var2);
         }

         var1.add(new MerchantRecipe(new ItemStack(this.sellItem, var6, 0), Items.emerald));
      }
   }

   public interface ITradeList {
      void modifyMerchantRecipeList(MerchantRecipeList var1, Random var2);
   }

   public static class ItemAndEmeraldToItem implements EntityVillager.ITradeList {
      public ItemStack buyingItemStack;
      public EntityVillager.PriceInfo buyingPriceInfo;
      public ItemStack sellingItemstack;
      public EntityVillager.PriceInfo field_179408_d;
      private static final long a = 101864719858829L;

      public ItemAndEmeraldToItem(Item var1, EntityVillager.PriceInfo var2, Item var3, EntityVillager.PriceInfo var4) {
         this.buyingItemStack = new ItemStack(var1);
         this.buyingPriceInfo = var2;
         this.sellingItemstack = new ItemStack(var3);
         this.field_179408_d = var4;
      }

      @Override
      public void modifyMerchantRecipeList(MerchantRecipeList var1, Random var2) {
         int var6 = 1;
         if (this.buyingPriceInfo != null) {
            var6 = this.buyingPriceInfo.getPrice(var2);
         }

         int var7 = 1;
         if (this.field_179408_d != null) {
            var7 = this.field_179408_d.getPrice(var2);
         }

         var1.add(
            new MerchantRecipe(
               new ItemStack(this.buyingItemStack.getItem(), var6, this.buyingItemStack.getMetadata()),
               new ItemStack(Items.emerald),
               new ItemStack(this.sellingItemstack.getItem(), var7, this.sellingItemstack.getMetadata())
            )
         );
      }
   }

   public static class ListEnchantedBookForEmeralds implements EntityVillager.ITradeList {
      private static final long a = 137297919037171L;

      @Override
      public void modifyMerchantRecipeList(MerchantRecipeList var1, Random var2) {
         Enchantment var6 = Enchantment.enchantmentsBookList[var2.nextInt(Enchantment.enchantmentsBookList.length)];
         int var7 = MathHelper.getRandomIntegerInRange(var2, var6.getMinLevel(), var6.getMaxLevel());
         ItemStack var8 = Items.enchanted_book.getEnchantedItemStack(new EnchantmentData(var6, var7));
         int var9 = 2 + var2.nextInt(5 + var7 * 10) + 3 * var7;
         if (var9 > 64) {
            var9 = 64;
         }

         var1.add(new MerchantRecipe(new ItemStack(Items.book), new ItemStack(Items.emerald, var9), var8));
      }
   }

   public static class ListEnchantedItemForEmeralds implements EntityVillager.ITradeList {
      public ItemStack enchantedItemStack;
      public EntityVillager.PriceInfo priceInfo;
      private static final long a = 22539798456444L;
      private static final long b = -638612833201815537L;

      public ListEnchantedItemForEmeralds(Item var1, EntityVillager.PriceInfo var2) {
         this.enchantedItemStack = new ItemStack(var1);
         this.priceInfo = var2;
      }

      @Override
      public void modifyMerchantRecipeList(MerchantRecipeList var1, Random var2) {
         int var6 = 1;
         if (this.priceInfo != null) {
            var6 = this.priceInfo.getPrice(var2);
         }

         ItemStack var7 = new ItemStack(Items.emerald, var6, 0);
         ItemStack var8 = new ItemStack(this.enchantedItemStack.getItem(), 1, this.enchantedItemStack.getMetadata());
         var8 = EnchantmentHelper.addRandomEnchantment(var2, var8, 5 + var2.nextInt((int)b));
         var1.add(new MerchantRecipe(var7, var8));
      }
   }

   public static class ListItemForEmeralds implements EntityVillager.ITradeList {
      public ItemStack itemToBuy;
      public EntityVillager.PriceInfo priceInfo;
      private static final long a = 92499067714708L;

      public ListItemForEmeralds(Item var1, EntityVillager.PriceInfo var2) {
         this.itemToBuy = new ItemStack(var1);
         this.priceInfo = var2;
      }

      public ListItemForEmeralds(ItemStack var1, EntityVillager.PriceInfo var2) {
         this.itemToBuy = var1;
         this.priceInfo = var2;
      }

      @Override
      public void modifyMerchantRecipeList(MerchantRecipeList var1, Random var2) {
         int var6 = 1;
         if (this.priceInfo != null) {
            var6 = this.priceInfo.getPrice(var2);
         }

         ItemStack var7;
         ItemStack var8;
         if (var6 < 0) {
            var7 = new ItemStack(Items.emerald, 1, 0);
            var8 = new ItemStack(this.itemToBuy.getItem(), -var6, this.itemToBuy.getMetadata());
         } else {
            var7 = new ItemStack(Items.emerald, var6, 0);
            var8 = new ItemStack(this.itemToBuy.getItem(), 1, this.itemToBuy.getMetadata());
         }

         var1.add(new MerchantRecipe(var7, var8));
      }
   }

   public static class PriceInfo extends Tuple<Integer, Integer> {
      private static final long a = 134305501741014L;

      public PriceInfo(int var1, int var2) {
         super(var1, var2);
      }

      public int getPrice(Random var1) {
         return this.getFirst() >= this.getSecond() ? this.getFirst() : this.getFirst() + var1.nextInt(this.getSecond() - this.getFirst() + 1);
      }
   }
}
