package bypass.util;

import bypass.ClientAccess;
import bypass.event.player.MoveEvent;
import bypass.module.hidden.ServerRotationTracker;
import bypass.module.movement.Flight;
import bypass.module.movement.Speed;
import bypass.server.ServerManager;
import bypass.server.ServerType;
import com.google.common.base.Predicates;
import java.awt.Color;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityEndermite;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityGiantZombie;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntityRabbit;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.potion.Potion;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.src.Reflector;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StringUtils;
import net.minecraft.util.Vec3;

public final class GameUtil implements ClientAccess {
   private static final Pattern E = Pattern.compile(
      "^(?:(?:(?:.+\\.)?hypixel\\.net)|(?:209\\.222\\.115\\.\\d{1,3})|(?:99\\.198\\.123\\.[123]?\\d?))\\.?(?::\\d{1,5}\\.?)?$", 2
   );
   private static Pattern Q = Pattern.compile("§(.).*§r");
   public static String N = null;
   private static final long b = 10198663265794L;

   public static boolean isPassive(Entity var0) {
      return var0 instanceof EntityPig
         || var0 instanceof EntityCow
         || var0 instanceof EntitySheep
         || var0 instanceof EntityChicken
         || var0 instanceof EntitySquid
         || var0 instanceof EntityBat
         || var0 instanceof EntityVillager
         || var0 instanceof EntityOcelot
         || var0 instanceof EntityHorse
         || var0 instanceof EntityRabbit && isNormalRabbit(var0);
   }

   public static boolean isNeutral(Entity var0) {
      return var0 instanceof EntityWolf && isCalmWolf(var0)
         || var0 instanceof EntityIronGolem && isCalmIronGolem(var0)
         || var0 instanceof EntityEnderman && isCalmEnderman(var0)
         || var0 instanceof EntityPigZombie && isCalmPigZombie(var0);
   }

   public static boolean isHostile(Entity var0) {
      return var0 instanceof EntityCreeper
         || var0 instanceof EntitySkeleton
         || var0 instanceof EntityZombie && !(var0 instanceof EntityPigZombie)
         || var0 instanceof EntityBlaze
         || var0 instanceof EntitySpider
         || var0 instanceof EntityWitch
         || var0 instanceof EntitySlime
         || var0 instanceof EntitySilverfish
         || var0 instanceof EntityGuardian
         || var0 instanceof EntityEndermite
         || var0 instanceof EntityGhast
         || var0 instanceof EntityWolf && !isCalmWolf(var0)
         || var0 instanceof EntityPigZombie && !isCalmPigZombie(var0)
         || var0 instanceof EntityEnderman && !isCalmEnderman(var0)
         || var0 instanceof EntityRabbit && !isNormalRabbit(var0)
         || var0 instanceof EntityIronGolem && !isCalmIronGolem(var0);
   }

   public static boolean isBoss(Entity var0) {
      return var0 instanceof EntityDragon || var0 instanceof EntityWither || var0 instanceof EntityGiantZombie;
   }

   public static boolean isVehicle(Entity var0) {
      return var0 instanceof EntityBoat || var0 instanceof EntityMinecart;
   }

   public static boolean isObject(Entity var0) {
      return var0 instanceof EntityEnderCrystal || var0 instanceof EntityFallingBlock || var0 instanceof EntityFireball || var0 instanceof EntityArmorStand;
   }

   public static boolean isCalmWolf(Entity var0) {
      return var0 instanceof EntityWolf && !((EntityWolf)var0).isAngry();
   }

   public static boolean isCalmPigZombie(Entity var0) {
      return var0 instanceof EntityPigZombie && var0.rotationPitch == 0.0F && ((EntityPigZombie)var0).getRevengeTimer() <= 0;
   }

   public static boolean isCalmIronGolem(Entity var0) {
      return var0 instanceof EntityIronGolem && var0.rotationPitch == 0.0F;
   }

   public static boolean isCalmEnderman(Entity var0) {
      return var0 instanceof EntityEnderman && !((EntityEnderman)var0).isScreaming();
   }

   public static boolean isNormalRabbit(Entity var0) {
      return var0 instanceof EntityRabbit && ((EntityRabbit)var0).getRabbitType() != 99;
   }

   public static boolean isRemoved(Entity var0) {
      return var0 == null || !mc.theWorld.loadedEntityList.contains(var0);
   }

