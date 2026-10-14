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

   public static boolean isRegistered() {
      return alreadyRegistered;
   }

   public static void registerDispenserBehaviors() {
      BlockDispenser.dispenseBehaviorRegistry.putObject(Items.arrow, new BehaviorProjectileDispense() {
         @Override
         public IProjectile getProjectileEntity(World worldIn, IPosition position) {
            EntityArrow entityArrow = new EntityArrow(worldIn, position.getX(), position.getY(), position.getZ());
            entityArrow.canBePickedUp = 1;
            return entityArrow;
         }
      });
      BlockDispenser.dispenseBehaviorRegistry.putObject(Items.egg, new BehaviorProjectileDispense() {
         @Override
         public IProjectile getProjectileEntity(World worldIn, IPosition position) {
            return new EntityEgg(worldIn, position.getX(), position.getY(), position.getZ());
         }
      });
      BlockDispenser.dispenseBehaviorRegistry.putObject(Items.snowball, new BehaviorProjectileDispense() {
         @Override
         public IProjectile getProjectileEntity(World worldIn, IPosition position) {
            return new EntitySnowball(worldIn, position.getX(), position.getY(), position.getZ());
         }
      });
      BlockDispenser.dispenseBehaviorRegistry.putObject(Items.experience_bottle, new BehaviorProjectileDispense() {
         @Override
         public IProjectile getProjectileEntity(World worldIn, IPosition position) {
            return new EntityExpBottle(worldIn, position.getX(), position.getY(), position.getZ());
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

         @Override
         public ItemStack dispense(IBlockSource source, final ItemStack stack) {
            return ItemPotion.isSplash(stack.getMetadata()) ? (new BehaviorProjectileDispense() {
               @Override
               public IProjectile getProjectileEntity(World worldIn, IPosition position) {
                  return new EntityPotion(worldIn, position.getX(), position.getY(), position.getZ(), stack.copy());
               }

               @Override
               public float func_82498_a() {
                  return super.func_82498_a() * 0.5F;
               }

               @Override
               public float func_82500_b() {
                  return super.func_82500_b() * 1.25F;
               }
            }).dispense(source, stack) : this.field_150843_b.dispense(source, stack);
         }
      });
      BlockDispenser.dispenseBehaviorRegistry.putObject(Items.spawn_egg, new BehaviorDefaultDispenseItem() {
         @Override
         public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
            EnumFacing enumFacing = BlockDispenser.getFacing(source.getBlockMetadata());
            double d = source.getX() + enumFacing.getFrontOffsetX();
            double e = source.getBlockPos().getY() + 0.2F;
            double d2 = source.getZ() + enumFacing.getFrontOffsetZ();
            Entity entity = ItemMonsterPlacer.spawnCreature(source.getWorld(), stack.getMetadata(), d, e, d2);
            if (entity instanceof EntityLivingBase && stack.hasDisplayName()) {
               ((EntityLiving)entity).setCustomNameTag(stack.getDisplayName());
            }

            stack.splitStack(1);
            return stack;
         }
      });
      BlockDispenser.dispenseBehaviorRegistry.putObject(Items.fireworks, new BehaviorDefaultDispenseItem() {
         @Override
         public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
            EnumFacing enumFacing2 = BlockDispenser.getFacing(source.getBlockMetadata());
            double d3 = source.getX() + enumFacing2.getFrontOffsetX();
            double d4 = source.getBlockPos().getY() + 0.2F;
            double d5 = source.getZ() + enumFacing2.getFrontOffsetZ();
            EntityFireworkRocket entityFireworkRocket = new EntityFireworkRocket(source.getWorld(), d3, d4, d5, stack);
            source.getWorld().spawnEntityInWorld(entityFireworkRocket);
            stack.splitStack(1);
            return stack;
         }

         @Override
         public void playDispenseSound(IBlockSource source) {
            source.getWorld().playAuxSFX(1002, source.getBlockPos(), 0);
         }
      });
      BlockDispenser.dispenseBehaviorRegistry.putObject(Items.fire_charge, new BehaviorDefaultDispenseItem() {
         @Override
         public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
            EnumFacing enumFacing3 = BlockDispenser.getFacing(source.getBlockMetadata());
            IPosition iPosition = BlockDispenser.getDispensePosition(source);
            double d6 = iPosition.getX() + enumFacing3.getFrontOffsetX() * 0.3F;
            double d7 = iPosition.getY() + enumFacing3.getFrontOffsetY() * 0.3F;
            double d8 = iPosition.getZ() + enumFacing3.getFrontOffsetZ() * 0.3F;
            World world = source.getWorld();
            Random random = world.rand;
            double d9 = random.nextGaussian() * 0.05 + enumFacing3.getFrontOffsetX();
            double d10 = random.nextGaussian() * 0.05 + enumFacing3.getFrontOffsetY();
            double d11 = random.nextGaussian() * 0.05 + enumFacing3.getFrontOffsetZ();
            world.spawnEntityInWorld(new EntitySmallFireball(world, d6, d7, d8, d9, d10, d11));
            stack.splitStack(1);
            return stack;
         }

         @Override
         public void playDispenseSound(IBlockSource source) {
            source.getWorld().playAuxSFX(1009, source.getBlockPos(), 0);
         }
      });
      BlockDispenser.dispenseBehaviorRegistry.putObject(Items.boat, new BehaviorDefaultDispenseItem() {
         private final BehaviorDefaultDispenseItem field_150842_b = new BehaviorDefaultDispenseItem();

         @Override
         public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
            EnumFacing enumFacing4 = BlockDispenser.getFacing(source.getBlockMetadata());
            World world2 = source.getWorld();
            double d12 = source.getX() + enumFacing4.getFrontOffsetX() * 1.125F;
            double d13 = source.getY() + enumFacing4.getFrontOffsetY() * 1.125F;
            double d14 = source.getZ() + enumFacing4.getFrontOffsetZ() * 1.125F;
            BlockPos blockPos = source.getBlockPos().offset(enumFacing4);
            Material material = world2.getBlockState(blockPos).getBlock().getMaterial();
            double d15;
            if (Material.water.equals(material)) {
               d15 = 1.0;
            } else {
               if (!Material.air.equals(material) || !Material.water.equals(world2.getBlockState(blockPos.down()).getBlock().getMaterial())) {
                  return this.field_150842_b.dispense(source, stack);
               }

               d15 = 0.0;
            }

            EntityBoat entityBoat = new EntityBoat(world2, d12, d13 + d15, d14);
            world2.spawnEntityInWorld(entityBoat);
            stack.splitStack(1);
            return stack;
         }

         @Override
         public void playDispenseSound(IBlockSource source) {
            source.getWorld().playAuxSFX(1000, source.getBlockPos(), 0);
         }
      });
      BehaviorDefaultDispenseItem behaviorDefaultDispenseItem = new BehaviorDefaultDispenseItem() {
         private final BehaviorDefaultDispenseItem field_150841_b = new BehaviorDefaultDispenseItem();

         @Override
         public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
            ItemBucket itemBucket = (ItemBucket)stack.getItem();
            BlockPos blockPos2 = source.getBlockPos().offset(BlockDispenser.getFacing(source.getBlockMetadata()));
            if (itemBucket.tryPlaceContainedLiquid(source.getWorld(), blockPos2)) {
               stack.setItem(Items.bucket);
               stack.stackSize = 1;
               return stack;
            } else {
               return this.field_150841_b.dispense(source, stack);
            }
         }
      };
      BlockDispenser.dispenseBehaviorRegistry.putObject(Items.lava_bucket, behaviorDefaultDispenseItem);
      BlockDispenser.dispenseBehaviorRegistry.putObject(Items.water_bucket, behaviorDefaultDispenseItem);
      BlockDispenser.dispenseBehaviorRegistry.putObject(Items.bucket, new BehaviorDefaultDispenseItem() {
         private final BehaviorDefaultDispenseItem field_150840_b = new BehaviorDefaultDispenseItem();

         @Override
         public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
            World world3 = source.getWorld();
            BlockPos blockPos3 = source.getBlockPos().offset(BlockDispenser.getFacing(source.getBlockMetadata()));
            IBlockState iBlockState = world3.getBlockState(blockPos3);
            Block block = iBlockState.getBlock();
            Material material2 = block.getMaterial();
            Item item;
            if (Material.water.equals(material2) && block instanceof BlockLiquid && iBlockState.getValue(BlockLiquid.LEVEL) == 0) {
               item = Items.water_bucket;
            } else {
               if (!Material.lava.equals(material2) || !(block instanceof BlockLiquid) || iBlockState.getValue(BlockLiquid.LEVEL) != 0) {
                  return super.dispenseStack(source, stack);
               }

               item = Items.lava_bucket;
            }

            world3.setBlockToAir(blockPos3);
            if (--stack.stackSize == 0) {
               stack.setItem(item);
               stack.stackSize = 1;
            } else if (source.<TileEntityDispenser>getBlockTileEntity().addItemStack(new ItemStack(item)) < 0) {
               this.field_150840_b.dispense(source, new ItemStack(item));
            }

            return stack;
         }
      });
      BlockDispenser.dispenseBehaviorRegistry.putObject(Items.flint_and_steel, new BehaviorDefaultDispenseItem() {
         private boolean field_150839_b = true;

         @Override
         public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
            World world4 = source.getWorld();
            BlockPos blockPos4 = source.getBlockPos().offset(BlockDispenser.getFacing(source.getBlockMetadata()));
            if (world4.isAirBlock(blockPos4)) {
               world4.setBlockState(blockPos4, Blocks.fire.getDefaultState());
               if (stack.attemptDamageItem(1, world4.rand)) {
                  stack.stackSize = 0;
               }
            } else if (world4.getBlockState(blockPos4).getBlock() == Blocks.tnt) {
               Blocks.tnt.onBlockDestroyedByPlayer(world4, blockPos4, Blocks.tnt.getDefaultState().withProperty(BlockTNT.EXPLODE, true));
               world4.setBlockToAir(blockPos4);
            } else {
               this.field_150839_b = false;
            }

            return stack;
         }

         @Override
         public void playDispenseSound(IBlockSource source) {
            if (this.field_150839_b) {
               source.getWorld().playAuxSFX(1000, source.getBlockPos(), 0);
            } else {
               source.getWorld().playAuxSFX(1001, source.getBlockPos(), 0);
            }
         }
      });
      BlockDispenser.dispenseBehaviorRegistry.putObject(Items.dye, new BehaviorDefaultDispenseItem() {
         private boolean field_150838_b = true;

         @Override
         public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
            if (EnumDyeColor.WHITE == EnumDyeColor.byDyeDamage(stack.getMetadata())) {
               World world5 = source.getWorld();
               BlockPos blockPos5 = source.getBlockPos().offset(BlockDispenser.getFacing(source.getBlockMetadata()));
               if (ItemDye.applyBonemeal(stack, world5, blockPos5)) {
                  if (!world5.isRemote) {
                     world5.playAuxSFX(2005, blockPos5, 0);
                  }
               } else {
                  this.field_150838_b = false;
               }

               return stack;
            } else {
               return super.dispenseStack(source, stack);
            }
         }

         @Override
         public void playDispenseSound(IBlockSource source) {
            if (this.field_150838_b) {
               source.getWorld().playAuxSFX(1000, source.getBlockPos(), 0);
            } else {
               source.getWorld().playAuxSFX(1001, source.getBlockPos(), 0);
            }
         }
      });
      BlockDispenser.dispenseBehaviorRegistry.putObject(Item.getItemFromBlock(Blocks.tnt), new BehaviorDefaultDispenseItem() {
         @Override
         public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
            World world6 = source.getWorld();
            BlockPos blockPos6 = source.getBlockPos().offset(BlockDispenser.getFacing(source.getBlockMetadata()));
            EntityTNTPrimed entityTNTPrimed = new EntityTNTPrimed(world6, blockPos6.getX() + 0.5, blockPos6.getY(), blockPos6.getZ() + 0.5, (EntityLivingBase)null);
            world6.spawnEntityInWorld(entityTNTPrimed);
            world6.playSoundAtEntity(entityTNTPrimed, "game.tnt.primed", 1.0F, 1.0F);
            stack.stackSize--;
            return stack;
         }
      });
      BlockDispenser.dispenseBehaviorRegistry.putObject(Items.skull, new BehaviorDefaultDispenseItem() {
         private boolean field_179240_b = true;

         @Override
         public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
            World world7 = source.getWorld();
            EnumFacing enumFacing5 = BlockDispenser.getFacing(source.getBlockMetadata());
            BlockPos blockPos7 = source.getBlockPos().offset(enumFacing5);
            BlockSkull blockSkull = Blocks.skull;
            if (!world7.isAirBlock(blockPos7) || !blockSkull.canDispenserPlace(world7, blockPos7, stack)) {
               this.field_179240_b = false;
            } else if (!world7.isRemote) {
               world7.setBlockState(blockPos7, blockSkull.getDefaultState().withProperty(BlockSkull.FACING, EnumFacing.UP), 3);
               TileEntity tileEntity = world7.getTileEntity(blockPos7);
               if (tileEntity instanceof TileEntitySkull) {
                  if (stack.getMetadata() == 3) {
                     GameProfile gameProfile = null;
                     if (stack.hasTagCompound()) {
                        NBTTagCompound nbtTagCompound = stack.getTagCompound();
                        if (nbtTagCompound.hasKey("SkullOwner", 10)) {
                           gameProfile = NBTUtil.readGameProfileFromNBT(nbtTagCompound.getCompoundTag("SkullOwner"));
                        } else if (nbtTagCompound.hasKey("SkullOwner", 8)) {
                           String string = nbtTagCompound.getString("SkullOwner");
                           if (!StringUtils.isNullOrEmpty(string)) {
                              gameProfile = new GameProfile((UUID)null, string);
                           }
                        }
                     }

                     ((TileEntitySkull)tileEntity).setPlayerProfile(gameProfile);
                  } else {
                     ((TileEntitySkull)tileEntity).setType(stack.getMetadata());
                  }

                  ((TileEntitySkull)tileEntity).setSkullRotation(enumFacing5.getOpposite().getHorizontalIndex() * 4);
                  Blocks.skull.checkWitherSpawn(world7, blockPos7, (TileEntitySkull)tileEntity);
               }

               stack.stackSize--;
            }

            return stack;
         }

         @Override
         public void playDispenseSound(IBlockSource source) {
            if (this.field_179240_b) {
               source.getWorld().playAuxSFX(1000, source.getBlockPos(), 0);
            } else {
               source.getWorld().playAuxSFX(1001, source.getBlockPos(), 0);
            }
         }
      });
      BlockDispenser.dispenseBehaviorRegistry.putObject(Item.getItemFromBlock(Blocks.pumpkin), new BehaviorDefaultDispenseItem() {
         private boolean field_179241_b = true;

         @Override
         public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
            World world8 = source.getWorld();
            BlockPos blockPos8 = source.getBlockPos().offset(BlockDispenser.getFacing(source.getBlockMetadata()));
            BlockPumpkin blockPumpkin = (BlockPumpkin)Blocks.pumpkin;
            if (world8.isAirBlock(blockPos8) && blockPumpkin.canDispenserPlace(world8, blockPos8)) {
               if (!world8.isRemote) {
                  world8.setBlockState(blockPos8, blockPumpkin.getDefaultState(), 3);
               }

               stack.stackSize--;
            } else {
               this.field_179241_b = false;
            }

            return stack;
         }

         @Override
         public void playDispenseSound(IBlockSource source) {
            if (this.field_179241_b) {
               source.getWorld().playAuxSFX(1000, source.getBlockPos(), 0);
            } else {
               source.getWorld().playAuxSFX(1001, source.getBlockPos(), 0);
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

   public static void printToSYSOUT(String string) {
      if (HUD.isDevelopment) {
         SYSOUT.println(string);
      }
   }
}
