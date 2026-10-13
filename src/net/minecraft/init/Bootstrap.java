package net.minecraft.init;

import bypass.module.visuals.HUD;
import com.mojang.authlib.GameProfile;
import java.io.PrintStream;
import java.util.Random;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockFire;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockPumpkin;
import net.minecraft.block.BlockSkull;
import net.minecraft.block.BlockTNT;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.LoggingPrintStream;
import net.minecraft.util.StringUtils;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Bootstrap {
   private static final PrintStream SYSOUT = System.out;
   private static boolean alreadyRegistered = false;
   private static final Logger LOGGER = LogManager.getLogger();
   private static final long a = 5940285432529L;

   public static boolean isRegistered() {
      return alreadyRegistered;
   }

   public static void registerDispenserBehaviors() {
      BlockDispenser.dispenseBehaviorRegistry.putObject(Items.arrow, new BehaviorProjectileDispense() {
         @Override
         public IProjectile getProjectileEntity(World var1, IPosition var2) {
            EntityArrow var3 = new EntityArrow(var1, var2.getX(), var2.getY(), var2.getZ());
            var3.canBePickedUp = 1;
            return var3;
         }
      });
      BlockDispenser.dispenseBehaviorRegistry.putObject(Items.egg, new BehaviorProjectileDispense() {
         @Override
         public IProjectile getProjectileEntity(World var1, IPosition var2) {
            return new EntityEgg(var1, var2.getX(), var2.getY(), var2.getZ());
         }
      });
      BlockDispenser.dispenseBehaviorRegistry.putObject(Items.snowball, new BehaviorProjectileDispense() {
         @Override
         public IProjectile getProjectileEntity(World var1, IPosition var2) {
            return new EntitySnowball(var1, var2.getX(), var2.getY(), var2.getZ());
         }
      });
      BlockDispenser.dispenseBehaviorRegistry.putObject(Items.experience_bottle, new BehaviorProjectileDispense() {
         @Override
         public IProjectile getProjectileEntity(World var1, IPosition var2) {
            return new EntityExpBottle(var1, var2.getX(), var2.getY(), var2.getZ());
         }

         @Override
         public float func_82498_a() {
            return super.func_82498_a() * 0.5F;
         }

         @Override
         public float func_82500_b() {
            return super.func_82500_b() * 1.25F;
         }
      });
      BlockDispenser.dispenseBehaviorRegistry.putObject(Items.potionitem, new IBehaviorDispenseItem() {
         private final BehaviorDefaultDispenseItem field_150843_b = new BehaviorDefaultDispenseItem();
         private static final long a = 70477952389734L;

         @Override
         public ItemStack dispense(IBlockSource var1, final ItemStack var2) {
            return ItemPotion.isSplash(var2.getMetadata()) ? (new BehaviorProjectileDispense() {
               @Override
               public IProjectile getProjectileEntity(World var1, IPosition var2x) {
                  return new EntityPotion(var1, var2x.getX(), var2x.getY(), var2x.getZ(), var2.copy());
               }

               @Override
               public float func_82498_a() {
                  return super.func_82498_a() * 0.5F;
               }

               @Override
               public float func_82500_b() {
                  return super.func_82500_b() * 1.25F;
               }
            }).dispense(var1, var2) : this.field_150843_b.dispense(var1, var2);
         }
      });
      BlockDispenser.dispenseBehaviorRegistry.putObject(Items.spawn_egg, new BehaviorDefaultDispenseItem() {
         private static final long b = 78067703884201L;

         @Override
         public ItemStack dispenseStack(IBlockSource var1, ItemStack var2) {
            EnumFacing var6 = BlockDispenser.getFacing(var1.getBlockMetadata());
            double var7 = var1.getX() + var6.getFrontOffsetX();
            double var9 = var1.getBlockPos().getY() + 0.2F;
            double var11 = var1.getZ() + var6.getFrontOffsetZ();
            Entity var13 = ItemMonsterPlacer.spawnCreature(var1.getWorld(), var2.getMetadata(), var7, var9, var11);
            if (var13 instanceof EntityLivingBase && var2.hasDisplayName()) {
               ((EntityLiving)var13).setCustomNameTag(var2.getDisplayName());
            }

            var2.splitStack(1);
            return var2;
         }
      });
      BlockDispenser.dispenseBehaviorRegistry.putObject(Items.fireworks, new BehaviorDefaultDispenseItem() {
         private static final long b = 49479359609860L;
         private static final long c = 1825254924913476586L;

         @Override
         public ItemStack dispenseStack(IBlockSource var1, ItemStack var2) {
            EnumFacing var3 = BlockDispenser.getFacing(var1.getBlockMetadata());
            double var4 = var1.getX() + var3.getFrontOffsetX();
            double var6 = var1.getBlockPos().getY() + 0.2F;
            double var8 = var1.getZ() + var3.getFrontOffsetZ();
            EntityFireworkRocket var10 = new EntityFireworkRocket(var1.getWorld(), var4, var6, var8, var2);
            var1.getWorld().spawnEntityInWorld(var10);
            var2.splitStack(1);
            return var2;
         }

         @Override
         public void playDispenseSound(IBlockSource var1) {
            var1.getWorld().playAuxSFX((int)c, var1.getBlockPos(), 0);
         }
      });
      BlockDispenser.dispenseBehaviorRegistry.putObject(Items.fire_charge, new BehaviorDefaultDispenseItem() {
         private static final long b = 106517926278307L;
         private static final long c = 7390723583439274993L;

         @Override
         public ItemStack dispenseStack(IBlockSource var1, ItemStack var2) {
            EnumFacing var6 = BlockDispenser.getFacing(var1.getBlockMetadata());
            IPosition var7 = BlockDispenser.getDispensePosition(var1);
            double var8 = var7.getX() + var6.getFrontOffsetX() * 0.3F;
            double var10 = var7.getY() + var6.getFrontOffsetY() * 0.3F;
            double var12 = var7.getZ() + var6.getFrontOffsetZ() * 0.3F;
            World var14 = var1.getWorld();
            Random var15 = var14.rand;
            double var16 = var15.nextGaussian() * 0.05 + var6.getFrontOffsetX();
            double var18 = var15.nextGaussian() * 0.05 + var6.getFrontOffsetY();
            double var20 = var15.nextGaussian() * 0.05 + var6.getFrontOffsetZ();
            var14.spawnEntityInWorld(new EntitySmallFireball(var14, var8, var10, var12, var16, var18, var20));
            var2.splitStack(1);
            return var2;
         }

         @Override
         public void playDispenseSound(IBlockSource var1) {
            var1.getWorld().playAuxSFX((int)c, var1.getBlockPos(), 0);
         }
      });
      BlockDispenser.dispenseBehaviorRegistry.putObject(Items.boat, new BehaviorDefaultDispenseItem() {
         private final BehaviorDefaultDispenseItem field_150842_b = new BehaviorDefaultDispenseItem();
         private static final long b = 107772798036385L;
         private static final long c = 7084252489771385832L;

         @Override
         public ItemStack dispenseStack(IBlockSource var1, ItemStack var2) {
            EnumFacing var6 = BlockDispenser.getFacing(var1.getBlockMetadata());
            World var7 = var1.getWorld();
            double var8 = var1.getX() + var6.getFrontOffsetX() * 1.125F;
            double var10 = var1.getY() + var6.getFrontOffsetY() * 1.125F;
            double var12 = var1.getZ() + var6.getFrontOffsetZ() * 1.125F;
            BlockPos var14 = var1.getBlockPos().offset(var6);
            Material var15 = var7.getBlockState(var14).getBlock().getMaterial();
            double var16;
            if (Material.water.equals(var15)) {
               var16 = 1.0;
            } else {
               if (!Material.air.equals(var15) || !Material.water.equals(var7.getBlockState(var14.down()).getBlock().getMaterial())) {
                  return this.field_150842_b.dispense(var1, var2);
               }

               var16 = 0.0;
            }

            EntityBoat var18 = new EntityBoat(var7, var8, var10 + var16, var12);
            var7.spawnEntityInWorld(var18);
            var2.splitStack(1);
            return var2;
         }

         @Override
         public void playDispenseSound(IBlockSource var1) {
            var1.getWorld().playAuxSFX((int)c, var1.getBlockPos(), 0);
         }
      });
      BehaviorDefaultDispenseItem var4 = new BehaviorDefaultDispenseItem() {
         private final BehaviorDefaultDispenseItem field_150841_b = new BehaviorDefaultDispenseItem();
         private static final long b = 49241626613634L;

         @Override
         public ItemStack dispenseStack(IBlockSource var1, ItemStack var2) {
            ItemBucket var6 = (ItemBucket)var2.getItem();
            BlockPos var7 = var1.getBlockPos().offset(BlockDispenser.getFacing(var1.getBlockMetadata()));
            if (var6.tryPlaceContainedLiquid(var1.getWorld(), var7)) {
               var2.setItem(Items.bucket);
               var2.stackSize = 1;
               return var2;
            } else {
               return this.field_150841_b.dispense(var1, var2);
            }
         }
      };
      BlockDispenser.dispenseBehaviorRegistry.putObject(Items.lava_bucket, var4);
      BlockDispenser.dispenseBehaviorRegistry.putObject(Items.water_bucket, var4);
      BlockDispenser.dispenseBehaviorRegistry.putObject(Items.bucket, new BehaviorDefaultDispenseItem() {
         private final BehaviorDefaultDispenseItem field_150840_b = new BehaviorDefaultDispenseItem();
         private static final long b = 4532547767672L;

         @Override
         public ItemStack dispenseStack(IBlockSource var1, ItemStack var2) {
            World var6 = var1.getWorld();
            BlockPos var7 = var1.getBlockPos().offset(BlockDispenser.getFacing(var1.getBlockMetadata()));
            IBlockState var8 = var6.getBlockState(var7);
            Block var9 = var8.getBlock();
            Material var10 = var9.getMaterial();
            Item var11;
            if (Material.water.equals(var10) && var9 instanceof BlockLiquid && var8.getValue(BlockLiquid.LEVEL) == 0) {
               var11 = Items.water_bucket;
            } else {
               if (!Material.lava.equals(var10) || !(var9 instanceof BlockLiquid) || var8.getValue(BlockLiquid.LEVEL) != 0) {
                  return super.dispenseStack(var1, var2);
               }

               var11 = Items.lava_bucket;
            }

            var6.setBlockToAir(var7);
            if (--var2.stackSize == 0) {
               var2.setItem(var11);
               var2.stackSize = 1;
            } else if (var1.<TileEntityDispenser>getBlockTileEntity().addItemStack(new ItemStack(var11)) < 0) {
               this.field_150840_b.dispense(var1, new ItemStack(var11));
            }

            return var2;
         }
      });
      BlockDispenser.dispenseBehaviorRegistry.putObject(Items.flint_and_steel, new BehaviorDefaultDispenseItem() {
         private boolean field_150839_b = true;
         private static final long b = 24054458888466L;

         @Override
         public ItemStack dispenseStack(IBlockSource var1, ItemStack var2) {
            World var6 = var1.getWorld();
            BlockPos var7 = var1.getBlockPos().offset(BlockDispenser.getFacing(var1.getBlockMetadata()));
            if (var6.isAirBlock(var7)) {
               var6.setBlockState(var7, Blocks.fire.getDefaultState());
               if (var2.attemptDamageItem(1, var6.rand)) {
                  var2.stackSize = 0;
               }
            } else if (var6.getBlockState(var7).getBlock() == Blocks.tnt) {
               Blocks.tnt.onBlockDestroyedByPlayer(var6, var7, Blocks.tnt.getDefaultState().withProperty(BlockTNT.EXPLODE, true));
               var6.setBlockToAir(var7);
            } else {
               this.field_150839_b = false;
            }

            return var2;
         }

         @Override
         public void playDispenseSound(IBlockSource var1) {
            if (this.field_150839_b) {
               var1.getWorld().playAuxSFX(1000, var1.getBlockPos(), 0);
            } else {
               var1.getWorld().playAuxSFX(1001, var1.getBlockPos(), 0);
            }
         }
      });
      BlockDispenser.dispenseBehaviorRegistry.putObject(Items.dye, new BehaviorDefaultDispenseItem() {
         private boolean field_150838_b = true;
         private static final long b = 83328342553774L;

         @Override
         public ItemStack dispenseStack(IBlockSource var1, ItemStack var2) {
            if (EnumDyeColor.WHITE == EnumDyeColor.byDyeDamage(var2.getMetadata())) {
               World var6 = var1.getWorld();
               BlockPos var7 = var1.getBlockPos().offset(BlockDispenser.getFacing(var1.getBlockMetadata()));
               if (ItemDye.applyBonemeal(var2, var6, var7)) {
                  if (!var6.isRemote) {
                     var6.playAuxSFX(2005, var7, 0);
                  }
               } else {
                  this.field_150838_b = false;
               }

               return var2;
            } else {
               return super.dispenseStack(var1, var2);
            }
         }

         @Override
         public void playDispenseSound(IBlockSource var1) {
            if (this.field_150838_b) {
               var1.getWorld().playAuxSFX(1000, var1.getBlockPos(), 0);
            } else {
               var1.getWorld().playAuxSFX(1001, var1.getBlockPos(), 0);
            }
         }
      });
      BlockDispenser.dispenseBehaviorRegistry.putObject(Item.getItemFromBlock(Blocks.tnt), new BehaviorDefaultDispenseItem() {
         private static final long b = 53035816366307L;

         @Override
         public ItemStack dispenseStack(IBlockSource var1, ItemStack var2) {
            World var5 = var1.getWorld();
            BlockPos var6 = var1.getBlockPos().offset(BlockDispenser.getFacing(var1.getBlockMetadata()));
            EntityTNTPrimed var7 = new EntityTNTPrimed(var5, var6.getX() + 0.5, var6.getY(), var6.getZ() + 0.5, (EntityLivingBase)null);
            var5.spawnEntityInWorld(var7);
            var5.playSoundAtEntity(var7, "game.tnt.primed", 1.0F, 1.0F);
            var2.stackSize--;
            return var2;
         }
      });
      BlockDispenser.dispenseBehaviorRegistry.putObject(Items.skull, new BehaviorDefaultDispenseItem() {
         private boolean field_179240_b = true;
         private static final long b = 131181088363358L;

         @Override
         public ItemStack dispenseStack(IBlockSource var1, ItemStack var2) {
            World var6 = var1.getWorld();
            EnumFacing var7 = BlockDispenser.getFacing(var1.getBlockMetadata());
            BlockPos var8 = var1.getBlockPos().offset(var7);
            BlockSkull var9 = Blocks.skull;
            if (!var6.isAirBlock(var8) || !var9.canDispenserPlace(var6, var8, var2)) {
               this.field_179240_b = false;
            } else if (!var6.isRemote) {
               var6.setBlockState(var8, var9.getDefaultState().withProperty(BlockSkull.FACING, EnumFacing.UP), 3);
               TileEntity var10 = var6.getTileEntity(var8);
               if (var10 instanceof TileEntitySkull) {
                  if (var2.getMetadata() == 3) {
                     GameProfile var11 = null;
                     if (var2.hasTagCompound()) {
                        NBTTagCompound var12 = var2.getTagCompound();
                        if (var12.hasKey("SkullOwner", 10)) {
                           var11 = NBTUtil.readGameProfileFromNBT(var12.getCompoundTag("SkullOwner"));
                        } else if (var12.hasKey("SkullOwner", 8)) {
                           String var13 = var12.getString("SkullOwner");
                           if (!StringUtils.isNullOrEmpty(var13)) {
                              var11 = new GameProfile((UUID)null, var13);
                           }
                        }
                     }

                     ((TileEntitySkull)var10).setPlayerProfile(var11);
                  } else {
                     ((TileEntitySkull)var10).setType(var2.getMetadata());
                  }

                  ((TileEntitySkull)var10).setSkullRotation(var7.getOpposite().getHorizontalIndex() * 4);
                  Blocks.skull.checkWitherSpawn(var6, var8, (TileEntitySkull)var10);
               }

               var2.stackSize--;
            }

            return var2;
         }

         @Override
         public void playDispenseSound(IBlockSource var1) {
            if (this.field_179240_b) {
               var1.getWorld().playAuxSFX(1000, var1.getBlockPos(), 0);
            } else {
               var1.getWorld().playAuxSFX(1001, var1.getBlockPos(), 0);
            }
         }
      });
      BlockDispenser.dispenseBehaviorRegistry.putObject(Item.getItemFromBlock(Blocks.pumpkin), new BehaviorDefaultDispenseItem() {
         private boolean field_179241_b = true;
         private static final long b = 57079700377394L;

         @Override
         public ItemStack dispenseStack(IBlockSource var1, ItemStack var2) {
            World var6 = var1.getWorld();
            BlockPos var7 = var1.getBlockPos().offset(BlockDispenser.getFacing(var1.getBlockMetadata()));
            BlockPumpkin var8 = (BlockPumpkin)Blocks.pumpkin;
            if (var6.isAirBlock(var7) && var8.canDispenserPlace(var6, var7)) {
               if (!var6.isRemote) {
                  var6.setBlockState(var7, var8.getDefaultState(), 3);
               }

               var2.stackSize--;
            } else {
               this.field_179241_b = false;
            }

            return var2;
         }

         @Override
         public void playDispenseSound(IBlockSource var1) {
            if (this.field_179241_b) {
               var1.getWorld().playAuxSFX(1000, var1.getBlockPos(), 0);
            } else {
               var1.getWorld().playAuxSFX(1001, var1.getBlockPos(), 0);
            }
         }
      });
   }

   public static void register() {
      if (!alreadyRegistered) {
         alreadyRegistered = true;
         if (LOGGER.isDebugEnabled()) {
            redirectOutputToLog();
         }

         Block.registerBlocks();
         BlockFire.init();
         Item.registerItems();
         StatList.init();
         registerDispenserBehaviors();
      }
   }

   private static void redirectOutputToLog() {
      System.setErr(new LoggingPrintStream("STDERR", System.err));
      System.setOut(new LoggingPrintStream("STDOUT", SYSOUT));
   }

   public static void printToSYSOUT(String var0) {
      if (HUD.isDevelopment) {
         SYSOUT.println(var0);
      }
   }
}