   public static boolean isDead(EntityLivingBase var0) {
      return var0.getHealth() == 0.0F || var0.deathTime > 0;
   }

   public static boolean canSee(Entity var0) {
      for (int var4 = -1; var4 < 2; var4++) {
         for (int var5 = -1; var5 < 2; var5++) {
            for (int var6 = -1; var6 < 2; var6++) {
               double var7 = var4 / 1.8;
               double var9 = var5 / 1.8;
               double var11 = var6 / 1.8;
               if (mc.theWorld
                     .rayTraceBlocks(
                        new Vec3(mc.thePlayer.posX, mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ),
                        new Vec3(var0.posX + var7, var0.posY + var9 + var0.getEyeHeight(), var11 + var0.posZ)
                     )
                  == null) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   public static boolean isOverLiquid(AxisAlignedBB var0) {
      AxisAlignedBB var1 = new AxisAlignedBB(var0.minX + 0.305, var0.minY - 0.1, var0.minZ + 0.305, var0.maxX - 0.305, var0.maxY, var0.maxZ - 0.305);
      Block var2 = mc.theWorld.getBlockState(new BlockPos(var1.minX, var1.minY, var1.minZ)).getBlock();
      return var2 instanceof BlockLiquid;
   }

   public static boolean isHoldingSword(EntityPlayer var0) {
      return var0.getCurrentEquippedItem() != null ? var0.getCurrentEquippedItem().getItem() instanceof ItemSword : false;
   }

   public static int getLatencyAdjustedHurtTime(EntityLivingBase var0) {
      return var0.hurtTime - MathHelper.ceiling_float_int((float)ServerRotationTracker.U() / 50.0F);
   }

   public static boolean N() {
      long var0 = b ^ 33165469906170L;
      long var2 = var0 ^ 35290612201291L;
      return !mc.thePlayer.isOnLadder()
         && !mc.thePlayer.isInWater()
         && !mc.thePlayer.isPotionActive(Potion.blindness)
         && !mc.thePlayer.E()
         && mc.thePlayer.isCollidedVertically
         && !mc.playerController.isHittingBlock
         && !client.getModuleManager().get(Speed.class).isEnabled()
         && !client.getModuleManager().get(Flight.class).isEnabled()
         && !isOverLiquid(mc.thePlayer.getEntityBoundingBox());
   }

   public static boolean isOnServer(String var0) {
      return mc.isSingleplayer()
         ? false
         : mc.getCurrentServerData() != null && mc.getCurrentServerData().serverIP.toLowerCase().contains(var0)
            || mc.thePlayer.getClientBrand() != null && mc.thePlayer.getClientBrand().toLowerCase().contains(var0);
   }

   public static boolean isOnHypixel() {
      return ServerManager.X == ServerType.HYPIXEL && !Minecraft.c;
   }

   public static boolean isInHypixelGame() {
      return ServerManager.X == ServerType.HYPIXEL && !Minecraft.c && hasSidebar();
   }

   public static boolean P() {
      ServerData var3 = mc.getCurrentServerData();
      return var3 != null && E.matcher(var3.serverIP).find();
   }

   public static boolean isMoving(Entity var0) {
      return var0 == mc.thePlayer
         ? mc.gameSettings.keyBindForward.pressed
            || mc.gameSettings.keyBindBack.pressed
            || mc.gameSettings.keyBindRight.pressed
            || mc.gameSettings.keyBindLeft.pressed
         : var0.posX != var0.lastTickPosX || var0.posY != var0.lastTickPosY || var0.posZ != var0.lastTickPosZ;
   }

   public static boolean S(Entity var0) {
      if (!(var0 instanceof EntityLivingBase)) {
         return false;
      }

      EntityLivingBase var4 = (EntityLivingBase)var0;
      if (var0 instanceof EntityPlayer) {
         EntityPlayer var5 = (EntityPlayer)var0;
         boolean var6 = false;

         for (ItemStack var10 : var5.inventory.armorInventory) {
            if (var10 != null) {
               var6 = true;
            }
         }

         return r_boolean(var4) && !isHoldingItem(var4) && !var6;
      } else {
         return r_boolean(var4) && !isHoldingItem(var4);
      }
   }

   public static boolean isHoldingItem(EntityLivingBase var0) {
      return var0 != null && var0.getHeldItem() != null;
   }

   public static boolean r_boolean(EntityLivingBase var0) {
      return var0.isInvisibleToPlayer(mc.thePlayer);
   }

   public static String getScoreboardTitle() {
      if (mc.theWorld == null) {
         return "";
      }

      Scoreboard var3 = mc.theWorld.getScoreboard();
      if (var3 == null) {
         return "";
      }

      ScoreObjective var4 = null;
      ScorePlayerTeam var5 = var3.getPlayersTeam(mc.thePlayer.getName());
      if (var5 != null && var5.getChatFormat() != null) {
         int var6 = var5.getChatFormat().getColorIndex();
         if (var6 >= 0) {
            var4 = var3.getObjectiveInDisplaySlot(3 + var6);
         }
      }

      ScoreObjective var7 = var4 != null ? var4 : var3.getObjectiveInDisplaySlot(1);
      return var7 == null ? "" : StringUtils.stripControlCodes(var7.getDisplayName());
   }

   public static boolean scoreboardTitleContains(String var0) {
      return getScoreboardTitle().contains(var0);
   }

   private static boolean hasSidebar() {
      if (mc.theWorld == null) {
         return false;
      }

      Scoreboard var3 = mc.theWorld.getScoreboard();
      if (var3 == null) {
         return false;
      }

      ScoreObjective var4 = null;
      ScorePlayerTeam var5 = var3.getPlayersTeam(mc.thePlayer.getName());
      if (var5 != null && var5.getChatFormat() != null) {
         int var6 = var5.getChatFormat().getColorIndex();
         if (var6 >= 0) {
            var4 = var3.getObjectiveInDisplaySlot(3 + var6);
         }
      }

      ScoreObjective var7 = var4 != null ? var4 : var3.getObjectiveInDisplaySlot(1);
      return var7 != null;
   }

   public static MovingObjectPosition A(float var0, float var1, double var2) {
      Entity var7 = null;
      double var9 = var2;
      MovingObjectPosition var8;
      MovingObjectPosition var11 = var8 = rayTraceBlocks(var2, var0, var1);
      Vec3 var12 = mc.thePlayer.getPositionEyes(1.0F);
      if (var11 != null) {
         var9 = var11.hitVec.distanceTo(var12);
      }

      Vec3 var13 = mc.thePlayer.getVectorForRotation(var1, var0);
      Vec3 var14 = var12.addVector(var13.xCoord * var2, var13.yCoord * var2, var13.zCoord * var2);
      Vec3 var15 = null;
      List var16 = mc.theWorld
         .getEntitiesInAABBexcluding(
            mc.thePlayer,
            mc.thePlayer.getEntityBoundingBox().addCoord(var13.xCoord * var2, var13.yCoord * var2, var13.zCoord * var2).expand(1.0, 1.0, 1.0),
            Predicates.and(EntitySelectors.NOT_SPECTATING)
         );
      double var17 = var9;

      for (Object var20$raw : var16) {
         Entity var20 = (Entity)var20$raw;
         float var21 = var20.getCollisionBorderSize();
         AxisAlignedBB var22 = var20.getEntityBoundingBox().expand(var21, var21, var21);
         MovingObjectPosition var23 = var22.calculateIntercept(var12, var14);
         if (var22.isVecInside(var12)) {
            if (var17 >= 0.0) {
               var7 = var20;
               var15 = var23 == null ? var12 : var23.hitVec;
               var17 = 0.0;
            }
         } else if (var23 != null) {
            double var24 = var12.distanceTo(var23.hitVec);
            if (var24 < var17 || var17 == 0.0) {
               boolean var26 = false;
               if (Reflector.ForgeEntity_canRiderInteract.exists()) {
                  var26 = Reflector.callBoolean(var20, Reflector.ForgeEntity_canRiderInteract);
               }

               if (var20 != var20.ridingEntity || var26) {
                  var7 = var20;
                  var15 = var23.hitVec;
                  var17 = var24;
               } else if (var17 == 0.0) {
                  var7 = var20;
                  var15 = var23.hitVec;
               }
            }
         }
      }

      if (var7 != null && var12.distanceTo(var15) > var2) {
         var7 = null;
         var8 = new MovingObjectPosition(MovingObjectPosition.MovingObjectType.MISS, var15, null, new BlockPos(var15));
      }

      if ((var7 instanceof EntityLivingBase || var7 instanceof EntityFallingBlock) && (var17 < var9 || var11 == null)) {
         var8 = new MovingObjectPosition(var7, var15);
      }

      return var8;
   }

   public static MovingObjectPosition V(float var0, float var1, double var2) {
      Entity var8 = null;
      double var10 = mc.playerController.getBlockReachDistance();
      double var12 = var10;
      boolean var14 = false;
      MovingObjectPosition var9;
      MovingObjectPosition var15 = var9 = rayTraceBlocks(var10, var0, var1);
      Vec3 var16 = mc.thePlayer.getPositionEyes(1.0F);
      if (mc.playerController.L()) {
         var10 = 6.0;
         var12 = 6.0;
      } else if (var10 > 3.0) {
         var14 = true;
      }

      if (var15 != null) {
         var12 = var15.hitVec.distanceTo(var16);
      }

      Vec3 var17 = mc.thePlayer.getVectorForRotation(var1, var0);
      Vec3 var18 = var16.addVector(var17.xCoord * var10, var17.yCoord * var10, var17.zCoord * var10);
      Vec3 var19 = null;
      List var20 = mc.theWorld
         .getEntitiesInAABBexcluding(
            mc.thePlayer,
            mc.thePlayer.getEntityBoundingBox().addCoord(var17.xCoord * var10, var17.yCoord * var10, var17.zCoord * var10).expand(1.0, 1.0, 1.0),
            Predicates.and(EntitySelectors.NOT_SPECTATING)
         );
      double var21 = var12;

      for (Object var24$raw : var20) {
         Entity var24 = (Entity)var24$raw;
         float var25 = var24.getCollisionBorderSize();
         AxisAlignedBB var26 = var24.getEntityBoundingBox().expand(var25, var25, var25);
         MovingObjectPosition var27 = var26.calculateIntercept(var16, var18);
         if (var26.isVecInside(var16)) {
            if (var21 >= 0.0) {
               var8 = var24;
               var19 = var27 == null ? var16 : var27.hitVec;
               var21 = 0.0;
            }
         } else if (var27 != null) {
            double var28 = var16.distanceTo(var27.hitVec);
            if (var28 < var21 || var21 == 0.0) {
               boolean var30 = false;
               if (Reflector.ForgeEntity_canRiderInteract.exists()) {
                  var30 = Reflector.callBoolean(var24, Reflector.ForgeEntity_canRiderInteract);
               }

               if (var24 != var24.ridingEntity || var30) {
                  var8 = var24;
                  var19 = var27.hitVec;
                  var21 = var28;
               } else if (var21 == 0.0) {
                  var8 = var24;
                  var19 = var27.hitVec;
               }
            }
         }
      }

      if (var8 != null && var14 && var16.distanceTo(var19) > var2) {
         var8 = null;
         var9 = new MovingObjectPosition(MovingObjectPosition.MovingObjectType.MISS, var19, null, new BlockPos(var19));
      }

      if (var8 != null && (var21 < var12 || var15 == null)) {
         var9 = new MovingObjectPosition(var8, var19);
      }

      return var9;
   }

   private static MovingObjectPosition rayTraceBlocks(double var0, float var2, float var3) {
      Vec3 var4 = mc.thePlayer.getPositionEyes(1.0F);
      Vec3 var5 = mc.thePlayer.getVectorForRotation(var3, var2);
      Vec3 var6 = var4.addVector(var5.xCoord * var0, var5.yCoord * var0, var5.zCoord * var0);
      return mc.theWorld.rayTraceBlocks(var4, var6, false, false, true);
   }

   public static void setMoveSpeed(MoveEvent var0, double var1, float var3) {
      double var7 = mc.thePlayer.movementInput.moveForward;
      double var9 = mc.thePlayer.movementInput.moveStrafe;
      float var11 = mc.thePlayer.rotationYaw;
      if (var7 == 0.0 && var9 == 0.0) {
         var0.setX(0.0);
         var0.setZ(0.0);
      } else {
         if (var7 != 0.0) {
            if (var9 > 0.0) {
               var11 += var7 > 0.0 ? -45 : 45;
            } else if (var9 < 0.0) {
               var11 += var7 > 0.0 ? 45 : -45;
            }

            var9 = 0.0;
            if (var7 > 0.0) {
               var7 = 1.0;
            } else if (var7 < 0.0) {
               var7 = -1.0;
            }
         }

         double var12 = Math.cos(Math.toRadians(var11 + var3));
         double var14 = Math.sin(Math.toRadians(var11 + var3));
         var0.setX((var7 * var1 * var12 + var9 * var1 * var14) * 0.99);
         var0.setZ((var7 * var1 * var14 - var9 * var1 * var12) * 0.99);
      }
   }

   public static int j(ItemStack var0) {
      if (var0.getItem() instanceof ItemArmor) {
         ItemArmor var4 = (ItemArmor)var0.getItem();
         if (var4.getArmorMaterial() == ItemArmor.ArmorMaterial.LEATHER) {
            return var4.getColor(var0);
         }

         switch (var4.getArmorMaterial()) {
            case DIAMOND:
               return -7740176;
            case IRON:
               return -5131342;
            case CHAIN:
               return -6908266;
            case GOLD:
               return -988591;
         }
      }

      return -1;
   }

   public static Block getBlock(BlockPos var0) {
      return mc.theWorld.getBlockState(var0).getBlock();
   }

   public static Block getBlock(double var0, double var2, double var4) {
      return mc.theWorld.getBlockState(new BlockPos((int)Math.floor(var0), (int)Math.floor(var2), (int)Math.floor(var4))).getBlock();
   }

   public static Block getBlockAtPlayer() {
      return getBlockAtPlayer(0.0);
   }

   public static Block getBlockAtPlayer(double var0) {
      return getBlock(mc.thePlayer.posX, mc.thePlayer.posY + var0, mc.thePlayer.posZ);
   }

   public static int getHealthColorRGB(EntityLivingBase var0) {
      int var3 = 100 - (int)var0.getHealth() * 100 / (int)var0.getMaxHealth();
      int var4 = 255 * var3 / 100;
      int var5 = 255 * (100 - var3) / 100;
      var4 = MathHelper.clamp_int(var4, 0, 255);
      var5 = MathHelper.clamp_int(var5, 0, 255);
      return new Color(var4, var5, 0).getRGB();
   }

   public static Color getHealthColor(EntityLivingBase var0) {
      int var3 = 100 - (int)var0.getHealth() * 100 / (int)var0.getMaxHealth();
      int var4 = 255 * var3 / 100;
      int var5 = 255 * (100 - var3) / 100;
      var4 = MathHelper.clamp_int(var4, 0, 255);
      var5 = MathHelper.clamp_int(var5, 0, 255);
      return new Color(var4, var5, 0);
   }

   private static String getColorCode(EntityPlayer var0) {
      Matcher var4 = Q.matcher(var0.getDisplayName().getFormattedText());
      return var4.find() ? var4.group(1) : "f";
   }

   private static String getColorCode(String var0) {
      Matcher var4 = Q.matcher(var0);
      return var4.find() ? var4.group(1) : "f";
   }

   public static boolean O(EntityPlayer var0, EntityLivingBase var1) {
      if (var1.getDisplayName() == null) {
         return false;
      }

      if (N == null || mc.thePlayer.ticksExisted % 214 == 0) {
         AbstractClientPlayer var5 = (AbstractClientPlayer)var0;
         if (var5.getPlayerInfo() != null && !Objects.equals(var5.getPlayerInfo().getGameProfile().getName(), mc.thePlayer.getName())) {
            for (NetworkPlayerInfo var8 : GuiPlayerTabOverlay.field_175252_a.sortedCopy(mc.thePlayer.sendQueue.getPlayerInfoMap())) {
               if (var8.getGameProfile().getName().equals(var5.getPlayerInfo().getGameProfile().getName())) {
                  N = var8.getDisplayName() != null
                     ? var8.getDisplayName().getFormattedText()
                     : ScorePlayerTeam.formatPlayerName(var8.getPlayerTeam(), var8.getGameProfile().getName());
                  N = N + "§r";
                  break;
               }
            }
         }
      }

      return N != null
         ? N.contains("§" + getColorCode(N)) && var1.getDisplayName().getFormattedText().contains("§" + getColorCode(N))
         : var0.getDisplayName().getFormattedText().contains("§" + getColorCode(var0))
            && var1.getDisplayName().getFormattedText().contains("§" + getColorCode(var0));
   }
}
